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
        <xmks-edit-card v-if="form.id" title="移动试题" desc="移动试题">
            <template #card-main>
                <el-form ref="moveFormRef" :model="moveForm" :rules="moveFormRules" inline label-width="100"
                    size="large" class="form">
                    <!-- 100% 超出界面 -->
                    <el-form-item label="选试题：" prop="questionIds" style="width: 880px;">
                        <xmks-select v-model="moveForm.questionIds" url="question/listpage"
                            :params="{ questionBankId: moveForm.id }" search-parm-name="title" option-label="title"
                            option-value="id" :multiple="true" clearable :page-size="100" search-placeholder="请输入题干进行筛选"
                            placeholder="请输入题干进行筛选">
                            <template #default="{ option }">
                                {{ option.id }} - {{ option.title }}
                            </template>
                        </xmks-select>
                    </el-form-item>
                    <el-form-item label="移动到：" prop="questionBankId" style="width: 100%;">
                        <xmks-select v-model="moveForm.questionBankId" url="questionBank/listpage" :params="{}"
                            search-parm-name="name" option-label="name" option-value="id" :multiple="false" clearable
                            :page-size="100" :disabled-values=[form.id] search-placeholder="请输入题库名称进行筛选"
                            placeholder="请输入题库名称进行筛选">
                            <template #default="{ option }">
                                {{ option.name }} {{ form.id == option.id ? '（不可选）' : '' }}
                            </template>
                        </xmks-select>
                    </el-form-item>
                </el-form>
            </template>
            <template #card-side>
                <el-button type="primary" class="form__btn" @click="move" style="margin-bottom: 40px;">移动试题</el-button>
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
import XmksSelect from '@/components/xmks-select.vue'
import { questionMove } from '@/api/exam/question'

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

const moveFormRef = ref<FormInstance>()// 表单引用
const moveForm = reactive({// 表单
    id: null,// ID
    questionIds: [],// 试题IDS
    questionBankId: [],// 题库ID
})
const moveFormRules = reactive<FormRules>({// 表单校验规则
    questionIds: [
        { required: true, message: '请选择试题', trigger: 'blur' },
    ],
    questionBankId: [
        { required: true, message: '请选择题库', trigger: 'blur' },
    ],
})

/************************组件生命周期相关*********************/
onMounted(async () => {
    if (route.path.indexOf('add') !== -1) {// 添加

    } else {// 修改
        const { data: { data } } = await questionBankGet({ id: route.params.id })
        form.id = data.id
        form.name = data.name

        moveForm.id = data.id
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

// 移动
async function move() {
    // 数据校验
    try {
        await moveFormRef.value?.validate()
    } catch (e) {
        return
    }

    // 修改
    const { data: { code } } = await questionMove({ ids: moveForm.questionIds, questionBankId: moveForm.questionBankId })
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
