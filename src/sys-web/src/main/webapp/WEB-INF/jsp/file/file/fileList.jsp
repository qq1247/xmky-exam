<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>附件列表</title>
		<%@include file="/script/myJs/common.jspf"%>
	</head>
	<body>
		<div class="layui-fluid">
			<div class="layui-card">
				<%-- 附件查询条件 --%>
				<form id="fileQueryForm" class="layui-form layui-card-header layuiadmin-card-header-auto">
					<div class="layui-form-item">
						<div class="layui-inline">
							<input type="text" name="two" placeholder="请输入名称" class="layui-input">
						</div>
						<div class="layui-inline">
							<input type="text" name="three" placeholder="请输入扩展名" class="layui-input">
						</div>
						<div class="layui-inline">
							<button type="button" class="layui-btn layuiadmin-btn-useradmin" onclick="fileQuery();">
								<i class="layui-icon layuiadmin-button-btn"></i>查询
							</button>
							<button type="button" class="layui-btn layui-btn-primary layuiadmin-btn-useradmin" onclick="fileReset();">
								<i class="layui-icon layuiadmin-button-btn"></i>重置
							</button>
						</div>
					</div>
				</form>
				<div class="layui-card-body">
					<div style="padding-bottom: 10px;">
						<my:auth url="file/toUpload"><button class="layui-btn layuiadmin-btn-useradmin" onclick="toFileUpload();">上传</button></my:auth>
					</div>
					<script type="text/html" id="fileToolbar">
						<my:auth url="file/doDownload"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="fileDownload"><i class="layui-icon layui-icon-edit"></i>下载</a></my:auth>
						<my:auth url="file/doDel"><a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="fileDel"><i class="layui-icon layui-icon-delete"></i>删除</a></my:auth>
					</script>
					<%-- 附件数据表格 --%>
					<table id="fileTable" lay-filter="fileTable"></table>
				</div>
			</div>
		</div>
	</body>
	<%@include file="/script/myJs/tail.jspf"%>
	<script type="text/javascript">
		//定义变量
		var fileQueryForm = $("#fileQueryForm"); //附件查询对象
		var uploader; //附件上传对象
	
		//页面加载完毕，执行如下方法：
		$(function() {
			initFileTable();
		});
	
		//初始化附件表格
		function initFileTable() {
			layui.table.render({
				elem : "#fileTable",
				url : "file/list",
				cols : [[ 
						{field : "NAME", title : "名称", align : "center"},
						{field : "EXT_NAME", title : "扩展名", align : "center"},
						{field : "IP", title : "上传ip", align : "center"},
						{field : "USERNAME", title : "上传用户", align : "center"},
						{field : "UPDATE_TIME_STR", title : "上传时间", align : "center"},
						{fixed : "right", title : "操作 ", toolbar : "#fileToolbar", align : "center"}
						]],
				page : true,
				height : "full-180",
				method : "post",
				defaultToolbar : [],
				parseData: function(file){
					return {
						"code" : file.succ,
						"msg" : file.msg,
						"count" : file.data.total,
						"data" : file.data.rows
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
			layui.table.on("tool(fileTable)", function(obj){
				var data = obj.data;
				if(obj.event === "fileDownload") {
					doFileDownload(obj.data.ID);
				} else if(obj.event === "fileDel") {
					doFileDel(obj.data.ID);
				}
			});
		}
	
		//附件查询
		function fileQuery() {
			layui.table.reload("fileTable", {"where" : $.fn.my.serializeObj(fileQueryForm)});
		}
	
		//附件重置
		function fileReset() {
			fileQueryForm[0].reset();
			fileQuery();
		}
		
		//到达上传附件页面
		function toFileUpload() {
			$.ajax({
				url : "file/toUpload",
				dataType : "html",
				success : function(obj) {
					layer.open({
						title : "上传附件",
						area : ["800px", "500px"],
						content : obj,
						btn : ["上传", "取消"],
						type : 1,
						yes : function(index, layero){
							doFileUpload(index);
						},
						success: function(layero, index){
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
									max_file_size : "99mb"
								},
								init : {
									FilesAdded : function(up, files) {
										$("#file_console").html("");
										
										var fileListObj = $("#fileList");
										$.each(files, function(index, domEle){
											var html = [];
											html.push("<tr id='file_row_" + domEle.id + "'>");
											html.push("	<td>"+domEle.name+"<input type='hidden' id='ids_" + domEle.id + "' name='ids'></td>");
											html.push("	<td>"+plupload.formatSize(domEle.size)+"</td>");
											html.push("	<td id='upload_progress_"+domEle.id+"'>0%</td>");
											html.push("	<td><button class=\"layui-btn layui-btn-mini layui-btn-danger test-upload-demo-delete\" onclick='removeRowForFile(\"" + domEle.id + "\")'>移除</button></td>");
											html.push("</tr>");
											fileListObj.append(html.join(""));
										});
										uploader.start();
									},
									UploadProgress : function(up, file) {
										$("#upload_progress_" + file.id).html(file.percent + "%");
									},
									FileUploaded : function(up, file, responseObj) { //每个附件上传后，服务端返回的响应消息。
										var response = $.parseJSON(responseObj.response);
										if(!response.succ){
											$("#upload_progress_" + file.id).html(response.msg);
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
							
							layui.form.render(null, "fileEditFrom");
						}
					});
				}
			});
		}
		
		//完成上传附件
		function doFileUpload(fileUploadDialogIndex){
			var queued = uploader.total.queued; 
			if(queued != 0){
				layer.alert(obj.msg, {"title" : "正在上传中。。。"});
				return;
			}
			
			layui.form.on("submit(fileUploadBtn)", function(data) {
				var ids = [];
				$("#fileList [name='ids']").each(function (index, domEle) {
					ids.push(domEle.value);
				});
				data.field.ids = ids;
				
				layer.confirm("确定要上传？", function(index) {
					$.ajax({
						url : "file/doUpload",
						data : data.field,
						success : function(obj) {
							fileQuery();
							
							if (!obj.succ) {
								layer.alert(obj.msg, {"title" : "提示消息"});
								return;
							}
							
							layer.close(index);
							layer.close(fileUploadDialogIndex);
						}
					});
				});
			});
			$("[lay-filter='fileUploadBtn']").click();
		}

		//完成删除附件
		function doFileDel(id) {
			layer.confirm("确定要删除？", function(index) {
				$.ajax({
					url : "file/doDel",
					data : {id : id},
					success : function(obj) {
						fileQuery();
						
						if (!obj.succ) {
							layer.alert(obj.msg, {"title" : "提示消息"});
							return;
						}
						
						layer.close(index);
					}
				});
			});
		}
		
		//移除单行附件
		function removeRowForFile(fileId) {
			$("#file_row_" + fileId).remove();
			uploader.removeFile(uploader.getFile(fileId));
		}
		
		//完成附件下载
		function doFileDownload(id) {
			window.location.href = "file/doDownload?id=" + id;
		}
	</script>
</html>