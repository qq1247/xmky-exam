<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<div lay-filter="questionEditFrom" class="layui-form" style="padding: 20px 0 0 0;">
	<input type="hidden" name="id" value="${question.id}" />
	<div class="layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">试题分类：</label>
			<div class="layui-input-block">
				<input type="hidden" id="questionTypeId" name="questionTypeId" value="${questionType.id}">
				<input id="questionTypeName" name="questionTypeName" value="${questionType.name}" 
					class="layui-input layui-disabled" lay-verify="required" readonly="readonly">
			</div>
		</div>
	</div>
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">类型：</label>
			<div class="layui-input-block">
				<c:forEach var="dict" items="${QUESTION_TYPE_DICT_LIST }">
				<input type="radio" lay-filter="type" name="type" value="${dict.dictKey }" title="${dict.dictValue }" 
					${question.type == dict.dictKey ? "checked" : "" } ${!empty question.id ? "disabled" : ""}>
				</c:forEach>
			</div>
		</div>
	</div>
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">难度：</label>
			<div class="layui-input-block">
				<c:forEach var="dict" items="${QUESTION_DIFFICULTY_DICT_LIST }">
				<input type="radio" name="difficulty" value="${dict.dictKey }" title="${dict.dictValue }" ${question.difficulty == dict.dictKey ? "checked" : "" } >
				</c:forEach>
			</div>
		</div>
	</div>
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">题干：</label>
			<div class="layui-input-block">
				<script id="title" name="title" type="text/plain" style="width:100%;height: 50px;">${question.title }</script>
			</div>
		</div>
	</div>
	<div id="optionRow" class="layui-row layui-form-item" ${(question.type != 1 && question.type != 2) ? "style='display: none;'" : "" }>
		<div class="layui-col-md11">
			<label class="layui-form-label">选项：</label>
			<div class="layui-input-block">
				<fieldset class="layui-elem-field">
					<legend></legend>
					<div id="optionPanel" class="layui-field-box">
						<c:if test="${question.type == 1 || question.type == 2 }">
						<c:forEach var="lab" items="${fn:split('A,B,C,D,E,F,G', ',') }">
						<c:set var="optionX" value="option${lab }"></c:set>
						<c:if test="${!empty question[optionX] }">
						<div class="layui-row layui-col-space10">
							<div class="layui-col-md1">
								<label class="layui-form-label" style="float: right;display: inline;">${lab }：</label>
							</div>
							<div class="layui-col-md11">
								<script id="option${lab }" name="option${lab }" type="text/plain" style="width:100%;height: 50px;">${question[optionX] }</script>
								<script type="text/javascript">
									UE.delEditor("option${lab }");
									UE.getEditor("option${lab }");
								</script>
							</div>
						</div>
						</c:if>
						</c:forEach>
						<div class="layui-row layui-col-space10">
							<div class="layui-col-md1">
							</div>
							<div class="layui-col-md11">
								<button class="layui-btn layui-btn-primary layui-btn-sm" onclick="addQuestionOption(this);"><i class="layui-icon">&#xe654;</i>添加选项</button>
								<button class="layui-btn layui-btn-primary layui-btn-sm" onclick="delQuestionOption(this);"><i class="layui-icon">&#xe67e</i>移除选项</button>
							</div>
						</div>
						</c:if>
					</div>
				</fieldset>
			</div>
		</div>
	</div>
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">答案：</label>
			<div id="answerPanel" class="layui-input-block">
				<c:if test="${question.type == 1 }">
				<c:forEach var="lab" items="${fn:split('A,B,C,D,E,F,G', ',') }">
				<c:set var="optionX" value="option${lab }"></c:set>
				<c:if test="${!empty question[optionX] }">
				<input type="radio" name="answer" value="${lab }" title="${lab }" ${fn:contains(question.answer, lab) ? "checked" : ""}>
				</c:if>
				</c:forEach>
				</c:if>
				<c:if test="${question.type == 2 }">
				<c:forEach var="lab" items="${fn:split('A,B,C,D,E,F,G', ',') }">
				<c:set var="optionX" value="option${lab }"></c:set>
				<c:if test="${!empty question[optionX] }">
				<input type="checkbox" name="answer" value="${lab }" title="${lab }" lay-skin="primary" ${fn:contains(question.answer, lab) ? "checked" : ""}>
				</c:if>
				</c:forEach>
				</c:if>
				<c:if test="${question.type == 3 }">
				<div id="answer" class="xm-select-demo"></div>
				<% pageContext.setAttribute("a", "\n"); %>
				<script type="text/javascript">
					xmSelect.render({
						el : "#answer",
						name : "answer",
						initValue: [<c:forEach var="answer" items="${fn:split(question.answer, a) }" varStatus="s">"${answer }",</c:forEach>],
						data : [
								<c:forEach var="answer" items="${fn:split(question.answer, a) }" varStatus="s">
								{name: "${answer }", value: "${answer }", selected: true},
								</c:forEach>
							],
						filterable : true,
						tips : "请添加填空项",
						searchTips : "一个填空是一个标签，如果一个填空有多个同义词，可用三个竖线分开。如：山西|||晋；一般|||通常|||普遍",
						empty : "",
						layVerify: "required",
						layVerType: "msg",
						create : function(val, arr){
							return {
								name: val,
								value: val
							}
						}
					});
					
					var answerSelect = $("#answer").children();
					answerSelect.attr("style", "z-index:20000001;");
				</script>
				</c:if>
				<c:if test="${question.type == 4 }">
				<input type="radio" name="answer" value="对" title="对" ${fn:contains(question.answer, "对") ? "checked" : ""}>
				<input type="radio" name="answer" value="错" title="错" ${fn:contains(question.answer, "错") ? "checked" : ""}>
				</c:if>
				<c:if test="${question.type == 5 }">
				<script id="answer" name="answer" type="text/plain" style="width:100%;height: 50px;">${question.answer }</script>
				<script type="text/javascript">
					UE.delEditor("answer");
					UE.getEditor("answer");
				</script>
				</c:if>
			</div>
		</div>
	</div>
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">解析：</label>
			<div class="layui-input-block">
				<script id="analysis" name="analysis" type="text/plain" style="width:100%;height: 50px;">${question.analysis }</script>
			</div>
		</div>
	</div>
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">默认分值：</label>
			<div class="layui-input-block">
				<input name="score" value="${question.score }" 
					class="layui-input" lay-verify="number" placeholder="请输入默认分值">
			</div>
		</div>
	</div>
	<div id="scoreOptionsRow" class="layui-row layui-form-item" ${(question.type != 2 && question.type != 3) ? "style='display: none;'" : "" }>
		<div class="layui-col-md11">
			<label class="layui-form-label">默认选项：</label>
			<div id="scoreOptionsPanel" class="layui-input-block">
				<c:if test="${question.type == 2 || question.type == 3}">
				<input type="checkbox" lay-filter="scoreOptions" name="scoreOptions" value="1" lay-skin="switch" lay-text="半对半分|全对得分" ${fn:contains(question.scoreOptions, "1") ? "checked" : "" }>
				</c:if>
				<c:if test="${question.type == 3}">
				<input type="checkbox" lay-filter="scoreOptions" name="scoreOptions" value="2" lay-skin="switch" lay-text="答案无顺序|答案有顺序" ${fn:contains(question.scoreOptions, "2") ? "checked" : "" }>
				<input type="checkbox" lay-filter="scoreOptions" name="scoreOptions" value="3" lay-skin="switch" lay-text="大小写不敏感|大小写敏感" ${fn:contains(question.scoreOptions, "3") ? "checked" : "" }>
				<input type="checkbox" lay-filter="scoreOptions" name="scoreOptions" value="4" lay-skin="switch" lay-text="包含答案得分|等于答案得分" ${fn:contains(question.scoreOptions, "4") ? "checked" : "" }>
				</c:if>
			</div>
		</div>
	</div>
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">排序：</label>
			<div class="layui-input-block">
				<input name="no" value="${question.no }" 
					class="layui-input" lay-verify="required|number" placeholder="请输入排序">
			</div>
		</div>
	</div>
	<div class="layui-row layui-form-item">
		<div class="layui-col-md11">
			<label class="layui-form-label">状态：</label>
			<div class="layui-input-block">
				<input type="checkbox" name="state" value="1"
					lay-skin="switch"  lay-text="启用|禁用" ${question.state == 1 ? "checked" : "" }>
				
			</div>
		</div>
	</div>
	<div class="layui-form-item layui-hide">
		<input type="button" lay-submit lay-filter="questionEditBtn" value="确认">
	</div>
</div>
