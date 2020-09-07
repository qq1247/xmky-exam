<%@ page language="java" pageEncoding="utf-8"%>
<div lay-filter="cronEditFrom" class="layui-form" style="padding: 20px 0 0 0;">
	<input type="hidden" id="cron_id" name="id" value="${cron.id}" />
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">名称：</label>
			<div class="layui-input-block">
				<input name="name" value="${cron.name }" 
					class="layui-input" lay-verify="required" placeholder="请输入名称">
			</div>
		</div>
	</div>
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">实现类：</label>
			<div class="layui-input-block">
				<input name="jobClass" value="${cron.jobClass }" 
					class="layui-input" lay-verify="required" placeholder="请输入实现类">
			</div>
		</div>
	</div>
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">cron表达式：</label>
			<div class="layui-input-block">
				<input name="cron" value="${cron.cron }" 
					class="layui-input" lay-verify="required" placeholder="请输入cron表达式">
			</div>
		</div>
	</div>
	<div class="layui-form-item layui-hide">
		<input type="button" lay-submit lay-filter="cronEditBtn" value="确认">
	</div>
</div>
