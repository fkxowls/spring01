package com.spring.study.common.aop;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.condition.RequestConditionHolder;

import com.spring.study.board.controller.AticleController;
import com.spring.study.board.vo.AticleVo;
import com.spring.study.member.vo.MemberDTO;

public class idCheckAspectBackUp {
	private static final Logger logger = LoggerFactory.getLogger(AticleController.class);

	public void idCheck2(JoinPoint joinFoint) {
		logger.info(" ===========   		idCheckAOP start()			=========");
	}

	// 여기에 들어오는 메서드는 무엇인가
	public String idCheck(ProceedingJoinPoint joinPoint) throws Throwable {
		logger.info(" ===========   		writeFormAspect start()			=========");

		Object[] args = joinPoint.getArgs();
		boolean isEquals = false;
		String redirect = "";

		HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()
				.getSession();
		MemberDTO memberDTO = (MemberDTO) session.getAttribute("memberSession");
		String sessionId = memberDTO.getMemberId();
		/*
		 * MemberDTO member = (MemberDTO) session.getAttribute("memberSession"); String
		 * sessionId = member.getMemberId(); String writerId = (String)
		 * req.getAttribute("writeMemberId");
		 */

		AticleVo articleVo = null;
		for (Object tmp : args) {
			if (tmp instanceof AticleVo) {
				articleVo = (AticleVo) tmp;
				String writerId = articleVo.getwriteMemberId();

				if (sessionId.equals(writerId)) {
					isEquals = true;
				}

			}
		}
		Object obj = joinPoint.proceed();
		if (obj instanceof String) {
			redirect = (String) obj;

			if (!isEquals) {
				redirect = "redirect:/board/listArticle.do";
			}
			redirect = "redirect:/board/modifyForm.do";
		}
		
		return redirect;
	}
}
