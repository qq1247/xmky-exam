<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div lay-filter="chapterEditFrom" class="layui-form" style="padding: 20px 0 0 0;">
	<input type="hidden" name="id" value="${chapter.id}" />
	<input type="hidden" name="paperId" value="${chapter.paperId}" />
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">名称：</label>
			<div class="layui-input-block">
				<input name="name" value="${chapter.name}" 
					class="layui-input" lay-verify="required">
			</div>
		</div>
	</div>
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">描述：</label>
			<div class="layui-input-block">
				<script id="description" name="description" type="text/plain" style="width:100%;height: 50px;">${chapter.description }</script>
	 		</div>
	 	</div>
	</div>
	<div class="layui-form-item layui-hide">
		<input type="button" lay-submit lay-filter="chapterEditBtn" value="确认">
	</div>
</div>
