<template>
    <!-- 列表样式 -->
    <el-card v-if="display === 'list'" class="question-list" shadow="hover">
        <div class="question-list-top">
            <span>{{ no }}、</span>
            <div class="question-list-top-title" v-html="title"></div>
        </div>
        <div class="question-list-bottom">
            <el-tag size="small" type="" round effect="light">
                {{ dictStore.getValue('QUESTION_TYPE', type) }}
            </el-tag>
            <el-tag effect="plain" size="small" type="success" round>
                {{ dictStore.getValue('PAPER_MARK_TYPE', markType) }}
            </el-tag>
            <el-tag effect="plain" size="small" type="danger" round>
                {{ score }}分
            </el-tag>
            <el-tag effect="plain" size="small" type="warning" round>
                {{ updateUserName }}
            </el-tag>
        </div>
        <div class="question-list-bottom-right">
            <slot name="bottom-right"></slot>
        </div>
    </el-card>
    <!-- 试卷样式 -->
    <div v-else-if="display === 'paper'" class="question-paper">
        <!-- 题干 -->
        <QuestionTitle 
            :no="no" 
            :editable="editable"
            :type="type" 
            :title="title" 
            :score="score" 
            :answers="answers"
            :userAnswers="userAnswers"
            :userScore="userScore"
            :userAnswerShow="userAnswerShow"
            :errShow="errShow"
            @change="(value: string[]) => emit('change', value)"
            />
        <!-- 单选题选项 -->
        <el-radio-group 
            v-if="type === 1" 
            :modelValue="userAnswerShow ? (userAnswers[0] || '') : (answers[0] || '')"
            @change="(value: string) => emit('change', [value])"
            @update:modelValue="(value: string) => userAnswers[0] = value"
            :disabled="!editable"
            >
            <el-radio v-for="(option, index) in options" :key="index" :label="`${optionLabs[index]}`">
                <div v-html="`${optionLabs[index]}、${option}`" :style="{ color: errColor(optionLabs[index]) }"/>
            </el-radio>
        </el-radio-group>
        <!-- 多选题选项 -->
        <el-checkbox-group 
            v-else-if="type === 2" 
            :modelValue="userAnswerShow ? userAnswers : answers"
            @update:modelValue="(value: string[]) => userAnswers = value"
            @change="(value: string[]) => emit('change', value)" 
            :disabled="!editable"
            >
            <el-checkbox v-for="(option, index) in options" :key="index" :label="`${optionLabs[index]}`">
                <div v-html="`${optionLabs[index]}、${option}`" :style="{ color: errColor(optionLabs[index]) }"/>
            </el-checkbox>
        </el-checkbox-group>
        <!-- 填空题（题干区域） -->
        <!-- 判断题选项 -->
        <el-radio-group 
            v-else-if="type === 4" 
            :modelValue="userAnswerShow ? (userAnswers[0] || '') : (answers[0] || '')"
            @update:modelValue="(value: string) => userAnswers[0] = value"
            @change="(value: string) => emit('change', [value])" 
            :disabled="!editable"
            >
            <el-radio v-for="(option, index) in ['对', '错']" :key="index" :label="option">
                <div v-html="`${option}`" :style="{ color: errColor(option) }"/>
            </el-radio>
        </el-radio-group>
        <!-- 问答题答案 -->
        <el-input 
            v-if="type === 5" 
            :modelValue="qaAnswer"
            @update:modelValue="(value: string) => userAnswers[0] = value"
            :rows="2" 
            placeholder="请输入答案"
            type="textarea" 
            :autosize="{ minRows: 2 }" 
            @input="(value: string) => emit('change', [value])" 
            :readonly="!editable" 
            resize="none" 
            />
        <div class="question-paper-bottom-right">
            <slot name="bottom-right"></slot>
        </div>
        <slot name="bottom"></slot>
    </div>
</template>
  
<script lang="ts" setup>
import { ref, computed, watch } from 'vue'
import QuestionTitle from './QuestionTitle.vue'
import { useDictStore } from '@/stores/dict';

// 定义变量
const emit = defineEmits<{
    (e: 'change', answers: string[]): void
}>()

const props = withDefaults(defineProps<{
    no?: string | number // 题号
    editable?: boolean // 可编辑（true：是；false：否）
    display?: string // 显示（paper：试卷；list：列表）
    type: number // 试题类型（1：单选；2：多选；3：填空；4：判断；5：问答）
    markType: number // 阅卷类型
    title: string // 题干
    score: number // 分数
    answers?: string[] // 标准答案
    userAnswers?: string[] //用户答案
    userScore?: number //用户分数
    options?: string[] // 试题选项
    userAnswerShow?: boolean // 用户答案显示（true：用户答案；false：标准答案）
    errShow?: boolean // 错误显示（true：标记错误；false：不显示）
    updateUserName?: string // 修改用户名称
}>(), {
    editable: false,
    display: 'paper',
    answers: () => [], 
    userAnswers: () => [],
    options: () => [],
    userAnswerShow: true,
    errShow: false,
})
const dictStore = useDictStore()// 字典缓存
const no = ref(props.no) // 题号
const userAnswers = ref(props.userAnswers) // 用户答案
const userAnswerShow = ref(props.userAnswerShow) // 显示答案
const optionLabs = ref(['A', 'B', 'C', 'D', 'E', 'F', 'G'])
const qaAnswer = computed({// 问答答案
    get() {
        // 如果是显示用户答案，返回用户答案
        if (userAnswerShow.value) {
            if (!props.userAnswers || !props.userAnswers[0]) {
                return ''
            }
            return escape2Html(props.userAnswers[0])
        }
        
        // 否则返回标准答案
        if (props.type === 5 && props.markType === 1) {
            let answer = ''
            props.answers?.forEach((cur: string, index: number) => {
                answer += `关键词${index + 1}：${(escape2Html(cur) as string).replaceAll('\n', '、')}\n`
            })
    
            return answer
        }
    
        if (props.type === 5 && props.markType === 2) {
            return escape2Html(props.answers[0])
        }
    
        return null
    },
    set(n) {
        
    }
})

// 监听属性
watch(() => props.userAnswerShow, () => {
    userAnswerShow.value = props.userAnswerShow
})
watch(() => props.userAnswers, () => {
    userAnswers.value = props.userAnswers
})
watch(() => props.no, (n, o) => {
    no.value = props.no
})

// 错误颜色
function errColor(curAnswer: string) {
    if (!props.errShow) {
        return ''
    }

    if (props.type === 1 || props.type === 2 || props.type === 4) {// 单多选
        if (props.answers.includes(curAnswer) ) {// 标记出所有正确答案
            return 'var(--el-color-success)'
        } else if (props.userAnswers.includes(curAnswer) && !props.answers.includes(curAnswer)) {// 用户某项答案不在标准答案里，标记为红色
            return 'var(--el-color-error)'
        }  
    }
}

function escape2Html(txt: string|string[]) {
    if (typeof txt === 'string') {
        return txt.replaceAll('&lt;', '<')
            .replaceAll('&gt;', '>')
            .replaceAll('&amp;', '&')
            .replaceAll('&quot;', '"')
            .replaceAll('&apos;', "'")
            .replaceAll('&ldquo;', "“")
            .replaceAll('&rdquo;', "”")
    }

    return (txt as string[]).map((t: string) => {
        return t.replaceAll('&lt;', '<')
            .replaceAll('&gt;', '>')
            .replaceAll('&amp;', '&')
            .replaceAll('&quot;', '"')
            .replaceAll('&apos;', "'")
            .replaceAll('&ldquo;', "“")
            .replaceAll('&rdquo;', "”")
    })
}
</script>
  
<style lang="scss" scoped>
.question-list {
    cursor: pointer;
    margin: 10px;
    position: relative;

    &:hover {
        border: 1px solid var(--el-color-primary);
        background-color: var(--el-color-primary-light-9);

        .question-list-bottom-right {
            display: inline-block;
        }
    }

    :deep(.el-card__body) {
        padding: 10px 15px;

        .question-list-top {
            display: flex;
            align-items: flex-start;

            font-size: 13px;
            color: var(--el-text-color-regular);
            font-weight: bold;
            padding-bottom: 10px;

            .question-list-top-title {
                display: inline-block;
                word-break: keep-all;
                white-space: nowrap;
                text-overflow: ellipsis;
                overflow: hidden;
            }
        }

        .question-list-bottom {
            .el-tag {
                margin-right: 10px;
            }
        }
    }
}

.question-paper {
    margin: 10px 20px;
    border-bottom: 1px solid var(--el-border-color);
    padding: 10px 15px;
    position: relative;

    &:hover {
        .question-paper-bottom-right {
            display: inline-block;
        }
    }

    .el-checkbox-group {
        display: flex;
        flex-direction: column;
        // padding-left: 20px;
        align-items: flex-start;

        .el-checkbox {
            white-space: initial;
            height: auto;
            margin-bottom: 10px;

            .el-checkbox__label {
                font-size: 13px;
            }
        }
    }

    .el-radio-group {
        display: flex;
        flex-direction: column;
        // padding-left: 20px;
        align-items: flex-start;

        .el-radio {
            white-space: initial;
            height: auto;
            margin-bottom: 10px;

            .el-radio__label {
                font-size: 13px;
            }
        }
    }

    :deep(.el-textarea__inner) {
        box-shadow: initial;

        &:hover {
            box-shadow: 0 0 0 1px var(--el-color-primary) inset;
        }
    }
}

.question-list-bottom-right,
.question-paper-bottom-right {
    display: none;
    position: absolute;
    right: 15px;
    bottom: 11px;
}
</style>
