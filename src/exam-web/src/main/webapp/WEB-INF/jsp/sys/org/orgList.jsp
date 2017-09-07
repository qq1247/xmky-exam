<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>组织机构列表</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'west',border:false" style="width: 240px;padding:5px">
				<div class="easyui-panel" data-options="fit:true">
					<ul id="orgTree"></ul>
					<div id="orgTreeMenu" class="easyui-menu" style="width:120px;">
						<my:auth url="org/toAdd"><div onclick="toOrgAdd()" data-options="iconCls:'icon-add'">添加</div></my:auth>
						<my:auth url="org/toEdit"><div onclick="toOrgEditForMenu()" data-options="iconCls:'icon-edit'">修改</div></my:auth>
						<div class="menu-sep"></div>
						<my:auth url="org/doDel"><div onclick="doOrgDelForMenu()" data-options="iconCls:'icon-remove'">删除</div></my:auth>
						<div class="menu-sep"></div>
						<div onclick="orgTreeFlush()" data-options="iconCls:'icon-reload'">刷新</div>
					</div>
				</div>
			</div>
			<div data-options="region:'center',border:false">
				<div class="easyui-layout" data-options="fit:true">
					<div data-options="region:'center',border:false">
						<%-- 组织机构查询条件 --%>
						<div id="orgToolbar" style="padding:0 30px;">
							<div class="conditions">
								<form id="orgQueryForm">
									<input type="hidden" id="org_one" name="one">
									<span class="con-span">名称：</span>
									<input name="two" class="easyui-textbox" style="width:166px;height:35px;line-height:35px;">
									<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" data-options="selected:true" onclick="orgQuery();">查询</a>
									<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-clear" onclick="orgReset();">重置</a>
								</form>
							</div>
							<div class="opt-buttons">
								<my:auth url="org/toAdd"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" data-options="selected:true" onclick="toOrgAdd();">添加</a></my:auth>
								<my:auth url="org/toEdit"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" onclick="toOrgEditForBtn();">修改</a></my:auth>
								<my:auth url="org/doDel"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" onclick="doOrgDelForBtn();">删除</a></my:auth>
								<my:auth url="org/toMove"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" onclick="toOrgMoveForBtn();">移动</a></my:auth>
							</div>
						</div>
						<%-- 组织机构数据表格 --%>
						<table id="orgGrid">
						</table>
					</div>
				</div>
			</div>
		</div>
	</body>
	<script type="text/javascript">
		//定义变量
		var orgGrid = $("#orgGrid"); //组织机构表格对象
		var orgQueryForm = $("#orgQueryForm"); //组织机构查询对象
		var orgTree = $("#orgTree"); //组织机构树对象
		var curSelOrgId = ""; //当前选中的组织机构ID
		var curSelOrgName = ""; //当前选中的组织机构名称
	
		//页面加载完毕，执行如下方法：
		$(function() {
			initOrgGrid();
			initOrgTree();
		});
	
		//初始化组织机构表格
		function initOrgGrid() {
			orgGrid.datagrid( {
				url : "",
				onDblClickRow : <my:auth url="org/toEdit">toOrgEditForDblClick</my:auth>,
				toolbar : "#orgToolbar",
				columns : [[ 
						{field : "ID", title : "", checkbox : true}, 
						{field : "NAME", title : "名称", width : 80, align : "center"},
						{field : "PARENT_NAME", title : "上级组织机构 ", width : 80, align : "center"},
						{field : "NO", title : "排序 ", width : 80, align : "center"}
						]]
			});
		}
		
		//初始化组织机构树
		function initOrgTree(){
			orgTree.tree({
				idFiled : "ID",
				textFiled : "NAME",
				parentField : "PARENT_ID",
				iconClsFiled : "ICON",
				checkedFiled : "CHECKED",
				lines : true,
			    url : "org/treeList",
				<my:auth url="org/toMove">dnd : true,
				onStopDrag : toOrgMoveForMenu,</my:auth>
				onContextMenu : function(e, node){
					e.preventDefault();
					$(this).tree("select", node.target);
					$("#orgTreeMenu").menu("show", {
						left : e.pageX,
						top : e.pageY
					})
				},
				onSelect : function(node){
					curSelOrgId = node.ID;
					curSelOrgName = node.NAME;
					$("#org_one").val(curSelOrgId);
					orgGrid.datagrid("uncheckAll");
					orgGrid.datagrid("reload", $.fn.my.serializeObj(orgQueryForm));
				},
				onLoadSuccess : function(node, data){
					if(!curSelOrgId || !orgGrid.datagrid("options").url){//如果是第一次
						curSelOrgId = 1;
						orgGrid.datagrid("options").url = "org/list";
					}
					
					var node = orgTree.tree("find", curSelOrgId);
					if(!node){
						curSelOrgId = 1;
						node = orgTree.tree("find", curSelOrgId);
					}
					orgTree.tree("select", node.target);
				}
			});
		}
	
		//组织机构查询
		function orgQuery() {
			orgGrid.datagrid("uncheckAll");
			orgGrid.datagrid("load", $.fn.my.serializeObj(orgQueryForm));
		}
	
		//组织机构重置
		function orgReset() {
			orgQueryForm.form("reset");
			orgQuery();
		}
		
		//到达添加组织机构页面
		function toOrgAdd() {
			if(!curSelOrgId){
				parent.$.messager.alert("提示消息", "请选择上级组织机构！", "info");
				return;
			}
			
			var orgAddDialog;
			orgAddDialog = $("<div/>").dialog({
				title : "添加组织机构",
				href : "org/toAdd",
				buttons : [{
					text : "添加", 
					iconCls : "icon-add", 
					selected : true, 
					handler : function (){
						doOrgAdd(orgAddDialog);
					}
				}],
				onLoad : function() {
					$("#org_parentId").val(curSelOrgId);
					$("#org_parentName").val(curSelOrgName);
					$("#org_name").textbox({
						required : true,
						validType : ["length[1, 16]"]
					});
					$("#org_no").numberspinner({
						required : true,
						min : 1,
						max : 100
					});
				}
			});
		}
		
		//完成添加组织机构
		function doOrgAdd(orgAddDialog) {
			var orgEditFrom = $("#orgEditFrom");
			if(!orgEditFrom.form("validate")){
				return;
			}
			$.messager.confirm("确认消息", "确定要添加？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.progress();
				orgEditFrom.form("submit", {
					url : "org/doAdd",
					success : function(data) {
						orgTree.tree("reload");
						$.messager.progress("close"); 
						
						var obj = $.parseJSON(data);
						if(!obj.success){
							parent.$.messager.alert("提示消息", obj.message, "info");
							return;
						}
						
						orgAddDialog.dialog("close");
					}
				});
			})
		}
		
		//到达修改组织机构页面
		function toOrgEdit(id){
			var orgEditDialog;
			orgEditDialog = $("<div/>").dialog({
				title : "修改组织机构",
				href : "org/toEdit",
				queryParams : {"id" : id},
				buttons : [{
					text : "修改", 
					iconCls : "icon-edit", 
					selected : true, 
					handler : function (){
						doOrgEdit(orgEditDialog);
					}
				}],
				onLoad : function() {
					$("#org_name").textbox({
						required : true,
						validType : ["length[1, 16]"]
					});
					$("#org_no").numberspinner({
						required : true,
						min : 1,
						max : 100
					});
				}
			});
		}
		
		//到达修改组织机构页面
		function toOrgEditForBtn(){
			var orgGridRows = orgGrid.datagrid("getChecked");
			if(orgGridRows.length != 1){
				parent.$.messager.alert("提示消息", "请选择一行数据！", "info");
				return;
			}
			
			toOrgEdit(orgGridRows[0].ID);
		}
		
		//到达修改组织机构页面
		function toOrgEditForDblClick(rowIndex, rowData){
			orgGrid.datagrid("uncheckAll");
			orgGrid.datagrid("checkRow", rowIndex);
			toOrgEditForBtn();
		}
		
		//到达修改组织机构页面
		function toOrgEditForMenu() {
			toOrgEdit(curSelOrgId);
		}
		
		//完成修改组织机构
		function doOrgEdit(orgEditDialog) {
			var orgEditFrom = $("#orgEditFrom");
			if(!orgEditFrom.form("validate")){
				return;
			}
			$.messager.confirm("确认消息", "确定要修改？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.progress();
				orgEditFrom.form("submit", {
					url : "org/doEdit",
					success : function(data) {
						orgTree.tree("reload");
						$.messager.progress("close"); 
						
						var obj = $.parseJSON(data);
						if(!obj.success){
							parent.$.messager.alert("提示消息", obj.message, "info");
							return;
						}
						
						orgEditDialog.dialog("close");
					}
				});
			})
		}
		
		//完成删除组织机构
		function doOrgDel(params) {
			$.messager.confirm("确认消息", "确定要删除？", function(ok){
				if (!ok){
					return;
				}
				
				$.messager.progress();
				$.ajax({
					url : "org/doDel",
					data : params,
					success : function(obj){
						orgTree.tree("reload");
						$.messager.progress("close");
						
						if(!obj.success){
							parent.$.messager.alert("提示消息", obj.message, "info");
						}
					}
				});
			});
		}
		
		//完成删除组织机构
		function doOrgDelForBtn() {
			//校验数据有效性
			var orgGridRows = orgGrid.datagrid("getChecked");
			if(orgGridRows.length == 0){
				parent.$.messager.alert("提示消息", "请选择一行或多行数据！", "info");
				return;
			}
			
			//删除
			doOrgDel($.fn.my.serializeField(orgGridRows));
		}
		
		//完成删除组织机构
		function doOrgDelForMenu() {
			var params = {"ids" : curSelOrgId};
			var parentNode = orgTree.tree("find", curSelOrgId);
			curSelOrgId = parentNode.PARENT_ID;
			
			doOrgDel(params);
		}
		
		//到达移动组织机构页面
		function toOrgMoveForBtn(){
			var orgGridRows = orgGrid.datagrid("getChecked");
			if(orgGridRows.length != 1){
				parent.$.messager.alert("提示消息","请选择一行数据！", "info");
				return;
			}
			
			var orgDialog;
			orgDialog = $("<div/>").dialog({
				title : "选择组织机构",
				width : 300,
				height : 400,
				href : "org/toMove",
				buttons : [{
					text : "确定", 
					iconCls : "icon-ok", 
					selected : true,
					handler : function (){
						var orgMoveNode = $("#orgMoveTree").tree("getSelected");
						if(!orgMoveNode){
							parent.$.messager.alert("提示消息", "请选择一行数据！", "info");
							return;
						}
						
						var sourceId = orgGridRows[0].ID;
						var sourceName = orgGridRows[0].NAME;
						var targetId = orgMoveNode.ID;
						var targetName = orgMoveNode.NAME;
						if(sourceId == targetId){
							parent.$.messager.alert("提示消息", "源组织机构和目标组织机构一致！", "info");
							return;
						}
						if(orgMoveNode.PARENT_SUB.indexOf(orgGridRows[0].PARENT_SUB) >= 0){
							parent.$.messager.alert("提示消息", "父组织机构不能移动到子组织机构下！", "info");
							return;
						}
						
						doOrgMove(sourceId, sourceName, targetId, targetName, orgDialog);
					}
				}],
				onLoad : function(){
					var orgMoveTree = $("#orgMoveTree"); 
					orgMoveTree.tree({
						idFiled : "ID",
						textFiled : "NAME",
						parentField : "PARENT_ID",
						iconClsFiled : "ICON",
						checkedFiled : "CHECKED",
						lines : true,
					    url : "org/moveOrgTreeList",
					});
				}
			});
		}

		//到达移动组织机构页面
		function toOrgMoveForMenu(node){
			//校验数据有效性
			var targetNode = orgTree.tree("getParent", node.target);
			if(!targetNode){//拖动时有bug，有时会拖到空白地方。
				return;
			}
			var sourceId = node.ID;
			var sourceName = node.NAME;
			var sourceParentId = node.PARENT_ID;
			var targetId = targetNode.ID;
			var targetName = targetNode.NAME;
			if(sourceParentId == targetId){ //如果移动后的组织机构和源组织机构一样，不处理
				return;
			}
			
			//移动组织机构
			doOrgMove(sourceId, sourceName, targetId, targetName);
		}
		
		//完成移动组织机构
		function doOrgMove(sourceId, sourceName, targetId, targetName, dialog){
			$.messager.confirm("确认消息", "确定要移动【"+sourceName+"】到【"+targetName+"】？", function(ok){
				if (!ok){
					if(!dialog){
						orgTree.tree("reload");//拖动后有bug，可能会错位，所以刷新一次。
					}
					return;
				}
				
				var params = {"sourceId" : sourceId, "targetId" : targetId};
				$.messager.progress();
				$.ajax({
					url : "org/doMove",
					data : params,
					success : function(obj){
						orgTree.tree("reload");
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
		
		//刷新组织机构
		function orgTreeFlush(){
			orgTree.tree("reload");
		}
	</script>
</html>