<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<form id="examEditFrom" method="post">
	<input type="hidden" id="exam_id" name="id" value="${exam.id }" />
	<table class="kv-table">
		<tr>
			<td class="kv-label">考试分类：</td>
			<td class="kv-content">
				<input type="hidden" id="exam_examTypeId" name="examTypeId" value="${examType.id }"/>
				<input id="exam_examTypeName" name="examTypeName" value="${examType.name }" readonly="readonly" style="width:180px;height:30px;"/>
			</td>
			<td class="kv-label">状态：</td>
			<td class="kv-content">
				<select id="exam_state" name="state" style="width:185px;height:35px;line-height:35px;">
					<option value=""></option>
					<c:forEach var="dict" items="${STATE }">
					<option value="${dict.dictKey }" ${dict.dictKey == exam.state ? "selected" : ""}>${dict.dictValue }</option>
					</c:forEach>
				</select>
			</td>
		</tr>
		<tr>
			<td class="kv-label">名称：</td>
			<td class="kv-content">
				<input id="exam_name" name="name" value="${exam.name }" style="width:185px;height:35px;" />
			</td>
			<td class="kv-label" colspan="3"></td>
		</tr>
		<tr>
			<td class="kv-label">试卷：</td>
			<td class="kv-content">
				<input id="exam_paperId" name="paperId" value="${exam.paperId }" style="width:185px;height:35px;" />
				<input type="hidden" id="exam_paperName" name="paperName" value="${paper.name }" />
			</td>
			<td class="kv-label">及格分数：</td>
			<td class="kv-content">
				<input id="exam_passScore" name="passScore" value="${exam.passScore }" style="width:185px;height:35px;" />
			</td>
		</tr>
		<tr>
			<td class="kv-label">开始时间：</td>
			<td class="kv-content">
				<input id="exam_startTime" name="startTime" value="${exam.startTime }" style="width:185px;height:35px;" />
			</td>
			<td class="kv-label">结束时间：</td>
			<td class="kv-content">
				<input id="exam_endTime" name="endTime" value="${exam.endTime }" style="width:185px;height:35px;" />
			</td>
		</tr>
		<tr>
			<td class="kv-label">描述：</td>
			<td colspan="3" class="kv-content">
				<textarea id="exam_description" name="description">${exam.description }</textarea>
			</td>
		</tr>
	</table>
</form>