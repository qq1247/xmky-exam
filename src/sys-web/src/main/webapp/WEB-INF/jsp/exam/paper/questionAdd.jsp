<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!-- <div lay-filter="questionEditFrom" class="layui-form" style="padding: 20px 0 0 0;"> -->
	<div class="layui-row layui-col-space10">
		<!-- <div class="layui-col-md2">
			<div class="layui-card">
				<div class="layui-form">
	      			<ul id="questionTypeTree" class="ztree"></ul>
	 			</div>
			</div>
		</div>
		<div class="layui-col-md10"> -->
			<div class="layui-card">
				<%-- 试题查询条件 --%>
				<form id="questionQueryForm" lay-filter="questionQueryForm" class="layui-form layui-card-header layuiadmin-card-header-auto">
					<input type="hidden" id="questionOne" name="one">
					<div class="layui-form-item ">
						<div class="layui-inline">
							<input type="text" name="two" placeholder="请输入编号" class="layui-input">
						</div>
						<div class="layui-inline">
							<input type="text" name="three" placeholder="请输入题干" class="layui-input">
						</div>
						<div class="layui-inline">
							<input type="text" name="seven" placeholder="请输入试题分类" class="layui-input">
						</div>
						<div class="layui-inline">
							<input type="text" name="eight" placeholder="请输入默认分值" class="layui-input">
						</div>
						<div class="layui-inline" style="width: 156px;">
							<select name="five">
								<option value="">请选择类型</option>
								<c:forEach var="questionTypeDict" items="${QUESTION_TYPE_DICT_LIST }">
								<option value="${questionTypeDict.dictKey }">${questionTypeDict.dictValue }</option>
								</c:forEach>
			      			</select>
						</div>
						<div class="layui-inline" style="width: 156px;">
							<select name="six">
								<option value="">请选择难度</option>
								<c:forEach var="questionDifficultyDict" items="${QUESTION_DIFFICULTY_DICT_LIST }">
								<option value="${questionDifficultyDict.dictKey }">${questionDifficultyDict.dictValue }</option>
								</c:forEach>
			      			</select>
						</div>
						
						<div class="layui-inline">
							<button type="button" class="layui-btn layuiadmin-btn-useradmin" onclick="questionQuery();">
								<i class="layui-icon layuiadmin-button-btn"></i>查询
							</button>
							<button type="button" class="layui-btn layui-btn-primary layuiadmin-btn-useradmin" onclick="questionReset();">
								<i class="layui-icon layuiadmin-button-btn"></i>重置
							</button>
						</div>
					</div>
				</form>
				<div class="layui-card-body">
					<div style="padding-bottom: 10px;">
					</div>
					<script type="text/html" id="questionToolbar">
						<my:auth url="paper/toQuestionAdd"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="questionAdd"><i class="layui-icon layui-icon-edit"></i>添加</a></my:auth>
					</script>
					<%-- 试题数据表格 --%>
					<table id="questionTable" lay-filter="questionTable"></table>
				</div>
			</div>
		<!-- </div> -->
	</div>
<!-- </div> -->
