package com.spring.study.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.spring.study.common.model.BaseDao;
import com.spring.study.common.model.BaseParam;
import com.spring.study.common.model.PageList;
import com.spring.study.model.comments.Comment;
import com.spring.study.model.comments.CommentDto;
import com.spring.study.model.comments.CommentParam;
import com.spring.study.model.comments.CommentVo;
import com.spring.study.model.user.User;

import defalut.ArticleController;


@Repository("commentDAO")
public class CommentDao  extends BaseDao{
	private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);
	private static String mapper = "mapper.article";
	
	@Autowired
	SqlSession sqlSession;
	
	public boolean isExistsComment(String commentId) {
		String result = sqlSession.selectOne("mapper.comment.isExistComment", commentId);
		
		return false;
	}
	
	public PageList<Comment> commentsList(CommentParam commentsParam) {
		return super.selectPageDto("mapper.comment.listComment", commentsParam);
	}
		
	public int writeComment(CommentDto dto, User user) {
		CommentVo vo = new CommentVo();	
		vo.setWriteMemberId(user.getUserId());
		vo.setArticleId(dto.getArticleId());
		vo.setParentId(dto.getParentId());
		vo.setContent(dto.getContent());
		vo.setSecretTypeCd(dto.getSecretTypeCd());
		
		return sqlSession.insert("mapper.comment.insertComment", vo);
	}
	//PageInfo 제대로 출력안됨, endPagingInfo 담는거 못함, BaseDao로 이동해야함
	public <E> Map<String, PageList<E>> getFeedList(BaseParam baseParam, Function<E, String> articleIdGroup) {
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
		//XXX TotalCount로 페이징 처리한 정보는 어떻게 해야할까요
		listMap = commentsList.stream().collect(Collectors.groupingBy(articleIdGroup));
		List<E> totalComments = sqlSession.selectList("mapper.comment.totalComments", baseParam);
		//totalCount
//		Map<String, String> totalComments = sqlSession.selectMap("mapper.comment.totalComments",baseParam);
//		listMap = totalComments.stream().collect(Collectors.groupingBy(articleIdGroup));
		
//		Set<String> keys = listMap.keySet();
		List<E> list = new ArrayList(listMap.values());
//		for(int i=0; i<listMap.size() i++) {
//			if(listMap.containsKey()) {
//				
//			}
//		}
		

		return pageListMap;
	}
	
	public String getWriterOfArticle(String articleId) {
		// 글작성자 가져오는 쿼리 날리기
		return null;
	}

	
}
