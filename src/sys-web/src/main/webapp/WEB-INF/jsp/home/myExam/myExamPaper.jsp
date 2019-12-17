<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
	<head>
		<title>${exam.name }</title>
		<%@include file="/script/home/common.jspf"%>
		<link href="css/paper.css" rel="stylesheet">
	</head>
	<body>
		<div class="exam-header-plus"></div>
		<div class="container paper">
			<div class="row">
				<div class="col-md-10">
					<div class="row">
						<div class="col-md-12">
							<div class="panel panel-default">
								<ul class="list-group">
									<li class="list-group-item">
										<h1>${exam.name }</h1>
										<h5>${exam.description }</h5>
									</li>
								</ul>
							</div>
						</div>
					</div>
					<c:forEach var="paperQuestionEx" items="${paperQuestionExList }" varStatus="v">
					<div class="row">
						<div class="col-md-12">
							<div class="panel panel-default">
								<div class="panel-body">
									<h3>${paperQuestionEx.name }</h3><%-- 试题标题 --%>
									<h5 style="margin-bottom: 0px;">${paperQuestionEx.description }</h5><%-- 试题描述 --%>
								</div>
								<ul class="list-group collapse in">
									<c:set var="labs" value="${fn:split('A,B,C,D,E,F,G', ',')}"></c:set>
									<c:forEach var="subPaperQuestionEx" items="${paperQuestionEx.subList }" varStatus="v1">
									<c:set var="examUserQuestion" value="${examUserQuestionMap[subPaperQuestionEx.questionId + 0]}"></c:set>
									<li class="list-group-item">
										<ul class="list-group collapse in">
											<li id="title_${examUserQuestion.id }" class="list-group-item question-title">
											<strong>${subPaperQuestionEx.no }、</strong>${subPaperQuestionEx.question.title }
											</li>
											<%-- 单选题 --%>
											<c:if test="${subPaperQuestionEx.question.type == 1 }">
											<c:forEach var="lab" items="${labs }">
											<c:set var="ol" value="option${lab }"></c:set>
											<c:if test="${!empty subPaperQuestionEx.question[ol] }">
											<li class="question-option ${examUserQuestion.answer == lab ? 'question-option-select' : '' }" 
													examUserQuestionId="${examUserQuestion.id}" onclick="radioSubmit(this)">
												<input type="radio" name="option_${examUserQuestion.id}" value="${lab }" 
														${examUserQuestion.answer == lab ? 'checked="checked"' : '' }>
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
														${fn:contains(op1, lab) ?'checked="checked"' : '' }>
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
														examUserQuestionId="${examUserQuestion.id}" onblur="txtSubmit(this)">
											</c:forEach>
											</c:if>
											<%-- 判断题 --%>
											<c:if test="${subPaperQuestionEx.question.type == 4 }">
											<li class="question-option ${examUserQuestion.answer == '对' ? 'question-option-select' : '' }" 
													examUserQuestionId="${examUserQuestion.id}" onclick="radioSubmit(this)">
												<input type="radio" name="option_${examUserQuestion.id}" value="对"
													${examUserQuestion.answer == "对" ? "checked='checked'" : "" }>对
											</li>
											<li class="question-option ${examUserQuestion.answer == '错' ? 'question-option-select' : '' }" 
													examUserQuestionId="${examUserQuestion.id}" onclick="radioSubmit(this)">
												<input type="radio" name="option_${examUserQuestion.id}" value="错"
													${examUserQuestion.answer == "错" ? "checked='checked'" : "" }>错
											</li>
											</c:if>
											<%-- 问答题 --%>
											<c:if test="${subPaperQuestionEx.question.type == 5 }">
											<textarea name="option_${examUserQuestion.id}" style="width: 100%;height: 150px" 
												class="fillblanks" examUserQuestionId="${examUserQuestion.id}" onblur="txtSubmit(this)"+
												>${examUserQuestion.answer}</textarea>
											</c:if>
										</ul>
									</li>
									</c:forEach>
								</ul>
							</div>
						</div>
					</div>
					</c:forEach>
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
								<li><a id="an_card_${examUserQuestion.id }" href="${basePath }home/myExam/toPaper?examId=${examId }#title_${examUserQuestion.id }" 
										${!empty examUserQuestion.answer ? 'class="answer-card-yes"' : 'class="answer-card-no"' }
										>${subPaperQuestionEx.no }</a></li>
								</c:forEach>
							</ul>
						</div>
						</c:forEach>
						<div class="answer-card-content">
							<div class="answer-card-content-tittle">
								<button type="button" class="btn btn-info" onclick="doPaper(false)">交卷</button>
								<button type="button" class="btn btn-info" onclick="window.location.href='home/toHome'">返回</button>
							</div>
						</div>
					</div>
				</div>
			</div>
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
				$("#remainingTime").html(h + "：" + m + "：" + s + "")
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
		
		//单选提交
		function radioSubmit(li){
			//选中当前行
			var $li = $(li);
			var examUserQuestionId = $li.attr("examUserQuestionId")
			var $curInput = $li.find("input[name='option_"+examUserQuestionId+"']");
			$curInput.prop("checked", true);
			$li.addClass("question-option-select");
			
			//其他行设为未选择
			var $ul = $li.parent();
			var $otherInputs = $ul.find("input").not($curInput[0]);
			$otherInputs.prop("checked", false);
			$ul.find("li").not($li[0]).removeClass("question-option-select");
			
			//答题卡标记为已做
			var anCardOption = $("#an_card_" + examUserQuestionId);
			anCardOption.removeClass("answer-card-no");
			anCardOption.addClass("answer-card-yes");
			
			//异步提交答案
			var answer = $curInput.val();
			$.ajax({
				data : {examUserQuestionId : examUserQuestionId, answer : answer },
				url : "home/myExam/updateAnswer",
				async : true,
				success : function(obj) {
					if(obj.succ){
						return;
					}
						
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
		
		//多选提交
		function checkboxSubmit(li){
			//选中当前行
			var $li = $(li);
			var examUserQuestionId = $li.attr("examUserQuestionId");
			var $curInput = $li.find("input[name='option_"+examUserQuestionId+"']");
			var checked = $curInput.prop("checked");
			if (checked) {
				$li.removeClass("question-option-select");
				$curInput.prop("checked", false);
			} else {
				$li.addClass("question-option-select");
				$curInput.prop("checked", true);
			}
			
			//答题卡标记为已做
			var answer = "";
			$li.parent().find("input[name='option_"+examUserQuestionId+"']:checked").each(function(index, domEle){
				if(index > 0){
					answer += ",";
				}
				
				answer += domEle.value;
			});
			
			var anCardOption = $("#an_card_" + examUserQuestionId);
			if (answer) {
				anCardOption.removeClass("answer-card-no");
				anCardOption.addClass("answer-card-yes");
			} else {
				anCardOption.removeClass("answer-card-yes");
				anCardOption.addClass("answer-card-no");
			}
			
			//异步提交答案
			$.ajax({
				data : {examUserQuestionId : examUserQuestionId, answer : answer },
				url : "home/myExam/updateAnswer",
				async : true,
				success : function(obj) {
					if(obj.succ){
						return;
					}
						
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
		
		//文本提交
		function txtSubmit(input){
			//答题卡标记为已做
			var $input = $(input);
			var examUserQuestionId = $input.attr("examUserQuestionId");
			var $inputs = $input.parent().find("[name='option_"+examUserQuestionId+"']");
			var answer = "";
			$inputs.each(function (index, domEle) {
				if(index > 0){
					answer += "\n";
				}
				answer += $(domEle).val();
			});
			
			var anCardOption = $("#an_card_" + examUserQuestionId);
			if (answer.replace(/\n/g, "")) {
				anCardOption.removeClass("answer-card-no");
				anCardOption.addClass("answer-card-yes");
			} else {
				anCardOption.removeClass("answer-card-yes");
				anCardOption.addClass("answer-card-no");
			}
			
			//异步提交答案
			$.ajax({
				data : {examUserQuestionId : examUserQuestionId, answer : answer },
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
	</script>
</html>