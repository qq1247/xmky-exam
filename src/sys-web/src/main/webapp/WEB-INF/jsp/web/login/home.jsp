<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>首页</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body class="layui-layout-body">
		<div id="LAY_app">
			<div class="layui-layout layui-layout-admin">
				<div class="layui-header">
					<!-- 头部区域 -->
					<ul class="layui-nav layui-layout-left">
						<li class="layui-nav-item layadmin-flexible" lay-unselect>
							<a href="javascript:;" layadmin-event="flexible" title="侧边伸缩">
								<i class="layui-icon layui-icon-shrink-right" id="LAY_app_flexible"></i>
							</a>
						</li>
						<li class="layui-nav-item layui-hide-xs" lay-unselect>
							<a href="#" target="_blank" title="前台">
								<i class="layui-icon layui-icon-website"></i>
							</a>
						</li>
						<li class="layui-nav-item" lay-unselect>
							<a href="javascript:;" layadmin-event="refresh" title="刷新">
								<i class="layui-icon layui-icon-refresh-3"></i>
							</a>
						</li>
						<li class="layui-nav-item layui-hide-xs" lay-unselect>
							<input type="text" placeholder="搜索..." autocomplete="off" class="layui-input layui-input-search"
								layadmin-event="serach" lay-action="template/search.html?keywords=">
						</li>
					</ul>
					<ul class="layui-nav layui-layout-right" lay-filter="layadmin-layout-right">
	
						<li class="layui-nav-item" lay-unselect>
							<a lay-href="app/message/index.html" layadmin-event="message" lay-text="消息中心">
								<i class="layui-icon layui-icon-notice"></i>
	
								<!-- 如果有新消息，则显示小圆点 -->
								<span class="layui-badge-dot"></span>
							</a>
						</li>
						<li class="layui-nav-item layui-hide-xs" lay-unselect>
							<a href="javascript:;" layadmin-event="theme">
								<i class="layui-icon layui-icon-theme"></i>
							</a>
						</li>
						<li class="layui-nav-item layui-hide-xs" lay-unselect>
							<a href="javascript:;" layadmin-event="note">
								<i class="layui-icon layui-icon-note"></i>
							</a>
						</li>
						<li class="layui-nav-item layui-hide-xs" lay-unselect>
							<a href="javascript:;" layadmin-event="fullscreen">
								<i class="layui-icon layui-icon-screen-full"></i>
							</a>
						</li>
						<li class="layui-nav-item" lay-unselect>
							<a href="javascript:;">
								<cite>${USER.name }</cite>
							</a>
							<dl class="layui-nav-child">
								<dd>
									<a lay-href="">基本资料</a>
								</dd>
								<dd>
									<a href="javascript:void(0);" onclick="topwdUpdate();">修改密码</a>
								</dd>
								<hr>
								<dd style="text-align: center;">
									<a href="login/doOut">退出</a>
								</dd>
							</dl>
						</li>
	
						<li class="layui-nav-item layui-hide-xs" lay-unselect>
							<a href="javascript:;" layadmin-event="about">
								<i class="layui-icon layui-icon-more-vertical"></i>
							</a>
						</li>
						<li class="layui-nav-item layui-show-xs-inline-block layui-hide-sm" lay-unselect>
							<a href="javascript:;" layadmin-event="more">
								<i class="layui-icon layui-icon-more-vertical"></i>
							</a>
						</li>
					</ul>
				</div>
	
				<!-- 侧边菜单 -->
				<div class="layui-side layui-side-menu">
					<div class="layui-side-scroll">
						<div class="layui-logo" lay-href="#">
							<span>管理系统</span>
						</div>

					<ul class="layui-nav layui-nav-tree" lay-shrink="all" id="LAY-system-side-menu" lay-filter="layadmin-system-side-menu">
						<c:forEach var="menu" items="${MENU_LIST[0].children }" varStatus="s">
						<c:forEach var="menu2" items="${menu.children }" varStatus="s2">
						<my:auth url="${menu2.url }">
						<li data-name="${menu2.name}" class="layui-nav-item ${s2.index == 0 ? "layui-nav-itemed" : "" }">
							<a href="javascript:;" lay-tips="${menu2.name}" lay-direction="2">
								<i class="layui-icon layui-icon-home"></i>
								<cite>${menu2.name}</cite>
							</a>
							<dl class="layui-nav-child">
								<c:forEach var="menu3" items="${menu2.children }" varStatus="s3">
									<my:auth url="${menu3.url }">
										<dd data-name="${menu2.name}" ${s2.index == 0 && s3.index == 0 ? 'class="layui-this"' : '' }>
											<a lay-href="${menu3.url }">${menu3.name}</a>
										</dd>
									</my:auth>
								</c:forEach>
							</dl>
						</li>
						</my:auth>
						</c:forEach>
						</c:forEach>
					</ul>
				</div>
				</div>
	
				<!-- 页面标签 -->
				<div class="layadmin-pagetabs" id="LAY_app_tabs">
					<div class="layui-icon layadmin-tabs-control layui-icon-prev" layadmin-event="leftPage"></div>
					<div class="layui-icon layadmin-tabs-control layui-icon-next" layadmin-event="rightPage"></div>
					<div class="layui-icon layadmin-tabs-control layui-icon-down">
						<ul class="layui-nav layadmin-tabs-select" lay-filter="layadmin-pagetabs-nav">
							<li class="layui-nav-item" lay-unselect>
								<a href="javascript:;"></a>
								<dl class="layui-nav-child layui-anim-fadein">
									<dd layadmin-event="closeThisTabs">
										<a href="javascript:;">关闭当前标签页</a>
									</dd>
									<dd layadmin-event="closeOtherTabs">
										<a href="javascript:;">关闭其它标签页</a>
									</dd>
									<dd layadmin-event="closeAllTabs">
										<a href="javascript:;">关闭全部标签页</a>
									</dd>
								</dl>
							</li>
						</ul>
					</div>
					<div class="layui-tab" lay-unauto lay-allowClose="true" lay-filter="layadmin-layout-tabs">
						<ul class="layui-tab-title" id="LAY_app_tabsheader">
							<li lay-id="#" lay-attr="" class="layui-this">
								<i class="layui-icon layui-icon-home"></i>
							</li>
						</ul>
					</div>
				</div>
	
	
				<!-- 主体内容 -->
				<div class="layui-body" id="LAY_app_body">
					<div class="layadmin-tabsbody-item layui-show">
						<iframe src="" frameborder="0" class="layadmin-iframe"></iframe>
					</div>
				</div>
	
				<!-- 辅助元素，一般用于移动设备下遮罩 -->
				<div class="layadmin-body-shade" layadmin-event="shade"></div>
			</div>
		</div>
		
	</body>
	<%@include file="/script/myJs/tail.jspf"%>
	<script type="text/javascript">
		// 到达修改密码页面
		function topwdUpdate() {
			$.ajax({
				url : "login/toPwdUpdate",
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "修改密码",
						area : ["400px", "250px"],
						content : obj,
						btn : ["修改", "取消"],
						type : 1,
						yes : function(index, layero) {
							doPwdUpdate(index);
						},
						success: function(layero, index) {
							
						}
					});
				}
			});
		}
		
		//完成修改密码
		function doPwdUpdate(pwdUpdateIndex) {
			layui.form.on("submit(pwdUpdateBtn)", function(data) {
				layer.confirm("确定要修改？", function(index) {
					$.ajax({
						url : "login/doPwdUpdate",
						data : data.field,
						success : function(obj) {
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
							layer.close(pwdUpdateIndex);
						}
					});
				});
			});
			$("[lay-filter='pwdUpdateBtn']").click();
		}
	</script>
</html>

