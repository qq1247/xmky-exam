<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<div class="easyui-layout" data-options="fit:true">
	<div data-options="region:'west',border:false" style="width: 340px;padding: 5px">
		<div class="easyui-panel" data-options="fit:true">
			<input type="hidden" id="paperCfg_paperId" name="paperId" value=""/>
			<ul id="paperCfgPaperTree"></ul>
			<div id="paperCfgAddMenu" class="easyui-menu" style="width:120px;">
				<my:auth url="paper/toPaperCfgAdd"><div onclick="toPaperCfgAdd()" data-options="iconCls:'icon-add'">添加章节</div></my:auth>
				<div class="menu-sep"></div>
				<div onclick="paperCfgPaperTreeFlush();paperCfgPreviewFlush();" data-options="iconCls:'icon-reload'">刷新</div>
			</div>
			<div id="paperCfgListMenu" class="easyui-menu" style="width:120px;">
				<my:auth url="paper/toPaperCfgEdit"><div onclick="toPaperCfgEdit()" data-options="iconCls:'icon-edit'">修改章节</div></my:auth>
				<div class="menu-sep"></div>
				<my:auth url="paper/toPaperCfgList"><div onclick="toPaperCfgList()" data-options="iconCls:'icon-add'">添加试题</div></my:auth>
				<my:auth url="paper/doPaperCfgDel"><div onclick="doPaperCfgDel()" data-options="iconCls:'icon-remove'">删除试题</div></my:auth>
				<div class="menu-sep"></div>
				<my:auth url="paper/toPaperCfgSort"><div onclick="toPaperCfgSort()" data-options="iconCls:'icon-arrow_ns'">排序</div></my:auth>
				<div onclick="paperCfgPaperTreeFlush();paperCfgPreviewFlush();" data-options="iconCls:'icon-reload'">刷新</div>
			</div>
			<div id="paperCfgDelMenu" class="easyui-menu" style="width:120px;">
				<my:auth url="paper/doPaperCfgDel"><div onclick="doPaperCfgDel()" data-options="iconCls:'icon-remove'">删除试题</div></my:auth>
				<div class="menu-sep"></div>
				<my:auth url="paper/toPaperCfgSort"><div onclick="toPaperCfgSort()" data-options="iconCls:'icon-arrow_ns'">排序</div></my:auth>
				<div onclick="paperCfgPaperTreeFlush();paperCfgPreviewFlush();" data-options="iconCls:'icon-reload'">刷新</div>
			</div>
		</div>
	</div>
	<div data-options="region:'center',border:false">
		<iframe id="paperCfgPreview" src="paper/toPaperCfgPreview?id=${id }" style="width: 98%;height: 98%;"></iframe>
	</div>
</div>