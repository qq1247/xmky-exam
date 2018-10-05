<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
	<head>
		<title>试题列表</title>
		<%@include file="/script/home/common.jspf"%>
	</head>
	<body>
		<%@include file="/script/home/head.jspf"%>
		<div class="container">
			<div class="row">
				<div class="col-md-3">
					<div id="questionTypeTree" class="exam-tree"></div>
				</div>
				<div class="col-md-9">
					<div class="panel panel-default exam-query">
						<div class="panel-heading">查询条件</div>
						<div class="panel-body">
							<form class="form-horizontal" role="form">
								<div class="row">
									<div class="col-md-4">
										<div class="form-group">
											<label for="one" class="col-md-5 control-label">名称：</label>
											<div class="col-md-7">
												<input type="text" name="one" class="form-control" placeholder="请输入名称">
											</div>
										</div>
									</div>
									<div class="col-md-4">
										
									</div>
									<div class="col-md-4">
										<div class="form-group">
											<div class="col-md-5">
											</div>
											<div class="col-md-7">
												<button type="button" class="btn btn-primary">
													<span class="glyphicon glyphicon-search"></span>
													&nbsp;查询
												</button>
											</div>
										</div>
									</div>
								</div>
							</form>
						</div>
					</div>
					
					<div style="height: 30px;"></div>
					<div class="table-responsive">
						<table id="questionTable"></table>
					</div>
				</div>
			</div>
		</div>
	</body>
	<script type="text/javascript">
		//定义变量
		var questionTypeTree = $("#questionTypeTree");
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initQuestionTypeTree();
			initQuestionGrid();
		});
		
		//初始化试题分类树
		function initQuestionTypeTree(){
			$.ajax({
				url : "home/question/questionTypeTreeList",
				success : function(arr) {
					questionTypeTree.treeview({
						showBorder: false,
						expandIcon: "glyphicon glyphicon-chevron-right",
						collapseIcon: "glyphicon glyphicon-chevron-down",
						nodeIcon: "glyphicon glyphicon-bookmark",
						color: "#428BCA",
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
							
						}
					});
				}
			});
		}
		
		//初始化列表
		function initQuestionGrid(){
			$("#questionTable").bootstrapTable({
				url : "home/question/list",
				columns : [
							{field : "ID", title : "", checkbox : true}, 
							{field : "CODE", title : "编号", width : 50, align : "center"},
							{field : "TITLE", title : "题干 ", width : 300, align : "center"},
							{field : "TYPE_NAME", title : "类型", width : 80, align : "center"},
							{field : "DIFFICULTY_NAME", title : "难度", width : 80, align : "center"},
							{field : "STATE_NAME", title : "状态 ", width : 80, align : "center"},
							{field : "QUESTION_TYPE_NAME", title : "分类 ", width : 80, align : "center"}
							]
			});
		}
		
		//到达添加试题页面
		function toQuestionAdd(){
			var questionTypeId = "";
			var nodes = questionTypeTree.treeview("getSelected");
			if(nodes.length == 1){
				questionTypeId = nodes[0].ID;
			}
			
			window.location.href = "home/question/toAdd?questionTypeId=" + questionTypeId;
		}
	</script>
</html>