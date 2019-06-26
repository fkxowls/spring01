package com.spring.study.board.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
// @RequestMapping("/board") // 이거 사용 지양
public class AticleController {
	private static final Logger logger = LoggerFactory.getLogger(AticleController.class);

	@Autowired
	ArticleService articleService;

	@RequestMapping(value = "/board/listArticleForm", method = RequestMethod.GET)
	public String listArticleForm(Model model, @RequestParam(value = "page", required = false) String page) {
		logger.info("===========		listArticleForm() start	==============");

		int _page = page == null ? 1 : Integer.parseInt(page);

		//EndPagePaging vo = articleService.EndPaging();
		
		//model.addAttribute("totalArticles", vo.getTotalCount());
		//model.addAttribute("totalPage", vo.getTotalPage());
		//model.addAttribute("articleList", vo.getList());
		
		
		HasNextPaging vo2 = articleService.hasNextPaging(_page);

		model.addAttribute("page", _page);
		model.addAttribute("articleList", vo2.getList());

		return "board/listArticle2";

	

	}

	// endPage
	@RequestMapping(value = "/board/listArticle")
	//@ResponseBody										//"/board/endNum"
	public String listArticle(Model model,@RequestParam(value = "page", required = false) int page) {
		logger.info("===========		listArticle() start	==============");
	
		EndPagePaging vo = articleService.setStartNum(page);
		model.addAttribute("startNum", vo.getStartNum());
		model.addAttribute("endNum", vo.getEndNum());
		
		
		return "board/contentsList";

	}

	// hasNext
	@RequestMapping(value = "/board/listArticle2", method = RequestMethod.GET)
	public String listArticle2(Model model, @RequestParam(value = "page", required = false) String page) {
		logger.info("===========		listArticle() start	==============");
		
		int _page = page == null ? 1 : Integer.parseInt(page);
		
		HasNextPaging vo = new HasNextPaging();
		vo = articleService.hasNextPaging(_page);
		model.addAttribute("articleList", vo.getList());
		model.addAttribute("hasNext", vo.isHasNext());	
		logger.info("=========== 		hasNext(): {}",vo.isHasNext());	
		
		return "board/listArticle2";

	}

	@RequestMapping(value = "/board/viewArticle", method = RequestMethod.GET)
	public String viewArticle(Model model, @RequestParam("articleNo") String articleNum) {
		AticleVo articleVo = new AticleVo();
		logger.info("===========		viewArticle() start	==============");
		int _articleNum = Integer.parseInt(articleNum);
		articleVo = articleService.viewArticle(_articleNum);
		model.addAttribute("articleVo", articleVo);

		return "board/viewArticle";
	}

	// @RequestBody
	@RequestMapping(value = "/board/modifyArticle", method = RequestMethod.POST)
	@ResponseBody
	public void modifArticle(@RequestBody AticleVo articleVo, HttpServletResponse resp) {
		logger.info("============		modifArticle() start		============");

		articleService.modifyArticle(articleVo);
	}

	@RequestMapping(value = "/board/inserComment", method = RequestMethod.POST)
	@ResponseBody
	public int insertComment(@RequestBody ArticleReplyVo replyVo) {
		logger.info("============		insertComment() start		============");
		logger.info("============		replyVo:{}", replyVo.getContent());

		// TODO 뷰단에서 사용자명 계속 전달하는 것 다 걷어내고, 이 위치에서 세션에서 멤버ID 가져온다
		return articleService.insertComment(replyVo);
	}

	@RequestMapping(value = "/board/testView", method = RequestMethod.GET)
	public String testView() {
		return "redirect:/board/viewArticle?articleNo=10";
	}

	@RequestMapping(value = "/board/commentList", produces = "application/json; charset=utf8")
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

	@RequestMapping(value = "/board/deleteArticleForm", method = RequestMethod.POST)
	@ResponseBody
	public void deleteArticleForm(@RequestBody() AticleVo articleVo, HttpServletResponse resp) {
		logger.info("===========		deleteArticleForm() start		=================");
		try {
			resp.sendRedirect("/board/deleteArticle2.do?articleNo=" + articleVo.getArticleNo());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/board/deleteArticle2", method = RequestMethod.POST)
	public String deleteArticle(@RequestParam("articleNo") int articleNo) {
		logger.info("===========		deleteArticle() start		=================");
		articleService.deleteArticle(articleNo);
		return "redirect:/board/listArticle.do";
	}

	@RequestMapping(value = "/board/modifyForm", method = RequestMethod.POST)
	public String modifyForm(@ModelAttribute("articleVo") AticleVo articleVo, Model model) {
		logger.info("===========		modifyForm() start		==============");
		model.addAttribute("articleVo", articleVo);
		return "board/modifyForm";
	}

	@RequestMapping(value = "/board/inserReComment", method = RequestMethod.POST)
	@ResponseBody
	public void insertReComment(@RequestBody ArticleReplyVo replyVo) {
		logger.info("============		insertReComment() start		============");
		logger.info("============		replyVo{}:", replyVo.getParentNo());

		articleService.insertReComment(replyVo);
	}

	@RequestMapping(value = "/board/doWriteForm", method = RequestMethod.GET)
	public String form() {
		logger.info("=============		form() start		==============");
		String viewName = "board/addArticleForm";
		return viewName;
	}

	@RequestMapping(value = "/board/replyArticleForm", method = RequestMethod.POST)
	public String replyArticleForm(@ModelAttribute("articleNo") AticleVo articleVo, Model model) {
		logger.info("=============		replyArticleForm() start		==============");
		model.addAttribute("title", articleVo.getTitle());
		return "board/replyArticleForm";
		//
	}

	@RequestMapping(value = "/board/WrtiteArticle", method = RequestMethod.POST)
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

	@RequestMapping(value = "/board/replyArticle", method = RequestMethod.POST)
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
