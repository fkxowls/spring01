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

import com.spring.study.common.model.PageList;
import com.spring.study.dao.CommentDao;
import com.spring.study.model.article.ArticleVo;
import com.spring.study.model.comments.CommentsDto;
import com.spring.study.model.comments.CommentsParam;
import com.spring.study.model.comments.CommentsVo;

@Aspect
@Component
public class ArticleAddContentsAspect {
	private static final Logger logger = LoggerFactory.getLogger(ArticleAddContentsAspect.class);
	
	@Autowired
	private CommentDao commentDAO;
	
	@Around("@annotation(com.spring.study.common.aop.AddComments)")
	public Object addComments(ProceedingJoinPoint joinPoint) {
		System.out.println("aspect");
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
		
		//ArticleController getArticleList()에서 aop사용
		//XXX 리턴값을 PageList로 받는 이유가 무엇인가요
		CommentsDto req = new CommentsDto(articleNumbers, 1, 10);
		//XXX 5 commentsList는 코멘트파라미터객체를 주입받아야하는데 여기서 어떻게 주입을 해야할까요?? 페이지 정보도 어떻게 받아야할까여, 현재 접속한 유저정보를 어떻게 받아야할까요..
		//현재 접속한 유저아이디를 어떻게 가져와야할까여...
		CommentsParam commentsParam = new CommentsParam.Builder(1, 10,articleNumbers)
				 .userId().build();
		PageList<CommentsVo> commentsPageDto = commentDAO.commentsList(req);//TODO 여기 오류 해결
		
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
		
		Map<String, List<CommentsVo>> commentsListByArticleId = commentsPageDto.getCommentsList().stream()
				.collect(Collectors.groupingBy(CommentsVo::getArticleId));
		
		returnList.stream().forEach( vo -> vo.setCommentsList(commentsListByArticleId.get(vo.getArticleId())) );
		
//		Stream<ArticleVo> stream = returnList.stream();
//		stream = stream.filter( vo -> null == vo.getArticleNo() );
//		stream = stream.limit(10);
//		stream.forEach( vo -> vo.setCommentsList(commentsListByArticleNo.get(vo.getArticleNo())) );
		
		return returnObj;
	}
}
