<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>资源列表</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'west',border:false" style="width: 240px;padding:5px">
				<div class="easyui-panel" data-options="fit:true">
					<select id="resTreeType" name="type" class="easyui-combobox" data-options="editable:false,onSelect:resTreeFlush" style="width:229px;height:35px;line-height:35px;">
						<c:forEach var="dict" items="${RES_TYPE }">
						<option value="${dict.dictKey }">${dict.dictValue }</option>
						</c:forEach>
					</select>
					<ul id="resTree"></ul>
					<div id="resTreeMenu" class="easyui-menu" style="width:120px;">
						<my:auth url="res/toAdd"><div onclick="toResAdd()" data-options="iconCls:'icon-add'">添加</div></my:auth>
						<my:auth url="res/toEdit"><div onclick="toResEditForMenu()" data-options="iconCls:'icon-edit'">修改</div></my:auth>
						<div class="menu-sep"></div>
						<my:auth url="res/doDel"><div onclick="doResDelForMenu()" data-options="iconCls:'icon-remove'">删除</div></my:auth>
						<div class="menu-sep"></div>
						<div onclick="resTreeFlush()" data-options="iconCls:'icon-reload'">刷新</div>
					</div>
				</div>
			</div>
			<div data-options="region:'center',border:false">
				<div class="easyui-layout" data-options="fit:true">
					<div data-options="region:'center',border:false">
						<%-- 资源查询条件 --%>
						<div id="resToolbar" style="padding:0 30px;">
							<div class="conditions">
								<form id="resQueryForm">
									<input type="hidden" id="res_one" name="one">
									<input type="hidden" id="res_ten" name="ten">
									<span class="con-span">名称： </span>
									<input name="two" class="easyui-textbox" style="width:166px;height:35px;line-height:35px;">
									<span class="con-span">url： </span>
									<input name="three" class="easyui-textbox" style="width:166px;height:35px;line-height:35px;">
									<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" data-options="selected:true" onclick="resQuery();">查询</a>
									<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-clear" onclick="resReset();">重置</a>
								</form>
							</div>
							<div class="opt-buttons">
								<my:auth url="res/toAdd"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" data-options="selected:true" onclick="toResAdd();">添加</a></my:auth>
								<my:auth url="res/toEdit"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" onclick="toResEditForBtn();">修改</a></my:auth>
								<my:auth url="res/doDel"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" onclick="doResDelForBtn();">删除</a></my:auth>
								<my:auth url="res/toMove"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" onclick="toResMoveForBtn();">移动</a></my:auth>
							</div>
						</div>
						<%-- 资源数据表格 --%>
						<table id="resGrid">
						</table>	
					</div>
				</div>
			</div>
		</div>
	</body>
	<script type="text/javascript">
		//定义变量
		var resGrid = $("#resGrid"); //资源表格对象
		var resQueryForm = $("#resQueryForm"); //资源查询对象
		var resTree = $("#resTree"); //资源树对象
		var curSelResId = ""; //当前选中的资源ID
		var curSelResName = ""; //当前选中的资源名称
		var resTreeType = $("#resTreeType");
	
		//页面加载完毕，执行如下方法：
		$(function() {
			initResGrid();
			initResTree();
		});
	
		//初始化资源表格
		function initResGrid() {
			resGrid.datagrid( {
				url : "",
				onDblClickRow : <my:auth url="res/toEdit">toResEditForDblClick</my:auth>, 
				toolbar : "#resToolbar",
				columns : [[ 
						{field : "ID", title : "", checkbox : true}, 
						{field : "NAME", title : "名称", width : 80, align : "center"},
						{field : "URL", title : "ulr", width : 160, align : "center"},
						{field : "NO", title : "排序", width : 80, align : "center"},
						{field : "PARENT_NAME", title : "上级资源 ", width : 80, align : "center"}
						]]
			});
		}
		
		//初始化资源树
		function initResTree(){
			resTree.tree({
				idFiled : "ID",
				textFiled : "NAME",
				parentField : "PARENT_ID",
				iconClsFiled : "ICON",
				checkedFiled : "CHECKED",
				lines : true,
			    url : "res/treeList",
				<my:auth url="res/toMove">dnd : true,
				onStopDrag : toResMoveForMenu,</my:auth>
				onContextMenu : function(e, node){
					e.preventDefault();
					$(this).tree("select", node.target);
					$("#resTreeMenu").menu("show", {
						left : e.pageX,
						top : e.pageY
					})
				},
				onSelect : function(node){
					curSelResId = node.ID;
					curSelResName = node.NAME;
					$("#res_one").val(curSelResId);
					resGrid.datagrid("uncheckAll");
					resGrid.datagrid("reload", $.fn.my.serializeObj(resQueryForm));
				},
				onBeforeLoad : function(node, param){
					var type = resTreeType.combobox("getValue");
					param.type = type;
					$("#res_ten").val(type);
				},
				onLoadSuccess : function(node, data){
					if(!curSelResId || !resGrid.datagrid("options").url){//如果是第一次
						curSelResId = 1;
						resGrid.datagrid("options").url = "res/list";
						resTree.tree("collapseAll");
					}
					
					var node = resTree.tree("find", curSelResId);
					if(!node){
						curSelResId = 1;
						node = orgTree.tree("find", curSelResId);
					}
					resTree.tree("select", node.target);
				}
			});
		}
	
		//资源查询
		function resQuery() {
			resGrid.datagrid("uncheckAll");
			resGrid.datagrid("load", $.fn.my.serializeObj(resQueryForm));
		}
	
		//资源重置
		function resReset() {
			resQueryForm.form("reset");
			resQuery();
		}
		
		//到达添加资源页面
		function toResAdd() {
			if(!curSelResId){
				parent.$.messager.alert("提示消息", "请选择上级资源！", "info");
				return;
			}
			
			var resAddDialog;
			resAddDialog = $("<div/>").dialog({
				title : "添加资源",
				href : "res/toAdd",
				buttons : [{
					text : "添加", 
					iconCls : "icon-add", 
					selected : true, 
					handler : function (){
						doResAdd(resAddDialog);
					}
				}],
				onLoad : function() {
					$("#res_parentId").val(curSelResId);
					$("#res_parentName").val(curSelResName);
					$("#res_type").val(resTreeType.combobox("getValue"));
					$("#res_name").textbox({
						required : true,
						validType : ["length[1, 16]"]
					});
					$("#res_icon").textbox({
						validType : ["length[1, 16]"]
					});
					$("#res_url").textbox({
						required : true,
						validType : ["length[1, 512]"]
					});
					$("#res_no").numberspinner({
						required : true,
						min : 1,
						max : 100
					});
				}
			});
		}

		//完成添加资源
		function doResAdd(resAddDialog) {
			var resEditFrom = $("#resEditFrom");
			if(!resEditFrom.form("validate")){
				return;
			}
			$.messager.confirm("确认消息", "确定要添加？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.progress();
				resEditFrom.form("submit", {
					url : "res/doAdd",
					success : function(data) {
						resTree.tree("reload");
						$.messager.progress("close");
	
						var obj = $.parseJSON(data);
						if (!obj.success) {
							parent.$.messager.alert("提示消息", obj.message, "info");
							return;
						}
	
						resAddDialog.dialog("close");
					}
				});
			})
		}

		//到达修改资源页面
		function toResEdit(id) {
			var resEditDialog;
			resEditDialog = $("<div/>").dialog({
				title : "修改资源",
				href : "res/toEdit",
				queryParams : {"id" : id},
				buttons : [{
					text : "修改",
					iconCls : "icon-edit",
					selected : true,
					handler : function() {
						doResEdit(resEditDialog);
					}
				}],
				onLoad : function() {
					$("#res_name").textbox({
						required : true,
						validType : ["length[1, 16]"]
					});
					$("#res_icon").textbox({
						validType : ["length[1, 16]"]
					});
					$("#res_url").textbox({
						required : true,
						validType : ["length[1, 512]"]
					});
					$("#res_no").numberspinner({
						required : true,
						min : 1,
						max : 100
					});
				}
			});
		}

		//到达修改资源页面
		function toResEditForBtn() {
			var resGridRows = resGrid.datagrid("getChecked");
			if (resGridRows.length != 1) {
				parent.$.messager.alert("提示消息", "请选择一行数据！", "info");
				return;
			}

			toResEdit(resGridRows[0].ID);
		}

		//到达修改资源页面
		function toResEditForDblClick(rowIndex, rowData) {
			resGrid.datagrid("uncheckAll");
			resGrid.datagrid("checkRow", rowIndex);
			toResEditForBtn();
		}

		//到达修改资源页面
		function toResEditForMenu() {
			toResEdit(curSelResId);
		}

		//完成修改资源
		function doResEdit(resEditDialog) {
			var resEditFrom = $("#resEditFrom");
			if(!resEditFrom.form("validate")){
				return;
			}
			$.messager.confirm("确认消息", "确定要修改？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.progress();
				resEditFrom.form("submit", {
					url : "res/doEdit",
					success : function(data) {
						resTree.tree("reload");
						$.messager.progress("close");
	
						var obj = $.parseJSON(data);
						if (!obj.success) {
							parent.$.messager.alert("提示消息", obj.message, "info");
							return;
						}
	
						resEditDialog.dialog("close");
					}
				});
			})
		}

		//完成删除资源
		function doResDel(params) {
			$.messager.confirm("确认消息", "确定要删除？", function(ok) {
				if (!ok) {
					return;
				}

				$.messager.progress();
				$.ajax({
					url : "res/doDel",
					data : params,
					success : function(obj) {
						resTree.tree("reload");
						$.messager.progress("close");

						if (!obj.success) {
							parent.$.messager.alert("提示消息", obj.message, "info");
						}
					}
				});
			});
		}

		//完成删除资源
		function doResDelForBtn() {
			//校验数据有效性
			var resGridRows = resGrid.datagrid("getChecked");
			if (resGridRows.length == 0) {
				parent.$.messager.alert("提示消息", "请选择一行或多行数据！", "info");
				return;
			}

			//删除
			doResDel($.fn.my.serializeField(resGridRows));
		}

		//完成删除资源
		function doResDelForMenu() {
			var params = {"ids" : curSelResId};
			var parentNode = resTree.tree("find", curSelResId);
			curSelResId = parentNode.PARENT_ID;

			doResDel(params);
		}
		
		//到达移动资源页面
		function toResMoveForBtn(){
			var resGridRows = resGrid.datagrid("getChecked");
			if(resGridRows.length != 1){
				parent.$.messager.alert("提示消息","请选择一行数据！", "info");
				return;
			}
			
			var resDialog;
			resDialog = $("<div/>").dialog({
				title : "选择资源",
				width : 300,
				height : 400,
				href : "res/toMove",
				buttons : [{
					text : "确定", 
					iconCls : "icon-ok", 
					selected : true,
					handler : function (){
						var resMoveNode = $("#resMoveTree").tree("getSelected");
						if(!resMoveNode){
							parent.$.messager.alert("提示消息", "请选择一行数据！", "info");
							return;
						}
						
						var sourceId = resGridRows[0].ID;
						var sourceName = resGridRows[0].NAME;
						var targetId = resMoveNode.ID;
						var targetName = resMoveNode.NAME;
						if(sourceId == targetId){
							parent.$.messager.alert("提示消息", "源资源和目标资源一致！", "info");
							return;
						}
						if(resMoveNode.PARENT_SUB.indexOf(resGridRows[0].PARENT_SUB) >= 0){
							parent.$.messager.alert("提示消息", "父资源不能移动到子资源下！", "info");
							return;
						}
						
						doResMove(sourceId, sourceName, targetId, targetName, resDialog);
					}
				}],
				onLoad : function(){
					var resMoveTree = $("#resMoveTree"); 
					resMoveTree.tree({
						idFiled : "ID",
						textFiled : "NAME",
						parentField : "PARENT_ID",
						iconClsFiled : "ICON",
						checkedFiled : "CHECKED",
						lines : true,
					    url : "res/moveResTreeList",
					});
				}
			});
		}
		
		//到达移动资源页面
		function toResMoveForMenu(node){
			//校验数据有效性
			var targetNode = resTree.tree("getParent", node.target);
			if(!targetNode){//拖动时有bug，有时会拖到空白地方。
				return;
			}
			var sourceId = node.ID;
			var sourceName = node.NAME;
			var sourceParentId = node.PARENT_ID;
			var targetId = targetNode.ID;
			var targetName = targetNode.NAME;
			if(sourceParentId == targetId){ //如果移动后的资源和源资源一样，不处理
				return;
			}
			
			//移动资源
			doResMove(sourceId, sourceName, targetId, targetName);
		}

		//完成移动资源
		function doResMove(sourceId, sourceName, targetId, targetName, dialog){
			$.messager.confirm("确认消息", "确定要移动【"+sourceName+"】到【"+targetName+"】？", function(ok){
				if (!ok){
					if(!dialog){
						resTree.tree("reload");//拖动后有bug，可能会错位，所以刷新一次。
					}
					return;
				}
				
				var params = {"sourceId" : sourceId, "targetId" : targetId};
				$.messager.progress();
				$.ajax({
					url : "res/doMove",
					data : params,
					success : function(obj){
						resTree.tree("reload");
						if(dialog){
							dialog.dialog("close");
						}
						$.messager.progress("close");
						
						if(!obj.success){
							parent.$.messager.alert("提示消息", obj.message, "info");
						}
					}
				});
			});
		}

		//刷新资源
		function resTreeFlush() {
			resTree.tree("reload");
		}
	</script>
</html>