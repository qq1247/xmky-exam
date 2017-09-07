<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<form id="questionEditFrom" method="post">
	<input type="hidden" id="question_id" name="id" value="${question.id }" />
	<input type="hidden" id="question_optionA_" value="${question.optionA }" />
	<input type="hidden" id="question_optionB_" value="${question.optionB }" />
	<input type="hidden" id="question_optionC_" value="${question.optionC }" />
	<input type="hidden" id="question_optionD_" value="${question.optionD }" />
	<input type="hidden" id="question_optionE_" value="${question.optionE }" />
	<input type="hidden" id="question_optionF_" value="${question.optionF }" />
	<input type="hidden" id="question_optionG_" value="${question.optionG }" />
	<input type="hidden" id="question_answer_" value="${question.answer }" />
	
	<table class="kv-table">
		<tr>
			<td class="kv-label">试题分类：</td>
			<td class="kv-content">
				<input type="hidden" id="question_questionTypeId" name="questionTypeId" value="${questionType.id }"/>
				<input id="question_questionTypeName" name="questionTypeName" value="${questionType.name }" readonly="readonly" style="width:180px;height:30px;"/>
			</td>
			<td class="kv-label">状态：</td>
			<td class="kv-content">
				<select id="question_state" name="state" style="width:185px;height:35px;line-height:35px;">
					<option value=""></option>
					<c:forEach var="dict" items="${STATE }">
					<option value="${dict.dictKey }" ${dict.dictKey == question.state ? "selected" : ""}>${dict.dictValue }</option>
					</c:forEach>
				</select>
			</td>
		</tr>
		<tr>
			<td class="kv-label">类型：</td>
			<td class="kv-content">
				<select id="question_type" name="type" style="width:185px;height:35px;line-height:35px;">
					<option value=""></option>
					<c:forEach var="dict" items="${QUESTION_TYPE }">
					<option value="${dict.dictKey }" ${dict.dictKey == question.type ? "selected" : ""}>${dict.dictValue }</option>
					</c:forEach>
				</select>
			</td>
			<td class="kv-label">难度：</td>
			<td class="kv-content">
				<select id="question_difficulty" name="difficulty" style="width:185px;height:35px;line-height:35px;">
					<option value=""></option>
					<c:forEach var="dict" items="${QUESTION_DIFFICULTY }">
					<option value="${dict.dictKey }" ${dict.dictKey == question.difficulty ? "selected" : ""}>${dict.dictValue }</option>
					</c:forEach>
				</select>
			</td>
		</tr>
		<tr>
			<td id="titleTd1" class="kv-label">题干：</td>
			<td class="kv-content" colspan="3">
				<textarea id="question_title" name="title">${question.title }</textarea>
			</td>
		</tr>
		<tr id="optionATr" style="display: none;">
			<td id="optionATd1" class="kv-label"></td>
			<td id="optionATd2" class="kv-content" colspan="3"></td>
		</tr>
		<tr id="optionBTr" style="display: none;">
			<td id="optionBTd1" class="kv-label"></td>
			<td id="optionBTd2" class="kv-content" colspan="3"></td>
		</tr>
		<tr id="optionCTr" style="display: none;">
			<td id="optionCTd1" class="kv-label"></td>
			<td id="optionCTd2" class="kv-content" colspan="3"></td>
		</tr>
		<tr id="optionDTr" style="display: none;">
			<td id="optionDTd1" class="kv-label"></td>
			<td id="optionDTd2" class="kv-content" colspan="3"></td>
		</tr>
		<tr id="optionETr" style="display: none;">
			<td id="optionETd1" class="kv-label"></td>
			<td id="optionETd2" class="kv-content" colspan="3"></td>
		</tr>
		<tr id="optionFTr" style="display: none;">
			<td id="optionFTd1" class="kv-label"></td>
			<td id="optionFTd2" class="kv-content" colspan="3"></td>
		</tr>
		<tr id="optionGTr" style="display: none;">
			<td id="optionGTd1" class="kv-label"></td>
			<td id="optionGTd2" class="kv-content" colspan="3"></td>
		</tr>
		<tr>
			<td class="kv-label">答案：</td>
			<td id="answerTd2" class="kv-content" colspan="3"></td>
		</tr>
		<tr>
			<td class="kv-label">解析：</td>
			<td class="kv-content" colspan="3">
				<textarea id="question_analysis" name="analysis">${question.analysis }</textarea>
			</td>
		</tr>
	</table>
</form>