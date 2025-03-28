<template>
    <div class="question-import">
        <div class="question-import__head">
            <el-button type="primary" class="question-import__btn" @click="$router.go(-1)">
                <span class="iconfont icon-zuo question-import__btn-icon"></span>
                <span class="editor-toolbar__btn-txt">返回</span>
            </el-button>
            <el-button type="primary" class="question-import__btn" @click="txtImport">
                完成导入
            </el-button>
        </div>
        <div class="question-import__main">
            <question-editor ref="questionEditorRef"></question-editor>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { questionAdd } from '@/api/exam/question'
import QuestionEditor from '@/components/question/xmks-question-editor.vue'
import { ElMessage } from 'element-plus'
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

/************************变量定义相关***********************/
const route = useRoute()// 路由
const router = useRouter()// 路由
const questionEditorRef = ref<InstanceType<typeof QuestionEditor>>();

/************************事件相关*****************************/
// 完成导入
async function txtImport() {
    const result = questionEditorRef.value?.validate()
    if (!result?.succ) {
        ElMessage.error(result?.msg)
        return
    }

    for (const index in result.data) {
        const question = result.data[index as unknown as number];
        question.questionBankId = Number(route.params.questionBankId);
        await questionAdd({ ...question });
    }

    router.push(`/question-bank/question-nav/list/${route.params.questionBankId}`)
}
</script>
<style scoped lang="scss">
.question-import {
    display: flex;
    flex-direction: column;
    width: 1200px;
    padding: 20px;
    margin-top: 20px;
    background-color: #FFFFFF;
    border-radius: 15px 15px 15px 15px;

    .question-import__head {
        display: flex;
        justify-content: space-between;
        margin-bottom: 10px;

        .question-import__btn {
            height: 38px;
            padding: 8px 30px;
            border: 0px;
            color: #FFFFFF;
            border-radius: 6px 6px 6px 6px;
            background-image: linear-gradient(to right, #04C7F2, #259FF8);

            .question-import__btn-icon {
                font-size: 14px;
            }

            .editor-toolbar__btn-txt {
                margin-left: 2px;
            }
        }
    }

    .question-import__main {
        flex: 1;
    }
}
</style>
