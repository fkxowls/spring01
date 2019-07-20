package com.spring.study.board.controller;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.SessionAttributes;

import com.spring.study.board.service.ArticleService;
import com.spring.study.board.vo.ArticleReplyVo;
import com.spring.study.board.vo.ArticleVo;
import com.spring.study.board.vo.CommonRequestDto;
import com.spring.study.board.vo.PagingResponseDTO;
import com.spring.study.member.vo.MemberDTO;

import javassist.NotFoundException;
import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;

//@Controller
@SessionAttributes("nowArticleNo")
// @RequestMapping("/board") // 이거 사용 지양
public class AticleController2 {
	private static final Logger logger = LoggerFactory.getLogger(AticleController2.class);
	private static final int pageSize = 10;

	@Autowired
	private ArticleService articleService;

	// endPage 페이징정보가져오기
	@RequestMapping(value = "/board/listArticleForm", method = RequestMethod.GET)
	public String listArticleForm(Model model) {
		logger.info("===========		listArticleForm() start	==============");

		// endPage
		PagingResponseDTO<ArticleVo> PagingResponseDTO = articleService.EndPagination(1, 10);
	
		model.addAttribute("totalPage", PagingResponseDTO.getTotalPage());
		model.addAttribute("articleList", PagingResponseDTO.getList());

		return "board/listArticle2";
	}

	// endPage 더보기버튼
	@RequestMapping(value = "/board/ajaxArticle")
	public String ajaxArticle(Model model, @RequestParam int page) {
		logger.info("===========		listArticle() start	==============");

		List<ArticleVo> articleList = articleService.EndPagingMore(page, 10);
		model.addAttribute("articleList", articleList);

		return "/WEB-INF/views/board/ajaxArticle";

	}
	// endPage restAPI
	@RequestMapping(value = "/board/article/{page}/datas")
	public @ResponseBody Map<String, Object> getArticleDatas(@PathVariable int page) {
		Map<String, Object> result = new HashMap<>();

		if (1 == page) {
			PagingResponseDTO<ArticleVo> articlePageDto = articleService.EndPaging(1, pageSize);

			result.put("totalPage", String.valueOf(articlePageDto.getTotalPage()));
			result.put("articleList", articlePageDto.getList());
		} else {
			List<ArticleVo> articleList = articleService.EndPagingMore(page, pageSize);

			result.put("nextPage", "0");
			result.put("totalPage", "0");
			result.put("articleList", articleList);
		}

		return result;
	}

	// hasNext 페이징정보가져오기
	@RequestMapping(value = "/board/listArticle2", method = RequestMethod.GET)
	public String listArticle2(Model model) {
		logger.info("===========		listArticle() start	==============");

		// hasNext
		PagingResponseDTO<ArticleVo> articlePageDto = articleService.hasNextPaging(1, 10);
		model.addAttribute("hasNext", articlePageDto.getHasNext());
		model.addAttribute("articleList", articlePageDto.getList());

		return "board/listArticle2";
	}

	// hasNext
	@RequestMapping(value = "/board/ajaxArticle2")
	public String ajaxArticle2(Model model, @RequestParam int page) {
		logger.info("===========		listArticle() start	==============");

		PagingResponseDTO<ArticleVo> articlePageDto = articleService.hasNextPagingMore(page, 10);
		model.addAttribute("articleList", articlePageDto.getList());
		model.addAttribute("hasNext", articlePageDto.getHasNext());

		return "/WEB-INF/views/board/ajaxArticle";
	}

	// hasNext
	@RequestMapping(value = "/board/article/{page}/datas2")
	public @ResponseBody Map<String, Object> getArticleDatas2(Model model, @PathVariable int page) {
		PagingResponseDTO<ArticleVo> articlePageDto = articleService.hasNextPagingMore(page, pageSize);

		Map<String, Object> result = new HashMap<>();
		result.put("articleList", articlePageDto.getList());
		result.put("hasNext", String.valueOf(articlePageDto.getHasNext()));

		return result;
	}

	@RequestMapping(value = "/board/viewArticle", method = RequestMethod.GET)
	public String viewArticle(Model model, @RequestParam("articleNo") String articleNum) {
		ArticleVo articleVo = new ArticleVo();
		logger.info("===========		viewArticle() start	==============");

		int _articleNum = Integer.parseInt(articleNum);
		articleVo = articleService.viewArticle(articleNum);

		model.addAttribute("articleVo", articleVo);

		return "board/viewArticle";
	}

	@RequestMapping(value = "/board/{articleNo}", method = RequestMethod.GET)
	public @ResponseBody ArticleVo viewArticle2(Model model, @PathVariable String articleNo) {
		ArticleVo articleVo = articleService.viewArticle(articleNo);

		return articleVo;
	}

	@RequestMapping(value = "/board/modifyArticle", method = RequestMethod.POST)
	@ResponseBody
	public void modifyArticle(@RequestBody ArticleVo articleVo) {
		logger.info("============		modifArticle() start		============");

		try {
			articleService.modifyArticle(articleVo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/board/{articleNo}", method = RequestMethod.PUT)
	public @ResponseBody Map<String, Object> modifyArticle2(@RequestBody ArticleVo articleVo,
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

	@RequestMapping(value = "/board/writeComment", method = RequestMethod.POST)
	@ResponseBody
	public int insertComment(@RequestBody ArticleReplyVo replyVo, HttpSession session) {
		logger.info("============		insertComment() start		============");
		logger.info("============		replyVo:{}", replyVo.getContent());

		MemberDTO member = (MemberDTO) session.getAttribute("memberSession");
		replyVo.setWriteMemberId(member.getMemberId());
		// TODO 뷰단에서 사용자명 계속 전달하는 것 다 걷어내고, 이 위치에서 세션에서 멤버ID 가져온다
		return articleService.insertComment(replyVo);
	}
	
	@RequestMapping(value = "/board/{articleNo}/comment", method = RequestMethod.POST)
	public @ResponseBody int insertComment2(@RequestBody ArticleReplyVo replyVo, HttpSession session) {
		MemberDTO member = (MemberDTO) session.getAttribute("memberSession");
		replyVo.setWriteMemberId(member.getMemberId());
		// TODO 뷰단에서 사용자명 계속 전달하는 것 다 걷어내고, 이 위치에서 세션에서 멤버ID 가져온다
		//세션을 어떻게 넘겨야하는가
		return articleService.insertComment(replyVo);
	}

	@RequestMapping(value = "/board/commentList", produces = "application/json; charset=utf8")
	@ResponseBody // ?
	public List<ArticleReplyVo> listComment(@RequestParam("articleNo") String articleNo) {
		logger.info("============		listComment() start");

		List<ArticleReplyVo> vo = articleService.getComments(articleNo);

		return vo;
	}
	
	@RequestMapping(value = "/board/{articleNo}/comment", method = RequestMethod.GET, produces = "application/json; charset=utf8")
	public @ResponseBody List<ArticleReplyVo> getCommentdatas(@PathVariable("articleNo") String articleNo) {
		List<ArticleReplyVo> vo = articleService.getComments(articleNo);

		return vo;
	}

	// transaction 적용
	@RequestMapping(value = "/board/replyArticle", method = RequestMethod.POST)
	@ResponseBody
	public String replyArticle(@RequestBody ArticleVo articleVo) {
		logger.info("=============		replyArticle() start		==============");

		int num = 0;

		try {
			articleService.replyArticle(articleVo);
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/board/listArticleForm.do";
		}

		return "redirect:/board/viewArticle.do?article=" + num;
	}
	
	@RequestMapping(value = "/board/{parentNo}/reply", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> writeReply(@RequestBody ArticleVo articleVo, @PathVariable String parentNo) {
		Map<String, Object> result = new HashMap<>();
/*
		if(5 != parentNo.length()) { // TODO 시퀀스는 5자리가 보장되도록 작업 해줄 것
			result.put("code", HttpStatus.BAD_REQUEST);
			result.put("msg", "입력 값이 올바르지 않습니다. 다시 확인 해주세요.");
			return result;
		}
*/		
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
	
	@RequestMapping(value = "/board/deleteArticle", method = RequestMethod.POST)
	public @ResponseBody String deleteArticle(@RequestBody ArticleVo articleVo) {
		logger.info("===========		deleteArticle() start		=================");
		
		try {
			articleService.deleteArticle(articleVo);
		} catch (Exception e) {
			logger.info("댓글이 달린 글은 삭제 불가");
			e.printStackTrace();
		}

		return "redirect:/board/listArticle.do";
	}
/*	
	@RequestMapping(value = "/board/{articleNo}", method = RequestMethod.DELETE)
	public @ResponseBody Map<String, Object> deleteArticle2(@PathVariable String articleNo) {
		Map<String, Object> result = new HashMap<>();
		
		try {
			articleService.deleteArticle(articleNo);
			result.put("msg",HttpStatus.OK);
		} catch (Exception e) {
			result.put("msg", "댓글이 달린 글은 삭제할 수 없습니다.");
			result.put("code", HttpStatus.CONFLICT);
			e.printStackTrace();
		}

		return result;
	}
*/	
	@RequestMapping(value = "/board/writeReComment", method = RequestMethod.POST)
	@ResponseBody
	public void insertReComment(@RequestBody ArticleReplyVo replyVo, HttpSession session) {
		logger.info("============		insertReComment() start		============");

		MemberDTO member = (MemberDTO) session.getAttribute("memberSession");
		replyVo.setWriteMemberId(member.getMemberId());
		articleService.insertReComment(replyVo);

	}

	@RequestMapping(value = "/board/modifyForm", method = RequestMethod.POST)
	public String modifyForm(@ModelAttribute("articleVo") ArticleVo articleVo, Model model) {
		logger.info("===========		modifyForm() start		==============");

		model.addAttribute("articleVo", articleVo);

		return "board/modifyForm";
	}
	
	@RequestMapping(value = "/board/writeArticleForm", method = RequestMethod.GET)
	public String form() {
		logger.info("=============		form() start		==============");

		String viewName = "board/addArticleForm";

		return viewName;
	}

	@RequestMapping(value = "/board/writeReplyForm", method = RequestMethod.POST)
	public String replyArticleForm(@RequestParam("articleNo") String articleNo, @RequestParam("title") String title,
			Model model) {
		logger.info("=============		replyArticleForm() start		==============");

		model.addAttribute("articleNo", articleNo);
		model.addAttribute("title", title);
		return "board/replyArticleForm";

	}

	@RequestMapping(value = "/board/WrtiteArticle", method = RequestMethod.POST)
	public String writeArticle(@ModelAttribute("articleVo") ArticleVo articleVo, HttpServletRequest req) {
		logger.info("=============		writeArticle() start		==============");

		try {
			req.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		articleService.writeArticle(articleVo);
		int articleNo = articleService.getSequence();

		return "redirect:/board/viewArticle.do?articleNo=" + articleNo;
	}

}
