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
								<my:auth url="questionType/toAuth"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" onclick="toQuestionTypeAuth();">设置权限</a></my:auth>
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
						{field : "NO", title : "排序 ", width : 80, align : "center"},
						{field : "USER_NAMES", title : "用户权限 ", width : 80, align : "center"},
						{field : "ORG_NAMES", title : "机构权限 ", width : 80, align : "center"},
						{field : "POST_NAMES", title : "岗位权限  ", width : 80, align : "center"}
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
						if(!obj.succ){
							parent.$.messager.alert("提示消息", obj.msg, "info");
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
						if(!obj.succ){
							parent.$.messager.alert("提示消息", obj.msg, "info");
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
						
						if(!obj.succ){
							parent.$.messager.alert("提示消息", obj.msg, "info");
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
						
						if(!obj.succ){
							parent.$.messager.alert("提示消息", obj.msg, "info");
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
		function toQuestionTypeAuth(){
			var questionTypeGridRows = $("#questionTypeGrid").datagrid("getChecked");
			if (questionTypeGridRows.length != 1) {
				parent.$.messager.alert("提示消息", "请选择一行数据！", "info");
				return;
			}
			
			var questionTypeAuthDialog;
			questionTypeAuthDialog = $("<div/>").dialog({
				title : "权限列表",
				href : "questionType/toAuth",
				queryParams : {id : questionTypeGridRows[0].ID},
				maximized : true,
				onLoad : function() {
					
				}
			});
		}
		
		//权限用户查询
		function questionTypeAuthUserQuery(){
			var questionTypeAuthUserGrid = $("#questionTypeAuthUserGrid");
			var questionTypeAuthUserQueryForm = $("#questionTypeAuthUserQueryForm");
			questionTypeAuthUserGrid.datagrid("uncheckAll");
			questionTypeAuthUserGrid.datagrid("load", $.fn.my.serializeObj(questionTypeAuthUserQueryForm));
		}
		
		//权限用户重置
		function questionTypeAuthUserReset(){
			var questionTypeAuthUserQueryForm = $("#questionTypeAuthUserQueryForm");
			questionTypeAuthUserQueryForm.form("reset");
			questionTypeAuthUserQuery();
		}
		
		//完成添加权限用户
		function doQuestionTypeAuthUser(questionTypeAuthUserListDialog){
			var questionTypeAuthUserGridRows = $("#questionTypeAuthUserGrid").datagrid("getChecked");
			if (questionTypeAuthUserGridRows.length == 0) {
				parent.$.messager.alert("提示消息", "请选择一行或多行数据！", "info");
				return;
			}
			
			var params = $.fn.my.serializeField(questionTypeAuthUserGridRows, {attrName : "userIds"});
			params += "&id=" + $("#questionTypeAuthUser_ten").val();
			
			$.messager.confirm("确认消息", "确定要添加？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.progress();
				$.ajax({
					url : "questionType/doAuthUser",
					data : params,
					success : function(obj){
						$("#questionTypeAuthUserGrid").datagrid("reload");
						$.messager.progress("close");
						
						if(!obj.succ){
							parent.$.messager.alert("提示消息", obj.msg, "info");
							return;
						}
						questionTypeAuthUserListDialog.dialog("close");
					}
				});
			});
		}
		
		//到达添加权限用户页面
		function toQuestionTypeAuthUserAddList(){
			var questionTypeAuthUserAddListDialog;
			questionTypeAuthUserAddListDialog = $("<div/>").dialog({
				title : "用户列表",
				href : "questionType/toAuthUserAddList",
				queryParams : {id : $("#questionTypeAuthUser_ten").val()},
				toolbar : "#questionTypeAuthUserAddToolbar",
				maximized : true,
				buttons : [{
					text : "添加",
					iconCls : "icon-add", 
					selected : true, 
					handler : function (){
						doQuestionTypeAuthUserAdd(questionTypeAuthUserAddListDialog);
					}
				}],
				onLoad : function() {
					var questionTypeAuthUserAddGrid = $("#questionTypeAuthUserAddGrid"); //用户表格对象
					var questionTypeAuthUserAddQueryForm = $("#questionTypeAuthUserAddQueryForm"); //用户查询对象
					var questionTypeAuthUserAddOrgTree = $("#questionTypeAuthUserAddOrgTree"); //组织机构树对象
					var questionTypeAuthUserAddCurSelOrgId = ""; //当前选中的组织机构ID
					var questionTypeAuthUserAddCurSelOrgName = ""; //当前选中的组织机构名称
					
					questionTypeAuthUserAddGrid.datagrid({
						url : "",
						columns : [[ 
								{field : "ID", title : "", checkbox : true}, 
								{field : "USER_NAME", title : "姓名", width : 80, align : "center"}, 
								{field : "LOGIN_NAME", title : "登录名称", width : 80, align : "center"}, 
								{field : "ORG_NAME", title : "组织机构", width : 80, align : "center"},
								{field : "POST_NAMES", title : "岗位", width : 80, align : "center"},
								]]
					});
					
					questionTypeAuthUserAddOrgTree.tree({
						idFiled : "ID",
						textFiled : "NAME",
						parentField : "PARENT_ID",
						iconClsFiled : "ICON",
						checkedFiled : "CHECKED",
					    url : "questionType/authUserOrgTreeList",
						onSelect : function(node){
							questionTypeAuthUserAddCurSelOrgId = node.ID;
							questionTypeAuthUserAddCurSelOrgName = node.NAME;
							
							$("#questionTypeAuthUserAdd_one").val(questionTypeAuthUserAddCurSelOrgId);
							questionTypeAuthUserAddGrid.datagrid("uncheckAll");
							questionTypeAuthUserAddGrid.datagrid("reload", $.fn.my.serializeObj(questionTypeAuthUserAddQueryForm));
						},
						onLoadSuccess : function(node, data){
							if(!questionTypeAuthUserAddCurSelOrgId || !questionTypeAuthUserAddGrid.datagrid("options").url){//如果是第一次
								questionTypeAuthUserAddCurSelOrgId = 1;
								questionTypeAuthUserAddGrid.datagrid("options").url = "questionType/authUserAddList";
							}
							
							var node = questionTypeAuthUserAddOrgTree.tree("find", questionTypeAuthUserAddCurSelOrgId);
							if(!node){
								questionTypeAuthUserAddCurSelOrgId = 1;
								node = questionTypeAuthUserAddOrgTree.tree("find", questionTypeAuthUserAddCurSelOrgId);
							}
							questionTypeAuthUserAddOrgTree.tree("select", node.target);
						}
					});
				}
			});
		}
		
		//添加权限用户查询
		function questionTypeAuthUserAddQuery(){
			var questionTypeAuthUserAddGrid = $("#questionTypeAuthUserAddGrid");
			var questionTypeAuthUserAddQueryForm = $("#questionTypeAuthUserAddQueryForm");
			questionTypeAuthUserAddGrid.datagrid("uncheckAll");
			questionTypeAuthUserAddGrid.datagrid("load", $.fn.my.serializeObj(questionTypeAuthUserAddQueryForm));
		}
		
		//添加权限用户重置
		function questionTypeAuthUserAddReset(){
			var questionTypeAuthUserAddQueryForm = $("#questionTypeAuthUserAddQueryForm");
			questionTypeAuthUserAddQueryForm.form("reset");
			questionTypeAuthUserAddQuery();
		}
		
		//完成添加权限用户
		function doQuestionTypeAuthUserAdd(questionTypeAuthUserAddListDialog){
			var questionTypeAuthUserAddGridRows = $("#questionTypeAuthUserAddGrid").datagrid("getChecked");
			if (questionTypeAuthUserAddGridRows.length == 0) {
				parent.$.messager.alert("提示消息", "请选择一行或多行数据！", "info");
				return;
			}
			
			var params = $.fn.my.serializeField(questionTypeAuthUserAddGridRows, {attrName : "userIds"});
			params += "&id=" + $("#questionTypeAuthUserAdd_ten").val();
			
			$.messager.confirm("确认消息", "确定要添加？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.confirm("确认消息", "同步到子【试题分类】？", function(ok) {
					params += "&syn2Sub=" + ok;
					$.messager.progress();
					$.ajax({
						url : "questionType/doAuthUserAdd",
						data : params,
						success : function(obj){
							$("#questionTypeAuthUserGrid").datagrid("reload");
							questionTypeTree.tree("reload");
							$.messager.progress("close");
							
							if(!obj.succ){
								parent.$.messager.alert("提示消息", obj.msg, "info");
								return;
							}
							questionTypeAuthUserAddListDialog.dialog("close");
						}
					});
				});
			});
		}
		
		//完成删除权限用户
		function doQuestionTypeAuthUserDel(){
			var questionTypeAuthUserGridRows = $("#questionTypeAuthUserGrid").datagrid("getChecked");
			if (questionTypeAuthUserGridRows.length == 0) {
				parent.$.messager.alert("提示消息", "请选择一行或多行数据！", "info");
				return;
			}
			
			var params = $.fn.my.serializeField(questionTypeAuthUserGridRows, {attrName : "userIds"});
			params += "&id=" + $("#questionTypeAuthUser_ten").val();
			$.messager.confirm("确认消息", "确定要删除？", function(ok) {
				if (!ok) {
					return;
				}
				$.messager.confirm("确认消息", "同步到子【试题分类】？", function(ok) {
					params += "&syn2Sub=" + ok;
					$.messager.progress();
					$.ajax({
						url : "questionType/doAuthUserDel",
						data : params,
						success : function(obj){
							$("#questionTypeAuthUserGrid").datagrid("reload");
							questionTypeTree.tree("reload");
							$.messager.progress("close");
							
							if(!obj.succ){
								parent.$.messager.alert("提示消息", obj.msg, "info");
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
			var questionTypeAuthUserGrid = $("#questionTypeAuthUserGrid"); //用户表格对象
			var questionTypeAuthUserQueryForm = $("#questionTypeAuthUserQueryForm"); //用户查询对象
			var questionTypeAuthUserOrgTree = $("#questionTypeAuthUserOrgTree"); //组织机构树对象
			var questionTypeAuthUserCurSelOrgId = ""; //当前选中的组织机构ID
			var questionTypeAuthUserCurSelOrgName = ""; //当前选中的组织机构名称
			
			questionTypeAuthUserGrid.datagrid({
				url : "",
				columns : [[ 
						{field : "ID", title : "", checkbox : true}, 
						{field : "USER_NAME", title : "姓名", width : 80, align : "center"}, 
						{field : "LOGIN_NAME", title : "登录名称", width : 80, align : "center"}, 
						{field : "ORG_NAME", title : "组织机构", width : 80, align : "center"},
						{field : "POST_NAMES", title : "岗位", width : 80, align : "center"},
						]]
			});
			
			questionTypeAuthUserOrgTree.tree({
				idFiled : "ID",
				textFiled : "NAME",
				parentField : "PARENT_ID",
				iconClsFiled : "ICON",
				checkedFiled : "CHECKED",
			    url : "questionType/authUserOrgTreeList",
				onSelect : function(node){
					questionTypeAuthUserCurSelOrgId = node.ID;
					questionTypeAuthUserCurSelOrgName = node.NAME;
					
					$("#questionTypeAuthUser_one").val(questionTypeAuthUserCurSelOrgId);
					questionTypeAuthUserGrid.datagrid("uncheckAll");
					questionTypeAuthUserGrid.datagrid("reload", $.fn.my.serializeObj(questionTypeAuthUserQueryForm));
				},
				onLoadSuccess : function(node, data){
					if(!questionTypeAuthUserCurSelOrgId || !questionTypeAuthUserGrid.datagrid("options").url){//如果是第一次
						questionTypeAuthUserCurSelOrgId = 1;
						questionTypeAuthUserGrid.datagrid("options").url = "questionType/authUserList";
					}
					
					var node = questionTypeAuthUserOrgTree.tree("find", questionTypeAuthUserCurSelOrgId);
					if(!node){
						questionTypeAuthUserCurSelOrgId = 1;
						node = questionTypeAuthUserOrgTree.tree("find", questionTypeAuthUserCurSelOrgId);
					}
					questionTypeAuthUserOrgTree.tree("select", node.target);
				}
			});
		}
		
		//更新机构标签
		function updateOrgTab(){
			var questionTypeAuthOrgOrgTree = $("#questionTypeAuthOrgOrgTree"); 
			questionTypeAuthOrgOrgTree.tree({
			    url : "questionType/authOrgOrgTreeList",
			    queryParams : {
			    	id : $("#questionTypeAuthUser_ten").val()
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
			var questionTypeAuthPostOrgTree = $("#questionTypeAuthPostOrgTree"); 
			questionTypeAuthPostOrgTree.tree({
			    url : "questionType/authPostOrgTreeList",
			    queryParams : {
			    	id : $("#questionTypeAuthUser_ten").val()
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
		function doQuestionTypeAuthOrgUpdate(){
			var orgTree = $("#questionTypeAuthOrgOrgTree");
			var orgNodes = orgTree.tree("getChecked", ["checked"]);
			var params = "&id=" + $("#questionTypeAuthUser_ten").val();
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

				$.messager.confirm("确认消息", "同步到子【试题分类】？", function(ok) {
					params += "&syn2Sub=" + ok;
					
					$.messager.progress();
					$.ajax({
						url : "questionType/doAuthOrgUpdate",
						data : params,
						success : function(obj){
							orgTree.tree("reload");
							questionTypeTree.tree("reload");
							$.messager.progress("close");
							
							if(!obj.succ){
								parent.$.messager.alert("提示消息", obj.msg, "info");
								return;
							}
						}
					});
				});
			});
		}
		
		//保存岗位权限
		function doQuestionTypeAuthPostUpdate(){
			var orgPostTree = $("#questionTypeAuthPostOrgTree");
			var postNodes = orgPostTree.tree("getChecked", ["checked"]);
			var params = "&id=" + $("#questionTypeAuthUser_ten").val();
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

				$.messager.confirm("确认消息", "同步到子【试题分类】？", function(ok) {
					params += "&syn2Sub=" + ok;
					
					$.messager.progress();
					$.ajax({
						url : "questionType/doAuthPostUpdate",
						data : params,
						success : function(obj){
							orgPostTree.tree("reload");
							questionTypeTree.tree("reload");
							$.messager.progress("close");
							
							if(!obj.succ){
								parent.$.messager.alert("提示消息", obj.msg, "info");
								return;
							}
						}
					});
				});
			});
		}
	</script>
</html>