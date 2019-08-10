package com.spring.study.board.controller;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
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

import com.spring.study.board.model.ArticleVo;
import com.spring.study.board.model.CommonRequestDto;
import com.spring.study.board.service.ArticleService;
import com.spring.study.comment.model.CommentPageList;
import com.spring.study.comment.model.CommentsRequestDto;
import com.spring.study.comment.model.CommentsVo;
import com.spring.study.comment.service.CommentsService;
import com.spring.study.common.model.PageList;
import com.spring.study.member.model.Member;

import javassist.NotFoundException;

//@Controller
// @RequestMapping("/board") // 이거 사용 지양 //물어보기
public class OldAticleController {
	private static final Logger logger = LoggerFactory.getLogger(OldAticleController.class);
	private static final int pageSize = 10;

	@Autowired
	private ArticleService articleService;
	@Autowired
	private CommentsService commentsService;
	/****************************************************************************************************
	 ****************************************************************************************************
	 ****************************************************************************************************/
	//endPage 기반
	@RequestMapping(value = "/board/article/list")
	public String getArticleList(Model model) {
		CommonRequestDto req = new CommonRequestDto.Builder(1, pageSize).build();

		PageList<ArticleVo> pageList = articleService.getArticlePageListWithCount(req);

		model.addAttribute("articleList", pageList.getList());
		model.addAttribute("totalPage", pageList.getTotalPage());

		return "board/listArticle2";
	}
	
	@RequestMapping(value = "/board/article/{page}/more")
	public String getArticleMore(Model model, @PathVariable int page) {
		CommonRequestDto req = new CommonRequestDto.Builder(page, pageSize).build();
		
		List<ArticleVo> list = articleService.getArticleList(req);
		
		model.addAttribute("articleList", list);

		return "/WEB-INF/views/board/ajaxArticle";

	}
	//hasNext 기반
	@RequestMapping(value = "/board/article2/list", method = RequestMethod.GET)
	public String getArticleList2(Model model) {
		CommonRequestDto req = new CommonRequestDto.Builder(1, pageSize).useMoreView(true).build();

		PageList<ArticleVo> pageList = articleService.getArticlePageList(req);

		model.addAttribute("articleList", pageList.getList());
		model.addAttribute("hasNext", pageList.getHasNext());

		return "board/listArticle2";
	}

	@RequestMapping(value = "/board/article2/{page}/more")
	public String getArticleMore2(Model model, @PathVariable int page) {
		CommonRequestDto req = new CommonRequestDto.Builder(page, pageSize).useMoreView(true).build();

		PageList<ArticleVo> pageList = articleService.getArticlePageList(req);
		
		model.addAttribute("articleList", pageList.getList());
		model.addAttribute("hasNext", pageList.getHasNext());

		return "/WEB-INF/views/board/ajaxArticle";
	}
	/****************************************************************************************************
	 ****************************************************************************************************
	 ****************************************************************************************************/

	// 글 상세보기 ///
	@RequestMapping(value = "/board/viewArticle", method = RequestMethod.GET)
	public String viewArticle(Model model, @RequestParam String articleId, Member member) {
		ArticleVo articleVo = new ArticleVo();
		String returnURI = "";
		
		try {
			articleVo = articleService.getArticleContents(articleId, member);
			model.addAttribute("articleVo", articleVo);
			returnURI = "board/viewArticle";
		} catch (SQLException e) {
			returnURI = "/board/listArticleForm";
			
			e.printStackTrace();
		} catch (NotFoundException e) {
			returnURI = "/member/loginForm";
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 

		return returnURI;
	}

	// 글쓰기 
	@RequestMapping(value = "/board/WrtiteArticle", method = RequestMethod.POST)
	public @ResponseBody String writeArticle(@RequestBody ArticleVo articleVo, HttpServletRequest req) {
		Map<String, Object> returnMap = new HashMap<String, Object>();

		try {
			req.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String writenArticleId = articleService.writeArticle(articleVo);
	
		return "/board/viewArticle.do?articleId=" + writenArticleId;
	}

	// 글쓰기(수정) 
	@RequestMapping(value = "/board/modifyArticle", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> modifyArticle(@RequestBody ArticleVo articleVo, Member member) {
		Map<String, String> result = new HashMap<>();
		try {
			articleService.modifyArticle(articleVo, member);
			result.put("msg", "글 수정 완료");
			logger.info("=============		modifyArticle() {}",result.get("msg"));
		
		} catch (NotFoundException e) {
			result.put("msg", e.getMessage());
			logger.info("=============		modifyArticle() {}",result.get("msg"));
		
		} catch (Exception e) {    
			result.put("msg", "알수없는 오류입니다");
			logger.info("=============		modifyArticle() {}",result.get("msg"));
			e.printStackTrace();
		
		} finally {
			result.put("redirect", "/board/viewArticle.do?articleId=" + articleVo.getArticleId());
		}

		return result;
	}

	// 글쓰기(답글)
	// transaction 적용
	@RequestMapping(value = "/board/replyArticle", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> writeReply(@RequestBody ArticleVo articleVo) {
		Map<String, String> returnMap = new HashMap<>();

		try {
			String result = articleService.writeReply(articleVo);
			returnMap.put("msg", result);
			returnMap.put("redirect", "/board/viewArticle.do?articleId=" + articleVo.getArticleId());
			logger.info("=============		replyArticle() {}, redirect uri {}", returnMap.get("msg"), returnMap.get("redirect"));
		
		} catch (NotFoundException e) {
			returnMap.put("msg", e.getMessage());
			returnMap.put("redirect", "/board/listArticleForm.do");
			logger.info("=============		replyArticle() {}, redirect uri {}", returnMap.get("msg"), returnMap.get("redirect"));
			
		} catch (Exception e) {
			returnMap.put("msg", "알수없는 오류입니다");
			returnMap.put("redirect", "/board/listArticleForm.do");
			logger.info("=============		replyArticle() {}, redirect uri {}", returnMap.get("msg"), returnMap.get("redirect"));
			e.printStackTrace();
			
		}

		return returnMap;
	}
	/**
	 * 수정일짜 default되어 있는거 제거 후 댓글 수정시 수정일자 new Date()로 넣기
	 **/
	// 댓글쓰기 //대댓글쓰기
	@RequestMapping(value = "/board/writeComment", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> writeComment(@RequestBody CommentsRequestDto commentsRequestDto, Member member) {
		Map<String, String> returnMap = new HashMap<>();
		
		try {
			String result = commentsService.writeComment(commentsRequestDto, member);
			returnMap.put("msg", result);
			logger.info("=============		writeComment() {}",returnMap.get("msg"));		
		} catch (NotFoundException e) {
			returnMap.put("msg", e.getMessage());
			logger.info("=============		writeComment() {}",returnMap.get("msg"));
		} catch (Exception e) {
			returnMap.put("msg", "알 수 없는 오류 발생");
			logger.info("=============		writeComment() {}",returnMap.get("msg"));
			e.printStackTrace();	
		}

		return returnMap;
	}
/*
	// 댓글쓰기와 합침
	@RequestMapping(value = "/board/writeReComment", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> insertReComment(@RequestBody ArticleReplyVo replyVo, HttpSession session) {
		Map<String, String> result = new HashMap<>();

		//여기도 수정
		Member member = (Member) session.getAttribute("memberSession");
		replyVo.setWriteMemberId(member.getMemberId());
		articleService.insertReComment(replyVo);

		return result;
	}
*/
	// 글 삭제
	@RequestMapping(value = "/board/deleteArticle", method = RequestMethod.POST)
	public @ResponseBody Map<String, String> deleteArticle(@RequestBody ArticleVo articleVo) {
		
		Map<String, String> result = new HashMap<>();
		try {
			articleService.deleteArticle(articleVo);
			result.put("redirect", "/board/listArticleForm");
			result.put("msg", "글이 삭제되었습니다");
			logger.info("=============		replyArticle() {}, redirect uri {}", result.get("msg"), result.get("redirect"));
	
		} catch (Exception e) {
			result.put("msg", e.getMessage());
			logger.info("=============		writeComment() {}",result.get("msg"));
			e.printStackTrace();
		}

		return result;
	}

	// 댓글목록 ///
	@RequestMapping(value = "/board/commentList", method = RequestMethod.GET)
	public @ResponseBody CommentPageList getCommentList(@ModelAttribute CommentsRequestDto commentsRequestDto, Member member) {
		CommentPageList commentPageList = new CommentPageList();
		String userId;
		if(member.isLogin()) {
			userId = member.getMemberId();
			commentPageList = commentsService.getComments(commentsRequestDto, userId); 
		}else { 
			userId ="";
			commentPageList = commentsService.getComments(commentsRequestDto, userId); 
		}

		
		return commentPageList;
	}

	// 글쓰기페이지
	@RequestMapping(value = "/board/writeArticleForm", method = RequestMethod.GET)
	public String form() {
		String viewName = "board/addArticleForm";

		return viewName;
	}

	// 글쓰기페이지(답글)
	@RequestMapping(value = "/board/writeReplyForm", method = RequestMethod.POST)
	public String replyArticleForm(@RequestParam String articleId, @RequestParam String title,
			Model model) {

		model.addAttribute("articleId", articleId);
		model.addAttribute("title", title);

		return "board/replyArticleForm";
	}

	// 글수정페이지 ///
	@RequestMapping(value = "/board/modifyForm", method = RequestMethod.GET)
	public String modifyForm(@ModelAttribute("articleVo") ArticleVo articleVo, Model model,Member member) {
		String returnURI = "";
		boolean isEquals = false;
		
		try {
			isEquals = articleService.isEqualsWriterId(articleVo, member);
		} catch (Exception e) {
			return "member/loginForm";
		}

		if (isEquals) {
			// 이렇게 수정할 글 정보를 글상세보기에서 넘겨준다 = 잘못된 방법??
			// 글번호로 글 조회해서가져온다가 맞는듯
			model.addAttribute("articleVo", articleVo);
			returnURI = "board/modifyForm";
		} else {
			returnURI = "board/viweArticle.do?ArticleId=" + articleVo.getArticleId();
		}

		return returnURI;
	}
	
	// 공지글 목록 //보완하기
		@RequestMapping(value = "/board/noticeList", method = RequestMethod.GET)
		public @ResponseBody Map<String, Object> getNoticeList() {
			Map<String, Object> result = new HashMap<>();
			List<ArticleVo> noticeList = articleService.getNoticeList();
			result.put("noticleList", noticeList);
			return result;
		}

	// 내가 쓴 글 보기 ///
	@RequestMapping(value = "/board/myArticleList", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> getMyArticleList(Member member) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
	
		List<ArticleVo> myArticleList = articleService.getMyArticleList(member);

		returnMap.put("articleList", myArticleList);
		// 내글 보기는 글목록과 다른 페이지에서 보여줘야하는데 귀찮아서 기존 리스트 페이지에
		returnMap.put("uri", "board/listArticle2");
		return returnMap;
	}
}
