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
			<div id="myMarkQuery" class="row" style="padding-bottom: 25px;">
				<div class="col-md-3">
					<input id="three" type="text" name="three" class="form-control query" placeholder="请输入机构名称或用户名称进行搜索" >
				</div>
				<div class="col-md-3">
					<button type="submit" class="btn btn-primary" style="height: 40px;width: 150px;max-width: 100%;" onclick="loadMyExamUserList(1, 20)">
						<i class="glyphicon glyphicon-search"></i> 搜索
					</button>
				</div>
			</div>
			<div id="myMarkList" class="row">
			</div>
			<div class="row">
				<ul id="myMarkPaginator"></ul>
			</div>
		</div>
	</body>
	<script type="text/javascript">
		//定义变量
		var $three = $("#three");
		var $myMarkQuery = $("#myMarkQuery");
		var $myMarkList = $("#myMarkList");
		
		//页面加载完毕，执行如下方法：
		$(function() {
			loadMyExamUserList(1, 20);
		});
		
		//加载我的考试用户列表
		function loadMyExamUserList(page, rows){
			$.ajax({
				url : "home/myMark/list",
				data : {
					page : page,
					rows : rows,
					three : $three.val()
				},
				success : function(obj) {
					/* if (obj.rows == 0) {
						$myMarkList.html("");
						$myMarkQuery.hide();
						return;
					}
					
					$myMarkQuery.show(); */
					
					var html = [];
					for (var i in obj.rows) {
						html.push('<div class="col-md-3">');
						html.push('	<div class="mark">');
						html.push('		<div class="title">');
						html.push('			<div class="title-left">');
						html.push('				<img src="img/defaultMan.jpg" class="img-circle" style="width: 80px;height: 80px;">');
						html.push('			</div>');
						html.push('			<div class="title-right">');
						html.push('				<h4>' + obj.rows[i].ORG_NAME + '</h4>');
						html.push('				<h4>' + obj.rows[i].USER_NAME + '</h4>');
						html.push('				<h4 style="margin-bottom: 0px;">' + obj.rows[i].EXAM_USER_TOTAL_SCORE + '/' + obj.rows[i].PAPER_TOTLE_SCORE + '</h4>');
						html.push('			</div>');
						html.push('		</div>');
						html.push('		<div class="handle">');
						html.push('			<a>');
						html.push('				<i class="glyphicon glyphicon-user"></i>');
						html.push('				<span><small></small>个人信息</span>');
						html.push('			</a>');
						html.push('			<a>');
						html.push('				<i class="glyphicon glyphicon-th-list"></i>');
						html.push('				<span><small></small>往期成绩</span>');
						html.push('			</a>');
						html.push('			<a>');
						html.push('				<i class="glyphicon glyphicon-search"></i>');
						html.push('				<span><small></small>预览试卷</span>');
						html.push('			</a>');
						html.push('			<a onclick="toMark(' + obj.rows[i].ID + ')" >');
						html.push('				<i class="glyphicon glyphicon-play"></i>');
						html.push('				<span><small></small>判卷</span>');
						html.push('			</a>');
						html.push('		</div>');
						html.push('	</div>');
						html.push('</div>');
					}
					
					$myMarkList.html(html.join(""));
					
					var totalPages = parseInt((obj.total - 1) / rows) + 1;
					if (totalPages == 1) {
						return;
					}
					
					$("#myMarkPaginator").bootstrapPaginator({
						bootstrapMajorVersion : 3,
						currentPage : page,
						totalPages : totalPages,
						onPageChanged : function(e, oldPage, newPage) {
							loadMyExamUserList(newPage, rows);
						}
					}); 
				}
			});
		}
		
		//达到判卷页面
		function toMark(examUserId){
			window.location.href = "home/myMark/toMark?examUserId=" + examUserId;
		}
	</script>
</html>