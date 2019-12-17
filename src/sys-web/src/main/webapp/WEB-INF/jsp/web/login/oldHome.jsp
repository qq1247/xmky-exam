<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="my" uri="myTag/core"%>

<html>
	<head>
		<title>首页</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'north',border:false,split:true">
				<div>
					<h1>后台管理</h1>
				</div>
				<div>
					<p>${USER.name }，欢迎您！</p>
					<p>
						<a href="javascript:void(0);">用户手册</a>
						|<a href="login/doOut">安全退出</a>
					</p>
				</div>
			</div>
			<div data-options="region:'south',split:true">copyright &copy; 版权所有</div>
			<%-- 
			<div data-options="region:'east',split:true" title="East" style="width: 180px;">
				<ul class="easyui-tree" data-options="url:'tree_data1.json',method:'get',animate:true,dnd:true"></ul>
			</div> --%>
			<div data-options="region:'west',split:true,border:false" title="导航菜单" style="width: 220px;">
				<div class="easyui-accordion" data-options="border:false,cache:false,fit:true">
					<c:forEach var="menu" items="${MENU_LIST }">
					<my:auth url="${menu.url }">
					<div title="${menu.name }" style="padding: 5px;">
						<ul class="easyui-tree" data-options="onClick:openTab">
							<c:forEach var="subMenu" items="${menu.subMenuList }">
							<my:auth url="${subMenu.url }">
							<li data-options="id:${subMenu.id },attributes:{'title':'${subMenu.name }','url':'${subMenu.url }'}">
								<a href="javascript:void(0);">${subMenu.name }</a>
							</li>
							</my:auth>
							</c:forEach>
						</ul>
					</div>
					</my:auth>
					</c:forEach>
				</div>
			</div>
			<div data-options="region:'center',border:false,title:''">
				<div id="tabs" class="easyui-tabs" data-options="fit:true">
				</div>
			</div>
		</div>
	</body>
	<script type="text/javascript">
		//定义变量
		var $tabs = $("#tabs");
		
		//打开tab页
		function openTab(node) {
			//如果已打开标签页，则选中并返回。
			if($tabs.tabs("exists", node.attributes.title)){
				$tabs.tabs("sel", node.attributes.title);
				return;
			}
			
			//打开标签页
			$tabs.tabs("add", {
				title : node.attributes.title,
				closable : true, 
				content : "<iframe scrolling='no' frameborder='0' style='width: 100%;height: 100%;' src='"+node.attributes.url+"'></iframe>",
				tools : [{
					iconCls : "icon-mini-refresh",
					handler : function() {
						$tabs.tabs("sel", node.attributes.title);
						var curTab = $tabs.tabs("getSeled");
						$tabs.tabs("update", {
							tab: curTab,
							options: { }
						});
					}
				}]
			});
		}
	</script>
</html>