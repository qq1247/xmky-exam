<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
	<head>
		<title>试题修改</title>
		<%@include file="/script/home/common.jspf"%>
	</head>
	<body>
		<%@include file="/script/home/head.jspf"%>
		<div class="container">
			<div class="row">
				<div class="col-md-3">
					<div id="questionTypeTree" class="exam-tree"></div>
				</div>
				<div class="col-md-9">
					<div class="panel panel-default">
   						<div class="panel-body">
   							<form id="questionForm" class="form-horizontal" role="form">
   								<input type="hidden" id="id" name="id" value="${question.id }" />
								<div class="row">
									<div class="col-md-6">
										<div class="form-group">
											<label for="questionTypeName" class="col-md-4 control-label">分类：</label>
											<div class="col-md-8">
												<input type="hidden" id="questionTypeId" name="questionTypeId" value="${questionType.id }" />
												<input type="text" id="questionTypeName" name="questionTypeName" value="${questionType.name }"
													class="form-control" readonly="readonly" placeholder="请选择左侧试题分类">
											</div>
										</div>
									</div>
									<div class="col-md-6">
										<div class="form-group">
											<label for="state" class="col-md-4 control-label">状态：</label>
											<div class="col-md-8">
												<select id="state" name="state" class="form-control">
													<c:forEach var="dict" items="${STATE_DICT }">
													<option value="${dict.dictKey }" ${dict.dictKey == question.state ? "selected" : ""}>${dict.dictValue }</option>
													</c:forEach>
												</select>
											</div>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-6">
										<div class="form-group">
											<label for="type" class="col-md-4 control-label">类型：</label>
											<div class="col-md-8">
												<select id="type" name="type" class="form-control" onchange="initType();" ${!empty question.id ? "disabled='disabled'" : ""}>
													<c:forEach var="dict" items="${QUESTION_TYPE_DICT }">
													<option value="${dict.dictKey }" ${dict.dictKey == question.type ? "selected" : ""}>${dict.dictValue }</option>
													</c:forEach>
												</select>
											</div>
										</div>
									</div>
									<div class="col-md-6">
										<div class="form-group">
											<label for="difficulty" class="col-md-4 control-label">难度：</label>
											<div class="col-md-8">
												<select id="difficulty" name="difficulty" class="form-control">
													<c:forEach var="dict" items="${QUESTION_DIFFICULTY_DICT }">
													<option value="${dict.dictKey }" ${dict.dictKey == question.difficulty ? "selected" : ""}>${dict.dictValue }</option>
													</c:forEach>
												</select>
											</div>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-12">
										<div class="form-group">
											<label for="title" class="col-md-2 control-label">题干：</label>
											<div class="col-md-10">
												<textarea id="title" name="title">${question.title }</textarea>
												<small class="help-block"></small>
											</div>
										</div>
									</div>
								</div>
								<div id="optionRow" class="row" ${(question.type != 1 && question.type != 2) ? "style='display: none;'" : "" }>
									<div class="col-md-12">
										<div class="form-group">
											<label class="col-md-2 control-label">选项：</label>
											<div class="col-md-10">
												<div class="panel panel-default">
													<div id="optionPanel" class="panel-body">
														<c:if test="${question.type == 1 || question.type == 2 }">
														<c:set var="labs" value="${fn:split('A,B,C,D,E,F,G', ',')}"></c:set>
														<c:forEach var="lab" items="${labs }">
														<c:set var="ol" value="option${lab }"></c:set>
														<c:if test="${!empty question[ol] }">
														<div class="row">
															<div class="col-md-12">
																<div class="form-group">
																	<div class="col-md-1 lab">
																	</div>
																	<div class="col-md-10 content">
																		<c:if test="${!empty question[ol] }">
																		<textarea id="${ol }" name="${ol }">${question[ol] }</textarea>
																		<small class="help-block"></small>
																		</c:if>
																	</div>
																	<div class="col-md-1 oprate">
																	</div>
																</div>
															</div>
														</div>
														</c:if>
														</c:forEach>
														</c:if>
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-12">
										<div class="form-group">
											<label for="answer" class="col-md-2 control-label">答案：</label>
											<div class="col-md-10">
												<div class="panel panel-default">
													<div id="answerPanel" class="panel-body">
														<c:if test="${question.type == 1 }">
														<c:set var="labs" value="${fn:split('A,B,C,D,E,F,G', ',')}"></c:set>
														<c:forEach var="lab" items="${labs }">
														<c:set var="ol" value="option${lab }"></c:set>
														<c:if test="${!empty question[ol] }">
														<label class="radio-inline">
															<input type="radio" name="answer" value="${lab }" ${fn:contains(question.answer, lab) ? "checked='checked'" : ""} >${lab }
														</label>
														</c:if>
														</c:forEach>
														</c:if>
														<c:if test="${question.type == 2 }">
														<c:set var="labs" value="${fn:split('A,B,C,D,E,F,G', ',')}"></c:set>
														<c:forEach var="lab" items="${labs }">
														<c:set var="ol" value="option${lab }"></c:set>
														<c:if test="${!empty question[ol] }">
														<label class="checkbox-inline">
															<input type="checkbox" name="answer" value="${lab }" ${fn:contains(question.answer, lab) ? "checked='checked'" : ""} >${lab }
														</label>
														</c:if>
														</c:forEach>
														</c:if>
														<c:if test="${question.type == 3 }">
														<div class="note">
															如果每个空有多个同义词，则用三个竖线分开。如：山西|||晋；一般|||通常|||普遍
														</div>
														<% pageContext.setAttribute("v", "\n"); %>
														<c:forEach var="answer" items="${fn:split(question.answer, v) }" varStatus="s">
														<div class="form-group">
															<label class="col-md-2 control-label lab"></label>
															<div class="col-md-8 content">
																<input type="text" id="answer${s.count }" name="answer" class="form-control" value="${answer }">
															</div>
															<div class="col-md-2 oprate"></div>
														</div>
														</c:forEach>
														<label class="checkbox-inline">
															<input type="checkbox" name="optionA" value="1" ${question.optionA == "1" ? "checked='checked'" : ""} >
															答案无顺序
														</label>
														<label class="checkbox-inline">
															<input type="checkbox" name="optionB" value="1" ${question.optionB == "1" ? "checked='checked'" : ""} >
															忽略大小写
														</label>
														<label class="checkbox-inline">
															<input type="checkbox" name="optionC" value="1" ${question.optionC == "1" ? "checked='checked'" : ""} >
															半对半分
														</label>
														</c:if>
														<c:if test="${question.type == 4 }">
														<label class="radio-inline">
															<input type="radio" name="answer" value="对" ${fn:contains(question.answer, "对") ? "checked='checked'" : ""}>对
														</label>
														<label class="radio-inline">
															<input type="radio" name="answer" value="错" ${fn:contains(question.answer, "错") ? "checked='checked'" : ""}>错
														</label>
														</c:if>
														<c:if test="${question.type == 5 }">
														<textarea id="answer" name="answer">${question.answer }</textarea>
														<small class="help-block"></small>
														</c:if>
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-12">
										<div class="form-group">
											<label for="analysis" class="col-md-2 control-label">解析：</label>
											<div class="col-md-10">
												<textarea id="analysis" name="analysis">${question.analysis }</textarea>
												<small class="help-block"></small>
											</div>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-12" style="text-align: center;">
										<button type="button" class="btn btn-primary" onclick="javascript:history.back(-1);">返回</button>
										<c:if test="${empty question.id }">
										<button type="button" class="btn btn-primary" onclick="doAdd()">添加</button>
										</c:if>
										<c:if test="${!empty question.id }">
										<button type="button" class="btn btn-primary" onclick="doEdit()">修改</button>
										</c:if>
									</div>
								</div>
							</form>
   						</div>
   					</div>
				</div>
			</div>
		</div>
	</body>
	<script type="text/javascript">
		//定义变量
		var $questionTypeTree = $("#questionTypeTree");
		var $questionTypeId = $("#questionTypeId");
		var $questionTypeName = $("#questionTypeName");
		var $type = $("#type");//试题类型对象
		var $optionPanel = $("#optionPanel");//选项面板对象
		var $answerPanel = $("#answerPanel");//答案面板对象
		var $optionRow = $("#optionRow");//选项行对象
		var optionLabs = ["A", "B", "C", "D", "E", "F", "G"];//选项标签
		var optionMinRow = 2;//选择选项最小行数
		var fillBlanksLabs = ["一", "二", "三", "四", "五", "六", "七"];//选项标签
		var fillBlanksMinRow = 1;//填空选项最小行数fillBlanksLabs
		var $questionForm = $("#questionForm");
		var $title = $("#title");
		var $analysis = $("#analysis");
		var $id = $("#id");
		var $KOption = {
			uploadJson : "home/question/doTempUpload",
			filePostName : "files",
			width : "100%",
			minHeight : 50,
			items : ['justifyleft', 'justifycenter', 'justifyright', 'formatblock', 'fontname', 'fontsize', 
			        '|', 'forecolor', 'hilitecolor', 'bold', 'italic', 'underline', 'strikethrough', 'lineheight', 'removeformat',  
			        '|', 'preview', 'fullscreen', 'code', 
					'|', 'image', 'flash', 'table', 'anchor', 'link', 'unlink'
				],
			afterBlur : function(){this.sync();}
		}
		var $minKOption = {
			uploadJson : "home/question/doTempUpload",
			filePostName : "files",
			width : "100%",
			minHeight : 50,
			items : ['justifyleft', 
					'formatblock', 'fontname', 'fontsize', '|', 'forecolor', 'hilitecolor', 'bold', 'italic', 
					'underline', 'strikethrough', 'removeformat', '|', 'image', 'flash'
				],
			afterBlur : function(){this.sync();}
		}
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initQuestionTypeTree();
			initEditor();
			
			if(!$id.val()){//新增
				initType();
			}else{//修改
				if($type.val() == "1" || $type.val() == "2"){//单选或多选
					updateOptionPanel();
				}
				if($type.val() == "3"){//填空
					updateFillBlanksPanel();
				}
				if($type.val() == "5"){//问答
					KindEditor.create("#answer", $KOption);
				}
			}
		});
		
		//初始化试题分类树
		function initQuestionTypeTree(){
			$.ajax({
				url : "home/question/questionTypeTreeList",
				success : function(arr) {
					$questionTypeTree.treeview({
						showBorder: false,
						expandIcon: "glyphicon glyphicon-chevron-right",
						collapseIcon: "glyphicon glyphicon-chevron-down",
						nodeIcon: "glyphicon glyphicon-bookmark",
						color: "#428BCA",
						showTags: true, 
						levels: 3,
						data: generateBootstrapTree(arr, {
							idFiled : "ID",
							textFiled : "NAME", 
							parentField : "PARENT_ID",
							disabledFiled : "DISABLED",
							expandedFiled : "EXPANDED"
						}),
						onNodeSelected : function(event, data) {
							$questionTypeId.val(data.ID);
							$questionTypeName.val(data.NAME);
						},
						onNodeUnselected : function(event, data) {
							$questionTypeId.val("");
							$questionTypeName.val("");
						}
					});
				}
			});
		}
		
		//初始化编辑器
		function initEditor(){
			KindEditor.create("#title", $KOption);
			KindEditor.create("#analysis", $KOption);
		}
		
		//初始化类型
		function initType(){
			initOptionPanel();
			updateAnswerPanel();
		}
		
		//初始化选项面板
		function initOptionPanel(){
			//恢复初始状态
			$optionPanel.empty();
			$optionRow.hide();
			
			//如果不是单选、多选则不处理。
			var $typeValue = $type.val();
			if($typeValue != "1" && $typeValue != "2"){
				return;
			}
			
			//初始化选项面板
			if(optionMinRow > optionLabs.length){
				return;
			}
			
			$optionRow.show();
			for(var i = 0; i < optionMinRow; i++){
				var html = [];
				html.push('<div class="row">');
				html.push('	<div class="col-md-12">');
				html.push('		<div class="form-group">');
				html.push('			<div class="col-md-1 lab">');
				html.push('			</div>');
				html.push('			<div class="col-md-10 content">');
				html.push('			</div>');
				html.push('			<div class="col-md-1 oprate">');
				html.push('			</div>');
				html.push('		</div>');
				html.push('	</div>');
				html.push('</div>');
				
				$optionPanel.append(html.join(""));
			}
				
			updateOptionPanel();
		}
		
		//更新答案面板
		function updateAnswerPanel(){
			$answerPanel.empty();
			
			var optionRows = $optionPanel.children(".row");
			if($type.val() == "1"){//如果是单选
				for(var i = 0; i < optionRows.length; i++){
					var html = [];
					html.push('<label class="radio-inline">');
					html.push('	<input type="radio" name="answer" value="'+optionLabs[i]+'">'+optionLabs[i]);
					html.push('</label>');
					$answerPanel.append(html.join(""));
				}
			}else if($type.val() == "2"){//如果是多选
				for(var i = 0; i < optionRows.length; i++){
					var html = [];
					html.push('<label class="checkbox-inline">');
					html.push('	<input type="checkbox" name="answer" value="'+optionLabs[i]+'">'+optionLabs[i]);
					html.push('</label>');
					$answerPanel.append(html.join(""));
				}
			}else if($type.val() == "3"){//如果是填空
				var html = [];
				html.push('<div class="note">');
				html.push('	如果每个空有多个同义词，则用三个竖线分开。如：山西|||晋；一般|||通常|||普遍');
				html.push('</div>');
			
				for(var i = 0; i < fillBlanksMinRow; i++){
					html.push('<div class="form-group">');
					html.push('	<label class="col-md-2 control-label lab"></label>');
					html.push('	<div class="col-md-8 content">');
					html.push('	</div>');
					html.push('	<div class="col-md-2 oprate">');
					html.push('	</div>');
					html.push('</div>');
				}
				
				html.push('	<label class="checkbox-inline">');
				html.push('		<input type="checkbox" name="optionA" value="1">答案无顺序');
				html.push('	</label>');
				html.push('	<label class="checkbox-inline">');
				html.push('		<input type="checkbox" name="optionB" value="1">忽略大小写');
				html.push('	</label>');
				html.push('	<label class="checkbox-inline">');
				html.push('		<input type="checkbox" name="optionC" value="1">半对半分');
				html.push('	</label>');
				
				$answerPanel.append(html.join(""));
				updateFillBlanksPanel();
			}else if($type.val() == "4"){//如果是判断
				var html = [];
				html.push('<label class="radio-inline">');
				html.push('	<input type="radio" name="answer" value="对">对');
				html.push('</label>');
				html.push('<label class="radio-inline">');
				html.push('	<input type="radio" name="answer" value="错">错');
				html.push('</label>');
				$answerPanel.append(html.join(""));
			}else if($type.val() == "5"){//如果是问答
				var html = [];
				html.push('<textarea id="answer" name="answer"></textarea>');
				html.push('<small class="help-block"></small>');
				$answerPanel.append(html.join(""));
				KindEditor.create("#answer", $KOption);
			}
		}
		
		//更新选项面板
		function updateOptionPanel(){
			var labAreas = $optionPanel.find(".lab");
			if(labAreas.length > optionLabs.length){
				return;
			}
			
			for(var i = 0; i < labAreas.length; i++){
				var labArea = $(labAreas[i]);
				labArea.html(optionLabs[i] + "：");
			}
			
			var contentAreas = $optionPanel.find(".content");
			for(var i = 0; i < contentAreas.length; i++){
				var contentArea = $(contentAreas[i]);
				var textarea = contentArea.children("textarea");
				if(!textarea[0]){
					contentArea.append('<textarea id="option'+optionLabs[i]+'" name="option'+optionLabs[i]+'"></textarea>');//如果当前区域没有textarea则添加
					contentArea.append('<small class="help-block"></small>');
					KindEditor.create("#option" + optionLabs[i], $minKOption);
				}else{
					textarea.attr("id", "option" + optionLabs[i]);//更改id和name，不影响富文本编辑器回填值
					textarea.attr("name", "option" + optionLabs[i]);
					
					var style = textarea.attr("style");
					if(!(style && style.indexOf("display") != -1 && style.indexOf("none") != -1)){//如果只是textarea，变更成富文本编辑器
						KindEditor.create("#option" + optionLabs[i], $minKOption);
					}
				}
			}
			
			var oprateAreas = $optionPanel.find(".oprate");
			for(var i = 0; i < oprateAreas.length; i++){
				var oprateArea = $(oprateAreas[i]);
				oprateArea.empty();
				var add = true, del = true, up = true, down = true;
				if(oprateAreas.length >= optionLabs.length){//达到最大值，不添加添加按钮
					add = false;
				}
				if(oprateAreas.length <= optionMinRow){//小于最小行数，不添加删除按钮
					del = false;
				}
				if(i == 0){//第一个不添加向上按钮
					up = false;
				}
				if(i == (oprateAreas.length - 1)){//最后一个不添加向下按钮
					down = false;
				}
				
				var html = [];
				if(add){
					html.push('<a href="javascript:void(0);" onclick="addOption(this)"><span class="glyphicon glyphicon-plus"></span></a>');
				}
				if(del){
					html.push('<a href="javascript:void(0);" onclick="delOption(this)"><span class="glyphicon glyphicon-trash"></span></a>');
				}
				if(up){
					html.push('<a href="javascript:void(0);" onclick="upOption(this)"><span class="glyphicon glyphicon-arrow-up"></span></a>');
				}
				if(down){
					html.push('<a href="javascript:void(0);" onclick="downOption(this)"><span class="glyphicon glyphicon-arrow-down"></span></a>');
				}
				oprateArea.append(html.join(""));
			}
		}
		
		//添加选项
		function addOption(curObj){
			var optionRows = $optionPanel.children(".row");
			if(optionRows.length >= optionLabs.length){
				return;
			}
			
			var curOptionRow = $(curObj).parent().parent().parent().parent();
			var html = [];
			html.push('<div class="row">');
			html.push('	<div class="col-md-12">');
			html.push('		<div class="form-group">');
			html.push('			<div class="col-md-1 lab">');
			html.push('			</div>');
			html.push('			<div class="col-md-10 content">');
			html.push('			</div>');
			html.push('			<div class="col-md-1 oprate">');
			html.push('			</div>');
			html.push('		</div>');
			html.push('	</div>');
			html.push('</div>');
			
			curOptionRow.after(html.join(""));
			updateOptionPanel();
			updateAnswerPanel();
		}
		
		//删除选项
		function delOption(curObj){
			var optionRows = $optionPanel.children(".row");
			if(optionRows.length <= optionMinRow){
				return;
			}
			
			var curOptionRow = $(curObj).parent().parent().parent().parent();
			curOptionRow.remove();
			updateOptionPanel();
			updateAnswerPanel();
		}
		
		//上移选项
		function upOption(curObj){
			var curOptionRow = $(curObj).parent().parent().parent().parent();
			var prevOptionRow = curOptionRow.prev();
			
			var textarea = prevOptionRow.find("textarea[id^='option']");
			var id = textarea.attr("id");
			curOptionRow.after(prevOptionRow);
			KindEditor.remove("#" + id, $minKOption);
			
			updateOptionPanel();
			updateAnswerPanel();
		}
		
		//下移选项
		function downOption(curObj){
			var curOptionRow = $(curObj).parent().parent().parent().parent();
			var nextOptionRow = curOptionRow.next();
			
			var textarea = nextOptionRow.find("textarea[id^='option']");
			var id = textarea.attr("id");
			curOptionRow.before(nextOptionRow);
			KindEditor.remove("#" + id, $minKOption);
			
			updateOptionPanel();
			updateAnswerPanel();
		}
		
		//更新填空选项面板
		function updateFillBlanksPanel(){
			var labAreas = $answerPanel.find(".lab");
			if(labAreas.length > fillBlanksLabs.length){
				return;
			}
			
			for(var i = 0; i < labAreas.length; i++){
				var labArea = $(labAreas[i]);
				labArea.attr("for", "answer" + i);
				labArea.html("填空" + fillBlanksLabs[i] + "：");
			}
			
			var contentAreas = $answerPanel.find(".content");
			for(var i = 0; i < contentAreas.length; i++){
				var contentArea = $(contentAreas[i]);
				var input = contentArea.children("input");
				if(!input[0]){
					contentArea.append('<input type="text" id="answer' + i + '" name="answer" class="form-control">');//如果当前区域没有input则添加
				}else{
					input.attr("id", "answer" + i);
				}
			}
			
			var oprateAreas = $answerPanel.find(".oprate");
			for(var i = 0; i < oprateAreas.length; i++){
				var oprateArea = $(oprateAreas[i]);
				oprateArea.empty();
				var add = true, del = true, up = true, down = true;
				if(oprateAreas.length >= fillBlanksLabs.length){//达到最大值，不添加添加按钮
					add = false;
				}
				if(oprateAreas.length <= fillBlanksMinRow){//小于最小行数，不添加删除按钮
					del = false;
				}
				if(i == 0){//第一个不添加向上按钮
					up = false;
				}
				if(i == (oprateAreas.length - 1)){//最后一个不添加向下按钮
					down = false;
				}
				
				var html = [];
				if(add){
					html.push('<a href="javascript:void(0);" onclick="addFillBlanksOption(this)"><span class="glyphicon glyphicon-plus"></span></a>');
				}
				if(del){
					html.push('<a href="javascript:void(0);" onclick="delFillBlanksOption(this)"><span class="glyphicon glyphicon-trash"></span></a>');
				}
				if(up){
					html.push('<a href="javascript:void(0);" onclick="upFillBlanksOption(this)"><span class="glyphicon glyphicon-arrow-up"></span></a>');
				}
				if(down){
					html.push('<a href="javascript:void(0);" onclick="downFillBlanksOption(this)"><span class="glyphicon glyphicon-arrow-down"></span></a>');
				}
				oprateArea.append(html.join(""));
			}
		}
		
		//添加填空选项
		function addFillBlanksOption(curObj){
			var optionRows = $answerPanel.find(".lab");
			if(optionRows.length >= fillBlanksLabs.length){
				return;
			}
			
			var curOptionRow = $(curObj).parent().parent();
			var html = [];
			html.push('<div class="form-group">');
			html.push('	<label class="col-md-2 control-label lab"></label>');
			html.push('	<div class="col-md-8 content">');
			html.push('	</div>');
			html.push('	<div class="col-md-2 oprate">');
			html.push('	</div>');
			html.push('</div>');
			
			curOptionRow.after(html.join(""));
			updateFillBlanksPanel();
		}
		
		//删除填空选项
		function delFillBlanksOption(curObj){
			var optionRows = $answerPanel.find(".lab");
			if(optionRows.length <= fillBlanksMinRow){
				return;
			}
			
			var curOptionRow = $(curObj).parent().parent();
			curOptionRow.remove();
			updateFillBlanksPanel();
		}
		
		//上移填空选项
		function upFillBlanksOption(curObj){
			var curOptionRow = $(curObj).parent().parent();
			var prevOptionRow = curOptionRow.prev();
			
			curOptionRow.after(prevOptionRow);
			updateFillBlanksPanel();
		}
		
		//下移填空选项
		function downFillBlanksOption(curObj){
			var curOptionRow = $(curObj).parent().parent();
			var nextOptionRow = curOptionRow.next();
			
			curOptionRow.before(nextOptionRow);
			updateFillBlanksPanel();
		}
		
		//校验
		function valid(){
			if(!$title.val()){//校验题干
				$title.parent().parent().removeClass("has-success").addClass("has-error");
				$title.prev().removeClass("field-success").addClass("field-error");
				$title.next(".help-block").html("请填写必填项目");
				return false;
			}
			
			$title.parent().parent().removeClass("has-error").addClass("has-success");
			$title.prev().removeClass("field-error").addClass("field-success");
			$title.next(".help-block").html("");
			
			if($type.val() == "1" || $type.val() == "2"){//校验选项
				var edits = [];
				$optionPanel.find("textarea[name^='option']").each(function (index, domEle) {
					edits.push($(domEle));
				});
				
				for(var i = 0; i < edits.length; i++){
					var $edit = edits[i];
					if(!$edit.val()){
						$edit.parent().parent().parent().parent().parent().parent().parent().parent().removeClass("has-success").addClass("has-error");
						$edit.prev().removeClass("field-success").addClass("field-error");
						$edit.next(".help-block").html("请填写必填项目");
						return false;
					}
					
					$edit.parent().parent().parent().parent().parent().parent().parent().parent().removeClass("has-error").addClass("has-success");
					$edit.prev().removeClass("field-error").addClass("field-success");
					$edit.next(".help-block").html("");
				}
			}
			
			if($type.val() == "5"){//校验答案
				var $answer = $("#answer");
				if(!$answer.val()){
					$answer.parent().parent().parent().parent().parent().removeClass("has-success").addClass("has-error");
					$answer.prev().removeClass("field-success").addClass("field-error");
					$answer.next(".help-block").html("请填写必填项目");
					return false;
				}
				
				$answer.parent().parent().parent().parent().parent().removeClass("has-error").addClass("has-success");
				$answer.prev().removeClass("field-error").addClass("field-success");
				$answer.next(".help-block").html("");
			}
			
			$questionForm.bootstrapValidator("addField", "questionTypeName", {//校验分类
				validators : {
					notEmpty : {}
				}
			});
			
			if($type.val() == "1" || $type.val() == "2" || $type.val() == "3" || $type.val() == "4"){//校验答案
				$questionForm.bootstrapValidator("addField", "answer", {
					validators : {
						notEmpty : {}
					}
				});
			}
			
			var bv = $(questionForm).data('bootstrapValidator');
			bv.validate();
			return bv.isValid();
		}
		
		//完成试题添加
		function doAdd(){
			if(!valid()){
				return;
			}
			
			$.ajax({
				url : "home/question/doAdd",
				data : $questionForm.serialize(),
				success : function(obj) {
					if (!obj.success) {
						BootstrapDialog.show({
							title : "提示消息",
							message : obj.message,
							buttons : [{
								label : "确定",
								cssClass : 'btn-primary',
								action : function(dialogItself) {
									dialogItself.close();
								}
							}]
						});
						return;
					}
					
					window.location.href = "home/question/toList";
				}
			});
		}
		
		//完成试题修改
		function doEdit(){
			if(!valid()){
				return;
			}
			
			$.ajax({
				url : "home/question/doEdit",
				data : $questionForm.serialize(),
				success : function(obj) {
					if (!obj.success) {
						BootstrapDialog.show({
							title : "提示消息",
							message : obj.message,
							buttons : [{
								label : "确定",
								cssClass : 'btn-primary',
								action : function(dialogItself) {
									dialogItself.close();
								}
							}]
						});
						return;
					}
					
					window.location.href = "home/question/toList";
				}
			});
		}
	</script>
</html>