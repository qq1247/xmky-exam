<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div lay-filter="paperRemarkEditFrom" class="layui-form" style="padding: 20px 0 0 0;">
	<input type="hidden" name="id" value="${paperRemark.id}" />
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">分数A：</label>
			<div class="layui-input-block">
				<input name="scoreA" value="${paperRemark.scoreA}" 
					class="layui-input" lay-verify="required">
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">分数A评语：</label>
			<div class="layui-input-block">
				<input name="scoreARemark" value="${paperRemark.scoreARemark}" 
					class="layui-input" lay-verify="required">
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">分数B：</label>
			<div class="layui-input-block">
				<input name="scoreB" value="${paperRemark.scoreB}" 
					class="layui-input" lay-verify="required">
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">分数B评语：</label>
			<div class="layui-input-block">
				<input name="scoreBRemark" value="${paperRemark.scoreBRemark}" 
					class="layui-input" lay-verify="required">
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">分数C：</label>
			<div class="layui-input-block">
				<input name="scoreC" value="${paperRemark.scoreC}" 
					class="layui-input" lay-verify="required">
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">分数C评语：</label>
			<div class="layui-input-block">
				<input name="scoreCRemark" value="${paperRemark.scoreCRemark}" 
					class="layui-input" lay-verify="required">
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">分数D：</label>
			<div class="layui-input-block">
				<input name="scoreD" value="${paperRemark.scoreD}" 
					class="layui-input" lay-verify="required">
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">分数D评语：</label>
			<div class="layui-input-block">
				<input name="scoreDRemark" value="${paperRemark.scoreDRemark}" 
					class="layui-input" lay-verify="required">
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">分数E：</label>
			<div class="layui-input-block">
				<input name="scoreE" value="${paperRemark.scoreE}" 
					class="layui-input" lay-verify="required">
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">分数E评语：</label>
			<div class="layui-input-block">
				<input name="scoreERemark" value="${paperRemark.scoreERemark}" 
					class="layui-input" lay-verify="required">
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">试卷ID：</label>
			<div class="layui-input-block">
				<input name="paperId" value="${paperRemark.paperId}" 
					class="layui-input" lay-verify="required">
			</div>
		</div>
	</div>
	<div class="layui-form-item layui-hide">
		<input type="button" lay-submit lay-filter="paperRemarkEditBtn" value="确认">
	</div>
</div>
