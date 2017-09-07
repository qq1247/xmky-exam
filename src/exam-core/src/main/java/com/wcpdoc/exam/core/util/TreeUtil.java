package com.wcpdoc.exam.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 树工具类
 * 
 * @author Administrator
 *
 */
public class TreeUtil {
	public static List<Tree> getTree(Collection<Tree> list) {
		if (ValidateUtil.isValid(list)) {
			throw new RuntimeException("无法获取参数：treeList");
		}

		Map<Integer, Tree> map = new HashMap<Integer, Tree>();
		for (Tree tree : list) {
			map.put(tree.getId(), tree);
		}

		List<Tree> treeList = new ArrayList<Tree>();
		for (Tree tree : list) {
			if (tree.getParentId() == 0) {
				treeList.add(tree);
			} else {
				Tree parentTree = map.get(tree.getParentId());
				List<Tree> children = parentTree.getChildren();
				if (children == null) {
					children = new ArrayList<Tree>();
					parentTree.setChildren(new ArrayList<Tree>());
				}

				children.add(tree);
			}
		}
		return treeList;
	}
	
	public interface Tree {

		public Integer getId();

		public Integer getParentId();

		public void setChildren(Collection<Tree> treeList);

		public List<Tree> getChildren();
	}
}
