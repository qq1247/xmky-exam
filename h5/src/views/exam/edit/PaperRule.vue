<template>
    <div class="paper-rule">
        <!-- 答题卡 -->
        <el-card shadow="never" class="paper-rule-left">
            <el-divider>
                答题卡 - 拖动排序
            </el-divider>
            <draggable v-model="form.examRules" item-key="no" @end="form.noUpdate()">
                <template #item="{ element }">
                    <div v-if="element.type === 1" class="paper-rule-left-chapter">{{ element.chapterName }}</div>
                    <el-button v-else type="primary" plain>{{ element.no }}</el-button>
                </template>
            </draggable>
        </el-card>
        <!-- 试卷规则 -->
        <el-scrollbar height="calc(100vh - 200px)" class="paper-rule-right">
            <el-card shadow="never">
                <el-form ref="formRef" :model="form" :rules="formRules" label-width="100" :inline="true">
                    <!-- 工具条 -->
                    <div class="paper-rule-right-toolbar">
                        <el-button type="success" size="small" round @click="ruleAdd()">
                            <span class="iconfont icon-plus" style="font-size: 14px;"></span>&nbsp;规则添加
                        </el-button>
                        <el-button type="success" size="small" round @click="chapterAdd()">
                            <span class="iconfont icon-plus" style="font-size: 14px;"></span>&nbsp;章节添加
                        </el-button>
                        <el-button type="success" size="small" round @click="paperReset()">
                            <span class="iconfont icon-clear" style="font-size: 14px;"></span>&nbsp;试卷重置
                        </el-button>
                    </div>
                    <!-- 右上角显示打分 -->
                    <div class="paper-rule-right-score">
                        <span class="paper-rule-right-score-value">{{ form.totalScore }}</span>
                        <span class="iconfont icon-fenshudixian"></span>
                    </div>
                    <!-- 试卷名称
                    <div class="paper-rule-right-title">
                        <el-input v-model="form.paperName" :maxlength="16" placeholder="请输入试卷名称" />
                    </div> -->
                    <!-- 抽题规则 -->
                    <template v-for="(examRule, index) in form.examRules" :key="index">
                        <!-- 章节 -->
                        <div v-if="examRule.type === 1" class="paper-rule-right-chapter">
                            <el-form-item :prop="`examRules[${index}].chapterName`" :rules="formRules.chapterName">
                                <el-input v-model="examRule.chapterName" maxlength="14" placeholder="请输入章节名称" />
                            </el-form-item>
                            <el-input v-model="examRule.chapterTxt" type="textarea" maxlength="128"
                                :autosize="{ minRows: 1 }" resize="none" placeholder="请输入章节描述" />
                            <el-button type="danger" @click="chapterDel(examRule as ExamQuestion)" size="small" circle>
                                <span class="iconfont icon-close" style="font-size:12px;font-weight: bold;"></span>
                            </el-button>
                        </div>
                        <!-- 规则 -->
                        <div v-if="examRule.type === 2" class="paper-rule-right-rule">
                            <!-- 题库 -->
                            <el-form-item 
                                :prop="`examRules[${index}].questionTypeId`" 
                                :rules="formRules.questionTypeId" 
                                style="width: 150px;"
                                >
                                <Select
                                    v-model="examRule.questionTypeId"
                                    url="questionType/listpage"
                                    :params="{}"
                                    search-parm-name=""
                                    option-value="id"
                                    option-label="name"
                                    :multiple="false"
                                    search-placeholder="请输入题库名称进行过滤"
                                    :options="questionTypeListpage.list"
                                    placeholder="请选择题库"
                                >
                                    <template #default="{ option }">
                                        {{option.name}} - {{ option.questionNum }}题
                                    </template>
                                </Select>
                            </el-form-item>
                            <!-- 试题类型 -->
                            <el-form-item 
                                :prop="`examRules[${index}].questionType`"
                                :rules="formRules.questionType" 
                                style="width: 100px;"
                                >
                                <el-select 
                                    v-model="examRule.questionType" 
                                    clearable 
                                    placeholder="请选择试题类型"
                                    @change="() => {
                                        if (examRule.questionType === 2) {// 如果是多选，设置漏选分数为分数一半
                                            if (examRule.scores && examRule.score) {
                                                examRule.scores = examRule.score / 2
                                            }
                                        } else {// 否则清空子分数
                                            examRule.scores = undefined
                                        }
                                        examRule.markOptions = [] // 清空阅卷选项
                                    }"
                                    >
                                    <el-option v-for="dict in dictStore.getList('QUESTION_TYPE')" :key="dict.dictKey"
                                        :label="dict.dictValue" :value="parseInt(dict.dictKey)" />
                                </el-select>
                            </el-form-item>
                            <!-- 阅卷类型 -->
                            <el-form-item 
                                v-if="examRule.questionType === 3 || examRule.questionType === 5" 
                                :prop="`examRules[${index}].markType`"
                                :rules="formRules.markType" 
                                style="width: 100px;"
                                :disabled="true"
                                >
                                <el-select v-model="examRule.markType" clearable placeholder="阅卷类型" disabled>
                                    <el-option v-for="dict in dictStore.getList('PAPER_MARK_TYPE')" :key="dict.dictKey"
                                        :label="dict.dictValue" :value="parseInt(dict.dictKey)" />
                                </el-select>
                            </el-form-item>
                            <!-- 阅卷选项（智能填空或智能问答时显示） -->
                            <el-form-item 
                                v-if="examRule.markType === 1 && (examRule.questionType === 3 || examRule.questionType === 5)" 
                                :prop="`examRules[${index}].markOptions`" 
                                :rules="formRules.markOptions" 
                                style="width: 190px;"
                                >
                                <el-select v-model="examRule.markOptions" clearable placeholder="阅卷选项" multiple collapse-tags>
                                    <template v-for="dict in dictStore.getList('QUESTION_MARK_OPTIONS')" :key="dict.dictKey">
                                        <el-option 
                                            v-if="dict.dictKey === '3' || (dict.dictKey === '2' && examRule.questionType === 3)" 
                                            :label="dict.dictValue" 
                                            :value="parseInt(dict.dictKey)"
                                            />
                                    </template>
                                </el-select>
                            </el-form-item>
                            <!-- 题数 -->
                            <el-form-item
                                :prop="`examRules[${index}].num`" 
                                :rules="formRules.num"
                                >
                                添加
                                <el-input-number v-model="examRule.num" :min="1" :max="100" :step="10" :precision="0" controls-position="right" />
                                题，
                            </el-form-item>
                            <!-- 分数 -->
                            <el-form-item 
                                :prop="`examRules[${index}].score`" 
                                :rules="formRules.score"
                                >
                                每题
                                <el-input-number 
                                    v-model="examRule.score" 
                                    :min="0.5" 
                                    :max="20" 
                                    :step="0.5" 
                                    :precision="1"
                                    controls-position="right" 
                                    @change="() => {// 如果是多选，修改时默认漏选分数为分数的一半
                                        if (examRule.questionType === 2) {
                                            if (examRule.scores && examRule.score) {
                                                examRule.scores = examRule.score / 2
                                            }
                                        }
                                    }"/>
                                分，
                            </el-form-item>
                            <!-- 漏选分数 -->
                            <el-form-item 
                                v-if="examRule.questionType === 2"
                                :prop="`examRules[${index}]`" 
                                :rules="formRules.scores"
                                >
                                漏选
                                <el-input-number 
                                    v-model="examRule.scores" 
                                    :min="0" 
                                    :max="20" 
                                    :step="0.5" 
                                    :precision="1"
                                    controls-position="right"
                                    @change="() => {// 如果漏选分数大于等于分数，漏选分数恢复成分数一半
                                        if (examRule.questionType === 2) {
                                            if (examRule.scores && examRule.score) {
                                                if (examRule.scores >= examRule.score) {
                                                    examRule.scores = examRule.score / 2
                                                }
                                            }
                                        }
                                    }"/>
                                分
                            </el-form-item>
                            <el-form-item>
                                <el-button v-if="index > 0" type="danger" @click="ruleDel(examRule as ExamQuestion)" size="small" text>
                                    <span class="iconfont icon-close" style="font-size: 12px;font-weight: bold;"></span>
                                </el-button>
                            </el-form-item>
                        </div>
                    </template>
                </el-form>
            </el-card>
        </el-scrollbar>
    </div>
</template>
<script lang="ts" setup>
import { computed, onMounted, reactive, ref, watch } from 'vue';
import { useDictStore } from '@/stores/dict';
import { useExamStore, type ExamRule, type ExamQuestion } from '@/stores/exam';
import draggable from 'vuedraggable'
import type { FormInstance, FormRules } from 'element-plus';
import Select from '@/components/Select.vue';
import http from '@/request';

// 定义变量
defineExpose({ next });
const dictStore = useDictStore() // 字典储存
const form = useExamStore() // 表单
const formRef = ref<FormInstance>()// 表单引用
const formRules = reactive<FormRules>({// 表单校验规则
    chapterName: [{ required: true, message: '请输入章节名称', trigger: 'change' }],
    questionTypeId: [{ required: true, message: '请选择题库', trigger: 'change' }],
    questionType: [{ required: true, message: '请选择试题类型', trigger: 'change' }],
    markType: [{ required: true, message: '请选择阅卷类型', trigger: 'change' }],
    markOptions: [{ required: false, message: '请选择阅卷选项', trigger: 'change' }],
    num: [{
        trigger: 'change',
        validator: (rule: any, value: any, callback: any) => {
            if (!value) {
                return callback(new Error('请输入题数'))
            }
            if (!/^[0-9]*$/.test(value)) {
                return callback(new Error('请输入正整数'))
            }
            if (value > 100) {
                return callback(new Error('最大100题'))
            }
            return callback()
        }
    }],
    score: [{
        trigger: 'change',
        validator: (rule: any, value: any, callback: any) => {
            if (!value) {
                return callback(new Error('请输入分数'))
            }
            if (!/^(([1-9]{1}\d*)|(0{1}))(\.\d{1,2})?$/.test(value)) {
                return callback(new Error('正数，最多两位小数'))
            }
            if (value > 20) {
                return callback(new Error('最大20分'))
            }
            return callback()
        }
    }],
    scores: [{
        trigger: 'change',
        validator: (rule: any, value: ExamRule, callback: any) => {
            if (value.scores == null) {
                return callback(new Error('请输入分数'))
            }
            if (!/^(([1-9]{1}\d*)|(0{1}))(\.\d{1,2})?$/.test(value.scores.toString())) {
                return callback(new Error('正数，最多两位小数'))
            }
            if (value.score && value.score <= value.scores) {
                return callback(new Error('不能大于分数'))
            }
            if (value.scores > 20) {
                return callback(new Error('最大20分'))
            }
            return callback()
        }
    }],
})
const questionTypeListpage = reactive({// 题库分页列表
    curPage: 1,
    pageSize: 100,
    total: 0,
    list: [] as any[],
})

// 组件挂载完成后，执行如下方法
onMounted(async () => {
    // 如果页面什么都没有，创建一个章节和一个规则
    if (form.examRules.length === 0) {
        chapterAdd()
        ruleAdd()
    }

    while (true) {
        let { data: { data } } = await http.post("questionType/listpage", { 
            curPage: questionTypeListpage.curPage++,
            pageSize: questionTypeListpage.pageSize,
        })

        questionTypeListpage.list.push(...data.list)
        if (questionTypeListpage.list.length >= data.total) {// 分批次取出
            break
        }
    }
})

// 章节添加
function chapterAdd() {
    form.examRules.push({
        no: 0,
        type: 1,
        chapterName: '单选题',
        chapterTxt: '',
    })
}

// 章节删除
function chapterDel(examRule: ExamQuestion) {
    ruleDel(examRule)
}

// 规则添加
function ruleAdd() {
    form.examRules.push({
        no: 0,
        type: 2,
        questionTypeId: undefined,
        questionType: 1,
        markType: 1,
        markOptions: [],
        num: 10,
        score: 1,
        scores: undefined,
    })
    form.noUpdate()
}

// 规则删除
function ruleDel(examRule: ExamQuestion) {
    form.examRules = form.examRules.filter(cur => examRule != cur)
    form.noUpdate()
}

// 试卷重置
function paperReset() {
    form.examRules = []
}

// 下一步
async function next() {
    if (!formRef.value) return false
    return await formRef.value.validate((valid, fields) => {
        return valid
    })
}
</script>

<style lang="scss" scoped>
.paper-rule {
    flex: 1;
    display: flex;

    .paper-rule-left {
        width: 240px;
        min-height: 300px;

        .el-button {
            height: 30px;
            width: 30px;
            padding: 0;
            border: 0;
            margin: 2px;
        }

        :deep(.el-divider__text) {
            width: 150px;
        }

        .paper-rule-left-chapter {
            font-size: 13px;
            font-weight: bold;
            margin: 5px 0px;
            cursor: pointer;
        }
    }

    .paper-rule-right {
        flex: 1;

        :deep(.el-card) {
            min-height: calc(100vh - 200px);
            margin-left: 10px;
            position: relative;

            &:hover {
                .paper-rule-right-toolbar {
                    display: block;
                }
            }

            .paper-rule-right-toolbar {
                position: absolute;
                right: 10px;
                top: 5px;
                display: none;
                z-index: 2;
            }

            .paper-rule-right-score {
                position: absolute;
                right: 30px;
                top: 30px;
                z-index: 2;

                display: flex;
                flex-direction: column;
                color: red;
                font-size: 26px;

                .iconfont {
                    font-size: 58px;
                    line-height: 30px;
                }
                .paper-rule-right-score-value {
                    text-align: center;
                }
            }

            .paper-rule-right-title {
                margin-top: 10px;

                .el-input__wrapper {
                    box-shadow: initial;

                    .el-input__inner {
                        text-align: center;
                        font-size: 18px;
                        font-weight: bold;
                    }
                }
            }

            .paper-rule-right-chapter {
                padding: 0px 20px;
                position: relative;

                &:hover {
                    .el-button {
                        display: inline-block;
                    }
                }

                .el-input__wrapper {
                    box-shadow: initial;

                    .el-input__inner {
                        font-size: 16px;
                        font-weight: bold;
                    }
                }

                .el-textarea__inner {
                    box-shadow: initial;
                }

                .el-button {
                    position: absolute;
                    bottom: 5px;
                    right: 100px;
                    display: none;
                }

                .el-form-item {// 校验通过时，恢复和章节描述的距离
                    margin-bottom: 0;
                    &.is-error {
                        margin-bottom: 18px;
                    }
                }
                .el-form-item__error {
                    margin-left: 12px;
                }
            }

            .paper-rule-right-rule {
                padding: 0px 30px;
                .el-form-item {
                    margin-right: 5px;
                    .el-input-number {
                        width: 40px;
                        .el-input-number__decrease, .el-input-number__increase {
                            display: none;
                        }
                        .el-input__wrapper {
                            padding: 0px;
                            box-shadow: initial;
                            border-bottom: 1px solid var(--el-border-color);
                            border-radius: 0;
                        }
                    }
                }
            }
        }
    }
}
</style>