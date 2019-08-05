package com.spring.study.board.controller;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
//공지글 볼때 등급낮아서 팅겨내는 로직 다시 하기
@Controller
public class RestAPIController {
	private static final Logger logger = LoggerFactory.getLogger(AticleController.class);
	private static final int pageSize = 10;

	@Autowired
	private ArticleService articleService;
	
	// endPage restAPI
		@RequestMapping(value = "/board/article/{page}/list")
		public @ResponseBody Map<String, Object> getFeedTypeArticlesByTotalCount(@PathVariable int page) {
			Map<String, Object> result = new HashMap<>();
			//첫 진입시 page는 어떻게 가져올지 다시 생각하기
			CommonRequestDto req = new CommonRequestDto.Builder(page, pageSize).build();
			
			if (1 == page) {
				PageList<ArticleVo> articlePageDto = articleService.getArticlePageListWithCount(req);

				result.put("totalPage", String.valueOf(articlePageDto.getTotalPage()));
				result.put("articleList", articlePageDto.getList());
			} else {
				List<ArticleVo> articleList = articleService.getArticleList(req);

				result.put("nextPage", "0");
				result.put("totalPage", "0");
				result.put("articleList", articleList);
			}

			return result;
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
			String returnURI = "";
			//상태코드 입력하기
			try {
				articleVo = articleService.getArticleContents(articleId, member);
				model.addAttribute("articleVo", articleVo);
				returnURI = "board/viewArticle";
			} catch (SQLException e) {
				returnURI = "redirect:/board/listArticleForm";
				//여기 리다이렉트를 여기서 처리하는게 맞는가?
				model.addAttribute("redirect", "redirect:/board/listArticleForm");
				e.printStackTrace();
			} catch (NotFoundException e) {
				returnURI = "member/loginForm";
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				model.addAttribute("uri", returnURI);
			}
			return returnURI;
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
		
		//글쓰기
		
		//글 수정
		
		//글 삭제
		
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
