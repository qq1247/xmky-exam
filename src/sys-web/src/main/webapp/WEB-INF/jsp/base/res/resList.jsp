<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>资源列表</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
		<div class="layui-fluid">
			<div class="layui-row layui-col-space10">
				<div class="layui-col-md2">
					<div class="layui-card">
						<div class="layui-form">
							<select lay-filter="resTreeBtn">
			     				<option value="1">后台</option>
								<option value="2">前台</option>
			      			</select>
			      			<ul id="resTree" class="ztree" style="overflow: auto;"></ul>
			 			</div>
					</div>
				</div>
				<div class="layui-col-md10">
					<div class="layui-card">
						<%-- 资源查询条件 --%>
						<form id="resQueryForm" class="layui-form layui-card-header layuiadmin-card-header-auto">
							<input type="hidden" id="resOne" name="one">
							<input type="hidden" id="resTwo" name="two">
							<div class="layui-form-item ">
								<div class="layui-inline">
									<input type="text" name="two" placeholder="请输入名称" class="layui-input">
								</div>
								<div class="layui-inline">
									<input type="text" name="three" placeholder="请输入url" class="layui-input">
								</div>
								<div class="layui-inline">
									<button type="button" class="layui-btn layuiadmin-btn-useradmin" onclick="resQuery();">
										<i class="layui-icon layuiadmin-button-btn"></i>查询
									</button>
									<button type="button" class="layui-btn layui-btn-primary layuiadmin-btn-useradmin" onclick="resReset();">
										<i class="layui-icon layuiadmin-button-btn"></i>重置
									</button>
								</div>
							</div>
						</form>
						<div class="layui-card-body">
							<div style="padding-bottom: 10px;">
								<my:auth url="res/toAdd"><button class="layui-btn layuiadmin-btn-useradmin" onclick="toResAdd();">添加</button></my:auth>
							</div>
						    <script type="text/html" id=resToolbar>
								<my:auth url="res/toEdit"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="resEdit"><i class="layui-icon layui-icon-edit"></i>修改</a></my:auth>
								<my:auth url="res/toMove"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="resMove"><i class="layui-icon layui-icon-edit"></i>移动</a></my:auth>
								<my:auth url="res/doDel"><a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="resDel"><i class="layui-icon layui-icon-delete"></i>删除</a></my:auth>
							</script>
							<%-- 资源数据表格 --%>
							<table id="resTable" lay-filter="resTable"></table>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
	<%@include file="/script/myJs/tail.jspf"%>
	<script type="text/javascript">
		//定义变量
		var resQueryForm = $("#resQueryForm"); //资源查询对象
		var resTree; //资源树对象
		var curSelResId = ""; //当前选中的资源ID
		var curSelResName = ""; //当前选中的资源名称
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initResTable();
			initResTree();
		});
	
		//初始化资源表格
		function initResTable() {
			layui.table.render({
				elem : "#resTable",
				url : "res/list",
				cols : [[ 
						{field : "NAME", title : "名称", align : "center"},
						{field : "URL", title : "ulr", align : "center"},
						{field : "LEVEL", title : "层级", align : "center"},
						{field : "ICON", title : "图标", align : "center"},
						{field : "NO", title : "排序", align : "center"},
						{field : "PARENT_NAME", title : "上级资源 ", align : "center"},
						{fixed : "right", title : "操作 ", toolbar : "#resToolbar", align : "center", width : 220}
						]],
				page : true,
				height : "full-180",
				method : "post",
				defaultToolbar : [],
				parseData : function(obj) {
					return {
						"code" : obj.succ,
						"msg" : obj.msg,
						"count" : obj.data.total,
						"data" : obj.data.rows
					};
				},
				request : {
					pageName: "curPage",
					limitName: "pageSize"
				}, 
				response : {
					statusCode : true
				}
			});
			layui.table.on("rowDouble(resTable)", function(obj) {
				<my:auth url="res/toEdit">toResEdit(obj.data.ID);</my:auth>
			});
			layui.table.on("tool(resTable)", function(obj) {
				var data = obj.data;
				if (obj.event === "resEdit") {
					toResEdit(obj.data.ID);
				} else if (obj.event === "resMove") {
					toResMove(obj.data);
				} else if (obj.event === "resDel") {
					doResDel(obj.data.ID);
				}
			});
		}
		
		//初始化资源树
		function initResTree() {
			resTree = $.fn.zTree.init($("#resTree"), {
				async : {
					url : "res/treeList",
					otherParam : {
						type : function() {
							return $("[lay-filter='resTreeBtn']").parent().find("dd[class='layui-this']").attr("lay-value");
						}
					},
					enable : true,
					dataFilter : ztreeDataFilter
				},
				callback : {
					onClick : function(event, treeId, treeNode) {
						curSelResId = treeNode.ID;
						curSelResName = treeNode.NAME;
						$("#resOne").val(curSelResId);
						resQuery();
					},
					onAsyncSuccess : function(event, treeId, msg, treeNode) {
						var resTree = $.fn.zTree.getZTreeObj(treeId);
						resTree.expandAll(true);
						
						if (!curSelResId) {
							var rootNode = resTree.getNodesByFilter(function(node) { return (node.level == 0); }, true);
							resTree.selectNode(rootNode);
							
							curSelResId = rootNode.ID;
							curSelResName = rootNode.NAME;
							$("#resOne").val(curSelResId);
							return;
						}
						
						var curNode = resTree.getNodeByParam("id", curSelResId, null);
						resTree.selectNode(curNode);
						
						resQuery();
					}
				}
			});
			
			layui.form.on("select(resTreeBtn)", function(data) {
				$("#resTwo").val(data.value);
				resTreeFlush();
			});
			
			$("#resTree").height($(window).height() - 80);
		}
		
		//资源查询
		function resQuery() {
			layui.table.reload("resTable", {"where" : $.fn.my.serializeObj(resQueryForm)});
		}
	
		//资源重置
		function resReset() {
			resQueryForm[0].reset();
			resQuery();
		}
		
		//到达添加资源页面
		function toResAdd() {
			if (!curSelResId) {
				layer.alert("请选择上级资源！", {"title" : "提示消息"});
				return;
			}
			
			$.ajax({
				url : "res/toAdd",
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "添加资源",
						area : ["800px", "500px"],
						content : obj,
						btn : ["添加", "取消"],
						type : 1,
						yes : function(index, layero) {
							doResAdd(index);
						},
						success : function(layero, index) {
							$("#parentResId").val(curSelResId);
							$("#parentResName").val(curSelResName);
							$("#resType").val($("[lay-filter='resTreeBtn']").parent().find("dd[class='layui-this']").attr("lay-value"));
							layui.form.render(null, "resEditFrom");
						}
					});
				}
			});
		}

		//完成添加资源
		function doResAdd(resAddDialogIndex) {
			layui.form.on("submit(resEditBtn)", function(data) {
				layer.confirm("确定要添加？", function(index) {
					$.ajax({
						url : "res/doAdd",
						data : data.field,
						success : function(obj) {
							resTreeFlush();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
							layer.close(resAddDialogIndex);
						}
					});
				});
			});
			$("[lay-filter='resEditBtn']").click();
		}
		
		//到达修改资源页面
		function toResEdit(id) {
			$.ajax({
				url : "res/toEdit",
				data : {id : id},
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "修改资源",
						area : ["800px", "500px"],
						content : obj,
						btn : ["修改", "取消"],
						type : 1,
						yes : function(index, layero) {
							doResEdit(index);
						},
						success: function(layero, index) {
							layui.form.render(null, "resEditFrom");
						}
					});
				}
			});
		}

		//完成修改资源
		function doResEdit(resEditDialogIndex) {
			layui.form.on("submit(resEditBtn)", function(data) {
				layer.confirm("确定要修改？", function(index) {
					$.ajax({
						url : "res/doEdit",
						data : data.field,
						success : function(obj) {
							resTreeFlush();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
							layer.close(resEditDialogIndex);
						}
					});
				});
			});
			$("[lay-filter='resEditBtn']").click();
		}

		//完成删除资源
		function doResDel(id) {
			layer.confirm("确定要删除？", function(index) {
				$.ajax({
					url : "res/doDel",
					data : {id : id},
					success : function(obj) {
						resTreeFlush();
						
						if (!obj.succ) {
							layer.alert(obj.msg, {"title" : "提示消息"});
							return;
						}
						
						layer.close(index);
					}
				});
			});
		}
		
		//到达移动资源页面
		function toResMove(resObj) {
			$.ajax({
				url : "res/toMove",
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "选择资源",
						area : ["300px", "500px"],
						content : obj,
						btn : ["移动", "取消"],
						type : 1,
						yes : function(index, layero) {
							var resMoveTreeObj = $.fn.zTree.getZTreeObj("resMoveTree");
							var resMoveNodes = resMoveTreeObj.getSelectedNodes();
							if (resMoveNodes.length != 1) {
								layer.alert("请选择一行数据！", {"title" : "提示消息"});
								return;
							}
							
							var sourceId = resObj.ID;
							var sourceName = resObj.NAME;
							var targetId = resMoveNodes[0].ID;
							var targetName = resMoveNodes[0].NAME;
							if (sourceId == targetId) {
								layer.alert("源资源和目标资源一致！", {"title" : "提示消息"});
								return;
							}
							if (resMoveNodes[0].PARENT_SUB.indexOf(resObj.PARENT_SUB) >= 0) {
								layer.alert("父资源不能移动到子资源下！", {"title" : "提示消息"});
								return;
							}
							
							doResMove(sourceId, sourceName, targetId, targetName, index);
						},
						success: function(layero, index) {
							$.fn.zTree.init($("#resMoveTree"), {
								async : {
									url : "res/treeList",
									otherParam : {
										type : resObj.TYPE
									},
									enable : true,
									dataFilter : ztreeDataFilter
								},
								callback : {
									onAsyncSuccess : function(event, treeId, msg, treeNode) {
										var rootNode = resTree.getNodesByFilter(function(node) { return (node.level == 0); }, true);
										resTree.selectNode(rootNode);
									}
								}
							});
						}
					});
				}
			});
		}
		
		//完成移动资源
		function doResMove(sourceId, sourceName, targetId, targetName, resDialogIndex) {
			layer.confirm("确定要移动【"+sourceName+"】到【"+targetName+"】？", {title : "提示消息"}, function(index) {
				$.ajax({
					url : "res/doMove",
					data : {
						sourceId : sourceId, 
						targetId : targetId
					},
					success : function(obj) {
						resTreeFlush();
						
						if (!obj.succ) {
							layer.alert(obj.msg, {"title" : "提示消息"});
							return;
						}
						
						layer.close(index);
						layer.close(resDialogIndex);
					}
				});
			});
			$("[lay-filter='resMoveBtn']").click();
		}
		
		//刷新资源
		function resTreeFlush() {
			resTree.reAsyncChildNodes(null, "refresh");
		}
	</script>
</html>
