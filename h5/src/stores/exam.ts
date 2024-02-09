import { ref, computed, reactive } from 'vue'
import { defineStore, acceptHMRUpdate } from 'pinia'
import dayjs from 'dayjs'

/**
 * 考试储存
 */
export const useExamStore = defineStore('exam', () => {
    // 定义变量
    const id = ref(null)// 考试ID
    const name = ref(`考试-${dayjs().add(0, 'day').format('YYYY-MM-DD')}`)// 考试名称
    const paperName = ref(`试卷-${dayjs().add(0, 'day').format('YYYY-MM-DD')}`)// 试卷名称
    const examTimes = ref([
        dayjs().add(0, 'day').hour(8).minute(0).second(0).format('YYYY-MM-DD HH:mm:ss'),
        dayjs().add(0, 'day').hour(10).minute(0).second(0).format('YYYY-MM-DD HH:mm:ss'),
    ])// 考试时间
    
    const markTimes = ref([
        dayjs().add(0, 'day').hour(14).minute(0).second(0).format('YYYY-MM-DD HH:mm:ss'),
        dayjs().add(0, 'day').hour(18).minute(0).second(0).format('YYYY-MM-DD HH:mm:ss'),
    ])// 阅卷时间
    const genType = ref(1) // 组卷方式（1：人工组卷；2：随机组卷）
    const passScore = ref(0) // 及格分数
    const sxes = ref([] as number[]) // 防作弊
    const showType = ref(1) // 展示方式
    const anonState = ref(2) // 匿名阅卷（1：是；2：否）
    const scoreState = ref(2) // 成绩查询（1：考试结束后；2：不公布；3：交卷后）
    const rankState = ref(2) // 排名公布（1：是；2：否）
    const state = ref(1) // 状态（1：发布；2：暂停；）
    const examQuestions = ref([] as ExamQuestion[])// 试卷信息
    const examRules = ref([] as ExamRule[])// 试卷规则
    const examUserIds = ref([] as number[])// 考试用户
    const markUserIds = ref([] as number[])// 阅卷用户
    const limitMinute = 0 // 限制分钟

    // 计算属性
    const totalScore = computed(() => {// 总分
        if (genType.value === 1) { // 人工组卷
            return parseFloat(examQuestions.value.reduce((total, cur) => {
                return cur.type === 1 ? total : (cur.score as number) + total
            }, 0).toFixed(2)) // 0.1 + 0.2 = 0.30000000000000004
        } else {// 随机组卷
            return parseFloat(examRules.value.reduce((total, cur) => {// 题数*分数
                return cur.type === 1 ? total : ((cur.score as number) * (cur.num as number)) + total
            }, 0).toFixed(2))
        }
    })
    const markQuestions = computed(() => {// 阅卷试题
        if (genType.value === 1) {
            return examQuestions.value.filter(examQuestion => (examQuestion.type === 2 && examQuestion?.markType === 2))
        } else {
            return examRules.value.filter(examRule => (examRule.type === 2 && examRule?.markType === 2))
        }
    })
    const examUserNum = computed(() => examUserIds.value.length)// 考试人数
    const markUserNum = computed(() => markUserIds.value.length)// 阅卷人数
    const objectiveQuestionNum = computed(() => { // 客观题数
        if (genType.value === 1) {
            return examQuestions.value.reduce((total, examQuestion) => {
                return (examQuestion.type === 2 && examQuestion?.markType === 1) ? total + 1 : total
            }, 0)
        } else {
            return examRules.value.reduce((total, examRule) => {
                return (examRule.type === 2 && examRule?.markType === 1) ? total + (examRule.num as number) : total
            }, 0)
        }
    })
    const subjectiveQuestionNum = computed(() => { // 主观题数
        if (genType.value === 1) {
            return examQuestions.value.reduce((total, examQuestion) => {
                return (examQuestion.type === 2 && examQuestion?.markType === 2) ? total + 1 : total
            }, 0)
        } else {
            return examRules.value.reduce((total, examRule) => {
                return (examRule.type === 2 && examRule?.markType === 2) ? total + (examRule.num as number) : total
            }, 0)
        }
    })
    const markType = computed(() => {// 阅卷类型
        return subjectiveQuestionNum.value > 0 ? 2 : 1
    })
    const examTimeDiff = computed(() => {// 考试时间差（分钟）
        if(!examTimes.value[0] || !examTimes.value[1]) {
            return 0
        }

        return dayjs(examTimes.value[1], 'YYYY-MM-DD HH:mm:ss').diff(dayjs(examTimes.value[0], 'YYYY-MM-DD HH:mm:ss'), 'minute')
    })

    // 序号更新
    function noUpdate() {
        let no = 1
        let no2 = -1 // 拖动排序使用，保证唯一
        if (genType.value === 1) {
            examQuestions.value.forEach(examQuestion => {
                if (examQuestion.type === 2) {
                    examQuestion.no = no++
                } else {
                    examQuestion.no = no2--
                }
            });
        } else if (genType.value === 2) {
            examRules.value.forEach(examRule => {
                if (examRule.type === 2) {
                    examRule.no = no++
                } else {
                    examRule.no = no2--
                }
            });
        }
    }
    return {
        // 属性
        id, name, paperName, examTimes, markTimes, limitMinute, genType, passScore, sxes, showType, anonState, scoreState, rankState, state, examQuestions, examRules, examUserIds, markUserIds,
        // 计算属性
        totalScore, markQuestions, markType, examUserNum, markUserNum, objectiveQuestionNum, subjectiveQuestionNum, examTimeDiff,
        // 方法
        noUpdate
    }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useExamStore, import.meta.hot))
}

/**
 * 试卷接口
 */
export interface ExamQuestion {
    type: number // 类型 （1：章节；2：试题）
    no?: number // 序号
    chapterName?: string // 章节名称（type === 1有效）
    chapterTxt?: string // 章节描述（type === 1有效）
    questionId?: number// 试题编号（题库选择有；文本导入没有，后台为先保存题在使用）
    questionType?: number// 试题类型 （1：单选；2：多选；3：填空；4：判断；5：问答）
    markType?: number// （阅卷类型 1：客观题；2：主观题）
    title?: string// 标题
    options?: string[]// 选项
    markOptions?: number[] //（2：答案无顺序；3：不区分大小写；)
    score?: number// 分数
    answers?: string[]// 答案
    scores?: number[]// 答案分数
    userAnswers?: string[]// 用户答案
    userScore?: number// 用户分数
    analysis?: string// 解析
    commit?: boolean// 是否提交
}

/**
 * 试题接口
 */
export interface Question {
    no?: number // 序号
    id: number// 试题编号（题库选择有；文本导入没有，后台为先保存题在使用）
    type: number// 试题类型 （1：单选；2：多选；3：填空；4：判断；5：问答）
    markType: number// （阅卷类型 1：客观题；2：主观题）
    title: string// 标题
    options: string[]// 选项
    markOptions: number[] //（2：答案无顺序；3：不区分大小写；)
    score: number// 分数
    answers: string[]// 答案
    scores: number[]// 答案分数
    userAnswers?: string[]// 用户答案
    userScore?: number// 用户分数
    analysis: string// 解析
}

/**
 * 抽题规则接口
 */
export interface ExamRule {
    type: number // 类型 （1：章节；2：试题）
    no?: number // 序号
    chapterName?: string // 章节名称（type === 1有效）
    chapterTxt?: string // 章节描述（type === 1有效）
    questionTypeId?: number // 题库ID
    questionType?: number // 试题类型 （1：单选；2：多选；3：填空；4：判断；5：问答）
    markType?: number// （阅卷类型 1：客观题；2：主观题）
    markOptions?: number[] //（2：答案无顺序；3：不区分大小写；)
    num?: number // 题数
    score?: number // 分数
    scores?: number // 子分数，用于表示多选漏选分值，客观填空问答每空分值
}

/**
 * 菜单接口
 */
export interface Menu {
    name: String // 名称
    icon: String // 图标
    event: Function // 点击后回调方法
}