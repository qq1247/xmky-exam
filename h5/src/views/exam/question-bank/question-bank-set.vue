<template>
    <el-scrollbar max-height="calc(100vh - 190px)" class="question-bank-set">
        <xmks-edit-card title="题库" desc="题库">
            <template #card-main>
                <el-form ref="formRef" :model="form" :rules="formRules" label-width="100" size="large" class="form">
                    <el-form-item label="名称" prop="name">
                        <el-input v-model="form.name" placeholder="请输入名称" />
                    </el-form-item>
                    <el-form-item>
                        <el-button v-if="$route.path.indexOf('add') !== -1" type="primary" class="form__btn"
                            @click="add">添加</el-button>
                        <el-button v-else type="primary" class="form__btn" @click="edit">修改</el-button>
                    </el-form-item>
                </el-form>
            </template>
        </xmks-edit-card>
        <xmks-edit-card v-if="form.id" title="清空试题" desc="清空试题">
            <template #card-side>
                <el-button type="primary" class="form__btn" :class="{ 'form__btn--warn': clearConfirm }"
                    style="margin-bottom: 14px;" @click="clear">清空试题</el-button>
            </template>
        </xmks-edit-card>
        <xmks-edit-card v-if="form.id" title="删除题库" desc="删除题库">
            <template #card-side>
                <el-button type="primary" class="form__btn" :class="{ 'form__btn--warn': delConfirm }" @click="del"
                    style="margin-bottom: 14px;">删除题库</el-button>
            </template>
        </xmks-edit-card>
    </el-scrollbar>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted } from 'vue'
import { type FormInstance, type FormRules } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import XmksEditCard from '@/components/card/xmks-card-edit.vue'
import type { QuestionBank } from '@/ts/exam/question-bank'
import { questionBankAdd, questionBankDel, questionBankEdit, questionBankGet, questionBankClear } from '@/api/exam/question-bank'

/************************变量定义相关***********************/
const route = useRoute()// 路由
const router = useRouter()// 路由
const formRef = ref<FormInstance>()// 表单引用
const form = reactive<QuestionBank>({
    id: null,
    name: '',
})
const formRules = reactive<FormRules>({// 表单规则
    name: [
        { required: true, message: '请输入名称', trigger: 'blur' },
        { min: 1, max: 16, message: '长度介于1-16', trigger: 'blur' },
    ],
})
const delConfirm = ref(false) // 删除确认
const clearConfirm = ref(false) // 清空确认


/************************组件生命周期相关*********************/
onMounted(async () => {
    if (route.path.indexOf('add') !== -1) {// 添加

    } else {// 修改
        const { data: { data } } = await questionBankGet({ id: route.params.id })
        form.id = data.id
        form.name = data.name
    }
})

/************************事件相关*****************************/
// 添加
async function add() {
    // 数据校验
    try {
        await formRef.value?.validate()
    } catch (e) {
        return
    }

    // 添加
    const { data: { code } } = await questionBankAdd({ ...form })
    if (code !== 200) {
        return
    }

    router.push("/question-bank-list")
}

// 修改
async function edit() {
    // 数据校验
    try {
        await formRef.value?.validate()
    } catch (e) {
        return
    }

    // 修改
    const { data: { code } } = await questionBankEdit({ ...form })
    if (code !== 200) {
        return
    }

    router.push("/question-bank-list")
}

// 删除
async function del() {
    if (!delConfirm.value) {
        delConfirm.value = true
        return
    }

    const { data: { code } } = await questionBankDel({ id: form.id })
    if (code !== 200) {
        return
    }

    router.push("/question-bank-list")
}

// 清空题库
async function clear() {
    if (!clearConfirm.value) {
        clearConfirm.value = true
        return
    }

    const { data: { code } } = await questionBankClear({ id: form.id })
    if (code !== 200) {
        return
    }

    router.push("/question-bank-list")
}
</script>

<style lang="scss" scoped>
.question-bank-set {
    display: flex;
    flex-direction: column;
    width: 1200px;
    background-color: #FFFFFF;
    margin: 20px 0px;
    padding: 40px 30px 0px 30px;
    border-radius: 15px 15px 15px 15px;

    .form {
        margin-top: 20px;
    }

    .form__btn {
        height: 38px;
        padding: 0px 30px;
        border-radius: 6px;
        border: 0px;
        color: #FFFFFF;
        font-size: 14px;
        background-image: linear-gradient(to right, #04C7F2, #259FF8);
    }

    .form__btn--secondary {
        color: #04C7F2;
        border: 1px solid #04C7F2;
        background-image: linear-gradient(to right, #FFFFFF, #FFFFFF);
    }

    .form__btn--warn {
        color: #FF5D15;
        border: 1px solid #FF5D15;
        background-image: linear-gradient(to right, #FFFFFF, #FFFFFF);
    }
}
</style>
