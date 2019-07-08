package com.spring.study.common.interceptor;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.spring.study.board.controller.AticleController;
import com.spring.study.member.vo.MemberDTO;

public class LoginSessionInterceptor extends HandlerInterceptorAdapter {
	private static final Logger logger = LoggerFactory.getLogger(AticleController.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		logger.info("=================		preHandle() start		==================");
		HttpSession session = request.getSession();
		//String contextPath = request.getContextPath();

		
		String sessionId ="";
		try {
			MemberDTO memberDTO = (MemberDTO) session.getAttribute("memberSession");
			sessionId = memberDTO.getMemberId();
		} catch (Exception e) {
			response.sendRedirect("/member/loginForm.do");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

}
