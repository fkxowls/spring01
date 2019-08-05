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
	<form name="frmArticle" method="post"
		action="/board/modifyForm.do?articleId=${articleVo.articleId}">
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

			<table id="commentList" data-article-id="${articleVo.articleId}" border=0 align="center">
				
			</table>

			<tr id="tr_btn">
				<td colspan="2" align="center">
					<input type="submit" value="수정하기" <%-- onClick="fn_modifyForm('${articleVo.articleId}','${articleVo.writeMemberId }')" --%>>
					<input type="button" value="삭제하기" onClick="fn_delete('${articleVo.articleId}','${memberSession.memberId }')"> 
					<input type=button value="리스트로 돌아가기">
					<input type=button value="답글쓰기" onClick="fn_reply('${articleVo.articleId}')">
				</td>
			</tr>
		</table>
	</form>
</body>
<script type="text/javascript">

	function fn_modifyForm(articleId,writeMemberId){
		alert(writeMemberId);
		frmArticle.action = "${contextPath}/board/modifyForm.do?writeMemberId="+writeMemberId+"&articleId="+articleId;
		frmArticle.submit();
		/* var data = {};
		data.articleId = articleId;
		data.writeMemberId = writeMemberId;
		var sendData = JSON.stringify(data);
		
		$.ajax({
			type: 'post',
			url : '${contextPath}/board/modifyForm.do',
			headers : {
				"Content-Type" : "application/json"
			},
			dataType : 'json',
			data : sendData
		}).done(function(data){
			alert(data.msg);
			$(location).attr('href', data.redirect);
		}); */
		
	}

	function fn_delete(articleId,sessionId) {
		var data = {};
		data.articleId = articleId;
		data.writeMemberId = sessionId;
		var sendData = JSON.stringify(data);
		
		$.ajax({
			type: 'delete',
			url : "${contextPath}/board/"+articleId,
			headers : {
				"Content-Type" : "application/json"
			},
			dataType : 'json',
			data : sendData
		}).done(function(data){
			alert(data.msg);
			$(location).attr('href', data.redirect);
		});
	}

	function fn_reply(num) {
		var title = $('#title').val();
		alert(title);
		frmArticle.action = "/board/writeReplyForm.do?articleId="+num+"&title="+title;
		frmArticle.submit();
	}

	function getCommentList() {
		var articleId = $('#commentList').data('articleId');
        var writeMemberId = $('#writeMemberId').val(); 
		var commentsPage = 1;
		var data = {};
		
		data.articleId = articleId;
		data.commentsPage = commentsPage;
		data.writeMemberId = writeMemberId;
		
		var sendData = JSON.stringify(data);
		alert(sendData);
		
		$.ajax({
			type : 'get',
            url : '${contextPath}/board/commentList.do?articleId='+articleId+'&commentsPage='+commentsPage+'&writeMemberId='+writeMemberId,
            dataType : 'json',
            data : $('#commentForm').serialize(),
            contentType : "application/json; charset=UTF-8",
		}).done(function(data){
			var html = "";
            alert("성공");
            alert(data.commentsList.length);
            for (var i = 0; i < data.commentsList.length; i++) {
            	alert(i);
                html += "<tr><td align=left width=110>";
                html += data.commentsList[i].writeMemberId + ":&nbsp;</td>";
                html += "<td width=230>";
                /* html +="<c:if test='"+result[i].level+">0'>";
                html +="[답글]</c:if>""; */
                html += data.commentsList[i].content + "</td>";
                html += "<td>";
                html += "<a id='reCommentBtn' onClick=fn_reComment("+data.commentsList[i].replyId+")>"
                        + "답글" + "</a></td></tr>"
                html += "<tr class=recomment"+data.commentsList[i].replyId+"></tr>";
            }
            $("#commentList").html(html);
        })
	}
	
	function fn_insertComment() {
		var articleId = $('#commentList').data('articleId');
		var content = $('input[name=comment]').val();
		var secretTypeCd = 10;
		var data = {};
		
		data.articleId = articleId;
		data.content = content;
		data.secretTypeCd = secretTypeCd;
		
		var sendData = JSON.stringify(data);

		$.ajax({
			type : "post",
			url : "${contextPath}/board/writeComment.do",
			headers : {
				"Content-Type" : "application/json"
			},
			dataType : "json",
			data : sendData
			}).done(function(data){
				getCommentList();
			})
	}

	function fn_reComment(idx) {
		var tagName = ".recomment" + idx;
		var html = "";
		html += "<td colspan=3><input type=text size=45 name=reComment></td>";
		html += "<td><input type=button value=등록 onClick=insertReComment('" + idx + "')></td>";
		html += "<td><input type=</td>"
		$(tagName).append(html);
	}

	function insertReComment(idx) {
		alert(idx);
		var articleId = $('#commentList').data('articleId');
		var content =	$('input[name=reComment]').val();
		var parentId = idx
		var data = {};
		
		data.articleId = articleId;
		data.content = content;
		data.parentId = parentId;
		
		var sendData = JSON.stringify(data);
	
		$.ajax({
			type : "post",
			url : "${contextPath}/board/writeReComment.do",
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