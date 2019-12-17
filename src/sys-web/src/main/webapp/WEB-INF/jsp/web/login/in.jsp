<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<html>
	<head>
		<title>登录</title>
		<script type="text/javascript">
			if (top != self) {
			    top.location = self.location;
			}
		</script>
		
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
		<form id="form" action="login/doIn" method="post">
			<table style="width: 100%;height: 100%;text-align: center;">
				<tr>
					<td style="width: 30%;">
					</td>
					<td style="width: 40%;">
						<div class="easyui-panel" title="登录" 
							style="width: 400px; padding: 30px 70px 20px 70px;">
							<div style="margin-bottom: 10px">
								<input name="loginName" class="easyui-textbox"
									style="width: 100%; height: 40px; padding: 12px" value="" 
									data-options="prompt:'在此输入用户名',iconCls:'icon-man',iconWidth:38">
							</div>
							<div style="margin-bottom: 20px">
								<input name="pwd" class="easyui-textbox" type="pwd"
									style="width: 100%; height: 40px; padding: 12px" value=""
									data-options="prompt:'在此输入密码',iconCls:'icon-lock',iconWidth:38">
							</div>
							<div>
								<a href="#" class="easyui-linkbutton"
									data-options="iconCls:'icon-ok'"
									style="padding: 5px 0px; width: 100%;" onclick="document.getElementById('form').submit();return false">
									<span style="font-size: 14px;">登录</span> </a>
								<span id="message" style="color: red;"></span>
							</div>
						</div>
					</td>
					<td style="width: 30%;">
						
					</td>
				</tr>
			</table>
		</form>
	</body>
	<script type="text/javascript">
		var message = "<%= request.getParameter("message") == null ? "" : request.getParameter("message") %>";
		if(message){
			$("#message").html(decodeURI(message));
		}
	</script>
</html>