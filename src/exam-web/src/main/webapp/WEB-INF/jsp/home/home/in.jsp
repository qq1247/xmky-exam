<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
	<head>
		<title>在线考试系统首页</title>
		<%@include file="/script/home/common.jspf"%>
		<script src="script/bootstrapTreeview/bootstrap-treeview.min.js"></script>
	</head>
	<body>
		<%@include file="/script/home/head.jspf"%>
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