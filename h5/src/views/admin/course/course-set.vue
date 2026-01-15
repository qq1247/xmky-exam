<template>
    <el-scrollbar max-height="calc(100vh - 190px)" class="course-set">
        <xmks-edit-card title="课程" desc="课程">
            <template #card-main>
                <el-form ref="formRef" :model="form" :rules="formRules" label-width="100" size="large" class="form">
                    <el-form-item label="名称" prop="name">
                        <el-input v-model="form.name" placeholder="请输入名称" />
                    </el-form-item>
                    <el-form-item label="简介" prop="content">
                        <el-input v-model="form.content" type="textarea" :autosize="{ minRows: 2, maxRows: 4 }"
                            :show-word-limit="true" maxlength="128" placeholder="请输入简介" />
                    </el-form-item>
                    <el-form-item label="机构及用户" prop="orgIds">
                        <xmks-select v-model="form.orgIds" url="org/listpage" :params="{}" search-parm-name="name"
                            option-label="name" option-value="id" :options="orgs" :multiple="true" clearable
                            :page-size="100" search-placeholder="请输入机构名称进行筛选" placeholder="请选择机构">
                            <template #default="{ option }">
                                {{ option.name }} - {{ option.orgName }}
                            </template>
                        </xmks-select>
                    </el-form-item>
                    <el-form-item label="" prop="userIds">
                        <xmks-select v-model="form.userIds" url="user/listpage"
                            :params="{ state: 1, role: 'EXAM_USER' }" search-parm-name="name" option-label="name"
                            option-value="id" :options="users" :multiple="true" clearable :page-size="100"
                            search-placeholder="请输入机构名称或用户名称进行筛选" placeholder="请选择用户">
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
        <xmks-edit-card v-if="form.id" title="发布课程" desc="发布课程">
            <template #card-main>
                <el-form size="large" class="form">
                    <el-radio-group v-model="form.state">
                        <el-radio v-for="(dict, index) in dictStore.getList('STATE_PS')" :key="index"
                            :value="parseInt(dict.dictKey)">
                            {{ dict.dictValue }}
                        </el-radio>
                    </el-radio-group>
                </el-form>
            </template>
            <template #card-side>
                <el-button type="primary" class="form__btn" @click="state" style="margin-bottom: 30px;">保存设置</el-button>
            </template>
        </xmks-edit-card>
        <xmks-edit-card v-if="shareForm.id" title="共享权限" desc="私有：仅自己管理；只读：多子管理员可使用；读写：多子管理员可编辑。">
            <template #card-main>
                <el-form ref="shareFormRef" :model="shareForm" :rules="shareFormRules" label-width="100" size="large"
                    class="form">
                    <el-form-item label="设置权限" prop="name">
                        <el-radio-group v-model="shareForm.shareAuth">
                            <el-radio v-for="(option, index) in dictStore.getList('SHARE_AUTH')" :key="index"
                                :value="parseInt(option.dictKey)">
                                {{ option.dictValue }}
                            </el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-form>
            </template>
            <template #card-side>
                <el-button type="primary" class="form__btn" style="margin-bottom: 14px;" @click="share">保存设置</el-button>
            </template>
        </xmks-edit-card>
        <xmks-edit-card v-if="form.id" title="删除课程" desc="删除课程">
            <template #card-side>
                <el-button type="primary" class="form__btn" :class="{ 'form__btn--warn': delConfirm }" @click="del"
                    style="margin-bottom: 14px;">删除课程</el-button>
            </template>
        </xmks-edit-card>
    </el-scrollbar>
</template>

<script lang="ts" setup>
import { courseAdd, courseDel, courseEdit, courseGet, courseShare, courseState } from '@/api/course/course'
import XmksEditCard from '@/components/card/xmks-card-edit.vue'
import xmksSelect from '@/components/xmks-select.vue'
import { useDictStore } from '@/stores/dict'
import { useUserStore } from '@/stores/user'
import type { Course } from '@/ts/course/course'
import { type FormInstance, type FormRules } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

/************************变量定义相关***********************/
const route = useRoute()// 路由
const router = useRouter()// 路由
const userStore = useUserStore()// 字典缓存
const dictStore = useDictStore()// 字典缓存
const formRef = ref<FormInstance>()// 表单引用
const form = reactive<Course>({
    id: null,
    name: '',
    content: '',
    orgIds: [],
    userIds: [],
    shareAuth: null,
    state: null
})
const formRules = reactive<FormRules>({// 表单规则
    name: [
        { required: true, message: '请输入名称', trigger: 'blur' },
        { min: 1, max: 16, message: '长度介于1-16', trigger: 'blur' },
    ],
    content: [
        // { required: true, message: '请输入简介', trigger: 'blur' },
        { min: 0, max: 128, message: '长度介于0-128', trigger: 'blur' },
    ],
    shareAuth: [
        { required: true, message: '请选择共享权限', trigger: 'blur' },
    ],
    userIds: [
        {
            validator: (rule: any, value: number[], callback: any) => {
                if (!form.userIds.length && !form.orgIds?.length) {
                    return callback(new Error("机构或用户最少选一个"))
                }
                return callback()
            }
        },
    ],
    orgIds: [
        { required: false, message: '请选择学习机构', trigger: 'blur' },
    ],
})

const shareFormRef = ref<FormInstance>()// 表单引用
const shareForm = reactive({// 表单
    id: null,
    shareAuth: null,
    createUserId: null,
})
const shareFormRules = reactive<FormRules>({// 表单校验规则
    shareAuth: [
        { required: true, message: '请选择共享权限', trigger: 'blur' },
    ],
})

const delConfirm = ref(false) // 删除确认
const users = ref<Record<string, unknown>[]>([]) // 用户列表
const orgs = ref<Record<string, unknown>[]>([]) // 机构列表

/************************组件生命周期相关*********************/
onMounted(async () => {
    if (route.path.indexOf('add') !== -1) {// 添加

    } else {// 修改
        const { data: { data } } = await courseGet({ id: route.params.id })
        form.id = data.id
        form.name = data.name
        form.content = data.content
        form.shareAuth = data.shareAuth
        form.userIds = data.users.map((user: any) => user.id)
        form.orgIds = data.orgs.map((org: any) => org.id)
        form.state = data.state

        users.value = data.users
        orgs.value = data.orgs

        shareForm.id = data.id
        shareForm.shareAuth = data.shareAuth
        shareForm.createUserId = data.createUserId
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
    const { data: { code } } = await courseAdd({ ...form })
    if (code !== 200) {
        return
    }

    router.push("/course-list")
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
    const { data: { code } } = await courseEdit({ ...form })
    if (code !== 200) {
        return
    }

    router.push("/course-list")
}

// 发布
async function state() {
    const { data: { code } } = await courseState({ id: form.id })
    if (code !== 200) {
        return
    }

    router.push("/course-list")
}

// 共享权限
async function share() {
    // 数据校验
    try {
        await shareFormRef.value?.validate()
    } catch (e) {
        return
    }

    // 设置权限
    const { data: { code } } = await courseShare({ id: shareForm.id, shareAuth: shareForm.shareAuth })
    if (code !== 200) {
        return
    }

    router.push("/course-list")
}

// 删除
async function del() {
    if (!delConfirm.value) {
        delConfirm.value = true
        return
    }

    const { data: { code } } = await courseDel({ id: form.id })
    if (code !== 200) {
        return
    }

    router.push("/course-list")
}

</script>

<style lang="scss" scoped>
.course-set {
    display: flex;
    flex-direction: column;
    width: 1200px;
    background-color: #FFFFFF;
    margin: 20px 0px;
    padding: 40px 30px 0px 30px;
    border-radius: 15px 15px 15px 15px;

    .form {
        margin-top: 20px;

        .form__divide {
            display: flex;
            justify-content: center;
            width: 50px;
        }
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
