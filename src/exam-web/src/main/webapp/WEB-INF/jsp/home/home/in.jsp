<%@page import="java.util.List"%>
<%@page import="org.apache.commons.io.FileUtils"%>
<%@page import="com.wcpdoc.exam.core.util.ValidateUtil"%>
<%@page import="java.io.File"%>
<%@page import="com.wcpdoc.exam.core.util.StringUtil"%>
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
								<!-- 系统管理员 sysadmin 111111<br/>
								张三 zs zs 判卷用户<br/>
								李四 ls ls 判卷用户<br/>
								王五 ww ww 考试用户<br/>
								赵六 zl zl 考试用户<br/>
								钱七 qq qq 考试用户<br/>
								孙八 sb sb 考试用户<br/>
								杨九 yj yj 考试用户<br/> -->
							</div>
						</form>
					</div>
				</div>
			</div>
			<%-- <%
			int count=0;
			String webPath = request.getSession().getServletContext().getRealPath(File.separator);
			File file = new File(webPath + "count.txt");
			if(!file.exists()){
				file.createNewFile();
			}
			String countStr = "0";
			List<String> lines = StringUtil.getLastLine(file, 1);
			if(lines.size() > 0){
				String line1 = lines.get(0);
				if(ValidateUtil.isValid(line1)){
					countStr = line1;
				}
			}
			
			count = Integer.parseInt(countStr) + 1;
			FileUtils.write(file, count + "");
			%>
			第&nbsp;<%=count %>&nbsp;位访客 --%>
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