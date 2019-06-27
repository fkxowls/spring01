package com.spring.study.member.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.spring.study.board.controller.AticleController;
import com.spring.study.member.service.MemberService;
import com.spring.study.member.vo.MemberDTO;
import com.spring.study.member.vo.MemVo;

@Controller
@SessionAttributes("memberSession")
public class MemberController {
	private static final Logger logger = LoggerFactory.getLogger(AticleController.class);

	@Autowired
	MemberService memberService;

	@RequestMapping(value = "/member/loginForm", method = { RequestMethod.GET, RequestMethod.POST })
	public String loginForm() {
		logger.info("============		loginForm() start		==============");
		return "member/loginForm";
	}

	@RequestMapping(value = "/member/login", method = RequestMethod.POST)
	public String login(@ModelAttribute("memberVo") MemVo memberVo, HttpSession session, HttpServletRequest req,
			Model model) {
		logger.info("============		login() start		==============");

		MemVo member = memberService.login(memberVo);

		// TODO 세션에 로그인 정보 담는다 -> 지금은 이름, ID 정도만 담으면 될 것 같음
		MemberDTO memberDTO = memberService.setMemberSession(member);
		session.setAttribute("memberSession", memberDTO);


		return "redirect:/board/listArticleForm.do";
	}

	@RequestMapping(value = "/member/logout", method = RequestMethod.GET)
	public String logout(SessionStatus sessionStatus, HttpServletRequest req, HttpSession session) {

		// session.removeAttribute("id");
		sessionStatus.setComplete();
		return "redirect:/board/listArticleForm.do";
	}

}
