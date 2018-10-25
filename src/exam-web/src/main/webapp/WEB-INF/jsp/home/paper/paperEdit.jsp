<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
	<head>
		<title>试卷修改</title>
		<%@include file="/script/home/common.jspf"%>
	</head>
	<body>
		<%@include file="/script/home/head.jspf"%>
		<div class="container">
			<div class="row">
				<div class="col-md-3">
					<div id="paperTypeTree" class="exam-tree"></div>
				</div>
				<div class="col-md-9">
					<div class="panel panel-default">
   						<div class="panel-body">
   							<form id="editForm" class="form-horizontal" role="form">
   								<input type="hidden" id="id" name="id" value="${paper.id }" />
								<div class="row">
									<div class="col-md-6">
										<div class="form-group">
											<label for="paperTypeName" class="col-md-4 control-label">试卷分类：</label>
											<div class="col-md-8">
												<input type="hidden" id="paperTypeId" name="paperTypeId" value="${paperType.id }" />
												<input type="text" id="paperTypeName" name="paperTypeName" value="${paperType.name }"
													class="form-control" readonly="readonly" placeholder="请选择左侧试卷分类">
											</div>
										</div>
									</div>
									<div class="col-md-6">
										
									</div>
								</div>
								<div class="row">
									<div class="col-md-6">
										<div class="form-group">
											<label for="name" class="col-md-4 control-label">名称：</label>
											<div class="col-md-8">
												<input type="text" id="name" name="name" value="${paper.name }"
													class="form-control" placeholder="请输入名称">
											</div>
										</div>
									</div>
									<div class="col-md-6">
										<div class="form-group">
											<label for="state" class="col-md-4 control-label">状态：</label>
											<div class="col-md-8">
												<select id="state" name="state" class="form-control">
													<c:forEach var="dict" items="${STATE_DICT }">
													<option value="${dict.dictKey }" ${dict.dictKey == paper.state ? "selected" : ""}>${dict.dictValue }</option>
													</c:forEach>
												</select>
											</div>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-12">
										<div class="form-group">
											<label for="description" class="col-md-2 control-label">描述：</label>
											<div class="col-md-10">
												<textarea id="description" name="description">${paper.description }</textarea>
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
										<c:if test="${empty paper.id }">
										<button type="button" class="btn btn-primary" onclick="doAdd()">
											<span class="glyphicon glyphicon-ok"></span>
											&nbsp;添加
										</button>
										</c:if>
										<c:if test="${!empty paper.id }">
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
		var $paperTypeTree = $("#paperTypeTree");
		var $paperTypeId = $("#paperTypeId");
		var $paperTypeName = $("#paperTypeName");
		var $editForm = $("#editForm");
		var $description = $("#description");
		var $id = $("#id");
		
		var $minKOption = {
			uploadJson : "home/paper/doTempUpload",
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
			initPaperTypeTree();
			initValid();
			initEditor();
		});
		
		//初始化试卷分类树
		function initPaperTypeTree(){
			$.ajax({
				url : "home/paper/paperTypeTreeList",
				success : function(arr) {
					$paperTypeTree.treeview({
						showBorder: false,
						expandIcon: "glyphicon glyphicon-chevron-right",
						collapseIcon: "glyphicon glyphicon-chevron-down",
						nodeIcon: "glyphicon glyphicon-bookmark",
						color: "#428BCA",
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
							$paperTypeId.val(data.ID);
							$paperTypeName.val(data.NAME).change();
						},
						onNodeUnselected : function(event, data) {
							$paperTypeId.val("");
							$paperTypeName.val("").change();
						}
					});
				}
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
					}, paperTypeName : {
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
		
		//完成试卷添加
		function doAdd(){
			if(!valid()){
				return;
			}
			
			$.ajax({
				url : "home/paper/doAdd",
				data : $editForm.serialize(),
				success : function(obj) {
					if (!obj.success) {
						BootstrapDialog.show({
							title : "提示消息",
							message : obj.message,
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
					
					window.location.href = "home/paper/toList";
				}
			});
		}
		
		//完成试卷修改
		function doEdit(){
			if(!valid()){
				return;
			}
			
			$.ajax({
				url : "home/paper/doEdit",
				data : $editForm.serialize(),
				success : function(obj) {
					if (!obj.success) {
						BootstrapDialog.show({
							title : "提示消息",
							message : obj.message,
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
					
					window.location.href = "home/paper/toList";
				}
			});
		}
	</script>
</html>