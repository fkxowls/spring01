<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<%
	request.setCharacterEncoding("UTF-8");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style>
#tr_file_upload {
	display: none;
}

#tr_btn_modify {
	display: none;
}
</style>
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
</head>
<body>
	<form name="frmArticle" method="post"
		action="${contextPath}/board/modifyArticle.do">
		<table border=0 align="center">
			<%-- 
  <tr>
   <td width=150 align="center" bgcolor=#FF9933>
      글번호
   </td>
   <td colspan="2">
    <input type="text"  value="${articleVo.articleId }"  disabled />
    <input type="hidden" name="articleId" id="i_articleId" value="${articleVo.articleId}"  />
   </td> 
  </tr>
   --%>
			<tr>
				<td width="150" align="center" bgcolor="#FF9933">아이디</td>
				<td colspan="2"><input type=text value="${articleVo.writeMemberId }"
					name="writeMemberId" disabled /></td>
			</tr>
			<tr>
				<td width="150" align="center" bgcolor="#FF9933">제목</td>
				<td colspan="2"><input type=text value="${articleVo.title }" name="title" /></td>
			</tr>
			<tr>
				<td align="right">공지글 여부</td>
				<td colspan="2"><input type="radio" name="isNotice"
					value="1">공지글 <input type="radio" name="isNotice"
					value="0">일반글</td>
			</tr>
			<tr>
				<td width="150" align="center" bgcolor="#FF9933">등록일자</td>
				<td colspan="2">
					<input type=text value="<fmt:formatDate value="${articleVo.writeDate}" />" disabled />
				</td>
			</tr>
			<tr>
				<td colspan="3"><input type="text" name="content2" /> <textarea
						id="content" name="content" rows="20" cols="60" maxlength="4000"></textarea>
				</td>
			</tr>

			<tr id="tr_btn">
				<td colspan="2" align="center"><input type="button" id="btn1"
					name="btn1" onClick="fn_modify('${articleVo.articleId}')" value="수정완료" /></td>
			</tr>
		</table>
	</form>
	<script type="text/javascript">
		function fn_modify(articleId) {
			/* var jsonStr = JSON.stringify(($.arrayToJsonObject($('form').serializeArray()))); */

			var title = $('input[name=title]').val();
			var content = $('textarea[name=content]').val();
			var articleId = articleId;
			var writeMemberId = $('input[name=writeMemberId]').val();
			var data = {};

			data.title = title
			data.content = $('textarea[name=content]').val();
			data.articleId = articleId;
			data.writeMemberId = writeMemberId;

			var sendData = JSON.stringify(data);

			$.ajax({
				type : 'put',
				/* url : '${contextPath}/board/modifyArticle', */
				url:  '${contextPath}/board/'+articleId,
				contentType : 'application/json',
				dataType : 'json',
				data : sendData
			}).done(function(data) {
				alert(data.msg)
				$(location).attr('href', data.redirect);
			})

		}
	</script>
</body>
</html>