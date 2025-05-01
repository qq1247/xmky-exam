<template>
    <div class="xmks-question">
        <!-- 列表样式 -->
        <div v-if="display === 'list'" class="list">
            <div class="list__title">
                <slot name="title-pre"></slot>
                <span v-html="title"></span>
            </div>
            <div class="list__tags">
                <el-tag class="list__tag list__tag--type">
                    {{ dictStore.getValue('QUESTION_TYPE', type) }}
                </el-tag>
                <el-tag class="list__tag list__tag--mark-type">
                    {{ dictStore.getValue('PAPER_MARK_TYPE', markType) }}
                </el-tag>
                <el-tag class="list__tag list__tag--score">
                    {{ score }}分
                </el-tag>
                <el-tag class="list__tag list__tag--username">
                    {{ updateUserName }}
                </el-tag>
            </div>
            <div class="list__opt">
                <span v-for="(btn, index) in btns" :key="index" :data-name="btn.name" class="list__btn"
                    @click="btn.event">
                    <i :class="`iconfont ${btn.icon} `"></i>
                </span>
            </div>
        </div>
        <!-- 试卷样式 -->
        <div v-else-if="display === 'paper'" class="question">
            <!-- 题干 -->
            <div class="question__title">
                <span class="question__title-pre">
                    <slot name="title-pre"></slot>
                </span>
                <xmks-question-title :type="type" :title="title" :answers="answers" :user-answers="userAnswers"
                    :editable="editable" :user-answer-show="userAnswerShow" :answerShow="answerShow" :score="score"
                    :user-score="userScore" @change="(value: string[]) => emit('change', value)"></xmks-question-title>
            </div>
            <div v-if="imgIds.length" class="question__img-group">
                <photo-provider :default-backdrop-opacity="0.6">
                    <photo-consumer v-for="(imgId, index) in imgIds" :key="index" :src="`${downloadUrl}?id=${imgId}`">
                        <div class="question_img-inner">
                            <el-image :src="`${downloadUrl}?id=${imgId}`" fit="contain" />
                            <span class="question_img-desc">图{{ toChinaNum(index + 1) }}</span>
                        </div>
                    </photo-consumer>
                </photo-provider>
            </div>
            <!-- 单选题选项 -->
            <el-radio-group v-if="type === 1"
                :modelValue="userAnswerShow ? (userAnswers[0] || '') : (answerShow ? (answers[0] || '') : '')"
                @change="(value: string) => emit('change', [value])" :disabled="!editable"
                class="question__single-select-group">
                <el-radio v-for="(option, index) in options" :key="index" :value="toLetter(index)"
                    class="question__single-select" :class="{
                        'question__single-select--succ': isCorrectSelect(toLetter(index)),
                        'question__single-select--fail': isWrongSelect(toLetter(index))
                    }">
                    {{ toLetter(index) }}、{{ escape2Html(option) }}
                </el-radio>
            </el-radio-group>
            <!-- 多选题选项 -->
            <el-checkbox-group v-else-if="type === 2"
                :modelValue="userAnswerShow ? userAnswers : (answerShow ? answers : [])"
                @change="(value: string[]) => emit('change', value)" :disabled="!editable"
                class="question__multiple-select-group">
                <el-checkbox v-for="(option, index) in options" :key="index" :value="`${toLetter(index)}`"
                    class="question__multiple-select" :class="{
                        'question__multiple-select--succ': isCorrectSelect(toLetter(index)),
                        'question__multiple-select--fail': isWrongSelect(toLetter(index))
                    }">
                    {{ toLetter(index) }}、{{ escape2Html(option) }}
                </el-checkbox>
            </el-checkbox-group>
            <!-- 填空题（题干区域） -->
            <!-- 判断题选项 -->
            <el-radio-group v-else-if="type === 4"
                :modelValue="userAnswerShow ? (userAnswers[0] || '') : (answerShow ? (answers[0] || '') : '')"
                @change="(value: string) => emit('change', [value])" :disabled="!editable"
                class="question__single-select-group">
                <el-radio v-for="(option, index) in ['对', '错']" :key="index" :value="option"
                    class="question__single-select" :class="{
                        'question__single-select--succ': isCorrectSelect(option),
                        'question__single-select--fail': isWrongSelect(option)
                    }">
                    {{ option }}
                </el-radio>
            </el-radio-group>
            <!-- 问答题答案 -->
            <el-input v-if="type === 5 && !userAnswerShow && !answerShow" :modelValue="''" placeholder="请输入答案"
                type="textarea" :autosize="{ minRows: 5 }" :readonly="true" resize="none" class="question__qa" />
            <el-input v-else-if="type === 5 && answerShow" :modelValue="qaAnswer" placeholder="请输入答案" type="textarea"
                :autosize="{ minRows: 5 }" :readonly="true" resize="none" class="question__qa question__qa--answer" />
            <el-input v-else-if="type === 5 && userAnswerShow" :modelValue="escape2Html(userAnswers[0]) || ''"
                placeholder="请输入答案" type="textarea" :autosize="{ minRows: 5 }"
                @input="(value: string) => emit('change', [value])" :readonly="!editable" resize="none"
                class="question__qa question__qa--user-answer" />
            <!-- 解析 -->
            <div v-if="analysisShow" class="question__analysis">
                <div class="question__analysis-title">解析</div>
                <span class="question__analysis-content" v-html="analysis?.replaceAll('\n', '<br/>') || '暂无解析'">
                </span>
            </div>
            <!-- 底部插槽，可用于设置分数等用途 -->
            <slot name="foot"></slot>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref, computed, watch } from 'vue'
import XmksQuestionTitle from './xmks-question-title.vue'
import { useDictStore } from '@/stores/dict'
import type { CardBtn } from '@/ts/common/card-btn'
import { toChinaNum, toLetter } from '@/util/numberUtil'
import { escape2Html } from '@/util/htmlUtil'
import http from '@/request'

/************************变量定义相关***********************/
const emit = defineEmits<{
    (e: 'change', answers: string[]): void
}>()

const props = withDefaults(defineProps<{
    type: number // 试题类型（1：单选；2：多选；3：填空；4：判断；5：问答）
    title: string // 题干
    imgIds?: number[] // 图片附件IDS
    options?: string[] // 试题选项
    answers?: string[] // 标准答案
    markType: number // 阅卷类型（1：客观题；2：主观题）
    score: number // 分数
    scores?: number[] // 子分数
    analysis?: string // 解析
    userAnswers?: string[] //用户答案
    userScore?: number | null //用户分数
    editable?: boolean // 可编辑（true：是；false：否）
    display?: string // 显示（paper：试卷；list：列表；paper-with-answer：列表带答案）
    answerShow?: boolean // 标准答案显示
    userAnswerShow?: boolean // 用户答案显示
    analysisShow?: boolean // 解析显示
    updateUserName?: string // 修改用户名称
    btns?: CardBtn[];// 按钮组
}>(), {
    editable: false,
    display: 'paper',
    imgIds: () => [],
    answers: () => [],
    userAnswers: () => [],
    options: () => [],
    userAnswerShow: true,
    answerShow: false,
    analysisShow: false,
    updateUserName: '',
})

const dictStore = useDictStore()// 字典缓存
const userAnswers = ref(props.userAnswers) // 用户答案
const downloadUrl = `${http.defaults.baseURL}file/download`// 下载地址

/************************计算属性相关*************************/
const qaAnswer = computed(() => {// 标准问答答案
    if (props.type !== 5) {
        return ''
    }
    if (props.markType === 1) {
        return escape2Html(props.answers.map((cur, index) => {
            return `关键词${toChinaNum(index + 1)}（${(props.scores as number[])[index]}分）：${cur.replaceAll('\n', '|')}`
        })).join('\n')
    }
    if (props.markType === 2) {
        return escape2Html(props.answers[0] || '')

    }
    return ''
})

const isCorrectSelect = computed(() => (curAnswer: string) => {// 是否正确选择
    if (!(props.type === 1 || props.type === 2 || props.type === 4)) {
        return false
    }
    if (!props.answerShow) {
        return false
    }
    if (props.answers.includes(curAnswer)) {
        return true
    }
    return false
});
const isWrongSelect = computed(() => (curAnswer: string) => {// 是否错误选择
    if (!(props.type === 1 || props.type === 2 || props.type === 4)) {
        return false
    }
    if (!props.answerShow) {
        return false
    }
    if (props.userAnswers.includes(curAnswer) && !props.answers.includes(curAnswer)) {
        return true
    }
    return false
});

/************************监听相关*****************************/
watch(() => props.userAnswers, () => {
    userAnswers.value = props.userAnswers
})

</script>

<style lang="scss" scoped>
.xmks-question {
    .list {
        display: flex;
        flex-direction: column;
        justify-content: center;
        position: relative;
        height: 85px;
        border-bottom: 1px dashed #E5E5E5;
        cursor: pointer;
        padding-left: 10px;

        &:hover {
            background-color: #F2F6F9;

            .list__opt {
                display: block;
            }
        }

        .list__title {
            width: 900px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            font-size: 14px;
            line-height: 30px;
            color: #303133;
        }

        .list__tags {
            .list__tag {
                height: 22px;
                padding: 0px 10px;
                font-size: 12px;
                margin-right: 10px;

                &.list__tag--type {
                    color: #1EA1EE;
                    background-color: #E4F6FF;
                    border: 1px solid #C0EAFF;
                }

                &.list__tag--mark-type {
                    color: #FC8113;
                    background-color: #FDEDD9;
                    border: 1px solid #FED9B3;
                }

                &.list__tag--score {
                    color: #FE7068;
                    background-color: #FFE6E6;
                    border: 1px solid #FFCAC7;
                }

                &.list__tag--username {
                    color: #1AC693;
                    background-color: #E8F9F4;
                    border: 1px solid #AFE7D6;
                }
            }
        }

        .list__opt {
            display: none;
            position: absolute;
            top: 16px;
            right: 30px;

            .list__btn {
                display: inline-block;
                width: 28px;
                height: 28px;
                line-height: 28px;
                margin: 0px 10px;
                text-align: center;
                border-radius: 50%;
                border: 1px solid #E5E5E5;
                color: #8F939C;
                position: relative;
                cursor: pointer;
                z-index: 1;

                &::before {
                    content: "";
                    display: block;
                    position: absolute;
                    bottom: -10px;
                    left: 50%;
                    transform: translateX(-50%);
                    border-width: 0 5px 10px 5px;
                    border-style: solid;
                    border-color: transparent transparent #04C7F2;
                    opacity: 0;
                }

                &::after {
                    content: attr(data-name);
                    display: block;
                    position: absolute;
                    bottom: -40px;
                    transform: translateX(-50%);
                    left: 50%;
                    height: 30px;
                    width: 80px;
                    line-height: 30px;
                    background-image: linear-gradient(to right, #04C7F2, #259FF8);
                    color: white;
                    border-radius: 6px;
                    font-size: 14px;
                    opacity: 0;
                }

                &:hover {
                    border: 1px solid #04C7F2;
                    background-image: linear-gradient(to right, #04C7F2, #259FF8);
                    color: white;

                    &::before,
                    &::after {
                        opacity: 1;
                    }
                }
            }
        }
    }

    :deep(.question) {
        border-bottom: 1px dashed #E5E5E5;
        padding-bottom: 10px;
        margin-top: 10px;

        .question__title {
            display: flex;
            align-items: flex-start;

            .question__title-pre {
                font-size: 14px;
                color: #303133;
                line-height: 28px;
            }
        }

        .question__img-group {
            margin-bottom: 8px;

            :deep(.PhotoSlider__Backdrop) {
                opacity: 0.6;
            }

            .el-image__inner {
                background-color: #fff;
                border: 1px solid #dcdfe6;
                border-radius: 6px;
                height: 148px;
                width: 148px;
                margin: 0 8px 4px 0;
                overflow: hidden;
                padding: 0px;

            }

            .question_img-inner {
                display: flex;
                flex-direction: column;
                align-items: center;
            }

            .question_img-desc {
                font-size: 14px;
                color: #000000;
            }
        }

        .question__single-select-group {
            display: flex;
            flex-direction: column;
            align-items: flex-start;

            .question__single-select {
                width: 100%;
                display: flex;
                min-height: 38px;
                border-radius: 10px 10px 10px 10px;
                margin-bottom: 6px;

                .el-radio__input {
                    margin-left: 6px;
                }

                .el-radio__label {
                    white-space: normal;
                    word-wrap: break-word;
                    line-height: initial;
                }
            }

            .is-checked {
                background-color: #E0F9FF;
            }


            .question__single-select--succ {
                background-color: #D1F2D7;
            }

            .question__single-select--fail {
                background-color: #FAD8D6;
            }
        }

        .question__multiple-select-group {
            display: flex;
            flex-direction: column;
            align-items: flex-start;

            .el-checkbox {
                width: 100%;
                min-height: 38px;
                border-radius: 10px 10px 10px 10px;
                margin-bottom: 6px;

                .el-checkbox__input {
                    margin-left: 6px;
                }

                .el-checkbox__label {
                    white-space: normal;
                    word-wrap: break-word;
                    line-height: initial;
                }
            }

            .is-checked {
                background-color: #E0F9FF;
            }

            .question__multiple-select--succ {
                background-color: #D1F2D7;
            }

            .question__multiple-select--fail {
                background-color: #FAD8D6;
            }
        }

        .question__qa {
            .el-textarea__inner {
                background-color: #F5F7FA;
                padding: 20px !important;
                border-radius: 10px 10px 10px 10px;
                box-shadow: initial;
                padding: 0;
            }

            &.question__qa--answer {
                .el-textarea__inner {
                    background-color: #D1F2D7;
                }
            }

            &.question__qa--user-answer {
                .el-textarea__inner {
                    background-color: #E0F9FF;
                }
            }
        }

        .question__analysis {
            display: flex;
            min-height: 50px;
            margin-top: 20px;
            padding: 10px 20px 5px 20px;
            position: relative;
            border: 1px solid #E5E5E5;

            .question__analysis-title {
                position: absolute;
                left: 15px;
                top: -8px;
                padding: 0px 10px;
                background-color: #FFFFFF;
                font-size: 14px;
                color: #999999;
            }

            .question__analysis-content {
                font-size: 14px;
                color: #999999;
                line-height: 28px;
            }
        }
    }
}
</style>
