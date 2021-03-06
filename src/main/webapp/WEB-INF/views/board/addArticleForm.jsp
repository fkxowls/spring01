<%@ page language="java" contentType="text/html; charset=UTF-8"
     pageEncoding="UTF-8"
    isELIgnored="false" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath"  value="${pageContext.request.contextPath}"  /> 
<%
  request.setCharacterEncoding("UTF-8");
%> 
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
<body>
<h1 style="text-align:center">글쓰기</h1>
  <form name="articleForm" method="post" action="${contextPath }/board/WrtiteArticle.do">
    <table border="0" align="center">
      <tr>
					<td align="right"> 작성자</td>
					<td colspan=2  align="left"><input type="text" size="20" maxlength="100" name="writeMemberId" value="${writeMemberId }" readOnly/> </td>
			</tr>

	     <tr>
			   <td align="right">글제목: </td>
			   <c:if test="${null == articleDto }">
			   <td colspan="2"><input type="text" size="67"  maxlength="500" id="title" name="title"/></td>
		      </c:if>
		      <c:if test="${null != articleDto }">
               <td colspan="2"><input type="text" size="67"  maxlength="500" data-article-id="${articleDto.articleId }" id="title" name="title" value="${articleDto.title }"/></td>
              </c:if>
		 </tr>
		 <c:if test="${null == articleDto}">
		 <tr>
			   <td align="right">공지글 여부 </td>
			   <td colspan="2">
			   		<input type="radio" name="articleTypeCd" value="20">일반글
			   		<input type="radio" name="articleTypeCd" value="10">공지글
			   </td>
		 </tr>
		 </c:if>
	 		<tr>
				<td align="right" valign="top"><br>글내용: </td>
				<td colspan=2><textarea id="content" name="content" rows="10" cols="65" maxlength="4000"></textarea> </td>
     </tr>

	    <tr>
	      <td align="right"> </td>
	      <td colspan="2">
	       <input type="button" value="글쓰기" onClick="fn_writeArticle()" />
	       <input type=button value="목록보기"/>
	      </td>
     </tr>
    </table>
    </form>
</body>
<script type="text/javascript">
	function fn_writeArticle(){
		var title = $('input[name=title]').val();
		var content = $('textarea[name=content]').val();
		var writeMemberId = $('input[name=writeMemberId]').val();
		var articleTypeCd = $('input[name=articleTypeCd]').val();
		var displayStartDate = new Date("2019-08-17T09:38:51.249Z");
		var displayEndDate = new Date("2019-09-10T09:38:51.249Z");
		var data = {};
		alert(articleTypeCd)
		data.title = title;
		data.writeMemberId = writeMemberId;
		data.articleTypeCd = articleTypeCd;
		data.content = content;
		
		var noticeArticle = {}
		data.displayStartDate = displayStartDate;
		data.displayEndDate = displayEndDate;
		
		data.noticeArticle = noticeArticle;
		
		var sendData = JSON.stringify(data);
		alert(sendData);
		
		$.ajax({
			type : "post",
			url : "/board/article",
			headers : {
				"Content-Type" : "application/json"
			},
			dataType : "json",
			data : sendData
			}).done(function(data){
				alert(data.msg);
				$(location).attr('href',data.redirect);
			});
	}
	

</script>
</html>