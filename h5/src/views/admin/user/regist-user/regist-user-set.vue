<template>
    <el-scrollbar max-height="calc(100vh - 190px)" class="regist-user-set">
        <xmks-edit-card title="注册用户" desc="注册用户">
            <template #card-main>
                <el-form ref="formRef" :model="form" :rules="formRules" label-width="100" size="large" class="form">
                    <el-form-item label="所属机构" prop="orgId">
                        <xmks-select v-model="form.orgId as number" url="org/listpage" :params="{ state: 1 }"
                            search-parm-name="name" option-label="name" option-value="id" :multiple="false"
                            :options="orgs" :disabled="true" search-placeholder="请输入机构用户名称进行筛选">
                            <template #default="{ option }">
                                {{ option.name }} - {{ option.parentName }}
                            </template>
                        </xmks-select>
                    </el-form-item>
                    <el-form-item label="姓名" prop="name">
                        <el-input v-model="form.name" :disabled="true" placeholder="请输入姓名" />
                    </el-form-item>
                    <el-form-item label="登录账号" prop="loginName">
                        <el-input v-model="form.loginName" :disabled="true" placeholder="请输入登录账号" />
                    </el-form-item>
                    <el-form-item label="注册时间" prop="registTime">
                        <el-input v-model="form.registTime" placeholder="请输入注册时间" :disabled="true" />
                    </el-form-item>
                    <el-form-item label="审批" prop="state">
                        <el-radio-group v-model="form.state" :disabled="true">
                            <el-radio v-for="(option, index) in dictStore.getList('APPROVE_STATE')" :key="index"
                                :value="parseInt(option.dictKey)">
                                {{ option.dictValue }}
                            </el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item label="审批意见" prop="remark">
                        <el-radio-group v-model="form.remark" style="width: 100%;">
                            <el-input v-model="form.remark" :disabled="form.state !== 3" placeholder="请输入审批意见" />
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item>
                        <el-button v-if="form.state === 3" type="primary" class="form__btn"
                            @click="approve">同意</el-button>
                        <el-button v-if="form.state === 3" type="primary" class="form__btn form__btn--warn"
                            @click="reject">拒绝</el-button>
                    </el-form-item>
                </el-form>
            </template>
        </xmks-edit-card>
    </el-scrollbar>
</template>

<script lang="ts" setup>
import { orgGet } from '@/api/base/org'
import { registUserApprove, registUserGet, registUserReject } from '@/api/base/regist-user'
import XmksEditCard from '@/components/card/xmks-card-edit.vue'
import XmksSelect from '@/components/xmks-select.vue'
import { useDictStore } from '@/stores/dict'
import type { Org } from '@/ts/base/org'
import type { RegistUser } from '@/ts/base/registUser'
import { type FormInstance, type FormRules } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

/************************变量定义相关***********************/
const route = useRoute()// 路由
const router = useRouter()// 路由
const dictStore = useDictStore()// 路由
const formRef = ref<FormInstance>()// 表单引用
const form = reactive<RegistUser>({
    id: null,
    name: '',
    loginName: '',
    orgId: 0,
    registTime: '',
    state: null,
    remark: ''
})
const formRules = reactive<FormRules>({// 表单规则
    remark: [
        { min: 0, max: 64, message: '长度介于0-64', trigger: 'blur' },
    ],
})
const orgs = ref([] as Org[])

/************************组件生命周期相关*********************/
onMounted(async () => {
    const { data: { data } } = await registUserGet({ id: route.params.id })
    form.id = data.id
    form.loginName = data.loginName
    form.name = data.name
    form.registTime = data.registTime
    form.orgId = data.orgId
    form.state = data.state
    form.remark = data.remark

    const { data: { data: org } } = await orgGet({ id: form.orgId })
    orgs.value.push(org)
})

/************************事件相关*****************************/
// 同意
async function approve() {
    // 数据校验
    try {
        await formRef.value?.validate()
    } catch (e) {
        return
    }

    // 同意
    if (!form.remark) {
        form.remark = '同意'
    }
    const { data: { code } } = await registUserApprove({ ...form })
    if (code !== 200) {
        return
    }

    router.push("/user-nav/regist-user-list")
}

// 拒绝
async function reject() {
    // 数据校验
    try {
        await formRef.value?.validate()
    } catch (e) {
        return
    }

    // 拒绝
    if (!form.remark) {
        form.remark = '拒绝'
    }
    const { data: { code } } = await registUserReject({ ...form })
    if (code !== 200) {
        return
    }

    router.push("/user-nav/regist-user-list")
}

</script>

<style lang="scss" scoped>
.regist-user-set {
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
