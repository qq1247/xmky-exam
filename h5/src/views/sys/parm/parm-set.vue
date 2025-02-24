<template>
    <el-scrollbar max-height="calc(100vh - 190px)" class="parm-set">
        <xmks-edit-card title="默认密码" desc="添加新用户或重置密码时，设置密码生成策略。">
            <template #card-main>
                <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdFormRules" label-width="100" size="large"
                    :inline="true" class="form">
                    <el-form-item label="生成策略" prop="type">
                        <el-radio-group v-model="pwdForm.type">
                            <el-radio :value="1">随机密码</el-radio>
                            <el-radio :value="2">固定密码</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item v-if="pwdForm.type === 2" label="" prop="value">
                        <el-input v-model="pwdForm.value" type="password" placeholder="请输入密码" />
                    </el-form-item>
                </el-form>
            </template>
            <template #card-side>
                <el-button type="primary" class="form__btn" @click="pwdSave"
                    style="margin-bottom: 40px;">保存设置</el-button>
            </template>
        </xmks-edit-card>
        <xmks-edit-card title="企业信息" desc="企业信息">
            <template #card-main>
                <el-form ref="entFormRef" :model="entForm" :rules="entFormRules" label-width="100" size="large"
                    :inline="true" class="form">
                    <el-form-item label="企业名称" prop="name">
                        <el-input v-model="entForm.name" placeholder="请输入企业名称" />
                    </el-form-item>
                    <el-form-item label="Logo" prop="logoFileId">
                        <el-upload :action="`${uploadUrl}`" :headers="{ Authorization: userStore.accessToken }"
                            name="files" :show-file-list="false" :before-upload="uploadBefore"
                            :on-success="uploadSuccess">
                            <img :src="logoUrl" class="form__logo" />
                        </el-upload>
                    </el-form-item>
                </el-form>
            </template>
            <template #card-side>
                <el-button type="primary" class="form__btn" @click="entSave"
                    style="margin-bottom: 40px;">保存设置</el-button>
            </template>
        </xmks-edit-card>
        <xmks-edit-card title="移动端设置" desc="用于生成移动端扫码答题的链接">
            <template #card-main>
                <el-form ref="mFormRef" :model="mForm" :rules="mFormRules" label-width="100" size="large" :inline="true"
                    class="form">
                    <el-form-item label="移动端主机" prop="host">
                        <el-input v-model="mForm.host"
                            placeholder="如：http://192.168.1.99:8080 或 https://exam.xxx.com/m" />
                    </el-form-item>
                </el-form>
            </template>
            <template #card-side>
                <el-button type="primary" class="form__btn" @click="mSave" style="margin-bottom: 40px;">保存设置</el-button>
            </template>
        </xmks-edit-card>
        <xmks-edit-card title="自定义信息" desc="自定义内容">
            <template #card-main>
                <el-form ref="customFormRef" :model="customForm" :rules="customFormRules" label-width="100" size="large"
                    :inline="false" class="form">
                    <el-form-item label="标题" prop="title">
                        <el-input v-model="customForm.title" placeholder="请输入标题" />
                    </el-form-item>
                    <el-form-item label="内容" prop="content">
                        <el-input v-model="customForm.content" placeholder="请输入内容" :autosize="{ minRows: 2 }"
                            type="textarea" />
                    </el-form-item>
                </el-form>
            </template>
            <template #card-side>
                <el-button type="primary" class="form__btn" @click="customSave"
                    style="margin-bottom: 40px;">保存设置</el-button>
            </template>
        </xmks-edit-card>
        <xmks-edit-card title="刷新缓存" desc="直接在数据库修改用户、考试等信息时，因为做了缓存处理，点击刷新按钮同步">
            <template #card-side>
                <el-button type="primary" class="form__btn" @click="refresh"
                    style="margin-bottom: 14px;">刷新缓存</el-button>
            </template>
        </xmks-edit-card>
    </el-scrollbar>
</template>

<script lang="ts" setup>
import type { ParmPwd } from '@/ts/sys/parm'
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { parmCustom, parmEnt, parmGet, parmM, parmPwd } from '@/api/sys/parm'
import XmksEditCard from '@/components/card/xmks-card-edit.vue'
import http from '@/request'
import { ElMessage, type FormInstance, type FormRules, type UploadFile, type UploadFiles, type UploadRawFile } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { useCustomStore } from '@/stores/custom'
import { cacheRefresh } from '@/api/sys/cache'

/************************变量定义相关***********************/
const route = useRoute()// 路由
const router = useRouter()// 路由
const userStore = useUserStore()// 用户缓存
const customStore = useCustomStore()// 自定义缓存

const pwdFormRef = ref<FormInstance>()// 密码表单引用
const pwdForm = reactive<ParmPwd>({// 密码表单
    type: 1,
    value: '',
})
const pwdFormRules = reactive<FormRules>({// 密码表单规则
    value: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 1, max: 16, message: '长度介于1-16', trigger: 'blur' },
    ],
})

const entForm = reactive({// 企业表单
    name: '',// 企业名称
    logoFileId: null, // logo附件ID
})
const logoUrl = ref(`${http.defaults.baseURL}login/logo?t=${new Date().getTime()}`)// logo链接
const uploadUrl = `${http.defaults.baseURL}file/upload`// 上传地址
const downloadUrl = `${http.defaults.baseURL}file/download`// 下载地址
const entFormRef = ref<FormInstance>() // 企业表单引用
const entFormRules = reactive<FormRules>({// 企业表单校验规则
    name: [
        { required: true, message: '请输入企业名称', trigger: 'blur' },
        { min: 1, max: 16, message: '长度介于1-16', trigger: 'blur' },
    ],
    logoFileId: [
        // { required: true, message: '请上传Logo', trigger: 'blur' },
    ],
})

const mForm = reactive({// 移动端表单
    host: '',// 主机地址
})
const mFormRef = ref<FormInstance>() // 移动端表单引用
const mFormRules = reactive<FormRules>({// 移动端表单校验规则
    host: [
        { required: true, message: '请输入移动端访问地址', trigger: 'blur' },
        { min: 1, max: 128, message: '长度介于1-128', trigger: 'blur' },
    ],
})

const customForm = reactive({// 表单
    title: '',// 服务名称
    content: '',// 服务内容
})
const customFormRef = ref<FormInstance>() // 表单引用
const customFormRules = reactive<FormRules>({// 表单校验规则
    title: [
        { required: true, message: '请输入标题', trigger: 'blur' },
        { min: 1, max: 16, message: '长度介于1-16', trigger: 'blur' },
    ],
    content: [
        { required: true, message: '请输入内容', trigger: 'blur' },
        { min: 1, max: 128, message: '长度介于1-128', trigger: 'blur' },
    ],
})

/************************组件生命周期相关*********************/
onMounted(async () => {
    if (route.path.indexOf('add') !== -1) {// 添加

    } else {// 修改
        const { data: { data } } = await parmGet({ id: route.params.id })
        pwdForm.type = data.pwdType
        pwdForm.value = data.pwdValue
        entForm.name = data.entName
        mForm.host = data.mHost
        customForm.title = data.customTitle
        customForm.content = data.customContent
    }
})

/************************事件相关*****************************/

// 密码修改
async function pwdSave() {
    // 数据校验
    if (!pwdFormRef.value || !await pwdFormRef.value.validate()) {
        return
    }

    // 修改
    const { data: { code } } = await parmPwd({ ...pwdForm })
    if (code !== 200) {
        return
    }

    router.push("/sys-nav/parm-list")
}

// 企业修改
async function entSave() {
    // 数据校验
    if (!entFormRef.value || !await entFormRef.value.validate()) {
        return
    }

    const { data: { code } } = await parmEnt({ ...entForm })
    if (code !== 200) {
        return
    }

    userStore.sysName = entForm.name
    document.title = userStore.sysName

    router.push("/sys-nav/parm-list")
}

// 移动端修改
async function mSave() {
    // 数据校验
    if (!mFormRef.value || !await mFormRef.value.validate()) {
        return
    }

    const { data: { code } } = await parmM({ ...mForm })
    if (code !== 200) {
        return
    }

    router.push("/sys-nav/parm-list")
}

// 自定义修改
async function customSave() {
    // 数据校验
    if (!customFormRef.value || !await customFormRef.value.validate()) {
        return
    }

    const { data: { code } } = await parmCustom({ ...customForm })
    if (code !== 200) {
        return
    }

    customStore.title = customForm.title
    customStore.content = customForm.content.replaceAll('\n', '<br/>')


    router.push("/sys-nav/parm-list")
}

// 刷新缓存
async function refresh() {
    const { data: { code } } = await cacheRefresh({
        cacheNames: ['USER_CACHE', 'ORG_CACHE', 'PARM_CACHE', 'PROGRESS_BAR_CACHE', 'QUESTION_CACHE', 'EXAM_CACHE', 'MYEXAM_CACHE', 'MYQUESTION_CACHE']
    })
    if (code !== 200) {
        return
    }

    router.push("/sys-nav/parm-list")
}

// 上传之前处理
function uploadBefore(rawFile: UploadRawFile) {
    if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
        ElMessage.error('只允许 jpg和png 格式')
        return false
    }
    if (rawFile.size / 1024 > 512) {
        ElMessage.error('最大0.5兆')
        return false
    }

    return true
}

// 上传成功处理
function uploadSuccess(response: any, uploadFile: UploadFile, uploadFiles: UploadFiles) {
    entForm.logoFileId = response.data.fileIds
    logoUrl.value = `${downloadUrl}?id=${entForm.logoFileId}`
}

</script>

<style lang="scss" scoped>
.parm-set {
    display: flex;
    flex-direction: column;
    width: 1200px;
    background-color: #FFFFFF;
    margin: 20px 0px;
    padding: 40px 30px 0px 30px;
    border-radius: 15px 15px 15px 15px;

    .form {
        margin-top: 20px;

        .form__logo {
            width: 50px;
            height: 50px;
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
