<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>试卷分类列表</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'west',border:false" style="width: 240px;padding:5px">
				<div class="easyui-panel" data-options="fit:true">
					<ul id="paperTypeTree"></ul>
					<div id="paperTypeTreeMenu" class="easyui-menu" style="width:120px;">
						<my:auth url="paperType/toAdd"><div onclick="toPaperTypeAdd()" data-options="iconCls:'icon-add'">添加</div></my:auth>
						<my:auth url="paperType/toEdit"><div onclick="toPaperTypeEditForMenu()" data-options="iconCls:'icon-edit'">修改</div></my:auth>
						<div class="menu-sep"></div>
						<my:auth url="paperType/doDel"><div onclick="doPaperTypeDelForMenu()" data-options="iconCls:'icon-remove'">删除</div></my:auth>
						<div class="menu-sep"></div>
						<div onclick="paperTypeTreeFlush()" data-options="iconCls:'icon-reload'">刷新</div>
					</div>
				</div>
			</div>
			<div data-options="region:'center',border:false">
				<div class="easyui-layout" data-options="fit:true">
					<div data-options="region:'center',border:false">
						<%-- 试卷分类查询条件 --%>
						<div id="paperTypeToolbar" style="padding:0 30px;">
							<div class="conditions">
								<form id="paperTypeQueryForm">
									<input type="hidden" id="paperType_one" name="one">
									<span class="con-span">名称：</span>
									<input name="two" class="easyui-textbox" style="width:166px;height:35px;line-height:35px;">
									<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" data-options="selected:true" onclick="paperTypeQuery();">查询</a>
									<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-clear" onclick="paperTypeReset();">重置</a>
								</form>
							</div>
							<div class="opt-buttons">
								<my:auth url="paperType/toAdd"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" data-options="selected:true" onclick="toPaperTypeAdd();">添加</a></my:auth>
								<my:auth url="paperType/toEdit"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" onclick="toPaperTypeEditForBtn();">修改</a></my:auth>
								<my:auth url="paperType/doDel"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" onclick="doPaperTypeDelForBtn();">删除</a></my:auth>
								<my:auth url="paperType/toMove"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" onclick="toPaperTypeMoveForBtn();">移动</a></my:auth>
							</div>
						</div>
						<%-- 试卷分类数据表格 --%>
						<table id="paperTypeGrid">
						</table>
					</div>
				</div>
			</div>
		</div>
	</body>
	<script type="text/javascript">
		//定义变量
		var paperTypeGrid = $("#paperTypeGrid"); //试卷分类表格对象
		var paperTypeQueryForm = $("#paperTypeQueryForm"); //试卷分类查询对象
		var paperTypeTree = $("#paperTypeTree"); //试卷分类树对象
		var curSelPaperTypeId = ""; //当前选中的试卷分类ID
		var curSelPaperTypeName = ""; //当前选中的试卷分类名称
	
		//页面加载完毕，执行如下方法：
		$(function() {
			initPaperTypeGrid();
			initPaperTypeTree();
		});
	
		//初始化试卷分类表格
		function initPaperTypeGrid() {
			paperTypeGrid.datagrid( {
				url : "",
				onDblClickRow : <my:auth url="paperType/toEdit">toPaperTypeEditForDblClick</my:auth>,
				toolbar : "#paperTypeToolbar",
				columns : [[ 
						{field : "ID", title : "", checkbox : true}, 
						{field : "NAME", title : "名称", width : 80, align : "center"},
						{field : "PARENT_NAME", title : "上级试卷分类 ", width : 80, align : "center"},
						{field : "NO", title : "排序", width : 80, align : "center"},
						]]
			});
		}
		
		//初始化试卷分类树
		function initPaperTypeTree(){
			paperTypeTree.tree({
				idFiled : "ID",
				textFiled : "NAME",
				parentField : "PARENT_ID",
				iconClsFiled : "ICON",
				checkedFiled : "CHECKED",
				lines : true,
			    url : "paperType/treeList",
				<my:auth url="paperType/doMove">dnd : true,
				onStopDrag : toPaperTypeMoveForMenu,</my:auth>
				onContextMenu : function(e, node){
					e.preventDefault();
					$(this).tree("select", node.target);
					$("#paperTypeTreeMenu").menu("show", {
						left : e.pageX,
						top : e.pageY
					})
				},
				onSelect : function(node){
					curSelPaperTypeId = node.ID;
					curSelPaperTypeName = node.NAME;
					$("#paperType_one").val(curSelPaperTypeId);
					paperTypeGrid.datagrid("uncheckAll");
					paperTypeGrid.datagrid("reload", $.fn.my.serializeObj(paperTypeQueryForm));
				},
				onLoadSuccess : function(node, data){
					if(!curSelPaperTypeId || !paperTypeGrid.datagrid("options").url){//如果是第一次
						curSelPaperTypeId = 1;
						paperTypeGrid.datagrid("options").url = "paperType/list";
					}
					
					var node = paperTypeTree.tree("find", curSelPaperTypeId);
					if(!node){
						curSelPaperTypeId = 1;
						node = paperTypeTree.tree("find", curSelPaperTypeId);
					}
					paperTypeTree.tree("select", node.target);
				}
			});
		}
	
		//试卷分类查询
		function paperTypeQuery() {
			paperTypeGrid.datagrid("uncheckAll");
			paperTypeGrid.datagrid("load", $.fn.my.serializeObj(paperTypeQueryForm));
		}
	
		//试卷分类重置
		function paperTypeReset() {
			paperTypeQueryForm.form("reset");
			paperTypeQuery();
		}
		
		//到达添加试卷分类页面
		function toPaperTypeAdd() {
			if(!curSelPaperTypeId){
				parent.$.messager.alert("提示消息", "请选择上级试卷分类！", "info");
				return;
			}
			
			var paperTypeAddDialog;
			paperTypeAddDialog = $("<div/>").dialog({
				title : "添加试卷分类",
				href : "paperType/toAdd",
				buttons : [{
					text : "添加", 
					iconCls : "icon-add", 
					selected : true, 
					handler : function (){
						doPaperTypeAdd(paperTypeAddDialog);
					}
				}],
				onLoad : function() {
					$("#paperType_parentId").val(curSelPaperTypeId);
					$("#paperType_parentName").val(curSelPaperTypeName);
					$("#paperType_name").textbox({
						required : true,
						validType : ["length[1, 16]"]
					});
					$("#paperType_no").numberspinner({
						required : true,
						min : 1,
						max : 100
					});
				}
			});
		}
		
		//完成添加试卷分类
		function doPaperTypeAdd(paperTypeAddDialog) {
			var paperTypeEditFrom = $("#paperTypeEditFrom");
			if(!paperTypeEditFrom.form("validate")){
				return;
			}
			$.messager.confirm("确认消息", "确定要添加？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.progress();
				paperTypeEditFrom.form("submit", {
					url : "paperType/doAdd",
					success : function(data) {
						paperTypeTree.tree("reload");
						$.messager.progress("close"); 
						
						var obj = $.parseJSON(data);
						if(!obj.success){
							parent.$.messager.alert("提示消息", obj.message, "info");
							return;
						}
						
						paperTypeAddDialog.dialog("close");
					}
				});
			})
		}
		
		//到达修改试卷分类页面
		function toPaperTypeEdit(id){
			var paperTypeEditDialog;
			paperTypeEditDialog = $("<div/>").dialog({
				title : "修改试卷分类",
				href : "paperType/toEdit",
				queryParams : {"id" : id},
				buttons : [{
					text : "修改", 
					iconCls : "icon-edit", 
					selected : true, 
					handler : function (){
						doPaperTypeEdit(paperTypeEditDialog);
					}
				}],
				onLoad : function() {
					$("#paperType_name").textbox({
						required : true,
						validType : ["length[1, 16]"]
					});
					$("#paperType_no").numberspinner({
						required : true,
						min : 1,
						max : 100
					});
				}
			});
		}
		
		//到达修改试卷分类页面
		function toPaperTypeEditForBtn(){
			var paperTypeGridRows = paperTypeGrid.datagrid("getChecked");
			if(paperTypeGridRows.length != 1){
				parent.$.messager.alert("提示消息", "请选择一行数据！", "info");
				return;
			}
			
			toPaperTypeEdit(paperTypeGridRows[0].ID);
		}
		
		//到达修改试卷分类页面
		function toPaperTypeEditForDblClick(rowIndex, rowData){
			paperTypeGrid.datagrid("uncheckAll");
			paperTypeGrid.datagrid("checkRow", rowIndex);
			toPaperTypeEditForBtn();
		}
		
		//到达修改试卷分类页面
		function toPaperTypeEditForMenu() {
			toPaperTypeEdit(curSelPaperTypeId);
		}
		
		//完成修改试卷分类
		function doPaperTypeEdit(paperTypeEditDialog) {
			var paperTypeEditFrom = $("#paperTypeEditFrom");
			if(!paperTypeEditFrom.form("validate")){
				return;
			}
			$.messager.confirm("确认消息", "确定要修改？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.progress();
				paperTypeEditFrom.form("submit", {
					url : "paperType/doEdit",
					success : function(data) {
						paperTypeTree.tree("reload");
						$.messager.progress("close"); 
						
						var obj = $.parseJSON(data);
						if(!obj.success){
							parent.$.messager.alert("提示消息", obj.message, "info");
							return;
						}
						
						paperTypeEditDialog.dialog("close");
					}
				});
			})
		}
		
		//完成删除试卷分类
		function doPaperTypeDel(params) {
			$.messager.confirm("确认消息", "确定要删除？", function(ok){
				if (!ok){
					return;
				}
				
				$.messager.progress();
				$.ajax({
					url : "paperType/doDel",
					data : params,
					success : function(obj){
						paperTypeTree.tree("reload");
						$.messager.progress("close");
						
						if(!obj.success){
							parent.$.messager.alert("提示消息", obj.message, "info");
						}
					}
				});
			});
		}
		
		//完成删除试卷分类
		function doPaperTypeDelForBtn() {
			//校验数据有效性
			var paperTypeGridRows = paperTypeGrid.datagrid("getChecked");
			if(paperTypeGridRows.length == 0){
				parent.$.messager.alert("提示消息", "请选择一行或多行数据！", "info");
				return;
			}
			
			//删除
			doPaperTypeDel($.fn.my.serializeField(paperTypeGridRows));
		}
		
		//完成删除试卷分类
		function doPaperTypeDelForMenu() {
			var params = {"ids" : curSelPaperTypeId};
			var parentNode = paperTypeTree.tree("find", curSelPaperTypeId);
			curSelPaperTypeId = parentNode.PARENT_ID;
			
			doPaperTypeDel(params);
		}
		
		//到达移动试卷分类页面
		function toPaperTypeMoveForBtn(){
			var paperTypeGridRows = paperTypeGrid.datagrid("getChecked");
			if(paperTypeGridRows.length != 1){
				parent.$.messager.alert("提示消息","请选择一行数据！", "info");
				return;
			}
			
			var paperTypeDialog;
			paperTypeDialog = $("<div/>").dialog({
				title : "选择试卷分类",
				width : 300,
				height : 400,
				href : "paperType/toMove",
				buttons : [{
					text : "确定", 
					iconCls : "icon-ok", 
					selected : true,
					handler : function (){
						var paperTypeMoveNode = $("#paperTypeMoveTree").tree("getSelected");
						if(!paperTypeMoveNode){
							parent.$.messager.alert("提示消息", "请选择一行数据！", "info");
							return;
						}
						
						var sourceId = paperTypeGridRows[0].ID;
						var sourceName = paperTypeGridRows[0].NAME;
						var targetId = paperTypeMoveNode.ID;
						var targetName = paperTypeMoveNode.NAME;
						if(sourceId == targetId){
							parent.$.messager.alert("提示消息", "源试卷分类和目标试卷分类一致！", "info");
							return;
						}
						if(paperTypeMoveNode.PARENT_SUB.indexOf(paperTypeGridRows[0].PARENT_SUB) >= 0){
							parent.$.messager.alert("提示消息", "父试卷分类不能移动到子试卷分类下！", "info");
							return;
						}
						
						doPaperTypeMove(sourceId, sourceName, targetId, targetName, paperTypeDialog);
					}
				}],
				onLoad : function(){
					var paperTypeMoveTree = $("#paperTypeMoveTree"); 
					paperTypeMoveTree.tree({
						idFiled : "ID",
						textFiled : "NAME",
						parentField : "PARENT_ID",
						iconClsFiled : "ICON",
						checkedFiled : "CHECKED",
						lines : true,
					    url : "paperType/movePaperTypeTreeList",
					});
				}
			});
		}

		//到达移动试卷分类页面
		function toPaperTypeMoveForMenu(node){
			//校验数据有效性
			var targetNode = paperTypeTree.tree("getParent", node.target);
			if(!targetNode){//拖动时有bug，有时会拖到空白地方。
				return;
			}
			var sourceId = node.ID;
			var sourceName = node.NAME;
			var sourceParentId = node.PARENT_ID;
			var targetId = targetNode.ID;
			var targetName = targetNode.NAME;
			if(sourceParentId == targetId){ //如果移动后的试卷分类和源试卷分类一样，不处理
				return;
			}
			
			//移动试卷分类
			doPaperTypeMove(sourceId, sourceName, targetId, targetName);
		}
		
		//完成移动试卷分类
		function doPaperTypeMove(sourceId, sourceName, targetId, targetName, dialog){
			$.messager.confirm("确认消息", "确定要移动【"+sourceName+"】到【"+targetName+"】？", function(ok){
				if (!ok){
					if(!dialog){
						paperTypeTree.tree("reload");//拖动后有bug，可能会错位，所以刷新一次。
					}
					return;
				}
				
				var params = {"sourceId" : sourceId, "targetId" : targetId};
				$.messager.progress();
				$.ajax({
					url : "paperType/doMove",
					data : params,
					success : function(obj){
						paperTypeTree.tree("reload");
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
		
		//刷新试卷分类
		function paperTypeTreeFlush(){
			paperTypeTree.tree("reload");
		}
	</script>
</html>