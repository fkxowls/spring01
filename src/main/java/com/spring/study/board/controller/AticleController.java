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
@SessionAttributes("nowArticleId")
// @RequestMapping("/board") // 이거 사용 지양
public class AticleController {
	private static final Logger logger = LoggerFactory.getLogger(AticleController.class);
	private static final int pageSize = 10;

	@Autowired
	private ArticleService articleService;

	// endPage 페이징정보가져오기
	@RequestMapping(value = "/board/listArticleForm", method = RequestMethod.GET)
	public String listArticleFormByTotalArticle(Model model) {

		PagingResponseDTO<ArticleVo> PagingResponseDTO = articleService.EndPagination(1, 10);

		model.addAttribute("totalPage", PagingResponseDTO.getTotalPage());
		model.addAttribute("articleList", PagingResponseDTO.getList());

		return "board/listArticle2";
	}

	// endPage moreView
	@RequestMapping(value = "/board/ajaxArticle")
	public String moreArticleByTotalArticle(Model model, @RequestParam("page") String page) {

		List<ArticleVo> articleList = articleService.EndPagingMore(Integer.parseInt(page), 10);
		model.addAttribute("articleList", articleList);

		return "/WEB-INF/views/board/ajaxArticle";

	}

	// hasNext 페이징정보가져오기
	@RequestMapping(value = "/board/listArticle2", method = RequestMethod.GET)
	public String listArticleFormByHasNext(Model model) {

		PagingResponseDTO<ArticleVo> articlePageDto = articleService.hasNextPaging(1, 10);
		model.addAttribute("hasNext", articlePageDto.getHasNext());
		model.addAttribute("articleList", articlePageDto.getList());

		return "board/listArticle2";
	}

	// hasNext moreView
	@RequestMapping(value = "/board/ajaxArticle2")
	public String moreArticleFormByHasNext(Model model, @RequestParam("page") int page) {

		PagingResponseDTO<ArticleVo> articlePageDto = articleService.hasNextPagingMore(page, 10);
		model.addAttribute("articleList", articlePageDto.getList());
		model.addAttribute("hasNext", articlePageDto.getHasNext());

		return "/WEB-INF/views/board/ajaxArticle";
	}

	// 글 상세보기 ///
	@RequestMapping(value = "/board/viewArticle", method = RequestMethod.GET)
	public String viewArticle(Model model, @RequestParam("articleId") String articleNum, HttpSession session) {
		ArticleVo articleVo = new ArticleVo();
		MemberDTO memberDTO = (MemberDTO) session.getAttribute("memberSession");
		String returnURI = "";
		
		try {
			articleVo = articleService.viewArticle(articleNum, memberDTO);
			model.addAttribute("articleVo", articleVo);
			returnURI = "board/viewArticle";
		} catch (SQLException e) {
			//컨트롤러로 안가짐 redirect해야함
			returnURI = "redirect:/board/listArticleForm";
			e.printStackTrace();
		} catch (NotFoundException e) {
			//이건 바로jsp로가는거임
			returnURI = "member/loginForm";
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 

		return returnURI;
	}

	// 공지글 목록 ///
	@RequestMapping(value = "/board/noticeList", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> getNoticeList() {
		Map<String, Object> result = new HashMap<>();
		List<ArticleVo> noticeList = articleService.getNoticeList();
		result.put("noticleList", noticeList);
		return result;
	}

	// 글쓰기 ///
	@RequestMapping(value = "/board/WrtiteArticle", method = RequestMethod.POST)
	public @ResponseBody String writeArticle(@RequestBody ArticleVo articleVo, HttpServletRequest req) {
		logger.info("=============		writeArticle() start		==============");
		Map<String, Object> returnMap = new HashMap<String, Object>();

		try {
			req.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// TODO 글번호
		// int articleId = articleService.getSequence();
		String num = articleService.writeArticle(articleVo);
		// 여기 수정해야함
		return "/board/viewArticle.do?articleId=" + num;
	}

	// 글쓰기(수정) ///
	@RequestMapping(value = "/board/modifyArticle", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> modifyArticle(@RequestBody ArticleVo articleVo, HttpSession session) {
		Map<String, String> result = new HashMap<>();

		MemberDTO memberDTO = (MemberDTO) session.getAttribute("memberSession");
		// String sessionId = member.getMemberId();

		try {
			articleService.modifyArticle(articleVo, memberDTO);
			System.out.println("1");
			result.put("msg", "글 수정 완료");
		} catch (NotFoundException e) {
			System.out.println("2");
			result.put("msg", e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("3");
			result.put("msg", "알수없는 오류입니다");
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
	public Map<String, String> replyArticle(@RequestBody ArticleVo articleVo) {
		Map<String, String> returnMap = new HashMap<>();

		try {
			String result = articleService.replyArticle(articleVo);
			returnMap.put("msg", result);
			returnMap.put("redirect", "/board/viewArticle.do?articleId=" + articleVo.getArticleId());
		} catch (NotFoundException e) {
			returnMap.put("msg", e.getMessage());
			returnMap.put("redirect", "/board/listArticleForm.do");
			e.printStackTrace();
		} catch (Exception e) {
			returnMap.put("msg", "알수없는 오류입니다");
			returnMap.put("redirect", "/board/listArticleForm.do");
			e.printStackTrace();
		}

		return returnMap;
	}

	// 댓글쓰기
	@RequestMapping(value = "/board/writeComment", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> writeComment(@RequestBody ArticleReplyVo replyVo, HttpSession session) {
		Map<String, String> returnMap = new HashMap<>();

		MemberDTO member = (MemberDTO) session.getAttribute("memberSession");
		replyVo.setWriteMemberId(member.getMemberId());

		try {
			String result = articleService.insertComment(replyVo);
			returnMap.put("msg", result);
		} catch (NotFoundException e) {
			returnMap.put("msg", e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			returnMap.put("msg", "알 수 없는 오류 발생");
			e.printStackTrace();
		}

		return returnMap;
	}

	// 댓글쓰기(대댓글) //여기 보완해야함
	@RequestMapping(value = "/board/writeReComment", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> insertReComment(@RequestBody ArticleReplyVo replyVo, HttpSession session) {
		Map<String, String> result = new HashMap<>();

		// 여기서 일어날 수 있는 오류는???
		MemberDTO member = (MemberDTO) session.getAttribute("memberSession");
		replyVo.setWriteMemberId(member.getMemberId());
		articleService.insertReComment(replyVo);

		return result;
	}

	// 글 삭제
	@RequestMapping(value = "/board/deleteArticle", method = RequestMethod.POST)
	public @ResponseBody Map<String, String> deleteArticle(@RequestBody ArticleVo articleVo) {
		Map<String, String> result = new HashMap<>();

		try {
			articleService.deleteArticle(articleVo);
			result.put("redirect", "/board/listArticleForm");
			result.put("msg", "글이 삭제되었습니다");
		} catch (Exception e) {
			result.put("msg", e.getMessage());
			e.printStackTrace();
		}

		return result;
	}

	// 댓글목록
	@RequestMapping(value = "/board/commentList", produces = "application/json; charset=utf8")
	@ResponseBody // ?
	public List<ArticleReplyVo> listComment(@RequestParam("articleId") String articleId,HttpSession session) {
		
		MemberDTO member = (MemberDTO) session.getAttribute("memberSession");
		List<ArticleReplyVo> vo = articleService.getComments(articleId, member);

		return vo;
	}

	// 글쓰기페이지
	@RequestMapping(value = "/board/writeArticleForm", method = RequestMethod.GET)
	public String form() {
		String viewName = "board/addArticleForm";

		return viewName;
	}

	// 글쓰기페이지(답글)
	@RequestMapping(value = "/board/writeReplyForm", method = RequestMethod.POST)
	public String replyArticleForm(@RequestParam("articleId") String articleId, @RequestParam("title") String title,
			Model model) {

		model.addAttribute("articleId", articleId);
		model.addAttribute("title", title);

		return "board/replyArticleForm";
	}

	// 글수정페이지 ///
	@RequestMapping(value = "/board/modifyForm", method = RequestMethod.POST)
	public String modifyForm(@ModelAttribute("articleVo") ArticleVo articleVo, Model model, HttpSession session) {
		String returnURI = "";

		MemberDTO memberDTO = (MemberDTO) session.getAttribute("memberSession");

		boolean isEquals = false;
		try {
			isEquals = articleService.isEqualsWriterId(articleVo, memberDTO);
		} catch (Exception e) {
			return "member/loginForm";
		}

		if (isEquals) {
			// 이렇게 수정할 글 정보를 글상세보기에서 넘겨준다 = 잘못된 방법??
			// 글번호로 글 조회해서가져온다가 맞는듯
			model.addAttribute("articleVo", articleVo);
			System.out.println("is");
			returnURI = "board/modifyForm";
		} else {
			System.out.println("not - is");
			returnURI = "board/viweArticle.do?ArticleId=" + articleVo.getArticleId();
		}

		return returnURI;
	}

	// 내가 쓴 글 보기 ///
	@RequestMapping(value = "/board/myArticleList", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> getMyArticleList(HttpSession session) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		MemberDTO member = (MemberDTO) session.getAttribute("memberSession");

		List<ArticleVo> myArticleList = articleService.getMyArticleList(member);

		returnMap.put("articleList", myArticleList);
		// 내글 보기는 글목록과 다른 페이지에서 보여줘야하는데 귀찮아서 기존 리스트 페이지에
		returnMap.put("uri", "board/listArticle2");
		return returnMap;
	}
}
