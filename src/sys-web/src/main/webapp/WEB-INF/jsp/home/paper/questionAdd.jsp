<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="col-md-12">
	<div class="row">
		<div class="col-md-3">
			<div id="questionTypeTree" class="exam-tree"></div>
		</div>
		<div class="col-md-9">
			<div class="panel panel-default exam-query">
				<div class="panel-body">
					<form id="questionQueryForm" class="form-horizontal" role="form">
						<input type="hidden" id="questionOne" name="one"/>
						<input type="hidden" name="ten" value="${id }"/>
						<div class="row">
							<div class="col-md-4">
								<div class="form-group">
									<label for="two" class="control-label col-md-4">编号：</label>
									<div class="col-md-8">
										<input type="text" id="two" name="two" class="form-control" placeholder="请输入编号">
									</div>
								</div>
							</div>
							<div class="col-md-4">
								<div class="form-group">
									<label for="three" class="control-label col-md-4">题干：</label>
									<div class="col-md-8">
										<input type="text" id="three" name="three" class="form-control" placeholder="请输入题干">
									</div>
								</div>
							</div>
							<div class="col-md-4">
								<div class="form-group">
									<label for="four" class="control-label col-md-4">状态：</label>
									<div class="col-md-8">
										<select id="four" name="four" class="form-control">
											<option value=""></option>
											<c:forEach var="dict" items="${STATE_DICT }">
											<option value="${dict.dictKey }">${dict.dictValue }</option>
											</c:forEach>
										</select>
									</div>
								</div>
							</div>
						</div>
						<div class="row">
							<div class="col-md-4">
								<div class="form-group">
									<label for="five" class="control-label col-md-4">类型：</label>
									<div class="col-md-8">
										<select id="five" name="five" class="form-control">
											<option value=""></option>
											<c:forEach var="dict" items="${QUESTION_TYPE_DICT }">
											<option value="${dict.dictKey }">${dict.dictValue }</option>
											</c:forEach>
										</select>
									</div>
								</div>
							</div>
							<div class="col-md-4">
								<div class="form-group">
									<label for="six" class="control-label col-md-4">难度：</label>
									<div class="col-md-8">
										<select id="six" name="six" class="form-control">
											<option value=""></option>
											<c:forEach var="dict" items="${QUESTION_DIFFICULTY_DICT }">
												<option value="${dict.dictKey }">${dict.dictValue }</option>
											</c:forEach>
										</select>
									</div>
								</div>
							</div>
							<div class="col-md-4" style="text-align: right;">
								<button type="button" class="btn btn-primary" onclick="questionQuery();">
									<span class="glyphicon glyphicon-search"></span>
									&nbsp;查询
								</button>
								<button type="button" class="btn btn-primary" onclick="questionReset();">
									<span class="glyphicon glyphicon-repeat"></span>
									&nbsp;重置
								</button>
							</div>
						</div>
					</form>
				</div>
			</div>
			<div class="panel panel-default exam-list">
				<div class="panel-body">
					<table id="questionTable"></table>
				</div>
			</div>
		</div>
	</div>
</div>