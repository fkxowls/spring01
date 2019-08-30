package com.spring.study.common.model;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;

import com.spring.study.model.article.ArticleParam;

public class BaseDao {

	@Autowired
	SqlSession sqlSession;
															//XXX BaseDao는 BaseParam으로만 받아야하는건가요??
	protected <E> PageList<E> selectPageDto(String statement, ArticleParam parameter) {
		return selectPageDto(statement, null, parameter);
	}

	protected <E> PageList<E> selectPageDto(String statement, String countStatement, ArticleParam parameter) {
		int totalCount = 0;
		if (parameter.isUseEndCount() && null != countStatement) {
			totalCount = sqlSession.selectOne(countStatement);
		}
		
		List<E> list = sqlSession.selectList(statement, parameter);
		boolean hasNext = false;
		if (parameter.isUseHasNext() && null != list) {
			hasNext = list.size() > parameter.getPageSize() ? true : false;
		}
		if (hasNext) {
			list = list.subList(0, parameter.getPageSize());
		}

		return new PageList<E>(parameter.getPage(), parameter.getPageSize(), list, totalCount, hasNext);
	}
}
