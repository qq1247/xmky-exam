<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@include file="/WEB-INF/jsp/exam/paper/paperCfgStyle.jsp"%>

<div lay-filter="paperCfgFrom" class="layui-form" style="padding: 20px 0 0 0;">
	<div class="layui-row">
		<div class="layui-col-md-offset1 layui-col-md8">
			<!-- 试卷 -->
			<%@include file="/WEB-INF/jsp/exam/paper/paperCfgQuestion.jsp"%>
		</div>
		<div class="layui-col-md2">
			<!-- 答题卡 -->
			<%@include file="/WEB-INF/jsp/exam/paper/paperCfgCard.jsp"%>
		</div>
	</div>
</div>
<script type="text/javascript">
	//定义变量
	var curSelQuestionTypeId; //当前选中的试题分类ID
	var curSelQuestionTypeName; //当前选中的试题分类名称

	//到达添加章节页面
	function toChapterAdd(id) {
		$.ajax({
			url : "paper/toChapterAdd",
			data : {id : id},
			dataType : "html",
			success : function(obj) {
				layer.open({
					title : "添加章节",
					area : ["800px", "500px"],
					content : obj,
					btn : ["添加", "取消"],
					type : 1,
					yes : function(index, layero) {
						doChapterAdd(index);
					},
					success: function(layero, index) {
						UE.delEditor("description");
						UE.getEditor("description");
						layui.form.render(null, "chapterEditFrom");
					}
				});
			}
		});
	}
	
	// 完成添加章节
	function doChapterAdd(chapterAddDialogIndex) {
		layui.form.on("submit(chapterEditBtn)", function(data) {
			var descriptionUE = UE.getEditor("description");
			if (descriptionUE.hasContents()) {
				data.field.description = descriptionUE.getContent();
			}
			layer.confirm("确定要添加？", function(index) {
				$.ajax({
					url : "paper/doChapterAdd",
					data : data.field,
					success : function(obj) {
						if (!obj.succ) {
							layer.alert(obj.msg, {"title" : "提示消息"});
							return;
						}
						
						reloadPaperCfg();
						layer.close(index);
						layer.close(chapterAddDialogIndex);
					}
				});
			});
		});
		$("[lay-filter='chapterEditBtn']").click();
	}
	
	//到达修改章节页面
	function toChapterEdit(chapterId) {
		event.stopPropagation();
		$.ajax({
			url : "paper/toChapterEdit",
			data : {chapterId : chapterId},
			dataType : "html",
			success : function(obj) {
				layer.open({
					title : "修改章节",
					area : ["800px", "500px"],
					content : obj,
					btn : ["修改", "取消"],
					type : 1,
					yes : function(index, layero) {
						doChapterEdit(index);
					},
					success: function(layero, index) {
						UE.delEditor("description");
						UE.getEditor("description");
						layui.form.render(null, "chapterEditFrom");
					}
				});
			}
		});
	}
	
	// 完成修改章节
	function doChapterEdit(chapterEditDialogIndex) {
		layui.form.on("submit(chapterEditBtn)", function(data) {
			var descriptionUE = UE.getEditor("description");
			if (descriptionUE.hasContents()) {
				data.field.description = descriptionUE.getContent();
			}
			layer.confirm("确定要修改？", function(index) {
				$.ajax({
					url : "paper/doChapterEdit",
					data : data.field,
					success : function(obj) {
						if (!obj.succ) {
							layer.alert(obj.msg, {"title" : "提示消息"});
							return;
						}
						
						reloadPaperCfg();
						layer.close(index);
						layer.close(chapterEditDialogIndex);
					}
				});
			});
		});
		$("[lay-filter='chapterEditBtn']").click();
	}
	
	// 完成删除章节
	function doChapterDel(chapterId) {
		event.stopPropagation();
		layer.confirm("确定要删除？", function(index) {
			$.ajax({
				url : "paper/doChapterDel",
				data : {chapterId : chapterId},
				success : function(obj) {
					if (!obj.succ) {
						layer.alert(obj.msg, {"title" : "提示消息"});
						return;
					}
					
					layer.close(index);
					reloadPaperCfg();
				}
			});
		});
	}
	
	//完成章节上移
	function doChapterUp(chapterId) {
		event.stopPropagation();
		$.ajax({
			url : "paper/doChapterUp",
			data : {
				chapterId : chapterId
			},
			success : function(obj) {
				reloadPaperCfg();
				layer.msg(obj.msg);
			}
		});
	}
	
	//完成章节下移
	function doChapterDown(chapterId) {
		event.stopPropagation();
		$.ajax({
			url : "paper/doChapterDown",
			data : {
				chapterId : chapterId
			},
			success : function(obj) {
				reloadPaperCfg();
				layer.msg(obj.msg);
			}
		});
	}
	
	//到达添加试题页面
	function toQuestionAdd(id, chapterId) {
		$.ajax({
			url : "paper/toQuestionAdd",
			data : {id : id},
			dataType : "html",
			success : function(obj) {
				layer.open({
					title : "添加试题",
					area : ["1200px", "600px"],
					content : obj,
					btn : ["关闭"],
					type : 1,
					/* yes : function(index, layero) {
						
					}, */
					success: function(layero, index) {
						/* layui.layer.full(index); */
						layui.table.render({
							elem : "#questionTable",
							url : "paper/questionList",
							where : {nine : id},
							cols : [[
									{field : "CODE", title : "编号", align : "center"},
									{field : "TITLE", title : "题干", align : "center", width : 400},
									{field : "TYPE_NAME", title : "类型", align : "center"},
									{field : "DIFFICULTY_NAME", title : "难度", align : "center"},
									{field : "STATE_NAME", title : "状态", align : "center"},
									{field : "QUESTION_TYPE_NAME", title : "分类", align : "center"},
									{field : "SCORE", title : "默认分值", align : "center"},
									{field : "NO", title : "排序", align : "center"},
									{fixed: 'right', title : "操作 ", toolbar : "#questionToolbar", align : "center", width : 200}
									]],
							page : true,
							height : "full-180",
							method : "post",
							defaultToolbar : [],
							parseData: function(question) {
								return {
									"code" : question.succ,
									"msg" : question.msg,
									"count" : question.data.total,
									"data" : question.data.rows
								};
							},
							request: {
								pageName: "curPage",
								limitName: "pageSize"
							}, 
							response: {
								statusCode : true
							}
						});
						layui.table.on("tool(questionTable)", function(obj) {
							var data = obj.data;
							if (obj.event === "questionAdd") {
								doQuestionAdd(obj.data.ID, chapterId);
							}
						});
						
						questionTypeTree = $.fn.zTree.init($("#questionTypeTree"), {
							async : {
								url : "paper/questionTypeTreeList",
								enable : true,
								dataFilter : ztreeDataFilter
							},
							callback : {
								onClick : function(event, treeId, treeNode) {
									curSelQuestionTypeId = treeNode.ID;
									curSelQuestionTypeName = treeNode.NAME;
									$("#questionOne").val(curSelQuestionTypeId);
									questionQuery();
								},
								onAsyncSuccess : function(event, treeId, msg, treeNode) {
									var questionTypeTree = $.fn.zTree.getZTreeObj(treeId);
									questionTypeTree.expandAll(true);
									
									if (!curSelQuestionTypeId) {
										var rootNode = questionTypeTree.getNodesByFilter(function(node) { return (node.level == 0); }, true);
										questionTypeTree.selectNode(rootNode);
										
										curSelQuestionTypeId = rootNode.ID;
										curSelQuestionTypeName = rootNode.NAME;
										$("#questionOne").val(curSelQuestionTypeId);
										return;
									}
									
									var curNode = questionTypeTree.getNodeByParam("id", curSelQuestionTypeId, null);
									questionTypeTree.selectNode(curNode);
									
									questionQuery();
								}
							}
						});
						
						$("#questionTypeTree").height($(window).height() - 45);
						
						layui.form.render(null, "questionQueryForm");
					}
				});
			}
		});
	}
	
	//完成试题添加
	function doQuestionAdd(questionId, chapterId) {
		$.ajax({
			url : "paper/doQuestionAdd",
			data : {
				chapterId : chapterId,
				questionIds : questionId,
			},
			success : function(obj) {
				questionQuery();
				reloadPaperCfg();
				layer.msg(obj.msg);
			}
		});
	}
	
	//完成试题删除
	function doQuestionDel(paperQuestionId) {
		layer.confirm("确定要删除？", function(index) {
			$.ajax({
				url : "paper/doQuestionDel",
				data : {
					paperQuestionId : paperQuestionId
				},
				success : function(obj) {
					if (!obj.succ) {
						layer.alert(obj.msg, {"title" : "提示消息"});
						return;
					}
					
					layer.close(index);
					reloadPaperCfg();
				}
			});
		});
	}
	
	//完成试题清空
	function doQuestionClear(chapterId) {
		layer.confirm("确定要清空？", function(index) {
			$.ajax({
				url : "paper/doQuestionClear",
				data : {
					chapterId : chapterId
				},
				success : function(obj) {
					if (!obj.succ) {
						layer.alert(obj.msg, {"title" : "提示消息"});
						return;
					}
					
					layer.close(index);
					reloadPaperCfg();
				}
			});
		});
	}
	
	//完成设置分数
	function doScoreUpdate(paperQuestionId) {
		var score = event.target.value;
		var patt  = /^(([1-9][0-9]*)|(([0]\.\d{1,2}|[1-9][0-9]*\.\d{1,2})))$/;
		if (!patt.test(score)) {
			layer.msg("必须为正数，最多保留两位小数");
			event.target.value = event.target.defaultValue;
			return;
		}
		
		$.ajax({
			url : "paper/doScoreUpdate",
			data : {
				paperQuestionId : paperQuestionId,
				score : score
			},
			success : function(obj) {
				layer.msg(obj.msg);
				reloadPaperCfg();
			}
		});
	}
	
	//完成设置选项
	function doOptionsUpdate(curObj) {
		var curObj = $(curObj);
		var paperQuestionId = curObj.attr("pqId");
		var options = [];
		curObj.parent().find(":checked").each(function() {
			options.push($(this).val());
	    });
		
		$.ajax({
			url : "paper/doOptionsUpdate",
			data : {
				paperQuestionId : paperQuestionId,
				options :options
			},
			success : function(obj) {
				layer.msg(obj.msg);
				reloadPaperCfg();
			}
		});
	}
	
	//完成试题上移
	function doQuestionUp(paperQuestionId) {
		$.ajax({
			url : "paper/doQuestionUp",
			data : {
				paperQuestionId : paperQuestionId
			},
			success : function(obj) {
				reloadPaperCfg();
				layer.msg(obj.msg);
			}
		});
	}
	
	//完成试题下移
	function doQuestionDown(paperQuestionId) {
		$.ajax({
			url : "paper/doQuestionDown",
			data : {
				paperQuestionId : paperQuestionId
			},
			success : function(obj) {
				reloadPaperCfg();
				layer.msg(obj.msg);
			}
		});
	}
	
	//试题查询
	function questionQuery() {
		layui.table.reload("questionTable", {"where" : $.fn.my.serializeObj($("#questionQueryForm"))});
	}
	
	//试题重置
	function questionReset() {
		$("#questionQueryForm")[0].reset();
		questionQuery();
	}
	
	// 重新加载页面
	function reloadPaperCfg() {
		$.ajax({
			url : "paper/toCfg",
			data : {id : "${paper.id }"},
			dataType : "html",
			success : function(obj) {
				var content = $("#paperContent");
				content.html(obj);
				
				layui.element.render("collapse", "exam-card");
				layui.form.render(null, "paperCfgFrom");
			}
		});
	}

	// 单选多选提交
	function rdoCbxSubmit(examUserQuestionId) {
		// 选中当前行
		var li = $(event.target);
		li.children("div")[0].click();
		
	  //答题卡标记为已做
		var vals = [];
	    $("li :checked").each(function() {
	    	vals.push($(this).val());
	    });
	    
		var anCardOption = $("#examCard" + examUserQuestionId);
		if (vals.length == 0) {
			anCardOption.removeClass("select");
		} else {
			anCardOption.addClass("select");
		}
		
		//异步提交答案
		$.ajax({
			data : {
				examUserQuestionId : examUserQuestionId, 
				answer : vals 
			},
			url : "myExam/updateAnswer",
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
</script>
























