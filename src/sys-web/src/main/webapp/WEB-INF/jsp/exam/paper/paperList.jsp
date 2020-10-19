<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>试卷列表</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
		<div class="layui-fluid">
			<div class="layui-row layui-col-space10">
				<div class="layui-col-md2">
					<div class="layui-card">
						<div class="layui-form">
			      			<ul id="paperTypeTree" class="ztree"></ul>
			 			</div>
					</div>
				</div>
				<div class="layui-col-md10">
					<div class="layui-card">
					<%-- 试卷查询条件 --%>
					<form id="paperQueryForm" class="layui-form layui-card-header layuiadmin-card-header-auto">
						<input type="hidden" id="paperOne" name="one">
						<div class="layui-form-item ">
							<div class="layui-inline">
								<input type="text" name="three" placeholder="请输入名称" class="layui-input">
							</div>
							<div class="layui-inline">
								<button type="button" class="layui-btn layuiadmin-btn-useradmin" onclick="paperQuery();">
									<i class="layui-icon layuiadmin-button-btn"></i>查询
								</button>
								<button type="button" class="layui-btn layui-btn-primary layuiadmin-btn-useradmin" onclick="paperReset();">
									<i class="layui-icon layuiadmin-button-btn"></i>重置
								</button>
							</div>
						</div>
					</form>
					<div class="layui-card-body">
						<div style="padding-bottom: 10px;">
							<my:auth url="paper/toAdd"><button class="layui-btn layuiadmin-btn-useradmin" onclick="toPaperAdd();">添加</button></my:auth>
						</div>
						<script type="text/html" id="paperToolbar">
						<my:auth url="paper/toEdit"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="paperEdit"><i class="layui-icon layui-icon-edit"></i>修改</a></my:auth>
						<my:auth url="paper/toCfg"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="paperCfg"><i class="layui-icon layui-icon-edit"></i>配置试卷</a></my:auth>
						<my:auth url="paper/doDel"><a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="paperDel"><i class="layui-icon layui-icon-delete"></i>删除</a></my:auth>
					</script>
						<%-- 试卷数据表格 --%>
						<table id="paperTable" lay-filter="paperTable"></table>
					</div>
				</div>
				</div>
			</div>
		</div>
	</body>
	<%@include file="/script/myJs/tail.jspf"%>
	<script type="text/javascript">
		//定义变量
		var paperQueryForm = $("#paperQueryForm"); //试卷查询对象
		var paperTypeTree; //试卷分类树对象
		var curSelPaperTypeId = ""; //当前选中的试卷分类ID
		var curSelPaperTypeName = ""; //当前选中的试卷分类名称
		
		var remarkOptionLabs = ["A", "B", "C", "D", "E"];//评语选项标签
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initPaperTable();
			initPaperTypeTree();
		});
	
		//初始化试卷表格
		function initPaperTable() {
			layui.table.render({
				elem : "#paperTable",
				url : "paper/list",
				cols : [[
						{field : "NAME", title : "名称", align : "center"},
						{field : "PASS_SCORE", title : "及格分数", align : "center"},
						{field : "TOTAL_SCORE", title : "总分数", align : "center"},
						{field : "PAPER_TYPE_NAME", title : "试卷分类", align : "center"},
						{field : "STATE_NAME", title : "状态", align : "center", templet : function(d) {
							return '<input type="checkbox" name="state" value="'+d.ID+'" '
								+ 'lay-skin="switch" lay-text="已发布|未发布" lay-filter="paperPublish"' + (d.STATE == 1 ? 'checked' : '') + '>'
						}},
						{fixed : "right", title : "操作 ", toolbar : "#paperToolbar", align : "center", width : 280}
						]],
				page : true,
				height : "full-180",
				method : "post",
				defaultToolbar : [],
				parseData: function(paper){
					return {
						"code" : paper.succ,
						"msg" : paper.msg,
						"count" : paper.data.total,
						"data" : paper.data.rows
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
			layui.table.on("rowDouble(paperTable)", function(obj){
				<my:auth url="paper/toEdit">toPaperEdit(obj.data.ID);</my:auth>
			});
			layui.table.on("tool(paperTable)", function(obj){
				var data = obj.data;
				if(obj.event === "paperEdit") {
					toPaperEdit(obj.data.ID);
				} else if(obj.event === "paperDel") {
					doPaperDel(obj.data.ID);
				} else if(obj.event === "paperCfg") {
					toPaperCfg(obj.data.ID, obj.data.NAME);
				}
			});
			layui.form.on("switch(paperPublish)", function(obj) {
				doPaperPublish(obj.value);
			});
		}
		
		//初始化试卷分类树
		function initPaperTypeTree() {
			paperTypeTree = $.fn.zTree.init($("#paperTypeTree"), {
				async : {
					url : "paper/paperTypeTreeList",
					enable : true,
					dataFilter : ztreeDataFilter
				},
				callback : {
					onClick : function(event, treeId, treeNode) {
						curSelPaperTypeId = treeNode.ID;
						curSelPaperTypeName = treeNode.NAME;
						$("#paperOne").val(curSelPaperTypeId);
						paperQuery();
					},
					onAsyncSuccess : function(event, treeId, msg, treeNode) {
						var paperTypeTree = $.fn.zTree.getZTreeObj(treeId);
						paperTypeTree.expandAll(true);
						
						if (!curSelPaperTypeId) {
							var rootNode = paperTypeTree.getNodesByFilter(function(node) { return (node.level == 0); }, true);
							paperTypeTree.selectNode(rootNode);
							
							curSelPaperTypeId = rootNode.ID;
							curSelPaperTypeName = rootNode.NAME;
							$("#paperOne").val(curSelPaperTypeId);
							return;
						}
						
						var curNode = paperTypeTree.getNodeByParam("id", curSelPaperTypeId, null);
						paperTypeTree.selectNode(curNode);
						
						paperQuery();
					}
				}
			});
			
			$("#paperTypeTree").height($(window).height() - 45);
		}
		
		//试卷查询
		function paperQuery() {
			layui.table.reload("paperTable", {"where" : $.fn.my.serializeObj(paperQueryForm)});
		}
	
		//试卷重置
		function paperReset() {
			paperQueryForm[0].reset();
			paperQuery();
		}
		
		//到达添加试卷页面
		function toPaperAdd() {
			if(!curSelPaperTypeId){
				layer.alert("请选择试卷分类！", {"title" : "提示消息"});
				return;
			}
			
			$.ajax({
				url : "paper/toAdd",
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "添加试卷",
						area : ["800px", "500px"],
						content : obj,
						btn : ["添加", "取消"],
						type : 1,
						yes : function(index, layero){
							doPaperAdd(index);
						},
						success: function(layero, index){
							$("#paperTypeId").val(curSelPaperTypeId);
							$("#paperTypeName").val(curSelPaperTypeName);
							UE.delEditor("description");
							UE.getEditor("description");
							layui.form.render(null, "paperEditFrom");
						}
					});
				}
			});
		}
		
		//完成添加试卷
		function doPaperAdd(paperAddDialogIndex) {
			layui.form.on("submit(paperEditBtn)", function(data) {
				layer.confirm("确定要添加？", function(index) {
					var descriptionUE = UE.getEditor("description");
					if (descriptionUE.hasContents()) {
						data.field.description = descriptionUE.getContent();
					}
					
					$.ajax({
						url : "paper/doAdd",
						data : data.field,
						success : function(obj) {
							paperQuery();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
							layer.close(paperAddDialogIndex);
						}
					});
				});
			});
			$("[lay-filter='paperEditBtn']").click();
		}
		
		//到达修改试卷页面
		function toPaperEdit(id) {
			$.ajax({
				url : "paper/toEdit",
				data : {id : id},
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "修改试卷",
						area : ["800px", "500px"],
						content : obj,
						btn : ["修改", "取消"],
						type : 1,
						yes : function(index, layero){
							doPaperEdit(index);
						},
						success: function(layero, index){
							UE.delEditor("description");
							UE.getEditor("description");
							layui.form.render(null, "paperEditFrom");
						}
					});
				}
			});
		}

		//完成修改试卷
		function doPaperEdit(paperEditDialogIndex) {
			layui.form.on("submit(paperEditBtn)", function(data) {
				layer.confirm("确定要修改？", function(index) {
					var descriptionUE = UE.getEditor("description");
					if (descriptionUE.hasContents()) {
						data.field.description = descriptionUE.getContent();
					}
					
					$.ajax({
						url : "paper/doEdit",
						data : data.field,
						success : function(obj) {
							paperQuery();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
							layer.close(paperEditDialogIndex);
						}
					});
				});
			});
			$("[lay-filter='paperEditBtn']").click();
		}

		//完成删除试卷
		function doPaperDelForBtn() {
			layer.confirm("确定要删除？", function(index) {
				$.ajax({
					url : "paper/doDel",
					data : {id : id},
					success : function(obj) {
						paperQuery();
						
						if (!obj.succ) {
							layer.alert(obj.msg, {"title" : "提示消息"});
							return;
						}
						
						layer.close(index);
					}
				});
			});
		}

		//到达配置试卷页面
		function toPaperCfg(id, name) {
			$.ajax({
				url : "paper/toCfg",
				data : {id : id},
				dataType : "html",
				success : function(obj) {
					layer.open({
						id : "paperContent",
						title : "配置【" + name + "】",
						area : ["800px", "500px"],
						content : obj,
						btn : [],
						type : 1,
						yes : function(index, layero){
							
						},
						success: function(layero, index){
							layui.layer.full(index);
							layui.element.render("collapse", "exam-card");
							layui.form.on("switch(options)", function(data) {
								doOptionsUpdate($(data.elem));
							});
							layui.form.render(null, "paperCfgFrom");
						}
					});
				}
			});
		}
		
		// 添加分值评语
		function addScoreRemark(curObj) {
			var optionRows = $("input[name$='Remark']");
			if(optionRows.length >= remarkOptionLabs.length){// 如果已加到最大添加行，则不处理
				return;
			}
			
			var index = optionRows.length;
			var html = [];
			html.push('<div class="layui-col-md11" style="padding-top: 5px;">');
			html.push('	<label class="layui-form-label"></label>');
			html.push('	<div class="layui-form-mid">大于等于</div>');
			html.push('	<div class="layui-input-inline" style="width: 60px;">');
			html.push('		<input type="text" name=score'+remarkOptionLabs[index]+' value=""');
			html.push('			class="layui-input" lay-verify="required|number" placeholder="分值">');
			html.push('	</div>');
			html.push('	<div class="layui-form-mid">评语</div>');
			html.push('	<div class="layui-input-inline" style="width: 368px;">');
			html.push('		<input name="score'+remarkOptionLabs[index]+'Remark" value="" ');
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
		
		// 试卷发布
		function doPaperPublish(id) {
			layer.confirm("发布后不能配置试卷，确定？", function(index) {
				$.ajax({
					url : "paper/doPublish",
					data : {
						id : id
					},
					success : function(obj) {
						paperQuery();
						
						if(!obj.succ){
							layer.alert(obj.msg, {"title" : "提示消息"});
							return;
						}
						
						layer.close(index);
					}
				});
			});
		}
	</script>
</html>
