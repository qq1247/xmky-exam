<%@ page language="java" pageEncoding="utf-8"%>
<div lay-filter="dictEditFrom" class="layui-form" style="padding: 20px 0 0 0;">
	<input type="hidden" name="id" value="${dict.id }" />
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">索引：</label>
			<div class="layui-input-block">
				<input name="dictIndex" value="${dict.dictIndex }" 
					class="layui-input" lay-verify="required" placeholder="请输入索引">
			</div>
		</div>
	</div>
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">键：</label>
			<div class="layui-input-block">
				<input name="dictKey" value="${dict.dictKey }" 
					class="layui-input" lay-verify="required" placeholder="请输入键">
			</div>
		</div>
	</div>
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">值：</label>
			<div class="layui-input-block">
				<input name="dictValue" value="${dict.dictValue }" 
					class="layui-input" lay-verify="required" placeholder="请输入值">
			</div>
		</div>
	</div>
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">排序：</label>
			<div class="layui-input-block">
				<input name="no" value="${dict.no }" 
					class="layui-input" lay-verify="required|number" placeholder="请输入排序">
			</div>
		</div>
	</div>
	<div class="layui-form-item layui-hide">
		<input type="button" lay-submit lay-filter="dictEditBtn" value="确认">
	</div>
</div>
