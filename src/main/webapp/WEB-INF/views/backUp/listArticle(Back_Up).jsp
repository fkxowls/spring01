<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath"  value="${pageContext.request.contextPath}"  />
<!DOCTYPE html>
<html>
<head>
<meta charset="EUC-KR">
 <style>
   .cls1 {text-decoration:none;}
   .cls2{text-align:center; font-size:30px;}
 	.s1 {float: right}
  </style>
 
<title>글목록</title>
</head>
<body>
	<c:if test="${memberSession.memberId != null }">
	<span class="s1">${memberSession.memberId }님 Level:${memberSession.memberLevel } <a href="${contextPath }/member/logout">로그아웃</a></span>
	</c:if>
	<c:if test="${memberSession.memberId== null }">
	<span class="s1"><a href="${contextPath }/member/loginForm">로그인</a></span>
	</c:if>
	<br><br>

<h1>글개수: ${totalArticle } </h1>
	<table align="center" border="1" width="80%">
		<tr height="10" align="center" bgcolor="lightgreen">
			<td>글번호: </td>
			<td>작성자</td>
			<td>제목</td>
			<td>작성일</td>
		</tr>
		
		<c:choose>
		<c:when test='${articleList == null } '>
			<tr height='10'>
				<td colspan='4'>
					등록된글이 없습니다
				</td>
			</tr>
		</c:when>		
		 <c:when test='${articleList !=null }' >
    <c:forEach  var='articleVO' items='${articleList }' varStatus='articleNum' >
     <tr align='center' data-article-no='${articleVO.articleNo}'>
	<td width='5%'>${articleNum.count}</td>
	<td width='10%'><span>${articleVO.writeMemberId }</span></td>
	<td align='left'  width='35%'>
	    <span style='padding-right:30px'></span>    
	   <c:choose>
	      <c:when test='${articleVO.lvl > 1 }'>  
	         <c:forEach begin='1' end='${articleVO.lvl }' step='1'>
	             <span style='padding-left:10px'></span> 
	         </c:forEach>
	         <span style='font-size:12px;'>[답변]</span>
                   <a class='cls1' href='${contextPath}/board/viewArticle.do?articleNo=${articleVO.articleNo}'>${articleVO.title}</a>
	          </c:when>
	          <c:otherwise>
	            <a class='cls1' href='${contextPath}/board/viewArticle.do?articleNo=${articleVO.articleNo}'>${articleVO.title}</a>
	          </c:otherwise>
	        </c:choose>
	       
	  </td>
	  <td  width='10%'><fmt:formatDate value='${articleVO.writeDate}' /></td> 
	</tr>
    </c:forEach>
     </c:when>
    </c:choose>
</table>

<div class="cls2">
 	<c:if test="${totalArticle<=100 }" >
	      <c:forEach   var="page" begin="${startPage }" end="${(totalArticle/10)+1}" step="1" ><!-- ${(totalArticle-1)/10 +1} -->	        	 
	            <a class="no-uline"  href="${contextPath}/board/listArticle.do?pageNum=${page}">${page } </a>
	      </c:forEach>
    </c:if>
    <c:if test="${totalArticle>100 }" >
	      <c:forEach   var="page" begin="${startPage }" end="${startPage+9}" step="1" ><!-- ${(totalArticle-1)/10 +1} -->	        	 
	            <a class="no-uline"  href="${contextPath}/board/listArticle.do?pageNum=${page}">${page } </a>
	      </c:forEach>
    </c:if>
    
</div>
	<a class="cls1" href="${contextPath}/board/WriteForm.do"><p class=cls2>글쓰기</p></a>
</body>
</html>