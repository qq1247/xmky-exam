<%@ page language="java" pageEncoding="utf-8"%>
<div lay-filter="paperTypeAuthFrom" class="layui-form" style="padding: 0px 20px 20px 20px;">
	<input type="hidden" name="id" value="${paperType.id }" />
	<div class="layui-tab layui-tab-brief">
		<ul class="layui-tab-title">
			<li class="layui-this">用户授权</li>
			<li>岗位授权</li>
			<li>组织机构授权</li>
		</ul>
		<div class="layui-tab-content" style="padding: 10px 0px;">
			<div class="layui-tab-item layui-show">
				<div id="userIds"></div>
			</div>
			<div class="layui-tab-item">
				<div id="postIds"></div>
			</div>
			<div class="layui-tab-item">
				<div id="orgIds"></div>
			</div>
		</div>
	</div>
	<div class="layui-form-item layui-hide">
		<input type="button" lay-submit lay-filter="paperTypeAuthBtn">
	</div>
</div>
