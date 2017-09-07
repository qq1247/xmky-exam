<%@ page language="java" pageEncoding="utf-8"%>
<form id="questionTypeEditFrom" method="post">
	<input type="hidden" id="questionType_id" name="id" value="${questionType.id }" />
	<table class="kv-table">
		<tr>
			<td class="kv-label">上级试题分类：</td>
			<td class="kv-content">
				<input type="hidden" id="questionType_parentId" name="parentId" value="${pQuestionType.id }"/>
				<input id="questionType_parentName" name="parentName" value="${pQuestionType.name }" readonly="readonly" style="width:180px;height:30px;"/>
			</td>
			<td class="kv-label"></td>
			<td class="kv-content">
			</td>
		</tr>
		<tr>
			<td class="kv-label">试题分类名称：</td>
			<td class="kv-content">
				<input id="questionType_name" name="name" value="${questionType.name }" style="width:185px;height:35px;"/>
			</td>
			<td class="kv-label">排序：</td>
			<td class="kv-content">
				<input id="questionType_no" name="no" value="${questionType.no }" style="width:185px;height:35px;"/>
			</td>
		</tr>
	</table>
</form>