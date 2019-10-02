package com.spring.study.common.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.spring.study.model.user.Member;
import com.spring.study.model.user.User;
import com.spring.study.model.user.UserVo;

public class UserArgumentResolver implements HandlerMethodArgumentResolver{

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return User.class.isAssignableFrom(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		HttpServletRequest requset = (HttpServletRequest) webRequest.getNativeRequest();
		HttpSession session = requset.getSession();
		
		
		User user = (User) session.getAttribute("memberSession");
		if(null == user) {
			user = new User();
			user.setUserId(StringUtils.EMPTY);
			user.setUserLevel(StringUtils.EMPTY);
			session.setAttribute("userSession", user);
		}
		return user;
	}

}
