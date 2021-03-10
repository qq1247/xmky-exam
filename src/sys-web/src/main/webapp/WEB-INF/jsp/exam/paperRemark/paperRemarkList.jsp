<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>试卷评语列表</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
		<div class="layui-fluid">
			<div class="layui-card">
				<%-- 试卷评语查询条件 --%>
				<form id="paperRemarkQueryForm" class="layui-form layui-card-header layuiadmin-card-header-auto">
					<div class="layui-form-item ">
						<div class="layui-inline">
							<input type="text" name="two" placeholder="请输入ID" class="layui-input">
						</div>
						<div class="layui-inline">
							<button type="button" class="layui-btn layuiadmin-btn-useradmin" onclick="paperRemarkQuery();">
								<i class="layui-icon layuiadmin-button-btn"></i>查询
							</button>
							<button type="button" class="layui-btn layui-btn-primary layuiadmin-btn-useradmin" onclick="paperRemarkReset();">
								<i class="layui-icon layuiadmin-button-btn"></i>重置
							</button>
						</div>
					</div>
				</form>
				<div class="layui-card-body">
					<div style="padding-bottom: 10px;">
						<my:auth url="paperRemark/toAdd"><button class="layui-btn layuiadmin-btn-useradmin" onclick="toPaperRemarkAdd();">添加</button></my:auth>
					</div>
					<script type="text/html" id="paperRemarkToolbar">
						<my:auth url="paperRemark/toEdit"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="paperRemarkEdit"><i class="layui-icon layui-icon-edit"></i>修改</a></my:auth>
						<my:auth url="paperRemark/doDel"><a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="paperRemarkDel"><i class="layui-icon layui-icon-delete"></i>删除</a></my:auth>
					</script>
					<%-- 试卷评语数据表格 --%>
					<table id="paperRemarkTable" lay-filter="paperRemarkTable"></table>
				</div>
			</div>
		</div>
	</body>
	<%@include file="/script/myJs/tail.jspf"%>
	<script type="text/javascript">
		//定义变量
		var paperRemarkQueryForm = $("#paperRemarkQueryForm"); //试卷评语查询对象
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initPaperRemarkTable();
		});
	
		//初始化试卷评语表格
		function initPaperRemarkTable() {
			layui.table.render({
				elem : "#paperRemarkTable",
				url : "paperRemark/list",
				cols : [[
						{field : "SCORE_A", title : "分数A", align : "center"},
						{field : "SCORE_A_REMARK", title : "分数A评语", align : "center"},
						{field : "SCORE_B", title : "分数B", align : "center"},
						{field : "SCORE_B_REMARK", title : "分数B评语", align : "center"},
						{field : "SCORE_C", title : "分数C", align : "center"},
						{field : "SCORE_C_REMARK", title : "分数C评语", align : "center"},
						{field : "SCORE_D", title : "分数D", align : "center"},
						{field : "SCORE_D_REMARK", title : "分数D评语", align : "center"},
						{field : "SCORE_E", title : "分数E", align : "center"},
						{field : "SCORE_E_REMARK", title : "分数E评语", align : "center"},
						{field : "PAPER_ID", title : "试卷ID", align : "center"},
						{fixed: 'right', title : "操作 ", toolbar : "#paperRemarkToolbar", align : "center"}
						]],
				page : true,
				height : "full-180",
				method : "post",
				defaultToolbar : [],
				parseData: function(paperRemark){
					return {
						"code" : paperRemark.succ,
						"msg" : paperRemark.msg,
						"count" : paperRemark.data.total,
						"data" : paperRemark.data.rows
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
			layui.table.on("rowDouble(paperRemarkTable)", function(obj){
				<my:auth url="paperRemark/toEdit">toPaperRemarkEdit(obj.data.ID);</my:auth>
			});
			layui.table.on("tool(paperRemarkTable)", function(obj){
				var data = obj.data;
				if(obj.event === "paperRemarkEdit") {
					toPaperRemarkEdit(obj.data.ID);
				} else if(obj.event === "paperRemarkDel") {
					doPaperRemarkDel(obj.data.ID);
				}
			});
		}
		
		//试卷评语查询
		function paperRemarkQuery() {
			layui.table.reload("paperRemarkTable", {"where" : $.fn.my.serializeObj(paperRemarkQueryForm)});
		}
	
		//试卷评语重置
		function paperRemarkReset() {
			paperRemarkQueryForm[0].reset();
			paperRemarkQuery();
		}
		
		//到达添加试卷评语页面
		function toPaperRemarkAdd() {
			$.ajax({
				url : "paperRemark/toAdd",
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "添加试卷评语",
						area : ["800px", "500px"],
						content : obj,
						btn : ["添加", "取消"],
						type : 1,
						yes : function(index, layero){
							doPaperRemarkAdd(index);
						},
						success: function(layero, index){
							layui.form.render(null, "paperRemarkEditFrom");
						}
					});
				}
			});
		}

		//完成添加试卷评语
		function doPaperRemarkAdd(paperRemarkAddDialogIndex) {
			layui.form.on("submit(paperRemarkEditBtn)", function(data) {
				layer.confirm("确定要添加？", function(index) {
					$.ajax({
						url : "paperRemark/doAdd",
						data : data.field,
						success : function(obj) {
							initPaperRemarkTable();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
							layer.close(paperRemarkAddDialogIndex);
						}
					});
				});
			});
			$("[lay-filter='paperRemarkEditBtn']").click();
		}
		
		//到达修改试卷评语页面
		function toPaperRemarkEdit(id) {
			$.ajax({
				url : "paperRemark/toEdit",
				data : {"id" : id},
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "修改试卷评语",
						area : ["800px", "500px"],
						content : obj,
						btn : ["修改", "取消"],
						type : 1,
						yes : function(index, layero){
							doPaperRemarkEdit(index);
						},
						success: function(layero, index){
							layui.form.render(null, "paperRemarkEditFrom");
						}
					});
				}
			});
		}

		//完成修改试卷评语
		function doPaperRemarkEdit(paperRemarkEditDialogIndex) {
			layui.form.on("submit(paperRemarkEditBtn)", function(data) {
				layer.confirm("确定要修改？", function(index) {
					$.ajax({
						url : "paperRemark/doEdit",
						data : data.field,
						success : function(obj) {
							initPaperRemarkTable();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
							layer.close(paperRemarkEditDialogIndex);
						}
					});
				});
			});
			$("[lay-filter='paperRemarkEditBtn']").click();;
		}

		//完成删除试卷评语
		function doPaperRemarkDel(id) {
			layer.confirm("确定要删除？", function(index) {
				$.ajax({
					url : "paperRemark/doDel",
					data : {id : id},
					success : function(obj) {
						initPaperRemarkTable();
						
						if (!obj.succ) {
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
