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
			      			<ul id="examTypeTree" class="ztree"></ul>
			 			</div>
					</div>
				</div>
				<div class="layui-col-md10">
					<div class="layui-card">
						<%-- 试题分类查询条件 --%>
						<form id="examTypeQueryForm" class="layui-form layui-card-header layuiadmin-card-header-auto">
							<input type="hidden" id="examTypeOne" name="one">
							<div class="layui-form-item">
								<div class="layui-inline">
									<input type="text" name="two" placeholder="请输入名称" class="layui-input">
								</div>
								<div class="layui-inline">
									<button type="button" class="layui-btn layuiadmin-btn-useradmin" onclick="examTypeQuery();">
										<i class="layui-icon layuiadmin-button-btn"></i>查询
									</button>
									<button type="button" class="layui-btn layui-btn-primary layuiadmin-btn-useradmin" onclick="examTypeReset();">
										<i class="layui-icon layuiadmin-button-btn"></i>重置
									</button>
								</div>
							</div>
						</form>
						<div class="layui-card-body">
							<div style="padding-bottom: 10px;">
								<my:auth url="examType/toAdd"><button class="layui-btn layuiadmin-btn-useradmin" onclick="toExamTypeAdd();">添加</button></my:auth>
							</div>
							<script type="text/html" id="examTypeToolbar">
								<my:auth url="examType/toEdit"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="examTypeEdit"><i class="layui-icon layui-icon-edit"></i>修改</a></my:auth>
								<my:auth url="examType/toMove"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="examTypeMove"><i class="layui-icon layui-icon-edit"></i>移动</a></my:auth>
								<my:auth url="examType/toAuth"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="examTypeAuth"><i class="layui-icon layui-icon-edit"></i>操作权限</a></my:auth>
								<my:auth url="examType/doDel"><a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="examTypeDel"><i class="layui-icon layui-icon-delete"></i>删除</a></my:auth>
							</script>
							<%-- 试题分类数据表格 --%>
							<table id="examTypeTable" lay-filter="examTypeTable"></table>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
	<%@include file="/script/myJs/tail.jspf"%>
	<script type="text/javascript">
		//定义变量
		var examTypeQueryForm = $("#examTypeQueryForm"); //试题分类查询对象
		var examTypeTree; //试题分类树对象
		var curSelExamTypeId = ""; //当前选中的试题分类ID
		var curSelExamTypeName = ""; //当前选中的试题分类名称
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initExamTypeTable();
			initExamTypeTree();
		});
	
		//初始化试题分类表格
		function initExamTypeTable() {
			layui.table.render({
				elem : "#examTypeTable",
				url : "examType/list",
				cols : [[
						{field : "NAME", title : "名称", align : "center"},
						{field : "USER_NAMES", title : "用户权限", align : "center"},
						{field : "ORG_NAMES", title : "机构权限", align : "center"},
						{field : "POST_NAMES", title : "岗位权限", align : "center"},
						{field : "PARENT_NAME", title : "上级试题分类 ", align : "center"},
						{field : "NO", title : "排序", align : "center"},
						{fixed: 'right', title : "操作 ", toolbar : "#examTypeToolbar", align : "center", width : 300}
						]],
				page : true,
				height : "full-180",
				method : "post",
				defaultToolbar : [],
				parseData: function(examType){
					return {
						"code" : examType.succ,
						"msg" : examType.msg,
						"count" : examType.data.total,
						"data" : examType.data.rows
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
			layui.table.on("rowDouble(examTypeTable)", function(obj){
				<my:auth url="examType/toEdit">toExamTypeEdit(obj.data.ID);</my:auth>
			});
			layui.table.on("tool(examTypeTable)", function(obj){
				var data = obj.data;
				if(obj.event === "examTypeEdit") {
					toExamTypeEdit(obj.data.ID);
				} else if(obj.event === "examTypeMove") {
					toExamTypeMove(obj.data);
				} else if(obj.event === "examTypeDel") {
					doExamTypeDel(obj.data.ID);
				} else if(obj.event === "examTypeAuth") {
					toExamTypeAuth(obj.data.ID);
				}
			});
		}
		
		//初始化试题分类树
		function initExamTypeTree() {
			examTypeTree = $.fn.zTree.init($("#examTypeTree"), {
				async : {
					url : "examType/treeList",
					enable : true,
					dataFilter : ztreeDataFilter
				},
				callback : {
					onClick : function(event, treeId, treeNode) {
						curSelExamTypeId = treeNode.ID;
						curSelExamTypeName = treeNode.NAME;
						$("#examTypeOne").val(curSelExamTypeId);
						examTypeQuery();
					},
					onAsyncSuccess : function(event, treeId, msg, treeNode) {
						var examTypeTree = $.fn.zTree.getZTreeObj(treeId);
						examTypeTree.expandAll(true);
						
						if (!curSelExamTypeId) {
							var rootNode = examTypeTree.getNodesByFilter(function(node) { return (node.level == 0); }, true);
							examTypeTree.selectNode(rootNode);
							
							curSelExamTypeId = rootNode.ID;
							curSelExamTypeName = rootNode.NAME;
							$("#examTypeOne").val(curSelExamTypeId);
							return;
						}
						
						var curNode = examTypeTree.getNodeByParam("id", curSelExamTypeId, null);
						examTypeTree.selectNode(curNode);
						
						examTypeQuery();
					}
				}
			});
			
			$("#examTypeTree").height($(window).height() - 45);
		}
		
		//试题分类查询
		function examTypeQuery() {
			layui.table.reload("examTypeTable", {"where" : $.fn.my.serializeObj(examTypeQueryForm)});
		}
	
		//试题分类重置
		function examTypeReset() {
			examTypeQueryForm[0].reset();
			examTypeQuery();
		}
		
		//到达添加试题分类页面
		function toExamTypeAdd() {
			if(!curSelExamTypeId){
				layer.alert("请选择上级试题分类！", {"title" : "提示消息"});
				return;
			}
			
			$.ajax({
				url : "examType/toAdd",
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "添加试题分类",
						area : ["800px", "500px"],
						content : obj,
						btn : ["添加", "取消"],
						type : 1,
						yes : function(index, layero){
							doExamTypeAdd(index);
						},
						success: function(layero, index){
							$("#parentExamTypeId").val(curSelExamTypeId);
							$("#parentExamTypeName").val(curSelExamTypeName);
							layui.form.render(null, "examTypeEditFrom");
						}
					});
				}
			});
		}

		//完成添加试题分类
		function doExamTypeAdd(examTypeAddDialogIndex) {
			layui.form.on("submit(examTypeEditBtn)", function(data) {
				layer.confirm("确定要添加？", function(index) {
					$.ajax({
						url : "examType/doAdd",
						data : data.field,
						success : function(obj) {
							examTypeTreeFlush();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
							layer.close(examTypeAddDialogIndex);
						}
					});
				});
			});
			$("[lay-filter='examTypeEditBtn']").click();
		}
		
		//到达修改试题分类页面
		function toExamTypeEdit(id) {
			$.ajax({
				url : "examType/toEdit",
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
							doExamTypeEdit(index);
						},
						success: function(layero, index){
							layui.form.render(null, "examTypeEditFrom");
						}
					});
				}
			});
		}
		
		//完成修改试题分类
		function doExamTypeEdit(examTypeEditDialogIndex) {
			layui.form.on("submit(examTypeEditBtn)", function(data) {
				layer.confirm("确定要修改？", function(index) {
					$.ajax({
						url : "examType/doEdit",
						data : data.field,
						success : function(obj) {
							examTypeTreeFlush();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
							layer.close(examTypeEditDialogIndex);
						}
					});
				});
			});
			$("[lay-filter='examTypeEditBtn']").click();
		}

		//完成删除试题分类
		function doExamTypeDel(id) {
			layer.confirm("确定要删除？", function(index) {
				$.ajax({
					url : "examType/doDel",
					data : {id : id},
					success : function(obj) {
						examTypeTreeFlush();
						
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
		function toExamTypeMove(examTypeObj){
			$.ajax({
				url : "examType/toMove",
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "选择试题分类",
						area : ["300px", "500px"],
						content : obj,
						btn : ["移动", "取消"],
						type : 1,
						yes : function(index, layero){
							var examTypeMoveTreeObj = $.fn.zTree.getZTreeObj("examTypeMoveTree");
							var examTypeMoveNodes = examTypeMoveTreeObj.getSelectedNodes();
							if (examTypeMoveNodes.length != 1) {
								layer.alert("请选择一行数据！", {"title" : "提示消息"});
								return;
							}
							
							var sourceId = examTypeObj.ID;
							var sourceName = examTypeObj.NAME;
							var targetId = examTypeMoveNodes[0].ID;
							var targetName = examTypeMoveNodes[0].NAME;
							if(sourceId == targetId){
								layer.alert("源试题分类和目标试题分类一致！", {"title" : "提示消息"});
								return;
							}
							if(examTypeMoveNodes[0].PARENT_SUB.indexOf(examTypeObj.PARENT_SUB) >= 0){
								layer.alert("父试题分类不能移动到子试题分类下！", {"title" : "提示消息"});
								return;
							}
							
							doExamTypeMove(sourceId, sourceName, targetId, targetName, index);
						},
						success: function(layero, index) {
							$.fn.zTree.init($("#examTypeMoveTree"), {
								async : {
									url : "examType/treeList",
									otherParam : {
										
									},
									enable : true,
									dataFilter : ztreeDataFilter
								},
								callback : {
									onAsyncSuccess : function(event, treeId, msg, treeNode) {
										var rootNode = examTypeTree.getNodesByFilter(function(node) { return (node.level == 0); }, true);
										examTypeTree.selectNode(rootNode);
									}
								}
							});
						}
					});
				}
			});
		}
		
		//完成移动试题分类
		function doExamTypeMove(sourceId, sourceName, targetId, targetName, examTypeDialogIndex){
			layer.confirm("确定要移动【"+sourceName+"】到【"+targetName+"】？", {title : "确认消息"}, function(index){
				var params = {"sourceId" : sourceId, "targetId" : targetId};
				$.ajax({
					url : "examType/doMove",
					data : params,
					success : function(obj){
						examTypeTreeFlush();
						
						if(!obj.succ){
							layer.alert(obj.msg, {"title" : "提示消息"});
							return;
						}
						
						layer.close(index);
						layer.close(examTypeDialogIndex);
					}
				});
			});
			$("[lay-filter='examTypeMoveBtn']").click();
		}
		
		//刷新试题分类
		function examTypeTreeFlush() {
			examTypeTree.reAsyncChildNodes(null, "refresh");
		}
		
		//到达授权试题分类页面
		function toExamTypeAuth(id) {
			$.ajax({
				url : "examType/toAuth",
				data : {"id" : id},
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "授权试题分类",
						area : ["800px", "500px"],
						content : obj,
						btn : ["授权", "授权并同步到子分类", "取消"],
						type : 1,
						yes : function(index, layero){
							doExamTypeAuth(index, false);
						},
						btn2: function(index, layero){
							doExamTypeAuth(index, true);
							return false;
						},
						success: function(layero, index){
							var userIdSelect = xmSelect.render({
								el : "#userIds",
								name : "userIds",
								autoRow : true,
								toolbar : { show: true },
								filterable : true,
								remoteSearch : true,
								tips : "请输入",
								searchTips : "请输入",
								remoteMethod : function(val, cb, show){
									if(!val){
										return cb([]);
									}
									
									$.ajax({
										url : "examType/authUserList",
										data : {
											two : val
										},
										async : true,
										success : function(obj) {
											var users = [];
											for (var i in obj.data.rows) {
												users.push({"name" : obj.data.rows[i].NAME+"-"+obj.data.rows[i].ORG_NAME, value : obj.data.rows[i].ID});
											}
											cb(users);
										}
									});
								},
							});
							
							$.ajax({
								url : "examType/authUserList",
								data : {
									ten : id
								},
								async : true,
								success : function(obj) {
									var users = [];
									for (var i in obj.data.rows) {
										users.push({"name" : obj.data.rows[i].NAME+"-"+obj.data.rows[i].ORG_NAME, value : obj.data.rows[i].ID, selected : true});
									}
									
									userIdSelect.update({
										data: users
									})
								}
							});
							
							var postIdSelect = xmSelect.render({
								el : "#postIds",
								name : "postIds",
								autoRow : true,
								toolbar : { show: true },
								filterable : true,
								remoteSearch : true,
								tips : "请输入",
								searchTips : "请输入",
								remoteMethod : function(val, cb, show){
									if(!val){
										return cb([]);
									}
									
									$.ajax({
										url : "examType/authPostList",
										data : {
											two : val
										},
										async : true,
										success : function(obj) {
											var posts = [];
											for (var i in obj.data.rows) {
												posts.push({"name" : obj.data.rows[i].NAME+"-"+obj.data.rows[i].ORG_NAME, value : obj.data.rows[i].ID});
											}
											cb(posts);
										}
									});
								},
							});
							
							$.ajax({
								url : "examType/authPostList",
								data : {
									ten : id
								},
								async : true,
								success : function(obj) {
									var posts = [];
									for (var i in obj.data.rows) {
										posts.push({"name" : obj.data.rows[i].NAME+"-"+obj.data.rows[i].ORG_NAME, value : obj.data.rows[i].ID, selected : true});
									}
									
									postIdSelect.update({
										data: posts
									})
								}
							});
							
							var orgIdSelect = xmSelect.render({
								el : "#orgIds",
								name : "orgIds",
								autoRow : true,
								toolbar : { show: true },
								filterable : true,
								remoteSearch : true,
								tips : "请输入",
								searchTips : "请输入",
								remoteMethod : function(val, cb, show){
									if(!val){
										return cb([]);
									}
									
									$.ajax({
										url : "examType/authOrgList",
										data : {
											two : val
										},
										async : true,
										success : function(obj) {
											var orgs = [];
											for (var i in obj.data.rows) {
												orgs.push({"name" : obj.data.rows[i].NAME+"-"+obj.data.rows[i].PARENT_ORG_NAME, value : obj.data.rows[i].ID});
											}
											cb(orgs);
										}
									});
								},
							});
							
							$.ajax({
								url : "examType/authOrgList",
								data : {
									ten : id
								},
								async : true,
								success : function(obj) {
									var orgs = [];
									for (var i in obj.data.rows) {
										orgs.push({"name" : obj.data.rows[i].NAME+"-"+obj.data.rows[i].PARENT_ORG_NAME, value : obj.data.rows[i].ID, selected : true});
									}
									
									orgIdSelect.update({
										data: orgs
									})
								}
							});
							
							layui.form.render(null, "authUserQueryForm");
						}
					});
				}
			});
		}
		
		// 完成授权试题分类
		function doExamTypeAuth(examTypeAuthDialogIndex, syn2Sub) {
			layui.form.on("submit(examTypeAuthBtn)", function(data) {
				layer.confirm("确定要授权？", function(index) {
					data.field.syn2Sub = syn2Sub;
					$.ajax({
						url : "examType/doAuth",
						data : data.field,
						success : function(obj) {
							examTypeTreeFlush();
							
							if(!obj.succ){
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
							layer.close(examTypeAuthDialogIndex);
						}
					});
				});
			});
			$("[lay-filter='examTypeAuthBtn']").click();
		}
	</script>
</html>
