<template>
    <div class="my-course-read">
        <div class="my-course__side">
            <div class="user">
                <div class="user__avatar">
                    <span class="iconfont icon-rentouxiang user__avatar-icon"></span>
                </div>
                <span class="user__label">账号：<span class="user__value">{{ user.loginName }}</span></span>
                <span class="user__label">姓名：<span class="user__value">{{ user.name }}</span></span>
                <span class="user__label">机构：<span class="user__value">{{ user.orgName }}</span></span>
            </div>
            <div class="notes">
                <xmks-card-guide title="注意事项" icon="icon-tubiaoziti-34" class="notes__head"></xmks-card-guide>
                <div class="notes__main">
                    <span class="notes__txt">
                        1、本课程视频已禁用快进等操作，请勿尝试违规行为，否则可能导致本次学习无效<br />
                        2、学习过程中如出现答题界面，请认真作答；仅当答题成功后方可继续观看，未完成答题将无法标记课程为“已完成”<br />
                    </span>
                </div>
            </div>
        </div>
        <el-scrollbar max-height="calc(100vh - 104px)" class="my-course-read__main">
            <div class="course">
                <xmks-card-guide title="课程信息" icon="icon-tubiaoziti-35" class="course__head"></xmks-card-guide>
                <div class="course__main">
                    <div class="course__title">{{ course.name }}</div>
                    <div class="course__content">{{ course.content }}</div>
                </div>
            </div>
            <div class="my-course">
                <xmks-card-guide title="我的课程" icon="icon-icon_xiugaishijian" class="my-course__head"></xmks-card-guide>
                <div class="my-course__main">
                    <div class="opt-panel">
                        <div class="opt-panel__statis">
                            <div class="opt-panel__statis-item">总题数：<br /><span class="opt-panel__statis-value">{{
                                questionNum }}</span>道</div>
                            <div class="opt-panel__statis-item">已答题：<br /><span class="opt-panel__statis-value">{{
                                questionNum - unAnsweredQuestionNum }}</span>道</div>
                            <div class="opt-panel__statis-item">未答题：<br /><span class="opt-panel__statis-value">{{
                                unAnsweredQuestionNum }}</span>道</div>
                        </div>
                        <div class="opt-panel__progress">
                            <span class="opt-panel__progress-label">课程进度：</span>
                            <el-progress class="opt-panel__progress-bar" :percentage="watchedVideoNum / videoNum * 100">
                                <el-button text class="opt-panel__progress-label">{{ watchedVideoNum }}/{{ videoNum
                                    }}</el-button>
                            </el-progress>
                        </div>
                        <!-- <div class="opt-panel__time">
                            视频长度：05:32:45
                        </div> -->
                    </div>
                    <div class="opt-panel__opt">
                        <div v-if="showBtn" class="xmks-card-empty" @click="toStudy">
                            <span class="iconfont xmks-card-empty__icon icon-tubiaoziti22-22"></span>
                            <span class="xmks-card-empty__txt">去学习</span>
                        </div>
                    </div>
                </div>
            </div>
        </el-scrollbar>
    </div>
</template>
<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import XmksCardGuide from '@/components/card/xmks-card-guide.vue'
import type { User } from '@/ts/base/user'
import { userGet } from '@/api/base/user'
import { useRoute, useRouter } from 'vue-router'
import { myCourseCourseListpage, myCourseGenerate, myCourseList } from '@/api/my/my-course'
import type { MyCourseMaterial } from '@/ts/course/my-course-material'

/************************变量定义相关***********************/
const route = useRoute()// 路由
const router = useRouter()// 路由
const user = reactive<User>({ // 用户
    id: null,
    name: '',
    loginName: '',
    role: '',
    state: null,
    orgName: ''
})
const course = reactive({
    name: '',
    content: '',
}) // 课程信息
const myCourseMaterials = ref<MyCourseMaterial[]>([]) // 课程资料
const showBtn = ref(false) // 按钮显示

/************************组件生命周期相关*********************/
onMounted(async () => {
    userQuery()
    courseQuery()
    await courseGenerate()
    myCourseListQuery()
    showBtn.value = true
})

/************************计算属性相关*************************/
const questionNum = computed(() => {// 总题数
    return myCourseMaterials.value.reduce((total, myCourseMaterial) => {
        return total + myCourseMaterial.questions?.length as number
    }, 0)
})
const unAnsweredQuestionNum = computed(() => {// 未答题数
    return myCourseMaterials.value.reduce((total, myCourseMaterial) => {
        return total + myCourseMaterial.questions.filter(q => !q.answerTime).length
    }, 0)
})
const videoNum = computed(() => {// 总资料数
    return myCourseMaterials.value.length
})
const watchedVideoNum = computed(() => {// 已完成学习数量
    return myCourseMaterials.value.filter(myCourseMaterial => myCourseMaterial.state === 1).length
})

/************************事件相关*****************************/
// 用户查询
async function userQuery() {
    const { data: { data } } = await userGet({})
    user.id = data.id;
    user.name = data.name;
    user.loginName = data.loginName;
    user.orgName = data.orgName;
}

// 课程查询
async function courseQuery() {
    const { data: { data } } = await myCourseCourseListpage({ courseId: route.params.courseId })
    course.name = data.list[0].name
    course.content = data.list[0].content
}

// 课程生成
async function courseGenerate() {
    await myCourseGenerate({ courseId: route.params.courseId })
}

// 我的课程列表查询
async function myCourseListQuery() {
    const { data: { data } } = await myCourseList({ courseId: route.params.courseId })
    myCourseMaterials.value = data
}


// 去学习
async function toStudy() {
    router.push(`/my-course/paper/${route.params.courseId}`)
}

</script>
<style lang="scss" scoped>
.my-course-read {
    display: flex;
    width: 1200px;
    margin: 20px 0px;

    .my-course__side {
        display: flex;
        flex-direction: column;
        width: 270px;
        margin-right: 20px;

        .user {
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            position: relative;
            background-color: #FFFFFF;
            height: 180px;
            padding: 20px 0px;
            border-radius: 15px 15px 15px 15px;
            margin-top: 30px;

            .user__avatar {
                display: flex;
                justify-content: center;
                align-items: center;
                position: absolute;
                width: 70px;
                height: 70px;
                border: 5px solid #FFFFFF;
                background-color: #F0FAFF;
                top: 0px;
                left: 50%;
                transform: translateX(-50%) translateY(-50%);
                border-radius: 50%;

                .user__avatar-icon {
                    font-size: 48px;
                    color: #04C7F2;
                }
            }

            .user__label {
                font-size: 14px;
                color: #8F939C;
                line-height: 28px;

                .user__value {
                    color: #333333;
                }
            }
        }

        .notes {
            flex: 1;
            display: flex;
            flex-direction: column;
            margin-top: 20px;

            .notes__head {
                margin-bottom: 8px;
            }

            .notes__main {
                flex: 1;
                display: flex;
                background-color: #FFFFFF;
                height: 270px;
                padding: 20px 30px;
                border-radius: 15px 15px 15px 15px;

                .notes__txt {
                    font-size: 14px;
                    color: #E43D33;
                    line-height: 36px;
                }
            }
        }
    }

    .my-course-read__main {
        flex: 1;

        .course {
            .course__head {
                margin-bottom: 8px;
            }

            .course__main {
                display: flex;
                flex-direction: column;
                background-color: #FFFFFF;
                height: 180px;
                padding: 20px;
                border-radius: 15px 15px 15px 15px;
                overflow: scroll;

                .course__title {
                    font-size: 16px;
                    color: #303133;
                    line-height: 45px;
                }

                .course__content {
                    // text-indent: 2em;
                    font-size: 14px;
                    color: #999999;
                    line-height: 22px;
                    white-space: pre-line;
                }
            }
        }

        .my-course {
            display: flex;
            flex-direction: column;
            margin-top: 20px;

            .my-course__head {
                margin-bottom: 8px;
            }

            .my-course__main {
                display: flex;
                background-color: #FFFFFF;
                height: 330px;
                padding: 20px 0px 20px 30px;
                border-radius: 15px 15px 15px 15px;

                .opt-panel {
                    width: 480px;
                    padding: 40px;
                    border-right: 1px solid #E5E5E5;

                    .opt-panel__statis {
                        display: flex;
                        font-size: 14px;
                        color: #8F939C;
                        line-height: 36px;

                        .opt-panel__statis-item {
                            flex: 1;
                            text-align: center;
                            border-right: 1px solid #E5E5E5;

                            &:last-child {
                                border-right: none;
                            }
                        }

                        .opt-panel__statis-value {
                            font-size: 16px;
                            color: #1EA1EE;
                        }
                    }

                    .opt-panel__progress {
                        display: flex;
                        align-items: center;
                        margin-top: 30px;

                        .opt-panel__progress-label {
                            font-size: 14px;
                            color: #8F939C;
                        }

                        .opt-panel__progress-bar {
                            flex: 1;
                        }

                        :deep(.el-progress-bar__outer) {
                            background-color: #C4C4C4;

                            .el-progress-bar__inner {
                                background: linear-gradient(to right, #04C7F2, #259FF8);
                            }
                        }
                    }

                    .opt-panel__time {
                        display: flex;
                        justify-content: center;
                        font-size: 14px;
                        color: #04C7F2;
                    }
                }

                .opt-panel__opt {
                    flex: 1;
                    display: flex;
                    justify-content: center;
                    align-items: center;

                    .xmks-card-empty {
                        display: flex;
                        flex-direction: column;
                        justify-content: center;
                        align-items: center;
                        min-width: 385px;
                        min-height: 220px;
                        background: #ffffff;
                        border-radius: 15px;
                        cursor: pointer;

                        &:hover {
                            .xmks-card-empty__icon {
                                background-color: #04C7F2;
                                border: 1px solid #04C7F2;
                                color: #fff;
                            }

                            .xmks-card-empty__txt {
                                color: #04C7F2;
                            }
                        }

                        .xmks-card-empty__icon {
                            border: 1px dashed #B6E1FC;
                            color: #B6E1FC;
                            font-size: 34px;
                            padding: 40px;
                            border-radius: 50%;
                            transition: all .3s ease-in-out;
                        }

                        .xmks-card-empty__txt {
                            font-size: 14px;
                            color: #999999;
                            line-height: 48px;
                        }
                    }
                }
            }

        }
    }

}
</style>
