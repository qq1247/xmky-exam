<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>我的考试</title>
		<%@include file="/script/home/common.jspf"%>
	</head>
	<body>
		<%@include file="/script/home/head.jspf"%>
		<div class="container">
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default exam-query">
						<div class="panel-body">
							<form id="queryForm" class="form-horizontal" role="form">
								<input type="hidden" id="one" name="one"/>
								<div class="row">
									<div class="col-md-4">
										<div class="form-group">
											<label for="two" class="control-label col-md-4">名称：</label>
											<div class="col-md-8">
												<input type="text" id="two" name="two" class="form-control" placeholder="请输入名称">
											</div>
										</div>
									</div>
									<div class="col-md-4">
										<div class="form-group">
											<label for="five" class="control-label col-md-4">考试开始大于：</label>
											<div class="col-md-8">
												<input type="text" id="five" name="five" class="form-control" placeholder="请输入开始时间">
											</div>
										</div>
									</div>
									<div class="col-md-4">
										<div class="form-group">
											<label for="six" class="control-label col-md-4">考试开始小于：</label>
											<div class="col-md-8">
												<input type="text" id="six" name="six" class="form-control" placeholder="请输入结束时间">
											</div>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-4">
										
									</div>
									<div class="col-md-4">
										
									</div>
									<div class="col-md-4" style="text-align: right;">
										<button type="button" class="btn btn-primary" onclick="query();">
											<span class="glyphicon glyphicon-search"></span>
											&nbsp;查询
										</button>
										<button type="button" class="btn btn-primary" onclick="reset();">
											<span class="glyphicon glyphicon-repeat"></span>
											&nbsp;重置
										</button>
									</div>
								</div>
							</form>
						</div>
					</div>
					<div class="panel panel-default exam-list">
						<div class="panel-body">
							<div id="toolbar">
								
							</div>
							<table id="table"></table>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
	<script type="text/javascript">
		//定义变量
		var $table = $("#table");
		var $queryForm = $("#queryForm");
		var $one = $("#one");
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initTime();
			initTable();
		});
		
		//初始化时间控件
		function initTime(){
			$('#five').datetimepicker({
				language : "zh-CN",
				format : "yyyy-mm-dd hh:ii:ss"
			});
			$('#six').datetimepicker({
				language : "zh-CN",
				format : "yyyy-mm-dd hh:ii:ss"
			});
		}
		
		//初始化列表
		function initTable(){
			$table.bootstrapTable({
				url : "home/myExam/list",
				queryParams : function(params){
					var customeParams = $.fn.my.serializeObj($queryForm);
					customeParams.page = this.pageNumber;
					customeParams.rows = this.pageSize;
					return customeParams;
				},
				columns : [ 
							{field : "state", checkbox : true}, 
							{field : "NAME", title : "名称", width : 160, align : "center"},
							{field : "START_TIME_STR", title : "考试时间", width : 160, align : "center",
								formatter : function(value, row, index){
									return row.START_TIME_STR + "<br/>" + row.END_TIME_STR;
								}
							},
							{field : "MARK_START_TIME_STR", title : "判卷时间", width : 160, align : "center",
								formatter : function(value, row, index){
									return row.MARK_START_TIME_STR + "<br/>" + row.MARK_END_TIME_STR;
								}
							},
							{field : "PASS_SCORE", title : "及格分数", width : 80, align : "center", 
								formatter : function(value, row, index){
									return row.PASS_SCORE + "/" + row.PAPER_TOTLE_SCORE;
								}
							},
							{field : "TOTAL_SCORE", title : "得分", width : 80, align : "center"},
							{field : "EXAM_USER_STATE_NAME", title : "状态", width : 80, align : "center"},
							{field : "OPTION", title : "操作", width : 80, align : "center", 
								formatter : function(value, row, index){
									if(row.START != 1){
										return "";
									}
									return '<button type="button" class="btn btn-primary" onclick="toPaper('+row.ID+');"><span class="glyphicon glyphicon-play"></span>&nbsp;开始</button>';
								}
							}
							],
				toolbar : "#toolbar"
			});
		}
		
		//查询
		function query(){
			$table.bootstrapTable('refresh', {pageNumber : 1});
		}
		
		//重置
		function reset(){
			$queryForm[0].reset();
			query();
		}
		
		//到达试卷页面
		function toPaper(examId){
			window.location.href = "home/myExam/toPaper?examId=" + examId;
		}
	</script>
</html>