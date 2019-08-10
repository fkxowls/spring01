package com.spring.study.member.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.study.board.controller.OldAticleController;
import com.spring.study.member.dao.MemberDAO;
import com.spring.study.member.model.MemberVo;
import com.spring.study.member.model.Member;

@Service("memberService")
public class MemberService {
	private static final Logger logger = LoggerFactory.getLogger(OldAticleController.class);
	
	@Autowired
	MemberDAO memberDAO;
	
	public MemberVo login(MemberVo memberVo) {
		logger.info("============		memberService login() start		==============");
		MemberVo memVo = memberDAO.checkMember(memberVo);
		logger.info("============		memberService login() end		==============");
		return memVo;
	}

	public Member setMemberSession(MemberVo member) {
		Member memberDTO = new Member();
		memberDTO.setMemberId(member.getMemberId());
		memberDTO.setMemberLevel(member.getMemberLevel());
		return memberDTO;
	}

}
