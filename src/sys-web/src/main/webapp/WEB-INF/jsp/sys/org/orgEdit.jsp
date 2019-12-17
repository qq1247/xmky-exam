<%@ page language="java" pageEncoding="utf-8"%>
<form id="orgEditFrom" method="post">
	<input type="hidden" id="org_id" name="id" value="${org.id }" />
	<table class="kv-table">
		<tr>
			<td class="kv-label">
				上级组织机构：
			</td>
			<td class="kv-content">
				<input type="hidden" id="org_parentId" name="parentId" value="${pOrg.id }"/>
				<input id="org_parentName" name="parentName" value="${pOrg.name }" readonly="readonly" style="width:180px;height:30px;"/>
			</td>
			<td class="kv-label">
			</td>
			<td class="kv-content">
			</td>
		</tr>
		<tr>
			<td class="kv-label">
				名称：
			</td>
			<td class="kv-content">
				<input id="org_name" name="name" value="${org.name }" style="width:185px;height:35px;"/>
			</td>
			<td class="kv-label">
				排序：
			</td>
			<td class="kv-content">
				<input id="org_no" name="no" value="${org.no }" style="width:185px;height:35px;"/>
			</td>
		</tr>
	</table>
</form>