<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
	<head>
		<title>在线考试系统首页</title>
		<%@include file="/script/home/common.jspf"%>
	</head>
	<body>
		<%@include file="/script/home/head.jspf"%>
			<section class="service-item" style="padding-top: 200px;">
				<div class="container">
				<div class="row">
				<div class="col-xs-6 col-md-offset-3">
				<span style="font-size: 30px;">用户登录</span>
				<div class="panel panel-default">
				<div class="panel-body">
				<form id="loginForm" name="loginForm" action="home/pubDoIn" method="post" class="form-horizontal" role="form">
				<div class="form-group">
				<label for="userName" class="col-sm-2 control-label">用户名：</label>
				<div class="col-sm-10">
				<input type="text" class="form-control" id="userName" name="loginName"
				placeholder="请输入用户名" value="sysadmin">
				</div>
				</div>
				<div class="form-group">
				<label for="pwd" class="col-sm-2 control-label">密码：</label>
				<div class="col-sm-10">
				<input type="pwd" class="form-control" id="pwd" name="pwd"
				placeholder="请输入密码" value="111111">
				</div>
				</div>
				<div class="form-group">
				<div class="col-sm-12">
				<span id="message" style="color: red;"></span>
				<button type="submit" class="btn btn-primary btn-lg btn-block">登录</button>
				</div>
				</div>
				</form>
				</div>
				</div>
				</div>
				</div>
				</div>
				</section> 
	</body>
	<%-- <script type="text/javascript">
		//页面加载完毕，执行如下方法：
		$(function() {
			var message = "<%= request.getParameter("message") == null ? "" : request.getParameter("message") %>";
			if(message){
				$("#message").html(decodeURI(message));
			}
			
			initHome();
		});
		
		//初始化
		function initHome(){
			$("#loginForm").bootstrapValidator({
				message : '未校验',
				trigger : "change",
				feedbackIcons : {
					valid : 'glyphicon glyphicon-ok',
					invalid : 'glyphicon glyphicon-remove',
					validating : 'glyphicon glyphicon-refresh'
				},
				fields : {
					loginName : {
						validators : {
							notEmpty : {
								message : "必填项"
							}
						}
					},
					pwd : {
						validators : {
							notEmpty : {
								message : "必填项"
							}
						}
					}
				}
			});
		}
	</script> --%>
</html>