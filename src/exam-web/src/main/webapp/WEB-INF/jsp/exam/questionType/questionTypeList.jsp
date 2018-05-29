<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>试题分类列表</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'west',border:false" style="width: 240px;padding:5px">
				<div class="easyui-panel" data-options="fit:true">
					<ul id="questionTypeTree"></ul>
					<div id="questionTypeTreeMenu" class="easyui-menu" style="width:120px;">
						<my:auth url="questionType/toAdd"><div onclick="toQuestionTypeAdd()" data-options="iconCls:'icon-add'">添加</div></my:auth>
						<my:auth url="questionType/toEdit"><div onclick="toQuestionTypeEditForMenu()" data-options="iconCls:'icon-edit'">修改</div></my:auth>
						<div class="menu-sep"></div>
						<my:auth url="questionType/doDel"><div onclick="doQuestionTypeDelForMenu()" data-options="iconCls:'icon-remove'">删除</div></my:auth>
						<div class="menu-sep"></div>
						<div onclick="questionTypeTreeFlush()" data-options="iconCls:'icon-reload'">刷新</div>
					</div>
				</div>
			</div>
			<div data-options="region:'center',border:false">
				<div class="easyui-layout" data-options="fit:true">
					<div data-options="region:'center',border:false">
						<%-- 试题分类查询条件 --%>
						<div id="questionTypeToolbar" style="padding:0 30px;">
							<div class="conditions">
								<form id="questionTypeQueryForm">
									<input type="hidden" id="questionType_one" name="one">
									<span class="con-span">名称：</span>
									<input name="two" class="easyui-textbox" style="width:166px;height:35px;line-height:35px;">
									<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" data-options="selected:true" onclick="questionTypeQuery();">查询</a>
									<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-clear" onclick="questionTypeReset();">重置</a>
								</form>
							</div>
							<div class="opt-buttons">
								<my:auth url="questionType/toAdd"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" data-options="selected:true" onclick="toQuestionTypeAdd();">添加</a></my:auth>
								<my:auth url="questionType/toEdit"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" onclick="toQuestionTypeEditForBtn();">修改</a></my:auth>
								<my:auth url="questionType/doDel"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" onclick="doQuestionTypeDelForBtn();">删除</a></my:auth>
								<my:auth url="questionType/doMove"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" onclick="toQuestionTypeMoveForBtn();">移动</a></my:auth>
								<my:auth url="questionType/toAuth"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" onclick="toAuth();">设置权限</a></my:auth>
							</div>
						</div>
						<%-- 试题分类数据表格 --%>
						<table id="questionTypeGrid">
						</table>
					</div>
				</div>
			</div>
		</div>
	</body>
	<script type="text/javascript">
		//定义变量
		var questionTypeGrid = $("#questionTypeGrid"); //试题分类表格对象
		var questionTypeQueryForm = $("#questionTypeQueryForm"); //试题分类查询对象
		var questionTypeTree = $("#questionTypeTree"); //试题分类树对象
		var curSelQuestionTypeId = ""; //当前选中的试题分类ID
		var curSelQuestionTypeName = ""; //当前选中的试题分类名称
	
		//页面加载完毕，执行如下方法：
		$(function() {
			initQuestionTypeGrid();
			initQuestionTypeTree();
		});
	
		//初始化试题分类表格
		function initQuestionTypeGrid() {
			questionTypeGrid.datagrid( {
				url : "",
				onDblClickRow : <my:auth url="questionType/toEdit">toQuestionTypeEditForDblClick</my:auth>,
				toolbar : "#questionTypeToolbar",
				columns : [[ 
						{field : "ID", title : "", checkbox : true}, 
						{field : "NAME", title : "名称", width : 80, align : "center"},
						{field : "PARENT_NAME", title : "上级试题分类 ", width : 80, align : "center"},
						{field : "NO", title : "排序 ", width : 80, align : "center"}
						]]
			});
		}
		
		//初始化试题分类树
		function initQuestionTypeTree(){
			questionTypeTree.tree({
				idFiled : "ID",
				textFiled : "NAME",
				parentField : "PARENT_ID",
				iconClsFiled : "ICON",
				checkedFiled : "CHECKED",
				lines : true,
			    url : "questionType/treeList",
				<my:auth url="questionType/toMove">dnd : true,
				onStopDrag : toQuestionTypeMoveForMenu,</my:auth>
				onContextMenu : function(e, node){
					e.preventDefault();
					$(this).tree("select", node.target);
					$("#questionTypeTreeMenu").menu("show", {
						left : e.pageX,
						top : e.pageY
					})
				},
				onSelect : function(node){
					curSelQuestionTypeId = node.ID;
					curSelQuestionTypeName = node.NAME;
					$("#questionType_one").val(curSelQuestionTypeId);
					questionTypeGrid.datagrid("uncheckAll");
					questionTypeGrid.datagrid("reload", $.fn.my.serializeObj(questionTypeQueryForm));
				},
				onLoadSuccess : function(node, data){
					if(!curSelQuestionTypeId || !questionTypeGrid.datagrid("options").url){//如果是第一次
						curSelQuestionTypeId = 1;
						questionTypeGrid.datagrid("options").url = "questionType/list";
					}
					
					var node = questionTypeTree.tree("find", curSelQuestionTypeId);
					if(!node){
						curSelQuestionTypeId = 1;
						node = questionTypeTree.tree("find", curSelQuestionTypeId);
					}
					questionTypeTree.tree("select", node.target);
				}
			});
		}
	
		//试题分类查询
		function questionTypeQuery() {
			questionTypeGrid.datagrid("uncheckAll");
			questionTypeGrid.datagrid("load", $.fn.my.serializeObj(questionTypeQueryForm));
		}
	
		//试题分类重置
		function questionTypeReset() {
			questionTypeQueryForm.form("reset");
			questionTypeQuery();
		}
		
		//到达添加试题分类页面
		function toQuestionTypeAdd() {
			if(!curSelQuestionTypeId){
				parent.$.messager.alert("提示消息", "请选择上级试题分类！", "info");
				return;
			}
			
			var questionTypeAddDialog;
			questionTypeAddDialog = $("<div/>").dialog({
				title : "添加试题分类",
				href : "questionType/toAdd",
				buttons : [{
					text : "添加", 
					iconCls : "icon-add", 
					selected : true, 
					handler : function (){
						doQuestionTypeAdd(questionTypeAddDialog);
					}
				}],
				onLoad : function() {
					$("#questionType_parentId").val(curSelQuestionTypeId);
					$("#questionType_parentName").val(curSelQuestionTypeName);
					$("#questionType_name").textbox({
						required : true,
						validType : ["length[1, 16]"]
					});
					$("#questionType_no").numberspinner({
						required : true,
						min : 1,
						max : 100
					});
				}
			});
		}
		
		//完成添加试题分类
		function doQuestionTypeAdd(questionTypeAddDialog) {
			var questionTypeEditFrom = $("#questionTypeEditFrom");
			if(!questionTypeEditFrom.form("validate")){
				return;
			}
			$.messager.confirm("确认消息", "确定要添加？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.progress();
				questionTypeEditFrom.form("submit", {
					url : "questionType/doAdd",
					success : function(data) {
						questionTypeTree.tree("reload");
						$.messager.progress("close"); 
						
						var obj = $.parseJSON(data);
						if(!obj.success){
							parent.$.messager.alert("提示消息", obj.message, "info");
							return;
						}
						
						questionTypeAddDialog.dialog("close");
					}
				});
			})
		}
		
		//到达修改试题分类页面
		function toQuestionTypeEdit(id){
			var questionTypeEditDialog;
			questionTypeEditDialog = $("<div/>").dialog({
				title : "修改试题分类",
				href : "questionType/toEdit",
				queryParams : {"id" : id},
				buttons : [{
					text : "修改", 
					iconCls : "icon-edit", 
					selected : true, 
					handler : function (){
						doQuestionTypeEdit(questionTypeEditDialog);
					}
				}],
				onLoad : function() {
					$("#questionType_name").textbox({
						required : true,
						validType : ["length[1, 16]"]
					});
					$("#questionType_no").numberspinner({
						required : true,
						min : 1,
						max : 100
					});
				}
			});
		}
		
		//到达修改试题分类页面
		function toQuestionTypeEditForBtn(){
			var questionTypeGridRows = questionTypeGrid.datagrid("getChecked");
			if(questionTypeGridRows.length != 1){
				parent.$.messager.alert("提示消息", "请选择一行数据！", "info");
				return;
			}
			
			toQuestionTypeEdit(questionTypeGridRows[0].ID);
		}
		
		//到达修改试题分类页面
		function toQuestionTypeEditForDblClick(rowIndex, rowData){
			questionTypeGrid.datagrid("uncheckAll");
			questionTypeGrid.datagrid("checkRow", rowIndex);
			toQuestionTypeEditForBtn();
		}
		
		//到达修改试题分类页面
		function toQuestionTypeEditForMenu() {
			toQuestionTypeEdit(curSelQuestionTypeId);
		}
		
		//完成修改试题分类
		function doQuestionTypeEdit(questionTypeEditDialog) {
			var questionTypeEditFrom = $("#questionTypeEditFrom");
			if(!questionTypeEditFrom.form("validate")){
				return;
			}
			$.messager.confirm("确认消息", "确定要修改？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.progress();
				questionTypeEditFrom.form("submit", {
					url : "questionType/doEdit",
					success : function(data) {
						questionTypeTree.tree("reload");
						$.messager.progress("close"); 
						
						var obj = $.parseJSON(data);
						if(!obj.success){
							parent.$.messager.alert("提示消息", obj.message, "info");
							return;
						}
						
						questionTypeEditDialog.dialog("close");
					}
				});
			})
		}
		
		//完成删除试题分类
		function doQuestionTypeDel(params) {
			$.messager.confirm("确认消息", "确定要删除？", function(ok){
				if (!ok){
					return;
				}
				
				$.messager.progress();
				$.ajax({
					url : "questionType/doDel",
					data : params,
					success : function(obj){
						questionTypeTree.tree("reload");
						$.messager.progress("close");
						
						if(!obj.success){
							parent.$.messager.alert("提示消息", obj.message, "info");
						}
					}
				});
			});
		}
		
		//完成删除试题分类
		function doQuestionTypeDelForBtn() {
			//校验数据有效性
			var questionTypeGridRows = questionTypeGrid.datagrid("getChecked");
			if(questionTypeGridRows.length == 0){
				parent.$.messager.alert("提示消息", "请选择一行或多行数据！", "info");
				return;
			}
			
			//删除
			doQuestionTypeDel($.fn.my.serializeField(questionTypeGridRows));
		}
		
		//完成删除试题分类
		function doQuestionTypeDelForMenu() {
			var params = {"ids" : curSelQuestionTypeId};
			var parentNode = questionTypeTree.tree("find", curSelQuestionTypeId);
			curSelQuestionTypeId = parentNode.PARENT_ID;
			
			doQuestionTypeDel(params);
		}
		
		//到达移动试题分类页面
		function toQuestionTypeMoveForBtn(){
			var questionTypeGridRows = questionTypeGrid.datagrid("getChecked");
			if(questionTypeGridRows.length != 1){
				parent.$.messager.alert("提示消息","请选择一行数据！", "info");
				return;
			}
			
			var questionTypeDialog;
			questionTypeDialog = $("<div/>").dialog({
				title : "选择试题分类",
				width : 300,
				height : 400,
				href : "questionType/toMove",
				buttons : [{
					text : "确定", 
					iconCls : "icon-ok", 
					selected : true,
					handler : function (){
						var questionTypeMoveNode = $("#questionTypeMoveTree").tree("getSelected");
						if(!questionTypeMoveNode){
							parent.$.messager.alert("提示消息", "请选择一行数据！", "info");
							return;
						}
						
						var sourceId = questionTypeGridRows[0].ID;
						var sourceName = questionTypeGridRows[0].NAME;
						var targetId = questionTypeMoveNode.ID;
						var targetName = questionTypeMoveNode.NAME;
						if(sourceId == targetId){
							parent.$.messager.alert("提示消息", "源试题分类和目标试题分类一致！", "info");
							return;
						}
						if(questionTypeMoveNode.PARENT_SUB.indexOf(questionTypeGridRows[0].PARENT_SUB) >= 0){
							parent.$.messager.alert("提示消息", "父试题分类不能移动到子试题分类下！", "info");
							return;
						}
						
						doQuestionTypeMove(sourceId, sourceName, targetId, targetName, questionTypeDialog);
					}
				}],
				onLoad : function(){
					var questionTypeMoveTree = $("#questionTypeMoveTree"); 
					questionTypeMoveTree.tree({
						idFiled : "ID",
						textFiled : "NAME",
						parentField : "PARENT_ID",
						iconClsFiled : "ICON",
						checkedFiled : "CHECKED",
						lines : true,
					    url : "questionType/moveQuestionTypeTreeList",
					});
				}
			});
		}

		//到达移动试题分类页面
		function toQuestionTypeMoveForMenu(node){
			//校验数据有效性
			var targetNode = questionTypeTree.tree("getParent", node.target);
			if(!targetNode){//拖动时有bug，有时会拖到空白地方。
				return;
			}
			var sourceId = node.ID;
			var sourceName = node.NAME;
			var sourceParentId = node.PARENT_ID;
			var targetId = targetNode.ID;
			var targetName = targetNode.NAME;
			if(sourceParentId == targetId){ //如果移动后的试题分类和源试题分类一样，不处理
				return;
			}
			
			//移动试题分类
			doQuestionTypeMove(sourceId, sourceName, targetId, targetName);
		}
		
		//完成移动试题分类
		function doQuestionTypeMove(sourceId, sourceName, targetId, targetName, dialog){
			$.messager.confirm("确认消息", "确定要移动【"+sourceName+"】到【"+targetName+"】？", function(ok){
				if (!ok){
					if(!dialog){
						questionTypeTree.tree("reload");//拖动后有bug，可能会错位，所以刷新一次。
					}
					return;
				}
				
				var params = {"sourceId" : sourceId, "targetId" : targetId};
				$.messager.progress();
				$.ajax({
					url : "questionType/doMove",
					data : params,
					success : function(obj){
						questionTypeTree.tree("reload");
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
		
		//刷新试题分类
		function questionTypeTreeFlush(){
			questionTypeTree.tree("reload");
		}
		
		//到达权限页面
		function toAuth(){
			var paperAddListDialog;
			paperAddListDialog = $("<div/>").dialog({
				title : "权限列表",
				href : "questionType/toAuth",
				maximized : true,
				buttons : [{
					text : "添加",
					iconCls : "icon-add", 
					selected : true, 
					handler : function (){
						//doPaperAdd(paperAddListDialog);
					}
				}],
				onLoad : function() {
					var paperAddListGrid = $("#paperAddListGrid");
					paperAddListGrid.datagrid( {
						toolbar : "#paperAddListToolbar",
						columns : [[ 
								{field : "ID", title : "", checkbox : true},
								{field : "NAME", title : "名称", width : 50, align : "center"},
								{field : "TOTLE_SCORE", title : "总分数", width : 80, align : "center"},
								{field : "STATE_NAME", title : "状态", width : 80, align : "center"}
								]]
					});
					
					var paperAddListTypeTree = $("#paperAddListTypeTree");
					var paperAddListQueryForm = $("#paperAddListQueryForm");
					var curSelPaperTypeId = "";
					var curSelPaperTypeName = "";
					paperAddListTypeTree.tree({
						idFiled : "ID",
						textFiled : "NAME",
						parentField : "PARENT_ID",
						iconClsFiled : "ICON",
						checkedFiled : "CHECKED",
						lines : true,
					    url : "exam/paperAddListTypeTreeList",
						onSelect : function(node){
							curSelPaperTypeId = node.ID;
							curSelPaperTypeName = node.NAME;
							
							$("#paperAddList_one").val(curSelPaperTypeId);
							paperAddListGrid.datagrid("uncheckAll");
							paperAddListGrid.datagrid("reload", $.fn.my.serializeObj(paperAddListQueryForm));
						},
						onLoadSuccess : function(node, data){
							if(!curSelPaperTypeId || !paperAddListGrid.datagrid("options").url){//如果是第一次
								curSelPaperTypeId = 1;
								paperAddListGrid.datagrid("options").url = "exam/paperAddList";
							}
							
							var node = paperAddListTypeTree.tree("find", curSelPaperTypeId);
							if(!node){
								curSelPaperTypeId = 1;
								node = paperAddListTypeTree.tree("find", curSelPaperTypeId);
							}
							paperAddListTypeTree.tree("select", node.target);
						}
					});
				}
			});
		}
	</script>
</html>