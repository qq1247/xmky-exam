<%@ page language="java" pageEncoding="utf-8"%>
<div lay-filter="orgEditFrom" class="layui-form" style="padding: 20px 0 0 0;">
	<input type="hidden" id="org_id" name="id" value="${org.id }" />
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">上级组织机构：</label>
			<div class="layui-input-block">
				<input type="hidden" id="org_parentId" name="parentId" value="${pOrg.id }"/>
				<input id="org_parentName" name="parentName" value="${pOrg.name }" 
					class="layui-input layui-disabled" lay-verify="required" readonly="readonly">
			</div>
		</div>
	</div>
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">名称：</label>
			<div class="layui-input-block">
				<input name="name" value="${org.name }" 
					class="layui-input" lay-verify="required" placeholder="请输入名称">
			</div>
		</div>
	</div>
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">编码：</label>
			<div class="layui-input-block">
				<input name="code" value="${org.code }" 
					class="layui-input" lay-verify="" placeholder="请输入唯一编码">
			</div>
		</div>
	</div>
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">排序：</label>
			<div class="layui-input-block">
				<input name="no" value="${org.no }" 
					class="layui-input" lay-verify="required|number" placeholder="请输入排序">
			</div>
		</div>
	</div>
	<div class="layui-form-item layui-hide">
		<input type="button" lay-submit lay-filter="orgEditBtn">
	</div>
</div>
