<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>数据字典列表</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
		<div class="layui-fluid">
			<div class="layui-card">
				<%-- 数据字典查询条件 --%>
				<form id="dictQueryForm" class="layui-form layui-card-header layuiadmin-card-header-auto">
					<div class="layui-form-item">
						<div class="layui-inline">
							<input type="text" name="two" placeholder="请输入索引" class="layui-input">
						</div>
						<div class="layui-inline">
							<input type="text" name="three" placeholder="请输入键" class="layui-input">
						</div>
						<div class="layui-inline">
							<input type="text" name="four" placeholder="请输入值" class="layui-input">
						</div>
						<div class="layui-inline">
							<button type="button" class="layui-btn layuiadmin-btn-useradmin" onclick="dictQuery();">
								<i class="layui-icon layuiadmin-button-btn"></i>查询
							</button>
							<button type="button" class="layui-btn layui-btn-primary layuiadmin-btn-useradmin" onclick="dictReset();">
								<i class="layui-icon layuiadmin-button-btn"></i>重置
							</button>
						</div>
					</div>
				</form>
				<div class="layui-card-body">
					<div style="padding-bottom: 10px;">
						<my:auth url="dict/toAdd"><button class="layui-btn layuiadmin-btn-useradmin" onclick="toDictAdd();">添加</button></my:auth>
					</div>
					<script type="text/html" id="dictToolbar">
						<my:auth url="dict/toEdit"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="dictEdit"><i class="layui-icon layui-icon-edit"></i>修改</a></my:auth>
						<my:auth url="dict/doDel"><a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="dictDel"><i class="layui-icon layui-icon-delete"></i>删除</a></my:auth>
					</script>
					<%-- 数据字典数据表格 --%>
					<table id="dictTable" lay-filter="dictTable"></table>
				</div>
			</div>
		</div>
	</body>
	<%@include file="/script/myJs/tail.jspf"%>
	<script type="text/javascript">
		//定义变量
		var dictQueryForm = $("#dictQueryForm"); //数据字典查询对象
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initDictTable();
		});
	
		//初始化数据字典表格
		function initDictTable() {
			layui.table.render({
				elem : "#dictTable",
				url : "dict/list",
				cols : [[ 
						{field : "DICT_INDEX", title : "索引"},
						{field : "DICT_KEY", title : "键", align : "center"},
						{field : "DICT_VALUE", title : "值", align : "center"},
						{field : "NO", title : "排序 ", align : "center"},
						{fixed: 'right', title : "操作 ", toolbar : "#dictToolbar", align : "center"}
						]],
				page : true,
				height : "full-180",
				method : "post",
				defaultToolbar : [],
				parseData: function(dict){
					return {
						"code" : dict.succ,
						"msg" : dict.msg,
						"count" : dict.data.total,
						"data" : dict.data.rows
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
			layui.table.on("rowDouble(dictTable)", function(obj){
				<my:auth url="dict/toEdit">toDictEdit(obj.data.ID);</my:auth>
			});
			layui.table.on("tool(dictTable)", function(obj){
				var data = obj.data;
				if(obj.event === "dictEdit") {
					toDictEdit(obj.data.ID);
				} else if(obj.event === "dictDel") {
					doDictDel(obj.data.ID);
				}
			});
		}
		
		//数据字典查询
		function dictQuery() {
			layui.table.reload("dictTable", {"where" : $.fn.my.serializeObj(dictQueryForm)});
		}
	
		//数据字典重置
		function dictReset() {
			dictQueryForm[0].reset();
			dictQuery();
		}
		
		//到达添加数据字典页面
		function toDictAdd() {
			$.ajax({
				url : "dict/toAdd",
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "添加数据字典",
						area : ["800px", "500px"],
						content : obj,
						btn : ["添加", "取消"],
						type : 1,
						yes : function(index, layero){
							doDictAdd(index);
						},
						success: function(layero, index){
							layui.form.render(null, "dictEditFrom");
						}
					});
				}
			});
		}

		//完成添加数据字典
		function doDictAdd(dictAddDialogIndex) {
			layui.form.on("submit(dictEditBtn)", function(data) {
				layer.confirm("确定要添加？", function(index) {
					$.ajax({
						url : "dict/doAdd",
						data : data.field,
						success : function(obj) {
							dictQuery();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
							layer.close(dictAddDialogIndex);
						}
					});
				});
			});
			$("[lay-filter='dictEditBtn']").click();
		}
		
		//到达修改数据字典页面
		function toDictEdit(id) {
			$.ajax({
				url : "dict/toEdit",
				data : {id : id},
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "修改数据字典",
						area : ["800px", "500px"],
						content : obj,
						btn : ["修改", "取消"],
						type : 1,
						yes : function(index, layero){
							doDictEdit(index);
						},
						success: function(layero, index){
							layui.form.render(null, "dictEditFrom");
						}
					});
				}
			});
		}
		
		//完成修改数据字典
		function doDictEdit(dictEditDialogIndex) {
			layui.form.on("submit(dictEditBtn)", function(data) {
				layer.confirm("确定要修改？", function(index) {
					$.ajax({
						url : "dict/doEdit",
						data : data.field,
						success : function(obj) {
							dictQuery();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
							layer.close(dictEditDialogIndex);
						}
					});
				});
			});
			$("[lay-filter='dictEditBtn']").click();
		}
		

		//完成删除数据字典
		function doDictDel(id) {
			layer.confirm("确定要删除？", function(index) {
				$.ajax({
					url : "dict/doDel",
					data : {id : id},
					success : function(obj) {
						dictQuery();
						
						if (!obj.succ) {
							layer.alert(obj.msg, {"title" : "提示消息"});
							return;
						}
						
						layer.close(index);
					}
				});
			});
		}
	</script>
</html>
