<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>日志列表</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'center',border:false" style="padding: 20px;">
				<span id="log_text"></span>
			</div>
		</div>
	</body>
	<script type="text/javascript">
		//页面加载完毕，执行如下方法：
		$(function() {
			loadLog();
			setInterval("loadLog()", 5000);
		});
		
		//加载日志
		function loadLog(){
			$.ajax({
				url : "log/list",
				async : true,
				success : function(obj){
					$("#log_text").html("")
					var index;
					for(index in obj.rows){
						$("#log_text").append(obj.rows[index].LINE_STR + "<br/>");
					}
				}
			});
		}
	</script>
</html>