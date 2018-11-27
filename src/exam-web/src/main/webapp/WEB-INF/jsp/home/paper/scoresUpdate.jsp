<%@ page language="java" pageEncoding="utf-8"%>
<form id="editForm" class="form-horizontal" role="form">
	<input type="hidden" name=chapterId value="${chapterId }">
	<div class="col-md-12">
		<div class="row">
			<div class="col-md-12">
				<div class="form-group">
					<label for="score" class="col-md-2 control-label">分数：</label>
					<div class="col-md-10">
						<input type="text" id="score" name="score" value=""
							class="form-control" placeholder="请输入分数">
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<div class="form-group">
					<label class="checkbox-inline">
						<input type="checkbox" name="options" value="1"
							data-toggle="tooltip" title="默认全对得分">半对半分
					</label>
					<label class="checkbox-inline">
						<input type="checkbox" name="options" value="2"
							data-toggle="tooltip" title="默认答案有前后顺序">答案无顺序
					</label>
					<label class="checkbox-inline">
						<input type="checkbox" name="options" value="3"
							data-toggle="tooltip" title="默认大小写敏感">大小写不敏感
					</label>
					<label class="checkbox-inline">
						<input type="checkbox" name="options" value="4"
							data-toggle="tooltip" title="默认等于答案得分">用户答案包含试题答案
					</label>
				</div>
			</div>
		</div>
	</div>
</form>