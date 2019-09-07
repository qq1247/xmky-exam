<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
	<head>
		<title>${exam.name }</title>
		<%@include file="/script/home/common.jspf"%>
		<link href="css/paper.css" rel="stylesheet">
		<style type="text/css">
			.exam-bar {
				background: #38f838 none repeat scroll 0% 0%;
				height: 36px;
				font-size: 16px;
				margin-bottom: 0;
			}
			.exam-header-plus {
				padding-top: 66px;
			}
			.paper .help-block {
			    font-size: 12px;
			    float: none;
			    color: #a94442;
			    margin-bottom: 0px !important;
			    display: inline;
			}
		</style>
	</head>
	<body>
		<div class="exam-header-plus"></div>
		<form role="form">
			<div class="container paper">
				<div class="row">
					<div class="col-md-10">
						<div class="panel panel-default">
							<ul class="list-group">
								<li class="list-group-item">
									<input type="hidden" id="id" name="id" value="${paper.id }">
									<h1>${exam.name }</h1>
									<h5>${exam.description }</h5>
								</li>
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
									<li><a id="an_card_${examUserQuestion.id }" href="${basePath }home/myMark/toMark?examUserId=${examUserId }#title_${examUserQuestion.id }" 
											class="answer-card-yes" 
											>${subPaperQuestionEx.no }</a></li>
									</c:forEach>
								</ul>
							</div>
							</c:forEach>
							<div class="answer-card-content">
								<div class="answer-card-content-tittle">
									<button type="button" class="btn btn-info" onclick="doMark(false)">判卷</button>
									<button type="button" class="btn btn-info" onclick="javascript:history.back(-1);">返回</button>
								</div>
							</div>
						</div>
					</div>
				</div>
				<c:forEach var="paperQuestionEx" items="${paperQuestionExList }" varStatus="v">
				<div class="row">
					<div class="col-md-10">
						<div class="panel panel-default">
							<div class="panel-body">
								<table style="width: 100%;">
									<tr>
										<td style="width: 100%;vertical-align: top;">
											<h3>${paperQuestionEx.name }</h3>
											<h5>${paperQuestionEx.description }</h5>
										</td>
									</tr>
								</table>
							</div>
							<ul class="list-group collapse in">
								<c:set var="labs" value="${fn:split('A,B,C,D,E,F,G', ',')}"></c:set>
								<c:forEach var="subPaperQuestionEx" items="${paperQuestionEx.subList }" varStatus="v1">
								<c:set var="examUserQuestion" value="${examUserQuestionMap[subPaperQuestionEx.questionId + 0]}"></c:set>
								<li id="title_${examUserQuestion.id }" class="list-group-item">
									<table style="width: 100%;" class="${empty examUserQuestion.score ? 'field-error' : ''}">
										<tr>
											<td style="width: 1%;vertical-align: top;">
												${subPaperQuestionEx.no }。
											</td>
											<td style="width: 99%;vertical-align: top;">
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
															<li class="question-option ${examUserQuestion.answer == lab ? 'question-option-select' : '' }" 
																	examUserQuestionId="${examUserQuestion.id}" onclick="radioSubmit(this)">
																<input type="radio" name="option_${examUserQuestion.id}" value="${lab }" 
																		${examUserQuestion.answer == lab ? 'checked="checked"' : '' } disabled="disabled">
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
															<c:set var="op1" value=",${examUserQuestion.answer},"></c:set>
															<li class="question-option ${fn:contains(op1, lab) ? 'question-option-select' : '' }" 
																	examUserQuestionId="${examUserQuestion.id}" onclick="checkboxSubmit(this)">
																<input type="checkbox" name="option_${examUserQuestion.id}" value="${lab }"
																		${fn:contains(op1, lab) ?'checked="checked"' : '' } disabled="disabled">
																${lab }：${subPaperQuestionEx.question[ol] }
															</li>
															</c:if>
															</c:forEach>
															</c:if>
															<%-- 填空题 --%>
															<c:if test="${subPaperQuestionEx.question.type == 3 }">
															<% pageContext.setAttribute("v", "\n"); %>
															<c:set var="lab1s" value="${fn:split('一,二,三,四,五,六,七', ',')}"></c:set>
															<c:set var="answers" value="${fn:split(examUserQuestion.answer, v)}"></c:set>
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
																		examUserQuestionId="${examUserQuestion.id}" disabled="disabled" onblur="txtSubmit(this)">
															</c:forEach>
															</c:if>
															<%-- 判断题 --%>
															<c:if test="${subPaperQuestionEx.question.type == 4 }">
															<li class="question-option ${examUserQuestion.answer == '对' ? 'question-option-select' : '' }" 
																	examUserQuestionId="${examUserQuestion.id}" onclick="radioSubmit(this)">
																<input type="radio" name="option_${examUserQuestion.id}" value="对"
																	${examUserQuestion.answer == "对" ? "checked='checked'" : "" } disabled="disabled">对
															</li>
															<li class="question-option ${examUserQuestion.answer == '错' ? 'question-option-select' : '' }" 
																	examUserQuestionId="${examUserQuestion.id}" onclick="radioSubmit(this)">
																<input type="radio" name="option_${examUserQuestion.id}" value="错"
																	${examUserQuestion.answer == "错" ? "checked='checked'" : "" } disabled="disabled">错
															</li>
															</c:if>
															<%-- 问答题 --%>
															<c:if test="${subPaperQuestionEx.question.type == 5 }">
															<textarea name="option_${examUserQuestion.id}" style="width: 100%;height: 150px" 
																class="fillblanks" examUserQuestionId="${examUserQuestion.id}" onblur="txtSubmit(this)"+
																disabled="disabled">${examUserQuestion.answer}</textarea>
															</c:if>
														</div>
													</div>
													<div class="row">
														<div class="col-md-12">
															<label class="control-label">【标准答案-${subPaperQuestionEx.score }分】：${subPaperQuestionEx.question.answer }</label>
														</div>
													</div>
													<c:if test="${!empty subPaperQuestionEx.question.analysis  }">
													<div class="row">
														<div class="col-md-12">
															<label class="control-label">【试题解析】：${subPaperQuestionEx.question.analysis }</label>
														</div>
													</div>
													</c:if>
													<div class="row">
														<div class="col-md-12">
															<div class="form-group ${empty examUserQuestion.score ? 'has-error' : ''}" style="margin-bottom: 0px;">
																<label for="score_${subPaperQuestionEx.id }" class="control-label">【得分】：</label>
																<c:if test="${subPaperQuestionEx.question.type != 5 }">
																	${examUserQuestion.score }
																</c:if>
																<c:if test="${subPaperQuestionEx.question.type == 5 }">
																<input type="text" id="qst_sc_${examUserQuestion.id  }" name="score" value="${examUserQuestion.score }" placeholder="0.00"
																	style="width: 40px;border:0; border-bottom: 1px solid #d8d8d8; text-align: center;"
																	onblur="updateScore('qst_sc_${examUserQuestion.id }', ${subPaperQuestionEx.score })"
																	>分
																	<small class="help-block"></small>
																</c:if>
															</div>
														</div>
													</div>
												</div>
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
		</form>
	</body>
	<script type="text/javascript">
		//定义变量
		var endTime = null;
		var sysTime = null;
		var diffTime = 0;
		var timer = null;
		var num = 0;
		
		//页面加载完毕，执行如下方法
		$(function(){
			var endTimeStr ="${markEndTime }";
			endTime = new Date(endTimeStr.substring(0, 4),
					endTimeStr.substring(5, 7),
					endTimeStr.substring(8, 10),
					endTimeStr.substring(11, 13),
					endTimeStr.substring(14, 16),
					endTimeStr.substring(17, 19)
					);
			
			updateRemainingTime();
			timer = setInterval(updateRemainingTime, 1000);
		});
		
		//更新剩余时间
		function updateRemainingTime(){
			if(num-- > 0){
				if(diffTime <= 0){
					clearInterval(timer);
					doPaper(true);
					return;
				}
				
				if(diffTime < 300){
					$(".exam-bar").css("background", "red none repeat scroll 0% 0%");
				}
				
				diffTime--;
				
				var h = parseInt(diffTime / 3600);
				if(h < 10){
					h = "0" + h;
				}
				var m = parseInt(diffTime % 3600 / 60);
				if(m < 10){
					m = "0" + m;
				}
				
				var s = parseInt(diffTime % 60);
				if(s < 10){
					s = "0" + s;
				}
				$("#remainingTime").html(h + "：" + m + "：" + s + "");
				console.info(h + "：" + m + "：" + s + "");
				console.info($("#remainingTime").html());
				console.info($("#remainingTime").val());
				return;
			}
			
			$.ajax({
				url : "home/sysTime",
				async : true,
				success : function(obj) {
					sysTime = new Date(obj.data.sysTime.substring(0, 4),
							obj.data.sysTime.substring(5, 7),
							obj.data.sysTime.substring(8, 10),
							obj.data.sysTime.substring(11, 13),
							obj.data.sysTime.substring(14, 16),
							obj.data.sysTime.substring(17, 19)
							);
					
					diffTime = (endTime.getTime() - sysTime.getTime() - 1500) / 1000;//获取用时500，下一次循环用时995，以后精确时间
					num = 60;
				}
			});
		}
		
		//更新分数
		function updateScore(id, maxScore){
			var $scoreInput = $("#" + id);
			var currScore = $scoreInput.val();
			$scoreInput.parent().parent().parent().parent().parent().parent().parent().parent().removeClass("field-error");
			$scoreInput.parent().removeClass("has-error");
			$scoreInput.next().html("");
			
			if(currScore && currScore != "0"){
				var patt = /^(([1-9][0-9]*)|(([0]\.\d{1,2}|[1-9][0-9]*\.\d{1,2})))$/;
				if(!patt.test(currScore)){
					$scoreInput.parent().parent().parent().parent().parent().parent().parent().parent().addClass("field-error");
					$scoreInput.parent().addClass("has-error");
					$scoreInput.next().html("必须为正数，最多保留两位小数");
					return;
				}
			}
			
			if(currScore){
				$scoreInput.parent().addClass("has-success");
			}else{
				$scoreInput.parent().addClass("has-error");
			}
			
			if(currScore < 0){
				currScore = 0;
				$scoreInput.val(0);
			}else if(currScore > maxScore){
				currScore = maxScore;
				$scoreInput.val(maxScore);
			}
			
			$.ajax({
				data : {examUserQuestionId : id.substr(7), score : currScore},
				url : "home/myMark/updateScore",
				async : true, //异步提交
				success : function(obj) {
					if(!obj.succ){
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
				}
			});
		}
		
		//完成判卷
		function doMark(){
			$.ajax({
				data : {examUserId : "${examUser.id }"},
				url : "home/myMark/doMark",
				async : true, //异步提交
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
					
					if(!obj.succ){
						return;
					}
					
					setTimeout("window.location.href = 'home/myMark/toList'", 2000);
				}
			});
		}
	</script>
</html>