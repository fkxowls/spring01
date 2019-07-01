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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spring.study.board.service.ArticleService;
import com.spring.study.board.vo.ArticleReplyVo;
import com.spring.study.board.vo.AticleVo;
import com.spring.study.board.vo.PageDto;
import com.spring.study.member.vo.MemberDTO;

@Controller
@SessionAttributes("nowArticleNo")
// @RequestMapping("/board") // 이거 사용 지양
public class AticleController {
	private static final Logger logger = LoggerFactory.getLogger(AticleController.class);
	private static final int pageSize = 10;

	@Autowired
	ArticleService articleService;

	// endPage
	@RequestMapping(value = "/board/listArticleForm", method = RequestMethod.GET)
	public String listArticleForm(Model model) {
		logger.info("===========		listArticleForm() start	==============");

		// endPage
		PageDto<AticleVo> articlePageDto = articleService.EndPaging(1, 10);
		model.addAttribute("nextPage", articlePageDto.getNextPage());
		model.addAttribute("totalPage", articlePageDto.getTotalPage());
		model.addAttribute("articleList", articlePageDto.getList());

		return "board/listArticle2";

	

	}

	// endPage
	@RequestMapping(value = "/board/ajaxArticle")
	public String ajaxArticle(Model model, @RequestParam int page) {
		logger.info("===========		listArticle() start	==============");
	
		List<AticleVo> articleList = articleService.EndPagingMore(page, 10);
		model.addAttribute("articleList", articleList);
		
		return "/WEB-INF/views/board/ajaxArticle";

	}
	
	@RequestMapping(value = "/board/article/{page}/datas")
	public @ResponseBody Map<String, Object> getArticleDatas(Model model, @PathVariable int page) {
		Map<String, Object> result = new HashMap<>();
		
		if(1 == page) {
			PageDto<AticleVo> articlePageDto = articleService.EndPaging(1, pageSize);
			
			result.put("nextPage", String.valueOf(articlePageDto.getNextPage()));
			result.put("totalPage", String.valueOf(articlePageDto.getTotalPage()));
			result.put("articleList", articlePageDto.getList());
		} else {
			List<AticleVo> articleList = articleService.EndPagingMore(1, pageSize);

			result.put("nextPage", "0");
			result.put("totalPage", "0");
			result.put("articleList", articleList);
		}
		
		return result;
	}

	// hasNext
	@RequestMapping(value = "/board/listArticle2", method = RequestMethod.GET)
	public String listArticle2(Model model) {
		logger.info("===========		listArticle() start	==============");
		
		// hasNext
		PageDto<AticleVo> articlePageDto = articleService.hasNextPaging(1, 10);
		model.addAttribute("hasNext", articlePageDto.getHasNext());
		model.addAttribute("nextPage", articlePageDto.getNextPage());
		model.addAttribute("articleList", articlePageDto.getList());
		
		return "board/listArticle2";
	}

	// hasNext
	@RequestMapping(value = "/board/ajaxArticle2")
	public String ajaxArticle2(Model model, @RequestParam int page) {
		logger.info("===========		listArticle() start	==============");
	
		PageDto<AticleVo> articlePageDto = articleService.hasNextPagingMore(page, 10);
		model.addAttribute("articleList", articlePageDto.getList());
		model.addAttribute("hasNext", articlePageDto.getHasNext());
		
		return "/WEB-INF/views/board/ajaxArticle";

	}

	// hasNext
	@RequestMapping(value = "/board/article/{page}/datas2")
	public @ResponseBody Map<String, Object> getArticleDatas2(Model model, @PathVariable int page) {
		PageDto<AticleVo> articlePageDto = articleService.hasNextPagingMore(page, pageSize);
		
		Map<String, Object> result = new HashMap<>();
		result.put("articleList", articlePageDto.getList());
		result.put("hasNext", String.valueOf(articlePageDto.getHasNext()));
		
		return result;
	}

	@RequestMapping(value = "/board/viewArticle", method = RequestMethod.GET)
	public String viewArticle(Model model, @RequestParam("articleNo") String articleNum) {
		AticleVo articleVo = new AticleVo();
		logger.info("===========		viewArticle() start	==============");
		int _articleNum = Integer.parseInt(articleNum);
		articleVo = articleService.viewArticle(_articleNum);
		
		//session.setAttribute("nowArticleNo", _articleNum);
		
		model.addAttribute("articleVo", articleVo);

		return "board/viewArticle";
	}

	@RequestMapping(value = "/board/modifyArticle", method = RequestMethod.POST)
	@ResponseBody
	public void modifArticle(@RequestBody AticleVo articleVo) {
		logger.info("============		modifArticle() start		============");

		articleService.modifyArticle(articleVo);
	}

	@RequestMapping(value = "/board/inserComment", method = RequestMethod.POST)
	@ResponseBody
	public int insertComment(@RequestBody ArticleReplyVo replyVo,HttpSession session) {
		logger.info("============		insertComment() start		============");
		logger.info("============		replyVo:{}", replyVo.getContent());
		
		MemberDTO member = (MemberDTO) session.getAttribute("memberSession");
		replyVo.setWriteMemberId(member.getMemberId());
		// TODO 뷰단에서 사용자명 계속 전달하는 것 다 걷어내고, 이 위치에서 세션에서 멤버ID 가져온다
		return articleService.insertComment(replyVo);
	}

	@RequestMapping(value = "/board/testView", method = RequestMethod.GET)
	public String testView() {
		return "redirect:/board/viewArticle?articleNo=10";
	}

	@RequestMapping(value = "/board/commentList", produces = "application/json; charset=utf8")
	@ResponseBody // ?
	public List<ArticleReplyVo> listComment(@RequestParam("articleNo") int articleNo) {
		logger.info("============		listComment() start");
		
		//int articleNo = (int) session.getAttribute("nowArticleNo");
		List<ArticleReplyVo> vo = articleService.listComment(articleNo);
		
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
	public void insertReComment(@RequestBody ArticleReplyVo replyVo, HttpSession session) {
		logger.info("============		insertReComment() start		============");
		logger.info("============		replyVo{}:", replyVo.getParentNo());
		
		MemberDTO member = (MemberDTO) session.getAttribute("memberSession");
		replyVo.setWriteMemberId(member.getMemberId());
		articleService.insertReComment(replyVo);
	}

	@RequestMapping(value = "/board/doWriteForm", method = RequestMethod.GET)
	public String form() {
		logger.info("=============		form() start		==============");
		String viewName = "board/addArticleForm";
		return viewName;
	}

	@RequestMapping(value = "/board/replyArticleForm", method = RequestMethod.POST)
	public String replyArticleForm(@RequestParam("articleNo") int articleNo, @RequestParam("title") String title, Model model) {
		logger.info("=============		replyArticleForm() start		==============");
		System.out.println(title);
		model.addAttribute("articleNo", articleNo);
		model.addAttribute("title", title);
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
