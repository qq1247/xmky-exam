<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>邮箱列表</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
		<div class="layui-fluid">
			<div class="layui-card">
				<%-- 邮箱查询条件 --%>
				<form id="emailQueryForm" class="layui-form layui-card-header layuiadmin-card-header-auto">
					<div class="layui-form-item ">
						<div class="layui-inline">
							<input type="text" name="two" placeholder="请输入ID" class="layui-input">
						</div>
						<div class="layui-inline">
							<button type="button" class="layui-btn layuiadmin-btn-useradmin" onclick="emailQuery();">
								<i class="layui-icon layuiadmin-button-btn"></i>查询
							</button>
							<button type="button" class="layui-btn layui-btn-primary layuiadmin-btn-useradmin" onclick="emailReset();">
								<i class="layui-icon layuiadmin-button-btn"></i>重置
							</button>
						</div>
					</div>
				</form>
				<div class="layui-card-body">
					<div style="padding-bottom: 10px;">
						<my:auth url="email/toAdd"><button class="layui-btn layuiadmin-btn-useradmin" onclick="toEmailAdd();">添加</button></my:auth>
					</div>
					<script type="text/html" id="emailToolbar">
						<my:auth url="email/toEdit"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="emailEdit"><i class="layui-icon layui-icon-edit"></i>修改</a></my:auth>
						<my:auth url="email/doDel"><a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="emailDel"><i class="layui-icon layui-icon-delete"></i>删除</a></my:auth>
					</script>
					<%-- 邮箱数据表格 --%>
					<table id="emailTable" lay-filter="emailTable"></table>
				</div>
			</div>
		</div>
	</body>
	<%@include file="/script/myJs/tail.jspf"%>
	<script type="text/javascript">
		//定义变量
		var emailQueryForm = $("#emailQueryForm"); //邮箱查询对象
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initEmailTable();
		});
	
		//初始化邮箱表格
		function initEmailTable() {
			layui.table.render({
				elem : "#emailTable",
				url : "email/list",
				cols : [[
						{field : "EMAIL_HOST", title : "邮箱地址", align : "center"},
						{field : "EMAIL_NAME", title : "邮箱账号", align : "center"},
						{field : "EMAIL_PWD", title : "邮箱密码", align : "center"},
						{fixed: 'right', title : "操作 ", toolbar : "#emailToolbar", align : "center"}
						]],
				page : true,
				height : "full-180",
				method : "post",
				defaultToolbar : [],
				parseData: function(email){
					return {
						"code" : email.succ,
						"msg" : email.msg,
						"count" : email.data.total,
						"data" : email.data.rows
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
			layui.table.on("rowDouble(emailTable)", function(obj){
				<my:auth url="email/toEdit">toEmailEdit(obj.data.ID);</my:auth>
			});
			layui.table.on("tool(emailTable)", function(obj){
				var data = obj.data;
				if(obj.event === "emailEdit") {
					toEmailEdit(obj.data.ID);
				} else if(obj.event === "emailDel") {
					doEmailDel(obj.data.ID);
				}
			});
		}
		
		//邮箱查询
		function emailQuery() {
			layui.table.reload("emailTable", {"where" : $.fn.my.serializeObj(emailQueryForm)});
		}
	
		//邮箱重置
		function emailReset() {
			emailQueryForm[0].reset();
			emailQuery();
		}
		
		//到达添加邮箱页面
		function toEmailAdd() {
			$.ajax({
				url : "email/toAdd",
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "添加邮箱",
						area : ["800px", "500px"],
						content : obj,
						btn : ["添加", "取消"],
						type : 1,
						yes : function(index, layero){
							doEmailAdd(index);
						},
						success: function(layero, index){
							layui.form.render(null, "emailEditFrom");
						}
					});
				}
			});
		}

		//完成添加邮箱
		function doEmailAdd(emailAddDialogIndex) {
			layui.form.on("submit(emailEditBtn)", function(data) {
				layer.confirm("确定要添加？", function(index) {
					$.ajax({
						url : "email/doAdd",
						data : data.field,
						success : function(obj) {
							initEmailTable();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
							layer.close(emailAddDialogIndex);
						}
					});
				});
			});
			$("[lay-filter='emailEditBtn']").click();
		}
		
		//到达修改邮箱页面
		function toEmailEdit(id) {
			$.ajax({
				url : "email/toEdit",
				data : {"id" : id},
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "修改邮箱",
						area : ["800px", "500px"],
						content : obj,
						btn : ["修改", "取消"],
						type : 1,
						yes : function(index, layero){
							doEmailEdit(index);
						},
						success: function(layero, index){
							layui.form.render(null, "emailEditFrom");
						}
					});
				}
			});
		}

		//完成修改邮箱
		function doEmailEdit(emailEditDialogIndex) {
			layui.form.on("submit(emailEditBtn)", function(data) {
				layer.confirm("确定要修改？", function(index) {
					$.ajax({
						url : "email/doEdit",
						data : data.field,
						success : function(obj) {
							initEmailTable();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
							layer.close(emailEditDialogIndex);
						}
					});
				});
			});
			$("[lay-filter='emailEditBtn']").click();;
		}

		//完成删除邮箱
		function doEmailDel(id) {
			layer.confirm("确定要删除？", function(index) {
				$.ajax({
					url : "email/doDel",
					data : {id : id},
					success : function(obj) {
						initEmailTable();
						
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
