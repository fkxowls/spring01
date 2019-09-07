package com.spring.study.dao;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.spring.study.model.user.User;
import com.spring.study.model.user.UserVo;

@Repository("MemberDao")
public class MemberDao {
	private static final Logger logger = LoggerFactory.getLogger(MemberDao.class);
	@Autowired
	SqlSession sqlSession;
	
	public UserVo checkMember(UserVo userVo) {
		logger.info("===================== 			memberDAO checkMember() start		==========================");
		return sqlSession.selectOne("mapper.member.checkMember", userVo);
	}

}
