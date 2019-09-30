<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>数据字典列表</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'center',border:false">
				<%-- 数据字典查询条件 --%>
				<div id="dictToolbar" style="padding:0 30px;">
					<div class="conditions">
						<form id="dictQueryForm">
							<span class="con-span">索引：</span>
							<input name="two" class="easyui-textbox" style="width:166px;height:35px;line-height:35px;">
							<span class="con-span">键：</span>
							<input name="three" class="easyui-textbox" style="width:166px;height:35px;line-height:35px;">
							<span class="con-span">值：</span>
							<input name="four" class="easyui-textbox" style="width:166px;height:35px;line-height:35px;">
							<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" data-options="selected:true" onclick="dictQuery();">查询</a>
							<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-clear" onclick="dictReset();">重置</a>
						</form>
					</div>
					<div class="opt-buttons">
						<my:auth url="dict/toAdd"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" data-options="selected:true" onclick="toDictAdd();">添加</a></my:auth>
						<my:auth url="dict/toEdit"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" onclick="toDictEditForBtn();">修改</a></my:auth>
						<my:auth url="dict/doDel"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" onclick="doDictDelForBtn();">删除</a></my:auth>
					</div>
				</div>
				<%-- 数据字典数据表格 --%>
				<table id="dictGrid">
				</table>
			</div>
		</div>
	</body>
	<script type="text/javascript">
		//定义变量
		var dictGrid = $("#dictGrid"); //数据字典表格对象
		var dictQueryForm = $("#dictQueryForm"); //数据字典查询对象
	
		//页面加载完毕，执行如下方法：
		$(function() {
			initDictGrid();
		});
	
		//初始化数据字典表格
		function initDictGrid() {
			dictGrid.datagrid( {
				url : "dict/list",
				onDblClickRow : <my:auth url="dict/toEdit">toDictEditForDblClick</my:auth>, 
				toolbar : "#dictToolbar",
				columns : [[ 
						{field : "ID", title : "", checkbox : true}, 
						{field : "DICT_INDEX", title : "索引", width : 80, align : "center"},
						{field : "DICT_KEY", title : "键", width : 80, align : "center"},
						{field : "DICT_VALUE", title : "值", width : 80, align : "center"},
						{field : "NO", title : "排序 ", width : 80, align : "center"}
						]]
			});
		}
	
		//数据字典查询
		function dictQuery() {
			dictGrid.datagrid("uncheckAll");
			dictGrid.datagrid("load", $.fn.my.serializeObj(dictQueryForm));
		}
	
		//数据字典重置
		function dictReset() {
			dictQueryForm.form("reset");
			dictQuery();
		}
		
		//到达添加数据字典页面
		function toDictAdd() {
			var dictAddDialog;
			dictAddDialog = $("<div/>").dialog({
				title : "添加数据字典",
				href : "dict/toAdd",
				buttons : [{
					text : "添加", 
					iconCls : "icon-add", 
					selected : true, 
					handler : function (){
						doDictAdd(dictAddDialog);
					}
				}],
				onLoad : function() {
					$("#dict_index").textbox({
						required : true,
						validType : ["length[1, 32]", "zsx"]
					});
					$("#dict_key").textbox({
						required : true,
						validType : ["length[1, 16]", "zs"]
					});
					$("#dict_value").textbox({
						required : true
					});
					$("#dict_no").numberspinner({
							required : true,
							min : 1,
							max : 100
					});
				}
			});
		}

		//完成添加数据字典
		function doDictAdd(dictAddDialog) {
			var dictEditFrom = $("#dictEditFrom");
			if(!dictEditFrom.form("validate")){
				return;
			}
			$.messager.confirm("确认消息", "确定要添加？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.progress();
				dictEditFrom.form("submit", {
					url : "dict/doAdd",
					success : function(data) {
						dictGrid.datagrid("reload");
						$.messager.progress("close");
						
						var obj = $.parseJSON(data);
						if (!obj.succ) {
							parent.$.messager.alert("提示消息", obj.msg, "info");
							return;
						}

						dictAddDialog.dialog("close");
					}
				});
			})
		}

		//到达修改数据字典页面
		function toDictEdit(id) {
			var dictEditDialog;
			dictEditDialog = $("<div/>").dialog({
				title : "修改数据字典",
				href : "dict/toEdit",
				queryParams : {"id" : id},
				buttons : [{
					text : "修改", 
					iconCls : "icon-edit", 
					selected : true,
					handler : function() {
						doDictEdit(dictEditDialog);
					}
				}],
				onLoad : function() {
					$("#dict_index").textbox({
						required : true,
						validType : ["length[1, 32]", "zsx"]
					});
					$("#dict_key").textbox({
						required : true,
						validType : ["length[1, 16]", "zs"]
					});
					$("#dict_value").textbox({
						required : true
					});
					$("#dict_no").numberspinner({
						required : true,
						min : 1,
						max : 100
					});
				}
			});
		}

		//到达修改数据字典页面
		function toDictEditForBtn() {
			var dictGridRows = dictGrid.datagrid("getChecked");
			if (dictGridRows.length != 1) {
				parent.$.messager.alert("提示消息", "请选择一行数据！", "info");
				return;
			}

			toDictEdit(dictGridRows[0].ID);
		}

		//到达修改数据字典页面
		function toDictEditForDblClick(rowIndex, rowData) {
			dictGrid.datagrid("uncheckAll");
			dictGrid.datagrid("checkRow", rowIndex);
			toDictEditForBtn();
		}

		//完成修改数据字典
		function doDictEdit(dictEditDialog) {
			var dictEditFrom = $("#dictEditFrom");
			if(!dictEditFrom.form("validate")){
				return;
			}
			$.messager.confirm("确认消息", "确定要修改？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.progress();
				dictEditFrom.form("submit", {
					url : "dict/doEdit",
					success : function(data) {
						dictGrid.datagrid("reload");
						$.messager.progress("close");

						var obj = $.parseJSON(data);
						if (!obj.succ) {
							parent.$.messager.alert("提示消息", obj.msg, "info");
							return;
						}

						dictEditDialog.dialog("close");
					}
				});
			})
		}

		//完成删除数据字典
		function doDictDel(params) {
			$.messager.confirm("确认消息", "确定要删除？", function(ok) {
				if (!ok) {
					return;
				}

				$.messager.progress();
				$.ajax({
					url : "dict/doDel",
					data : params,
					success : function(obj) {
						dictGrid.datagrid("reload");
						$.messager.progress("close");
						
						if (!obj.succ) {
							parent.$.messager.alert("提示消息", obj.msg, "info");
						}
					}
				});
			});
		}

		//完成删除数据字典
		function doDictDelForBtn() {
			//校验数据有效性
			var dictGridRows = dictGrid.datagrid("getChecked");
			if (dictGridRows.length == 0) {
				parent.$.messager.alert("提示消息","请选择一行或多行数据！", "info");
				return;
			}

			//删除
			doDictDel($.fn.my.serializeField(dictGridRows));
		}
	</script>
</html>