<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    isELIgnored="false" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
  request.setCharacterEncoding("UTF-8");
%> 
<c:set var="contextPath"  value="${pageContext.request.contextPath}"  />
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>header</title>
</head>
<body>

	<table border=0 width="100%">
		<tr>
			<td>
				<a >
				</a>
			</td>
			<td>
				<h1><font size=30></font></h1>
			</td>
			<%-- <td>
				<!-- <a href="#"><h3>로그인</h3></a> -->
				<c:choose>
					<c:when test="${isLogOn == true && member!=null }">
						<h3>환영합니다. ${member.name }님</h3>
						<a ><h3>로그아웃</h3></a>
					</c:when>
					<c:otherwise>
						<a ><h3>로그인</h3></a>
					</c:otherwise>
				</c:choose>
			</td> --%>
			</tr>
	</table>
</body>
</html>