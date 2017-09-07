<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>考试分类列表</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'west',border:false" style="width: 240px;padding:5px">
				<div class="easyui-panel" data-options="fit:true">
					<ul id="examTypeTree"></ul>
					<div id="examTypeTreeMenu" class="easyui-menu" style="width:120px;">
						<my:auth url="examType/toAdd"><div onclick="toExamTypeAdd()" data-options="iconCls:'icon-add'">添加</div></my:auth>
						<my:auth url="examType/toEdit"><div onclick="toExamTypeEditForMenu()" data-options="iconCls:'icon-edit'">修改</div></my:auth>
						<div class="menu-sep"></div>
						<my:auth url="examType/doDel"><div onclick="doExamTypeDelForMenu()" data-options="iconCls:'icon-remove'">删除</div></my:auth>
						<div class="menu-sep"></div>
						<div onclick="examTypeTreeFlush()" data-options="iconCls:'icon-reload'">刷新</div>
					</div>
				</div>
			</div>
			<div data-options="region:'center',border:false">
				<div class="easyui-layout" data-options="fit:true">
					<div data-options="region:'center',border:false">
						<%-- 考试分类查询条件 --%>
						<div id="examTypeToolbar" style="padding:0 30px;">
							<div class="conditions">
								<form id="examTypeQueryForm">
									<input type="hidden" id="examType_one" name="one">
									<span class="con-span">名称：</span>
									<input name="two" class="easyui-textbox" style="width:166px;height:35px;line-height:35px;">
									<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" data-options="selected:true" onclick="examTypeQuery();">查询</a>
									<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-clear" onclick="examTypeReset();">重置</a>
								</form>
							</div>
							<div class="opt-buttons">
								<my:auth url="examType/toAdd"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" data-options="selected:true" onclick="toExamTypeAdd();">添加</a></my:auth>
								<my:auth url="examType/toEdit"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" onclick="toExamTypeEditForBtn();">修改</a></my:auth>
								<my:auth url="examType/doDel"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" onclick="doExamTypeDelForBtn();">删除</a></my:auth>
								<my:auth url="examType/toMove"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" onclick="toExamTypeMoveForBtn();">移动</a></my:auth>
							</div>
						</div>
						<%-- 考试分类数据表格 --%>
						<table id="examTypeGrid">
						</table>
					</div>
				</div>
			</div>
		</div>
	</body>
	<script type="text/javascript">
		//定义变量
		var examTypeGrid = $("#examTypeGrid"); //考试分类表格对象
		var examTypeQueryForm = $("#examTypeQueryForm"); //考试分类查询对象
		var examTypeTree = $("#examTypeTree"); //考试分类树对象
		var curSelExamTypeId = ""; //当前选中的考试分类ID
		var curSelExamTypeName = ""; //当前选中的考试分类名称
	
		//页面加载完毕，执行如下方法：
		$(function() {
			initExamTypeGrid();
			initExamTypeTree();
		});
	
		//初始化考试分类表格
		function initExamTypeGrid() {
			examTypeGrid.datagrid( {
				url : "",
				onDblClickRow : <my:auth url="examType/toEdit">toExamTypeEditForDblClick</my:auth>,
				toolbar : "#examTypeToolbar",
				columns : [[ 
						{field : "ID", title : "", checkbox : true}, 
						{field : "NAME", title : "名称", width : 80, align : "center"},
						{field : "PARENT_NAME", title : "上级考试分类 ", width : 80, align : "center"},
						{field : "NO", title : "排序", width : 80, align : "center"}
						]]
			});
		}
		
		//初始化考试分类树
		function initExamTypeTree(){
			examTypeTree.tree({
				idFiled : "ID",
				textFiled : "NAME",
				parentField : "PARENT_ID",
				iconClsFiled : "ICON",
				checkedFiled : "CHECKED",
				lines : true,
			    url : "examType/treeList",
				<my:auth url="examType/toMove">dnd : true,
				onStopDrag : toExamTypeMoveForMenu,</my:auth>
				onContextMenu : function(e, node){
					e.preventDefault();
					$(this).tree("select", node.target);
					$("#examTypeTreeMenu").menu("show", {
						left : e.pageX,
						top : e.pageY
					})
				},
				onSelect : function(node){
					curSelExamTypeId = node.ID;
					curSelExamTypeName = node.NAME;
					$("#examType_one").val(curSelExamTypeId);
					examTypeGrid.datagrid("uncheckAll");
					examTypeGrid.datagrid("reload", $.fn.my.serializeObj(examTypeQueryForm));
				},
				onLoadSuccess : function(node, data){
					if(!curSelExamTypeId || !examTypeGrid.datagrid("options").url){//如果是第一次
						curSelExamTypeId = 1;
						examTypeGrid.datagrid("options").url = "examType/list";
					}
					
					var node = examTypeTree.tree("find", curSelExamTypeId);
					if(!node){
						curSelExamTypeId = 1;
						node = examTypeTree.tree("find", curSelExamTypeId);
					}
					examTypeTree.tree("select", node.target);
				}
			});
		}
	
		//考试分类查询
		function examTypeQuery() {
			examTypeGrid.datagrid("uncheckAll");
			examTypeGrid.datagrid("load", $.fn.my.serializeObj(examTypeQueryForm));
		}
	
		//考试分类重置
		function examTypeReset() {
			examTypeQueryForm.form("reset");
			examTypeQuery();
		}
		
		//到达添加考试分类页面
		function toExamTypeAdd() {
			if(!curSelExamTypeId){
				parent.$.messager.alert("提示消息", "请选择上级考试分类！", "info");
				return;
			}
			
			var examTypeAddDialog;
			examTypeAddDialog = $("<div/>").dialog({
				title : "添加考试分类",
				href : "examType/toAdd",
				buttons : [{
					text : "添加", 
					iconCls : "icon-add", 
					selected : true, 
					handler : function (){
						doExamTypeAdd(examTypeAddDialog);
					}
				}],
				onLoad : function() {
					$("#examType_parentId").val(curSelExamTypeId);
					$("#examType_parentName").val(curSelExamTypeName);
					$("#examType_name").textbox({
						required : true,
						validType : ["length[1, 16]"]
					});
					$("#examType_no").numberspinner({
						required : true,
						min : 1,
						max : 100
					});
				}
			});
		}
		
		//完成添加考试分类
		function doExamTypeAdd(examTypeAddDialog) {
			var examTypeEditFrom = $("#examTypeEditFrom");
			if(!examTypeEditFrom.form("validate")){
				return;
			}
			$.messager.confirm("确认消息", "确定要添加？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.progress();
				examTypeEditFrom.form("submit", {
					url : "examType/doAdd",
					success : function(data) {
						examTypeTree.tree("reload");
						$.messager.progress("close"); 
						
						var obj = $.parseJSON(data);
						if(!obj.success){
							parent.$.messager.alert("提示消息", obj.message, "info");
							return;
						}
						
						examTypeAddDialog.dialog("close");
					}
				});
			})
		}
		
		//到达修改考试分类页面
		function toExamTypeEdit(id){
			var examTypeEditDialog;
			examTypeEditDialog = $("<div/>").dialog({
				title : "修改考试分类",
				href : "examType/toEdit",
				queryParams : {"id" : id},
				buttons : [{
					text : "修改", 
					iconCls : "icon-edit", 
					selected : true, 
					handler : function (){
						doExamTypeEdit(examTypeEditDialog);
					}
				}],
				onLoad : function() {
					$("#examType_name").textbox({
						required : true,
						validType : ["length[1, 16]"]
					});
					$("#examType_no").numberspinner({
						required : true,
						min : 1,
						max : 100
					});
				}
			});
		}
		
		//到达修改考试分类页面
		function toExamTypeEditForBtn(){
			var examTypeGridRows = examTypeGrid.datagrid("getChecked");
			if(examTypeGridRows.length != 1){
				parent.$.messager.alert("提示消息", "请选择一行数据！", "info");
				return;
			}
			
			toExamTypeEdit(examTypeGridRows[0].ID);
		}
		
		//到达修改考试分类页面
		function toExamTypeEditForDblClick(rowIndex, rowData){
			examTypeGrid.datagrid("uncheckAll");
			examTypeGrid.datagrid("checkRow", rowIndex);
			toExamTypeEditForBtn();
		}
		
		//到达修改考试分类页面
		function toExamTypeEditForMenu() {
			toExamTypeEdit(curSelExamTypeId);
		}
		
		//完成修改考试分类
		function doExamTypeEdit(examTypeEditDialog) {
			var examTypeEditFrom = $("#examTypeEditFrom");
			if(!examTypeEditFrom.form("validate")){
				return;
			}
			$.messager.confirm("确认消息", "确定要修改？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.progress();
				examTypeEditFrom.form("submit", {
					url : "examType/doEdit",
					success : function(data) {
						examTypeTree.tree("reload");
						$.messager.progress("close"); 
						
						var obj = $.parseJSON(data);
						if(!obj.success){
							parent.$.messager.alert("提示消息", obj.message, "info");
							return;
						}
						
						examTypeEditDialog.dialog("close");
					}
				});
			})
		}
		
		//完成删除考试分类
		function doExamTypeDel(params) {
			$.messager.confirm("确认消息", "确定要删除？", function(ok){
				if (!ok){
					return;
				}
				
				$.messager.progress();
				$.ajax({
					url : "examType/doDel",
					data : params,
					success : function(obj){
						examTypeTree.tree("reload");
						$.messager.progress("close");
						
						if(!obj.success){
							parent.$.messager.alert("提示消息", obj.message, "info");
						}
					}
				});
			});
		}
		
		//完成删除考试分类
		function doExamTypeDelForBtn() {
			//校验数据有效性
			var examTypeGridRows = examTypeGrid.datagrid("getChecked");
			if(examTypeGridRows.length == 0){
				parent.$.messager.alert("提示消息", "请选择一行或多行数据！", "info");
				return;
			}
			
			//删除
			doExamTypeDel($.fn.my.serializeField(examTypeGridRows));
		}
		
		//完成删除考试分类
		function doExamTypeDelForMenu() {
			var params = {"ids" : curSelExamTypeId};
			var parentNode = examTypeTree.tree("find", curSelExamTypeId);
			curSelExamTypeId = parentNode.PARENT_ID;
			
			doExamTypeDel(params);
		}
		
		//到达移动考试分类页面
		function toExamTypeMoveForBtn(){
			var examTypeGridRows = examTypeGrid.datagrid("getChecked");
			if(examTypeGridRows.length != 1){
				parent.$.messager.alert("提示消息","请选择一行数据！", "info");
				return;
			}
			
			var examTypeDialog;
			examTypeDialog = $("<div/>").dialog({
				title : "选择考试分类",
				width : 300,
				height : 400,
				href : "examType/toMove",
				buttons : [{
					text : "确定", 
					iconCls : "icon-ok", 
					selected : true,
					handler : function (){
						var examTypeMoveNode = $("#examTypeMoveTree").tree("getSelected");
						if(!examTypeMoveNode){
							parent.$.messager.alert("提示消息", "请选择一行数据！", "info");
							return;
						}
						
						var sourceId = examTypeGridRows[0].ID;
						var sourceName = examTypeGridRows[0].NAME;
						var targetId = examTypeMoveNode.ID;
						var targetName = examTypeMoveNode.NAME;
						if(sourceId == targetId){
							parent.$.messager.alert("提示消息", "源考试分类和目标考试分类一致！", "info");
							return;
						}
						if(examTypeMoveNode.PARENT_SUB.indexOf(examTypeGridRows[0].PARENT_SUB) >= 0){
							parent.$.messager.alert("提示消息", "父考试分类不能移动到子考试分类下！", "info");
							return;
						}
						
						doExamTypeMove(sourceId, sourceName, targetId, targetName, examTypeDialog);
					}
				}],
				onLoad : function(){
					var examTypeMoveTree = $("#examTypeMoveTree"); 
					examTypeMoveTree.tree({
						idFiled : "ID",
						textFiled : "NAME",
						parentField : "PARENT_ID",
						iconClsFiled : "ICON",
						checkedFiled : "CHECKED",
						lines : true,
					    url : "examType/moveExamTypeTreeList",
					});
				}
			});
		}

		//到达移动考试分类页面
		function toExamTypeMoveForMenu(node){
			//校验数据有效性
			var targetNode = examTypeTree.tree("getParent", node.target);
			if(!targetNode){//拖动时有bug，有时会拖到空白地方。
				return;
			}
			var sourceId = node.ID;
			var sourceName = node.NAME;
			var sourceParentId = node.PARENT_ID;
			var targetId = targetNode.ID;
			var targetName = targetNode.NAME;
			if(sourceParentId == targetId){ //如果移动后的考试分类和源考试分类一样，不处理
				return;
			}
			
			//移动考试分类
			doExamTypeMove(sourceId, sourceName, targetId, targetName);
		}
		
		//完成移动考试分类
		function doExamTypeMove(sourceId, sourceName, targetId, targetName, dialog){
			$.messager.confirm("确认消息", "确定要移动【"+sourceName+"】到【"+targetName+"】？", function(ok){
				if (!ok){
					if(!dialog){
						examTypeTree.tree("reload");//拖动后有bug，可能会错位，所以刷新一次。
					}
					return;
				}
				
				var params = {"sourceId" : sourceId, "targetId" : targetId};
				$.messager.progress();
				$.ajax({
					url : "examType/doMove",
					data : params,
					success : function(obj){
						examTypeTree.tree("reload");
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
		
		//刷新考试分类
		function examTypeTreeFlush(){
			examTypeTree.tree("reload");
		}
	</script>
</html>