<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:forEach var="articleVO" items="${articleList }" varStatus="vs">
    <ul align="center" class="contents" <c:if test="${vs.last}">data-has-next="${hasNext}"</c:if>>
        <li>${articleVO.articleNo }</li>
        <li class="left"><a class='cls1' href="${contextPath}/board/viewArticle.do?articleNo=${articleVO.articleNo}">${articleVO.title}</a></li>
        <li>${articleVO.writeMemberId }</li>
    </ul>
</c:forEach>