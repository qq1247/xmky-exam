<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>附件列表</title>
		<%@include file="/script/myJs/common.jspf"%>
		<script type="text/javascript" src="script/plupload/plupload.full.min.js"></script>
		<script type="text/javascript" src="script/plupload/i18n/zh_CN.js"></script>
	</head>
	<body>
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'center',border:false">
				<%-- 附件查询条件 --%>
				<div id="fileToolbar" style="padding:0 30px;">
					<div class="conditions">
						<form id="fileQueryForm">
							<span class="con-span">名称： </span>
							<input name="two" class="easyui-textbox" style="width:166px;height:35px;line-height:35px;">
							<span class="con-span">扩展名： </span>
							<input name="three" class="easyui-textbox" style="width:166px;height:35px;line-height:35px;">
							<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" data-options="selected:true" onclick="fileQuery();">查询</a>
							<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-clear" onclick="fileReset();">重置</a>
						</form>
					</div>
					<div class="opt-buttons">
						<my:auth url="file/toUpload"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" data-options="selected:true" onclick="toFileUpload();">上传</a></my:auth>
						<my:auth url="file/doDownload"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" onclick="doFileDownload();">下载</a></my:auth>
						<my:auth url="file/doDel"><a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" onclick="doFileDelForBtn();">删除</a></my:auth>
					</div>
				</div>
				<%-- 附件数据表格 --%>
				<table id="fileGrid">
				</table>
			</div>
		</div>
	</body>
	<script type="text/javascript">
		//定义变量
		var fileGrid = $("#fileGrid"); //附件表格对象
		var fileQueryForm = $("#fileQueryForm"); //附件查询对象
		var uploader; //附件上传对象
	
		//页面加载完毕，执行如下方法：
		$(function() {
			initFileGrid();
		});
	
		//初始化附件表格
		function initFileGrid() {
			fileGrid.datagrid( {
				url : "file/list",
				toolbar : "#fileToolbar",
				columns : [[ 
						{field : "ID", title : "", checkbox : true}, 
						{field : "NAME", title : "名称", width : 80, align : "center"},
						{field : "EXT_NAME", title : "扩展名", width : 80, align : "center"},
						{field : "IP", title : "上传ip", width : 80, align : "center"},
						{field : "USERNAME", title : "上传用户", width : 80, align : "center"},
						{field : "UPDATE_TIME_STR", title : "上传时间", width : 80, align : "center"},
						]]
			});
		}
	
		//附件查询
		function fileQuery() {
			fileGrid.datagrid("uncheckAll");
			fileGrid.datagrid("load", $.fn.my.serializeObj(fileQueryForm));
		}
	
		//附件重置
		function fileReset() {
			fileQueryForm.form("reset");
			fileQuery();
		}
		
		//到达上传附件页面
		function toFileUpload() {
			var fileUploadDialog;
			fileUploadDialog = $("<div/>").dialog({
				title : "上传附件",
				href : "file/toUpload",
				buttons : [{
					text : "上传", 
					iconCls : "icon-add", 
					selected : true,
					handler : function (){
						doFileUpload(fileUploadDialog);
					}
				}],
				onLoad : function() {
					uploader = new plupload.Uploader({
						browse_button : "file_browse",
						file_data_name : "files",
						url : "${pageContext.request.contextPath}/file/doTempUpload",
						flash_swf_url : "script/plupload/Moxie.swf",
						filters : {
							mime_types : [{ title : "图片", extensions : "jpg,gif,png" }, 
							              { title : "压缩文件", extensions : "zip,rar" },
							              { title : "文档", extensions : "doc,docx,xls,xlsx" }
							              ],
							max_file_size : "99mb",
							prevent_duplicates : true
						},
						init : {
							FilesAdded : function(up, files) {
								$("#file_console").html("");
								
								var fileListObj = $("#file_list");
								$.each(files, function(index, domEle){
									var html = [];
									html.push("<div id='file_row_" + domEle.id + "'>");
									html.push("	<input type='hidden' id='ids_" + domEle.id + "' name='ids'>");
									html.push("	<span>" + domEle.name + "（" + plupload.formatSize(domEle.size) + "）</span>");
									html.push("	<span id='upload_progress_" + domEle.id + "'>0%</span>");
									html.push("	<a href='javascript:void(0);' onclick='removeRowForFile(\"" + domEle.id + "\")'>移除</a>");
									html.push("<div>");
									fileListObj.append(html.join(""));
								});
								uploader.start();
							},
							UploadProgress : function(up, file) {
								$("#upload_progress_" + file.id).html(file.percent + "%");
							},
							FileUploaded : function(up, file, responseObj) { //每个附件上传后，服务端返回的响应消息。
								var response = $.parseJSON(responseObj.response);
								if(response.success != true){
									$("#upload_progress_" + file.id).html(response.message);
									return;
								}
								$("#ids_" + file.id).val(response.data.fileIds);
							},
							UploadComplete : function(up, files){//所有附件上传完成后
								
							},
							Error : function(up, err) { //客户端的错误消息。如附件大小错误，附件不存在， http错误等。
								$("#file_console").html("");
								
								var uploadProgressObj = $("#upload_progress_" + err.file.id);
								if(uploadProgressObj[0]){
									uploadProgressObj.html(err.message);
								}else{
									$("#file_console").html(err.message);
								}
							}
						}
					});
					
					uploader.init();
				}
			});
		}
		
		//完成上传附件
		function doFileUpload(fileUploadDialog){
			var fileUploadEditFrom = $("#fileUploadEditFrom");
			$.messager.confirm("确认消息", "确定要上传？", function(ok) {
				if (!ok) {
					return;
				}
				
				var queued = uploader.total.queued; 
				if(queued != 0){
					parent.$.messager.alert("提示消息", "正在上传中。。。", "info");
					return;
				}
				
				fileUploadEditFrom.form("submit", {
					url : "file/doUpload",
					success : function(data) {
						fileGrid.datagrid("reload");
						$.messager.progress("close");

						var obj = $.parseJSON(data);
						if (!obj.success) {
							parent.$.messager.alert("提示消息", obj.message, "info");
							return;
						}

						fileUploadDialog.dialog("close");
					}
				});
			})
		}

		//完成删除附件
		function doFileDel(params) {
			$.messager.confirm("确认消息", "确定要删除？", function(ok) {
				if (!ok) {
					return;
				}

				$.messager.progress();
				$.ajax({
					url : "file/doDel",
					data : params,
					success : function(obj) {
						fileGrid.datagrid("reload");
						$.messager.progress("close");
						
						if (!obj.success) {
							parent.$.messager.alert("提示消息", obj.message, "info");
						}
					}
				});
			});
		}

		//完成删除附件
		function doFileDelForBtn() {
			//校验数据有效性
			var fileGridRows = fileGrid.datagrid("getChecked");
			if (fileGridRows.length == 0) {
				parent.$.messager.alert("提示消息", "请选择一行或多行数据！", "info");
				return;
			}

			//删除
			doFileDel($.fn.my.serializeField(fileGridRows));
		}
		
		//移除单行附件
		function removeRowForFile(fileid){
			$("#file_row_" + fileid).remove();
			uploader.removeFile(uploader.getFile(fileid));
		}
		
		//完成附件下载
		function doFileDownload(){
			var fileGridRows = fileGrid.datagrid("getChecked");
			if (fileGridRows.length != 1) {
				parent.$.messager.alert("提示消息", "请选择一行数据！", "info");
				return;
			}
			
			window.location.href = "file/doDownload?id=" + fileGridRows[0].ID;
		}
		
	</script>
</html>