package com.spring.study.board.controller;

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
import org.springframework.web.bind.annotation.SessionAttributes;

import com.spring.study.board.service.ArticleService;
import com.spring.study.board.vo.ArticleReplyVo;
import com.spring.study.board.vo.AticleVo;
import com.spring.study.board.vo.PagingResponseDTO;
import com.spring.study.member.vo.MemberDTO;

import javassist.NotFoundException;
import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;

/************************************
	restAPI용 컨트롤러를 따로 분리해보자
	
************************************/
@Controller
public class RestAPIController {
	private static final Logger logger = LoggerFactory.getLogger(AticleController.class);
	private static final int pageSize = 10;

	@Autowired
	private ArticleService articleService;
	
	// endPage restAPI
		@RequestMapping(value = "/board/article/{page}/datas")
		public @ResponseBody Map<String, Object> getArticleDatas(@PathVariable int page) {
			Map<String, Object> result = new HashMap<>();

			if (1 == page) {
				PagingResponseDTO<AticleVo> articlePageDto = articleService.EndPaging(1, pageSize);

				result.put("nextPage", String.valueOf(articlePageDto.getNextPage()));
				result.put("totalPage", String.valueOf(articlePageDto.getTotalPage()));
				result.put("articleList", articlePageDto.getList());
			} else {
				List<AticleVo> articleList = articleService.EndPagingMore(page, pageSize);

				result.put("nextPage", "0");
				result.put("totalPage", "0");
				result.put("articleList", articleList);
			}

			return result;
		}
		
		// hasNext
		@RequestMapping(value = "/board/article/{page}/datas2")
		public @ResponseBody Map<String, Object> getArticleDatas2(Model model, @PathVariable int page) {
			PagingResponseDTO<AticleVo> articlePageDto = articleService.hasNextPagingMore(page, pageSize);

			Map<String, Object> result = new HashMap<>();
			result.put("articleList", articlePageDto.getList());
			result.put("hasNext", String.valueOf(articlePageDto.getHasNext()));

			return result;
		}
		
		@RequestMapping(value = "/board/{articleNo}", method = RequestMethod.GET)
		public @ResponseBody AticleVo viewArticle2(Model model, @PathVariable String articleNo) {
			AticleVo articleVo = articleService.viewArticle(articleNo);

			return articleVo;
		}
		
		@RequestMapping(value = "/board/{articleNo}", method = RequestMethod.PUT)
		public @ResponseBody Map<String, Object> modifyArticle(@RequestBody AticleVo articleVo,
				@PathVariable String articleNo) {
			Map<String, Object> result = new HashMap<>();
			//articleVo.setArticleNo(0);

			try {
				articleService.modifyArticle(articleVo);
				result.put("code",HttpStatus.OK);
			} catch (Exception e) {
				result.put("msg", "해당 글이 존재하지 않습니다.");
				result.put("code", HttpStatus.NOT_FOUND);
				e.printStackTrace();
			}

			return result;
		}
		
		@RequestMapping(value = "/board/{articleNo}/comment", method = RequestMethod.POST)
		public @ResponseBody int insertComment(@RequestBody ArticleReplyVo replyVo, HttpSession session) {
			MemberDTO member = (MemberDTO) session.getAttribute("memberSession");
			replyVo.setWriteMemberId(member.getMemberId());
			// TODO 뷰단에서 사용자명 계속 전달하는 것 다 걷어내고, 이 위치에서 세션에서 멤버ID 가져온다
			//세션을 어떻게 넘겨야하는가
			return articleService.insertComment(replyVo);
		}
		
		@RequestMapping(value = "/board/{articleNo}/comment", method = RequestMethod.GET, produces = "application/json; charset=utf8")
		public @ResponseBody List<ArticleReplyVo> getCommentdatas(@PathVariable("articleNo") String articleNo) {
			List<ArticleReplyVo> vo = articleService.getComments(articleNo);

			return vo;
		}
		
		@RequestMapping(value = "/board/{parentNo}/reply", method = RequestMethod.POST)
		public @ResponseBody Map<String,Object> writeReply(@RequestBody AticleVo articleVo, @PathVariable String parentNo) {
			Map<String, Object> result = new HashMap<>();

			if(5 != parentNo.length()) { // TODO 시퀀스는 5자리가 보장되도록 작업 해줄 것
				result.put("code", HttpStatus.BAD_REQUEST);
				result.put("msg", "입력 값이 올바르지 않습니다. 다시 확인 해주세요.");
				return result;
			}
			
			articleVo.setParentNo(parentNo);
			
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
		public @ResponseBody Map<String, Object> deleteArticle(@RequestBody AticleVo articleVo) {
			Map<String, Object> result = new HashMap<>();
			
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
