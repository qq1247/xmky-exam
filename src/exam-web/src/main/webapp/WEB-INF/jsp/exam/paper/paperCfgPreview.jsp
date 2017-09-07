<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>试卷预览</title>
		<%
		String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/"; 
		%>
		<base href="<%=basePath%>">
		
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=Edge">
		<meta http-equiv="Expires" content="0">
		<meta http-equiv="Pragma" content="no-cache">
		<meta http-equiv="Cache-control" content="no-cache">
		<meta http-equiv="Cache" content="no-cache">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		
		<link href="script/home/css/bootstrap.min.css" rel="stylesheet">
		<link href="script/home/css/font-awesome.min.css" rel="stylesheet">
		<link href="script/home/css/animate.min.css" rel="stylesheet">
		<link href="script/home/css/prettyPhoto.css" rel="stylesheet">
		<link href="script/home/css/main.css" rel="stylesheet">
		<link href="script/home/css/responsive.css" rel="stylesheet">
		<!--[if lt IE 9]>
		<script src="script/home/js/html5shiv.js"></script>
		<script src="script/home/js/respond.min.js"></script>
		<![endif]-->       
		<link rel="shortcut icon" href="script/home/script/home/images/ico/favicon.ico">
		<link rel="apple-touch-icon-precomposed" sizes="144x144" href="script/home/script/home/images/ico/apple-touch-icon-144-precomposed.png">
		<link rel="apple-touch-icon-precomposed" sizes="114x114" href="script/home/script/home/images/ico/apple-touch-icon-114-precomposed.png">
		<link rel="apple-touch-icon-precomposed" sizes="72x72" href="script/home/script/home/images/ico/apple-touch-icon-72-precomposed.png">
		<link rel="apple-touch-icon-precomposed" href="script/home/script/home/images/ico/apple-touch-icon-57-precomposed.png">
		
		<script src="script/home/js/jquery.js"></script>
		<script src="script/home/js/bootstrap.min.js"></script>
		<script src="script/bootstrapValidator/js/bootstrapValidator.min.js"></script>
		<script src="script/bootstrapValidator/js/language/zh_CN.js"></script>
		<script src="script/home/js/jquery.prettyPhoto.js"></script>
		<script src="script/home/js/jquery.isotope.min.js"></script>
		<script src="script/home/js/main.js"></script>
		<script src="script/home/js/wow.min.js"></script>
		<script src="script/home/js/bootstrap-paginator.min.js"></script>
		
		<!-- 自定义 -->
		<script src="script/myJs/myJs.js"></script>

		<style>
			/* 导航样式 */
			ul.nav-tabs {
				width: 140px;
				margin-top: 20px;
				border-radius: 4px;
				border: 1px solid #ddd;
				box-shadow: 0 1px 4px rgba(0, 0, 0, 0.067);
			}
			
			ul.nav-tabs li {
				margin: 0;
				border-top: 1px solid #ddd;
			}
			
			ul.nav-tabs li:first-child {
				border-top: none;
			}
			
			ul.nav-tabs li a {
				margin: 0;
				padding: 8px 16px;
				border-radius: 0;
			}
			
			ul.nav-tabs li.active a, ul.nav-tabs li.active a:hover {
				color: #fff;
				background: #0088cc;
				border: 1px solid #0088cc;
			}
			
			ul.nav-tabs li:first-child a {
				border-radius: 4px 4px 0 0;
			}
			
			ul.nav-tabs li:last-child a {
				border-radius: 0 0 4px 4px;
			}
			
			ul.nav-tabs.affix {
				top: 30px; /* Set the top position of pinned element */
			}
		</style>
	</head>
	<body data-spy="scroll" data-target="#myScrollspy">
		<form id="paperPreviewForm" role="form">
			<div class="container">
				<div class="row">
					<div class="col-md-10 col-lg-10">
						<c:forEach var="paperQuestionEx" items="${paperQuestionExList[0].subList }" varStatus="vs">
						<div class="row">
							<div class="col-md-12 col-lg-12">
								<div class="panel panel-default">
									<div id="section-${vs.index }" class="panel-heading">
										${paperQuestionEx.name }
										<button type="button" class="btn btn-primary btn-xs" style="float: right;" onclick="toBatchUpdateScore(this)">批量设置分数</button>
									</div>
									<c:if test="${!empty paperQuestionEx.description }">
									<div class="panel-body">
										<p>${paperQuestionEx.description }</p>
									</div>
									</c:if>
									<ul class="list-group">
										<c:forEach var="subPaperQuestionEx" items="${paperQuestionEx.subList }" varStatus="v">
										<li class="list-group-item">
											<%-- 题干 --%>
											<input type="hidden" name="paperQuestionIds" value="${subPaperQuestionEx.id }">
											<span style="vertical-align: top; ">${v.count}：</span>
											<span style="display: inline-block;">${subPaperQuestionEx.question.title }</span>
											<%-- 如果是单选题 --%>
											<c:if test="${subPaperQuestionEx.question.type == '1' }">
											<c:forEach var="dict" items="${questionOptions }">
											<c:set var="op" value="option${dict.dictValue }"></c:set>
											<c:if test="${!empty subPaperQuestionEx.question[op] }">
											<div class="radio">
												<label>
													<input type="radio" name="optionsRadios" id="optionsRadios1" value="option1">
													${dict.dictValue }：<span style="display: inline-block;">${subPaperQuestionEx.question[op] }</span>
												</label>
											</div>
											</c:if>
											</c:forEach>
											</c:if>
											<%-- 如果是多选题 --%>
											<c:if test="${subPaperQuestionEx.question.type == '2' }">
											<c:forEach var="dict" items="${questionOptions }">
											<c:set var="op" value="option${dict.dictValue }"></c:set>
											<c:if test="${!empty subPaperQuestionEx.question[op] }">
											<div class="checkbox">
												<label>
													<input type="checkbox" value="">
													${dict.dictValue }：<span style="display: inline-block;">${subPaperQuestionEx.question[op] }</span>
												</label>
											</div>
											</c:if>
											</c:forEach>
											</c:if>
											<%-- 如果是填空题 --%>
											<c:if test="${subPaperQuestionEx.question.type == '3' }">
											<div class="form-group">
												<input type="text" class="form-control" id="name" placeholder="答案：">
											</div>
											</c:if>
											<%-- 如果是判断题 --%>
											<c:if test="${subPaperQuestionEx.question.type == '4' }">
											<div class="form-group">
												<label class="checkbox-inline">
													<input type="radio" name="optionsRadios" id="optionsRadios1" value="option1">
													对
												</label>
												<label class="checkbox-inline">
													<input type="radio" name="optionsRadios" id="optionsRadios1" value="option1">
													错
												</label>
											</div>
											</c:if>
											<%-- 如果是问答题 --%>
											<c:if test="${subPaperQuestionEx.question.type == '5' }">
											<div class="form-group">
												<textarea class="form-control" rows="3"></textarea>
											</div>
											</c:if>
											<div class="form-group">
												<input type="text" name="scores" value="${subPaperQuestionEx.score }" class="form-control" placeholder="输入分值">
											</div>
										</li>
										</c:forEach>
									</ul>
								</div>
							</div>
						</div>
						</c:forEach>
						<div class="row">
							<div class="col-md-12 col-lg-12">
								<div id="paperPreviewSuccessMessage" class="alert alert-success" style="display: none;" onclick="showSuccessMessage(false)">
									<strong>成功！</strong>
									设置成功
								</div>
								<div id="paperPreviewMessage" class="alert alert-warning" style="display: none;" onclick="showWarnMessage(false)">
									<strong>警告！</strong>
									校验不通过
								</div>
								<button type="button" class="btn btn-primary btn-lg btn-block" onclick="updateScore()">设置分数</button>
							</div>
						</div>
					</div>
					<div class="col-md-2 col-lg-2" id="myScrollspy">
						<ul class="nav nav-tabs nav-stacked" data-spy="affix" data-offset-top="125">
							<c:forEach var="paperQuestionEx" items="${paperQuestionExList[0].subList }" varStatus="vs">
							<li ${vs.index == 0 ? 'class="active"' : ''}>
								<a href="paper/toPaperPreview?id=${id }#section-${vs.index }">${paperQuestionEx.name }</a>
							</li>
							</c:forEach>
						</ul>
					</div>
				</div>
			</div>
		</form>
		<!-- 模态框（Modal） -->
		<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
			aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
						<h4 class="modal-title" id="myModalLabel">提示消息</h4>
					</div>
					<div class="modal-body">
						<form id="modalForm" role="form">
							<div class="form-group">
								<input id="modalScore" type="text" name="score" class="form-control" placeholder="输入分值">
							</div>
						</form>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
						<button id="modalConfirmButton" type="button" onclick="doBatchUpdateScore()" class="btn btn-primary">设置</button>
					</div>
				</div>
				<!-- /.modal-content -->
			</div>
			<!-- /.modal -->
		</div>
	</body>
	<script type="text/javascript">
		//页面加载完毕，执行如下方法：
		$(function() {
			initPaperPreview();
		});
		
		//初始化
		function initPaperPreview(){
			$("[role=\"form\"]").bootstrapValidator({
				message : 'This value is not valid',
				trigger : "change",
				feedbackIcons : {
					valid : 'glyphicon glyphicon-ok',
					invalid : 'glyphicon glyphicon-remove',
					validating : 'glyphicon glyphicon-refresh'
				},
				fields : {
					scores : {
						validators : {
							notEmpty : {
								message : "必填项"
							},
							regexp : {
								regexp : /^(([1-9][0-9]*)|(([0]\.\d{1,2}|[1-9][0-9]*\.\d{1,2})))$/,
								message : "必须为正数，最多保留两位小数"
							}
						}
					},
					score : {
						validators : {
							notEmpty : {
								message : "必填项"
							},
							regexp : {
								regexp : /^(([1-9][0-9]*)|(([0]\.\d{1,2}|[1-9][0-9]*\.\d{1,2})))$/,
								message : "必须为正数，最多保留两位小数"
							}
						}
					}
				}
			});
		}
		
		//设置分数
		function updateScore(){
			var bootstrapValidator = $("#paperPreviewForm").data('bootstrapValidator');
			bootstrapValidator.validate();
			if(!bootstrapValidator.isValid()){
				$("#paperPreviewMessage").show();
				return;
			}
			
			$("#paperPreviewMessage").hide();
			$.ajax({
				url : "paper/doPaperCfgScoreUpdate",
				data : $("#paperPreviewForm").serialize(),
				success : function(jsonObj) {
					if(!jsonObj.success){
						$("#paperPreviewMessage").show();
						return;
					}
					parent.paperGrid.datagrid("reload");
					$("#paperPreviewSuccessMessage").show();
				}
			});
		}
		
		//到达批量设置分数页面
		var currObj;
		function toBatchUpdateScore(_currObj){
			$("#myModal").modal("show");
			currObj = _currObj;
			//$("#modalConfirmButton").unbind().bind("click", doBatchUpdateScore(currObj));
		}
		
		//完成批量设置分数
		function doBatchUpdateScore(){
			var bootstrapValidator = $("#modalForm").data('bootstrapValidator');
			bootstrapValidator.validate();
			if(!bootstrapValidator.isValid()){
				return;
			}
			
			$("#myModal").modal("hide");
			var score = $("#modalScore").val();
			$(currObj).parent().parent().find("input[name=\"scores\"]").val(score).change();
		}
		
		//显示警告消息
		function showWarnMessage(ok){
			if(ok){
				$("#paperPreviewMessage").show();
			}else{
				$("#paperPreviewMessage").hide();
			}
		}
		
		//显示成功消息
		function showSuccessMessage(ok){
			if(ok){
				$("#paperPreviewSuccessMessage").show();
			}else{
				$("#paperPreviewSuccessMessage").hide();
			}
		}
	</script>
</html>