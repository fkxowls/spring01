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

public class InterceptorBack_Up extends HandlerInterceptorAdapter {
	private static final Logger logger = LoggerFactory.getLogger(AticleController.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		logger.info("=================		preHandle() start		==================");
		HttpSession session = request.getSession();
		String contextPath = request.getContextPath();

		String writeMemberId ="";
		String sessionId	="";
		try {
			MemberDTO memberDTO = (MemberDTO) session.getAttribute("memberSession");

			writeMemberId = request.getParameter("writeMemberId");
			sessionId = memberDTO.getMemberId();
			logger.info("==================================			writeMemberId:{}", writeMemberId);
			logger.info("==================================			SessionId:{}", memberDTO.getMemberId());

		} catch (Exception e) {
			logger.info("==========================				dtoCheck : false");
			response.sendRedirect(contextPath+"/board/listArticle.do");// ?
			e.printStackTrace();
			return false;
		}
		
		boolean result = checkId(writeMemberId, sessionId);
		if(result != true)
			response.sendRedirect(contextPath+"/board/listArticle.do");
		return result;
	}

	private boolean checkId(String memberId, String sessionId) throws IOException {
		logger.info("===============================			checkId() start");
		logger.info("==================================			writeMemberId:{}", memberId);
		logger.info("==================================			SessionId:{}", sessionId);
		if (!memberId.equals(sessionId)) {
			return false;
		} else {
			return true;
		}
	}

}
