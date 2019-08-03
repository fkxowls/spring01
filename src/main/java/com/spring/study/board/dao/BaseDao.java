package com.spring.study.board.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;

import com.spring.study.board.vo.CommonRequestDto;
import com.spring.study.common.model.PageList;

public class BaseDao {
	
	@Autowired
	SqlSession sqlSession;

	protected <E> PageList<E> selectPageDto(String statement, Object parameter) {
		return selectPageDto(statement, null, parameter);
	}

	protected <E> PageList<E> selectPageDto(String statement, String countStatement, Object parameter) {
		CommonRequestDto dto = (CommonRequestDto) parameter;
		
		int totalCount = 0;
		if (null != countStatement) {
			totalCount = sqlSession.selectOne(countStatement);
		}

		List<E> list = sqlSession.selectList(statement, dto);
		boolean hasNext = false;
		if(dto.moreView() && null != list) {
			hasNext = list.size() > dto.getPageSize() ? true : false;
		}
		if(hasNext) {
			list = list.subList(0, dto.getPageSize());
		}
		
		return new PageList<E>(dto.getPage(), dto.getPageSize(), list, totalCount, hasNext);
	}
}
