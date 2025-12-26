<template>
    <el-scrollbar max-height="calc(100vh - 240px)" class="profile">
        <xmks-edit-card title="修改密码" desc="修改密码">
            <template #card-main>
                <el-form ref="formRef" :model="form" :rules="formRules" label-width="100" size="large" class="form">
                    <el-form-item label="旧密码" prop="oldPwd">
                        <el-input v-model.trim="form.oldPwd" type="password" placeholder="请输入旧密码" />
                    </el-form-item>
                    <el-form-item label="新密码" prop="newPwd">
                        <el-input v-model.trim="form.newPwd" type="password" placeholder="请输入新密码" />
                    </el-form-item>
                    <el-form-item label="再次确认" prop="newPwd2">
                        <el-input v-model.trim="form.newPwd2" type="password" placeholder="请输入新密码" />
                    </el-form-item>
                    <el-form-item>
                        <el-button class="form__btn" @click="pwd">修改</el-button>
                    </el-form-item>
                </el-form>
            </template>
        </xmks-edit-card>
        <xmks-edit-card title="用户头像" desc="用户头像">
            <template #card-main>
                <el-form ref="avatarFormRef" :model="avatarForm" :rules="avatarFormRules" label-width="100" size="large"
                    class="form">
                    <el-form-item label="" prop="logoFileId">
                        <el-upload :http-request="customUpload" :show-file-list="false" :before-upload="uploadBefore"
                            :on-success="uploadSuccess">
                            <img v-if="avatarForm.avatarFileId" :src="`${downloadUrl}?id=${avatarForm.avatarFileId}`"
                                class="form__avatar" />
                            <span v-else class="iconfont icon-tubiaoziti2-02 form__update-icon"></span>
                        </el-upload>
                    </el-form-item>
                    <el-form-item>
                        <el-button class="form__btn" @click="avatar">保存</el-button>
                    </el-form-item>
                </el-form>
            </template>
        </xmks-edit-card>
    </el-scrollbar>
</template>

<script lang="ts" setup>
import { loginAvatar, loginPwd } from '@/api/login'
import { fileUpload } from '@/api/sys/file'
import XmksEditCard from '@/components/card/xmks-card-edit.vue'
import http from '@/request'
import { useUserStore } from '@/stores/user'
import { ElMessage, type FormInstance, type FormRules, type UploadFile, type UploadFiles, type UploadRawFile, type UploadRequestOptions } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

/************************变量定义相关***********************/
const userStore = useUserStore()// 用户缓存
const router = useRouter()// 路由
const formRef = ref<FormInstance>()// 表单引用
const form = reactive({
    oldPwd: '',
    newPwd: '',
    newPwd2: '',
})
const formRules = reactive<FormRules>({// 表单规则
    oldPwd: [
        { required: true, message: '请输入旧密码', trigger: 'blur' },
    ],
    newPwd: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 6, max: 20, message: '长度介于6-20', trigger: 'blur' },
    ],
    newPwd2: [{
        trigger: 'blur',
        validator: (rule: any, value: any, callback: any) => {
            if (!value) {
                return callback(new Error('请输入新密码'))
            }
            if (form.newPwd != form.newPwd2) {
                return callback(new Error('两次密码不一致'))
            }
            return callback()
        }
    }],
})

const downloadUrl = `${http.defaults.baseURL}file/download`// 下载地址
const avatarForm = reactive({// 系统表单
    avatarFileId: userStore.avatarFileId, // 头像附件ID
})
const avatarFormRef = ref<FormInstance>() // 系统表单引用
const avatarFormRules = reactive<FormRules>({// 系统表单校验规则
    name: [
        { required: true, message: '请输入系统名称', trigger: 'blur' },
        { min: 1, max: 16, message: '长度介于1-16', trigger: 'blur' },
    ],
    logoFileId: [
        // { required: true, message: '请上传Logo', trigger: 'blur' },
    ],
})

/************************组件生命周期相关*********************/
onMounted(async () => {
})

/************************事件相关*****************************/
// 密码修改
async function pwd() {
    // 数据校验
    try {
        await formRef.value?.validate()
    } catch (e) {
        return
    }

    // 密码修改
    const { data: { code } } = await loginPwd({ ...form })
    if (code !== 200) {
        return
    }

    router.push('/home')
}

// 上传
async function customUpload(options: UploadRequestOptions) {
    const { file } = options
    const formData = new FormData()
    formData.append('files', file)
    const response = await fileUpload(formData)

    if (response.data.code === 200) {
        return response.data
    }

    throw new Error(response.data.msg);
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
    avatarForm.avatarFileId = response.data.fileIds
}

// 头像更新
async function avatar() {
    if (!avatarForm.avatarFileId) {
        ElMessage.error('请上传头像')
        return
    }

    const { data: { code } } = await loginAvatar({ ...avatarForm })
    if (code !== 200) {
        return
    }

    userStore.avatarFileId = avatarForm.avatarFileId

    router.push('/home')
}
</script>

<style lang="scss" scoped>
.profile {
    display: flex;
    flex-direction: column;
    width: 1200px;
    margin: 20px 0px;
    padding: 0px 0px 0px 0px;
    border-radius: 15px 15px 15px 15px;

    .form {
        margin-top: 20px;

        :deep(.form__update-icon) {
            width: 100px;
            height: 100px;
            text-align: center;
            align-content: center;
            border: 1px dashed #dcdcdc;
            border-radius: 6px;
            font-size: 28px;
            color: #8c939d;
            cursor: pointer;
            position: relative;
            overflow: hidden;
            transition: 0.2s;

            &:hover {
                border-color: #04C7F2;
            }
        }

        .form__avatar {
            width: 100px;
            height: 100px;
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
