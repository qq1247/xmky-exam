<template>
    <el-card v-if="exam.markType === 1" shadow="never">
        <el-empty description="无需阅卷"/>
    </el-card>
    <el-card v-else-if="exam.markState === 1 || !exam.markStart" shadow="never">
        <el-empty description="阅卷未开始"/>
    </el-card>
    <el-card v-else-if="exam.markState === 3" shadow="never">
        <el-empty description="阅卷已结束"/>
    </el-card>
    <el-card v-else-if="receiveTask" shadow="never">
        <el-form  ref="formRef" :model="form" :rules="formRules">
            <el-form-item label="" prop="">
                您已领取试卷{{ paperNum }}份，未批阅{{ paperNumOfUnMark }}份
                &nbsp;&nbsp;
                <el-button v-if="paperNum > 0" type="primary" @click="receiveTask = false">
                    查看
                </el-button>
            </el-form-item>
            <el-form-item label="本次批阅：" prop="num">
                <el-input-number v-model="form.num" :min="1" :max="100" :step="10" :precision="0" controls-position="right" />&nbsp;份
                &nbsp;&nbsp;
                <el-button type="primary" @click="assign">
                    领取
                </el-button>
            </el-form-item>
        </el-form>
    </el-card>
    <div v-else class="mark" v-loading="loading" :element-loading-text="loadingText" element-loading-background="rgba(122, 122, 122, 0.8)">
        <el-button class="mark-left" @click="paperPre" :disabled="userListpage.list.length > 0 && userListpage.list[0].userId === curUserId">&lt;&lt;上一卷</el-button>
        <div class="mark-center">
            <el-scrollbar height="calc(100vh - 240px)" >
                <el-card shadow="never" class="mark-center-question">
                    <el-form ref="formRef" :model="form" :rules="formRules">
                        <template v-for="(examQuestion, index) in examQuestions" :key="index">
                            <div v-if="paperShow && examQuestion.type === 1" class="paper-right-chapter">
                                <el-input :value="examQuestion.chapterName" maxlength="14" placeholder="请输入章节名称" :readonly="true"/>
                                <el-input v-if="examQuestion.chapterTxt" :value="examQuestion.chapterTxt" type="textarea" maxlength="128" 
                                    :autosize="{ minRows: 1 }" resize="none" placeholder="请输入章节描述" :readonly="true"/>
                            </div>
                            <Question 
                                v-else-if="paperShow || curQuestionNos.indexOf(examQuestion.no as number) != -1"
                                :no="examQuestion.no" 
                                :type="examQuestion.questionType || 1"
                                :markType="examQuestion.markType || 1" 
                                :title="examQuestion.title || ''" 
                                :score="examQuestion.score || 1" 
                                :userAnswers="examQuestion.userAnswers"
                                :options="examQuestion.options"
                                :editable="false"
                                @change=""
                                >
                                <template v-if="examQuestion.markType === 2" #bottom>
                                    <div class="mark-center-question-score">
                                        <span>本题得</span>
                                        <el-input-number 
                                            v-model="examQuestion.userScore" 
                                            :min="0" 
                                            :max="20" 
                                            size="small"
                                            :precision="1"
                                            :readonly="examQuestion.commit"
                                            @blur="() => score(examQuestion)"
                                            ></el-input-number>分<!-- 用blur事件，输入字母或删除数字不触发change事件 -->
                                    </div>
                                    <div class="mark-center-question-plate">
                                        <el-button type="primary" plain size="small" :disabled="examQuestion.commit" @click="examQuestion.userScore = 0;score(examQuestion)">零分</el-button>
                                        <template v-for="i in 10">
                                            <el-button      
                                                v-if="examQuestion.score && examQuestion.score <= 5  && (i * 0.5) < examQuestion.score" 
                                                type="primary" 
                                                plain 
                                                size="small"
                                                :disabled="examQuestion.commit"
                                                @click="examQuestion.userScore = i * 0.5;score(examQuestion)"
                                                >{{ i * 0.5 }}</el-button>
                                            <el-button 
                                                v-else-if="examQuestion.score && examQuestion.score <= 10 && (i      ) < examQuestion.score" 
                                                type="primary" 
                                                plain 
                                                size="small"
                                                :disabled="examQuestion.commit"
                                                @click="examQuestion.userScore = i;score(examQuestion)"
                                                >{{ i       }}</el-button>
                                            <el-button 
                                                v-else-if="examQuestion.score && examQuestion.score <= 20 && (i *   2) < examQuestion.score" 
                                                type="primary" 
                                                plain 
                                                size="small"
                                                :disabled="examQuestion.commit"
                                                @click="examQuestion.userScore = i * 2;score(examQuestion)"
                                                >{{ i  *  2 }}</el-button>
                                            </template>
                                        <el-button type="primary" plain size="small" :disabled="examQuestion.commit" @click="examQuestion.userScore = examQuestion.score;score(examQuestion)">满分</el-button>
                                        <el-tooltip placement="top" effect="light" :content="answerShow(examQuestion)" raw-content>
                                            <el-button type="success" size="small" style="float: right;">标准答案</el-button>
                                        </el-tooltip>
                                    </div>
                                </template>
                            </Question>
                        </template>
                    </el-form>
                </el-card>
            </el-scrollbar>
            <el-card shadow="never" class="mark-center-handle">
                <el-form :inline="true">
                    <el-form-item label="当前批阅：">
                        <el-select v-model="curUserId" filterable placeholder="请选择" >
                            <el-option
                                v-for="user in userListpage.list"
                                :key="user.userId"
                                :label="user.userName"
                                :value="user.userId"
                                :disabled="user.myExamState === 1"
                            >
                                {{ user.userName }} - 
                                <span v-if="user.myExamState === 1">{{dictStore.getValue('EXAM_STATE', user.myExamState)}}</span>
                                <span v-else :style="user.myExamMarkState !== 3 ? 'color: #f56c6c;' : ''">{{ dictStore.getValue('MARK_STATE', user.myExamMarkState) }}</span><!-- 用class的bug：:teleported="false"定位错误；:teleported="true"在body中，class控制不了 -->
                            </el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="批阅进度：">
                        <el-progress :percentage="((paperNum - paperNumOfUnMark) * 100 / paperNum) || 0"><!-- 后台没请求完会导致NAN，所以加 || 0 -->
                            <el-button text>{{ paperNum - paperNumOfUnMark }} / {{ paperNum }}</el-button>
                        </el-progress>
                    </el-form-item>
                    <el-form-item label="批阅题号：">
                        <el-checkbox v-model="paperShow" label="整卷显示"/>
                        <el-checkbox-group v-model="curQuestionNos" :disabled="paperShow">
                            <template v-for="(examQuestion, index) in examQuestions" :key="index">
                                <el-checkbox v-if="examQuestion.markType === 2" :label="examQuestion.no" />
                            </template>
                        </el-checkbox-group>
                    </el-form-item>
                </el-form>
            </el-card>
        </div>
        <el-button class="mark-right" @click="paperNext" :disabled="userListpage.list.length > 0 && userListpage.list[userListpage.list.length - 1].userId === curUserId">下一卷>></el-button>
    </div>
</template>

<script lang="ts" setup>
import http from '@/request';
import type { ExamQuestion } from '@/stores/exam';
import type { FormInstance, FormRules } from 'element-plus';
import { computed, onMounted, reactive, ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import Question from '@/components/question/Question.vue';
import { useDictStore } from '@/stores/dict';
import dayjs from "dayjs";

//  定义变量
const route = useRoute()
const dictStore = useDictStore()// 字典缓存
const form = reactive({// 表单
    examId: 0,// 考试ID
    num: 10// 新批阅份数
})
const formRef = ref<FormInstance>()// 表单引用
const formRules = reactive<FormRules>({// 表单校验规则
    num: [
        { required: true, message: '请输入批阅份数', trigger: 'blur' },
    ],
})

const receiveTask = ref(false)// 显示领取任务页面

const userListpage = reactive({// 用户分页列表
    curPage: 1,
    pageSize: 100,
    list: [] as any[],// 自己已领取试卷的用户列表
})

const exam = reactive({// 考试信息
    id: 0,
    markType: 0,// 阅卷方式（1：客观题；2：主观题；）
    markState: 0,
    markStartTime: null,
    markEndTime: null,
    markStart: true, // 阅卷已开始（时间开始）
    sxes: [] as number[],// 反作弊（1：试题乱序；2：选项乱序；）
})
const curUserId = ref() // 当前选中用户
const paperShow = ref(false) // false：主观题显示；true：整卷显示
const curQuestionNos = ref([] as number[])// 当前选中的试题
const examQuestions = ref([] as ExamQuestion[])// 试卷信息
const loading = ref(false) // 显示加载页面
const loadingText = ref('loading...')// 显示加载页面内容

// 计算属性
const paperNum = computed(() => userListpage.list.length)// 领取试卷数量
const paperNumOfUnMark = computed(() => userListpage.list.filter((user) => user.myExamMarkState !== 3).length) // 已领取未批阅数量

// 监听属性
watch(curUserId, async () => {// 切换试卷
    if (!curUserId.value) {
        return
    }
    if (exam.markType === 1) {// 客观试卷，无需显示
        return
    }
    if (exam.markState === 1 || exam.markState === 3) {// 阅卷未开始 阅卷已结束 不显示
        return
    }

    let userName = userListpage.list.filter((user) => user.userId === curUserId.value)[0].userName
    loadingText.value = `正在打开${userName}的试卷...`
    loading.value = true
    let { data: { data } } = await http.post("myMark/paper", { examId: form.examId, userId: curUserId.value })
    let no = 1
    examQuestions.value = data.map((examQuestion: ExamQuestion) => {
        examQuestion.commit = false

        if (examQuestion.type === 2) {// 处理题号
            examQuestion.no = no++
        }
        return examQuestion
    })

    if (!paperShow.value) {
        if (exam.markType === 2 && exam.sxes.includes(1)) {// 主观题且试题随机，每张卷子都重新获取题号（题号不一样）
            curQuestionNos.value.length = 0
        }
        if (!curQuestionNos.value.length) {
            examQuestions.value.forEach((examQuestion : ExamQuestion) => {
                if (examQuestion.markType === 2) {
                    curQuestionNos.value.push(examQuestion.no as number)
                }
            });
        }
    }
    setTimeout(() => loading.value = false, 500)
})

// 组件挂载完成后，执行如下方法
onMounted(async () => {
    // 获取考试信息
    form.examId = parseInt(route.params.examId as string)
    let { data: { data } } = await http.post("exam/get", {id: form.examId})
    exam.id = form.examId
    exam.markType = data.markType
    exam.markState = data.markState
    exam.markStartTime = data.markStartTime
    exam.markEndTime = data.markEndTime
    exam.markEndTime = data.markEndTime
    exam.sxes = data.sxes

    if (exam.markType === 1) {// 客观试卷，不处理
        return
    }

    let markStartTime = dayjs(exam.markStartTime, 'YYYY-MM-DD HH:mm:ss').toDate()
    let { data: { data: data1 } } = await http.post("login/sysTime", {  })
    let curTime = dayjs(data1, 'YYYY-MM-DD HH:mm:ss').toDate()
    if (curTime.getTime() < markStartTime.getTime()) {// 阅卷时间未开始 不显示
        exam.markStart = false 
        return
    }
    
    // 获取领取的试卷
    await getUserList()

    if (userListpage.list.length > 0) {
        curUserId.value = userListpage.list[0].userId
    }

    // 考试阅卷未结束，并且第一次进入阅卷或自己已领取全部阅完，则显示领取试卷页
    if (exam.markState === 2) {// 考试正在批阅中
        if (paperNum.value === 0 || paperNumOfUnMark.value === 0) {// 没有领取过试卷 或 已领取且全部阅完
            receiveTask.value = true
        }
    }
})

// 获取用户列表
async function getUserList() {
    userListpage.curPage = 1
    userListpage.list.length = 0

    while (true) {
        let { data: { data } } = await http.post("myMark/userListpage", { 
            examId: form.examId,
            state: 3,// 未考试的不显示；考试中的时间到会变为已交卷
            curPage: userListpage.curPage++,
            pageSize: userListpage.pageSize,
        })

        userListpage.list.push(...data.list)
        if (userListpage.list.length >= data.total) {// 分批次取出
            break
        }
    }
}

// 分配试卷
async function assign() {
    if (!formRef.value) return
    await formRef.value.validate(async (valid, fields) => {
        if (!valid) {
            return
        }

        await http.post("myMark/assign", {...form})
        await getUserList()

        if (userListpage.list.length > 0) {
            curUserId.value = userListpage.list[0].userId
        }

        receiveTask.value = false // 就算没有新批阅的试卷，也切换页面
    })
}

// 上一卷
function paperPre() {
    let curIndex = userListpage.list.findIndex((user) => user.userId === curUserId.value)
    curUserId.value = userListpage.list[--curIndex].userId
}

// 下一卷
function paperNext() {
    let curIndex = userListpage.list.findIndex((user) => user.userId === curUserId.value)
    curUserId.value = userListpage.list[++curIndex].userId
}

// 打分
async function score(examQuestion: ExamQuestion) {
    // 单题打分
    if (!examQuestion.userScore) {
        examQuestion.userScore = 0
    }

    examQuestion.commit = true
    let { data: { code, data } } = await http.post("myMark/score", {
        examId: exam.id,
        userId: curUserId.value,
        questionId: examQuestion.questionId,
        userScore: examQuestion.userScore
    })

    if (code !== 200) {
        examQuestion.userScore = undefined// 如果打分失败，把页面的分数清空掉，实现前后端数据同步
        examQuestion.commit = false
        return
    }

    // 如果当前试卷主观题都打分，完成阅卷。
    let markAll = examQuestions.value.every(examQuestion => examQuestion.type === 1 || examQuestion.markType === 1 || examQuestion.userScore != null)// 用!=，不要用!==，一个条件就够了
    if (markAll) {
        loadingText.value = `正在合计总分...`
        loading.value = true
        let { data: { code, data } } = await http.post("myMark/finish", {
            examId: exam.id,
            userId: curUserId.value,
        })

        if (code === 200) {// 如果阅卷成功，把考试用户的阅卷状态变更一下，实现前后端数据同步
            let curIndex = userListpage.list.findIndex((user) => user.userId === curUserId.value)
            userListpage.list[curIndex].myExamMarkState = 3
        }

        setTimeout(() => loading.value = false, 500)
    }

    examQuestion.commit = false
}

function answerShow(examQuestion: ExamQuestion) {
    if (examQuestion.questionType === 1 // 单选
        || examQuestion.questionType === 2 // 多选
        || examQuestion.questionType === 4 // 判断
        || (examQuestion.questionType === 5 && examQuestion.markType === 2)) {// 主观问答
        return examQuestion.answers && examQuestion.answers[0] && examQuestion.answers[0].replaceAll('\n', '<br/>')
    }

    if (examQuestion.questionType === 3 // 填空
        || (examQuestion.questionType === 5 && examQuestion.markType === 1)) {// 客观问答
        let answerFormat = ''
        examQuestion.answers?.forEach((answer, index) => {
            answerFormat += `${ examQuestion.questionType === 3 ? '填空' : '关键词' }${ index + 1 }：${ answer.replaceAll('\n', '、') }<br/>`
        })

        return answerFormat
    }
}
</script>

<style lang="scss" scoped>
.mark {
    height: calc(100% - 20px);
    display: flex;
    .mark-center {
        width: 800px;
        display: flex;
        flex-direction: column;
        margin: 0px 5px;
        :deep(.mark-center-question) {
            min-height: calc(100vh - 240px);
            .el-input__wrapper {
                box-shadow: initial;
                .el-input__inner {
                    // font-size: 16px;
                    font-weight: bold;
                }
            }
            .mark-center-question-score {
                span {
                    font-size: 13px;
                    color: var(--el-text-color-regular);
                }
                .el-button {
                    margin-left: 10px;
                    span {
                        color: white;
                    }
                }
                .el-input-number {
                        width: 40px;
                        .el-input-number__decrease, .el-input-number__increase {
                            display: none;
                        }
                        .el-input__wrapper {
                            padding: 0px;
                            box-shadow: initial;
                            border-bottom: 1px solid var(--el-border-color);
                            border-radius: 0;
                            .el-input__inner {
                                font-size: 13px;
                                font-weight: normal;
                            }
                        }
                    }
            }
            .mark-center-question-plate {
                margin-top: 10px;
                .el-button:not(:first-child) {
                    margin-left: 8px;
                }
            }
        }
        :deep(.mark-center-handle) {
            margin-top: 5px;
            .el-form-item {
                margin-bottom: 0px;
                .el-progress-bar {
                    width: 220px;
                }

                .el-checkbox:last-of-type {
                    margin-right: 20px;
                }
            }
        }
    }
    .mark-left, .mark-right {
        flex: 1;
        height: 100%;
    }
}
</style>
