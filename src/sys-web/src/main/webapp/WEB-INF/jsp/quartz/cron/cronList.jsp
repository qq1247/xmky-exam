<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>定时任务列表</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'center',border:false">
				<%-- 定时任务查询条件 --%>
				<div id="cronToolbar" style="padding:0 30px;">
					<div class="conditions">
						<form id="cronQueryForm">
							<span class="con-span">名称：</span>
							<input name="two" class="easyui-textbox" style="width:166px;height:35px;line-height:35px;">
							<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" data-options="selected:true" onclick="cronQuery();">查询</a>
							<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-clear" onclick="cronReset();">重置</a>
						</form>
					</div>
					<div class="opt-buttons">
						<my:auth url="cron/toAdd"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" data-options="selected:true" onclick="toCronAdd();">添加</a></my:auth>
						<my:auth url="cron/toEdit"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" onclick="toCronEditForBtn();">修改</a></my:auth>
						<my:auth url="cron/doDel"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" onclick="doCronDelForBtn();">删除</a></my:auth>
						<my:auth url="cron/startTask"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-ok" onclick="startTask();">启动</a></my:auth>
						<my:auth url="cron/stopTask"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-cancel" onclick="stopTask();">停止</a></my:auth>
						<my:auth url="cron/runOnceTask"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-reload" onclick="runOnceTask();">执行一次</a></my:auth>
					</div>
				</div>
				<%-- 定时任务数据表格 --%>
				<table id="cronGrid">
				</table>
			</div>
		</div>
	</body>
	<script type="text/javascript">
		//定义变量
		var cronGrid = $("#cronGrid"); //定时任务表格对象
		var cronQueryForm = $("#cronQueryForm"); //定时任务查询对象
	
		//页面加载完毕，执行如下方法：
		$(function() {
			initCronGrid();
		});
	
		//初始化定时任务表格
		function initCronGrid() {
			cronGrid.datagrid( {
				url : "cron/list",
				onDblClickRow : <my:auth url="cron/toEdit">toCronEditForDblClick</my:auth>, 
				toolbar : "#cronToolbar",
				columns : [[ 
						{field : "ID", title : "", checkbox : true}, 
						{field : "NAME", title : "名称", width : 80, align : "center"},
						{field : "JOB_CLASS", title : "实现类", width : 160, align : "center"},
						{field : "CRON", title : "表达式", width : 80, align : "center"},
						{field : "STATE_NAME", title : "状态 ", width : 80, align : "center"},
						{field : "RECENT_TRIGGER_TIME", title : "最近三次运行时间 ", width : 160, align : "center"}
						]]
			});
		}
	
		//定时任务查询
		function cronQuery() {
			cronGrid.datagrid("uncheckAll");
			cronGrid.datagrid("load", $.fn.my.serializeObj(cronQueryForm));
		}
	
		//定时任务重置
		function cronReset() {
			cronQueryForm.form("reset");
			cronQuery();
		}
		
		//到达添加定时任务页面
		function toCronAdd() {
			var cronAddDialog;
			cronAddDialog = $("<div/>").dialog({
				title : "添加定时任务",
				href : "cron/toAdd",
				buttons : [{
					text : "添加", 
					iconCls : "icon-add", 
					selected : true, 
					handler : function (){
						doCronAdd(cronAddDialog);
					}
				}],
				onLoad : function() {
					$("#cron_name").textbox({
						required : true,
						validType : ["length[1, 16]"]
					});
					$("#cron_cron").textbox({
						required : true,
						validType : ["length[1, 64]"]
					});
					$("#cron_jobClass").textbox({
						required : true,
						validType : ["length[1, 64]"]
					});
				}
			});
		}

		//完成添加定时任务
		function doCronAdd(cronAddDialog) {
			var cronEditFrom = $("#cronEditFrom");
			if(!cronEditFrom.form("validate")){
				return;
			}
			$.messager.confirm("确认消息", "确定要添加？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.progress();
				cronEditFrom.form("submit", {
					url : "cron/doAdd",
					success : function(data) {
						cronGrid.datagrid("reload");
						$.messager.progress("close");
						
						var obj = $.parseJSON(data);
						if (!obj.succ) {
							parent.$.messager.alert("提示消息", obj.msg, "info");
							return;
						}

						cronAddDialog.dialog("close");
					}
				});
			})
		}

		//到达修改定时任务页面
		function toCronEdit(id) {
			var cronEditDialog;
			cronEditDialog = $("<div/>").dialog({
				title : "修改定时任务",
				href : "cron/toEdit",
				queryParams : {"id" : id},
				buttons : [{
					text : "修改", 
					iconCls : "icon-edit", 
					selected : true,
					handler : function() {
						doCronEdit(cronEditDialog);
					}
				}],
				onLoad : function() {
					$("#cron_name").textbox({
						required : true,
						validType : ["length[1, 16]"]
					});
					$("#cron_cron").textbox({
						required : true,
						validType : ["length[1, 64]"]
					});
					$("#cron_jobClass").textbox({
						required : true,
						validType : ["length[1, 64]"]
					});
				}
			});
		}

		//到达修改定时任务页面
		function toCronEditForBtn() {
			var cronGridRows = cronGrid.datagrid("getChecked");
			if (cronGridRows.length != 1) {
				parent.$.messager.alert("提示消息", "请选择一行数据！", "info");
				return;
			}

			toCronEdit(cronGridRows[0].ID);
		}

		//到达修改定时任务页面
		function toCronEditForDblClick(rowIndex, rowData) {
			cronGrid.datagrid("uncheckAll");
			cronGrid.datagrid("checkRow", rowIndex);
			toCronEditForBtn();
		}

		//完成修改定时任务
		function doCronEdit(cronEditDialog) {
			var cronEditFrom = $("#cronEditFrom");
			if(!cronEditFrom.form("validate")){
				return;
			}
			$.messager.confirm("确认消息", "确定要修改？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.progress();
				cronEditFrom.form("submit", {
					url : "cron/doEdit",
					success : function(data) {
						cronGrid.datagrid("reload");
						$.messager.progress("close");

						var obj = $.parseJSON(data);
						if (!obj.succ) {
							parent.$.messager.alert("提示消息", obj.msg, "info");
							return;
						}

						cronEditDialog.dialog("close");
					}
				});
			})
		}

		//完成删除定时任务
		function doCronDel(params) {
			$.messager.confirm("确认消息", "确定要删除？", function(ok) {
				if (!ok) {
					return;
				}

				$.messager.progress();
				$.ajax({
					url : "cron/doDel",
					data : params,
					success : function(obj) {
						cronGrid.datagrid("reload");
						$.messager.progress("close");
						
						if (!obj.succ) {
							parent.$.messager.alert("提示消息", obj.msg, "info");
						}
					}
				});
			});
		}

		//完成删除定时任务
		function doCronDelForBtn() {
			//校验数据有效性
			var cronGridRows = cronGrid.datagrid("getChecked");
			if (cronGridRows.length == 0) {
				parent.$.messager.alert("提示消息","请选择一行或多行数据！", "info");
				return;
			}

			//删除
			doCronDel($.fn.my.serializeField(cronGridRows));
		}
		
		//启动任务
		function startTask() {
			//校验数据有效性
			var cronGridRows = cronGrid.datagrid("getChecked");
			if (cronGridRows.length == 0) {
				parent.$.messager.alert("提示消息","请选择一行或多行数据！", "info");
				return;
			}

			//启动任务
			$.messager.confirm("确认消息", "确定要启动？", function(ok) {
				if (!ok) {
					return;
				}

				$.messager.progress();
				$.ajax({
					url : "cron/startTask",
					data : $.fn.my.serializeField(cronGridRows),
					success : function(obj) {
						cronGrid.datagrid("reload");
						$.messager.progress("close");
						
						if (!obj.succ) {
							parent.$.messager.alert("提示消息", obj.msg, "info");
						}
					}
				});
			});
		}
		
		//停止任务
		function stopTask() {
			//校验数据有效性
			var cronGridRows = cronGrid.datagrid("getChecked");
			if (cronGridRows.length == 0) {
				parent.$.messager.alert("提示消息","请选择一行或多行数据！", "info");
				return;
			}

			//停止任务
			$.messager.confirm("确认消息", "确定要停止？", function(ok) {
				if (!ok) {
					return;
				}

				$.messager.progress();
				$.ajax({
					url : "cron/stopTask",
					data : $.fn.my.serializeField(cronGridRows),
					success : function(obj) {
						cronGrid.datagrid("reload");
						$.messager.progress("close");
						
						if (!obj.succ) {
							parent.$.messager.alert("提示消息", obj.msg, "info");
						}
					}
				});
			});
		}
		
		//执行一次
		function runOnceTask() {
			//校验数据有效性
			var cronGridRows = cronGrid.datagrid("getChecked");
			if (cronGridRows.length == 0) {
				parent.$.messager.alert("提示消息","请选择一行或多行数据！", "info");
				return;
			}

			//停止任务
			$.messager.confirm("确认消息", "确定要执行一次？", function(ok) {
				if (!ok) {
					return;
				}

				$.messager.progress();
				$.ajax({
					url : "cron/runOnceTask",
					data : $.fn.my.serializeField(cronGridRows),
					success : function(obj) {
						cronGrid.datagrid("reload");
						$.messager.progress("close");
						
						if (!obj.succ) {
							parent.$.messager.alert("提示消息", obj.msg, "info");
						}
					}
				});
			});
		}
	</script>
</html>