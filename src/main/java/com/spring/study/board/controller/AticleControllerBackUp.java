package com.spring.study.board.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
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
import com.spring.study.board.vo.HasNextPaging;


//@Controller			
//@SessionAttributes("nowArticleNo")
//@RequestMapping("/board")
public class AticleControllerBackUp {
	private static final Logger logger = LoggerFactory.getLogger(AticleControllerBackUp.class);
	
	@Autowired
	ArticleService articleService;
	/*
	 //제일 처음 어거지 리스트
	@RequestMapping(value="/listArticle", method=RequestMethod.GET)
	public String listArticle(Model model,HttpServletRequest req) {
		logger.info("===========		listArticle() start	==============");
		
		
		PageNumVo pnv = articleService.pageNum(req);
		logger.info("===========		pageNum:{}",pnv.getPageNum());
		List article = articleService.listArticle(pnv.getPageNum());
		int totalArticle = articleService.getTotalArticles();
	
		model.addAttribute("pageNum",pnv.getPageNum());
		model.addAttribute("totalArticle",totalArticle);
		model.addAttribute("startPage",pnv.getStartPage());
		model.addAttribute("articleList",article);

		return "board/listArticle";
	}
	*/
	@RequestMapping(value="listArticleForm", method=RequestMethod.GET)
	public String listArticleForm(Model model, @RequestParam(value="startNum", required=false) String num) {
		logger.info("===========		listArticleForm() start	==============");
		
		return "board/listArticle";
	}
	/*
	//endPage기반 중간 페이징처리 x 첫시작과 끝시작 / 쿼리 인덱스 근데 리스트페이징 정보랑 글목록이랑 어떻게 ajax로 추가함??	
	@RequestMapping(value="/listArticle",method= RequestMethod.GET)
	@ResponseBody
	public List<AticleVo> listArticle(Model model, @RequestParam(value="startNum", required=false) String num
			,@RequestBody() AticleVo articleVo) {
		logger.info("===========		listArticle() start	==============");
		
		ListPagingVo paging = articleService.paging(num);
		List<AticleVo> articleList = articleService.listArticle(paging);
		
		model.addAttribute("startNum",paging.getStartNum());
		model.addAttribute("endNum",paging.getEndNum());
		model.addAttribute("endPage",paging.getEndPage());
		model.addAttribute("articleList", articleList);
		
		return articleList;

	}
	*/
	@RequestMapping(value="/listArticle2",method= RequestMethod.GET)
	public String listArticle2(Model model, @RequestParam(value="endNum", required=false) String endNum
			,@RequestParam(value="isNext", required=false) String isNext) {
		logger.info("===========		listArticle() start	==============");
		System.out.println("endNum: "+endNum);
		System.out.println("isNext: "+isNext);
		HasNextPaging paging = articleService.paging2(endNum,isNext);
		List<AticleVo> list = articleService.listArticle2(paging);
	
		boolean _isNext = articleService.isNext(list);
		System.out.println("endNum: "+endNum);
		
		model.addAttribute("startNum",paging.getStartNum());
		model.addAttribute("endNum",paging.getEndNum());
		model.addAttribute("endPage",100);
		model.addAttribute("isNext",_isNext);
		model.addAttribute("articleList", list);
		
		return "board/listArticle";

	}
	
	@RequestMapping(value="/viewArticle", method=RequestMethod.GET)
	public String viewArticle(Model model,@RequestParam("articleNo")int articleNum, HttpServletRequest req) {
		AticleVo articleVo = new AticleVo();
		logger.info("===========		viewArticle() start	==============");

		//model.addAttribute("nowArticleNo",articleNum);
		//HttpSession session = req.getSession();
		//session.setAttribute("nowArticleNo", articleNum);
		articleVo = articleService.viewArticle(articleNum);
		model.addAttribute("articleVo",articleVo);
		
		return "board/viewArticle";
	}
	
	//@RequestBody
	@RequestMapping(value="/modifyArticle",method=RequestMethod.POST)
	@ResponseBody
	public void modifArticle(@RequestBody AticleVo articleVo) {
		logger.info("============		modifArticle() start		============");	
		
		articleService.modifyArticle(articleVo);

	}
	
	@RequestMapping(value="/inserComment",method=RequestMethod.POST)
	@ResponseBody
	public void insertComment(@RequestBody ArticleReplyVo replyVo) {
		logger.info("============		insertComment() start		============");
		logger.info("============		replyVo:{}",replyVo.getContent());
		
		articleService.insertComment(replyVo);	
	}
	
	@RequestMapping(value="/testView",method=RequestMethod.GET)
	public String testView() {
		return "redirect:/board/viewArticle?articleNo=10";
	}
	
	@RequestMapping(value="/commentList", produces="application/json; charset=utf8")
	@ResponseBody//												?
	public List<ArticleReplyVo> listComment(HttpServletRequest req){
		logger.info("============		listComment() start");
		HttpSession session = req.getSession();
		int articleNo = (Integer) session.getAttribute("nowArticleNo");
		List<ArticleReplyVo> vo = articleService.listComment(articleNo);
		for(int i=0; i<vo.size(); i++)
			logger.info("=============		vo:{}",vo.get(i));
		return  vo;
	}
	
	@RequestMapping(value="/deleteArticleForm",method=RequestMethod.POST)
	@ResponseBody
	public String deleteArticleForm(@RequestBody() AticleVo articleVo,Model model) {
		logger.info("===========		deleteArticleForm() start		=================");	
		
		return "/board/deleteArticle2.do?articleNo="+articleVo.getArticleNo();
		//return "redirect:/board/listArticle.do";
	}
	
	@RequestMapping(value="/deleteArticle2",method=RequestMethod.POST)
	//@ResponseBody
	//public String deleteArticle(@RequestBody() AticleVo articleVo) {	
	public String deleteArticle(@RequestParam("articleNo") int articleNo) {
		logger.info("===========		deleteArticle() start		=================");	
		//articleService.deleteArticle(articleVo.getArticleNo());
		articleService.deleteArticle(articleNo);
		return "redirect:/board/listArticle.do";
	}
	
	@RequestMapping(value="/modifyForm", method=RequestMethod.POST)
	public String modifyForm(@ModelAttribute("articleVo") AticleVo articleVo, Model model) {
		logger.info("===========		modifyForm() start		==============");
		model.addAttribute("articleVo", articleVo);
		return "board/modifyForm";
	}
	
	/*
	@RequestMapping(value="/modifyForm", method=RequestMethod.POST)
	public String modifyForm(@ModelAttribute("articleVo") AticleVo articleVo, Model model) {
		logger.info("===========		modifyForm() start		==============");
		
		model.addAttribute("articleVo", articleVo);
		
		return "board/modifyForm";
	}
	*/
	
	
	@RequestMapping(value="/inserReComment",method=RequestMethod.POST)
	@ResponseBody
	public void insertReComment(@RequestBody ArticleReplyVo replyVo) {
		logger.info("============		insertReComment() start		============");
		logger.info("============		replyVo{}:",replyVo.getContent());
		
		articleService.insertReComment(replyVo);	
	}

	@RequestMapping(value="/doWriteForm", method=RequestMethod.GET)
	public String form() {
		logger.info("=============		form() start		==============");	
		String viewName = "board/addArticleForm";
		return viewName;
	}
	
	@RequestMapping(value="/replyArticleForm",method=RequestMethod.POST)
	public String replyArticleForm(@ModelAttribute("articleNo") AticleVo articleVo,Model model) {
		logger.info("=============		replyArticleForm() start		==============");
		model.addAttribute("title", articleVo.getTitle());
		return "board/replyArticleForm";
		//
	}
	
	
	@RequestMapping(value="/WrtiteArticle", method=RequestMethod.POST)
	public String writeArticle(@ModelAttribute("articleVo") AticleVo articleVo,HttpServletRequest req) {
		logger.info("=============		writeArticle() start		==============");	
		try {
			req.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		articleService.writeArticle(articleVo);
		int articleNo = articleService.getSequence();
		
		return "redirect:/board/viewArticle.do?articleNo="+articleNo;
	}
	

	  @RequestMapping(value="/replyArticle", method=RequestMethod.POST)
	  @ResponseBody
	  public String replyArticle(@RequestBody AticleVo articleVo,RedirectAttributes redirectAttr, HttpServletRequest req) {
		  logger.info("=============		replyArticle() start		==============");
		  HttpSession session = req.getSession();
		  articleVo.setArticleNo((Integer) session.getAttribute("nowArticleNo"));
		  logger.info("=============		articleVo:{}",articleVo.getArticleNo());
		  int num = articleService.replyArticle(articleVo);
		  logger.info("=============		num:{}",num);		
		  redirectAttr.addAttribute("articleNo",num);
		  return "board/viewArticle.do"; 
	  }
	  
	
	 
}
