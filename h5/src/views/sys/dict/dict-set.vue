<template>
    <el-scrollbar max-height="calc(100vh - 190px)" class="dict-set">
        <xmks-edit-card title="字典" desc="字典">
            <template #card-main>
                <el-form ref="formRef" :model="form" :rules="formRules" label-width="100" size="large" class="form">
                    <el-form-item label="索引" prop="dictIndex">
                        <el-input v-model="form.dictIndex" placeholder="请输入索引" />
                    </el-form-item>
                    <el-form-item label="键" prop="dictKey">
                        <el-input v-model="form.dictKey" placeholder="请输入键" />
                    </el-form-item>
                    <el-form-item label="值" prop="dictValue">
                        <el-input v-model="form.dictValue" placeholder="请输入值" />
                    </el-form-item>
                    <el-form-item label="排序" prop="no">
                        <el-input-number v-model="form.no" :min="1" :max="100" :precision="0" controls-position="right"
                            size="large" />
                    </el-form-item>
                    <el-form-item>
                        <el-button v-if="$route.path.indexOf('add') !== -1" type="primary" class="form__btn"
                            @click="add">添加</el-button>
                        <el-button v-else type="primary" class="form__btn" @click="edit">修改</el-button>
                    </el-form-item>
                </el-form>
            </template>
        </xmks-edit-card>
        <xmks-edit-card v-if="form.id" title="删除字典" desc="删除字典">
            <template #card-side>
                <el-button type="primary" class="form__btn" :class="{ 'form__btn--warn': delConfirm }" @click="del"
                    style="margin-bottom: 14px;">删除字典</el-button>
            </template>
        </xmks-edit-card>
    </el-scrollbar>
</template>

<script lang="ts" setup>
import { dictAdd, dictDel, dictEdit, dictGet } from '@/api/sys/dict'
import XmksEditCard from '@/components/card/xmks-card-edit.vue'
import type { Dict } from '@/ts/sys/dict'
import { type FormInstance, type FormRules } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

/************************变量定义相关***********************/
const route = useRoute()// 路由
const router = useRouter()// 路由
const formRef = ref<FormInstance>()// 表单引用
const form = reactive<Dict>({// 表单
    id: null,
    dictIndex: '',
    dictKey: '',
    dictValue: '',
    no: 1,
})
const delConfirm = ref(false) // 删除确认
const formRules = reactive<FormRules>({// 表单规则
    dictIndex: [
        { required: true, message: '请输入索引', trigger: 'blur' },
        { min: 1, max: 16, message: '长度介于1-16', trigger: 'blur' },
    ],
    dictKey: [
        { required: true, message: '请输入键', trigger: 'blur' },
        { min: 1, max: 16, message: '长度介于1-16', trigger: 'blur' },
    ],
    dictValue: [
        { required: true, message: '请输入值', trigger: 'blur' },
        { min: 1, max: 16, message: '长度介于1-16', trigger: 'blur' },
    ],
    no: [
        { required: true, message: '请输入排序', trigger: 'blur' },
    ],
})

/************************组件生命周期相关*********************/
onMounted(async () => {
    if (route.path.indexOf('add') !== -1) {// 添加

    } else {// 修改
        const { data: { data } } = await dictGet({ id: route.params.id })
        form.id = data.id
        form.dictIndex = data.dictIndex
        form.dictKey = data.dictKey
        form.dictValue = data.dictValue
        form.no = data.no
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
    const { data: { code } } = await dictAdd({ ...form })
    if (code !== 200) {
        return
    }

    router.push("/sys-nav/dict-list")
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
    const { data: { code } } = await dictEdit({ ...form })
    if (code !== 200) {
        return
    }

    router.push("/sys-nav/dict-list")
}

// 删除
async function del() {
    if (!delConfirm.value) {
        delConfirm.value = true
        return
    }

    const { data: { code } } = await dictDel({
        id: form.id,
    })
    if (code !== 200) {
        return
    }

    router.push("/sys-nav/dict-list")
}
</script>

<style lang="scss" scoped>
.dict-set {
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
