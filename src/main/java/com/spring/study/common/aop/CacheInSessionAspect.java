package com.spring.study.common.aop;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class CacheInSessionAspect {
	private static final FastDateFormat fdf = FastDateFormat.getInstance("yyyyMMddHHmmss", Locale.getDefault());
	@Around("@annotation(com.spring.study.common.aop.CacheInSession) && @ annotation(target)")
	public Object getCachedObject(ProceedingJoinPoint joinPoint, CacheInSession target) throws Throwable {
		
		HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		HttpServletResponse res = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
		HttpSession session = req.getSession();
		Object returnObject;

		// null -> new String[0]; 등 예외처리는 나중에. 일단 구현부터.
		String cacheKey = target.name();
		final String type = target.type();
		Class c = Class.forName(type);
		String[] keys = StringUtils.split(target.key(), ",");
		
		List<String> keyList = Arrays.asList(keys);
		Object[] arguments = joinPoint.getArgs();
		for(Object object : arguments) {
			if(!StringUtils.equals(type, object.getClass().getName())) {
				continue;
			}
			for(String key : keyList) {
				final String methodName = "get" + key.substring(0, 1).toUpperCase() + key.substring(1);
				Method method = c.getMethod(methodName);
	            Object value = method.invoke(object);
	            cacheKey += ":" + String.valueOf(value);
			}
			break;
	    }

		Date curDate = new Date();
		String expireDate = (String)session.getAttribute("expire" + cacheKey);
		if(!expiresDate(curDate,expireDate)) {
			returnObject = session.getAttribute(cacheKey);
			if(null != returnObject) {
				return returnObject;
			}
		}

		// TODO 없으면 수행 후 세션에 담고 -> 그거 리턴
		returnObject = joinPoint.proceed();
		String ttl = target.ttl();
		Date expireTime = DateUtils.addSeconds(curDate, Integer.parseInt(ttl));
	
		expireDate = fdf.format(expireTime);
		session.setAttribute(cacheKey, returnObject);
		session.setAttribute("expire" + cacheKey, expireDate);

		return returnObject;
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
