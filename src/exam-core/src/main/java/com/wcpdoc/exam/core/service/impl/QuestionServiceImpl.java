package com.wcpdoc.exam.core.service.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.wcpdoc.base.util.CurLoginUserUtil;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.BigDecimalUtil;
import com.wcpdoc.core.util.StringUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.constant.ExamConstant;
import com.wcpdoc.exam.core.dao.QuestionDao;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.entity.QuestionOption;
import com.wcpdoc.exam.core.entity.QuestionType;
import com.wcpdoc.exam.core.service.ExamCacheService;
import com.wcpdoc.exam.core.service.QuestionAnswerService;
import com.wcpdoc.exam.core.service.QuestionExService;
import com.wcpdoc.exam.core.service.QuestionOptionService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.service.QuestionTypeService;
import com.wcpdoc.exam.core.util.QuestionUtil;
import com.wcpdoc.file.service.FileService;

/**
 * 试题服务层实现
 * 
 * v1.0 zhanghc 2017-05-07 14:56:29
 */
@Service
public class QuestionServiceImpl extends BaseServiceImp<Question> implements QuestionService {
	@Resource
	private QuestionDao questionDao;
	@Resource
	private QuestionExService questionExService;
	@Resource
	private QuestionTypeService questionTypeService;
	@Resource
	private FileService fileService;
	@Resource
	private ExamCacheService examCacheService;
	@Resource
	private QuestionAnswerService questionAnswerService;
	@Resource
	private QuestionOptionService questionOptionService;

	@Override
	public RBaseDao<Question> getDao() {
		return questionDao;
	}

	@Override
	public void addEx(Question question, List<String> options, List<String> answers, List<BigDecimal> scores) {
		// 数据校验
		QuestionType questionType = addValid0(question);
		addValid(question, options, answers, scores, questionType);

		// 试题添加
		question.setCreateUserId(questionType.getCreateUserId());// 如果是管理员添加子管理的题库，创建人还是子管理员（比如需要根据创建人查询自己的试题）
		question.setUpdateTime(new Date());
		question.setUpdateUserId(getCurUser().getId());
		question.setState(1);
		save(question);

		// 答案添加
		addAnswer(question, answers, scores);

		// 选项添加
		addOption(question, options);

		// 附件保存
		addFile(question, options);
	}

	@Override
	@Caching(evict = { //
			@CacheEvict(value = ExamConstant.QUESTION_CACHE, key = ExamConstant.QUESTION_KEY_PRE + "#question.id"), //
			@CacheEvict(value = ExamConstant.QUESTION_CACHE, key = ExamConstant.QUESTION_OPTION_KEY_PRE
					+ "#question.id"), //
			@CacheEvict(value = ExamConstant.QUESTION_CACHE, key = ExamConstant.QUESTION_ANSWER_KEY_PRE
					+ "#question.id"),//
	})
	public void updateEx(Question question, List<String> options, List<String> answers, List<BigDecimal> scores) {
		// 数据校验
		QuestionType questionType = updateValie0(question);
		addValid(question, options, answers, scores, questionType);
		questionExService.updateValid(question);

		// 试题修改
		Question entity = examCacheService.getQuestion(question.getId());
		entity.setTitle(question.getTitle());
		entity.setMarkType(question.getMarkType());
		entity.setScore(question.getScore());
		// entity.setType(question.getType());// 类型不能改
		// entity.setState(question.getState());// 状态不能改
		entity.setMarkOptions(question.getMarkOptions());
		entity.setAnalysis(question.getAnalysis());
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		updateById(entity);

		// 答案修改
		updateAnswer(question, answers, scores);

		// 选项修改
		updateOption(question, options);

		// 附件保存
		addFile(question, options);
	}

	@Override
	@Caching(evict = { //
			@CacheEvict(value = ExamConstant.QUESTION_CACHE, key = ExamConstant.QUESTION_KEY_PRE + "#id"), //
			@CacheEvict(value = ExamConstant.QUESTION_CACHE, key = ExamConstant.QUESTION_OPTION_KEY_PRE + "#id"), //
			@CacheEvict(value = ExamConstant.QUESTION_CACHE, key = ExamConstant.QUESTION_ANSWER_KEY_PRE + "#id"),//
	})
	public void delEx(Integer id) {
		// 数据校验
		delValid(id);

		// 试题删除
		Question question = examCacheService.getQuestion(id);
		question.setState(0);
		question.setUpdateTime(new Date());
		question.setUpdateUserId(getCurUser().getId());
		updateById(question);
	}

	@Override
	public void copy(Integer id) throws Exception {
		// 数据校验
		copyValid(id);

		// 试题复制
		Question question = examCacheService.getQuestion(id);
		Question questionNew = new Question();
		BeanUtils.copyProperties(questionNew, question);
		// questionOfCopy.setState(2);
		questionNew.setId(null);
		questionNew.setUpdateTime(new Date());
		questionNew.setUpdateUserId(getCurUser().getId());
		save(questionNew);

		// 答案复制
		List<QuestionAnswer> questionAnswerList = examCacheService.getQuestionAnswerList(question.getId());
		for (QuestionAnswer questionAnswer : questionAnswerList) {
			QuestionAnswer questionAnswerNew = new QuestionAnswer();
			BeanUtils.copyProperties(questionAnswerNew, questionAnswer);
			questionAnswerNew.setId(null);
			questionAnswerNew.setQuestionId(questionNew.getId());
			questionAnswerService.save(questionAnswerNew);
		}

		// 选项复制
		if (QuestionUtil.hasSingleChoice(question) || QuestionUtil.hasMultipleChoice(question)) {
			List<QuestionOption> questionOptionList = examCacheService.getQuestionOptionList(question.getId());
			for (QuestionOption questionOption : questionOptionList) {
				QuestionOption questionOptionNew = new QuestionOption();
				BeanUtils.copyProperties(questionOptionNew, questionOption);
				questionOptionNew.setId(null);
				questionOptionNew.setQuestionId(questionNew.getId());
				questionOptionService.save(questionOptionNew);
			}
		}
	}

	@Override
	public List<Question> getListByDel() {
		return questionDao.getListByDel();
	}

	@Override
	public List<Integer> getIds(Integer questionTypeId) {
		return questionDao.getIds(questionTypeId);
	}

	@Override
	public List<Question> getList(Integer questionTypeId) {
		return questionDao.getList(questionTypeId);
	}

	private QuestionType addValid0(Question question) {
		if (!ValidateUtil.isValid(question.getQuestionTypeId())) {
			throw new MyException("参数错误：questionTypeId");
		}
		return questionTypeService.getById(question.getQuestionTypeId());
	}

	private void addAnswer(Question question, List<String> answers, List<BigDecimal> scores) {
		if (QuestionUtil.hasSingleChoice(question) || QuestionUtil.hasTrueFalse(question)) {
			QuestionAnswer questionAnswer = new QuestionAnswer();
			questionAnswer.setAnswer(answers.get(0));
			questionAnswer.setScore(null);
			questionAnswer.setQuestionId(question.getId());
			questionAnswer.setNo(1);
			questionAnswerService.save(questionAnswer);
		} else if ((QuestionUtil.hasQA(question) && QuestionUtil.hasSubjective(question))) {// 答案有逗号，接收参数会分隔，这里合并一下
			QuestionAnswer questionAnswer = new QuestionAnswer();
			questionAnswer.setAnswer(StringUtil.join(answers, ","));
			questionAnswer.setScore(null);
			questionAnswer.setQuestionId(question.getId());
			questionAnswer.setNo(1);
			questionAnswerService.save(questionAnswer);
		} else if (QuestionUtil.hasMultipleChoice(question)) {
			QuestionAnswer questionAnswer = new QuestionAnswer();
			Collections.sort(answers);// 页面前选b在选a，传值为ba，排序后在保存
			questionAnswer.setAnswer(StringUtil.join(answers, ","));
			questionAnswer.setScore(scores.get(0));
			questionAnswer.setQuestionId(question.getId());
			questionAnswer.setNo(1);
			questionAnswerService.save(questionAnswer);
		} else if (QuestionUtil.hasFillBlank(question)
				|| (QuestionUtil.hasQA(question) && QuestionUtil.hasObjective(question))) {
			for (int i = 0; i < answers.size(); i++) {
				QuestionAnswer questionAnswer = new QuestionAnswer();
				questionAnswer.setAnswer(answers.get(i));
				questionAnswer.setScore(scores.get(i));
				questionAnswer.setQuestionId(question.getId());
				questionAnswer.setNo(i + 1);
				questionAnswerService.save(questionAnswer);
			}
		}
	}

	private void updateAnswer(Question question, List<String> answers, List<BigDecimal> scores) {
		List<QuestionAnswer> questionAnswerList = examCacheService.getQuestionAnswerList(question.getId());
		for (QuestionAnswer questionAnswer : questionAnswerList) {
			questionAnswerService.removeById(questionAnswer.getId());
		}

		addAnswer(question, answers, scores);
	}

	private void addOption(Question question, List<String> options) {
		if (QuestionUtil.hasSingleChoice(question) || QuestionUtil.hasMultipleChoice(question)) {
			for (int i = 0; i < options.size(); i++) {
				QuestionOption questionOption = new QuestionOption();
				questionOption.setQuestionId(question.getId());
				questionOption.setOptions(options.get(i));
				questionOption.setNo(i + 1);
				questionOptionService.save(questionOption);
			}
		}
	}

	private void updateOption(Question question, List<String> options) {
		if (!(QuestionUtil.hasSingleChoice(question) || QuestionUtil.hasMultipleChoice(question))) {
			return;
		}
		List<QuestionOption> questionOptionList = examCacheService.getQuestionOptionList(question.getId());
		for (QuestionOption questionOption : questionOptionList) {
			questionOptionService.removeById(questionOption.getId());
		}
		addOption(question, options);
	}

	private void addFile(Question question, List<String> options) {
		// 之后做带附件题使用
	}

	private void addValid(Question question, List<String> options, List<String> answers, List<BigDecimal> scores,
			QuestionType questionType) {
		if (!(CurLoginUserUtil.isSelf(questionType.getCreateUserId()) || CurLoginUserUtil.isAdmin())) {// 子管理可以改自己创建的题库，管理员可以改所有子管理的题库
			throw new MyException("无操作权限");
		}

		if (!ValidateUtil.isValid(question.getType())) {
			throw new MyException("参数错误：type");
		}
		if (!ValidateUtil.isValid(question.getTitle())) {
			throw new MyException("参数错误：title");
		}
		if (!ValidateUtil.isValid(answers)) {
			throw new MyException("参数错误：answers");
		}
		if (!ValidateUtil.isValid(question.getScore())) {
			throw new MyException("参数错误：score");
		}

		if (QuestionUtil.hasSingleChoice(question)) {// 如果是单选
			if (question.getMarkType() != 1) {
				throw new MyException("参数错误：markType");
			}
			if (ValidateUtil.isValid(question.getMarkOptions())) {
				throw new MyException("参数错误：markOptions");
			}
			if (!ValidateUtil.isValid(options)) {
				throw new MyException("参数错误：options");
			}
			if (options.size() < 2) {
				throw new MyException("参数错误：options长度小于2");
			}
			if (answers.size() != 1) {
				throw new MyException("参数错误：answers");
			}
			if (!"ABCDEFG".contains(answers.get(0))) {
				throw new MyException("参数错误：answer");
			}
			int answerIndex = answers.get(0).getBytes()[0] - 65;
			if (options.size() < answerIndex + 1) {// 总共四个选项，答案是E就是有问题的
				throw new MyException("选项和答案不匹配");
			}
			if (ValidateUtil.isValid(scores)) {
				throw new MyException("参数错误：scores");
			}
		} else if (QuestionUtil.hasMultipleChoice(question)) {// 如果是多选
			if (question.getMarkType() != 1) {
				throw new MyException("参数错误：markType");
			}
			if (ValidateUtil.isValid(question.getMarkOptions())) {
				throw new MyException("参数错误：markOptions");
			}
			if (!ValidateUtil.isValid(options)) {
				throw new MyException("参数错误：options");
			}
			if (options.size() < 2) {
				throw new MyException("参数错误：options长度小于2");
			}
			if (answers.size() < 2) { // 最少两个答案
				throw new MyException("参数错误：answers最少两个答案");
			}
			for (int i = 0; i < answers.size(); i++) {
				if (!"ABCDEFG".contains(answers.get(i))) {
					throw new MyException("参数错误：answers");
				}
				int answerIndex = answers.get(i).getBytes()[0] - 65;
				if (options.size() < answerIndex + 1) {// 总共四个选项，答案包含E就是有问题的
					throw new MyException("选项和答案不匹配");
				}
			}
			if (scores.size() != 1) {
				throw new MyException("参数错误：scores");
			}
			if (BigDecimalUtil.newInstance(question.getScore()).sub(scores.get(0)).getResult().doubleValue() <= 0) {
				throw new MyException("漏选分数大于试题分数");
			}
		} else if (QuestionUtil.hasFillBlank(question)) {
			if (question.getMarkType() != 1 && question.getMarkType() != 2) {
				throw new MyException("参数错误：markType");
			}
			if (ValidateUtil.isValid(options)) {
				throw new MyException("参数错误：options");
			}
			int fillBlankNum = QuestionUtil.getFillBlankNum(question.getTitle());
			if (fillBlankNum == 0) {
				throw new MyException("最少1个填空");
			}
			if (fillBlankNum > 7) {
				throw new MyException("最多7个填空");
			}
			if (fillBlankNum != answers.size()) {
				throw new MyException("填空和答案数量不匹配");
			}

			if (QuestionUtil.hasObjective(question)) {
				if (ValidateUtil.isValid(question.getMarkOptions())) {
					if (question.getMarkOptions().size() > 2) {
						throw new MyException("参数错误：scoreOption");
					}
					for (Integer markOption : question.getMarkOptions()) {
						if (markOption != 2 && markOption != 3) {// 分值选项（2：答案无顺序；3：不区分大小写；)
							throw new MyException("参数错误：scoreOption");
						}
					}
				}

				if (!ValidateUtil.isValid(scores)) {
					throw new MyException("参数错误：scores");
				}
				if (scores.size() != answers.size()) {
					throw new MyException("答案和分数不匹配");
				}

				BigDecimalUtil totalScore = BigDecimalUtil.newInstance(BigDecimal.ZERO);
				for (BigDecimal answerScore : scores) {
					totalScore.add(answerScore);
				}
				if (totalScore.sub(question.getScore()).getResult().doubleValue() != 0) {
					throw new MyException("单项分值合计和总分数不匹配");
				}
			}
			if (QuestionUtil.hasSubjective(question)) {
				if (ValidateUtil.isValid(question.getMarkOptions())) {
					throw new MyException("参数错误：scoreOption");
				}
				// if (ValidateUtil.isValid(scores)) {
				// throw new MyException("参数错误：scores");
				// } // 主观填空允许有子分数
			}
		} else if (QuestionUtil.hasTrueFalse(question)) {
			if (question.getMarkType() != 1) {
				throw new MyException("参数错误：markType");
			}
			if (ValidateUtil.isValid(question.getMarkOptions())) {
				throw new MyException("参数错误：markOptions");
			}
			if (ValidateUtil.isValid(options)) {
				throw new MyException("参数错误：options");
			}
			if (answers.size() != 1) {
				throw new MyException("参数错误：answers");
			}

			if (!"对是√否错×".contains(answers.get(0))) {
				throw new MyException("参数错误：answers");
			}

			if (ValidateUtil.isValid(scores)) {
				throw new MyException("参数错误：scores");
			}
		} else if (QuestionUtil.hasQA(question)) {
			if (question.getMarkType() != 1 && question.getMarkType() != 2) {
				throw new MyException("参数错误：markType");
			}
			if (ValidateUtil.isValid(options)) {
				throw new MyException("参数错误：options");
			}
			if (QuestionUtil.hasObjective(question)) {
				if (ValidateUtil.isValid(question.getMarkOptions())) {
					if (question.getMarkOptions().size() != 1) {
						throw new MyException("参数错误：scoreOption");
					}
					for (Integer scoreOption : question.getMarkOptions()) {
						if (scoreOption != 3) {// 分值选项（1：漏选得分；2：答案无顺序；3：不区分大小写；)
							throw new MyException("参数错误：scoreOption");
						}
					}
				}

				if (!ValidateUtil.isValid(scores)) {
					throw new MyException("参数错误：scores");
				}
				if (scores.size() != answers.size()) {
					throw new MyException("答案和分数不匹配");
				}

				BigDecimalUtil totalScore = BigDecimalUtil.newInstance(BigDecimal.ZERO);
				for (BigDecimal answerScore : scores) {
					totalScore.add(answerScore);
				}
				if (totalScore.sub(question.getScore()).getResult().doubleValue() != 0) {
					throw new MyException("单项分值合计和总分数不匹配");
				}
			}
			if (QuestionUtil.hasSubjective(question)) {
				if (ValidateUtil.isValid(question.getMarkOptions())) {
					throw new MyException("参数错误：scoreOption");
				}
				if (ValidateUtil.isValid(scores)) {
					throw new MyException("参数错误：scores");
				}
			}
		}
	}

	private QuestionType updateValie0(Question question) {
		Question entity = examCacheService.getQuestion(question.getId());
		if (entity.getType() != question.getType()) {
			throw new MyException("类型不能修改");
		}
		if (entity.getState() == 0) {
			throw new MyException("已删除");
		}

		return questionTypeService.getById(entity.getQuestionTypeId());
	}

	private void delValid(Integer id) {
		if (!ValidateUtil.isValid(id)) {
			throw new MyException("参数错误：id");
		}
		Question question = examCacheService.getQuestion(id);
		if (!(CurLoginUserUtil.isSelf(question.getCreateUserId()) || CurLoginUserUtil.isAdmin())) {
			throw new MyException("无操作权限");
		}
	}

	private void copyValid(Integer id) {
		Question question = examCacheService.getQuestion(id);
		if (!(CurLoginUserUtil.isSelf(question.getCreateUserId()) || CurLoginUserUtil.isAdmin())) {
			throw new MyException("无操作权限");
		}
	}
}