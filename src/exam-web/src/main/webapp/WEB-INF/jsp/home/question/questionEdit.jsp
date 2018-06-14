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
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initQuestionTypeTree();
			KindEditor.ready(function(K) {
				questionTitleEditor = K.create("#question_title", {
					uploadJson : "home/question/doTempUpload",
					filePostName : "files",
					width : "100%",
					minHeight : 50,
					items : [
				 		'source', 'wordpaste', '|', 'justifyleft', 'justifycenter', 'justifyright', 'insertorderedlist', 'insertunorderedlist', 
				 		'indent', 'outdent', 'subscript', 'superscript', '|', 'formatblock', 'fontname', 'fontsize', '|', 
				 		'forecolor', 'hilitecolor', 'bold', 'italic', 'underline', 'strikethrough', 'lineheight', 'removeformat', 
				 		'/', 'image', 'media', 'table', 'hr', 'emoticons', 'baidumap', 'pagebreak', 'anchor', 'link', 
				 		'unlink', '|', 'fullscreen' 
				 	]
				});
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
	</script>
</html>