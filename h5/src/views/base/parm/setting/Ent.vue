<template>
    <el-card class="edit" shadow="never">
        <template #header>
            <div class="edit-title">企业信息</div>
            <div class="edit-desc">变更首页左上角的企业名称、Logo</div>
        </template>
        <el-form ref="formRef" :model="form" :rules="formRules" label-width="100" size="large">
            <el-form-item label="企业名称" prop="name">
                <el-input v-model="form.name" placeholder="请输入企业名称"/>
            </el-form-item>
            <el-form-item label="Logo" prop="logoFileId">
                <el-upload
                    :action="`${ uploadUrl }`"
                    :headers="{Authorization: userStore.accessToken}"
                    name="files"
                    :show-file-list="false"
                    :before-upload="uploadBefore"
                    :on-success="uploadSuccess"
                    class="avatar-uploader"
                >
                    <img :src="logoUrl" class="avatar" />
                    <!-- <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon> -->
                </el-upload>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="save">保存</el-button>
            </el-form-item>
        </el-form>
    </el-card>
</template>

<script lang="ts" setup>
import { reactive, onMounted, ref } from 'vue'
import http from '@/request'
import { ElMessage, type FormInstance, type FormRules, type UploadFile, type UploadFiles, type UploadRawFile } from 'element-plus';
import { useUserStore } from '@/stores/user';
import { useRouter } from 'vue-router';

// 定义变量
const router = useRouter()
const userStore = useUserStore()
const form = reactive({// 表单
    name: null,// 企业名称
    logoFileId: null, // 上传附件ID
})
const logoUrl = ref(`${ http.defaults.baseURL }login/logo`)// logo链接
const uploadUrl = `${ http.defaults.baseURL }file/upload`// 上传地址
const downloadUrl = `${ http.defaults.baseURL }file/download`// 下载地址
const formRef = ref<FormInstance>() // 表单引用
const formRules = reactive<FormRules>({// 表单校验规则
    name: [
        { required: true, message: '请输入企业名称', trigger: 'blur' },
        { min: 1, max: 16, message: '长度介于1-16', trigger: 'blur' },
    ],
    logoFileId: [
        // { required: true, message: '请上传Logo', trigger: 'blur' },
    ],
})

// 组件挂载完成后，执行如下方法
onMounted(async() => {
    let { data: { data } } = await http.post("parm/get")
    form.name = data.entName
})

// 保存
async function save() {
    if (!formRef.value) return
    await formRef.value.validate(async (valid, fields) => {
        if (!valid) {
            return
        }

        let { data: { code, msg } } = await http.post("parm/ent", {...form})
        if (code === 200) {
            ElMessage.success('保存成功')
        }

        router.go(0)
    })
}

// 上传之前处理
function uploadBefore (rawFile: UploadRawFile) {
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
    form.logoFileId = response.data.fileIds
    logoUrl.value = `${ downloadUrl }?id=${ form.logoFileId }`
}
</script>

<style lang="scss" scoped>
.edit {
    padding: 10px;

    :deep(.el-card__header) {
        padding-bottom: 0px;

        .edit-title {
            font-size: 14px;
            font-weight: bold;
            padding-bottom: 5px;
        }

        .edit-desc {
            font-size: 12px;
            color: var(--el-text-color-secondary);
        }
    }
}

.avatar-uploader .avatar {// elementui例子样式
  width: 178px;
  height: 178px;
  display: block;
}


:deep(.avatar-uploader .el-upload) {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

:deep(.avatar-uploader .el-upload:hover) {
  border-color: var(--el-color-primary);
}

:deep(.el-icon.avatar-uploader-icon) {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
}

</style>