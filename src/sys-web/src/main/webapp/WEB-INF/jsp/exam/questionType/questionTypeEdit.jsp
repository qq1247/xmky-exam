<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div lay-filter="dictEditFrom" class="layui-form" style="padding: 20px 0 0 0;">
	<input type="hidden" id="questionType_id" name="id" value="${questionType.id}" />
	<div class="layui-form-item">
		<label class="layui-form-label">名称：</label>
		<div class="layui-input-inline">
				<input name="name" value="${questionType.name}" class="layui-input">
		</div>
	</div>
	<div class="layui-form-item">
		<label class="layui-form-label">排序：</label>
		<div class="layui-input-inline">
				<input name="no" value="${questionType.no}" class="layui-input">
		</div>
	</div>
	<div class="layui-form-item layui-hide">
		<input type="button" lay-submit lay-filter="questionTypeEditBtn" value="确认">
	</div>
</div>
