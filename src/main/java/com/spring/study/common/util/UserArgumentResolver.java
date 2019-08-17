package com.spring.study.common.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.spring.study.model.member.Member;
import com.spring.study.model.member.User;
import com.spring.study.model.member.UserVo;

public class UserArgumentResolver implements HandlerMethodArgumentResolver{

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		System.out.println(parameter.getParameterType());
		System.out.println(Member.class.getClass().getTypeName());
		return User.class.isAssignableFrom(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		HttpServletRequest requset = (HttpServletRequest) webRequest.getNativeRequest();
		HttpSession session = requset.getSession();
		User user = (User) session.getAttribute("memberSession");
		return user;
	}

}
