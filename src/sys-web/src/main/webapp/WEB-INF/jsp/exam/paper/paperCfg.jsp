<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@include file="/WEB-INF/jsp/exam/paper/paperCfgStyle.jsp"%>

<div lay-filter="paperCfgFrom" class="layui-form" style="padding: 20px 0 0 0;">
	<div class="layui-row">
		<div class="layui-col-md-offset1 layui-col-md8">
			<!-- 试卷 -->
			<%@include file="/WEB-INF/jsp/exam/paper/paperCfgQuestion.jsp"%>
		</div>
		<div class="layui-col-md2">
			<!-- 答题卡 -->
			<%@include file="/WEB-INF/jsp/exam/paper/paperCfgCard.jsp"%>
		</div>
	</div>
</div>
<%@include file="/WEB-INF/jsp/exam/paper/paperCfgJs.jsp"%>