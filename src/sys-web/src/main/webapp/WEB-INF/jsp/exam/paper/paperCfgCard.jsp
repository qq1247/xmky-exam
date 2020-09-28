<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<div lay-filter="exam-card" class="layui-collapse exam-card">
	<div class="layui-colla-item">
		<h2 class="exam-head">
			答题卡
			<span class="layui-icon layui-icon-add-1" title="添加章节" onclick="toChapterAdd('${id }')"></span>
		</h2>
	</div>
	<c:forEach var="paperQuestionEx" items="${paperQuestionExList }" varStatus="v">
	<div class="layui-colla-item">
		<h2 class="layui-colla-title">
			${paperQuestionEx.name } 共${fn:length(paperQuestionEx.subList)}题
			<c:if test="${!v.first }">
			<span class="layui-icon layui-icon-return" style="font-weight: bold;line-height: 22px;height: 22px;margin-top: 8px;transform: rotate(90deg);" title="上移章节" onclick="doChapterUp('${paperQuestionEx.id }');"></span>
			</c:if>
			<c:if test="${!v.last }">
			<span class="layui-icon layui-icon-return" style="font-weight: bold;line-height: 22px;height: 22px;margin-top: 8px;transform: rotate(270deg);" title="下移章节" onclick="doChapterDown('${paperQuestionEx.id }');"></span>
			</c:if>
			<span class="layui-icon layui-icon-delete" style="font-weight: bold;" title="删除章节" onclick="doChapterDel('${paperQuestionEx.id }');"></span>
			<span class="layui-icon layui-icon-edit" style="font-weight: bold;" title="修改章节" onclick="toChapterEdit('${paperQuestionEx.id }');"></span>
		</h2>
		<div class="layui-colla-content layui-show">
			<c:forEach var="subPaperQuestionEx" items="${paperQuestionEx.subList }" varStatus="v1">
			<c:set var="examUserQuestion" value="${examUserQuestionMap[subPaperQuestionEx.questionId + 0]}"></c:set>
			<a id="examCard${subPaperQuestionEx.question.id }" href="paper/toList#title${subPaperQuestionEx.question.id }">${subPaperQuestionEx.no }</a>
			</c:forEach>
		</div>
	</div>
	</c:forEach>
</div>