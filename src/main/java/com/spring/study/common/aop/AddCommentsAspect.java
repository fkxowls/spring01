package com.spring.study.common.aop;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
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
public class AddCommentsAspect {
	private static final Logger logger = LoggerFactory.getLogger(AddCommentsAspect.class);
	
	@Autowired
	private CommentDao commentDAO;
	
	
	@SuppressWarnings("unchecked")
	@Around("@annotation(com.spring.study.common.aop.AddComments) && @ annotation(target)")
	public Object addComments(ProceedingJoinPoint joinPoint, AddComments target) {
		HttpServletRequest req = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("userSession");

		Object obj = null;
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
	
		String articleNumbers = pageList.stream()
				.map(ArticleVo::getArticleId)
				.collect(Collectors.joining(","));
		CommentParam commentParam = new CommentParam.Builder(10, articleNumbers)
				.userId(user.getUserId())
				.useMore(target.useMore())
				.build();
		
		Map<String, PageList<Comment>> commentList = commentDAO.getCommentsListMap(commentParam, Comment::getArticleId);
		
		pageList.stream()
				.forEach( vo -> vo.setCommentsList(commentList.get(vo.getArticleId())) );
		
		return obj;
	}
}
