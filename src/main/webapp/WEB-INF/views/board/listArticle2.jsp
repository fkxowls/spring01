<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath"  value="${pageContext.request.contextPath}"  />
<!DOCTYPE html>
<html>
 <head>
<style type="text/css">
    body{
        line-height:2em;        
        font-family:"맑은 고딕";
}
    ul, li{ 
        list-style:none;
        text-align:center;
        padding:0;
        margin:0;
}

    #mainWrapper{
        width: 800px;
        margin: 0 auto; /*가운데 정렬*/
    }

    #mainWrapper > ul > li:first-child {
        text-align: center;
        font-size:14pt;
        height:40px;
        vertical-align:middle;
        line-height:30px;
}

    #ulTable {margin-top:10px;}
    

    #ulTable > li:first-child > ul > li {
        background-color:#c9c9c9;
        font-weight:bold;
        text-align:center;
}

    #ulTable > li > ul {
        clear:both;
        padding:0px auto;
        position:relative;
        min-width:40px;
}
    #ulTable > li > ul > li { 
        float:left;
        font-size:10pt;
        border-bottom:1px solid silver;
        vertical-align:baseline;
}    

    #ulTable > li > ul > li:first-child               {width:10%;} /*No 열 크기*/
    #ulTable > li > ul > li:first-child +li           {width:45%;} /*제목 열 크기*/
    #ulTable > li > ul > li:first-child +li+li        {width:20%;} /*작성일 열 크기*/
    #ulTable > li > ul > li:first-child +li+li+li     {width:15%;} /*작성자 열 크기*/
    #ulTable > li > ul > li:first-child +li+li+li+li{width:10%;} /*조회수 열 크기*/

    #divPaging {
          clear:both; 
        margin:0 auto; 
        width:220px; 
        height:50px;
}

    #divPaging > div {
        float:left;
        width: 30px;
        margin:0 auto;
        text-align:center;
}

    #liSearchOption {clear:both;}
    #liSearchOption > div {
        margin:0 auto; 
        margin-top: 30px; 
        width:auto; 
        height:100px; 

}

    .left {
        text-align : left;
}


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
<div id="mainWrapper">
<ul>
    <li>
        <ul id ="ulTable">
            <li>
                <ul>
                    <li>No</li>
                    <li>제목</li>
                    <li>작성자</li>
                    <li>작성일</li>
                </ul>
             </li>
             <c:choose>
             <c:when test="${articleList == null } ">
             <li>
                 <ul>
                   	<li>등록된 글이 없습니다</li>
                 </ul>
             </li>
             </c:when>
                    
             <c:when test="${articleList !=null }" >
             <li id="addList" data-total-page="${totalPage }">
             	<%@ include file="/WEB-INF/views/board/ajaxArticle.jsp" %>
             </li>
             </c:when>
    		 </c:choose>
		 </ul>
 	</li>
 	
 	<li>
 		<div id="divPaging">
 			<c:if test="${0 != totalPage and totalPage != page }">
 				<button type="button" id="moreContent"  data-next-page="1" onClick="getMoreContents2(this);">More ~ EndPage</button>
 			</c:if>
 			<c:if test="${hasNext}">
 				<button type="button" id="moreContent"  data-next-page="1" onClick="getMoreContents(this);">More ~ HasNext</button>
 			</c:if>
 			<a class="cls1" href="${writeArticleForm }"><p class=cls2>글쓰기</p></a>
 		</div>
 	</li>
</ul>
<script  src="/resources/js/simpleStore.js"></script>
<script type="text/javascript">

function getMoreContents(btnEl){
	var nextPage = $(btnEl).data('nextPage');
	var hasNext = $(".contents").last().data('hasNext');
	if(!hasNext) { return; }
	
	$.ajax({
		url: '/board/ajaxArticle2.do?page=' + nextPage,
		method:'GET',
		dataType: 'html'
	}).done(function(data){ 
		var html = $($.parseHTML(data)).filter('.contents');
		$('#addList').append(html);
		$(btnEl).data("nextPage", nextPage + 1).attr("data-list-page", nextPage + 1);
		hasNext = html.last().data('hasNext');
		
		if(!hasNext) { $(btnEl).hide(); }
		
		SimpleStore.commit('addList', $('#addList').html());
		SimpleStore.commit('divPaging', $('#divPaging').html());
	});
}

function getMoreContents2(btnEl){
	var nextPage = $(btnEl).data('nextPage');
	var totalPage = $('#addList').data('totalPage');
	if(nextPage > totalPage) { return; }
	
	$.ajax({
		url: '/board/ajaxArticle.do?page=' + nextPage,
		method:'GET',
		dataType: 'html'
	}).done(function(data){ 
		var html = $($.parseHTML(data)).filter('.contents');
		$('#addList').append(html);
		$(btnEl).data("nextPage", nextPage + 1).attr("data-list-page", nextPage + 1);
		
		if(nextPage === totalPage) { $(btnEl).hide(); }
		
		SimpleStore.commit('addList', $('#addList').html());
		SimpleStore.commit('divPaging', $('#divPaging').html());
	});
}

function viewArticle(articleId) {
	$.ajax({
		type: 'get', 
		url : '/board/'+articleId
	})
	
	}


/*
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
*/

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

</script>
</body>
<!-- </html> -->