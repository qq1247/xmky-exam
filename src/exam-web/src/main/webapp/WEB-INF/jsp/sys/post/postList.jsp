<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>岗位列表</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'west',border:false" style="width: 240px;padding:5px">
				<div class="easyui-panel" data-options="fit:true">
					<ul id="orgTree"></ul>
					<div id="postTreeMenu" class="easyui-menu" style="width:120px;">
						<my:auth url="post/toAdd"><div onclick="toPostAdd()" data-options="iconCls:'icon-add'">添加</div></my:auth>
						<div onclick="postTreeFlush()" data-options="iconCls:'icon-reload'">刷新</div>
					</div>
					<div id="postTreeMenu2" class="easyui-menu" style="width:120px;">
						<my:auth url="post/toEdit"><div onclick="toPostEditForMenu()" data-options="iconCls:'icon-edit'">修改</div></my:auth>
						<my:auth url="post/doDel"><div onclick="doPostDelForMenu()" data-options="iconCls:'icon-remove'">删除</div></my:auth>
					</div>
				</div>
			</div>
			<div data-options="region:'center',border:false">
				<div class="easyui-layout" data-options="fit:true">
					<div data-options="region:'center',border:false">
						<%-- 岗位查询条件 --%>
						<div id="postToolbar" style="padding:0 30px;">
							<div class="conditions">
								<form id="postQueryForm">
									<input type="hidden" id="post_one" name="one">
									<span class="con-span">名称：</span>
									<input name="two" class="easyui-textbox" style="width:166px;height:35px;line-height:35px;">
									<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" data-options="selected:true" onclick="postQuery();">查询</a>
									<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-clear" onclick="postReset();">重置</a>
								</form>
							</div>
							<div class="opt-buttons">
								<my:auth url="post/toAdd"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" data-options="selected:true" onclick="toPostAdd();">添加</a></my:auth>
								<my:auth url="post/toEdit"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" onclick="toPostEditForBtn();">修改</a></my:auth>
								<my:auth url="post/doDel"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" onclick="doPostDelForBtn();">删除</a></my:auth>
								<my:auth url="post/toResUpdate"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" onclick="toResUpdate();">设置权限</a></my:auth>
							</div>
						</div>
						<%-- 岗位数据表格 --%>
						<table id="postGrid">
						</table>
					</div>
				</div>
			</div>
		</div>
	</body>
	<script type="text/javascript">
		//定义变量
		var postGrid = $("#postGrid"); //岗位表格对象
		var postQueryForm = $("#postQueryForm"); //岗位查询对象
		var orgTree = $("#orgTree"); //组织机构树对象
		var curSelOrgId = ""; //当前选中的组织机构ID
		var curSelOrgName = ""; //当前选中的组织机构名称
		var curSelPostId = ""; //当前选中的岗位ID
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initPostGrid();
			initOrgTree();
		});
	
		//初始化岗位表格
		function initPostGrid() {
			postGrid.datagrid( {
				url : "",
				onDblClickRow : <my:auth url="post/toEdit">toPostEditForDblClick</my:auth>,
				toolbar : "#postToolbar",
				columns : [[ 
						{field : "ID", title : "", checkbox : true}, 
						{field : "NAME", title : "名称", width : 80, align : "center"}, 
						{field : "ORGNAME", title : "组织机构", width : 80, align : "center"}
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
			    url : "post/orgPostTreeList",
				onContextMenu : function(e, node){
					e.preventDefault();
					$(this).tree("select", node.target);
					if(node.TYPE == "ORG"){
						$("#postTreeMenu").menu("show", {
							left : e.pageX,
							top : e.pageY
						})
						return;
					}
					if(node.TYPE == "POST"){
						$("#postTreeMenu2").menu("show", {
							left : e.pageX,
							top : e.pageY
						})
						return;
					}
				},
				onSelect : function(node){
					if(node.TYPE == "ORG"){
						curSelPostId = "";
						curSelOrgId = node.ID;
						curSelOrgName = node.NAME;
					}else if(node.TYPE == "POST"){
						curSelPostId = node.ID;
						var parentNode = orgTree.tree("getParent", node.target);
						curSelOrgId = parentNode.ID;
						curSelOrgName = parentNode.NAME;
					}
					
					$("#post_one").val(curSelOrgId);
					postGrid.datagrid("uncheckAll");
					postGrid.datagrid("reload", $.fn.my.serializeObj(postQueryForm));
				},
				onLoadSuccess : function(node, data){
					if(!curSelOrgId || !postGrid.datagrid("options").url){//如果是第一次
						curSelOrgId = 1;
						postGrid.datagrid("options").url = "post/list";
					}
					
					var node = orgTree.tree("find", curSelOrgId);
					if(!node){
						curSelOrgId = 1;
						node = orgTree.tree("find", curSelOrgId);
					}
					orgTree.tree("select", node.target);
				},
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
		
		//岗位查询
		function postQuery() {
			postGrid.datagrid("uncheckAll");
			postGrid.datagrid("load", $.fn.my.serializeObj(postQueryForm));
		}
	
		//岗位重置
		function postReset() {
			postQueryForm.form("reset");
			postQuery();
		}
		
		//到达添加岗位页面
		function toPostAdd() {
			if(!curSelOrgId){
				parent.$.messager.alert("提示消息", "请选择组织机构！", "info");
				return;
			}
			
			var postAddDialog;
			postAddDialog = $("<div/>").dialog({
				href : "post/toAdd",
				title : "添加岗位",
				buttons : [{
					text : "添加", 
					iconCls : "icon-add", 
					selected : true, 
					handler : function (){
						doPostAdd(postAddDialog);
					}
				}],
				onLoad : function(){
					$("#post_orgId").val(curSelOrgId);
					$("#post_orgName").val(curSelOrgName);
					$("#post_name").textbox({
						required : true,
						validType : ["length[1, 16]"]
					});
				}
			});
		}
		
		//完成添加岗位
		function doPostAdd(postAddDialog) {
			var postEditFrom = $("#postEditFrom");
			if(!postEditFrom.form("validate")){
				return;
			}
			$.messager.confirm("确认消息", "确定要添加？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.progress();
				postEditFrom.form("submit", {
					url : "post/doAdd",
					success : function(data) {
						orgTree.tree("reload");
						$.messager.progress("close"); 
						
						var obj = $.parseJSON(data);
						if(!obj.success){
							parent.$.messager.alert("提示消息", obj.message, "info");
							return;
						}
						
						postAddDialog.dialog("close");
					}
				});
			})
		}
		
		//到达修改岗位页面
		function toPostEdit(id){
			var postEditDialog;
			postEditDialog = $("<div/>").dialog({
				title : "修改岗位",
				href : "post/toEdit",
				queryParams : {"id": id},
				buttons : [{
					text : "修改", 
					iconCls : "icon-edit", 
					selected : true, 
					handler : function (){
						doPostEdit(postEditDialog);
					}
				}],
				onLoad : function(){
					$("#post_name").textbox({
						required : true,
						validType : ["length[1, 16]"]
					});
				}
			});
		}
		
		//到达修改岗位页面
		function toPostEditForBtn(){
			var postGridRows = postGrid.datagrid("getChecked");
			if(postGridRows.length != 1){
				parent.$.messager.alert("提示消息", "请选择一行数据！", "info");
				return;
			}
			
			toPostEdit(postGridRows[0].ID);
		}
		
		//到达修改岗位页面
		function toPostEditForDblClick(rowIndex, rowData){
			postGrid.datagrid("uncheckAll");
			postGrid.datagrid("checkRow", rowIndex);
			toPostEditForBtn();
		}
		
		//到达修改岗位页面
		function toPostEditForMenu() {
			toPostEdit(curSelPostId);
		}
		
		//完成修改岗位
		function doPostEdit(postEditDialog) {
			var postEditFrom = $("#postEditFrom");
			if(!postEditFrom.form("validate")){
				return;
			}
			$.messager.confirm("确认消息", "确定要修改？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.progress();
				postEditFrom.form("submit", {
					url : "post/doEdit",
					success : function(data) {
						orgTree.tree("reload");
						$.messager.progress("close"); 
						
						var obj = $.parseJSON(data);
						if(!obj.success){
							parent.$.messager.alert("提示消息", obj.message, "info");
							return;
						}
						
						postEditDialog.dialog("close");
					}
				});
			})
		}
		
		//完成删除岗位
		function doPostDelete(params) {
			$.messager.confirm("确认消息", "确定要删除？", function(ok){
				if (!ok){
					return;
				}

				$.messager.progress();
				$.ajax({
					url : "post/doDel",
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
		
		//完成删除岗位
		function doPostDelForBtn() {
			//校验数据有效性
			var postGridRows = postGrid.datagrid("getChecked");
			if(postGridRows.length == 0){
				parent.$.messager.alert("提示消息", "请选择一行或多行数据！", "info");
				return;
			}

			//删除
			doPostDelete($.fn.my.serializeField(postGridRows));
		}
		
		//完成删除岗位
		function doPostDelForMenu() {
			var params = {"ids" : curSelPostId};
			doPostDelete(params);
		}
		
		//刷新岗位
		function postTreeFlush(){
			orgTree.tree("reload");
		}
		
		//到达设置权限页面
		function toResUpdate(){
			var postGridRows = postGrid.datagrid("getChecked");
			if(postGridRows.length != 1){
				parent.$.messager.alert("提示消息", "请选择一行数据！", "info");
				return;
			}
			
			var resUpdateDialog;
			resUpdateDialog = $("<div/>").dialog({
				title : "选择权限",
				width : 300,
				height : 400,
				href : "post/toResUpdate",
				buttons : [{
					text : "确定", 
					iconCls : "icon-ok", 
					selected : true,
					handler : function (){
						doResUpdate(resUpdateDialog);
					}
				}],
				onLoad : function(){
					var resUpdateTree = $("#resUpdateTree");
					resUpdateTree.tree({
						idFiled : "ID",
						textFiled : "NAME",
						parentField : "PARENT_ID",
						iconClsFiled : "ICON",
						checkedFiled : "CHECKED",
					    url : "post/resUpdateResTreeList",
					    queryParams : {id : postGridRows[0].ID},
						checkbox : true,
						onLoadSuccess : function(node, data){
							resUpdateTree.tree("collapseAll");
						}
					});
				}
			});
		}
		
		//完成设置权限
		function doResUpdate(resDialog) {
			var params = "";
			var postGridRows = postGrid.datagrid("getChecked");
			var id = postGridRows[0].ID;
			params += "id=" + id;
			
			var nodes = $("#resUpdateTree").tree("getChecked", ["checked", "indeterminate"]);
			if(nodes.length == 0){
				params += "&resIds=";
			}else{
				for(index in nodes){
					params += "&resIds=" + nodes[index].ID;
				}
			}
			
			$.messager.confirm("确认消息", "确定要设置？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.progress();
				$.ajax({
					url : "post/doResUpdate",
					data : params,
					success : function(obj){
						$.messager.progress("close");
						
						if(!obj.success){
							parent.$.messager.alert("提示消息", obj.message, "info");
							return;
						}
						
						resDialog.dialog("close");
					}
				});
			});
		}
	</script>
</html>