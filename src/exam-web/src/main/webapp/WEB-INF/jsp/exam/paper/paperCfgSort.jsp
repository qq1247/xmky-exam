<%@ page language="java" pageEncoding="utf-8"%>
<form id="paperCfgSortEditFrom" method="post">
	<input type="hidden" id="paperCfgSort_id" name="paperQuestionId"/>
	<input type="hidden" id="paperCfgSort_maxNo" name="maxNo" value="${maxNo }"/>
	<table class="kv-table">
		<tr>
			<td class="kv-label">排到之后：</td>
			<td colspan="3" class="kv-content">
				<input id="paperCfgSort_no" name="no" value="${paperQuestion.no }" style="width:185px;height:35px;" />
			</td>
		</tr>
	</table>
</form>