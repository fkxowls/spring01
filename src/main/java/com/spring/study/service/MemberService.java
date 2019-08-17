package com.spring.study.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.study.dao.MemberDAO;
import com.spring.study.model.member.Member;
import com.spring.study.model.member.User;
import com.spring.study.model.member.UserVo;

@Service("memberService")
public class MemberService {
	private static final Logger logger = LoggerFactory.getLogger(MemberService.class);
	
	@Autowired
	MemberDAO memberDAO;
	
	public UserVo login(UserVo memberVo) {
		logger.info("============		memberService login() start		==============");
		UserVo memVo = memberDAO.checkMember(memberVo);
		logger.info("============		memberService login() end		==============");
		return memVo;
	}

	public UserVo setMemberSession(UserVo user) {
		UserVo UserVo = new UserVo();
		UserVo.setMemberId(user.getMemberId());
		UserVo.setMemberLevel(user.getMemberLevel());
		return UserVo;
	}

}
