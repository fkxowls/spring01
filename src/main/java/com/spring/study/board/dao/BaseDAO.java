package com.spring.study.board.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;

import com.spring.study.board.vo.PageDto;

public class BaseDAO {
	@Autowired
	SqlSession sqlSession;
	
	protected <E> PageDto<E> selectPageDto(String statement, Object parameter) {
		return selectPageDto(statement, null, parameter);
	}

	protected <E> PageDto<E> selectPageDto(String statement, String countStatement, Object parameter) { // parameter는 별도의 리퀘스트 DTO로 찢어준다
		PageDto<E> vo = (PageDto<E>)parameter;
		
		int page = vo.getPage();
		int pageSize = vo.getPageSize();
		int startNum = (page - 1) * pageSize + 1;
		int endNum = page * pageSize;

		int totalCount = 0;
		if(null == countStatement) {
			endNum = endNum + 1;
		} else {
			totalCount = sqlSession.selectOne(countStatement);
		}
		
		vo.setStartNum(startNum);
		vo.setEndNum(endNum);
		
		List<E> list = sqlSession.selectList(statement, vo);
		boolean hasNext = false;
		if(null == countStatement && null != list) {
			hasNext = list.size() > pageSize ? true : false;
		}
		if(true == hasNext) {
			list = list.subList(0, pageSize);
		}
		
		return new PageDto<E>(page, pageSize, totalCount, list);
	}
}
