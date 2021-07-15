<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<div lay-filter="exam-card" class="layui-collapse exam-card">
	<div class="layui-colla-item">
		<h2 class="exam-head">
			答题卡
			<c:if test="${design }">
			<span class="layui-icon layui-icon-add-1" title="添加章节" onclick="toChapterAdd('${id }')"></span>
			</c:if>
		</h2>
		<div class="layui-colla-content layui-show">
			<div id="countdown"></div>
		</div>
	</div>
	<c:forEach var="paperQuestionEx" items="${paperQuestionExList }" varStatus="v">
	<div class="layui-colla-item">
		<h2 class="layui-colla-title">
			${paperQuestionEx.name } 共${fn:length(paperQuestionEx.subList)}题
			<c:if test="${design }">
			<c:if test="${!v.first }">
			<span class="layui-icon layui-icon-return" style="font-weight: bold;line-height: 22px;height: 22px;margin-top: 8px;transform: rotate(90deg);" title="上移章节" onclick="doChapterUp('${paperQuestionEx.id }');"></span>
			</c:if>
			<c:if test="${!v.last }">
			<span class="layui-icon layui-icon-return" style="font-weight: bold;line-height: 22px;height: 22px;margin-top: 8px;transform: rotate(270deg);" title="下移章节" onclick="doChapterDown('${paperQuestionEx.id }');"></span>
			</c:if>
			<span class="layui-icon layui-icon-delete" style="font-weight: bold;" title="删除章节" onclick="doChapterDel('${paperQuestionEx.id }');"></span>
			<span class="layui-icon layui-icon-edit" style="font-weight: bold;" title="修改章节" onclick="toChapterEdit('${paperQuestionEx.id }');"></span>
			</c:if>
		</h2>
		<div class="layui-colla-content layui-show">
			<c:forEach var="subPaperQuestionEx" items="${paperQuestionEx.subList }" varStatus="v1">
			<c:set var="myExamDetail" value="${myExamDetailMap[subPaperQuestionEx.questionId + 0]}"></c:set>
			<c:if test="${design || answer }">
			<a id="examCard_${myExamDetail.id }" 
				href="${requestScope['javax.servlet.forward.request_uri']}?${!empty param.id ? 'id=': 'myExamId=' }${!empty param.id ? param.id : param.myExamId}#title${subPaperQuestionEx.question.id }"
				${!empty myExamDetail.answer ? 'class="select"' : '' }
				>${subPaperQuestionEx.no }</a>
			</c:if>
			<c:if test="${mark }">
			<c:if test="${subPaperQuestionEx.question.type == 5 }">
			<a id="examCard_${myExamDetail.id }" 
				href="${requestScope['javax.servlet.forward.request_uri']}?${!empty param.id ? 'id=': 'myExamId=' }${!empty param.id ? param.id : param.myExamId}#title${subPaperQuestionEx.question.id }"
				${!empty myExamDetail.score ? 'class="select"' : '' }
				>${subPaperQuestionEx.no }</a>
			</c:if>
			</c:if>
			</c:forEach>
		</div>
	</div>
	</c:forEach>
	<div class="layui-colla-item">
		<c:if test="${design }">
		<h2 class="exam-head" onclick="colseDesignWin(false)">关闭</h2>
		</c:if>
		<c:if test="${answer }">
		<h2 class="exam-head" onclick="doPaper(false)">交卷</h2>
		</c:if>
		<c:if test="${mark }">
		<h2 class="exam-head" onclick="doMark(false)">已阅</h2>
		</c:if>
	</div>
</div>
