package com.spring.study.member.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.study.board.controller.AticleController;
import com.spring.study.member.dao.MemberDAO;
import com.spring.study.member.vo.Member;
import com.spring.study.member.vo.MemVo;

@Service("memberService")
public class MemberService {
	private static final Logger logger = LoggerFactory.getLogger(AticleController.class);
	
	@Autowired
	MemberDAO memberDAO;
	
	public MemVo login(MemVo memberVo) {
		logger.info("============		memberService login() start		==============");
		MemVo memVo = memberDAO.checkMember(memberVo);
		logger.info("============		memberService login() end		==============");
		return memVo;
	}

	public Member setMemberSession(MemVo member) {
		Member memberDTO = new Member();
		memberDTO.setMemberId(member.getMemberId());
		memberDTO.setMemberLevel(member.getMemberLevel());
		return memberDTO;
	}

}
