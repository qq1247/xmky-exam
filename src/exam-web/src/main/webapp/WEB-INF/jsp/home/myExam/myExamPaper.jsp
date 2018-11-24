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
		</style>
	</head>
	<body>
		<nav class="navbar navbar-fixed-top exam-bar" role="navigation">
			<table style="width: 100%;height: 100%;">
				<tr>
					<td style="width: 50%;text-align: left;">【${exam.name }】【${USER.name }】【${startTime }&nbsp;&nbsp;-&nbsp;&nbsp;${endTime }】</td>
					<td style="width: 20%;text-align: center;">剩余时间：<span id="remainingTime"></span>&nbsp;&nbsp;</td>
					<td style="width: 30%;text-align: right;">
						<button type="button" class="btn btn-info" onclick="doPaper(false)">交卷</button>
						<button type="button" class="btn btn-info" onclick="javascript:history.back(-1);">返回</button>
						&nbsp;&nbsp;
					</td>
				</tr>
			</table>
		</nav>
		<div class="exam-header-plus"></div>
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
								<table style="width: 100%;">
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
																	onclick="updateChoiceAnswer('qst_${euq.id }')">
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
																	onclick="updateChoiceAnswer('qst_${euq.id }')">
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
																	onblur="updateTextAnswer('qst_${euq.id }')">
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
																onclick="updateChoiceAnswer('qst_${euq.id }')">
															对
														</label>
														<label class="radio-inline">
															<input type="radio" name="qst_${euq.id}" value="错"
																${euq.answer == "错" ? "checked='checked'" : "" }
																onclick="updateChoiceAnswer('qst_${euq.id }')">
															错
														</label>
														</c:if>
														<%-- 问答题 --%>
														<c:if test="${subPqEx.question.type == 5 }">
														<textarea id="qst_${euq.id}" name="qst_${euq.id}" 
															class="form-control" rows="6" onblur="updateTextAnswer('qst_${euq.id }')"
															>${euq.answer}</textarea>
														</c:if>
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
			var endTimeStr ="${endTime }";
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
				$("#remainingTime").html(h + "：" + m + "：" + s)
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
		
		//更新选择答案
		function updateChoiceAnswer(name){console.info(name);
			var id = name.substr(4);
			var answer = "";
			$("input[name='" + name + "']:checked").each(function(index, domEle){
				if(index > 0){
					answer += ",";
				}
				
				answer += domEle.value;
			});
			
			$.ajax({
				data : {examUserQuestionId : id, answer : answer },
				url : "home/myExam/updateAnswer",
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
		
		//更新文本答案
		function updateTextAnswer(name){
			var id = name.substr(4);
			var answers = $("[name='" + name + "']");
			var answer = "";
			answers.each(function (index, domEle) {
				if(index > 0){
					answer += "\n";
				}
				answer += $(domEle).val();console.info(answer)
			});
			
			$.ajax({
				data : {examUserQuestionId : id, answer : answer },
				url : "home/myExam/updateAnswer",
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
		
		//完成试卷
		function doPaper(auto){
			$.ajax({
				url : "home/myExam/doPaper",
				data : {examUserId : "${examUser.id }"},
				async : true, 
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
					
					var message = "";
					if(auto){
						message = "考试时间到！";
					}else{
						message = "交卷成功！";
					}
					BootstrapDialog.show({
						title : "提示消息",
						message : message,
						buttons : [{
							label : "&nbsp;确定",
							icon : "glyphicon glyphicon-ok",
							cssClass : "btn-primary",
							action : function(dialogItself) {
								dialogItself.close();
							}
						}]
					});
					
					setTimeout("window.location.href = 'home/toHome'", 2000)
				}
			});
		}
	</script>
</html>