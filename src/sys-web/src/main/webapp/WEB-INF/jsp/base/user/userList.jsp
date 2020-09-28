<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>用户列表</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
		<div class="layui-fluid">
			<div class="layui-row layui-col-space10">
				<div class="layui-col-md2">
					<div class="layui-card">
						<div class="layui-form">
			      			<ul id="orgTree" class="ztree" style="overflow: auto;"></ul>
			 			</div>
					</div>
				</div>
				<div class="layui-col-md10">
					<div class="layui-card">
						<%-- 用户查询条件 --%>
						<form id="userQueryForm" class="layui-form layui-card-header layuiadmin-card-header-auto">
							<input type="hidden" id="userOne" name="one">
							<div class="layui-form-item">
								<div class="layui-inline">
									<input type="text" name="two" placeholder="请输入昵称" class="layui-input">
								</div>
								<div class="layui-inline">
									<input type="text" name="three" placeholder="请输入组织机构" class="layui-input">
								</div>
								<div class="layui-inline">
									<input type="text" name="four" placeholder="请输入岗位" class="layui-input">
								</div>
								<div class="layui-inline">
									<button type="button" class="layui-btn layuiadmin-btn-useradmin" onclick="userQuery();">
										<i class="layui-icon layuiadmin-button-btn"></i>查询
									</button>
									<button type="button" class="layui-btn layui-btn-primary layuiadmin-btn-useradmin" onclick="userReset();">
										<i class="layui-icon layuiadmin-button-btn"></i>重置
									</button>
								</div>
							</div>
						</form>
						<div class="layui-card-body">
							<div style="padding-bottom: 10px;">
								<my:auth url="user/toAdd"><button class="layui-btn layuiadmin-btn-useradmin" onclick="toUserAdd();">添加</button></my:auth>
							</div>
							<script type="text/html" id="userToolbar">
								<my:auth url="user/toEdit"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="userEdit"><i class="layui-icon layui-icon-edit"></i>修改</a></my:auth>
								<my:auth url="user/toOrgUpdate"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="orgUpdate"><i class="layui-icon layui-icon-edit"></i>设置机构</a></my:auth>
								<my:auth url="user/toPostUpdate"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="postUpdate"><i class="layui-icon layui-icon-edit"></i>设置岗位</a></my:auth>
								<my:auth url="user/initPwd"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="initPwd"><i class="layui-icon layui-icon-edit"></i>初始密码</a></my:auth>
								<my:auth url="user/doDel"><a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="userDel"><i class="layui-icon layui-icon-delete"></i>删除</a></my:auth>
							</script>
							<%-- 用户数据表格 --%>
							<table id="userTable" lay-filter="userTable"></table>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
	<%@include file="/script/myJs/tail.jspf"%>
	<script type="text/javascript">
		//定义变量
		var userQueryForm = $("#userQueryForm"); //用户查询对象
		var orgTree; //组织机构树对象
		var curSelOrgId = ""; //当前选中的用户ID
		var curSelOrgName = ""; //当前选中的用户名称
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initUserTable();
			initOrgTree();
		});
	
		//初始化用户表格
		function initUserTable() {
			layui.table.render({
				elem : "#userTable",
				url : "user/list",
				cols :  [[ 
						{field : "NAME", title : "昵称", align : "center"}, 
						{field : "LOGIN_NAME", title : "登录名称", align : "center"}, 
						{field : "ORG_NAME", title : "组织机构", align : "center"},
						{field : "POST_NAMES", title : "岗位", align : "center"},
						{fixed: 'right', title : "操作 ", toolbar : "#userToolbar", align : "center", width : 500}
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
			layui.table.on("rowDouble(userTable)", function(obj){
				<my:auth url="user/toEdit">toUserEdit(obj.data.ID);</my:auth>
			});
			layui.table.on("tool(userTable)", function(obj){
				var data = obj.data;
				if(obj.event === "userEdit") {
					toUserEdit(obj.data.ID);
				} else if(obj.event === "orgUpdate") {
					toOrgUpdate(obj.data.ID);
				} else if(obj.event === "postUpdate") {
					toPostUpdate(obj.data.ID);
				} else if(obj.event === "initPwd") {
					initPwd(obj.data.ID);
				} else if(obj.event === "userDel") {
					doUserDel(obj.data.ID);
				}
			});
		}
		
		//初始化组织机构树
		function initOrgTree() {
			orgTree = $.fn.zTree.init($("#orgTree"), {
				async : {
					enable : true,
					url : "user/orgTreeList",
					dataFilter : ztreeDataFilter
				},
				callback : {
					onClick : function(event, treeId, treeNode) {
						curSelOrgId = treeNode.ID;
						curSelOrgName = treeNode.NAME;
						$("#userOne").val(curSelOrgId);
						userQuery();
					},
					onAsyncSuccess : function(event, treeId, msg, treeNode) {
						var orgTree = $.fn.zTree.getZTreeObj(treeId);
						orgTree.expandAll(true);
						
						if (!curSelOrgId) {
							var rootNode = orgTree.getNodesByFilter(function(node) { return (node.level == 0); }, true);
							orgTree.selectNode(rootNode);
							
							curSelOrgId = rootNode.ID;
							curSelOrgName = rootNode.NAME;
							$("#userOne").val(curSelOrgId);
							return;
						}
						
						var curNode = orgTree.getNodeByParam("id", curSelOrgId, null);
						orgTree.selectNode(curNode);
						
						userQuery();
					}
				}
			});
			$("#orgTree").height($(window).height() - 45);
		}
		
		//用户查询
		function userQuery() {
			layui.table.reload("userTable", {"where" : $.fn.my.serializeObj(userQueryForm)});
		}
	
		//用户重置
		function userReset() {
			userQueryForm[0].reset();
			userQuery();
		}
		
		//到达添加用户页面
		function toUserAdd() {
			if(!curSelOrgId){
				layer.alert("请选择组织机构！", {"title" : "提示消息"});
				return;
			}
			
			$.ajax({
				url : "user/toAdd",
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "添加用户",
						area : ["800px", "500px"],
						content : obj,
						btn : ["添加", "取消"],
						type : 1,
						yes : function(index, layero){
							doUserAdd(index);
						},
						success: function(layero, index){
							$("#user_orgId").val(curSelOrgId);
							$("#user_orgName").val(curSelOrgName);
							layui.form.render(null, "userEditFrom");
						}
					});
				}
			});
		}

		//完成添加用户
		function doUserAdd(userAddDialogIndex) {
			layui.form.on("submit(userEditBtn)", function(data) {
				layer.confirm("确定要添加？", function(index) {
					$.ajax({
						url : "user/doAdd",
						data : data.field,
						success : function(obj) {
							orgTreeFlush();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							if(obj.data.initPwd){
								layer.alert("初始密码：【" + obj.data.initPwd + "】", {"title" : "提示消息"});
							}
							
							layer.close(index);
							layer.close(userAddDialogIndex);
						}
					});
				});
			});
			$("[lay-filter='userEditBtn']").click();
		}
		
		//到达修改用户页面
		function toUserEdit(id) {
			$.ajax({
				url : "user/toEdit",
				data : {id : id},
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "修改用户",
						area : ["800px", "500px"],
						content : obj,
						btn : ["修改", "取消"],
						type : 1,
						yes : function(index, layero){
							doUserEdit(index);
						},
						success: function(layero, index){
							layui.form.render(null, "userEditFrom");
						}
					});
				}
			});
		}

		//完成修改用户
		function doUserEdit(userEditDialogIndex) {
			layui.form.on("submit(userEditBtn)", function(data) {
				layer.confirm("确定要修改？", function(index) {
					$.ajax({
						url : "user/doEdit",
						data : data.field,
						success : function(obj) {
							orgTreeFlush();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							if(obj.data.initPwd){
								layer.alert("初始密码：【" + obj.data.initPwd + "】", {"title" : "提示消息"});
							}
							
							layer.close(index);
							layer.close(userEditDialogIndex);
						}
					});
				});
			});
			$("[lay-filter='userEditBtn']").click();;
		}

		//完成删除用户
		function doUserDel(id) {
			layer.confirm("确定要删除？", function(index) {
				$.ajax({
					url : "user/doDel",
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
		
		//完成初始化密码
		function initPwd(id) {
			//初始化密码
			layer.confirm("确定要初始密码？", {title : "提示消息"}, function(index){
				var params = {id : id};
				$.ajax({
					url : "user/initPwd",
					data : {id : id},
					success : function(obj){
						if(!obj.succ){
							layer.alert(obj.msg, {"title" : "提示消息"});
							return;
						}
						
						if(obj.data.initPwd){
							layer.alert("初始密码：【" + obj.data.initPwd + "】", {"title" : "提示消息"});
						}
						
						layer.close(index);
					}
				});
			});
		}
		
		//到达设置岗位页面
		function toPostUpdate(id){
			$.ajax({
				url : "user/toPostUpdate",
				data : {},
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "选择岗位",
						area : ["300px", "500px"],
						content : obj,
						type : 1,
						btn : ["确定", "取消"],
						yes : function(index, layero){
							doPostUpdate(id, index);
						},
						success: function(layero, index){
							$.fn.zTree.init($("#postUpdateTree"), {
								async : {
									url : "user/postTreeList",
									otherParam : {
										id : id
									},	
									enable : true,
									dataFilter : ztreeDataFilter
								},
								check: {
									enable: true
								}
							});
						}
					});
				}
			});
		}
		
		//完成设置岗位
		function doPostUpdate(id, postDialogIndex){
			layer.confirm("确定要设置？", {title : "提示消息"}, function(index){
				var postNodes = $.fn.zTree.getZTreeObj("postUpdateTree").getCheckedNodes();
				var postIds = [];
				for (var i in postNodes) {
					postIds.push(postNodes[i].ID);
				}
			
				$.ajax({
					url : "user/doPostUpdate",
					data : {
						id : id,
						postIds : postIds
					},
					success : function(obj) {
						orgTreeFlush();
						
						if(!obj.succ){
							layer.alert(obj.msg, {"title" : "提示消息"});
							return;
						}
						
						layer.close(index);
						layer.close(postDialogIndex);
					}
				});
			});
			$("[lay-filter='postUpdateBtn']").click();
		}
		
		//到达设置组织机构页面
		function toOrgUpdate(id){
			$.ajax({
				url : "user/toOrgUpdate",
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "选择组织机构",
						area : ["300px", "500px"],
						content : obj,
						btn : ["确定", "取消"],
						type : 1,
						yes : function(index, layero){
							doOrgUpdate(id, index);
						},
						success: function(layero, index) {
							$.fn.zTree.init($("#orgUpdateTree"), {
								async : {
									url : "user/orgTreeList",
									enable : true,
									dataFilter : ztreeDataFilter
								},
								callback : {
									onAsyncSuccess : function(event, treeId, msg, treeNode) {
										var orgTree = $.fn.zTree.getZTreeObj(treeId);
										orgTree.expandAll(true);
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
		
		//完成设置组织机构
		function doOrgUpdate(id, orgDialogIndex){
			layer.confirm("确定要设置？", {title : "提示消息"}, function(index){
				var orgNodes = $.fn.zTree.getZTreeObj("orgUpdateTree").getSelectedNodes();
				var orgId = orgNodes[0].ID;
				
				$.ajax({
					url : "user/doOrgUpdate",
					data : {
						id : id,
						orgId : orgId
					},
					success : function(obj) {
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
			$("[lay-filter='orgUpdateBtn']").click();
		}
		
		//刷新组织机构树
		function orgTreeFlush() {
			orgTree.reAsyncChildNodes(null, "refresh");
		}
	</script>
</html>
