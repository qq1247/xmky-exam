<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>账户明细列表</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
		<div class="layui-fluid">
			<div class="layui-card">
				<form id="accountDetailQueryForm" class="layui-form layui-card-header layuiadmin-card-header-auto">
					<div class="layui-form-item">
						<div class="layui-inline">
							<div class="layui-input-block">
								<input type="text" name="four" placeholder="请输入值标题" class="layui-input">
							</div>
						</div>
						<div class="layui-inline">
							<button type="button" class="layui-btn layuiadmin-btn-useradmin" onclick="accountDetailQuery();">
								<i class="layui-icon layuiadmin-button-btn"></i>查询
							</button>
							<button type="button" class="layui-btn layui-btn-primary layuiadmin-btn-useradmin" onclick="accountDetailReset();">
								<i class="layui-icon layuiadmin-button-btn"></i>重置
							</button>
						</div>
					</div>
				</form>
				<div class="layui-card-body">
					<div id="accountDetailToolbar" style="display: none;">
						<%-- <div class="layui-btn-container">
							<my:auth url="accountDetail/toAdd"><button class="layui-btn layuiadmin-btn-useradmin" onclick="toAccountDetailAdd();">添加</button></my:auth>
							<my:auth url="accountDetail/toEdit"><button class="layui-btn layui-btn-primary layuiadmin-btn-useradmin" onclick="toAccountDetailEditForBtn();">修改</button></my:auth>
							<my:auth url="accountDetail/doDel"><button class="layui-btn layui-btn-primary layuiadmin-btn-useradmin" onclick="doAccountDetailDelForBtn();">删除</button></my:auth>
						</div> --%>
					</div>
					<%-- 数据表格 --%>
					<table id="accountDetailTable" lay-filter="accountDetailTable"></table>
				</div>
			</div>
		</div>
	</body>
	<%@include file="/script/myJs/tail.jspf"%>
	<script type="text/javascript">
		//定义变量
		var accountDetailTableId = "accountDetailTable";//账户明细表格ID
		var accountDetailQueryForm = $("#accountDetailQueryForm"); //账户明细查询对象
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initAccountDetailTable();
		});
	
		//初始化账户明细表格
		function initAccountDetailTable() {
			layui.table.render({
				elem : "#" + accountDetailTableId,
				url : "accountDetail/list",
				cols : [[ 
						{field : "ID", title : "", checkbox : true},
						{field : "TITLE", title : "标题", align : "center"},
						{field : "BIZ_ID", title : "业务", align : "center"},
						{field : "BIZ_TYPE_NAME", title : "类型", align : "center"},
						{field : "NAME", title : "用户名称", align : "center"},
						{field : "AMOUNT_TYPE_NAME", title : "余额类型", align : "center"},
						{field : "AMOUNT", title : "余额", align : "center"}
						]],
				page : true,
				height : "full-135",
				method : "post",
				defaultToolbar : [],
				parseData: function(accountDetail){
					return {
						"code" : accountDetail.succ,
						"msg" : accountDetail.msg,
						"count" : accountDetail.data.total,
						"data" : accountDetail.data.rows
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
			layui.table.on("rowDouble("+accountDetailTableId+")", function(obj){
				<my:auth url="accountDetail/toEdit">toAccountDetailEditForDblClick(obj.data.ID);</my:auth>
			});
		}
		
		//账户明细查询
		function accountDetailQuery() {
			layui.table.reload(accountDetailTableId, {"where" : $.fn.my.serializeObj(accountDetailQueryForm)});
		}
	
		//账户明细重置
		function accountDetailReset() {
			accountDetailQueryForm[0].reset();
			accountDetailQuery();
		}
		
		//到达添加账户明细页面
		function toAccountDetailAdd() {
			$.ajax({
				url : "accountDetail/toAdd",
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "添加账户明细",
						type : 1,
						area : ["800px", "500px"],
						content : obj,
						
						btn : ["添加", "取消"],
						yes : function(index, layero){
							doAccountDetailAdd(index);
						},
						success: function(layero, index){
							layui.form.render(null, "accountDetailEditFrom");
						}
					});
				}
			});
		}

		//完成添加账户明细
		function doAccountDetailAdd(accountDetailAddDialogIndex) {
			layui.form.on("submit(accountDetailEditBtn)", function(data) {
				layer.confirm("确定要添加？", function(index) {
					$.ajax({
						url : "accountDetail/doAdd",
						data : data.field,
						success : function(obj) {
							accountDetailQuery();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
							layer.close(accountDetailAddDialogIndex);
						}
					});
				});
			});
			$("#accountDetailEditBtn").click();
		}
		
		//到达修改账户明细页面
		function toAccountDetailEdit(id) {
			$.ajax({
				url : "accountDetail/toEdit",
				data : {"id" : id},
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "修改账户明细",
						type : 1,
						area : ["800px", "500px"],
						content : obj,
						
						btn : ["修改", "取消"],
						yes : function(index, layero){
							doAccountDetailEdit(index);
						},
						success: function(layero, index){
							layui.form.render(null, "accountDetailEditFrom");
						}
					});
				}
			});
		}

		//到达修改账户明细页面
		function toAccountDetailEditForBtn() {
			var accountDetailTableRows = layui.table.checkStatus(accountDetailTableId);
			if (accountDetailTableRows.data.length != 1) {
				layer.alert("请选择一行数据！", {"title" : "提示消息"});
				return;
			}
			
			toAccountDetailEdit(accountDetailTableRows.data[0].ID);
		}

		//到达修改账户明细页面
		function toAccountDetailEditForDblClick(id) {
			toAccountDetailEdit(id);
		}

		//完成修改账户明细
		function doAccountDetailEdit(accountDetailEditDialogIndex) {
			layui.form.on("submit(accountDetailEditBtn)", function(data) {
				layer.confirm("确定要修改？", function(index) {
					$.ajax({
						url : "accountDetail/doEdit",
						data : data.field,
						success : function(obj) {
							accountDetailQuery();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
							layer.close(accountDetailEditDialogIndex);
						}
					});
				});
			});
			$("#accountDetailEditBtn").click();;
		}

		//完成删除账户明细
		function doAccountDetailDelForBtn() {
			//校验数据有效性
			var accountDetailTableRows = layui.table.checkStatus(accountDetailTableId);
			if (accountDetailTableRows.data.length == 0) {
				layer.alert("请选择一行或多行数据！", {"title" : "提示消息"});
				return;
			}
			
			//删除
			layer.confirm("确定要删除？", function(index) {
				$.ajax({
					url : "accountDetail/doDel",
					data : $.fn.my.serializeField(accountDetailTableRows.data),
					success : function(obj) {
						accountDetailQuery();
						
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
