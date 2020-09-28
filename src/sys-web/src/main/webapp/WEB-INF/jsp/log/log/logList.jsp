<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>日志列表</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
		<div class="layui-fluid">
			<div class="layui-card">
				<div class="layui-form layui-card-header layuiadmin-card-header-auto">
					<div class="layui-row layui-form-item">
						<div class="layui-col-md12">
							<label class="layui-form-label">日志文件：</label>
							<div class="layui-input-block">
								<select name="logName" lay-filter="logName">
									<c:forEach var="logFile" items="${logFileList }">
									<option value="${logFile.name }">${logFile.name }</option>
									</c:forEach>
								</select>
							</div>
						</div>
					</div>
				</div>
				<div class="layui-card-body">
					<pre id="log_text"></pre>
				</div>
			</div>
		</div>
	</body>
	<%@include file="/script/myJs/tail.jspf"%>
	<script type="text/javascript">
		// 定义变量
		var logText = $("#log_text"); //输出日志位置
		var curReadLen; // 当前读取日志文件字节数
	
		// 页面加载完毕，执行如下方法：
		$(function() {
			loadLog();
			var loadLogInterval = setInterval("loadLog()", 3000);
			
			layui.form.on("select(logName)", function(data){
				clearInterval(loadLogInterval);
				logText.html("");
				curReadLen = null;
				loadLog();
				loadLogInterval = setInterval("loadLog()", 3000);
			});
		});
		
		// 加载日志
		function loadLog() {
			$.ajax({
				url : "log/list",
				async : true,
				data : {
	                curReadLen : curReadLen,
	                logName : function() {
						return $("[lay-filter='logName']").parent().find("dd[class='layui-this']").attr("lay-value");
					}
	            },
				success : function(obj){
					curReadLen = obj.data.CUR_READ_LEN;
					for(var index in obj.data.STR_LIST){
						logText.append(obj.data.STR_LIST[index] + "<br/>" );
					}
				}
			});
		}
	</script>
</html>