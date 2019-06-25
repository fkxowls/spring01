<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath"  value="${pageContext.request.contextPath}"  />
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>글목록</title>
 <style>
   .cls1 {text-decoration:none;}
   .cls2{text-align:center; font-size:30px;}
 	.s1 {float: right}
  </style>
<script  src="http://code.jquery.com/jquery-latest.min.js"></script> 
</head>
<body>
	<c:if test="${memberSession.memberId != null }">
	<span class="s1">${memberSession.memberId }님 Level:${memberSession.memberLevel } <a href="${contextPath }/member/logout">로그아웃</a></span>
	</c:if>
	<c:if test="${memberSession.memberId== null }">
	<span class="s1"><a href="${contextPath }/member/loginForm">로그인</a></span>
	</c:if>
	<br><br>

<h1>233</h1>
 </h1>
	<table align="center" id="articleList" border="1" width="80%">
		<tr height="10" align="center" bgcolor="lightgreen">
			<td>글번호호 </td>
			<td>작성자</td>
			<td>제목</td>
			<td>작성일</td>
		</tr>
		 <c:choose>
		<c:when test="${articleList == null } ">
			<tr height="10">
				<td colspan="4">
					등록된글이 없습니다
				</td>
			</tr>
		</c:when>		
		 <c:when test="${articleList !=null }" >
    <c:forEach  var="articleVO" items="${articleList }" varStatus="articleNum" >
     <tr align="center" data-article-no="${articleVO.articleNo}">//?????
	<td width="5%">${articleNum.count},</td>
	<td width="10%"><span>${articleVO.writeMemberId }</span></td>
	<td align='left'  width="35%">
	    <span style="padding-right:30px"></span>    
	   <c:choose>
	      <c:when test='${articleVO.lvl > 1 }'>  
	         <c:forEach begin="1" end="${articleVO.lvl }" step="1">
	             <span style="padding-left:10px"></span> 
	         </c:forEach>
	         <span style="font-size:12px;">[답변]</span>
                   <a class='cls1' href="${contextPath}/board/viewArticle.do?articleNo=${articleVO.articleNo}">${articleVO.title}</a>
	          </c:when>
	          <c:otherwise>
	            <a class='cls1' href="${contextPath}/board/viewArticle.do?articleNo=${articleVO.articleNo}">${articleVO.title}</a>
	          </c:otherwise>
	        </c:choose>
	       
	  </td>
	  <td  width="10%"><fmt:formatDate value="${articleVO.writeDate}" /></td> 
	</tr>
	
    </c:forEach>
     </c:when>
    </c:choose>
	
	</table>

<div class="cls2" id="btn"> 	      	
	          	<!-- <input type="button" onClick="getArticleList()" value="더보기"> -->
	          	<a href="/board/listArticle2.do?endNum=${endNum }&isNext=${isNext}">더보기(hasNext)</a>
	          	<%-- <a href="/board/listArticle.do?startNum=${startNum }">더보기(endPage)</a> --%>
	         <%--  <a id="moreContent" name="moreContent" href="javascript:getArticleList1(${startNum })">더보기</a> --%> 
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
	alert('수정');
	//getArticleList1(0);
}) 
</script>
</html>