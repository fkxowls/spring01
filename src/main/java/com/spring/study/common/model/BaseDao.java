package com.spring.study.common.model;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseDao {

	@Autowired
	SqlSession sqlSession;

	protected <E> PageList<E> selectPageDto(String statement, BaseParam parameter) {
		return selectPageDto(statement, null, parameter);
	}

	protected <E> PageList<E> selectPageDto(String statement, String countStatement, BaseParam parameter) {
		int totalCount = 0;
		if (null != countStatement) {
			totalCount = sqlSession.selectOne(countStatement);
		}
		
		List<E> list = sqlSession.selectList(statement, parameter);
		boolean hasNext = false;
		if (parameter.moreView() && null != list) {
			hasNext = list.size() > parameter.getPageSize() ? true : false;
		}
		if (hasNext) {
			list = list.subList(0, parameter.getPageSize());
		}

		return new PageList<E>(parameter.getPage(), parameter.getPageSize(), list, totalCount, hasNext);
	}
}