<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>试卷列表</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
		<div class="layui-fluid">
			<div class="layui-card">
				<%-- 试卷查询条件 --%>
				<form id="paperOptionQueryForm" class="layui-form layui-card-header layuiadmin-card-header-auto">
					<div class="layui-form-item ">
						<div class="layui-inline">
							<input type="text" name="two" placeholder="请输入ID" class="layui-input">
						</div>
						<div class="layui-inline">
							<button type="button" class="layui-btn layuiadmin-btn-useradmin" onclick="paperOptionQuery();">
								<i class="layui-icon layuiadmin-button-btn"></i>查询
							</button>
							<button type="button" class="layui-btn layui-btn-primary layuiadmin-btn-useradmin" onclick="paperOptionReset();">
								<i class="layui-icon layuiadmin-button-btn"></i>重置
							</button>
						</div>
					</div>
				</form>
				<div class="layui-card-body">
					<div style="padding-bottom: 10px;">
						<my:auth url="paperOption/toAdd"><button class="layui-btn layuiadmin-btn-useradmin" onclick="toPaperOptionAdd();">添加</button></my:auth>
					</div>
					<script type="text/html" id="paperOptionToolbar">
						<my:auth url="paperOption/toEdit"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="paperOptionEdit"><i class="layui-icon layui-icon-edit"></i>修改</a></my:auth>
						<my:auth url="paperOption/doDel"><a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="paperOptionDel"><i class="layui-icon layui-icon-delete"></i>删除</a></my:auth>
					</script>
					<%-- 试卷数据表格 --%>
					<table id="paperOptionTable" lay-filter="paperOptionTable"></table>
				</div>
			</div>
		</div>
	</body>
	<%@include file="/script/myJs/tail.jspf"%>
	<script type="text/javascript">
		//定义变量
		var paperOptionQueryForm = $("#paperOptionQueryForm"); //试卷查询对象
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initPaperOptionTable();
		});
	
		//初始化试卷表格
		function initPaperOptionTable() {
			layui.table.render({
				elem : "#paperOptionTable",
				url : "paperOption/list",
				cols : [[
						{field : "QUESTION", title : "1：正常；2：乱序", align : "center"},
						{field : "QUESTION_OPTION", title : "1：正常；2：乱序", align : "center"},
						{field : "RIGHT_CLICK", title : "1：正常；2：禁用", align : "center"},
						{field : "RIGHT_COPY", title : "1：正常；2：禁用", align : "center"},
						{field : "MINIMIZE", title : "最小化警告", align : "center"},
						{field : "MINIMIZE_NUM", title : "最小化警告次数", align : "center"},
						{field : "PAPER_ID", title : "试卷ID", align : "center"},
						{fixed: 'right', title : "操作 ", toolbar : "#paperOptionToolbar", align : "center"}
						]],
				page : true,
				height : "full-180",
				method : "post",
				defaultToolbar : [],
				parseData: function(paperOption){
					return {
						"code" : paperOption.succ,
						"msg" : paperOption.msg,
						"count" : paperOption.data.total,
						"data" : paperOption.data.rows
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
			layui.table.on("rowDouble(paperOptionTable)", function(obj){
				<my:auth url="paperOption/toEdit">toPaperOptionEdit(obj.data.ID);</my:auth>
			});
			layui.table.on("tool(paperOptionTable)", function(obj){
				var data = obj.data;
				if(obj.event === "paperOptionEdit") {
					toPaperOptionEdit(obj.data.ID);
				} else if(obj.event === "paperOptionDel") {
					doPaperOptionDel(obj.data.ID);
				}
			});
		}
		
		//试卷查询
		function paperOptionQuery() {
			layui.table.reload("paperOptionTable", {"where" : $.fn.my.serializeObj(paperOptionQueryForm)});
		}
	
		//试卷重置
		function paperOptionReset() {
			paperOptionQueryForm[0].reset();
			paperOptionQuery();
		}
		
		//到达添加试卷页面
		function toPaperOptionAdd() {
			$.ajax({
				url : "paperOption/toAdd",
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "添加试卷",
						area : ["800px", "500px"],
						content : obj,
						btn : ["添加", "取消"],
						type : 1,
						yes : function(index, layero){
							doPaperOptionAdd(index);
						},
						success: function(layero, index){
							layui.form.render(null, "paperOptionEditFrom");
						}
					});
				}
			});
		}

		//完成添加试卷
		function doPaperOptionAdd(paperOptionAddDialogIndex) {
			layui.form.on("submit(paperOptionEditBtn)", function(data) {
				layer.confirm("确定要添加？", function(index) {
					$.ajax({
						url : "paperOption/doAdd",
						data : data.field,
						success : function(obj) {
							initPaperOptionTable();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
							layer.close(paperOptionAddDialogIndex);
						}
					});
				});
			});
			$("[lay-filter='paperOptionEditBtn']").click();
		}
		
		//到达修改试卷页面
		function toPaperOptionEdit(id) {
			$.ajax({
				url : "paperOption/toEdit",
				data : {"id" : id},
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "修改试卷",
						area : ["800px", "500px"],
						content : obj,
						btn : ["修改", "取消"],
						type : 1,
						yes : function(index, layero){
							doPaperOptionEdit(index);
						},
						success: function(layero, index){
							layui.form.render(null, "paperOptionEditFrom");
						}
					});
				}
			});
		}

		//完成修改试卷
		function doPaperOptionEdit(paperOptionEditDialogIndex) {
			layui.form.on("submit(paperOptionEditBtn)", function(data) {
				layer.confirm("确定要修改？", function(index) {
					$.ajax({
						url : "paperOption/doEdit",
						data : data.field,
						success : function(obj) {
							initPaperOptionTable();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
							layer.close(paperOptionEditDialogIndex);
						}
					});
				});
			});
			$("[lay-filter='paperOptionEditBtn']").click();;
		}

		//完成删除试卷
		function doPaperOptionDel(id) {
			layer.confirm("确定要删除？", function(index) {
				$.ajax({
					url : "paperOption/doDel",
					data : {id : id},
					success : function(obj) {
						initPaperOptionTable();
						
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
