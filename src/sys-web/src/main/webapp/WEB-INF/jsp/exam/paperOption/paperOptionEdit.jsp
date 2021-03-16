<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div lay-filter="paperOptionEditFrom" class="layui-form" style="padding: 20px 0 0 0;">
	<input type="hidden" name="id" value="${paperOption.id}" />
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">1：正常；2：乱序：</label>
			<div class="layui-input-block">
				<input name="question" value="${paperOption.question}" 
					class="layui-input" lay-verify="required">
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">1：正常；2：乱序：</label>
			<div class="layui-input-block">
				<input name="questionOption" value="${paperOption.questionOption}" 
					class="layui-input" lay-verify="required">
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">1：正常；2：禁用：</label>
			<div class="layui-input-block">
				<input name="rightClick" value="${paperOption.rightClick}" 
					class="layui-input" lay-verify="required">
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">1：正常；2：禁用：</label>
			<div class="layui-input-block">
				<input name="rightCopy" value="${paperOption.rightCopy}" 
					class="layui-input" lay-verify="required">
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">最小化警告：</label>
			<div class="layui-input-block">
				<input name="minimize" value="${paperOption.minimize}" 
					class="layui-input" lay-verify="required">
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">最小化警告次数：</label>
			<div class="layui-input-block">
				<input name="minimizeNum" value="${paperOption.minimizeNum}" 
					class="layui-input" lay-verify="required">
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">试卷ID：</label>
			<div class="layui-input-block">
				<input name="paperId" value="${paperOption.paperId}" 
					class="layui-input" lay-verify="required">
			</div>
		</div>
	</div>
	<div class="layui-form-item layui-hide">
		<input type="button" lay-submit lay-filter="paperOptionEditBtn" value="确认">
	</div>
</div>
