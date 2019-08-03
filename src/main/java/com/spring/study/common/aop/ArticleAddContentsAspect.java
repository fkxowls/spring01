package com.spring.study.common.aop;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.spring.study.board.dao.CommentDao;
import com.spring.study.board.vo.ArticleReplyVo;
import com.spring.study.board.vo.ArticleVo;
import com.spring.study.common.model.PageList;

@Aspect
@Component
public class ArticleAddContentsAspect {
	private static final Logger logger = LoggerFactory.getLogger(ArticleAddContentsAspect.class);
	
	@Autowired
	private CommentDao commentDAO;
	
	@Around("@annotation(com.spring.study.common.aop.AddComments)")
	public Object addComments(ProceedingJoinPoint joinPoint) {

		Object returnObj = null;
		try {
			returnObj = joinPoint.proceed();
		} catch (Throwable e) {	e.printStackTrace(); }
		
		List<ArticleVo> returnList = new LinkedList<ArticleVo>();
		if (returnObj instanceof PageList) {
			returnList = ((PageList<ArticleVo>) returnObj).getList();
		} else if (returnObj instanceof List) {
			returnList = (List<ArticleVo>) returnObj;
		} else if (returnObj instanceof ArticleVo) {
			returnList.add((ArticleVo) returnObj);
		} else {
			return returnObj;
		}
		
//		List<String> articleNumberList = new ArrayList<String>();
//		for(ArticleVo vo : returnList) {
//			articleNumberList.add(vo.getArticleNo());
//		}
//		String articleNumbers = String.join(",", articleNumberList);
	
		String articleNumbers = returnList.stream()
				.map(ArticleVo::getArticleId)
				.collect(Collectors.joining(","));
		
		//TODO ArticleReplyVo 페이징 정보도 담을 수 있게 만들기/ 이왕이면 request/response로 나눠보자
		List<ArticleReplyVo> commentsList = commentDAO.commentsList(articleNumbers);
		
//		for(ArticleVo ArticleVo : returnList) {
//			String key = ArticleVo.getArticleNo();
//			ArticleVo.setCommentsList(Lists.newArrayList());
//			for(int i = 0; i < commentsList.size(); i++) {
//				ArticleReplyVo articleReplyVo = commentsList.get(i);
//				if(null == articleReplyVo.getArticleNo()) {
//					continue;
//				}
//				if(key.equals(articleReplyVo.getArticleNo())) {
//					ArticleVo.getCommentsList().add(articleReplyVo);
//				}
//				if(10 == ArticleVo.getCommentsList().size()) {
//					break;
//				}
//			}
//		}
		
		Map<String, List<ArticleReplyVo>> commentsListByArticleId = commentsList.stream()
				.collect(Collectors.groupingBy(ArticleReplyVo::getArticleId));
		
		returnList.stream().forEach( vo -> vo.setCommentsList(commentsListByArticleId.get(vo.getArticleId())) );
		
//		Stream<ArticleVo> stream = returnList.stream();
//		stream = stream.filter( vo -> null == vo.getArticleNo() );
//		stream = stream.limit(10);
//		stream.forEach( vo -> vo.setCommentsList(commentsListByArticleNo.get(vo.getArticleNo())) );
		
		return returnObj;
	}
}
