package com.spring.study.common.aop;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.spring.study.common.model.PageList;
import com.spring.study.dao.CommentDao;
import com.spring.study.model.article.Article;
import com.spring.study.model.article.ArticleVo;
import com.spring.study.model.comments.Comment;
import com.spring.study.model.comments.CommentDto;
import com.spring.study.model.comments.CommentParam;
import com.spring.study.model.comments.CommentVo;
import com.spring.study.model.user.User;
import com.spring.study.model.user.UserVo;

@Aspect
@Component
public class ArticleAddContentsAspect {
	private static final Logger logger = LoggerFactory.getLogger(ArticleAddContentsAspect.class);
	
	@Autowired
	private CommentDao commentDAO;
	
	
	@Around("@annotation(com.spring.study.common.aop.AddComments)" /* + "&& args(user2)" */)
	public Object addComments(ProceedingJoinPoint joinPoint) {
		Object obj = null;
		HttpServletRequest req = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("userSession");
		
		try {
			obj = joinPoint.proceed();
		} catch (Throwable e) {	e.printStackTrace(); }
		
		
		List<Article> pageList = new LinkedList<Article>();
		if (obj instanceof PageList) {
			pageList = ((PageList<Article>) obj).getList();
		} else if (obj instanceof List) {
			pageList = (List<Article>) obj;
		} else if (obj instanceof Article) {
			pageList.add((Article) obj);
		} else {
			return obj;
		}
		
//		if(null == user) {
//			user = new User();
//			user.setUserId(StringUtils.EMPTY);
//			user.setUserLevel("00");
//			session.setAttribute("userSession", user);
//		}
		String articleNumbers = pageList.stream()
				.map(ArticleVo::getArticleId)
				.collect(Collectors.joining(","));
		//TODO 댓글 페이징 정보가 이상하게 들어감 ex) startNum -9, 및 페이징 정보와 댓글 리스트 분리
		CommentParam commentsParam = new CommentParam.Builder(1, 10,articleNumbers).userId(user.getUserId())
				.build();
		PageList<Comment> commentsPageDto = commentDAO.commentsList(commentsParam);
		
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
		
		Map<String, List<Comment>> commentsListByArticleId = commentsPageDto.getList().stream()
				.collect(Collectors.groupingBy(Comment::getArticleId));
		
		pageList.stream().forEach( vo -> vo.setCommentsList(commentsListByArticleId.get(vo.getArticleId())));
		
//		Stream<ArticleVo> stream = returnList.stream();
//		stream = stream.filter( vo -> null == vo.getArticleNo() );
//		stream = stream.limit(10);
//		stream.forEach( vo -> vo.setCommentsList(commentsListByArticleNo.get(vo.getArticleNo())) );
		
		return obj;
	}
}
