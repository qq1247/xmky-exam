<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>考试列表</title>
		<%@include file="/script/home/common.jspf"%>
	</head>
	<body>
		<%@include file="/script/home/head.jspf"%>
		<div class="container">
			<div class="row">
				<div class="col-md-3">
					<div id="examTypeTree" class="exam-tree"></div>
				</div>
				<div class="col-md-9">
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
											<label for="three" class="control-label col-md-4">试卷：</label>
											<div class="col-md-8">
												<input type="text" id="three" name="three" class="form-control" placeholder="请输入试卷">
											</div>
										</div>
									</div>
									<div class="col-md-4" style="text-align: right;">
										<div class="form-group">
											<label for="four" class="control-label col-md-4">状态：</label>
											<div class="col-md-8">
												<select id="four" name="four" class="form-control">
													<option value=""></option>
													<c:forEach var="dict" items="${STATE_DICT }">
													<option value="${dict.dictKey }">${dict.dictValue }</option>
													</c:forEach>
												</select>
											</div>
										</div>
									</div>
								</div>
								<div class="row">
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
								<button type="button" class="btn btn-primary" onclick="toMarkList();">
									<span class="glyphicon glyphicon-search"></span>
									&nbsp;考试详情
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
		var $examTypeTree = $("#examTypeTree");
		var $table = $("#table");
		var $queryForm = $("#queryForm");
		var $one = $("#one");
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initTime();
			initExamTypeTree();
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
		
		//初始化考试分类树
		function initExamTypeTree(){
			$.ajax({
				url : "home/exam/examTypeTreeList",
				success : function(arr) {
					$examTypeTree.treeview({
						showBorder: false,
						expandIcon: "glyphicon glyphicon-chevron-right",
						collapseIcon: "glyphicon glyphicon-chevron-down",
						nodeIcon: "glyphicon glyphicon-bookmark",
						//color: "#428BCA",
						showTags: true, 
						levels: 3,
						data: generateBootstrapTree(arr, {
							idFiled : "ID",
							textFiled : "NAME", 
							parentField : "PARENT_ID",
							disabledFiled : "DISABLED",
							expandedFiled : "EXPANDED"
						}),
						onNodeSelected : function(event, data) {
							$one.val(data.ID);
							query();
						},
						onNodeUnselected : function(event, data) {
							$one.val("");
							query();
						}
					});
				}
			});
		}
		
		//初始化列表
		function initTable(){
			$table.bootstrapTable({
				url : "home/mark/examList",
				queryParams : function(params){
					var customeParams = $.fn.my.serializeObj($queryForm);
					customeParams.page = this.pageNumber;
					customeParams.rows = this.pageSize;
					return customeParams;
				},
				columns : [ 
							{field : "state", checkbox : true},
							{field : "NAME", title : "名称", width : 160, align : "center"},
							{field : "PAPER_NAME", title : "试卷", width : 160, align : "center"},
							{field : "PASS_SCORE", title : "及格分数", width : 80, align : "center", 
								formatter : function(value, row, index){
									return row.PASS_SCORE + "/" + row.PAPER_TOTLE_SCORE;
								}
							},
							{field : "START_TIME_STR", title : "考试开始", width : 160, align : "center"},
							{field : "END_TIME_STR", title : "考试结束", width : 160, align : "center"}
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
		
		//达到判卷列表
		function toMarkList(){
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
			
			window.location.href = "home/mark/toList?one=" + nodes[0].ID;
		}
	</script>
</html>