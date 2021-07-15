<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div lay-filter="batchScoresUpdateFrom" class="layui-form" style="padding: 20px 0 0 0;">
	<input type="hidden" name=chapterId value="${chapterId }">
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">分数：</label>
			<div class="layui-input-block">
				<input name="score" value="" 
					class="layui-input" lay-verify="required" placeholder="请输入分数">
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">选项：</label>
			<div class="layui-input-block">
				<input type="checkbox" name="batchScoresOptions" value="1" lay-skin="switch" lay-text="半对半分|全对得分" >
				<input type="checkbox" name="batchScoresOptions" value="2" lay-skin="switch" lay-text="答案无顺序|答案有顺序" >
				<input type="checkbox" name="batchScoresOptions" value="3" lay-skin="switch" lay-text="大小写不敏感|大小写敏感" >
				<input type="checkbox" name="batchScoresOptions" value="4" lay-skin="switch" lay-text="包含答案得分|等于答案得分" >
	 		</div>
	 	</div>
	</div>
	<div class="layui-form-item layui-hide">
		<input type="button" lay-submit lay-filter="batchScoresUpdateBtn" value="确认">
	</div>
</div>
