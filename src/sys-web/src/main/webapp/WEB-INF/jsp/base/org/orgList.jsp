<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>组织机构列表</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
		<div class="layui-fluid">
			<div class="layui-row layui-col-space10">
				<div class="layui-col-md2">
					<div class="layui-card">
						<div class="layui-form">
			      			<ul id="orgTree" class="ztree"></ul>
			 			</div>
					</div>
				</div>
				<div class="layui-col-md10">
					<div class="layui-card">
						<%-- 组织机构查询条件 --%>
						<form id="orgQueryForm" class="layui-form layui-card-header layuiadmin-card-header-auto">
							<input type="hidden" id="orgOne" name="one">
							<div class="layui-form-item">
								<div class="layui-inline">
									<input type="text" name="two" placeholder="请输入名称" class="layui-input">
								</div>
								<div class="layui-inline">
									<button type="button" class="layui-btn layuiadmin-btn-useradmin" onclick="orgQuery();">
										<i class="layui-icon layuiadmin-button-btn"></i>查询
									</button>
									<button type="button" class="layui-btn layui-btn-primary layuiadmin-btn-useradmin" onclick="orgReset();">
										<i class="layui-icon layuiadmin-button-btn"></i>重置
									</button>
								</div>
							</div>
						</form>
						<div class="layui-card-body">
							<div style="padding-bottom: 10px;">
								<my:auth url="org/toAdd"><button class="layui-btn layuiadmin-btn-useradmin" onclick="toOrgAdd();">添加</button></my:auth>
							</div>
							<script type="text/html" id="orgToolbar">
								<my:auth url="org/toEdit"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="orgEdit"><i class="layui-icon layui-icon-edit"></i>修改</a></my:auth>
								<my:auth url="org/toMove"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="orgMove"><i class="layui-icon layui-icon-edit"></i>移动</a></my:auth>
								<my:auth url="org/doDel"><a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="orgDel"><i class="layui-icon layui-icon-delete"></i>删除</a></my:auth>
							</script>
							
							<%-- 组织机构数据表格 --%>
							<table id="orgTable" lay-filter="orgTable"></table>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
	<%@include file="/script/myJs/tail.jspf"%>
	<script type="text/javascript">
		//定义变量
		var orgQueryForm = $("#orgQueryForm"); //组织机构查询对象
		var orgTree; //组织机构树对象
		var curSelOrgId = ""; //当前选中的组织机构ID
		var curSelOrgName = ""; //当前选中的组织机构名称
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initOrgTable();
			initOrgTree();
		});
	
		//初始化组织机构表格
		function initOrgTable() {
			layui.table.render({
				elem : "#orgTable",
				url : "org/list",
				cols : [[
						{field : "NAME", title : "名称", align : "center"},
						{field : "PARENT_NAME", title : "上级组织机构 ", align : "center"},
						{field : "NO", title : "排序 ", align : "center"},
						{fixed: 'right', title : "操作 ", toolbar : "#orgToolbar", align : "center"}
						]],
				page : true,
				height : "full-180",
				method : "post",
				defaultToolbar : [],
				parseData: function(obj){
					return {
						"code" : obj.succ,
						"msg" : obj.msg,
						"count" : obj.data.total,
						"data" : obj.data.rows
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
			layui.table.on("rowDouble(orgTable)", function(obj){
				<my:auth url="org/toEdit">toOrgEdit(obj.data.ID);</my:auth>
			});
			layui.table.on("tool(orgTable)", function(obj){
				var data = obj.data;
				if(obj.event === "orgEdit") {
					toOrgEdit(obj.data.ID);
				} else if(obj.event === "orgMove") {
					toOrgMove(obj.data);
				} else if(obj.event === "orgDel") {
					doOrgDel(obj.data.ID);
				}
			});
		}
		
		//初始化组织机构树
		function initOrgTree() {
			orgTree = $.fn.zTree.init($("#orgTree"), {
				async : {
					url : "org/treeList",
					enable : true,
					dataFilter : ztreeDataFilter
				},
				callback : {
					onClick : function(event, treeId, treeNode) {
						curSelOrgId = treeNode.ID;
						curSelOrgName = treeNode.NAME;
						$("#orgOne").val(curSelOrgId);
						orgQuery();
					},
					onAsyncSuccess : function(event, treeId, msg, treeNode) {
						var orgTree = $.fn.zTree.getZTreeObj(treeId);
						orgTree.expandAll(true);
						
						if (!curSelOrgId) {
							var rootNode = orgTree.getNodesByFilter(function(node) { return (node.level == 0); }, true);
							orgTree.selectNode(rootNode);
							
							curSelOrgId = rootNode.ID;
							curSelOrgName = rootNode.NAME;
							$("#orgOne").val(curSelOrgId);
							return;
						}
						
						var curNode = orgTree.getNodeByParam("id", curSelOrgId, null);
						orgTree.selectNode(curNode);
						
						orgQuery();
					}
				}
			});
			
			$("#orgTree").height($(window).height() - 45);
		}
		
		//组织机构查询
		function orgQuery() {
			layui.table.reload("orgTable", {"where" : $.fn.my.serializeObj(orgQueryForm)});
		}
	
		//组织机构重置
		function orgReset() {
			orgQueryForm[0].reset();
			orgQuery();
		}
		
		//到达添加组织机构页面
		function toOrgAdd() {
			if(!curSelOrgId){
				layer.alert("请选择上级组织机构！", {"title" : "提示消息"});
				return;
			}
			
			$.ajax({
				url : "org/toAdd",
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "添加组织机构",
						area : ["800px", "500px"],
						content : obj,
						btn : ["添加", "取消"],
						type : 1,
						yes : function(index, layero){
							doOrgAdd(index);
						},
						success: function(layero, index){
							$("#parentOrgId").val(curSelOrgId);
							$("#parentOrgName").val(curSelOrgName);
							layui.form.render(null, "orgEditFrom");
						}
					});
				}
			});
		}

		//完成添加组织机构
		function doOrgAdd(orgAddDialogIndex) {
			layui.form.on("submit(orgEditBtn)", function(data) {
				layer.confirm("确定要添加？", function(index) {
					$.ajax({
						url : "org/doAdd",
						data : data.field,
						success : function(obj) {
							orgTreeFlush();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							if(obj.data.initPwd){
								layer.alert("初始账号密码：【" + obj.data.loginName + "，" + obj.data.initPwd + "】", {"title" : "提示消息"});
							}
							
							layer.close(index);
							layer.close(orgAddDialogIndex);
						}
					});
				});
			});
			$("[lay-filter='orgEditBtn']").click();
		}
		
		//到达修改组织机构页面
		function toOrgEdit(id) {
			$.ajax({
				url : "org/toEdit",
				data : {id : id},
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "修改组织机构",
						area : ["800px", "500px"],
						content : obj,
						btn : ["修改", "取消"],
						type : 1,
						yes : function(index, layero){
							doOrgEdit(index);
						},
						success: function(layero, index){
							layui.form.render(null, "orgEditFrom");
						}
					});
				}
			});
		}

		//完成修改组织机构
		function doOrgEdit(orgEditDialogIndex) {
			layui.form.on("submit(orgEditBtn)", function(data) {
				layer.confirm("确定要修改？", function(index) {
					$.ajax({
						url : "org/doEdit",
						data : data.field,
						success : function(obj) {
							orgTreeFlush();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
							layer.close(orgEditDialogIndex);
						}
					});
				});
			});
			$("[lay-filter='orgEditBtn']").click();
		}
		
		//完成删除组织机构
		function doOrgDel(id) {
			layer.confirm("确定要删除？", function(index) {
				$.ajax({
					url : "org/doDel",
					data : {id : id},
					success : function(obj) {
						orgTreeFlush();
						
						if (!obj.succ) {
							layer.alert(obj.msg, {"title" : "提示消息"});
							return;
						}
						
						layer.close(index);
					}
				});
			});
		}

		//到达移动组织机构页面
		function toOrgMove(orgObj){
			$.ajax({
				url : "org/toMove",
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "选择组织机构",
						area : ["300px", "500px"],
						content : obj,
						btn : ["移动", "取消"],
						type : 1,
						yes : function(index, layero){
							var orgMoveTreeObj = $.fn.zTree.getZTreeObj("orgMoveTree");
							var orgMoveNodes = orgMoveTreeObj.getSelectedNodes();
							if (orgMoveNodes.length != 1) {
								layer.alert("请选择一行数据！", {"title" : "提示消息"});
								return;
							}
							
							var sourceId = orgObj.ID;
							var sourceName = orgObj.NAME;
							var targetId = orgMoveNodes[0].ID;
							var targetName = orgMoveNodes[0].NAME;
							if(sourceId == targetId){
								layer.alert("源组织机构和目标组织机构一致！", {"title" : "提示消息"});
								return;
							}
							if(orgMoveNodes[0].PARENT_SUB.indexOf(orgObj.PARENT_SUB) >= 0){
								layer.alert("父组织机构不能移动到子组织机构下！", {"title" : "提示消息"});
								return;
							}
							
							doOrgMove(sourceId, sourceName, targetId, targetName, index);
						},
						success: function(layero, index) {
							$.fn.zTree.init($("#orgMoveTree"), {
								async : {
									url : "org/treeList",
									otherParam : {
										
									},
									enable : true,
									dataFilter : ztreeDataFilter
								},
								callback : {
									onAsyncSuccess : function(event, treeId, msg, treeNode) {
										var rootNode = orgTree.getNodesByFilter(function(node) { return (node.level == 0); }, true);
										orgTree.selectNode(rootNode);
									}
								}
							});
						}
					});
				}
			});
		}
		
		//完成移动组织机构
		function doOrgMove(sourceId, sourceName, targetId, targetName, orgDialogIndex){
			layer.confirm("确定要移动【"+sourceName+"】到【"+targetName+"】？", {title : "提示消息"}, function(index){
				var params = {"sourceId" : sourceId, "targetId" : targetId};
				$.ajax({
					url : "org/doMove",
					data : params,
					success : function(obj){
						orgTreeFlush();
						
						if(!obj.succ){
							layer.alert(obj.msg, {"title" : "提示消息"});
							return;
						}
						
						layer.close(index);
						layer.close(orgDialogIndex);
					}
				});
			});
			$("[lay-filter='orgMoveBtn']").click();
		}
		
		//刷新组织机构树
		function orgTreeFlush() {
			orgTree.reAsyncChildNodes(null, "refresh");
		}
	</script>
</html>
