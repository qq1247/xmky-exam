<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>考试列表</title>
		<%@include file="/script/myJs/common.jspf"%>
		<script type="text/javascript" src="script/ckeditor/ckeditor.js"></script>
	</head>
	<body>
		<div class="easyui-layout" data-options="fit:true">
			<!-- 左侧试卷分类菜单 -->
			<div data-options="region:'west',border:false" style="width: 240px;padding:5px">
				<div class="easyui-panel" data-options="fit:true">
					<ul id="examTypeTree"></ul>
					<div id="examTypeTreeMenu" class="easyui-menu" style="width:120px;">
						<my:auth url="exam/toAdd"><div onclick="toExamAdd()" data-options="iconCls:'icon-add'">添加</div></my:auth>
						<div class="menu-sep"></div>
						<div onclick="examTypeTreeFlush()" data-options="iconCls:'icon-reload'">刷新</div>
					</div>
				</div>
			</div>
			<div data-options="region:'center',border:false">
				<%-- 考试查询条件 --%>
				<div id="examToolbar" style="padding:0 30px;">
					<div class="conditions">
						<form id="examQueryForm">
							<input type="hidden" id="exam_one" name="one">
							<span class="con-span">名称：</span>
							<input name="two" class="easyui-textbox" style="width:166px;height:35px;line-height:35px;">
							<span class="con-span">试卷：</span>
							<input name="three" class="easyui-textbox" style="width:166px;height:35px;line-height:35px;">
							<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" data-options="selected:true" onclick="examQuery();">查询</a>
							<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-clear" onclick="examReset();">重置</a>
						</form>
					</div>
					<div class="opt-buttons">
						<my:auth url="exam/toAdd"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" data-options="selected:true" onclick="toExamAdd();">添加</a></my:auth>
						<my:auth url="exam/toEdit"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" onclick="toExamEditForBtn();">修改</a></my:auth>
						<my:auth url="exam/doDel"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" onclick="doExamDelForBtn();">删除</a></my:auth>
						<my:auth url="exam/toExamUserList"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" onclick="toExamUserList();">考试用户</a></my:auth>
						<my:auth url="exam/toMarkUserList"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" onclick="toMarkUserList();">判卷用户</a></my:auth>
					</div>
				</div>
				<%-- 考试数据表格 --%>
				<table id="examGrid">
				</table>
			</div>
		</div>
	</body>
	<script type="text/javascript">
		//定义变量
		var examGrid = $("#examGrid"); //考试表格对象
		var examQueryForm = $("#examQueryForm"); //考试查询对象
		var examTypeTree = $("#examTypeTree"); //考试查询对象
		var curSelExamTypeId = ""; //当前选中的考试分类ID
		var curSelExamTypeName = ""; //当前选中的考试分类名称
		var toolbar = [
		                //加粗     斜体，     下划线      穿过线      下标字        上标字
		                ["Bold","Italic","Underline","Strike","Subscript","Superscript"],
		                // 数字列表          实体列表            减小缩进    增大缩进
		                ["NumberedList","BulletedList","-","Outdent","Indent"],
		                //左对 齐             居中对齐          右对齐          两端对齐
		                ["JustifyLeft","JustifyCenter","JustifyRight","JustifyBlock"],
		                //超链接  取消超链接 锚点
		                ["Link","Unlink","Anchor"],
		                //图片    flash    表格       水平线            表情       特殊字符        分页符
		                ["Image","Flash","Table","HorizontalRule","Smiley","SpecialChar","PageBreak"],
		                // 样式       格式      字体    字体大小
		                ["Styles","Format","Font","FontSize"],
		                //文本颜色     背景颜色
		                ["TextColor","BGColor"]];
	
		//页面加载完毕，执行如下方法：
		$(function() {
			initExamGrid();
			initExamTypeTree();
		});
	
		//初始化考试表格
		function initExamGrid() {
			examGrid.datagrid( {
				url : "",
				onDblClickRow : <my:auth url="exam/toEdit">toExamEditForDblClick</my:auth>,
				toolbar : "#examToolbar",
				columns : [[ 
						{field : "ID", title : "", checkbox : true}, 
						{field : "NAME", title : "名称", width : 80, align : "center"},
						{field : "PAPER_NAME", title : "试卷", width : 80, align : "center"},
						{field : "PASS_SCORE", title : "及格分数", width : 80, align : "center", 
							formatter : function(value, row, index){
								return row.PASS_SCORE + "/" + row.PAPER_TOTLE_SCORE;
							}
						},
						{field : "START_TIME_STR", title : "开始时间", width : 80, align : "center"},
						{field : "END_TIME_STR", title : "结束时间", width : 80, align : "center"},
						{field : "STATE_NAME", title : "状态", width : 80, align : "center"}
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
			    url : "exam/examTypeTreeList",
				dnd : true,
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
					
					$("#exam_one").val(curSelExamTypeId);
					examGrid.datagrid("uncheckAll");
					examGrid.datagrid("reload", $.fn.my.serializeObj(examQueryForm));
				},
				onLoadSuccess : function(node, data){
					if(!curSelExamTypeId || !examGrid.datagrid("options").url){//如果是第一次
						curSelExamTypeId = 1;
						examGrid.datagrid("options").url = "exam/list";
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
	
		//考试查询
		function examQuery() {
			examGrid.datagrid("uncheckAll");
			examGrid.datagrid("load", $.fn.my.serializeObj(examQueryForm));
		}
	
		//考试重置
		function examReset() {
			examQueryForm.form("reset");
			examQuery();
		}
		
		//到达添加考试页面
		function toExamAdd() {
			if(!curSelExamTypeId){
				parent.$.messager.alert("提示消息", "请选择考试分类！", "info");
				return;
			}
			
			var examAddDialog;
			examAddDialog = $("<div/>").dialog({
				title : "添加考试",
				href : "exam/toAdd",
				buttons : [{
					text : "添加",
					iconCls : "icon-add",
					selected : true,
					handler : function(){
						doExamAdd(examAddDialog);
					}
				}],
				onLoad : function() {
					$("#exam_examTypeId").val(curSelExamTypeId);
					$("#exam_examTypeName").val(curSelExamTypeName);
					
					$("#exam_name").textbox({
						required : true,
						validType : ["length[1, 16]"]
					});
					$("#exam_state").combobox({
						required : true,
						editable : false
					});
					$("#exam_paperId").textbox({
						required : true,
						editable : false,
						icons : [{
							iconCls : "icon-add",
							handler : function(e){
								toPaperAddList();
							}
						}]
					});
					$("#exam_passScore").numberspinner({
						required : true,
						min : 0,
						max : 1000,
						precision : 2
					});
					$("#exam_startTime").datetimebox({
						required : true,
						editable : false
					});
					$("#exam_endTime").datetimebox({
						required : true,
						editable : false
					});
					CKEDITOR.replace("exam_description", {
						height: "80px",
			        	toolbar : toolbar
					});
				}
			});
		}

		//完成添加考试
		function doExamAdd(examAddDialog) {
			var examEditFrom = $("#examEditFrom");
			if(!examEditFrom.form("validate")){
				return;
			}
			
			$.messager.confirm("提示消息", "确定要添加？", function(ok){
				if (!ok){
					return;
				}
				
				examEditFrom.form("submit", {
					url : "exam/doAdd",
					success : function(data) {
						examGrid.datagrid("reload");
						$.messager.progress("close");

						var obj = $.parseJSON(data);
						if (!obj.success) {
							parent.$.messager.alert("提示消息", obj.message, "info");
							return;
						}

						examAddDialog.dialog("close");
					}
				});
			});
		}

		//到达修改考试页面
		function toExamEdit(id) {
			var examEditDialog;
			examEditDialog = $("<div/>").dialog({
				title : "修改考试",
				href : "exam/toEdit",
				queryParams : {"id" : id},
				buttons : [ {
					text : "修改",
					iconCls : "icon-edit",
					selected : true,
					handler : function() {
						doExamEdit(examEditDialog);
					}
				} ],
				onLoad : function() {
					$("#exam_name").textbox({
						required : true,
						validType : ["length[1, 16]"]
					});
					$("#exam_state").combobox({
						required : true,
						editable : false
					});
					
					$("#exam_paperId").textbox({
						required : true,
						editable : false,
						icons : [{
							iconCls : "icon-add",
							handler : function(e){
								toPaperAddList();
							}
						}]
					});
					$("#exam_paperId").textbox("setText", $("#exam_paperName").val());
					$("#exam_passScore").numberspinner({
						required : true,
						min : 0,
						max : 1000,
						precision : 2
					});
					$("#exam_startTime").datetimebox({
						required : true,
						editable : false
					});
					$("#exam_endTime").datetimebox({
						required : true,
						editable : false
					});
					CKEDITOR.replace("exam_description", {
						height: "80px",
			        	toolbar : toolbar
					});
				}
			});
		}

		//到达修改考试页面
		function toExamEditForBtn() {
			var examGridRows = examGrid.datagrid("getChecked");
			if (examGridRows.length != 1) {
				parent.$.messager.alert("提示消息", "请选择一行数据！", "info");
				return;
			}

			toExamEdit(examGridRows[0].ID);
		}

		//到达修改考试页面
		function toExamEditForDblClick(rowIndex, rowData) {
			examGrid.datagrid("uncheckAll");
			examGrid.datagrid("checkRow", rowIndex);
			toExamEditForBtn();
		}

		//完成修改考试
		function doExamEdit(examEditDialog) {
			var examEditFrom = $("#examEditFrom");
			if(!examEditFrom.form("validate")){
				return;
			}
			$.messager.confirm("确认消息", "确定要修改？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.progress();
				examEditFrom.form("submit", {
					url : "exam/doEdit",
					success : function(data) {
						examGrid.datagrid("reload");
						$.messager.progress("close");
	
						var obj = $.parseJSON(data);
						if (!obj.success) {
							parent.$.messager.alert("提示消息", obj.message, "info");
							return;
						}
	
						examEditDialog.dialog("close");
					}
				});
			})
		}

		//完成删除考试
		function doExamDel(params) {
			$.messager.confirm("确认消息", "确定要删除？", function(ok) {
				if (!ok) {
					return;
				}

				$.messager.progress();
				$.ajax({
					url : "exam/doDel",
					data : params,
					success : function(obj) {
						examGrid.datagrid("reload");
						$.messager.progress("close");
						
						if (!obj.success) {
							parent.$.messager.alert("提示消息", obj.message, "info");
						}
					}
				});
			});
		}

		//完成删除考试
		function doExamDelForBtn() {
			//校验数据有效性
			var examGridRows = examGrid.datagrid("getChecked");
			if (examGridRows.length == 0) {
				parent.$.messager.alert("提示消息", "请选择一行或多行数据！", "info");
				return;
			}

			//删除
			doExamDel($.fn.my.serializeField(examGridRows));
		}
		
		//到达添加试卷页面
		function toPaperAddList(){
			var paperAddListDialog;
			paperAddListDialog = $("<div/>").dialog({
				title : "试卷列表",
				href : "exam/toPaperAddList",
				maximized : true,
				buttons : [{
					text : "添加",
					iconCls : "icon-add", 
					selected : true, 
					handler : function (){
						doPaperAdd(paperAddListDialog);
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
		
		//添加试卷查询
		function paperAddListQuery(){
			var paperAddListQueryForm = $("#paperAddListQueryForm");
			var paperAddListGrid = $("#paperAddListGrid");
			paperAddListGrid.datagrid("uncheckAll");
			paperAddListGrid.datagrid("load", $.fn.my.serializeObj(paperAddListQueryForm));
		}
	
		//添加试卷重置
		function paperAddListReset() {
			var paperAddListQueryForm = $("#paperAddListQueryForm");
			paperAddListQueryForm.form("reset");
			paperAddListQuery();
		}
		
		//完成添加试卷
		function doPaperAdd(paperAddListDialog){
			var paperGridRows = $("#paperAddListGrid").datagrid("getChecked");
			if (paperGridRows.length != 1) {
				parent.$.messager.alert("提示消息", "请选择一行数据！", "info");
				return;
			}
			
			$("#exam_paperId").textbox("setValue", paperGridRows[0].ID);
			$("#exam_paperId").textbox("setText", paperGridRows[0].NAME);
			paperAddListDialog.dialog("close");
		}
		
		//到达考试用户列表页面
		function toExamUserList(){
			var examGridRows = $("#examGrid").datagrid("getChecked");
			if (examGridRows.length != 1) {
				parent.$.messager.alert("提示消息", "请选择一行数据！", "info");
				return;
			}
			
			var examUserListDialog;
			examUserListDialog = $("<div/>").dialog({
				title : "考试用户列表",
				href : "exam/toExamUserList",
				queryParams : {id : examGridRows[0].ID},
				maximized : true,
				onLoad : function() {
					var examUserGrid = $("#examUserGrid");
					var examUserQueryForm = $("#examUserQueryForm");
					var examUserOrgTree = $("#examUserOrgTree");
					var examUserCurSelOrgId = "";
					var examUserCurSelOrgName = "";
					
					examUserGrid.datagrid({
						url : "",
						toolbar : "#examUserToolbar",
						columns : [[ 
								{field : "ID", title : "", checkbox : true}, 
								{field : "USER_NAME", title : "姓名", width : 80, align : "center"}, 
								{field : "LOGIN_NAME", title : "登录名称", width : 80, align : "center"}, 
								{field : "ORG_NAME", title : "组织机构", width : 80, align : "center"},
								{field : "POST_NAMES", title : "岗位", width : 80, align : "center"},
								]]
					});
					examUserOrgTree.tree({
						idFiled : "ID",
						textFiled : "NAME",
						parentField : "PARENT_ID",
						iconClsFiled : "ICON",
						checkedFiled : "CHECKED",
						lines : true,
					    url : "exam/examUserOrgTreeList",
						onSelect : function(node){
							examUserCurSelOrgId = node.ID;
							examUserCurSelOrgName = node.NAME;
							
							$("#examUser_one").val(examUserCurSelOrgId);
							examUserGrid.datagrid("uncheckAll");
							examUserGrid.datagrid("reload", $.fn.my.serializeObj(examUserQueryForm));
						},
						onLoadSuccess : function(node, data){
							if(!examUserCurSelOrgId || !examUserGrid.datagrid("options").url){//如果是第一次
								examUserCurSelOrgId = 1;
								examUserGrid.datagrid("options").url = "exam/examUserList";
							}
							
							var node = examUserOrgTree.tree("find", examUserCurSelOrgId);
							if(!node){
								examUserCurSelOrgId = 1;
								node = orgTree.tree("find", examUserCurSelOrgId);
							}
							examUserOrgTree.tree("select", node.target);
						}
					});
				}
			});
		}
		
		//考试用户查询
		function examUserQuery(){
			var examUserGrid = $("#examUserGrid");
			var examUserQueryForm = $("#examUserQueryForm");
			examUserGrid.datagrid("uncheckAll");
			examUserGrid.datagrid("load", $.fn.my.serializeObj(examUserQueryForm));
		}
		
		//考试用户重置
		function examUserReset(){
			var examUserQueryForm = $("#examUserQueryForm");
			examUserQueryForm.form("reset");
			examUserQuery();
		}
		
		//到达添加考试用户页面
		function toExamUserAddList(){
			var examUserAddListDialog;
			examUserAddListDialog = $("<div/>").dialog({
				title : "用户列表",
				href : "exam/toExamUserAddList",
				queryParams : {id : $("#examUser_ten").val()},
				toolbar : "#examUserAddToolbar",
				maximized : true,
				buttons : [{
					text : "添加",
					iconCls : "icon-add", 
					selected : true, 
					handler : function (){
						doExamUserAdd(examUserAddListDialog);
					}
				}],
				onLoad : function() {
					var examUserAddGrid = $("#examUserAddGrid"); //用户表格对象
					var examUserAddQueryForm = $("#examUserAddQueryForm"); //用户查询对象
					var examUserAddOrgTree = $("#examUserAddOrgTree"); //组织机构树对象
					var examUserAddCurSelOrgId = ""; //当前选中的组织机构ID
					var examUserAddCurSelOrgName = ""; //当前选中的组织机构名称
					
					examUserAddGrid.datagrid({
						url : "",
						columns : [[ 
								{field : "ID", title : "", checkbox : true}, 
								{field : "NAME", title : "姓名", width : 80, align : "center"}, 
								{field : "LOGIN_NAME", title : "登录名称", width : 80, align : "center"}, 
								{field : "ORG_NAME", title : "组织机构", width : 80, align : "center"},
								{field : "POST_NAMES", title : "岗位", width : 80, align : "center"},
								]]
					});
					
					examUserAddOrgTree.tree({
						idFiled : "ID",
						textFiled : "NAME",
						parentField : "PARENT_ID",
						iconClsFiled : "ICON",
						checkedFiled : "CHECKED",
						lines : true,
					    url : "exam/examUserAddOrgTreeList",
						onSelect : function(node){
							examUserAddCurSelOrgId = node.ID;
							examUserAddCurSelOrgName = node.NAME;
							
							$("#examUserAdd_one").val(examUserAddCurSelOrgId);
							examUserAddGrid.datagrid("uncheckAll");
							examUserAddGrid.datagrid("reload", $.fn.my.serializeObj(examUserAddQueryForm));
						},
						onLoadSuccess : function(node, data){
							if(!examUserAddCurSelOrgId || !examUserAddGrid.datagrid("options").url){//如果是第一次
								examUserAddCurSelOrgId = 1;
								examUserAddGrid.datagrid("options").url = "exam/examUserAddList";
							}
							
							var node = examUserAddOrgTree.tree("find", examUserAddCurSelOrgId);
							if(!node){
								examUserAddCurSelOrgId = 1;
								node = examUserAddOrgTree.tree("find", examUserAddCurSelOrgId);
							}
							examUserAddOrgTree.tree("select", node.target);
						}
					});
				}
			});
		}
		
		//添加考试用户查询
		function examUserAddQuery(){
			var examUserAddGrid = $("#examUserAddGrid");
			var examUserAddQueryForm = $("#examUserAddQueryForm");
			examUserAddGrid.datagrid("uncheckAll");
			examUserAddGrid.datagrid("load", $.fn.my.serializeObj(examUserAddQueryForm));
		}
		
		//添加考试用户重置
		function examUserAddReset(){
			var examUserAddQueryForm = $("#examUserAddQueryForm");
			examUserAddQueryForm.form("reset");
			examUserAddQuery();
		}
		
		//完成添加考试用户
		function doExamUserAdd(examUserAddListDialog){
			var examUserAddGridRows = $("#examUserAddGrid").datagrid("getChecked");
			if (examUserAddGridRows.length == 0) {
				parent.$.messager.alert("提示消息", "请选择一行或多行数据！", "info");
				return;
			}
			
			var params = $.fn.my.serializeField(examUserAddGridRows, {attrName : "userIds"});
			params += "&id=" + $("#examUserAdd_ten").val();
			
			$.messager.confirm("确认消息", "确定要添加？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.progress();
				$.ajax({
					url : "exam/doExamUserAdd",
					data : params,
					success : function(obj){
						$("#examUserGrid").datagrid("reload");
						$.messager.progress("close");
						
						if(!obj.success){
							parent.$.messager.alert("提示消息", obj.message, "info");
							return;
						}
						examUserAddListDialog.dialog("close");
					}
				});
			});
		}
		
		//完成删除考试用户
		function doExamUserDel(){
			var examUserGridRows = $("#examUserGrid").datagrid("getChecked");
			if (examUserGridRows.length == 0) {
				parent.$.messager.alert("提示消息", "请选择一行或多行数据！", "info");
				return;
			}
			
			var params = $.fn.my.serializeField(examUserGridRows, {attrName : "examUserIds"});
			$.messager.confirm("确认消息", "确定要删除？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.progress();
				$.ajax({
					url : "exam/doExamUserDel",
					data : params,
					success : function(obj){
						$("#examUserGrid").datagrid("reload");
						$.messager.progress("close");
						
						if(!obj.success){
							parent.$.messager.alert("提示消息", obj.message, "info");
							return;
						}
					}
				});
			});
		}
		
		//到达考试用户列表页面
		function toMarkUserList(){
			var examGridRows = $("#examGrid").datagrid("getChecked");
			if (examGridRows.length != 1) {
				parent.$.messager.alert("提示消息", "请选择一行数据！", "info");
				return;
			}
			
			var markUserListDialog;
			markUserListDialog = $("<div/>").dialog({
				title : "判卷用户列表",
				href : "exam/toMarkUserList",
				queryParams : {id : examGridRows[0].ID},
				maximized : true,
				onLoad : function() {
					var markUserGrid = $("#markUserGrid");
					var markUserQueryForm = $("#markUserQueryForm");
					var markUserOrgTree = $("#markUserOrgTree");
					var markUserCurSelOrgId = "";
					var markUserCurSelOrgName = "";
					
					markUserGrid.datagrid({
						url : "",
						toolbar : "#markUserToolbar",
						columns : [[ 
								{field : "ID", title : "", checkbox : true}, 
								{field : "USER_NAME", title : "姓名", width : 80, align : "center"}, 
								{field : "LOGIN_NAME", title : "登录名称", width : 80, align : "center"}, 
								{field : "ORG_NAME", title : "组织机构", width : 80, align : "center"},
								{field : "POST_NAMES", title : "岗位", width : 80, align : "center"},
								]]
					});
					markUserOrgTree.tree({
						idFiled : "ID",
						textFiled : "NAME",
						parentField : "PARENT_ID",
						iconClsFiled : "ICON",
						checkedFiled : "CHECKED",
						lines : true,
					    url : "exam/markUserOrgTreeList",
						onSelect : function(node){
							markUserCurSelOrgId = node.ID;
							markUserCurSelOrgName = node.NAME;
							
							$("#markUser_one").val(markUserCurSelOrgId);
							markUserGrid.datagrid("uncheckAll");
							markUserGrid.datagrid("reload", $.fn.my.serializeObj(markUserQueryForm));
						},
						onLoadSuccess : function(node, data){
							if(!markUserCurSelOrgId || !markUserGrid.datagrid("options").url){//如果是第一次
								markUserCurSelOrgId = 1;
								markUserGrid.datagrid("options").url = "exam/markUserList";
							}
							
							var node = markUserOrgTree.tree("find", markUserCurSelOrgId);
							if(!node){
								markUserCurSelOrgId = 1;
								node = orgTree.tree("find", markUserCurSelOrgId);
							}
							markUserOrgTree.tree("select", node.target);
						}
					});
				}
			});
		}
		
		//考试用户查询
		function markUserQuery(){
			var markUserGrid = $("#markUserGrid");
			var markUserQueryForm = $("#markUserQueryForm");
			markUserGrid.datagrid("uncheckAll");
			markUserGrid.datagrid("load", $.fn.my.serializeObj(markUserQueryForm));
		}
		
		//考试用户重置
		function markUserReset(){
			var markUserQueryForm = $("#markUserQueryForm");
			markUserQueryForm.form("reset");
			markUserQuery();
		}
		
		//到达添加考试用户页面
		function toMarkUserAddList(){
			var markUserAddListDialog;
			markUserAddListDialog = $("<div/>").dialog({
				title : "用户列表",
				href : "exam/toMarkUserAddList",
				queryParams : {id : $("#markUser_ten").val()},
				toolbar : "#markUserAddToolbar",
				maximized : true,
				buttons : [{
					text : "添加",
					iconCls : "icon-add", 
					selected : true, 
					handler : function (){
						doMarkUserAdd(markUserAddListDialog);
					}
				}],
				onLoad : function() {
					var markUserAddGrid = $("#markUserAddGrid"); //用户表格对象
					var markUserAddQueryForm = $("#markUserAddQueryForm"); //用户查询对象
					var markUserAddOrgTree = $("#markUserAddOrgTree"); //组织机构树对象
					var markUserAddCurSelOrgId = ""; //当前选中的组织机构ID
					var markUserAddCurSelOrgName = ""; //当前选中的组织机构名称
					
					markUserAddGrid.datagrid({
						url : "",
						columns : [[ 
								{field : "ID", title : "", checkbox : true}, 
								{field : "NAME", title : "姓名", width : 80, align : "center"}, 
								{field : "LOGIN_NAME", title : "登录名称", width : 80, align : "center"}, 
								{field : "ORG_NAME", title : "组织机构", width : 80, align : "center"},
								{field : "POST_NAMES", title : "岗位", width : 80, align : "center"},
								]]
					});
					
					markUserAddOrgTree.tree({
						idFiled : "ID",
						textFiled : "NAME",
						parentField : "PARENT_ID",
						iconClsFiled : "ICON",
						checkedFiled : "CHECKED",
						lines : true,
					    url : "exam/markUserAddOrgTreeList",
						onSelect : function(node){
							markUserAddCurSelOrgId = node.ID;
							markUserAddCurSelOrgName = node.NAME;
							
							$("#markUserAdd_one").val(markUserAddCurSelOrgId);
							markUserAddGrid.datagrid("uncheckAll");
							markUserAddGrid.datagrid("reload", $.fn.my.serializeObj(markUserAddQueryForm));
						},
						onLoadSuccess : function(node, data){
							if(!markUserAddCurSelOrgId || !markUserAddGrid.datagrid("options").url){//如果是第一次
								markUserAddCurSelOrgId = 1;
								markUserAddGrid.datagrid("options").url = "exam/markUserAddList";
							}
							
							var node = markUserAddOrgTree.tree("find", markUserAddCurSelOrgId);
							if(!node){
								markUserAddCurSelOrgId = 1;
								node = markUserAddOrgTree.tree("find", markUserAddCurSelOrgId);
							}
							markUserAddOrgTree.tree("select", node.target);
						}
					});
				}
			});
		}
		
		//添加考试用户查询
		function markUserAddQuery(){
			var markUserAddGrid = $("#markUserAddGrid");
			var markUserAddQueryForm = $("#markUserAddQueryForm");
			markUserAddGrid.datagrid("uncheckAll");
			markUserAddGrid.datagrid("load", $.fn.my.serializeObj(markUserAddQueryForm));
		}
		
		//添加考试用户重置
		function markUserAddReset(){
			var markUserAddQueryForm = $("#markUserAddQueryForm");
			markUserAddQueryForm.form("reset");
			markUserAddQuery();
		}
		
		//完成添加考试用户
		function doMarkUserAdd(markUserAddListDialog){
			var markUserAddGridRows = $("#markUserAddGrid").datagrid("getChecked");
			if (markUserAddGridRows.length == 0) {
				parent.$.messager.alert("提示消息", "请选择一行或多行数据！", "info");
				return;
			}
			
			var params = $.fn.my.serializeField(markUserAddGridRows, {attrName : "userIds"});
			params += "&id=" + $("#markUserAdd_ten").val();
			
			$.messager.confirm("确认消息", "确定要添加？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.progress();
				$.ajax({
					url : "exam/doMarkUserAdd",
					data : params,
					success : function(obj){
						$("#markUserGrid").datagrid("reload");
						$.messager.progress("close");
						
						if(!obj.success){
							parent.$.messager.alert("提示消息", obj.message, "info");
							return;
						}
						markUserAddListDialog.dialog("close");
					}
				});
			});
		}
		
		//完成删除考试用户
		function doMarkUserDel(){
			var markUserGridRows = $("#markUserGrid").datagrid("getChecked");
			if (markUserGridRows.length == 0) {
				parent.$.messager.alert("提示消息", "请选择一行或多行数据！", "info");
				return;
			}
			
			var params = $.fn.my.serializeField(markUserGridRows, {attrName : "markUserIds"});
			$.messager.confirm("确认消息", "确定要删除？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.progress();
				$.ajax({
					url : "exam/doMarkUserDel",
					data : params,
					success : function(obj){
						$("#markUserGrid").datagrid("reload");
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