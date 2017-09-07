<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="easyui-layout" data-options="fit:true">
	<!-- 左侧试题分类菜单 -->
	<div data-options="region:'west',border:false" style="width: 240px;padding:5px">
		<div class="easyui-panel" data-options="fit:true">
			<ul id="paperCfgListQuestionTypeTree"></ul>
		</div>
	</div>
	<div data-options="region:'center',border:false">
		<%-- 试题查询条件 --%>
		<div id="paperCfgListToolbar" style="padding:0 30px;">
			<div class="conditions">
				<form id="paperCfgListQueryForm">
					<input type="hidden" id="paperCfgList_one" name="one">
					<input type="hidden" id="paperCfgList_ten" name="ten">
					<span class="con-span">类型：</span>
					<select id="paperCfgList_two" name="two" class="easyui-combobox" data-options="editable:false" style="width:166px;height:35px;line-height:35px;">
						<option value=""></option>
						<c:forEach var="dict" items="${QUESTION_TYPE }">
						<option value="${dict.dictKey }">${dict.dictValue }</option>
						</c:forEach>
					</select>
					<span class="con-span">难度：</span>
					<select id="paperCfgList_three" name="three" class="easyui-combobox" data-options="editable:false" style="width:166px;height:35px;line-height:35px;">
						<option value=""></option>
						<c:forEach var="dict" items="${QUESTION_DIFFICULTY }">
						<option value="${dict.dictKey }">${dict.dictValue }</option>
						</c:forEach>
					</select>
					<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" data-options="selected:true" onclick="paperCfgListQuery();">查询</a>
					<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-clear" onclick="paperCfgListReset();">重置</a>
				</form>
			</div>
		</div>
		<%-- 试题数据表格 --%>
		<table id="paperCfgListQuestionGrid">
		</table>
	</div>
</div>