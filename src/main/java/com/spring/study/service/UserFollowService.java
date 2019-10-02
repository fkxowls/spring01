package com.spring.study.service;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.spring.study.dao.UserFollowDao;
import com.spring.study.model.user.UserFollowVo;

public class UserFollowService {
	@Autowired
	private UserFollowDao userFollowDao;

	public void insertUserFollow(String followUserId, String loginUserId) throws SQLException {
		UserFollowVo userFollowVo = new UserFollowVo();
		userFollowVo.setFolloweeId(followUserId);
		userFollowVo.setFollowerId(loginUserId);
		
		int resultCnt = userFollowDao.insetUserFollow(userFollowVo);
		
		if(1 != resultCnt) {
			throw new SQLException("팔로우 추가에 실패 했습니다.");
		}
	}

	public List<String> getFolloweeIds(String userId) {
		return userFollowDao.selectFolloweeIds(userId);
	}
}
