<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>判卷</title>
		<%@include file="/script/home/common.jspf"%>
	</head>
	<body>
		<div class="navbar navbar-inverse affix" style="width: 100%;height: 40px; z-index: 999999;padding: 0px;color: white;font-weight: bold;font-size: 14px;" >
			<table style="width: 100%;height: 100%;">
				<tr>
					<td style="width: 30%;text-align: left;">用户名：${user.name } | 判卷人：${USER.name }</td>
					<td style="width: 40%;text-align: center;">试卷名称：${paper.name }</td>
					<td id="remainingTd" style="width: 30%;text-align: right;background-color: green;">
						<button type="button" class="btn btn-info" onclick="doMark()">完成判卷</button>
						<button type="button" class="btn btn-info" onclick="toMark()">返回</button>
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
														disabled="disabled">
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
													<c:set var="op1" value=",${examUserQuestion.answer },"></c:set>
													<c:set var="op2" value=",${dict.dictValue },"></c:set>
													<input type="checkbox" name="qst_${examUserQuestion.id}" value="${dict.dictValue }"
														${fn:contains(op1, op2) ? "checked='checked'" : "" }
														disabled="disabled">
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
													disabled="disabled">
											</div>
											</c:if>
											<%-- 如果是判断题 --%>
											<c:if test="${subPaperQuestionEx.question.type == '4' }">
											<div class="form-group">
												<label class="checkbox-inline">
													<input type="radio" name="qst_${examUserQuestion.id}" value="对"
														${examUserQuestion.answer == "对" ? "checked='checked'" : "" }
														disabled="disabled">
													对
												</label>
												<label class="checkbox-inline">
													<input type="radio" name="qst_${examUserQuestion.id}" value="错"
														${examUserQuestion.answer == "错" ? "checked='checked'" : "" }
														disabled="disabled">
													错
												</label>
											</div>
											</c:if>
											<%-- 如果是问答题 --%>
											<c:if test="${subPaperQuestionEx.question.type == '5' }">
											<div class="form-group">
												<textarea id="qst_${examUserQuestion.id}" name="qst_${examUserQuestion.id}" 
													class="form-control" rows="3" 
													disabled="disabled">${examUserQuestion.answer}</textarea>
											</div>
											</c:if>
											
											<div class="well">
												<c:set var="right" value="${subPaperQuestionEx.question.answer == examUserQuestion.answer }"></c:set>
												<c:set var="color" value="${right ? 'green' : 'red'}"></c:set>
												<div class="row">
													<label for="firstname" class="col-sm-8 control-label" style="color: ${color};margin-top: 5px">
														标准答案：${subPaperQuestionEx.question.answer }
													</label>
													<div class="col-sm-4 control-label">
														分值【${subPaperQuestionEx.score }】：
														<input type="text" id="qst_sc_${examUserQuestion.id}" class="form-control" placeholder="得分：" 
															style="width: 100px;display: inline-block;" 
															<c:if test="${subPaperQuestionEx.question.type == '1' || subPaperQuestionEx.question.type == '4'}">
															readonly="readonly" value="${right ? subPaperQuestionEx.score : 0}"
															</c:if>
															<c:if test="${subPaperQuestionEx.question.type != '1' && subPaperQuestionEx.question.type != '4'}">
															value="${examUserQuestion.score }" onblur="updateScore('qst_sc_${examUserQuestion.id }', '${subPaperQuestionEx.score }')"
															</c:if>
															/>
													</div>
												</div>
												<div class="row">
													<label class="control-label" style="margin-left: 15px;">
														试题解析：${subPaperQuestionEx.question.analysis }
													</label>
												</div>
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
								<button type="button" class="btn btn-primary btn-lg btn-block" onclick="doMark()">完成判卷</button>
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
		//页面加载完毕，执行如下方法：
		$(function() {
			addPaperValid();
		});
		
		//添加校验
		function addPaperValid(){
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
		
		//更新分数
		function updateScore(id, maxScore){
			var obj = $("#" + id);
			var currScore = obj.val();
			if(currScore < 0){
				currScore = 0;
				obj.val(0);
			}else if(currScore > maxScore){
				currScore = maxScore;
				obj.val(maxScore);
			}
			
			$.ajax({
				data : {examUserQuestionId : id.substr(7), score : currScore},
				url : "home/mark/updateScore",
				async : true, //异步提交
				success : function(obj) {
					if(!obj.success){
						$("#modalMessage").html(obj.message);
						$('#myModal').modal('show');
					}
				}
			});
		}
		
		//完成判卷
		function doMark(){
			$.ajax({
				data : {examUserId : "${examUser.id }"},
				url : "home/mark/doMark",
				async : true, //异步提交
				success : function(obj) {
					$("#modalMessage").html(obj.message);
					$('#myModal').modal('show');
					
					if(!obj.success){
						return;
					}
					setTimeout("window.location.href = 'home/mark/toList'", 2000)
				}
			});
		}
		
		//返回
		function toMark(){
			window.location.href = "home/mark/toList";
		}
	</script>
</html>