<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
	<head>
		<title>配置试卷</title>
		<%@include file="/script/home/common.jspf"%>
		<link href="css/paper.css" rel="stylesheet">
	</head>
	<body>
		<%@include file="/script/home/head.jspf"%>
		<div class="container paper">
			<div class="row">
				<div class="col-md-10">
					<div class="panel panel-default">
						<ul class="list-group">
							<li class="list-group-item">
								<input type="hidden" id="id" name="id" value="${paper.id }">
								<h1>${paper.name }</h1>
								<h5>${paper.description }</h5>
							</li>
						</ul>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-md-10">
					<button type="button" class="btn btn-primary" onclick="toChapterAdd();">
						<span class="glyphicon glyphicon-plus"></span>
						&nbsp;添加章节
					</button>
					<!-- <button type="button" class="btn btn-primary" onclick="toChapterAdd();">
						<span class="glyphicon glyphicon-zoom-in"></span>
						&nbsp;预览试卷
					</button> -->
					<button type="button" class="btn btn-primary" onclick="window.location.href='home/paper/toList'">
						<span class="glyphicon glyphicon-arrow-left"></span>
						&nbsp;返回
					</button>
				</div>
			</div>
			<c:forEach var="paperQuestionEx" items="${paperQuestionExList }" varStatus="v">
			<div class="row">
				<div class="col-md-10">
					<div class="panel panel-default">
						<div class="panel-body">
							<table style="width: 100%;">
								<tr>
									<td style="width: 80%;vertical-align: top;">
										<h3>${paperQuestionEx.name }</h3>
										<h5>${paperQuestionEx.description }</h5>
									</td>
									<td style="width: 20%;vertical-align: top;text-align: center;">
										<a data-toggle="tooltip" title="修改章节" class="glyphicon glyphicon-pencil" onclick="toChapterEdit('${paperQuestionEx.id }');"></a>
										<a data-toggle="tooltip" title="删除章节" class="glyphicon glyphicon-trash" onclick="doChapterDel('${paperQuestionEx.id }');"></a>
										<a data-toggle="tooltip" title="添加试题" class="glyphicon glyphicon-plus" onclick="toQuestionAdd('${paper.id }', '${paperQuestionEx.id }');"></a>
										<a data-toggle="tooltip" title="清空试题" class="glyphicon glyphicon-trash" onclick="doQuestionClear('${paperQuestionEx.id }');"></a>
										<a data-toggle="tooltip" title="设置分数" class="glyphicon glyphicon-pencil" onclick="toScoresUpdate('${paperQuestionEx.id }');"></a>
										<c:if test="${!v.first }">
										<a data-toggle="tooltip" title="章节上移" class="glyphicon glyphicon-arrow-up" onclick="doChapterUp('${paperQuestionEx.id }');"></a>
										</c:if>
										<c:if test="${!v.last }">
										<a data-toggle="tooltip" title="章节下移" class="glyphicon glyphicon-arrow-down" onclick="doChapterDown('${paperQuestionEx.id }');"></a>
										</c:if>
										<a data-toggle="tooltip" title="章节展开" class="glyphicon glyphicon-triangle-bottom" onclick="doChapterCollapse(this);"></a>
									</td>
								</tr>
							</table>
						</div>
						<ul class="list-group collapse in">
							<c:set var="labs" value="${fn:split('A,B,C,D,E,F,G', ',')}"></c:set>
							<c:forEach var="subPaperQuestionEx" items="${paperQuestionEx.subList }" varStatus="v1">
							<li id="title_${subPaperQuestionEx.question.id }" class="list-group-item">
								<table style="width: 100%;">
									<tr>
										<td style="width: 1%;vertical-align: top;">
											${subPaperQuestionEx.no }。
										</td>
										<td style="width: 74%;vertical-align: top;">
											<div class="col-md-12">
												<div class="row">
													<div class="col-md-12">
														${subPaperQuestionEx.question.title }
													</div>
												</div>
												<div class="row">
													<div class="col-md-12">
														<%-- 单选题 --%>
														<c:if test="${subPaperQuestionEx.question.type == 1 }">
														<c:forEach var="lab" items="${labs }">
														<c:set var="ol" value="option${lab }"></c:set>
														<c:if test="${!empty subPaperQuestionEx.question[ol] }">
														<li class="question-option ${subPaperQuestionEx.question.answer == lab ? 'question-option-select' : '' }" 
																examUserQuestionId="${examUserQuestion.id}">
															<input type="radio" name="option_${subPaperQuestionEx.question.id}" value="${lab }" 
																	${subPaperQuestionEx.question.answer == lab ? 'checked="checked"' : '' }
																	disabled="disabled"/>
															${lab }：${subPaperQuestionEx.question[ol] }
														</li>
														</c:if>
														</c:forEach>
														</c:if>
														<%-- 多选题 --%>
														<c:if test="${subPaperQuestionEx.question.type == 2 }">
														<c:forEach var="lab" items="${labs }">
														<c:set var="ol" value="option${lab }"></c:set>
														<c:if test="${!empty subPaperQuestionEx.question[ol] }">
														<c:set var="op1" value=",${subPaperQuestionEx.question.answer},"></c:set>
														<li class="question-option ${fn:contains(op1, lab) ? 'question-option-select' : '' }" 
																examUserQuestionId="${examUserQuestion.id}" onclick="checkboxSubmit(this)">
															<input type="checkbox" name="option_${examUserQuestion.id}" value="${lab }"
																	${fn:contains(op1, lab) ?'checked="checked"' : '' }
																	disabled="disabled">
															${lab }：${subPaperQuestionEx.question[ol] }
														</li>
														</c:if>
														</c:forEach>
														</c:if>
														<%-- 填空题 --%>
														<c:if test="${subPaperQuestionEx.question.type == 3 }">
														<% pageContext.setAttribute("v", "\n"); %>
														<c:set var="lab1s" value="${fn:split('一,二,三,四,五,六,七', ',')}"></c:set>
														<c:set var="answers" value="${fn:split(subPaperQuestionEx.question.answer, v)}"></c:set>
														<c:set var="a" value="${examUserQuestion.answer }"></c:set>
														<%
														Object a = pageContext.getAttribute("a");
														if(a != null){
															pageContext.setAttribute("answers", a.toString().split("\n"));
														}
														%>
														<c:forEach var="answer" items="${fn:split(subPaperQuestionEx.question.answer, v) }" varStatus="s">
															<input type="text" name="option_${examUserQuestion.id}" value="${answers[s.index]}" 
																	class="fillblanks" style="width: 200px;height: 45px;" placeholder="填空${lab1s[s.index] }" 
																	examUserQuestionId="${examUserQuestion.id}" onblur="txtSubmit(this)" disabled="disabled">
														</c:forEach>
														</c:if>
														<%-- 判断题 --%>
														<c:if test="${subPaperQuestionEx.question.type == 4 }">
														<li class="question-option ${subPaperQuestionEx.question.answer == '对' ? 'question-option-select' : '' }" 
																examUserQuestionId="${examUserQuestion.id}" onclick="radioSubmit(this)">
															<input type="radio" name="option_${subPaperQuestionEx.question.id}" value="对"
																${subPaperQuestionEx.question.answer == "对" ? "checked='checked'" : "" }
																disabled="disabled">对
														</li>
														<li class="question-option ${subPaperQuestionEx.question.answer == '错' ? 'question-option-select' : '' }" 
																examUserQuestionId="${examUserQuestion.id}" onclick="radioSubmit(this)">
															<input type="radio" name="option_${subPaperQuestionEx.question.id}" value="错"
																${subPaperQuestionEx.question.answer == "错" ? "checked='checked'" : "" }
																disabled="disabled">错
														</li>
														</c:if>
														<%-- 问答题 --%>
														<c:if test="${subPaperQuestionEx.question.type == 5 }">
														<textarea name="option_${examUserQuestion.id}" style="width: 100%;height: 150px" 
															class="fillblanks" examUserQuestionId="${examUserQuestion.id}" onblur="txtSubmit(this)"+
															disabled="disabled">${subPaperQuestionEx.question.answer}</textarea>
														</c:if>
													</div>
												</div>
												<c:if test="${!empty subPaperQuestionEx.question.analysis }">
												<div class="row">
													<div class="col-md-12">
														【解析】：${subPaperQuestionEx.question.analysis }
													</div>
												</div>
												</c:if>
											</div>
										</td>
										<td style="width: 10%;vertical-align: top;">
											<form>
												<input type="hidden" name="paperQuestionId" value="${subPaperQuestionEx.id }" >
												<input type="text" name="score" value="${subPaperQuestionEx.score }" placeholder="0.00"
													style="width: 40px;border:0; border-bottom: 1px solid #d8d8d8; text-align: center;">分
												<small class="help-block"></small>
												<c:if test="${subPaperQuestionEx.question.type == 2 }">
												<div class="checkbox">
													<label>
														<input type="checkbox" name="options" value="1"
															data-toggle="tooltip" title="默认全对得分"
															${fn:contains(subPaperQuestionEx.options, "1") ? "checked='checked'" : "" }>半对半分
													</label>
												</div>
												</c:if>
												<c:if test="${subPaperQuestionEx.question.type == 3 }">
												<div class="checkbox">
													<label>
														<input type="checkbox" name="options" value="1" 
															data-toggle="tooltip" title="默认全对得分" data-placement="left"
															${fn:contains(subPaperQuestionEx.options, "1") ? "checked='checked'" : "" }>半对半分
													</label>
												</div>
												<div class="checkbox">
													<label>
														<input type="checkbox" name="options" value="2"
															data-toggle="tooltip" title="默认答案有前后顺序" data-placement="left"
															${fn:contains(subPaperQuestionEx.options, "2") ? "checked='checked'" : "" }>答案无顺序
													</label>
												</div>
												<div class="checkbox">
													<label>
														<input type="checkbox" name="options" value="3"
															data-toggle="tooltip" title="默认大小写敏感" data-placement="left"
															${fn:contains(subPaperQuestionEx.options, "3") ? "checked='checked'" : "" }>大小写不敏感
													</label>
												</div>
												<div class="checkbox">
													<label>
														<input type="checkbox" name="options" value="4"
															data-toggle="tooltip" title="默认等于答案得分" data-placement="left"
															${fn:contains(subPaperQuestionEx.options, "4") ? "checked='checked'" : "" }>用户答案包含试题答案
													</label>
												</div>
												</c:if>
											</form>
										</td>
										<td style="width: 15%;vertical-align: top;text-align: center;">
											<a data-toggle="tooltip" title="保存得分" class="glyphicon glyphicon-ok" onclick="doScoreUpdate(this);"></a>
											<c:if test="${!v1.first }">
											<a data-toggle="tooltip" title="上移" class="glyphicon glyphicon-arrow-up" onclick="doQuestionUp('${subPaperQuestionEx.id }')"></a>
											</c:if>
											<c:if test="${!v1.last }">
											<a data-toggle="tooltip" title="下移" class="glyphicon glyphicon-arrow-down" onclick="doQuestionDown('${subPaperQuestionEx.id }')"></a>
											</c:if>
											<!-- <a data-toggle="tooltip" title="展开" class="glyphicon glyphicon-triangle-bottom"></a> -->
											<a data-toggle="tooltip" title="删除" class="glyphicon glyphicon-trash" onclick="doQuestionDel('${subPaperQuestionEx.id }')"></a>
										</td>
									</tr>
								</table>
							</li>
							</c:forEach>
						</ul>
					</div>
				</div>
				<div class="col-md-2">
					<div class="answer-card">
						<div class="answer-card-title">
							<h1>答题卡</h1>
							<p id="remainingTime"></p>
						</div>
						<c:forEach var="paperQuestionEx" items="${paperQuestionExList }" varStatus="v">
							<div class="answer-card-content">
							<div class="answer-card-content-tittle">
								<div style="width: 150px;float: left;text-align: center;font-weight: bold;padding-top: 10px;">${paperQuestionEx.name }</div>
								<div style="width: 100px;float: right;text-align: center;padding-top: 10px;">共${fn:length(paperQuestionEx.subList)}题</div>
							</div>
							<ul>
								<c:forEach var="subPaperQuestionEx" items="${paperQuestionEx.subList }" varStatus="v1">
								<c:set var="examUserQuestion" value="${examUserQuestionMap[subPaperQuestionEx.questionId + 0]}"></c:set>
								<li><a id="an_card_${subPaperQuestionEx.question.id }" href="${basePath }home/paper/toCfg?id=${id }#title_${subPaperQuestionEx.question.id }" 
										class="answer-card-yes"
										>${subPaperQuestionEx.no }</a></li>
								</c:forEach>
							</ul>
						</div>
						</c:forEach>
						<div class="answer-card-content">
							<div class="answer-card-content-tittle">
								
							</div>
						</div>
					</div>
				</div>
			</div>
			</c:forEach>
		</div>
	</body>
	<script type="text/javascript">
		//定义变量
		var $id = $("#id");
		var $minKOption = {
				uploadJson : "home/paper/doTempUpload",
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
	
		$(function(){
			iniTtooltip();
			
		});
		
		//初始化提示工具
		function iniTtooltip(){
			$("[data-toggle='tooltip']").tooltip();
		}
		
		//到达添加章节页面
		function toChapterAdd(){
			BootstrapDialog.show({
				title : "添加章节",
				message : function(dialog) {
					var $message = $("<div></div>");
					var pageToLoad = dialog.getData("pageToLoad");
					$message.load(pageToLoad);
					return $message;
				},
				data : {
					"pageToLoad" : "home/paper/toChapterAdd?id=" + $id.val()
				},
				buttons : [{
					label : "&nbsp;确定",
					icon : "glyphicon glyphicon-ok",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						var $editForm = $("#editForm");
						var bv = $("#editForm").data('bootstrapValidator');
						if(!bv.isValid()){
							return;
						}
						
						$.ajax({
							url : "home/paper/doChapterAdd",
							data : $editForm.serialize(),
							success : function(obj) {
								if (!obj.succ) {
									BootstrapDialog.show({
										title : "提示消息",
										message : obj.msg,
										buttons : [{
											label : "&nbsp;确定",
											icon : "glyphicon glyphicon-ok",
											cssClass : "btn-primary",
											action : function(dialogItself) {
												dialogItself.close();
											}
										}]
									});
									return;
								}
								
								window.location.reload();
							}
						});
					}
				}, {
					label : "&nbsp;取消",
					icon : "glyphicon glyphicon-remove",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						dialogItself.close();
					}
				}],
				onshown : function(){
					KindEditor.create("#description", $minKOption);
					$("#editForm").bootstrapValidator({
						excluded : [":disabled"],
						fields : {
							name : {
								validators : {
									notEmpty : {}
								}
							}
						}
					});
				}
			});
		}
		
		//到达修改章节页面
		function toChapterEdit(chapterId){
			BootstrapDialog.show({
				title : "修改章节",
				message : function(dialog) {
					var $message = $("<div></div>");
					var pageToLoad = dialog.getData("pageToLoad");
					$message.load(pageToLoad);
					return $message;
				},
				data : {
					"pageToLoad" : "home/paper/toChapterEdit?chapterId=" + chapterId
				},
				buttons : [{
					label : "&nbsp;确定",
					icon : "glyphicon glyphicon-ok",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						var $editForm = $("#editForm");
						var bv = $("#editForm").data('bootstrapValidator');
						if(!bv.isValid()){
							return;
						}
						
						$.ajax({
							url : "home/paper/doChapterEdit",
							data : $editForm.serialize(),
							success : function(obj) {
								if (!obj.succ) {
									BootstrapDialog.show({
										title : "提示消息",
										message : obj.msg,
										buttons : [{
											label : "&nbsp;确定",
											icon : "glyphicon glyphicon-ok",
											action : function(dialogItself) {
												dialogItself.close();
											}
										}]
									});
									return;
								}
								
								window.location.reload();
							}
						});
					}
				}, {
					label : "&nbsp;取消",
					icon : "glyphicon glyphicon-remove",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						dialogItself.close();
					}
				}],
				onshown : function(){
					KindEditor.create("#description", $minKOption);
					$("#editForm").bootstrapValidator({
						excluded : [":disabled"],
						fields : {
							name : {
								trigger : "change",
								validators : {
									notEmpty : {}
								}
							}
						}
					});
					$("#chapterName").change();
				}
			});
		}
		
		//完成删除章节
		function doChapterDel(chapterId){
			BootstrapDialog.show({
				title : "提示消息",
				message : "确定删除？",
				buttons : [{
					label : "&nbsp;确定",
					icon : "glyphicon glyphicon-ok",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						$.ajax({
							url : "home/paper/doChapterDel",
							data : {chapterId : chapterId},
							success : function(obj) {
								if (!obj.succ) {
									BootstrapDialog.show({
										title : "提示消息",
										message : obj.msg,
										buttons : [{
											label : "&nbsp;确定",
											icon : "glyphicon glyphicon-ok",
											cssClass : "btn-primary",
											action : function(dialogItself) {
												dialogItself.close();
											}
										}]
									});
									return;
								}
								
								window.location.reload();
							}
						});
					}
				}, {
					label : "&nbsp;取消",
					icon : "glyphicon glyphicon-remove",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						dialogItself.close();
					}
				}]
			});
		}
		
		//完成章节展开
		function doChapterCollapse(obj){
			var ul = $(obj).parent().parent().parent().parent().parent().next()
			ul.collapse("toggle");
		}
		
		//到达添加试题页面
		function toQuestionAdd(id, chapterId){
			BootstrapDialog.show({
				title : "添加试题",
				cssClass : "exam-list",
				message : function(dialog) {
					var $message = $("<div></div>");
					var pageToLoad = dialog.getData("pageToLoad");
					$message.load(pageToLoad);
					return $message;
				},
				data : {
					"pageToLoad" : "home/paper/toQuestionAdd?id=" + id
				},
				buttons : [{
					label : "&nbsp;确定",
					icon : "glyphicon glyphicon-ok",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						var nodes = $("#questionTable").bootstrapTable("getSelections");
						if(nodes.length == 0){
							BootstrapDialog.show({
								title : "提示消息",
								message : "请选择一行或多行数据！",
								buttons : [{
									label : "&nbsp;确定",
									icon : "glyphicon glyphicon-ok",
									cssClass : "btn-primary",
									action : function(dialogItself) {
										dialogItself.close();
									}
								}]
							});
							return;
						}
						
						$.ajax({
							url : "home/paper/doQuestionAdd",
							data : "chapterId=" + chapterId + "&" + $.fn.my.serializeField(nodes, {"attrName" : "questionIds"}),
							success : function(obj) {
								if (!obj.succ) {
									BootstrapDialog.show({
										title : "提示消息",
										message : obj.msg,
										buttons : [{
											label : "&nbsp;确定",
											icon : "glyphicon glyphicon-ok",
											cssClass : "btn-primary",
											action : function(dialogItself) {
												dialogItself.close();
											}
										}]
									});
									return;
								}
								
								window.location.reload();
							}
						});
					}
				}, {
					label : "&nbsp;取消",
					icon : "glyphicon glyphicon-remove",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						dialogItself.close();
					}
				}],
				onshown : function(){
					var $questionTypeTree = $("#questionTypeTree");
					var $questionTable = $("#questionTable");
					var $questionQueryForm = $("#questionQueryForm");
					var $questionOne = $("#questionOne");
					var $questionTable = $("#questionTable");
					$.ajax({
						url : "home/paper/questionTypeTreeList",
						success : function(arr) {
							$questionTypeTree.treeview({
								showBorder: false,
								expandIcon: "glyphicon glyphicon-chevron-right",
								collapseIcon: "glyphicon glyphicon-chevron-down",
								nodeIcon: "glyphicon glyphicon-bookmark",
								//color: "#428BCA",
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
									$questionOne.val(data.ID);
									questionQuery();
								},
								onNodeUnselected : function(event, data) {
									$questionOne.val("");
									questionQuery();
								}
							});
						}
					});
					$questionTable.bootstrapTable({
						url : "home/paper/questionList",
						queryParams : function(params){
							var customeParams = $.fn.my.serializeObj($questionQueryForm);
							customeParams.page = this.pageNumber;
							customeParams.rows = this.pageSize;
							return customeParams;
						},
						columns : [
									{field : "state", checkbox : true}, 
									{field : "CODE", title : "编号", width : 50, align : "center"},
									{field : "TITLE", title : "题干 ", width : 300, align : "center"},
									{field : "TYPE_NAME", title : "类型", width : 80, align : "center"},
									{field : "DIFFICULTY_NAME", title : "难度", width : 80, align : "center"},
									{field : "STATE_NAME", title : "状态 ", width : 80, align : "center"},
									{field : "QUESTION_TYPE_NAME", title : "分类 ", width : 80, align : "center"}
									]
					});
				}
			});
		}
		
		//试题查询
		function questionQuery(){
			$("#questionTable").bootstrapTable('refresh', {pageNumber : 1});
		}
		
		//试题重置
		function questionReset(){
			$("#questionQueryForm")[0].reset();
			questionQuery();
		}
		
		//完成试题清空
		function doQuestionClear(chapterId){
			BootstrapDialog.show({
				title : "提示消息",
				message : "确定清空？",
				buttons : [{
					label : "&nbsp;确定",
					icon : "glyphicon glyphicon-ok",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						$.ajax({
							url : "home/paper/doQuestionClear",
							data : {chapterId : chapterId},
							success : function(obj) {
								if (!obj.succ) {
									BootstrapDialog.show({
										title : "提示消息",
										message : obj.msg,
										buttons : [{
											label : "&nbsp;确定",
											icon : "glyphicon glyphicon-ok",
											cssClass : "btn-primary",
											action : function(dialogItself) {
												dialogItself.close();
											}
										}]
									});
									return;
								}
								
								window.location.reload();
							}
						});
					}
				}, {
					label : "&nbsp;取消",
					icon : "glyphicon glyphicon-remove",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						dialogItself.close();
					}
				}]
			});
		}
		
		//到达设置分数页面
		function toScoresUpdate(chapterId){
			BootstrapDialog.show({
				title : "设置分数",
				message : function(dialog) {
					var $message = $("<div></div>");
					var pageToLoad = dialog.getData("pageToLoad");
					$message.load(pageToLoad);
					return $message;
				},
				data : {
					"pageToLoad" : "home/paper/toScoresUpdate?chapterId=" + chapterId
				},
				buttons : [{
					label : "&nbsp;确定",
					icon : "glyphicon glyphicon-ok",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						var $editForm = $("#editForm");
						var bv = $("#editForm").data('bootstrapValidator');
						if(!bv.isValid()){
							return;
						}
						
						$.ajax({
							url : "home/paper/doScoresUpdate",
							data : $editForm.serialize(),
							success : function(obj) {
								if (!obj.succ) {
									BootstrapDialog.show({
										title : "提示消息",
										message : obj.msg,
										buttons : [{
											label : "&nbsp;确定",
											icon : "glyphicon glyphicon-ok",
											cssClass : "btn-primary",
											action : function(dialogItself) {
												dialogItself.close();
											}
										}]
									});
									return;
								}
								
								window.location.reload();
							}
						});
					}
				}, {
					label : "&nbsp;取消",
					icon : "glyphicon glyphicon-remove",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						dialogItself.close();
					}
				}],
				onshown : function(){
					$("#editForm").bootstrapValidator({
						excluded : [":disabled"],
						fields : {
							score : {
								trigger : "change",
								validators : {
									notEmpty : {},
									regexp : {
										regexp : /^(([1-9][0-9]*)|(([0]\.\d{1,2}|[1-9][0-9]*\.\d{1,2})))$/,
										message : "必须为正数，最多保留两位小数"
									}
								}
							}
						}
					});
					
					var editForm = $("#editForm");
					editForm.find("input[name='score']").val("1.00").change();
					editForm.find("[data-toggle='tooltip']").tooltip();
				}
			});
		}
		
		//完成章节上移
		function doChapterUp(chapterId){
			$.ajax({
				url : "home/paper/doChapterUp",
				data : {
					chapterId : chapterId
				},
				success : function(obj) {
					if (!obj.succ) {
						BootstrapDialog.show({
							title : "提示消息",
							message : obj.msg,
							buttons : [{
								label : "&nbsp;确定",
								icon : "glyphicon glyphicon-ok",
								cssClass : "btn-primary",
								action : function(dialogItself) {
									dialogItself.close();
								}
							}]
						});
						return;
					}
					
					window.location.reload();
				}
			});
		}
		
		//完成章节下移
		function doChapterDown(chapterId){
			$.ajax({
				url : "home/paper/doChapterDown",
				data : {
					chapterId : chapterId
				},
				success : function(obj) {
					if (!obj.succ) {
						BootstrapDialog.show({
							title : "提示消息",
							message : obj.msg,
							buttons : [{
								label : "&nbsp;确定",
								icon : "glyphicon glyphicon-ok",
								cssClass : "btn-primary",
								action : function(dialogItself) {
									dialogItself.close();
								}
							}]
						});
						return;
					}
					
					window.location.reload();
				}
			});
		}
		
		//完成设置分数
		function doScoreUpdate(a){
			var $form = $(a).parent().prev().children();
			var $score = $form.find("input[name='score']");
			var $small = $form.find("small");
			$score.removeClass('field-error');
			$small.html("");
			
			var score = $score.val();
			var patt  = /^(([1-9][0-9]*)|(([0]\.\d{1,2}|[1-9][0-9]*\.\d{1,2})))$/;
			if(!patt.test(score)){
				$score.addClass("field-error");
				$small.html("必须为正数，最多保留两位小数");
				return;
			}
			
			$.ajax({
				url : "home/paper/doScoreUpdate",
				data : $form.serialize(),
				success : function(obj) {
					BootstrapDialog.show({
						title : "提示消息",
						message : obj.msg,
						buttons : [{
							label : "&nbsp;确定",
							icon : "glyphicon glyphicon-ok",
							cssClass : "btn-primary",
							action : function(dialogItself) {
								dialogItself.close();
							}
						}]
					});
				}
			});
		}
		
		//完成试题上移
		function doQuestionUp(paperQuestionId){
			$.ajax({
				url : "home/paper/doQuestionUp",
				data : {
					paperQuestionId : paperQuestionId
				},
				success : function(obj) {
					if (!obj.succ) {
						BootstrapDialog.show({
							title : "提示消息",
							message : obj.msg,
							buttons : [{
								label : "&nbsp;确定",
								icon : "glyphicon glyphicon-ok",
								cssClass : "btn-primary",
								action : function(dialogItself) {
									dialogItself.close();
								}
							}]
						});
						return;
					}
					
					window.location.reload();
				}
			});
		}
		
		//完成试题下移
		function doQuestionDown(paperQuestionId){
			$.ajax({
				url : "home/paper/doQuestionDown",
				data : {
					paperQuestionId : paperQuestionId
				},
				success : function(obj) {
					if (!obj.succ) {
						BootstrapDialog.show({
							title : "提示消息",
							message : obj.msg,
							buttons : [{
								label : "&nbsp;确定",
								icon : "glyphicon glyphicon-ok",
								cssClass : "btn-primary",
								action : function(dialogItself) {
									dialogItself.close();
								}
							}]
						});
						return;
					}
					
					window.location.reload();
				}
			});
		}
		
		//完成试题删除
		function doQuestionDel(paperQuestionId){
			BootstrapDialog.show({
				title : "提示消息",
				message : "确定删除？",
				buttons : [{
					label : "&nbsp;确定",
					icon : "glyphicon glyphicon-ok",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						$.ajax({
							url : "home/paper/doQuestionDel",
							data : {
								paperQuestionId : paperQuestionId
							},
							success : function(obj) {
								if (!obj.succ) {
									BootstrapDialog.show({
										title : "提示消息",
										message : obj.msg,
										buttons : [{
											label : "&nbsp;确定",
											icon : "glyphicon glyphicon-ok",
											cssClass : "btn-primary",
											action : function(dialogItself) {
												dialogItself.close();
											}
										}]
									});
									return;
								}
								
								window.location.reload();
							}
						});
					}
				}, {
					label : "&nbsp;取消",
					icon : "glyphicon glyphicon-remove",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						dialogItself.close();
					}
				}]
			});
		}
	</script>
</html>