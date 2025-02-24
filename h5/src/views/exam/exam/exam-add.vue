<template>
    <div class="exam-add">
        <div class="exam-add__head">
            <div class="exam-pre-step" @click="preStep()">
                <span class="iconfont icon-zuo exam-pre-step__icon"
                    :class="{ 'exam-pre-step__icon--active': !isFirstStep(), 'exam-pre-step__icon--inactive': isFirstStep() }"></span>
                <span class="exam-pre-step__txt"
                    :class="{ 'exam-pre-step__txt--active': !isFirstStep(), 'exam-pre-step__txt--inactive': isFirstStep() }">
                    上一步
                </span>
            </div>
            <div class="exam-step__wrap">
                <template v-for="(step, index) in steps" :key="index">
                    <div class="exam-step__inner">
                        <el-image :src="isActiveStep(step) ? step.activeImg : step.img"
                            class="exam-step__img"></el-image>
                        <span class="exam-step__txt"
                            :class="{ 'exam-step__txt--active': isActiveStep(step), 'exam-step__txt--inactive': !isActiveStep(step) }">
                            {{ step.title }}
                        </span>
                        <span v-if="isCurStep(step)" class="iconfont icon-xiala exam-step__icon"
                            :class="{ 'exam-step__icon--active': isActiveStep(step), 'exam-step__icon--inactive': !isActiveStep(step) }">
                        </span>
                    </div>
                    <div v-if="index != steps.length - 1" class="exam-step__progress">
                        <div class="exam-step__progress-line"
                            :class="{ 'exam-step__progress--active': isActiveStepLine(step), 'exam-step__progress--inactive': !isActiveStepLine(step) }">
                        </div>
                        <span class="iconfont icon-xiala exam-step__progress-icon"
                            :class="{ 'exam-step__progress-icon--active': isActiveStepLine(step), 'exam-step__progress-icon--inactive': !isActiveStepLine(step) }">
                        </span>
                        <div class="exam-step__progress-line"
                            :class="{ 'exam-step__progress--active': isActiveStepLine(step), 'exam-step__progress--inactive': !isActiveStepLine(step) }">
                        </div>
                    </div>
                </template>
            </div>
            <div class="exam-next-step" @click="nextStep()">
                <span class="exam-next-step__txt exam-next-step__txt--active"
                    :class="{ 'exam-next-step__txt--active': !isLastStep(), 'exam-next-step__txt--inactive': isLastStep() }">
                    {{ isLastStep() ? '完成' : '下一步' }}
                </span>
                <span class="iconfont icon-you exam-next-step__icon "
                    :class="{ 'exam-next-step__icon--active': !isLastStep(), 'exam-next-step__icon--inactive': isLastStep() }">
                </span>
            </div>
        </div>
        <div class="exam-add__main">
            <paper-select v-if="isFirstStep()" @select="(type) => _paperSelect(type)"></paper-select>
            <paper-config v-if="curStep === 1 && form.genType === 1" ref="paperConfigRef"
                :paperShow="paperShow"></paper-config>
            <paper-rule v-if="curStep === 1 && form.genType === 2" ref="paperRuleRef"></paper-rule>
            <exam-config v-if="curStep === 2" ref="examConfigRef"></exam-config>
            <exam-user-select v-if="curStep === 3" ref="userSelectRef"></exam-user-select>
            <exam-publish v-if="curStep === 4" ref="userSelectRef" :publishShow="publishShow"></exam-publish>
            <exam-progress v-if="curStep === 5"></exam-progress>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { ref, onMounted, computed } from 'vue'
import { useExamStore } from '@/stores/exam'
import type { Step } from '@/ts/nav/step'
import PaperSelect from './exam-add/paper-select.vue'
import PaperConfig from './exam-add/paper-config.vue'
import PaperRule from './exam-add/paper-rule.vue'
import ExamConfig from './exam-add/exam-config.vue'
import ExamUserSelect from './exam-add/exam-user-select.vue'
import examPublish from './exam-add/exam-publish.vue'
import ExamProgress from './exam-add/exam-progress.vue'


/************************变量定义相关***********************/
const form = useExamStore() // 考试表单
const paperConfigRef = ref()// 试卷配置组件
const paperRuleRef = ref()// 试卷规则组件
const examConfigRef = ref()// 考试配置组件
const userSelectRef = ref()// 用户选择组件

const paperShow = ref('paper') // 试卷显示
const publishShow = ref('publish') // 发布显示

const curStep = ref(0);// 当前第几步
const steps = ref<Step[]>([// 步骤信息
    {
        title: '01 选择试卷',
        img: '/img/exam/step/paper-select/step01.png',
        activeImg: '/img/exam/step/paper-select/step01-active.png',
        step: 1,
    },
    {
        title: '02 配置试卷',
        img: '/img/exam/step/paper-select/step02.png',
        activeImg: '/img/exam/step/paper-select/step02-active.png',
        step: 2,
    },
    {
        title: '03 配置考试',
        img: '/img/exam/step/paper-select/step03.png',
        activeImg: '/img/exam/step/paper-select/step03-active.png',
        step: 3,
    },
    {
        title: '04 选择用户',
        img: '/img/exam/step/paper-select/step04.png',
        activeImg: '/img/exam/step/paper-select/step04-active.png',
        step: 4,
    },
    {
        title: '05 考试概览',
        img: '/img/exam/step/paper-select/step01.png',
        activeImg: '/img/exam/step/paper-select/step01-active.png',
        step: 5,
    },
    {
        title: '06 发布考试',
        img: '/img/exam/step/paper-select/step05.png',
        activeImg: '/img/exam/step/paper-select/step05-active.png',
        step: 6,
    },
])

/************************组件生命周期相关*********************/
onMounted(() => {

})

/************************计算属性相关*************************/
const isCurStep = computed(() => (step: Step) => step.step === curStep.value + 1); // 是否当前步骤
const isFirstStep = computed(() => () => curStep.value === 0); // 是否第一步
const isLastStep = computed(() => () => curStep.value === 5); // 是否最后一步
const isActiveStep = computed(() => (step: Step) => step.step <= curStep.value + 1); // 是否激活步骤
const isActiveStepLine = computed(() => (step: Step) => step.step < curStep.value + 1); // 是否激活步骤线

/************************事件相关*****************************/
// 下一步
async function nextStep() {
    if (curStep.value === 0) {// 试卷选择
        curStep.value++
        return
    }

    if (curStep.value === 1) {// 试卷配置
        const component = form.genType === 1 ? paperConfigRef : paperRuleRef
        if (await component.value.next()) {
            curStep.value++
        }
        return
    }

    if (curStep.value === 2) {// 考试配置
        const component = examConfigRef

        try {
            if (await component.value.next()) {
                curStep.value++
                if (form.loginType === 2) {// 如果是免登录，不需要选择考试用户
                    curStep.value++
                }
            }
        } catch (e) {
            alert(e)
            return false
        }
        return
    }

    if (curStep.value === 3) {// 用户选择
        const component = userSelectRef
        if (await component.value.next()) {
            curStep.value++
        }
        return
    }

    if (curStep.value === 4) {// 考试概览
        curStep.value++
        return
    }
}

// 上一步
async function preStep() {
    if (curStep.value <= 0) {
        return
    }

    curStep.value--
    if (curStep.value === 1) {// 后退的时候，恢复默认显示
        paperShow.value = 'paper'
        return
    }

    if (curStep.value === 3 && form.loginType === 2) {// 如果是免登录，不需要选择考试用户
        curStep.value--
        return
    }
}

// 试卷选择方式
function _paperSelect(type: string) {
    // 选择空白试卷，组固定试卷，显示空白试卷
    if (type === 'blank') {
        form.genType = 1
        paperShow.value = 'paper'
        nextStep()
    }
    // 选择在线导入，组固定试卷，显示试题编辑器
    else if (type === 'quick') {
        form.genType = 1
        paperShow.value = 'editor'
        nextStep()
    }
    // 选择随机试卷，组随机试卷，显示抽题规则页面
    else if (type === 'random') {
        form.genType = 2
        nextStep()
    }
}
</script>
<style scoped lang="scss">
.exam-add {
    display: flex;
    flex-direction: column;
    width: 1200px;
    margin: auto;

    .exam-add__head {
        display: flex;
        padding: 20px 20px 15px 20px;
        margin-top: 20px;
        border-radius: 15px;
        background-color: white;

        .exam-pre-step {
            display: flex;
            align-items: center;
            padding-left: 10px;
            width: 80px;
            cursor: pointer;

            .exam-pre-step__icon {
                font-size: 28px;
                color: #E6E6E6;

                &.exam-pre-step__icon--active {
                    color: #1EA1EE;
                }

                &.exam-pre-step__icon--inactive {
                    visibility: hidden;
                    color: #999999;
                }
            }


            .exam-pre-step__txt {
                display: flex;
                flex-direction: column;
                width: 15px;
                color: #999999;
                font-size: 14px;
                margin-left: 5px;

                &.exam-pre-step__txt--active {
                    color: #1EA1EE;
                }

                &.exam-pre-step__txt--inactive {
                    visibility: hidden;
                    color: #999999;
                }
            }
        }

        .exam-next-step {
            display: flex;
            justify-content: right;
            align-items: center;
            padding-right: 10px;
            width: 80px;
            cursor: pointer;

            .exam-next-step__icon {
                font-size: 26px;
                color: #E6E6E6;

                &.exam-next-step__icon--active {
                    color: #1EA1EE;
                }

                &.exam-next-step__icon--inactive {
                    visibility: hidden;
                    color: #999999;
                }
            }


            .exam-next-step__txt {
                display: flex;
                flex-direction: column;
                width: 15px;
                color: #1EA1EE;
                font-size: 14px;
                margin-left: 5px;

                &.exam-next-step__txt--active {
                    color: #1EA1EE;
                }

                &.exam-next-step__txt--inactive {
                    visibility: hidden;
                    color: #999999;
                }
            }

        }

        .exam-step__wrap {
            flex: 1;
            display: flex;
            align-items: center;
            padding: 0px 20px;

            .exam-step__inner {
                display: flex;
                flex-direction: column;
                align-items: center;
                position: relative;

                .exam-step__img {
                    height: 50px;
                    width: 50px;
                }

                .exam-step__txt {
                    margin-top: 4px;
                    font-size: 14px;

                    &.exam-step__txt--active {
                        color: #1EA1EE;
                    }

                    &.exam-step__txt--inactive {
                        color: #999999;
                    }
                }


                .exam-step__icon {
                    position: absolute;
                    bottom: -16px;
                    left: 50%;
                    transform: translateX(-50%);
                    color: #1EA1EE;
                    font-size: 20px;

                    &.exam-step__icon--active {
                        color: #1EA1EE;
                    }

                    &.exam-step__icon--inactive {
                        color: #999999;
                    }
                }


            }

            .exam-step__progress {
                flex: 1;
                display: flex;
                align-items: center;
                transform: translateY(-15px);

                .exam-step__progress-line {
                    flex: 1;
                    height: 2px;
                    background-color: #EAF1FF;
                }

                .exam-step__progress-icon {
                    transform: rotate(270deg);
                    color: #EAF1FF;

                    &.exam-step__progress-icon--active {
                        color: #1EA1EE;
                    }

                    &.exam-step__progress-icon--inactive {
                        color: #EAF1FF;
                    }
                }



                .exam-step__progress--active {
                    background-color: #1EA1EE;
                }

                .exam-step__progress--inactive {
                    background-color: #EAF1FF;
                }
            }
        }

    }

    .exam-add__main {
        flex: 1;
        display: flex;
        margin: 20px 0px;
        border-radius: 15px;
    }
}
</style>
