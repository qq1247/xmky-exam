<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>在线用户列表</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'center',border:false">
				<%-- 在线用户查询条件 --%>
				<div id="onlineUserToolbar" style="padding:0 30px;">
					<div class="conditions">
						<form id="onlineUserQueryForm">
							<span class="con-span">登陆名称： </span>
							<input name="two" class="easyui-textbox" style="width:166px;height:35px;line-height:35px;">
							<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" data-options="selected:true" onclick="onlineUserQuery();">查询</a>
							<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-clear" onclick="onlineUserReset();">重置</a>
						</form>
					</div>
					<div class="opt-buttons">
						<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" data-options="selected:true" onclick="doOnlineUserDelForBtn();">强制退出</a>
					</div>
				</div>
				<%-- 在线用户数据表格 --%>
				<table id="onlineUserGrid">
				</table>
			</div>
		</div>
	</body>
	<script type="text/javascript">
		//定义变量
		var onlineUserGrid = $("#onlineUserGrid"); //在线用户表格对象
		var onlineUserQueryForm = $("#onlineUserQueryForm"); //用户查询对象
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initOnlineUserGrid();
		});
		
		//初始化在线用户表格
		function initOnlineUserGrid() {
			onlineUserGrid.datagrid( {
				url : "onlineUser/list",
				toolbar : "#onlineUserToolbar",
				columns : [[ 
						{field : "ID", title : "", checkbox : true}, 
						{field : "SESSIONID", title : "SESSIONID", width : 80, align : "center"}, 
						{field : "LOGINNAME", title : "登录名称", width : 80, align : "center"}
						]]
			});
		}

		//在线用户查询
		function onlineUserQuery() {
			onlineUserGrid.datagrid("uncheckAll");
			onlineUserGrid.datagrid("load", $.fn.my.serializeObj(onlineUserQueryForm));
		}
	
		//在线用户重置
		function onlineUserReset() {
			onlineUserQueryForm.form("reset");
			onlineUserQuery();
		}
		
		//完成强制退出在线用户
		function doOnlineUserDelete(params) {
			$.messager.confirm("确认消息", "确定要强制退出？", function(ok){
				if (!ok){
					return;
				}

				$.messager.progress();
				$.ajax({
					url : "onlineUser/doDel",
					data : params,
					success : function(obj){
						$.messager.progress("close");
						
						onlineUserGrid.datagrid("load", $.fn.my.serializeObj(onlineUserQueryForm));
						if(!obj.success){
							parent.$.messager.alert("提示消息", obj.message, "info");
						}
					}
				});
			});
		}
		
		//完成强制退出在线用户
		function doOnlineUserDelForBtn() {
			//校验数据有效性
			var onlineUserGridRows = onlineUserGrid.datagrid("getChecked");
			if(onlineUserGridRows.length == 0){
				parent.$.messager.alert("提示消息", "请选择一行或多行数据！", "info");
				return;
			}

			//强制退出
			doOnlineUserDelete($.fn.my.serializeField(onlineUserGridRows));
		}
	</script>
</html>