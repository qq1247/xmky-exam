<template>
    <el-scrollbar max-height="calc(100vh - 190px)" class="course-material-set">
        <xmks-edit-card title="课程资料" desc="课程资料">
            <template #card-main>
                <el-form ref="formRef" :model="form" :rules="formRules" label-width="100" size="large" class="form">
                    <el-form-item label="视频" prop="videoFileId">
                        <div class="img-group">
                            <div v-if="videoOptions.src" class="img">
                                <longze-video-play ref="videoPlayerRef" v-bind="videoOptions"></longze-video-play>
                                <div class="img__inner" style="margin-top: 10px;">
                                    <span class="img__txt">视频</span>
                                    <span @click.stop="videoUploadRef!.clearFiles(); videoOptions.src = ''"
                                        class="iconfont icon-shanchu img__btn"></span>
                                </div>
                            </div>
                            <el-upload ref="videoUploadRef" v-model:file-list="videoFileList"
                                :http-request="customUpload" :show-file-list="false" accept=".mp4,.MP4" :limit="1"
                                :before-upload="uploadBeforeOfVideo" :multiple="false"
                                :on-success="uploadSuccessOfVideo" :on-exceed="uploadExceedOfVideo"
                                :on-remove="uploadRemoveOfVideo">
                                <span class="iconfont icon-tubiaoziti2-02"></span>
                            </el-upload>
                        </div>
                    </el-form-item>
                    <el-form-item label="名称" prop="name">
                        <el-input v-model="form.name" placeholder="请输入名称" />
                    </el-form-item>
                    <el-form-item label="简介" prop="content">
                        <el-input v-model="form.content" type="textarea" :autosize="{ minRows: 2, maxRows: 4 }"
                            :show-word-limit="true" maxlength="128" placeholder="请输入简介" />
                    </el-form-item>
                    <el-form-item label=" 答题" prop="courseQuestions">
                        <div v-for="(courseTime, index) in form.courseQuestions" :key="index" class="form__times">
                            <el-time-picker v-model="courseTime.courseTime" format="HH:mm:ss" value-format="HH:mm:ss"
                                placeholder="请选择时间" />
                            <xmks-select v-model="(courseTime.questionId as number)" url="question/listpage"
                                :params="{}" search-parm-name="title" option-label="title" option-value="id"
                                :options="questions" :multiple="false" clearable :page-size="5"
                                search-placeholder="请输入题干进行筛选" placeholder="请选择试题">
                                <template #default="{ option }">
                                    {{ option.id }} - {{ option.title }}
                                </template>
                            </xmks-select>
                            <span class="iconfont icon-tubiaoziti2-01 form__times-btn" @click="delOption(index)"></span>
                        </div>
                    </el-form-item>
                    <el-form-item>
                        <el-button :disabled="form.courseQuestions.length >= 60" type="primary" size="small" plain
                            class="form__option-btn" @click="addOption">
                            <span class="iconfont icon-tubiaoziti2-02 form__option-btn-icon"
                                style="font-size: 12px;"></span>
                            <span class="form__option-btn-txt">添加时间</span>
                        </el-button>
                    </el-form-item>
                    <el-form-item label="第" prop="no">
                        <el-input-number v-model="form.no" :min="1" :max="100" :precision="0" controls-position="right"
                            size="large" />&nbsp;小节
                    </el-form-item>
                    <el-form-item>
                        <el-button v-if="$route.path.indexOf('add') !== -1" type="primary" class="form__btn"
                            @click="add">添加</el-button>
                        <el-button v-else type="primary" class="form__btn" @click="edit">修改</el-button>
                    </el-form-item>
                </el-form>
            </template>
        </xmks-edit-card>
        <xmks-edit-card v-if="form.id" title="删除课程资料" desc="删除课程资料" class="form">
            <template #card-side>
                <el-button type="primary" class="form__btn" :class="{ 'form__btn--warn': delConfirm }" @click="de1"
                    style="margin-bottom: 14px;">删除课程资料</el-button>
            </template>
        </xmks-edit-card>
    </el-scrollbar>
</template>

<script lang="ts" setup>
import { courseMaterialAdd, courseMaterialDel, courseMaterialEdit, courseMaterialGet } from '@/api/course/course-material'
import { fileUpload } from '@/api/sys/file'
import XmksEditCard from '@/components/card/xmks-card-edit.vue'
import XmksSelect from '@/components/xmks-select.vue'
import http from '@/request'
import type { CourseMaterial } from '@/ts/course/course-material'
import type { Question } from '@/ts/exam/question'
import { ElMessage, genFileId, type FormInstance, type FormRules, type UploadFile, type UploadFiles, type UploadInstance, type UploadRawFile, type UploadRequestOptions, type UploadUserFile } from 'element-plus'
import { longzeVideoPlay } from "longze-vue3-video-player"
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

/************************变量定义相关***********************/
const route = useRoute()// 路由
const router = useRouter()// 路由
const formRef = ref<FormInstance>()// 表单引用
const form = reactive<CourseMaterial>({
    id: null,
    name: '',
    content: '',
    videoFileId: null,
    courseQuestions: [],
    no: 1,
})
const formRules = reactive<FormRules>({// 表单规则
    name: [
        { required: true, message: '请输入名称', trigger: 'blur' },
        { min: 1, max: 16, message: '长度介于1-16', trigger: 'blur' },
    ],
    content: [
        { required: true, message: '请输入简介', trigger: 'blur' },
        { min: 1, max: 128, message: '长度介于1-128', trigger: 'blur' },
    ],
    videoFileId: [{
        trigger: 'blur',
        validator: (rule: any, value: any, callback: any) => {
            if (!videoFileList.value[0]?.uid) {
                return callback(new Error('请上传视频'));
            }

            return callback()
        }
    }],
    no: [
        { required: true, message: '请选择排序', trigger: 'blur' },
    ],
    courseQuestions: [{
        trigger: 'blur',
        validator: (rule: any, value: any, callback: any) => {
            if (!Array.isArray(value) || value.length === 0) {
                return callback();
            }
            const courseTimes = new Set<string>();
            for (let i = 0; i < value.length; i++) {
                const item = value[i];
                if (!item.courseTime) {
                    return callback(new Error(`第 ${i + 1} 行：请选择时间`));
                }
                if (!item.questionId) {
                    return callback(new Error(`第 ${i + 1} 行：请选择试题`));
                }
                if (courseTimes.has(item.courseTime)) {
                    return callback(new Error(`第 ${i + 1} 行：时间不能重复`));
                }
                courseTimes.add(item.courseTime);
            }
            return callback()
        }
    }],
})
const questions = ref<Question[]>([]) // 试题列表
const delConfirm = ref(false) // 删除确认
const downloadUrl = `${http.defaults.baseURL}file/download`// 下载地址
const videoUploadRef = ref<UploadInstance>() // 视频上传引用
const videoFileList = ref<UploadUserFile[]>([]) // 视频文件列表
const videoPlayerRef = ref()// 视频播放器引用
const videoOptions = reactive({
    width: "600px", //播放器宽度
    height: "400px", //播放器高度
    color: "#409eff", //主题色
    title: "视频", //视频名称
    src: "", //视频源
    muted: false, //静音
    webFullScreen: false,
    speedRate: ["0.5", "1.0", "2.0"], //播放倍速
    autoPlay: false, //自动播放
    loop: false, //循环播放
    mirror: false, //镜像画面
    ligthOff: false, //关灯模式
    volume: 0.3, //默认音量大小
    control: true, //是否显示控制
    controlBtns: [
        "audioTrack",
        "quality",
        "speedRate",
        "volume",
        "fullScreen",
    ], //显示所有按钮,
});
/************************组件生命周期相关*********************/
onMounted(async () => {
    if (route.path.indexOf('add') !== -1) {// 添加

    } else {// 修改
        const { data: { data } } = await courseMaterialGet({ id: route.params.id })
        form.id = data.id
        form.name = data.name
        form.content = data.content
        form.videoFileId = data.videoFileId
        form.no = data.no
        if (form.videoFileId) {
            videoOptions.src = `${downloadUrl}?id=${form.videoFileId}`
            videoFileList.value.push({
                uid: form.videoFileId,
                url: `${downloadUrl}?id=${form.videoFileId}`,
                name: `${form.videoFileId}`
            })
        }
        form.courseQuestions = data.courseQuestions

        questions.value = data.courseQuestions.map((courseQuestion: any) => ({
            id: courseQuestion.questionId,
            title: courseQuestion.questionTitle,
        }) as Question)
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
    const params = {
        name: form.name,
        content: form.content,
        courseId: route.params.courseId,
        videoFileId: videoFileList.value[0]?.uid,
        courseTimes: [] as string[],
        questionIds: [] as string[],
        no: form.no,
    };

    form.courseQuestions.forEach((q) => {
        params.courseTimes.push(q.courseTime ?? '');
        params.questionIds.push(q.questionId?.toString() ?? '');
    });

    const { data: { code } } = await courseMaterialAdd(params)
    if (code !== 200) {
        return
    }

    router.push(`/course/course-nav/list/${route.params.courseId}`)
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
    const params = {
        id: form.id,
        name: form.name,
        content: form.content,
        courseId: route.params.courseId,
        videoFileId: videoFileList.value[0]?.uid,
        courseTimes: [] as string[],
        questionIds: [] as string[],
        no: form.no,
    };

    form.courseQuestions.forEach((q) => {
        params.courseTimes.push(q.courseTime ?? '');
        params.questionIds.push(q.questionId?.toString() ?? '');
    });

    const { data: { code } } = await courseMaterialEdit(params)
    if (code !== 200) {
        return
    }

    router.back()
}

// 删除
async function de1() {
    if (!delConfirm.value) {
        delConfirm.value = true
        return
    }

    const { data: { code } } = await courseMaterialDel({ id: form.id })
    if (code !== 200) {
        return
    }

    router.back()
}

// 添加选项
function addOption() {
    form.courseQuestions.push({
        courseTime: '00:00:00',
        questionId: ''
    })
}

// 删除选项
function delOption(index: number) {
    form.courseQuestions.splice(index, 1)
}

// 上传之前处理
function uploadBeforeOfVideo(rawFile: UploadRawFile) {
    if (rawFile.type !== 'video/mp4') {
        ElMessage.error('只允许 mp4 格式')
        return false
    }
    if (rawFile.size / 1024 > 102400) {
        ElMessage.error('视频最大支持100兆')
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
function uploadSuccessOfVideo(response: any, uploadFile: UploadFile, uploadFiles: UploadFiles) {
    if (response.code === 200) {
        uploadFile.uid = response.data.fileIds
        uploadFile.url = `${downloadUrl}?id=${response.data.fileIds}`

        videoOptions.src = uploadFile.url
    } else {
        uploadFiles.splice(uploadFiles.indexOf(uploadFile), 1)
    }
}

// 上传超出限制处理（视频只允许上传一个）
function uploadExceedOfVideo(files: File[], uploadFiles: UploadUserFile[]) {
    videoUploadRef.value!.clearFiles()
    const file = files[0] as UploadRawFile
    file.uid = genFileId()
    videoUploadRef.value!.handleStart(file)
    videoUploadRef.value!.submit()
}

// 上传移除 
function uploadRemoveOfVideo(uploadFile: UploadFile, uploadFiles: UploadFiles) {
    videoOptions.src = ''
}

</script>

<style lang="scss" scoped>
.course-material-set {
    display: flex;
    flex-direction: column;
    width: 1200px;
    background-color: #FFFFFF;
    margin: 20px 0px;
    padding: 40px 30px 0px 30px;
    border-radius: 15px 15px 15px 15px;

    .form {
        margin-top: 20px;

        .form__btn {
            height: 38px;
            padding: 0px 30px;
            border-radius: 6px;
            border: 0px;
            color: #FFFFFF;
            font-size: 14px;
            background-image: linear-gradient(to right, #04C7F2, #259FF8);

            &.form__btn--secondary {
                color: #04C7F2;
                border: 1px solid #04C7F2;
                background-image: linear-gradient(to right, #FFFFFF, #FFFFFF);
            }

            &.form__btn--warn {
                color: #FF5D15;
                border: 1px solid #FF5D15;
                background-image: linear-gradient(to right, #FFFFFF, #FFFFFF);
            }
        }

        .form__option-btn {
            border: 0px;
            width: 100px;
            height: 30px;
            border-radius: 6px 6px 6px 6px;

            .form__option-btn-icon {
                font-size: 12px;
            }

            .form__option-btn-txt {
                margin-left: 4px;
                font-size: 12px;
            }
        }

        .form__times-btn {
            margin-left: 20px;
            cursor: pointer;
        }

        .form__times {
            width: 100%;
            display: flex;
        }

        :deep(.img-group) {
            display: flex;

            .el-upload {
                width: 148px;
                height: 148px;
                border: 1px dashed var(--el-border-color);
                border-radius: 6px;
                cursor: pointer;
                position: relative;
                overflow: hidden;
                transition: var(--el-transition-duration-fast);
            }

            .el-upload:hover {
                border-color: #3AA8EF;
                background-color: #FAFAFA;
            }

            .iconfont {
                font-size: 20px;
                color: #8c939d;
            }

            .PhotoSlider__Backdrop {
                opacity: 0.6;
            }

            .el-image__inner {
                background-color: #fff;
                border: 1px solid #dcdfe6;
                border-radius: 6px;
                height: 148px;
                width: 148px;
                margin: 0 8px 8px 0;
                overflow: hidden;
                padding: 0px;
            }

            .img {
                display: flex;
                flex-direction: column;
                align-items: center;

                .el-image {
                    cursor: move;
                }

                .img__inner {
                    line-height: 0px;

                    .img__txt {
                        line-height: 14px;
                        font-size: 14px;
                        color: #000000;
                    }

                    .img__btn {
                        cursor: pointer;
                        margin-left: 5px;
                        font-size: 16px;
                    }
                }
            }
        }
    }
}
</style>
