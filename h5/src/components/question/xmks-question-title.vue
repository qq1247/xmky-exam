<template>
    <div class="xmks-question-title">
        <template v-if="type !== 3">
            <span v-html="titles[0].value"></span>
            <span>（{{ score }}分）</span>
        </template>
        <template v-else-if="type === 3 && !userAnswerShow && !answerShow">
            <template v-for="(title, index) in titles" :key="index">
                <span v-if="title.type === 'txt'" v-html="title.value"></span>
                <el-input v-else :key="index" modelValue="" :style="{ width: title.value.length * 14 + 'px' }"
                    :readonly="!editable" class="fillblank"></el-input>
            </template>
            <span>（{{ score }}分）</span>
        </template>
        <template v-else-if="type === 3 && answerShow">
            <template v-for="(title, index) in titles" :key="index">
                <span v-if="title.type === 'txt'" v-html="title.value"></span>
                <el-input v-else :key="index" :modelValue="answerShow ? (escape2Html(answers[title.index] || '')) : ''"
                    :style="{ width: title.value.length * 14 + 'px' }" :readonly="!editable"
                    class="fillblank fillblank--answer"></el-input>
            </template>
            <span>（{{ score }}分）</span>
        </template>
        <template v-else-if="type === 3 && userAnswerShow">
            <template v-for="(title, index) in titles" :key="index">
                <span v-if="title.type === 'txt'" v-html="title.value"></span>
                <el-input v-else :key="index" :modelValue="escape2Html(userAnswers[title.index] || '')"
                    :style="{ width: title.value.length * 14 + 'px' }" :readonly="!editable"
                    @input="(value: string) => { userAnswers[title.index] = value; emit('change', userAnswers) }"
                    class="fillblank fillblank--user-answer"></el-input>
            </template>
            <span>（{{ score }}分）</span>
        </template>
        <span v-if="userAnswerShow && userScore != null" class="iconfont score" :class="{
            'icon-lianxi-61 score--right': isRight,
            'icon-tubiaoziti2-01 score--wrong': isWrong,
            'icon-bandui score--half': isHalf,
        }">
            {{ userScore }}分
        </span>
    </div>
</template>

<script lang="ts" setup>
import { ref, computed, watch } from 'vue'
import { escape2Html } from '@/util/htmlUtil'


/************************变量定义相关***********************/
const emit = defineEmits<{
    (e: 'change', answers: string[]): void
}>()
const props = withDefaults(defineProps<{
    type: number // 试题类型
    title: string // 题干
    score?: number // 分数
    userScore?: number | null // 用户分数
    answers?: string[] // 标准答案
    userAnswers?: string[] // 用户答案
    editable?: boolean // 可编辑
    userAnswerShow?: boolean // 用户答案显示
    answerShow?: boolean // 标准答案显示
}>(), {
    editable: false,
    answers: () => [],
    userAnswers: () => [],
    userAnswerShow: true,
    answerShow: false,
})
const userAnswers = ref(props.userAnswers) // 用户答案
const userAnswerShow = ref(props.userAnswerShow) // 用户答案显示

/************************监听相关*****************************/
watch(() => props.userAnswerShow, () => {
    userAnswerShow.value = props.userAnswerShow
})
watch(() => props.userAnswers, () => {
    userAnswers.value = props.userAnswers
})

/************************计算属性相关*************************/
const isHalf = computed(() => props.userScore != null && props.userScore > 0 && props.userScore !== props.score); // 是否半对
const isRight = computed(() => props.userScore != null && props.userScore === props.score); // 是否答对
const isWrong = computed(() => props.userScore != null && props.userScore === 0); // 是否答错

const answers = computed(() => { // 标准答案
    if (props.type !== 3) {
        return props.answers
    }
    return props.answers.map((answer) => answer.replaceAll('\n', '|'))
})

const titles = computed(() => {// 题干
    // 如果不是填空题，正常返回
    if (props.type !== 3) {
        return [{
            type: 'txt',
            value: props.title?.replaceAll('\n', '<br/>'),
            index: -1,
        },]
    }

    // 如果是填空题，按下划线分割，用于转化为输入框
    let title = props.title.replaceAll('\n', '<br/>')// '______AAAAA_____BBBBB____________CCCCC__DDDDD______'
    const titles = []
    let pos = 0
    title.match(/[_]{5,}/g)?.forEach((value) => {
        const index = title.indexOf(value)
        if (index > 0) {
            titles.push({
                type: 'txt',
                value: title.substring(0, index),
                index: -1,
            })
        }
        titles.push({
            type: 'input',// 如果是填空题，上面循环的时候，把这节内容转为input
            value,
            index: pos++, // 如果是填空题，上面循环的时候，找对应答案的位置
        })
        title = title.substring(index + value.length)

        if (!userAnswers.value[pos - 1]) {// 没填答案则初始化一个空值（第一次填第二空，刷新会跑在第一空）
            userAnswers.value[pos - 1] = ''
        }
    })
    if (title) {
        titles.push({
            type: 'txt',
            value: title,
            index: -1,
        })
    }
    return titles
})

</script>

<style lang="scss">
.xmks-question-title {
    display: flex-line;
    font-size: 14px;
    color: #303133;
    line-height: 28px;
    word-break: break-all;

    .fillblank {
        .el-input__wrapper {
            position: relative;
            height: 38px;
            box-shadow: initial;
            border-radius: 10px 10px 10px 10px;

            &.is-focus {
                box-shadow: initial;
            }

            &::after {
                content: "";
                position: absolute;
                left: 0;
                bottom: 10px;
                width: 100%;
                height: 1px;
                background-color: #333333;
            }

            .el-input__inner {
                font-size: 14px;
                color: #303133;
            }
        }

        &.fillblank--answer {
            .el-input__wrapper {
                background: #D1F2D7;
            }
        }

        &.fillblank--user-answer {
            .el-input__wrapper {
                background: #E0F9FF;
            }
        }
    }

    .score {
        font-size: 16px;

        &.score--right {
            color: #4FBF63;
        }

        &.score--wrong {
            color: #FF0000;
        }

        &.score--half {
            color: #FF6960;
        }
    }
}
</style>