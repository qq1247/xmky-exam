<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>考试列表</title>
		<%@include file="/script/home/common.jspf"%>
	</head>
	<body>
		<%@include file="/script/home/head.jspf"%>
		<div class="container">
			<c:if test="${nav }">
			<div class="row">
				<ul class="process process-plus">
					<li onclick="window.location.href='home/question/toList?nav=true'">
						<div class="item passed">
							<div class="step">
								<i>1</i>
								<label>添加试题</label>
							</div>
							<span></span>
						</div>
					</li>
					<li onclick="window.location.href='home/paper/toList?nav=true'">
						<div class="item passed">
							<div class="step">
								<i>2</i>
								<label>添加试卷</label>
							</div>
							<span></span>
						</div>
					</li>
					<li onclick="window.location.href='home/exam/toList?nav=true'">
						<div class="item current">
							<div class="step">
								<i>3</i>
								<label>添加考试</label>
							</div>
							<span style="color: red;"></span>
						</div>
					</li>
				</ul>
			</div>
			</c:if>
			<div class="row">
				<div class="col-md-3">
					<div id="examTypeTree" class="exam-tree"></div>
				</div>
				<div class="col-md-9">
					<div class="panel panel-default exam-query">
						<div class="panel-body">
							<form id="queryForm" class="form-horizontal" role="form">
								<input type="hidden" id="one" name="one"/>
								<div class="row">
									<div class="col-md-4">
										<div class="form-group">
											<label for="two" class="control-label col-md-4">名称：</label>
											<div class="col-md-8">
												<input type="text" id="two" name="two" class="form-control" placeholder="请输入名称">
											</div>
										</div>
									</div>
									<div class="col-md-4">
										<div class="form-group">
											<label for="three" class="control-label col-md-4">试卷：</label>
											<div class="col-md-8">
												<input type="text" id="three" name="three" class="form-control" placeholder="请输入试卷">
											</div>
										</div>
									</div>
									<div class="col-md-4" style="text-align: right;">
										<div class="form-group">
											<label for="four" class="control-label col-md-4">状态：</label>
											<div class="col-md-8">
												<select id="four" name="four" class="form-control">
													<option value=""></option>
													<c:forEach var="dict" items="${STATE_DICT }">
													<option value="${dict.dictKey }">${dict.dictValue }</option>
													</c:forEach>
												</select>
											</div>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-4">
										<div class="form-group">
											<label for="five" class="control-label col-md-4">考试开始大于：</label>
											<div class="col-md-8">
												<input type="text" id="five" name="five" class="form-control" placeholder="请输入开始时间">
											</div>
										</div>
									</div>
									<div class="col-md-4">
										<div class="form-group">
											<label for="six" class="control-label col-md-4">考试开始小于：</label>
											<div class="col-md-8">
												<input type="text" id="six" name="six" class="form-control" placeholder="请输入结束时间">
											</div>
										</div>
									</div>
									<div class="col-md-4" style="text-align: right;">
										<button type="button" class="btn btn-primary" onclick="query();">
											<span class="glyphicon glyphicon-search"></span>
											&nbsp;查询
										</button>
										<button type="button" class="btn btn-primary" onclick="reset();">
											<span class="glyphicon glyphicon-repeat"></span>
											&nbsp;重置
										</button>
									</div>
								</div>
							</form>
						</div>
					</div>
					<div class="panel panel-default exam-list">
						<div class="panel-body">
							<div id="toolbar">
								<button type="button" class="btn btn-primary" onclick="toAdd();">
									<span class="glyphicon glyphicon-plus"></span>
									&nbsp;添加
								</button>
								<button type="button" class="btn btn-primary" onclick="toEdit();">
									<span class="glyphicon glyphicon-pencil"></span>
									&nbsp;修改
								</button>
								<button type="button" class="btn btn-primary" onclick="doDel();">
									<span class="glyphicon glyphicon-trash"></span>
									&nbsp;删除
								</button>
								<button type="button" class="btn btn-primary" onclick="doPublish();">
									<span class="glyphicon glyphicon-ok"></span>
									&nbsp;发布
								</button>
								<button type="button" class="btn btn-primary" onclick="toExamUserList();">
									<span class="glyphicon glyphicon-user"></span>
									&nbsp;考试用户
								</button>
								<button type="button" class="btn btn-primary" onclick="toMarkUserList();">
									<span class="glyphicon glyphicon-user"></span>
									&nbsp;判卷用户
								</button>
							</div>
							<table id="table"></table>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
	<script type="text/javascript">
		//定义变量
		var $examTypeTree = $("#examTypeTree");
		var $table = $("#table");
		var $queryForm = $("#queryForm");
		var $one = $("#one");
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initTime();
			initExamTypeTree();
			initTable();
		});
		
		//初始化时间控件
		function initTime(){
			$('#five').datetimepicker({
				language : "zh-CN",
				format : "yyyy-mm-dd hh:ii:ss"
			});
			$('#six').datetimepicker({
				language : "zh-CN",
				format : "yyyy-mm-dd hh:ii:ss"
			});
		}
		
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
							$one.val(data.ID);
							query();
						},
						onNodeUnselected : function(event, data) {
							$one.val("");
							query();
						}
					});
				}
			});
		}
		
		//初始化列表
		function initTable(){
			$table.bootstrapTable({
				url : "home/exam/list",
				queryParams : function(params){
					var customeParams = $.fn.my.serializeObj($queryForm);
					customeParams.page = this.pageNumber;
					customeParams.rows = this.pageSize;
					return customeParams;
				},
				columns : [ 
							{field : "state", checkbox : true}, 
							{field : "NAME", title : "名称", width : 160, align : "center"},
							{field : "PAPER_NAME", title : "试卷", width : 160, align : "center"},
							{field : "PASS_SCORE", title : "及格分数", width : 80, align : "center", 
								formatter : function(value, row, index){
									return row.PASS_SCORE + "/" + row.PAPER_TOTLE_SCORE;
								}
							},
							{field : "START_TIME_STR", title : "考试时间", width : 160, align : "center", 
								formatter : function(value, row, index){
									return row.START_TIME_STR + "<br/>" + row.END_TIME_STR;
								}
							},
							{field : "MARK_START_TIME_STR", title : "判卷时间", width : 160, align : "center", 
								formatter : function(value, row, index){
									return row.MARK_START_TIME_STR + "<br/>" + row.MARK_END_TIME_STR;
								}
							},
							{field : "STATE_NAME", title : "状态", width : 80, align : "center"}
							],
				toolbar : "#toolbar"
			});
		}
		
		//查询
		function query(){
			$table.bootstrapTable('refresh', {pageNumber : 1});
		}
		
		//重置
		function reset(){
			$queryForm[0].reset();
			query();
		}
		
		//到达添加考试页面
		function toAdd(){
			var treeNodes = $examTypeTree.treeview("getSelected");
			if(treeNodes.length != 1){
				BootstrapDialog.show({
					title : "提示消息",
					message : "请选择考试分类",
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
			
			window.location.href = "home/exam/toAdd?examTypeId=" + treeNodes[0].ID;
		}
		
		//到达修改考试页面
		function toEdit(){
			var nodes = $table.bootstrapTable("getSelections");
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
			
			if(nodes[0].STATE == 1){
				BootstrapDialog.show({
					title : "提示消息",
					message : "考试已发布！",
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
			
			window.location.href = "home/exam/toEdit?id=" + nodes[0].ID;
		}
		
		//完成考试删除
		function doDel(){
			var nodes = $table.bootstrapTable("getSelections");
			if(nodes.length == 0){
				BootstrapDialog.show({
					title : "提示消息",
					message : "请选择一行或多行数据！",
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
			
			BootstrapDialog.show({
				title : "提示消息",
				message : "确定删除？",
				buttons : [{
					label : "&nbsp;确定",
					icon : "glyphicon glyphicon-ok",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						$.ajax({
							url : "home/exam/doDel",
							data : $.fn.my.serializeField(nodes),
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
				}, {
					label : "&nbsp;取消",
					icon : "glyphicon glyphicon-remove",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						dialogItself.close();
					}
				}]
			});
		}
		
		//完成考试发布
		function doPublish(){
			var nodes = $table.bootstrapTable("getSelections");
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
			
			if(nodes[0].STATE == 1){
				BootstrapDialog.show({
					title : "提示消息",
					message : "考试已发布！",
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
			
			BootstrapDialog.show({
				title : "提示消息",
				message : "确定发布？",
				buttons : [{
					label : "&nbsp;确定",
					icon : "glyphicon glyphicon-ok",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						$.ajax({
							url : "home/exam/doPublish",
							data : {
								id : nodes[0].ID
							},
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
				}, {
					label : "&nbsp;取消",
					icon : "glyphicon glyphicon-remove",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						dialogItself.close();
					}
				}]
			});
		}
		
		//到达考试用户列表页面
		function toExamUserList(){
			var nodes = $table.bootstrapTable("getSelections");
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
			
			if(nodes[0].STATE != 1){
				BootstrapDialog.show({
					title : "提示消息",
					message : "考试未发布！",
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
			
			BootstrapDialog.show({
				title : "考试用户列表",
				cssClass : "exam-list",
				message : function(dialog) {
					var $message = $("<div></div>");
					var pageToLoad = dialog.getData("pageToLoad");
					$message.load(pageToLoad);
					return $message;
				},
				data : {
					"pageToLoad" : "home/exam/toExamUserList?id=" + nodes[0].ID
				},
				buttons : [{/* 
					label : "&nbsp;取消",
					icon : "glyphicon glyphicon-remove",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						dialogItself.close();
					} */
				}],
				onshown : function(){
					var $examUserOrgTree = $("#examUserOrgTree");
					var $examUserTable = $("#examUserTable");
					var $examUserQueryForm = $("#examUserQueryForm");
					var $examUserOne = $("#examUserOne");
					$.ajax({
						url : "home/exam/examUserOrgTreeList",
						success : function(arr) {
							$examUserOrgTree.treeview({
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
									$examUserOne.val(data.ID);
									examUserQuery();
								},
								onNodeUnselected : function(event, data) {
									$examUserOne.val("");
									examUserQuery();
								}
							});
						}
					});
					$examUserTable.bootstrapTable({
						url : "home/exam/examUserList",
						queryParams : function(params){
							var customeParams = $.fn.my.serializeObj($examUserQueryForm);
							customeParams.page = this.pageNumber;
							customeParams.rows = this.pageSize;
							return customeParams;
						},
						columns : [ 
									{field : "state", checkbox : true}, 
									{field : "USER_NAME", title : "姓名", width : 80, align : "center"}, 
									{field : "LOGIN_NAME", title : "登录名称", width : 80, align : "center"}, 
									{field : "ORG_NAME", title : "组织机构", width : 80, align : "center"},
									{field : "POST_NAMES", title : "岗位", width : 80, align : "center"},
									]
					});
				}
			});
		}
		
		//考试用户查询
		function examUserQuery(){
			$("#examUserTable").bootstrapTable('refresh', {pageNumber : 1});
		}
		
		//考试用户重置
		function examUserReset(){
			$("#examUserQueryForm")[0].reset();
			examUserQuery();
		}
		
		//到达添加考试用户页面
		function toExamUserAdd(){
			BootstrapDialog.show({
				title : "添加考试用户",
				cssClass : "exam-list",
				message : function(dialog) {
					var $message = $("<div></div>");
					var pageToLoad = dialog.getData("pageToLoad");
					$message.load(pageToLoad);
					return $message;
				},
				data : {
					"pageToLoad" : "home/exam/toExamUserAdd?id=" + $("#examUserTen").val()
				},
				buttons : [{
					label : "&nbsp;确定",
					icon : "glyphicon glyphicon-ok",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						var nodes = $("#examUserAddTable").bootstrapTable("getSelections");
						if(nodes.length == 0){
							BootstrapDialog.show({
								title : "提示消息",
								message : "请选择一行或多行数据！",
								buttons : [{
									label : "&nbsp;确定",
									icon : "glyphicon glyphicon-ok",
									cssClass : "btn-primary",
									action : function(dialogItself2) {
										dialogItself2.close();
									}
								}]
							});
							return;
						}
						
						BootstrapDialog.show({
							title : "提示消息",
							message : "确定添加？",
							buttons : [{
								label : "&nbsp;确定",
								icon : "glyphicon glyphicon-ok",
								cssClass : "btn-primary",
								action : function(dialogItself2) {
									$.ajax({
										url : "home/exam/doExamUserAdd",
										data : $.fn.my.serializeField(nodes, {attrName : "userIds"}) + "&id=" + $("#examUserAddTen").val(),
										success : function(obj) {
											if (!obj.succ) {
												BootstrapDialog.show({
													title : "提示消息",
													message : obj.msg,
													buttons : [{
														label : "&nbsp;确定",
														icon : "glyphicon glyphicon-ok",
														cssClass : "btn-primary",
														action : function(dialogItself3) {
															dialogItself3.close();
														}
													}]
												});
												return;
											}
											
											examUserQuery();
											dialogItself2.close();
											dialogItself.close();
										}
									});
								}
							}, {
								label : "&nbsp;取消",
								icon : "glyphicon glyphicon-remove",
								cssClass : "btn-primary",
								action : function(dialogItself2) {
									dialogItself2.close();
								}
							}]
						});
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
					var $examUserAddOrgTree = $("#examUserAddOrgTree");
					var $examUserAddTable = $("#examUserAddTable");
					var $examUserAddQueryForm = $("#examUserAddQueryForm");
					var $examUserAddOne = $("#examUserAddOne");
					$.ajax({
						url : "home/exam/examUserOrgTreeList",
						success : function(arr) {
							$examUserAddOrgTree.treeview({
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
									$examUserAddOne.val(data.ID);
									examUserAddQuery();
								},
								onNodeUnselected : function(event, data) {
									$examUserAddOne.val("");
									examUserAddQuery();
								}
							});
						}
					});
					$examUserAddTable.bootstrapTable({
						url : "home/exam/examUserAddList",
						queryParams : function(params){
							var customeParams = $.fn.my.serializeObj($examUserAddQueryForm);
							customeParams.page = this.pageNumber;
							customeParams.rows = this.pageSize;
							return customeParams;
						},
						columns : [ 
									{field : "state", checkbox : true}, 
									{field : "USER_NAME", title : "姓名", width : 80, align : "center"}, 
									{field : "LOGIN_NAME", title : "登录名称", width : 80, align : "center"}, 
									{field : "ORG_NAME", title : "组织机构", width : 80, align : "center"},
									{field : "POST_NAMES", title : "岗位", width : 80, align : "center"},
									]
					});
				}
			});
		}
		
		//添加考试用户查询
		function examUserAddQuery(){
			$("#examUserAddTable").bootstrapTable('refresh', {pageNumber : 1});
		}
		
		//添加考试用户重置
		function examUserAddReset(){
			$("#examUserAddQueryForm")[0].reset();
			examUserAddQuery();
		}
		
		//完成删除考试用户
		function doExamUserDel(){
			var nodes = $("#examUserTable").bootstrapTable("getSelections");
			if(nodes.length == 0){
				BootstrapDialog.show({
					title : "提示消息",
					message : "请选择一行或多行数据！",
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
			
			BootstrapDialog.show({
				title : "提示消息",
				message : "确定删除？",
				buttons : [{
					label : "&nbsp;确定",
					icon : "glyphicon glyphicon-ok",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						$.ajax({
							url : "home/exam/doExamUserDel",
							data : $.fn.my.serializeField(nodes, {attrName : "examUserIds"}),
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
								
								examUserQuery();
								dialogItself.close();
							}
						});
					}
				}, {
					label : "&nbsp;取消",
					icon : "glyphicon glyphicon-remove",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						dialogItself.close();
					}
				}]
			});
		}
		
		//到达判卷用户列表页面
		function toMarkUserList(){
			var nodes = $table.bootstrapTable("getSelections");
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
			
			if(nodes[0].STATE != 1){
				BootstrapDialog.show({
					title : "提示消息",
					message : "考试未发布！",
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
			
			BootstrapDialog.show({
				title : "判卷用户列表",
				cssClass : "exam-list",
				message : function(dialog) {
					var $message = $("<div></div>");
					var pageToLoad = dialog.getData("pageToLoad");
					$message.load(pageToLoad);
					return $message;
				},
				data : {
					"pageToLoad" : "home/exam/toMarkUserList?id=" + nodes[0].ID
				},
				buttons : [{/* 
					label : "&nbsp;取消",
					icon : "glyphicon glyphicon-remove",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						dialogItself.close();
					} */
				}],
				onshown : function(){
					var $markUserOrgTree = $("#markUserOrgTree");
					var $markUserTable = $("#markUserTable");
					var $markUserQueryForm = $("#markUserQueryForm");
					var $markUserOne = $("#markUserOne");
					$.ajax({
						url : "home/exam/markUserOrgTreeList",
						success : function(arr) {
							$markUserOrgTree.treeview({
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
									$markUserOne.val(data.ID);
									markUserQuery();
								},
								onNodeUnselected : function(event, data) {
									$markUserOne.val("");
									markUserQuery();
								}
							});
						}
					});
					$markUserTable.bootstrapTable({
						url : "home/exam/markUserList",
						queryParams : function(params){
							var customeParams = $.fn.my.serializeObj($markUserQueryForm);
							customeParams.page = this.pageNumber;
							customeParams.rows = this.pageSize;
							return customeParams;
						},
						columns : [ 
									{field : "state", checkbox : true}, 
									{field : "USER_NAME", title : "姓名", width : 80, align : "center"}, 
									{field : "LOGIN_NAME", title : "登录名称", width : 80, align : "center"}, 
									{field : "ORG_NAME", title : "组织机构", width : 80, align : "center"},
									{field : "POST_NAMES", title : "岗位", width : 80, align : "center"},
									]
					});
				}
			});
		}
		
		//判卷用户查询
		function markUserQuery(){
			$("#markUserTable").bootstrapTable('refresh', {pageNumber : 1});
		}
		
		//判卷用户重置
		function markUserReset(){
			$("#markUserQueryForm")[0].reset();
			markUserQuery();
		}
		
		//到达添加判卷用户页面
		function toMarkUserAdd(){
			BootstrapDialog.show({
				title : "添加判卷用户",
				cssClass : "exam-list",
				message : function(dialog) {
					var $message = $("<div></div>");
					var pageToLoad = dialog.getData("pageToLoad");
					$message.load(pageToLoad);
					return $message;
				},
				data : {
					"pageToLoad" : "home/exam/toMarkUserAdd?id=" + $("#markUserTen").val()
				},
				buttons : [{
					label : "&nbsp;确定",
					icon : "glyphicon glyphicon-ok",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						var nodes = $("#markUserAddTable").bootstrapTable("getSelections");
						if(nodes.length == 0){
							BootstrapDialog.show({
								title : "提示消息",
								message : "请选择一行或多行数据！",
								buttons : [{
									label : "&nbsp;确定",
									icon : "glyphicon glyphicon-ok",
									cssClass : "btn-primary",
									action : function(dialogItself2) {
										dialogItself2.close();
									}
								}]
							});
							return;
						}
						
						BootstrapDialog.show({
							title : "提示消息",
							message : "确定添加？",
							buttons : [{
								label : "&nbsp;确定",
								icon : "glyphicon glyphicon-ok",
								cssClass : "btn-primary",
								action : function(dialogItself2) {
									$.ajax({
										url : "home/exam/doMarkUserAdd",
										data : $.fn.my.serializeField(nodes, {attrName : "userIds"}) + "&id=" + $("#markUserAddTen").val(),
										success : function(obj) {
											if (!obj.succ) {
												BootstrapDialog.show({
													title : "提示消息",
													message : obj.msg,
													buttons : [{
														label : "&nbsp;确定",
														icon : "glyphicon glyphicon-ok",
														cssClass : "btn-primary",
														action : function(dialogItself3) {
															dialogItself3.close();
														}
													}]
												});
												return;
											}
											
											markUserQuery();
											dialogItself2.close();
											dialogItself.close();
										}
									});
								}
							}, {
								label : "&nbsp;取消",
								icon : "glyphicon glyphicon-remove",
								cssClass : "btn-primary",
								action : function(dialogItself2) {
									dialogItself2.close();
								}
							}]
						});
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
					var $markUserAddOrgTree = $("#markUserAddOrgTree");
					var $markUserAddTable = $("#markUserAddTable");
					var $markUserAddQueryForm = $("#markUserAddQueryForm");
					var $markUserAddOne = $("#markUserAddOne");
					$.ajax({
						url : "home/exam/markUserOrgTreeList",
						success : function(arr) {
							$markUserAddOrgTree.treeview({
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
									$markUserAddOne.val(data.ID);
									markUserAddQuery();
								},
								onNodeUnselected : function(event, data) {
									$markUserAddOne.val("");
									markUserAddQuery();
								}
							});
						}
					});
					$markUserAddTable.bootstrapTable({
						url : "home/exam/markUserAddList",
						queryParams : function(params){
							var customeParams = $.fn.my.serializeObj($markUserAddQueryForm);
							customeParams.page = this.pageNumber;
							customeParams.rows = this.pageSize;
							return customeParams;
						},
						columns : [ 
									{field : "state", checkbox : true}, 
									{field : "USER_NAME", title : "姓名", width : 80, align : "center"}, 
									{field : "LOGIN_NAME", title : "登录名称", width : 80, align : "center"}, 
									{field : "ORG_NAME", title : "组织机构", width : 80, align : "center"},
									{field : "POST_NAMES", title : "岗位", width : 80, align : "center"},
									]
					});
				}
			});
		}
		
		//添加判卷用户查询
		function markUserAddQuery(){
			$("#markUserAddTable").bootstrapTable('refresh', {pageNumber : 1});
		}
		
		//添加判卷用户重置
		function markUserAddReset(){
			$("#markUserAddQueryForm")[0].reset();
			markUserAddQuery();
		}
		
		//完成删除判卷用户
		function doMarkUserDel(){
			var nodes = $("#markUserTable").bootstrapTable("getSelections");
			if(nodes.length == 0){
				BootstrapDialog.show({
					title : "提示消息",
					message : "请选择一行或多行数据！",
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
			
			BootstrapDialog.show({
				title : "提示消息",
				message : "确定删除？",
				buttons : [{
					label : "&nbsp;确定",
					icon : "glyphicon glyphicon-ok",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						$.ajax({
							url : "home/exam/doMarkUserDel",
							data : $.fn.my.serializeField(nodes, {attrName : "markUserIds"}),
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
								
								markUserQuery();
								dialogItself.close();
							}
						});
					}
				}, {
					label : "&nbsp;取消",
					icon : "glyphicon glyphicon-remove",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						dialogItself.close();
					}
				}]
			});
		}
	</script>
</html>