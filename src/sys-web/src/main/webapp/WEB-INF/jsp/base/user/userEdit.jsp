<%@ page language="java" pageEncoding="utf-8"%>
<div lay-filter="userEditFrom" class="layui-form" style="padding: 20px 0 0 0;">
	<input type="hidden" id="user_id" name="id" value="${user.id }" />
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">上级组织机构：</label>
			<div class="layui-input-block">
				<input type="hidden" id="user_orgId" name="orgId" value="${org.id }"/>
				<input id="user_orgName" name="orgName" value="${org.name }" 
					class="layui-input layui-disabled" lay-verify="required" readonly="readonly">
			</div>
		</div>
	</div>
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">昵称：</label>
			<div class="layui-input-block">
				<input name="name" value="${user.name }" 
					class="layui-input" lay-verify="required" placeholder="请输入昵称">
			</div>
		</div>
	</div>
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">登录名称：</label>
			<div class="layui-input-block">
				<input name="loginName" value="${user.loginName }" 
					class="layui-input" lay-verify="required" placeholder="请输入登录名称">
			</div>
		</div>
	</div>
	<div class="layui-form-item layui-hide">
		<input type="button" lay-submit lay-filter="userEditBtn" value="确认">
	</div>
</div>
