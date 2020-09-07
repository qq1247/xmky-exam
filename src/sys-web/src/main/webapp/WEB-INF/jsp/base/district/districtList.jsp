<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>行政区域列表</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
			<div class="layui-fluid">
			<div class="layui-row layui-col-space10">
				<div class="layui-col-md2">
					<div class="layui-card">
						<div class="layui-form">
			      			<ul id="districtTree" class="ztree" style="overflow: auto;"></ul>
			 			</div>
					</div>
				</div>
				<div class="layui-col-md10">
					<div class="layui-card">
				    	<%-- 行政区域查询条件 --%>
						<form id="districtQueryForm" class="layui-form layui-card-header layuiadmin-card-header-auto">
							<input type="hidden" id="district_one" name="one">
							<div class="layui-form-item"> 
								 <div class="layui-inline">
									<input type="text" name="two" placeholder="请输入名称" class="layui-input">
								</div>
								<div class="layui-inline">
									<input type="text" name="three" placeholder="请输入编码" class="layui-input">
								</div>
								<div class="layui-inline">
									<select name="four">
										<option value="">请选择类型</option>
										<c:forEach var="districtType" items="${districtTypeList }">
					     				<option value="${districtType.dictKey }">${districtType.dictValue }</option>
					     				</c:forEach>
					      			</select>
								</div>
								<div class="layui-inline">
									<select name="five">
										<option value="">请选择层级</option>
					     				<option value="1">1</option>
					     				<option value="2">2</option>
					     				<option value="3">3</option>
					     				<option value="4">4</option>
					      			</select>
								</div>
								<div class="layui-inline">
									<button type="button" class="layui-btn layuiadmin-btn-useradmin" onclick="districtQuery();">
										<i class="layui-icon layuiadmin-button-btn"></i>查询
									</button>
									<button type="button" class="layui-btn layui-btn-primary layuiadmin-btn-useradmin" onclick="districtReset();">
										<i class="layui-icon layuiadmin-button-btn"></i>重置
									</button>
								</div>
							 </div> 
						</form>
						<div class="layui-card-body">
							<div style="padding-bottom: 10px;">
							</div>
							<%-- 行政区域数据表格 --%>
							<table id="districtTable" lay-filter="districtTable"></table>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
	<%@include file="/script/myJs/tail.jspf"%>
	<script type="text/javascript">
		//定义变量
		var districtQueryForm = $("#districtQueryForm"); //行政区域查询对象
		var curSelDistrictId = ""; //当前选中的行政区域ID
		var curSelDistrictName = ""; //当前选中的行政区域名称
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initDistrictTable();
			initDistrictTree();
		});
	
		//初始化行政区域表格
		function initDistrictTable() {
			layui.table.render({
				elem : "#districtTable",
				url : "district/list",
				/* toolbar : "#districtToolbar", */
				cols : [[ 
						{field : "NAME", title : "名称", align : "center"},
						{field : "PARENT_NAME", title : "上级行政区域", align : "center"},
						{field : "CODE", title : "编号", align : "center"},
						{field : "TYPE_NAME", title : "类型", align : "center"},
						{field : "LEVEL", title : "层级", align : "center"},
						{field : "NO", title : "排序", align : "center"}
						]],
				page : true,
				height : "full-180",
				method : "post",
				defaultToolbar : [],
				parseData: function(district){
					return {
						"code" : district.succ,
						"msg" : district.msg,
						"count" : district.data.total,
						"data" : district.data.rows
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
		}
		
		//初始化行政区域树
		function initDistrictTree() {
			districtTree = $.fn.zTree.init($("#districtTree"), {
				async : {
					url : "district/treeList",
					enable : true,
					dataFilter : ztreeDataFilter
				},
				callback : {
					onClick : function(event, treeId, treeNode) {
						curSelDistrictId = treeNode.ID;
						curSelDistrictName = treeNode.NAME;
						$("#district_one").val(curSelDistrictId);
						districtQuery();
					},
					onAsyncSuccess : function(event, treeId, msg, treeNode) {
						var districtTree = $.fn.zTree.getZTreeObj(treeId);
						districtTree.expandAll(true);
						
						if (!curSelDistrictId) {
							var rootNode = districtTree.getNodesByFilter(function(node) { return (node.level == 0); }, true);
							districtTree.selectNode(rootNode);
							
							curSelDistrictId = rootNode.ID;
							curSelDistrictName = rootNode.NAME;
							$("#district_one").val(curSelDistrictId);
							return;
						}
						
						var curNode = districtTree.getNodeByParam("id", curSelDistrictId, null);
						districtTree.selectNode(curNode);
						
						districtQuery();
					}
				}
			});
			
			$("#districtTree").height($(window).height() - 80);
		}
		
		//行政区域查询
		function districtQuery() {
			var where = $.fn.my.serializeObj(districtQueryForm);
			layui.table.reload("districtTable", {"where" : where});
		}
	
		//行政区域重置
		function districtReset() {
			districtQueryForm[0].reset();
			districtQuery();
		}
	</script>
</html>
