<template>
    <el-scrollbar max-height="calc(100vh - 230px)" class="org-set">
        <xmks-edit-card title="机构" desc="机构">
            <template #card-main>
                <el-form ref="formRef" :model="form" :rules="formRules" label-width="100" size="large" class="form">
                    <el-form-item label="所属机构" prop="parentName">
                        <el-input v-model="form.parentName" disabled />
                    </el-form-item>
                    <el-form-item label="名称" prop="name">
                        <el-input v-model="form.name" placeholder="请输入名称" />
                    </el-form-item>
                    <el-form-item label="排序" prop="no">
                        <el-input-number v-model="form.no" :min="1" :max="100" :precision="0" controls-position="right"
                            size="large" />
                    </el-form-item>
                    <el-form-item>
                        <el-button v-if="$route.path.indexOf('add') !== -1" type="primary" class="form__btn"
                            @click="add">
                            添加
                        </el-button>
                        <el-button v-else type="primary" class="form__btn" @click="edit">修改</el-button>
                    </el-form-item>
                </el-form>
            </template>
        </xmks-edit-card>
        <xmks-edit-card v-if="form.id" title="删除用户" desc="批量删除考试用户">
            <template #card-main>
                <el-form ref="userDelFormRef" :model="userDelForm" :rules="userDelFormRules" inline label-width="100"
                    size="large" class="form">
                    <el-form-item label="选择用户" prop="questionIds" style="width: 100%;">
                        <xmks-select v-model="userDelForm.userIds" url="user/listpage"
                            :params="{ state: 1, role: 'EXAM_USER', orgId: route.params.id }" search-parm-name="name"
                            option-label="name" option-value="id" :options="[]" :multiple="true" clearable
                            :page-size="100" search-placeholder="请输入机构名称或用户名称进行筛选" placeholder="请输入机构名称或用户名称进行筛选">
                            <template #default="{ option }">
                                {{ option.name }} - {{ option.orgName }}
                            </template>
                        </xmks-select>
                    </el-form-item>
                </el-form>
            </template>
            <template #card-side>
                <el-button type="primary" class="form__btn" @click="userBatchDel"
                    style="margin-bottom: 40px;">删除用户</el-button>
            </template>
        </xmks-edit-card>
        <xmks-edit-card v-if="form.id" title="删除机构" desc="如果存在子机构，则不能删除">
            <template #card-side>
                <el-button type="primary" class="form__btn" :class="{ 'form__btn--warn': delConfirm }" @click="del"
                    style="margin-bottom: 14px;">删除机构</el-button>
            </template>
        </xmks-edit-card>
    </el-scrollbar>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import XmksEditCard from '@/components/card/xmks-card-edit.vue'
import type { Org } from '@/ts/base/org'
import { orgAdd, orgDel, orgEdit, orgGet } from '@/api/base/org'
import XmksSelect from '@/components/xmks-select.vue'
import { userDel } from '@/api/base/user'

/************************变量定义相关***********************/
const route = useRoute()// 路由
const router = useRouter()// 路由
const formRef = ref<FormInstance>()// 表单引用
const form = reactive<Org>({// 表单
    id: null,
    name: '',
    parentId: null,
    parentName: '',
    no: 1,
})
const formRules = reactive<FormRules>({// 表单规则
    name: [
        { required: true, message: '请输入名称', trigger: 'blur' },
        { min: 1, max: 16, message: '长度介于1-16', trigger: 'blur' },
    ],
    no: [
        { required: true, message: '请输入排序', trigger: 'blur' },
    ],
})
const delConfirm = ref(false) // 删除确认

const userDelFormRef = ref<FormInstance>()// 表单引用
const userDelForm = reactive({// 表单
    userIds: [],// 用户IDS
})
const userDelFormRules = reactive<FormRules>({// 表单校验规则
    userIds: [
        { required: true, message: '请选择考试用户', trigger: 'blur' },
    ],
})

/************************组件生命周期相关*********************/
onMounted(async () => {
    if (route.path.indexOf('add') !== -1) {// 添加
        const { data: { data } } = await await orgGet({ id: route.params.parentId })
        form.parentId = data.id
        form.parentName = data.name
    } else {// 修改
        const { data: { data } } = await await orgGet({ id: route.params.id })
        form.id = data.id
        form.name = data.name
        form.parentId = data.parentId
        form.parentName = data.parentName
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
    const { data: { code } } = await orgAdd({ ...form })
    if (code !== 200) {
        return
    }

    router.push("/user-nav/org-list")
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
    const { data: { code } } = await orgEdit({ ...form })
    if (code !== 200) {
        return
    }

    router.push("/user-nav/org-list")
}

// 用户删除
async function userBatchDel() {
    // 数据校验
    try {
        await userDelFormRef.value?.validate()
    } catch (e) {
        return
    }

    // 修改
    const { data: { code } } = await userDel({ ids: userDelForm.userIds })
    if (code !== 200) {
        return
    }

    router.push("/user-nav/org-list")
}

// 删除
async function del() {
    if (!delConfirm.value) {
        delConfirm.value = true
        return
    }

    const { data: { code } } = await orgDel({
        id: form.id,
    })
    if (code !== 200) {
        return
    }

    router.push("/user-nav/org-list")
}
</script>

<style lang="scss" scoped>
.org-set {
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