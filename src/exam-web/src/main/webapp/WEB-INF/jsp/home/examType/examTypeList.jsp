<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>考试分类列表</title>
		<%@include file="/script/home/common.jspf"%>
	</head>
	<body>
		<%@include file="/script/home/head.jspf"%>
		<div class="container">
			<div class="row">
				<div class="col-md-3">
					<div id="tree" class="exam-tree"></div>
				</div>
				<div class="col-md-9">
					<div class="panel panel-default exam-query">
						<div class="panel-body">
							<form id="queryForm" class="form-horizontal" role="form">
								<input type="hidden" id="one" name="one"/>
								<div class="row">
									<div class="col-md-4">
										<div class="form-group">
											<div class="form-group">
											<label for="two" class="control-label col-md-4">名称：</label>
											<div class="col-md-8">
												<input type="text" id="two" name="two" class="form-control" placeholder="请输入名称">
											</div>
										</div>
										</div>
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
								<button type="button" class="btn btn-primary" onclick="toAdd();">
									<span class="glyphicon glyphicon-plus"></span>
									&nbsp;添加
								</button>
								<button type="button" class="btn btn-primary" onclick="toEdit();">
									<span class="glyphicon glyphicon-pencil"></span>
									&nbsp;修改
								</button>
								<button type="button" class="btn btn-primary" onclick="doDel();">
									<span class="glyphicon glyphicon-trash"></span>
									&nbsp;删除
								</button>
								<button type="button" class="btn btn-primary" onclick="toAuth();">
									<span class="glyphicon glyphicon-pencil"></span>
									&nbsp;授权
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
		var $tree = $("#tree");
		var $table = $("#table");
		var $queryForm = $("#queryForm");
		var $one = $("#one");
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initTree();
			initTable();
		});
		
		//初始化考试分类树
		function initTree(){
			$.ajax({
				url : "home/examType/treeList",
				success : function(arr) {
					$tree.treeview({
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
				url : "home/examType/list",
				queryParams : function(params){
					var customeParams = $.fn.my.serializeObj($queryForm);
					customeParams.page = this.pageNumber;
					customeParams.rows = this.pageSize;
					return customeParams;
				},
				columns : [ 
							{field : "state", checkbox : true},
							{field : "NAME", title : "名称", width : 80, align : "center"},
							{field : "PARENT_NAME", title : "上级考试分类 ", width : 80, align : "center"},
							{field : "NO", title : "排序 ", width : 80, align : "center"},
							{field : "USER_NAMES", title : "用户权限 ", width : 80, align : "center"},
							{field : "ORG_NAMES", title : "机构权限 ", width : 80, align : "center"},
							{field : "POST_NAMES", title : "岗位权限  ", width : 80, align : "center"}
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
		
		//到达添加考试分类页面
		function toAdd(){
			var treeNodes = $tree.treeview("getSelected");
			if(treeNodes.length != 1){
				BootstrapDialog.show({
					title : "提示消息",
					message : "请选择考试分类",
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
			
			window.location.href = "home/examType/toAdd?parentId=" + treeNodes[0].ID;
		}
		
		//到达修改考试分类页面
		function toEdit(){
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
			
			window.location.href = "home/examType/toEdit?id=" + nodes[0].ID;
		}
		
		//完成考试分类删除
		function doDel(){
			var nodes = $table.bootstrapTable("getSelections");
			if(nodes.length == 0){
				BootstrapDialog.show({
					title : "提示消息",
					message : "请选择一行或多行数据！",
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
			
			BootstrapDialog.show({
				title : "提示消息",
				message : "确定删除？",
				buttons : [{
					label : "&nbsp;确定",
					icon : "glyphicon glyphicon-ok",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						$.ajax({
							url : "home/examType/doDel",
							data : $.fn.my.serializeField(nodes),
							success : function(obj) {
								if (!obj.succ) {
									BootstrapDialog.show({
										title : "提示消息",
										message : obj.msg,
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
								
								window.location.href = "home/examType/toList";
							}
						});
					}
				}, {
					label : "&nbsp;取消",
					icon : "glyphicon glyphicon-remove",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						dialogItself.close();
					}
				}]
			});
			return;
		}
		
		//到达授权页面
		function toAuth(){
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
			
			window.location.href = "home/examType/toAuth?id=" + nodes[0].ID;
		}
	</script>
</html>