<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>用户列表</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
		<div class="easyui-layout" data-options="fit:true">
			<!-- 左侧组织机构菜单 -->
			<div data-options="region:'west',border:false" style="width: 240px;padding:5px">
				<div class="easyui-panel" data-options="fit:true">
					<ul id="orgTree"></ul>
					<div id="orgTreeMenu" class="easyui-menu" style="width:120px;">
						<my:auth url="user/toAdd"><div onclick="toUserAdd()" data-options="iconCls:'icon-add'">添加</div></my:auth>
						<div class="menu-sep"></div>
						<div onclick="orgTreeFlush()" data-options="iconCls:'icon-reload'">刷新</div>
					</div>
				</div>
			</div>
			<div data-options="region:'center',border:false">
				<div class="easyui-layout" data-options="fit:true">
					<div data-options="region:'center',border:false">
						<%-- 用户查询条件 --%>
						<div id="userToolbar" style="padding:0 30px;">
							<div class="conditions">
								<form id="userQueryForm">
									<input type="hidden" id="user_one" name="one">
									<span class="con-span">名称：</span>
									<input name="two" class="easyui-textbox" style="width:166px;height:35px;line-height:35px;">
									<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" data-options="selected:true" onclick="userQuery();">查询</a>
									<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-clear" onclick="userReset();">重置</a>
								</form>
							</div>
							<div class="opt-buttons">
								<my:auth url="user/toAdd"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" data-options="selected:true" onclick="toUserAdd();">添加</a></my:auth>
								<my:auth url="user/toEdit"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" onclick="toUserEditForBtn();">修改</a></my:auth>
								<my:auth url="user/doDel"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" onclick="doUserDelForBtn();">删除</a></my:auth>
								<my:auth url="user/initPwd"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" onclick="initPwd();">初始密码</a></my:auth>
								<my:auth url="user/toOrgUpdate"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" onclick="toOrgUpdate();">设置机构</a></my:auth>
								<my:auth url="user/toPostUpdate"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" onclick="toPostUpdate();">设置岗位</a></my:auth>
							</div>
						</div>
						<%-- 用户数据表格 --%>
						<table id="userGrid">
						</table>
					</div>
				</div>
			</div>
		</div>
	</body>
	<script type="text/javascript">
		//定义变量
		var userGrid = $("#userGrid"); //用户表格对象
		var userQueryForm = $("#userQueryForm"); //用户查询对象
		var orgTree = $("#orgTree"); //组织机构树对象
		var curSelOrgId = ""; //当前选中的组织机构ID
		var curSelOrgName = ""; //当前选中的组织机构名称
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initUserGrid();
			initOrgTree();
		});
	
		//初始化用户表格
		function initUserGrid() {
			userGrid.datagrid( {
				url : "",
				onDblClickRow : <my:auth url="user/toEdit">toUserEditForDblClick</my:auth>,
				toolbar : "#userToolbar",
				columns : [[ 
						{field : "ID", title : "", checkbox : true}, 
						{field : "NAME", title : "姓名", width : 80, align : "center"}, 
						{field : "LOGIN_NAME", title : "登录名称", width : 80, align : "center"}, 
						{field : "ORG_NAME", title : "组织机构", width : 80, align : "center"},
						{field : "POST_NAMES", title : "岗位", width : 80, align : "center"},
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
			    url : "user/orgTreeList",
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
					
					$("#user_one").val(curSelOrgId);
					userGrid.datagrid("uncheckAll");
					userGrid.datagrid("reload", $.fn.my.serializeObj(userQueryForm));
				},
				onLoadSuccess : function(node, data){
					if(!curSelOrgId || !userGrid.datagrid("options").url){//如果是第一次
						curSelOrgId = 1;
						userGrid.datagrid("options").url = "user/list";
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
		
		//用户查询
		function userQuery() {
			userGrid.datagrid("uncheckAll");
			userGrid.datagrid("load", $.fn.my.serializeObj(userQueryForm));
		}
	
		//用户重置
		function userReset() {
			userQueryForm.form("reset");
			userQuery();
		}
		
		//到达添加用户页面
		function toUserAdd() {
			if(!curSelOrgId){
				parent.$.messager.alert("提示消息", "请选择组织机构！", "info");
				return;
			}
			
			var userAddDialog;
			userAddDialog = $("<div/>").dialog({
				href : "user/toAdd",
				title : "添加用户",
				buttons : [{
					text : "添加", 
					iconCls : "icon-add", 
					selected : true, 
					handler : function (){
						doUserAdd(userAddDialog);
					}
				}],
				onLoad : function(){
					$("#user_orgId").val(curSelOrgId);
					$("#user_orgName").val(curSelOrgName);
					$("#user_name").textbox({
						required : true,
						validType : ["length[1, 16]"]
					});
					$("#user_loginName").textbox({
						required : true,
						validType : ["length[1, 16]", "zsx"]
					});
				}
			});
		}
		
		//完成添加用户
		function doUserAdd(userAddDialog) {
			var userEditFrom = $("#userEditFrom");
			if(!userEditFrom.form("validate")){
				return;
			}
			$.messager.confirm("确认消息", "确定要添加？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.progress();
				userEditFrom.form("submit", {
					url : "user/doAdd",
					success : function(data) {
						orgTree.tree("reload");
						$.messager.progress("close"); 
						
						var obj = $.parseJSON(data);
						if(!obj.success){
							parent.$.messager.alert("提示消息", obj.message, "info");
							return;
						}
						if(obj.data.initPwd){
							parent.$.messager.alert("提示消息", "初始密码：【" + obj.data.initPwd + "】", "info");
						}
						
						userAddDialog.dialog("close");
					}
				});
			})
		}
		
		//到达修改用户页面
		function toUserEdit(id){
			var userEditDialog;
			userEditDialog = $("<div/>").dialog({
				title : "修改用户",
				href : "user/toEdit",
				queryParams : {
					"id": id
				},
				buttons : [{
					text : "修改", 
					iconCls : "icon-edit", 
					selected : true, 
					handler : function (){
						doUserEdit(userEditDialog);
					}
				}],
				onLoad : function(){
					$("#user_name").textbox({
						required : true,
						validType : ["length[1, 16]"]
					});
					$("#user_loginName").textbox({
						required : true,
						validType : ["length[1, 16]", "zsx"]
					});
				}
			});
		}
		
		//到达修改用户页面
		function toUserEditForBtn(){
			var userGridRows = userGrid.datagrid("getChecked");
			if(userGridRows.length != 1){
				parent.$.messager.alert("提示消息", "请选择一行数据！", "info");
				return;
			}
			
			toUserEdit(userGridRows[0].ID);
		}
		
		//到达修改用户页面
		function toUserEditForDblClick(rowIndex, rowData){
			userGrid.datagrid("uncheckAll");
			userGrid.datagrid("checkRow", rowIndex);
			toUserEditForBtn();
		}
		
		//完成修改用户
		function doUserEdit(userEditDialog) {
			var userEditFrom = $("#userEditFrom");
			if(!userEditFrom.form("validate")){
				return;
			}
			$.messager.confirm("确认消息", "确定要修改？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.progress();
				userEditFrom.form("submit", {
					url : "user/doEdit",
					success : function(data) {
						orgTree.tree("reload");
						$.messager.progress("close"); 
						
						var obj = $.parseJSON(data);
						if(!obj.success){
							parent.$.messager.alert("提示消息", obj.message, "info");
							return;
						}
						if(obj.data.initPwd){
							parent.$.messager.alert("提示消息", "初始密码：【" + obj.data.initPwd + "】", "info");
						}
						
						userEditDialog.dialog("close");
					}
				});
			})
		}
		
		//完成删除用户
		function doUserDelete(params) {
			$.messager.confirm("确认消息", "确定要删除？", function(ok){
				if (!ok){
					return;
				}

				$.messager.progress();
				$.ajax({
					url : "user/doDel",
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
		
		//完成删除用户
		function doUserDelForBtn() {
			//校验数据有效性
			var userGridRows = userGrid.datagrid("getChecked");
			if(userGridRows.length == 0){
				parent.$.messager.alert("提示消息", "请选择一行或多行数据！", "info");
				return;
			}

			//删除
			doUserDelete($.fn.my.serializeField(userGridRows));
		}
		
		//完成初始化密码
		function initPwd() {
			//校验数据有效性
			var userGridRows = userGrid.datagrid("getChecked");
			if(userGridRows.length != 1){
				parent.$.messager.alert("提示消息", "请选择一行数据！", "info");
				return;
			}
			
			//初始化密码
			$.messager.confirm("确认消息", "确定要初始密码？", function(ok){
				if (!ok){
					return;
				}

				var params = {id : userGridRows[0].ID};
				$.messager.progress();
				$.ajax({
					url : "user/initPwd",
					data : params,
					success : function(obj){
						orgTree.tree("reload");
						$.messager.progress("close");
						
						if(!obj.success){
							parent.$.messager.alert("提示消息", obj.message, "info");
							return;
						}
						if(obj.data.initPwd){
							parent.$.messager.alert("提示消息", "初始密码：【" + obj.data.initPwd + "】", "info");
						}
					}
				});
			});
		}
		
		//刷新用户
		function orgTreeFlush(){
			orgTree.tree("reload");
		}
		
		//到达设置岗位页面
		function toPostUpdate(){
			var userGridRows = userGrid.datagrid("getChecked");
			if(userGridRows.length == 0){
				parent.$.messager.alert("提示消息", "请选择一行或多行数据！", "info");
				return;
			}
			
			var orgName = "";
			for(var i = 0; i < userGridRows.length; i++){
				if(i == 0){
					orgName = userGridRows[i].ORG_NAME
					continue;
				}
				if(orgName != userGridRows[i].ORG_NAME){
					parent.$.messager.alert("提示消息", "请选择同一个组织机构的用户！", "info");
					return;
				}
			}
			
			var postDialog;
			postDialog = $("<div/>").dialog({
				title : "选择岗位",
				width : 300,
				height : 400,
				href : "user/toPostUpdate",
				buttons : [{
					text : "确定", 
					iconCls : "icon-ok", 
					selected : true,
					handler : function (){
						doPostUpdate(postDialog);
					}
				}],
				onLoad : function(){
					var userGridRows = userGrid.datagrid("getChecked");
					var params = $.fn.my.serializeField(userGridRows);
					
					var postUpdateTree = $("#postUpdateTree"); 
					postUpdateTree.tree({
						idFiled : "ID",
						textFiled : "NAME",
						parentField : "PARENT_ID",
						iconClsFiled : "ICON",
						checkedFiled : "CHECKED",
						lines : true,
					    url : "user/postUpdatePostTreeList?" + params,
						checkbox : true,
						onSelect : function(node){
							if(!node.checked){
								$(this).tree("check", node.target);
							}else{
								$(this).tree("uncheck", node.target);
							}
						}
					});
				}
			});
		}
		
		//完成设置岗位
		function doPostUpdate(postDialog){
			var userGridRows = userGrid.datagrid("getChecked");
			var params = $.fn.my.serializeField(userGridRows);
			var postNodes = $("#postUpdateTree").tree("getChecked", ["checked", "indeterminate"]);
			if(postNodes.length == 0){
				params += "&postIds=";
			}else{
				for(index in postNodes){
					params += "&postIds=" + postNodes[index].ID;
				}
			}
			
			$.messager.confirm("确认消息", "确定要设置？", function(ok){
				if (!ok){
					return;
				}

				$.messager.progress();
				$.ajax({
					url : "user/doPostUpdate",
					data : params,
					success : function(obj){
						orgTree.tree("reload");
						postDialog.dialog("close");
						$.messager.progress("close");
						
						if(!obj.success){
							parent.$.messager.alert("提示消息", obj.message, "info");
							return;
						}
					}
				});
			});
		}
		
		//到达设置组织机构页面
		function toOrgUpdate(){
			var userGridRows = userGrid.datagrid("getChecked");
			if(userGridRows.length == 0){
				parent.$.messager.alert("提示消息","请选择一行或多行数据！", "info");
				return;
			}
			
			var orgDialog;
			orgDialog = $("<div/>").dialog({
				title : "选择组织机构",
				width : 300,
				height : 400,
				href : "user/toOrgUpdate",
				buttons : [{
					text : "确定", 
					iconCls : "icon-ok", 
					selected : true,
					handler : function (){
						doOrgUpdate(orgDialog);
					}
				}],
				onLoad : function(){
					var orgUpdateTree = $("#orgUpdateTree"); 
					orgUpdateTree.tree({
						idFiled : "ID",
						textFiled : "NAME",
						parentField : "PARENT_ID",
						iconClsFiled : "ICON",
						checkedFiled : "CHECKED",
						lines : true,
					    url : "user/orgUpdateOrgTreeList",
					});
				}
			});
		}
		
		//完成设置组织机构
		function doOrgUpdate(orgDialog){
			var orgNode = $("#orgUpdateTree").tree("getSelected");
			if(!orgNode){
				parent.$.messager.alert("提示消息", "请选择一行数据！", "info");
				return;
			}
			
			$.messager.confirm("确认消息", "确定要设置？", function(ok){
				if (!ok){
					return;
				}
				
				var userGridRows = userGrid.datagrid("getChecked");
				var params = $.fn.my.serializeField(userGridRows);
				params += ("&orgId=" + orgNode.ID);

				$.messager.progress();
				$.ajax({
					url : "user/doOrgUpdate",
					data : params,
					success : function(obj){
						orgTree.tree("reload");
						orgDialog.dialog("close");
						$.messager.progress("close");
						
						if(!obj.success){
							parent.$.messager.alert("提示消息", obj.message, "info");
							return;
						}
					}
				});
			});
		}
	</script>
</html>