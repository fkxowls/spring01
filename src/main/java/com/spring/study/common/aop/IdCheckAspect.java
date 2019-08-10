package com.spring.study.common.aop;

import java.io.IOException;

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

import com.spring.study.board.controller.OldAticleController;
import com.spring.study.board.model.ArticleVo;
import com.spring.study.board.service.ArticleService;
import com.spring.study.member.model.Member;

public class IdCheckAspect {
	private static final Logger logger = LoggerFactory.getLogger(OldAticleController.class);

	public Object idCheck(ProceedingJoinPoint joinPoint) {

		ArticleVo articleVo = (ArticleVo) extractValuObject(joinPoint);
		boolean isEquals = equalsId(articleVo);
		String redirect = "";

		Object obj = null;
		try {
			obj = joinPoint.proceed();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (!isEquals) {
			redirect = "redirect:/board/listArticleForm.do";
			// response.sendRedirect("/board/listArticleForm.do");
			return redirect;
		} else {
			return obj;
		}

	}
	//글삭제 댓글 삭제 댓글 수정은 Form을 안거치고 삭제되어야함
	public Object idCheck2(ProceedingJoinPoint joinPoint) {
		ArticleVo articleVo = (ArticleVo) extractValuObject(joinPoint);

		String writerId = articleVo.getwriteMemberId();
		String articleId = articleVo.getArticleId();
		boolean isEquals = equalsId(articleVo);
		String redirect = "";
		
		if (!isEquals) {
			return redirect = "redirect:/board/listArticleForm.do";
		}else {
			Object obj = null;
			try {
				obj = joinPoint.proceed();
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return obj;
		}
		
	}

	public Object extractValuObject(ProceedingJoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		ArticleVo articleVo = null;
		for (Object tmp : args) {
			if (tmp instanceof ArticleVo) {
				articleVo = (ArticleVo) tmp;
				break;
			}
		}
		logger.info("==============                         =============================" );
		System.out.println(articleVo.getArticleId());
		System.out.println(articleVo.getwriteMemberId());
		logger.info("==============                         =============================" );
		return articleVo;
	}

	public boolean equalsId(ArticleVo vo) {
		boolean isEquals = false;
		String redirect = "";

		HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()
				.getSession();
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getResponse();

		Member memberDTO = (Member) session.getAttribute("memberSession");
		String sessionId = "";

		try {
			sessionId = memberDTO.getMemberId();
		} catch (NullPointerException e) {
			sessionId = "없음";
		}

		String writerId = vo.getwriteMemberId();
		if (sessionId.equals(writerId)) {
			isEquals = true;
		}

		return isEquals;
	}
	
	
/*
	public Object idCheck3(ProceedingJoinPoint joinPoint) throws Throwable {
		logger.info(" ===========   		writeFormAspect start()			=========");

		Object[] args = joinPoint.getArgs();
		boolean isEquals = false;
		String redirect = "";

		HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()
				.getSession();

		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getResponse();

		MemberDTO memberDTO = (MemberDTO) session.getAttribute("memberSession");
		String sessionId = "";

		try {
			sessionId = memberDTO.getMemberId();
		} catch (NullPointerException e) {
			sessionId = "없음";
		}

		ArticleVo articleVo = null;
		for (Object tmp : args) {
			if (tmp instanceof ArticleVo) {
				articleVo = (ArticleVo) tmp;
				String writerId = articleVo.getwriteMemberId();
				if (sessionId.equals(writerId)) {
					isEquals = true;
				}
				break;
			}
		}

		Object obj = joinPoint.proceed();// 기준점

		if (!isEquals) {
			redirect = "redirect:/board/listArticleForm.do"; //
			response.sendRedirect("/board/listArticleForm.do");
			return redirect;
		} else {
			return obj;
		}

	}
	/*
	/*
	 * public void idCheck3(JoinPoint joinPoint) {
	 * logger.info(" ===========   					=========");
	 * 
	 * Object[] args = joinPoint.getArgs(); HttpServletResponse response =
	 * ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
	 * .getResponse(); HttpSession session = ((ServletRequestAttributes)
	 * RequestContextHolder.currentRequestAttributes()).getRequest() .getSession();
	 * MemberDTO memberDTO = (MemberDTO) session.getAttribute("memberSession");
	 * String sessionId = ""; try { sessionId = memberDTO.getMemberId(); } catch
	 * (NullPointerException e) { sessionId = "없음"; }
	 * 
	 * ArticleVo articleVo; String writerId; int articleNo = 0; for (Object tmp :
	 * args) { if (tmp instanceof ArticleVo) {
	 * 
	 * articleVo = (ArticleVo) tmp; writerId = articleVo.getwriteMemberId();
	 * articleNo = articleVo.getArticleNo(); if (!sessionId.equals(writerId)) { }
	 * break; } } ArticleService articleService = new ArticleService();
	 * articleService.viewArticle(articleNo); }
	 */
}