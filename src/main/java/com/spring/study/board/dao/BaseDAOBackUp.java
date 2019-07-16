package com.spring.study.board.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;

import com.spring.study.board.vo.CommonRequestDto;
import com.spring.study.board.vo.PagingResponseDTO;

public class BaseDAOBackUp {
	@Autowired
	SqlSession sqlSession;

	protected <E> PagingResponseDTO<E> selectPageDto(String statement, Object parameter) {
		return selectPageDto(statement, null, parameter);
	}

	protected <E> PagingResponseDTO<E> selectPageDto(String statement, String countStatement, Object parameter) {
		CommonRequestDto dto = (CommonRequestDto) parameter;
		
		/********************
		 * 불변객체로 쭈욱 가고싶은데 hasNext,endPage 분기를 어떻게 할까 기존 pageDTO에있던 setter삭제 빌더클래스에
		 * startNum과 endNum구현 page와 pageSize만 받은 DTO를 파라미터로 받고 필요한 startNum과 endNum를 가공한
		 * 후 새로운 pageDTO를 생성??
		 *********************/

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
		
		return new PagingResponseDTO<E>(dto.getPage(), dto.getPageSize(), list, totalCount, hasNext);
	}
}
