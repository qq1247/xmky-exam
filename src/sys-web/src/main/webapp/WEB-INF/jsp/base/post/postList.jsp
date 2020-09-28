<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>岗位列表</title>
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
						<%-- 岗位查询条件 --%>
						<form id="postQueryForm" class="layui-form layui-card-header layuiadmin-card-header-auto">
							<input type="hidden" id="postOne" name="one">
							<div class="layui-form-item">
								<div class="layui-inline">
									<input type="text" name="two" placeholder="请输入名称" class="layui-input">
								</div>
								<div class="layui-inline">
									<input type="text" name="three" placeholder="请输入组织机构" class="layui-input">
								</div>
								<div class="layui-inline">
									<button type="button" class="layui-btn layuiadmin-btn-useradmin" onclick="postQuery();">
										<i class="layui-icon layuiadmin-button-btn"></i>查询
									</button>
									<button type="button" class="layui-btn layui-btn-primary layuiadmin-btn-useradmin" onclick="postPostet();">
										<i class="layui-icon layuiadmin-button-btn"></i>重置
									</button>
								</div>
							</div>
						</form>
						<div class="layui-card-body">
							<div style="padding-bottom: 10px;">
								<my:auth url="post/toAdd"><button class="layui-btn layuiadmin-btn-useradmin" onclick="toPostAdd();">添加</button></my:auth>
							</div>
							<script type="text/html" id="postToolbar">
								<my:auth url="post/toEdit"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="postEdit"><i class="layui-icon layui-icon-edit"></i>修改</a></my:auth>
								<my:auth url="post/toResUpdate"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="resUpdate"><i class="layui-icon layui-icon-edit"></i>设置权限</a></my:auth>
								<my:auth url="post/doDel"><a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="postDel"><i class="layui-icon layui-icon-delete"></i>删除</a></my:auth>
							</script>
							<%-- 岗位数据表格 --%>
							<table id="postTable" lay-filter="postTable"></table>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
	<%@include file="/script/myJs/tail.jspf"%>
	<script type="text/javascript">
		//定义变量
		var postQueryForm = $("#postQueryForm"); //岗位查询对象
		var orgTree; //组织机构树对象
		var curSelOrgId = ""; //当前选中的组织机构ID
		var curSelOrgName = ""; //当前选中的组织机构名称
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initPostTable();
			initOrgTree();
		});
	
		//初始化岗位表格
		function initPostTable() {
			layui.table.render({
				elem : "#postTable",
				url : "post/list",
				cols : [[
						{field : "NAME", title : "名称", align : "center"}, 
						{field : "CODE", title : "编码", align : "center"}, 
						{field : "ORGNAME", title : "组织机构", align : "center"},
						{fixed: 'right', title : "操作 ", toolbar : "#postToolbar", align : "center"}
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
			layui.table.on("rowDouble(postTable)", function(obj){
				<my:auth url="post/toEdit">toPostEdit(obj.data.ID);</my:auth>
			});
			layui.table.on("tool(postTable)", function(obj){
				var data = obj.data;
				if(obj.event === "postEdit") {
					toPostEdit(obj.data.ID);
				} else if(obj.event === "resUpdate") {
					toResUpdate(obj.data.ID);
				} else if(obj.event === "postDel") {
					doPostDel(obj.data.ID);
				}
			});
		}
		
		//初始化组织机构树
		function initOrgTree() {
			orgTree = $.fn.zTree.init($("#orgTree"), {
				async : {
					url : "post/orgTreeList",
					enable : true,
					dataFilter : ztreeDataFilter
				},
				callback : {
					onClick : function(event, treeId, treeNode) {
						curSelOrgId = treeNode.ID;
						curSelOrgName = treeNode.NAME;
						$("#postOne").val(curSelOrgId);
						postQuery();
					},
					onAsyncSuccess : function(event, treeId, msg, treeNode) {
						var orgTree = $.fn.zTree.getZTreeObj(treeId);
						orgTree.expandAll(true);
						
						if (!curSelOrgId) {
							var rootNode = orgTree.getNodesByFilter(function(node) { return (node.level == 0); }, true);
							orgTree.selectNode(rootNode);
							
							curSelOrgId = rootNode.ID;
							curSelOrgName = rootNode.NAME;
							$("#postOne").val(curSelOrgId);
							return;
						}
						
						var curNode = orgTree.getNodeByParam("id", curSelOrgId, null);
						orgTree.selectNode(curNode);
						
						postQuery();
					}
				}
			});
			
			$("#orgTree").height($(window).height() - 45);
		}
		
		//岗位查询
		function postQuery() {
			layui.table.reload("postTable", {"where" : $.fn.my.serializeObj(postQueryForm)});
		}
	
		//岗位重置
		function postPostet() {
			postQueryForm[0].reset();
			postQuery();
		}
		
		//到达添加岗位页面
		function toPostAdd() {
			if(!curSelOrgId){
				layer.alert("请选择组织机构！", {"title" : "提示消息"});
				return;
			}
			
			$.ajax({
				url : "post/toAdd",
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "添加岗位",
						area : ["800px", "500px"],
						content : obj,
						btn : ["添加", "取消"],
						type : 1,
						yes : function(index, layero){
							doPostAdd(index);
						},
						success: function(layero, index){
							$("#orgId").val(curSelOrgId);
							$("#orgName").val(curSelOrgName);
							layui.form.render(null, "postEditFrom");
						}
					});
				}
			});
		}

		//完成添加岗位
		function doPostAdd(postAddDialogIndex) {
			layui.form.on("submit(postEditBtn)", function(data) {
				layer.confirm("确定要添加？", function(index) {
					$.ajax({
						url : "post/doAdd",
						data : data.field,
						success : function(obj) {
							orgTreeFlush();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
							layer.close(postAddDialogIndex);
						}
					});
				});
			});
			$("[lay-filter='postEditBtn']").click();
		}
		
		//到达修改岗位页面
		function toPostEdit(id) {
			$.ajax({
				url : "post/toEdit",
				data : {id : id},
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "修改岗位",
						area : ["800px", "500px"],
						content : obj,
						btn : ["修改", "取消"],
						type : 1,
						yes : function(index, layero){
							doPostEdit(index);
						},
						success: function(layero, index){
							layui.form.render(null, "postEditFrom");
						}
					});
				}
			});
		}

		//完成修改岗位
		function doPostEdit(postEditDialogIndex) {
			layui.form.on("submit(postEditBtn)", function(data) {
				layer.confirm("确定要修改？", function(index) {
					$.ajax({
						url : "post/doEdit",
						data : data.field,
						success : function(obj) {
							orgTreeFlush();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
							layer.close(postEditDialogIndex);
						}
					});
				});
			});
			$("[lay-filter='postEditBtn']").click();;
		}
		
		//完成删除岗位
		function doPostDel(id) {
			layer.confirm("确定要删除？", function(index) {
				$.ajax({
					url : "post/doDel",
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
		
		//到达设置权限页面
		function toResUpdate(id) {
			$.ajax({
				url : "post/toResUpdate",
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "选择权限",
						area : ["300px", "500px"],
						content : obj,
						btn : ["确定", "取消"],
						type : 1,
						yes : function(index, layero){
							doResUpdate(id, index);
						},
						success: function(layero, index){
							$.fn.zTree.init($("#resUpdateTree"), {
								async : {
									url : "post/resTreeList",
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
		
		//完成设置权限
		function doResUpdate(id, resDialogIndex) {
			var resNodes = $.fn.zTree.getZTreeObj("resUpdateTree").getCheckedNodes();
			var resIds = $.fn.my.serializeField(resNodes, {attrName : "resIds"});
			var params = "id=" + id + "&" + resIds;
			
			layer.confirm("确定要设置？", {title : "提示消息"}, function(index){
				$.ajax({
					url : "post/doResUpdate",
					data : params,
					success : function(obj) {
						if(!obj.succ){
							layer.alert(obj.msg, {"title" : "提示消息"});
							return;
						}
						
						layer.close(index);
						layer.close(resDialogIndex);
					}
				});
			});
			$("[lay-filter='resUpdateBtn']").click();
		}
		
		//刷新组织机构树
		function orgTreeFlush() {
			orgTree.reAsyncChildNodes(null, "refresh");
		}
	</script>
</html>
