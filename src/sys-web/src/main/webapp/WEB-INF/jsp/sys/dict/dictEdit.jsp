<%@ page language="java" pageEncoding="utf-8"%>
<form id="dictEditFrom" method="post">
	<input type="hidden" id="dict_id" name="id" value="${dict.id }" />
	<table class="kv-table">
		<tr>
			<td class="kv-label">索引：</td>
			<td class="kv-content">
				<input id="dict_index" name="dictIndex" value="${dict.dictIndex }" style="width:185px;height:35px;"/>
			</td>
			<td class="kv-label">排序：</td>
			<td class="kv-content">
				<input id="dict_no" name="no" value="${dict.no }" style="width:185px;height:35px;"/>
			</td>
		</tr>
		<tr>
			<td class="kv-label">键：</td>
			<td class="kv-content">
				<input id="dict_key" name="dictKey" value="${dict.dictKey }" style="width:185px;height:35px;"/>
			</td>
			<td class="kv-label">值：</td>
			<td class="kv-content">
				<input id="dict_value" name="dictValue" value="${dict.dictValue }" style="width:185px;height:35px;"/>
			</td>
		</tr>
	</table>
</form>