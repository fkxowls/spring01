package com.spring.study.common.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.spring.study.board.controller.AticleController;
import com.spring.study.member.vo.Member;

public class LoginSessionInterceptor extends HandlerInterceptorAdapter {
	private static final Logger logger = LoggerFactory.getLogger(AticleController.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		logger.info("=================		preHandle() start		==================");
		HttpSession session = request.getSession();

		Member member = (Member) session.getAttribute("memberSession");
		
		if(null == member) {
			member = new Member();
			member.setMemberId(StringUtils.EMPTY);
			session.setAttribute("memberSession", member);
		}
		
		// TODO member 객체를 컨트롤러에 주입해줄 수 있도록 처리
		
		// TODO 여기 이런 기능이 있는 건 맞지 않음
		if(StringUtils.isEmpty(member.getMemberId())) {
			response.sendRedirect("/member/loginForm.do");
			return false;
		}
		
		return true;
	}

}
