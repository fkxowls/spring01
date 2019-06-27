<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<%
	request.setCharacterEncoding("UTF-8");
%>
<html>
<head>
<script  src="http://code.jquery.com/jquery-latest.min.js"></script>
</head>
<body>
<h1>${nowArticleNo }</h1>
	<form name="frmArticle" method="post"
		action="${contextPath}/board/modifyForm.do?articleNo=${articleVo.articleNo}">
		<table border=0 align="center">

			<tr>
				<td width="150" align="center" bgcolor="#FF9933">아이디</td>
				<td colspan="2"><input type=text value="${articleVo.writeMemberId }" id="writeMemberId" name="writeMemberId" /></td>
			</tr>
			<tr>
				<td width="150" align="center" bgcolor="#FF9933">제목</td>
				<td colspan="2"><input type=text value="${articleVo.title }" id="title" name="title" /></td>
			</tr>
			<tr>
				<td width="150" align="center" bgcolor="#FF9933">등록일자</td>
				<td colspan="2"><input type=text value="<fmt:formatDate value="${articleVo.writeDate}" />" /></td>
			</tr>
			<tr>
				<td colspan="3"><textarea rows="20" cols="60" id="" name="content">${articleVo.content }</textarea></td>
			</tr>
			<tr id="tr_b_reply">
				<td colspan="2"><input type="text" size="57" maxlength="350" id="comment" name="comment"></td>
				<td><input type=button value="등록" onClick="fn_insertComment()">
			</tr>

			<table id="commentList" data-article-no="${articleVo.articleNo}" border=0 align="center">
				
			</table>

			<tr id="tr_btn">
				<td colspan="2" align="center"><input type=submit value="수정하기">
					<input type="button" value="삭제하기" onClick="fn_delete()"> 
					<input type=button value="리스트로 돌아가기">
					<input type=button value="답글쓰기" onClick="fn_reply('${articleVo.articleNo}')">
				</td>
			</tr>
		</table>
	</form>
</body>
<script type="text/javascript">
	function fn_delete() {
		/* frmArticle.action="${contextPath}/board/deleteArticleForm.do";
		frmArticle.submit(); */

		var jsonText = '{"articleNo":${articleVo.articleNo},"writeMemberId":"${memberSession.memberId }"}';
		var sendData1 = JSON.stringify(jsonText);
		var sendData2 = JSON.parse(sendData1);
		if (confirm("삭제하겠습니까?")) {
			$.ajax({
				type : "post",
				url : "${contextPath}/board/deleteArticleForm.do",
				headers : {
					"Content-Type" : "application/json"
				},
				dataType : 'json',
				data : sendData2,
				success : function(result) {
					if (result == "success") {
						alert("삭제완료");
					}
				},
				error : function(request, status, error) {
					alert("code = " + request.status + " message = " + request.responseText + " error = " + error); // 실패 시 처리
				}
			});
		}

	}

	function fn_reply(num) {
		var title = $('#title').val();
		alert(title);
		frmArticle.action = "${contextPath}/board/replyArticleForm.do?articleNo="+num+"&title="+title;
		frmArticle.submit();
	}

	function getCommentList() {
		// todo JSON.parse('{"' + decodeURI(location.search.substring(1)).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}').articleNo
		//var articleNo = JSON.parse('{"' + decodeURI(location.search.substring(1)).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}').articleNo;
		var articleNo = $('#commentList').data('articleNo');
		
		$.ajax({
			type : 'GET',
			url : '${contextPath}/board/commentList.do?articleNo=' + articleNo,
			dataType : 'json',
			data : $('#commentForm').serialize(),
			contentType : "application/json; charset=UTF-8",
			success : function(data) {
				var html = "";
				alert("성공");
				for (var i = 0; i < data.length; i++) {
					html += "<tr><td align=left width=110>";
					html += data[i].writeMemberId + ":&nbsp;</td>";
					html += "<td width=230>";
					/* html +="<c:if test='"+data[i].level+">0'>";
					html +="[답글]</c:if>""; */
					html += data[i].content + "</td>";
					html += "<td>";
					html += "<a id='reCommentBtn' onClick=fn_reComment("+data[i].replyNo+")>"
							+ "답글" + "</a></td></tr>"
					html += "<tr class=recomment"+data[i].replyNo+"></tr>";
				}
				$("#commentList").html(html);
			}
		})
	}
	
	function fn_insertComment() {
		var articleNo = $('#commentList').data('articleNo');
		var content = $('input[name=comment]').val();
		var data = {};
		
		data.articleNo = articleNo;
		data.content = content;
	
		var sendData = JSON.stringify(data);

		$.ajax({
			type : "post",
			url : "${contextPath}/board/inserComment.do",
			/* contentType: "application/json", */
			headers : {
				"Content-Type" : "application/json"
			},
			dataType : "json",
			data : sendData,
			success : function() {
				alert("success");
				getCommentList();
			},
			error : function() {
				alert(data);
			}
		})
	}

	function fn_reComment(idx) {
		var tagName = ".recomment" + idx;
		var html = "";
		html += "<td colspan=3><input type=text size=45 name=reComment></td>";
		html += "<td><input type=button value=등록 onClick=insertReComment('" + idx + "')></td>";
		$(tagName).append(html);
	}

	function insertReComment(idx) {
		alert(idx);
		var articleNo = $('#commentList').data('articleNo');
		var content =	$('input[name=reComment]').val();
		var data = {};
		
		data.articleNo = articleNo;
		data.content = content;
		
		var sendData = JSON.stringify(data);
	
		$.ajax({
			type : "post",
			url : "${contextPath}/board/inserReComment.do",
			/* contentType: "application/json", */
			headers : {
				"Content-Type" : "application/json"
			},
			dataType : "json",
			data : sendData,
			success : function() {
				alert("success");
				getCommentList();
			},
			error : function() {
				alert(sendData);
			}
		})
	}
	
$(document).ready(function(){
	 getCommentList();
 }) 
</script>
</body>
</html>