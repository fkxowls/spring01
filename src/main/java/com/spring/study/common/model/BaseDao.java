package com.spring.study.common.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;

import com.spring.study.model.article.ArticleParam;

public class BaseDao {

	@Autowired
	SqlSession sqlSession;
	
	protected <E> PageList<E> selectPageDto(String statement, BaseParam parameter) {
		return selectPageDto(statement, null, parameter);
	}

	protected <E> PageList<E> selectPageDto(String statement, String countStatement, BaseParam parameter) {
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
	//메서드 이름 생각하기
	public <E> Map<String, PageList<E>> groupById(BaseParam baseParam, Function<E, String> articleIdGroup) {
		int totalCount = 0;
		boolean hasNext = false;
		
		Map<String, PageList<E>> pageListMap = new HashMap<>();
		Map<String, List<E>> listMap = new HashMap<>();
		List<E> commentsList = sqlSession.selectList("mapper.comment.listComment", baseParam);
		listMap = commentsList.stream().collect(Collectors.groupingBy(articleIdGroup));
		
		if(baseParam.isUseHasNext() && commentsList != null) {
			for(Entry<String, List<E>> entry : listMap.entrySet()) {	
				List<E> resultList = entry.getValue();
				if(resultList.size() > 10) { 
					hasNext = true; 
					resultList.subList(0, baseParam.getPageSize());
				}
				
				pageListMap.put(entry.getKey(), new PageList<E>(baseParam.getPage(), baseParam.getPageSize(), resultList, 0, hasNext));
			}
		}
		return pageListMap;
	}
}
