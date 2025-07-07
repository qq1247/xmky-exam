<template>
    <el-scrollbar height="calc(100vh - 230px)" class="paper-select">
        <div class="paper-select__main">
            <div class="paper-create">
                <span class="iconfont icon-a-16ri-02 paper-create__icon"></span>
                <span class="paper-create__title">空白试卷</span>
                <span class="paper-create__desc">从零开始创建一张试卷</span>
                <el-button type="primary" class="paper-create__btn" @click="emit('select', 'blank')">创建</el-button>
            </div>
            <div class="paper-create">
                <span class="iconfont icon-kuaisushengcheng paper-create__icon"></span>
                <span class="paper-create__title">快速生成试卷</span>
                <span class="paper-create__desc">即时编辑试题，快速导入</span>
                <el-button type="primary" class="paper-create__btn" @click="emit('select', 'quick')">创建</el-button>
            </div>
            <div class="paper-create">
                <span class="iconfont icon-a-16ri-01 paper-create__icon"></span>
                <span class="paper-create__title">随机试卷</span>
                <span class="paper-create__desc">配置抽题规则，每个人试卷不一样</span>
                <el-button type="primary" class="paper-create__btn" @click="emit('select', 'random')">创建</el-button>
            </div>
        </div>
        <div class="paper-select__foot">
            <div class="exam-copy__wrap">
                <div class="exam-copy__inner">
                    <div class="exam-copy-tip">
                        <span class="iconfont icon-tubiaoziti-10 exam-copy-tip__icon"></span>
                        <div class="exam-copy-tip__content">
                            <span class="exam-copy-tip__title">复制考试</span>
                            <span class="exam-copy-tip__desc">从已有考试创建一份副本，随后进行灵活调整。</span>
                        </div>
                    </div>
                    <div class="exam-copy-select">
                        <xmks-select v-model="copyExamId" url="exam/listpage" :params="{}" search-parm-name="name"
                            option-label="name" option-value="id" :options="[]" :multiple="false" clearable
                            search-placeholder="请输入考试名称进行筛选">
                            <template #default="{ option }">
                                {{ option.name }}
                            </template>
                        </xmks-select>
                    </div>
                    <div class="exam-copy-extract">
                        <el-radio-group v-model="importUser">
                            <el-radio value="none">无</el-radio>
                            <el-radio value="all">导入考试用户</el-radio>
                            <el-radio value="retake">导入补考用户</el-radio>
                        </el-radio-group>
                    </div>
                </div>
            </div>
        </div>
    </el-scrollbar>
</template>
<script lang="ts" setup>
import { examPaper, examUser } from '@/api/exam/exam';
import xmksSelect from '@/components/xmks-select.vue';
import { useExamStore } from '@/stores/exam';
import { ref, watch } from 'vue';

/************************变量定义相关***********************/
const emit = defineEmits<{
    (e: 'select', type: string): void
}>()

const form = useExamStore() // 表单
const copyExamId = ref()
const importUser = ref('none')

/************************监听相关*****************************/
watch(() => copyExamId.value, () => {
    copy(copyExamId.value)
})

/************************事件相关*****************************/
// 复制考试
async function copy(id: number) {
    const { data: { data } } = await examPaper({ id })
    form.id = null
    form.name = data.name + '-复制'
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
    form.loginType = data.loginType
    form.anonState = data.anonState
    form.scoreState = data.scoreState
    form.rankState = data.rankState
    form.state = 1
    if (data.genType === 1) {
        form.examQuestions = data.examQuestions
    } else {
        form.examRules = data.examRules
    }
    form.userIds = []
    form.markUserIds = []
    form.limitMinute = data.limitMinute

    form.questionNoUpdate()
    if (form.loginType === 1) {// 匿名考试不需要导入考试用户
        if (importUser.value === 'all') {
            const { data: { data: myExams } } = await examUser({ id })
            myExams.forEach((myExam: { userId: number; answerState: number; }) => {
                form.userIds.push(myExam.userId)
            });
        } else if (importUser.value === 'retake') {
            const { data: { data: myExams } } = await examUser({ id })
            myExams.forEach((myExam: { userId: number; answerState: number; }) => {
                console.log(myExam);
                if (myExam.answerState === 2) {
                    form.userIds.push(myExam.userId)
                }
            });
        }
    }

    if (data.genType === 1) {
        emit('select', 'blank')
    } else {
        emit('select', 'random')
    }
}
</script>

<style scoped lang="scss">
.paper-select {
    flex: 1;
    background-color: #f2f3f5;

    :deep(.el-scrollbar__view) {
        .paper-select__main {
            display: flex;
            justify-content: space-between;
            background-color: white;
            padding: 20px 20px 40px 20px;
            border-radius: 15px 15px 15px 15px;

            .paper-create {
                display: flex;
                flex-direction: column;
                align-items: center;
                width: 320px;
                height: 217px;

                .paper-create__icon {
                    margin-top: 37px;
                    font-size: 48px;
                    background-clip: text;
                    color: transparent;
                }

                .paper-create__title {
                    margin-top: 10px;
                    font-size: 16px;
                    color: #333333;
                }

                .paper-create__desc {
                    margin-top: 10px;
                    font-size: 14px;
                    color: #999999;
                }

                .paper-create__btn {
                    width: 130px;
                    height: 38px;
                    border-radius: 8px;
                    margin-top: 20px;
                    color: white;
                    font-size: 14px;
                    border: none;
                    background-image: linear-gradient(to right, #04C7F2, #259FF8);
                }

                &:nth-child(1) {
                    background: url('/img/exam/step/paper-select/blank_bg.png') no-repeat;
                    background-size: cover;

                    .paper-create__icon {
                        background-image: linear-gradient(0deg, #259ff8, #04c7f2);
                    }
                }

                &:nth-child(2) {
                    width: 480px;
                    background: url('/img/exam/step/paper-select/quick_bg.png') no-repeat;
                    background-size: cover;

                    .paper-create__icon {
                        background-image: linear-gradient(0deg, #02DC8C, #3ABD84);
                    }
                }

                &:nth-child(3) {
                    background: url('/img/exam/step/paper-select/random_bg.png') no-repeat;
                    background-size: cover;

                    .paper-create__icon {
                        background-image: linear-gradient(0deg, #A49EF6, #8C84F4);
                    }
                }

            }
        }

        .paper-select__foot {
            display: flex;
            margin-top: 20px;
            background-color: white;
            height: 228px;
            border-radius: 15px 15px 15px 15px;

            .exam-copy__wrap {
                flex: 1;
                display: flex;
                flex-direction: column;
                justify-content: center;
                align-items: center;
                margin: 20px;
                border: 1px dashed #E5E5E5;
                border-radius: 15px 15px 15px 15px;

                .exam-copy__inner {
                    width: 460px;

                    .exam-copy-tip {
                        display: flex;
                        margin-bottom: 20px;

                        .exam-copy-tip__icon {
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            width: 45px;
                            height: 45px;
                            padding: 5px;
                            font-size: 28px;
                            color: #cccccc;
                            border: 1px dashed #E5E5E5;
                        }

                        .exam-copy-tip__content {
                            display: flex;
                            flex-direction: column;
                            justify-content: space-between;
                            margin-left: 20px;

                            .exam-copy-tip__title {
                                font-size: 14px;
                                color: #000000;
                            }

                            .exam-copy-tip__desc {
                                font-size: 14px;
                                color: #999999;
                            }
                        }
                    }

                    .exam-copy-select {
                        .el-select__selection {
                            height: 34px;
                        }
                    }

                    .exam-copy-extract {
                        margin-top: 10px;
                    }
                }

            }
        }
    }
}
</style>
