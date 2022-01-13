<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh">
	<head>
		<%
			String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
					+ request.getContextPath() + "/";
		%>
		<base href="<%=basePath%>">
		<meta charset="utf-8" />
		<title>Full example - Editor.md examples</title>
		<link rel="stylesheet" href="script/markdown/css/style.css" />
		<link rel="stylesheet" href="script/markdown/css/editormd.css" />
		<link rel="shortcut icon" href="#" type="image/x-icon" />
	</head>
	<body>
		<div id="layout">
			<div class="btns">
				<select id="mdSelect">
					<option value="">请选择</option>
					<c:forEach var="mdFile" items="${mdFileList }">
					<option value="${mdFile.id}">${mdFile.file }</option>
					</c:forEach>
				</select>
				<button id="mdSave">保存</button>
			</div>
			<div id="test-editormd"></div>
		</div>
		<script src="script/markdown/js/jquery.min.js"></script>
		<script src="script/markdown/js/editormd.min.js"></script>
		<script type="text/javascript">
			var editor;
			$(function() {
				editor = editormd("test-editormd", {
					width : "90%",
					height : 740,
					path : 'script/markdown/lib/',
					//theme : "dark",
					//previewTheme : "dark",
					//editorTheme : "pastel-on-dark",
					markdown : "",
					codeFold : true,
					//syncScrolling : false,
					saveHTMLToTextarea : true, // 保存 HTML 到 Textarea
					searchReplace : true,
					watch : true,                // 关闭实时预览
					htmlDecode : "style,script,iframe|on*", // 开启 HTML 标签解析，为了安全性，默认不开启    
					//toolbar  : false,             //关闭工具栏
					//previewCodeHighlight : false, // 关闭预览 HTML 的代码块高亮，默认开启
					emoji : true,
					taskList : true,
					tocm : true, // Using [TOCM]
					tex : true, // 开启科学公式TeX语言支持，默认关闭
					flowChart : true, // 开启流程图支持，默认关闭
					sequenceDiagram : true, // 开启时序/序列图支持，默认关闭,
					//dialogLockScreen : false,   // 设置弹出层对话框不锁屏，全局通用，默认为true
					//dialogShowMask : false,     // 设置弹出层对话框显示透明遮罩层，全局通用，默认为true
					//dialogDraggable : false,    // 设置弹出层对话框不可拖动，全局通用，默认为true
					//dialogMaskOpacity : 0.4,    // 设置透明遮罩层的透明度，全局通用，默认值为0.1
					//dialogMaskBgColor : "#000", // 设置透明遮罩层的背景颜色，全局通用，默认为#fff
					imageUpload : false,
					imageFormats : [ "jpg", "jpeg", "gif", "png", "bmp", "webp" ],
					imageUploadURL : "./php/upload.php",
					onload : function() {
						this.previewing();
						//this.fullscreen();
						//this.unwatch();
						//this.watch().fullscreen();
	
						//this.setMarkdown("#PHP");
						//this.width("100%");
						//this.height(480);
						//this.resize("100%", 640);
						
						$("#mdSelect").change(function() {
							$.ajax({
								url : "doc/get",
								data : {
									id : $(this).val()
								},
								type : "post",
								async : false,
								cache : false,
								dataType : "json",
								traditional : true,
								success : function(data) {
									if (data.code != 200) {
										alert(data.msg);
										return;
									}
									
									editor.setMarkdown(data.data);
								}
							});
						});
						$("#mdSelect").val("apiExam");
						$("#mdSelect").trigger("change");

						
						$("#mdSave").click(function() {
							var pwd = prompt("请输入超管密码","");
							if (!pwd) {
								return;
							}
							
							$.ajax({
								url : "doc/edit",
								data : {
									id : $("#mdSelect").val(),
									content : editor.getMarkdown(),
									pwd : pwd
								},
								type : "post",
								async : false,
								cache : false,
								dataType : "json",
								traditional : true,
								success : function(data) {
									if (data.code != 200) {
										alert(data.msg);
										return;
									}
								}
							});
						});
					}
				});
			});
		</script>
	</body>
</html>