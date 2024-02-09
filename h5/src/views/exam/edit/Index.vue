<template >
    <div class="edit">
        <el-steps :space="200" :active="active" simple class="edit-top">
            <el-step title="试卷选择"><template #icon><span class="iconfont icon-classify"></span></template></el-step>
            <el-step title="试卷配置"><template #icon><span class="iconfont icon-shiti"></span></template></el-step>
            <el-step title="考试配置"><template #icon><span class="iconfont icon-setting"></span></template></el-step>
            <el-step title="用户选择"><template #icon><span class="iconfont icon-persons"></span></template></el-step>
            <el-step title="考试发布"><template #icon><span class="iconfont icon-publish"></span></template></el-step>
        </el-steps>
        <PaperSelect v-if="active === 0" @select="(type) => paperSelect(type)"></PaperSelect>
        <Paper ref="paper" v-if="active === 1 && form.genType === 1" :show="paperShow" @paperShowUpdate="(show) => paperShow = show"></Paper>
        <PaperRule ref="paperRule" v-if="active === 1 && form.genType === 2"></PaperRule>
        <ExamSetting ref="examSetting" v-if="active === 2"></ExamSetting>
        <ExamUser ref="examUser" v-if="active === 3"></ExamUser>
        <ExamPublish v-if="active === 4"></ExamPublish>
        <div class="edit-bottom">
            <div v-if="progressBar.percent > 0 && active >= 4" class="edit-bottom-percent">
                <el-progress :percentage="progressBar.percent">{{ progressBar.msg }}</el-progress>
            </div>
            <el-button v-if="active >= 1 && paperShow != 'editor' && btnShow" type="primary" @click="active--">上一步</el-button>
            <el-button v-if="active >= 1 && active < 4 && paperShow != 'editor' && btnShow" type="primary" @click="next()">下一步</el-button>
            <el-button v-if="active >= 4 && paperShow != 'editor' && btnShow" type="primary" @click="publish()">发布</el-button>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, reactive } from 'vue'
import PaperSelect from './PaperSelect.vue';
import Paper from './Paper.vue';
import PaperRule from './PaperRule.vue';
import ExamSetting from './ExamSetting.vue';
import ExamUser from './ExamUser.vue';
import ExamPublish from './ExamPublish.vue';
import { useExamStore, type ExamQuestion, type ExamRule } from '@/stores/exam';
import { ElMessage } from 'element-plus';
import http from '@/request';
import dayjs from 'dayjs'
import { useRouter, useRoute } from 'vue-router'

// 定义变量
const route = useRoute()
const router = useRouter()
const active = ref(0) // 当前激活步骤
const paperShow = ref('paper') // 试卷显示
const form = useExamStore() // 考试储存
const paper = ref()// 试卷配置组件
const paperRule = ref()// // 试卷配置组件
const examSetting = ref()// 考试配置组件
const examUser = ref()// 考试用户组件
const btnShow = ref(true)// 按钮显示
const progressBar = reactive({// 进度条
    percent: 0,// 进度
    code: 200, // 响应码
    msg: '', // 响应消息
})

// 组件挂载完成后，执行如下方法
onMounted(async () => {
    // 如果是添加，初始化数据
    if (route.path.indexOf('/exam/add') >= 0) {
        form.id = null
        form.name = `考试-${dayjs().add(0, 'day').format('YYYY-MM-DD')}`
        form.paperName = `试卷-${dayjs().add(0, 'day').format('YYYY-MM-DD')}`
        form.examTimes = [
                dayjs().add(0, 'day').hour(8).minute(0).second(0).format('YYYY-MM-DD HH:mm:ss'),
                dayjs().add(0, 'day').hour(10).minute(0).second(0).format('YYYY-MM-DD HH:mm:ss'),
            ]
        form.markTimes = [
            dayjs().add(0, 'day').hour(14).minute(0).second(0).format('YYYY-MM-DD HH:mm:ss'),
            dayjs().add(0, 'day').hour(18).minute(0).second(0).format('YYYY-MM-DD HH:mm:ss'),
        ]
        form.genType = 1
        form.passScore = 0
        form.sxes = [] as number[]
        form.showType = 1
        form.anonState = 2
        form.scoreState = 2
        form.rankState = 2
        form.state = 1
        form.examQuestions = [] as ExamQuestion[]
        form.examRules = [] as ExamRule[]
        form.examUserIds = [] as number[]
        form.markUserIds = [] as number[]
        form.limitMinute = 0
    }
    // 如果是修改，回显数据
    else if (route.path.indexOf('/exam/edit/') >= 0 && route.params.id) {
        let { data: { data } } = await http.post("exam/paper", { id: route.params.id })
        form.id = data.id
        form.name = data.name
        form.paperName = data.paperName
        form.examTimes[0] = data.startTime
        form.examTimes[1] = data.endTime
        if (data.markType === 2) {
            form.markTimes[0] = data.markStartTime
            form.markTimes[1] = data.markEndTime
        }
        form.genType = data.genType
        form.passScore = data.passScore
        form.sxes = data.sxes
        form.showType = data.showType
        form.anonState = data.anonState
        form.scoreState = data.scoreState
        form.rankState = data.rankState
        form.state = data.state
        form.examQuestions = data.examQuestions
        form.examRules = data.examRules
        form.examUserIds = data.examUserIds
        form.markUserIds = data.markUserIds
        form.limitMinute = data.limitMinute
        form.noUpdate()

        if (form.genType === 1) {// 根据组卷类型自动跳到下一步
            paperSelect('blank')
        } else {
            paperSelect('random')
        }
    }
})

// 试卷选择方式
function paperSelect(type: string) {
    // 选择空白试卷，组固定试卷，显示空白试卷
    if (type === 'blank') {
        form.genType = 1
        paperShow.value = 'paper'
        next()
    } 
    // 选择文本导入，组固定试卷，显示试题编辑器
    else if (type === 'quick') {
        form.genType = 1 
        paperShow.value = 'editor'
        next()
    } 
    // 选择随机试卷，组随机试卷，显示抽题规则页面
    else if (type === 'random') {
        form.genType = 2 
        next()
    }
}

// 下一步
async function next() {
    if (active.value === 0) {// 试卷选择
        active.value++
        return
    }

    if (active.value === 1) {// 试卷配置
        let component = form.genType === 1 ? paper : paperRule
        if (await component.value.next()) {
            if ((form.subjectiveQuestionNum + form.objectiveQuestionNum) === 0) {
                ElMessage.error(`最少添加一${form.genType === 1 ? '道试题' : '条规则'}`)
                return
            }

            active.value++
        }
        return
    }

    if (active.value === 2) {// 考试配置
        let component = examSetting
        if (await component.value.next()) {
            active.value++
        }
        return
    }

    if (active.value === 3) {// 用户选择
        let component = examUser
        if (await component.value.next()) {
            active.value++
        }
    }
}

// 发布考试
async function publish() {
    // 隐藏发布等按钮
    btnShow.value = false

    // 发布考试
    let { data: { code, data : processBarId } } = await http.post("exam/publish", JSON.stringify({
            id: form.id,
            name: form.name,
            paperName: form.paperName,
            genType: form.genType,
            passScore: form.passScore,
            sxes: form.sxes,
            showType: form.showType,
            anonState: form.anonState,
            scoreState: form.scoreState,
            rankState: form.rankState,
            state: form.state,
            examQuestions: form.examQuestions,
            examRules: form.examRules,
            examUserIds: form.examUserIds,
            markUserIds: form.markUserIds,
            totalScore: form.totalScore,
            markType: form.markType,
            startTime: form.examTimes[0],
            endTime: form.examTimes[1],
            limitMinute: form.limitMinute,
            markStartTime: form.markType === 2 ? form?.markTimes[0] : '',
            markEndTime: form.markType === 2 ? form?.markTimes[1] : '',
        }), 
        { headers: { 'Content-Type': 'application/json' }}
    )

    if (code !== 200) {// 如果有异常，显示按钮重新操作
        btnShow.value = true
        return
    }

    // 显示进度
    let timer = setInterval(async function () {
        let { data: { code, msg, data } } = await http.post("progressBar/get", { id : processBarId })
        progressBar.code = code
        progressBar.msg = msg
        progressBar.percent = data.percent

        if (code !== 200) {// 如果报错，停止进度条，显示按钮
            clearInterval(timer);
            btnShow.value = true
            return
        }

        if (progressBar.percent < 100) {// 如果进度不足100%，间隔1秒在查询进度
            return
        }
        
        // 完成后跳转道考试列表页面
        clearInterval(timer);
        router.push("/exam") 

    }, 1000);

}
</script>

<style lang="scss" scoped>
.edit {
    display: flex;
    flex-direction: column;
    height: calc(100% - 20px);
    position: relative;

    .edit-top {
        background-color: white;
    }

    .edit-bottom {
        position: absolute;
        bottom: 0px;
        right: 0px;
        .edit-bottom-percent {
            display: inline-block;
            margin: 0px 30px 30px 0px;
            width: 400px;
        }
    }
}
</style>