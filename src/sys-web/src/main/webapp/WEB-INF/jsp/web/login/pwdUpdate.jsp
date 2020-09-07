<%@ page language="java" pageEncoding="utf-8"%>
<div lay-filter="updatePwdFrom" class="layui-form" style="padding: 20px 0 0 0;">
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">旧密码：</label>
			<div class="layui-input-block">
				<input type="text" name="oldPwd" placeholder="请输入旧密码" class="layui-input" lay-verify="required">
			</div>
		</div>
	</div>
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">新密码：</label>
			<div class="layui-input-block">
				<input type="text" name="newPwd" placeholder="请输入新密码" class="layui-input" lay-verify="required">
			</div>
		</div>
	</div>
	<div class="layui-form-item layui-hide">
		<input type="button" lay-submit lay-filter="pwdUpdateBtn">
	</div>
</div>