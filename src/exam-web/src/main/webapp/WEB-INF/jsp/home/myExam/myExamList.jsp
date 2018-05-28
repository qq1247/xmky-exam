<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
	<head>
		<title>我的考试</title>
		<%@include file="/script/home/common.jspf"%>
	</head>
	<body>
		<%@include file="/script/home/head.jspf"%>
		<section class="service-item" style="padding-top: 200px;">
			<div class="container">
				<div class="row">
					<div class="col-xs-12">
						<table class="table table-bordered">
							<caption>我的考试</caption>
							<thead>
								<tr>
									<th>考试名称</th>
									<th>试卷名称</th>
									<th>开始时间</th>
									<th>结束时间</th>
									<th>考试得分/及格分数/总分数</th>
									<th>考试状态</th>
									<th>操作</th>
								</tr>
							</thead>
							<tbody id="myExamGrid">
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</section>
		<section class="service-item" style="padding-top: 200px;">
			<div class="container">
				<div class="row">
					<div class="col-xs-12">
						<ul id="pagingBar"></ul>
					</div>
				</div>
			</div>
		</section>
		<%@include file="/script/home/footer.jspf"%>
	</body>
	<script type="text/javascript">
		//定义变量
		var pagingBar = $("#pagingBar");//分页工具条
	
		//页面加载完毕，执行如下方法：
		$(function() {
			pagingBar.bootstrapPaginator({
				bootstrapMajorVersion : 3,
				curPage: 1,
				totalPages: 10,
				numberofPages : 10,
				itemTexts : function(type, page, cur) {
					switch (type) {
					case "first":
						return "首页";
					case "prev":
						return "上页";
					case "next":
						return "下页";
					case "last":
						return "末页";
					case "page":
						return page;
					}
				},
				tooltipTitles : function(type, page, cur) {
					switch (type) {
					case "first":
						return "首页";
					case "prev":
						return "上页";
					case "next":
						return "下页";
					case "last":
						return "末页";
					case "page":
						return (page === cur) ? "当前第" + page + "页" : "跳转到第" + page + "页";
					}
				},
				onPageClicked : function(e, originalEvent, type, page){
					updateMyExamGrid(page);
	            }
			});
			
			updateMyExamGrid(1);
		});
		
		//更新表格
		function updateMyExamGrid(page){
			$.ajax({
				url : "home/myExam/list",
				data : {page : page},
				success : function(obj) {
					$('#pagingBar').bootstrapPaginator({
						totalPages: obj.total
					});
					
					var html = [];
					for(var i in obj.rows){
						if(i % 2 == 0){
							html.push("<tr class='success'>");
						}else{
							html.push("<tr >");
						}
						
						html.push("	<td>"+obj.rows[i].EXAM_NAME+"</td>");
						html.push("	<td>"+obj.rows[i].PAPER_NAME+"</td>");
						html.push("	<td>"+obj.rows[i].EXAM_START_TIME_STR+"</td>");
						html.push("	<td>"+obj.rows[i].EXAM_END_TIME_STR+"</td>");
						html.push("	<td>"+obj.rows[i].EXAM_USER_TOTAL_SCORE+"/"+obj.rows[i].EXAM_PASS_SCORE+"/"+obj.rows[i].PAPER_TOTLE_SCORE+"</td>");
						html.push("	<td>"+obj.rows[i].EXAM_USER_STATE_NAME+"</td>");
						if(obj.rows[i].START){
							html.push("	<td><a href='home/myExam/toPaper?examUserId="+obj.rows[i].ID+"'>开始考试</a></td>");
						}else{
							html.push("	<td><a href='home/myExam/toPaperView?examUserId="+obj.rows[i].ID+"'>预览试卷</a></td>");
						}
						html.push("</tr>");
					}
					
					$("#myExamGrid").empty();
	         		$("#myExamGrid").append(html.join(""));
				}
			});
		}
	</script>
</html>