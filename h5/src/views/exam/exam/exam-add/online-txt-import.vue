e<template>
    <div class="online-txt-import">
        <div class="online-txt-import__head">
            <el-button type="primary" class="online-txt-import__btn" @click="back">
                <span class="iconfont icon-zuo online-txt-import__btn-icon"></span>
                <span class="editor-toolbar__btn-txt">返回</span>
            </el-button>
            <el-button type="primary" class="online-txt-import__btn" @click="txtImport">
                完成导入
            </el-button>
        </div>
        <div class="online-txt-import__main">
            <xmks-question-editor ref="questionEditorRef"></xmks-question-editor>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { ref } from 'vue'
import XmksQuestionEditor from '@/components/question/xmks-question-editor.vue'
import { useExamStore } from '@/stores/exam'
import type { Question } from '@/ts/exam/question'
import { ElMessage } from 'element-plus'

/************************变量定义相关***********************/
const emit = defineEmits(['back', 'completed'])

const form = useExamStore() // 考试表单
const questionEditorRef = ref<InstanceType<typeof XmksQuestionEditor>>();

/************************事件相关*****************************/
// 返回
function back() {
    emit('back')
}

// 完成导入
function txtImport() {
    const result = questionEditorRef.value?.validate()
    if (!result?.succ) {
        ElMessage.error(result?.msg)
        return
    }
    result?.data?.forEach((question: Question) => {
        form.examQuestions.push({
            type: 2,
            questionId: question.id,
            questionType: question.type,
            markType: question.markType,
            title: question.title,
            score: question.score,
            answers: question.answers,
            scores: question.scores,
            options: question.options,
            markOptions: question.markOptions,
            analysis: question.analysis,
        })
    })

    form.questionNoUpdate()

    emit('completed')
}
</script>
<style scoped lang="scss">
.online-txt-import {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 20px;
    background-color: #FFFFFF;
    border-radius: 15px 15px 15px 15px;

    .online-txt-import__head {
        display: flex;
        justify-content: space-between;
        margin-bottom: 10px;

        .online-txt-import__btn {
            height: 38px;
            padding: 8px 30px;
            border: 0px;
            color: #FFFFFF;
            border-radius: 6px 6px 6px 6px;
            background-image: linear-gradient(to right, #04C7F2, #259FF8);

            .online-txt-import__btn-icon {
                font-size: 14px;
            }

            .editor-toolbar__btn-txt {
                margin-left: 2px;
            }
        }
    }

    .online-txt-import__main {
        flex: 1;
    }
}
</style>
