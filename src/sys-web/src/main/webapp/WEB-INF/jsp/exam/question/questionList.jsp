<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>试题列表</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
		<div class="layui-fluid">
			<div class="layui-row layui-col-space10">
				<div class="layui-col-md2">
					<div class="layui-card">
						<div class="layui-form">
			      			<ul id="questionTypeTree" class="ztree"></ul>
			 			</div>
					</div>
				</div>
				<div class="layui-col-md10">
					<div class="layui-card">
						<%-- 试题查询条件 --%>
						<form id="questionQueryForm" class="layui-form layui-card-header layuiadmin-card-header-auto">
							<input type="hidden" id="questionOne" name="one">
							<div class="layui-form-item ">
								<div class="layui-inline">
									<input type="text" name="two" placeholder="请输入编号" class="layui-input">
								</div>
								<div class="layui-inline">
									<input type="text" name="three" placeholder="请输入题干" class="layui-input">
								</div>
								<div class="layui-inline">
									<select name="five">
										<option value="">请选择类型</option>
										<c:forEach var="questionTypeDict" items="${QUESTION_TYPE_DICT_LIST }">
										<option value="${questionTypeDict.dictKey }">${questionTypeDict.dictValue }</option>
										</c:forEach>
					      			</select>
								</div>
								<div class="layui-inline">
									<select name="six">
										<option value="">请选择难度</option>
										<c:forEach var="questionDifficultyDict" items="${QUESTION_DIFFICULTY_DICT_LIST }">
										<option value="${questionDifficultyDict.dictKey }">${questionDifficultyDict.dictValue }</option>
										</c:forEach>
					      			</select>
								</div>
								<div class="layui-inline">
									<button type="button" class="layui-btn layuiadmin-btn-useradmin" onclick="questionQuery();">
										<i class="layui-icon layuiadmin-button-btn"></i>查询
									</button>
									<button type="button" class="layui-btn layui-btn-primary layuiadmin-btn-useradmin" onclick="questionReset();">
										<i class="layui-icon layuiadmin-button-btn"></i>重置
									</button>
								</div>
							</div>
						</form>
						<div class="layui-card-body">
							<div style="padding-bottom: 10px;">
								<my:auth url="question/toAdd"><button class="layui-btn layuiadmin-btn-useradmin" onclick="toQuestionAdd();">添加</button></my:auth>
								<my:auth url="question/wordTemplateExport"><button class="layui-btn layuiadmin-btn-useradmin" onclick="wordTemplateExport();">下载试题模板</button></my:auth>
								<my:auth url="question/wordImp"><button id="wordImp" class="layui-btn layuiadmin-btn-useradmin">导入试题</button></my:auth>
							</div>
							<script type="text/html" id="questionToolbar">
								<my:auth url="question/toEdit"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="questionEdit"><i class="layui-icon layui-icon-edit"></i>修改</a></my:auth>
								<my:auth url="question/doDel"><a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="questionDel"><i class="layui-icon layui-icon-delete"></i>删除</a></my:auth>
							</script>
							<script type="text/html" id="stateToolbar">
								<input type="checkbox" name="state" value="{{d.ID}}" lay-filter="questionPublish" 
									lay-skin="switch" lay-text="启用|禁用" {{d.STATE == 1 ? 'checked' : ''}}>
							</script>
							<%-- 试题数据表格 --%>
							<table id="questionTable" lay-filter="questionTable"></table>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
	<%@include file="/script/myJs/tail.jspf"%>
	<script type="text/javascript">
		//定义变量
		var questionQueryForm = $("#questionQueryForm"); //试题查询对象
		var questionTypeTree; //试题分类树对象
		var rootNodeId = ""; // 根节点ID
		var curSelQuestionTypeId = ""; //当前选中的试题分类ID
		var curSelQuestionTypeName = ""; //当前选中的试题分类名称
		
		var optionLabs = ["A", "B", "C", "D", "E", "F", "G"];//选项标签
		var optionMinRow = 2;//选择选项最小行数
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initQuestionTable();
			initQuestionTypeTree();
			initWordImp();
		});
	
		//初始化试题表格
		function initQuestionTable() {
			layui.table.render({
				elem : "#questionTable",
				url : "question/list",
				cols : [[
						{field : "CODE", title : "编号", align : "center"},
						{field : "TITLE", title : "题干", align : "center", width : 400},
						{field : "TYPE_NAME", title : "类型", align : "center"},
						{field : "DIFFICULTY_NAME", title : "难度", align : "center"},
						{field : "STATE_NAME", title : "状态", align : "center", templet: "#stateToolbar", unresize: true},
						{field : "QUESTION_TYPE_NAME", title : "分类", align : "center"},
						{field : "SCORE", title : "默认分值", align : "center"},
						{field : "NO", title : "排序", align : "center"},
						{fixed : "right", title : "操作 ", toolbar : "#questionToolbar", align : "center", width : 200},
						]],
				page : true,
				height : "full-180",
				method : "post",
				defaultToolbar : [],
				parseData : function(question) {
					return {
						"code" : question.succ,
						"msg" : question.msg,
						"count" : question.data.total,
						"data" : question.data.rows
					};
				},
				request : {
					pageName: "curPage",
					limitName: "pageSize"
				}, 
				response : {
					statusCode : true
				}
			});
			layui.table.on("rowDouble(questionTable)", function(obj) {
				<my:auth url="question/toEdit">toQuestionEdit(obj.data.ID);</my:auth>
			});
			layui.table.on("tool(questionTable)", function(obj) {
				var data = obj.data;
				if (obj.event === "questionEdit") {
					toQuestionEdit(obj.data.ID);
				} else if (obj.event === "questionDel") {
					doQuestionDel(obj.data.ID);
				}
			});
			layui.form.on("switch(questionPublish)", function(obj) {
				doQuestionPublish(obj.value);
			});
		}
		
		//初始化试题分类树
		function initQuestionTypeTree() {
			questionTypeTree = $.fn.zTree.init($("#questionTypeTree"), {
				async : {
					url : "question/questionTypeTreeList",
					enable : true,
					dataFilter : ztreeDataFilter
				},
				callback : {
					onClick : function(event, treeId, treeNode) {
						curSelQuestionTypeId = treeNode.ID;
						curSelQuestionTypeName = treeNode.NAME;
						if (rootNodeId == curSelQuestionTypeId) {
							$("#questionOne").val(null);
						} else {
							$("#questionOne").val(curSelQuestionTypeId);
						}
						questionQuery();
					},
					onAsyncSuccess : function(event, treeId, msg, treeNode) {
						var questionTypeTree = $.fn.zTree.getZTreeObj(treeId);
						questionTypeTree.expandAll(true);
						
						if (!curSelQuestionTypeId) {
							var rootNode = questionTypeTree.getNodesByFilter(function(node) { return (node.level == 0); }, true);
							questionTypeTree.selectNode(rootNode);
							
							curSelQuestionTypeId = rootNode.ID;
							curSelQuestionTypeName = rootNode.NAME;
							rootNodeId = rootNode.ID;
							// $("#questionOne").val(curSelQuestionTypeId);
							return;
						}
						
						var curNode = questionTypeTree.getNodeByParam("id", curSelQuestionTypeId, null);
						questionTypeTree.selectNode(curNode);
						
						questionQuery();
					}
				}
			});
			
			$("#questionTypeTree").height($(window).height() - 45);
		}
		
		//试题查询
		function questionQuery() {
			layui.table.reload("questionTable", {"where" : $.fn.my.serializeObj(questionQueryForm)});
		}
	
		//试题重置
		function questionReset() {
			questionQueryForm[0].reset();
			questionQuery();
		}
		
		//到达添加试题页面
		function toQuestionAdd() {
			if (!curSelQuestionTypeId) {
				layer.alert("请选择试题分类！", {"title" : "提示消息"});
				return;
			}
			
			$.ajax({
				url : "question/toAdd",
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "添加",
						area : ["800px", "500px"],
						content : obj,
						btn : ["添加", "取消"],
						type : 1,
						yes : function(index, layero) {
							doQuestionAdd(index);
						},
						success: function(layero, index) {
							layui.layer.full(index);
							
							$("#questionTypeId").val(curSelQuestionTypeId);
							$("#questionTypeName").val(curSelQuestionTypeName);
							UE.delEditor("title");
							UE.getEditor("title");
							UE.delEditor("analysis");
							UE.getEditor("analysis");
							
							layui.form.on("radio(type)", function(data) {
								initType();
							});
							
							initType();
							layui.form.render(null, "questionEditFrom");
						}
					});
				}
			});
		}

		//完成试题添加
		function doQuestionAdd(questionAddDialogIndex) {
			layui.form.on("submit(questionEditBtn)", function(data) {
				// 校验数据有效性
				var titleUE = UE.getEditor("title");
				if (!titleUE.hasContents()) {
					layer.alert("题干不允许为空！", {"title" : "提示消息"});
					return;
				}
				data.field["title"] = titleUE.getContent();
				
				var typeValue = $("input[name='type']:checked").val();
				if (typeValue == "1" || typeValue == "2") {//校验选项
					var optionRows = $("#optionPanel").children();
					for (var i = 0; i < optionRows.length - 1; i++) {
						var optionUE = UE.getEditor("option" + optionLabs[i]);
						if (!optionUE.hasContents()) {
							layer.alert("选项" + optionLabs[i] + "不允许为空！", {"title" : "提示消息"});
							return;
						}
						data.field["option" + optionLabs[i]] = optionUE.getContent();
					}
				}
				
				if (typeValue == "1" || typeValue == "4") {//校验答案
					var answer = $("input[name='answer']:checked").val();
					if (!answer) {
						layer.alert("请选择一个答案！", {"title" : "提示消息"});
						return;
					}
					
				} else if (typeValue == "2") {
					var answer = $("input[name='answer']:checked");
					var answers = [];
					answer.each(function (index, domEle) { 
						answers.push(domEle.value);
					});
					
					if (answers.length == 0) {
						layer.alert("请至少选择一个答案！", {"title" : "提示消息"});
						return;
					}
					data.field.answer = answers.join(",");
				} else if (typeValue == "3") {
					var answers = xmSelect.get("#answer", true).getValue("value");
					if (answers.length == 0) {
						layer.alert("请至少添加一个答案！", {"title" : "提示消息"});
						return;
					}
					
					data.field.answer = answers.join('\n');
				} else if (typeValue == "5") {
					var answerUE = UE.getEditor("answer");
					if (!answerUE.hasContents()) {
						layer.alert("答案不允许为空！", {"title" : "提示消息"});
						return;
					}
					data.field.answer = answerUE.getContent();
				}
				
				var analysisUE = UE.getEditor("analysis");
				if (analysisUE.hasContents()) {
					data.field.analysis = analysisUE.getContent();
				}
				
				// 完成试题添加
				layer.confirm("确定要添加？", function(index) {
					var state = $("input[name='state']:checked").val();
					if (!state) {
						data.field.state = 2;
					}
					
					var scoreOption = $("input[name='scoreOptions']:checked");
					var scoreOptions = [];
					scoreOption.each(function (index, domEle) { 
						scoreOptions.push(domEle.value);
					});
					data.field.scoreOptions = scoreOptions;
					
					$.ajax({
						url : "question/doAdd",
						data : data.field,
						success : function(obj) {
							questionQuery();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
							layer.close(questionAddDialogIndex);
						}
					});
				});
			});
			$("[lay-filter='questionEditBtn']").click();
		}
		
		//到达修改试题页面
		function toQuestionEdit(id) {
			$.ajax({
				url : "question/toEdit",
				data : {id : id},
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "修改",
						area : ["800px", "500px"],
						content : obj,
						btn : ["修改", "生成新版本", "取消"],
						type : 1,
						yes : function(index, layero) {
							doQuestionEdit(index, false);
						},
						btn2 : function(index, layero) {
							doQuestionEdit(index, true);
							return false;
						},
						success: function(layero, index) {
							layui.layer.full(index);
							
							UE.delEditor("title");
							UE.getEditor("title");
							UE.delEditor("analysis");
							UE.getEditor("analysis");
							
							layui.form.render(null, "questionEditFrom");
						}
					});
				}
			});
		}

		//完成试题修改
		function doQuestionEdit(questionEditDialogIndex, newVer) {
			layui.form.on("submit(questionEditBtn)", function(data) {
				var titleUE = UE.getEditor("title");
				if (!titleUE.hasContents()) {
					layer.alert("题干不允许为空！", {"title" : "提示消息"});
					return;
				}
				data.field["title"] = titleUE.getContent();
				
				var typeValue = $("input[name='type']:checked").val();
				if (typeValue == "1" || typeValue == "2") {//校验选项
					var optionRows = $("#optionPanel").children();
					for (var i = 0; i < optionRows.length - 1; i++) {
						var optionUE = UE.getEditor("option" + optionLabs[i]);
						if (!optionUE.hasContents()) {
							layer.alert("选项" + optionLabs[i] + "不允许为空！", {"title" : "提示消息"});
							return;
						}
						data.field["option" + optionLabs[i]] = optionUE.getContent();
					}
				}
				
				if (typeValue == "1" || typeValue == "4") {//校验答案
					var answer = $("input[name='answer']:checked").val();
					if (!answer) {
						layer.alert("请选择一个答案！", {"title" : "提示消息"});
						return;
					}
					
				} else if (typeValue == "2") {
					var answer = $("input[name='answer']:checked");
					var answers = [];
					answer.each(function (index, domEle) { 
						answers.push(domEle.value);
					});
					
					if (answers.length == 0) {
						layer.alert("请至少选择一个答案！", {"title" : "提示消息"});
						return;
					}
					data.field.answer = answers.join(",");
				} else if (typeValue == "3") {
					var answers = xmSelect.get("#answer", true).getValue("value");
					if (answers.length == 0) {
						layer.alert("请至少添加一个答案！", {"title" : "提示消息"});
						return;
					}
					
					data.field.answer = answers.join('\n');
				} else if (typeValue == "5") {
					var answerUE = UE.getEditor("answer");
					if (!answerUE.hasContents()) {
						layer.alert("答案不允许为空！", {"title" : "提示消息"});
						return;
					}
					data.field.answer = answerUE.getContent();
				}
				
				var analysisUE = UE.getEditor("analysis");
				if (analysisUE.hasContents()) {
					data.field.analysis = analysisUE.getContent();
				}
				
				var msg = newVer ? "确定要生成新版本？" : "当前修改会同步到引用的试卷<br/>确定要修改？";
				layer.confirm(msg, function(index) {
					data.field.newVer = newVer;
					var state = $("input[name='state']:checked").val();
					if (!state) {
						data.field.state = 2;
					}
					
					var scoreOption = $("input[name='scoreOptions']:checked");
					var scoreOptions = [];
					scoreOption.each(function (index, domEle) { 
						scoreOptions.push(domEle.value);
					});
					data.field.scoreOptions = scoreOptions;
					
					$.ajax({
						url : "question/doEdit",
						data : data.field,
						success : function(obj) {
							questionQuery();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
							layer.close(questionEditDialogIndex);
						}
					});
				});
			});
			$("[lay-filter='questionEditBtn']").click();;
		}

		//完成试题删除
		function doQuestionDel(id) {
			layer.confirm("确定要删除？", function(index) {
				$.ajax({
					url : "question/doDel",
					data : {id : id},
					success : function(obj) {
						questionQuery();
						
						if (!obj.succ) {
							layer.alert(obj.msg, {"title" : "提示消息"});
							return;
						}
						
						layer.close(index);
					}
				});
			});
		}
		
		//初始化类型
		function initType() {
			updateOptionPanel();
			updateAnswerPanel();
			updateScoreOptions();
		}
		
		//更新选项面板
		function updateOptionPanel() {
			//恢复初始状态
			var optionPanel = $("#optionPanel");
			var optionRow = $("#optionRow");
			
			optionPanel.empty();
			optionRow.hide();
			
			//如果不是单选、多选则不处理。
			var typeValue = $("input[name='type']:checked").val();
			if (typeValue != "1" && typeValue != "2") {
				return;
			}
			
			//初始化选项面板
			if (optionMinRow > optionLabs.length) {
				return;
			}
			
			optionRow.show();//后显示会有问题，js加载不到
			for(var i = 0; i < optionMinRow; i++) {
				var optionLabVal = optionLabs[i];
				var html = [];
				html.push('<div class="layui-row layui-col-space10">');
				html.push('	<div class="layui-col-md1">');
				html.push('		<label class="layui-form-label">'+optionLabVal+'：</label>');
				html.push('	</div>');
				html.push('	<div class="layui-col-md11">');
				html.push('		<script id="option'+optionLabVal+'" name="option'+optionLabVal+'" type="text/plain" style="width:100%;height: 50px;"><\/script>');
				html.push('	</div>');
				html.push('</div>');
				
				optionPanel.append(html.join(""));
				UE.delEditor("option"+optionLabVal);
				UE.getEditor("option"+optionLabVal);
			}
			
			var html = [];
			html.push('<div class="layui-row layui-col-space10">');
			html.push('	<div class="layui-col-md1">');
			html.push('	</div>');
			html.push('	<div class="layui-col-md11">');
			html.push('		<button class="layui-btn layui-btn-primary layui-btn-sm" onclick="addQuestionOption(this);"><i class="layui-icon">&#xe654;</i>添加选项</button>');
			html.push('		<button class="layui-btn layui-btn-primary layui-btn-sm" onclick="delQuestionOption(this);"><i class="layui-icon">&#xe67e</i>移除选项</button>');
			html.push('	</div>');
			html.push('</div>');
			optionPanel.append(html.join(""));
		}
		
		//更新答案面板
		function updateAnswerPanel() {
			// 清空面板
			var optionPanel = $("#optionPanel");
			var answerPanel = $("#answerPanel");
			var typeValue = $("input[name='type']:checked").val();
			answerPanel.empty();
			
			// 重新初始化
			var optionRows = optionPanel.children();
			if (typeValue == "1") {//如果是单选
				for(var i = 0; i < optionRows.length - 1; i++) {
					var html = [];
					html.push('<input type="radio" name="answer" value="'+optionLabs[i]+'" title="'+optionLabs[i]+'">');
					answerPanel.append(html.join(""));
					layui.form.render(null, "questionEditFrom");
				}
			} else if (typeValue == "2") {//如果是多选
				for(var i = 0; i < optionRows.length - 1; i++) {
					var html = [];
					html.push('<input type="checkbox" name="answer" value="'+optionLabs[i]+'" title="'+optionLabs[i]+'" lay-skin="primary">');
					answerPanel.append(html.join(""));
					layui.form.render(null, "questionEditFrom");
				}
			} else if (typeValue == "3") {//如果是填空
				var html = [];
				html.push('<div id="answer" class="xm-select-demo"></div>');
				answerPanel.append(html.join(""));
				
				xmSelect.render({
					el : "#answer",
					name : "answer",
					filterable : true,
					tips : "请添加填空项",
					searchTips : "一个填空是一个标签，如果一个填空有多个同义词，可用三个竖线分开。如：山西|||晋；一般|||通常|||普遍",
					empty : "",
					layVerify: "required",
					layVerType: "msg",
					create : function(val, arr) {
						return {
							name: val,
							value: val
						}
					}
				});
				
				var answerSelect = $("#answer").children();
				answerSelect.attr("style", "z-index:20000001;");
			} else if (typeValue == "4") {//如果是判断
				var html = [];
				html.push('<input type="radio" name="answer" value="对" title="对" lay-verify="required">');
				html.push('<input type="radio" name="answer" value="错" title="错" lay-verify="required">');
				answerPanel.append(html.join(""));
				layui.form.render(null, "questionEditFrom");
			} else if (typeValue == "5") {//如果是问答
				var html = [];
				html.push('<div><script id="answer" name="answer" type="text/plain" style="width:100%;height: 50px;"><\/script></div>');
				answerPanel.append(html.join(""));
				UE.delEditor("answer");
				UE.getEditor("answer");
				layui.form.render(null, "questionEditFrom");
			} 
		}

		// 添加试题选项
		function addQuestionOption(curObj) {
			var addBtnRow = $(curObj).parent().parent();
			var optionRows = addBtnRow.parent().children();
			
			if (optionRows.length - 1 >= optionLabs.length) {// 如果已加到最大添加行，则不处理
				return;
			}
			
			var nextOptionLabVal = optionLabs[optionRows.length - 1];
			var html = [];
			html.push('<div class="layui-row layui-col-space10">');
			html.push('	<div class="layui-col-md1">');
			html.push('		<label class="layui-form-label">'+nextOptionLabVal+'：</label>');
			html.push('	</div>');
			html.push('	<div class="layui-col-md11">');
			html.push('		<script id="option'+nextOptionLabVal+'" name="option'+nextOptionLabVal+'" type="text/plain" style="width:100%;height: 50px;"><\/script>');
			html.push('	</div>');
			html.push('</div>');
			
			addBtnRow.before(html.join(""));
			UE.delEditor("option"+nextOptionLabVal);
			UE.getEditor("option"+nextOptionLabVal);
			
			updateAnswerPanel();
		}
		
		// 删除试题选项
		function delQuestionOption(curObj) {
			var delBtnRow = $(curObj).parent().parent();
			var optionRows = delBtnRow.parent().children();
			
			if (optionRows.length - 1 <= optionMinRow) {// 如果已删到最小保留行，则不处理
				return;
			}
			
			var preOptionRows = delBtnRow.prev();
			preOptionRows.remove();
			updateAnswerPanel();
		}
		
		// 更新分值选项
		function updateScoreOptions() {
			//恢复初始状态
			var scoreOptionsPanel = $("#scoreOptionsPanel");
			var scoreOptionsRow = $("#scoreOptionsRow");
			
			scoreOptionsPanel.empty();
			scoreOptionsRow.hide();
			
			//如果不是多选、填空则不处理。
			var typeValue = $("input[name='type']:checked").val();
			if (typeValue != "2" && typeValue != "3") {
				return;
			}
			
			scoreOptionsRow.show();
			var html = [];
			if (typeValue == "2" || typeValue == "3") {
				html.push('<input type="checkbox" lay-filter="scoreOptions" name="scoreOptions" value="1" lay-skin="switch" lay-text="半对半分|全对得分" >');
			}
			if (typeValue == "3") {
				html.push('<input type="checkbox" lay-filter="scoreOptions" name="scoreOptions" value="2" lay-skin="switch" lay-text="答案无顺序|答案有顺序" >');
				html.push('<input type="checkbox" lay-filter="scoreOptions" name="scoreOptions" value="3" lay-skin="switch" lay-text="大小写不敏感|大小写敏感" >');
				html.push('<input type="checkbox" lay-filter="scoreOptions" name="scoreOptions" value="4" lay-skin="switch" lay-text="包含答案得分|等于答案得分" >');
			}
			scoreOptionsPanel.append(html.join(""));
			layui.form.render(null, "questionEditFrom");
		}
		
		// word模板导出
		function wordTemplateExport() {
			window.location.href = "question/wordTemplateExport";
		}
		
		// word试题导出
		function initWordImp() {
			var uploader = new plupload.Uploader({
				browse_button : "wordImp",
				file_data_name : "file",
				url : "",
				multipart_params : {
					
				},
				flash_swf_url : "script/plupload/Moxie.swf",
				filters : {
					mime_types : [{ title : "文档", extensions : "doc" }
					              ],
					max_file_size : "10mb"
				},
				init : {
					FilesAdded : function(up, files) {
						if (!curSelQuestionTypeId) {
							layer.alert("请选择试题分类！", {"title" : "提示消息"});
							return;
						}
						
						uploader.start();
					},
					BeforeUpload : function (uploader, files) {
		                uploader.settings.url = "${pageContext.request.contextPath}/question/wordImp?questionTypeId=" + curSelQuestionTypeId;
					},
					UploadProgress : function(up, file) {
					},
					FileUploaded : function(up, file, responseObj) { //每个附件上传后，服务端返回的响应消息。
						questionQuery();
					
						var response = $.parseJSON(responseObj.response);
						layer.alert(response.msg, {"title" : "提示消息"});
					},
					UploadComplete : function(up, files) {//所有附件上传完成后
						
					},
					Error : function(up, err) { //客户端的错误消息。如附件大小错误，附件不存在， http错误等。
						layer.alert(err.message, {"title" : "提示消息"});
					}
				}
			});
			
			uploader.init();
		}
		
		// 试题发布
		function doQuestionPublish(id) {
			$.ajax({
				url : "question/doPublish",
				data : {
					id : id
				},
				success : function(obj) {
					questionQuery();
					
					if (!obj.succ) {
						layer.alert(obj.msg, {"title" : "提示消息"});
						return;
					}
					
					layer.close(index);
				}
			});
		}
	</script>
</html>
