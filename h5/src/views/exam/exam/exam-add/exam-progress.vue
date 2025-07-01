<template>
    <div class="exam-progress">
        <!-- <div v-if="navShow" class="exam-progress__head">
            <el-button type="primary" class="exam-progress__btn" @click="publish">
                <span class="iconfont icon-zuo exam-progress__btn-icon"></span>
                <span class="exam-progress__btn-txt">返回</span>
            </el-button>
        </div> -->
        <div height="calc(100vh - 318px)" class="exam-progress__main">
            <div class="create-progress">
                <el-progress :percentage="progressBar.percent" class="create-progress__progress" />
                <div class="create-progress__inner">
                    <span class="create-progress__title">{{ progressBar.title }}</span>
                    <span class="create-progress__desc">{{ progressBar.msg }} </span>
                </div>
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue'
import { progressBarGet } from '@/api/sys/progress-bar'
import http from '@/request'
import { useExamStore } from '@/stores/exam'
import { useRouter } from 'vue-router'

/************************变量定义相关***********************/
const router = useRouter()// 路由
const form = useExamStore() // 表单
const navShow = ref(false) // 按钮显示
const progressBar = reactive({// 进度条
    percent: 0,// 进度
    title: '', // 响应码
    msg: '', // 响应消息
})

/************************组件生命周期相关*********************/
onMounted(() => {
    publish()
})

/************************事件相关*****************************/
async function publish() {
    // 发布考试
    progressBar.percent = 0
    progressBar.title = '正在发布考试...'
    progressBar.msg = ''

    const { data: { code, data: processBarId } } = await http.post("exam/publish", JSON.stringify({
        id: form.id,
        name: form.name,
        paperName: form.paperName,
        genType: form.genType,
        passScore: form.passScore,
        sxes: form.sxes,
        showType: form.showType,
        loginType: form.loginType,
        anonState: form.anonState,
        scoreState: form.scoreState,
        rankState: form.rankState,
        state: form.state,
        examQuestions: form.examQuestions,
        examRules: form.examRules,
        userIds: form.userIds,
        orgIds: form.orgIds,
        markUserIds: form.markUserIds,
        totalScore: form.totalScore,
        markType: form.markType,
        startTime: form.examTimes[0],
        endTime: form.examTimes[1],
        limitMinute: form.limitMinute,
        markStartTime: form.markType === 2 ? form?.markTimes[0] : '',
        markEndTime: form.markType === 2 ? form?.markTimes[1] : '',
    }),
        { headers: { 'Content-Type': 'application/json' } }
    )

    if (code !== 200) {// 如果有异常，显示按钮重新操作
        progressBar.title = '发布失败'
        navShow.value = true
        return
    }

    // 显示当前进度
    const timer = setInterval(async function () {
        const { data: { code, msg, data } } = await progressBarGet({ id: processBarId })
        progressBar.msg = msg
        progressBar.percent = data.percent

        if (code !== 200) {// 如果报错，停止进度条，显示按钮
            clearInterval(timer);
            progressBar.title = '发布失败'
            navShow.value = true
            return
        }

        if (progressBar.percent < 100) {// 如果进度不足100%，间隔1秒在查询进度
            return
        }

        // 返回考试列表页面
        clearInterval(timer);
        router.push("/exam-list")
    }, 1000);

}
</script>
<style scoped lang="scss">
.exam-progress {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 20px;
    background-color: #FFFFFF;
    border-radius: 15px 15px 15px 15px;

    .exam-progress__head {
        display: flex;
        justify-content: space-between;
        padding-bottom: 10px;
        border-bottom: 1px solid #E5E5E5;

        .exam-progress__btn {
            height: 38px;
            padding: 8px 30px;
            border: 0px;
            color: #FFFFFF;
            border-radius: 6px 6px 6px 6px;
            background-image: linear-gradient(to right, #04C7F2, #259FF8);

            .exam-progress__btn-icon {
                font-size: 14px;
            }

            .exam-progress__btn-txt {
                margin-left: 2px;
            }
        }
    }

    .exam-progress__main {
        display: flex;
        justify-content: center;
        align-items: center;
        height: calc(100vh - 312px);

        .create-progress {
            display: flex;
            flex-direction: column;
            width: 500px;

            :deep(.create-progress__progress) {
                .el-progress-bar__inner {
                    background-image: linear-gradient(to right, #04C7F2, #259FF8);
                }
            }

            .create-progress__inner {
                display: flex;
                flex-direction: column;
                align-items: center;

                .create-progress__title {
                    margin-top: 30px;
                    font-size: 16px;
                    color: #333333;
                }

                .create-progress__desc {
                    margin-top: 10px;
                    font-size: 14px;
                    color: #999999;
                }
            }
        }
    }
}
</style>
