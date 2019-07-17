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
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
</head>

<body>

<h1 style="text-align:center">답글쓰기</h1>
  <form name="articleForm" method="post" action="${contextPath }/board/replyArticle.do">
    <table border="0" align="center">
      <tr>
					<td align="right"> 작성자</td>
					<td colspan=2  align="left"><input type="text" size="20" maxlength="100" name="writeMemberId" value="${memberSession.memberId }" readOnly/> </td>
			</tr>

	     <tr>
			   <td align="right">글제목: </td>
			   <td colspan="2"><input type="text" size="67"  maxlength="500" id="title" name="title" value="[답글]${title}"/></td>
		 </tr>
	 		<tr>
				<td align="right" valign="top"><br>글내용: </td>
				<td colspan=2><textarea id="content" name="content" rows="10" cols="65" maxlength="4000"></textarea> </td>
     </tr>

	    <tr>
	      <td align="right"> </td>
	      <td colspan="2">
	       <input type="button" data-article-no="${articleNo }" class="btn1" value="답글쓰기" />
	       <input type=button value="목록보기"/>
	      </td>
     </tr>
    </table>
    </form>
<script  type="text/javascript">
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

$(document).ready(function(){
	$('.btn1').click(function(){
		var title = $('input[name=title]').val();
		var content = $('textarea[name=content]').val();
		var parentNo = $('.btn1').data('articleNo'); // TODO parentNo로 전부 이름변경
		var articleNo = $('.btn1').data('articleNo'); //isExistsArticle메소드로 현재글이있는지 판단하려면 articleNo도 필요함
		var writeMemberId = $('input[name=writeMemberId]').val();
		
		var data = {};
		data.title = title;
		data.content = content;
		data.articleNo = articleNo;
		data.parentNo = parentNo;
		data.writeMemberId = writeMemberId;
		
		$.ajax({
			type: 'POST',
			//url:  '${contextPath}/board/' + parentNo + '/reply',
			url:  '${contextPath}/board/replyArticle',
			contentType: 'application/json',
			dataType:  'json',
			data: JSON.stringify(data)
		}).done(function(data){
			alert(data.msg);
			$(location).attr('href', data.redirect);
		}).fail(function(data){
			alert(data.msg);
			$(location).attr('href', data.redirect);
		})
			
	})
	
})
</script>
</body>
</html>