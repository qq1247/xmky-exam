<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
	<head>
		<title>试题列表</title>
		<%@include file="/script/home/common.jspf"%>
	</head>
	<body>
		<%@include file="/script/home/head.jspf"%>
		<div class="examlist">
			<div class="container">
				<div class="row">
					<div class="col-md-3">
						<div id="questionTypeTree" class="tree"></div>
					</div>
					<div class="col-md-9">
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
								<button type="button" class="btn btn-primary" onclick="toQuestionAdd()">
									<span class="glyphicon glyphicon-plus"></span>
									&nbsp;添加
								</button>
							</div>
						</form>
						<div style="height: 30px;"></div>
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
		
		function toQuestionAdd(){
			var nodes = questionTypeTree.treeview("getSelected");
			if(nodes.length == 0){
				bootbox.alert({ 
					size: "small",
					title: "提示消息",
					message: "请选择左侧试题分类！"
				})
				return;
			}
			
			window.location.href = "home/question/toAdd?questionTypeId=" + nodes[0].ID;
		}
	</script>
</html>