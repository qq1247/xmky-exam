<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
	<head>
		<title>${exam.name }</title>
		<%@include file="/script/home/common.jspf"%>
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
		<nav class="navbar navbar-fixed-top exam-bar" role="navigation">
			<table style="width: 100%;height: 100%;">
				<tr>
					<td style="width: 70%;text-align: left;">【${exam.name }】【${user.name }】【${startTime }&nbsp;&nbsp;-&nbsp;&nbsp;${endTime }】</td>
					<td style="width: 30%;text-align: right;">
						<button type="button" class="btn btn-info" onclick="doMark(false)">判卷</button>
						<button type="button" class="btn btn-info" onclick="javascript:history.back(-1);">返回</button>
						&nbsp;&nbsp;
					</td>
				</tr>
			</table>
		</nav>
		<div class="exam-header-plus"></div>
		<form role="form">
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
				<c:forEach var="pqEx" items="${paperQuestionExList }" varStatus="v">
				<div class="row">
					<div class="col-md-12">
						<div class="panel panel-default">
							<div class="panel-body">
								<table style="width: 100%;">
									<tr>
										<td style="width: 100%;vertical-align: top;">
											<h3>${pqEx.name }</h3>
											<h5>${pqEx.description }</h5>
										</td>
									</tr>
								</table>
							</div>
							<ul class="list-group collapse in">
								<c:set var="labs" value="${fn:split('A,B,C,D,E,F,G', ',')}"></c:set>
								<c:forEach var="subPqEx" items="${pqEx.subList }" varStatus="v1">
								<c:set var="euq" value="${examUserQuestionMap[subPqEx.questionId + 0]}"></c:set>
								<li class="list-group-item">
									<table style="width: 100%;" class="${empty euq.score ? 'field-error' : ''}">
										<tr>
											<td style="width: 1%;vertical-align: top;">
												${subPqEx.no }。
											</td>
											<td style="width: 99%;vertical-align: top;">
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
																	<input type="radio" name="qst_${euq.id}" value="${lab }" 
																		${euq.answer == lab ? "checked='checked'" : "" }
																		disabled="disabled">
																	${lab }：${subPqEx.question[ol] }
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
															<c:set var="op1" value=",${euq.answer},"></c:set>
															<div class="checkbox">
																<label>
																	<input type="checkbox" name="qst_${euq.id}" value="${lab }"
																		${fn:contains(op1, lab) ? "checked='checked'" : "" }
																		disabled="disabled">
																	${lab }：${subPqEx.question[ol] }
																</label>
															</div>
															</c:if>
															</c:forEach>
															</c:if>
															<%-- 填空题 --%>
															<c:if test="${subPqEx.question.type == 3 }">
															<% pageContext.setAttribute("v", "\n"); %>
															<c:set var="lab1s" value="${fn:split('一,二,三,四,五,六,七', ',')}"></c:set>
															<c:set var="answers" value="${fn:split(euq.answer, v)}"></c:set>
															<c:forEach var="answer" items="${fn:split(subPqEx.question.answer, v) }" varStatus="s">
															<div class="row form-group">
																<label class="col-md-1 control-label">填空${lab1s[s.index] }：</label>
																<div class="col-md-5">
																	<input type="text" name="qst_${euq.id}" 
																		value="${answers[s.index]}" class="form-control" placeholder="答案："
																		disabled="disabled">
																</div>
																<div class="col-md-6"></div>
															</div>
															</c:forEach>
															</c:if>
															<%-- 判断题 --%>
															<c:if test="${subPqEx.question.type == 4 }">
															<label class="radio-inline">
																<input type="radio" name="qst_${euq.id}" value="对"
																	${euq.answer == "对" ? "checked='checked'" : "" }
																	disabled="disabled">
																对
															</label>
															<label class="radio-inline">
																<input type="radio" name="qst_${euq.id}" value="错"
																	${euq.answer == "错" ? "checked='checked'" : "" }
																	disabled="disabled">
																错
															</label>
															</c:if>
															<%-- 问答题 --%>
															<c:if test="${subPqEx.question.type == 5 }">
															<textarea id="qst_${euq.id}" name="qst_${euq.id}" 
																class="form-control" rows="6" disabled="disabled"
																>${euq.answer}</textarea>
															</c:if>
														</div>
													</div>
													<div class="row">
														<div class="col-md-12">
															<label class="control-label">【标准答案-${subPqEx.score }分】：${subPqEx.question.answer }</label>
														</div>
													</div>
													<div class="row">
														<div class="col-md-12">
															<label class="control-label">【试题解析】：${subPqEx.question.analysis }</label>
														</div>
													</div>
													<div class="row">
														<div class="col-md-12">
															<div class="form-group ${empty euq.score ? 'has-error' : ''}" style="margin-bottom: 0px;">
																<label for="score_${subPqEx.id }" class="control-label">【得分】：</label>
																<c:if test="${subPqEx.question.type != 5 }">
																	${euq.score }
																</c:if>
																<c:if test="${subPqEx.question.type == 5 }">
																<input type="text" id="qst_sc_${euq.id  }" name="score" value="${euq.score }" placeholder="0.00"
																	style="width: 40px;border:0; border-bottom: 1px solid #d8d8d8; text-align: center;"
																	onblur="updateScore('qst_sc_${euq.id }', ${subPqEx.score })"
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
	
		//页面加载完毕，执行如下方法
		$(function(){
		});
		
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