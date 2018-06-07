<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>试题列表</title>
		<%@include file="/script/home/common.jspf"%>
		<script type="text/javascript" src="script/ckeditor/ckeditor.js"></script>
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
		var questionTypeId = ${questionTypeId};
		var question_questionTypeId = $("#question_questionTypeId");
		var question_questionTypeName = $("#question_questionTypeName");
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
			initQuestionTypeTree();
			CKEDITOR.replace("question_title", {
				height: "80px",
	        	toolbar : ckToolbar
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
							question_questionTypeId.val(data.ID);
							question_questionTypeName.val(data.NAME);
						}
					});

					questionTypeTree.treeview("selectNode", [ questionTypeId]);
				}
			});
		}
	</script>
</html>