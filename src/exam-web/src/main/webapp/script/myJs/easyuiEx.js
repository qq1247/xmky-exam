/**
 * 重写tree方法
 */

$.fn.tree.defaults.lines = true;
$.fn.tree.defaults.onSelect = function(node){
	if(!node.checked){
		$(this).tree("check", node.target);
	}else{
		$(this).tree("uncheck", node.target);
	}
};
$.fn.tree.defaults.loadFilter = function(data, parent) {
	var opt = $(this).data().tree.options;
	var idFiled = opt.idFiled || "id";
	var textFiled = opt.textFiled || "text";
	var checkedFiled = opt.checkedFiled || "checked";
	var iconClsFiled = opt.iconClsFiled || "iconCls";
	var parentField = opt.parentField;
	var list = data;
	var TreeList = [];
	var treeMap = {};

	for (var i = 0; i < list.length; i++) {
		list[i]["id"] = list[i][idFiled];
		list[i]["text"] = list[i][textFiled];
		if(list[i][checkedFiled]){
			list[i]["checked"] = true;
		}
		list[i]["iconCls"] = list[i][iconClsFiled];
		treeMap[list[i]["id"]] = list[i];
	}

	for (var i = 0; i < list.length; i++) {
		if (treeMap[list[i][parentField]] && list[i]["id"] != list[i][parentField]) {
			if (!treeMap[list[i][parentField]]["children"]) {
				treeMap[list[i][parentField]]["children"] = [];
			}
			treeMap[list[i][parentField]]["children"].push(list[i]);
		} else {
			TreeList.push(list[i]);
		}
	}
	return TreeList;
};

/**
 * 重写validatebox方法
 */
$.fn.validatebox.defaults.validateOnBlur = true;

/**
 * 重写dialog方法
 */
$.fn.dialog.defaults.cache = false;
$.fn.dialog.defaults.modal = true;
$.fn.dialog.defaults.width = 800;
$.fn.dialog.defaults.height = 500;
$.fn.dialog.defaults.onClose = function() {
	$(this).dialog("destroy");
};
$.fn.dialog.defaults.onMove = function(left, top) { // window拖动时触发，限制弹出框拖动范围
	var parentObj = $(this).panel("panel").parent();
	var width = $(this).panel("options").width;
	var height = $(this).panel("options").height;
	var parentWidth = $("body").width();
	var parentHeight = $("body").height();
	var scrollLeft = document.body.scrollLeft;
	var scrollTop = document.body.scrollTop;
	// 当弹出框尺寸大于浏览器大小时，弹出框自动缩小为浏览器当前尺寸
	if (width > parentWidth)
		$(this).window("resize", {
			width : parentWidth - 1
		});
	if (height > parentHeight)
		$(this).window("resize", {
			height : parentHeight - 1
		});
	// 当弹出框被拖动到浏览器外时，将弹出框定位至浏览器边缘
	if (left < scrollLeft) {
		$(this).window("move", {
			left : scrollLeft
		});
	}
	if (top < scrollTop) {
		$(this).window("move", {
			top : scrollTop
		});
	}
	if (left > scrollLeft && left > parentWidth - width + scrollLeft) {
		$(this).window("move", {
			left : parentWidth - width + scrollLeft
		});
	}
	if (top > scrollTop && top > parentHeight - height + scrollTop) {
		$(this).window("move", {
			top : parentHeight - height + scrollTop
		});
	}
}

/**
 * 重写datagrid
 */
$.fn.datagrid.defaults.idField = "ID";
$.fn.datagrid.defaults.title = "";
$.fn.datagrid.defaults.fit = true;
$.fn.datagrid.defaults.fitColumns = true;
$.fn.datagrid.defaults.autoRowHeight = false;
$.fn.datagrid.defaults.rownumbers = true;
$.fn.datagrid.defaults.pagination = true;
$.fn.datagrid.defaults.striped = true;
$.fn.datagrid.defaults.ctrlSelect = true;
$.fn.datagrid.defaults.pageSize = 10;
$.fn.datagrid.defaults.pageList = [10,15,50];

/**
 * 添加校验
 */
$.extend($.fn.validatebox.defaults.rules,
{
	zsx : {// 字母数字下划线
		validator : function(value) {
			return /^[a-zA-Z][a-zA-Z0-9_]*$/i.test(value);
		},
		message : "必须是字母开头，允许字母数字下划线"
	},
	zs : {// 字母数字
		validator : function(value) {
			return /^[a-zA-Z0-9]*$/i.test(value);
		},
		message : "必须是字母或数字"
	},
	zzs : {// 正整数
		validator : function(value) {
			return /^[1-9]\d*$/i.test(value);
		},
		message : "必须是正整数"
	},
	equalTo : { 
		validator: function (value, param) {
			return $(param[0]).val() == value; 
		},
		message: '两次新密码不一致' 
	} 
})