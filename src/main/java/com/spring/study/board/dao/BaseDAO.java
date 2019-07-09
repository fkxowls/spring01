package com.spring.study.board.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;

import com.spring.study.board.vo.PageDto;
import com.spring.study.board.vo.PageDto.Builder;
import com.spring.study.board.vo.PagingResponseDTO;

public class BaseDAO {
	@Autowired
	SqlSession sqlSession;

	protected <E> PagingResponseDTO<E> selectPageDto(String statement, Object parameter) {
		return selectPageDto(statement, null, parameter);
	}

	protected <E> PagingResponseDTO<E> selectPageDto(String statement, String countStatement, Object parameter) { // parameter는
																													// 별도의																									// 찢어준다
		PageDto<E> vo = (PageDto<E>) parameter;

		int page = vo.getPage();
		int pageSize = vo.getPageSize();
		int startNum = (page - 1) * pageSize + 1;
		int endNum = page * pageSize;

		/********************
		 * 불변객체로 쭈욱 가고싶은데 hasNext,endPage 분기를 어떻게 할까 기존 pageDTO에있던 setter삭제 빌더클래스에
		 * startNum과 endNum구현 page와 pageSize만 받은 DTO를 파라미터로 받고 필요한 startNum과 endNum를 가공한
		 * 후 새로운 pageDTO를 생성??
		 *********************/

		PageDto.Builder requestVo = new Builder(page, pageSize);
		requestVo.startNum(startNum);

		int totalCount = 0;
		if (null == countStatement) {
			endNum = endNum + 1;
			requestVo.endNum(endNum).build();
		} else {
			requestVo.endNum(endNum).build();
			totalCount = sqlSession.selectOne(countStatement);
		}

		List<E> list = sqlSession.selectList(statement, requestVo);
		boolean hasNext = false;
		if (null == countStatement && null != list) {
			hasNext = list.size() > pageSize ? true : false;
		}
		if (true == hasNext) {
			list = list.subList(0, pageSize);
			//빌더패턴으로 바꾸기
			return new PagingResponseDTO<E>(page, pageSize, totalCount, list, hasNext);
		}
		return new PagingResponseDTO<E>(page, pageSize, totalCount, list);
	}
}
