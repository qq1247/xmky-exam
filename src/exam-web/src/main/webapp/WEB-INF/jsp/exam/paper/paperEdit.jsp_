<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<form id="paperEditFrom" method="post">
	<input type="hidden" id="paper_id" name="id" value="${paper.id }" />
	<table class="kv-table">
		<tr>
			<td class="kv-label">试卷分类：</td>
			<td class="kv-content">
				<input type="hidden" id="paper_paperTypeId" name="paperTypeId" value="${paperType.id }" />
				<input id="paper_paperTypeName" name="paperTypeName" value="${paperType.name }" readonly="readonly" style="width:180px;height:30px;"/>
			</td>
			<td class="kv-label">状态：</td>
			<td class="kv-content">
				<select id="paper_state" name="state" style="width:185px;height:35px;line-height:35px;">
					<option value=""></option>
					<c:forEach var="dict" items="${STATE }">
					<option value="${dict.dictKey }" ${dict.dictKey == paper.state ? "selected" : ""}>${dict.dictValue }</option>
					</c:forEach>
				</select>
			</td>
		</tr>
		<tr>
			<td class="kv-label">名称：</td>
			<td class="kv-content">
				<input id="paper_name" name="name" value="${paper.name }" style="width:185px;height:35px;"/>
			</td>
			<td class="kv-label"></td>
			<td class="kv-content">
			</td>
		</tr>
		<tr>
			<td class="kv-label">描述：</td>
			<td colspan="3" class="kv-content">
				<textarea id="paper_description" name="description">${paper.description }</textarea>
			</td>
		</tr>
	</table>
</form>