<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>试卷列表</title>
		<%@include file="/script/myJs/common.jspf"%>
		<script type="text/javascript" src="script/ckeditor/ckeditor.js"></script>
	</head>
	<body>
		<div class="easyui-layout" data-options="fit:true">
			<!-- 左侧试卷分类菜单 -->
			<div data-options="region:'west',border:false" style="width: 240px;padding:5px">
				<div class="easyui-panel" data-options="fit:true">
					<ul id="paperTypeTree"></ul>
					<div id="paperTypeTreeMenu" class="easyui-menu" style="width:120px;">
						<my:auth url="paper/toAdd"><div onclick="toPaperAdd()" data-options="iconCls:'icon-add'">添加</div></my:auth>
						<div class="menu-sep"></div>
						<div onclick="paperTypeTreeFlush()" data-options="iconCls:'icon-reload'">刷新</div>
					</div>
				</div>
			</div>
			<div data-options="region:'center',border:false">
				<%-- 试卷查询条件 --%>
				<div id="paperToolbar" style="padding:0 30px;">
					<div class="conditions">
						<form id="paperQueryForm">
							<input type="hidden" id="paper_one" name="one">
							<span class="con-span">名称：</span>
							<input name="two" class="easyui-textbox" style="width:166px;height:35px;line-height:35px;">
							<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" data-options="selected:true" onclick="paperQuery();">查询</a>
							<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-clear" onclick="paperReset();">重置</a>
						</form>
					</div>
					<div class="opt-buttons">
						<my:auth url="paper/toAdd"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" data-options="selected:true" onclick="toPaperAdd();">添加</a></my:auth>
						<my:auth url="paper/toEdit"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" onclick="toPaperEditForBtn();">修改</a></my:auth>
						<my:auth url="paper/doDel"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" onclick="doPaperDelForBtn();">删除</a></my:auth>
						<my:auth url="paper/toPaperTypeUpdate"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" onclick="toPaperTypeUpdate();">设置分类</a></my:auth>
						<my:auth url="paper/toPaperCfg"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" onclick="toPaperCfg();">配置试卷</a></my:auth>
					</div>
				</div>
				<%-- 试卷数据表格 --%>
				<table id="paperGrid">
				</table>
			</div>
		</div>
	</body>
	<script type="text/javascript">
		//定义变量
		var paperGrid = $("#paperGrid"); //试卷表格对象
		var paperQueryForm = $("#paperQueryForm"); //试卷查询对象
		var paperTypeTree = $("#paperTypeTree"); //试卷分类树对象
		var curSelPaperTypeId = ""; //当前选中的试卷分类ID
		var curSelPaperTypeName = ""; //当前选中的试卷分类名称
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
			initPaperGrid();
			initPaperTypeTree();
		});
	
		//初始化试卷表格
		function initPaperGrid() {
			paperGrid.datagrid( {
				onDblClickRow : <my:auth url="paper/toEdit">toPaperEditForDblClick</my:auth>, 
				toolbar : "#paperToolbar",
				columns : [[ 
						{field : "ID", title : "", checkbox : true}, 
						{field : "NAME", title : "名称", width : 50, align : "center"},
						{field : "TOTLE_SCORE", title : "总分数", width : 80, align : "center"},
						{field : "PAPER_TYPE_NAME", title : "试卷分类", width : 50, align : "center"},
						{field : "STATE_NAME", title : "状态", width : 80, align : "center"}
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
			    url : "paper/paperTypeTreeList",
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
					
					$("#paper_one").val(curSelPaperTypeId);
					paperGrid.datagrid("uncheckAll");
					paperGrid.datagrid("reload", $.fn.my.serializeObj(paperQueryForm));
				},
				onLoadSuccess : function(node, data){
					if(!curSelPaperTypeId || !paperGrid.datagrid("options").url){//如果是第一次
						curSelPaperTypeId = 1;
						paperGrid.datagrid("options").url = "paper/list";
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
	
		//试卷查询
		function paperQuery() {
			paperGrid.datagrid("uncheckAll");
			paperGrid.datagrid("load", $.fn.my.serializeObj(paperQueryForm));
		}
	
		//试卷重置
		function paperReset() {
			paperQueryForm.form("reset");
			paperQuery();
		}
		
		//到达添加试卷页面
		function toPaperAdd() {
			if(!curSelPaperTypeId){
				parent.$.messager.alert("提示消息", "请选择试卷分类！", "info");
				return;
			}
			
			var paperAddDialog;
			paperAddDialog = $("<div/>").dialog({
				title : "添加试卷",
				href : "paper/toAdd",
				buttons : [{
					text : "添加", 
					iconCls : "icon-add", 
					selected : true, 
					handler : function (){
						doPaperAdd(paperAddDialog);
					}
				}],
				onLoad : function() {
					$("#paper_state").combobox({
						required : true,
						editable : false
					});
					$("#paper_name").textbox({
						required : true,
						validType : ["length[1, 16]"]
					});
					CKEDITOR.replace("paper_description", {
						height: "80px",
			        	toolbar : toolbar
					});
					
					$("#paper_paperTypeId").val(curSelPaperTypeId);
					$("#paper_paperTypeName").val(curSelPaperTypeName);
				}
			});
		}

		//完成添加试卷
		function doPaperAdd(paperAddDialog) {
			var paperEditFrom = $("#paperEditFrom");
			if(!paperEditFrom.form("validate")){
				return;
			}
			
			$.messager.confirm("确认消息", "确定要添加？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.progress();
				paperEditFrom.form("submit", {
					url : "paper/doAdd",
					success : function(data) {
						paperGrid.datagrid("reload");
						$.messager.progress("close");

						var obj = $.parseJSON(data);
						if (!obj.success) {
							parent.$.messager.alert("提示消息", obj.message, "info");
							return;
						}

						paperAddDialog.dialog("close");
					}
				});
			});
		}

		//到达修改试卷页面
		function toPaperEdit(id) {
			var paperEditDialog;
			paperEditDialog = $("<div/>").dialog({
				title : "修改试卷",
				href : "paper/toEdit",
				queryParams : {"id" : id},
				buttons : [ {
					text : "修改",
					iconCls : "icon-edit",
					selected : true,
					handler : function() {
						doPaperEdit(paperEditDialog);
					}
				} ],
				onLoad : function() {
					$("#paper_state").combobox({
						required : true,
						editable : false
					});
					$("#paper_name").textbox({
						required : true,
						validType : ["length[1, 16]"]
					});
					
					CKEDITOR.replace("paper_description", {
						height: "80px",
			        	toolbar : toolbar
					});
				}
			});
		}

		//到达修改试卷页面
		function toPaperEditForBtn() {
			var paperGridRows = paperGrid.datagrid("getChecked");
			if (paperGridRows.length != 1) {
				parent.$.messager.alert("提示消息", "请选择一行数据！", "info");
				return;
			}

			toPaperEdit(paperGridRows[0].ID);
		}

		//到达修改试卷页面
		function toPaperEditForDblClick(rowIndex, rowData) {
			paperGrid.datagrid("uncheckAll");
			paperGrid.datagrid("checkRow", rowIndex);
			toPaperEditForBtn();
		}

		//完成修改试卷
		function doPaperEdit(paperEditDialog) {
			var paperEditFrom = $("#paperEditFrom");
			if(!paperEditFrom.form("validate")){
				return;
			}
				
			$.messager.confirm("确认消息", "确定要修改？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.progress();
				paperEditFrom.form("submit", {
					url : "paper/doEdit",
					success : function(data) {
						paperGrid.datagrid("reload");
						$.messager.progress("close");

						var obj = $.parseJSON(data);
						if (!obj.success) {
							parent.$.messager.alert("提示消息", obj.message, "info");
							return;
						}

						paperEditDialog.dialog("close");
					}
				});
			});
		}

		//完成删除试卷
		function doPaperDel(params) {
			$.messager.confirm("确认消息", "确定要删除？", function(ok) {
				if (!ok) {
					return;
				}

				$.messager.progress();
				$.ajax({
					url : "paper/doDel",
					data : params,
					success : function(obj) {
						paperGrid.datagrid("reload");
						$.messager.progress("close");
						
						if (!obj.success) {
							parent.$.messager.alert("提示消息", obj.message, "info");
						}
					}
				});
			});
		}

		//完成删除试卷
		function doPaperDelForBtn() {
			//校验数据有效性
			var paperGridRows = paperGrid.datagrid("getChecked");
			if (paperGridRows.length == 0) {
				parent.$.messager.alert("提示消息", "请选择一行或多行数据！", "info");
				return;
			}

			//删除
			doPaperDel($.fn.my.serializeField(paperGridRows));
		}
		
		//答案排序
		function answerSort(){
			$("#paper_answer").combobox("setValues", $("#paper_answer").combobox("getValues").sort());
		}
		
		//到达设置试卷分类页面
		function toPaperTypeUpdate(){
			var paperGridRows = paperGrid.datagrid("getChecked");
			if(paperGridRows.length == 0){
				parent.$.messager.alert("提示消息", "请选择一行或多行数据！", "info");
				return;
			}
			
			var paperTypeUpdateDialog;
			paperTypeUpdateDialog = $("<div/>").dialog({
				title : "选择试卷分类",
				width : 300,
				height : 400,
				href : "paper/toPaperTypeUpdate",
				buttons : [{
					text : "确定", 
					iconCls : "icon-ok", 
					selected : true,
					handler : function (){
						doPaperTypeUpdate(paperTypeUpdateDialog);
					}
				}],
				onLoad : function(){
					var paperTypeUpdatePaperTypeTree = $("#paperTypeUpdatePaperTypeTree"); 
					paperTypeUpdatePaperTypeTree.tree({
						idFiled : "ID",
						textFiled : "NAME",
						parentField : "PARENT_ID",
						iconClsFiled : "ICON",
						checkedFiled : "CHECKED",
					    url : "paper/paperTypeUpdatePaperTypeTreeList",
					});
				}
			});
		}
		
		//完成设置试卷分类
		function doPaperTypeUpdate(paperTypeUpdateDialog){
			var paperTypeUpdatePaperTypeNode = $("#paperTypeUpdatePaperTypeTree").tree("getSelected");
			if(!paperTypeUpdatePaperTypeNode){
				parent.$.messager.alert("提示消息", "请选择一行数据！", "info");
				return;
			}
			
			$.messager.confirm("确认消息", "确定要设置？", function(ok){
				if (!ok){
					return;
				}
				
				var paperGridRows = paperGrid.datagrid("getChecked");
				var params = $.fn.my.serializeField(paperGridRows)
				params += ("&paperTypeId=" + paperTypeUpdatePaperTypeNode.ID);

				$.messager.progress();
				$.ajax({
					url : "paper/doPaperTypeUpdate",
					data : params,
					success : function(obj){
						paperTypeUpdateDialog.dialog("close");
						paperTypeTree.tree("reload");
						$.messager.progress("close");
						
						if(!obj.success){
							parent.$.messager.alert("提示消息", obj.message, "info");
							return;
						}
					}
				});
			});
		}
		
		//到达配置试卷页面
		function toPaperCfg(){
			var paperGridRows = paperGrid.datagrid("getChecked");
			if (paperGridRows.length != 1) {
				parent.$.messager.alert("提示消息", "请选择一行数据！", "info");
				return;
			}
			
			var paperCfgDialog;
			paperCfgDialog = $("<div/>").dialog({
				title : "配置试卷",
				maximized : true,
				href : "paper/toPaperCfg",
				queryParams : {id : paperGridRows[0].ID },
				buttons : [{
					text : "关闭", 
					iconCls : "icon-ok",
					selected : true, 
					handler : function (){
						paperCfgDialog.dialog("close");
					}
				}],
				onLoad : function(){
					$("#paperCfg_paperId").val(paperGridRows[0].ID);
					initPaperCfgPaperTree(paperGridRows[0].ID);
				},
				onClose : function(){
					$("#addChapterMenu").remove();
					$("#addQuestionMenu").remove();
					$("#delQuestionMenu").remove();
					$(this).dialog("destroy");
				}
			});
		}
		
		//初始化配置试卷树
		function initPaperCfgPaperTree(id){
			var paperCfgPaperTree = $("#paperCfgPaperTree"); 
			paperCfgPaperTree.tree({
				idFiled : "ID",
				textFiled : "NAME",
				parentField : "PARENT_ID",
				iconClsFiled : "ICON",
				checkedFiled : "CHECKED",
			    url : "paper/paperCfgPaperTreeList",
			    queryParams : {id : id},
				onContextMenu : function(e, node){
					e.preventDefault();
					$(this).tree("select", node.target);
					
					var treeLevel = $(node.target).parentsUntil("ul.tree","ul").length + 1;
					if(treeLevel == 1){
						$("#paperCfgAddMenu").menu("show", {
							left : e.pageX,
							top : e.pageY
						});
					}else if(treeLevel == 2){
						$("#paperCfgListMenu").menu("show", {
							left : e.pageX,
							top : e.pageY
						});
					}else if(treeLevel == 3){
						$("#paperCfgDelMenu").menu("show", {
							left : e.pageX,
							top : e.pageY
						});
					}
				}
			});
		}
		
		//到达添加章节页面
		function toPaperCfgAdd(){
			var paperCfgAddDialog;
			paperCfgAddDialog = $("<div/>").dialog({
				title : "添加章节",
				href : "paper/toPaperCfgAdd",
				buttons : [{
					text : "添加", 
					iconCls : "icon-add", 
					selected : true, 
					handler : function (){
						doPaperCfgAdd(paperCfgAddDialog);
					}
				}],
				onLoad : function() {
					var id = $("#paperCfg_paperId").val();
					var paperCfgPaperRows = $("#paperCfgPaperTree").tree("getSelected");
					var paperCfgPaperId = paperCfgPaperRows.ID;
					
					$("#paperCfgEdit_parentId").val(paperCfgPaperId);
					$("#paperCfgEdit_paperId").val(id);
					$("#paperCfgEdit_name").textbox({
						required : true
					});
					
					CKEDITOR.replace("paperCfgEdit_description", {
						height: "80px",
			        	toolbar : toolbar
					});
				}
			});
		}
		
		//完成添加章节
		function doPaperCfgAdd(paperCfgAddDialog) {
			var paperCfgEditForm = $("#paperCfgEditForm");
			if(!paperCfgEditForm.form("validate")){
				return;
			}
			
			$.messager.confirm("提示消息", "确定要添加？", function(ok){
				if (!ok){
					return;
				}
				paperCfgEditForm.form("submit", {
					url : "paper/doPaperCfgAdd",
					success : function(data) {
						paperCfgPaperTreeFlush();
						paperCfgPreviewFlush();
						$.messager.progress("close");

						var obj = $.parseJSON(data);
						if (!obj.success) {
							parent.$.messager.alert("提示消息", obj.message, "info");
							return;
						}

						paperCfgAddDialog.dialog("close");
					}
				});
			});
		}
		
		//到达修改章节页面
		function toPaperCfgEdit(paperId) {
			var paperCfgPaperRows = $("#paperCfgPaperTree").tree("getSelected");
			var paperCfgPaperId = paperCfgPaperRows.ID;
			
			var paperCfgEditDialog;
			paperCfgEditDialog = $("<div/>").dialog({
				title : "修改章节",
				href : "paper/toPaperCfgEdit",
				queryParams : {"paperQuestionId" : paperCfgPaperId},
				buttons : [ {
					text : "修改",
					iconCls : "icon-edit",
					selected : true,
					handler : function() {
						doPaperCfgEdit(paperCfgEditDialog);
					}
				} ],
				onLoad : function() {
					$("#paperCfgEdit_name").textbox({
						required : true
					});
					
					CKEDITOR.replace("paperCfgEdit_description", {
						height: "80px",
			        	toolbar : toolbar
					});
				}
			});
		}
		
		//完成修改章节
		function doPaperCfgEdit(paperCfgEditDialog) {
			var paperCfgEditForm = $("#paperCfgEditForm");
			if(!paperCfgEditForm.form("validate")){
				return;
			}
				
			$.messager.confirm("提示消息", "确定要修改？", function(ok){
				if (!ok){
					return;
				}
				
				paperCfgEditForm.form("submit", {
					url : "paper/doPaperCfgEdit",
					success : function(data) {
						paperCfgPaperTreeFlush();
						paperCfgPreviewFlush();
						$.messager.progress("close");

						var obj = $.parseJSON(data);
						if (!obj.success) {
							parent.$.messager.alert("提示消息", obj.message, "info");
							return;
						}

						paperCfgEditDialog.dialog("close");
					}
				});
			});
		}
		
		//到达试题列表页面
		function toPaperCfgList(){
			var paperCfgListDialog;
			paperCfgListDialog = $("<div/>").dialog({
				title : "试题列表",
				href : "paper/toPaperCfgList",
				maximized : true,
				buttons : [ {
					text : "添加",
					iconCls : "icon-add",
					selected : true,
					handler : function() {
						doPaperCfgListAdd(paperCfgListDialog);
					}
				} ],
				onLoad : function() {
					$("#paperCfgList_ten").val($("#paperCfg_paperId").val());
					var paperCfgListQuestionGrid = $("#paperCfgListQuestionGrid").datagrid( {
						toolbar : "#paperCfgListToolbar",
						queryParams : {ten : $("#paperCfgList_ten").val()},
						columns : [[ 
								{field : "ID", title : "", checkbox : true}, 
								{field : "CODE", title : "编号", width : 50, align : "center"},
								{field : "TITLE", title : "题干 ", width : 300, align : "center"},
								{field : "TYPE_NAME", title : "类型", width : 80, align : "center"},
								{field : "DIFFICULTY_NAME", title : "难度", width : 80, align : "center"},
								{field : "STATE_NAME", title : "状态 ", width : 80, align : "center"}
								]]
					});
					
					var paperCfgListCurSelQuestionTypeId;
					var paperCfgListCurSelQuestionTypeName;
					var paperCfgListQuestionTypeTree = $("#paperCfgListQuestionTypeTree").tree({
						idFiled : "ID",
						textFiled : "NAME",
						parentField : "PARENT_ID",
						iconClsFiled : "ICON",
						checkedFiled : "CHECKED",
					    url : "paper/paperCfgListQuestionTypeTreeList",
						onSelect : function(node){
							paperCfgListCurSelQuestionTypeId = node.ID;
							paperCfgListCurSelQuestionTypeName = node.NAME;
							
							$("#paperCfgList_one").val(paperCfgListCurSelQuestionTypeId);
							paperCfgListQuestionGrid.datagrid("uncheckAll");
							paperCfgListQuestionGrid.datagrid("reload", $.fn.my.serializeObj($("#paperCfgListQueryForm")));
						},
						onLoadSuccess : function(node, data){
							if(!paperCfgListCurSelQuestionTypeId || !paperCfgListQuestionGrid.datagrid("options").url){//如果是第一次
								paperCfgListCurSelQuestionTypeId = 1;
								paperCfgListQuestionGrid.datagrid("options").url = "paper/paperCfgList";
							}
							
							var paperCfgListQuestionTypeNode = $("#paperCfgListQuestionTypeTree").tree("find", paperCfgListCurSelQuestionTypeId);
							if(!paperCfgListQuestionTypeNode){
								paperCfgListCurSelQuestionTypeId = 1;
								paperCfgListQuestionTypeNode = paperCfgListQuestionTypeTree.tree("find", paperCfgListCurSelQuestionTypeId);
							}
							$("#paperCfgListQuestionTypeTree").tree("select", paperCfgListQuestionTypeNode.target);
						}
					});
				}
			});
		}
		
		//试题查询
		function paperCfgListQuery() {
			var paperCfgListQuestionGrid = $("#paperCfgListQuestionGrid");
			var paperCfgListQueryForm = $("#paperCfgListQueryForm");
			paperCfgListQuestionGrid.datagrid("uncheckAll");
			paperCfgListQuestionGrid.datagrid("load", $.fn.my.serializeObj(paperCfgListQueryForm));
		}
	
		//试题重置
		function paperCfgListReset() {
			var paperCfgListQueryForm = $("#paperCfgListQueryForm");
			paperCfgListQueryForm.form("reset");
			paperCfgListQuery();
		}
		
		//完成试题添加
		function doPaperCfgListAdd(paperCfgListDialog){
			//校验数据有效性
			var paperCfgListQuestionGrid = $("#paperCfgListQuestionGrid").datagrid("getChecked");
			if (paperCfgListQuestionGrid.length == 0) {
				parent.$.messager.alert("提示消息", "请选择一行或多行数据！", "info");
				return;
			}

			//添加
			var params = {};
			var questionIds = "";
			for (var i = 0; i < paperCfgListQuestionGrid.length; i++) {
				if(i > 0){
					questionIds += ",";
				}
				questionIds += paperCfgListQuestionGrid[i].ID;
			}
			params.questionIds = questionIds;
			
			var parentPaperQuestionRow = $("#paperCfgPaperTree").tree("getSelected");
			var parentPaperQuestionId = parentPaperQuestionRow.ID;
			params.parentPaperQuestionId = parentPaperQuestionId;
			
			params.paperId = $("#paperCfg_paperId").val();
			
			$.messager.confirm("确认消息", "确定要添加？", function(ok) {
				if (!ok) {
					return;
				}

				$.messager.progress();
				$.ajax({
					url : "paper/doPaperCfgListAdd",
					data : params,
					success : function(obj) {
						paperGrid.datagrid("reload");
						paperCfgPaperTreeFlush();
						paperCfgPreviewFlush();
						$.messager.progress("close");
						
						paperCfgListDialog.dialog("close");
						if (!obj.success) {
							parent.$.messager.alert("提示消息", obj.message, "info");
						}
					}
				});
			});
		}
		
		//刷新试题树
		function paperCfgPaperTreeFlush(){
			$("#paperCfgPaperTree").tree("reload");
		}
		
		//刷新试卷预览
		function paperCfgPreviewFlush(){
			$("#paperCfgPreview").attr("src", $("#paperCfgPreview").attr("src"));
		}
		
		//删除试题
		function doPaperCfgDel(){
			$.messager.confirm("确认消息", "确定要删除？", function(ok) {
				if (!ok) {
					return;
				}

				var paperQuestionRow = $("#paperCfgPaperTree").tree("getSelected");
				var paperQuestionId = paperQuestionRow.ID;
				var params = {paperQuestionId : paperQuestionId};
				
				$.messager.progress();
				$.ajax({
					url : "paper/doPaperCfgDel",
					data : params,
					success : function(obj) {
						paperGrid.datagrid("reload");
						paperCfgPaperTreeFlush();
						paperCfgPreviewFlush();
						$.messager.progress("close");
						
						if (!obj.success) {
							parent.$.messager.alert("提示消息", obj.message, "info");
						}
					}
				});
			});
		}
		
		//到达试题排序页面
		function toPaperCfgSort(){
			var paperCfgPaperRows = $("#paperCfgPaperTree").tree("getSelected");
			var paperCfgPaperId = paperCfgPaperRows.ID;
			var params = {paperQuestionId : paperCfgPaperId};
			
			var paperCfgSortDialog;
			paperCfgSortDialog = $("<div/>").dialog({
				title : "试题排序",
				href : "paper/toPaperCfgSort",
				queryParams : params,
				buttons : [{
					text : "确定", 
					iconCls : "icon-ok", 
					selected : true,
					handler : function (){
						doPaperCfgSort(paperCfgSortDialog);
					}
				}],
				onLoad : function(){
					$("#paperCfgSort_id").val(paperCfgPaperId);
					$("#paperCfgSort_no").numberspinner({
						required : true,
						min : 1,
						max : parseInt($("#paperCfgSort_maxNo").val())
					});
				}
			});
		}
		
		//完成试题排序
		function doPaperCfgSort(paperCfgSortDialog){
			var paperCfgSortEditFrom = $("#paperCfgSortEditFrom");
			if(!paperCfgSortEditFrom.form("validate")){
				return;
			}
			
			$.messager.confirm("确认消息", "确定要排序？", function(ok){
				if (!ok){
					return;
				}
				
				$.messager.progress();
				$.ajax({
					url : "paper/doPaperCfgSort",
					data : paperCfgSortEditFrom.serialize(),
					success : function(obj){
						paperCfgPaperTreeFlush();
						paperCfgPreviewFlush();
						$.messager.progress("close");
						
						if(!obj.success){
							parent.$.messager.alert("提示消息", obj.message, "info");
							return;
						}
						paperCfgSortDialog.dialog("close");
					}
				});
			});
		}
	</script>
</html>