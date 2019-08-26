<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>考试列表</title>
		<%@include file="/script/home/common.jspf"%>
		<link href="css/exam.css" rel="stylesheet">
	</head>
	<body>
		<%@include file="/script/home/head.jspf"%>
		<div class="container">
			<div class="row" style="padding-bottom: 25px;">
				<div class="col-md-3">
					<input id="two" type="text" name="two" value="${pageIn.two }" class="form-control query" placeholder="请输入考试名称进行搜索" >
				</div>
				<div class="col-md-3">
					<button type="submit" class="btn btn-primary" style="height: 40px;width: 150px;max-width: 100%;" onclick="query()">
						<i class="glyphicon glyphicon-search"></i> 搜索
					</button>
				</div>
			</div>
			<div class="row">
				<c:forEach var="row" items="${pageOut.rows }">
				<div class="col-md-4">
					<div class="exam">
						<h3>${row.NAME }</h3>
						<p><span class="time">${row.MARK_START_TIME_STR }</span></p>
						<div>
							<button>
								<i class="warn"><small></small></i>
								<span>进入</span>
							</button>
						</div>
						<div class="handle">
							<a>
								<i class="glyphicon glyphicon-user"></i>
								<span><small></small>50人</span>
							</a>
							<a onclick="toMarkList('${row.ID }')" >
								<i class="glyphicon glyphicon-play"></i>
								<span><small></small>进入</span>
							</a>
						</div>
					</div>
				</div>
				</c:forEach>
			</div>
			
			<div class="row" style="padding-left: 15px;">
				<c:if test="${pageNum > 1 }">
				<ul class="pagination">
					<li><a href="home/myMark/toExamList?page=1&rows=${pageIn.rows }&two=${pageIn.two }">&laquo;</a></li>
					<c:forEach var="curPage" begin="1" end="${pageNum }">
					<c:set var="clazz" value="${pageIn.page == curPage ? 'active' : '' }"></c:set>
					<li class="${clazz }"><a href="home/myMark/toExamList?page=${curPage }&rows=${pageIn.rows }&two=${pageIn.two }">${curPage }</a></li>
					</c:forEach>
					<li><a href="home/myMark/toExamList?page=${pageNum }&rows=${pageIn.rows }&two=${pageIn.two }">&raquo;</a></li>
				</ul>
				</c:if>
			</div>
		</div>
	</body>
	<script type="text/javascript">
		//查询
		function query(){
			window.location.href = "home/myMark/toExamList?two=" + encodeURI($("#two").val());
		}
		
		//达到判卷列表
		function toMarkList(examId){
			window.location.href = "home/myMark/toList?examId=" + examId;
		}
	</script>
</html>