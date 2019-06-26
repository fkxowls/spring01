<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    isELIgnored="false" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath"  value="${pageContext.request.contextPath}"  />

<%
  request.setCharacterEncoding("UTF-8");
%> 
<!-- <!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style>   
</style>
<script  src="http://code.jquery.com/jquery-latest.min.js"></script>
</head> -->
<body>
 <form name="frmArticle" method="post" action="${contextPath}/board/modifyForm.do?articleNo=${articleVo.articleNo}" >
  <table  border=0  align="center">
<%--   <tr>
   <td width=150 align="center" bgcolor=#FF9933>
      글번호
   </td>
   <td colspan="2">
    <input type="text" name="articleNo" value="${articleVo.articleNo }"  />
    <input type="hidden" name="articleNO" id="i_articleNO" value="${articleVo.articleNo}"  />
   </td>
  </tr> --%>
  <tr>
    <td width="150" align="center" bgcolor="#FF9933">
      아이디
   </td>
   <td  colspan="2">
    <input type=text value="${articleVo.writeMemberId }" id="writeMemberId" name="writeMemberId"   />
   </td>
  </tr>
  <tr>
    <td width="150" align="center" bgcolor="#FF9933">
      제목 
   </td>
   <td  colspan="2">																	<!-- disabled면 왜 값이 안넘어가는가  -->
    <input type=text value="${articleVo.title }" id="title" name="title"    />
   </td>   
  </tr>
  <tr>
	   <td width="150" align="center" bgcolor="#FF9933">
	      등록일자
	   </td>
	   <td  colspan="2">
	    <input type=text value="<fmt:formatDate value="${articleVo.writeDate}" />"  />
	   </td>   
  </tr>
  <tr>
   <td colspan="3">
    <textarea rows="20" cols="60" id="" name="content"  >${articleVo.content }</textarea>
   </td>  
  </tr>
  <tr id="tr_b_reply">
		<td colspan="2"><input type="text"   size="57" maxlength="350" id="comment" name="comment"></td>
		<td><input type=button value="등록" onClick="fn_insertComment('${articleVo.articleNo}','${memberSession.memberId}')">
	
	</tr>

<!--   <form id="commentForm" name="commentList" method="post">
  </form>  -->
  	<table id="commentList" border=0  align="center">
  	
  		
  	</table>
  
  <tr  id="tr_btn"    >
   <td colspan="2" align="center">
      
	    <input type=submit value="수정하기">
	  	<input type="button" value="삭제하기" onClick="fn_delete()">
	    <input type=button value="리스트로 돌아가기">
	    <input type=button value="답글쓰기" onClick="fn_reply()">
   </td>
  </tr>
   </table>
 </form>
 </body>
<script type="text/javascript">

function fn_delete(){
	/* frmArticle.action="${contextPath}/board/deleteArticleForm.do";
	frmArticle.submit(); */
	
	
	var jsonText = '{"articleNo":${articleVo.articleNo},"writeMemberId":"${memberSession.memberId }"}';
	var sendData1 = JSON.stringify(jsonText);
	var sendData2 = JSON.parse(sendData1);
	if(confirm("삭제하겠습니까?")){
		$.ajax({
		type: "post",		
		url: "${contextPath}/board/deleteArticleForm.do",
		headers: {
			"Content-Type" : "application/json"
		},
		dataType:  'json',
		data: sendData2,
		success: function(result){
			if(result == "success"){
				alert("삭제완료");
			}
		},
		error:function(request,status,error){
		        alert("code = "+ request.status + " message = " + request.responseText + " error = " + error); // 실패 시 처리
		}
		});
	}
	
}

function fn_reply(){
	frmArticle.action="${contextPath}/board/replyArticleForm.do?articleNo=${nowArticleNo}";
	frmArticle.submit();
	
	/* $.ajax({
		type: "post",
		url: "${contextPath}/board/replyArticleForm.do",
		success:function(){	
		} 
	}) */
}

function queryStringToJSON(queryString) {
  if(queryString.indexOf('?') > -1){
    queryString = queryString.split('?')[1];
  }
  var pairs = queryString.split('&');
  var result = {};
  pairs.forEach(function(pair) {
    pair = pair.split('=');
    result[pair[0]] = decodeURIComponent(pair[1] || '');
  });
  return result;
}

function getCommentList(articleNo,writerId){
	// todo JSON.parse('{"' + decodeURI(location.search.substring(1)).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}').articleNo
	
	$.ajax({
		type: 'GET',
		url: '${contextPath}/board/commentList.do?articleNo='+articleNo,
		dataType: 'json',
		data: $('#commentForm').serialize(),
		contentType: "application/json; charset=UTF-8", 
		success: function(data){
			var html = "";
			alert("성공");
			for(var i=0; i<data.length; i++){
				html +="<tr><td align=left width=110>";
				html +=data[i].writeMemberId+":&nbsp;</td>";
				html +="<td width=230>";
				/* html +="<c:if test='"+data[i].level+">0'>";
				html +="[답글]</c:if>""; */
				html +=data[i].content+"</td>";
				html +="<td>";
				html +="<a onClick=fn_reComment('"+data[i].replyNo+"','"+articleNo+"','"+writerId+"')>"+"답글"+"</a></td></tr>"
				html +="<tr class=recomment"+data[i].replyNo+"></tr>";
			}
			$("#commentList").html(html); 
		}
	})
}

function fn_insertComment(articleNum,writerId){
	var content = $('input[name=comment]').val();
	var data = {};
	data.articleNo = articleNum;
	data.writeMemberId = writerId;
	data.content = content;
	
	var jsonText = '{"articleNo": "'+articleNum+'","writeMemberId":"'+writerId+'","content":"'+ content+'"}';
	var sendData1 = JSON.stringify(jsonText);
	var sendData2 = JSON.parse(sendData1);
	
	$.ajax({
		type: "post",
		url: "${contextPath}/board/inserComment.do",
		/* contentType: "application/json", */
		headers: {
			"Content-Type" : "application/json"
		},
		dataType: "json",
		data: JSON.stringify(data),
		success: function(){
			alert("success");
			getCommentList(articleNum);
		},
		error: function(){
			alert(sendData2);
		}
	})
}

function fn_reComment(idx,articleNo,writerId){
	var tagName = ".recomment"+idx; 
	var html = "";
	html +="<td colspan=3><input type=text size=45 name=reComment></td>"; 
	html +="<td><input type=button value=등록 onClick=insertReComment('"+idx+"','"+articleNo+"','"+writerId+"')></td>";
	$(tagName).append(html);
} 

function insertReComment(idx,articleNo,writerId){
	alert(idx);
	var content = $('input[name=reComment]').val();
	var jsonText = '{"articleNo": "'+articleNo+'","parentNo":"'+idx+'","writeMemberId":"'+writerId+'","content":"'+ content+'"}';
	var sendData1 = JSON.stringify(jsonText);
	var sendData2 = JSON.parse(sendData1);
	
	$.ajax({
		type: "post",
		url: "${contextPath}/board/inserReComment.do",
		/* contentType: "application/json", */
		headers: {
			"Content-Type" : "application/json"
		},
		dataType: "json",
		data: sendData2,
		success: function(){
			alert("success");
			getCommentList();
		},
		error: function(){
			alert(sendData2);
		}
	})	
}
$(window).load(function(){
	getCommentList('${articleVo.articleNo}','${memberSession.memberId}');
})
/* $(document).ready(function(){
		getCommentList(data1,data2);
}) */

</script>
<!-- </body>
</html> -->