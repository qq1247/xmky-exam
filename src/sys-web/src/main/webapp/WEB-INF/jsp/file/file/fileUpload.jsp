<%@ page language="java" pageEncoding="utf-8"%>
<div lay-filter="fileUploadFrom" class="layui-form" style="padding: 20px 0 0 0;">
	<div class="layui-col-md12">
		<div class="layui-card">
			<div class="layui-card-body">
				<div class="layui-upload">
					<button type="button" class="layui-btn layui-btn-normal" id="file_browse">选择多文件</button>
					<div id="file_console"></div>
					<div class="layui-upload-list">
						<table class="layui-table">
							<thead>
								<tr>
									<th>文件名</th>
									<th>大小</th>
									<th>状态</th>
									<th>操作</th>
								</tr>
							</thead>
							<tbody id="fileList"></tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="layui-form-item layui-hide">
		<input type="button" lay-submit lay-filter="fileUploadBtn" value="确认">
	</div>
</div>