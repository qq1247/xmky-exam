<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
	<head>
		<title>试题分类${empty questionType.id ? "添加" : "修改"}</title>
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
					<div class="panel panel-default">
   						<div class="panel-body">
   							<form id="editForm" class="form-horizontal" role="form">
   								<input type="hidden" id="id" name="id" value="${questionType.id }" />
								<div class="row">
									<div class="col-md-6">
										<div class="form-group">
											<label for="parentName" class="col-md-4 control-label">上级分类：</label>
											<div class="col-md-8">
												<input type="hidden" id="parentId" name="parentId" value="${parent.id }" />
												<input type="text" id="parentName" name="parentName" value="${parent.name }"
													class="form-control" readonly="readonly" placeholder="请选择左侧试题分类">
											</div>
										</div>
									</div>
									<div class="col-md-6">
										
									</div>
								</div>
								<div class="row">
									<div class="col-md-6">
										<div class="form-group">
											<label for="name" class="col-md-4 control-label">名称：</label>
											<div class="col-md-8">
												<input type="text" id="name" name="name" value="${questionType.name }"
													class="form-control" placeholder="请输入名称">
											</div>
										</div>
									</div>
									<div class="col-md-6">
										<div class="form-group">
											<label for="no" class="col-md-4 control-label">排序：</label>
											<div class="col-md-8">
												<input type="text" id="no" name="no" value="${questionType.no }"
													class="form-control" placeholder="请输入排序">
											</div>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-12" style="text-align: center;">
										<button type="button" class="btn btn-primary" onclick="javascript:history.back(-1);">
											<span class="glyphicon glyphicon-arrow-left"></span>
											&nbsp;返回
										</button>
										<c:if test="${empty questionType.id }">
										<button type="button" class="btn btn-primary" onclick="doAdd()">
											<span class="glyphicon glyphicon-ok"></span>
											&nbsp;添加
										</button>
										</c:if>
										<c:if test="${!empty questionType.id }">
										<button type="button" class="btn btn-primary" onclick="doEdit()">
											<span class="glyphicon glyphicon-ok"></span>
											&nbsp;修改
										</button>
										</c:if>
									</div>
								</div>
							</form>
   						</div>
   					</div>
				</div>
			</div>
		</div>
	</body>
	<script type="text/javascript">
		//定义变量
		var $tree = $("#tree");
		var $parentId = $("#parentId");
		var $parentName = $("#parentName");
		var $editForm = $("#editForm");
		var $id = $("#id");
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initTree();
			initValid();
		});
		
		//初始化试题分类树
		function initTree(){
			$.ajax({
				url : "home/questionType/treeList",
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
							$parentId.val(data.ID);
							$parentName.val(data.NAME).change();
						},
						onNodeUnselected : function(event, data) {
							$parentId.val("");
							$parentName.val("").change();
						}
					});
				}
			});
		}
		
		//初始化校验
		function initValid(){
			$editForm.bootstrapValidator({
				excluded : [":disabled"],
				fields : {
					name : {
						validators : {
							notEmpty : {}
						}}, 
					no : {
						validators : {
							notEmpty : {},
							integer : {}
						}}, 
					parentName : {
						trigger : "change",
						validators : {
							notEmpty : {}
						}
					}
				}
			});
		}
		
		//校验
		function valid(){
			var bv = $editForm.data('bootstrapValidator');
			bv.validate();
			return bv.isValid();
		}
		
		//完成试题添加
		function doAdd(){
			if(!valid()){
				return;
			}
			
			$.ajax({
				url : "home/questionType/doAdd",
				data : $editForm.serialize(),
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
					
					window.location.href = "home/questionType/toList";
				}
			});
		}
		
		//完成试题修改
		function doEdit(){
			if(!valid()){
				return;
			}
			
			$.ajax({
				url : "home/questionType/doEdit",
				data : $editForm.serialize(),
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
					
					window.location.href = "home/questionType/toList";
				}
			});
		}
	</script>
</html>