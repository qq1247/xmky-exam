<template>
    <xmks-anti-cheat v-if="myExam.state === 2" :screen-switch="!exam.sxes.includes(3)" :debug="!exam.sxes.includes(4)"
        :hotkey="!exam.sxes.includes(4)" @screen-switch="(content) => sxes(3, content)"
        @hotkey="(content) => sxes(4, content)" @debug="(content) => sxes(4, content)"></xmks-anti-cheat>
    <div v-loading="load.loading" :element-loading-text="load.text"
        element-loading-background="rgba(122, 122, 122, 0.8)" class="my-exam-paper">
        <div class="my-exam-paper__head">
            <xmks-count-down v-if="examing" :expireTime="myExam.answerEndTime" preTxt="距考试结束：" :remind="300"
                color="#04C7F2" remind-color="#E43D33" font-size="14px" @end="finish">
            </xmks-count-down>
            <el-button v-if="examing" type="primary" class="my-exam-paper__btn"
                :class="{ 'my-exam-paper__btn--warn': finishConfirm }" @click="() => {
                    // 二次确认是否交卷
                    if (!finishConfirm) {
                        finishConfirm = true
                        return
                    }
                    finish()
                }">
                <span class="iconfont icon-lianxi-61 my-exam-paper__btn-icon"></span>
                <span class="editor-toolbar__btn-txt">{{ finishConfirm ? '再次确认' : '完成交卷' }}</span>
            </el-button>
        </div>
        <div class="my-exam-paper__main">
            <div v-if="toolbarShow" class="paper-toolbar">
                <el-button type='' class="editor-toolbar__btn "
                    :class="{ 'editor-toolbar__btn--active': toolbars.answerShow }"
                    @click="toolbars.answerShow = !toolbars.answerShow">
                    <span class="iconfont icon-icon-03 editor-toolbar__btn-icon"></span>
                    <span class="editor-toolbar__btn-txt">{{ toolbars.answerShow ? '隐藏标准答案' : '显示标准答案' }}</span>
                </el-button>
                <el-button type='' class="editor-toolbar__btn"
                    :class="{ 'editor-toolbar__btn--active': toolbars.analysisShow }"
                    @click="toolbars.analysisShow = !toolbars.analysisShow">
                    <span class="iconfont icon-icon-06 editor-toolbar__btn-icon"></span>
                    <span class="editor-toolbar__btn-txt">{{ toolbars.analysisShow ? '隐藏解析' : '显示解析' }}</span>
                </el-button>
            </div>
            <div class="paper__wrap">
                <div class="answer-sheet">
                    <el-divider class="answer-sheet_title">
                        答题卡
                    </el-divider>
                    <el-scrollbar height="calc(100vh - 300px)" class="answer-sheet__wrap">
                        <div class="answer-sheet">
                            <template v-for="(examQuestion, index) in examQuestions" :key="index">
                                <div v-if="examQuestion.type === 1" @click="scrollView(index)"
                                    class="answer-sheet__chapter">
                                    {{ examQuestion.chapterName }}
                                </div>
                                <div v-else @click="scrollView(index)" class="answer-sheet__question" :class="{
                                    'answer-sheet__question--answered': answered(examQuestion),
                                    'answer-sheet__question--right': isRight(examQuestion),
                                    'answer-sheet__question--wrong': isWrong(examQuestion),
                                    'answer-sheet__question--half': isHalf(examQuestion),
                                }">
                                    {{ examQuestion.no }}
                                </div>
                            </template>
                        </div>
                    </el-scrollbar>
                </div>
                <el-scrollbar height="calc(100vh - 226px)" class="paper">
                    <div v-if="myExam.totalScore != null" class="total-score">
                        <div class="total-score__inner">
                            <span class="total-score__value">
                                {{ myExam.totalScore }}
                            </span>
                            <span class="total-score__unit">分</span>
                        </div>
                        <span class="iconfont icon-defen total-score__icon"></span>
                    </div>
                    <div class="paper__name">
                        {{ exam.paperName }}
                    </div>
                    <template v-for="(examQuestion, index) in examQuestions" :key="index">
                        <div v-if="examQuestion.type === 1" :id="`q${index}`" class="chapter">
                            <span class="chapter__name">{{ examQuestion.chapterName }}</span>
                            <span v-if="examQuestion.chapterTxt" class="chapter__desc">
                                {{ examQuestion.chapterTxt }}
                            </span>
                        </div>
                        <xmks-question v-else :id="`q${index}`" :type="examQuestion.questionType as number"
                            :title="examQuestion.title as string" :img-ids="examQuestion.imgFileIds"
                            :video-id="examQuestion.videoFileId" :options="examQuestion.options"
                            :answers="examQuestion.answers" :markType="examQuestion.markType as number"
                            :score="examQuestion.score as number" :scores="examQuestion.scores"
                            :analysis="examQuestion.analysis" :userAnswers="examQuestion.userAnswers"
                            :userScore="examQuestion.userScore" :answer-show="toolbars.answerShow"
                            :user-answer-show="true" :analysisShow="toolbars.analysisShow" :display="'paper'"
                            :editable="examing" class="_question"
                            @change="(answers: string[]) => { examQuestion.userAnswers = answers; answer(examQuestion) }">
                            <template #title-pre>{{ examQuestion.no }}、</template>
                            <template #foot>
                                <div v-if="examQuestion.questionType === 5 && examQuestion.markType === 2"
                                    class="upload-group">
                                    <div class="upload__btn">
                                        <vue-draggable v-model="examQuestion.imgFiles">
                                            <photo-provider :default-backdrop-opacity="0.6">
                                                <photo-consumer v-for="(file, index) in examQuestion.imgFiles"
                                                    :key="index" :src="`${downloadUrl}?id=${file.uid}`">
                                                    <div class="img">
                                                        <el-image :src="`${downloadUrl}?id=${file.uid}`"
                                                            fit="contain" />
                                                        <div class="img__inner">
                                                            <span class="img__txt">
                                                                图{{ toChinaNum(index + 1) }}
                                                            </span>
                                                            <span v-if="myExam.state === 2" @click.stop="() => {
                                                                examQuestion.imgFiles.splice(index, 1);
                                                                examQuestion.answerImgFileIds = examQuestion.imgFiles.map((file: UploadUserFile) => file.uid)
                                                                answer(examQuestion);
                                                            }" class="iconfont icon-shanchu img__btn"></span>
                                                        </div>
                                                    </div>
                                                </photo-consumer>
                                            </photo-provider>
                                        </vue-draggable>
                                        <el-upload v-if="myExam.state === 2" v-model:file-list="examQuestion.imgFiles"
                                            :action="uploadUrl" :headers="{ Authorization: userStore.accessToken }"
                                            name="files" :show-file-list="false" accept=".jpg,.png,.jpeg,JPG,PNG,JPEG"
                                            :limit="2" :before-upload="uploadBefore" :multiple="true"
                                            :on-success="(response: any, uploadFile: UploadFile, uploadFiles: UploadFiles) => uploadSuccess(response, uploadFile, uploadFiles, examQuestion)">
                                            <span
                                                class="iconfont icon-zhaopian upload__btn-icon upload__btn-icon--img"></span>
                                            <span class="upload__btn-txt">照片</span>
                                        </el-upload>
                                    </div>
                                    <div class="upload__btn">
                                        <div v-if="examQuestion.videoFiles.length" class="img">
                                            <longze-video-play
                                                v-bind="examQuestion.videoFiles[0].option"></longze-video-play>
                                            <div class="img__inner" style="margin-top: 10px;">
                                                <span class="img__txt">视频</span>
                                                <span v-if="myExam.state === 2" @click.stop="() => {
                                                    examQuestion.videoFiles.splice(index, 1);
                                                    examQuestion.answerVideoFileIds = examQuestion.videoFiles.map((file: UploadUserFile) => file.uid)
                                                    answer(examQuestion);
                                                }" class="iconfont icon-shanchu img__btn"></span>
                                            </div>
                                        </div>
                                        <el-upload v-if="myExam.state === 2" v-model:file-list="examQuestion.videoFiles"
                                            :action="uploadUrl" :headers="{ Authorization: userStore.accessToken }"
                                            name="files" :show-file-list="false" accept=".mp4,.MP4" :limit="1"
                                            :before-upload="uploadBeforeOfVideo" :multiple="false"
                                            :on-success="(response: any, uploadFile: UploadFile, uploadFiles: UploadFiles) => uploadSuccessOfVideo(response, uploadFile, uploadFiles, examQuestion)">
                                            <span
                                                class="iconfont icon-shipin upload__btn-icon upload__btn-icon--video"></span>
                                            <span class="upload__btn-txt">视频</span>
                                        </el-upload>
                                    </div>
                                </div>
                            </template>
                        </xmks-question>
                    </template>
                </el-scrollbar>
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { loginSysTime } from '@/api/login'
import { myExamAnswer, myExamExamGet, myExamFinish, myExamGet, myExamPaper, myExamSxe } from '@/api/my/my-exam'
import XmksQuestion from '@/components/question/xmks-question.vue'
import XmksAntiCheat from '@/components/xmks-anti-cheat.vue'
import xmksCountDown from '@/components/xmks-count-down.vue'
import http from '@/request'
import type { Exam, ExamQuestion } from '@/ts/exam/exam'
import type { MyExam } from '@/ts/exam/my-exam'
import { delay } from '@/util/timeUtil'
import { ElMessage, genFileId, type UploadFile, type UploadFiles, type UploadRawFile, type UploadUserFile } from 'element-plus'
import _ from 'lodash'
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { toChinaNum } from '@/util/numberUtil'
import { useUserStore } from '@/stores/user'
import { VueDraggable } from 'vue-draggable-plus'
import { longzeVideoPlay } from "longze-vue3-video-player";

/************************变量定义相关***********************/
const route = useRoute() // 路由
const router = useRouter()// 路由
const userStore = useUserStore()// 用户缓存
const toolbars = reactive({// 工具栏
    answerShow: false,
    totalScoreShow: false,
    analysisShow: false,
})

const examQuestions = ref<ExamQuestion[]>([])// 试卷
const exam = reactive<Exam>({
    id: null,
    name: '',
    paperName: '',
    startTime: '',
    endTime: '',
    markStartTime: '',
    markEndTime: '',
    markState: null,
    scoreState: null,
    rankState: null,
    passScore: null,
    totalScore: null,
    markType: null,
    genType: null,
    loginType: null,
    sxes: [],
    state: null,
    userNum: null,
    limitMinute: null,
    retakeNum: null
})
const myExam = reactive<MyExam>({
    examId: null,
    userId: null,
    answerStartTime: '',
    answerEndTime: '',
    markStartTime: '',
    markEndTime: '',
    objectiveScore: null,
    totalScore: null,
    state: null,
    markState: null,
    answerState: null,
    no: null,
    ver: null
})
const finishConfirm = ref(false) // 交卷确认
const load = reactive({// 加载
    loading: false, // 显示加载页面
    text: '',// 显示加载页面内容
    second: 6,// 倒计时秒数
})

const uploadUrl = `${http.defaults.baseURL}file/upload`// 上传地址
const downloadUrl = `${http.defaults.baseURL}file/download`// 下载地址

/************************组件生命周期相关*********************/
onMounted(async () => {
    await paperQuery() // 先获取试卷信息，因为后台检测到，当前用户如果是第一次打开试卷，才生成当前用户的考试和结束时间
    examQuery()
    myExamQuery()
})

/************************计算属性相关*************************/
const examing = computed(() => (myExam.state === 1 && myExam.markState != 3) || myExam.state === 2) // 考试中。未考试且未阅卷显示（未考试且考试结束时间到，后台会处理成未考试且已阅卷）；考试中显示
const toolbarShow = computed(() => (exam.scoreState == 1 && exam.markState == 3) || (exam.scoreState == 3 && myExam.markState as number >= 2)) // 如果是考试结束后显示成绩，需要等到考试结束。如果是交卷后显示成绩，交卷后就可以显示全部标准答案（包括主观题答案）。
const answered = computed(() => (examQuestion: ExamQuestion) => examQuestion.userAnswers?.some((userAnswer) => userAnswer.length > 0))// 是否已答
const isHalf = computed(() => (question: ExamQuestion) => question.userScore != null && question.userScore > 0 && question.userScore !== question.score); // 是否半对
const isRight = computed(() => (question: ExamQuestion) => question.userScore != null && question.userScore === question.score); // 是否答对
const isWrong = computed(() => (question: ExamQuestion) => question.userScore != null && question.userScore === 0); // 是否答错

/************************事件相关*****************************/
// 试卷查询
async function paperQuery() {
    const { data: { data: data1 } } = await myExamPaper({ examId: route.params.examId })
    let no = 1;
    examQuestions.value = data1.map((examQuestion: ExamQuestion) => {
        if (examQuestion.type === 2) { // 处理题号、答题附件
            examQuestion.no = no++;
            if (examQuestion.markType === 2 && examQuestion.questionType === 5) { // 主观问答题添加图片和视频附件
                examQuestion.imgFiles = []
                examQuestion.answerImgFileIds?.forEach((fileId: number) => {
                    examQuestion.imgFiles.push({
                        uid: fileId,
                        url: `${downloadUrl}?id=${fileId}`,
                        name: `${fileId}`
                    })
                })
                examQuestion.videoFiles = []
                examQuestion.answerVideoFileIds?.forEach((fileId: number) => {
                    examQuestion.videoFiles.push({
                        uid: fileId,
                        url: `${downloadUrl}?id=${fileId}`,
                        name: `${fileId}`,
                        option: {
                            width: "200px", //播放器宽度
                            height: "146px", //播放器高度
                            color: "#409eff", //主题色
                            title: "视频", //视频名称
                            src: `${downloadUrl}?id=${fileId}`, //视频源
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
                        }
                    })
                })
            }
        }
        return examQuestion;
    })
}

// 考试查询
async function examQuery() {
    const { data: { data } } = await myExamExamGet({ examId: route.params.examId })
    exam.id = parseInt(route.params.examId as string)
    exam.name = data.name
    exam.paperName = data.paperName
    exam.startTime = data.startTime
    exam.endTime = data.endTime
    exam.markStartTime = data.markStartTime
    exam.markEndTime = data.markEndTime
    exam.markState = data.markState
    exam.scoreState = data.scoreState
    exam.rankState = data.rankState
    exam.passScore = data.passScore
    exam.totalScore = data.totalScore
    exam.markType = data.markType
    exam.genType = data.genType
    exam.sxes = data.sxes
    exam.state = data.state
    exam.userNum = data.userNum
    exam.limitMinute = data.limitMinute
    exam.retakeNum = data.retakeNum
}
// 我的考试查询
async function myExamQuery() {
    const { data: { data } } = await myExamGet({ examId: route.params.examId })
    myExam.examId = data.examId
    myExam.userId = data.userId
    myExam.answerStartTime = data.answerStartTime
    myExam.answerEndTime = data.answerEndTime
    myExam.markStartTime = data.markStartTime
    myExam.markEndTime = data.markEndTime
    myExam.objectiveScore = data.objectiveScore
    myExam.totalScore = data.totalScore
    myExam.state = data.state
    myExam.markState = data.markState
    myExam.answerState = data.answerState
    myExam.no = data.no
    myExam.ver = data.ver
}

// 滚动预览
function scrollView(index: number) {
    (document.querySelector(`#q${index}`) as HTMLElement).scrollIntoView(true)
};

// 防作弊
async function sxes(type: number, content: string) {
    if (type === 3) {
        ElMessage.error(`禁止考试中切屏，请继续答题`)
    } else {
        ElMessage.error(`禁止浏览器调试，请正常答题`)
    }

    const { data: { data } } = await myExamSxe({ examId: route.params.examId, type, content })
    if (data) {
        ElMessage.error('多次检测到作弊，强制交卷')
        router.push('/my-exam-list')
    }

};

const answer = _.debounce(async function (examQuestion: ExamQuestion) {// // 答题  _.debounce返回的包装后的函数，所以能正常传参
    const { data: { code } } = await myExamAnswer({
        examId: route.params.examId,
        questionId: examQuestion.questionId,
        answers: examQuestion.userAnswers,
        imgFileIds: (examQuestion.markType === 2 && examQuestion.questionType === 5) ? examQuestion.answerImgFileIds : null,
        videoFileIds: (examQuestion.markType === 2 && examQuestion.questionType === 5) ? examQuestion.answerVideoFileIds : null,
    })
    if (code != 200) {// 答题失败也不要清空答案，比如问答题清空就尴尬了
        router.push('/my-exam-list')
        return
    }
    // examQuestion.userAnswers = answers; // 在浏览器做单多选题时，延时500毫秒显示，像页面卡顿，体验差。所以改为不管后台是否更新成功，优先页面显示成功。但会极小概率造成页面已答题，后台未同步，因为非金融等业务，所以优先保证使用体验性。
}, 500) // 延时一秒体验不好，填完直接退出页面不提交

async function finish() {
    // 交卷
    load.loading = true
    load.text = `交卷中`
    await myExamFinish({ examId: route.params.examId })

    // 如果考试成绩是不公布，返回首页
    if (exam.scoreState === 2) {
        load.text = `考试已结束`
        await delay(2000)
        // console.log(`考试已结束`)
        router.push('/home')
        return
    }

    // 如果考试成绩是交卷后公布，并且是主观题试卷，提示后返回首页（需人工阅卷）
    // if (exam.scoreState === 3 && exam.markType === 2) {
    // 	情况不存在，业务上做了主观题试卷不允许交卷后公布，允许也没有意义
    // 	return;
    // }

    // 如果考试成绩是交卷后公布，并且是客观题试卷，查询成绩
    if (exam.scoreState === 3 && exam.markType === 1) {
        load.text = `成绩查询中`
        for (let i = 0; i < 6; i++) {
            if (i >= 5) {
                load.text = `查询失败，请稍后再次查询`
                await delay(1000)
                // console.log(`查询失败，请稍后再次查询`)
                router.push('/home')
                return
            }

            await delay(1000)
            await myExamQuery()

            if (myExam.markState as number === 3) { // 阅卷状态（1：未阅卷；2：阅卷中；3：已阅卷；）
                load.text = `考试已结束`
                await delay(1000)
                // console.log(`考试已结束`)
                router.push(`/my-exam/read/${route.params.examId}`)
                return
            }
        }
        return
    }

    // 如果考试成绩是考试结束后公布，但是提前交卷，提示后返回首页
    const { data: { data } } = await loginSysTime({})
    const curTime = new Date(data)
    const examEndTime = new Date(exam.endTime)
    const remainSecond = (examEndTime.getTime() - curTime.getTime()) / 1000
    if (exam.scoreState === 1 && remainSecond > 3) {
        load.text = `整场考试未结束，请于${exam.endTime}后查询`
        await delay(1000)
        // console.log(`整场考试未结束，请于${exam.endTime}后查询`)
        router.push('/home')
        return
    }

    // 如果考试成绩是考试结束后公布，并且在接近整场考试结束时交卷，查询成绩
    if (exam.scoreState === 1 && remainSecond <= 3) {
        load.text = `成绩查询中`
        for (let i = 0; i < 6; i++) {
            if (i >= 5) {
                load.text = `查询失败，请稍后再次查询`
                await delay(1000)
                // console.log('查询失败，请稍后再次查询')
                router.push('/home')
                return
            }

            await delay(1000)
            await myExamQuery()

            if (myExam.markState as number >= 2) { // 阅卷状态（1：未阅卷；2：阅卷中；3：已阅卷；）
                load.text = `考试已结束`
                await delay(1000)
                // console.log('如果考试成绩是考试结束后公布，并且在接近整场考试结束时交卷，查询成绩')
                router.push(`/my-exam/read/${route.params.examId}`)
                return
            }
        }
        return
    }
}

// 上传之前处理
function uploadBefore(rawFile: UploadRawFile) {
    if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
        ElMessage.error('只允许 jpg和png 格式')
        return false
    }
    if (rawFile.size / 1024 > 2048) {
        ElMessage.error('图片最大支持2兆')
        return false
    }

    return true
}
function uploadBeforeOfVideo(rawFile: UploadRawFile) {
    if (rawFile.type !== 'video/mp4') {
        ElMessage.error('只允许 mp4 格式')
        return false
    }
    if (rawFile.size / 1024 > 10240) {
        ElMessage.error('视频最大支持10兆')
        return false
    }

    return true
}

// 上传成功处理
function uploadSuccess(response: any, uploadFile: UploadFile, uploadFiles: UploadFiles, examQuestion: ExamQuestion) {
    if (response.code === 200) {
        uploadFile.uid = response.data.fileIds
        uploadFile.url = `${downloadUrl}?id=${response.data.fileIds}`

        examQuestion.imgFiles = uploadFiles
        examQuestion.answerImgFileIds = examQuestion.imgFiles.map((file: UploadUserFile) => file.uid)
        answer(examQuestion)
    } else {
        uploadFiles.splice(uploadFiles.indexOf(uploadFile), 1)
    }
}
function uploadSuccessOfVideo(response: any, uploadFile: UploadFile, uploadFiles: UploadFiles, examQuestion: ExamQuestion) {
    if (response.code === 200) {
        uploadFile.uid = response.data.fileIds
        uploadFile.url = `${downloadUrl}?id=${response.data.fileIds}`


        examQuestion.videoFiles = [{
            uid: uploadFile.uid,
            url: `${downloadUrl}?id=${uploadFile.uid}`,
            name: `${uploadFile.uid}`,
            option: {
                width: "200px", //播放器宽度
                height: "146px", //播放器高度
                color: "#409eff", //主题色
                title: "视频", //视频名称
                src: `${downloadUrl}?id=${uploadFile.uid}`, //视频源
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
            }
        }]

        examQuestion.answerVideoFileIds = examQuestion.videoFiles.map((file: UploadUserFile) => file.uid)
        answer(examQuestion)
    } else {
        uploadFiles.splice(uploadFiles.indexOf(uploadFile), 1)
    }
}

</script>
<style lang="scss" scoped>
.my-exam-paper {
    display: flex;
    flex-direction: column;
    width: 1200px;
    margin: 20px 0px;
    background-color: #ffffff;
    border-radius: 15px 15px 15px 15px;

    .my-exam-paper__head {
        display: flex;
        justify-content: end;
        align-items: center;
        margin: 20px 20px 0px 20px;

        .my-exam-paper__btn {
            display: flex;
            justify-content: center;
            align-items: center;
            width: 120px;
            height: 40px;
            margin-left: 10px;
            border: 0px;
            border-radius: 8px;
            color: white;

            background: linear-gradient(to right, #04c7f2, #259ff8);

            &.my-exam-paper__btn--warn {
                color: #FF5D15;
                border: 1px solid #FF5D15;
                background-image: linear-gradient(to right, #FFFFFF, #FFFFFF)
            }

            .my-exam-paper__btn-icon {
                width: 18px;
                height: 18px;
                margin-right: 4px;
            }

            .my-exam-paper__btn-txt {
                font-size: 14px;
            }
        }
    }

    .my-exam-paper__main {
        flex: 1;
        display: flex;
        flex-direction: column;
        margin: 10px 20px 20px 20px;
        border: 1px solid #E5E5E5;

        .paper-toolbar {
            display: flex;
            justify-content: flex-end;
            align-items: center;
            border-bottom: 1px solid #E5E5E5;
            height: 50px;
            padding: 0px 10px;

            .editor-toolbar__tip {
                font-size: 14px;
                color: #999999;
                line-height: 45px;

                .editor-toolbar__tip-warn {
                    color: #E43D33;
                }
            }

            .editor-toolbar__btn {
                padding: 0px 0px;
                margin: 0px 10px;
                border: 0px;
                position: relative;

                &:hover {
                    color: #999999;
                }

                &::after {
                    content: '';
                    position: absolute;
                    width: 1px;
                    height: 20px;
                    background-color: #CCCCCC;
                    right: -10px;
                    top: 50%;
                    transform: translateY(-50%)
                }

                &:last-child::after {
                    display: none;
                }

                .editor-toolbar__btn-icon {
                    font-size: 16px;
                }

                .editor-toolbar__btn-txt {
                    margin-left: 2px;
                    font-size: 14px;
                    color: #999999;
                    line-height: 45px;
                }

                &.editor-toolbar__btn--active {
                    .editor-toolbar__btn-icon {
                        color: #1EA1EE;
                    }

                    .editor-toolbar__btn-txt {
                        color: #1EA1EE;
                    }
                }
            }

        }

        .paper__wrap {
            display: flex;

            .answer-sheet {
                display: flex;
                flex-direction: column;
                width: 220px;
                border-right: 1px solid #E5E5E5;

                .answer-sheet_title {
                    margin: 40px 0px 10px 0px;

                    .el-divider__text {
                        font-size: 16px;
                        color: #303133;
                    }
                }

                :deep(.answer-sheet__wrap) {
                    .el-scrollbar__view {
                        display: flex;
                        justify-content: center;


                        .answer-sheet {
                            display: flex;
                            flex-direction: row;
                            flex-wrap: wrap;
                            padding: 10px 0px 10px 10px;

                            .answer-sheet__chapter {
                                flex: 1 0 100%;
                                font-size: 14px;
                                color: #303133;
                                margin: 15px 0px 5px 0px;
                                cursor: pointer;
                            }

                            .answer-sheet__question {
                                display: flex;
                                justify-content: center;
                                align-items: center;
                                position: relative;
                                width: 28px;
                                height: 28px;
                                margin: 5px 5px 5px 0px;
                                border-radius: 6px 6px 6px 6px;
                                font-size: 12px;
                                color: #8F939C;
                                background-color: #F5F5F5;
                                border: 1px solid #CCCCCC;
                                cursor: pointer;
                                z-index: 1;

                                &.answer-sheet__question--answered {
                                    background-color: #E4F6FF;
                                    border: 1px solid #1EA1EE;
                                }

                                &.answer-sheet__question--right {
                                    background-color: #D1F2D7;
                                    border: 1px solid #4FBF63;
                                }

                                &.answer-sheet__question--wrong {
                                    background-color: #FAD8D6;
                                    border: 1px solid #FF6960;
                                }

                                &.answer-sheet__question--half {
                                    border: initial;

                                    &::before {
                                        content: "";
                                        position: absolute;
                                        top: 0;
                                        right: 0;
                                        left: 0;
                                        height: 50%;
                                        background-color: #FAD8D6;
                                        border-left: 1px solid #FF6960;
                                        border-top: 1px solid #FF6960;
                                        border-right: 1px solid #FF6960;
                                        border-radius: 6px 6px 0px 0px;
                                        z-index: -1;
                                    }

                                    &::after {
                                        content: "";
                                        position: absolute;
                                        bottom: 0;
                                        right: 0;
                                        left: 0;
                                        height: 50%;
                                        background-color: #D1F2D7;
                                        border-left: 1px solid #4FBF63;
                                        border-bottom: 1px solid #4FBF63;
                                        border-right: 1px solid #4FBF63;
                                        border-radius: 0px 0px 6px 6px;
                                        z-index: -1;
                                    }
                                }
                            }

                            .answer-sheet__sort {
                                padding: 0px 10px;
                                width: 190px;
                                height: 40px;
                                line-height: 40px;
                                margin: 10px 5px;
                                background: #F1F8FF;
                                font-size: 14px;
                                border: 1px solid #CCCCCC;
                                white-space: nowrap;
                                overflow: hidden;
                                text-overflow: ellipsis;
                                cursor: move;
                            }
                        }

                    }
                }

            }

            :deep(.paper) {
                flex: 1;
                margin-top: 20px;

                .el-scrollbar__view {
                    display: flex;
                    flex-direction: column;
                    padding: 10px 10px 10px 10px;
                    position: relative;

                    .paper__name {
                        display: flex;
                        justify-content: center;
                        text-align: center;
                        font-size: 24px;
                        color: #303133;
                        line-height: 45px;
                    }

                    .chapter {
                        display: flex;
                        flex-direction: column;

                        .chapter__name {
                            margin-top: 10px;
                            font-size: 16px;
                            font-size: 16px;
                            color: #303133;
                            line-height: 30px;
                        }

                        .chapter__desc {
                            margin-top: 5px;
                            font-size: 14px;
                            color: #999999;
                            line-height: 22px;
                        }
                    }

                    // question 存在冲突
                    ._question {
                        .mark-option {
                            display: flex;
                            justify-content: space-between;
                            align-items: center;

                            margin-top: 10px;
                            padding: 10px 20px 5px 20px;
                            position: relative;
                            border: 1px solid #E5E5E5;

                            .mark-option__title {
                                position: absolute;
                                left: 15px;
                                top: -8px;
                                padding: 0px 10px;
                                background-color: #FFFFFF;
                                font-size: 14px;
                                color: #999999;
                            }

                            .mark-option__txt {
                                font-size: 14px;
                                color: #999999;
                                line-height: 45px;
                            }

                            .mark-option__input-number {
                                position: relative;
                                width: 40px;

                                .el-input-number__decrease,
                                .el-input-number__increase {
                                    display: none;
                                }

                                .el-input__wrapper {
                                    padding: 0px;
                                    box-shadow: initial;
                                    border-radius: 0;
                                }

                                &::after {
                                    content: "";
                                    position: absolute;
                                    left: 0;
                                    bottom: 5px;
                                    width: 100%;
                                    height: 1px;
                                    background-color: #333333;
                                }
                            }

                            .mark-option__btn {
                                border: 0px;
                                height: 30px;
                                border-radius: 6px 6px 6px 6px;

                                .mark-option__btn-icon {
                                    font-size: 12px;
                                }

                                .mark-option__btn-txt {
                                    margin-left: 4px;
                                    font-size: 12px;
                                }
                            }
                        }
                    }

                    .total-score {
                        display: flex;
                        flex-direction: column;
                        align-items: center;

                        position: absolute;
                        right: 50px;
                        top: 10px;
                        z-index: 1;

                        .total-score__inner {
                            display: flex;
                            align-items: flex-end;

                            .total-score__value {
                                font-weight: bold;
                                font-size: 36px;
                                color: #FF0000;
                                line-height: 45px;

                            }

                            .total-score__unit {
                                color: #FF0000;
                                font-size: 14px;
                                line-height: 30px;

                            }

                        }

                        .total-score__icon {
                            font-size: 88px;
                            text-align: center;
                            color: #FF0000;
                            line-height: 20px;
                        }
                    }

                    .upload-group {
                        display: flex;
                        justify-content: left;
                        align-items: end;
                        margin-top: 10px;

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

                        .upload__btn {
                            margin-right: 10px;

                            .el-upload {
                                display: flex;
                                flex-direction: column;
                                width: 50px;
                                height: 45px;
                            }

                            .upload__btn-icon {

                                padding: 6px;
                                border-radius: 50%;
                                font-size: 14px;
                                color: #fff;

                                &.upload__btn-icon--img {
                                    background-color: #fcc129;
                                }

                                &.upload__btn-icon--video {
                                    background-color: #287ce7;
                                }
                            }

                            .upload__btn-txt {
                                margin-top: 2px;
                                font-size: 11px;
                            }
                        }
                    }
                }
            }
        }

    }

}
</style>
