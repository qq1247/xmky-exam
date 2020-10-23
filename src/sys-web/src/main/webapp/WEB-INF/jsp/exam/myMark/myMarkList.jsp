<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>我的阅卷列表</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
		<div class="layui-fluid">
			<div class="layui-card">
				<%-- 我的阅卷查询条件 --%>
				<form id="myMarkQueryForm" class="layui-form layui-card-header layuiadmin-card-header-auto">
					<input type="hidden" id="myMarkOne" name="one">
					<div class="layui-form-item ">
						<div class="layui-inline">
							<input type="text" name="three" placeholder="请输入名称" class="layui-input">
						</div>
						<div class="layui-inline">
							<button type="button" class="layui-btn layuiadmin-btn-useradmin" onclick="myMarkQuery();">
								<i class="layui-icon layuiadmin-button-btn"></i>查询
							</button>
							<button type="button" class="layui-btn layui-btn-primary layuiadmin-btn-useradmin" onclick="myMarkReset();">
								<i class="layui-icon layuiadmin-button-btn"></i>重置
							</button>
						</div>
					</div>
				</form>
				<div class="layui-card-body">
					<script type="text/html" id="myMarkToolbar">
						{{#  if (d.EXAM_HAND == "AWAIT") { }}
						{{#  } }}
						{{#  if (d.EXAM_HAND == "START") { }}
						<my:auth url="myMark/doAutoMark"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="myMarkAutoMark"><i class="layui-icon layui-icon-edit"></i>自动阅卷</a></my:auth>
						{{#  } }}
						{{#  if (d.EXAM_HAND == "START" || d.EXAM_HAND == "END") { }}
						<my:auth url="myMark/toDetailList"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="myMarkPaperList"><i class="layui-icon layui-icon-edit"></i>试卷列表</a></my:auth>
						{{#  } }}
					</script>
					<%-- 我的阅卷数据表格 --%>
					<table id="myMarkTable" lay-filter="myMarkTable"></table>
				</div>
			</div>
		</div>
	</body>
	<%@include file="/script/myJs/tail.jspf"%>
	<script type="text/javascript">
		//定义变量
		var myMarkQueryForm = $("#myMarkQueryForm"); //我的阅卷查询对象
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initMyMarkTable();
		});
	
		//初始化我的阅卷表格
		function initMyMarkTable() {
			layui.table.render({
				elem : "#myMarkTable",
				url : "myMark/list",
				cols : [[
						{field : "NAME", title : "考试名称", align : "center"},
						{field : "START_TIME", title : "考试时间", align : "center", templet : function(d) {
							var startTime = new Date(d.START_TIME_STR);
							var endTime = new Date(d.END_TIME_STR);
							return d.START_TIME_STR + "（"+Math.round((endTime.getTime() - startTime.getTime()) / 60000)+"分钟）";
						}},
						{field : "PASS_SCORE", title : "及格分数/总分数", align : "center", templet : function(d) {
							return d.PASS_SCORE + "/" + d.PAPER_TOTLE_SCORE;
						}},
						{field : "USER_NUM", title : "参考人数", align : "center"},
						{fixed : "right", title : "操作 ", toolbar : "#myMarkToolbar", align : "center", width : 280}
						]],
				page : true,
				height : "full-180",
				method : "post",
				defaultToolbar : [],
				parseData : function(myMark) {
					return {
						"code" : myMark.succ,
						"msg" : myMark.msg,
						"count" : myMark.data.total,
						"data" : myMark.data.rows
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
			layui.table.on("rowDouble(myMarkTable)", function(obj) {
			});
			layui.table.on("tool(myMarkTable)", function(obj) {
				var data = obj.data;
				if (obj.event === "myMarkAutoMark") {
					doMyMarkAutoMark(obj.data.ID);
				} else if (obj.event === "myMarkPaperList") {
					toMyMarkPaperList(obj.data.ID);
				}
			});
		}
		
		//我的阅卷查询
		function myMarkQuery() {
			layui.table.reload("myMarkTable", {"where" : $.fn.my.serializeObj(myMarkQueryForm)});
		}
	
		//我的阅卷重置
		function myMarkReset() {
			myMarkQueryForm[0].reset();
			myMarkQuery();
		}
		
		// 到达我的阅卷列表页面
		function toMyMarkPaperList(examId, examName) {
			$.ajax({
				url : "myMark/toDetailList",
				data : {examId : examId},
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : examName,
						area : ["800px", "500px"],
						content : obj,
						btn : [],
						type : 1,
						yes : function(index, layero) {
							
						},
						success: function(layero, index) {
							layui.layer.full(index);
							setTimeout("initMyExamTable()", 200);
						}
					});
				}
			});
		}
		
		// 自动阅卷
		function doMyMarkAutoMark(examId) {
			$.ajax({
				url : "myMark/doAutoMark",
				data : {examId : examId},
				success : function(obj) {
					if (!obj.succ) {
						layer.alert(obj.msg, {"title" : "提示消息"});
						return;
					}

					var progressIndex = layer.open({
			    	    type: 0,
			    	    title: false,
			    	    closeBtn: 0,
			    	    btn: false,
			    	    content: '<div lay-filter="progress" class="layui-progress layui-progress-big" lay-showPercent="true"><div class="layui-progress-bar" lay-percent="0%"><span class="layui-progress-text">0%</span></div></div>'
			    	});
					
					var timer = setInterval(function() {
						$.ajax({
							url : "progressBar/get",
							data : {id : obj.data},
							success : function(obj) {
								if (!obj.succ) {
									layer.alert(obj.msg, {"title" : "提示消息"});
									clearInterval(timer);
									return;
								}
								
								layui.element.progress("progress", obj.data.percent + "%");
								if (obj.data.curNum  == obj.data.totalNum) {
		    						clearInterval(timer);
		    						layer.close(progressIndex);
		    						layer.alert(obj.data.msg, {"title" : "提示消息"});
		                            return;
		    					}
							}
						});
					}, 1000);
				}
			});
		}
		
		//初始化我的阅卷详细表格
		function initMyExamTable() {
			layui.table.render({
				elem : "#myMarkDetailTable",
				url : "myMark/detailList",
				cols : [[
						{field : "USER_NAME", title : "用户", align : "center"},
						{field : "MY_EXAM_TOTAL_SCORE", title : "分数", align : "center", templet : function(d) {
							if (d.MY_EXAM_ANSWER_STATE == 2) {
								return '<span style="color: red;">'+d.MY_EXAM_TOTAL_SCORE+'（'+d.MY_EXAM_ANSWER_STATE_NAME+'）</span>';
							}
							return d.MY_EXAM_TOTAL_SCORE;
						}},
						{field : "MY_EXAM_STATE_NAME", title : "状态", align : "center", templet : function(d) {
							return d.MY_EXAM_STATE_NAME + "/" + d.MY_EXAM_MARK_STATE_NAME;
						}},
						{field : "USER_REMARK", title : "评语", align : "center"},
						{fixed : "right", title : "操作 ", toolbar : "#myMarkDetailToolbar", align : "center"}
						]],
				page : true,
				height : "full-180",
				method : "post",
				defaultToolbar : [],
				parseData : function(myMarkDetail) {
					return {
						"code" : myMarkDetail.succ,
						"msg" : myMarkDetail.msg,
						"count" : myMarkDetail.data.total,
						"data" : myMarkDetail.data.rows
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
			layui.table.on("tool(myMarkDetailTable)", function(obj) {
				var data = obj.data;
				if (obj.event === "myMarkToMark") {
					toMyMarkMark(obj.data.ID);
				}
			});
		}
		
		//我的阅卷详细查询
		function myMarkDetailQuery() {
			var myMarkDetailQueryForm = $("#myMarkQueryForm");
			layui.table.reload("myMarkDetailTable", {"where" : $.fn.my.serializeObj(myMarkDetailQueryForm)});
		}
	
		//我的阅卷详细重置
		function myMarkDetailReset() {
			var myMarkDetailQueryForm = $("#myMarkQueryForm");
			myMarkDetailQueryForm[0].reset();
			myMarkDetailQuery();
		}
		
		// 到达我的阅卷页面
		function toMyMarkMark(myExamId) {
			window.open("myMark/toMark?myExamId=" + myExamId);
		}
	</script>
</html>
