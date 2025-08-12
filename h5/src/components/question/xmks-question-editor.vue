<template>
    <div class="xmks-question-editor">
        <div class="xmks-question-editor__head">
            <div class="editor-opt">
                <el-button v-if="toolbars.egShow" type='' class="editor-opt__btn" @click="egShow">
                    <span class="iconfont icon-bianjibanli-93 editor-opt__btn-icon"></span>
                    <span class="editor-opt__btn-txt">返回编辑</span>
                </el-button>
                <el-button v-else type='' class="editor-opt__btn" @click="egShow">
                    <span class="iconfont icon-yangli editor-opt__btn-icon"></span>
                    <span class="editor-opt__btn-txt">查看样例</span>
                </el-button>
            </div>
            <div class="editor-toolbar">
                <div class="editor-toolbar__tip">
                    共{{ questions.length }}题 错误
                    <span class="editor-toolbar__tip-warn">{{ errNum }}</span>题
                </div>
                <el-button type='' class="editor-toolbar__btn" @click="locationErr">
                    <span class="iconfont icon-dingwei editor-toolbar__btn-icon"></span>
                    <span class="editor-toolbar__btn-txt">定位错误</span>
                </el-button>
                <el-button type='' class="editor-toolbar__btn "
                    :class="{ 'editor-toolbar__btn--active': toolbars.answerShow }"
                    @click="toolbars.answerShow = !toolbars.answerShow">
                    <span class="iconfont icon-icon-03 editor-toolbar__btn-icon"></span>
                    <span class="editor-toolbar__btn-txt">{{ toolbars.answerShow ? '隐藏标准答案' : '显示标准答案' }}</span>
                </el-button>
                <el-button type='' class="editor-toolbar__btn"
                    :class="{ 'editor-toolbar__btn--active': toolbars.analysisShow }"
                    @click="toolbars.analysisShow = !toolbars.analysisShow">
                    <span class="iconfont icon-icon-06 editor-toolbar__btn-icon"></span>
                    <span class="editor-toolbar__btn-txt">{{ toolbars.analysisShow ? '隐藏解析' : '显示解析' }}</span>
                </el-button>
                <el-button type='' class="editor-toolbar__btn"
                    :class="{ 'editor-toolbar__btn--active': toolbars.markOptionShow }"
                    @click="toolbars.markOptionShow = !toolbars.markOptionShow">
                    <span class="iconfont icon-icon-05 editor-toolbar__btn-icon"></span>
                    <span class="editor-toolbar__btn-txt">{{ toolbars.markOptionShow ? '隐藏阅卷选项' : '显示阅卷选项' }}</span>
                </el-button>
            </div>
        </div>
        <div class="xmks-question-editor__main">
            <el-scrollbar max-height="calc(100vh - 370px)" class="edit-area">
                <el-input ref="txtRef" v-model="txt" :autosize="{ minRows: 50, maxRows: 10000 }" type="textarea"
                    resize="none" maxlength="5000" :readonly="toolbars.egShow" class="edit-area__input "
                    @paste="handlePaste" />
                <div v-if="!txt.length" class="edit-area__tip">
                    <span class="iconfont icon-bianjibanli-93 edit-area__tip-icon"></span>
                    <span class="edit-area__tip-title">快去编辑试题吧！</span>
                    <span class="edit-area__tip-desc">点击左上角“查看样例”预览效果</span>
                </div>
            </el-scrollbar>
            <el-scrollbar max-height="calc(100vh - 370px)" class="review-area">
                <template v-for="(question, index) in questions" :key="index">
                    <el-alert v-if="question.errs" :title="`${index + 1}、${question.errs}`" type="error"
                        :closable="false" />
                    <xmks-question v-else :type="question.type" :title="question.title" :img-ids="question.imgFileIds"
                        :video-id="question.videoFileId" :options="question.options" :answers="question.answers"
                        :markType="question.markType" :score="question.score" :scores="question.scores"
                        :analysis="question.analysis" :userAnswers="[]" :userScore="0"
                        :answer-show="toolbars.answerShow" :user-answer-show="false"
                        :analysisShow="toolbars.analysisShow" :display="'paper'" :editable="false">
                        <template #title-pre>{{ index + 1 }}、</template>
                        <template #foot>
                            <div v-if="toolbars.markOptionShow" class="mark-option">
                                <div class="mark-option__title">阅卷选项</div>
                                <template v-if="question.type === 1 || question.type === 2
                                    || question.type === 4 || (question.type === 5 && question.markType === 2)">
                                    <span class="mark-option__txt">本题</span>
                                    <el-input-number v-model="question.score" :min="0.5" :max="20" :precision="2"
                                        controls-position="right" class="mark-option__input-number"
                                        :readonly="true" /><!-- 用blur事件，输入字母或删除数字不触发change事件 -->
                                    <span class="mark-option__txt">分</span>
                                </template>
                                <template v-if="question.type === 2">
                                    <span class="mark-option__txt">，漏选</span>
                                    <el-input-number v-model="question.scores[0]" :min="0" :max="20" :precision="2"
                                        controls-position="right" class="mark-option__input-number"
                                        :readonly="true"></el-input-number>
                                    <span class="mark-option__txt">分</span>
                                </template>
                                <template
                                    v-if="question.type === 3 || (question.type === 5 && question.markType === 1)">
                                    <template v-for="(score, index) of question.scores" :key="index">
                                        {{ index > 0 ? "，" : "" }}
                                        <span class="mark-option__txt">
                                            第{{ index + 1 }}{{ question.type === 3 ? '空' : '关键词' }}
                                        </span>
                                        <el-input-number v-if="question.scores" v-model="question.scores[index]"
                                            :min="0.5" :max="20" :precision="2" class="mark-option__input-number"
                                            :readonly="true"></el-input-number>
                                        <span class="mark-option__txt">分</span>
                                    </template>
                                    <el-checkbox-group v-model="question.markOptions" style="width:300px">
                                        <el-tooltip v-if="question.markType === 1 && question.type === 3"
                                            content="默认答案有顺序">
                                            <el-checkbox :value="2" class="checkbox">答案无顺序</el-checkbox>
                                        </el-tooltip>
                                        <el-tooltip v-if="question.markType === 1" content="默认区分大小写">
                                            <el-checkbox :value="3" class="checkbox">不分大小写</el-checkbox>
                                        </el-tooltip>
                                    </el-checkbox-group>
                                </template>
                            </div>
                        </template>
                    </xmks-question>
                </template>
            </el-scrollbar>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { computed, nextTick, reactive, ref, watch } from 'vue'
import XmksQuestion from '@/components/question/xmks-question.vue'
import type { Question } from '@/ts/exam/question'
import { ElMessage } from 'element-plus';
import axios from 'axios';
import http from "@/request"
import { useUserStore } from '@/stores/user';

/************************变量定义相关***********************/
const emit = defineEmits<{// 定义事件
    (e: 'change', questions: Question[]): void
}>()
defineExpose({ validate })// 定义暴露函数

const toolbars = reactive({// 工具栏状态
    answerShow: false,// 答案显示
    analysisShow: false,// 解析显示
    markOptionShow: false,// 阅卷选项显示
    egShow: false,// 示例显示
})

const userScore = useUserStore()// 用户信息

const txtRef = ref()// 编辑器文本引用
const txt = ref('') // 编辑器文本
const txtBak = ref('') // 文本备份（用于切换示例）
const txtEg = ref(`1.这是一道单选题的题干，简单写法
A.单选题的A选项
B。单选题的B选项
C、单选题的C选项
D、单选题的D选项
[B]

1.这是一道多选题的题干，可选写法，
题干可换行。
A.多选题的A选项，
B。多选题的B选项
C、多选题的C选项
D、多选题的D选项
[AB][3分][1分]
[解析]字母+点（句号|顿号）表示【选项】，【选项】前面为【题干】，【选项】后面为【答案分数】，在后面为【解析】
【题干】：1、允许换行；2、复制（ctrl+v）图片到到题干最后一行，会显示图片，最多4张，单张图片不大于2兆；3：支持代码高亮，格式和markdown保持一致，如题干插入片段\`\`\`js\nalert('hello world'); \n\`\`\`
【答案分数】：第一项为答案；第二项为分数（不填默认1分），第三项为漏选分数（多选题有效，不填默认为总分一半）
【解析】：试题解析，练习或考后查阅

3、这是一道填空题_____，_____，简单写法
[北京 上海][主观题]

3、这是一道填空题________，____________，可选写法
[山西 山西省 晋][老婆 媳妇 内人][2分][2分][答案无顺序][不区分大小写]
[解析]连续五个或五个以上的下划线，表示一个填空，下划线越长，填空越长
中括号内【主观题】表示这道题需要人工阅卷，[答案无顺序][不区分大小写]无效
中括号内【答案无顺序】【不区分大小写】，表示智能阅卷时判分规则，默认答案有顺序，区分大小写
中括号内【数字分】表示该题分数，一个空对应一个分数，不填或少填，对应的空默认为1分
中括号内【文字】表示答案，n个填空就有n个答案，用空格分割表示。如：[北京 上海]
中括号内【文字】表示答案，如果填空有多个备选答案，则用多组答案表示，一组答案表示对应填空的答案，一个答案内用空格分割表示多个备选答案。如：[山西 山西省 晋][老婆 媳妇 内人]

4、这是一道判断题的题干，简单写法
[对]

4、这是一道判断题的题干，可选写法
[√][2分]
[解析]中括号内【文字】表示答案，可填“对错√×是否”
中括号内【数字分】表示该题分数，不填默认1分

5、这是一道问答题的题干，简单写法
[我是问答题的答案]

5、这是一道问答题的题干，可选写法
[我是问答题的答案，
可换行
换行][10分]
[解析]中括号内【文字】表示答案
中括号内【数字分】表示该题分数，不填默认1分
默认为主观题，需要人工阅卷
`)// 最后一行需要为空行

/************************监听相关*****************************/
watch(txt,// 试题文本变化并格式正确时，触发change事件
    () => {
        if (!toolbars.egShow && questions.value.length > 0 && errNum.value === 0) {
            emit('change', questions.value)
        }
    }
);

/************************计算属性相关*************************/
const questions = computed(() => {// 文本解析为试题可读字段
    const questionTxts = txt2QuestionTxt(txt.value)
    if (questionTxts.length === 1 && questionTxts[0][0] === '') {
        return []
    }
    return questionTxts.map((questionTxt) => {
        return parseQuestion(questionTxt)
    })
})
const errNum = computed(() => {// 错误题数
    return questions.value.reduce((total, cur) => {
        if (cur.errs) {
            total++
        }
        return total
    }, 0)
})

/************************事件相关*****************************/
// 拆分文本，每段为一道试题文本
function txt2QuestionTxt(txt: string) {
    let singleQuestion: string[] = [] // 单题
    const questions: string[][] = []  // 单题列表
    txt.split(/\n/).forEach((curRowTxt) => {// 文本按回车分割
        if (/^\d+[.。、]/.test(curRowTxt)) {// 如果已数字开头，后跟.。、号开头
            if (singleQuestion.length > 0) {
                questions.push(singleQuestion)// 解析为一道题
                singleQuestion = []
            }
        }
        singleQuestion.push(curRowTxt)// 当前行文本存入单题
    })

    questions.push(singleQuestion) // 最后一行处理
    return questions
}

// 解析试题
function parseQuestion(questionTxt: string[]) {
    // 拆分字段并校验格式
    const question: Question = {
        type: 0,
        title: '',
        options: [],
        markType: 0,
        markOptions: [],
        score: 0,
        answers: [],
        scores: [],
        analysis: '',
        errs: '',
    }
    const optionIndexArr: number[] = []
    let answerIndex = -1
    let analysisIndex = -1
    questionTxt.forEach((txt, index) => {
        if (/^\[解析\]/.test(txt)) { // 已[解析]开头
            analysisIndex = index
        } else if (/^\[(?!解析)/.test(txt)) {// 已[号开头，并且不包含解析
            answerIndex = index
        } else if (/^[A-Za-z][.。、]/.test(txt)) {// 已abcdefg中的一个开头，后面跟.。、或没有
            optionIndexArr.push(index)
        }
    })

    if (answerIndex === -1) {
        question.errs = `缺少答案：${questionTxt.join('')}`
        return question
    }
    if (optionIndexArr.length > 0) {// 如果存在（单多选）选项，并且选项在答案之后
        for (const optionIndex of optionIndexArr) {
            if (optionIndex >= answerIndex) {
                question.errs = `选项 和 答案 顺序错误：${questionTxt.join('')}`
                return question
            }
        }
    }
    if (analysisIndex !== -1 && answerIndex > analysisIndex) {// 如果存在解析，并且答案在解析之后
        question.errs = `答案 和 解析 顺序错误：${questionTxt.join('')}`
        return question
    }


    let type = 5; // 试题类型，默认为问答
    const title = questionTxt.slice(0, optionIndexArr.length > 0 ? optionIndexArr[0] : answerIndex).join("\n").replace(/^\d+[.。、]/, '') // 题干（换行用\n）
    if (optionIndexArr.length > 0) {// 如果有选项
        type = /^\[[A-Ga-g]{2,}\]/.test(questionTxt[answerIndex]) ? 2 : 1 // 找到大于一个答案就是多选，否则单选（格式不对可能没有答案，后面处理）
    } else if (/_{5,}/.test(title)) { // 如果题干有连续大于等于五个的下划线，类型为填空
        type = 3
    } else if (/^\[[对错是否√×]\]/.test(questionTxt[answerIndex])) {// 答案行包含对错是否√×，类型为判断
        type = 4
    }

    const answerEndIndex = analysisIndex !== -1 ? analysisIndex : questionTxt.length // 有解析截取到解析，否则剩余内容都是
    const answerGroup = questionTxt.slice(answerIndex, answerEndIndex).join('<br/>').match(/\[(.+?)\]/g) // 按中括号拆分出答案、分数、阅卷选项
    if (!answerGroup) {
        question.errs = `不能从题干下一行中解析出答案、分数、阅卷选项：${questionTxt.join('')}`
        return question
    }

    const scores: number[] = [] // 分数组
    let answers: string[] = []  // 答案组
    let markOptions: number[] = []  // 阅卷选项组
    let subjective = false // 是否主观题
    answerGroup.forEach((answer) => {
        answer = answer.substring(1, answer.length - 1)
        if (/^([0-9]{1}|^[1-9]{1}\d{1,15})(\.\d{1,2})?分$/.test(answer)) {// 最多1到2位小数，超过不识别
            scores.push(Math.min(parseFloat(answer.replace('分', '')), 20)) // 最大20分
        } else if (/^答案无顺序$/.test(answer)) {
            markOptions.push(2)
        } else if (/^不区分大小写$/.test(answer)) {
            markOptions.push(3)
        } else if (/^主观题$/.test(answer)) {
            subjective = true
        } else {
            answers.push(answer) // 剩下都归答案
        }
    })

    const options = [] // 选项组
    let fillBlanksCount = 0 // 填空数量
    if (type === 1 || type === 2) {// 如果是单多选
        if (type === 2 && scores.length > 1) {// 如果是多选并且有第二个分数
            if (scores[0] <= scores[1]) {
                question.errs = `漏选分值不能大于总分：${questionTxt.join('')}`
                return question
            }
        }
        if (optionIndexArr.length < 2) {
            question.errs = `最少2个选项：${questionTxt.join('')}`
            return question
        }
        if (optionIndexArr.length > 7) {
            question.errs = `最多7个选项：${questionTxt.join('')}`
            return question
        }
        for (let i = 0; i < optionIndexArr.length - 1; i++) {
            let optionContent = questionTxt.slice(optionIndexArr[i], optionIndexArr[i + 1]).join("<br/>") // 回车行转br标签
            const optionIndex = optionContent.substring(0, 1).toUpperCase().charCodeAt(0) - 65
            optionContent = optionContent.replace(/^[A-Za-z][.。、]/, '')
            options.push(optionContent)

            if (i !== optionIndex) {
                question.errs = `选项顺序错误：${questionTxt.join('')}`
                return question
            }
        }
        let optionContent = questionTxt.slice(optionIndexArr[optionIndexArr.length - 1], answerIndex).join("<br/>") // 最后一个选项从当前行到答案行之间的都是
        const optionIndex = optionContent.substring(0, 1).toUpperCase().charCodeAt(0) - 65
        optionContent = optionContent.replace(/^[A-Za-z][.。、]/, '')
        options.push(optionContent)
        if (optionIndexArr.length - 1 !== optionIndex) {
            question.errs = `选项顺序错误：${questionTxt.join('')}`
            return question
        }

        if (!/^\[[A-Ga-g]+\]/.test(questionTxt[answerIndex])) {// 如果答案不是abcdefg中的一个或多个
            question.errs = `答案超出范围：${questionTxt.join('')}`
            return question
        }

        if (answers[0].length !== new Set(answers[0].toUpperCase()).size) {
            question.errs = `答案重复：${questionTxt.join('')}`
            return question
        }

        for (const answer of answers[0].toUpperCase()) {
            if (answer.charCodeAt(0) - 64 > options.length) {
                question.errs = `答案超出可选范围：${questionTxt.join('')}`
                return question
            }
        }
    } else if (type === 3) {
        const matchs = title.match(/_{5,}/g)
        if (matchs) {
            fillBlanksCount = matchs.length
            if (fillBlanksCount > 7) {
                question.errs = `填空数量不能大于7个：${questionTxt.join('')}`
                return question
            }

            if (answers.length === 0) {// 没有答案，报错
                question.errs = `填空没有答案：${questionTxt.join('')}`
                return question
            }
            if (answers.length === 1 && fillBlanksCount === 1) {// 一个答案一个填空，不处理

            } else if (answers.length === 1 && fillBlanksCount > 1) {// 一个答案，多个填空，答案按空格分隔，数量不等错误
                const answerCount = answers[0].split(/ +/).length // 多个空格按一个算
                if (fillBlanksCount !== answerCount) {
                    question.errs = `${answerCount}个答案，${fillBlanksCount}个填空：${questionTxt.join('')}`
                    return question
                }
            } else if (answers.length > 1 && fillBlanksCount === 1) {// 多个答案，一个填空错误；
                question.errs = `${answers.length}个答案，${fillBlanksCount}个填空：${questionTxt.join('')}`
                return question
            } else if (answers.length !== fillBlanksCount) {// 多个答案，多个填空，数量不等错误
                question.errs = `${answers.length}个答案，${fillBlanksCount}个填空：${questionTxt.join('')}`
                return question
            }
        }
    }

    // 拼装试题
    const analysis = analysisIndex === -1 ? '' : questionTxt.slice(analysisIndex, questionTxt.length).join("\n").replace(/^\[解析]/, '')
    const markType = (type === 5 || (type === 3 && subjective)) ? 2 : 1  // 如果是问答题或（填空带主观题）设置为主观题，剩下是客观题
    let score = 1
    const _scores = []
    if (type === 1) {// 单选
        score = scores.length > 0 ? scores[0] : 1 // 如果有则使用，没有默认为1分
        answers.length = 1 // 去除多余答案
        answers[0] = answers[0].toUpperCase()
        markOptions = []
    } else if (type === 4) {// 判断
        score = scores.length > 0 ? scores[0] : 1 // 如果有则使用，没有默认为1分
        answers.length = 1 // 去除多余答案
        answers[0] = answers[0].replace(/^[是√]/, '对').replace(/[否×]/, '错')
        markOptions = []
    } else if (type === 5) {// 问答
        score = scores.length > 0 ? scores[0] : 1 // 如果有则使用，没有默认为1分
        answers.length = 1 // 去除多余答案
        answers[0] = answers[0].replace(/<br\/>/g, '\n')
        markOptions = []
    } else if (type === 2) {// 多选
        score = scores.length > 0 ? scores[0] : 1
        _scores.push(scores.length > 1 ? scores[1] : score / 2) // 如果有第二个分数则使用，没有默认为总分一半
        answers = answers[0].toUpperCase().split("") // 答案拆分，满足接口
        markOptions = []
    } else if (type === 3) {// 填空
        score = 0;
        for (let i = 0; i < fillBlanksCount; i++) {
            _scores.push(scores[i] ? scores[i] : 1)
            score += _scores[i]
        }

        if (answers.length === 1 && fillBlanksCount > 1) {// 一个答案，多个填空，答案按空格分隔
            answers = answers[0].split(/ +/)
        } else {// 其他情况
            answers = answers.map(answer => {
                return answer.replace(/ +/g, '\n')
            })
        }

        if (markType === 2) {
            markOptions = [] // 主观题没有阅卷选项
        }
    }

    const imgFileIds: number[] = []
    let _title = title.replace(/\[图片:(\d+)]/g, (_, num) => {
        imgFileIds.push(parseInt(num) ^ 0x55AA)
        return ''
    });
    const videoFileIds: number[] = []
    _title = _title.replace(/\[视频:(\d+)]/g, (_, num) => {
        videoFileIds.push(parseInt(num) ^ 0x55AA)
        return ''
    });

    question.type = type
    question.title = _title.replace(/(\r\n|\n|\r)$/, '') //剔除图片后去掉末尾的换行
    question.imgFileIds = imgFileIds.slice(0, 4) // 最多4张图片
    question.videoFileId = videoFileIds.slice(0, 1)[0] || null // 最多一个视频

    question.options = options
    question.answers = answers
    question.score = score
    question.scores = _scores
    question.analysis = analysis
    question.markType = markType
    question.markOptions = markOptions
    return question
}

// 示例显示
function egShow() {
    if (!toolbars.egShow) {
        toolbars.egShow = true
        txtBak.value = txt.value
        txt.value = txtEg.value
    } else {
        toolbars.egShow = false
        txt.value = txtBak.value
    }
}

// 定位错误
function locationErr() {
    const elements = document.getElementsByClassName('el-alert')
    if (elements.length) {
        elements[0].scrollIntoView()
    }
}

// 数据校验
function validate(): { succ: boolean; msg: string, data?: Question[] } {
    return {
        succ: errNum.value === 0,
        msg: `共编辑${questions.value.length}题，其中格式错误${errNum.value}题，请检查`,
        data: errNum.value === 0 ? questions.value : []
    }
}

async function handlePaste(e: ClipboardEvent) {
    // 检查是否有附件粘贴事件
    const items = e.clipboardData?.items
    if (!items) return

    // 上传图片
    const imageItems = Array.from(items).filter(item => item.type.includes('image'))
    if (imageItems.length) {
        e.preventDefault()
        for (const item of imageItems) {
            const file = item.getAsFile()
            if (!file) continue

            try {
                const attachmentId = await uploadImage(file)

                // 插入附件标记
                if (attachmentId) {
                    insertAtCursor(`[图片:${attachmentId ^ 0x55AA}]`)
                }

            } catch (e) {
                ElMessage.error(e instanceof Error ? e.message : '上传图片异常')
            }
        }
    }

    // 上传视频
    const videoItems = Array.from(items).filter(item => item.type.includes('video/mp4'))
    if (videoItems.length) {
        e.preventDefault()
        for (const item of videoItems) {
            const file = item.getAsFile()
            if (!file) continue

            try {
                const attachmentId = await uploadVideo(file)

                // 插入附件标记
                if (attachmentId) {
                    insertAtCursor(`[视频:${attachmentId ^ 0x55AA}]`)
                }

            } catch (e) {
                ElMessage.error(e instanceof Error ? e.message : '上传视频异常')
            }
        }
    }
}

// 在光标位置插入文本
const insertAtCursor = (imgSymbol: string) => {
    const textarea = txtRef.value?.textarea
    if (!textarea) return

    const startPos = textarea.selectionStart
    const endPos = textarea.selectionEnd

    txt.value = txt.value.substring(0, startPos) + imgSymbol + txt.value.substring(endPos)

    // 更新光标位置
    nextTick(() => {
        textarea.selectionStart = textarea.selectionEnd = startPos + imgSymbol.length
        textarea.focus()
    })
}

// 图片上传
async function uploadImage(file: File) {
    const MAX_SIZE = 2 * 1024 * 1024;
    if (file.size > MAX_SIZE) {
        ElMessage.error('图片最大2兆');
        return null;
    }

    const formData = new FormData()
    formData.append('files', file)

    try {
        const response = await axios.post(`${http.defaults.baseURL}file/upload`,
            formData,
            {
                headers: {
                    'Authorization': userScore.accessToken,
                    'Content-Type': 'multipart/form-data'
                }
            })
        return response.data.data.fileIds
    } catch (e) {
        ElMessage.error(e instanceof Error ? e.message : '上传图片异常')
    }
}

// 视频上传
async function uploadVideo(file: File) {
    const MAX_SIZE = 10 * 1024 * 1024;
    if (file.size > MAX_SIZE) {
        ElMessage.error('视频最大10兆');
        return null;
    }

    const formData = new FormData()
    formData.append('files', file)

    try {
        const response = await axios.post(`${http.defaults.baseURL}file/upload`,
            formData,
            {
                headers: {
                    'Authorization': userScore.accessToken,
                    'Content-Type': 'multipart/form-data'
                }
            })
        return response.data.data.fileIds
    } catch (e) {
        ElMessage.error(e instanceof Error ? e.message : '上传视频异常')
    }
}

</script>

<style lang="scss" scoped>
.xmks-question-editor {
    flex: 1;
    border: 1px solid #E5E5E5;
    background-color: #fff;

    .xmks-question-editor__head {
        display: flex;
        justify-content: space-between;
        border-bottom: 1px solid #E5E5E5;

        .editor-opt {
            display: flex;
            justify-self: flex-end;
            align-items: center;
            height: 50px;

            .editor-opt__btn {
                padding: 0px 0px;
                margin: 0px 20px;
                border: 0px;

                &:hover {
                    color: #999999;
                }

                .editor-opt__btn-icon {
                    font-size: 16px;
                }

                .editor-opt__btn-txt {
                    margin-left: 2px;
                    font-size: 14px;
                    color: #999999;
                    line-height: 45px;
                }
            }
        }

        .editor-toolbar {
            display: flex;
            justify-self: flex-end;
            align-items: center;
            height: 50px;
            margin: 0px 10px;

            .editor-toolbar__tip {
                font-size: 14px;
                color: #999999;
                line-height: 45px;

                .editor-toolbar__tip-warn {
                    color: #E43D33;
                }
            }

            .editor-toolbar__btn {
                padding: 0px 0px;
                margin: 0px 10px;
                border: 0px;
                position: relative;

                &:hover {
                    color: #999999;
                }

                &::after {
                    content: '';
                    position: absolute;
                    width: 1px;
                    height: 20px;
                    background-color: #CCCCCC;
                    right: -10px;
                    top: 50%;
                    transform: translateY(-50%);
                }

                &:last-child::after {
                    display: none;
                }

                .editor-toolbar__btn-icon {
                    font-size: 16px;
                }

                .editor-toolbar__btn-txt {
                    margin-left: 2px;
                    font-size: 14px;
                    color: #999999;
                    line-height: 45px;
                }

                &.editor-toolbar__btn--active {
                    .editor-toolbar__btn-icon {
                        color: #1EA1EE;
                    }

                    .editor-toolbar__btn-txt {
                        color: #1EA1EE;
                    }
                }
            }
        }
    }

    :deep(.xmks-question-editor__main) {
        display: flex;

        .edit-area {
            flex: 1;
            position: relative;
            border-right: 1px solid #E5E5E5;

            .edit-area__input {
                .el-textarea__inner {
                    &:focus {
                        box-shadow: initial;
                    }

                    &:hover {
                        box-shadow: initial;
                    }

                    font-size: 16px;
                    color: #303133;
                    box-shadow: initial;
                    line-height: 30px;
                }
            }

            .edit-area__tip {
                display: flex;
                flex-direction: column;
                align-items: center;

                position: absolute;
                top: 240px;
                left: 50%;
                transform: translate(-50%, -50%);
                z-index: 1;

                .edit-area__tip-icon {
                    font-size: 52px;
                    color: #e5e5e5;
                }

                .edit-area__tip-title {
                    margin-top: 20px;
                    font-size: 16px;
                    color: #303133;
                    line-height: 45px;
                }

                .edit-area__tip-desc {
                    font-size: 14px;
                    color: #999999;
                    line-height: 45px;
                }
            }
        }

        .review-area {
            flex: 1;
            padding: 0px 20px 0px 20px;
            border-left: 1px solid #E5E5E5;

            .mark-option {
                margin-top: 10px;
                padding: 10px 20px 5px 20px;
                position: relative;
                border: 1px solid #E5E5E5;

                .mark-option__title {
                    position: absolute;
                    left: 15px;
                    top: -8px;
                    padding: 0px 10px;
                    background-color: #FFFFFF;
                    font-size: 14px;
                    color: #999999;
                }

                .mark-option__txt {
                    font-size: 14px;
                    color: #999999;
                    line-height: 45px;
                }

                .mark-option__input-number {
                    position: relative;
                    width: 40px;

                    .el-input-number__decrease,
                    .el-input-number__increase {
                        display: none;
                    }

                    .el-input__wrapper {
                        padding: 0px;
                        box-shadow: initial;
                        border-radius: 0;
                    }

                    &::after {
                        content: "";
                        position: absolute;
                        left: 0;
                        bottom: 5px;
                        width: 100%;
                        height: 1px;
                        background-color: #333333;
                    }
                }
            }
        }
    }
}
</style>
