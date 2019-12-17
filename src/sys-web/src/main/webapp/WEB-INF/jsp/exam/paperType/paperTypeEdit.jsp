<%@ page language="java" pageEncoding="utf-8"%>
<form id="paperTypeEditFrom" method="post">
	<input type="hidden" id="paperType_id" name="id" value="${paperType.id }" />
	<table class="kv-table">
		<tr>
			<td class="kv-label">上级试卷分类：</td>
			<td class="kv-content">
				<input type="hidden" id="paperType_parentId" name="parentId" value="${pPaperType.id }"/>
				<input id="paperType_parentName" name="parentName" value="${pPaperType.name }" readonly="readonly" style="width:180px;height:30px;"/>
			</td>
			<td class="kv-label"></td>
			<td class="kv-content">
			</td>
		</tr>
		<tr>
			<td class="kv-label">试卷分类名称：</td>
			<td class="kv-content">
				<input id="paperType_name" name="name" value="${paperType.name }" style="width:185px;height:35px;"/>
			</td>
			<td class="kv-label">排序：</td>
			<td class="kv-content">
				<input id="paperType_no" name="no" value="${paperType.no }" style="width:185px;height:35px;"/>
			</td>
		</tr>
	</table>
</form>