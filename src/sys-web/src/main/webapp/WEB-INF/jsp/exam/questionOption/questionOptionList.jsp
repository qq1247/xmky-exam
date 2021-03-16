<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>试题选项列表</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
		<div class="layui-fluid">
			<div class="layui-card">
				<%-- 试题选项查询条件 --%>
				<form id="questionOptionQueryForm" class="layui-form layui-card-header layuiadmin-card-header-auto">
					<div class="layui-form-item ">
						<div class="layui-inline">
							<input type="text" name="two" placeholder="请输入ID" class="layui-input">
						</div>
						<div class="layui-inline">
							<button type="button" class="layui-btn layuiadmin-btn-useradmin" onclick="questionOptionQuery();">
								<i class="layui-icon layuiadmin-button-btn"></i>查询
							</button>
							<button type="button" class="layui-btn layui-btn-primary layuiadmin-btn-useradmin" onclick="questionOptionReset();">
								<i class="layui-icon layuiadmin-button-btn"></i>重置
							</button>
						</div>
					</div>
				</form>
				<div class="layui-card-body">
					<div style="padding-bottom: 10px;">
						<my:auth url="questionOption/toAdd"><button class="layui-btn layuiadmin-btn-useradmin" onclick="toQuestionOptionAdd();">添加</button></my:auth>
					</div>
					<script type="text/html" id="questionOptionToolbar">
						<my:auth url="questionOption/toEdit"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="questionOptionEdit"><i class="layui-icon layui-icon-edit"></i>修改</a></my:auth>
						<my:auth url="questionOption/doDel"><a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="questionOptionDel"><i class="layui-icon layui-icon-delete"></i>删除</a></my:auth>
					</script>
					<%-- 试题选项数据表格 --%>
					<table id="questionOptionTable" lay-filter="questionOptionTable"></table>
				</div>
			</div>
		</div>
	</body>
	<%@include file="/script/myJs/tail.jspf"%>
	<script type="text/javascript">
		//定义变量
		var questionOptionQueryForm = $("#questionOptionQueryForm"); //试题选项查询对象
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initQuestionOptionTable();
		});
	
		//初始化试题选项表格
		function initQuestionOptionTable() {
			layui.table.render({
				elem : "#questionOptionTable",
				url : "questionOption/list",
				cols : [[
						{field : "OPTION_A", title : "选项A", align : "center"},
						{field : "OPTION_B", title : "选项B", align : "center"},
						{field : "OPTION_C", title : "选项C", align : "center"},
						{field : "OPTION_D", title : "选项D", align : "center"},
						{field : "OPTION_E", title : "选项E", align : "center"},
						{field : "OPTION_F", title : "选项F", align : "center"},
						{field : "OPTION_G", title : "选项G", align : "center"},
						{field : "QUESTION_ID", title : "试题ID", align : "center"},
						{fixed: 'right', title : "操作 ", toolbar : "#questionOptionToolbar", align : "center"}
						]],
				page : true,
				height : "full-180",
				method : "post",
				defaultToolbar : [],
				parseData: function(questionOption){
					return {
						"code" : questionOption.succ,
						"msg" : questionOption.msg,
						"count" : questionOption.data.total,
						"data" : questionOption.data.rows
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
			layui.table.on("rowDouble(questionOptionTable)", function(obj){
				<my:auth url="questionOption/toEdit">toQuestionOptionEdit(obj.data.ID);</my:auth>
			});
			layui.table.on("tool(questionOptionTable)", function(obj){
				var data = obj.data;
				if(obj.event === "questionOptionEdit") {
					toQuestionOptionEdit(obj.data.ID);
				} else if(obj.event === "questionOptionDel") {
					doQuestionOptionDel(obj.data.ID);
				}
			});
		}
		
		//试题选项查询
		function questionOptionQuery() {
			layui.table.reload("questionOptionTable", {"where" : $.fn.my.serializeObj(questionOptionQueryForm)});
		}
	
		//试题选项重置
		function questionOptionReset() {
			questionOptionQueryForm[0].reset();
			questionOptionQuery();
		}
		
		//到达添加试题选项页面
		function toQuestionOptionAdd() {
			$.ajax({
				url : "questionOption/toAdd",
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "添加试题选项",
						area : ["800px", "500px"],
						content : obj,
						btn : ["添加", "取消"],
						type : 1,
						yes : function(index, layero){
							doQuestionOptionAdd(index);
						},
						success: function(layero, index){
							layui.form.render(null, "questionOptionEditFrom");
						}
					});
				}
			});
		}

		//完成添加试题选项
		function doQuestionOptionAdd(questionOptionAddDialogIndex) {
			layui.form.on("submit(questionOptionEditBtn)", function(data) {
				layer.confirm("确定要添加？", function(index) {
					$.ajax({
						url : "questionOption/doAdd",
						data : data.field,
						success : function(obj) {
							initQuestionOptionTable();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
							layer.close(questionOptionAddDialogIndex);
						}
					});
				});
			});
			$("[lay-filter='questionOptionEditBtn']").click();
		}
		
		//到达修改试题选项页面
		function toQuestionOptionEdit(id) {
			$.ajax({
				url : "questionOption/toEdit",
				data : {"id" : id},
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "修改试题选项",
						area : ["800px", "500px"],
						content : obj,
						btn : ["修改", "取消"],
						type : 1,
						yes : function(index, layero){
							doQuestionOptionEdit(index);
						},
						success: function(layero, index){
							layui.form.render(null, "questionOptionEditFrom");
						}
					});
				}
			});
		}

		//完成修改试题选项
		function doQuestionOptionEdit(questionOptionEditDialogIndex) {
			layui.form.on("submit(questionOptionEditBtn)", function(data) {
				layer.confirm("确定要修改？", function(index) {
					$.ajax({
						url : "questionOption/doEdit",
						data : data.field,
						success : function(obj) {
							initQuestionOptionTable();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
							layer.close(questionOptionEditDialogIndex);
						}
					});
				});
			});
			$("[lay-filter='questionOptionEditBtn']").click();;
		}

		//完成删除试题选项
		function doQuestionOptionDel(id) {
			layer.confirm("确定要删除？", function(index) {
				$.ajax({
					url : "questionOption/doDel",
					data : {id : id},
					success : function(obj) {
						initQuestionOptionTable();
						
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
