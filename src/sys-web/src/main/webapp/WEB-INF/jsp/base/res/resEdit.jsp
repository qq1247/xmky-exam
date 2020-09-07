<%@ page language="java" pageEncoding="utf-8"%>
<div lay-filter="resEditFrom" class="layui-form" style="padding: 20px 0 0 0;">
	<input type="hidden" id="res_id" name="id" value="${res.id }" />
	<input type="hidden" id="res_type" name="type" value="${res.type }" />
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">上级资源：</label>
			<div class="layui-input-block">
				<input type="hidden" id="res_parentId" name="parentId" value="${pRes.id }" />
				<input id="res_parentName" name="parentName" value="${pRes.name }" 
					class="layui-input layui-disabled" lay-verify="required" readonly="readonly">
			</div>
		</div>
	</div>
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">名称：</label>
			<div class="layui-input-block">
				<input name="name" value="${res.name }" 
					class="layui-input" lay-verify="required" placeholder="请输入名称">
			</div>
		</div>
	</div>
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">url：</label>
			<div class="layui-input-block">
				<input name="url" value="${res.url }" 
					class="layui-input" lay-verify="required" placeholder="请输入url">
			</div>
		</div>
	</div>
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">排序：</label>
			<div class="layui-input-block">
				<input name="no" value="${res.no }" 
					class="layui-input" lay-verify="required|number" placeholder="请输入排序">
			</div>
		</div>
	</div>
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">图标：</label>
			<div class="layui-input-block">
				<input name="icon" value="${res.icon }" 
					class="layui-input" placeholder="请输入图标">
			</div>
		</div>
	</div>
	<div class="layui-form-item layui-hide">
		<input type="button" lay-submit lay-filter="resEditBtn">
	</div>
</div>
