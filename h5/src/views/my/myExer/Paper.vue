<template>
    <div class="paper">
        <!-- 答题卡 -->
        <div class="paper-left">
            <el-card shadow="never" class="paper-left-top">
                <!-- <el-avatar size="large"/> -->
                <span class="paper-left-top-username">
                    {{ user.name }} / {{ user.orgName }}
                </span>
                <div class="paper-left-top-statis">
                    <div>
                        <Iconfont icon="icon-jiangbei" :size="20" color="#F6961E;" :width="30" :height="30" :radius="5" background-color="#FDF3E7" />
                        <span class="paper-left-top-statis-value">{{ questionNum }}道</span>
                        <span class="paper-left-top-statis-txt">试题数量</span>
                    </div>
                    <div>
                        <Iconfont icon="icon-approval-fulll" :size="20" color="#05CAC1;" :width="30" :height="30" :radius="5" background-color="#E3F3FF" />
                        <span class="paper-left-top-statis-value">{{ questionErrNum }}道</span>
                        <span class="paper-left-top-statis-txt">错误题数</span>
                    </div>
                    <div>
                        <Iconfont icon="icon-shijianxuanzhong" :size="20" color="#F6961E;" :width="30" :height="30" :radius="5" background-color="#FDF3E7" />
                        <span class="paper-left-top-statis-value">{{ diffMinute }}分钟</span>
                        <span class="paper-left-top-statis-txt">答题用时</span>
                    </div>
                </div>
                <div class="paper-left-top-time">
                    <CountDown :expireTime="exer.endTime as Date" preTxt="距结束：" :remind="300" @end="exerEnd" @remind="exer.color='var(--el-color-danger)'" :color="exer.color"></CountDown>
                </div>
                <el-button type="primary" plain @click="$router.go(-1)">返回</el-button>
            </el-card>
            <el-card shadow="never" class="paper-left-bottom">
                <el-divider>
                    答题卡
                </el-divider>
                <el-scrollbar height="calc(100vh - 475px)">
                    <template v-for="(questionId, index) in exer.questionIds">
                        <el-button 
                            :type="errMark(index).type" 
                            :plain="errMark(index).plain"
                            @click="questionView(index)"
                            >{{ index + 1 }}</el-button>
                    </template>
                </el-scrollbar>
            </el-card>
        </div>
        <!-- 试卷 -->
        <el-card shadow="never" class="paper-right">
            <!-- 选项 -->
            <div class="paper-right-opt">
                <el-button link size="small" :disabled="curQuestion.index <= 0" @click="next(false)">{{ `<<上一题` }}</el-button>
                <el-button link size="small" :disabled="curQuestion.index >= exer.questionIds.length - 1" @click="next(true)">{{ `下一题>>` }}</el-button>
                <el-checkbox v-model="exer.answerShow" label="背题" size="small"></el-checkbox>
                <el-checkbox v-model="exer.randShow" label="随机" size="small"></el-checkbox>
                <el-checkbox v-if="exer.rmkState === 1" v-model="exer.rmkShow" label="评论" size="small"></el-checkbox>
            </div>
            <el-divider />
            <!-- 试题 -->
            <QuestionVue 
                v-if="curQuestion.index >= 0"
                :no="curQuestion.index + 1" 
                :type="curQuestion.question.type"
                :markType="curQuestion.question.markType" 
                :title="curQuestion.question.title" 
                :score="curQuestion.question.score"
                :answers="curQuestion.question.answers"
                :userScore="curQuestion.question.userScore"
                :userAnswers="curQuestion.question.userAnswers"
                :options="curQuestion.question.options"
                :editable="!exer.answerShow && curQuestion.question.userScore == null"
                @change="(answers: string[]) => {
                    exer.lastTime = new Date() // 答题就更新时间
                    curQuestion.question.userAnswers = answers
                    if (curQuestion.question.type === 1 || curQuestion.question.type === 4) {// 单选或判断，选完答案，自动下一题
                        next(true)
                    }
                }"
                :user-answer-show="!exer.answerShow"
                :errShow="!exer.answerShow && curQuestion.question.userScore != null"
                >
                <template #bottom-right>
                    <el-tooltip placement="top" effect="light" :content="answerFormat(curQuestion.question)" popper-class="popper-class">
                        <el-button type="success" size="small">标准答案</el-button>
                    </el-tooltip>
                </template>
            </QuestionVue>
            <!-- 评论 -->
            <div v-if="exer.rmkShow && exer.rmkState === 1" class="paper-right-my-rmk">
                <el-input v-model="exerRmk.content" placeholder="我是这样解题的：" :autosize="{ minRows: 1 }" type="textarea" maxlength="100" show-word-limit resize="none"/>
                <el-checkbox v-model="exerRmk.anon" label="匿名" size="small"/>
                <el-button type="primary" text bg size="small" @click="rmk">我来解题</el-button>
            </div>
            <el-divider v-if="exer.rmkShow && exer.rmkState === 1" content-position="left"></el-divider>
            <el-scrollbar max-height="calc(100vh - 400px)">
                <ul v-if="exer.rmkShow && exer.rmkState === 1" class="paper-right-rmk-list">
                    <li :id="`rmk${ rmk.id }`" v-for="rmk in rmkListpage.list" :key="rmk.id">
                        <el-text tag="p">{{ rmk.content }}</el-text>
                        <div>
                            <el-text type="info" size="small">{{ rmk.updateUserName || '匿名'}} {{ rmk.updateTime }}</el-text>
                            <span 
                                :class="{ 'iconfont': true, 'icon-dianzan3': rmk.hasLike, 'icon-dianzan1': !rmk.hasLike, }" 
                                @click="rmkLike(rmk.id)"
                                >&nbsp;{{ rmk.likeNum }}</span>
                        </div>
                    </li>
                    <li style="background-color: white;">
                        <el-button v-if="((rmkListpage.total - 1) / rmkListpage.pageSize) + 1 > rmkListpage.curPage" type="" link @click="rmkQuery()">查看更多>></el-button>
                    </li>
                </ul>
            </el-scrollbar>
        </el-card>
    </div>
</template>
<script lang="ts" setup>
import http from '@/request';
import { computed, onMounted, onUnmounted, reactive, ref, watch, } from 'vue';
import QuestionVue from '@/components/question/Question.vue';
import _ from 'lodash'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox, type Action } from 'element-plus';
import type { Question } from '@/stores/exam';
import dayjs from 'dayjs';
import CountDown from '@/components/CountDown.vue';
import { useUserStore } from '@/stores/user';

// 定义变量
const userStore = useUserStore()
const route = useRoute()
const router = useRouter()
const exer = reactive({// 练习信息
    id: 0, // 练习ID
    startTime: null as unknown,// 开始时间
    endTime: null as unknown,// 结束时间
    rmkState: 2,// 评论状态（1：是；2：否）
    questionIds: [] as number[], // 试题列表
    answerShow: false,// 标准答案显示
    randShow: false, // 乱序显示
    rmkShow: false, // 评论显示
    firstTime: new Date(),// 第一次进入时间
    lastTime: new Date(),// 最后一次答题时间
    color: '', // 倒计时颜色
}) 
const curQuestion = reactive({// 当前试题
    index: -1,// 索引
    question: {} as Question,// 内容
    cache: {} as { [questionId: number]: Question }// 缓存
})
const user = reactive({// 用户信息 
    name: '',// 姓名
    orgName: '',// 机构名称
})

const exerRmk = reactive({// 评论
    content: '',
    anon: false
})
const rmkListpage = reactive({// 评论分页列表
    curPage: 1,
    pageSize: 20,
    total: 0,
    cache: [] as number[],// 存放ID，用来过滤重复数据
    list: [] as any[],
})

// 组件挂载完成后，执行如下方法
onMounted(async () => {
    // 隐藏页头信息（模拟全屏）
    (document.getElementsByClassName('el-header')[0] as HTMLElement).style.display = 'none'

    // 获取机构信息
    let { data: { data: data2 } } = await http.post("user/get", {  })
    user.name = data2.name
    user.orgName = data2.orgName
    
    // 获取我的练习
    exer.id = parseInt(route.params.exerId as string)
    let { data: { data: data3 } } = await http.post("myExer/get", { exerId: exer.id })
    exer.startTime = dayjs(data3.startTime, 'YYYY-MM-DD HH:mm:ss').toDate()
    exer.endTime = dayjs(data3.endTime, 'YYYY-MM-DD HH:mm:ss').toDate()
    exer.rmkState = data3.rmkState

    let { data: { data } } = await http.post("myExer/questionList", { exerId: exer.id })
    data.userAnswers = []
    data.userScore = null
    exer.questionIds.push(...data)

    if (exer.questionIds.length) {// 显示第一个
        questionView(0)
    }
})

// 监听属性
watch(() => exer.randShow, async (n, o) => {// 随机和顺序，同一个索引不是同一个题
    questionView(curQuestion.index)
})


// 计算属性
const questionIdsOfShuffle = computed(() => {// 点随机的时候用
    return _.shuffle(exer.questionIds)
})
const questionNum = computed(() => {// 试题数量
    return exer.questionIds.length
})
const questionErrNum = computed(() => {// 试题错误数量
    return exer.questionIds.reduce((total, questionId) => {
        let question = curQuestion.cache[questionId]
        if (question && (question.type === 1 || question.type === 2 || question.type === 4)) {// 如果是单选多选判断
            if (question.userScore != null && question.userScore !== question.score) {// 答过题且不是全对
                total++// 错误数加一
            }
        }
        return total
    }, 0)
})
const diffMinute = computed(() => {// 答题时长（分钟数）
    return dayjs(exer.lastTime).diff(exer.firstTime, 'minute') + 1
})

// 组件卸载完成后，执行如下方法
onUnmounted(() => {
    (document.getElementsByClassName('el-header')[0] as HTMLElement).style.display = 'initial'
})

/**
 * 试题查阅
 * 
 * v1.0 zhanghc 2023-04-27 12:05:09
 * @param {*} index 试题列表索引
 * @return {*}
 */
async function questionView(index: number) {
    let questionId = exer.randShow ? questionIdsOfShuffle.value[index] : exer.questionIds[index]
    if (curQuestion.cache[questionId]) {
        curQuestion.index = index
        curQuestion.question = curQuestion.cache[questionId]
    } else {
        let { data: { data: data } } = await http.post("myExer/question", { exerId: exer.id, questionId })
        data.userAnswers = [] // 接口没有
        data.userScore = null // 接口没有
        curQuestion.question = data
        curQuestion.cache[questionId] = data
    
        curQuestion.index = index
    }

    rmkListpage.curPage = 1
    rmkListpage.cache = []
    rmkListpage.list = []
    rmkQuery()
}

/**
 * 下一题
 * 
 * v1.0 zhanghc 2023-04-27 16:22:00
 * @param {*} hasNext true：下一题：false:上一题
 * @return {*} void
 */
function next(hasNext: boolean) {
    // 数据有效性校验
    if (hasNext) {
        if (curQuestion.index >= exer.questionIds.length - 1) {
            ElMessage.success('最后一题')
            return
        }
    }
    if (!hasNext) {
        if (curQuestion.index < 0) {
            ElMessage.success('第一题')
            return
        }
    }

    // 如果是答题模式，选择错误时，标记错误
    if (!exer.answerShow) {
        if (curQuestion.question.type === 1 || curQuestion.question.type === 4) {// 如果是单选或判断
            if (curQuestion.question.userScore == null) {// 如果没打分
                if (curQuestion.question.userAnswers && curQuestion.question.userAnswers[0]) {// 如果有答案
                    curQuestion.question.userScore = 0
                    if (curQuestion.question.userAnswers[0] === curQuestion.question.answers[0]) {// 打分
                        curQuestion.question.userScore = curQuestion.question.score
                    }
                    if (curQuestion.question.userScore === 0) {// 如果答错，则停留当前题，不进入下一题
                        return 
                    }
                }
            }
        }
        if (curQuestion.question.type === 2) {// 如果是多选
            if (curQuestion.question.userScore == null) {// 如果没打分
                if (curQuestion.question.userAnswers && curQuestion.question.userAnswers[0]) {// 如果有答案
                    curQuestion.question.userScore = 0
                    let include = curQuestion.question.userAnswers.every((userAnswer: string) => curQuestion.question.answers.includes(userAnswer))
                    if (include && curQuestion.question.answers.length === curQuestion.question.userAnswers.length) {// 打分
                        curQuestion.question.userScore = curQuestion.question.score
                    } else if (include) {
                        curQuestion.question.userScore = curQuestion.question.scores[0]
                    }

                    if (curQuestion.question.userScore < curQuestion.question.score) {// 如果答错，则停留当前题，不进入下一题
                        return 
                    }
                }
            }
        }
    }

    // 下一题
    if (hasNext) {
        questionView(curQuestion.index + 1)
    } else {
        questionView(curQuestion.index - 1)
    }
}

/**
 * 答案格式化显示
 * 
 * v1.0 zhanghc 2023-04-27 14:45:00
 * @param {*} question 试题
 * @return {*} void
 */
function answerFormat(question: Question) {
    if (question.type === 1 // 单选
        || question.type === 4 // 判断
        || (question.type === 5 && question.markType === 2)) {// 主观问答
        return question.answers && question.answers[0] && question.answers[0].replaceAll('\n', '<br/>')
    }
    if (question.type === 2) {// 多选
        return question.answers?.toString().replaceAll(",", "")
    }

    if (question.type === 3 // 填空
        || (question.type === 5 && question.markType === 1)) {// 客观问答
        let answerFormat = ''
        question.answers?.forEach((answer: string, index: number) => {
            answerFormat += `${ question.type === 3 ? '填空' : '关键词' }${ index + 1 }：${ answer.replaceAll('\n', '、') }<br/>`
        })

        return answerFormat
    }
}

/**
 * 错误标记
 * 
 * v1.0 zhanghc 2023-04-27 14:45:00
 * @param {*} index 试题列表索引
 * @return {*} Object 样式
 */
function errMark(index: number) {
    let questionId = exer.randShow ? questionIdsOfShuffle.value[index] : exer.questionIds[index]
    let question = curQuestion.cache[questionId]
    if (!question) {// 如果没有查看过这道题，默认颜色
        return { type: 'primary', plain: true }
    }
    if (!question.userAnswers?.length) {// 如果没有作答，默认颜色
        return  { type: 'primary', plain: true }
    }
    if (question.type === 3 || question.type === 5) {// 如果是填空问答，填了答案就代表做过
        if (question.userAnswers.some(userAnswer => userAnswer.length > 0)) {
            return { type: 'primary', plain: false }
        }
        return { type: 'primary', plain: true }
    }
    if (question.type === 1 || question.type === 2 || question.type === 4) {// 如果是单选多选判断
        if (question.userScore == null) {// 没打分，默认颜色（作用与多选）
            return { type: 'primary', plain: true }
        }
        if (question.userScore === question.score) {// 满分绿色
            return {type: 'success', plain: false}
        }
        if (question.userScore === 0) { // 零分红色
            return {type: 'danger', plain: false}
        }
        return {type: 'warning', plain: false} // 不满分警告色
    }

    return { type: 'primary', plain: true }
}

// 模拟结束
function exerEnd() {
    ElMessageBox.alert('模拟已结束', '提示消息', {
        confirmButtonText: '确定',
        callback: async (action: Action) => {
            router.go(-1)
        },
    })
}

// 评论
async function rmk() {
    if (!exerRmk.content.trim()) {
        return
    }
    const { data: { code, data } } = await http.post('myExer/rmk', {
        exerId: exer.id,
        questionId: curQuestion.question.id,
        content: exerRmk.content,
        anon: exerRmk.anon,
    })

    if (code !== 200) {
        return
    }

    rmkListpage.list.unshift({// 新添加的放在第一个，有可能按点赞排序，在后面看不到
        id: data.id,
        content: exerRmk.content,
        updateTime: dayjs(new Date()).format('YYYY-MM-DD HH:mm:ss'),
        updateUserName: exerRmk.anon ? null : userStore.name,
        likeNum: 0,
        hasLike: false,
    })
    
    setTimeout(() => {// 滚动到最上
        document.getElementById(`rmk${ data.id }`)?.scrollIntoView(true)
    }, 200);

    exerRmk.content = ''// 评论后清空内容
}

// 评论查询
async function rmkQuery() {
    const { data: { code, data } } = await http.post('myExer/rmkListpage', {
        questionId: curQuestion.question.id,
        curPage: rmkListpage.curPage,
        pageSize: rmkListpage.pageSize,
    })

    if (code !== 200) {
        return
    }

    rmkListpage.list.push(...data.list.filter((cur: any) => {// 过滤重复数据
        cur.content = cur.content.replaceAll('&lt;', '<')
            .replaceAll('&gt;', '>')
            .replaceAll('&amp;', '&')
            .replaceAll('&quot;', '"')
            .replaceAll('&apos;', "'")
            .replaceAll('&ldquo;', "“")
            .replaceAll('&rdquo;', "”")

        if(rmkListpage.cache.includes(cur.id)) {
            return false
        }

        rmkListpage.cache.push(cur.id)
        return true
    }))
    rmkListpage.total = data.total
    rmkListpage.curPage++
}

// 评论点赞
async function rmkLike(exerRmkId: number) {
    const { data: { code, data } } = await http.post('myExer/rmkLike', { exerRmkId })
    if (code !== 200) {
        return
    }

    let rmk = rmkListpage.list.filter(cur => cur.id === exerRmkId)
    rmk[0].hasLike = !rmk[0].hasLike
    if (rmk[0].hasLike) {
        rmk[0].likeNum++
    } else {
        rmk[0].likeNum--
    }
}

</script>

<style lang="scss" scoped>
.paper {
    flex: 1;
    display: flex;
    .paper-left {
        width: 240px;
        display: flex;
        flex-direction: column;
        .paper-left-top {
            margin-bottom: 10px;
            :deep(.el-card__body) {
                display: flex;
                flex-direction: column;
                align-items: center;
                padding: 20px 10px;
                .el-avatar {
                    border: 2px solid var(--el-color-primary);
                    margin-bottom: 10px;
                }
                .paper-left-top-username {
                    margin-bottom: 15px;
                    font-size: 13px;
                    font-weight: bold;
                    color: var(--el-text-color-regular);
                }
                .paper-left-top-statis {
                    display: flex;
                    width: 100%;
                    div {
                        flex: 1;
                        margin: auto;
                        .paper-left-top-statis-value {
                            font-size: 12px;
                            font-weight: bold;
                            color: var(--el-text-color-regular);
                            padding-top: 5px;
                            display: block;
                            text-align: center;
                        }
                        .paper-left-top-statis-txt {
                            font-size: 12px;
                            font-weight: bold;
                            color: var(--el-text-color-secondary);
                            display: block;
                            text-align: center;
                        }
                    }
                }
                .paper-left-top-time {
                    border: 1px dashed var(--el-color-primary);
                    padding: 5px;
                    margin-top: 20px;
                    width: 100%;
                    span {
                        font-weight: bold;
                        font-size: 14px;
                        color: var(--el-color-primary);
                        margin: auto;
                        display: block;
                        text-align: center;
                    }
                }
                .el-button {
                    width: 100%;
                    margin-top: 10px;
                    margin-left: 0px;
                }
            }
        }

        .paper-left-bottom {
            .el-divider--horizontal {
                margin: 15px 0px 20px 0px;
            }
            .el-button {
                height: 30px;
                width: 30px;
                padding: 0;
                border: 0;
                margin: 2px;
            }
            .paper-left-bottom-chapter {
                font-size: 13px;
                font-weight: bold;
                margin: 5px 0px;
                cursor: pointer;
            }
        }
    }

    
    :deep(.paper-right) {
        flex: 1;
        width: 800px;
        min-height: calc(100vh - 50px);
        margin-left: 10px;
        .question-paper {
            min-height: 200px;
        }
        .paper-right-opt {
            display: flex;
            align-items: center;
            margin: 15px 15px 0px 15px;
            padding-left: 15px;
            .el-checkbox {
                margin-left: 30px;
                margin-right: 0px;
            }
        }
        .el-divider--horizontal {
            width: initial;
            margin: 5px 20px 10px 20px;
        }
        .paper-right-my-rmk {
            display: flex;
            margin: 10px 20px;
            align-items: flex-end;
            .el-textarea__inner {
                box-shadow: initial;
            }
            .el-checkbox {
                margin: 0px 10px;
            }
        }
        .paper-right-rmk-list {
            padding: 0;
            margin: 0;
            list-style: none;
            overflow: auto;
            li {
                display: flex;
                flex-direction: column;
                min-height: 50px;
                background: var(--el-color-primary-light-9);
                margin: 10px 20px;
                padding: 10px;
                color: var(--el-text-color-regular);
                .el-text {
                    margin: 0px 0px 5px 0px;
                    align-self: initial;
                }
                div {
                    display: flex;
                    justify-content: space-between;
                    align-items: baseline;
                    font-size: 12px;
                    .el-text {
                        margin: 0px;
                    }
                    .iconfont {
                        cursor: pointer;
                        font-size: 14px;
                        &:hover {
                            color: var(--el-color-primary);
                        }
                    }
                }
            }
        }
    }
}
</style>
<style>
.popper-class {
    max-width: 800px;
}
</style>