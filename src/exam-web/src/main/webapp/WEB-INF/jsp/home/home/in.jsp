<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
	<head>
		<title>在线考试系统首页</title>
		<script type="text/javascript">
			if (top != self) {
			    top.location = self.location;
			}
		</script>
		<%@include file="/script/home/common.jspf"%>
	</head>
	<body>
		<%@include file="/script/home/head.jspf"%>
		<div class="exam-login-bg">
			<div class="container">
				<div class="row">
					<div class="col-md-2"></div>
					<div class="col-md-8">
						<form id="loginForm" action="home/doIn" method="post">
							<div class="exam-login-box">
								<h1>在线考试账号登录</h1>
								<div>
									<span class="glyphicon glyphicon-user"></span>
									<input type="text" id="loginName" name="loginName" value="" placeholder="请输入账号">
									<small class="help-block"></small>
								</div>
								<div>
									<span class="glyphicon glyphicon-lock"></span>
									<input type="password" id="pwd" name="pwd" value="" placeholder="请输入密码">
									<small class="help-block"></small>
								</div>
								<div>
									<button type="button" class="btn btn-primary" onclick="doLogin();">登录</button>
								</div>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
	</body>
	<script type="text/javascript">
		//定义变量
		var $loginName = $("#loginName");
		var $pwd = $("#pwd");
		var $examLoginBbox = $(".exam-login-box");
		var $loginForm = $("#loginForm");
	
		//页面加载完毕，执行如下方法：
		$(function() {
			$(document).keydown(function (event) {
				if (event.keyCode == 13) {
					doLogin();
				}
			});
			
			var message = "<%= request.getParameter("message") == null ? "" : request.getParameter("message") %>";
			if(message){
				$pwd.next().html(decodeURI(message));
			}
		});
		
		//完成登录
		function doLogin(){
			$examLoginBbox.find(".field-error").removeClass('field-error');
			$examLoginBbox.find(".help-block").html("");
			
			var loginName = $.trim($loginName.val());
			var pwd = $.trim($pwd.val());
			if(!loginName){
				$loginName.addClass("field-error").next().html("必填项");
				$loginName.focus();
				return;
			}
			if(!(loginName.length >= 2 && loginName.length <= 16)){
				$loginName.addClass("field-error").next().html("长度介于2-16");
				$loginName.focus();
				return;
			}
			if(!pwd){
				$pwd.addClass("field-error").next().html("必填项");
				$pwd.focus();
				return;
			}
			
			$loginForm.submit();
		}
	</script>
</html>