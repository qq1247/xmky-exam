<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>登录</title>
		<script type="text/javascript">
			if (top != self) {
			    top.location = self.location;
			}
		</script>
		<%@include file="/script/myJs/common.jspf"%>
		<link rel="stylesheet" href="script/layuiadmin/style/login.css" media="all">
	</head>
	<body>
		<div class="layadmin-user-login layadmin-user-display-show">
			<div class="layadmin-user-login-main">
				<div class="layadmin-user-login-box layadmin-user-login-header">
					<h2>智云</h2>
					<p>登录入口</p>
				</div>
				<div class="layadmin-user-login-box layadmin-user-login-body layui-form">
					<form class="layui-form" action="${orgCode}/login/doIn" method="post">
						<div class="layui-form-item">
							<label class="layadmin-user-login-icon layui-icon layui-icon-username" for="loginName"></label>
							<input type="text" name="loginName" lay-verify="required" placeholder="在此输入用户名" class="layui-input">
						</div>
						<div class="layui-form-item">
							<label class="layadmin-user-login-icon layui-icon layui-icon-password" for="pwd"></label>
							<input type="password" name="pwd" id="pwd" lay-verify="required" placeholder="在此输入密码" class="layui-input">
						</div>
						<div class="layui-form-item">
							<button class="layui-btn layui-btn-fluid" lay-submit lay-filter="submitBtn">登 入</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</body>
	<script type="text/javascript" src="script/layuiadmin/layui/layui.all.js"></script>
	<script>
		var message = "<%= request.getParameter("message") == null ? "" : request.getParameter("message") %>";
		if(message){
			layer.msg(decodeURI(message));
		}
	</script>
</html>