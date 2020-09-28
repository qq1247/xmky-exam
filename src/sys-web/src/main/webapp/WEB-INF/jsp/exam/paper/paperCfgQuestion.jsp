<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<div class="layui-collapse exam" style="margin-bottom: 0px;border-bottom-width: 0px;">
	<div class="layui-colla-item">
		<!-- 试卷标题 -->
		<input type="hidden" id="id" name="id" value="${paper.id }">
		<h1 class="layui-colla-title">${paper.name }</h1>
		<h5 class="layui-colla-title">${paper.description }</h5>
	</div>
</div>
<c:forEach var="paperQuestionEx" items="${paperQuestionExList }" varStatus="v">
<div class="layui-collapse exam">
	<div class="layui-colla-item">
		<!-- 章节标题 -->
		<h2 class="layui-colla-title">${paperQuestionEx.name }</h2>
		<span class="layui-icon layui-icon-add-1 exam-qst-add" title="添加试题" onclick="toQuestionAdd('${paper.id }', '${paperQuestionEx.id }')"></span>
		<span class="layui-icon layui-icon-delete exam-qst-add" title="清空试题" onclick="doQuestionClear('${paperQuestionEx.id }');"></span>
		<h5 class="layui-colla-title">${paperQuestionEx.description }</h5>
		<c:set var="labs" value="${fn:split('A,B,C,D,E,F,G', ',')}"></c:set>
		<c:forEach var="subPaperQuestionEx" items="${paperQuestionEx.subList }" varStatus="v1">
		<div id="title${subPaperQuestionEx.question.id }" class="layui-colla-content layui-show">
			<!-- 试题标题 -->
			<span class="qst-no">${subPaperQuestionEx.no }。</span>
			<span class="qst-title">${subPaperQuestionEx.question.title }</span>
			<!-- 试题选项 -->
			<%-- 单选题 --%>
			<c:if test="${subPaperQuestionEx.question.type == 1 }">
			<ul>
			<c:forEach var="lab" items="${labs }">
			<c:set var="examUserQuestion" value="${examUserQuestionMap[subPaperQuestionEx.questionId + 0]}"></c:set>
			<c:set var="ol" value="option${lab }"></c:set>
			<c:if test="${!empty subPaperQuestionEx.question[ol] }">
			<li class="exam-qst-op ${subPaperQuestionEx.question.answer == lab ? 'exam-qst-op-check' : '' }" onclick="rdoCbxSubmit('${examUserQuestion.id}')">
				<input type="radio" name="option_${subPaperQuestionEx.question.id}" value="${lab }" title="" 
					<%-- ${subPaperQuestionEx.question.answer == lab ? 'checked="checked"' : '' } --%> disabled>${lab }、${subPaperQuestionEx.question[ol] }
			</li>
			</c:if>
			</c:forEach>
			</ul>
			</c:if>
			<%-- 多选题 --%>
			<c:if test="${subPaperQuestionEx.question.type == 2 }">
			<ul>
			<c:forEach var="lab" items="${labs }">
			<c:set var="ol" value="option${lab }"></c:set>
			<c:if test="${!empty subPaperQuestionEx.question[ol] }">
			<c:set var="op1" value=",${subPaperQuestionEx.question.answer},"></c:set>
			<li class="exam-qst-op exam-qst-op-checkbox ${subPaperQuestionEx.question.answer == lab ? 'exam-qst-op-check' : '' } " onclick="rdoCbxSubmit('${examUserQuestion.id}')">
				<input type="checkbox" name="option_${examUserQuestion.id}" value="${lab }" title=" " lay-skin="primary"
					<%-- ${fn:contains(op1, lab) ?'checked="checked"' : '' } --%> disabled>${lab }、${subPaperQuestionEx.question[ol] }
			</li>
			</c:if>
			</c:forEach>
			</ul>
			</c:if>
			<%-- 填空题 --%>
			<c:if test="${subPaperQuestionEx.question.type == 3 }">
			<% pageContext.setAttribute("v", "\n"); %>
			<c:set var="lab1s" value="${fn:split('一,二,三,四,五,六,七', ',')}"></c:set>
			<c:set var="answers" value="${fn:split(subPaperQuestionEx.question.answer, v)}"></c:set>
			<c:set var="a" value="${examUserQuestion.answer }"></c:set>
			<%
			Object a = pageContext.getAttribute("a");
			if (a != null) {
				pageContext.setAttribute("answers", a.toString().split("\n"));
			}
			%>
			<span class="qst-no"></span>
			<c:forEach var="answer" items="${fn:split(subPaperQuestionEx.question.answer, v) }" varStatus="s">
				<input name="option_${examUserQuestion.id}" value="" 
					class="layui-input btn txt" readonly placeholder="填空${lab1s[s.index] }">
			</c:forEach>
			</c:if>
			<%-- 判断题 --%>
			<c:if test="${subPaperQuestionEx.question.type == 4 }">
			<ul>
				<li class="exam-qst-op">
					<input type="radio" name="option_${subPaperQuestionEx.question.id}" value="对" title=""
						${subPaperQuestionEx.question.answer == '对' ? 'question-option-select' : '' } disabled>对
				</li>
				<li class="exam-qst-op">
					<input type="radio" name="option_${subPaperQuestionEx.question.id}" value="错" title="" disabled>错
				</li>
			</ul>
			</c:if>
			<%-- 问答题 --%>
			<c:if test="${subPaperQuestionEx.question.type == 5 }">
			<textarea name="option_${examUserQuestion.id}"
				class="layui-textarea txt" placeholder="请输入答案" disabled onblur="txtSubmit(this)"></textarea>
			</c:if>
			<!-- 试题答案 -->
			<span class="span">
				【${subPaperQuestionEx.score }分】：
				<c:if test="${subPaperQuestionEx.question.type == 3 }">
				<% pageContext.setAttribute("v", "\n"); %>
				<c:set var="lab1s" value="${fn:split('一,二,三,四,五,六,七', ',')}"></c:set>
				<c:set var="answers" value="${fn:split(subPaperQuestionEx.question.answer, v)}"></c:set>
				<c:set var="a" value="${examUserQuestion.answer }"></c:set>
				<%
				Object a = pageContext.getAttribute("a");
				if (a != null) {
					pageContext.setAttribute("answers", a.toString().split("\n"));
				}
				%>
				<c:forEach var="answer" items="${fn:split(subPaperQuestionEx.question.answer, v) }" varStatus="s">
				<span class="layui-badge-rim">${answers[s.index]}</span>
				</c:forEach>
				</c:if>
				<c:if test="${subPaperQuestionEx.question.type != 3 }">
				${subPaperQuestionEx.question.answer }
				</c:if>
			</span>
			<!-- 试题解析 -->
			<span class="span">
			【解析】：${subPaperQuestionEx.question.analysis }
			</span>
			<span class="span">
			【操作】：
				本题 <input name="score" value="${subPaperQuestionEx.score }" class="layui-input btn btn3 txt" onblur="doScoreUpdate('${subPaperQuestionEx.id}')" placeholder="分值">分
				<c:if test="${!v1.first }">
				<span class="layui-icon layui-icon-return btn2" style="transform:rotate(90deg)" title="上移试题" onclick="doQuestionUp('${subPaperQuestionEx.id }');"></span>
				</c:if>
				<c:if test="${!v1.last }">
				<span class="layui-icon layui-icon-return btn2" style="transform:rotate(270deg)" title="下移试题" onclick="doQuestionDown('${subPaperQuestionEx.id }');"></span>
				</c:if>
				<span class="layui-icon layui-icon-delete btn2" style="font-size: 24px;margin-right: 10px; " title="删除试题" onclick="doQuestionDel('${subPaperQuestionEx.id }');"></span>
				<c:if test="${subPaperQuestionEx.question.type == 2 || subPaperQuestionEx.question.type == 3}">
				<input type="checkbox" lay-filter="options" name="options" value="1" pqid="${subPaperQuestionEx.id}" lay-skin="switch" lay-text="半对半分|全对得分" ${fn:contains(subPaperQuestionEx.scoreOptions, "1") ? "checked" : "" }>
				</c:if>
				<c:if test="${subPaperQuestionEx.question.type == 3}">
				<input type="checkbox" lay-filter="options" name="options" value="2" pqid="${subPaperQuestionEx.id}" lay-skin="switch" lay-text="答案无顺序|答案有顺序" ${fn:contains(subPaperQuestionEx.scoreOptions, "2") ? "checked" : "" }>
				<input type="checkbox" lay-filter="options" name="options" value="3" pqid="${subPaperQuestionEx.id}" lay-skin="switch" lay-text="大小写不敏感|大小写敏感" ${fn:contains(subPaperQuestionEx.scoreOptions, "3") ? "checked" : "" }>
				<input type="checkbox" lay-filter="options" name="options" value="4" pqid="${subPaperQuestionEx.id}" lay-skin="switch" lay-text="包含答案得分|等于答案得分" ${fn:contains(subPaperQuestionEx.scoreOptions, "4") ? "checked" : "" }>
				</c:if>
			</span>
		</div>
		</c:forEach>
	</div>
</div>
</c:forEach>











