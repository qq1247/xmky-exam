<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>成绩列表</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'center',border:false">
				<%-- 成绩查询条件 --%>
				<div id="gradeReportToolbar" style="padding:0 30px;">
					<div class="conditions">
						<form id="gradeReportQueryForm">
							<span class="con-span">考试名称：</span>
							<input name="two" class="easyui-textbox" style="width:166px;height:35px;line-height:35px;">
							<span class="con-span">试卷名称：</span>
							<input name="three" class="easyui-textbox" style="width:166px;height:35px;line-height:35px;">
							<span class="con-span">考试用户：</span>
							<input name="four" class="easyui-textbox" style="width:166px;height:35px;line-height:35px;">
							<span class="con-span">阅卷用户：</span>
							<input name="five" class="easyui-textbox" style="width:166px;height:35px;line-height:35px;">
							<span class="con-span">考试状态：</span>
							<select name="six" class="easyui-combobox" data-options="editable:false" style="width:166px;height:35px;line-height:35px;">
								<option value=""></option>
								<c:forEach var="dict" items="${EXAM_USER_STATE }">
								<option value="${dict.dictKey }">${dict.dictValue }</option>
								</c:forEach>
							</select>
							<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" data-options="selected:true" onclick="gradeReportQuery();">查询</a>
							<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-clear" onclick="gradeReportReset();">重置</a>
						</form>
					</div>
					<div class="opt-buttons">
					</div>
				</div>
				<%-- 成绩数据表格 --%>
				<table id="gradeReportGrid">
				</table>
			</div>
		</div>
	</body>
	<script type="text/javascript">
		//定义变量
		var gradeReportGrid = $("#gradeReportGrid"); //成绩表格对象
		var gradeReportQueryForm = $("#gradeReportQueryForm"); //成绩查询对象
	
		//页面加载完毕，执行如下方法：
		$(function() {
			initGradeReportGrid();
		});
	
		//初始化成绩表格
		function initGradeReportGrid() {
			gradeReportGrid.datagrid( {
				url : "gradeReport/list",
				toolbar : "#gradeReportToolbar",
				columns : [[ 
						{field : "ID", title : "", checkbox : true}, 
						{field : "EXAM_NAME", title : "考试名称", width : 80, align : "center"},
						{field : "EXAM_START_TIME_STR", title : "开始时间", width : 80, align : "center"},
						{field : "EXAM_END_TIME_STR", title : "结束时间", width : 80, align : "center"},
						{field : "PAPER_NAME", title : "试卷名称", width : 80, align : "center"},
						{field : "USER_NAME", title : "考试用户", width : 80, align : "center"},
						{field : "TOTAL_SCORE", title : "考试得分", width : 80, align : "center"},
						{field : "EXAM_USER_STATE_NAME", title : "考试状态", width : 80, align : "center"},
						{field : "MARK_USER_NAME", title : "阅卷用户", width : 80, align : "center"},
						{field : "EXAM_USER_UPDATE_MARK_TIME_STR", title : "阅卷时间", width : 80, align : "center"}
						]]
			});
		}
	
		//成绩查询
		function gradeReportQuery() {
			gradeReportGrid.datagrid("uncheckAll");
			gradeReportGrid.datagrid("load", $.fn.my.serializeObj(gradeReportQueryForm));
		}
	
		//成绩重置
		function gradeReportReset() {
			gradeReportQueryForm.form("reset");
			gradeReportQuery();
		}
	</script>
</html>