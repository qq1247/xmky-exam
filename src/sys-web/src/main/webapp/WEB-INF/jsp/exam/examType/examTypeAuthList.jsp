<%@ page language="java" pageEncoding="utf-8"%>
<div class="easyui-tabs" data-options="fit:true,tabPosition:'left',onSelect:function(title,index){updateTabs(title)}">
	<div title="用户权限">
		<div class="easyui-layout" data-options="fit:true">
			<!-- 左侧组织机构菜单 -->
			<div data-options="region:'west',border:false" style="width: 240px;padding:5px">
				<div class="easyui-panel" data-options="fit:true">
					<ul id="examTypeAuthUserOrgTree"></ul>
				</div>
			</div>
			<div data-options="region:'center',border:false">
				<%-- 用户查询条件 --%>
				<div id="examTypeAuthUserToolbar" style="padding:0 30px;">
					<div class="conditions">
						<form id="examTypeAuthUserQueryForm">
							<input type="hidden" id="examTypeAuthUser_one" name="one">
							<input type="hidden" id="examTypeAuthUser_ten" name="ten" value="${id }">
							<span class="con-span">名称：</span>
							<input name="two" class="easyui-textbox" style="width:166px;height:35px;line-height:35px;">
							<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" data-options="selected:true" onclick="examTypeAuthUserQuery();">查询</a>
							<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-clear" onclick="examTypeAuthUserReset();">重置</a>
						</form>
					</div>
					<div class="opt-buttons">
						<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" data-options="selected:true" onclick="toExamTypeAuthUserAddList();">添加用户</a>
						<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" onclick="doExamTypeAuthUserDel();">删除用户</a>
					</div>
				</div>
				<%-- 用户数据表格 --%>
				<table id="examTypeAuthUserGrid">
				</table>
			</div>
		</div>
	</div>
	<div title="机构权限">
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'west',border:false" style="width: 240px;padding:5px">
				<div class="easyui-panel" data-options="fit:true">
					<ul id="examTypeAuthOrgOrgTree"></ul>
					<div style="padding: 20px;">
						<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-save" data-options="selected:true" onclick="doExamTypeAuthOrgUpdate();">保存机构</a>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div title="岗位权限">
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'west',border:false" style="width: 240px;padding:5px">
				<div class="easyui-panel" data-options="fit:true">
					<ul id="examTypeAuthPostOrgTree"></ul>
					<div style="padding: 20px;">
						<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-save" data-options="selected:true" onclick="doExamTypeAuthPostUpdate();">保存机构</a>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>