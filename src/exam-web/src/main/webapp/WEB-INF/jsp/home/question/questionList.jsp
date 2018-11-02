<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
						<div class="panel-body">
							<form id="queryForm" class="form-horizontal" role="form">
								<input type="hidden" id="one" name="one"/>
								<div class="row">
									<div class="col-md-4">
										<div class="form-group">
											<label for="two" class="control-label col-md-4">编号：</label>
											<div class="col-md-8">
												<input type="text" id="two" name="two" class="form-control" placeholder="请输入编号">
											</div>
										</div>
									</div>
									<div class="col-md-4">
										<div class="form-group">
											<label for="three" class="control-label col-md-4">题干：</label>
											<div class="col-md-8">
												<input type="text" id="three" name="three" class="form-control" placeholder="请输入题干">
											</div>
										</div>
									</div>
									<div class="col-md-4">
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
											<label for="five" class="control-label col-md-4">类型：</label>
											<div class="col-md-8">
												<select id="five" name="five" class="form-control">
													<option value=""></option>
													<c:forEach var="dict" items="${QUESTION_TYPE_DICT }">
													<option value="${dict.dictKey }">${dict.dictValue }</option>
													</c:forEach>
												</select>
											</div>
										</div>
									</div>
									<div class="col-md-4">
										<div class="form-group">
											<label for="six" class="control-label col-md-4">难度：</label>
											<div class="col-md-8">
												<select id="six" name="six" class="form-control">
													<option value=""></option>
													<c:forEach var="dict" items="${QUESTION_DIFFICULTY_DICT }">
														<option value="${dict.dictKey }">${dict.dictValue }</option>
													</c:forEach>
												</select>
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
		var $questionTypeTree = $("#questionTypeTree");
		var $table = $("#table");
		var $queryForm = $("#queryForm");
		var $one = $("#one");
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initQuestionTypeTree();
			initTable();
		});
		
		//初始化试题分类树
		function initQuestionTypeTree(){
			$.ajax({
				url : "home/question/questionTypeTreeList",
				success : function(arr) {
					$questionTypeTree.treeview({
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
				url : "home/question/list",
				queryParams : function(params){
					var customeParams = $.fn.my.serializeObj($queryForm);
					customeParams.page = this.pageNumber;
					customeParams.rows = this.pageSize;
					return customeParams;
				},
				columns : [
							{field : "state", checkbox : true}, 
							{field : "CODE", title : "编号", width : 50, align : "center"},
							{field : "TITLE", title : "题干 ", width : 300, align : "center"},
							{field : "TYPE_NAME", title : "类型", width : 80, align : "center"},
							{field : "DIFFICULTY_NAME", title : "难度", width : 80, align : "center"},
							{field : "STATE_NAME", title : "状态 ", width : 80, align : "center"},
							{field : "QUESTION_TYPE_NAME", title : "分类 ", width : 80, align : "center"}
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
		
		//到达添加试题页面
		function toAdd(){
			var treeNodes = $questionTypeTree.treeview("getSelected");
			if(treeNodes.length != 1){
				BootstrapDialog.show({
					title : "提示消息",
					message : "请选择试题分类",
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
			
			window.location.href = "home/question/toAdd?questionTypeId=" + treeNodes[0].ID;
		}
		
		//到达修改试题页面
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
			
			window.location.href = "home/question/toEdit?id=" + nodes[0].ID;
		}
		
		//完成试题删除
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
							url : "home/question/doDel",
							data : $.fn.my.serializeField(nodes),
							success : function(obj) {
								if (!obj.success) {
									BootstrapDialog.show({
										title : "提示消息",
										message : obj.message,
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
								
								window.location.href = "home/question/toList";
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
		
	</script>
</html>