<%@ page language="java" pageEncoding="utf-8"%>
<form id="examTypeEditFrom" method="post">
	<input type="hidden" id="examType_id" name="id" value="${examType.id }" />
	<table class="kv-table">
		<tr>
			<td class="kv-label">上级考试分类：</td>
			<td class="kv-content">
				<input type="hidden" id="examType_parentId" name="parentId" value="${pExamType.id }"/>
				<input id="examType_parentName" name="parentName" value="${pExamType.name }" readonly="readonly" style="width:180px;height:30px;"/>
			</td>
			<td class="kv-label"></td>
			<td class="kv-content">
			</td>
		</tr>
		<tr>
			<td class="kv-label">考试分类名称：</td>
			<td class="kv-content">
				<input id="examType_name" name="name" value="${examType.name }" style="width:185px;height:35px;"/>
			</td>
			<td class="kv-label">排序：</td>
			<td class="kv-content">
				<input id="examType_no" name="no" value="${examType.no }" style="width:185px;height:35px;"/>
			</td>
		</tr>
	</table>
</form>