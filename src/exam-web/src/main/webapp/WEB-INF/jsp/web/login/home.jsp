<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="my" uri="myTag/core"%>

<html>
<head>
<title>首页</title>
<%@include file="/script/myJs/common.jspf"%>
<link rel="stylesheet" type="text/css" href="css/uimaker/green/css/platform.css">
<script type="text/javascript" src="script/uimaker/main.js"></script>
</head>
<body>
	<div class="container">
		<div id="pf-hd">
			<div class="pf-logo">
				<img src="css/uimaker/green/images/main/main_logo.png" alt="logo">
			</div>
			<div class="pf-nav-wrap">
				<!--<div class="pf-nav-ww">-->
				<div class="pf-nav-ww">
					<ul class="pf-nav">
					</ul>
				</div>
				<!-- </div> -->
				<a href="javascript:;" class="pf-nav-prev disabled iconfont">&#xe606;</a>
				<a href="javascript:;" class="pf-nav-next iconfont">&#xe607;</a>
			</div>
			<div class="pf-skin">
				<i class="iconfont">&#xe708;</i>
				<ul class="skin-list">
					<li class="skin-item" data-color="green"></li>
					<li class="skin-item" data-color="blue"></li>
					<li class="skin-item" data-color="lightblue"></li>
					<li class="skin-item" data-color="orange"></li>
					<li class="skin-item" data-color="red"></li>
					<li class="skin-item" data-color="darkblue"></li>
					<li class="skin-item" data-color="black"></li>
					<li class="skin-item" data-color="darkgrey"></li>
					<li class="skin-item" data-color="light"></li>
				</ul>
			</div>
			<div class="pf-user">
				<span class="msgts">0</span>
				<div class="pf-user-photo">
					<img src="css/uimaker/green/images/main/user.png" alt="">
				</div>
				<h4 class="pf-user-name ellipsis">${USER.name }</h4>
				<i class="iconfont xiala">&#xe607;</i>
				<div class="pf-user-panel">
					<ul class="pf-user-opt">
						<%-- 
						<li>
							<a href="javascript:;">
								<i class="iconfont">&#xe60d;</i>
								<span class="pf-opt-name">用户信息</span>
							</a>
						</li>
						<li class="pf-modify-pwd">
							<a href="http://www.exam.com">
								<i class="iconfont">&#xe6f8;</i>
								<span class="pf-opt-name">
									消息
									<i class="mailtext">[10]</i>
								</span>
							</a>
						</li>--%>
						<li class="pf-modify-pwd">
							<a href="javascript:void(0);" onclick="toUpdatePwd()">
								<i class="iconfont">&#xe634;</i>
								<span class="pf-opt-name">修改密码</span>
							</a>
						</li>
						<li class="pf-logout">
							<a href="home/pubToHome">
								<i class="iconfont">&#xe60e;</i>
								<span class="pf-opt-name">返回前台</span>
							</a>
						</li>
						<li class="pf-logout">
							<a href="login/pubDoOut">
								<i class="iconfont">&#xe60e;</i>
								<span class="pf-opt-name">退出</span>
							</a>
						</li>
					</ul>
				</div>
			</div>
		</div>
		<div id="pf-bd">
			<div class="pf-sider-wrap">
			</div>
			<div id="pf-page">
			</div>
		</div>
		<div id="pf-ft">
			<div class="system-name">
				<i class="iconfont">&#xe6fe;</i>
				<span>在线考试系统&nbsp;v1.0</span>
			</div>
			<div class="copyright-name">
				<span>CopyLeft&nbsp;2017&nbsp;&nbsp;exam.wcpdoc.com&nbsp;</span>
				<i class="iconfont">&#xe6ff;</i>
			</div>
		</div>
	</div>
	<div id="mm" class="easyui-menu tabs-menu" style="width: 120px; display: none;">
		<div id="mm-tabclose">刷新</div>
		<div id="mm-tabcloseother">关闭其他</div>
		<div id="mm-tabcloseall">关闭所有</div>
	</div>
	<!--[if IE 7]>
      <script type="text/javascript">
        $(window).resize(function(){
          $('#pf-bd').height($(window).height()-76);
        }).resize();
        
      </script>
    <![endif]-->

	<script type="text/javascript">
		var SystemMenu = 
		[<c:forEach var="menu" items="${menuList[0].children }" varStatus="s"><c:if test="${s.index != 0}">,</c:if>{
			title : "${menu.name}",
			icon : "&#xe63f;",
			<c:if test="${s.index == 0}">"isCurrent": true,</c:if>
			menu: [<c:forEach var="menu2" items="${menu.children }" varStatus="s2"><c:if test="${s2.index != 0}">,</c:if>{
				title: "${menu2.name}",
				icon: "&#xe620;",
				<c:if test="${s2.index == 0}">"isCurrent": true,</c:if>
				children: [<c:forEach var="menu3" items="${menu2.children }" varStatus="s3"><c:if test="${s3.index != 0}">,</c:if>{
						title: "${menu3.name}",
						href: "${menu3.url}",
						<c:if test="${s2.index == 0 && s3.index == 0}">"isCurrent": true</c:if>
					}
					</c:forEach>]
			}</c:forEach>]
		}
		</c:forEach>];
		
		mainPlatform.init();
		$(window).resize(function() {
			$('.tabs-panels').height($("#pf-page").height() - 46);
			$('.panel-body').not('.messager-body').height($(".easyui-dialog").height)
		}).resize();
		var page = 0, pages = ($('.pf-nav').height() / 70) - 1;

		if (pages === 0) {
			$('.pf-nav-prev,.pf-nav-next').hide();
		}
		$(document).on('click', '.pf-nav-prev,.pf-nav-next', function() {
			if ($(this).hasClass('disabled'))
				return;
			if ($(this).hasClass('pf-nav-next')) {
				page++;
				$('.pf-nav').stop().animate({
					'margin-top' : -70 * page
				}, 200);
				if (page == pages) {
					$(this).addClass('disabled');
					$('.pf-nav-prev').removeClass('disabled');
				} else {
					$('.pf-nav-prev').removeClass('disabled');
				}
			} else {
				page--;
				$('.pf-nav').stop().animate({
					'margin-top' : -70 * page
				}, 200);
				if (page == 0) {
					$(this).addClass('disabled');
					$('.pf-nav-next').removeClass('disabled');
				} else {
					$('.pf-nav-next').removeClass('disabled');
				}
			}
		})

		// setTimeout(function(){
		//    $('.tabs-panels').height($("#pf-page").height()-46);
		//    $('.panel-body').height($("#pf-page").height()-76)
		// }, 200)
		function replace(doc, style) {
			$('link', doc).each(function(index, one) {
				var path = $(one).attr('href').replace(/(static\/)\w+(\/css)/g, '$1' + style + '$2').replace(/(custom\/)\w+(\/)/g, '$1' + style + '$2'), sheet;
				if (doc.createStyleSheet) {
					sheet = doc.createStyleSheet(path);
					setTimeout(function() {
						$(one).remove();
					}, 500)
				} else {
					sheet = $('<link rel="stylesheet" type="text/css" href="' + path + '" />').appendTo($('head', doc));
					sheet = sheet[0];
					sheet.onload = function() {
						$(one).remove();
					}
				}
			})
			$('img', doc).each(function(index, one) {
				var path = $(one).attr('src').replace(/(static\/)\w+(\/images)/g, '$1' + style + '$2');
				$(one).attr('src', path);
			})
		}
		
		$('.skin-item').click(function() {
			var color = $(this).data('color');
			replaceAll(color);
		})
		
		function replaceAll(style) {
			$('iframe').each(function(index, one) {
				try {
					replace(one.contentWindow.document, style)
				} catch (e) {
					console.warn('origin cross');
				}
			})
			replace(document, style);
		}
		
		//到达修改密码页面
		function toUpdatePwd(){
			var updatePwdDialog;
			updatePwdDialog = $("<div/>").dialog({
				title : "修改密码",
				width : 350,
				height : 230,
				href : "login/pubToPwdUpdate",
				buttons : [{
					text : "确定",
					iconCls : "icon-ok",
					selected : true,
					handler : function (){
						doUpdatePwd(updatePwdDialog);
					}
				},{
					text : "取消",
					iconCls : "icon-cancel",
					handler : function (){
						updatePwdDialog.dialog("close");
					}
				}],
				onLoad : function(){
					$("#login_pwd").textbox({
						required : true,
						validType : ["length[1, 16]", "zs"]
					});
					$("#login_newPwd").textbox({
						required : true,
						validType : ["length[1, 16]", "zs"]
					});
					$("#login_newPwd2").textbox({
						required : true,
						validType : ["length[1, 16]", "zs", "equalTo['#login_newPwd']"]
					});
				},
				onMove : function(){
					
				}
			});
		}
		
		//完成修改密码
		function doUpdatePwd(updatePwdDialog){
			var updatePwdEditFrom = $("#updatePwdEditFrom");
			if(!updatePwdEditFrom.form("validate")){
				return;
			}
			
			$.messager.confirm("确认消息", "确定要修改？", function(ok) {
				if (!ok) {
					return;
				}
				
				$.messager.progress();
				updatePwdEditFrom.form("submit", {
					url : "login/pubDoPwdUpdate",
					success : function(data) {
						$.messager.progress("close"); 
						var obj = $.parseJSON(data);
						if(!obj.success){
							parent.$.messager.alert("提示消息", obj.message, "info");
							return;
						}
						
						updatePwdDialog.dialog("close");
					}
				});
			})
		}
	</script>
</body>
</html>

