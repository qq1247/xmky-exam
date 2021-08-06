<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>我的考试列表</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
		<div class="layui-fluid">
			<div class="layui-card">
				<%-- 我的考试查询条件 --%>
				<form id="myExamQueryForm" class="layui-form layui-card-header layuiadmin-card-header-auto">
					<input type="hidden" id="myExamOne" name="one">
					<div class="layui-form-item ">
						<div class="layui-inline">
							<input type="text" name="three" placeholder="请输入名称" class="layui-input">
						</div>
						<div class="layui-inline">
							<button type="button" class="layui-btn layuiadmin-btn-useradmin" onclick="myExamQuery();">
								<i class="layui-icon layuiadmin-button-btn"></i>查询
							</button>
							<button type="button" class="layui-btn layui-btn-primary layuiadmin-btn-useradmin" onclick="myExamReset();">
								<i class="layui-icon layuiadmin-button-btn"></i>重置
							</button>
						</div>
					</div>
				</form>
				<div class="layui-card-body">
					<script type="text/html" id="myExamToolbar">
						{{#  if (d.EXAM_HAND == "AWAIT") { }}
						{{#  } else if (d.EXAM_HAND == "START") { }}
						<my:auth url="myExam/toExam"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="myExamStart"><i class="layui-icon layui-icon-edit"></i>开始考试</a></my:auth>
						{{#  } else if (d.EXAM_HAND == "AWAIT") { }}
						<my:auth url="myExam/toExam"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="myExamReview"><i class="layui-icon layui-icon-edit"></i>预览</a></my:auth>
						{{#  } }}
					</script>
					<%-- 我的考试数据表格 --%>
					<table id="myExamTable" lay-filter="myExamTable"></table>
				</div>
			</div>
		</div>
	</body>
	<%@include file="/script/myJs/tail.jspf"%>
	<script type="text/javascript">
		//定义变量
		var myExamQueryForm = $("#myExamQueryForm"); //我的考试查询对象
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initMyExamTable();
		});
	
		//初始化我的考试表格
		function initMyExamTable() {
			layui.table.render({
				elem : "#myExamTable",
				url : "myExam/list",
				cols : [[
						{field : "EXAM_NAME", title : "试卷名称", align : "center"},
						{field : "EXAM_START_TIME", title : "考试时间", align : "center", width : 300, templet : function(d) {
							var startTime = new Date(d.EXAM_START_TIME_STR);
							var endTime = new Date(d.EXAM_END_TIME_STR);
							return d.EXAM_START_TIME_STR + "（"+Math.round((endTime.getTime() - startTime.getTime()) / 60000)+"分钟）";
						}},
						{field : "MY_EXAM_TOTAL_SCORE", title : "分数/总分数", align : "center", templet : function(d) {
							if (d.MY_EXAM_ANSWER_STATE == 2) {
								return '<span style="color: red;">'+d.MY_EXAM_TOTAL_SCORE+'/'+d.PAPER_TOTAL_SCORE+'（'+d.MY_EXAM_ANSWER_STATE_NAME+'）</span>';
							}
							return d.MY_EXAM_TOTAL_SCORE+'/'+d.PAPER_TOTAL_SCORE;
						}},
						{field : "MY_EXAM_STATE_NAME", title : "状态", align : "center", templet : function(d) {
							return d.MY_EXAM_STATE_NAME + "/" + d.MY_EXAM_MARK_STATE_NAME;
						}},
						{field : "USER_REMARK", title : "评语", align : "center"},
						{field : "USER_NUM", title : "参考人数", align : "center"},
						{fixed : "right", title : "操作 ", toolbar : "#myExamToolbar", align : "center", width : 280}
						]],
				page : true,
				height : "full-180",
				method : "post",
				defaultToolbar : [],
				parseData : function(myExam) {
					return {
						"code" : myExam.succ,
						"msg" : myExam.msg,
						"count" : myExam.data.total,
						"data" : myExam.data.rows
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
			layui.table.on("rowDouble(myExamTable)", function(obj) {
			});
			layui.table.on("tool(myExamTable)", function(obj) {
				var data = obj.data;
				if (obj.event === "myExamStart") {
					toMyExamPaper(obj.data.ID);
				}
			});
		}
		
		//我的考试查询
		function myExamQuery() {
			layui.table.reload("myExamTable", {"where" : $.fn.my.serializeObj(myExamQueryForm)});
		}
	
		//我的考试重置
		function myExamReset() {
			myExamQueryForm[0].reset();
			myExamQuery();
		}
		
		// 到达我的试卷页面
		function toMyExamPaper(myExamId) {
			window.open("myExam/toExam?myExamId=" + myExamId);
		}
		
		// 完成配置我的考试
		function doMyExamCfg(toMyExamCfgDialogIndex) {
			layui.form.on("submit(myExamCfgBtn)", function(data) {
				layer.confirm("确定要配置？", function(index) {
					$.ajax({
						url : "myExam/doCfg",
						data : data.field,
						success : function(obj) {
							myExamQuery();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
						}
					});
				});
			});
			$("[lay-filter='myExamCfgBtn']").click();
		}
	</script>
</html>
