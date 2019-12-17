<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>试卷分类授权列表</title>
		<%@include file="/script/home/common.jspf"%>
	</head>
	<body>
		<%@include file="/script/home/head.jspf"%>
		<div class="container">
			<div class="row">
				<div class="col-md-12">
					<ul class="nav nav-tabs">
						<li class="active">
							<a href="#authUser" data-toggle="tab">用户权限 </a>
						</li>
						<li>
							<a href="#authOrg" data-toggle="tab">机构权限</a>
						</li>
						<li>
							<a href="#authPost" data-toggle="tab">岗位权限</a>
						</li>
					</ul>
					<div class="tab-content">
						<div id="authUser" class="tab-pane fade in active">
							<div class="col-md-3">
								<div id="authUserOrgTree" class="exam-tree"></div>
							</div>
							<div class="col-md-9">
								<div class="panel panel-default exam-query">
									<div class="panel-body">
										<form id="authUserQueryForm" class="form-horizontal" role="form">
											<input type="hidden" id="authUserOne" name="one"/>
											<input type="hidden" id="authUserTen" name="ten" value="${id }">
											<div class="row">
												<div class="col-md-4">
													<div class="form-group">
														<div class="form-group">
														<label for="authUserTwo" class="control-label col-md-4">名称：</label>
														<div class="col-md-8">
															<input type="text" id="authUserTwo" name="two" class="form-control" placeholder="请输入名称">
														</div>
													</div>
													</div>
												</div>
												<div class="col-md-4">
												</div>
												<div class="col-md-4" style="text-align: right;">
													<button type="button" class="btn btn-primary" onclick="authUserQuery();">
														<span class="glyphicon glyphicon-search"></span>
														&nbsp;查询
													</button>
													<button type="button" class="btn btn-primary" onclick="authUserReset();">
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
										<div id="authUserToolbar">
											<button type="button" class="btn btn-primary" onclick="toAuthUserAddList();">
												<span class="glyphicon glyphicon-plus"></span>
												&nbsp;添加
											</button>
											<button type="button" class="btn btn-primary" onclick="doAuthUserDel();">
												<span class="glyphicon glyphicon-plus"></span>
												&nbsp;删除
											</button>
											<button type="button" class="btn btn-primary" onclick="javascript:history.back(-1);">
												<span class="glyphicon glyphicon-arrow-left"></span>
												&nbsp;返回
											</button>
										</div>
										<table id="authUserTable"></table>
									</div>
								</div>
							</div>
						</div>
						<div id="authOrg" class="tab-pane fade">
							<div class="col-md-3">
								<div id="authOrgTree" class="exam-tree"></div>
								<button type="button" class="btn btn-primary" onclick="doAuthOrgUpdate();">
									<span class="glyphicon glyphicon-ok"></span>
									&nbsp;保存
								</button>
								<button type="button" class="btn btn-primary" onclick="javascript:history.back(-1);">
									<span class="glyphicon glyphicon-arrow-left"></span>
									&nbsp;返回
								</button>
							</div>
						</div>
						<div id="authPost" class="tab-pane fade">
							<div class="col-md-3">
								<div id="authPostTree" class="exam-tree"></div>
								<button type="button" class="btn btn-primary" onclick="doAuthPostUpdate();">
									<span class="glyphicon glyphicon-ok"></span>
									&nbsp;保存
								</button>
								<button type="button" class="btn btn-primary" onclick="javascript:history.back(-1);">
									<span class="glyphicon glyphicon-arrow-left"></span>
									&nbsp;返回
								</button>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
	<script type="text/javascript">
		//定义变量
		var $authUserOrgTree = $("#authUserOrgTree");
		var $authUserTable = $("#authUserTable");
		var $authUserOne = $("#authUserOne");
		var $authUserQueryForm = $("#authUserQueryForm");
		
		var $authOrgTree = $("#authOrgTree");
		var $authPostTree = $("#authPostTree");
		
		//页面加载完毕，执行如下方法：
		$(function() {
			initAuthUserTree();
			initAuthUserTable();
			initAuthOrgTree();
			initAuthPostTree();
		});
		
		//初始化权限用户组织机构树
		function initAuthUserTree(){
			$.ajax({
				url : "home/paperType/authUserOrgTreeList",
				success : function(arr) {
					$authUserOrgTree.treeview({
						showBorder: false,
						expandIcon: "glyphicon glyphicon-chevron-right",
						collapseIcon: "glyphicon glyphicon-chevron-down",
						nodeIcon: "glyphicon glyphicon-bookmark",
						//color: "#428BCA",
						showTags: true, 
						levels: 3,
						data: generateBootstrapTree(arr, {
							idFiled : "ID",
							textFiled : "NAME", 
							parentField : "PARENT_ID",
							disabledFiled : "DISABLED",
							expandedFiled : "EXPANDED"
						}),
						onNodeSelected : function(event, data) {
							$authUserOne.val(data.ID);
							authUserQuery();
						},
						onNodeUnselected : function(event, data) {
							$authUserOne.val("");
							authUserQuery();
						}
					});
				}
			});
		}
		
		//初始化权限组织机构树
		function initAuthOrgTree(){
			$.ajax({
				url : "home/paperType/authOrgOrgTreeList?id=${id }",
				success : function(arr) {
					$authOrgTree.treeview({
						showBorder: false,
						expandIcon: "glyphicon glyphicon-chevron-right",
						collapseIcon: "glyphicon glyphicon-chevron-down",
						nodeIcon: "glyphicon glyphicon-bookmark",
						//color: "#428BCA",
						showTags: true, 
						levels: 3,
						showCheckbox: 1,
						multiSelect: 1,
						data: generateBootstrapTree(arr, {
							idFiled : "ID",
							textFiled : "NAME", 
							parentField : "PARENT_ID",
							checkedFiled : "CHECKED",
							disabledFiled : "DISABLED",
							expandedFiled : "EXPANDED"
						}),
						onNodeChecked : function(event, data) {
							var selectNodes = getChildNodeIdArr(data); //获取所有子节点
							if (selectNodes) { //子节点不为空，则选中所有子节点
								$authOrgTree.treeview('checkNode', [selectNodes, {silent : true } ]);
							}
							setParentNodeCheck(data, $authOrgTree);
						},
						onNodeUnchecked : function(event, data) {
							var selectNodes = getChildNodeIdArr(data); //获取所有子节点
							if (selectNodes) { //子节点不为空，则取消选中所有子节点
								$authOrgTree.treeview('uncheckNode', [ selectNodes, {silent : true } ]);
							}

							setParentNodeCheck(data, $authOrgTree);
						}
					});
				}
			});
		}
		
		//初始化权限岗位树
		function initAuthPostTree(){
			$.ajax({
				url : "home/paperType/authPostOrgTreeList?id=${id }",
				success : function(arr) {
					var defaluts = {
						idFiled : "id",
						textFiled : "text", 
						parentField : "parent",
						checkedFiled : "checked",
						disabledFiled : "disabled",
						expandedFiled : "expanded",
					};
					var opts = $.extend({}, defaluts, {
						idFiled : "ID",
						textFiled : "NAME", 
						parentField : "PARENT_ID",
						checkedFiled : "CHECKED",
						disabledFiled : "DISABLED",
						expandedFiled : "EXPANDED"
					});
					var treeMap = {};
					var treeList = [];
					for (var i = 0; i < arr.length; i++) {
						arr[i]["id"] = arr[i][opts.idFiled];
						if(arr[i].TYPE == "POST"){
							arr[i]["id"] = "p" + arr[i][opts.idFiled];
						}
						arr[i]["text"] = arr[i][opts.textFiled];
						arr[i]["state"] = {};
						arr[i]["state"]["checked"] = arr[i][opts.checkedFiled];
						arr[i]["state"]["disabled"] = arr[i][opts.disabledFiled];
						arr[i]["state"]["expanded"] = arr[i][opts.expandedFiled];
						
						treeMap[arr[i]["id"]] = arr[i];
					}
					
					for (var i = 0; i < arr.length; i++) {
						if (!(treeMap[arr[i][opts.parentField]] && arr[i]["id"] != arr[i][opts.parentField])) {
							treeList.push(arr[i]);
							continue;
						}
						
						if (!treeMap[arr[i][opts.parentField]]["nodes"]) {
							treeMap[arr[i][opts.parentField]]["nodes"] = [];
						}
						treeMap[arr[i][opts.parentField]]["nodes"].push(arr[i]);
						treeMap[arr[i][opts.parentField]]["tags"] = [treeMap[arr[i][opts.parentField]]["nodes"].length]
					}
						
					$authPostTree.treeview({
						showBorder: false,
						expandIcon: "glyphicon glyphicon-chevron-right",
						collapseIcon: "glyphicon glyphicon-chevron-down",
						nodeIcon: "glyphicon glyphicon-bookmark",
						//color: "#428BCA",
						showTags: true, 
						levels: 3,
						showCheckbox: 1,
						multiSelect: 1,
						data: treeList,
						onNodeChecked : function(event, data) {
							var selectNodes = getChildNodeIdArr(data); //获取所有子节点
							if (selectNodes) { //子节点不为空，则选中所有子节点
								$authPostTree.treeview('checkNode', [selectNodes, {silent : true } ]);
							}
							setParentNodeCheck(data, $authPostTree);
						},
						onNodeUnchecked : function(event, data) {
							var selectNodes = getChildNodeIdArr(data); //获取所有子节点
							if (selectNodes) { //子节点不为空，则取消选中所有子节点
								$authPostTree.treeview('uncheckNode', [ selectNodes, {silent : true } ]);
							}

							setParentNodeCheck(data, $authPostTree);
						}
					});
				}
			});
		}
		
		function getChildNodeIdArr(node) {
			var ts = [];
			if (node.nodes) {
				for (x in node.nodes) {
					ts.push(node.nodes[x].nodeId);
					if (node.nodes[x].nodes) {
						var getNodeDieDai = getChildNodeIdArr(node.nodes[x]);
						for (j in getNodeDieDai) {
							ts.push(getNodeDieDai[j]);
						}
					}
				}
			} else {
				ts.push(node.nodeId);
			}
			return ts;
		}

		function setParentNodeCheck(node, $tree) {
			var parentNode = $tree.treeview("getNode", node.parentId);
			if (!parentNode.nodes) {
				return;
			}
			
			var checkedCount = 0;
			for (x in parentNode.nodes) {
				if (parentNode.nodes[x].state.checked) {
					checkedCount++;
				}
			}
			
			if (checkedCount === parentNode.nodes.length) {
				$tree.treeview("checkNode", parentNode.nodeId);
			}else if(checkedCount == 0){
				$tree.treeview("uncheckNode", parentNode.nodeId);
			}
			
			setParentNodeCheck(parentNode, $tree);
		}
		
		//初始化权限用户列表
		function initAuthUserTable(){
			$authUserTable.bootstrapTable({
				url : "home/paperType/authUserList",
				queryParams : function(params){
					var customeParams = $.fn.my.serializeObj($authUserQueryForm);
					customeParams.page = this.pageNumber;
					customeParams.rows = this.pageSize;
					return customeParams;
				},
				columns : [ 
							{field : "state", checkbox : true},
							{field : "USER_NAME", title : "姓名", width : 80, align : "center"}, 
							{field : "LOGIN_NAME", title : "登录名称", width : 80, align : "center"}, 
							{field : "ORG_NAME", title : "组织机构", width : 80, align : "center"},
							{field : "POST_NAMES", title : "岗位", width : 80, align : "center"},
							],
				toolbar : "#authUserToolbar"
			});
		}
		
		//权限用户查询
		function authUserQuery(){
			$authUserTable.bootstrapTable('refresh', {pageNumber : 1});
		}
		
		//权限用户重置
		function authUserReset(){
			$authUserQueryForm[0].reset();
			authUserQuery();
		}
		
		//到达添加权限用户页面
		function toAuthUserAddList(){
			BootstrapDialog.show({
				title : "用户列表",
				cssClass : "exam-list",
				message : function(dialog) {
					var $message = $("<div></div>");
					var pageToLoad = dialog.getData("pageToLoad");
					$message.load(pageToLoad);
					return $message;
				},
				data : {
					"pageToLoad" : "home/paperType/toAuthUserAddList?id=${id }"
				},
				buttons : [{
					label : "&nbsp;确定",
					icon : "glyphicon glyphicon-ok",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						doAuthUserAdd();
						dialogItself.close();
					}
				}],
				onshown : function(){
					var $authUserAddTable = $("#authUserAddTable");
					var $authUserAddOrgTree = $("#authUserAddOrgTree");
					var $authUserAddOne = $("#authUserAddOne");
					var $authUserAddQueryForm = $("#authUserAddQueryForm");
					
					$.ajax({
						url : "home/paperType/authUserOrgTreeList",
						success : function(arr) {
							$authUserAddOrgTree.treeview({
								showBorder: false,
								expandIcon: "glyphicon glyphicon-chevron-right",
								collapseIcon: "glyphicon glyphicon-chevron-down",
								nodeIcon: "glyphicon glyphicon-bookmark",
								//color: "#428BCA",
								showTags: true, 
								levels: 3,
								data: generateBootstrapTree(arr, {
									idFiled : "ID",
									textFiled : "NAME", 
									parentField : "PARENT_ID",
									disabledFiled : "DISABLED",
									expandedFiled : "EXPANDED"
								}),
								onNodeSelected : function(event, data) {
									$authUserAddOne.val(data.ID);
									authUserAddQuery();
								},
								onNodeUnselected : function(event, data) {
									$authUserAddOne.val("");
									authUserAddQuery();
								}
							});
						}
					});
					
					$authUserAddTable.bootstrapTable({
						url : "home/paperType/authUserAddList",
						queryParams : function(params){
							var customeParams = $.fn.my.serializeObj($authUserAddQueryForm);
							customeParams.page = this.pageNumber;
							customeParams.rows = this.pageSize;
							return customeParams;
						},
						columns : [ 
									{field : "state", checkbox : true}, 
									{field : "USER_NAME", title : "姓名", width : 80, align : "center"}, 
									{field : "LOGIN_NAME", title : "登录名称", width : 80, align : "center"}, 
									{field : "ORG_NAME", title : "组织机构", width : 80, align : "center"},
									{field : "POST_NAMES", title : "岗位", width : 80, align : "center"},
									]
					});
				}
			});
		}
		
		//添加权限用户查询
		function authUserAddQuery(){
			var $authUserAddTable = $("#authUserAddTable");
			$authUserAddTable.bootstrapTable('refresh', {pageNumber : 1});
		}
		
		//添加权限用户重置
		function authUserAddReset(){
			var $authUserAddQueryForm = $("#authUserAddQueryForm");
			$authUserAddQueryForm[0].reset();
			authUserAddQuery();
		}
		
		//完成添加权限用户
		function doAuthUserAdd(){
			var $authUserAddTable = $("#authUserAddTable");
			var nodes = $authUserAddTable.bootstrapTable("getSelections");
			if(nodes.length == 0){
				BootstrapDialog.show({
					title : "提示消息",
					message : "请选择一行或多行数据！",
					buttons : [{
						label : "&nbsp;确定",
						icon : "glyphicon glyphicon-ok",
						cssClass : "btn-primary",
						action : function(dialogItself) {
							dialogItself.close();
						}
					}]
				});
				return;
			}
			
			BootstrapDialog.show({
				title : "提示消息",
				message : "确定要添加？",
				buttons : [{
					label : "&nbsp;确定",
					icon : "glyphicon glyphicon-ok",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						var params = $.fn.my.serializeField(nodes, {attrName : "userIds"});
						params += "&id=${id }";
						
						BootstrapDialog.show({
							title : "提示消息",
							message : "同步到子【试卷分类】？",
							buttons : [{
								label : "&nbsp;确定",
								icon : "glyphicon glyphicon-ok",
								cssClass : "btn-primary",
								action : function(dialogItself) {
									$.ajax({
										url : "home/paperType/doAuthUserAdd",
										data : params + "&syn2Sub=true",
										success : function(obj) {
											if (!obj.succ) {
												BootstrapDialog.show({
													title : "提示消息",
													message : obj.msg,
													buttons : [{
														label : "&nbsp;确定",
														icon : "glyphicon glyphicon-ok",
														cssClass : "btn-primary",
														action : function(dialogItself) {
															dialogItself.close();
														}
													}]
												});
												return;
											}
											
											window.location.href = "home/paperType/toAuth?id=${id }";
										}
									});
								}
							}, {
								label : "&nbsp;取消",
								icon : "glyphicon glyphicon-remove",
								cssClass : "btn-primary",
								action : function(dialogItself) {
									$.ajax({
										url : "home/paperType/doAuthUserAdd",
										data : params + "&syn2Sub=false",
										success : function(obj) {
											if (!obj.succ) {
												BootstrapDialog.show({
													title : "提示消息",
													message : obj.msg,
													buttons : [{
														label : "&nbsp;确定",
														icon : "glyphicon glyphicon-ok",
														cssClass : "btn-primary",
														action : function(dialogItself) {
															dialogItself.close();
														}
													}]
												});
												return;
											}
											
											window.location.href = "home/paperType/toAuth?id=${id }";
										}
									});
								}
							}]
						});
					}
				}, {
					label : "&nbsp;取消",
					icon : "glyphicon glyphicon-remove",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						dialogItself.close();
					}
				}]
			});
		}
		
		//完成删除权限用户
		function doAuthUserDel(){
			var $authUserTable = $("#authUserTable");
			var nodes = $authUserTable.bootstrapTable("getSelections");
			if(nodes.length == 0){
				BootstrapDialog.show({
					title : "提示消息",
					message : "请选择一行或多行数据！",
					buttons : [{
						label : "&nbsp;确定",
						icon : "glyphicon glyphicon-ok",
						cssClass : "btn-primary",
						action : function(dialogItself) {
							dialogItself.close();
						}
					}]
				});
				return;
			}
			
			BootstrapDialog.show({
				title : "提示消息",
				message : "确定要删除？",
				buttons : [{
					label : "&nbsp;确定",
					icon : "glyphicon glyphicon-ok",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						var params = $.fn.my.serializeField(nodes, {attrName : "userIds"});
						params += "&id=${id }";
						
						BootstrapDialog.show({
							title : "提示消息",
							message : "同步到子【试卷分类】？",
							buttons : [{
								label : "&nbsp;确定",
								icon : "glyphicon glyphicon-ok",
								cssClass : "btn-primary",
								action : function(dialogItself) {
									$.ajax({
										url : "home/paperType/doAuthUserDel",
										data : params + "&syn2Sub=true",
										success : function(obj) {
											if (!obj.succ) {
												BootstrapDialog.show({
													title : "提示消息",
													message : obj.msg,
													buttons : [{
														label : "&nbsp;确定",
														icon : "glyphicon glyphicon-ok",
														cssClass : "btn-primary",
														action : function(dialogItself) {
															dialogItself.close();
														}
													}]
												});
												return;
											}
											
											window.location.href = "home/paperType/toAuth?id=${id }";
										}
									});
								}
							}, {
								label : "&nbsp;取消",
								icon : "glyphicon glyphicon-remove",
								cssClass : "btn-primary",
								action : function(dialogItself) {
									$.ajax({
										url : "home/paperType/doAuthUserDel",
										data : params + "&syn2Sub=false",
										success : function(obj) {
											if (!obj.succ) {
												BootstrapDialog.show({
													title : "提示消息",
													message : obj.msg,
													buttons : [{
														label : "&nbsp;确定",
														icon : "glyphicon glyphicon-ok",
														cssClass : "btn-primary",
														action : function(dialogItself) {
															dialogItself.close();
														}
													}]
												});
												return;
											}
											
											window.location.href = "home/paperType/toAuth?id=${id }";
										}
									});
								}
							}]
						});
					}
				}, {
					label : "&nbsp;取消",
					icon : "glyphicon glyphicon-remove",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						dialogItself.close();
					}
				}]
			});
		}
		
		//完成机构权限保存
		function doAuthOrgUpdate(){
			BootstrapDialog.show({
				title : "提示消息",
				message : "确定要保存？",
				buttons : [{
					label : "&nbsp;确定",
					icon : "glyphicon glyphicon-ok",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						var orgNodes = $authOrgTree.treeview("getChecked");
						var params = "&id=${id }";
						if(orgNodes.length == 0){
							params += "&orgIds=";
						}else{
							for(index in orgNodes){
								params += "&orgIds=" + orgNodes[index].ID;
							}
						}
						
						BootstrapDialog.show({
							title : "提示消息",
							message : "同步到子【试卷分类】？",
							buttons : [{
								label : "&nbsp;确定",
								icon : "glyphicon glyphicon-ok",
								cssClass : "btn-primary",
								action : function(dialogItself) {
									$.ajax({
										url : "home/paperType/doAuthOrgUpdate",
										data : params + "&syn2Sub=true",
										success : function(obj) {
											if (!obj.succ) {
												BootstrapDialog.show({
													title : "提示消息",
													message : obj.msg,
													buttons : [{
														label : "&nbsp;确定",
														icon : "glyphicon glyphicon-ok",
														cssClass : "btn-primary",
														action : function(dialogItself) {
															dialogItself.close();
														}
													}]
												});
												return;
											}
											
											window.location.href = "home/paperType/toAuth?id=${id }";
										}
									});
								}
							}, {
								label : "&nbsp;取消",
								icon : "glyphicon glyphicon-remove",
								cssClass : "btn-primary",
								action : function(dialogItself) {
									$.ajax({
										url : "home/paperType/doAuthOrgUpdate",
										data : params + "&syn2Sub=false",
										success : function(obj) {
											if (!obj.succ) {
												BootstrapDialog.show({
													title : "提示消息",
													message : obj.msg,
													buttons : [{
														label : "&nbsp;确定",
														icon : "glyphicon glyphicon-ok",
														cssClass : "btn-primary",
														action : function(dialogItself) {
															dialogItself.close();
														}
													}]
												});
												return;
											}
											
											window.location.href = "home/paperType/toAuth?id=${id }";
										}
									});
								}
							}]
						});
					}
				}, {
					label : "&nbsp;取消",
					icon : "glyphicon glyphicon-remove",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						dialogItself.close();
					}
				}]
			});
		}
		
		//完成岗位权限保存
		function doAuthPostUpdate(){
			BootstrapDialog.show({
				title : "提示消息",
				message : "确定要保存？",
				buttons : [{
					label : "&nbsp;确定",
					icon : "glyphicon glyphicon-ok",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						var postNodes = $authPostTree.treeview("getChecked");
						var params = "&id=${id }";
						if(postNodes.length == 0){
							params += "&postIds=";
						}else{
							for(index in postNodes){
								if(postNodes[index].TYPE != "POST"){
									continue;
								}
								
								params += "&postIds=" + postNodes[index].ID;
							}
						}
						
						BootstrapDialog.show({
							title : "提示消息",
							message : "同步到子【试卷分类】？",
							buttons : [{
								label : "&nbsp;确定",
								icon : "glyphicon glyphicon-ok",
								cssClass : "btn-primary",
								action : function(dialogItself) {
									$.ajax({
										url : "home/paperType/doAuthPostUpdate",
										data : params + "&syn2Sub=true",
										success : function(obj) {
											if (!obj.succ) {
												BootstrapDialog.show({
													title : "提示消息",
													message : obj.msg,
													buttons : [{
														label : "&nbsp;确定",
														icon : "glyphicon glyphicon-ok",
														cssClass : "btn-primary",
														action : function(dialogItself) {
															dialogItself.close();
														}
													}]
												});
												return;
											}
											
											window.location.href = "home/paperType/toAuth?id=${id }";
										}
									});
								}
							}, {
								label : "&nbsp;取消",
								icon : "glyphicon glyphicon-remove",
								cssClass : "btn-primary",
								action : function(dialogItself) {
									$.ajax({
										url : "home/paperType/doAuthPostUpdate",
										data : params + "&syn2Sub=false",
										success : function(obj) {
											if (!obj.succ) {
												BootstrapDialog.show({
													title : "提示消息",
													message : obj.msg,
													buttons : [{
														label : "&nbsp;确定",
														icon : "glyphicon glyphicon-ok",
														cssClass : "btn-primary",
														action : function(dialogItself) {
															dialogItself.close();
														}
													}]
												});
												return;
											}
											
											window.location.href = "home/paperType/toAuth?id=${id }";
										}
									});
								}
							}]
						});
					}
				}, {
					label : "&nbsp;取消",
					icon : "glyphicon glyphicon-remove",
					cssClass : "btn-primary",
					action : function(dialogItself) {
						dialogItself.close();
					}
				}]
			});
		}
	</script>
</html>