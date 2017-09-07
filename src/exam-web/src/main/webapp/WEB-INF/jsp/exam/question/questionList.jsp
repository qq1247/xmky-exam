<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>试题列表</title>
		<%@include file="/script/myJs/common.jspf"%>
		<script type="text/javascript" src="script/ckeditor/ckeditor.js"></script>
	</head>
	<body>
		<div class="easyui-layout" data-options="fit:true">
			<!-- 左侧试题分类菜单 -->
			<div data-options="region:'west',border:false" style="width: 240px;padding:5px">
				<div class="easyui-panel" data-options="fit:true">
					<ul id="questionTypeTree"></ul>
					<div id="questionTypeTreeMenu" class="easyui-menu" style="width:120px;">
						<my:auth url="question/toAdd"><div onclick="toQuestionAdd()" data-options="iconCls:'icon-add'">添加</div></my:auth>
						<div class="menu-sep"></div>
						<div onclick="questionTypeTreeFlush()" data-options="iconCls:'icon-reload'">刷新</div>
					</div>
				</div>
			</div>
			<div data-options="region:'center',border:false">
				<%-- 试题查询条件 --%>
				<div id="questionToolbar" style="padding:0 30px;">
					<div class="conditions">
						<form id="questionQueryForm">
							<input type="hidden" id="question_one" name="one"/>
							<!-- <span class="con-span">分类：</span>
							<input name="five" class="easyui-textbox" style="width:166px;height:35px;line-height:35px;"> -->
							<span class="con-span">类型：</span>
							<select id="questionQueryForm_two" name="two" class="easyui-combobox" data-options="editable:false" style="width:166px;height:35px;line-height:35px;">
								<option value=""></option>
								<c:forEach var="dict" items="${QUESTION_TYPE }">
								<option value="${dict.dictKey }">${dict.dictValue }</option>
								</c:forEach>
							</select>
							<span class="con-span">难度：</span>
							<select id="questionQueryForm_three" name="three" class="easyui-combobox" data-options="editable:false" style="width:166px;height:35px;line-height:35px;">
								<option value=""></option>
								<c:forEach var="dict" items="${QUESTION_DIFFICULTY }">
								<option value="${dict.dictKey }">${dict.dictValue }</option>
								</c:forEach>
							</select>
							<span class="con-span">状态：</span>
							<select id="questionQueryForm_four" name="four" class="easyui-combobox" data-options="editable:false" style="width:166px;height:35px;line-height:35px;">
								<option value=""></option>
								<c:forEach var="dict" items="${STATE }">
								<option value="${dict.dictKey }">${dict.dictValue }</option>
								</c:forEach>
							</select>
							<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" data-options="selected:true" onclick="questionQuery();">查询</a>
							<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-clear" onclick="questionReset();">重置</a>
						</form>
					</div>
					<div class="opt-buttons">
						<my:auth url="question/toAdd"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" data-options="selected:true" onclick="toQuestionAdd();">添加</a></my:auth>
						<my:auth url="question/toEdit"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" onclick="toQuestionEditForBtn();">修改</a></my:auth>
						<my:auth url="question/doDel"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" onclick="doQuestionDelForBtn();">删除</a></my:auth>
						<my:auth url="question/toQuestionTypeUpdate"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" onclick="toQuestionTypeUpdate();">设置分类</a></my:auth>
					</div>
				</div>
				<%-- 试题数据表格 --%>
				<table id="questionGrid">
				</table>
			</div>
		</div>
	</body>
	<script type="text/javascript">
		//定义变量
		var questionGrid = $("#questionGrid"); //试题表格对象
		var questionQueryForm = $("#questionQueryForm"); //试题查询对象
		var questionTypeTree = $("#questionTypeTree"); //组织机构树对象
		var curSelQuestionTypeId = ""; //当前选中的组织机构ID
		var curSelQuestionTypeName = ""; //当前选中的组织机构名称
		var questionOptions = [
			<c:forEach var="dict" items="${QUESTION_OPTIONS }" varStatus="v">
			${v.index != 0 ? "," : ""}"${dict.dictValue}"
			</c:forEach>
		];
		var ckToolbar = [
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
			initQuestionGrid();
			initQuestionTypeTree();
		});
	
		//初始化试题表格
		function initQuestionGrid() {
			questionGrid.datagrid( {
				onDblClickRow : <my:auth url="question/toEdit">toQuestionEditForDblClick</my:auth>, 
				toolbar : "#questionToolbar",
				columns : [[ 
						{field : "ID", title : "", checkbox : true}, 
						{field : "CODE", title : "编号", width : 50, align : "center"},
						{field : "TITLE", title : "题干 ", width : 300, align : "center"},
						{field : "TYPE_NAME", title : "类型", width : 80, align : "center"},
						{field : "DIFFICULTY_NAME", title : "难度", width : 80, align : "center"},
						{field : "STATE_NAME", title : "状态 ", width : 80, align : "center"},
						{field : "QUESTION_TYPE_NAME", title : "分类 ", width : 80, align : "center"}
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
			    url : "question/questionTypeTreeList",
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
					
					$("#question_one").val(curSelQuestionTypeId);
					questionGrid.datagrid("uncheckAll");
					questionGrid.datagrid("reload", $.fn.my.serializeObj(questionQueryForm));
				},
				onLoadSuccess : function(node, data){
					if(!curSelQuestionTypeId || !questionGrid.datagrid("options").url){//如果是第一次
						curSelQuestionTypeId = 1;
						questionGrid.datagrid("options").url = "question/list";
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
	
		//试题查询
		function questionQuery() {
			questionGrid.datagrid("uncheckAll");
			questionGrid.datagrid("load", $.fn.my.serializeObj(questionQueryForm));
		}
	
		//试题重置
		function questionReset() {
			questionQueryForm.form("reset");
			questionQuery();
		}
		
		//到达添加试题页面
		function toQuestionAdd() {
			if(!curSelQuestionTypeId){
				parent.$.messager.alert("提示消息", "请选择试题分类！", "info");
				return;
			}
			
			var questionAddDialog;
			questionAddDialog = $("<div/>").dialog({
				title : "添加试题",
				href : "question/toAdd",
				width : 900,
				height : 500,
				maximized : true,
				buttons : [{
					text : "添加", 
					iconCls : "icon-add", 
					selected : true, 
					handler : function (){
						doQuestionAdd(questionAddDialog);
					}
				}],
				onLoad : function() {
					$("#question_type").combobox({
						required : true,
						editable : false,
						onSelect : updateOption
					});
					$("#question_difficulty").combobox({
						required : true,
						editable : false
					});
					$("#question_state").combobox({
						required : true,
						editable : false
					});
					CKEDITOR.replace("question_title", {
						height: "80px",
			        	toolbar : ckToolbar
					});
					CKEDITOR.replace("question_analysis", {
						height: "80px",
			        	toolbar : ckToolbar
					});
					
					$("#question_questionTypeId").val(curSelQuestionTypeId);
					$("#question_questionTypeName").val(curSelQuestionTypeName);
				}
			});
		}

		//完成添加试题
		function doQuestionAdd(questionAddDialog) {
			var questionEditFrom = $("#questionEditFrom");
			if(!questionEditFrom.form("validate")){
				return;
			}
			
			var type = $("#question_type").combobox("getValue");
			if(!type){
				return;
			}
			
			if(type == "1" || type == "2"){
				for(var i in questionOptions){
					var optionTr = $("#option" + questionOptions[i] + "Tr");
					if(!optionTr.is(":hidden")){
						if(CKEDITOR.instances["question_option" + questionOptions[i]]
							&& $.trim(CKEDITOR.instances["question_option" + questionOptions[i]].getData()) == ""){
							parent.$.messager.alert("提示消息", "选项" + questionOptions[i] + "为空", "info");
							CKEDITOR.instances["question_option" + questionOptions[i]].focus();
							return;
						}
					}
				}
			}
				
			$.messager.confirm("确认消息", "确定要添加？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.progress();
				questionEditFrom.form("submit", {
					url : "question/doAdd",
					success : function(data) {
						questionGrid.datagrid("reload");
						$.messager.progress("close");

						var obj = $.parseJSON(data);
						if (!obj.success) {
							parent.$.messager.alert("提示消息", obj.message, "info");
							return;
						}

						questionAddDialog.dialog("close");
					}
				});
			});
		}

		//到达修改试题页面
		function toQuestionEdit(id) {
			var questionEditDialog;
			questionEditDialog = $("<div/>").dialog({
				title : "修改试题",
				href : "question/toEdit",
				queryParams : {"id" : id},
				width : 900,
				height : 500,
				maximized : true,
				buttons : [ {
					text : "修改",
					iconCls : "icon-edit",
					selected : true,
					handler : function() {
						doQuestionEdit(questionEditDialog);
					}
				} ],
				onLoad : function() {
					$("#question_type").combobox({
						required : true,
						editable : false,
						readonly : true,
						onSelect : updateOption
					});
					$("#question_difficulty").combobox({
						required : true,
						editable : false
					});
					$("#question_state").combobox({
						required : true,
						editable : false
					});
					CKEDITOR.replace("question_title", {
						height: "80px",
			        	toolbar : ckToolbar
					});
					CKEDITOR.replace("question_analysis", {
						height: "80px",
			        	toolbar : ckToolbar
					});
					
					updateOption();
					
					var type = $("#question_type").combobox("getValue");
					if(!type){
						return;
					}
					
					if(type == "1" || type == "2"){
						for(var i in questionOptions){
							var optionVal = $("#question_option" + questionOptions[i] + "_").val()
							if(optionVal){
								_addOption(questionOptions[i]);
								CKEDITOR.instances["question_option" + questionOptions[i]].setData(optionVal);
							}
						}
						
						updateAnswer();
						
						$("#question_answer").combobox("setValues", $("#question_answer_").val());
					}else if(type == "4"){
						$("#question_answer").combobox("setValue", $("#question_answer_").val());
					}else if(type == "3" || type == "5"){
						CKEDITOR.instances.question_answer.setData($("#question_answer_").val());
					}
				}
			});
		}

		//到达修改试题页面
		function toQuestionEditForBtn() {
			var questionGridRows = questionGrid.datagrid("getChecked");
			if (questionGridRows.length != 1) {
				parent.$.messager.alert("提示消息", "请选择一行数据！", "info");
				return;
			}

			toQuestionEdit(questionGridRows[0].ID);
		}

		//到达修改试题页面
		function toQuestionEditForDblClick(rowIndex, rowData) {
			questionGrid.datagrid("uncheckAll");
			questionGrid.datagrid("checkRow", rowIndex);
			toQuestionEditForBtn();
		}

		//完成修改试题
		function doQuestionEdit(questionEditDialog) {
			var questionEditFrom = $("#questionEditFrom");
			if(!questionEditFrom.form("validate")){
				return;
			}
			
			var type = $("#question_type").combobox("getValue");
			if(!type){
				return;
			}
			
			if(type == "1" || type == "2"){
				for(var i in questionOptions){
					var optionTr = $("#option" + questionOptions[i] + "Tr");
					if(!optionTr.is(":hidden")){
						if(CKEDITOR.instances["question_option" + questionOptions[i]]
							&& $.trim(CKEDITOR.instances["question_option" + questionOptions[i]].getData()) == ""){
							parent.$.messager.alert("提示消息", "选项" + questionOptions[i] + "为空", "info");
							CKEDITOR.instances["question_option" + questionOptions[i]].focus();
							return;
						}
					}
				}
			}
				
			$.messager.confirm("确认消息", "确定要修改？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.progress();
				questionEditFrom.form("submit", {
					url : "question/doEdit",
					success : function(data) {
						questionGrid.datagrid("reload");
						$.messager.progress("close");

						var obj = $.parseJSON(data);
						if (!obj.success) {
							parent.$.messager.alert("提示消息", obj.message, "info");
							return;
						}

						questionEditDialog.dialog("close");
					}
				});
			});
		}

		//完成删除试题
		function doQuestionDel(params) {
			$.messager.confirm("确认消息", "确定要删除？", function(ok) {
				if (!ok) {
					return;
				}

				$.messager.progress();
				$.ajax({
					url : "question/doDel",
					data : params,
					success : function(obj) {
						questionGrid.datagrid("reload");
						$.messager.progress("close");
						
						if (!obj.success) {
							parent.$.messager.alert("提示消息", obj.message, "info");
						}
					}
				});
			});
		}

		//完成删除试题
		function doQuestionDelForBtn() {
			//校验数据有效性
			var questionGridRows = questionGrid.datagrid("getChecked");
			if (questionGridRows.length == 0) {
				parent.$.messager.alert("提示消息", "请选择一行或多行数据！", "info");
				return;
			}

			//删除
			doQuestionDel($.fn.my.serializeField(questionGridRows));
		}
		
		//更新选项
		function updateOption(){
			removeAllOption();
			$("#addOptionArea").remove();
			$("#answerTd2").empty();
			
			var type = $("#question_type").combobox("getValue");
			if(!type){
				return;
			}
			
			if(type == "1" || type == "2" || type == "4"){
				var html = [];
				if(type == "1" || type == "2"){
					html.push("<div id=\"addOptionArea\" style=\"padding: 10px;\">");
					html.push("<a id=\"addOption\" href=\"javascript:void(0);\" onClick=\"addOption();\">添加选项</a>");
					html.push("<a id=\"delOption\" href=\"javascript:void(0);\" onClick=delOption();>移除选项</a>");
					html.push("</div>");
					$("#titleTd1").append(html.join(""));
					
					$("#addOption").linkbutton({
						selected : true,
					    iconCls: "icon-add"   
					});
					$("#delOption").linkbutton({
					    iconCls: "icon-remove"   
					});
				}
				
				
				html = [];
				html.push("<input id=\"question_answer\" name=\"answer\" value=\"\" style=\"height: 30px;\" />");
				$("#answerTd2").append(html.join(""));
				
				var answerOptions = {
						required : true,
						editable : false,
						onSelect : answerSort,
						data : []
				};
				if(type == "2"){
					answerOptions.multiple = true;
				}
				if(type == "4"){
					answerOptions.data.push({"value" : "对", "text" : "对"});
					answerOptions.data.push({"value" : "错", "text" : "错"});
				}
				
				$("#question_answer").combobox(answerOptions);
			}else if(type == "3" || type == "5"){
				html = [];
				html.push("<textarea id=\"question_answer\" name=\"answer\">${question.answer }</textarea>");
				$("#answerTd2").append(html.join(""));
				
				CKEDITOR.replace("question_answer", {
					height: "80px",
		        	toolbar : ckToolbar
				});
			}
		}
		
		//移除所有选项
		function removeAllOption(){
			for(var i in questionOptions){
				var optionTr = $("#option" + questionOptions[i] + "Tr");
				if(!optionTr.is(":hidden")){
					$("#option" + questionOptions[i] + "Td1").empty();
					$("#option" + questionOptions[i] + "Td2").empty();
					optionTr.hide();
				}
			}
		}
		
		//添加选项
		function addOption(){
			for(var i in questionOptions){
				var optionTr = $("#option" + questionOptions[i] + "Tr");
				if(optionTr.is(":hidden")){
					_addOption(questionOptions[i]);
					break;
				}
			}
			
			updateAnswer();
		}
		
		function _addOption(optionChar){
			var optionTr = $("#option" + optionChar + "Tr");
			var optionTd1 = $("#option" + optionChar + "Td1");
			var optionTd2 = $("#option" + optionChar + "Td2");
			
			optionTd1.append("选项" + optionChar + "：");
			optionTd2.append("<textarea id=\"question_option" + optionChar + "\" name=\"option" + optionChar + "\"></textarea>");
			CKEDITOR.replace("question_option" + optionChar, {
				height: "80px",
	        	toolbar : ckToolbar
			});
			optionTr.show();
		}
		
		//移除选项
		function delOption(){
			for(var i in questionOptions){
				var index = questionOptions.length - 1 - i;
				var optionTr = $("#option" + questionOptions[index] + "Tr");
				if(!optionTr.is(":hidden")){
					_delOption(questionOptions[index]);
					break;
				}
			}
			
			updateAnswer();
		}
		
		function _delOption(optionChar){
			var optionTr = $("#option" + optionChar + "Tr");
			var optionTd1 = $("#option" + optionChar + "Td1");
			var optionTd2 = $("#option" + optionChar + "Td2");
			optionTd1.empty();
			optionTd2.empty();
			optionTr.hide();
		}
		
		//更新答案
		function updateAnswer(){
			var type = $("#question_type").combobox("getValue");
			if(!type){
				return;
			}
			
			if(type == "1" || type == "2"){
				var optionData = [];
				for(var i in questionOptions){
					var optionTr = $("#option" + questionOptions[i] + "Tr");
					if(!optionTr.is(":hidden")){
						optionData.push({"value" : questionOptions[i], "text" : questionOptions[i]});
					}
				}

				$("#question_answer").combobox("clear");
				$("#question_answer").combobox("loadData", optionData);
			}else if(type == "4"){
				var optionData = [];
				optionData.push({"value" : "对", "text" : "对"});
				optionData.push({"value" : "错", "text" : "错"});
				$("#question_answer").combobox("clear");
				$("#question_answer").combobox("loadData", optionData);
			}
		}
		
		//答案排序
		function answerSort(){
			$("#question_answer").combobox("setValues", $("#question_answer").combobox("getValues").sort());
		}
		
		//到达设置试题分类页面
		function toQuestionTypeUpdate(){
			var questionGridRows = questionGrid.datagrid("getChecked");
			if(questionGridRows.length == 0){
				parent.$.messager.alert("提示消息", "请选择一行或多行数据！", "info");
				return;
			}
			
			var questionTypeUpdateDialog;
			questionTypeUpdateDialog = $("<div/>").dialog({
				title : "选择试题分类",
				width : 300,
				height : 400,
				href : "question/toQuestionTypeUpdate",
				buttons : [{
					text : "确定", 
					iconCls : "icon-ok", 
					selected : true,
					handler : function (){
						doQuestionTypeUpdate(questionTypeUpdateDialog);
					}
				}],
				onLoad : function(){
					var questionTypeUpdateTree = $("#questionTypeUpdateTree"); 
					questionTypeUpdateTree.tree({
						idFiled : "ID",
						textFiled : "NAME",
						parentField : "PARENT_ID",
						iconClsFiled : "ICON",
						checkedFiled : "CHECKED",
						lines : true,
					    url : "question/questionTypeUpdateQuestionTypeTreeList",
					});
				}
			});
		}
		
		//完成设置试题分类
		function doQuestionTypeUpdate(questionTypeUpdateDialog){
			var questionTypeUpdateNode = $("#questionTypeUpdateTree").tree("getSelected");
			if(!questionTypeUpdateNode){
				parent.$.messager.alert("提示消息", "请选择一行数据！", "info");
				return;
			}
			
			$.messager.confirm("确认消息", "确定要设置？", function(ok){
				if (!ok){
					return;
				}
				
				var questionGridRows = questionGrid.datagrid("getChecked");
				var params = $.fn.my.serializeField(questionGridRows);
				params += ("&questionTypeId=" + questionTypeUpdateNode.ID);

				$.messager.progress();
				$.ajax({
					url : "question/doQuestionTypeUpdate",
					data : params,
					success : function(obj){
						questionTypeTree.tree("reload");
						questionTypeUpdateDialog.dialog("close");
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