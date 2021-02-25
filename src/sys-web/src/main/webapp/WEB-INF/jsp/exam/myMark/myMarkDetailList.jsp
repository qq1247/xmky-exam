<%@ page language="java" pageEncoding="utf-8"%>
<div class="layui-card">
	<%-- 我的阅卷详细查询条件 --%>
	<form id="myMarkDetailQueryForm" class="layui-form layui-card-header layuiadmin-card-header-auto">
		<input type="hidden" id="myMarkDetailOne" name="one" value="${examId }">
		<div class="layui-form-item ">
			<div class="layui-inline">
				<input type="text" name="three" placeholder="请输入名称" class="layui-input">
			</div>
			<div class="layui-inline">
				<button type="button" class="layui-btn layuiadmin-btn-useradmin" onclick="myMarkDetailQuery();">
					<i class="layui-icon layuiadmin-button-btn"></i>查询
				</button>
				<button type="button" class="layui-btn layui-btn-primary layuiadmin-btn-useradmin" onclick="myMarkDetailReset();">
					<i class="layui-icon layuiadmin-button-btn"></i>重置
				</button>
			</div>
		</div>
	</form>
	<div class="layui-card-body">
		<script type="text/html" id="myMarkDetailToolbar">
			{{#  if (d.EXAM_HAND == "AWAIT") { }}
			{{#  } else if (d.EXAM_HAND == "START") { }}
			<my:auth url="myMarkDetail/toPaper"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="myMarkToMark"><i class="layui-icon layui-icon-edit"></i>阅卷</a></my:auth>
			{{#  } else if (d.EXAM_HAND == "AWAIT") { }}
			<my:auth url="myMarkDetail/toPaper"><a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="myMarkDetailReview"><i class="layui-icon layui-icon-edit"></i>预览</a></my:auth>
			{{#  } }}
		</script>
		<%-- 我的阅卷详细数据表格 --%>
		<table id="myMarkDetailTable" lay-filter="myMarkDetailTable"></table>
	</div>
</div>
