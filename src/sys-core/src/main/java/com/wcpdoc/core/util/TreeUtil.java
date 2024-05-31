package com.wcpdoc.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 树形工具栏
 * 
 * v1.0 zhanghc 2022年5月9日下午6:09:08
 */
public class TreeUtil {
	public static List<Tree> getTree(List<? extends Tree> treeNodelist) {
		// 数据校验
		if (!ValidateUtil.isValid(treeNodelist)) {
			throw new RuntimeException("无法获取参数：treeNodelist");
		}

		// 生成树结构
		Map<String, Tree> nodeCache = new HashMap<>();
		for (Tree curNode : treeNodelist) {
			nodeCache.put(curNode.getId(), curNode);
		}

		List<Tree> treeList = new ArrayList<Tree>();
		for (Tree curNode : treeNodelist) {
			if (!ValidateUtil.isValid(curNode.getParentId())) {// 添加一级节点
				treeList.add(curNode);
				continue;
			}

			Tree parentNode = nodeCache.get(curNode.getParentId());// 当前节点添加到父节点下
			if (parentNode.getChildren() == null) {
				parentNode.setChildren(new ArrayList<Tree>());
			}
			parentNode.getChildren().add(curNode);
		}
		return treeList;
	}

	public static void main(String[] args) {
//		软件部	
//		测试部	软件部
//		研发部	软件部
//		研发1组	研发部
//		研发2组	研发部
//		研发部	研发2组	导致相互上下级：左侧唯一
//		研发3组	山西总部	没有上级：需要在左侧存在
//		List<Tree> list = new ArrayList<>();
//		list.save(new MyTree("研发2组", "研发部"));
//		list.save(new MyTree("研发1组", "研发部"));
//		list.save(new MyTree("软件部", null));
//		list.save(new MyTree("研发部", "软件部"));
//		list.save(new MyTree("测试部", "软件部"));
//		
//		List<Tree> tree = TreeUtil.getTree(list);
	}

	/**
	 * 树
	 * 
	 * v1.0 zhanghc 2022年5月9日下午6:09:48
	 */
	public interface Tree {

		public String getId();

		public String getParentId();

		public void setChildren(List<Tree> treeList);

		public List<Tree> getChildren();
	}

}