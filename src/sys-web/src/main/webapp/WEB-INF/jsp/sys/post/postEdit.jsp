<%@ page language="java" pageEncoding="utf-8"%>
<form id="postEditFrom" method="post">
	<input type="hidden" id="post_id" name="id" value="${post.id }" />
	<table class="kv-table">
		<tr>
			<td class="kv-label">
				组织机构：
			</td>
			<td class="kv-content">
				<input type="hidden" id="post_orgId" name="orgId" value="${org.id }"/>
				<input id="post_orgName" name="orgName" value="${org.name }" readonly="readonly" style="width:180px;height:30px;"/>
			</td>
			<td class="kv-label">
				名称：
			</td>
			<td class="kv-content">
				<input id="post_name" name="name" value="${post.name }" style="width:185px;height:35px;"/>
			</td>
		</tr>
	</table>
</form>