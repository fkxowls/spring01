package com.spring.study.board.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spring.study.ListPagingVo;
import com.spring.study.board.service.ArticleService;
import com.spring.study.board.vo.ArticleReplyVo;
import com.spring.study.board.vo.AticleVo;
import com.spring.study.board.vo.EndPagePaging;
import com.spring.study.board.vo.HasNextPaging;

@Controller
@SessionAttributes("nowArticleNo")
@RequestMapping("/board")
public class AticleController {
	private static final Logger logger = LoggerFactory.getLogger(AticleController.class);

	@Autowired
	ArticleService articleService;

	@RequestMapping(value = "listArticleForm", method = RequestMethod.GET)
	public String listArticleForm(Model model, @RequestParam(value = "startNum", required = false) String num) {
		logger.info("===========		listArticleForm() start	==============");
		
		EndPagePaging vo = new EndPagePaging();
		
		
		
		/*
		 * model.addAttribute("totalArticles", vo.getTotalCount());
		 * model.addAttribute("totalPage", vo.getTotalPage());
		 * model.addAttribute("articleList", vo.getList());
		 */
		
		return "board/listArticle2";
	
	}
						
	// endPage
	@RequestMapping(value = "/listArticle", produces = "application/json; charset=utf8")
	@ResponseBody
	public List<AticleVo> listArticle(@RequestParam(value = "endNum", required = false) String num) {
		logger.info("===========		listArticle() start	==============");

		//List<AticleVo> articleList = articleService.listArticle();

		return null;

	}
	// hasNext
	@RequestMapping(value = "/listArticle2", method = RequestMethod.GET)
	public String listArticle2(Model model, @RequestParam(value = "endNum", required = false) String endNum,
			@RequestParam(value = "isNext", required = false) String isNext) {
		logger.info("===========		listArticle() start	==============");
		System.out.println("endNum: " + endNum);
		System.out.println("isNext: " + isNext);
		HasNextPaging paging = articleService.paging2(endNum, isNext);
		List<AticleVo> list = articleService.listArticle2(paging);

		boolean _isNext = articleService.isNext(list);
		System.out.println("endNum: " + endNum);

		model.addAttribute("startNum", paging.getStartNum());
		model.addAttribute("endNum", paging.getEndNum());
		model.addAttribute("endPage", 100);
		model.addAttribute("isNext", _isNext);
		model.addAttribute("articleList", list);

		return "board/listArticle";

	}

	@RequestMapping(value = "/viewArticle", method = RequestMethod.GET)
	public String viewArticle(Model model, @RequestParam("articleNo") String articleNum) {
		AticleVo articleVo = new AticleVo();
		logger.info("===========		viewArticle() start	==============");
		int _articleNum = Integer.parseInt(articleNum);
		articleVo = articleService.viewArticle(_articleNum);
		model.addAttribute("articleVo", articleVo);

		return "board/viewArticle";
	}

	// @RequestBody
	@RequestMapping(value = "/modifyArticle", method = RequestMethod.POST)
	@ResponseBody
	public void modifArticle(@RequestBody AticleVo articleVo,HttpServletResponse resp) {
		logger.info("============		modifArticle() start		============");

		articleService.modifyArticle(articleVo);
	}

	@RequestMapping(value = "/inserComment", method = RequestMethod.POST)
	@ResponseBody
	public int insertComment(@RequestBody ArticleReplyVo replyVo) {
		logger.info("============		insertComment() start		============");
		logger.info("============		replyVo:{}", replyVo.getContent());

		// TODO 뷰단에서 사용자명 계속 전달하는 것 다 걷어내고, 이 위치에서 세션에서 멤버ID 가져온다
		return articleService.insertComment(replyVo);
	}

	@RequestMapping(value = "/testView", method = RequestMethod.GET)
	public String testView() {
		return "redirect:/board/viewArticle?articleNo=10";
	}

	@RequestMapping(value = "/commentList", produces = "application/json; charset=utf8")
	@ResponseBody // ?
	public List<ArticleReplyVo> listComment(@RequestParam("articleNo") String articleNo) {
		logger.info("============		listComment() start");
		logger.info("=========== No: {} ", articleNo);
		int _articleNo = Integer.parseInt(articleNo);
		List<ArticleReplyVo> vo = articleService.listComment(_articleNo);
		logger.info("============		listComment불러오는중");
		for (int i = 0; i < vo.size(); i++)
			logger.info("=============		vo:{}", vo.get(i));
		return vo;
	}

	@RequestMapping(value = "/deleteArticleForm", method = RequestMethod.POST)
	@ResponseBody
	public void deleteArticleForm(@RequestBody() AticleVo articleVo,HttpServletResponse resp) {
		logger.info("===========		deleteArticleForm() start		=================");
		try {
			resp.sendRedirect("/board/deleteArticle2.do?articleNo=" + articleVo.getArticleNo());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/deleteArticle2", method = RequestMethod.POST)
	public String deleteArticle(@RequestParam("articleNo") int articleNo) {
		logger.info("===========		deleteArticle() start		=================");
		articleService.deleteArticle(articleNo);
		return "redirect:/board/listArticle.do";
	}

	@RequestMapping(value = "/modifyForm", method = RequestMethod.POST)
	public String modifyForm(@ModelAttribute("articleVo") AticleVo articleVo, Model model) {
		logger.info("===========		modifyForm() start		==============");
		model.addAttribute("articleVo", articleVo);
		return "board/modifyForm";
	}

	@RequestMapping(value = "/inserReComment", method = RequestMethod.POST)
	@ResponseBody
	public void insertReComment(@RequestBody ArticleReplyVo replyVo) {
		logger.info("============		insertReComment() start		============");
		logger.info("============		replyVo{}:", replyVo.getParentNo());

		articleService.insertReComment(replyVo);
	}

	@RequestMapping(value = "/doWriteForm", method = RequestMethod.GET)
	public String form() {
		logger.info("=============		form() start		==============");
		String viewName = "board/addArticleForm";
		return viewName;
	}

	@RequestMapping(value = "/replyArticleForm", method = RequestMethod.POST)
	public String replyArticleForm(@ModelAttribute("articleNo") AticleVo articleVo, Model model) {
		logger.info("=============		replyArticleForm() start		==============");
		model.addAttribute("title", articleVo.getTitle());
		return "board/replyArticleForm";
		//
	}

	@RequestMapping(value = "/WrtiteArticle", method = RequestMethod.POST)
	public String writeArticle(@ModelAttribute("articleVo") AticleVo articleVo, HttpServletRequest req) {
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

	@RequestMapping(value = "/replyArticle", method = RequestMethod.POST)
	@ResponseBody
	public String replyArticle(@RequestBody AticleVo articleVo, RedirectAttributes redirectAttr,
			HttpServletRequest req) {
		logger.info("=============		replyArticle() start		==============");
		HttpSession session = req.getSession();
		articleVo.setArticleNo((Integer) session.getAttribute("nowArticleNo"));
		logger.info("=============		articleVo:{}", articleVo.getArticleNo());
		int num = articleService.replyArticle(articleVo);
		logger.info("=============		num:{}", num);
		redirectAttr.addAttribute("articleNo", num);
		return "board/viewArticle.do";
	}

}
