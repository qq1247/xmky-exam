<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>在线考试系统首页</title>
		<%@include file="/script/home/common.jspf"%>
		<link href="css/exam.css" rel="stylesheet">
		<link href="css/home.css" rel="stylesheet">
	</head>
	<body>
		<%@include file="/script/home/head.jspf"%>
		<div class="container">
			<%-- 考试相关 --%>
			<div id="myExamQuery" class="row" style="padding-bottom: 25px;">
				<div class="col-md-3">
					<input id="myExamName" type="text" name="myExamName" class="form-control query" placeholder="请输入考试名称进行搜索" >
				</div>
				<div class="col-md-3">
					<button type="submit" class="btn btn-primary" style="height: 40px;width: 150px;max-width: 100%;" onclick="loadMyExamList(1, 20)">
						<i class="glyphicon glyphicon-search"></i> 搜索
					</button>
				</div>
			</div>
			<div id="myExamList" class="row">
			</div>
			<div class="row">
				<ul id="myExamPaginator"></ul>
			</div>
			<%-- 判卷相关 --%>
			<div id="myMarkQuery"  class="row" style="padding-bottom: 25px;">
				<div class="col-md-3">
					<input id="myMarkName" type="text" name="myMarkName" class="form-control query" placeholder="请输入考试名称进行搜索" >
				</div>
				<div class="col-md-3">
					<button type="submit" class="btn btn-primary" style="height: 40px;width: 150px;max-width: 100%;" onclick="loadMyMarkList(1, 20)">
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
		var $myExamQuery = $("#myExamQuery");
		var $myExamList = $("#myExamList");
		var $myMarkQuery  = $("#myMarkQuery ");
		var $myMarkList  = $("#myMarkList ");
	
		//页面加载完毕，执行如下方法：
		$(function() {
			loadMyExamList(1, 20);
			loadMyMarkList(1, 20);
		});
		
		//加载我的考试列表
		function loadMyExamList(page, rows){
			$.ajax({
				url : "home/myExam/list",
				data : {
					page : page,
					rows : rows,
					two : $("#myExamName").val()
				},
				success : function(obj){
					if (obj.rows == 0) {
						$myExamList.html("");
						$myExamQuery.hide();
						return;
					}
					
					$myExamQuery.show();
					
					var html = [];
					for (var i in obj.rows) {
						html.push('<div class="col-md-4">');
						html.push('	<div class="exam">');
						html.push('		<h3>' + obj.rows[i].NAME + '</h3>');
						html.push('		<p><span class="time">' + obj.rows[i].START_TIME_STR + '</span></p>');
						html.push('		<div>');
						html.push('			<button>');
						if (obj.rows[i].EXAM_HAND == "AWAIT") {
							html.push('				<i class="normal"><small></small></i>');
							html.push('				<span>未开始</span>');
						} else if (obj.rows[i].EXAM_HAND == "START") {
							html.push('				<i class="normal"><small></small></i>');
							html.push('				<span>进行中</span>');
						} else if (obj.rows[i].EXAM_HAND == "END") {
							html.push('				<i class="warn"><small></small></i>');
							html.push('				<span>已结束</span>');
						}
						html.push('			</button>');
						html.push('		</div>');
						html.push('		<div class="handle">');
						html.push('			<a onclick="alert(\'开发中\')">');
						html.push('				<i class="glyphicon glyphicon-user"></i>');
						html.push('				<span><small></small>'+obj.rows[i].TOTAL_SCORE+' / '+obj.rows[i].PAPER_TOTLE_SCORE+'</span>');
						html.push('			</a>');
						html.push('			<a onclick="alert(\'开发中\')">');
						html.push('				<i class="glyphicon glyphicon-list"></i>');
						html.push('				<span><small></small>往期成绩</span>');
						html.push('			</a>');
						html.push('			<a onclick="alert(\'开发中\')">');
						html.push('				<i class="glyphicon glyphicon-comment"></i>');
						html.push('				<span><small></small>考试详情</span>');
						html.push('			</a>');
						if (obj.rows[i].EXAM_HAND == "AWAIT") {
							html.push('			<a>');
							html.push('				<i class="glyphicon glyphicon-play"></i>');
							html.push('				<span><small></small>未开始</span>');
							html.push('			</a>');
						} else if (obj.rows[i].EXAM_HAND == "START") {
							html.push('			<a onclick="toMyPaper(' + obj.rows[i].ID + ')">');
							html.push('				<i class="glyphicon glyphicon-play"></i>');
							html.push('				<span><small></small>开始考试</span>');
							html.push('			</a>');
						} else if (obj.rows[i].EXAM_HAND == "END") {
							html.push('			<a>');
							html.push('				<i class="glyphicon glyphicon-play"></i>');
							html.push('				<span><small></small>已结束</span>');
							html.push('			</a>');
						}
						html.push('		</div>');
						html.push('	</div>');
						html.push('</div>');
					}
					
					$myExamList.html(html.join(""));
					
					var totalPages = parseInt((obj.total - 1) / rows) + 1;
					if (totalPages == 1) {
						return;
					}
					
					$("#myExamPaginator").bootstrapPaginator({
						bootstrapMajorVersion : 3,
						currentPage : page,
						totalPages : totalPages,
						onPageChanged : function(e, oldPage, newPage) {
							loadMyExamList(newPage, rows);
						}
					}); 
				}
			});
		}
		
		//加载我的判卷列表
		function loadMyMarkList(page, rows){
			$.ajax({
				url : "home/myMark/examList",
				data : {
					page : page,
					rows : rows,
					two : $("#myMarkName").val()
				},
				success : function(obj){
					if (obj.rows == 0) {
						$myMarkList.html("");
						$myMarkQuery.hide();
						return;
					}
					
					$myMarkQuery.show();
					
					var html = [];
					for (var i in obj.rows) {
						html.push('<div class="col-md-4">');
						html.push('	<div class="exam">');
						html.push('		<h3>' + obj.rows[i].NAME + '</h3>');
						html.push('		<p><span class="time">' + obj.rows[i].MARK_START_TIME_STR + '</span></p>');
						html.push('		<div>');
						html.push('			<button>');
						if (obj.rows[i].EXAM_HAND == "AWAIT") {
							html.push('				<i class="normal"><small></small></i>');
							html.push('				<span>未开始</span>');
						} else if (obj.rows[i].EXAM_HAND == "START") {
							html.push('				<i class="normal"><small></small></i>');
							html.push('				<span>进行中</span>');
						} else if (obj.rows[i].EXAM_HAND == "END") {
							html.push('				<i class="warn"><small></small></i>');
							html.push('				<span>已结束</span>');
						}
						html.push('			</button>');
						html.push('		</div>');
						html.push('		<div class="handle">');
						html.push('			<a>');
						html.push('				<i class="glyphicon glyphicon-user"></i>');
						html.push('				<span><small></small>考试人数</span>');
						html.push('			</a>');
						html.push('			<a onclick="alert(\'开发中\')">');
						html.push('				<i class="glyphicon glyphicon-comment"></i>');
						html.push('				<span><small></small>考试详情</span>');
						html.push('			</a>');
						if (obj.rows[i].EXAM_HAND == "AWAIT") {
							html.push('			<a>');
							html.push('				<i class="glyphicon glyphicon-play"></i>');
							html.push('				<span><small></small>未开始</span>');
							html.push('			</a>');
						} else if (obj.rows[i].EXAM_HAND == "START") {
							html.push('			<a onclick="toMarkList(' + obj.rows[i].ID +')" >');
							html.push('				<i class="glyphicon glyphicon-play"></i>');
							html.push('				<span><small></small>开始判卷</span>');
							html.push('			</a>');
						} else if (obj.rows[i].EXAM_HAND == "END") {
							html.push('			<a onclick="toMarkList(' + obj.rows[i].ID +')" >');
							html.push('				<i class="glyphicon glyphicon-play"></i>');
							html.push('				<span><small></small>已结束</span>');
							html.push('			</a>');
						}
						
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
							loadMyMarkList(newPage, rows);
						}
					}); 
				}
			});
		}
		
		//到达我的试卷页面
		function toMyPaper(examId) {
			window.location.href = "home/myExam/toPaper?examId=" + examId;
		}
		
		//到达判卷列表
		function toMarkList(examId) {
			window.location.href = "home/myMark/toList?examId=" + examId;
		}
	</script>
</html>