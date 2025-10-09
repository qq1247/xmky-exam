package com.wcpdoc.exam.core.dao;

import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.exam.core.entity.MyExamQuestion;

/**
 * 我的试题数据访问层接口
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
public interface MyQuestionDao extends RBaseDao<MyExamQuestion> {

	/**
	 * 获取我的试题列表
	 * 
	 * v1.0 zhanghc 2017年7月3日上午9:44:45
	 * 
	 * @param myExamId
	 * @return List<MyQuestion>
	 */
	default List<MyExamQuestion> getList(Integer examId, Integer userId) {
		return selectList(new LambdaQueryWrapper<MyExamQuestion>().eq(MyExamQuestion::getExamId, examId)
				.eq(MyExamQuestion::getUserId, userId).orderByAsc(MyExamQuestion::getNo));
	}

	/**
	 * 获取我的试题
	 * 
	 * v1.0 zhanghc 2021年10月19日上午9:55:31
	 * 
	 * @param examId
	 * @param userId
	 * @param questionId
	 * @return MyQuestion
	 */
	default MyExamQuestion getMyQuestion(Integer examId, Integer userId, Integer questionId) {
		return selectOne(new LambdaQueryWrapper<MyExamQuestion>().eq(MyExamQuestion::getExamId, examId)
				.eq(MyExamQuestion::getUserId, userId).eq(MyExamQuestion::getQuestionId, questionId));
	}

	/**
	 * 我的试题清理
	 * 
	 * v1.0 zhanghc 2023年3月22日下午5:40:35
	 * 
	 * @param examId void
	 */
	default void clear(Integer examId) {
		delete(new LambdaQueryWrapper<MyExamQuestion>().eq(MyExamQuestion::getExamId, examId));
	}

	/**
	 * 考试ID列表
	 * 
	 * v1.0 zhanghc 2024年5月12日下午9:07:09
	 * 
	 * @param questionId
	 * @return List<Integer>
	 */
	default List<Integer> getExamIdList(Integer questionId) {
		return selectList(new QueryWrapper<MyExamQuestion>()//
				.select("DISTINCT EXAM_ID")//
				.lambda()//
				.eq(MyExamQuestion::getQuestionId, questionId))//
				.stream()//
				.map(MyExamQuestion::getExamId)//
				.collect(Collectors.toList());
	}
}
