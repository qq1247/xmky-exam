<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>${exam.name }</title>
		<%@include file="/script/myJs/common.jspf"%>
		<%@include file="/WEB-INF/jsp/exam/paper/paperCfgStyle.jsp"%>
	</head>
	<body>
		<div class="layui-card">
			<div class="layui-container">
				<div lay-filter="paperCfgFrom" class="layui-form" style="padding: 20px 0 0 0;">
					<div class="layui-row">
						<div class="layui-col-md-offset1 layui-col-md9">
							<!-- 试卷 -->
							<%@include file="/WEB-INF/jsp/exam/paper/paperCfgQuestion.jsp"%>
						</div>
						<div class="layui-col-md2">
							<!-- 答题卡 -->
							<%@include file="/WEB-INF/jsp/exam/paper/paperCfgCard.jsp"%>
						</div>
					</div>
					<div class="layui-form-item layui-hide">
						<input type="button" lay-submit lay-filter="paperValidBtn">
					</div>
				</div>
			</div>
		</div>
	</body>
	<%@include file="/script/myJs/tail.jspf"%>
	<%@include file="/WEB-INF/jsp/exam/myMark/myMarkJs.jsp"%>
