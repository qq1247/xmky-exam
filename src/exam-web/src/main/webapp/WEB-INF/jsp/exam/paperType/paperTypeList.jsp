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
								<my:auth url="paperType/toAuth"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" onclick="toPaperTypeAuth();">设置权限</a></my:auth>
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
							{field : "PARENT_NAME", title : "上级试题分类 ", width : 80, align : "center"},
							{field : "NO", title : "排序 ", width : 80, align : "center"},
							{field : "USER_NAMES", title : "用户权限 ", width : 80, align : "center"},
							{field : "ORG_NAMES", title : "机构权限 ", width : 80, align : "center"},
							{field : "POST_NAMES", title : "岗位权限  ", width : 80, align : "center"}
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

		//到达权限页面
		function toPaperTypeAuth(){
			var paperTypeGridRows = $("#paperTypeGrid").datagrid("getChecked");
			if (paperTypeGridRows.length != 1) {
				parent.$.messager.alert("提示消息", "请选择一行数据！", "info");
				return;
			}
			
			var paperTypeAuthDialog;
			paperTypeAuthDialog = $("<div/>").dialog({
				title : "权限列表",
				href : "paperType/toAuth",
				queryParams : {id : paperTypeGridRows[0].ID},
				maximized : true,
				onLoad : function() {
					
				}
			});
		}
		
		//权限用户查询
		function paperTypeAuthUserQuery(){
			var paperTypeAuthUserGrid = $("#paperTypeAuthUserGrid");
			var paperTypeAuthUserQueryForm = $("#paperTypeAuthUserQueryForm");
			paperTypeAuthUserGrid.datagrid("uncheckAll");
			paperTypeAuthUserGrid.datagrid("load", $.fn.my.serializeObj(paperTypeAuthUserQueryForm));
		}
		
		//权限用户重置
		function paperTypeAuthUserReset(){
			var paperTypeAuthUserQueryForm = $("#paperTypeAuthUserQueryForm");
			paperTypeAuthUserQueryForm.form("reset");
			paperTypeAuthUserQuery();
		}
		
		//完成添加权限用户
		function doPaperTypeAuthUser(paperTypeAuthUserListDialog){
			var paperTypeAuthUserGridRows = $("#paperTypeAuthUserGrid").datagrid("getChecked");
			if (paperTypeAuthUserGridRows.length == 0) {
				parent.$.messager.alert("提示消息", "请选择一行或多行数据！", "info");
				return;
			}
			
			var params = $.fn.my.serializeField(paperTypeAuthUserGridRows, {attrName : "userIds"});
			params += "&id=" + $("#paperTypeAuthUser_ten").val();
			
			$.messager.confirm("确认消息", "确定要添加？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.progress();
				$.ajax({
					url : "paperType/doAuthUser",
					data : params,
					success : function(obj){
						$("#paperTypeAuthUserGrid").datagrid("reload");
						$.messager.progress("close");
						
						if(!obj.success){
							parent.$.messager.alert("提示消息", obj.message, "info");
							return;
						}
						paperTypeAuthUserListDialog.dialog("close");
					}
				});
			});
		}
		
		//到达添加权限用户页面
		function toPaperTypeAuthUserAddList(){
			var paperTypeAuthUserAddListDialog;
			paperTypeAuthUserAddListDialog = $("<div/>").dialog({
				title : "用户列表",
				href : "paperType/toAuthUserAddList",
				queryParams : {id : $("#paperTypeAuthUser_ten").val()},
				toolbar : "#paperTypeAuthUserAddToolbar",
				maximized : true,
				buttons : [{
					text : "添加",
					iconCls : "icon-add", 
					selected : true, 
					handler : function (){
						doPaperTypeAuthUserAdd(paperTypeAuthUserAddListDialog);
					}
				}],
				onLoad : function() {
					var paperTypeAuthUserAddGrid = $("#paperTypeAuthUserAddGrid"); //用户表格对象
					var paperTypeAuthUserAddQueryForm = $("#paperTypeAuthUserAddQueryForm"); //用户查询对象
					var paperTypeAuthUserAddOrgTree = $("#paperTypeAuthUserAddOrgTree"); //组织机构树对象
					var paperTypeAuthUserAddCurSelOrgId = ""; //当前选中的组织机构ID
					var paperTypeAuthUserAddCurSelOrgName = ""; //当前选中的组织机构名称
					
					paperTypeAuthUserAddGrid.datagrid({
						url : "",
						columns : [[ 
								{field : "ID", title : "", checkbox : true}, 
								{field : "USER_NAME", title : "姓名", width : 80, align : "center"}, 
								{field : "LOGIN_NAME", title : "登录名称", width : 80, align : "center"}, 
								{field : "ORG_NAME", title : "组织机构", width : 80, align : "center"},
								{field : "POST_NAMES", title : "岗位", width : 80, align : "center"},
								]]
					});
					
					paperTypeAuthUserAddOrgTree.tree({
						idFiled : "ID",
						textFiled : "NAME",
						parentField : "PARENT_ID",
						iconClsFiled : "ICON",
						checkedFiled : "CHECKED",
					    url : "paperType/authUserOrgTreeList",
						onSelect : function(node){
							paperTypeAuthUserAddCurSelOrgId = node.ID;
							paperTypeAuthUserAddCurSelOrgName = node.NAME;
							
							$("#paperTypeAuthUserAdd_one").val(paperTypeAuthUserAddCurSelOrgId);
							paperTypeAuthUserAddGrid.datagrid("uncheckAll");
							paperTypeAuthUserAddGrid.datagrid("reload", $.fn.my.serializeObj(paperTypeAuthUserAddQueryForm));
						},
						onLoadSuccess : function(node, data){
							if(!paperTypeAuthUserAddCurSelOrgId || !paperTypeAuthUserAddGrid.datagrid("options").url){//如果是第一次
								paperTypeAuthUserAddCurSelOrgId = 1;
								paperTypeAuthUserAddGrid.datagrid("options").url = "paperType/authUserAddList";
							}
							
							var node = paperTypeAuthUserAddOrgTree.tree("find", paperTypeAuthUserAddCurSelOrgId);
							if(!node){
								paperTypeAuthUserAddCurSelOrgId = 1;
								node = paperTypeAuthUserAddOrgTree.tree("find", paperTypeAuthUserAddCurSelOrgId);
							}
							paperTypeAuthUserAddOrgTree.tree("select", node.target);
						}
					});
				}
			});
		}
		
		//添加权限用户查询
		function paperTypeAuthUserAddQuery(){
			var paperTypeAuthUserAddGrid = $("#paperTypeAuthUserAddGrid");
			var paperTypeAuthUserAddQueryForm = $("#paperTypeAuthUserAddQueryForm");
			paperTypeAuthUserAddGrid.datagrid("uncheckAll");
			paperTypeAuthUserAddGrid.datagrid("load", $.fn.my.serializeObj(paperTypeAuthUserAddQueryForm));
		}
		
		//添加权限用户重置
		function paperTypeAuthUserAddReset(){
			var paperTypeAuthUserAddQueryForm = $("#paperTypeAuthUserAddQueryForm");
			paperTypeAuthUserAddQueryForm.form("reset");
			paperTypeAuthUserAddQuery();
		}
		
		//完成添加权限用户
		function doPaperTypeAuthUserAdd(paperTypeAuthUserAddListDialog){
			var paperTypeAuthUserAddGridRows = $("#paperTypeAuthUserAddGrid").datagrid("getChecked");
			if (paperTypeAuthUserAddGridRows.length == 0) {
				parent.$.messager.alert("提示消息", "请选择一行或多行数据！", "info");
				return;
			}
			
			var params = $.fn.my.serializeField(paperTypeAuthUserAddGridRows, {attrName : "userIds"});
			params += "&id=" + $("#paperTypeAuthUserAdd_ten").val();
			
			$.messager.confirm("确认消息", "确定要添加？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.confirm("确认消息", "同步到子【试卷分类】？", function(ok) {
					params += "&syn2Sub=" + ok;
					$.messager.progress();
					$.ajax({
						url : "paperType/doAuthUserAdd",
						data : params,
						success : function(obj){
							$("#paperTypeAuthUserGrid").datagrid("reload");
							paperTypeTree.tree("reload");
							$.messager.progress("close");
							
							if(!obj.success){
								parent.$.messager.alert("提示消息", obj.message, "info");
								return;
							}
							paperTypeAuthUserAddListDialog.dialog("close");
						}
					});
				});
			});
		}
		
		//完成删除权限用户
		function doPaperTypeAuthUserDel(){
			var paperTypeAuthUserGridRows = $("#paperTypeAuthUserGrid").datagrid("getChecked");
			if (paperTypeAuthUserGridRows.length == 0) {
				parent.$.messager.alert("提示消息", "请选择一行或多行数据！", "info");
				return;
			}
			
			var params = $.fn.my.serializeField(paperTypeAuthUserGridRows, {attrName : "userIds"});
			params += "&id=" + $("#paperTypeAuthUser_ten").val();
			$.messager.confirm("确认消息", "确定要删除？", function(ok) {
				if (!ok) {
					return;
				}
				$.messager.confirm("确认消息", "同步到子【试卷分类】？", function(ok) {
					params += "&syn2Sub=" + ok;
					$.messager.progress();
					$.ajax({
						url : "paperType/doAuthUserDel",
						data : params,
						success : function(obj){
							$("#paperTypeAuthUserGrid").datagrid("reload");
							paperTypeTree.tree("reload");
							$.messager.progress("close");
							
							if(!obj.success){
								parent.$.messager.alert("提示消息", obj.message, "info");
								return;
							}
						}
					});
				});
			});
		}
		
		//更新tabs
		function updateTabs(title){
			if(title == "用户权限"){
				updateUserTab();
			}else if(title == "机构权限"){
				updateOrgTab();
			}else if(title == "岗位权限"){
				updatePostTab();
			}
		}
		
		//更新用户标签
		function updateUserTab(){
			var paperTypeAuthUserGrid = $("#paperTypeAuthUserGrid"); //用户表格对象
			var paperTypeAuthUserQueryForm = $("#paperTypeAuthUserQueryForm"); //用户查询对象
			var paperTypeAuthUserOrgTree = $("#paperTypeAuthUserOrgTree"); //组织机构树对象
			var paperTypeAuthUserCurSelOrgId = ""; //当前选中的组织机构ID
			var paperTypeAuthUserCurSelOrgName = ""; //当前选中的组织机构名称
			
			paperTypeAuthUserGrid.datagrid({
				url : "",
				columns : [[ 
						{field : "ID", title : "", checkbox : true}, 
						{field : "USER_NAME", title : "姓名", width : 80, align : "center"}, 
						{field : "LOGIN_NAME", title : "登录名称", width : 80, align : "center"}, 
						{field : "ORG_NAME", title : "组织机构", width : 80, align : "center"},
						{field : "POST_NAMES", title : "岗位", width : 80, align : "center"},
						]]
			});
			
			paperTypeAuthUserOrgTree.tree({
				idFiled : "ID",
				textFiled : "NAME",
				parentField : "PARENT_ID",
				iconClsFiled : "ICON",
				checkedFiled : "CHECKED",
			    url : "paperType/authUserOrgTreeList",
				onSelect : function(node){
					paperTypeAuthUserCurSelOrgId = node.ID;
					paperTypeAuthUserCurSelOrgName = node.NAME;
					
					$("#paperTypeAuthUser_one").val(paperTypeAuthUserCurSelOrgId);
					paperTypeAuthUserGrid.datagrid("uncheckAll");
					paperTypeAuthUserGrid.datagrid("reload", $.fn.my.serializeObj(paperTypeAuthUserQueryForm));
				},
				onLoadSuccess : function(node, data){
					if(!paperTypeAuthUserCurSelOrgId || !paperTypeAuthUserGrid.datagrid("options").url){//如果是第一次
						paperTypeAuthUserCurSelOrgId = 1;
						paperTypeAuthUserGrid.datagrid("options").url = "paperType/authUserList";
					}
					
					var node = paperTypeAuthUserOrgTree.tree("find", paperTypeAuthUserCurSelOrgId);
					if(!node){
						paperTypeAuthUserCurSelOrgId = 1;
						node = paperTypeAuthUserOrgTree.tree("find", paperTypeAuthUserCurSelOrgId);
					}
					paperTypeAuthUserOrgTree.tree("select", node.target);
				}
			});
		}
		
		//更新机构标签
		function updateOrgTab(){
			var paperTypeAuthOrgOrgTree = $("#paperTypeAuthOrgOrgTree"); 
			paperTypeAuthOrgOrgTree.tree({
			    url : "paperType/authOrgOrgTreeList",
			    queryParams : {
			    	id : $("#paperTypeAuthUser_ten").val()
			    },
				idFiled : "ID",
				textFiled : "NAME",
				parentField : "PARENT_ID",
				iconClsFiled : "ICON",
				checkedFiled : "CHECKED",
				checkbox : true
			});
		}
		
		//更新岗位标签
		function updatePostTab(){
			var paperTypeAuthPostOrgTree = $("#paperTypeAuthPostOrgTree"); 
			paperTypeAuthPostOrgTree.tree({
			    url : "paperType/authPostOrgTreeList",
			    queryParams : {
			    	id : $("#paperTypeAuthUser_ten").val()
			    },
				idFiled : "ID",
				textFiled : "NAME",
				parentField : "PARENT_ID",
				iconClsFiled : "ICON",
				checkedFiled : "CHECKED",
				checkbox : true,
				loadFilter : function(data, parent) {
					var opt = $(this).data().tree.options;
					var idFiled = opt.idFiled || "id";
					var textFiled = opt.textFiled || "text";
					var checkedFiled = opt.checkedFiled || "checked";
					var iconClsFiled = opt.iconClsFiled || "iconCls";
					var parentField = opt.parentField;
					var list = data;
					var TreeList = [];
					var treeMap = {};

					for (var i = 0; i < list.length; i++) {
						list[i]["id"] = list[i][idFiled];
						if(list[i].TYPE == "POST"){
							list[i]["id"] = "p" + list[i][idFiled];
						}
						list[i]["text"] = list[i][textFiled];
						if(list[i][checkedFiled]){
							list[i]["checked"] = true;
						}
						list[i]["iconCls"] = list[i][iconClsFiled];
						treeMap[list[i]["id"]] = list[i];
						if(list[i].TYPE == "POST"){
							treeMap[list[i]["id"]] = "p" + list[i];
						}
					}

					for (var i = 0; i < list.length; i++) {
						if (treeMap[list[i][parentField]] && list[i]["id"] != list[i][parentField]) {
							if (!treeMap[list[i][parentField]]["children"]) {
								treeMap[list[i][parentField]]["children"] = [];
							}
							treeMap[list[i][parentField]]["children"].push(list[i]);
						} else {
							TreeList.push(list[i]);
						}
					}
					return TreeList;
				}
			});
		}
		
		//保存机构权限
		function doPaperTypeAuthOrgUpdate(){
			var orgTree = $("#paperTypeAuthOrgOrgTree");
			var orgNodes = orgTree.tree("getChecked", ["checked"]);
			var params = "&id=" + $("#paperTypeAuthUser_ten").val();
			if(orgNodes.length == 0){
				params += "&orgIds=";
			}else{
				for(index in orgNodes){
					params += "&orgIds=" + orgNodes[index].ID;
				}
			}
			
			$.messager.confirm("确认消息", "确定要保存？", function(ok){
				if (!ok){
					return;
				}

				$.messager.confirm("确认消息", "同步到子【试卷分类】？", function(ok) {
					params += "&syn2Sub=" + ok;
					
					$.messager.progress();
					$.ajax({
						url : "paperType/doAuthOrgUpdate",
						data : params,
						success : function(obj){
							orgTree.tree("reload");
							paperTypeTree.tree("reload");
							$.messager.progress("close");
							
							if(!obj.success){
								parent.$.messager.alert("提示消息", obj.message, "info");
								return;
							}
						}
					});
				});
			});
		}
		
		//保存岗位权限
		function doPaperTypeAuthPostUpdate(){
			var orgPostTree = $("#paperTypeAuthPostOrgTree");
			var postNodes = orgPostTree.tree("getChecked", ["checked"]);
			var params = "&id=" + $("#paperTypeAuthUser_ten").val();
			if(postNodes.length == 0){
				params += "&postIds=";
			}else{
				for(index in postNodes){
					params += "&postIds=" + postNodes[index].ID;
				}
			}
			
			$.messager.confirm("确认消息", "确定要保存？", function(ok){
				if (!ok){
					return;
				}

				$.messager.confirm("确认消息", "同步到子【试卷分类】？", function(ok) {
					params += "&syn2Sub=" + ok;
					
					$.messager.progress();
					$.ajax({
						url : "paperType/doAuthPostUpdate",
						data : params,
						success : function(obj){
							orgPostTree.tree("reload");
							paperTypeTree.tree("reload");
							$.messager.progress("close");
							
							if(!obj.success){
								parent.$.messager.alert("提示消息", obj.message, "info");
								return;
							}
						}
					});
				});
			});
		}
	</script>
</html>