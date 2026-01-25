<template>
    <div class="my-course-paper">
        <div class="my-course-paper__head">
            <div class="title">{{ curMyCourseMaterial?.name }}</div>
            <div class="content">{{ curMyCourseMaterial?.content }}</div>
        </div>
        <div class="my-course-paper__main">
            <longze-video-play ref="videoPlayerRef" v-bind="videoOptions" @timeupdate="onTimeupdate"
                @ended="finish"></longze-video-play>

            <div v-if="answerShow" class="paper">
                <div class="paper__head"></div>
                <xmks-question v-if="curCourseQuestion" :type="curCourseQuestion.questionType as number"
                    :title="curCourseQuestion.title as string" :img-ids="curCourseQuestion.imgFileIds"
                    :video-id="curCourseQuestion.videoFileId" :options="curCourseQuestion.options"
                    :answers="curCourseQuestion.answers" :markType="curCourseQuestion.markType as number"
                    :score="curCourseQuestion.score as number" :scores="curCourseQuestion.scores"
                    :analysis="curCourseQuestion.analysis" :userAnswers="curCourseQuestion.userAnswers"
                    :userScore="curCourseQuestion.userScore" :answer-show="false" :user-answer-show="true"
                    :analysisShow="false" :display="'paper'" :editable="true" class="paper-question"
                    @change="(userAnswers: string[]) => (curCourseQuestion as ExamQuestion).userAnswers = userAnswers">
                </xmks-question>
                <div class="paper__foot">
                    <el-button type="primary" class="my-course__btn" @click="answer(course)">
                        确认作答
                    </el-button>
                </div>
            </div>
            <div class="course">
                <div class="course__title">{{ course.name }}</div>
                <el-scrollbar height="580px" class="answer-sheet__wrap">
                    <ul>
                        <li v-for="(myCourseMaterial, index) in myCourseMaterials" :key="index"
                            :class="{ 'succ': myCourseMaterial.state === 1, 'active': curMyCourseMaterial && curMyCourseMaterial?.courseMaterialId === myCourseMaterial.courseMaterialId }"
                            @click="() => {
                                curMyCourseMaterial = myCourseMaterial
                                videoOptions.src = `${downloadUrl}?id=${curMyCourseMaterial.videoFileId}`
                                triggeredTimes.clear()
                            }">
                            <span :class="['iconfont',
                                { 'icon-lianxi-61': myCourseMaterial.state === 1 },
                                { 'icon-dingwei': myCourseMaterial.state !== 1 }]"></span>
                            <span>{{ myCourseMaterial.name }}</span>
                        </li>
                    </ul>
                </el-scrollbar>
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import http from '@/request';
import { onMounted, reactive, ref } from 'vue';
import { longzeVideoPlay } from "longze-vue3-video-player";
import XmksQuestion from '@/components/question/xmks-question.vue';
import { myCourseAnswer, myCourseCourseListpage, myCourseFinish, myCourseList, myCourseQuestion } from '@/api/my/my-course';
import type { MyCourseMaterial } from '@/ts/course/my-course-material';
import type { Question } from '@/ts/exam/question';
import type { ExamQuestion } from '@/ts/exam/exam';
import { useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';

/************************变量定义相关***********************/
const route = useRoute()// 路由
const myCourseMaterials = ref<MyCourseMaterial[]>([]) // 我的课程资料列表
const downloadUrl = `${http.defaults.baseURL}file/download`// 下载地址
const videoPlayerRef = ref()// 视频播放器引用
const videoOptions = reactive({// 视频播放器选项
    width: "800px", //播放器宽度
    height: "600px", //播放器高度
    color: "#fff", //主题色
    title: "", //视频名称
    src: "", //视频源
    muted: false, //静音
    webFullScreen: false,
    autoPlay: false, //自动播放
    loop: false, //循环播放
    mirror: false, //镜像画面
    ligthOff: false, //关灯模式
    volume: 0.3, //默认音量大小
    control: true, //是否显示控制
    speed: false, //是否显示倍速
    currentTime: 0, //默认开始时间
    controlBtns: [
        'volume',
        'fullScreen',
    ],
});
const course = reactive({// 课程
    name: '',
    content: '',
})
const curMyCourseMaterial = ref<MyCourseMaterial>() // 当前我的课程资料
const triggeredTimes = new Set<number>() // 已触发的时间集合
const answerShow = ref(false) // 答题显示
const curCourseQuestion = ref<ExamQuestion>() // 当前课程试题

/************************组件生命周期相关*********************/
onMounted(async () => {
    courseQuery()
    await myCourseListQuery()
    const _myCourseMaterial = myCourseMaterials.value.find(
        material => material.state === 2 || material.state === 3
    );
    if (_myCourseMaterial) {// 默认显示第一个未开始或进行中的课程资料
        curMyCourseMaterial.value = _myCourseMaterial
        videoOptions.src = `${downloadUrl}?id=${_myCourseMaterial.videoFileId}`
    } else {// 否则重看第一个课程资料
        curMyCourseMaterial.value = myCourseMaterials.value[0]
        videoOptions.src = `${downloadUrl}?id=${myCourseMaterials.value[0].videoFileId}`
    }
})

/************************事件相关*****************************/
// 课程查询
async function courseQuery() {
    const { data: { data } } = await myCourseCourseListpage({ courseId: route.params.courseId })
    course.name = data.list[0].name
    course.content = data.list[0].content
}

// 我的课程查询
async function myCourseListQuery() {
    const { data: { data } } = await myCourseList({ courseId: 1 })
    myCourseMaterials.value.push(...data)
}

// 播放器事件回调
async function onTimeupdate(event: Event) {
    const video = event.target as HTMLVideoElement;
    const playTime = Math.floor(video.currentTime); // 向下取整，避免浮点误差

    if (curMyCourseMaterial.value?.questions?.length) {
        for (const curMyCourseQuestion of curMyCourseMaterial.value?.questions) {
            const triggerTime = timeToSeconds(curMyCourseQuestion.courseTime)
            if (triggerTime === -1) {
                continue;
            }
            if (triggeredTimes.has(triggerTime)) {
                continue
            }
            if (playTime === triggerTime) {
                answerShow.value = true
                triggeredTimes.add(triggerTime);
                videoPlayerRef.value.pause()

                const { data: { data } } = await myCourseQuestion({
                    courseMaterialId: curMyCourseMaterial.value.courseMaterialId,
                    questionId: curMyCourseQuestion.questionId
                })
                curCourseQuestion.value = data
                break
            }
        }
    }

}

// 答题
async function answer() {
    const { data: { code, data } } = await myCourseAnswer({
        courseMaterialId: curMyCourseMaterial.value?.courseMaterialId,
        questionId: curCourseQuestion.value?.questionId,
        userAnswers: curCourseQuestion.value?.userAnswers
    })

    if (code !== 200) {
        return
    }

    if (!data) {
        ElMessage.error('回答错误')
        return
    }

    answerShow.value = false
    videoPlayerRef.value.play()
}

// 完成
async function finish() {
    const { data: { code, data } } = await myCourseFinish({
        courseMaterialId: curMyCourseMaterial.value?.courseMaterialId,
    })
    if (code != 200) {
        return
    }

    if (curMyCourseMaterial.value) {
        curMyCourseMaterial.value.state = 1
    }
}
// 小时分秒转秒数
function timeToSeconds(timeStr: string): number {
    if (!timeStr) return -1;
    const parts = timeStr.split(':').map(Number);
    return parts[0] * 3600 + parts[1] * 60 + parts[2];
}

</script>
<style scoped lang="scss">
.my-course-paper {
    display: flex;
    width: 1200px;
    margin-top: 20px;
    padding: 30px;
    flex-direction: column;
    background-color: #ffffff;
    border-radius: 15px 15px 15px 15px;

    .my-course-paper__head {
        .title {
            font-size: 18px;
            color: #333333;
            line-height: 30px;
        }

        .content {
            font-size: 14px;
            color: #999999;
            line-height: 30px;
        }
    }

    .my-course-paper__main {
        display: flex;
        position: relative;
        margin-top: 20px;

        .paper {
            position: absolute;
            z-index: 10;
            width: 800px;
            height: 600px;
            top: 0;
            left: 0;
            background-color: #FFF;

            .paper__head {
                margin-bottom: 20px;
                border-bottom: 1px solid #E5E5E5;
            }

            .paper-question {
                :deep(.question) {
                    border-bottom: 1px solid #E5E5E5;
                }
            }

            .paper__foot {
                margin-top: 20px;

                .my-course__btn {
                    height: 30px;
                    padding: 0px 20px;
                    border-radius: 6px;
                    border: 0px;
                    color: #FFFFFF;
                    font-size: 14px;
                    background-image: linear-gradient(to right, #04C7F2, #259FF8);
                }
            }
        }

        .course {
            flex: 1;

            .course__title {
                font-size: 19px; // 20px盛不下16个字
                color: #333333;
                line-height: 30px;
                margin-left: 30px;
            }

            ul {
                padding-left: 30px;
                margin-top: 0px;

                li {
                    display: flex;
                    align-items: center;
                    height: 38px;
                    color: #999999;
                    cursor: pointer;
                    list-style-type: none;
                    border-bottom: 1px dashed #999999;

                    &:hover {
                        color: #04C7F2;
                        font-weight: bold;
                    }

                    &.succ {
                        color: #1AC693;
                    }

                    &.active {
                        color: #04C7F2;
                        font-weight: bold;
                    }

                    span {
                        margin-right: 10px;
                    }
                }
            }
        }

    }
}
</style>
