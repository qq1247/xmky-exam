<%@ page language="java" pageEncoding="utf-8"%>
<div class="easyui-layout" data-options="fit:true">
	<!-- 左侧试卷分类菜单 -->
	<div data-options="region:'west',border:false" style="width: 240px;padding:5px">
		<div class="easyui-panel" data-options="fit:true">
			<ul id="paperAddListTypeTree"></ul>
			<div id="paperAddListTypeTreeMenu" class="easyui-menu" style="width:120px;">
				<div class="menu-sep"></div>
				<div onclick="paperAddListTypeTreeFlush()" data-options="iconCls:'icon-reload'">刷新</div>
			</div>
		</div>
	</div>
	<div data-options="region:'center',border:false">
		<%-- 试卷查询条件 --%>
		<div id="paperAddListToolbar" style="padding:0 30px;">
			<div class="conditions">
				<form id="paperAddListQueryForm">
					<input type="hidden" id="paperAddList_one" name="one">
					<input type="hidden" id="paperAddList_ten" name="ten" value="1">
					<span class="con-span">名称：</span>
					<input id="paperAddList_two" name="two" class="easyui-textbox" style="width:166px;height:35px;line-height:35px;">
					<!-- <span class="con-span">开始时间：</span>
					<input id="paperAddList_three" name="three" class="easyui-datebox" style="width:166px;height:35px;line-height:35px;">
					<span class="con-span">结束时间：</span>
					<input id="paperAddList_four" name="four" class="easyui-datebox" style="width:166px;height:35px;line-height:35px;"> -->
					<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" data-options="selected:true" onclick="paperAddListQuery();">查询</a>
					<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-clear" onclick="paperAddListReset();">重置</a>
				</form>
			</div>
		</div>
		<%-- 试卷数据表格 --%>
		<table id="paperAddListGrid">
		</table>
	</div>
</div>