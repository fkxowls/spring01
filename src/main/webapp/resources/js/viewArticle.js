
function fn_delete(){
	if(confirm("삭제하겠습니까?")){
		$.ajax({
		type: "post",		
		url: "${contextPath}/board/doDeleteArticle.do?writeMemberId=${articleVo.writeMemberId}",
		success: function(result){
			if(result == "success"){
				alert("삭제완료");
			}
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

function getCommentList(){
	
	$.ajax({
		type: 'GET',
		url: '${contextPath}/board/commentList.do',
		dataType: 'json',
		data: $('#commentForm').serialize(),
		contentType: "application/x-www-form-urlencoded; charset=UTF-8", 
		success: function(data){
			var html = "";
			
			for(var i=0; i<data.length; i++){
				html +="<tr><td align=left width=110>";
				html +=data[i].writeMemberId+":&nbsp;</td>";
				html +="<td width=230>";
				/* html +="<c:if test='"+data[i].level+">0'>";
				html +="[답글]</c:if>""; */
				html +=data[i].content+"</td>";
				html +="<td>";
				html +="<a onClick='fn_reComment("+data[i].replyNo+")'>"+"답글"+"</a></td></tr>"
				html +="<tr class=recomment"+data[i].replyNo+"></tr>";
			}
			$("#commentList").html(html); 
		}
	})
}

function fn_insertComment(){
	var content = $('input[name=comment]').val();
	var jsonText = '{"articleNo": "${nowArticleNo}","writeMemberId":"${memberSession.memberId}","content":'+ content+'}';
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

function fn_reComment(idx){
	var tagName = ".recomment"+idx; 
	var html = "";
	html +="<td colspan=3><input type=text size=45 name=reComment></td>"; 
	html +="<td><input type=button value=등록 onClick=insertReComment("+idx+")></td>";
	$(tagName).append(html);
} 

function insertReComment(idx){
	alert(idx);
	var content = $('input[name=reComment]').val();
	var jsonText = '{"articleNo": "${nowArticleNo}","parentNo":'+idx+',"writeMemberId":"${memberSession.memberId}","content":'+ content+'}';
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

$(document).ready(function(){
		getCommentList();
})