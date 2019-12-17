<%@ page language="java" pageEncoding="utf-8"%>
<form id="cronEditFrom" method="post">
	<input type="hidden" id="cron_id" name="id" value="${cron.id }" />
	<table class="kv-table">
		<tr>
			<td class="kv-label">名称：</td>
			<td class="kv-content">
				<input id="cron_name" name="name" value="${cron.name }" style="width:570px;height:35px;"/>
			</td>
		</tr>
		<tr>
			<td class="kv-label">cron表达式：</td>
			<td class="kv-content">
				<input id="cron_cron" name="cron" value="${cron.cron }" style="width:570px;height:35px;"/>
			</td>
		</tr>
		<tr>
			<td class="kv-label">实现类：</td>
			<td class="kv-content">
				<input id="cron_jobClass" name="jobClass" value="${cron.jobClass }" style="width:570px;height:35px;"/>
			</td>
		</tr>
	</table>
</form>