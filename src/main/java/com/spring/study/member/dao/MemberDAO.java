package com.spring.study.member.dao;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.spring.study.board.controller.AticleController;
import com.spring.study.member.model.MemberVo;
import com.spring.study.member.model.Member;

@Repository("MemberDAO")
public class MemberDAO {
	private static final Logger logger = LoggerFactory.getLogger(AticleController.class);
	@Autowired
	SqlSession sqlSession;
	
	public MemberVo checkMember(MemberVo memberVo) {
		logger.info("===================== 			memberDAO checkMember() start		==========================");
		return sqlSession.selectOne("mapper.member.checkMember", memberVo);
	}

}
