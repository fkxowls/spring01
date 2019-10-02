package com.spring.study.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.spring.study.model.user.UserFollowVo;

@Repository
public class UserFollowDao {
	private static final String NAME_SPACE = "mapper.follow.";//com.kkk26kkk.bbs.UserFollow.dao. <이런식으로 변경하기
	@Autowired
	private SqlSessionTemplate sqlSession;
	public int insetUserFollow(UserFollowVo userFollowVo) {
		return sqlSession.insert("insertUserFollow", userFollowVo);
	}
	
	public List<String> selectFolloweeIds(String userId) {
		return sqlSession.selectList(NAME_SPACE + "selectFolloweeIds", userId);
	}
}
