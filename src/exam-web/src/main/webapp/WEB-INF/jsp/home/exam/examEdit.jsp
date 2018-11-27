<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
	<head>
		<title>考试${empty exam.id ? "添加" : "修改"}</title>
		<%@include file="/script/home/common.jspf"%>
	</head>
	<body>
		<%@include file="/script/home/head.jspf"%>
		<div class="container">
			<div class="row">
				<div class="col-md-3">
					<div id="examTypeTree" class="exam-tree"></div>
				</div>
				<div class="col-md-9">
					<div class="panel panel-default">
   						<div class="panel-body">
   							<form id="editForm" class="form-horizontal" role="form">
   								<input type="hidden" id="id" name="id" value="${exam.id }" />
								<div class="row">
									<div class="col-md-6">
										<div class="form-group">
											<label for="examTypeName" class="col-md-4 control-label">考试分类：</label>
											<div class="col-md-8">
												<input type="hidden" id="examTypeId" name="examTypeId" value="${examType.id }" />
												<input type="text" id="examTypeName" name="examTypeName" value="${examType.name }"
													class="form-control" readonly="readonly" placeholder="请选择左侧考试分类">
											</div>
										</div>
									</div>
									<div class="col-md-6">
										<div class="form-group">
											<label for="name" class="col-md-4 control-label">名称：</label>
											<div class="col-md-8">
												<input type="text" id="name" name="name" value="${exam.name }"
													class="form-control" placeholder="请输入名称">
											</div>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-6">
										<div class="form-group">
											<label for="paperName" class="col-md-4 control-label">试卷：</label>
											<div class="col-md-8">
												<input type="hidden" id="paperId" name="paperId" value="${exam.paperId }">
												<input type="text" id="paperName" name="paperName" value="${paper.name }"
													class="form-control" placeholder="请选择试卷" onclick="toPaperAdd()" readonly="readonly">
											</div>
										</div>
									</div>
									<div class="col-md-6">
										<div class="form-group">
											<label for="passScore" class="col-md-4 control-label">及格分数：</label>
											<div class="col-md-8">
												<input type="text" id="passScore" name="passScore" value="${exam.passScore }"
													class="form-control" placeholder="请输入及格分数">
											</div>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-6">
										<div class="form-group">
											<label for="startTime" class="col-md-4 control-label">考试开始：</label>
											<div class="col-md-8">
												<input type="text" id="startTime" name="startTime" value="${fn:substring(exam.startTime, 0, 19)}"
													class="form-control" placeholder="请输入开始时间" readonly="readonly">
											</div>
										</div>
									</div>
									<div class="col-md-6">
										<div class="form-group">
											<label for="endTime" class="col-md-4 control-label">考试结束：</label>
											<div class="col-md-8">
												<input type="text" id="endTime" name="endTime" value="${fn:substring(exam.endTime, 0, 19)}"
													class="form-control" placeholder="请输入结束时间" readonly="readonly">
											</div>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-6">
										<div class="form-group">
											<label for="markStartTime" class="col-md-4 control-label">判卷开始：</label>
											<div class="col-md-8">
												<input type="text" id="markStartTime" name="markStartTime" value="${fn:substring(exam.markStartTime, 0, 19)}"
													class="form-control" placeholder="请输入开始时间" readonly="readonly">
											</div>
										</div>
									</div>
									<div class="col-md-6">
										<div class="form-group">
											<label for="markEndTime" class="col-md-4 control-label">判卷结束：</label>
											<div class="col-md-8">
												<input type="text" id="markEndTime" name="markEndTime" value="${fn:substring(exam.markEndTime, 0, 19)}"
													class="form-control" placeholder="请输入结束时间" readonly="readonly">
											</div>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-12">
										<div class="form-group">
											<label for="description" class="col-md-2 control-label">描述：</label>
											<div class="col-md-10">
												<textarea id="description" name="description">${exam.description }</textarea>
												<small class="help-block"></small>
											</div>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-12" style="text-align: center;">
										<button type="button" class="btn btn-primary" onclick="javascript:history.back(-1);">
											<span class="glyphicon glyphicon-arrow-left"></span>
											&nbsp;返回
										</button>
										<c:if test="${empty exam.id }">
										<button type="button" class="btn btn-primary" onclick="doAdd()">
											<span class="glyphicon glyphicon-ok"></span>
											&nbsp;添加
										</button>
										</c:if>
										<c:if test="${!empty exam.id }">
										<button type="button" class="btn btn-primary" onclick="doEdit()">
											<span class="glyphicon glyphicon-ok"></span>
											&nbsp;修改
										</button>
										</c:if>
									</div>
								</div>
							</form>
   						</div>
   					</div>
				</div>
			</div>
		</div>
	</body>
	<script type="text/javascript">
		//定义变量
		var $examTypeTree = $("#examTypeTree");
		var $examTypeId = $("#examTypeId");
		var $examTypeName = $("#examTypeName");
		var $editForm = $("#editForm");
		var $paperId = $("#paperId");
		var $paperName = $("#paperName");
		
		var $minKOption = {
			uploadJson : "home/exam/doTempUpload",
			filePostName : "files",
			width : "100%",
			minHeight : 50,
			items : ['justifyleft', 'justifycenter', 'justifyright', 'formatblock', 'fontname', 'fontsize', 
			        '|', 'forecolor', 'hilitecolor', 'bold', 'italic', 'underline', 'strikethrough', 'lineheight', 'removeformat',  
			        '|', 'preview', 'fullscreen', 'code', 
					'|', 'image', 'flash', 'table', 'anchor', 'link', 'unlink'
				],
			afterBlur : function(){this.sync();}
		}
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initExamTypeTree();
			initTime();
			initValid();
			initEditor();
		});
		
		//初始化考试分类树
		function initExamTypeTree(){
			$.ajax({
				url : "home/exam/examTypeTreeList",
				success : function(arr) {
					$examTypeTree.treeview({
						showBorder: false,
						expandIcon: "glyphicon glyphicon-chevron-right",
						collapseIcon: "glyphicon glyphicon-chevron-down",
						nodeIcon: "glyphicon glyphicon-bookmark",
						//color: "#428BCA",
						showTags: true, 
						levels: 3,
						data: generateBootstrapTree(arr, {
							idFiled : "ID",
							textFiled : "NAME", 
							parentField : "PARENT_ID",
							disabledFiled : "DISABLED",
							expandedFiled : "EXPANDED"
						}),
						onNodeSelected : function(event, data) {
							$examTypeId.val(data.ID);
							$examTypeName.val(data.NAME).change();
						},
						onNodeUnselected : function(event, data) {
							$examTypeId.val("");
							$examTypeName.val("").change();
						}
					});
				}
			});
		}
		
		//初始化时间控件
		function initTime(){
			$("#startTime").datetimepicker({
				language : "zh-CN",
				todayHighlight : true,
				todayBtn : true,
				autoclose : true,
				todayHighlight : true,
				format : "yyyy-mm-dd hh:ii:ss"
			});
			$("#endTime").datetimepicker({
				language : "zh-CN",
				todayHighlight : true,
				todayBtn : true,
				autoclose : true,
				todayHighlight : true,
				format : "yyyy-mm-dd hh:ii:ss"
			});
			$("#markStartTime").datetimepicker({
				language : "zh-CN",
				todayHighlight : true,
				todayBtn : true,
				autoclose : true,
				todayHighlight : true,
				format : "yyyy-mm-dd hh:ii:ss"
			});
			$("#markEndTime").datetimepicker({
				language : "zh-CN",
				todayHighlight : true,
				todayBtn : true,
				autoclose : true,
				todayHighlight : true,
				format : "yyyy-mm-dd hh:ii:ss"
			});
		}
		
		//初始化编辑器
		function initEditor(){
			KindEditor.create("#description", $minKOption);
		}
		
		//初始化校验
		function initValid(){
			$editForm.bootstrapValidator({
				excluded : [":disabled"],
				fields : {
					name : {
						validators : {
							notEmpty : {}
						}
					}, examTypeName : {
						trigger : "change",
						validators : {
							notEmpty : {}
						}
					}, paperName : {
						trigger : "change",
						validators : {
							notEmpty : {}
						}
					}, passScore : {
						validators : {
							notEmpty : {
								
							}, regexp : {
								regexp : /^(([1-9][0-9]*)|(([0]\.\d{1,2}|[1-9][0-9]*\.\d{1,2})))$/,
								message : "必须为正数，最多保留两位小数"
							}
						}
					}, startTime : {
						trigger : "change",
						validators : {
							notEmpty : {}
						}
					}, endTime : {
						trigger : "change",
						validators : {
							notEmpty : {}
						}
					}, markStartTime : {
						trigger : "change",
						validators : {
							notEmpty : {}
						}
					}, markEndTime : {
						trigger : "change",
						validators : {
							notEmpty : {}
						}
					}
				}
			});
		}
		
		//校验
		function valid(){
			var bv = $editForm.data('bootstrapValidator');
			bv.validate();
			return bv.isValid();
		}
		
		//完成考试添加
		function doAdd(){
			if(!valid()){
				return;
			}
			
			$.ajax({
				url : "home/exam/doAdd",
				data : $editForm.serialize(),
				success : function(obj) {
					if (!obj.succ) {
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
					
					window.location.href = "home/exam/toList";
				}
			});
		}
		
		//完成考试修改
		function doEdit(){
			if(!valid()){
				return;
			}
			
			$.ajax({
				url : "home/exam/doEdit",
				data : $editForm.serialize(),
				success : function(obj) {
					if (!obj.succ) {
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
					
					window.location.href = "home/exam/toList";
				}
			});
		}
		
		//到达试卷列表页面
		function toPaperAdd(){
			BootstrapDialog.show({
				title : "添加试卷",
				cssClass : "exam-list",
				message : function(dialog) {
					var $message = $("<div></div>");
					var pageToLoad = dialog.getData("pageToLoad");
					$message.load(pageToLoad);
					return $message;
				},
				data : {
					"pageToLoad" : "home/exam/toPaperAdd"
				},
				buttons : [{
					label : "&nbsp;确定",
					icon : "glyphicon glyphicon-ok",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						var nodes = $("#paperTable").bootstrapTable("getSelections");
						if(nodes.length != 1){
							BootstrapDialog.show({
								title : "提示消息",
								message : "请选择一行数据！",
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
						
						$paperId.val(nodes[0].ID);
						$paperName.val(nodes[0].NAME).change();
						dialogItself.close();
					}
				}, {
					label : "&nbsp;取消",
					icon : "glyphicon glyphicon-remove",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						dialogItself.close();
					}
				}],
				onshown : function(){
					var $paperTypeTree = $("#paperTypeTree");
					var $paperTable = $("#paperTable");
					var $paperQueryForm = $("#paperQueryForm");
					var $paperOne = $("#paperOne");
					$.ajax({
						url : "home/exam/paperTypeTreeList",
						success : function(arr) {
							$paperTypeTree.treeview({
								showBorder: false,
								expandIcon: "glyphicon glyphicon-chevron-right",
								collapseIcon: "glyphicon glyphicon-chevron-down",
								nodeIcon: "glyphicon glyphicon-bookmark",
								//color: "#428BCA",
								showTags: true, 
								levels: 3,
								data: generateBootstrapTree(arr, {
									idFiled : "ID",
									textFiled : "NAME", 
									parentField : "PARENT_ID",
									disabledFiled : "DISABLED",
									expandedFiled : "EXPANDED"
								}),
								onNodeSelected : function(event, data) {
									$paperOne.val(data.ID);
									paperQuery();
								},
								onNodeUnselected : function(event, data) {
									$paperOne.val("");
									paperQuery();
								}
							});
						}
					});
					$paperTable.bootstrapTable({
						url : "home/exam/paperList",
						queryParams : function(params){
							var customeParams = $.fn.my.serializeObj($paperQueryForm);
							customeParams.page = this.pageNumber;
							customeParams.rows = this.pageSize;
							return customeParams;
						},
						columns : [ 
									{field : "state", checkbox : true},
									{field : "NAME", title : "名称", width : 50, align : "center"},
									{field : "TOTLE_SCORE", title : "总分数", width : 80, align : "center"},
									{field : "PAPER_TYPE_NAME", title : "试卷分类", width : 50, align : "center"},
									{field : "STATE_NAME", title : "状态", width : 80, align : "center"}
									]
					});
				}
			});
		}
		
		//试卷查询
		function paperQuery(){
			$("#paperTable").bootstrapTable('refresh', {pageNumber : 1});
		}
		
		//试卷重置
		function paperReset(){
			$("#paperQueryForm")[0].reset();
			paperQuery();
		}
	</script>
</html>