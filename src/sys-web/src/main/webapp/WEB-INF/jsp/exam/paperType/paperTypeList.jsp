<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>试卷分类列表</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
		<div class="layui-fluid">
			<div class="layui-row layui-col-space10">
				<div class="layui-col-md2">
					<div class="layui-card">
						<div class="layui-form">
			      			<ul id="paperTypeTree" class="ztree"></ul>
			 			</div>
					</div>
				</div>
				<div class="layui-col-md10">
					<div class="layui-card">
						<%-- 试卷分类查询条件 --%>
						<form id="paperTypeQueryForm" class="layui-form layui-card-header layuiadmin-card-header-auto">
							<input type="hidden" id="paperTypeOne" name="one">
							<div class="layui-form-item">
								<div class="layui-inline">
									<input type="text" name="two" placeholder="请输入名称" class="layui-input">
								</div>
								<div class="layui-inline">
									<button type="button" class="layui-btn layuiadmin-btn-useradmin" onclick="paperTypeQuery();">
										<i class="layui-icon layuiadmin-button-btn"></i>查询
									</button>
									<button type="button" class="layui-btn layui-btn-primary layuiadmin-btn-useradmin" onclick="paperTypeReset();">
										<i class="layui-icon layuiadmin-button-btn"></i>重置
									</button>
								</div>
							</div>
						</form>
						<div class="layui-card-body">
							<div style="padding-bottom: 10px;">
								<my:auth url="paperType/toAdd"><button class="layui-btn layuiadmin-btn-useradmin" onclick="toPaperTypeAdd();">添加</button></my:auth>
							</div>
							<script type="text/html" id="paperTypeToolbar">
								<my:auth url="paperType/toEdit"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="paperTypeEdit"><i class="layui-icon layui-icon-edit"></i>修改</a></my:auth>
								<my:auth url="paperType/toMove"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="paperTypeMove"><i class="layui-icon layui-icon-edit"></i>移动</a></my:auth>
								<my:auth url="paperType/toAuth"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="paperTypeAuth"><i class="layui-icon layui-icon-edit"></i>操作权限</a></my:auth>
								<my:auth url="paperType/doDel"><a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="paperTypeDel"><i class="layui-icon layui-icon-delete"></i>删除</a></my:auth>
							</script>
							<%-- 试卷分类数据表格 --%>
							<table id="paperTypeTable" lay-filter="paperTypeTable"></table>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
	<%@include file="/script/myJs/tail.jspf"%>
	<script type="text/javascript">
		//定义变量
		var paperTypeQueryForm = $("#paperTypeQueryForm"); //试卷分类查询对象
		var paperTypeTree; //试卷分类树对象
		var curSelPaperTypeId = ""; //当前选中的试卷分类ID
		var curSelPaperTypeName = ""; //当前选中的试卷分类名称
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initPaperTypeTable();
			initPaperTypeTree();
		});
	
		//初始化试卷分类表格
		function initPaperTypeTable() {
			layui.table.render({
				elem : "#paperTypeTable",
				url : "paperType/list",
				cols : [[
						{field : "NAME", title : "名称", align : "center"},
						{field : "USER_NAMES", title : "用户权限", align : "center"},
						{field : "ORG_NAMES", title : "机构权限", align : "center"},
						{field : "POST_NAMES", title : "岗位权限", align : "center"},
						{field : "PARENT_NAME", title : "上级试卷分类 ", align : "center"},
						{field : "NO", title : "排序", align : "center"},
						{fixed: 'right', title : "操作 ", toolbar : "#paperTypeToolbar", align : "center", width : 300}
						]],
				page : true,
				height : "full-180",
				method : "post",
				defaultToolbar : [],
				parseData: function(paperType){
					return {
						"code" : paperType.succ,
						"msg" : paperType.msg,
						"count" : paperType.data.total,
						"data" : paperType.data.rows
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
			layui.table.on("rowDouble(paperTypeTable)", function(obj){
				<my:auth url="paperType/toEdit">toPaperTypeEdit(obj.data.ID);</my:auth>
			});
			layui.table.on("tool(paperTypeTable)", function(obj){
				var data = obj.data;
				if(obj.event === "paperTypeEdit") {
					toPaperTypeEdit(obj.data.ID);
				} else if(obj.event === "paperTypeMove") {
					toPaperTypeMove(obj.data);
				} else if(obj.event === "paperTypeDel") {
					doPaperTypeDel(obj.data.ID);
				} else if(obj.event === "paperTypeAuth") {
					toPaperTypeAuth(obj.data.ID);
				}
			});
		}
		
		//初始化试卷分类树
		function initPaperTypeTree() {
			paperTypeTree = $.fn.zTree.init($("#paperTypeTree"), {
				async : {
					url : "paperType/treeList",
					enable : true,
					dataFilter : ztreeDataFilter
				},
				callback : {
					onClick : function(event, treeId, treeNode) {
						curSelPaperTypeId = treeNode.ID;
						curSelPaperTypeName = treeNode.NAME;
						$("#paperTypeOne").val(curSelPaperTypeId);
						paperTypeQuery();
					},
					onAsyncSuccess : function(event, treeId, msg, treeNode) {
						var paperTypeTree = $.fn.zTree.getZTreeObj(treeId);
						paperTypeTree.expandAll(true);
						
						if (!curSelPaperTypeId) {
							var rootNode = paperTypeTree.getNodesByFilter(function(node) { return (node.level == 0); }, true);
							paperTypeTree.selectNode(rootNode);
							
							curSelPaperTypeId = rootNode.ID;
							curSelPaperTypeName = rootNode.NAME;
							$("#paperTypeOne").val(curSelPaperTypeId);
							return;
						}
						
						var curNode = paperTypeTree.getNodeByParam("id", curSelPaperTypeId, null);
						paperTypeTree.selectNode(curNode);
						
						paperTypeQuery();
					}
				}
			});
			
			$("#paperTypeTree").height($(window).height() - 45);
		}
		
		//试卷分类查询
		function paperTypeQuery() {
			layui.table.reload("paperTypeTable", {"where" : $.fn.my.serializeObj(paperTypeQueryForm)});
		}
	
		//试卷分类重置
		function paperTypeReset() {
			paperTypeQueryForm[0].reset();
			paperTypeQuery();
		}
		
		//到达添加试卷分类页面
		function toPaperTypeAdd() {
			if(!curSelPaperTypeId){
				layer.alert("请选择上级试卷分类！", {"title" : "提示消息"});
				return;
			}
			
			$.ajax({
				url : "paperType/toAdd",
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "添加试卷分类",
						area : ["800px", "500px"],
						content : obj,
						btn : ["添加", "取消"],
						type : 1,
						yes : function(index, layero){
							doPaperTypeAdd(index);
						},
						success: function(layero, index){
							$("#parentPaperTypeId").val(curSelPaperTypeId);
							$("#parentPaperTypeName").val(curSelPaperTypeName);
							layui.form.render(null, "paperTypeEditFrom");
						}
					});
				}
			});
		}

		//完成添加试卷分类
		function doPaperTypeAdd(paperTypeAddDialogIndex) {
			layui.form.on("submit(paperTypeEditBtn)", function(data) {
				layer.confirm("确定要添加？", function(index) {
					$.ajax({
						url : "paperType/doAdd",
						data : data.field,
						success : function(obj) {
							paperTypeTreeFlush();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
							layer.close(paperTypeAddDialogIndex);
						}
					});
				});
			});
			$("[lay-filter='paperTypeEditBtn']").click();
		}
		
		//到达修改试卷分类页面
		function toPaperTypeEdit(id) {
			$.ajax({
				url : "paperType/toEdit",
				data : {"id" : id},
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "修改试卷分类",
						area : ["800px", "500px"],
						content : obj,
						btn : ["修改", "取消"],
						type : 1,
						yes : function(index, layero){
							doPaperTypeEdit(index);
						},
						success: function(layero, index){
							layui.form.render(null, "paperTypeEditFrom");
						}
					});
				}
			});
		}
		
		//完成修改试卷分类
		function doPaperTypeEdit(paperTypeEditDialogIndex) {
			layui.form.on("submit(paperTypeEditBtn)", function(data) {
				layer.confirm("确定要修改？", function(index) {
					$.ajax({
						url : "paperType/doEdit",
						data : data.field,
						success : function(obj) {
							paperTypeTreeFlush();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
							layer.close(paperTypeEditDialogIndex);
						}
					});
				});
			});
			$("[lay-filter='paperTypeEditBtn']").click();
		}

		//完成删除试卷分类
		function doPaperTypeDel(id) {
			layer.confirm("确定要删除？", function(index) {
				$.ajax({
					url : "paperType/doDel",
					data : {id : id},
					success : function(obj) {
						paperTypeTreeFlush();
						
						if (!obj.succ) {
							layer.alert(obj.msg, {"title" : "提示消息"});
							return;
						}
						
						layer.close(index);
					}
				});
			});
		}
		
		//到达移动试卷分类页面
		function toPaperTypeMove(paperTypeObj){
			$.ajax({
				url : "paperType/toMove",
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "选择试卷分类",
						area : ["300px", "500px"],
						content : obj,
						btn : ["移动", "取消"],
						type : 1,
						yes : function(index, layero){
							var paperTypeMoveTreeObj = $.fn.zTree.getZTreeObj("paperTypeMoveTree");
							var paperTypeMoveNodes = paperTypeMoveTreeObj.getSelectedNodes();
							if (paperTypeMoveNodes.length != 1) {
								layer.alert("请选择一行数据！", {"title" : "提示消息"});
								return;
							}
							
							var sourceId = paperTypeObj.ID;
							var sourceName = paperTypeObj.NAME;
							var targetId = paperTypeMoveNodes[0].ID;
							var targetName = paperTypeMoveNodes[0].NAME;
							if(sourceId == targetId){
								layer.alert("源试卷分类和目标试卷分类一致！", {"title" : "提示消息"});
								return;
							}
							if(paperTypeMoveNodes[0].PARENT_SUB.indexOf(paperTypeObj.PARENT_SUB) >= 0){
								layer.alert("父试卷分类不能移动到子试卷分类下！", {"title" : "提示消息"});
								return;
							}
							
							doPaperTypeMove(sourceId, sourceName, targetId, targetName, index);
						},
						success: function(layero, index) {
							$.fn.zTree.init($("#paperTypeMoveTree"), {
								async : {
									url : "paperType/treeList",
									otherParam : {
										
									},
									enable : true,
									dataFilter : ztreeDataFilter
								},
								callback : {
									onAsyncSuccess : function(event, treeId, msg, treeNode) {
										var rootNode = paperTypeTree.getNodesByFilter(function(node) { return (node.level == 0); }, true);
										paperTypeTree.selectNode(rootNode);
									}
								}
							});
						}
					});
				}
			});
		}
		
		//完成移动试卷分类
		function doPaperTypeMove(sourceId, sourceName, targetId, targetName, paperTypeDialogIndex){
			layer.confirm("确定要移动【"+sourceName+"】到【"+targetName+"】？", {title : "确认消息"}, function(index){
				var params = {"sourceId" : sourceId, "targetId" : targetId};
				$.ajax({
					url : "paperType/doMove",
					data : params,
					success : function(obj){
						paperTypeTreeFlush();
						
						if(!obj.succ){
							layer.alert(obj.msg, {"title" : "提示消息"});
							return;
						}
						
						layer.close(index);
						layer.close(paperTypeDialogIndex);
					}
				});
			});
			$("[lay-filter='paperTypeMoveBtn']").click();
		}
		
		//刷新试卷分类
		function paperTypeTreeFlush() {
			paperTypeTree.reAsyncChildNodes(null, "refresh");
		}
		
		//到达授权试卷分类页面
		function toPaperTypeAuth(id) {
			$.ajax({
				url : "paperType/toAuth",
				data : {"id" : id},
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "授权试卷分类",
						area : ["800px", "500px"],
						content : obj,
						btn : ["授权", "授权并同步到子分类", "取消"],
						type : 1,
						yes : function(index, layero){
							doPaperTypeAuth(index, false);
						},
						btn2: function(index, layero){
							doPaperTypeAuth(index, true);
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
										url : "paperType/authUserList",
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
								url : "paperType/authUserList",
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
										url : "paperType/authPostList",
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
								url : "paperType/authPostList",
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
										url : "paperType/authOrgList",
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
								url : "paperType/authOrgList",
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
		
		// 完成授权试卷分类
		function doPaperTypeAuth(paperTypeAuthDialogIndex, syn2Sub) {
			layui.form.on("submit(paperTypeAuthBtn)", function(data) {
				layer.confirm("确定要授权？", function(index) {
					data.field.syn2Sub = syn2Sub;
					$.ajax({
						url : "paperType/doAuth",
						data : data.field,
						success : function(obj) {
							paperTypeTreeFlush();
							
							if(!obj.succ){
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
							layer.close(paperTypeAuthDialogIndex);
						}
					});
				});
			});
			$("[lay-filter='paperTypeAuthBtn']").click();
		}
	</script>
</html>
