<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>判卷列表</title>
		<%@include file="/script/home/common.jspf"%>
		<link href="css/exam.css" rel="stylesheet">
		<link href="css/mark.css" rel="stylesheet">
	</head>
	<body>
		<%@include file="/script/home/head.jspf"%>
		<div class="container">
			<div class="row" style="padding-bottom: 25px;">
				<div class="col-md-3">
					<input id="three" type="text" name="three" value="${pageIn.three }" class="form-control query" placeholder="请输入机构名称进行搜索" >
				</div>
				<div class="col-md-3">
					<button type="submit" class="btn btn-primary" style="height: 40px;width: 150px;max-width: 100%;" onclick="query()">
						<i class="glyphicon glyphicon-search"></i> 搜索
					</button>
				</div>
			</div>
			
			<div class="row">
				<c:forEach var="row" items="${pageOut.rows }">
				<div class="col-md-3">
					<div class="mark">
						<div class="title">
							<div class="title-left">
								<img src="img/defaultMan.jpg" class="img-circle" style="width: 80px;height: 80px;">
							</div>
							<div class="title-right">
								<h4>${row.ORG_NAME }</h4>
								<h4>${row.USER_NAME }</h4>
								<h4 style="margin-bottom: 0px;">${row.EXAM_USER_TOTAL_SCORE }/${row.PAPER_TOTLE_SCORE }</h4>
							</div>
						</div>
						<div class="handle">
							<a>
								<i class="glyphicon glyphicon-user"></i>
								<span><small></small>个人信息</span>
							</a>
							<a>
								<i class="glyphicon glyphicon-th-list"></i>
								<span><small></small>往期成绩</span>
							</a>
							<a>
								<i class="glyphicon glyphicon-search"></i>
								<span><small></small>预览试卷</span>
							</a>
							<a onclick="toMark('${row.ID }')" >
								<i class="glyphicon glyphicon-play"></i>
								<span><small></small>判卷</span>
							</a>
						</div>
					</div>
				</div>
				</c:forEach>
			</div>
		</div>
	</body>
	<script type="text/javascript">
		//查询
		function query(){
			window.location.href = "home/myMark/toList?three=" + encodeURI($("#three").val());
		}
		
		//达到判卷页面
		function toMark(examUserId){
			window.location.href = "home/myMark/toMark?examUserId=" + examUserId;
		}
	</script>
</html>