package com.wcpdoc.exam.core.util;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.wcpdoc.core.util.BigDecimalUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyQuestion;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionAnswer;

/**
 * 我的考试工具类
 * 
 * v1.0 zhanghc 2022年5月20日上午9:20:36
 */
public class MyExamUtil {
	/**
	 * 分数显示
	 * 
	 * v1.0 zhanghc 2023年3月2日上午11:07:27
	 * 
	 * @param exam
	 * @param myExam
	 * @return boolean
	 */
	public static boolean scoreShow(Exam exam, MyExam myExam) {
		return (exam.getScoreState() == 1 && exam.getMarkState() == 3) // 如果是考试结束后显示成绩，需要等到考试结束
				|| (exam.getScoreState() == 3 && myExam.getMarkState() == 3);// 如果是交卷后显示成绩，需要等到该试卷阅卷完成。比如主观题没阅卷，得不到总分，得不到是否及格
	}

	/**
	 * 标准答案显示
	 * 
	 * v1.0 zhanghc 2023年2月20日上午11:32:34
	 * 
	 * @param exam
	 * @param myExam
	 * @return boolean
	 */
	public static boolean answerShow(Exam exam, MyExam myExam) {
		if (exam.getScoreState() == 1) {// 如果是考试结束后公布答案
			if (exam.getMarkType() == 1 && exam.getEndTime().getTime() < System.currentTimeMillis()) {
				return true;// 如果是客观题考试，考试结束后显示标准答案； 10:00-12:00 < 12:05
			}
			if (exam.getMarkType() == 2 && exam.getMarkEndTime().getTime() < System.currentTimeMillis()) {
				return true;// 如果是主观题考试，阅卷结束后显示标准答案
			}
		}

		if (exam.getScoreState() == 3) {// 如果是交卷后公布答案
			if (myExam.getState() == 3) {// 如果用户已交卷，显示标准答案
				return true;
			}
		}

		return false;
	}

	/**
	 * 问答处理
	 * 
	 * v1.0 chenyun 2021年7月21日下午2:09:40
	 * 
	 * @param question           试题
	 * @param questionAnswerList 试题答案
	 * @param myQuestion         我的试题 void
	 */
	public static void qAHandle(Question question, List<QuestionAnswer> questionAnswerList, MyQuestion myQuestion) {
		// 数据校验
		if (QuestionUtil.hasSubjective(question)) {
			return;
		}

		// 如果用户没有做答，0分
		if (!ValidateUtil.isValid(myQuestion.getUserAnswer())) {
			myQuestion.setUserScore(BigDecimal.ZERO);
			return;
		}

		/**
		 * 阅题
		 * 
		 * 试题答案：[山西\n山西省\n晋][老婆\n媳妇\n内人] 用户答案：我是山西的媳妇
		 */
		myQuestion.setUserScore(BigDecimal.ZERO);// 先初始化用户分数为0，防止多次累加
		boolean caseSensitive = !myQuestion.getMarkOptions().contains(3);// 区分大小写
		String myAnswer = caseSensitive ? myQuestion.getUserAnswer() : myQuestion.getUserAnswer().toLowerCase();// 获取用户答案
		for (int i = 0; i < questionAnswerList.size(); i++) {// 获取试题每个关键词
			QuestionAnswer questionAnswer = questionAnswerList.get(i);
			String[] synonymAnswers = caseSensitive ? questionAnswer.getAnswer().split("\n")
					: questionAnswer.getAnswer().toLowerCase().split("\n");
			for (int j = 0; j < synonymAnswers.length; j++) {// 获取关键词的每个同义词
				if (myAnswer.contains(synonymAnswers[j])) {// 用户答案和同义词对比，如果找到（对比条件不要反，用户答案是大段文字）
					myQuestion.setUserScore(// 累计该同义词对应关键词的分数（从我的试题中取分数，因为组卷可能会修改分数）
							BigDecimalUtil.newInstance(myQuestion.getUserScore()).add(myQuestion.getScores().get(i))
									.getResult());
					break;// 匹配到一个同义词就结束，继续对比下一个关键词
				}
			}
		}
	}

	/**
	 * 单选处理
	 * 
	 * v1.0 zhanghc 2020年10月13日下午8:06:10
	 * 
	 * @param question           试题
	 * @param questionAnswerList 试题答案
	 * @param myQuestion         我的试题 void
	 */
	public static void singleChoiceHandle(Question question, List<QuestionAnswer> questionAnswerList,
			MyQuestion myQuestion) {
		// 数据校验
		if (QuestionUtil.hasSubjective(question)) {
			return;
		}

		// 如果用户没有做答，0分
		if (!ValidateUtil.isValid(myQuestion.getUserAnswer())) {
			myQuestion.setUserScore(BigDecimal.ZERO);
			return;
		}

		// 阅题
		myQuestion.setUserScore(BigDecimal.ZERO);// 先初始化用户分数为0，防止多次累加
		if (questionAnswerList.get(0).getAnswer().equals(myQuestion.getUserAnswer())) {
			myQuestion.setUserScore(myQuestion.getScore());
		}
	}

	/**
	 * 多选处理
	 * 
	 * v1.0 zhanghc 2020年10月13日下午8:11:47
	 * 
	 * @param question           试题
	 * @param questionAnswerList 试题答案
	 * @param myQuestion         我的试题 void
	 */
	public static void multipleChoiceHandle(Question question, List<QuestionAnswer> questionAnswerList,
			MyQuestion myQuestion) {
		// 数据校验
		if (QuestionUtil.hasSubjective(question)) {
			return;
		}

		// 如果用户没有做答，0分
		if (!ValidateUtil.isValid(myQuestion.getUserAnswer())) {
			myQuestion.setUserScore(BigDecimal.ZERO);
			return;
		}

		// 阅题
		myQuestion.setUserScore(BigDecimal.ZERO);// 先初始化用户分数为0，防止多次累加
		Set<String> userAnswers = new HashSet<String>(Arrays.asList(myQuestion.getUserAnswer().split(",")));// 获取用户答案
		Set<String> questionAnswers = new HashSet<>(Arrays.asList(questionAnswerList.get(0).getAnswer().split(",")));// 获取试题答案
		if (questionAnswers.size() == userAnswers.size() && questionAnswers.containsAll(userAnswers)) {// 如果完全正确，得满分
			myQuestion.setUserScore(myQuestion.getScore());
		} else if (questionAnswers.containsAll(userAnswers)) {// 如果半对，得漏选分
			myQuestion.setUserScore(myQuestion.getScores().get(0));
		}
	}

	/**
	 * 填空处理
	 * 
	 * v1.0 zhanghc 2020年10月13日下午8:11:55
	 * 
	 * @param question           试题
	 * @param questionAnswerList 试题答案
	 * @param myQuestion         我的试题 void
	 */
	public static void fillBlankHandle(Question question, List<QuestionAnswer> questionAnswerList,
			MyQuestion myQuestion) {
		// 数据校验
		if (QuestionUtil.hasSubjective(question)) {
			return;
		}
		// 如果用户没有做答，0分
		if (!ValidateUtil.isValid(myQuestion.getUserAnswer())) {
			myQuestion.setUserScore(BigDecimal.ZERO);
			return;
		}

		/**
		 * 阅题
		 * 
		 * 涉密人员上岗前要经过_______和_______。 关键词一：保密审查 保密调查 关键词二：培训 岗前培训 用户答案：培训 审查
		 * 匹配结果：【培训】得分；【审查】不得分
		 */
		myQuestion.setUserScore(BigDecimal.ZERO);// 先初始化用户分数为0，防止多次累加
		boolean caseSensitive = !myQuestion.getMarkOptions().contains(3);// 区分大小写
		boolean answerOrder = !myQuestion.getMarkOptions().contains(2);// 答案有顺序
		String[] userAnswers = caseSensitive ? myQuestion.getUserAnswer().split("\n")// 获取用户答案（多空就是多个答案）
				: myQuestion.getUserAnswer().toLowerCase().split("\n");
		Set<Integer> useAnswers = new HashSet<>();// bug：客观多空填空题、答案无顺序-》一个正确答案分别填到三个空上-》当前题满分。所以标记一下，使用过的试题关键词就不在二次对比答案
		for (int i = 0; i < userAnswers.length; i++) {// 循环用户每一项答案（[培训 审查]）
			for (int j = 0; j < questionAnswerList.size(); j++) {// 循环试题答案关键词（[[保密审查,保密调查], [培训 审查]]）
				QuestionAnswer questionAnswer = questionAnswerList.get(j);
				String[] synonyms = caseSensitive ? questionAnswer.getAnswer().split("\n") // 获取试题答案关键词的所有同义词
						: questionAnswer.getAnswer().toLowerCase().split("\n");

				if (answerOrder) {// 如果答案有顺序
					if (i != j) {// 则用户第1个答案和试题第1个答案对比，第二个和第二个对比，以此类推
						continue;
					}
				}
				if (useAnswers.contains(j)) {// 该关键词使用过，不在对比
					continue;
				}

				for (String synonym : synonyms) {// 循环每一项同义词（保密审查 保密调查）
					if (userAnswers[i].contains(synonym)) {// 如果用户某一空答案，匹配某一项关键词的同义词
						myQuestion.setUserScore(BigDecimalUtil.newInstance(myQuestion.getUserScore())
								.add(myQuestion.getScores().get(j)).getResult());// 累计该关键词的分数
						useAnswers.add(j);
						break;// 匹配到一个同义词就结束；继续对比下一个用户答案。（这里的循环不能退出上层循环，下面还有一个break去处理）
					}
				}
				if (useAnswers.contains(j)) {
					break;// 处理下一个用户答案（作用于上一段for循环，用户答案匹配某个标准答案，就不在循环其他标准答案）
				}
			}
		}
	}
}
