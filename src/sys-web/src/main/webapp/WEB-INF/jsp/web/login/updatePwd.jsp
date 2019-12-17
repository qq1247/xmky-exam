<%@ page language="java" pageEncoding="utf-8"%>
<form id="updatePwdEditFrom" method="post">
	<table class="kv-table">
		<tr>
			<td  class="kv-label">
				原始密码：
			</td>
			<td class="kv-content">
				<input id="login_pwd" name="oldPwd" style="width:180px;height:30px;"/>
			</td>
		</tr>
		<tr>
			<td  class="kv-label">
				新密码
			</td>
			<td class="kv-content">
				<input id="login_newPwd" name="newPwd" style="width:180px;height:30px;"/>
			</td>
		</tr>
		<tr>
			<td  class="kv-label">
				重复新密码
			</td>
			<td class="kv-content">
				<input id="login_newPwd2" name="newPwd2" style="width:180px;height:30px;"/>
			</td>
		</tr>
	</table>
</form>