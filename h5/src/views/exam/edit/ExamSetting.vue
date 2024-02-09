<template>
    <el-card class="exam-setting" shadow="never">
        <el-form ref="formRef" :model="form" :rules="formRules" label-width="100" style="width: 500px;">
            <el-form-item label="考试名称：" prop="name">
                <el-input v-model="form.name" placeholder="请输入考试名称" />
            </el-form-item>
            <el-form-item label="考试时间：" prop="examTimes">
                <el-date-picker 
                    v-model="form.examTimes" 
                    type="datetimerange" 
                    start-placeholder="开始时间"
                    end-placeholder="结束时间"
                    value-format="YYYY-MM-DD HH:mm:ss"
                    format="YYYY-MM-DD HH:mm"
                    @change="() => { 
                        if (form.limitMinute >= form.examTimeDiff)  {
                            form.limitMinute = form.examTimeDiff - 1
                        }
                    }"
                    />
            </el-form-item>
            <el-form-item v-if="form.markType === 2" label="阅卷时间：" prop="markTimes">
                <el-date-picker 
                    v-model="form.markTimes" 
                    type="datetimerange" 
                    start-placeholder="开始时间"
                    end-placeholder="结束时间"
                    value-format="YYYY-MM-DD HH:mm:ss"
                    format="YYYY-MM-DD HH:mm"
                    >
                </el-date-picker>
                <el-alert :title="`需要阅卷题号：${markQuestions}`" type="warning" :closable="false"/>
            </el-form-item>
            <el-form-item label="限制时长：" prop="limitMinute">
                <el-input-number 
                    v-model="form.limitMinute" 
                    controls-position="right" 
                    :min="0" 
                    :max="form.examTimeDiff - 1"
                    :step="10"
                    :precision="0"
                    />&nbsp;/ {{form.examTimeDiff}}分钟
            <el-alert :title="`从第一次打开试卷开始计时，最长能考几分钟，0代表不限制`" type="warning" :closable="false"/>
            </el-form-item>
            <el-form-item label="及格分数：" prop="passScore">
                <el-input-number 
                    v-model="form.passScore" 
                    controls-position="right" 
                    :min="0" 
                    :max="form.totalScore"
                    :step="10"
                    :precision="1"
                    />&nbsp;/ {{form.totalScore}}分
            </el-form-item>
            <el-form-item v-if="form.genType === 1" label="防作弊：" prop="sxes">
                <el-checkbox-group v-model="form.sxes">
                    <el-checkbox :label="1" key="1">试题乱序</el-checkbox>
                    <el-checkbox :label="2" key="2">选项乱序</el-checkbox>
                </el-checkbox-group>
            </el-form-item>
            <!-- <el-form-item v-if="form.genType === 1" label="匿名阅卷：" prop="anonState">
                <el-radio-group v-model="form.anonState">
                    <el-radio v-for="(option, index) in dictStore.getList('STATE_YN')" :key="index" :label="parseInt(option.dictKey)">
                        {{ option.dictValue }}
                    </el-radio>
                </el-radio-group>
            </el-form-item> -->
            <el-form-item label="成绩查询：" prop="scoreState">
                <el-radio-group v-model="form.scoreState">
                    <el-radio 
                        v-for="(option, index) in dictStore.getList('SCORE_STATE')" 
                        :key="index" 
                        :label="parseInt(option.dictKey)"
                        :disabled="(form.markType === 2 && option.dictKey === '3') ? true : false"
                        >
                        {{ option.dictValue }}
                    </el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="排名公布：" prop="rankState">
                <el-radio-group v-model="form.rankState">
                    <el-radio v-for="(option, index) in dictStore.getList('STATE_ON')" :key="index" :label="parseInt(option.dictKey)">
                        {{ option.dictValue }}
                    </el-radio>
                </el-radio-group>
            </el-form-item>
        </el-form>
    </el-card>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, computed } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { useExamStore, type ExamQuestion } from '@/stores/exam';
import { useDictStore } from '@/stores/dict';
import dayjs from 'dayjs';

// 定义变量
defineExpose({ next });
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
            if(!value || !value[0] || !value[1]) {
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
            if(!form.markTimes || !form.markTimes[0] || !form.markTimes[1]) {
                return callback(new Error("请选择阅卷时间"))
            }
            if (dayjs(form.markTimes[0], 'YYYY-MM-DD HH:mm:ss').toDate() >= dayjs(form.markTimes[1], 'YYYY-MM-DD HH:mm:ss').toDate()) {
                return callback(new Error("开始时间必须大于结束时间"))
            }
            if (dayjs(form.examTimes[1], 'YYYY-MM-DD HH:mm:ss').toDate() >= dayjs(form.markTimes[0], 'YYYY-MM-DD HH:mm:ss').toDate()) {
                return callback(new Error("考试结束时间必须小于阅卷开始时间"))
            }
            return callback()
        }
    }],
    passScore: [
        { required: true, message: '请输入及格分数：', trigger: 'blur' },
    ],
})
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

// 组件挂载完成后，执行如下方法
onMounted(async () => {
    // 如果没有及格分数，默认为总分的百分之六十
    if(!form.passScore) {
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

// 下一步
async function next() {
    if (!formRef.value) return false
    return await formRef.value.validate((valid, fields) => {
        return valid
    })
}

</script>
  
<style lang="scss" scoped>
.exam-setting {
    flex: 1;
    .el-alert {
        padding: 0;
        height: 24px;
        :deep(.el-alert__title) {
            overflow: hidden;
            white-space: nowrap;
            text-overflow: ellipsis;
        }
    }
}
</style>