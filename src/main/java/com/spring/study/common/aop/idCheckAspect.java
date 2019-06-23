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
import org.springframework.ui.Model;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.condition.RequestConditionHolder;

import com.spring.study.board.controller.AticleController;
import com.spring.study.board.vo.AticleVo;
import com.spring.study.member.vo.MemberDTO;

public class idCheckAspect {
	private static final Logger logger = LoggerFactory.getLogger(AticleController.class);

	public void idCheck2(JoinPoint joinFoint) {
		logger.info(" ===========   		idCheckAOP start()			=========");
	}

	// 여기에 들어오는 메서드는 무엇인가
	public Object idCheck(ProceedingJoinPoint joinPoint) throws Throwable {
		logger.info(" ===========   		writeFormAspect start()			=========");

		Object[] args = joinPoint.getArgs();
		boolean isEquals = false;
		String redirect = "";
		/* HttpServletResponse resp = null; */
		HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()
				.getSession();
		
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
		
		MemberDTO memberDTO = (MemberDTO) session.getAttribute("memberSession");
		String sessionId = "";

		try {
			sessionId = memberDTO.getMemberId();
		} catch (NullPointerException e) {
			sessionId = "없음";
		}
		logger.info("sessionId:  {}",sessionId);
		AticleVo articleVo = null;
		for (Object tmp : args) {
			if (tmp instanceof AticleVo) {
				logger.info("오는가 안오는가1");
				articleVo = (AticleVo) tmp;
				String writerId = articleVo.getwriteMemberId();
				logger.info("writerId:  {}",writerId);
				if (sessionId.equals(writerId)) {
					logger.info("오는가 안오는가2");
					isEquals = true;
				}
				break;
			}
		}

		logger.info("오는가 안오는가3");
		Object obj = joinPoint.proceed();//기준점

		if (!isEquals) {
			logger.info("오는가 안오는가4");
			redirect = "redirect:/board/listArticleForm.do";
			//response.sendRedirect("/board/listArticleForm.do");
			return redirect;
		} else {
			logger.info("오는가 안오는가5");
			System.out.println("obj: "+obj.toString());
			return obj;
		}

	}
}
