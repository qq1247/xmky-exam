<%@ page language="java" pageEncoding="utf-8"%>
<div class="easyui-tabs" data-options="fit:true,tabPosition:'left',onSelect:function(title,index){updateTabs(title)}">
	<div title="用户权限">
		<div class="easyui-layout" data-options="fit:true">
			<!-- 左侧组织机构菜单 -->
			<div data-options="region:'west',border:false" style="width: 240px;padding:5px">
				<div class="easyui-panel" data-options="fit:true">
					<ul id="paperTypeAuthUserOrgTree"></ul>
				</div>
			</div>
			<div data-options="region:'center',border:false">
				<%-- 用户查询条件 --%>
				<div id="paperTypeAuthUserToolbar" style="padding:0 30px;">
					<div class="conditions">
						<form id="paperTypeAuthUserQueryForm">
							<input type="hidden" id="paperTypeAuthUser_one" name="one">
							<input type="hidden" id="paperTypeAuthUser_ten" name="ten" value="${id }">
							<span class="con-span">名称：</span>
							<input name="two" class="easyui-textbox" style="width:166px;height:35px;line-height:35px;">
							<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" data-options="selected:true" onclick="paperTypeAuthUserQuery();">查询</a>
							<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-clear" onclick="paperTypeAuthUserReset();">重置</a>
						</form>
					</div>
					<div class="opt-buttons">
						<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" data-options="selected:true" onclick="toPaperTypeAuthUserAddList();">添加用户</a>
						<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" onclick="doPaperTypeAuthUserDel();">删除用户</a>
					</div>
				</div>
				<%-- 用户数据表格 --%>
				<table id="paperTypeAuthUserGrid">
				</table>
			</div>
		</div>
	</div>
	<div title="机构权限">
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'west',border:false" style="width: 240px;padding:5px">
				<div class="easyui-panel" data-options="fit:true">
					<ul id="paperTypeAuthOrgOrgTree"></ul>
					<div style="padding: 20px;">
						<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-save" data-options="selected:true" onclick="doPaperTypeAuthOrgUpdate();">保存机构</a>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div title="岗位权限">
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'west',border:false" style="width: 240px;padding:5px">
				<div class="easyui-panel" data-options="fit:true">
					<ul id="paperTypeAuthPostOrgTree"></ul>
					<div style="padding: 20px;">
						<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-save" data-options="selected:true" onclick="doPaperTypeAuthPostUpdate();">保存机构</a>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>