import { ref, computed } from 'vue'
import { defineStore, acceptHMRUpdate } from 'pinia'
import dayjs from 'dayjs'
import type { ExamQuestion, ExamRule } from '@/ts/exam/exam'

// 考试缓存
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
    const sxes = ref<number[]>([]) // 防作弊
    const showType = ref(1) // 展示方式
    const loginType = ref(1) // 登录方式（1：账号登录；2：临时登录；）
    const anonState = ref(2) // 匿名阅卷（1：是；2：否）
    const scoreState = ref(2) // 成绩查询（1：考试结束后；2：不公布；3：交卷后）
    const rankState = ref(2) // 排名公布（1：是；2：否）
    const state = ref(1) // 状态（1：发布；2：暂停；）
    const examQuestions = ref<ExamQuestion[]>([])// 试卷信息
    const examRules = ref<ExamRule[]>([])// 试卷规则
    const userIds = ref<number[]>([])// 考试用户
    const orgIds = ref<number[]>([])// 机构
    const markUserIds = ref<number[]>([])// 阅卷用户
    const limitMinute = 0 // 限制分钟
    const retakeNum = 0 // 补考次数

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
    const examUserNum = computed(() => userIds.value.length)// 考试人数
    const markUserNum = computed(() => markUserIds.value.length)// 阅卷人数
    const objectiveQuestionNum = computed(() => { // 客观题数
        if (genType.value === 1) {
            return examQuestions.value.reduce((total, examQuestion) => {
                return (examQuestion.type === 2 && examQuestion?.markType === 1) ? total + 1 : total
            }, 0)
        }

        return examRules.value.reduce((total, examRule) => {
            return (examRule.type === 2 && examRule?.markType === 1) ? total + (examRule.num as number) : total
        }, 0)
    })
    const subjectiveQuestionNum = computed(() => { // 主观题数
        if (genType.value === 1) {
            return examQuestions.value.reduce((total, examQuestion) => {
                return (examQuestion.type === 2 && examQuestion?.markType === 2) ? total + 1 : total
            }, 0)
        }

        return examRules.value.reduce((total, examRule) => {
            return (examRule.type === 2 && examRule?.markType === 2) ? total + (examRule.num as number) : total
        }, 0)
    })
    const singleChoiceNum = computed(() => { // 单选题数
        if (genType.value === 1) {
            return examQuestions.value.filter(examQuestion => examQuestion.type === 2 && examQuestion?.questionType === 1).length
        }

        return examRules.value.reduce((total, examRule) => {
            return (examRule.type === 2 && examRule?.questionType === 1) ? total + (examRule.num as number) : total
        }, 0)
    })
    const multipleChoiceNum = computed(() => { // 多选题数
        if (genType.value === 1) {
            return examQuestions.value.filter(examQuestion => examQuestion.type === 2 && examQuestion?.questionType === 2).length
        }

        return examRules.value.reduce((total, examRule) => {
            return (examRule.type === 2 && examRule?.questionType === 2) ? total + (examRule.num as number) : total
        }, 0)
    })
    const fillblankNum = computed(() => { // 填空题数
        if (genType.value === 1) {
            return examQuestions.value.filter(examQuestion => examQuestion.type === 2 && examQuestion?.questionType === 3).length
        }

        return examRules.value.reduce((total, examRule) => {
            return (examRule.type === 2 && examRule?.questionType === 3) ? total + (examRule.num as number) : total
        }, 0)
    })
    const judgeNum = computed(() => { // 判断题数
        if (genType.value === 1) {
            return examQuestions.value.filter(examQuestion => examQuestion.type === 2 && examQuestion?.questionType === 4).length
        }

        return examRules.value.reduce((total, examRule) => {
            return (examRule.type === 2 && examRule?.questionType === 4) ? total + (examRule.num as number) : total
        }, 0)
    })
    const qaNum = computed(() => { // 问答题数
        if (genType.value === 1) {
            return examQuestions.value.filter(examQuestion => examQuestion.type === 2 && examQuestion?.questionType === 5).length
        }

        return examRules.value.reduce((total, examRule) => {
            return (examRule.type === 2 && examRule?.questionType === 5) ? total + (examRule.num as number) : total
        }, 0)
    })
    const chapterNum = computed(() => { // 章节数量
        if (genType.value === 1) {
            return examQuestions.value.filter(examQuestion => examQuestion.type === 1).length
        }

        return examRules.value.filter(examRule => examRule.type === 1).length
    })

    const markType = computed(() => {// 阅卷类型
        return subjectiveQuestionNum.value > 0 ? 2 : 1
    })
    const examTimeDiff = computed(() => {// 考试时间差（分钟）
        if (!examTimes?.value[0] || !examTimes?.value[1]) {// bug修复：清空时间examTimes为空，需要?
            return 1
        }

        const diff = dayjs(examTimes.value[1], 'YYYY-MM-DD HH:mm:ss').diff(dayjs(examTimes.value[0], 'YYYY-MM-DD HH:mm:ss'), 'minute')
        if (diff === 0) {// bug修复：el-input-number的min和max冲突，提示“min should not be greater than max”
            return 1
        }

        return diff
    })

    // 试题编号更新
    function questionNoUpdate() {
        let no = 1
        let no2 = -1 // 拖动排序使用，保证唯一
        if (genType.value === 1) {
            examQuestions.value.forEach(examQuestion => {
                if (examQuestion.type === 2) {
                    examQuestion.no = no++
                } else {
                    examQuestion.no = no2--
                }
            })
        } else if (genType.value === 2) {
            examRules.value.forEach(examRule => {
                if (examRule.type === 2) {
                    examRule.no = no++
                } else {
                    examRule.no = no2--
                }
            })
        }
    }
    return {
        // 属性
        id, name, paperName, examTimes, markTimes, limitMinute, genType, loginType, passScore, sxes, showType, anonState, scoreState, rankState, state, examQuestions, examRules, userIds, markUserIds, orgIds, retakeNum,
        // 计算属性
        totalScore, markQuestions, markType, examUserNum, markUserNum, objectiveQuestionNum, subjectiveQuestionNum, examTimeDiff, chapterNum, singleChoiceNum, multipleChoiceNum, fillblankNum, judgeNum, qaNum,

        // 方法
        questionNoUpdate
    }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useExamStore, import.meta.hot))
}