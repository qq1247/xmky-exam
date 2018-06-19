<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>试题列表</title>
		<%@include file="/script/home/common.jspf"%>
	</head>
	<body>
		<%@include file="/script/home/head.jspf"%>
		<div class="examlist">
			<div class="container">
				<div class="row">
					<div class="col-md-3">
						<div id="questionTypeTree" class="tree"></div>
					</div>
					<div class="col-md-9">
						<div class="panel panel-default">
    						<div class="panel-body">
    							<form class="form-horizontal" role="form">
									<div class="row">
										<div class="col-md-6">
											<div class="form-group">
												<label for="question_questionTypeName" class="col-md-4 control-label">试题分类：</label>
												<div class="col-md-8">
													<input type="hidden" id="question_questionTypeId" name="questionTypeId" value="${questionType.id }" />
													<input type="text" id="question_questionTypeName" name="questionTypeName" value="${question.questionTypeName }" 
														class="form-control" readonly="readonly" placeholder="请选择左侧试题分类">
												</div>
											</div>
										</div>
										<div class="col-md-6">
											<div class="form-group">
												<label for="question_state" class="col-md-4 control-label">状态：</label>
												<div class="col-md-8">
													<select id="question_state" name="state" class="form-control">
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
												<label for="question_type" class="col-md-4 control-label">类型：</label>
												<div class="col-md-8">
													<select id="question_type" name="type" class="form-control">
														<c:forEach var="dict" items="${QUESTION_TYPE_DICT }">
														<option value="${dict.dictKey }" ${dict.dictKey == question.type ? "selected" : ""}>${dict.dictValue }</option>
														</c:forEach>
													</select>
												</div>
											</div>
										</div>
										<div class="col-md-6">
											<div class="form-group">
												<label for="question_difficulty" class="col-md-4 control-label">难度：</label>
												<div class="col-md-8">
													<select id="question_difficulty" name="difficulty" class="form-control">
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
												<label for="question_type" class="col-md-2 control-label">题干：</label>
												<div class="col-md-10">
													<textarea id="question_title" name="title">${question.title }</textarea>
												</div>
											</div>
										</div>
									</div>
									<div class="row">
										<div class="col-md-12">
											<div class="form-group">
												<label class="col-md-2 control-label">选项：</label>
												<div class="col-md-10">
													<div class="panel panel-default">
														<div id="optionPanel" class="panel-body">
															
														</div>
													</div>
												</div>
											</div>
										</div>
									</div>
									<div class="row">
										<div class="col-md-12">
											<div class="form-group">
												<button type="button" class="btn btn-primary btn-lg btn-block">返回</button>
												<button type="button" class="btn btn-primary btn-lg btn-block">提交</button>
											</div>
										</div>
									</div>
								</form>
    						</div>
    					</div>
					</div>
				</div>
			</div>
		</div>
</body>
	<script type="text/javascript">
		//定义变量
		var questionTypeTree = $("#questionTypeTree");
		var questionTypeId = "${questionTypeId}";
		var questionQuestionTypeId = $("#questionQuestionTypeId");
		var questionQuestionTypeName = $("#questionQuestionTypeName");
		var questionTitleEditor = "";
		var optionPanel = $("#optionPanel");
		var optionValue = ["A", "B", "C", "D", "E", "F", "G"];
		var kindEditorOption = {
				uploadJson : "home/question/doTempUpload",
				filePostName : "files",
				width : "100%",
				minHeight : 50,
				items : [
					 		'source', 'wordpaste', '|', 'justifyleft', 'justifycenter', 'justifyright', 'insertorderedlist', 'insertunorderedlist', 
					 		'indent', 'outdent'
					 	]
			}
		
		//页面加载完毕，执行如下方法：
		$(function() {addOption();addOption();
			initQuestionTypeTree();
			KindEditor.ready(function(K) {
				questionTitleEditor = K.create("#question_title", kindEditorOption);
			});
		});
		
		//初始化试题分类树
		function initQuestionTypeTree(){
			$.ajax({
				url : "home/question/questionTypeTreeList",
				success : function(arr) {
					questionTypeTree.treeview({
						showBorder: false,
						expandIcon: "glyphicon glyphicon-chevron-right",
						collapseIcon: "glyphicon glyphicon-chevron-down",
						nodeIcon: "glyphicon glyphicon-bookmark",
						color: "#605F5F",
						showTags: true, 
						levels: 3,
						data: generateTree(arr, {
							idFiled : "ID",
							textFiled : "NAME", 
							parentField : "PARENT_ID",
							disabledFiled : "DISABLED",
							expandedFiled : "EXPANDED"
						}),
						onNodeSelected : function(event, data) {
							questionQuestionTypeId.val(data.ID);
							questionQuestionTypeName.val(data.NAME);
						}
					});

					if(questionTypeId){
						questionTypeTree.treeview("selectNode", [ parseInt(questionTypeId)]);
					}
				}
			});
		}
		
		//添加选项
		function addOption(curObj){
			var html = [];
			html.push('<div class="row">');
			html.push('	<div class="col-md-12">');
			html.push('		<div class="form-group">');
			html.push('			<div class="col-md-1">');
			html.push('				'+optionValue[i]+'：');
			html.push('			</div>');
			html.push('			<div class="col-md-10" id="d1">');
			html.push('				<textarea id="question_option'+optionValue[i]+'" name="option'+optionValue[i]+'"></textarea>');
			html.push('			</div>');
			html.push('			<div class="col-md-1">');
			html.push('				<a href="javascript:void(0);" onclick="addOption(this)"><span class="glyphicon glyphicon-plus"></span></a>');
			html.push('				<a href="javascript:void(0);" onclick="delOption(this)"><span class="glyphicon glyphicon-trash"></span></a>');
			html.push('				<a href="javascript:void(0);"><span class="glyphicon glyphicon-arrow-up"></span></a>');
			html.push('				<a href="javascript:void(0);"><span class="glyphicon glyphicon-arrow-down"></span></a>');
			html.push('			</div>');
			html.push('		</div>');
			html.push('	</div>');
			html.push('</div>');
			
			var row = $(curObj).parent().parent().parent().parent();
			row.after(html.join(""));
			
			
			
			
				
				
				
				optionPanel.append(html.join(""));
				KindEditor.ready(function(K) {
					K.create("#question_option" + optionValue[i], kindEditorOption);
				});
				
				return;
			}
		}
		
		//删除选项
		function delOption(){
			optionPanel.children(".row").last().remove();
		}
		
		//上移选项
		function upOption(curObj){
			
		}
		
		//下移选项
		function downOption(curObj){
			
		}
	</script>
</html>