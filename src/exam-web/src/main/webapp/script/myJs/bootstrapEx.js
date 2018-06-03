function generateTree(arr, options) {
	var defaluts = {
		idFiled : "id",
		textFiled : "text", 
		disabledFiled : "disabled",
		parentField : "parent"
	};
	var opts = $.extend({}, defaluts, options);
	var treeMap = {};
	var treeList = [];
	for (var i = 0; i < arr.length; i++) {
		arr[i]["id"] = arr[i][opts.idFiled];
		arr[i]["text"] = arr[i][opts.textFiled];
		if(!arr[i][opts.disabledFiled]){
			arr[i]["state"] = {disabled : true};
		}
		
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