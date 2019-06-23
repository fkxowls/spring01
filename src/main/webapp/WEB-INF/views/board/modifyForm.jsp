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
<style>
     #tr_file_upload{
       display:none;
     }
     #tr_btn_modify{
       display:none;
     }
   
   </style>
<script  src="http://code.jquery.com/jquery-latest.min.js"></script>
</head>
<body>
<h1> 세션아님1: ${articleVo.articleNo}</h1>
 <form name="frmArticle" method="post"  action="${contextPath}/board/modifyArticle.do" >
  <table  border=0  align="center">
   <%-- 
  <tr>
   <td width=150 align="center" bgcolor=#FF9933>
      글번호
   </td>
   <td colspan="2">
    <input type="text"  value="${articleVo.articleNo }"  disabled />
    <input type="hidden" name="articleNO" id="i_articleNO" value="${articleVo.articleNo}"  />
   </td> 
  </tr>
   --%>
  <tr>
    <td width="150" align="center" bgcolor="#FF9933">
      아이디
   </td>
   <td  colspan="2">
    <input type=text value="${articleVo.writeMemberId }" name="writeMemberId"  disabled />
   </td>
  </tr>
  <tr>
    <td width="150" align="center" bgcolor="#FF9933">
      제목 
   </td>
   <td  colspan="2">																	
    <input type=text value="${articleVo.title }"  name="title" />
   </td>   
  </tr>
  <tr>
	   <td width="150" align="center" bgcolor="#FF9933">
	      등록일자
	   </td>
	   <td  colspan="2">
	    <input type=text value="<fmt:formatDate value="${articleVo.writeDate}" />" disabled />
	   </td>   
  </tr>
  <tr>
   <td colspan="3">
   <input type="text" name="content2"/>
   <textarea id="content" name="content" rows="20" cols="60" maxlength="4000"></textarea> 
   </td>  
  </tr>
    
  <tr  id="tr_btn"    >
   <td colspan="2" align="center">
	      <input type="button" id="btn1"  name="btn1" onClick="fn_modify('${articleVo.articleNo}')" value="수정완료"/>
   </td>
  </tr>
 </table>
 </form>
 <script type="text/javascript">
$.arrayToJsonObject = function(array) {
    var object = {};
    $.each(array, function() {
        if (object[this.name]) {
            if (!object[this.name].push) {
                object[this.name] = [ object[this.name] ];
            }
            object[this.name].push(this.value || '');
        } else {
            object[this.name] = this.value || '';
        }
    });
    return object;
};



	function fn_modify(num){
		/* var jsonStr = JSON.stringify(($.arrayToJsonObject($('form').serializeArray()))); */
	
		var title = $('input[name=title]').val();
		var content = $('textarea[name=content]').val();
		var articleNo = num;
		var jsonText = '{"articleNo":'+articleNo+',"title":"'+title+'","content":"'+content+'"}';
				
		 var sendData1 = JSON.stringify(jsonText); 
		 var sendData2 = JSON.parse(sendData1);
		$.ajax({
			type: 'post',
			url:  '${contextPath}/board/modifyArticle.do',
			headers: {
				"Content-Type" : "application/json"
			},
			dataType:  'json',
			data: sendData2,
			success: function(){
				alert('성공')
				 window.location = "/board/viewArticle.do?articleNo="+articleNo;
			},
			error:function(request,status,error){
		        alert("code = "+ request.status + " message = " + request.responseText + " error = " + error); // 실패 시 처리
		     }
		});	
	}
	
$(document).ready(function(){

})
</script>
</body>
</html>