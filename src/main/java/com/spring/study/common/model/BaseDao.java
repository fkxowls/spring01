package com.spring.study.common.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;

import com.spring.study.model.article.ArticleReadCountVo;

public class BaseDao {
	@Autowired
	SqlSession sqlSession;
	
	protected <E> PageList<E> getPageListMore(String statement, BaseParam parameter) {
		return this.getPageList(statement, null, parameter);
	}
	
	protected <E> PageList<E> getPageListWithTotalCount(String statement, String countStatement, BaseParam parameter) {
		return this.getPageList(statement, countStatement, parameter);
	}

	protected <E> PageList<E> getPageList(String statement, String countStatement, BaseParam parameter) {
		
		List<E> list = sqlSession.selectList(statement, parameter);
		
		if(null == list) {
			return new PageList<E>(parameter.getPage(), parameter.getPageSize(), list, 0, false);
		}

		boolean hasNext = false;
		if(parameter.useMore() && list.size() > parameter.getPageSize()) {                                     
			hasNext = true;
			list = list.subList(0, parameter.getPageSize());
		}

		int totalCount = 0;                                                                                                                             
		if (parameter.useTotal() && null != countStatement) {                                                 
			totalCount = sqlSession.selectOne(countStatement);
		}

		return new PageList<E>(parameter.getPage(), parameter.getPageSize(), list, totalCount, hasNext);       
    }
	
/******************************************************************************************************************************************************/	
	
	protected <E> Map<String, PageList<E>> selectPageListMore(String statement, BaseParam parameter, Function<E, String> groupById) {
		return this.selectPageListMap(statement, null, parameter, groupById);
	}
	
	protected <E> Map<String, PageList<E>> selectPageListMapTotal(String statement, String statementTotalCount, BaseParam baseParam, Function<E, String> groupId) {
		return selectPageListMap(statement, statementTotalCount, baseParam, groupId);
	}
	
	protected <E> Map<String, PageList<E>> selectPageListMap(String statement, String countStatement, BaseParam parameter, Function<E, String> groupById) {
		Map<String, PageList<E>> pageListMap = new HashMap<>();
		
		List<E> list = sqlSession.selectList(statement, parameter);
		
		if(null == list) {
			pageListMap.put(null, new PageList<E>(parameter.getPage(), parameter.getPageSize(), list, 0, false));
			return pageListMap;
		}
		
		Map<String, List<E>> listMap = list.stream()
				.collect(Collectors.groupingBy(groupById));
		//Add Comment Paging Info
		Map<String, Integer> countMap = new HashMap<>();
		if (parameter.useTotal() && null != countStatement) {
			List<Total> countList = sqlSession.selectList(countStatement);
			countMap = countList.stream()
					.collect(Collectors.toMap(Total::getId, Total::getTotalCount));//"글번호", "카운트 수"
		}
		
		for(String key : listMap.keySet()) {
			List<E> subList = listMap.get(key);

			boolean hasNext = false;
			if(parameter.useMore() && subList.size() > parameter.getPageSize()) {
				hasNext = true;
				subList = subList.subList(0, parameter.getPageSize());
			}

			int totalCount = 0;
			Integer count = countMap.get(key); //"글번호", "카운트 수" 
			if (parameter.useTotal() && null != count) {
				totalCount = count;
			}

			pageListMap.put(key, new PageList<E>(parameter.getPage(), parameter.getPageSize(), subList, totalCount, hasNext));
		}
		
		return pageListMap;
		
	}
}
