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

@Controller
@SessionAttributes("nowArticleNo")
// @RequestMapping("/board") // 이거 사용 지양
public class AticleController {
	private static final Logger logger = LoggerFactory.getLogger(AticleController.class);
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

	// endPage moreView
	@RequestMapping(value = "/board/ajaxArticle")
	public String ajaxArticle(Model model, @RequestParam int page) {
		logger.info("===========		listArticle() start	==============");

		List<ArticleVo> articleList = articleService.EndPagingMore(page, 10);
		model.addAttribute("articleList", articleList);

		return "/WEB-INF/views/board/ajaxArticle";

	}

	// hasNext 페이징정보가져오기
	@RequestMapping(value = "/board/listArticle2", method = RequestMethod.GET)
	public String listArticle2(Model model) {
		logger.info("===========		listArticle() start	==============");

		PagingResponseDTO<ArticleVo> articlePageDto = articleService.hasNextPaging(1, 10);
		model.addAttribute("hasNext", articlePageDto.getHasNext());
		model.addAttribute("articleList", articlePageDto.getList());

		return "board/listArticle2";
	}

	// hasNext moreView
	@RequestMapping(value = "/board/ajaxArticle2")
	public String ajaxArticle2(Model model, @RequestParam int page) {
		logger.info("===========		listArticle() start	==============");

		PagingResponseDTO<ArticleVo> articlePageDto = articleService.hasNextPagingMore(page, 10);
		model.addAttribute("articleList", articlePageDto.getList());
		model.addAttribute("hasNext", articlePageDto.getHasNext());

		return "/WEB-INF/views/board/ajaxArticle";
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
	
	//댓글쓰기
	@RequestMapping(value = "/board/writeComment", method = RequestMethod.POST)
	@ResponseBody
	public int insertComment(@RequestBody ArticleReplyVo replyVo, HttpSession session) {
		logger.info("============		insertComment() start		============");
		logger.info("============		replyVo:{}", replyVo.getContent());

		MemberDTO member = (MemberDTO) session.getAttribute("memberSession");
		replyVo.setWriteMemberId(member.getMemberId());
		
		return articleService.insertComment(replyVo);
	}
	
	
	
	//글쓰기(수정)
	@RequestMapping(value = "/board/modifyArticle", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> modifyArticle(@RequestBody ArticleVo articleVo) {
		logger.info("============		modifArticle() start		============");
		Map<String, String> result = new HashMap<>(); 
		
		try {
			articleService.modifyArticle(articleVo);
			System.out.println("1");
			result.put("msg","글 수정 완료");
		} catch (NotFoundException e) {
			System.out.println("2");
			result.put("msg", e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("3");
			result.put("msg", "알수없는 오류입니다");
			e.printStackTrace();
		}finally {
			result.put("redirect","/board/viewArticle.do?articleNo="+articleVo.getArticleNo());			
		}
		
		return result;
	}
	
	//글쓰기(답글)
	// transaction 적용
	@RequestMapping(value = "/board/replyArticle", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> replyArticle(@RequestBody ArticleVo articleVo) {
		logger.info("=============		replyArticle() start		==============");
		Map<String, String> result = new HashMap<>(); 
		
		try {
			articleService.replyArticle(articleVo);
			result.put("msg","답글달기 성공");
			result.put("redirect","/board/viewArticle.do?articleNo="+articleVo.getArticleNo());
		} catch (NotFoundException e) {
			result.put("msg", e.getMessage());
			result.put("redirect","/board/listArticleForm.do");
			e.printStackTrace();
		} catch(Exception e) {
			result.put("msg", "알수없는 오류입니다");
			result.put("redirect","/board/listArticleForm.do");
			e.printStackTrace();
		}

		return result;
	}
	
	@RequestMapping(value = "/board/deleteArticle", method = RequestMethod.POST)
	public @ResponseBody Map<String, String> deleteArticle(@RequestBody ArticleVo articleVo) {
		logger.info("===========		deleteArticle() start		=================");
		Map<String, String> result = new HashMap<>(); 
		
		try {
			articleService.deleteArticle(articleVo);
			result.put("redirect","/board/listArticleForm");
			result.put("msg","글이 삭제되었습니다");
		} catch (Exception e) {
			result.put("msg",e.getMessage());
			e.printStackTrace();
		}

		return result;
	}
	
	//댓글쓰기(대댓글)
	@RequestMapping(value = "/board/writeReComment", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> insertReComment(@RequestBody ArticleReplyVo replyVo, HttpSession session) {
		logger.info("============		insertReComment() start		============");
		Map<String, String> result = new HashMap<>(); 
		
		//여기서 일어날 수 있는 오류는???
		MemberDTO member = (MemberDTO) session.getAttribute("memberSession");
		replyVo.setWriteMemberId(member.getMemberId());
		articleService.insertReComment(replyVo);

		return result;
	}
	
	//댓글목록
	@RequestMapping(value = "/board/commentList", produces = "application/json; charset=utf8")
	@ResponseBody // ?
	public List<ArticleReplyVo> listComment(@RequestParam("articleNo") String articleNo) {
		logger.info("============		listComment() start");

		List<ArticleReplyVo> vo = articleService.getComments(articleNo);

		return vo;
	}

	//글쓰기페이지
		@RequestMapping(value = "/board/writeArticleForm", method = RequestMethod.GET)
		public String form() {
			logger.info("=============		form() start		==============");

			String viewName = "board/addArticleForm";

			return viewName;
		}
	
	//글쓰기페이지(답글)
	@RequestMapping(value = "/board/writeReplyForm", method = RequestMethod.POST)
	public String replyArticleForm(@RequestParam("articleNo") String articleNo, @RequestParam("title") String title,
			Model model) {
		logger.info("=============		replyArticleForm() start		==============");

		model.addAttribute("articleNo", articleNo);
		model.addAttribute("title", title);
		return "board/replyArticleForm";

	}
	
	//글수정페이지
	@RequestMapping(value = "/board/modifyForm", method = RequestMethod.POST)
	public String modifyForm(@ModelAttribute("articleVo") ArticleVo articleVo, Model model) {
		logger.info("===========		modifyForm() start		==============");
		
		model.addAttribute("articleVo", articleVo);
		
		return "board/modifyForm";
		}

}
