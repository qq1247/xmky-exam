<%@ page language="java" pageEncoding="utf-8"%>
<form id="resEditFrom" method="post">
	<input type="hidden" id="res_id" name="id" value="${res.id }" />
	<input type="hidden" id="res_type" name="type" value="${res.type }"/>
	<table class="kv-table">
		<tr>
			<td class="kv-label">
				上级资源：
			</td>
			<td class="kv-content">
				<input type="hidden" id="res_parentId" name="parentId" value="${pRes.id }"/>
				<input id="res_parentName" name="parentName" value="${pRes.name }" readonly="readonly" style="width:180px;height:30px;"/>
			</td>
			<td class="kv-label">
				图标：
			</td>
			<td class="kv-content">
				<input id="res_icon" name="icon" value="${res.icon }" style="width:185px;height:35px;"/>
			</td>
		</tr>
		<tr>
			<td class="kv-label">
				名称：
			</td>
			<td class="kv-content">
				<input id="res_name" name="name" value="${res.name }" style="width:185px;height:35px;"/>
			</td>
			<td class="kv-label">
				排序：
			</td>
			<td class="kv-content">
				<input id="res_no" name="no" value="${res.no }" style="width:185px;height:35px;"/>
			</td>
		</tr>
		<tr>
			<td class="kv-label">
				url：
			</td>
			<td colspan="3" class="kv-content">
				<input id="res_url" name="url" value="${res.url }" style="height:35px;width:590px;"/>
			</td>
		</tr>
	</table>
</form>