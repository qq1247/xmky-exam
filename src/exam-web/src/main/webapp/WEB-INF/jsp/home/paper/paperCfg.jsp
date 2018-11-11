<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
	<head>
		<title>配置试卷</title>
		<%@include file="/script/home/common.jspf"%>
	</head>
	<body>
		<%@include file="/script/home/head.jspf"%>
		<div class="container paper">
			<div class="row">
				<div class="col-md-12">
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
				<div class="col-md-12">
					<button type="button" class="btn btn-primary" onclick="toChapterAdd();">
						<span class="glyphicon glyphicon-plus"></span>
						&nbsp;添加章节
					</button>
					<!-- <button type="button" class="btn btn-primary" onclick="toChapterAdd();">
						<span class="glyphicon glyphicon-zoom-in"></span>
						&nbsp;预览试卷
					</button> -->
					<button type="button" class="btn btn-primary" onclick="javascript:history.back(-1);">
						<span class="glyphicon glyphicon-arrow-left"></span>
						&nbsp;返回
					</button>
				</div>
			</div>
			<c:forEach var="pqEx" items="${paperQuestionExList }" varStatus="v">
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">
						<div class="panel-body">
							<table style="width: 100%;">
								<tr>
									<td style="width: 80%;vertical-align: top;">
										<h3>${pqEx.name }</h3>
										<h5>${pqEx.description }</h5>
									</td>
									<td style="width: 20%;vertical-align: top;text-align: center;">
										<a data-toggle="tooltip" title="修改章节" class="glyphicon glyphicon-pencil" onclick="toChapterEdit('${pqEx.id }');"></a>
										<a data-toggle="tooltip" title="删除章节" class="glyphicon glyphicon-trash" onclick="doChapterDel('${pqEx.id }');"></a>
										<a data-toggle="tooltip" title="添加试题" class="glyphicon glyphicon-plus" onclick="toQuestionAdd('${paper.id }', '${pqEx.id }');"></a>
										<a data-toggle="tooltip" title="清空试题" class="glyphicon glyphicon-trash" onclick="doQuestionClear('${pqEx.id }');"></a>
										<a data-toggle="tooltip" title="设置分数" class="glyphicon glyphicon-pencil" onclick="toScoresUpdate('${pqEx.id }');"></a>
										<c:if test="${!v.first }">
										<a data-toggle="tooltip" title="章节上移" class="glyphicon glyphicon-arrow-up" onclick="doChapterUp('${pqEx.id }');"></a>
										</c:if>
										<c:if test="${!v.last }">
										<a data-toggle="tooltip" title="章节下移" class="glyphicon glyphicon-arrow-down" onclick="doChapterDown('${pqEx.id }');"></a>
										</c:if>
										<a data-toggle="tooltip" title="章节展开" class="glyphicon glyphicon-triangle-bottom" onclick="doChapterCollapse(this);"></a>
									</td>
								</tr>
							</table>
						</div>
						<ul class="list-group collapse in">
							<c:set var="labs" value="${fn:split('A,B,C,D,E,F,G', ',')}"></c:set>
							<c:forEach var="subPqEx" items="${pqEx.subList }" varStatus="v1">
							<li class="list-group-item">
								<table style="width: 100%;">
									<tr>
										<td style="width: 1%;vertical-align: top;">
											${subPqEx.no }。
										</td>
										<td style="width: 74%;vertical-align: top;">
											<div class="col-md-12">
												<div class="row">
													<div class="col-md-12">
														${subPqEx.question.title }
													</div>
												</div>
												<div class="row">
													<div class="col-md-12">
														<%-- 单选题 --%>
														<c:if test="${subPqEx.question.type == 1 }">
														<c:forEach var="lab" items="${labs }">
														<c:set var="ol" value="option${lab }"></c:set>
														<c:if test="${!empty subPqEx.question[ol] }">
														<div class="radio">
															<label>
																<input type="radio" name="qst${subPqEx.question.id }">${lab }：${subPqEx.question[ol] }
															</label>
														</div>
														</c:if>
														</c:forEach>
														</c:if>
														<%-- 多选题 --%>
														<c:if test="${subPqEx.question.type == 2 }">
														<c:forEach var="lab" items="${labs }">
														<c:set var="ol" value="option${lab }"></c:set>
														<c:if test="${!empty subPqEx.question[ol] }">
														<div class="checkbox">
															<label>
																<input type="checkbox" >${lab }：${subPqEx.question[ol] }
															</label>
														</div>
														</c:if>
														</c:forEach>
														</c:if>
													</div>
												</div>
												<div class="row">
													<div class="col-md-12">
														【答案】：
														<% pageContext.setAttribute("sep", "\n"); %>
														<c:if test="${subPqEx.question.type != 3 }">
														${subPqEx.question.answer }
														</c:if>
														<c:if test="${subPqEx.question.type == 3 }">
														<c:forEach var="answer" items="${fn:split(subPqEx.question.answer, sep) }" varStatus="s3">
														<c:if test="${s3.index > 0 }">；</c:if>
														${answer }
														</c:forEach>
														</c:if>
													</div>
												</div>
												<div class="row">
													<div class="col-md-12">
														【解析】：${subPqEx.question.analysis }
													</div>
												</div>
											</div>
										</td>
										<td style="width: 10%;vertical-align: top;">
											<form>
												<input type="hidden" name="paperQuestionId" value="${subPqEx.id }" >
												<input type="text" name="score" value="${subPqEx.score }" placeholder="0.00"
													style="width: 40px;border:0; border-bottom: 1px solid #d8d8d8; text-align: center;">分
												<small class="help-block"></small>
												<c:if test="${subPqEx.question.type == 2 }">
												<div class="checkbox">
													<label>
														<input type="checkbox" name="options" value="1"
															data-toggle="tooltip" title="默认全对得分"
															${fn:contains(subPqEx.options, "1") ? "checked='checked'" : "" }>半对半分
													</label>
												</div>
												</c:if>
												<c:if test="${subPqEx.question.type == 3 }">
												<div class="checkbox">
													<label>
														<input type="checkbox" name="options" value="1" 
															data-toggle="tooltip" title="默认全对得分" data-placement="left"
															${fn:contains(subPqEx.options, "1") ? "checked='checked'" : "" }>半对半分
													</label>
												</div>
												<div class="checkbox">
													<label>
														<input type="checkbox" name="options" value="2"
															data-toggle="tooltip" title="默认答案有前后顺序" data-placement="left"
															${fn:contains(subPqEx.options, "2") ? "checked='checked'" : "" }>答案无顺序
													</label>
												</div>
												<div class="checkbox">
													<label>
														<input type="checkbox" name="options" value="3"
															data-toggle="tooltip" title="默认大小写敏感" data-placement="left"
															${fn:contains(subPqEx.options, "3") ? "checked='checked'" : "" }>大小写不敏感
													</label>
												</div>
												<div class="checkbox">
													<label>
														<input type="checkbox" name="options" value="4"
															data-toggle="tooltip" title="默认等于答案得分" data-placement="left"
															${fn:contains(subPqEx.options, "4") ? "checked='checked'" : "" }>包含答案得分
													</label>
												</div>
												</c:if>
											</form>
										</td>
										<td style="width: 15%;vertical-align: top;text-align: center;">
											<a data-toggle="tooltip" title="保存得分" class="glyphicon glyphicon-ok" onclick="doScoreUpdate(this);"></a>
											<c:if test="${!v1.first }">
											<a data-toggle="tooltip" title="上移" class="glyphicon glyphicon-arrow-up" onclick="doQuestionUp('${subPqEx.id }')"></a>
											</c:if>
											<c:if test="${!v1.last }">
											<a data-toggle="tooltip" title="下移" class="glyphicon glyphicon-arrow-down" onclick="doQuestionDown('${subPqEx.id }')"></a>
											</c:if>
											<!-- <a data-toggle="tooltip" title="展开" class="glyphicon glyphicon-triangle-bottom"></a> -->
											<a data-toggle="tooltip" title="删除" class="glyphicon glyphicon-trash" onclick="doQuestionDel('${subPqEx.id }')"></a>
										</td>
									</tr>
								</table>
							</li>
							</c:forEach>
						</ul>
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
								if (!obj.success) {
									BootstrapDialog.show({
										title : "提示消息",
										message : obj.message,
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
								if (!obj.success) {
									BootstrapDialog.show({
										title : "提示消息",
										message : obj.message,
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
								validators : {
									notEmpty : {}
								}
							}
						}
					});
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
								if (!obj.success) {
									BootstrapDialog.show({
										title : "提示消息",
										message : obj.message,
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
								if (!obj.success) {
									BootstrapDialog.show({
										title : "提示消息",
										message : obj.message,
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
								if (!obj.success) {
									BootstrapDialog.show({
										title : "提示消息",
										message : obj.message,
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
								if (!obj.success) {
									BootstrapDialog.show({
										title : "提示消息",
										message : obj.message,
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
					if (!obj.success) {
						BootstrapDialog.show({
							title : "提示消息",
							message : obj.message,
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
					if (!obj.success) {
						BootstrapDialog.show({
							title : "提示消息",
							message : obj.message,
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
						message : obj.message,
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
					if (!obj.success) {
						BootstrapDialog.show({
							title : "提示消息",
							message : obj.message,
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
					if (!obj.success) {
						BootstrapDialog.show({
							title : "提示消息",
							message : obj.message,
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
								if (!obj.success) {
									BootstrapDialog.show({
										title : "提示消息",
										message : obj.message,
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