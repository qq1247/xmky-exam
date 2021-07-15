function ztreeDataFilter(treeId, parentNode, responseData) {
	if (!responseData.succ) {
		alert("树节点加载失败：" + responseData.msg);
		return;
	}
	
	var list = responseData.data;
	var treeList = [];
	var treeMap = {};
	var idFiled = "ID";
	var textFiled = "NAME";
	var checkedFiled = "CHECKED";
	var parentField = "PARENT_ID";
	var iconClsFiled = "ICON";
	var openClsFiled = "OPEN";

	for (var i = 0; i < list.length; i++) {
		list[i]["id"] = list[i][idFiled];
		list[i]["name"] = list[i][textFiled];
		if(list[i][checkedFiled]){
			list[i]["checked"] = true;
		}
		if(list[i][openClsFiled]){
			list[i]["open"] = true;
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
			treeList.push(list[i]);
		}
	}
	return treeList;
}