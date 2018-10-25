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
					<div class="checkbox-inline">
						<label>
							<input type="checkbox" name="options" value="1">半对半分
						</label>
					</div>
					<div class="checkbox-inline">
						<label>
							<input type="checkbox" name="options" value="2">答案无顺序
						</label>
					</div>
					<div class="checkbox-inline">
						<label>
							<input type="checkbox" name="options" value="3">忽略大小写
						</label>
					</div>
				</div>
			</div>
		</div>
	</div>
</form>