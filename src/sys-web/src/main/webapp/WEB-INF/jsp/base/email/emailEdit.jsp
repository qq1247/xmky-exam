<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div lay-filter="emailEditFrom" class="layui-form" style="padding: 20px 0 0 0;">
	<input type="hidden" name="id" value="${email.id}" />
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">邮箱地址：</label>
			<div class="layui-input-block">
				<input name="emailHost" value="${email.emailHost}" 
					class="layui-input" lay-verify="required">
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">邮箱账号：</label>
			<div class="layui-input-block">
				<input name="emailName" value="${email.emailName}" 
					class="layui-input" lay-verify="required">
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">邮箱密码：</label>
			<div class="layui-input-block">
				<input name="emailPwd" value="${email.emailPwd}" 
					class="layui-input" lay-verify="required">
			</div>
		</div>
	</div>
	<div class="layui-form-item layui-hide">
		<input type="button" lay-submit lay-filter="emailEditBtn" value="确认">
	</div>
</div>
