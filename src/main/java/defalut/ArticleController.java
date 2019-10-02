package defalut;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spring.study.common.model.BaseParam;
import com.spring.study.common.model.PageList;
import com.spring.study.model.article.Article;
import com.spring.study.model.article.ArticleDto;
import com.spring.study.model.article.ArticleParam;
import com.spring.study.model.comments.Comment;
import com.spring.study.model.comments.CommentDto;
import com.spring.study.model.comments.CommentParam;
import com.spring.study.model.user.User;
import com.spring.study.service.ArticleService;
import com.spring.study.service.CommentsService;

import javassist.NotFoundException;
import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;

//@Controller
public class ArticleController {
	private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);
	private static final int pageSize = 10;
	private static final int commentPageSize = 10;
	@Autowired
	private ArticleService articleService;
	@Autowired
	private CommentsService commentsService;
	
	
	@RequestMapping(value = "/board/article/{page}/list")
	public @ResponseBody List<ArticleDto> getArticleList(@PathVariable int page, Model model,@RequestParam(required = false, defaultValue = "old") String sort, User user) {			
		//BaseParam requestParam = new BaseParam.Builder(page, pageSize).build();																					
		ArticleParam reqParam = new ArticleParam.Builder(pageSize).page(page).sort(sort).build();//
		List<Article> articleList = null;		
		
		if (1 == page) {
			PageList<Article> returnPageList = articleService.getArticlePageListWithCount(reqParam);
			articleList = returnPageList.getList();
			model.addAttribute("totalPage", returnPageList.getTotalPage());
		} else {
			articleList = articleService.getArticleList(reqParam);
		}
		
		List<ArticleDto> articleHeader = articleList.stream()
		.map(Article::displayArticles)
		.collect(Collectors.toList());
		
		return articleHeader;
	}
		
	// hasNext
	@RequestMapping(value = "/board/article2/{page}/list")
	public @ResponseBody Map<String, Object> getArticleList(Model model, @PathVariable int page) {
		ArticleParam req = new ArticleParam.Builder(pageSize).page(page).useMore(true).build();
	
		PageList<Article> pageListDto = articleService.getArticlePageList(req);
		List<Article> articleList = pageListDto.getList();
		
		List<ArticleDto> articleHeader = articleList.stream()
										.map(Article::displayArticles)
										.collect(Collectors.toList());
		
		Map<String, Object> result = new HashMap<>();
		result.put("articleList", articleList);
		result.put("hasNext", pageListDto.getHasNext());

		return result;
	}
	// 기준
	@RequestMapping(value = "/board/{articleId}/detail", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> viewArticle(@PathVariable String articleId, User user) {
		Map<String, Object> resultMap = new HashMap<>();

		try {
			Article article = articleService.getArticle(articleId, user);
			resultMap.put("code", HttpStatus.OK.value());
			resultMap.put("msg", HttpStatus.OK.getReasonPhrase());
			resultMap.put("article", article.showArticle());
		} catch (RuntimeException e) {
			resultMap.put("code", HttpStatus.FORBIDDEN.value());
			resultMap.put("msg", e.getMessage());
			resultMap.put("article", Collections.EMPTY_MAP);
		}
				
		return resultMap;
	}
														
	@RequestMapping(value = {"/board/writeArticleForm", "/board/{parentId}/"})
								
	public String moveWriteForm(@PathVariable(required = false) String parentId, Model model, User user, HttpServletRequest req) {
		ArticleDto articleDto = user.getUserInfo();
		
		if(!user.isLogon()) { return "redirect:/member/loginForm"; }
		
		if(req.getRequestURI().equals("/board/"+parentId+"/replyForm")) {
			//
			//혹은 boolean isNotice = articleService.isNoticeArticle(articleId);이 여러 메소드에서 반복적으로 나타나고 있는데 따로 분리하여 처리가능할까요???
			Article parentArticleInfo = articleService.getArticle(parentId, user);
			ArticleDto parentArticleDto = parentArticleInfo.showArticle();
			
			articleDto.setArticleId(parentArticleDto.getArticleId());				
			articleDto.setTitle("[Re]: " + parentArticleDto.getTitle());
			
			model.addAttribute("articleDto", articleDto);
			model.addAttribute("path", "/board/"+parentId+"/reply");
		}
		
		if(req.getRequestURI().equals("/board/writeArticleForm")) {
			model.addAttribute("path","/board/article");
		}
		
		model.addAttribute("writeMemberId", user.getUserId());
		return "board/addArticleForm";
	}
	
	@RequestMapping(value = "/board/article", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> writeArticle(@RequestBody ArticleDto articleDto, HttpServletRequest req, User user){
		Map<String, Object> resultState = new HashMap<>();
		
		//얘네는 파라미터 객체로 보낼게 아닌듯함 다시 dto로 보내기
		ArticleParam articleParam = new ArticleParam.Builder(articleDto.getTitle(), articleDto.getContent(), user.getUserId(), articleDto.getArticleTypeCd())
													.displayStartDate(articleDto.getDisplayStartDate())
													.displayEndDate(articleDto.getDisplayEndDate())
													.build();
		try {
			req.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String writenArticleId = "";
		try {
			writenArticleId = articleService.writeArticle(articleParam);
		} catch (SQLException e) {
			resultState.put("code", HttpStatus.INTERNAL_SERVER_ERROR);
			resultState.put("msg",e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			resultState.put("code", HttpStatus.SERVICE_UNAVAILABLE);
			resultState.put("msg","알 수 없는 오류 발생");
			e.printStackTrace();
		}
	
		resultState.put("code", HttpStatus.OK);
		resultState.put("msg", "글 등록이 완료되었습니다.");
		resultState.put("redirect", "/board/" + writenArticleId);
		
		return resultState;
	}
	
	@RequestMapping(value = "/board/{parentId}/reply", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> writeReply(@RequestBody ArticleDto articleDto, @PathVariable String parentId) {
		Map<String, Object> result = new HashMap<>();
		
		if(Article.checkId(parentId)) { 
			result.put("code", HttpStatus.BAD_REQUEST);
			result.put("msg", "입력 값이 올바르지 않습니다. 다시 확인 해주세요.");
			return result;
		}		
		
		try {
			String msg = articleService.writeReply(articleDto);
			result.put("code",HttpStatus.OK);
			result.put("msg", msg);
		} catch (NotFoundException e) {
			result.put("code", HttpStatus.NOT_FOUND);
			result.put("msg", e.getMessage());
			e.printStackTrace();
		} catch (InternalException e) {
			result.put("code", HttpStatus.INTERNAL_SERVER_ERROR);
			result.put("msg", e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			result.put("code", HttpStatus.INTERNAL_SERVER_ERROR);
			result.put("msg", "알 수 없는 오류가 발생 했습니다. 다시 시도 해주세요");
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping(value = "/board/{articleId}/modifyForm")
	public String moveModificationForm(@PathVariable String articleId, Model model, User user) {
		if(!user.isLogon()) { return "redirect:/member/loginForm"; }
		
		String returnPath ="";
		Map<String, Object> resultState = new HashMap<>();
		String writerId = articleService.getWriterId(articleId);
		
		if(user.checkUserId(writerId)) {
			Article article = articleService.getArticle(articleId);
			
		    model.addAttribute("articleVo", article);
			returnPath = "board/modifyForm";
		}else {
			resultState.put("msg","해당작성자만 가능합니다");
			resultState.put("code", HttpStatus.BAD_REQUEST);
			model.addAttribute("msg", "해당 작성자만 가능합니다");
			returnPath = "redirect:/board/"+articleId;
		}
		
		return returnPath;
	}
	
	@RequestMapping(value = "/board/{articleNo}", method = RequestMethod.PUT)
	public @ResponseBody Map<String, Object> modifyArticle(@RequestBody ArticleDto articleDto, User user) {
		Map<String, Object> resultState = new HashMap<>();
		
		if(!user.isLogon()) {
			resultState.put("msg", "로그인 세션 만료");
			resultState.put("code", HttpStatus.FORBIDDEN);
			return resultState;
		}
		
		if(Article.checkId(articleDto.getArticleId())) { 
			resultState.put("code", HttpStatus.BAD_REQUEST);
			resultState.put("msg", "잘못된 요청입니다.");
			return resultState;
		}
		
		
		try {
			articleService.modifyArticle(articleDto, user);
			resultState.put("code",HttpStatus.OK);
			resultState.put("msg",HttpStatus.OK.getReasonPhrase());
		} catch (NotFoundException e) {
			resultState.put("msg", e.getMessage());
			resultState.put("code", HttpStatus.NOT_FOUND);
			e.printStackTrace();
		} catch (Exception e) {
			resultState.put("msg", e.getMessage());
			resultState.put("code", HttpStatus.SERVICE_UNAVAILABLE);
			e.printStackTrace();
		}  finally {
			resultState.put("redirect", "/board/" + articleDto.getArticleId());
		}

		return resultState;
	}
	
	/*********************************************************/
	//여기까지 함

	
	@RequestMapping(value = "/board/{articleNo}/comments", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> writeComment(@RequestBody CommentDto commentsDto, User user) {
		Map<String, Object> returnMap = new HashMap<>();

		try {
			String msg = commentsService.writeComment(commentsDto, user);//
			returnMap.put("code",HttpStatus.OK);
			returnMap.put("msg",msg);
		} catch (NotFoundException e) {
			returnMap.put("code", HttpStatus.NOT_FOUND);
			returnMap.put("msg",e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			returnMap.put("code", HttpStatus.SERVICE_UNAVAILABLE);
			returnMap.put("msg", "알 수 없는 오류가 발생 했습니다. 다시 시도 해주세요");
			e.printStackTrace();
		}
			return returnMap;
	}
	
	@RequestMapping(value = "/board/{articleId}/comments/{commentsPage}")
	public @ResponseBody Map<String, Object> getCommentsList(@PathVariable String articleId, @PathVariable int commentsPage, User user) {
		Map<String, Object> returnMap = new HashMap<>();
		String userId = user.getUserId();
		if(!user.isLogon()) { userId = ""; }
		
		CommentParam commentsParam = new CommentParam.Builder(commentPageSize, articleId)
				.page(commentsPage)
				.userId(userId)
				.build();
		PageList<Comment> commentsPageList = commentsService.getCommentsPageList(commentsParam);
		
		returnMap.put("commentsList", commentsPageList.getList());
		returnMap.put("totalPage", commentsPageList.getTotalPage());
		return returnMap;
	}

	@RequestMapping(value = "/board/{articleNo}", method = RequestMethod.DELETE)
	public @ResponseBody Map<String, Object> deleteArticle(@RequestBody ArticleDto articleVo) {
		Map<String, Object> resultState = new HashMap<>();

		try {
			articleService.deleteArticle(articleVo);
			resultState.put("msg",HttpStatus.OK);
			resultState.put("path","/board/listArticleForm");
		} catch (DataIntegrityViolationException e) {
			resultState.put("code", HttpStatus.CONFLICT);
			resultState.put("msg", "댓글이 달린 글은 삭제할 수 없습니다.");
			e.printStackTrace();
		}  catch (NotFoundException e) {
			resultState.put("msg", e.getMessage());
			resultState.put("code", HttpStatus.NOT_FOUND);
			e.printStackTrace();
		} catch (Exception e) {
			resultState.put("code", HttpStatus.SERVICE_UNAVAILABLE);
			resultState.put("msg", e.getMessage());
			e.printStackTrace();
		} 

		return resultState;
	}
	
	
}
