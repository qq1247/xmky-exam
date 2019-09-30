<%@ page language="java" pageEncoding="utf-8"%>
<form id="userEditFrom" method="post">
	<input type="hidden" id="user_id" name="id" value="${user.id }" />
	<table class="kv-table">
		<tr>
			<td class="kv-label">
				组织机构：
			</td>
			<td class="kv-content">
				<input type="hidden" id="user_orgId" name="orgId" value="${org.id }"/>
				<input id="user_orgName" name="orgName" value="${org.name }" readonly="readonly" style="width:180px;height:30px;"/>
			</td>
			<td class="kv-label">
			</td>
			<td class="kv-content">
			</td>
		</tr>
		<tr>
			<td  class="kv-label">
				姓名：
			</td>
			<td class="kv-content">
				<input id="user_name" name="name" value="${user.name }" style="width:185px;height:35px;"/>
			</td>
			<td  class="kv-label">
				登录名称
			</td>
			<td class="kv-content">
				<input id="user_loginName" name="loginName" value="${user.loginName }" style="width:185px;height:35px;"/>
			</td>
		</tr>
	</table>
</form>