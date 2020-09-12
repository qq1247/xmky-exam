<%@ page language="java" pageEncoding="utf-8"%>
<div lay-filter="examTypeEditFrom" class="layui-form" style="padding: 20px 0 0 0;">
	<input type="hidden" name="id" value="${examType.id }" />
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">上级分类：</label>
			<div class="layui-input-block">
				<input type="hidden" id="parentExamTypeId" name="parentId" value="${parentExamType.id }" />
				<input id="parentExamTypeName" name="parentName" value="${parentExamType.name }" 
					class="layui-input layui-disabled" lay-verify="required" readonly="readonly">
			</div>
		</div>
	</div>
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">名称：</label>
			<div class="layui-input-block">
				<input name="name" value="${examType.name }" 
					class="layui-input" lay-verify="required" placeholder="请输入名称">
			</div>
		</div>
	</div>
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">排序：</label>
			<div class="layui-input-block">
				<input name="no" value="${examType.no }" 
					class="layui-input" lay-verify="required|number" placeholder="请输入排序">
			</div>
		</div>
	</div>
	<div class="layui-form-item layui-hide">
		<input type="button" lay-submit lay-filter="examTypeEditBtn" value="确认">
	</div>
</div>
