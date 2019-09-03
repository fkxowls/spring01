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
	
	
	@Around("@annotation(com.spring.study.common.aop.AddComments)" /* + "&& args(user2)" */)
	public Object addComments(ProceedingJoinPoint joinPoint) {
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
		CommentParam commentsParam = new CommentParam.Builder(1, 10,articleNumbers)
													 .userId(user.getUserId())
													 .useEndCount(true)
													 .build();
		Function<Comment, String> articleIdGroup = Comment::getArticleId;
		Map<String, PageList<Comment>> commentList = commentDAO.getFeedList(commentsParam, articleIdGroup);//XXX Paging 정보는 어떻게???
		
//		pageList.stream().forEach( vo -> vo.setCommentsList(commentList.get(vo.getArticleId())));
//		PageList<Comment> commentsPageDto = commentDAO.commentsList(commentsParam);
//		Map<String, List<Comment>> commentsListByArticleId = commentsPageDto.getList().stream()
//				.collect(Collectors.groupingBy(articleIdGroup));//TODO 그룹핑하는 거 메서드로 분리 
		
//		Consumer<Article> action = vo -> vo.setCommentsList(commentsListByArticleId.get(vo.getArticleId()));
//		action.accept(pageList.get(0));
		
		
//		Stream<ArticleVo> stream = returnList.stream();
//		stream = stream.filter( vo -> null == vo.getArticleNo() );
//		stream = stream.limit(10);
//		stream.forEach( vo -> vo.setCommentsList(commentsListByArticleNo.get(vo.getArticleNo())) );
		
		return obj;
	}
}
