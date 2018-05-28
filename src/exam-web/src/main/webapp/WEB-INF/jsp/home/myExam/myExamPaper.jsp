<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>我的考试</title>
		<%@include file="/script/home/common.jspf"%>
	</head>
	<body>
		<div class="navbar navbar-inverse affix" style="width: 100%;height: 40px; z-index: 999999;padding: 0px;color: white;font-weight: bold;font-size: 14px;" >
			<table style="width: 100%;height: 100%;">
				<tr>
					<td style="width: 30%;text-align: left;">用户名：${USER.name }</td>
					<td style="width: 40%;text-align: center;">试卷名称：${paper.name }</td>
					<td id="remainingTd" style="width: 30%;text-align: right;background-color: green;">
						剩余时间：<span id="remainingTime"></span>&nbsp;&nbsp;
						<button type="button" class="btn btn-info" onclick="doPaper(false)">交卷</button>
						<button type="button" class="btn btn-info" onclick="toMyExam()">返回</button>
						&nbsp;&nbsp;
					</td>
				</tr>
			</table>
		</div>
		<form id="paperForm" role="form">
			<input type="hidden" name="roomId" value="${room.id }">
			<div class="container" style="padding-top: 70px;">
				<div class="row">
					<div class="col-md-10 col-lg-10 col-md-offset-1">
						<c:forEach var="paperQuestionEx" items="${paperQuestionExList[0].subList }" varStatus="vs">
						<div class="row">
							<div class="col-md-12 col-lg-12">
								<div class="panel panel-primary">
									<div id="section-${vs.index }" class="panel-heading">
										${paperQuestionEx.name }
									</div>
									<c:if test="${!empty paperQuestionEx.description }">
									<div class="panel-body">
										<p>${paperQuestionEx.description }</p>
									</div>
									</c:if>
									<ul class="list-group">
										<c:forEach var="subPaperQuestionEx" items="${paperQuestionEx.subList }" varStatus="v">
										<c:set var="examUserQuestion" value="${examUserQuestionMap[subPaperQuestionEx.questionId + 0]}"></c:set>
										<li class="list-group-item">
											<%-- 题干 --%>
											<span style="vertical-align: top; ">${v.count}：</span>
											<span style="display: inline-block;">${subPaperQuestionEx.question.title }</span>
											<%-- 如果是单选题 --%>
											<c:if test="${subPaperQuestionEx.question.type == '1' }">
											<c:forEach var="dict" items="${questionOptions }">
											<c:set var="op" value="option${dict.dictValue }"></c:set>
											<c:if test="${!empty subPaperQuestionEx.question[op] }">
											<div class="radio">
												<label>
													<input type="radio" name="qst_${examUserQuestion.id}" value="${dict.dictValue }" 
														${examUserQuestion.answer == dict.dictValue ? "checked='checked'" : "" }
														onclick="updateChoiceAnswer('qst_${examUserQuestion.id }')">
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
													<c:set var="op1" value=",${examUserQuestion.answer},"></c:set>
													<c:set var="op2" value=",${dict.dictValue},"></c:set>
													<input type="checkbox" name="qst_${examUserQuestion.id}" value="${dict.dictValue }"
														${fn:contains(op1, dict.dictValue) ? "checked='checked'" : "" }
														onclick="updateChoiceAnswer('qst_${examUserQuestion.id }')">
													${dict.dictValue }：<span style="display: inline-block;">${subPaperQuestionEx.question[op] }</span>
												</label>
											</div>
											</c:if>
											</c:forEach>
											</c:if>
											<%-- 如果是填空题 --%>
											<c:if test="${subPaperQuestionEx.question.type == '3' }">
											<div class="form-group">
												<input type="text" id="qst_${examUserQuestion.id}" name="qst_${examUserQuestion.id}" 
													value="${examUserQuestion.answer}" class="form-control" placeholder="答案："
													onblur="updateTextAnswer('qst_${examUserQuestion.id }')">
											</div>
											</c:if>
											<%-- 如果是判断题 --%>
											<c:if test="${subPaperQuestionEx.question.type == '4' }">
											<div class="form-group">
												<label class="checkbox-inline">
													<input type="radio" name="qst_${examUserQuestion.id}" value="对"
														${examUserQuestion.answer == "对" ? "checked='checked'" : "" }
														onclick="updateChoiceAnswer('qst_${examUserQuestion.id }')">
													对
												</label>
												<label class="checkbox-inline">
													<input type="radio" name="qst_${examUserQuestion.id}" value="错"
														${examUserQuestion.answer == "错" ? "checked='checked'" : "" }
														onclick="updateChoiceAnswer('qst_${examUserQuestion.id }')">
													错
												</label>
											</div>
											</c:if>
											<%-- 如果是问答题 --%>
											<c:if test="${subPaperQuestionEx.question.type == '5' }">
											<div class="form-group">
												<textarea id="qst_${examUserQuestion.id}" name="qst_${examUserQuestion.id}" 
													class="form-control" rows="6" 
													onblur="updateTextAnswer('qst_${examUserQuestion.id }')">${examUserQuestion.answer}</textarea>
											</div>
											</c:if>
										</li>
										</c:forEach>
									</ul>
								</div>
							</div>
						</div>
						</c:forEach>
						<div class="row">
							<div class="col-md-12 col-lg-12">
								<button type="button" class="btn btn-primary btn-lg btn-block" onclick="doPaper(false)">交卷</button>
							</div>
						</div>
					</div>
				</div>
			</div>
		</form>
		<!-- 模态框（Modal） -->
		<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
			aria-hidden="true" style="margin-top: 120px;">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
						<h4 class="modal-title" id="myModalLabel">提示消息</h4>
					</div>
					<div class="modal-body">
						<span id="modalMessage"></span>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
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
		var paperForm = $("#paperForm");
		
		//页面加载完毕，执行如下方法：
		$(function() {
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
					$("#remainingTd").css("background-color", "red");
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
				url : "home/myExam/sysTime",
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
					if(!obj.success){
						$("#modalMessage").html(obj.message);
						$('#myModal').modal('show');
						return;
					}
					
					if(auto){
						$("#modalMessage").html("考试时间到！");
					}else{
						$("#modalMessage").html("交卷成功！");
					}
					$('#myModal').modal('show');
					
					setTimeout("window.location.href = 'home/pubToHome'", 2000)
				}
			});
		}
		
		//更新选择答案
		function updateChoiceAnswer(name){
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
					if(!obj.success){
						$("#modalMessage").html(obj.message);
						$('#myModal').modal('show');
					}
				}
			});
		}
		
		//更新文本答案
		function updateTextAnswer(name){
			var id = name.substr(4);
			var answer = $("#" + name).val();
			
			$.ajax({
				data : {examUserQuestionId : id, answer : answer },
				url : "home/myExam/updateAnswer",
				async : true, //异步提交
				success : function(obj) {
					if(!obj.success){
						$("#modalMessage").html(obj.message);
						$('#myModal').modal('show');
					}
				}
			});
		}
		
		//返回
		function toMyExam(){
			window.location.href = "home/myExam/toList";
		}
	</script>
</html>