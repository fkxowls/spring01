package com.spring.study.common.aop;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.spring.study.common.model.PageList;
import com.spring.study.dao.ArticleDao;
import com.spring.study.model.article.Article;
import com.spring.study.model.article.ArticleParam;

@Aspect
@Component
public class PageListCachingAspect {
	private static final FastDateFormat fdf = FastDateFormat.getInstance("yyyyMMddHHmmss", Locale.getDefault());
	@Around("@annotation(com.spring.study.common.aop.DaoCaching)")
	public Object getCachedObject(ProceedingJoinPoint joinPoint) {
		Object[] arguments = joinPoint.getArgs();
	    ArticleParam vo = null;
		for (Object object : arguments) {
			if(object instanceof ArticleParam) {
				vo = (ArticleParam) object;
			}
	    }
		
		//XXX aop로 어떻게 분리해야할까요
		//endPaging같은 경우 getArticlePageListWithTotalCount은 PageList<Article>
		//				  getMoreListArticle의 리턴타입은 List<Article>, 
		//Session에 담을때 PageList<Article>타입으로 넣어야할까요??
		//List<Article>타입으로 저장해야한다면 첫페이지 진입시에 페이징정보(totalPage)는 어떻게 해야할까여
	   	//Comments까지 캐싱이 되려면 Service - Dao 이 사이에 메서드를 하나 더 추가해서 거기에 @DaoCaching을 붙여야할까요?
		HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		HttpServletResponse resp = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getResponse();
		HttpSession session = req.getSession();
		
		PageList<Article> list = null;
		List<Article> tmpList = null;
		
		Date curDate = new Date();
		String key = ArticleDao.class.getName() + ".ListArticleTest" + "&page=" + vo.getPage() + "&pageSize=" + vo.getPageSize();
		String expireDate = (String) session.getAttribute("expire" + key);
	
		if(!expiresDate(curDate,expireDate)) {
			System.out.println("ex");
			list = (PageList<Article>) session.getAttribute(key);
			//list = new PageList<Article>(tmpList);
			return list;
		}
		
		Object obj = null;
		try {
			obj = joinPoint.proceed();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		list = (PageList<Article>) obj;
		tmpList = list.getList();
		key = ArticleDao.class.getName() + ".ListArticleTest" + "&page=" + vo.getPage() + "&pageSize=" + vo.getPageSize();
		String ttl = "3600"; // 1시간 // TODO 커스텀 ttl 만큼 날짜 추가해줌
		Date expireTime = DateUtils.addSeconds(curDate, Integer.parseInt(ttl));
	
		expireDate = fdf.format(expireTime);
		session.setAttribute(key, list);
		session.setAttribute("expire" + key, expireDate);
		return list;
	}
	
	private boolean expiresDate(Date curTime, String expireDate) {
		if(null == expireDate) {
			return true;
		}
		
		try {
			Date expireTime = DateUtils.parseDate(expireDate, fdf.getPattern());
			
			// 뒤날짜가 크면 -1 작으면 1
			int expire = DateUtils.truncatedCompareTo(curTime, expireTime, Calendar.SECOND);
			
			if(expire > 0) {
				return true;
			}
		} catch (ParseException e) {
			return true;
		}
		
		return false;
	}
	

}
