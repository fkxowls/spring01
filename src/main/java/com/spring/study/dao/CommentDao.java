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
	//Map<String, PageList<E>>//XXX 페이지 정보는 어떻게??
	public <E> Map<String, PageList<E>> getFeedList(BaseParam baseParam, Function<E, String> articleIdGroup) {
		int totalCount = 0;
		boolean hsaNext = false;
		if(baseParam.isUseEndCount()) {
			totalCount = sqlSession.selectOne("totalArticle");
		}
		
		Map<String, PageList<E>> pageListMap = new HashMap<>();
		Map<String, List<E>> listMap = new HashMap<>();
		List<E> commentsList = sqlSession.selectList("mapper.comment.listComment", baseParam);
		// {10037 = [[댓글1],[댓글2]], 10038 = [[댓글1],[댓글2]] } 페이지정보는 ???
		listMap = commentsList.stream().collect(Collectors.groupingBy(articleIdGroup));
//		Set<String> keys = listMap.keySet();
//		list.forEach(computeIfAbsent(keys, k -> new ArrayList<>()))
//		list.values().stream().collect(Collectors.toCollection(ArrayList::new));
		List<E> list = new ArrayList(listMap.values());
		
		PageList<E> pageList = new PageList<>(baseParam.getPage(),baseParam.getPageSize(),list,totalCount, false);
		pageListMap.put("commentPageList", pageList);

		return pageListMap;
	}
	
	public String getWriterOfArticle(String articleId) {
		// 글작성자 가져오는 쿼리 날리기
		return null;
	}

	
}
