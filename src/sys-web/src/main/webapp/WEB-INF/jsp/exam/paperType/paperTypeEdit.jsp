<%@ page language="java" pageEncoding="utf-8"%>
<div lay-filter="paperTypeEditFrom" class="layui-form" style="padding: 20px 0 0 0;">
	<input type="hidden" name="id" value="${paperType.id }" />
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">上级分类：</label>
			<div class="layui-input-block">
				<input type="hidden" id="parentPaperTypeId" name="parentId" value="${parentPaperType.id }" />
				<input id="parentPaperTypeName" name="parentName" value="${parentPaperType.name }" 
					class="layui-input layui-disabled" lay-verify="required" readonly="readonly">
			</div>
		</div>
	</div>
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">名称：</label>
			<div class="layui-input-block">
				<input name="name" value="${paperType.name }" 
					class="layui-input" lay-verify="required" placeholder="请输入名称">
			</div>
		</div>
	</div>
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">排序：</label>
			<div class="layui-input-block">
				<input name="no" value="${paperType.no }" 
					class="layui-input" lay-verify="required|number" placeholder="请输入排序">
			</div>
		</div>
	</div>
	<div class="layui-form-item layui-hide">
		<input type="button" lay-submit lay-filter="paperTypeEditBtn" value="确认">
	</div>
</div>
