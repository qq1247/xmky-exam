<%@ page language="java" pageEncoding="utf-8"%>
<form id="editForm" class="form-horizontal" role="form">
	<input type="hidden" name=id value="${chapter.id }">
	<input type="hidden" name="paperId" value="${chapter.paperId }">
	<div class="col-md-12">
		<div class="row">
			<div class="col-md-12">
				<div class="form-group">
					<label for="name" class="col-md-2 control-label">名称：</label>
					<div class="col-md-10">
						<input type="text" id="chapterName" name="name"
							value="${chapter.name }" class="form-control"
							placeholder="请输入名称">
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<div class="form-group">
					<label for="description" class="col-md-2 control-label">描述：</label>
					<div class="col-md-10">
						<textarea id="description" name="description">${chapter.description }</textarea>
					</div>
				</div>
			</div>
		</div>
	</div>
</form>