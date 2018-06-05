<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
	<head>
		<title>试题列表</title>
		<%@include file="/script/home/common.jspf"%>
		<script src="script/bootstrapTreeview/bootstrap-treeview.min.js"></script>
	</head>
	<body>
		<%@include file="/script/home/head.jspf"%>
		<div class="headerplus">
			<div class="container">
				<div class="row">
					<div class="col-md-12">
						<form class="form-inline query" role="form">
							<div class="form-group">
								<label class="sr-only" for="name">名称</label>
								<input type="text" class="form-control box" placeholder="请输入名称">
							</div>
							<div class="form-group">&nbsp;&nbsp;</div>
							<div class="form-group">
								<button type="button" class="btn btn-primary">
									<span class="glyphicon glyphicon-search"></span>
									&nbsp;查询
								</button>
							</div>
							<div class="form-group pull-right">
								<button type="button" class="btn btn-primary">
									<span class="glyphicon glyphicon-plus"></span>
									&nbsp;添加
								</button>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
		<div class="examlist">
			<div class="container">
				<div class="row">
					<div class="col-md-3">
						<div id="questionTypeTree" class="tree"></div>
					</div>
					<div class="col-md-9">
						<div class="table-responsive">
							<table class="table">
								<thead>
									<tr>
										<th>名称</th>
										<th>开始时间</th>
										<th>操作</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td>转正考试</td>
										<td>2018-02-03 11:55:35</td>
										<td>
											<span class="glyphicon glyphicon-pencil"></span>
											&nbsp;修改
										</td>
									</tr>
									<tr>
										<td>保密考试</td>
										<td>2018-02-03 11:55:35</td>
										<td>
											<span class="glyphicon glyphicon-pencil"></span>
											&nbsp;修改
										</td>
									</tr>
									<tr>
										<td>cmmi认证</td>
										<td>2018-02-03 11:55:35</td>
										<td>
											<span class="glyphicon glyphicon-pencil"></span>
											&nbsp;修改
										</td>
									</tr>
								</tbody>
							</table>
						</div>
					</div>
					<form class="form-horizontal" role="form">
						<div class="row">
							<div class="col-md-6">
								<div class="form-group">
									<label for="firstname" class="col-md-2 control-label">名字</label>
									<div class="col-md-10">
										<input type="text" class="form-control" id="firstname"
											placeholder="请输入名字" style="width: 200px;">
									</div>
								</div>
							</div>
							<div class="col-md-6">
								<div class="form-group">
									<label for="lastname" class="col-md-2 control-label">姓</label>
									<div class="col-md-10">
										<input type="text" class="form-control" id="lastname"
											placeholder="请输入姓" style="width: 200px;">
									</div>
								</div>
							</div>
						</div>
					</form>
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
						color: "#605F5F",
						showTags: true, 
						levels: 3,
						data: generateTree(arr, {
							idFiled : "ID",
							textFiled : "NAME", 
							disabledFiled : "DISABLED",
							parentField : "PARENT_ID"
						}),
						onNodeSelected : function(event, data) {
							
						}
					});
				}
			});
		}
	</script>
</html>