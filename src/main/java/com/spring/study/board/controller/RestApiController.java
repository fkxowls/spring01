package com.spring.study.board.controller;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.support.HttpRequestHandlerServlet;

import com.spring.study.board.model.ArticleVo;
import com.spring.study.board.model.CommonRequestDto;
import com.spring.study.board.service.ArticleService;
import com.spring.study.comment.model.CommentPageList;
import com.spring.study.comment.model.CommentsRequestDto;
import com.spring.study.comment.model.CommentsVo;
import com.spring.study.common.model.PageList;
import com.spring.study.member.model.Member;

import javassist.NotFoundException;
import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;

//AticleController를 REST로 변경 중 

/***		
	해야할 것
1.글수정폼 
2.글쓰기
3.글 수정
4.공지글 목록
5.내가 쓴글 보기
6.공지글 볼때 등급낮아서 팅겨내는 로직 다시 하기


***/		

@Controller
public class RestApiController {
	private static final Logger logger = LoggerFactory.getLogger(AticleController.class);
	private static final int pageSize = 10;

	@Autowired
	private ArticleService articleService;
	
	// endPage restAPI
		@RequestMapping(value = "/board/article/{page}/list")
		public String getFeedTypeArticlesByTotalCount(@PathVariable int page, Model model) {
			//Map<String, Object> result = new HashMap<>();
			
			CommonRequestDto req = new CommonRequestDto.Builder(page, pageSize).build();
			
			if (1 == page) {
				PageList<ArticleVo> articlePageDto = articleService.getArticlePageListWithCount(req);
				//result.put("totalPage", String.valueOf(articlePageDto.getTotalPage()));
				//result.put("articleList", articlePageDto.getList());
				model.addAttribute("totalPage", String.valueOf(articlePageDto.getTotalPage()));
				model.addAttribute("articleList", articlePageDto.getList());
				//여기서 글상세보기 주소는 article과 1:1관계임 즉 글상세보기uri는 dto에 있어야함?? 그럼 어떻게 처리를하는가..
			} else {
				List<ArticleVo> articleList = articleService.getArticleList(req);
				//result.put("nextPage", "0");
				//result.put("totalPage", "0");
				//result.put("articleList", articleList);
				model.addAttribute("articleList", articleList);
			}
			
			model.addAttribute("writeArticleForm","/board/writeArticleForm");
			
			return "board/listArticle2";
		}
		
		// hasNext
		@RequestMapping(value = "/board/article2/{page}/list")
		public @ResponseBody Map<String, Object> getFeedTypeArticlesByHasNext(Model model, @PathVariable int page) {
			CommonRequestDto req = new CommonRequestDto.Builder(page, pageSize).useMoreView(true).build();
		
			PageList<ArticleVo> articlePageDto = articleService.getArticlePageList(req);

			Map<String, Object> result = new HashMap<>();
			result.put("articleList", articlePageDto.getList());
			result.put("hasNext", String.valueOf(articlePageDto.getHasNext()));

			return result;
		}
		
		@RequestMapping(value = "/board/{articleId}", method = RequestMethod.GET)
		public String viewArticle(Model model, @PathVariable String articleId, Member member) {
			ArticleVo articleVo = new ArticleVo();
			Map<String, Object> resultStatus = new HashMap<>();
			String returnURI = "";
			
			//view단에서 어떻게 받아야함???
			try {
				articleVo = articleService.getArticleContents(articleId, member);
				resultStatus.put("code",HttpStatus.OK);
				resultStatus.put("msg", "로그인 성공");	
				
				model.addAttribute("articleVo", articleVo);				
				returnURI = "board/viewArticle";
			} catch (SQLException e) {
				//returnURI = "/board/listArticleForm";
				resultStatus.put("code", HttpStatus.FORBIDDEN);
				resultStatus.put("msg", e.getMessage());
				resultStatus.put("redirect","/board/listArticleForm");
				e.printStackTrace();
			} catch (NotFoundException e) {
				//returnURI = "reirect:/member/loginForm.do";
				resultStatus.put("code", HttpStatus.UNAUTHORIZED);
				resultStatus.put("msg", e.getMessage());
				resultStatus.put("redirect","/member/loginForm.do");
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				model.addAttribute("resultStatus", resultStatus);
				model.addAttribute("modifyFormPath","/board/"+articleId);
				model.addAttribute("replyFormPath","/board/"+articleId+"/reply");
				model.addAttribute("articleDeletePath","/board/"+articleId);
				//댓글리스트,댓글입력 주소도 여기서??
			}
			
			return returnURI;
		}
		
		@RequestMapping(value = {"/board/writeArticleForm", "/board/writeReplyForm"})
		public String writeForm(@RequestParam(required=false) String articleId, Model model, Member member, HttpServletRequest req) {
			ArticleVo articleVo = new ArticleVo();
			
			//if(!member.isLogin()) { return "redirect:/member/loginForm"; }
			if(member == null) { return "redirect:/member/loginForm"; }
			
			if(req.getRequestURI().equals("/board/writeReplyForm")) {
				//[답글]: ㅇㅇㅇㅇ 으로 제목을 지정하려면 db로 조회해서 ?? 아니면 파라미터로 제목만 보낸다?? 
				articleVo.setArticleId(articleId);				
			}
			articleVo.setwriteMemberId(member.getMemberId());
			
			model.addAttribute("articleVo",articleVo);
			//model.addAttribute("uri","/board/article");
			return "board/addArticleForm";
		}
		
		@RequestMapping(value = "/board/article", method = RequestMethod.POST)
		public @ResponseBody Map<String, Object> writeArticle(@RequestBody ArticleVo articleVo, HttpServletRequest req){
			Map<String, Object> resultMap = new HashMap<>();
			try {
				req.setCharacterEncoding("utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			//String articleId = articleService.giveArticleId();
			String writenArticleId = articleService.writeArticle(articleVo);
			resultMap.put("code", HttpStatus.OK);
			resultMap.put("msg", "글 등록이 완료되었습니다.");
			resultMap.put("redirect", "/board" + writenArticleId);
			
			return resultMap;
		}
		
		@RequestMapping(value = "/board/{articleNo}", method = RequestMethod.PUT)
		public @ResponseBody Map<String, Object> modifyArticle(@RequestBody ArticleVo articleVo,
				@PathVariable String articleNo, Member member) {
			Map<String, Object> result = new HashMap<>();

			try {
				articleService.modifyArticle(articleVo, member);
				result.put("code",HttpStatus.OK);
			} catch (NotFoundException e) {
				result.put("msg", e.getMessage());
				result.put("code", HttpStatus.NOT_FOUND);
				e.printStackTrace();
			} catch (NullPointerException e) {
				result.put("msg", e.getMessage());
				result.put("code", HttpStatus.FORBIDDEN);
				e.printStackTrace();
			} catch (Exception e) {
				result.put("msg", e.getMessage());
				result.put("code", HttpStatus.SERVICE_UNAVAILABLE);
				e.printStackTrace();
			}  finally {
				result.put("redirect", "/board/viewArticle.do?articleId=" + articleVo.getArticleId());
			}

			return result;
		}
		
		@RequestMapping(value = "/board/{articleNo}/comment", method = RequestMethod.POST)
		public @ResponseBody Map<String, Object> writeComment(@RequestBody CommentsRequestDto commentsRequestDto, Member member) {
			Map<String, Object> returnMap = new HashMap<>();

			System.out.println(member.getMemberId());
			try {
				String msg = articleService.writeComment(commentsRequestDto, member);
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
		//글작성자를 어떻게 보내야 하는가..... 해당 글번호로 조회해서 글작성자 아이디를 가져온다?? 
		@RequestMapping(value = "/board/{articleId}/comments/{commentsPage}", method = RequestMethod.GET, produces = "application/json; charset=utf8")
		public @ResponseBody CommentPageList getCommentsList(@PathVariable("articleId") String articleId, @PathVariable("commentsPage") int commentsPage, Member user) {
			CommentPageList commentPageList = new CommentPageList();
			String userId;
			
			if(user.isLogin()) {
				userId = user.getMemberId();
				commentPageList = articleService.getCommentsPageList(articleId, commentsPage, userId);
			}else {
				userId ="";
				commentPageList = articleService.getCommentsPageList(articleId, commentsPage, userId);
			}
			
			
			return commentPageList;
		}
		
		
		//이 아래로 아직안함
		@RequestMapping(value = "/board/{parentId}/reply", method = RequestMethod.POST)
		public @ResponseBody Map<String,Object> writeReply(@RequestBody ArticleVo articleVo, @PathVariable String parentId) {
			Map<String, Object> result = new HashMap<>();

			if(5 != parentId.length()) { // TODO 시퀀스는 5자리가 보장되도록 작업 해줄 것
				result.put("code", HttpStatus.BAD_REQUEST);
				result.put("msg", "입력 값이 올바르지 않습니다. 다시 확인 해주세요.");
				return result;
			}
			
			articleVo.setParentId(parentId);
			
			try {
				String msg = articleService.replyArticle(articleVo);
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
		
		@RequestMapping(value = "/board/{articleNo}", method = RequestMethod.DELETE)
		public @ResponseBody Map<String, Object> deleteArticle(@RequestBody ArticleVo articleVo) {
			Map<String, Object> result = new HashMap<>();
			
			//예외처리 구체적으로
			try {
				articleService.deleteArticle(articleVo);
				result.put("msg",HttpStatus.OK);
				result.put("url","/board/listArticleForm");
			} catch (Exception e) {
				result.put("msg", "댓글이 달린 글은 삭제할 수 없습니다.");
				result.put("code", HttpStatus.CONFLICT);
				e.printStackTrace();
			}

			return result;
		}
		
	
}
