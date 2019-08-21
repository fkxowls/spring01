package com.spring.study.controller;

import javax.servlet.http.HttpServletRequest;
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

import com.spring.study.model.user.Member;
import com.spring.study.model.user.User;
import com.spring.study.model.user.UserVo;
import com.spring.study.service.UserService;

//UserController도 완전히 갈아엎어야함
@Controller
@SessionAttributes("memberSession")
public class UserController {
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	UserService memberService;

	@RequestMapping(value = "/member/loginForm", method = { RequestMethod.GET, RequestMethod.POST })
	public String loginForm() {
		logger.info("============		loginForm() start		==============");
		return "member/loginForm";
	}

	@RequestMapping(value = "/member/login", method = RequestMethod.POST)
	public String login(@ModelAttribute() UserVo userVo, HttpSession session, HttpServletRequest req,
			Model model) {
		logger.info("============		login() start		==============");

		UserVo userInfo = memberService.login(userVo);
		User user = new User();
		user.setMemberId(userInfo.getMemberId());
		user.setMemberLevel(userInfo.getMemberLevel());
		//UserVo memberDTO = memberService.setMemberSession(user);
		session.setAttribute("memberSession", user);


		return "redirect:/board/article/list";
	}

	@RequestMapping(value = "/member/logout", method = RequestMethod.GET)
	public String logout(SessionStatus sessionStatus, HttpServletRequest req, HttpSession session) {

		// session.removeAttribute("id");
		sessionStatus.setComplete();
		return "redirect:/board/listArticleForm.do";
	}

}
