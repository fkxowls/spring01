package com.spring.study.common.aop;

import java.util.LinkedList;
import java.util.List;

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
		// 파라미터 선별작업은 우선 나중에
		Object obj = null;
		try {
			obj = joinPoint.proceed();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<AticleVo> list = new LinkedList<AticleVo>();
		if(obj instanceof PagingResponseDTO) {
			list = ((PagingResponseDTO<AticleVo>) obj).getList();
		} else if(obj instanceof List) {
			list = (List<AticleVo>) obj;
		} else if(obj instanceof AticleVo) {
			list.add((AticleVo) obj);
		} else {
			return obj;
		}
		
		for(AticleVo dto : list) {
			addCommentInfo(dto);
		}
		
		return obj;

	}
	
	private AticleVo addCommentInfo(AticleVo aticleVo) {
		String articleNo = aticleVo.getArticleNo();
		List<ArticleReplyVo> commentsList = articleDAO.listComment(articleNo);
		aticleVo.setCommentsList(commentsList);
		
		return aticleVo;
	}
}
