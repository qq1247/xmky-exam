<%@ page language="java" pageEncoding="utf-8"%>
<div class="easyui-layout" data-options="fit:true">
	<!-- 左侧组织机构菜单 -->
	<div data-options="region:'west',border:false" style="width: 240px;padding:5px">
		<div class="easyui-panel" data-options="fit:true">
			<ul id="paperTypeAuthUserAddOrgTree"></ul>
		</div>
	</div>
	<div data-options="region:'center',border:false">
		<%-- 用户查询条件 --%>
		<div id="paperTypeAuthUserAddToolbar" style="padding:0 30px;">
			<div class="conditions">
				<form id="paperTypeAuthUserAddQueryForm">
					<div style="padding-left: 250px;display: inline-block;"></div>
					<input type="hidden" id="paperTypeAuthUserAdd_one" name="one">
					<input type="hidden" id="paperTypeAuthUserAdd_ten" name="ten" value="${id }">
					<span class="con-span">名称：</span>
					<input name="two" class="easyui-textbox" style="width:166px;height:35px;line-height:35px;">
					<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" data-options="selected:true" onclick="paperTypeAuthUserAddQuery();">查询</a>
					<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-clear" onclick="paperTypeAuthUserAddReset();">重置</a>
				</form>
			</div>
		</div>
		<%-- 用户数据表格 --%>
		<table id="paperTypeAuthUserAddGrid">
		</table>
	</div>
</div>