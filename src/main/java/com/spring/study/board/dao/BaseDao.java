package com.spring.study.board.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;

import com.spring.study.common.model.CommonParamter;
import com.spring.study.common.model.PageList;

public class BaseDao {

	@Autowired
	SqlSession sqlSession;

	protected <E> PageList<E> selectPageDto(String statement, CommonParamter parameter) {
		return selectPageDto(statement, null, parameter);
	}

	protected <E> PageList<E> selectPageDto(String statement, String countStatement, CommonParamter parameter) {
		int totalCount = 0;
		if (null != countStatement) {
			totalCount = sqlSession.selectOne(countStatement);
		}
		//XXX 여기서 Parameter객체를 Vo에 담아서 보내야 하는데 startNum과 endNum처럼 테이블컬럼과 상관 없는 필드가 있으면 그냥 dto로 보내야하는건지
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
