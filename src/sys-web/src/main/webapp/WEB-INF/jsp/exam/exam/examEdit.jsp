<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<div lay-filter="examEditFrom" class="layui-form" style="padding: 20px 0 0 0;">
	<input type="hidden" name="id" value="${exam.id}" />
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">考试分类：</label>
			<div class="layui-input-block">
				<input type="hidden" id="examTypeId" name="examTypeId" value="${examType.id}">
				<input id="examTypeName" name="examTypeName" value="${examType.name}" 
					class="layui-input layui-disabled" lay-verify="required" readonly="readonly">
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">名称：</label>
			<div class="layui-input-block">
				<input name="name" value="${exam.name}" 
					class="layui-input" lay-verify="required" placeholder="请输入名称">
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">试卷：</label>
			<div class="layui-input-block">
				<input type="hidden" id="_paperId" value="${paper.id }">
				<input type="hidden" id="_paperName" value="${paper.name }">
				<input type="hidden" id="_paperTypeName" value="${paperType.name }">
				<div id="paperId"></div>
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">及格分数：</label>
			<div class="layui-input-block">
				<input id="passScore" name="passScore" value="${exam.passScore}" 
					class="layui-input" lay-verify="required|number" placeholder="请输入及格分数">
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">考试时间：</label>
			<div class="layui-input-inline">
				<input id="startTime" name="startTime" value="${fn:substring(exam.startTime, 0, 19) }" 
					class="layui-input" lay-verify="required" placeholder="请输入开始日期">
			</div>
			<div class="layui-form-mid">-</div>
			<div class="layui-input-inline">
				<input id="endTime" name="endTime" value="${fn:substring(exam.endTime, 0, 19) }"
					class="layui-input" lay-verify="required" placeholder="请输入结束日期">
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">阅卷时间：</label>
			<div class="layui-input-inline">
				<input id="markStartTime" name="markStartTime" value="${fn:substring(exam.markStartTime, 0, 19) }"
					class="layui-input" lay-verify="required" placeholder="请输入开始日期">
			</div>
			<div class="layui-form-mid">-</div>
			<div class="layui-input-inline">
				<input id="markEndTime" name="markEndTime" value="${fn:substring(exam.markEndTime, 0, 19) }"
					class="layui-input" lay-verify="required" placeholder="请输入结束日期">
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">成绩评语：</label>
			<div class="layui-form-mid">大于等于</div>
			<div class="layui-input-inline" style="width: 60px;">
				<input name=scoreA value="${exam.scoreA }"
					class="layui-input" lay-verify="required|number" placeholder="分值">
			</div>
			<div class="layui-form-mid">评语</div>
			<div class="layui-input-inline" style="width: 368px;">
				<input name="scoreARemark" value="${exam.scoreARemark}" 
					class="layui-input" lay-verify="required" placeholder="请输入评语">
			</div>
			<div class="layui-input-inline" style="width: 55px;margin-right: 0px;">
			</div>
		</div>
		<div class="layui-col-md11" style="padding-top: 5px;">
			<label class="layui-form-label"></label>
			<div class="layui-form-mid">大于等于</div>
			<div class="layui-input-inline" style="width: 60px;">
				<input name=scoreB value="${exam.scoreB }"
					class="layui-input" lay-verify="required|number" placeholder="分值">
			</div>
			<div class="layui-form-mid">评语</div>
			<div class="layui-input-inline" style="width: 368px;">
				<input name="scoreBRemark" value="${exam.scoreBRemark}" 
					class="layui-input" lay-verify="required" placeholder="请输入评语">
			</div>
			<div class="layui-input-inline" style="width: 55px;margin-right: 0px;">
				<button type="button" id="addScoreRemarkBtn" class="layui-btn layui-btn-primary" onclick="addScoreRemark(this);">
					<i class="layui-icon layui-icon-addition"></i>
				</button>
			</div>
		</div>
		<c:forEach var="lab" items="${fn:split('C,D,E', ',') }">
		<c:set var="scoreX" value="score${lab }"></c:set>
		<c:set var="scoreXRemark" value="score${lab }Remark"></c:set>
		<c:if test="${!empty exam[scoreX] }">
		<div class="layui-col-md11" style="padding-top: 5px;">
			<label class="layui-form-label"></label>
			<div class="layui-form-mid">大于等于</div>
			<div class="layui-input-inline" style="width: 60px;">
				<input name=${scoreX } value="${exam[scoreX] }"
					class="layui-input" lay-verify="required|number" placeholder="分值">
			</div>
			<div class="layui-form-mid">评语</div>
			<div class="layui-input-inline" style="width: 368px;">
				<input name="${scoreXRemark }" value="${exam[scoreXRemark] }" 
					class="layui-input" lay-verify="required" placeholder="请输入评语">
			</div>
			<div class="layui-input-inline" style="width: 55px;margin-right: 0px;">
				<button type="button" class="layui-btn layui-btn-primary" onclick="delScoreRemark(this);">
					<i class="layui-icon layui-icon-subtraction"></i>
				</button>
			</div>
		</div>
		</c:if>
		</c:forEach>
	</div>
	<%-- <div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">状态：</label>
			<div class="layui-input-block">
				<input type="checkbox" name="state" value="1"
					lay-skin="switch"  lay-text="发布|草稿" ${exam.state == 1 ? "checked" : "" }>
				
			</div>
		</div>
	</div> --%>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">描述：</label>
			<div class="layui-input-block">
				<script id="description" name="description" type="text/plain" style="width: 100%; height: 50px;">${exam.description }</script>
			</div>
		</div>
	</div>
	<div class="layui-form-item layui-hide">
		<input type="button" lay-submit lay-filter="examEditBtn" value="确认">
	</div>
</div>
