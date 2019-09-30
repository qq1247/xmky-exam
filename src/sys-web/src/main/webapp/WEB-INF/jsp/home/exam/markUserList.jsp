<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="col-md-12">
	<div class="row">
		<div class="col-md-3">
			<div id="markUserOrgTree" class="exam-tree"></div>
		</div>
		<div class="col-md-9">
			<div class="panel panel-default exam-query">
				<div class="panel-body">
					<form id="markUserQueryForm" class="form-horizontal" role="form">
						<input type="hidden" id="markUserOne" name="one"/>
						<input type="hidden" id="markUserTen" name="ten" value="${id }"/>
						<div class="row">
							<div class="col-md-4">
								<div class="form-group">
									<label for="two" class="control-label col-md-4">名称：</label>
									<div class="col-md-8">
										<input type="text" id="two" name="two" class="form-control" placeholder="请输入名称">
									</div>
								</div>
							</div>
							<div class="col-md-4">
								
							</div>
							<div class="col-md-4" style="text-align: right;">
								<button type="button" class="btn btn-primary" onclick="markUserQuery();">
									<span class="glyphicon glyphicon-search"></span>
									&nbsp;查询
								</button>
								<button type="button" class="btn btn-primary" onclick="markUserReset();">
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
					<div id="toolbar">
								<button type="button" class="btn btn-primary" onclick="toMarkUserAdd();">
									<span class="glyphicon glyphicon-plus"></span>
									&nbsp;添加
								</button>
								<button type="button" class="btn btn-primary" onclick="doMarkUserDel();">
									<span class="glyphicon glyphicon-trash"></span>
									&nbsp;删除
								</button>
							</div>
					<table id="markUserTable"></table>
				</div>
			</div>
		</div>
	</div>
</div>