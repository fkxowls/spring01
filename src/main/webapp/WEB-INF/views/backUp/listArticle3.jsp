<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath"  value="${pageContext.request.contextPath}"  />
<!DOCTYPE html>
<html>
<!-- <head>
<meta charset="UTF-8">
<title>글목록</title>
 <style>
   .cls1 {text-decoration:none;}
   .cls2{text-align:center; font-size:30px;}
 	.s1 {float: right}
  </style>
</head> -->
<script  src="http://code.jquery.com/jquery-latest.min.js"></script> 
<body>
	<c:if test="${memberSession.memberId != null }">
	<span class="s1">${memberSession.memberId }님 Level:${memberSession.memberLevel } <a href="${contextPath }/member/logout">로그아웃</a></span>
	</c:if>
	<c:if test="${memberSession.memberId== null }">
	<span class="s1"><a href="${contextPath }/member/loginForm">로그인</a></span>
	</c:if>
	<br><br>
	>
	
	<table align="center" id="articleList" border="1" width="80%">
		<tr height="10" align="center" bgcolor="lightgreen">
			<td>글번호</td>
			<td>작성자</td>
			<td>제목</td>
			<td>작성일</td>
		</tr>
	</table>
		<c:choose>
		<c:when test="${articleList == null } ">
		<table align="center"  border="1" width="80%">
			<tr height="10">
				<td colspan="4">
					등록된글이 없습니다
				</td>
			</tr>
		</table>
		</c:when>		
	<c:when test="${articleList !=null }" >
	<table align="center" id="contents" border="1" width="80%">
    <c:forEach  var="articleVO" items="${articleList }">
     <tr align="center">
     <td width="10%"> ${articleVO.articleNo } </td>
	<td width="10%"><span>${articleVO.writeMemberId }</span></td>
	<td align='left'  width="35%">
	<a class='cls1' href="${contextPath}/board/viewArticle.do?articleNo=${articleVO.articleNo}">${articleVO.title}</a>
	</td>
	<td  width="10%"><fmt:formatDate value="${articleVO.writeDate}" /></td> 
	</tr>
    </c:forEach>
    </table>
     </c:when>
    </c:choose>
    <table align="center" id="addList" border="1" width="80%">
    </table>
	

	          	<!-- <input type="button" onClick="getArticleList()" value="더보기"> -->
	        <!-- <a id="moreContent" id="moreContent"  data-list-page="2" href="javascript:getMore`()">더보기</a> -->
<div class="cls2" id="btn"> 	      	
	        <a id="moreContent"  data-list-page="2" href="javascript:getMoreContents2()">endNum</a>
	        <button type="button" id="moreContent"  data-list-page="2" onClick="getMoreContents(this);">hasNext</button>
</div>
	<a class="cls1" href="${contextPath}/board/doWriteForm.do"><p class=cls2>글쓰기</p></a>


</body>
<script type="text/javascript">
/*
 
function getArticleList(){
	
	
	 이렇게 보내면 왜 안되는가 증가된 startNum이 안넘어오는가
	var num = ${startNum };
	alert(num) ;
	$.ajax({
		type: "post",
		url: "/board/listArticle.do?startNum="+num,
		success:function(){
			
		}
	})
	
}
*/

function getMoreContents(btnEl){
	var page = $(btnEl).data('listPage');
	alert(page);
	var param ='';
	
	if(location.search){
		param = location.search+'&page='+page;
	}else{
		param = '?page='+page;
	}
	alert(param);
	
	$.ajax({
		url: '/board/listArticle2.do'+param,
		method: 'GET',
		dataType: 'html'
	}).done(function(data){
		var html = $($.parseHTML(data)).filter('#contents');
		alert(html);
		$('#addList').append(data);
		$(btnEl).data("listPage",page+1).attr("data-list-page",page+1);
		
		
		
	})
	
}

function getMoreContents2(){
	
	var page = $('#moreContent').data('listPage');
	var param ='';
	if(location.search){
		param = location.search+'&page='+page;
	}else{
		param = '?page='+page;
	}
	
	$('#btn').children().remove();//더보기 버튼 지우고 새로 만들면서 page값 입력
	
	$.ajax({
		url: '/board/listArticle.do?page=2',
		method:'GET',
		dataType: 'html'
	}).done(function(data){    
		var html =$($.parseHTML(data)).filter('.contents');
		$('.contents').append(html);
		alert("성공");
	});
	
	
}



function getArticleList2(num){
	var endNum = num+10;
	
	$.ajax({
		type: 'GET',
		url: '${contextPath}/board/listArticle.do',
		dataType: 'json',
		
		contentType: "application/json; charset=UTF-8",
		success: function(data){
			var list ="";
			var btn = "";
			
			
			if(data.length == null){
				alert('없습니다');
				list +="<td colspan='4'>등록된글이 없습니다</td>";
			}else{				
				for(num; num<endNum; num++){
					
				list +="<tr align='center'>";
				list +="<td width='5%'><span>"+data[num].articleNo+"</span></td>";
				list +="<td width='10%'><span>"+data[num].writeMemberId+"</span></td>";
				list +="<td align='left'  width='35%'>";
				list +=" <a class='cls1' href='${contextPath}/board/viewArticle.do?articleNo="+data[num].articleNo+"'>"+data[num].title+"</a></td></tr>";
				
				}
			}
			num = endNum;
			$("#articleList").append(list); 
			$('a[name=moreContent]').remove();
			btn +="<a id='moreContent' name='moreContent' href='javascript:getArticleList1("+num+")'>더보기</a> ";
			$("#btn").append(btn);
			
		}
	})
    .done(function(data) {
        var html = $($.parseHTML(data)).filter("li");
        moreitemBtnEle.hide();
        renderMoreitem(ulItemEle, html);
        updateMoreitem(ulItemEle);
    }.bind(this))
    .fail(function() {
        moreitemBtnEle.hide();
    })
    .always(function() {
        ssgPreOrder.mpoderComingsoonList.masonry('reloadItems').masonry(); // global
    });
}


/*
function fn_moreConent(){
	$.ajax({
		type: 'GET',
		url: "${context}/board/moreContent.do?page=${page}",
		dataType: 'json',		
		contentType: "application/json; charset=UTF-8",
		success:function(data){
			
		}
		
	})
}
*/

$(document).ready(function(){
	//getArticleList1(0);
}) 
</script>
<!-- </html> -->