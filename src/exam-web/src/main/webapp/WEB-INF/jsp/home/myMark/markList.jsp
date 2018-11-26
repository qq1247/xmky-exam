<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>判卷列表</title>
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
								<input type="hidden" name="one" value="${examId }">
								<div class="row">
									<div class="col-md-4">
										<div class="form-group">
											<label for="two" class="control-label col-md-4">用户：</label>
											<div class="col-md-8">
												<input type="text" id="two" name="two" class="form-control" placeholder="请输入名称">
											</div>
										</div>
									</div>
									<div class="col-md-4">
										<div class="form-group">
											<label for="three" class="control-label col-md-4">组织机构：</label>
											<div class="col-md-8">
												<input type="text" id="three" name="three" class="form-control" placeholder="请输入试卷">
											</div>
										</div>
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
								<button type="button" class="btn btn-primary" onclick="toMark();">
									<span class="glyphicon glyphicon-pencil"></span>
									&nbsp;判卷
								</button>
								<button type="button" class="btn btn-primary" onclick="javascript:history.back(-1);">
									<span class="glyphicon glyphicon-arrow-left"></span>
									&nbsp;返回
								</button>
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
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initTable();
		});
		
		//初始化列表
		function initTable(){
			$table.bootstrapTable({
				url : "home/myMark/list",
				queryParams : function(params){
					var customeParams = $.fn.my.serializeObj($queryForm);
					customeParams.page = this.pageNumber;
					customeParams.rows = this.pageSize;
					return customeParams;
				},
				columns : [ 
							{field : "state", checkbox : true},
							{field : "USER_NAME", title : "用户", width : 80, align : "center"},
							{field : "ORG_NAME", title : "组织机构", width : 80, align : "center"},
							{field : "MARK_USER_NAME", title : "判卷用户", width : 80, align : "center"},
							{field : "EXAM_USER_UPDATE_MARK_TIME_STR", title : "最后判卷时间", width : 80, align : "center"},
							{field : "EXAM_USER_TOTAL_SCORE", title : "得分", width : 80, align : "center"}
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
		
		//达到判卷页面
		function toMark(){
			var nodes = $table.bootstrapTable("getSelections");
			if(nodes.length != 1){
				BootstrapDialog.show({
					title : "提示消息",
					message : "请选择一行数据！",
					buttons : [{
						label : "&nbsp;确定",
						icon : "glyphicon glyphicon-ok",
						cssClass : "btn-primary",
						action : function(dialogItself) {
							dialogItself.close();
						}
					}]
				});
				return;
			}
			
			window.location.href = "home/myMark/toMark?examUserId=" + nodes[0].ID;
		}
	</script>
</html>