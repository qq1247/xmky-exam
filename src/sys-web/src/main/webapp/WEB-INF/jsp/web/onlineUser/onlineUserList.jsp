<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>在线用户列表</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
		<div class="layui-fluid">
			<div class="layui-card">
				<%-- 在线用户查询条件 --%>
				<form id="onlineUserQueryForm" class="layui-form layui-card-header layuiadmin-card-header-auto">
					<div class="layui-form-item">
						<div class="layui-inline">
							<input type="text" name="two" placeholder="请输入登陆名称" class="layui-input">
						</div>
						<div class="layui-inline">
							<button type="button" class="layui-btn layuiadmin-btn-useradmin" onclick="onlineUserQuery();">
								<i class="layui-icon layuiadmin-button-btn"></i>查询
							</button>
							<button type="button" class="layui-btn layui-btn-primary layuiadmin-btn-useradmin" onclick="onlineUserReset();">
								<i class="layui-icon layuiadmin-button-btn"></i>重置
							</button>
						</div>
					</div>
				</form>
				<div class="layui-card-body">
					<div style="padding-bottom: 10px;">
					</div>
					<script type="text/html" id="onlineUserToolbar">
						<my:auth url="onlineUser/doDel"><a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="onlineUserDel"><i class="layui-icon layui-icon-delete"></i>强制退出</a></my:auth>
					</script>
					<%-- 在线用户数据表格 --%>
					<table id="onlineUserTable" lay-filter="onlineUserTable"></table>
				</div>
			</div>
		</div>
	</body>
	<%@include file="/script/myJs/tail.jspf"%>
	<script type="text/javascript">
		//定义变量
		var onlineUserQueryForm = $("#onlineUserQueryForm"); //在线用户查询对象
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initOnlineUserTable();
		});
	
		//初始化在线用户表格
		function initOnlineUserTable() {
			layui.table.render({
				elem : "#onlineUserTable",
				url : "onlineUser/list",
				cols : [[ 
						{field : "SESSIONID", title : "SESSIONID", align : "center"}, 
						{field : "LOGINNAME", title : "登录名称", align : "center"},
						{fixed: 'right', title : "操作 ", toolbar : "#onlineUserToolbar", align : "center"}
						]],
				page : true,
				height : "full-180",
				method : "post",
				defaultToolbar : [],
				parseData: function(onlineUser){
					return {
						"code" : onlineUser.succ,
						"msg" : onlineUser.msg,
						"count" : onlineUser.data.total,
						"data" : onlineUser.data.rows
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
			layui.table.on("tool(onlineUserTable)", function(obj){
				var data = obj.data;
				if(obj.event === "onlineUserDel") {
					doOnlineUserDel(obj.data.ID);
				}
			});
		}
		
		//在线用户查询
		function onlineUserQuery() {
			layui.table.reload("onlineUserTable", {"where" : $.fn.my.serializeObj(onlineUserQueryForm)});
		}
	
		//在线用户重置
		function onlineUserReset() {
			onlineUserQueryForm[0].reset();
			onlineUserQuery();
		}

		//完成强制退出在线用户
		function doOnlineUserDel(id) {
			layer.confirm("确定要强制退出？", function(index) {
				$.ajax({
					url : "onlineUser/doDel",
					data : {id : id},
					success : function(obj) {
						onlineUserQuery();
						
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
