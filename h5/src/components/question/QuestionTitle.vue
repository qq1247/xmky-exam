<template>
    <div :id="`question-${props.no}`" class="question-title">
        <span>{{ no ? `${no}、` : '' }}</span>
        <template v-for="(title, index) in titles">
            <span v-if="title.type === 'txt'" v-html="title.value"></span>
            <el-input 
                v-else 
                :key="index" 
                :modelValue="userAnswerShow ? (userAnswers[title.index] || '') : (answers[title.index] || '')" 
                :style="{ width: title.value.length * 18 + 'px' }"
                :readonly="!editable"
                @update:modelValue="(value: string) => userAnswers[title.index] = value"
                @input="(value: string) => emit('change', userAnswers)" 
                ></el-input>
        </template>
        <span>（{{ score }}分）</span>
        <span 
            v-if="errShow" 
            :class="`iconfont ${score === userScore ? 'icon-duigoux' : userScore === 0 ? 'icon-cuo' : 'icon-bandui'}`" 
            :style="`color: var(${score === userScore ? '--el-color-success' : '--el-color-error'});`"
            >{{ userScore }}分
        </span>
    </div>
</template>
  
<script lang="ts" setup>
import { ref, computed, watchEffect, watch } from 'vue'

// 定义变量
const emit = defineEmits<{
    (e: 'change', answers: string[]): void
}>()
const props = withDefaults(defineProps<{
    no?: string | number // 题号
    editable?: boolean // 可编辑（true：是；false：否）
    type: number // 试题类型
    title: string // 题干
    score: number // 分数
    answers?: string[] // 标准答案
    userAnswers?: string[] // 用户答案
    userScore?: number // 用户分数
    userAnswerShow?: boolean // 用户答案显示（true：用户答案；false：标准答案）
    errShow?: boolean // 错误显示（true：标记错误；false：不显示）
}>(), {
    editable: false,
    answers: () => [], 
    userAnswers: () => [],
    userAnswerShow: true,
    errShow: false,
})
const answers = ref(props.answers) // 标准答案
const userAnswers = ref(props.userAnswers) // 用户答案
const userAnswerShow = ref(props.userAnswerShow) // 用户答案显示

// 监听属性
watch(() => props.userAnswerShow, () => {
    userAnswerShow.value = props.userAnswerShow
})
watch(() => props.userAnswers, () => {
    userAnswers.value = props.userAnswers
})

// 监听
watchEffect(() => {
    answers.value = props.answers.map((answer) => {
        if (props.type !== 3) {
            return answer
        }
        return answer.replaceAll('\n', '、')
    })
})

// 计算属性
const titles = computed(() => {
    // 如果不是填空题，正常返回
    if (props.type !== 3) {
        return [{
            type: 'txt',
            value: props.title,
            index: -1,
        },]
    }

    // 如果是填空题，按下划线分割，用于转化为输入框
    let title = props.title// '______AAAAA_____BBBBB____________CCCCC__DDDDD______'
    let titles = []
    let pos = 0
    title.match(/[_]{5,}/g)?.forEach((value) => {
        let index = title.indexOf(value)
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
.question-title {
    font-size: 13px;
    font-weight: bold;
    color: var(--el-text-color-primary);
    padding-bottom: 10px;
    word-break: break-all;

    .el-input {
        .el-input__wrapper {
            box-shadow: initial
        }
        &.is-disabled {
            .el-input__wrapper {
                background-color: white;
                box-shadow: initial;
            }
        }

        .el-input__inner {
            height: 24px;
            border: none;
            border-radius: 0;
            background-color: transparent;
            border-bottom: 1px solid var(--el-text-color-secondary);
            text-align: center;
        }
    }
}
</style>
  