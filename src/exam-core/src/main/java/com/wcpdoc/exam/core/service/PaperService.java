package com.wcpdoc.exam.core.service;

import java.math.BigDecimal;
import java.util.List;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperQuestion;
import com.wcpdoc.exam.core.entity.PaperRemark;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.ex.MyPaper;

/**
 * 试卷服务层接口
 * 
 * zhanghc 2018年10月21日上午8:16:06
 */
public interface PaperService extends BaseService<Paper> {

	/**
	 * 添加试卷
	 * 
	 * v1.0 chenyun 2021年8月19日下午5:03:26
	 * @param paper
	 * @param paperRemark 
	 * void
	 */
	void addAndUpdate(Paper paper, PaperRemark paperRemark);
	
	/**
	 * 修改试卷
	 * 
	 * v1.0 chenyun 2021年8月25日下午5:35:05
	 * @param paper 
	 * void
	 */
	void updateAndUpdate(Paper paper);
	
	/**
	 * 删除试卷
	 * 
	 * v1.0 chenyun 2021年8月25日下午5:35:00
	 * @param id 
	 * void
	 */
	void delAndUpdate(Integer id);
	
	/**
	 * 拷贝
	 * 
	 * v1.0 chenyun 2021年8月19日下午5:09:55
	 * @param id 
	 * void
	 */
	void copy(Integer id);
	
	/**
	 * 归档
	 * 
	 * v1.0 zhanghc 2021年10月20日下午1:36:08
	 * @param id 
	 * void
	 */
	void archive(Integer id);
	
	/**
	 * 完成添加章节
	 * 
	 * v1.0 zhanghc 2017年5月27日上午9:42:57
	 * @param chapter
	 * @param user
	 * void
	 */
	void chapterAdd(PaperQuestion chapter);
	
	/**
	 * 完成修改章节
	 * 
	 * v1.0 zhanghc 2017年5月27日上午11:05:42
	 * @param chapter
	 * @param user
	 * void
	 */
	void chapterEdit(PaperQuestion chapter);
	
	/**
	 * 完成删除章节
	 * 
	 * v1.0 zhanghc 2017年5月27日下午5:58:58
	 * @param chapterId
	 * void
	 */
	void chapterDel(Integer chapterId);
	
	/**
	 * 移动章节
	 * 
	 * v1.0 chenyun 2021年7月9日下午3:22:49
	 * @param sourceId
	 * @param targetId 
	 * void
	 */
	void chapterMove(Integer sourceId, Integer targetId);
	
	/**
	 * 移动试题
	 * 
	 * v1.0 chenyun 2021年7月9日下午3:22:49
	 * @param id
	 * @param sourceQuestionId 源试题ID
	 * @param targetQuestionId 目标试题ID
	 * void
	 */
	void questionMove(Integer id, Integer sourceQuestionId, Integer targetQuestionId);
	
	/**
	 * 完成添加试题
	 * 
	 * v1.0 zhanghc 2017年5月27日下午3:32:43
	 * @param chapterId
	 * @param questionIds
	 * @param no
	 * void
	 */
	void questionAdd(Integer chapterId, Integer[] questionIds, Integer no);
	
	/**
	 * 设置分数
	 * 
	 * v1.0 zhanghc 2018年10月21日下午3:10:37
	 * @param id
	 * @param questionId
	 * @param score
	 * @param subScores 试题为智能阅卷，并且是填空或问答是有效
	 * void
	 */
	void scoreUpdate(Integer id, Integer questionId, BigDecimal score, BigDecimal[] subScores, Integer[] aiOptions);

	/**
	 * 完成试题删除
	 * 
	 * v1.0 zhanghc 2018年10月21日下午10:43:15
	 * @param id
	 * @param questionId
	 * @param paperQuestionId void
	 */
	void questionDel(Integer id, Integer questionId);
	
	/**
	 * 完成清空试题
	 * 
	 * v1.0 zhanghc 2018年10月21日上午8:18:04
	 * @param chapterId
	 * void
	 */
	void questionClear(Integer chapterId);

	/**
	 * 获取试卷列表
	 * 
	 * v1.0 zhanghc 2017年8月6日下午9:43:55
	 * @param paperTypeId
	 * @return List<Paper>
	 */
	List<Paper> getList(Integer paperTypeId);

	/**
	 * 完成批量更新分数
	 * 
	 * v1.0 zhanghc 2017年6月9日下午4:21:52
	 * @param chapterId
	 * @param score
	 * @param subScores
	 * @param aiOptions void
	 */
	void batchScoreUpdate(Integer chapterId, BigDecimal score, BigDecimal subScores, Integer[] aiOptions);
	
	/**
	 * 合并
	 * 
	 * v1.0 chenyun 2021年3月2日下午1:25:51
	 * @param sourceId
	 * @param targetId void
	 */
	void move(Integer id, Integer paperTypeId);
	
	/**
	 * 发布
	 * 
	 * v1.0 chenyun 2021年8月23日上午11:06:26
	 * @param id void
	 */
	void publish(Integer id);

	/**
	 * 更新总分数
	 * 
	 * v1.0 zhanghc 2021年10月20日上午10:59:33
	 * @param id 
	 * void
	 */
	void totalScoreUpdate(Integer id);

	/**
	 * 反作弊
	 * 
	 * v1.0 zhanghc 2022年5月18日上午10:45:21
	 * @param id
	 * @param options 1：试题乱序；2：选项乱序；3：禁用右键；4：禁止复制；5：最小化警告
	 * @return PageResult
	 */
	void sxe(Integer id, Integer[] options);
	
	/**
	 * 授权
	 * 
	 * v1.0 zhanghc 2020年9月8日上午10:06:53
	 * @param id
	 * @param readUserIds
	 * void
	 */
	void auth(Integer id, Integer[] readUserIds);
	
	/**
	 * 获取我的试卷
	 * 
	 * v1.0 zhanghc 2022年5月20日下午3:04:01
	 * @return MyPaper
	 */
	MyPaper getMyPaper(Integer id);
	
	/**
	 * 获取我的试卷
	 * 
	 * v1.0 zhanghc 2022年5月20日下午3:06:04
	 * @param examId
	 * @param userId
	 * @return MyPaper
	 */
	MyPaper getMyPaperOfRand(Integer examId, Integer userId);

	/**
	 * 获取试题列表
	 * 
	 * v1.0 zhanghc 2022年5月24日下午4:25:06
	 * @param id
	 * @return List<Question>
	 */
	List<Question> getQuestionList(Integer id);

	/**
	 * 获取试题列表
	 * 
	 * v1.0 zhanghc 2022年5月24日下午4:25:35
	 * @param examId
	 * @param userId
	 * @return List<Question>
	 */
	List<Question> getQuestionList(Integer examId, Integer userId);
	
	/**
	 * 是否有读权限
	 * 
	 * v1.0 zhanghc 2021年11月5日下午5:50:58
	 * @param paper
	 * @return boolean
	 */
	boolean hasReadAuth(Paper paper);

	/**
	 * 是否有写权限（只能操作自己创建的分类）
	 * 
	 * v1.0 zhanghc 2022年6月17日上午11:19:58
	 * @param paper
	 * @return boolean
	 */
	boolean hasWriteAuth(Paper paper);
}
