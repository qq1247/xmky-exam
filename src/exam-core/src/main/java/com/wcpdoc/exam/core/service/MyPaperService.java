package com.wcpdoc.exam.core.service;

import java.util.List;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.ex.PaperPart;

/**
 * 我的试卷服务层接口
 * 
 * v1.0 zhanghc 2024年3月5日下午2:46:04
 */
public interface MyPaperService extends BaseService<Object> {
	/**
	 * 试卷生成
	 * 
	 * v1.0 zhanghc 2024年3月23日上午9:42:55
	 * 
	 * @param examId
	 * @param userId
	 * @param scoreShow
	 * @param answerShow
	 * @return List<PaperPart>
	 */
	List<PaperPart> generatePaper(Integer examId, Integer userId, Boolean scoreShow, Boolean answerShow);

	/**
	 * 阅卷完成<br/>
	 * 如果是客观题考试，直接出成绩<br/>
	 * 如果是主观题考试，阅完客观题后，等待人工阅卷<br/>
	 * 
	 * v1.0 zhanghc 2024年3月8日上午10:38:57
	 * 
	 * @param examId
	 * @param userId MyExam
	 */
	MyExam doMark(Integer examId, Integer userId);

	/**
	 * 考试完成
	 * 
	 * v1.0 zhanghc 2020年10月13日下午1:13:52
	 * 
	 * @param examId void
	 */
	void doExam(Integer examId);
}
