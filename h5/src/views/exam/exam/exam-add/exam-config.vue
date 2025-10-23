<template>
    <div class="exam-config">
        <el-scrollbar height="calc(100vh - 310px)" class="exam-config__main">
            <el-form ref="formRef" :model="form" :rules="formRules" label-width="100" size="large">
                <div class="exam-config-title">
                    基础配置
                </div>
                <el-form-item label="登录方式：" prop="loginType"
                    @mouseover="tipShow('登录方式', '账号登录：一般用于企业内部考试，需要企业用户输入账号密码登录进行考试。<br/>临时登录：一般用于面试等一次性考试。在登录页右下角点击 临时登录 按钮，输入考试名称、自定义内容就可以考试。')">
                    <el-radio-group v-model="form.loginType">
                        <el-radio v-for="(option, index) in dictStore.getList('LOGIN_TYPE')" :key="index"
                            :value="parseInt(option.dictKey)" border>
                            {{ option.dictValue }}
                        </el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="考试名称：" prop="name" @mouseover="tipShow('考试名称', '自定义考试名称')">
                    <el-input v-model="form.name" placeholder="请输入考试名称" />
                </el-form-item>
                <el-form-item label="考试时间：" prop="examTimes"
                    @mouseover="tipShow('考试时间', '考试开始时间：必须大于考试结束时间和当前时间；<br/>考试结束时间：必须小于阅卷开始时间')">
                    <el-date-picker v-model="form.examTimes" type="datetimerange" start-placeholder="开始时间"
                        end-placeholder="结束时间" value-format="YYYY-MM-DD HH:mm:ss" format="YYYY-MM-DD HH:mm" @change="() => {
                            if (form.limitMinute >= form.examTimeDiff) {
                                form.limitMinute = form.examTimeDiff - 1
                            }
                        }" />
                </el-form-item>
                <el-form-item v-if="form.markType === 2" label="阅卷时间：" prop="markTimes"
                    @mouseover="tipShow('阅卷时间', `阅卷开始时间必须大于考试结束时间，阅卷结束时间必须大于阅卷开始时间<br/>当前试卷有主观题（${markQuestions}），需要人工阅卷，请指定人工阅卷时间`)">
                    <el-date-picker v-model="form.markTimes" type="datetimerange" start-placeholder="开始时间"
                        end-placeholder="结束时间" value-format="YYYY-MM-DD HH:mm:ss" format="YYYY-MM-DD HH:mm">
                    </el-date-picker>
                </el-form-item>
                <el-form-item label="限时答题：" prop="limitMinute"
                    @mouseover="tipShow('限时答题', '等于0：不限时，只要考试时间范围内任意时间，考试用户都可以进入<br/>大于0：表示限时，但不超过120分钟<br/>限时计算时间策略：从用户第一次打开试卷开始计时，限时60分钟，但不超过考试结束时间。如：限时60分钟，但考试用户最后半小时进入，最多只能答题半小时。')">
                    <el-input-number v-model="form.limitMinute" controls-position="right" :min="0"
                        :max="form.examTimeDiff - 1" :step="10" :precision="0" />&nbsp;/ {{ form.examTimeDiff }}分钟
                </el-form-item>
                <el-form-item label="及格分数：" prop="passScore"
                    @mouseover="tipShow('及格分数', '默认总分的60%，可以二次修改。会在考试统计页面显示是否及格。')">
                    <el-input-number v-model="form.passScore" controls-position="right" :min="0" :max="form.totalScore"
                        :step="10" :precision="1" />&nbsp;/ {{ form.totalScore }}分
                </el-form-item>
                <div class="exam-config-title">
                    高级配置
                </div>
                <el-form-item label="防作弊：" prop="sxes" @mouseover="tipShow('防作弊',
                    `试题乱序：章节内试题进行随机排序，无章节则所有试题进行随机排序<br/>
                    选项乱序：单选多选题的选项进行随机排序<br/>
                    禁止考试中切屏：禁止切出浏览器查询资料，一分钟内算一次，超出3次自动交卷；<br/>
                    禁止浏览器调试：禁止复制粘贴、右键、调试代码等`)">
                    <el-checkbox-group v-model="form.sxes">
                        <el-checkbox v-for="dict in dictStore.getList('EXAM_SXES').filter(dict => {
                            if (form.genType === 2) {
                                if (dict.dictKey == '2' || dict.dictKey == '3' || dict.dictKey === '4') {
                                    return true
                                }
                                return false
                            }
                            return true
                        }
                        )" :key="dict.dictKey" :value="dict.dictKey">{{ dict.dictValue }}</el-checkbox>
                    </el-checkbox-group>
                </el-form-item>
                <!-- <el-form-item v-if="form.genType === 1" label="匿名阅卷：" prop="anonState">
                <el-radio-group v-model="form.anonState">
                    <el-radio v-for="(option, index) in dictStore.getList('STATE_YN')" :key="index" :label="parseInt(option.dictKey)">
                        {{ option.dictValue }}
                    </el-radio>
                </el-radio-group>
            </el-form-item> -->

                <el-form-item label="查询成绩：" prop="scoreState"
                    @mouseover="tipShow('查询成绩', '考试结束后：整场考试结束后，考试用户才可查询成绩<br/>不公布：考试用户不可以查询成绩<br/>交卷后：考试用户交卷后，可立即查看成绩（客观题试卷有效）。')">
                    <el-radio-group v-model="form.scoreState">
                        <el-radio v-for="(option, index) in dictStore.getList('SCORE_STATE')" :key="index"
                            :value="parseInt(option.dictKey)"
                            :disabled="(form.markType === 2 && option.dictKey === '3') ? true : false">
                            {{ option.dictValue }}
                        </el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="公布排名：" prop="rankState"
                    @mouseover="tipShow('公布排名', '公布：整场考试结束后，考试用户可查询排名<br/>不公布：考试用户不可查询排名')">
                    <el-radio-group v-model="form.rankState">
                        <el-radio v-for="(option, index) in dictStore.getList('STATE_ON')" :key="index"
                            :value="parseInt(option.dictKey)">
                            {{ option.dictValue }}
                        </el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item v-if="form.markType === 1" label="重考次数：" prop="retakeNum"
                    @mouseover="tipShow('重考次数', '1、如果是客观题试卷，在用户考试不及格时，可以重考的次数<br/>2、整场考试结束后，不允许重考')">
                    <el-input-number v-model="form.retakeNum" controls-position="right" :min="0" :max="10" :step="1"
                        :precision="0" />&nbsp;/ 10次
                </el-form-item>
            </el-form>
        </el-scrollbar>
        <div class="exam-config-side">
            <span class="exam-config-side__tip">配置说明：</span>
            <span class="exam-config-side__title" v-html="tip.title"></span>
            <span class="exam-config-side__desc" v-html="tip.desc"></span>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, computed } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { useExamStore } from '@/stores/exam'
import { useDictStore } from '@/stores/dict'
import dayjs from 'dayjs'

/************************变量定义相关***********************/
defineExpose({ next })
const dictStore = useDictStore()// 字典缓存
const formRef = ref<FormInstance>()// 表单引用
const form = useExamStore()
const formRules = reactive<FormRules>({// 表单校验规则
    name: [
        { required: true, message: '请输入考试名称', trigger: 'blur' },
        { min: 1, max: 16, message: '长度介于1-16', trigger: 'blur' },
    ],
    examTimes: [{
        trigger: 'change',
        validator: (rule: any, value: string[], callback: any) => {
            if (!value || !value[0] || !value[1]) {
                return callback(new Error("请选择考试时间"))
            }
            if (dayjs(value[0], 'YYYY-MM-DD HH:mm:ss').toDate() >= dayjs(value[1], 'YYYY-MM-DD HH:mm:ss').toDate()) {
                return callback(new Error("开始时间必须小于结束时间"))
            }
            return callback()
        }
    }],
    markTimes: [{
        trigger: 'change',
        validator: (rule: any, value: any, callback: any) => {
            if (!form.markTimes || !form.markTimes[0] || !form.markTimes[1]) {
                return callback(new Error("请选择阅卷时间"))
            }
            if (dayjs(form.markTimes[0], 'YYYY-MM-DD HH:mm:ss').toDate() >= dayjs(form.markTimes[1], 'YYYY-MM-DD HH:mm:ss').toDate()) {
                return callback(new Error("开始时间必须大于结束时间"))
            }
            if (!form.examTimes || !form.examTimes[0] || !form.examTimes[1]) {
                return callback()
            }
            if (dayjs(form.examTimes[1], 'YYYY-MM-DD HH:mm:ss').toDate() >= dayjs(form.markTimes[0], 'YYYY-MM-DD HH:mm:ss').toDate()) {
                return callback(new Error("考试结束时间必须小于阅卷开始时间"))
            }
            return callback()
        }
    }],
    passScore: [
        { required: true, message: '请输入及格分数', trigger: 'blur' },
    ],
    limitMinute: [
        { required: true, message: '请输入限时答题', trigger: 'blur' },
    ],
})

const tip = reactive({// 提示信息
    title: '',
    desc: '',
})
/************************组件生命周期相关*********************/
onMounted(async () => {
    // 如果没有及格分数，默认为总分的百分之六十
    if (!form.passScore) {
        form.passScore = Math.round(form.totalScore * 0.6)
    }

    // 如果是随机组卷，去掉随机选项
    if (form.genType === 2) {
        form.sxes = []
    }

    if (form.markType === 2 && form.scoreState === 3) {// 第一次是客观试卷，后添加主观题，如果选的交卷后，改为考试后
        form.scoreState = 1
    }
})

/************************计算属性相关*************************/
const markQuestions = computed(() => {// 阅卷题号
    let questionNums = ''
    form.markQuestions.forEach((markQuestion: any) => {
        if (questionNums) {
            questionNums += '、'
        }
        questionNums += markQuestion.no
    })
    return questionNums
})

/************************事件相关*****************************/
// 下一步
async function next() {
    try {
        await formRef.value?.validate()
        return true
    } catch (e) {
        ElMessage.error(`部分信息填写错误，请检查`)
        return false
    }
}
// 提示显示
function tipShow(title: string, desc: string) {
    tip.title = title
    tip.desc = desc

}

</script>

<style lang="scss" scoped>
.exam-config {
    flex: 1;
    display: flex;
    background-color: #ffffff;
    border-radius: 15px 15px 15px 15px;

    .exam-config__main {
        flex: 1;
        padding: 40px;
        border-right: 1px dashed #E5E5E5;
        height: initial;

        .exam-config-title {
            position: relative;
            border-bottom: 1px solid #E5E5E5;
            padding: 0px 10px 8px 15px;
            margin-bottom: 20px;
            font-weight: bold;
            font-size: 16px;
            color: #333333;

            &::after {
                content: "";
                position: absolute;
                left: 5px;
                top: 1px;
                width: 4px;
                height: 16px;
                border-radius: 2px 2px 2px 2px;
                background: #3AA8EF;
            }
        }
    }

    .exam-config-side {
        display: flex;
        flex-direction: column;
        width: 300px;
        padding: 90px 20px;

        .exam-config-side__tip {
            font-size: 18px;
            color: #333333;
            line-height: 30px;
        }

        .exam-config-side__title {
            font-size: 14px;
            color: #333333;
            line-height: 30px;
        }

        .exam-config-side__desc {
            font-size: 14px;
            color: #999999;
            line-height: 30px;
        }
    }
}
</style>
