<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시글 작성</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css">
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/js/bootstrap.min.js"></script>

<link rel="stylesheet" href="/resources/css/board.css" />
<script src="/resources/js/board.js"></script>
</head>
<body>
<div id="wrap">
   <div id="tb-wrap">
      <!-- 컴포넌트 시작 -->
      <table class="table table-striped" id="write">
         <tr>
            <th>작성자</th>
            <td><span id="userName" data-user-id="${article.userId }">${article.userName }</span></td>
         </tr>
         <tr>
            <th>제목</th>
            <td><input type="text" id="title" data-parent-id="${article.articleId}" value="${article.title }" /></td>
         </tr>
         <tr>
            <th>내용</th>
            <td><textarea rows="8" cols="50" id="contents"></textarea></td>
         </tr>
         <tr>
            <td><button type="button">등록</button></td>
            <td>--</td>
         </tr>
      <!-- 컴포넌트 종료 -->
      </table>
   </div>
</div>

<script>
var com = com || {};
com.kkk26kkk = com.kkk26kkk || {};
com.kkk26kkk.bbs = com.kkk26kkk.bbs || {};

com.kkk26kkk.bbs.write = (function(selector) {
    var ajax = { state: function() {} };
    
   $(document).ready(function() {
      // DOM 생성 완료 후 실행
   });
    
    $(window).load(function() {
       // 이미지 등 리소스 까지 완전히 화면 로드 완료 후 실행
    });
   
   $(selector).on("click", "button", function(e) {
        if('pending' === ajax.state()) {
           return;
        }
        
      var data = {};
      data.userId = $('#userName').attr("data-user-id");
      data.title = $('#title').val();
      data.parentId = $('#title').attr("data-parent-id");
      data.contents = $('#contents').val();
      
      writeArticle(data).done(movePage);
   });
   
   var writeArticle = function(data) {
      return ajax = $.ajax({
         url : "${path}",
         method : 'POST',
         contentType : 'application/json;charset=utf-8',
         dataType : 'json',
         data : JSON.stringify(data)
      });
   }
   
   var movePage = function(result){
      $(location).attr('href', result.redirect);
   }
   
/*    return {
      writeArticle: writeArticle
   } */
}("#write"));
</script>
</body>
</html>