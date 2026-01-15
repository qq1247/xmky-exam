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
        <xmks-edit-card v-if="shareForm.id" title="参考资料" desc="基于企业内部权威资料，在用户练题时提供相关参考，帮助其理解相关背景与依据。资料须与题库高度相关，以保障体验。">
            <template #card-main>
                <el-form ref="shareFormRef" :model="shareForm" :rules="shareFormRules" label-width="100" size="large"
                    class="form">
                    <el-tag v-for="file in files" :key="file.id" closable class="form__tag"
                        @close="() => docDel(file.id as number)">
                        {{ file.name }}
                    </el-tag>
                </el-form>
            </template>
            <template #card-side>
                <el-upload :http-request="customUpload" :show-file-list="false" accept=".doc,.docx,.pdf,DOC,DOCX,PDF"
                    :limit="4" :before-upload="uploadBefore" :multiple="true" :on-success="uploadSuccess">
                    <el-button type="primary" class="form__btn" style="margin-bottom: 14px;">上传资料</el-button>
                </el-upload>
            </template>
        </xmks-edit-card>
        <xmks-edit-card v-if="form.id" title="移动试题" desc="移动试题">
            <template #card-main>
                <el-form ref="moveFormRef" :model="moveForm" :rules="moveFormRules" inline label-width="100"
                    size="large" class="form">
                    <!-- 100% 超出界面 -->
                    <el-form-item label="选试题" prop="questionIds" style="width: 100%;">
                        <xmks-select v-model="moveForm.questionIds" url="question/listpage"
                            :params="{ questionBankId: moveForm.id }" search-parm-name="title" option-label="title"
                            option-value="id" :multiple="true" clearable :page-size="100" search-placeholder="请输入题干进行筛选"
                            placeholder="请输入题干进行筛选">
                            <template #default="{ option }">
                                {{ option.id }} - {{ option.title }}
                            </template>
                        </xmks-select>
                    </el-form-item>
                    <el-form-item label="移动到" prop="questionBankId" style="width: 100%;">
                        <xmks-select v-model="moveForm.questionBankId" url="question-bank/listpage" :params="{}"
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
import { ElMessage, type FormInstance, type FormRules, type UploadFile, type UploadFiles, type UploadRawFile, type UploadRequestOptions } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import XmksEditCard from '@/components/card/xmks-card-edit.vue'
import type { QuestionBank } from '@/ts/exam/question-bank'
import { questionBankAdd, questionBankDel, questionBankEdit, questionBankGet, questionBankClear, questionBankShare, questionBankDocAdd, questionBankDocDel } from '@/api/exam/question-bank'
import XmksSelect from '@/components/xmks-select.vue'
import { questionMove } from '@/api/exam/question'
import { useDictStore } from '@/stores/dict'
import { useUserStore } from '@/stores/user'
import { fileUpload } from '@/api/sys/file'

/************************变量定义相关***********************/
const route = useRoute()// 路由
const router = useRouter()// 路由
const dictStore = useDictStore()// 字典缓存
const userStore = useUserStore()// 字典缓存
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

const files = ref<{ id: string | number; name: string }[]>([])

/************************组件生命周期相关*********************/
onMounted(async () => {
    if (route.path.indexOf('add') !== -1) {// 添加

    } else {// 修改
        const { data: { data } } = await questionBankGet({ id: route.params.id })
        form.id = data.id
        form.name = data.name

        moveForm.id = data.id

        shareForm.id = data.id
        shareForm.shareAuth = data.shareAuth
        shareForm.createUserId = data.createUserId

        files.value = data.files
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

// 共享权限
async function share() {
    // 数据校验
    try {
        await shareFormRef.value?.validate()
    } catch (e) {
        return
    }

    // 设置权限
    const { data: { code } } = await questionBankShare({ id: shareForm.id, shareAuth: shareForm.shareAuth })
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

// 上传之前处理
function uploadBefore(rawFile: UploadRawFile) {
    const allowedTypes = [
        'application/msword', // .doc
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document', // .docx
        'application/pdf' // .pdf

    ]

    if (!allowedTypes.includes(rawFile.type)) {
        ElMessage.error('只允许上传 doc、docx 和 pdf')
        return false
    }
    if (rawFile.size / 1024 > 10240) {
        ElMessage.error('最大支持10兆')
        return false
    }

    return true
}

// 自定义上传
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

// 上传成功处理
async function uploadSuccess(response: any, uploadFile: UploadFile, uploadFiles: UploadFiles) {
    await questionBankDocAdd({ id: route.params.id, fileId: response.data.fileIds })

    const { data: { data } } = await questionBankGet({ id: route.params.id })
    files.value = data.files
}

// 附件删除
async function docDel(fileId: number) {
    await questionBankDocDel({ id: route.params.id, fileId })

    const { data: { data } } = await questionBankGet({ id: route.params.id })
    files.value = data.files
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

        :deep(.form__radio) {
            &.is-active {
                .el-radio-button__inner {
                    background-color: #259FF8;
                }
            }
        }
    }

    .form__tag {
        height: 22px;
        padding: 0px 10px;
        font-size: 12px;
        margin: 10px 10px 0px 0px;

        color: #1EA1EE;
        background-color: #E4F6FF;
        border: 1px solid #C0EAFF;
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
