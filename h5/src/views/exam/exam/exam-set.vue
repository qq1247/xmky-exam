<template>
    <div class="exam-set">
        <div class="exam-set__head">
            <div class="exam-set__name">
                <span>{{ form.name }}</span>
            </div>
            <div class="exam-info">
                <div class="exam-info__illust">
                    <span class="exam-info__illust-title">考试概览</span>
                    <span class="exam-info__illust-desc">♦ 抢分冲刺 | 绝地反击</span>
                </div>
                <div class="exam-info__outer">
                    <div class="exam-info__inner">
                        <div class="exam-info__tag">
                            <span class="iconfont icon-fabu-10 exam-info__tag-icon"></span>
                            <span class="exam-info__tag-txt">{{ dictStore.getValue('LOGIN_TYPE', form.loginType as
                                number)
                            }}</span>
                        </div>
                        <div class="exam-info__tag">
                            <span class="iconfont icon-fabu-11 exam-info__tag-icon"></span>
                            <span class="exam-info__tag-txt">
                                {{ dictStore.getValue('PAPER_GEN_TYPE', form.genType as number) }}
                            </span>
                        </div>
                        <div class="exam-info__tag">
                            <span class="iconfont icon-fabu-12 exam-info__tag-icon"></span>
                            <span class="exam-info__tag-txt">
                                {{ dictStore.getValue('PAPER_MARK_TYPE', form.markType as number) }}试卷
                            </span>
                        </div>
                    </div>
                    <div>
                        <span class="exam-info__lab">防止作弊：
                            <span class="exam-info__value">
                                <template v-if="!form.sxes.length">无</template>
                                <template v-else v-for="(sxe, index) in form.sxes">
                                    {{ index > 0 ? '、' : '' }}
                                    {{ dictStore.getValue('EXAM_SXES', sxe) }}
                                </template>
                            </span>
                        </span>
                    </div>
                    <div class="exam-info__row">
                        <span class="exam-info__lab">考试时间：
                            <span class="exam-info__value">
                                {{ form.startTime.substring(0, 16) }} - {{ form.endTime.substring(0, 16) }}
                            </span>
                        </span>
                        <span class="exam-info__lab" v-if="form.markType === 2">阅卷时间：
                            <span class="exam-info__value">
                                {{ form.markStartTime.substring(0, 16) }} - {{ form.markEndTime.substring(0, 16) }}
                            </span>
                        </span>
                    </div>
                    <div class="exam-info__row1">
                        <div class="exam-info__column">
                            <span class="exam-info__lab">及格分数</span>
                            <span class="exam-info__value">
                                {{ form.passScore }} / {{ form.totalScore }}分
                            </span>
                        </div>
                        <div class="exam-info__column">
                            <span class="exam-info__lab">限时答题</span>
                            <span class="exam-info__value">
                                {{ form.limitMinute === 0 ? '无' : `${form.limitMinute}分钟` }}
                            </span>
                        </div>
                        <div class="exam-info__column">
                            <span class="exam-info__lab">考试人数</span>
                            <span class="exam-info__value">{{ form.userNum }}人</span>
                        </div>
                        <div class="exam-info__column">
                            <span class="exam-info__lab">协助阅卷</span>
                            <span class="exam-info__value">{{ markUsers.length || '0' }}人</span>
                        </div>
                        <div class="exam-info__column">
                            <span class="exam-info__lab">公布成绩</span>
                            <span class="exam-info__value">
                                {{ dictStore.getValue('SCORE_STATE', form.scoreState as number) }}
                            </span>
                        </div>
                        <div class="exam-info__column">
                            <span class="exam-info__lab">排名公布</span>
                            <span class="exam-info__value">
                                {{ dictStore.getValue('STATE_ON', form.rankState as number) }}
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <el-scrollbar max-height="calc(100vh - 530px)" class="exam-set__main">
            <xmks-edit-card title="暂停考试" desc="暂停考试">
                <template #card-side>
                    <el-button type="primary" class="form__btn" @click="pause" style="margin-bottom: 14px;">
                        {{ form.state === 1 ? '暂停考试' : '发布考试' }}
                    </el-button>
                </template>
            </xmks-edit-card>
            <xmks-edit-card title="变更时间" desc="提前或延后考试时间、阅卷时间">
                <template #card-main>
                    <el-form ref="timeFormRef" :model="timeForm" :rules="timeFormRules" inline label-width="100"
                        size="large" class="form">
                        <el-form-item label="" prop="timeType">
                            <el-select v-model="timeForm.timeType" clearable placeholder="请选择时间选项">
                                <el-option label="考试开始时间" :value="1" />
                                <el-option label="考试结束时间" :value="2" />
                                <el-option v-if="form.markType === 2" label="阅卷开始时间" :value="3" />
                                <el-option v-if="form.markType === 2" label="阅卷结束时间" :value="4" />
                            </el-select>
                        </el-form-item>
                        <el-form-item label="" prop="add">
                            <el-select v-model="timeForm.add" clearable placeholder="请选择操作选项">
                                <el-option label="提前" :value="1" />
                                <el-option label="延后" :value="2" />
                            </el-select>
                        </el-form-item>
                        <el-form-item label="" prop="minute">
                            <el-input-number v-model="timeForm.minute" :min="1" :step="10"
                                :precision="0"></el-input-number>分钟
                        </el-form-item>
                    </el-form>
                </template>
                <template #card-side>
                    <el-button type="primary" class="form__btn" @click="time"
                        style="margin-bottom: 40px;">保存设置</el-button>
                </template>
            </xmks-edit-card>
            <xmks-edit-card v-if="form.markState !== 3" title="快捷变更时间" desc="快捷变更时间">
                <template #card-side>
                    <el-button v-if="form.startTime > curTime" type="primary" class="form__btn"
                        @click="timeForm.timeType = 1; timeForm.add = 1; timeForm.minute = 52560000; time()"
                        style="margin-bottom: 14px;">
                        立即开始考试
                    </el-button>
                    <el-button v-else-if="form.endTime > curTime" type="primary" class="form__btn"
                        @click="timeForm.timeType = 2; timeForm.add = 1; timeForm.minute = 52560000; time()"
                        style="margin-bottom: 14px;">
                        立即结束考试
                    </el-button>
                    <el-button v-else-if="form.markType === 2 && form.markStartTime > curTime" type="primary"
                        class="form__btn"
                        @click="timeForm.timeType = 3; timeForm.add = 1; timeForm.minute = 52560000; time()"
                        style="margin-bottom: 14px;">
                        立即开始阅卷
                    </el-button>
                    <el-button v-else-if="form.markType === 2 && form.markEndTime > curTime" type="primary"
                        class="form__btn"
                        @click="timeForm.timeType = 4; timeForm.add = 1; timeForm.minute = 52560000; time()"
                        style="margin-bottom: 14px;">
                        立即结束阅卷
                    </el-button>
                </template>
            </xmks-edit-card>
            <xmks-edit-card title="查询成绩" desc="考试结束后：整场考试结束后，考试用户才可查询成绩；交卷后：考试用户交卷后，可立即查看成绩（客观题试卷有效）。">
                <template #card-main>
                    <el-form ref="scoreFormRef" :model="scoreForm" :rules="scoreFormRules" inline label-width="100"
                        size="large" class="form">
                        <el-form-item label="" prop="scoreState">
                            <el-radio-group v-model="scoreForm.scoreState">
                                <el-radio v-for="(option, index) in dictStore.getList('SCORE_STATE')" :key="index"
                                    :value="parseInt(option.dictKey)"
                                    :disabled="(scoreForm.markType === 2 && option.dictKey === '3') ? true : false">
                                    {{ option.dictValue }}
                                </el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-form>
                </template>
                <template #card-side>
                    <el-button type="primary" class="form__btn" @click="score"
                        style="margin-bottom: 40px;">保存设置</el-button>
                </template>
            </xmks-edit-card>
            <xmks-edit-card title="公布排名" desc="公布：整场考试结束后，考试用户可查询排名；">
                <template #card-main>
                    <el-form ref="rankFormRef" :model="rankForm" :rules="rankFormRules" inline label-width="100"
                        size="large" class="form">
                        <el-form-item label="" prop="rankState">
                            <el-radio-group v-model="rankForm.rankState">
                                <el-radio v-for="(option, index) in dictStore.getList('STATE_ON')" :key="index"
                                    :value="parseInt(option.dictKey)">
                                    {{ option.dictValue }}
                                </el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-form>
                </template>
                <template #card-side>
                    <el-button type="primary" class="form__btn" @click="rank"
                        style="margin-bottom: 40px;">保存设置</el-button>
                </template>
            </xmks-edit-card>
            <xmks-edit-card title="考试用户" desc="考试未结束允许添加人员">
                <template #card-main>
                    <el-form ref="userFormRef" :model="userForm" :rules="userFormRules" inline label-width="100"
                        size="large" class="form">
                        <el-form-item label="" prop="orgIds" style="width: 100%;">
                            <xmks-select v-model="userForm.orgIds" url="org/listpage" :params="{}"
                                search-parm-name="name" option-label="name" option-value="id" :options="orgs"
                                :multiple="true" clearable :page-size="100" :disabled-values="disabledOrgIds"
                                search-placeholder="请输入机构名称进行筛选" placeholder="请输入机构名称进行筛选">
                                <template #default="{ option }">
                                    {{ option.name }} - {{ option.orgName }} {{ disabledOrgIds.includes(option.id) ?
                                        '（不可选）' : '' }}
                                </template>
                            </xmks-select>
                        </el-form-item>
                        <el-form-item label="" prop="userIds" style="width: 100%;">
                            <xmks-select v-model="userForm.userIds" url="user/listpage" :params="{ state: 1, type: 1 }"
                                search-parm-name="name" option-label="name" option-value="id" :options="users"
                                :multiple="true" clearable :page-size="100" :disabled-values="disabledUserIds"
                                search-placeholder="请输入机构名称或用户名称进行筛选" placeholder="请输入机构名称或用户名称进行筛选">
                                <template #default="{ option }">
                                    {{ option.name }} - {{ option.orgName }} {{ disabledUserIds.includes(option.id) ?
                                        '（不可选）' : '' }}

                                </template>
                            </xmks-select>
                        </el-form-item>
                    </el-form>
                </template>
                <template #card-side>
                    <el-button type="primary" class="form__btn" @click="userAdd"
                        style="margin-bottom: 40px;">保存设置</el-button>
                </template>
            </xmks-edit-card>
            <xmks-edit-card v-if="assistForm.markType === 2" title="协助阅卷" desc="协助管理员阅卷">
                <template #card-main>
                    <el-form ref="assistFormRef" :model="assistForm" :rules="assistFormRules" inline label-width="100"
                        size="large" class="form">
                        <el-form-item label="" prop="markUserIds">
                            <xmks-select v-model="assistForm.markUserIds" url="user/listpage"
                                :params="{ state: 1, type: 3 }" search-parm-name="name" option-label="name"
                                option-value="id" :options="markUsers" :multiple="true" clearable
                                search-placeholder="请输入用户名称进行筛选">
                                <template #default="{ option }">
                                    {{ option.name }}
                                </template>
                            </xmks-select>
                        </el-form-item>
                    </el-form>
                </template>
                <template #card-side>
                    <el-button type="primary" class="form__btn" @click="assist"
                        style="margin-bottom: 40px;">保存设置</el-button>
                </template>
            </xmks-edit-card>
            <xmks-edit-card title="扫码答题"
                :desc="`移动端扫码答题时使用。${userStore?.type === 0 ? '<a href=\'/parm-nav/set/1\'>去设置</a>' : '请联系管理员设置'}`">
                <template #card-main>
                    <div class="m">
                        <vue-qrcode v-if="mForm.host" :value="`${mForm.host}${mForm.uri}`" type="image/png"
                            :color="{ color: '' }" :options="{ width: 400 }"></vue-qrcode>
                        {{ mForm.host }}{{ mForm.uri }}
                    </div>
                </template>
            </xmks-edit-card>
            <xmks-edit-card v-if="form.id" title="删除考试" desc="删除考试">
                <template #card-side>
                    <el-button type="primary" class="form__btn" :class="{ 'form__btn--warn': delConfirm }" @click="del"
                        style="margin-bottom: 14px;">删除考试</el-button>
                </template>
            </xmks-edit-card>
        </el-scrollbar>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted } from 'vue'
import { type FormInstance, type FormRules } from 'element-plus'
import XmksEditCard from '@/components/card/xmks-card-edit.vue'
import { useRoute, useRouter } from 'vue-router'
import type { Exam } from '@/ts/exam/exam'
import { examAssist, examDel, examGet, examMarkUserList, examPause, examRank, examScore, examTime, examUserAdd } from '@/api/exam/exam'
import { useDictStore } from '@/stores/dict'
import { useUserStore } from '@/stores/user'
import XmksSelect from '@/components/xmks-select.vue'
import type { User } from '@/ts/base/user'
import VueQrcode from 'vue-qrcode'
import { parmGet } from '@/api/sys/parm'
import { loginSysTime } from '@/api/login'
import { userListpage } from '@/api/base/user'
import { orgListpage } from '@/api/base/org'

/************************变量定义相关***********************/
const route = useRoute()// 路由
const router = useRouter()// 路由
const dictStore = useDictStore() // 字典缓存
const userStore = useUserStore() // 用户缓存
const form = reactive<Exam>({
    id: null,
    name: '',
    paperName: '',
    genType: null,
    markType: null,
    loginType: null,
    startTime: '',
    endTime: '',
    limitMinute: null,
    markStartTime: '',
    markEndTime: '',
    markState: null,
    scoreState: null,
    rankState: null,
    passScore: null,
    totalScore: null,
    sxes: [],
    userNum: null,
    state: null,
})
const delConfirm = ref(false) // 删除确认

const curTime = ref('') // 当前时间
const timeFormRef = ref<FormInstance>()// 表单引用
const timeForm = reactive({// 表单
    id: null, // ID
    timeType: 2,// 时间类型
    add: 2, // 添加
    minute: 10, // 分钟数
    markType: 1, // 阅卷类型（用于控制显示阅卷时间）
})
const timeFormRules = reactive<FormRules>({// 表单校验规则
    timeType: [
        { required: true, message: '请选择时间类型', trigger: 'blur' },
    ],
    add: [
        { required: true, message: '请选择操作类型', trigger: 'blur' },
    ],
    minute: [
        { required: true, message: '请输入分钟数', trigger: 'blur' },
    ],
})

const scoreFormRef = ref<FormInstance>()// 表单引用
const scoreForm = reactive({// 表单
    id: null, // ID
    scoreState: null,// 成绩查询状态
    markType: null,// 阅卷类型
})
const scoreFormRules = reactive<FormRules>({// 表单校验规则
    scoreState: [
        { required: true, message: '请选择查询状态', trigger: 'blur' },
    ],
})

const rankFormRef = ref<FormInstance>()// 表单引用
const rankForm = reactive({// 表单
    id: null, // ID
    rankState: null,// 排名状态
})
const rankFormRules = reactive<FormRules>({// 表单校验规则
    rankState: [
        { required: true, message: '请选择排名状态', trigger: 'blur' },
    ],
})

const markUsers = ref([])// 阅卷用户
const assistFormRef = ref<FormInstance>()// 表单引用
const assistForm = reactive({// 表单
    id: null, // ID
    markUserIds: [],// 考试用户
    markType: null,// 阅卷类型
})
const assistFormRules = reactive<FormRules>({// 表单校验规则
    markUserIds: [
        // { required: true, message: '请选择阅卷用户', trigger: 'blur' },
    ],
})

const mForm = reactive({
    host: '',
    uri: '',
})


const users = ref<any[]>([]) // 考试用户
const disabledUserIds = ref<number[]>([]) // 禁用用户IDS
const orgs = ref<any[]>([]) // 机构
const disabledOrgIds = ref<number[]>([]) // 禁用机构IDS
const userFormRef = ref<FormInstance>()// 表单引用
const userForm = reactive({// 表单
    id: null, // ID
    userIds: [],// 考试用户IDS
    orgIds: [],// 机构IDS
})
const userFormRules = reactive<FormRules>({// 表单校验规则

})

/************************组件生命周期相关*********************/
onMounted(async () => {
    load()
})

/************************事件相关*****************************/

// 重新加载
async function load() {
    const { data: { data } } = await examGet({ id: route.params.id })
    form.id = data.id
    form.name = data.name
    form.genType = data.genType
    form.markType = data.markType
    form.loginType = data.loginType
    form.startTime = data.startTime
    form.endTime = data.endTime
    form.limitMinute = data.limitMinute
    form.markStartTime = data.markStartTime
    form.markEndTime = data.markEndTime
    form.markState = data.markState
    form.scoreState = data.scoreState
    form.rankState = data.rankState
    form.passScore = data.passScore
    form.totalScore = data.totalScore
    form.sxes = data.sxes
    form.userNum = data.userNum
    form.state = data.state

    timeForm.id = data.id
    timeForm.markType = data.markType

    scoreForm.id = data.id
    scoreForm.scoreState = data.scoreState
    scoreForm.markType = data.markType

    rankForm.id = data.id
    rankForm.rankState = data.rankState

    assistForm.id = data.id
    assistForm.markType = data.markType
    const { data: { data: data2 } } = await examMarkUserList({ id: form.id })
    markUsers.value = data2
    assistForm.markUserIds = markUsers.value.map((markUser: User) => {
        return markUser.id
    }) as never[]

    const { data: { data: data3 } } = await parmGet({ id: 1 })
    mForm.host = data3.mHost
    if (form.loginType === 2) {
        mForm.uri = `/pages/login/noLogin?redirectPath=` + encodeURIComponent(`/pages/myExam/myRead?examId=${form.id}&loginType=2`)
    } else {
        mForm.uri = `/pages/login/login?redirectPath=` + encodeURIComponent(`/pages/myExam/myRead?examId=${form.id}&loginType=1`)
    }

    const { data: { data: data4 } } = await loginSysTime({})
    curTime.value = data4

    userForm.id = data.id
    userForm.userIds = data.userIds
    userForm.orgIds = data.orgIds

    if (userForm.userIds.length) {
        let curPage = 1
        const pageSize = 100
        while (true) {
            const { data: { data: data5 } } = await userListpage({
                ids: userForm.userIds.join(),
                curPage: curPage++,
                pageSize: pageSize,
            })
            users.value.push(...data5.list)
            users.value.forEach((user: any) => {
                disabledUserIds.value.push(user.id)
            })

            if (users.value.length >= data5.total) {// 分批次取出
                break
            }
        }
    }
    if (userForm.orgIds.length) {
        let curPage = 1
        const pageSize = 100
        while (true) {
            const { data: { data: data5 } } = await orgListpage({
                ids: userForm.orgIds.join(),
                curPage: curPage++,
                pageSize: pageSize,
            })

            orgs.value.push(...data5.list)
            orgs.value.forEach((org: any) => {
                disabledOrgIds.value.push(org.id)
            })
            if (orgs.value.length >= data5.total) {// 分批次取出
                break
            }
        }
    }
}

// 暂停
async function pause() {
    const { data: { code } } = await examPause({
        id: form.id,
    })
    if (code !== 200) {
        return
    }

    router.push("/exam-list")
}

// 时间变更
async function time() {
    // 数据校验
    try {
        await timeFormRef.value?.validate()
    } catch (e) {
        return
    }

    // 时间变更
    const { data: { code } } = await examTime({
        id: timeForm.id,
        timeType: timeForm.timeType,
        minute: timeForm.add === 1 ? -timeForm.minute : timeForm.minute
    })
    if (code !== 200) {
        return
    }

    router.push("/exam-list")
}

// 成绩查询状态
async function score() {
    // 数据校验
    try {
        await scoreFormRef.value?.validate()
    } catch (e) {
        return
    }

    // 时间变更
    const { data: { code } } = await examScore({
        id: scoreForm.id,
        scoreState: scoreForm.scoreState,
    })
    if (code !== 200) {
        return
    }

    router.push("/exam-list")
}

// 排名状态
async function rank() {
    // 数据校验
    try {
        await rankFormRef.value?.validate()
    } catch (e) {
        return
    }

    // 时间变更
    const { data: { code } } = await examRank({
        id: rankForm.id,
        rankState: rankForm.rankState,
    })
    if (code !== 200) {
        return
    }

    router.push("/exam-list")
}

// 用户添加
async function userAdd() {
    // 数据校验
    try {
        await userFormRef.value?.validate()
    } catch (e) {
        return
    }

    // 用户添加
    const { data: { code } } = await examUserAdd({ ...userForm })
    if (code !== 200) {
        return
    }

    router.push("/exam-list")
}
// 协助阅卷
async function assist() {
    // 数据校验
    try {
        await assistFormRef.value?.validate()
    } catch (e) {
        return
    }

    // 时间变更
    const { data: { code } } = await examAssist({ ...assistForm })
    if (code !== 200) {
        return
    }

    router.push("/exam-list")
}


// 删除
async function del() {
    if (!delConfirm.value) {
        delConfirm.value = true
        return
    }

    const { data: { code } } = await examDel({ id: form.id })
    if (code !== 200) {
        return
    }

    router.push("/exam-list")
}
</script>

<style lang="scss" scoped>
.exam-set {
    display: flex;
    flex-direction: column;
    width: 1200px;
    margin: 20px 0px;
    border-radius: 15px 15px 15px 15px;

    .form {
        margin-top: 20px;

        .el-select {
            min-width: 200px;
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

    .exam-set__head {
        padding: 30px;
        border-radius: 15px 15px 15px 15px;
        background-color: #FFFFFF;


        .exam-set__name {
            font-size: 20px;
            color: #333333;
        }

        .exam-info {
            display: flex;
            margin-top: 20px;
            padding-top: 20px;
            border-top: 1px dashed #F2F2F2;

            .exam-info__illust {
                display: flex;
                flex-direction: column;
                width: 315px;
                height: 182px;
                padding: 60px 0px 0px 30px;
                border-radius: 15px;
                background: url('/img/exam/step/exam-publish/overview-bg.jpg') no-repeat;

                .exam-info__illust-title {
                    font-size: 32px;
                    font-weight: bold;
                }

                .exam-info__illust-desc {
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    width: 130px;
                    padding: 2px;
                    margin-top: 10px;
                    border-radius: 4px;
                    font-size: 11px;
                    background: linear-gradient(to right, #D1F9FA, #3EDAFA);
                }

            }

            .exam-info__outer {
                flex: 1;
                margin-left: 20px;

                .exam-info__inner {
                    display: flex;
                    margin-bottom: 15px;

                    .exam-info__tag {
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                        position: relative;
                        margin-right: 25px;
                        width: 124px;
                        height: 38px;
                        padding: 0px 12px;
                        background: #D4EEFE;
                        border-radius: 6px 6px 6px 6px;
                        border: 1px solid #B8E5FF;

                        &::before {
                            content: '';
                            position: absolute;
                            left: 45px;
                            width: 2px;
                            height: 25px;
                            background-color: #B8E5FF;
                        }

                        .exam-info__tag-icon {
                            font-size: 20px;
                            color: #1EA1EE;
                        }

                        .exam-info__tag-txt {
                            font-size: 12px;
                            color: #1EA1EE;
                            line-height: 15px;
                        }
                    }

                }

                .exam-info__lab {
                    font-size: 14px;
                    color: #999999;
                    line-height: 30px;

                }

                .exam-info__value {
                    font-size: 14px;
                    color: #333333;
                }

                .exam-info__row {
                    display: flex;
                    justify-content: space-between;
                    padding-right: 36px;
                }

                .exam-info__row1 {
                    display: grid;
                    grid-template-columns: repeat(6, 1fr);
                    margin-top: 10px;

                    .exam-info__column {
                        display: flex;
                        position: relative;
                        flex-direction: column;
                        align-items: center;

                        &::before {
                            content: '';
                            position: absolute;
                            left: 0px;
                            top: 50%;
                            transform: translateY(-50%);
                            width: 1px;
                            height: 46px;
                            background-color: #E5E5E5;
                        }

                        &:last-child {
                            &::after {
                                content: '';
                                position: absolute;
                                right: 0px;
                                top: 50%;
                                transform: translateY(-50%);
                                width: 1px;
                                height: 46px;
                                background-color: #E5E5E5;
                            }
                        }
                    }
                }
            }
        }
    }

    .exam-set__main {
        margin-top: 20px;
        padding: 30px;
        border-radius: 15px 15px 15px 15px;
        background-color: #FFFFFF;

        .m {
            display: flex;
            align-items: flex-end;
        }
    }

}
</style>