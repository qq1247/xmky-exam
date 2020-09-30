<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<div lay-filter="paperEditFrom" class="layui-form" style="padding: 20px 0 0 0;">
	<input type="hidden" name="id" value="${paper.id}" />
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">试卷分类：</label>
			<div class="layui-input-block">
				<input type="hidden" id="paperTypeId" name="paperTypeId" value="${paperType.id}">
				<input id="paperTypeName" name="paperTypeName" value="${paperType.name}" class="layui-input layui-disabled" lay-verify="required"
					readonly="readonly">
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">名称：</label>
			<div class="layui-input-block">
				<input name="name" value="${paper.name}" class="layui-input" lay-verify="required" placeholder="请输入名称">
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">及格分数：</label>
			<div class="layui-input-block">
				<input name="passScore" value="${paper.passScore}" 
					class="layui-input" lay-verify="required|number" placeholder="请输入及格分数">
			</div>
		</div>
	</div>
	<%-- <div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">展示方式：</label>
			<div class="layui-input-block">
				<c:forEach var="dict" items="${QUESTION_TYPE_DICT_LIST }">
					<input type="radio" name="previewType" value="${dict.dictKey }" title="${dict.dictValue }"
						${paper.previewType == dict.dictKey ? "checked" : "" }>
				</c:forEach>
			</div>
		</div>
	</div> --%>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">成绩评语：</label>
			<div class="layui-form-mid">大于等于</div>
			<div class="layui-input-inline" style="width: 60px;">
				<input name=scoreA value="${paper.scoreA }"
					class="layui-input" lay-verify="required|number" placeholder="分值">
			</div>
			<div class="layui-form-mid">评语</div>
			<div class="layui-input-inline" style="width: 368px;">
				<input name="scoreARemark" value="${paper.scoreARemark}" 
					class="layui-input" lay-verify="required" placeholder="请输入评语">
			</div>
			<div class="layui-input-inline" style="width: 55px;margin-right: 0px;">
			</div>
		</div>
		<div class="layui-col-md11" style="padding-top: 5px;">
			<label class="layui-form-label"></label>
			<div class="layui-form-mid">大于等于</div>
			<div class="layui-input-inline" style="width: 60px;">
				<input name=scoreB value="${paper.scoreB }"
					class="layui-input" lay-verify="required|number" placeholder="分值">
			</div>
			<div class="layui-form-mid">评语</div>
			<div class="layui-input-inline" style="width: 368px;">
				<input name="scoreBRemark" value="${paper.scoreBRemark}" 
					class="layui-input" lay-verify="required" placeholder="请输入评语">
			</div>
			<div class="layui-input-inline" style="width: 55px;margin-right: 0px;">
				<button type="button" class="layui-btn layui-btn-primary" onclick="addScoreRemark(this);">
					<i class="layui-icon layui-icon-addition"></i>
				</button>
			</div>
		</div>
		<c:forEach var="lab" items="${fn:split('C,D,E', ',') }">
		<c:set var="scoreX" value="score${lab }"></c:set>
		<c:set var="scoreXRemark" value="score${lab }Remark"></c:set>
		<c:if test="${!empty paper[scoreX] }">
		<div class="layui-col-md11" style="padding-top: 5px;">
			<label class="layui-form-label"></label>
			<div class="layui-form-mid">大于等于</div>
			<div class="layui-input-inline" style="width: 60px;">
				<input name=${scoreX } value="${paper[scoreX] }"
					class="layui-input" lay-verify="required|number" placeholder="分值">
			</div>
			<div class="layui-form-mid">评语</div>
			<div class="layui-input-inline" style="width: 368px;">
				<input name="${scoreXRemark }" value="${paper[scoreXRemark] }" 
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
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">状态：</label>
			<div class="layui-input-block">
				<input type="checkbox" name="state" value="1"
					lay-skin="switch"  lay-text="启用|禁用" ${paper.state == 1 ? "checked" : "" }>
				
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">描述：</label>
			<div class="layui-input-block">
				<script id="description" name="description" type="text/plain" style="width: 100%; height: 50px;">${paper.description }</script>
			</div>
		</div>
	</div>
	<div class="layui-form-item layui-hide">
		<input type="button" lay-submit lay-filter="paperEditBtn" value="确认">
	</div>
</div>
