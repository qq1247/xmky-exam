<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>考试列表</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
		<div class="layui-fluid">
			<div class="layui-row layui-col-space10">
				<div class="layui-col-md2">
					<div class="layui-card">
						<div class="layui-form">
			      			<ul id="examTypeTree" class="ztree"></ul>
			 			</div>
					</div>
				</div>
				<div class="layui-col-md10">
					<div class="layui-card">
					<%-- 考试查询条件 --%>
					<form id="examQueryForm" class="layui-form layui-card-header layuiadmin-card-header-auto">
						<input type="hidden" id="examOne" name="one">
						<div class="layui-form-item ">
							<div class="layui-inline">
								<input type="text" name="three" placeholder="请输入名称" class="layui-input">
							</div>
							<div class="layui-inline">
								<button type="button" class="layui-btn layuiadmin-btn-useradmin" onclick="examQuery();">
									<i class="layui-icon layuiadmin-button-btn"></i>查询
								</button>
								<button type="button" class="layui-btn layui-btn-primary layuiadmin-btn-useradmin" onclick="examReset();">
									<i class="layui-icon layuiadmin-button-btn"></i>重置
								</button>
							</div>
						</div>
					</form>
					<div class="layui-card-body">
						<div style="padding-bottom: 10px;">
							<my:auth url="exam/toAdd"><button class="layui-btn layuiadmin-btn-useradmin" onclick="toExamAdd();">添加</button></my:auth>
						</div>
						<script type="text/html" id="examToolbar">
							<my:auth url="exam/toEdit"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="examEdit"><i class="layui-icon layui-icon-edit"></i>修改</a></my:auth>
							<my:auth url="exam/toCfg"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="examCfg"><i class="layui-icon layui-icon-edit"></i>考试配置</a></my:auth>
							<my:auth url="exam/doDel"><a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="examDel"><i class="layui-icon layui-icon-delete"></i>删除</a></my:auth>
						</script>
						<%-- 考试数据表格 --%>
						<table id="examTable" lay-filter="examTable"></table>
					</div>
				</div>
				</div>
			</div>
		</div>
	</body>
	<%@include file="/script/myJs/tail.jspf"%>
	<script type="text/javascript">
		//定义变量
		var examQueryForm = $("#examQueryForm"); //考试查询对象
		var examTypeTree; //考试分类树对象
		var curSelExamTypeId = ""; //当前选中的考试分类ID
		var curSelExamTypeName = ""; //当前选中的考试分类名称
		
		var remarkOptionLabs = ["A", "B", "C", "D", "E"];//评语选项标签
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initExamTable();
			initExamTypeTree();
		});
	
		//初始化考试表格
		function initExamTable() {
			layui.table.render({
				elem : "#examTable",
				url : "exam/list",
				cols : [[
						{field : "NAME", title : "名称", align : "center"},
						{field : "PAPER_NAME", title : "试卷名称", align : "center"},
						{field : "PASS_SCORE", title : "及格分数", align : "center"},
						{field : "START_TIME_STR", title : "考试时间", align : "center"},
						{field : "MARK_START_TIME_STR", title : "阅卷时间", align : "center"},
						{field : "STATE_NAME", title : "状态", align : "center", templet : function(d) {
							return '<input type="checkbox" name="state" value="'+d.ID+'" '
								+ 'lay-skin="switch" lay-text="已发布|未发布" lay-filter="examPublish"' + (d.STATE == 1 ? 'checked' : '') + '>'
						}},
						{fixed : "right", title : "操作 ", toolbar : "#examToolbar", align : "center", width : 280}
						]],
				page : true,
				height : "full-180",
				method : "post",
				defaultToolbar : [],
				parseData : function(exam) {
					return {
						"code" : exam.succ,
						"msg" : exam.msg,
						"count" : exam.data.total,
						"data" : exam.data.rows
					};
				},
				request : {
					pageName: "curPage",
					limitName: "pageSize"
				}, 
				response : {
					statusCode : true
				}
			});
			layui.table.on("rowDouble(examTable)", function(obj) {
				<my:auth url="exam/toEdit">toExamEdit(obj.data.ID);</my:auth>
			});
			layui.table.on("tool(examTable)", function(obj) {
				var data = obj.data;
				if (obj.event === "examEdit") {
					toExamEdit(obj.data.ID);
				} else if (obj.event === "examDel") {
					doExamDel(obj.data.ID);
				} else if (obj.event === "examCfg") {
					toExamCfg(obj.data.ID);
				}
			});
			layui.form.on("switch(examPublish)", function(obj) {
				doExamPublish(obj.value);
			});
		}
		
		//初始化考试分类树
		function initExamTypeTree() {
			examTypeTree = $.fn.zTree.init($("#examTypeTree"), {
				async : {
					url : "exam/examTypeTreeList",
					enable : true,
					dataFilter : ztreeDataFilter
				},
				callback : {
					onClick : function(event, treeId, treeNode) {
						curSelExamTypeId = treeNode.ID;
						curSelExamTypeName = treeNode.NAME;
						$("#examOne").val(curSelExamTypeId);
						examQuery();
					},
					onAsyncSuccess : function(event, treeId, msg, treeNode) {
						var examTypeTree = $.fn.zTree.getZTreeObj(treeId);
						examTypeTree.expandAll(true);
						
						if (!curSelExamTypeId) {
							var rootNode = examTypeTree.getNodesByFilter(function(node) { return (node.level == 0); }, true);
							examTypeTree.selectNode(rootNode);
							
							curSelExamTypeId = rootNode.ID;
							curSelExamTypeName = rootNode.NAME;
							$("#examOne").val(curSelExamTypeId);
							return;
						}
						
						var curNode = examTypeTree.getNodeByParam("id", curSelExamTypeId, null);
						examTypeTree.selectNode(curNode);
						
						examQuery();
					}
				}
			});
			
			$("#examTypeTree").height($(window).height() - 45);
		}
		
		//考试查询
		function examQuery() {
			layui.table.reload("examTable", {"where" : $.fn.my.serializeObj(examQueryForm)});
		}
	
		//考试重置
		function examReset() {
			examQueryForm[0].reset();
			examQuery();
		}
		
		//到达添加考试页面
		function toExamAdd() {
			if (!curSelExamTypeId) {
				layer.alert("请选择考试分类！", {"title" : "提示消息"});
				return;
			}
			
			$.ajax({
				url : "exam/toAdd",
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "添加考试",
						area : ["800px", "500px"],
						content : obj,
						btn : ["添加", "取消"],
						type : 1,
						yes : function(index, layero) {
							doExamAdd(index);
						},
						success: function(layero, index) {
							var startTime = layui.laydate.render({
								elem : "#startTime",
								type : "datetime",
								min : 0,
								done : function(value, date) {
									endTime.config.min = lay.extend({}, date, {month : date.month - 1});
									endTime.config.elem[0].focus();
								}
							});
							var endTime = layui.laydate.render({
								elem : "#endTime",
								type : "datetime",
								min : 0,
								done : function(value, date) {
									startTime.config.max = lay.extend({}, date, {month : date.month - 1});
								}
							});
							var markStartTime = layui.laydate.render({
								elem : "#markStartTime",
								type : "datetime",
								min : 0,
								done : function(value, date) {
									markEndTime.config.min = lay.extend({}, date, {month : date.month - 1});
									markEndTime.config.elem[0].focus();
								}
							});
							var markEndTime = layui.laydate.render({
								elem : "#markEndTime",
								type : "datetime",
								min : 0,
								done : function(value, date) {
									markStartTime.config.max = lay.extend({}, date, {month : date.month - 1});
								}
							});
							
							var first = true;
							var paperSelect = xmSelect.render({
								el : "#paperId",
								name : "paperId",
								filterable : true,
								paging : true,
								pageRemote : true,
								radio : true,
								clickClose : true,
								//tips : "请输入试卷名称",
								searchTips : "可模糊搜索试卷名称、试卷分类名称",
								layVerify : "required",
								model : {label: {
											type: "text",
											text: {left: "", right: "", separator: "", },
										}
								},
								// pageSize : 5,
								on : function(data) {
									if (first) {
										first = false;
										return;
									}
									$("#passScore").val(data.arr[0].passScore);
									
									var scoreRows = $("[lay-filter='examEditFrom'] .layui-icon-subtraction").parent().parent().parent();
									scoreRows.remove();
									
									$("input[name='scoreA']").val(data.arr[0].scoreA);
									$("input[name='scoreARemark']").val(data.arr[0].scoreARemark);
									$("input[name='scoreB']").val(data.arr[0].scoreB);
									$("input[name='scoreBRemark']").val(data.arr[0].scoreBRemark);
									
									var addScoreRemarkBtn = $("#addScoreRemarkBtn");
									if (data.arr[0].scoreC != null) {
										addScoreRemark(addScoreRemarkBtn);
										$("input[name='scoreC']").val(data.arr[0].scoreC);
										$("input[name='scoreCRemark']").val(data.arr[0].scoreCRemark);
									}
									if (data.arr[0].scoreD != null) {
										addScoreRemark(addScoreRemarkBtn);
										$("input[name='scoreD']").val(data.arr[0].scoreD);
										$("input[name='scoreDRemark']").val(data.arr[0].scoreDRemark);
									}
									if (data.arr[0].scoreE != null) {
										addScoreRemark(addScoreRemarkBtn);
										$("input[name='scoreE']").val(data.arr[0].scoreE);
										$("input[name='scoreERemark']").val(data.arr[0].scoreERemark);
									}
									
								},
								remoteMethod : function(val, cb, show, pageIndex) {
									$.ajax({
										url : "exam/paperList",
										data : {
											two : val,
											curPage : pageIndex,
											pageSize : 5
										},
										async : true,
										success : function(obj) {
											var papers = [];
											for (var i in obj.data.rows) {
												papers.push({
													name :  obj.data.rows[i].NAME+"-"+obj.data.rows[i].PAPER_TYPE_NAME,
													value : obj.data.rows[i].ID,
													passScore : obj.data.rows[i].PASS_SCORE,
													scoreA : obj.data.rows[i].SCORE_A,
													scoreARemark : obj.data.rows[i].SCORE_A_REMARK,
													scoreB : obj.data.rows[i].SCORE_B,
													scoreBRemark : obj.data.rows[i].SCORE_B_REMARK,
													scoreC : obj.data.rows[i].SCORE_C,
													scoreCRemark : obj.data.rows[i].SCORE_C_REMARK,
													scoreD : obj.data.rows[i].SCORE_D,
													scoreDRemark : obj.data.rows[i].SCORE_D_REMARK,
													scoreE : obj.data.rows[i].SCORE_E,
													scoreERemark : obj.data.rows[i].SCORE_E_REMARK
												});
											}
											cb(papers, Math.ceil(obj.data.total / 5));
										}
									});
								}
							});
							
							$("#examTypeId").val(curSelExamTypeId);
							$("#examTypeName").val(curSelExamTypeName);
							UE.delEditor("description");
							UE.getEditor("description");
							layui.form.render(null, "examEditFrom");
						}
					});
				}
			});
		}

		//完成添加考试
		function doExamAdd(examAddDialogIndex) {
			layui.form.on("submit(examEditBtn)", function(data) {
				layer.confirm("确定要添加？", function(index) {
					var descriptionUE = UE.getEditor("description");
					if (descriptionUE.hasContents()) {
						data.field.description = descriptionUE.getContent();
					}

					$.ajax({
						url : "exam/doAdd",
						data : data.field,
						success : function(obj) {
							examQuery();

							if (!obj.succ) {
								layer.alert(obj.msg, {
									"title" : "提示消息"
								});
								return;
							}

							layer.close(index);
							layer.close(examAddDialogIndex);
						}
					});
				});
			});
			$("[lay-filter='examEditBtn']").click();
		}

		//到达修改考试页面
		function toExamEdit(id) {
			$.ajax({
				url : "exam/toEdit",
				data : {
					id : id
				},
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "修改考试",
						area : [ "800px", "500px" ],
						content : obj,
						btn : [ "修改", "取消" ],
						type : 1,
						yes : function(index, layero) {
							doExamEdit(index);
						},
						success : function(layero, index) {
							var startTime = layui.laydate.render({
								elem : "#startTime",
								type : "datetime",
								min : 0,
								value : $("#startTime").val(),
								done : function(value, date) {
									endTime.config.min = lay.extend({}, date, {month : date.month - 1});
									endTime.config.elem[0].focus();
								}
							});
							var endTime = layui.laydate.render({
								elem : "#endTime",
								type : "datetime",
								min : 0,
								value : $("#endTime").val(),
								done : function(value, date) {
									startTime.config.max = lay.extend({}, date, {month : date.month - 1});
								}
							});
							var markStartTime = layui.laydate.render({
								elem : "#markStartTime",
								type : "datetime",
								min : 0,
								value : $("#markStartTime").val(),
								done : function(value, date) {
									markEndTime.config.min = lay.extend({}, date, {month : date.month - 1});
									markEndTime.config.elem[0].focus();
								}
							});
							var markEndTime = layui.laydate.render({
								elem : "#markEndTime",
								type : "datetime",
								min : 0,
								value : $("#markEndTime").val(),
								done : function(value, date) {
									markStartTime.config.max = lay.extend({}, date, {month : date.month - 1});
								}
							});
							
							var first = true;
							var paperSelect = xmSelect.render({
								el : "#paperId",
								name : "paperId",
								filterable : true,
								paging : true,
								pageRemote : true,
								radio: true,
								clickClose: true,
								tips : "请输入试卷名称",
								searchTips : "请输入试卷名称",
								layVerify : "required",
								model: {label: {
										type: "text",
										text: {left: "", right: "", separator: "", },
									}
								},
								// pageSize : 5,
								on : function(data) {
									if (first) {
										first = false;
										return;
									}
									$("#passScore").val(data.arr[0].passScore);
									
									var scoreRows = $("[lay-filter='examEditFrom'] .layui-icon-subtraction").parent().parent().parent();
									scoreRows.remove();
									
									$("input[name='scoreA']").val(data.arr[0].scoreA);
									$("input[name='scoreARemark']").val(data.arr[0].scoreARemark);
									$("input[name='scoreB']").val(data.arr[0].scoreB);
									$("input[name='scoreBRemark']").val(data.arr[0].scoreBRemark);
									
									var addScoreRemarkBtn = $("#addScoreRemarkBtn");
									if (data.arr[0].scoreC != null) {
										addScoreRemark(addScoreRemarkBtn);
										$("input[name='scoreC']").val(data.arr[0].scoreC);
										$("input[name='scoreCRemark']").val(data.arr[0].scoreCRemark);
									}
									if (data.arr[0].scoreD != null) {
										addScoreRemark(addScoreRemarkBtn);
										$("input[name='scoreD']").val(data.arr[0].scoreD);
										$("input[name='scoreDRemark']").val(data.arr[0].scoreDRemark);
									}
									if (data.arr[0].scoreE != null) {
										addScoreRemark(addScoreRemarkBtn);
										$("input[name='scoreE']").val(data.arr[0].scoreE);
										$("input[name='scoreERemark']").val(data.arr[0].scoreERemark);
									}
									
								},
								remoteMethod : function(val, cb, show, pageIndex) {
									$.ajax({
										url : "exam/paperList",
										data : {
											two : val,
											curPage : pageIndex,
											pageSize : 5
										},
										async : true,
										success : function(obj) {
											var papers = [];
											for (var i in obj.data.rows) {
												var p = {
													name :  obj.data.rows[i].NAME+"-"+obj.data.rows[i].PAPER_TYPE_NAME,
													value : obj.data.rows[i].ID,
													passScore : obj.data.rows[i].PASS_SCORE,
													scoreA : obj.data.rows[i].SCORE_A,
													scoreARemark : obj.data.rows[i].SCORE_A_REMARK,
													scoreB : obj.data.rows[i].SCORE_B,
													scoreBRemark : obj.data.rows[i].SCORE_B_REMARK,
													scoreC : obj.data.rows[i].SCORE_C,
													scoreCRemark : obj.data.rows[i].SCORE_C_REMARK,
													scoreD : obj.data.rows[i].SCORE_D,
													scoreDRemark : obj.data.rows[i].SCORE_D_REMARK,
													scoreE : obj.data.rows[i].SCORE_E,
													scoreERemark : obj.data.rows[i].SCORE_E_REMARK
												};
												papers.push(p);
											}
											cb(papers, Math.ceil(obj.data.total / 5));
										}
									});
								}
							});
							var papers = [];
							papers.push({"name" : $("#_paperName").val()+"-"+$("#_paperTypeName").val(), value : $("#_paperId").val(), selected : true});
							paperSelect.update({
								data: papers
							});
							
							UE.delEditor("description");
							UE.getEditor("description");
							layui.form.render(null, "examEditFrom");
						}
					});
				}
			});
		}

		//完成修改考试
		function doExamEdit(examEditDialogIndex) {
			layui.form.on("submit(examEditBtn)", function(data) {
				layer.confirm("确定要修改？", function(index) {
					var descriptionUE = UE.getEditor("description");
					if (descriptionUE.hasContents()) {
						data.field.description = descriptionUE.getContent();
					}

					$.ajax({
						url : "exam/doEdit",
						data : data.field,
						success : function(obj) {
							examQuery();

							if (!obj.succ) {
								layer.alert(obj.msg, {
									"title" : "提示消息"
								});
								return;
							}

							layer.close(index);
							layer.close(examEditDialogIndex);
						}
					});
				});
			});
			$("[lay-filter='examEditBtn']").click();
		}

		//完成删除考试
		function doExamDel(id) {
			layer.confirm("确定要删除？", function(index) {
				$.ajax({
					url : "exam/doDel",
					data : {
						id : id
					},
					success : function(obj) {
						examQuery();

						if (!obj.succ) {
							layer.alert(obj.msg, {
								"title" : "提示消息"
							});
							return;
						}

						layer.close(index);
					}
				});
			});
		}

		// 到达配置考试页面
		function toExamCfg(id) {
			$.ajax({
				url : "exam/toCfg",
				data : {
					id : id
				},
				dataType : "html",
				success : function(obj) {
					layer.open({
						id : "examContent",
						title : "考试配置",
						area : [ "800px", "500px" ],
						content : obj,
						btn : ["确定", "关闭"],
						type : 1,
						yes : function(index, layero) {
							doExamCfg(index);
						},
						success : function(layero, index) {
							var userIdSelect = xmSelect.render({
								el : "#userIds",
								name : "userIds",
								filterable : true,
								paging : true,
								pageRemote : true,
								autoRow : true,
								//radio: true,
								//clickClose: true,
								//tips : "可模糊搜索用户昵称、组织机构名称",
								searchTips : "可模糊搜索用户昵称、组织机构名称",
								layVerify : "required",
								//model: {label: {
								//		type: "text",
								//		text: {left: "", right: "", separator: "", },
								//	}
								//},
								// pageSize : 5,
								remoteMethod : function(val, cb, show, pageIndex) {
									$.ajax({
										url : "exam/userList",
										data : {
											two : val,
											curPage : pageIndex,
											pageSize : 5
										},
										async : true,
										success : function(obj) {
											var users = [];
											for (var i in obj.data.rows) {
												users.push({"name" : obj.data.rows[i].NAME+"-"+obj.data.rows[i].ORG_NAME, value : obj.data.rows[i].ID});
											}
											cb(users, Math.ceil(obj.data.total / 5));
										}
									});
								}
							});
							$.ajax({
								url : "exam/userList",
								data : {
									ten : id,
									pageSize : 100
								},
								async : true,
								success : function(obj) {
									var users = [];
									for (var i in obj.data.rows) {
										users.push({"name" : obj.data.rows[i].NAME+"-"+obj.data.rows[i].ORG_NAME, value : obj.data.rows[i].ID, selected : true});
									}
									
									userIdSelect.update({
										data: users
									})
								}
							});
							
							var myMarkIdSelect = xmSelect.render({
								el : "#myMarkIds",
								name : "myMarkIds",
								filterable : true,
								paging : true,
								pageRemote : true,
								autoRow : true,
								//radio: true,
								//clickClose: true,
								//tips : "可模糊搜索用户昵称、组织机构名称",
								searchTips : "可模糊搜索用户昵称、组织机构名称",
								//layVerify : "required",
								//model: {label: {
								//		type: "text",
								//		text: {left: "", right: "", separator: "", },
								//	}
								//},
								// pageSize : 5,
								remoteMethod : function(val, cb, show, pageIndex) {
									$.ajax({
										url : "exam/userList",
										data : {
											two : val,
											curPage : pageIndex,
											pageSize : 5
										},
										async : true,
										success : function(obj) {
											var users = [];
											for (var i in obj.data.rows) {
												users.push({"name" : obj.data.rows[i].NAME+"-"+obj.data.rows[i].ORG_NAME, value : obj.data.rows[i].ID});
											}
											cb(users, Math.ceil(obj.data.total / 5));
										}
									});
								}
							});
							$.ajax({
								url : "exam/userList",
								data : {
									nine : id,
									pageSize : 100
								},
								async : true,
								success : function(obj) {
									var myMarks = [];
									for (var i in obj.data.rows) {
										myMarks.push({"name" : obj.data.rows[i].NAME+"-"+obj.data.rows[i].ORG_NAME, value : obj.data.rows[i].ID, selected : true});
									}
									
									myMarkIdSelect.update({
										data: myMarks
									})
								}
							});
							
							layui.form.render(null, "examCfgFrom");
						}
					});
				}
			});
		}
		
		// 完成配置考试
		function doExamCfg(toExamCfgDialogIndex) {
			layui.form.on("submit(examCfgBtn)", function(data) {
				layer.confirm("确定要配置？", function(index) {
					$.ajax({
						url : "exam/doCfg",
						data : data.field,
						success : function(obj) {
							examQuery();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
							//layer.close(toExamCfgDialogIndex);
						}
					});
				});
			});
			$("[lay-filter='examCfgBtn']").click();
		}

		// 添加分值评语
		function addScoreRemark(curObj) {
			var optionRows = $("input[name$='Remark']");
			if (optionRows.length >= remarkOptionLabs.length) {// 如果已加到最大添加行，则不处理
				return;
			}

			var index = optionRows.length;
			var html = [];
			html.push('<div class="layui-col-md11" style="padding-top: 5px;">');
			html.push('	<label class="layui-form-label"></label>');
			html.push('	<div class="layui-form-mid">大于等于</div>');
			html.push('	<div class="layui-input-inline" style="width: 60px;">');
			html.push('		<input type="text" name=score' + remarkOptionLabs[index] + ' value=""');
			html.push('			class="layui-input" lay-verify="required|number" placeholder="分值">');
			html.push('	</div>');
			html.push('	<div class="layui-form-mid">评语</div>');
			html.push('	<div class="layui-input-inline" style="width: 368px;">');
			html.push('		<input name="score' + remarkOptionLabs[index] + 'Remark" value="" ');
			html.push('			class="layui-input" lay-verify="required" placeholder="请输入评语">');
			html.push('	</div>');
			html.push('	<div class="layui-input-inline" style="width: 55px;margin-right: 0px;">');
			html.push('		<button type="button" class="layui-btn layui-btn-primary" onclick="delScoreRemark(this);">');
			html.push('			<i class="layui-icon layui-icon-subtraction"></i>');
			html.push('		</button>');
			html.push('	</div>');
			html.push('</div>');

			$(curObj).parent().parent().parent().append(html.join(""));
		}

		// 删除分值评语
		function delScoreRemark(curObj) {
			$(curObj).parent().parent().remove();
		}
		
		// 考试发布
		function doExamPublish(id) {
			$.ajax({
				url : "exam/doPublish",
				data : {
					id : id
				},
				success : function(obj) {
					examQuery();
					
					if (!obj.succ) {
						layer.alert(obj.msg, {"title" : "提示消息"});
						return;
					}
					
					layer.close(index);
				}
			});
		}
	</script>
</html>
