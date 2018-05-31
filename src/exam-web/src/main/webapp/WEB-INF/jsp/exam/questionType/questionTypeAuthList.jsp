<%@ page language="java" pageEncoding="utf-8"%>
<div class="easyui-tabs" data-options="fit:true,tabPosition:'left'">
	<div title="用户权限" data-options="">
		<div class="easyui-layout" data-options="fit:true">
			<!-- 左侧组织机构菜单 -->
			<div data-options="region:'west',border:false" style="width: 240px;padding:5px">
				<div class="easyui-panel" data-options="fit:true">
					<ul id="questionTypeAuthUserOrgTree"></ul>
				</div>
			</div>
			<div data-options="region:'center',border:false">
				<%-- 用户查询条件 --%>
				<div id="questionTypeAuthUserToolbar" style="padding:0 30px;">
					<div class="conditions">
						<form id="questionTypeAuthUserQueryForm">
							<input type="hidden" id="questionTypeAuthUser_one" name="one">
							<input type="hidden" id="questionTypeAuthUser_ten" name="ten" value="${id }">
							<span class="con-span">名称：</span>
							<input name="two" class="easyui-textbox" style="width:166px;height:35px;line-height:35px;">
							<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" data-options="selected:true" onclick="questionTypeAuthUserQuery();">查询</a>
							<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-clear" onclick="questionTypeAuthUserReset();">重置</a>
						</form>
					</div>
					<div class="opt-buttons">
						<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" data-options="selected:true" onclick="toQuestionTypeAuthUserAddList();">添加用户</a>
						<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" onclick="doQuestionTypeAuthUserDel();">删除用户</a>
					</div>
				</div>
				<%-- 用户数据表格 --%>
				<table id="questionTypeAuthUserGrid">
				</table>
			</div>
		</div>
	</div>
	<div title="机构权限" data-options="">
	</div>
	<div title="岗位权限" data-options="">
	</div>
</div>