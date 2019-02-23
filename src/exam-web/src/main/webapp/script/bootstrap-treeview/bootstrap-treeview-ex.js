function generateBootstrapTree(arr, options) {
	var defaluts = {
		idFiled : "id",
		textFiled : "text", 
		parentField : "parent",
		checkedFiled : "checked",
		disabledFiled : "disabled",
		expandedFiled : "expanded",
	};
	var opts = $.extend({}, defaluts, options);
	var treeMap = {};
	var treeList = [];
	for (var i = 0; i < arr.length; i++) {
		arr[i]["id"] = arr[i][opts.idFiled];
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
	return treeList;
}