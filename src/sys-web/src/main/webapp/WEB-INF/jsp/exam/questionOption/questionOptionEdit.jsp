<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div lay-filter="questionOptionEditFrom" class="layui-form" style="padding: 20px 0 0 0;">
	<input type="hidden" name="id" value="${questionOption.id}" />
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">选项A：</label>
			<div class="layui-input-block">
				<input name="optionA" value="${questionOption.optionA}" 
					class="layui-input" lay-verify="required">
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">选项B：</label>
			<div class="layui-input-block">
				<input name="optionB" value="${questionOption.optionB}" 
					class="layui-input" lay-verify="required">
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">选项C：</label>
			<div class="layui-input-block">
				<input name="optionC" value="${questionOption.optionC}" 
					class="layui-input" lay-verify="required">
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">选项D：</label>
			<div class="layui-input-block">
				<input name="optionD" value="${questionOption.optionD}" 
					class="layui-input" lay-verify="required">
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">选项E：</label>
			<div class="layui-input-block">
				<input name="optionE" value="${questionOption.optionE}" 
					class="layui-input" lay-verify="required">
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">选项F：</label>
			<div class="layui-input-block">
				<input name="optionF" value="${questionOption.optionF}" 
					class="layui-input" lay-verify="required">
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">选项G：</label>
			<div class="layui-input-block">
				<input name="optionG" value="${questionOption.optionG}" 
					class="layui-input" lay-verify="required">
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">试题ID：</label>
			<div class="layui-input-block">
				<input name="questionId" value="${questionOption.questionId}" 
					class="layui-input" lay-verify="required">
			</div>
		</div>
	</div>
	<div class="layui-form-item layui-hide">
		<input type="button" lay-submit lay-filter="questionOptionEditBtn" value="确认">
	</div>
</div>
