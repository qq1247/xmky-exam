<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>定时任务列表</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
		<div class="layui-fluid">
			<div class="layui-card">
				<%-- 定时任务查询条件 --%>
				<form id="cronQueryForm" class="layui-form layui-card-header layuiadmin-card-header-auto">
					<div class="layui-form-item">
						<div class="layui-inline">
							<input type="text" name="two" placeholder="请输入名字" class="layui-input">
						</div>
						<div class="layui-inline">
							<button type="button" class="layui-btn layuiadmin-btn-useradmin" onclick="cronQuery();">
								<i class="layui-icon layuiadmin-button-btn"></i>查询
							</button>
							<button type="button" class="layui-btn layui-btn-primary layuiadmin-btn-useradmin" onclick="cronReset();">
								<i class="layui-icon layuiadmin-button-btn"></i>重置
							</button>
						</div>
					</div>
				</form>
				<div class="layui-card-body">
						<div style="padding-bottom: 10px;">
							<my:auth url="cron/toAdd"><button class="layui-btn layuiadmin-btn-useradmin" onclick="toCronAdd();">添加</button></my:auth>
						</div>
						<script type="text/html" id="cronToolbar">
							<my:auth url="cron/toEdit"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="cronEdit"><i class="layui-icon layui-icon-edit"></i>修改</a></my:auth>
							<my:auth url="cron/startTask"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="cronStartTask"><i class="layui-icon layui-icon-right"></i>启动</a></my:auth>
							<my:auth url="cron/stopTask"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="cronStopTask"><i class="layui-icon layui-icon-left"></i>停止</a></my:auth>
							<my:auth url="cron/runOnceTask"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="cronRunOnceTask"><i class="layui-icon layui-icon-ok"></i>执行一次</a></my:auth>
							<my:auth url="cron/doDel"><a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="cronDel"><i class="layui-icon layui-icon-delete"></i>删除</a></my:auth>
						</script>
					<%-- 定时任务数据表格 --%>
					<table id="cronTable" lay-filter="cronTable"></table>
				</div>
			</div>
		</div>
	</body>
	<%@include file="/script/myJs/tail.jspf"%>
	<script type="text/javascript">
		//定义变量
		var cronQueryForm = $("#cronQueryForm"); //定时任务查询对象
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initCronTable();
		});
	
		//初始化定时任务表格
		function initCronTable() {
			layui.table.render({
				elem : "#cronTable",
				url : "cron/list",
				cols : [[
						{field : "NAME", title : "名称", align : "center"},
						{field : "JOB_CLASS", title : "实现类", align : "center"},
						{field : "CRON", title : "cron表达式", align : "center"},
						{field : "STATE_NAME", title : "状态", align : "center"},
						{field : "RECENT_TRIGGER_TIME", title : "最近三次运行时间 ", align : "center", width : 500},
						{fixed : "right", title : "操作 ", toolbar : "#cronToolbar", align : "center", width : 400},
						]],
				page : true,
				method : "post",
				height : "full-180",
				defaultToolbar : [],
				parseData : function(cron) {
					return {
						"code" : cron.succ,
						"msg" : cron.msg,
						"count" : cron.data.total,
						"data" : cron.data.rows
					};
				},
				request : {
					pageName: "curPage",
					limitName: "pageSize"
				}, 
				response : {
					statusCode : true
				}
			});
			layui.table.on("rowDouble(cronTable)", function(obj) {
				<my:auth url="cron/toEdit">toCronEdit(obj.data.ID);</my:auth>
			});
			layui.table.on("tool(cronTable)", function(obj) {
				var data = obj.data;
				if (obj.event === "cronEdit") {
					toCronEdit(obj.data.ID);
				} else if (obj.event === "cronStartTask") {
					startTask(obj.data.ID);
				} else if (obj.event === "cronStopTask") {
					stopTask(obj.data.ID);
				} else if (obj.event === "cronRunOnceTask") {
					runOnceTask(obj.data.ID);
				} else if (obj.event === "cronDel") {
					doCronDel(obj.data.ID);
				}
			});
		}
		
		//定时任务查询
		function cronQuery() {
			layui.table.reload("cronTable", {"where" : $.fn.my.serializeObj(cronQueryForm)});
		}
	
		//定时任务重置
		function cronReset() {
			cronQueryForm[0].reset();
			cronQuery();
		}
		
		//到达添加定时任务页面
		function toCronAdd() {
			$.ajax({
				url : "cron/toAdd",
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "添加定时任务",
						type : 1,
						area : ["800px", "500px"],
						content : obj,
						
						btn : ["添加", "取消"],
						yes : function(index, layero) {
							doCronAdd(index);
						},
						success: function(layero, index) {
							layui.form.render(null, "cronEditFrom");
						}
					});
				}
			});
		}

		//完成添加定时任务
		function doCronAdd(cronAddDialogIndex) {
			layui.form.on("submit(cronEditBtn)", function(data) {
				layer.confirm("确定要添加？", function(index) {
					$.ajax({
						url : "cron/doAdd",
						data : data.field,
						success : function(obj) {
							cronQuery();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
							layer.close(cronAddDialogIndex);
						}
					});
				});
			});
			$("[lay-filter='cronEditBtn']").click();
		}
		
		//到达修改定时任务页面
		function toCronEdit(id) {
			$.ajax({
				url : "cron/toEdit",
				data : {id : id},
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "修改定时任务",
						type : 1,
						area : ["800px", "500px"],
						content : obj,
						
						btn : ["修改", "取消"],
						yes : function(index, layero) {
							doCronEdit(index);
						},
						success: function(layero, index) {
							layui.form.render(null, "cronEditFrom");
						}
					});
				}
			});
		}

		//完成修改定时任务
		function doCronEdit(cronEditDialogIndex) {
			layui.form.on("submit(cronEditBtn)", function(data) {
				layer.confirm("确定要修改？", function(index) {
					$.ajax({
						url : "cron/doEdit",
						data : data.field,
						success : function(obj) {
							cronQuery();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
							layer.close(cronEditDialogIndex);
						}
					});
				});
			});
			$("[lay-filter='cronEditBtn']").click();
		}

		//完成删除定时任务
		function doCronDel(id) {
			layer.confirm("确定要删除？", function(index) {
				$.ajax({
					url : "cron/doDel",
					data : {id : id},
					success : function(obj) {
						cronQuery();
						
						if (!obj.succ) {
							layer.alert(obj.msg, {"title" : "提示消息"});
							return;
						}
						
						layer.close(index);
					}
				});
			});
		}
		
		//启动任务
		function startTask(id) {
			layer.confirm("确定要启动？", function(index) {
				$.ajax({
					url : "cron/startTask",
					data :  {id : id},
					success : function(obj) {
						cronQuery();
						
						if (!obj.succ) {
							layer.alert(obj.msg, {"title" : "提示消息"});
							return;
						}
						
						layer.close(index);
					}
				});
			});
		}
		
		//停止任务
		function stopTask(id) {
			layer.confirm("确定要停止？", function(index) {
				$.ajax({
					url : "cron/stopTask",
					data :  {id : id},
					success : function(obj) {
						cronQuery();
						
						if (!obj.succ) {
							layer.alert(obj.msg, {"title" : "提示消息"});
							return;
						}
						
						layer.close(index);
					}
				});
			});
		}
		
		//执行一次
		function runOnceTask(id) {
			layer.confirm("确定要执行一次？", function(index) {
				$.ajax({
					url : "cron/runOnceTask",
					data : {id : id},
					success : function(obj) {
						cronQuery();
						
						if (!obj.succ) {
							layer.alert(obj.msg, {"title" : "提示消息"});
							return;
						}
						
						layer.close(index);
					}
				});
			});
		}
	</script>
</html>
