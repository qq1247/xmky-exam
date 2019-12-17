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
								<my:auth url="examType/toAuth"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" onclick="toExamTypeAuth();">设置权限</a></my:auth>
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
							{field : "PARENT_NAME", title : "上级试题分类 ", width : 80, align : "center"},
							{field : "NO", title : "排序 ", width : 80, align : "center"},
							{field : "USER_NAMES", title : "用户权限 ", width : 80, align : "center"},
							{field : "ORG_NAMES", title : "机构权限 ", width : 80, align : "center"},
							{field : "POST_NAMES", title : "岗位权限  ", width : 80, align : "center"}
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
						if(!obj.succ){
							parent.$.messager.alert("提示消息", obj.msg, "info");
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
						if(!obj.succ){
							parent.$.messager.alert("提示消息", obj.msg, "info");
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
						
						if(!obj.succ){
							parent.$.messager.alert("提示消息", obj.msg, "info");
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
						
						if(!obj.succ){
							parent.$.messager.alert("提示消息", obj.msg, "info");
						}
					}
				});
			});
		}
		
		//刷新考试分类
		function examTypeTreeFlush(){
			examTypeTree.tree("reload");
		}

		//到达权限页面
		function toExamTypeAuth(){
			var examTypeGridRows = $("#examTypeGrid").datagrid("getChecked");
			if (examTypeGridRows.length != 1) {
				parent.$.messager.alert("提示消息", "请选择一行数据！", "info");
				return;
			}
			
			var examTypeAuthDialog;
			examTypeAuthDialog = $("<div/>").dialog({
				title : "权限列表",
				href : "examType/toAuth",
				queryParams : {id : examTypeGridRows[0].ID},
				maximized : true,
				onLoad : function() {
					
				}
			});
		}
		
		//权限用户查询
		function examTypeAuthUserQuery(){
			var examTypeAuthUserGrid = $("#examTypeAuthUserGrid");
			var examTypeAuthUserQueryForm = $("#examTypeAuthUserQueryForm");
			examTypeAuthUserGrid.datagrid("uncheckAll");
			examTypeAuthUserGrid.datagrid("load", $.fn.my.serializeObj(examTypeAuthUserQueryForm));
		}
		
		//权限用户重置
		function examTypeAuthUserReset(){
			var examTypeAuthUserQueryForm = $("#examTypeAuthUserQueryForm");
			examTypeAuthUserQueryForm.form("reset");
			examTypeAuthUserQuery();
		}
		
		//完成添加权限用户
		function doExamTypeAuthUser(examTypeAuthUserListDialog){
			var examTypeAuthUserGridRows = $("#examTypeAuthUserGrid").datagrid("getChecked");
			if (examTypeAuthUserGridRows.length == 0) {
				parent.$.messager.alert("提示消息", "请选择一行或多行数据！", "info");
				return;
			}
			
			var params = $.fn.my.serializeField(examTypeAuthUserGridRows, {attrName : "userIds"});
			params += "&id=" + $("#examTypeAuthUser_ten").val();
			
			$.messager.confirm("确认消息", "确定要添加？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.progress();
				$.ajax({
					url : "examType/doAuthUser",
					data : params,
					success : function(obj){
						$("#examTypeAuthUserGrid").datagrid("reload");
						$.messager.progress("close");
						
						if(!obj.succ){
							parent.$.messager.alert("提示消息", obj.msg, "info");
							return;
						}
						examTypeAuthUserListDialog.dialog("close");
					}
				});
			});
		}
		
		//到达添加权限用户页面
		function toExamTypeAuthUserAddList(){
			var examTypeAuthUserAddListDialog;
			examTypeAuthUserAddListDialog = $("<div/>").dialog({
				title : "用户列表",
				href : "examType/toAuthUserAddList",
				queryParams : {id : $("#examTypeAuthUser_ten").val()},
				toolbar : "#examTypeAuthUserAddToolbar",
				maximized : true,
				buttons : [{
					text : "添加",
					iconCls : "icon-add", 
					selected : true, 
					handler : function (){
						doExamTypeAuthUserAdd(examTypeAuthUserAddListDialog);
					}
				}],
				onLoad : function() {
					var examTypeAuthUserAddGrid = $("#examTypeAuthUserAddGrid"); //用户表格对象
					var examTypeAuthUserAddQueryForm = $("#examTypeAuthUserAddQueryForm"); //用户查询对象
					var examTypeAuthUserAddOrgTree = $("#examTypeAuthUserAddOrgTree"); //组织机构树对象
					var examTypeAuthUserAddCurSelOrgId = ""; //当前选中的组织机构ID
					var examTypeAuthUserAddCurSelOrgName = ""; //当前选中的组织机构名称
					
					examTypeAuthUserAddGrid.datagrid({
						url : "",
						columns : [[ 
								{field : "ID", title : "", checkbox : true}, 
								{field : "USER_NAME", title : "姓名", width : 80, align : "center"}, 
								{field : "LOGIN_NAME", title : "登录名称", width : 80, align : "center"}, 
								{field : "ORG_NAME", title : "组织机构", width : 80, align : "center"},
								{field : "POST_NAMES", title : "岗位", width : 80, align : "center"},
								]]
					});
					
					examTypeAuthUserAddOrgTree.tree({
						idFiled : "ID",
						textFiled : "NAME",
						parentField : "PARENT_ID",
						iconClsFiled : "ICON",
						checkedFiled : "CHECKED",
					    url : "examType/authUserOrgTreeList",
						onSelect : function(node){
							examTypeAuthUserAddCurSelOrgId = node.ID;
							examTypeAuthUserAddCurSelOrgName = node.NAME;
							
							$("#examTypeAuthUserAdd_one").val(examTypeAuthUserAddCurSelOrgId);
							examTypeAuthUserAddGrid.datagrid("uncheckAll");
							examTypeAuthUserAddGrid.datagrid("reload", $.fn.my.serializeObj(examTypeAuthUserAddQueryForm));
						},
						onLoadSuccess : function(node, data){
							if(!examTypeAuthUserAddCurSelOrgId || !examTypeAuthUserAddGrid.datagrid("options").url){//如果是第一次
								examTypeAuthUserAddCurSelOrgId = 1;
								examTypeAuthUserAddGrid.datagrid("options").url = "examType/authUserAddList";
							}
							
							var node = examTypeAuthUserAddOrgTree.tree("find", examTypeAuthUserAddCurSelOrgId);
							if(!node){
								examTypeAuthUserAddCurSelOrgId = 1;
								node = examTypeAuthUserAddOrgTree.tree("find", examTypeAuthUserAddCurSelOrgId);
							}
							examTypeAuthUserAddOrgTree.tree("select", node.target);
						}
					});
				}
			});
		}
		
		//添加权限用户查询
		function examTypeAuthUserAddQuery(){
			var examTypeAuthUserAddGrid = $("#examTypeAuthUserAddGrid");
			var examTypeAuthUserAddQueryForm = $("#examTypeAuthUserAddQueryForm");
			examTypeAuthUserAddGrid.datagrid("uncheckAll");
			examTypeAuthUserAddGrid.datagrid("load", $.fn.my.serializeObj(examTypeAuthUserAddQueryForm));
		}
		
		//添加权限用户重置
		function examTypeAuthUserAddReset(){
			var examTypeAuthUserAddQueryForm = $("#examTypeAuthUserAddQueryForm");
			examTypeAuthUserAddQueryForm.form("reset");
			examTypeAuthUserAddQuery();
		}
		
		//完成添加权限用户
		function doExamTypeAuthUserAdd(examTypeAuthUserAddListDialog){
			var examTypeAuthUserAddGridRows = $("#examTypeAuthUserAddGrid").datagrid("getChecked");
			if (examTypeAuthUserAddGridRows.length == 0) {
				parent.$.messager.alert("提示消息", "请选择一行或多行数据！", "info");
				return;
			}
			
			var params = $.fn.my.serializeField(examTypeAuthUserAddGridRows, {attrName : "userIds"});
			params += "&id=" + $("#examTypeAuthUserAdd_ten").val();
			
			$.messager.confirm("确认消息", "确定要添加？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.confirm("确认消息", "同步到子【考试分类】？", function(ok) {
					params += "&syn2Sub=" + ok;
					$.messager.progress();
					$.ajax({
						url : "examType/doAuthUserAdd",
						data : params,
						success : function(obj){
							$("#examTypeAuthUserGrid").datagrid("reload");
							examTypeTree.tree("reload");
							$.messager.progress("close");
							
							if(!obj.succ){
								parent.$.messager.alert("提示消息", obj.msg, "info");
								return;
							}
							examTypeAuthUserAddListDialog.dialog("close");
						}
					});
				});
			});
		}
		
		//完成删除权限用户
		function doExamTypeAuthUserDel(){
			var examTypeAuthUserGridRows = $("#examTypeAuthUserGrid").datagrid("getChecked");
			if (examTypeAuthUserGridRows.length == 0) {
				parent.$.messager.alert("提示消息", "请选择一行或多行数据！", "info");
				return;
			}
			
			var params = $.fn.my.serializeField(examTypeAuthUserGridRows, {attrName : "userIds"});
			params += "&id=" + $("#examTypeAuthUser_ten").val();
			$.messager.confirm("确认消息", "确定要删除？", function(ok) {
				if (!ok) {
					return;
				}
				$.messager.confirm("确认消息", "同步到子【考试分类】？", function(ok) {
					params += "&syn2Sub=" + ok;
					$.messager.progress();
					$.ajax({
						url : "examType/doAuthUserDel",
						data : params,
						success : function(obj){
							$("#examTypeAuthUserGrid").datagrid("reload");
							examTypeTree.tree("reload");
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
			var examTypeAuthUserGrid = $("#examTypeAuthUserGrid"); //用户表格对象
			var examTypeAuthUserQueryForm = $("#examTypeAuthUserQueryForm"); //用户查询对象
			var examTypeAuthUserOrgTree = $("#examTypeAuthUserOrgTree"); //组织机构树对象
			var examTypeAuthUserCurSelOrgId = ""; //当前选中的组织机构ID
			var examTypeAuthUserCurSelOrgName = ""; //当前选中的组织机构名称
			
			examTypeAuthUserGrid.datagrid({
				url : "",
				columns : [[ 
						{field : "ID", title : "", checkbox : true}, 
						{field : "USER_NAME", title : "姓名", width : 80, align : "center"}, 
						{field : "LOGIN_NAME", title : "登录名称", width : 80, align : "center"}, 
						{field : "ORG_NAME", title : "组织机构", width : 80, align : "center"},
						{field : "POST_NAMES", title : "岗位", width : 80, align : "center"},
						]]
			});
			
			examTypeAuthUserOrgTree.tree({
				idFiled : "ID",
				textFiled : "NAME",
				parentField : "PARENT_ID",
				iconClsFiled : "ICON",
				checkedFiled : "CHECKED",
			    url : "examType/authUserOrgTreeList",
				onSelect : function(node){
					examTypeAuthUserCurSelOrgId = node.ID;
					examTypeAuthUserCurSelOrgName = node.NAME;
					
					$("#examTypeAuthUser_one").val(examTypeAuthUserCurSelOrgId);
					examTypeAuthUserGrid.datagrid("uncheckAll");
					examTypeAuthUserGrid.datagrid("reload", $.fn.my.serializeObj(examTypeAuthUserQueryForm));
				},
				onLoadSuccess : function(node, data){
					if(!examTypeAuthUserCurSelOrgId || !examTypeAuthUserGrid.datagrid("options").url){//如果是第一次
						examTypeAuthUserCurSelOrgId = 1;
						examTypeAuthUserGrid.datagrid("options").url = "examType/authUserList";
					}
					
					var node = examTypeAuthUserOrgTree.tree("find", examTypeAuthUserCurSelOrgId);
					if(!node){
						examTypeAuthUserCurSelOrgId = 1;
						node = examTypeAuthUserOrgTree.tree("find", examTypeAuthUserCurSelOrgId);
					}
					examTypeAuthUserOrgTree.tree("select", node.target);
				}
			});
		}
		
		//更新机构标签
		function updateOrgTab(){
			var examTypeAuthOrgOrgTree = $("#examTypeAuthOrgOrgTree"); 
			examTypeAuthOrgOrgTree.tree({
			    url : "examType/authOrgOrgTreeList",
			    queryParams : {
			    	id : $("#examTypeAuthUser_ten").val()
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
			var examTypeAuthPostOrgTree = $("#examTypeAuthPostOrgTree"); 
			examTypeAuthPostOrgTree.tree({
			    url : "examType/authPostOrgTreeList",
			    queryParams : {
			    	id : $("#examTypeAuthUser_ten").val()
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
		function doExamTypeAuthOrgUpdate(){
			var orgTree = $("#examTypeAuthOrgOrgTree");
			var orgNodes = orgTree.tree("getChecked", ["checked"]);
			var params = "&id=" + $("#examTypeAuthUser_ten").val();
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

				$.messager.confirm("确认消息", "同步到子【考试分类】？", function(ok) {
					params += "&syn2Sub=" + ok;
					
					$.messager.progress();
					$.ajax({
						url : "examType/doAuthOrgUpdate",
						data : params,
						success : function(obj){
							orgTree.tree("reload");
							examTypeTree.tree("reload");
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
		function doExamTypeAuthPostUpdate(){
			var orgPostTree = $("#examTypeAuthPostOrgTree");
			var postNodes = orgPostTree.tree("getChecked", ["checked"]);
			var params = "&id=" + $("#examTypeAuthUser_ten").val();
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

				$.messager.confirm("确认消息", "同步到子【考试分类】？", function(ok) {
					params += "&syn2Sub=" + ok;
					
					$.messager.progress();
					$.ajax({
						url : "examType/doAuthPostUpdate",
						data : params,
						success : function(obj){
							orgPostTree.tree("reload");
							examTypeTree.tree("reload");
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