<template>
    <el-scrollbar max-height="calc(100vh - 190px)" class="sub-admin-set">
        <xmks-edit-card title="子管理员" desc="建议按部门或岗位分配子管理员，分别对考试用户进行管理。">
            <template #card-main>
                <el-form ref="formRef" :model="form" :rules="formRules" label-width="100" size="large" class="form">
                    <el-form-item label="姓名" prop="name">
                        <el-input v-model="form.name" placeholder="请输入姓名" />
                    </el-form-item>
                    <el-form-item label="登录账号" prop="loginName">
                        <el-input v-model="form.loginName" placeholder="请输入登录账号" />
                    </el-form-item>
                    <el-form-item label="管理机构" prop="orgIds">
                        <xmks-select v-model="form.orgIds" url="org/listpage" :params="{ state: 1 }"
                            search-parm-name="name" option-label="name" option-value="id" :options="orgs"
                            :multiple="true" clearable search-placeholder="请输入机构名称进行筛选">
                            <template #default="{ option }">
                                {{ option.name }} - {{ option.parentName }}
                            </template>
                        </xmks-select>
                    </el-form-item>
                    <el-form-item label="管理用户" prop="userIds">
                        <xmks-select v-model="form.userIds" url="user/listpage" :params="{ state: 1, type: 1 }"
                            search-parm-name="name" option-label="name" option-value="id" :options="users"
                            :multiple="true" clearable search-placeholder="请输入机构名称或用户名称进行筛选">
                            <template #default="{ option }">
                                {{ option.name }} - {{ option.orgName }}
                            </template>
                        </xmks-select>
                    </el-form-item>
                    <el-form-item>
                        <el-button v-if="$route.path.indexOf('add') !== -1" type="primary" class="form__btn"
                            @click="add">添加</el-button>
                        <el-button v-else type="primary" class="form__btn" @click="edit">修改</el-button>
                    </el-form-item>
                </el-form>
            </template>
        </xmks-edit-card>
        <xmks-edit-card v-if="form.id" title="重置密码"
            desc="在用户忘记密码时使用。<a href='/parm-nav/set/1' target='blank'>去设置默认密码</a>">
            <template #card-side>
                <el-button type="primary" class="form__btn" style="margin-bottom: 14px;"
                    @click="pwdInit">重置密码</el-button>
            </template>
        </xmks-edit-card>
        <xmks-edit-card v-if="form.id" title="冻结账号" desc="冻结账号">
            <template #card-main>
                <el-form size="large" class="form">
                    <el-radio-group v-model="form.state">
                        <el-radio v-for="(dict, index) in dictStore.getList('STATE_NF')" :key="index"
                            :value="parseInt(dict.dictKey)">
                            {{ dict.dictValue }}
                        </el-radio>
                    </el-radio-group>
                </el-form>
            </template>
            <template #card-side>
                <el-button type="primary" class="form__btn" @click="frozen"
                    style="margin-bottom: 30px;">保存设置</el-button>
            </template>
        </xmks-edit-card>
        <xmks-edit-card v-if="form.id" title="删除用户" desc="该子管理员创建的协助阅卷用户，也会删除。">
            <template #card-side>
                <el-button type="primary" class="form__btn" :class="{ 'form__btn--warn': delConfirm }" @click="del"
                    style="margin-bottom: 14px;">删除用户</el-button>
            </template>
        </xmks-edit-card>
    </el-scrollbar>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import type { User } from '@/ts/base/user'
import { userListpage, userAdd, userDel, userEdit, userFrozen, userGet, userPwdInit } from '@/api/base/user'
import XmksSelect from '@/components/xmks-select.vue'
import XmksEditCard from '@/components/card/xmks-card-edit.vue'
import { useDictStore } from '@/stores/dict'
import { orgListpage } from '@/api/base/org'

/************************变量定义相关***********************/
const route = useRoute()// 路由
const router = useRouter()// 路由
const formRef = ref<FormInstance>()// 表单引用
const dictStore = useDictStore()// 字典缓存
const form = reactive<User>({
    id: null,
    name: '',
    loginName: '',
    userIds: [],
    orgIds: [],
    type: 2,
    state: 1
})
const delConfirm = ref(false) // 删除确认
const formRules = reactive<FormRules>({// 表单规则
    loginName: [
        { required: true, message: '请输入登录账号', trigger: 'blur' },
        { min: 1, max: 16, message: '长度介于1-16', trigger: 'blur' },
    ],
    name: [
        { required: true, message: '请输入子管理姓名', trigger: 'blur' },
        { min: 1, max: 8, message: '长度介于1-8', trigger: 'blur' },
    ],
})
const users = ref<any[]>([])// 考试用户
const orgs = ref<any[]>([])// 机构

/************************组件生命周期相关*********************/
onMounted(async () => {
    if (route.path.indexOf('add') !== -1) {// 添加

    } else {// 修改
        const { data: { data } } = await userGet({ id: route.params.id })
        form.id = data.id
        form.name = data.name
        form.loginName = data.loginName
        form.userIds = data.userIds
        form.orgIds = data.orgIds
        form.state = data.state
    }

    if (form.userIds.length) {
        let curPage = 1
        while (true) {
            const { data: { data } } = await userListpage({
                ids: form.userIds.join(),
                state: 1, // 状态正常
                type: 1,// 考试用户
                curPage: curPage++,
                pageSize: 100,
            })

            users.value.push(...data.list)
            if (users.value.length >= data.total) {// 分批次取出
                break
            }
        }
    }

    if (form.orgIds.length) {
        let curPage = 1
        while (true) {
            const { data: { data } } = await orgListpage({
                ids: form.orgIds.join(),
                curPage: curPage++,
                pageSize: 100,
            })

            orgs.value.push(...data.list)
            if (orgs.value.length >= data.total) {// 分批次取出
                break
            }
        }
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
    const { data: { code, data } } = await userAdd({ ...form })
    if (code !== 200) {
        return
    }

    ElMessage({
        message: `密码初始化：${data.initPwd}`,
        type: 'success',
        showClose: true
    })

    router.push("/base-nav/sub-admin-list")
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
    const { data: { code, data } } = await userEdit({ ...form })
    if (code !== 200) {
        return
    }

    if (data?.initPwd) {
        ElMessage({
            message: `密码初始化：${data.initPwd}`,
            type: 'success',
            showClose: true
        })
    }

    router.push("/base-nav/sub-admin-list")
}

// 删除
async function del() {
    if (!delConfirm.value) {
        delConfirm.value = true
        return
    }

    const { data: { code } } = await userDel({
        id: form.id,
    })
    if (code !== 200) {
        return
    }

    router.push("/base-nav/sub-admin-list")
}

// 冻结
async function frozen() {
    const { data: { code } } = await userFrozen({ id: form.id, state: form.state })
    if (code !== 200) {
        return
    }

    router.push("/base-nav/sub-admin-list")
}

// 密码初始化
async function pwdInit() {
    const { data: { code, data } } = await userPwdInit({ id: form.id })
    if (code !== 200) {
        return
    }

    ElMessage({
        message: `密码初始化：${data.initPwd}`,
        type: 'success',
        showClose: true
    })

    router.push("/base-nav/sub-admin-list")
}
</script>

<style lang="scss" scoped>
.sub-admin-set {
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
