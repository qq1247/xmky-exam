<template>
    <div class="question-editor">
        <div class="question-editor-top">
            <div class="question-editor-left-btn">
                <el-button type="primary" size="small" @click="emit('back')">返回</el-button>
                <el-button type="success" size="small" @click="showEg">{{egShow ? '返回编辑' : '查看示例'}}</el-button>
            </div>
            <div>
                <slot name="top-right"></slot>
                <span style="font-size: 14px;">共{{ questions.length }}题</span>
                <span style="color: #f56c6c;font-size: 14px;">&nbsp;&nbsp;错误{{ errNum }}题&nbsp;&nbsp;</span>
                <el-button type="danger" size="small" @click="locationErr">定位错误</el-button>
                <el-button type="success" size="small" @click="txtImport">导入</el-button>
            </div>
        </div>
        <div class="question-editor-bottom">
            <el-scrollbar max-height="calc(100vh - 250px)">
                <el-input 
                    v-model="txt" 
                    :autosize="{ minRows: 50, maxRows: 10000 }"
                    type="textarea" 
                    resize="none"
                    placeholder="建议一次编辑10道题，多次导入" />
            </el-scrollbar>
            <el-scrollbar max-height="calc(100vh - 250px)">
                <template v-for="(question, index) in questions" :key="index">
                    <el-alert v-if="question.errs" :title="`${index+1}、${question.errs}`" type="error" :closable="false"/>
                    <Question 
                        v-else
                        :no="index + 1" 
                        :type="question.type"
                        :markType="question.markType" 
                        :title="question.title"
                        :score="question.score" 
                        :answers="question.answers"
                        :options="question.options"
                        :user-answer-show="false"
                        :editable="false">
                    </Question>
                </template>
            </el-scrollbar>
        </div>
    </div>
</template>
    
<script lang="ts" setup>
import { computed, ref } from 'vue';
import Question from '@/components/question/Question.vue';
import { ElMessage } from 'element-plus';

// 定义变量
const emit = defineEmits(['back', 'txtImport'])
const txt = ref('') // 编辑器文本
const egShow = ref(false) // 是否显示示例
const txtBak = ref('') // 文本备份（用于切换示例）
const egtxt = ref(`1.这是一道单选题的题干，简单写法
A.单选题的A选项
B。单选题的B选项
C、单选题的C选项
D单选题的D选项
[B]

1.这是一道多选题的题干，可选写法
A.多选题的A选项，
B。多选题的B选项
C、多选题的C选项
D多选题的D选项
[AB][3分][1分]
[解析]中括号内【字母】表示答案，一个答案表示单选题，一个以上答案表示多选题
中括号内【数字分】表示该题分数，第一项为该题分数，不填默认1分，第二项为漏选分数，多选题有效，不填默认为总分一半

3、这是一道填空题_____，_____，简单写法
[北京 上海][主观题]

3、这是一道填空题________，________，可选写法
[山西 山西省 晋][老婆 媳妇 内人][2分][2分][答案无顺序][不区分大小写]
[解析]连续五个或五个以上的下划线，表示一个填空
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
默认为主观题，需要人工阅卷`)
const questions = computed(() => {
    // 文本解析为试题可读字段
    let questionTxts = txt2QuestionTxt(txt.value)
    return questionTxts.map((questionTxt) => {
        return parseQuestion(questionTxt)
    })
})
const errNum = computed(() => {// 错误题数
    return questions.value.reduce((total, cur) => {
        if(cur.errs) {
            total++
        }
        return total
    }, 0)
})

// 拆分文本，每段为一道试题文本
function txt2QuestionTxt(txt: string) {
    let singleQuestion = [] as string[] // 单题
    let questions = [] as string[][] // 单题列表
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
    let question = {
        type: 0,
        title: '',
        options: [] as string[],
        answers: [] as string[],
        score: 0,
        scores: [] as number[],
        analysis: '',
        markType: 0,
        markOptions: [] as number[],
        state: 1,
        no: 1,
        errs: '',
    }
    let optionIndexArr = [] as number[], answerIndex = -1, analysisIndex = -1
    questionTxt.forEach((txt, index) => {
        if (/^\[解析\]/.test(txt)) { // 已[解析]开头
            analysisIndex = index
        } else if (/^\[(?!解析)/.test(txt)) {// 已[号开头，并且不包含解析
            answerIndex = index
        } else if (/^[A-Za-z][.。、]?/.test(txt)) {// 已abcdefg中的一个开头，后面跟.。、或没有
            optionIndexArr.push(index)
        }
    })

    if (answerIndex === -1) {
        question.errs = `缺少答案：${questionTxt.join('')}`
        return question
    }
    if (optionIndexArr.length > 0) {// 如果存在（单多选）选项，并且选项在答案之后
        for (let optionIndex of optionIndexArr) {
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


    let type = 5; // 试题类型
    let title = questionTxt.slice(0, optionIndexArr.length > 0 ? optionIndexArr[0] : answerIndex).join("<br/>").replace(/^\d+[.。、]/, '') // 题干（换行转br标签）
    if (optionIndexArr.length > 0) {// 如果有选项
        type = /^\[[A-Ga-g]{2,}\]/.test(questionTxt[answerIndex]) ? 2 : 1 // 找到大于一个答案就是多选，否则单选（格式不对可能没有答案，后面处理）
    } else if (/_{5,}/.test(title)) { // 如果题干有连续大于等于五个的下划线，类型为填空
        type = 3
    } else if (/^\[[对错是否√×]\]/.test(questionTxt[answerIndex])) {// 答案行包含对错是否√×，类型为判断
        type = 4
    }

    let answerEndIndex = analysisIndex !== -1 ? analysisIndex : questionTxt.length // 有解析截取到解析，否则剩余内容都是
    let answerGroup = questionTxt.slice(answerIndex, answerEndIndex).join('<br/>').match(/\[(.+?)\]/g) // 按中括号拆分出答案、分数、阅卷选项
    if (!answerGroup) {
        question.errs = `不能从题干下一行中解析出答案、分数、阅卷选项：${questionTxt.join('')}`
        return question
    }

    let scores = [] as number[] // 分数组
    let answers = [] as string[] // 答案组
    let markOptions = [] as number[] // 阅卷选项组
    let subjective = false // 是否主观题
    answerGroup.forEach((answer) => {
        answer = answer.substring(1, answer.length - 1)
        if (/^([0-9]{1}|^[1-9]{1}\d{1,15})(\.\d{1,2})?分$/.test(answer)) {// 最多1到2位小数，超过不识别
            let s = parseFloat(answer.replace('分', ''))
            scores.push(s > 20 ? 20 : s) // 最大20分
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
    

    let options = [] // 选项组
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
            let optionIndex = optionContent.substring(0, 1).toUpperCase().charCodeAt(0) - 65
            optionContent = optionContent.replace(/^[A-Za-z][.。、]?/, '')
            options.push(optionContent)

            if (i !== optionIndex) {
                question.errs = `选项顺序错误：${questionTxt.join('')}`
                return question
            }
        }
        let optionContent = questionTxt.slice(optionIndexArr[optionIndexArr.length - 1], answerIndex).join("<br/>") // 最后一个选项从当前行到答案行之间的都是
        let optionIndex = optionContent.substring(0, 1).toUpperCase().charCodeAt(0) - 65
        optionContent = optionContent.replace(/^[A-Za-z][.。、]?/, '')
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

        for (let answer of answers[0].toUpperCase()) {
            if(answer.charCodeAt(0) - 64 > options.length) {
                question.errs = `答案超出可选范围：${questionTxt.join('')}`
                return question
            }
        }
    } else if (type === 3) {
        let matchs = title.match(/_{5,}/g)
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
                let answerCount = answers[0].split(/ +/).length // 多个空格按一个算
                if (fillBlanksCount !== answerCount) {
                    question.errs = `${ answerCount }个答案，${ fillBlanksCount }个填空：${questionTxt.join('')}`
                    return question
                }
            } else if (answers.length > 1 && fillBlanksCount === 1) {// 多个答案，一个填空错误；
                question.errs = `${ answers.length }个答案，${ fillBlanksCount }个填空：${questionTxt.join('')}`
                return question
            } else if (answers.length !== fillBlanksCount) {// 多个答案，多个填空，数量不等错误
                question.errs = `${ answers.length }个答案，${ fillBlanksCount }个填空：${questionTxt.join('')}`
                return question
            }
        }
    }

    // 拼装试题
    let analysis = analysisIndex === -1 ? '' : questionTxt.slice(analysisIndex, questionTxt.length - 1).join("<br/>").replace(/^\[解析]/, '')
    let markType = (type === 5 || (type === 3 && subjective)) ? 2 : 1  // 如果是问答题或（填空带主观题）设置为主观题，剩下是客观题
    let score = 1, _scores = []
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

    question.type = type
    question.title = title
    question.options = options
    question.answers = answers
    question.score = score
    question.scores = _scores
    question.analysis = analysis
    question.markType = markType
    question.markOptions = markOptions
    question.state = 1
    question.no = 1
    return question
}

// 显示示例
function showEg() {
    if (!egShow.value) {
        egShow.value = true
        txtBak.value = txt.value
        txt.value = egtxt.value
    } else {
        egShow.value = false
        txt.value = txtBak.value
    }
}

// 定位错误
function locationErr() {
    let elements = document.getElementsByClassName('el-alert')
    if (elements.length) {
        elements[0].scrollIntoView()
    }

}

// 文本导入
function txtImport() {
    if (errNum.value > 0) {
        ElMessage.error(`解析试题错误${errNum.value}处，请处理`)
        return
    }
    emit('txtImport', questions)
}
</script>
    
<style lang="scss" scoped>
.question-editor {
    flex: 1;
    display: flex;
    flex-direction: column;
    background-color: white;
    padding: 10px;
    .question-editor-top {
        display: flex;
        justify-content: space-between;
        padding-bottom: 5px;
    }
    .question-editor-bottom {
        flex: 1;
        display: flex;
        .el-scrollbar {
            flex: 1;
            border: 1px solid var(--el-border-color);
    
            :deep(.el-textarea__inner) {
                box-shadow: initial;
            }
        }
    }

}
</style>
  