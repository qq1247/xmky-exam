<%@ page language="java" pageEncoding="utf-8"%>
<div lay-filter="examCfgFrom" class="layui-form" style="padding: 0px 20px 20px 20px;">
	<input type="hidden" name="id" value="${id }" />
	<div class="layui-tab layui-tab-brief">
		<ul class="layui-tab-title">
			<li class="layui-this">考试用户</li>
			<li>判卷用户</li>
		</ul>
		<div class="layui-tab-content" style="padding: 10px 0px;">
			<div class="layui-tab-item layui-show">
				<div id="userIds"></div>
			</div>
			<div class="layui-tab-item">
				<div id="markUserIds"></div>
			</div>
		</div>
	</div>
	<div class="layui-form-item layui-hide">
		<input type="button" lay-submit lay-filter="examCfgBtn">
	</div>
</div>
