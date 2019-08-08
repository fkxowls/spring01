<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:forEach var="articleVo" items="${articleList }" varStatus="vs">
    <ul align="center" class="contents" <c:if test="${vs.last}">data-has-next="${hasNext}"</c:if>>
        <li>${articleVo.articleId }</li>
        <%-- <li class="left"><a class='cls1' href="${contextPath}/board/viewArticle.do?articleId=${articleVO.articleId}">${articleVO.title}</a></li> --%>
        <li class="left"><a class='cls1' href="/board/${articleVo.articleId }">${articleVo.title}</a></li>
        <li>${articleVo.writeMemberId }</li>
        <li>${articleVo.writeDate }</li>
    </ul>
</c:forEach>