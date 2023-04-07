<template>
    <el-card class="exam-publish" shadow="never">
        <el-divider content-position="left">考试概览</el-divider>
        <el-form label-width="100" style="width: 500px;">
            <el-form-item label="考试名称：">{{ examStore.name }}</el-form-item>
            <el-form-item label="组卷方式：">{{ dictStore.getValue('PAPER_GEN_TYPE',  examStore.genType) }}</el-form-item>
            <el-form-item label="考试时间：">
                {{ examStore.examTimes[0] }} -
                {{ examStore.examTimes[1] }}
            </el-form-item>
            <el-form-item v-if="examStore.markType === 2" label="阅卷时间：">
                {{ examStore.markTimes[0] }} -
                {{ examStore.markTimes[1] }}
            </el-form-item>
            <el-form-item label="及格分数：">
                {{ examStore.passScore }} / {{ examStore.totalScore }}
            </el-form-item>
            <el-form-item label="防 作 弊：">
                {{ examStore.sxes.length ? '' : '无' }}
                {{ examStore.sxes.indexOf(1) !== -1 ? '试题乱序' : '' }}
                {{ examStore.sxes.indexOf(2) !== -1 ? '选项乱序' : '' }}
            </el-form-item>
            <!-- <el-form-item label="匿名阅卷：">
                {{ examStore.anonState === 1 ? '是' : '否' }}
            </el-form-item> -->
            <el-form-item label="成绩查询：">
                {{ dictStore.getValue('SCORE_STATE', examStore.scoreState) }}
            </el-form-item>
            <el-form-item label="排名公布：">
                {{ dictStore.getValue('STATE_ON', examStore.rankState) }}
            </el-form-item>
            <el-form-item label="考试人数：">
                考试{{ examStore.examUserNum }}人 
                <!-- {{ examStore.markType === 2 ? '，协助阅卷' + examStore.markUserNum + '人' : '' }} -->
            </el-form-item>
            <el-form-item label="试题数量：">
                客观题{{ examStore.objectiveQuestionNum }}道，主观题{{ examStore.subjectiveQuestionNum }}道
            </el-form-item>
        </el-form>
    </el-card>
</template>

<script lang="ts" setup>
import { useDictStore } from '@/stores/dict';
import { useExamStore } from '@/stores/exam';
import dayjs from 'dayjs'

// 定义变量
const dictStore = useDictStore() // 字典缓存
const examStore = useExamStore() // 考试缓存
</script>
  
<style lang="scss" scoped>
.exam-publish {
    flex: 1;

    .el-form-item {
        margin-bottom: 0
    }
}
</style>