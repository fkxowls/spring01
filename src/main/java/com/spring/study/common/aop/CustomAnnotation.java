package com.spring.study.common.aop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.spring.study.board.controller.AticleController;
import com.spring.study.board.dao.ArticleDAO;
import com.spring.study.board.vo.ArticleReplyVo;
import com.spring.study.board.vo.AticleVo;
import com.spring.study.board.vo.PagingResponseDTO;

//@Aspect
public class CustomAnnotation {
	private static final Logger logger = LoggerFactory.getLogger(AticleController.class);
	@Autowired
	private ArticleDAO articleDAO;

	// @Before("@annotation(com.spring.study.common.aop.AddComments)")
	public Object customAnnotationTest(ProceedingJoinPoint joinPoint) {
		logger.info("==================================customAnn");

		Object obj = null;
		try {
			obj = joinPoint.proceed();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<AticleVo> list = new LinkedList<AticleVo>();
		if (obj instanceof PagingResponseDTO) {
			list = ((PagingResponseDTO<AticleVo>) obj).getList();
		} else if (obj instanceof List) {
			list = (List<AticleVo>) obj;
		} else if (obj instanceof AticleVo) {
			list.add((AticleVo) obj);
		} else {
			return obj;
		}
		
		/*
		 * for (AticleVo dto : list) { addCommentInfo(dto); }
		 */
		
		//list에서 글번호만 따로 list에 담아 쿼리로 넘겨준다
		List<String> articleNumbers = new ArrayList<String>();
		String requestObj = "";
		for (int i = 0; i < list.size(); i++) {
			articleNumbers.add(list.get(i).getArticleNo());
		}
		Map<String, List<ArticleReplyVo>> commentsList = getCommentsList(articleNumbers);
		
		//map에 있는 댓글들을 각각의 글번호에 삽입
		for(int i=0; i<commentsList.size(); i++) {
			for(int k=0; k<list.size(); k++) {
				String key = list.get(k).getArticleNo();
				list.get(k).setCommentsList(commentsList.get(key));
			}
		}
		
		
		return obj;

	}

	private Map<String, List<ArticleReplyVo>> getCommentsList(List articleNumbers) {
		//Map<String, List<String>> requestObj = new HashMap<String, List<String>>();
		//requestObj.put("articleNo", articleNumbers);
		
		Map<String, List<ArticleReplyVo>> commentsList = articleDAO.listComment(articleNumbers);
		
		return commentsList;
	}
	
	private Object addComments() {
		return null;
	}
}
