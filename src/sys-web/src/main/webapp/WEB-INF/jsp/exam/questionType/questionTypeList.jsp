<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>试题分类列表</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
		<div class="layui-fluid">
			<div class="layui-row layui-col-space10">
				<div class="layui-col-md2">
					<div class="layui-card">
						<div class="layui-form">
			      			<ul id="questionTypeTree" class="ztree"></ul>
			 			</div>
					</div>
				</div>
				<div class="layui-col-md10">
					<div class="layui-card">
						<%-- 试题分类查询条件 --%>
						<form id="questionTypeQueryForm" class="layui-form layui-card-header layuiadmin-card-header-auto">
							<input type="hidden" id="questionType_one" name="one">
							<div class="layui-form-item">
								<div class="layui-inline">
									<input type="text" name="one" placeholder="请输入ID" class="layui-input">
								</div>
								<div class="layui-inline">
									<button type="button" class="layui-btn layuiadmin-btn-useradmin" onclick="questionTypeQuery();">
										<i class="layui-icon layuiadmin-button-btn"></i>查询
									</button>
									<button type="button" class="layui-btn layui-btn-primary layuiadmin-btn-useradmin" onclick="questionTypeReset();">
										<i class="layui-icon layuiadmin-button-btn"></i>重置
									</button>
								</div>
							</div>
						</form>
						<div class="layui-card-body">
							<div style="padding-bottom: 10px;">
								<my:auth url="questionType/toAdd"><button class="layui-btn layuiadmin-btn-useradmin" onclick="toQuestionTypeAdd();">添加</button></my:auth>
							</div>
							<script type="text/html" id="questionTypeToolbar">
								<my:auth url="questionType/toEdit"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="questionTypeEdit"><i class="layui-icon layui-icon-edit"></i>修改</a></my:auth>
								<my:auth url="questionType/toMove"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="questionTypeMove"><i class="layui-icon layui-icon-edit"></i>移动</a></my:auth>
								<my:auth url="questionType/doDel"><a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="questionTypeDel"><i class="layui-icon layui-icon-delete"></i>删除</a></my:auth>
							</script>
							<%-- 试题分类数据表格 --%>
							<table id="questionTypeTable" lay-filter="questionTypeTable"></table>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
	<%@include file="/script/myJs/tail.jspf"%>
	<script type="text/javascript">
		//定义变量
		var questionTypeQueryForm = $("#questionTypeQueryForm"); //试题分类查询对象
		var questionTypeTree; //试题分类树对象
		var curSelQuestionTypeId = ""; //当前选中的试题分类ID
		var curSelQuestionTypeName = ""; //当前选中的试题分类名称
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initQuestionTypeTable();
			initQuestionTypeTree();
		});
	
		//初始化试题分类表格
		function initQuestionTypeTable() {
			layui.table.render({
				elem : "#questionTypeTable",
				url : "questionType/list",
				cols : [[
						{field : "NAME", title : "名称", align : "center"},
						{field : "NO", title : "排序", align : "center"},
						{fixed: 'right', title : "操作 ", toolbar : "#questionTypeToolbar", align : "center"}
						]],
				page : true,
				height : "full-180",
				method : "post",
				defaultToolbar : [],
				parseData: function(questionType){
					return {
						"code" : questionType.succ,
						"msg" : questionType.msg,
						"count" : questionType.data.total,
						"data" : questionType.data.rows
					};
				},
				request: {
					pageName: "curPage",
					limitName: "pageSize"
				}, 
				response: {
					statusCode : true
				}
			});
			layui.table.on("rowDouble(questionTypeTable)", function(obj){
				<my:auth url="questionType/toEdit">toQuestionTypeEditForDblClick(obj.data.ID);</my:auth>
			});
			layui.table.on("tool(questionTypeTable)", function(obj){
				var data = obj.data;
				if(obj.event === "questionTypeEdit") {
					toQuestionTypeEdit(obj.data.ID);
				} else if(obj.event === "questionTypeMove") {
					toQuestionTypeMove(obj.data);
				} else if(obj.event === "questionTypeDel") {
					doQuestionTypeDel(obj.data.ID);
				}
			});
		}
		
		//初始化试题分类树
		function initQuestionTypeTree() {
			questionTypeTree = $.fn.zTree.init($("#questionTypeTree"), {
				async : {
					url : "questionType/treeList",
					enable : true,
					dataFilter : ztreeDataFilter
				},
				callback : {
					onClick : function(event, treeId, treeNode) {
						curSelQuestionTypeId = treeNode.ID;
						curSelQuestionTypeName = treeNode.NAME;
						$("#questionType_one").val(curSelQuestionTypeId);
						questionTypeQuery();
					},
					onAsyncSuccess : function(event, treeId, msg, treeNode) {
						var questionTypeTree = $.fn.zTree.getZTreeObj(treeId);
						questionTypeTree.expandAll(true);
						
						if (!curSelQuestionTypeId) {
							var rootNode = questionTypeTree.getNodesByFilter(function(node) { return (node.level == 0); }, true);
							questionTypeTree.selectNode(rootNode);
							
							curSelQuestionTypeId = rootNode.ID;
							curSelQuestionTypeName = rootNode.NAME;
							$("#questionType_one").val(curSelQuestionTypeId);
							return;
						}
						
						var curNode = questionTypeTree.getNodeByParam("id", curSelQuestionTypeId, null);
						questionTypeTree.selectNode(curNode);
						
						questionTypeQuery();
					}
				}
			});
			
			$("#questionTypeTree").height($(window).height() - 45);
		}
		
		//试题分类查询
		function questionTypeQuery() {
			layui.table.reload(questionTypeTable, {"where" : $.fn.my.serializeObj(questionTypeQueryForm)});
		}
	
		//试题分类重置
		function questionTypeReset() {
			questionTypeQueryForm[0].reset();
			questionTypeQuery();
		}
		
		//到达添加试题分类页面
		function toQuestionTypeAdd() {
			if(!curSelQuestionTypeId){
				layer.alert("请选择上级试题分类！", {"title" : "提示消息"});
				return;
			}
			
			$.ajax({
				url : "questionType/toAdd",
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "添加试题分类",
						area : ["800px", "500px"],
						content : obj,
						btn : ["添加", "取消"],
						type : 1,
						yes : function(index, layero){
							doQuestionTypeAdd(index);
						},
						success: function(layero, index){
							$("#questionType_parentId").val(curSelQuestionTypeId);
							$("#questionType_parentName").val(curSelQuestionTypeName);
							layui.form.render(null, "questionTypeEditFrom");
						}
					});
				}
			});
		}

		//完成添加试题分类
		function doQuestionTypeAdd(questionTypeAddDialogIndex) {
			layui.form.on("submit(questionTypeEditBtn)", function(data) {
				layer.confirm("确定要添加？", function(index) {
					$.ajax({
						url : "questionType/doAdd",
						data : data.field,
						success : function(obj) {
							questionTypeQuery();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
							layer.close(questionTypeAddDialogIndex);
						}
					});
				});
			});
			$("#questionTypeEditBtn").click();
		}
		
		//到达修改试题分类页面
		function toQuestionTypeEdit(id) {
			$.ajax({
				url : "questionType/toEdit",
				data : {"id" : id},
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "修改试题分类",
						area : ["800px", "500px"],
						content : obj,
						btn : ["修改", "取消"],
						type : 1,
						yes : function(index, layero){
							doQuestionTypeEdit(index);
						},
						success: function(layero, index){
							layui.form.render(null, "questionTypeEditFrom");
						}
					});
				}
			});
		}

		//完成修改试题分类
		function doQuestionTypeEdit(questionTypeEditDialogIndex) {
			layui.form.on("submit(questionTypeEditBtn)", function(data) {
				layer.confirm("确定要修改？", function(index) {
					$.ajax({
						url : "questionType/doEdit",
						data : data.field,
						success : function(obj) {
							questionTypeQuery();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
							layer.close(questionTypeEditDialogIndex);
						}
					});
				});
			});
			$("#questionTypeEditBtn").click();;
		}

		//完成删除试题分类
		function doQuestionTypeDel(id) {
			layer.confirm("确定要删除？", function(index) {
				$.ajax({
					url : "questionType/doDel",
					data : {id : id},
					success : function(obj) {
						questionTypeTreeFlush();
						
						if (!obj.succ) {
							layer.alert(obj.msg, {"title" : "提示消息"});
							return;
						}
						
						layer.close(index);
					}
				});
			});
		}
		
		//到达移动试题分类页面
		function toQuestionTypeMove(questionTypeObj){
			$.ajax({
				url : "questionType/toMove",
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "选择试题分类",
						area : ["300px", "500px"],
						content : obj,
						btn : ["移动", "取消"],
						type : 1,
						yes : function(index, layero){
							var questionTypeMoveTreeObj = $.fn.zTree.getZTreeObj("questionTypeMoveTree");
							var questionTypeMoveNodes = questionTypeMoveTreeObj.getSelectedNodes();
							if (questionTypeMoveNodes.length != 1) {
								layer.alert("请选择一行数据！", {"title" : "提示消息"});
								return;
							}
							
							var sourceId = questionTypeObj.ID;
							var sourceName = questionTypeObj.NAME;
							var targetId = questionTypeMoveNodes[0].ID;
							var targetName = questionTypeMoveNodes[0].NAME;
							if(sourceId == targetId){
								layer.alert("源试题分类和目标试题分类一致！", {"title" : "提示消息"});
								return;
							}
							if(questionTypeMoveNodes[0].PARENT_SUB.indexOf(questionTypeObj.PARENT_SUB) >= 0){
								layer.alert("父试题分类不能移动到子试题分类下！", {"title" : "提示消息"});
								return;
							}
							
							doQuestionTypeMove(sourceId, sourceName, targetId, targetName, index);
						},
						success: function(layero, index) {
							$.fn.zTree.init($("#questionTypeMoveTree"), {
								async : {
									url : "questionType/treeList",
									otherParam : {
										
									},
									enable : true,
									dataFilter : ztreeDataFilter
								},
								callback : {
									onAsyncSuccess : function(event, treeId, msg, treeNode) {
										var rootNode = questionTypeTree.getNodesByFilter(function(node) { return (node.level == 0); }, true);
										questionTypeTree.selectNode(rootNode);
									}
								}
							});
						}
					});
				}
			});
		}
		
		//完成移动试题分类
		function doQuestionTypeMove(sourceId, sourceName, targetId, targetName, questionTypeDialogIndex){
			layer.confirm("确定要移动【"+sourceName+"】到【"+targetName+"】？", {title : "确认消息"}, function(index){
				var params = {"sourceId" : sourceId, "targetId" : targetId};
				$.ajax({
					url : "questionType/doMove",
					data : params,
					success : function(obj){
						questionTypeTreeFlush();
						
						if(!obj.succ){
							layer.alert(obj.msg, {"title" : "提示消息"});
							return;
						}
						
						layer.close(index);
						layer.close(questionTypeDialogIndex);
					}
				});
			});
			$("[lay-filter='questionTypeMoveBtn']").click();
		}
		
		//刷新试题分类
		function questionTypeTreeFlush() {
			questionTypeTree.reAsyncChildNodes(null, "refresh");
		}
		
	</script>
</html>
