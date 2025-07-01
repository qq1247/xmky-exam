<template>
    <el-scrollbar max-height="calc(100vh - 190px)" class="question-set">
        <xmks-edit-card title="试题" desc="试题">
            <template #card-main>
                <el-form ref="formRef" :model="form" :rules="formRules" label-width="100" size="large" class="form">
                    <!-- 试题类型，如果是修改，不能改变试题类型 -->
                    <el-form-item label="类型" prop="type">
                        <el-radio-group v-model="form.type">
                            <el-radio v-for="dict in dictStore.getList('QUESTION_TYPE')" :key="dict.dictKey" border
                                :value="parseInt(dict.dictKey)" :disabled="form.id ? true : false">
                                {{ dict.dictValue }}
                            </el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <!-- 题干 -->
                    <el-form-item label="题干" prop="title">
                        <el-input v-model="form.title" placeholder="请输入题干" :autosize="{ minRows: 3 }" type="textarea" />
                    </el-form-item>
                    <!-- 图片 -->
                    <el-form-item label="图片" prop="imgFileIds">
                        <div class="question__img-group">
                            <vue-draggable v-model="imgFileList">
                                <photo-provider :default-backdrop-opacity="0.6">
                                    <photo-consumer v-for="(file, index) in imgFileList" :key="index"
                                        :src="`${downloadUrl}?id=${file.uid}`">
                                        <div class="question_img-outer">
                                            <el-image :src="`${downloadUrl}?id=${file.uid}`" fit="contain" />
                                            <div class="question_img-inner">
                                                <span class="question_img-txt">图{{ toChinaNum(index + 1) }}</span>
                                                <span @click.stop="imgFileList.splice(index, 1)"
                                                    class="iconfont icon-shanchu question_img-btn"></span>
                                            </div>
                                        </div>
                                    </photo-consumer>
                                </photo-provider>
                            </vue-draggable>
                            <el-upload v-model:file-list="imgFileList" :action="uploadUrl"
                                :headers="{ Authorization: userStore.accessToken }" name="files" :show-file-list="false"
                                accept=".jpg,.png,.jpeg,JPG,JPEG" :limit="4" :before-upload="uploadBefore"
                                :multiple="true" :on-success="uploadSuccess" class="">
                                <span class="iconfont icon-tubiaoziti2-02"></span>
                            </el-upload>
                        </div>
                        <!-- <el-upload v-model:file-list="imgFileList" :action="uploadUrl"
                            :headers="{ Authorization: userStore.accessToken }" name="files" list-type="picture-card"
                            :show-file-list="false" accept=".jpg,.png,.jpeg,JPG,JPEG" :limit="4"
                            :before-upload="uploadBefore" :multiple="true" :on-success="uploadSuccess"
                            :on-preview="uploadPreview">
                            <span class="iconfont icon-tubiaoziti2-02"></span>
                        </el-upload> -->
                        <!-- <el-dialog v-model="dialogVisible">
                            <img w-full :src="dialogImageUrl" alt="Preview Image" />
                        </el-dialog> -->
                    </el-form-item>
                    <!-- 选项（单多选有效） -->
                    <template v-if="hasSingleChoice(form) || hasMultipleChoice(form)">
                        <el-form-item v-for="(option, index) in form.options" :key="index"
                            :value="`选项${toLetter(index)}`" :prop="`options[${index}]`" :rules="formRules.options">
                            <el-input v-model="form.options[index]" placeholder="请输入选项" />
                        </el-form-item>
                        <el-form-item>
                            <el-button :disabled="form.options.length >= 7" type="primary" size="small" plain
                                class="form__option-btn" @click="addOption">
                                <span class="iconfont icon-tubiaoziti2-02 form__option-btn-icon"
                                    style="font-size: 12px;"></span>
                                <span class="form__option-btn-txt">添加选项</span>
                            </el-button>
                            <el-button :disabled="form.options.length <= 2" type="danger" size="small" plain
                                class="form__option-btn" @click="delOption">
                                <span class="iconfont icon-tubiaoziti2-01 form__option-btn-icon"></span>
                                <span class="form__option-btn-txt">删除选项</span>
                            </el-button>
                        </el-form-item>
                    </template>
                    <!-- 阅卷选项（填空问答有效，用于是否智能阅卷） -->
                    <el-form-item v-if="hasFillBlank(form) || hasQA(form)" label="阅卷选项">
                        <div class="mark-option">
                            <div class="mark-option__tip">
                                <span class="iconfont icon-tubiaoziti-34 mark-option__tip-icon"></span>
                                <span class="mark-option__tip-txt">选择主观题，需要人工阅卷。</span>
                            </div>
                            <div class="mark-option__inner">
                                <el-form-item label="" prop="markType">
                                    <el-radio-group v-model="form.markType" class="mark-option__mark-type">
                                        <el-radio-button label="客观题" :value="1" />
                                        <el-radio-button label="主观题" :value="2" />
                                    </el-radio-group>
                                </el-form-item>

                                <el-checkbox-group v-model="form.markOptions" class="mark-option__mark-options">
                                    <el-tooltip v-if="hasFillBlank(form) && hasObjective(form)" content="默认答案有顺序">
                                        <el-checkbox :value="2" class="checkbox">答案无顺序</el-checkbox>
                                    </el-tooltip>
                                    <el-tooltip v-if="hasObjective(form)" content="默认字母大小写敏感">
                                        <el-checkbox :value="3" class="checkbox">不分大小写</el-checkbox>
                                    </el-tooltip>
                                </el-checkbox-group>
                            </div>
                        </div>
                    </el-form-item>
                    <!-- 分数（填空或客观问答，有子分数合计） -->
                    <el-row>
                        <el-col :span="12">
                            <el-form-item label="分值" prop="score">
                                <el-input-number v-model="form.score" :min="0.5" :step="0.5" controls-position="right"
                                    :precision="2"
                                    :disabled="hasFillBlank(form) || (hasQA(form) && hasObjective(form))" />
                            </el-form-item>
                        </el-col>
                        <el-col v-if="hasMultipleChoice(form)" :span="12">
                            <el-form-item label="漏选得分" prop="scores[0]">
                                <el-input-number v-model="form.scores[0]" :min="0.5" :max="10" :step="0.5"
                                    :precision="2" controls-position="right" />
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <!-- 答案 -->
                    <el-form-item v-if="hasSingleChoice(form)" label="答案" prop="answers">
                        <el-radio-group v-model="form.answers[0]">
                            <el-radio v-for="(option, index) in form.options" :key="index" :value="toLetter(index)">
                                {{ toLetter(index) }}
                            </el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item v-else-if="hasMultipleChoice(form)" label="答案" prop="answers">
                        <el-checkbox-group v-model="form.answers">
                            <el-checkbox v-for="(option, index) in form.options" :key="index" :value="toLetter(index)">
                                {{ toLetter(index) }}
                            </el-checkbox>
                        </el-checkbox-group>
                    </el-form-item>
                    <el-form-item v-else-if="hasFillBlank(form) || (hasQA(form) && hasObjective(form))" label="答案">
                        <div class="answer">
                            <div class="answer__tip">
                                <span class="iconfont icon-tubiaoziti-34 answer__tip-icon"></span>
                                <span class="answer__tip-txt">
                                    单个{{ form.type === 3 ? '填空' : '关键词' }}可有多个备选答案，用多个标签表示。如“老婆”的备选答案有“媳妇”和“妻子”，都表示正确答案。
                                </span>
                            </div>
                            <div v-for="(answer, index) in form.answers" :key="index" class="answer__inner">
                                <el-form-item :prop="`answers[${index}]`" :rules="formRules.answers"
                                    :label="hasFillBlank(form) ? `第${toChinaNum(index + 1)}空` : `关键词${toChinaNum(index + 1)}`"
                                    label-width="initial" class="answer__select">
                                    <el-select v-model="form.answers[index]" remote multiple clearable filterable
                                        allow-create default-first-option placeholder="请输入答案或同义词" />
                                </el-form-item>
                                <el-form-item :prop="`scores[${index}]`" :rules="formRules.scores" label="得分"
                                    label-width="50px" class="answer__number">
                                    <el-input-number v-model="form.scores[index]" :min="0.5" :max="10" :step="0.5"
                                        :precision="1" controls-position="right" />
                                </el-form-item>
                            </div>
                            <el-form-item>
                                <el-button v-if="hasQA(form) && hasObjective(form)"
                                    :disabled="form.answers.length >= 100" type="primary" @click="addKeyword"
                                    size="small" plain class="answer__btn">
                                    <span class="iconfont icon-tubiaoziti2-02 answer__btn-icon"></span>
                                    <span class="answer__btn-txt">添加关键词</span>
                                </el-button>
                                <el-button v-if="hasQA(form) && hasObjective(form)" :disabled="form.answers.length <= 1"
                                    type="danger" @click="delKeyword" size="small" plain class="answer__btn">
                                    <span class="iconfont icon-delete icon-tubiaoziti2-01 answer__btn-icon"></span>
                                    <span class="answer__btn-txt">删除关键词</span>
                                </el-button>
                            </el-form-item>
                        </div>
                    </el-form-item>
                    <el-form-item v-else-if="hasJudge(form)" label="答案" prop="answers">
                        <el-radio-group v-model="form.answers[0]">
                            <el-radio v-for="judge in ['对', '错']" :key="judge" :value="judge">{{ judge }}</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item v-else-if="hasQA(form) && hasSubjective(form)" label="答案" prop="answers">
                        <el-input type="textarea" v-model="form.answers[0]" placeholder="请输入答案"
                            :autosize="{ minRows: 3 }" :show-word-limit="true" :maxlength="10000"></el-input>
                    </el-form-item>
                    <!-- 解析 -->
                    <el-form-item label="解析" prop="analysis">
                        <el-input v-model="form.analysis" placeholder="请输入解析" :autosize="{ minRows: 3 }" type="textarea"
                            :show-word-limit="true" :maxlength="10000" />
                    </el-form-item>
                    <el-form-item>
                        <el-button v-if="$route.path.indexOf('add') !== -1" type="primary" class="form__btn"
                            @click="add">添加</el-button>
                        <el-button v-else type="primary" class="form__btn" @click="edit">修改</el-button>
                        <!-- <el-button type="primary" class="form__btn form__btn--secondary"
                            @click="$router.go(-1)">取消</el-button> -->
                    </el-form-item>
                </el-form>
            </template>
        </xmks-edit-card>
        <xmks-edit-card v-if="form.id" title="复制试题" desc="复制试题" class="form">
            <template #card-side>
                <el-button type="primary" class="form__btn" style="margin-bottom: 14px;" @click="copy">复制试题</el-button>
            </template>
        </xmks-edit-card>
        <xmks-edit-card v-if="form.id" title="移动试题" desc="移动试题" class="form">
            <template #card-main>
                <el-form ref="moveFormRef" :model="moveForm" :rules="moveFormRules" inline label-width="100"
                    size="large" class="form">
                    <el-form-item label="" prop="questionBankId" style="width: 100%;">
                        <xmks-select v-model="moveForm.questionBankId" url="questionBank/listpage" :params="{}"
                            search-parm-name="name" option-label="name" option-value="id" :multiple="false" clearable
                            :page-size="100" :disabled-values=[form.id] search-placeholder="请输入题库名称进行筛选"
                            placeholder="请输入题库名称进行筛选">
                            <template #default="{ option }">
                                {{ option.name }} {{ form.id == option.id ? '（不可选）' : '' }}
                            </template>
                        </xmks-select>
                    </el-form-item>
                </el-form>
            </template>
            <template #card-side>
                <el-button type="primary" class="form__btn" @click="move" style="margin-bottom: 40px;">移动试题</el-button>
            </template>
        </xmks-edit-card>
        <xmks-edit-card v-if="form.id" title="删除试题" desc="删除试题" class="form">
            <template #card-side>
                <el-button type="primary" class="form__btn" :class="{ 'form__btn--warn': delConfirm }" @click="de1"
                    style="margin-bottom: 14px;">删除试题</el-button>
            </template>
        </xmks-edit-card>
    </el-scrollbar>
</template>

<script lang="ts" setup>
import { questionAdd, questionCopy, questionDel, questionEdit, questionGet, questionMove } from '@/api/exam/question'
import XmksEditCard from '@/components/card/xmks-card-edit.vue'
import http from '@/request'
import { useDictStore } from '@/stores/dict'
import { useUserStore } from '@/stores/user'
import type { Question } from '@/ts/exam/question'
import { escape2Html } from '@/util/htmlUtil'
import { toChinaNum, toLetter } from '@/util/numberUtil'
import { hasFillBlank, hasJudge, hasMultipleChoice, hasObjective, hasQA, hasSingleChoice, hasSubjective } from '@/util/questionUtil'
import { Decimal } from 'decimal.js-light'
import { ElMessage, type FormInstance, type FormRules, type UploadFile, type UploadFiles, type UploadRawFile, type UploadUserFile } from 'element-plus'
import { nextTick, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { VueDraggable } from 'vue-draggable-plus'
import XmksSelect from '@/components/xmks-select.vue'

/************************变量定义相关***********************/
const route = useRoute()// 路由
const router = useRouter()// 路由
const userStore = useUserStore()// 用户缓存
const dictStore = useDictStore()// 字典缓存
const formRef = ref<FormInstance>()// 表单引用
const form = reactive<Question>({// 表单
    id: null,
    type: 1,
    title: '',
    options: [],
    markType: 1,
    markOptions: [],
    score: 1,
    answers: [],
    scores: [],
    analysis: '',
    questionBankId: null,
    imgFileIds: [],
})
const formRules = reactive<FormRules>({// 表单规则
    title: [{
        trigger: 'blur',
        validator: (rule: any, value: any, callback: any) => {
            if (!value) {
                return callback(new Error('请输入题干'))
            }
            if (hasFillBlank(form) && !/[_]{5,}/.test(value)) {
                return callback(new Error('最少一个填空（连续五个或五个以上下划线（_____）表示一个填空）'))
            }
            return callback()
        }
    }],
    options: [{
        trigger: 'blur',
        validator: (rule: any, value: any, callback: any) => {
            if (!value) {
                return callback(new Error('请输入选项'))
            }
            return callback()
        }
    }],
    score: [{
        trigger: 'blur',
        validator: (rule: any, value: any, callback: any) => {
            if (!value) {
                return callback(new Error('请输入分值'))
            }
            if (!/^(([1-9]{1}\d*)|(0{1}))(\.\d{1,2})?$/.test(value)) {
                return callback(new Error('正数，最多两位小数'))
            }
            if (form.score > 20) {
                return callback(new Error('最大20分'))
            }
            return callback()
        }
    }],
    answers: [{
        trigger: 'blur',
        validator: (rule: any, value: Array<string>, callback: any) => {
            if (!value) {
                return callback(new Error('请输入答案'))
            }
            //   if (!value instanceof Array) {
            //     return callback(new Error('答案格式错误'))
            //   }
            if (value.length === 0) {
                return callback(new Error('请输入答案'))
            }
            if (hasMultipleChoice(form) && value.length < 2) {// 多选最少两个答案
                return callback(new Error('最少两个答案'))
            }

            value.forEach(cur => {// 主观问答，值为空字符串会通过校验，特殊处理一下
                if (cur.length === 0) {
                    return callback(new Error('请输入答案'))
                }
            })
            return callback()
        }
    }],
    scores: [{
        trigger: 'blur',
        validator: (rule: any, value: any, callback: any) => {
            if (!value) {
                return callback(new Error('请输入分值'))
            }
            if (!/^(([1-9]{1}\d*)|(0{1}))(\.\d{1,2})?$/.test(value)) {
                return callback(new Error('正数，最多两位小数'))
            }
            if (hasMultipleChoice(form) && value >= form.score) {// 填空漏选分数校验
                return callback(new Error(`应小于${form.score}分`))
            }
            return callback()
        }
    }],
})
const delConfirm = ref(false) // 删除确认

const uploadUrl = `${http.defaults.baseURL}file/upload`// 上传地址
const downloadUrl = `${http.defaults.baseURL}file/download`// 下载地址
const imgFileList = ref<UploadUserFile[]>([])

const dialogImageUrl = ref('')
const dialogVisible = ref(false)

const moveFormRef = ref<FormInstance>()// 表单引用
const moveForm = reactive({// 表单
    id: null,// ID
    questionBankId: [],// 题库ID
})
const moveFormRules = reactive<FormRules>({// 表单校验规则
    questionBankId: [
        { required: true, message: '请选择题库', trigger: 'blur' },
    ],
})

/************************组件生命周期相关*********************/
onMounted(async () => {
    if (route.path.indexOf('add') !== -1) {// 添加
        if (hasSingleChoice(form)) {
            form.title = `这是一道单选题的题干，支持代码高亮，格式和markdown保持一致，如插入片段\`\`\`js\nalert('hello world'); \n\`\`\``
            addOption()
            addOption()
        } else if (hasMultipleChoice(form)) {
            form.title = '这是一道多选题的题干'
            form.scores.push(0.5)
            addOption()
            addOption()
        } else if (hasFillBlank(form)) {
            form.title = '这是一道填空题的题干，连续五个或五个以上下划线（_____）表示一个填空'
        } else if (hasJudge(form)) {
            form.title = '这是一道判断题的题干'
        } else if (hasQA(form)) {
            form.markType = 2 // 问答默认为主观题
            form.title = '这是一道问答题的题干'
        }
        form.questionBankId = parseInt(route.params.questionBankId as string)
    } else {// 修改
        const { data: { data } } = await questionGet({ id: route.params.id })
        form.id = data.id
        form.type = data.type
        await nextTick()
        form.title = escape2Html(data.title)
        await nextTick()
        form.markType = data.markType
        await nextTick()
        form.score = data.score
        await nextTick()
        form.scores = data.scores
        await nextTick()
        form.markOptions = data.markOptions
        form.answers = data.answers.map((an: string | string[]) => escape2Html(an))
        form.options = data.options.map((op: string) => escape2Html(op))
        form.analysis = escape2Html(data.analysis)
        form.questionBankId = data.questionBankId
        form.imgFileIds = data.imgFileIds

        form.imgFileIds?.forEach(fileId => {
            imgFileList.value.push({
                uid: fileId,
                url: `${downloadUrl}?id=${fileId}`,
                name: `${fileId}`
            })
        })

        moveForm.id = data.id
    }
})

/************************监听相关*****************************/
/**
 * 类型修改
 * 初始化数据，清除校验（显示了多余的校验文字）
 */
watch(() => form.type, (type) => {
    formRef.value?.clearValidate(); // 第一次还没有生成clearValidate方法，用?处理一下
    form.title = ''
    form.options.splice(0)
    form.markType = 1
    form.markOptions.splice(0)
    form.score = 1
    form.scores.splice(0)
    form.answers.splice(0)
    form.analysis = ''
    if (type === 1) {
        form.title = `这是一道单选题的题干，支持代码高亮，格式和markdown保持一致，如题干插入片段\`\`\`js\nalert('hello world'); \n\`\`\``
        addOption()
        addOption()
    } else if (type === 2) {
        form.title = '这是一道多选题的题干'
        form.scores.push(0.5)
        addOption()
        addOption()
    } else if (type === 3) {
        form.title = '这是一道填空题的题干，连续五个或五个以上下划线（_____）表示一个填空'
    } else if (type === 4) {
        form.title = '这是一道判断题的题干'
    } else if (type === 5) {
        form.markType = 2 // 问答默认为主观题
        form.title = '这是一道问答题的题干'
    }
})

/**
 * 题干修改（填空有效）
 * 解析下划线数量，转成填空答案
 */
watch(() => form.title, (title) => {
    if (!hasFillBlank(form)) {
        return;
    }

    let fillblanksNum = title.match(/[_]{5,}/g)?.length//获取填空数量
    if (!fillblanksNum) {
        fillblanksNum = 0
    } else if (fillblanksNum > 7) {// 最多7个填空
        fillblanksNum = 7
    }

    while (fillblanksNum > form.answers.length) {// 填空和答案对齐（多退少补）
        form.answers.push('')
        form.scores.push(1)
    }
    while (fillblanksNum < form.answers.length) {
        form.answers.pop()
        form.scores.pop()
    }
})

/**
 * 阅卷类型修改（填空问答有效）
 * 
 * 如果是主观题，清除阅卷选项
 * 如果是客观问答题，添加一个关键词、分数
 */
watch(() => form.markType, (markType) => {
    formRef.value?.clearValidate(); // 切换阅卷类型时，清空校验。如主观问答没填答案，切换到客观问答时，保留了校验文字

    if (markType === 2) {
        form.markOptions.splice(0)
    }

    if (hasQA(form)) {
        form.answers.splice(0)
        form.scores.splice(0)

        form.answers.push('')
        if (markType === 1) {
            form.scores.push(1)
        }
    }
})

/**
 * 分数修改（多选有效）
 * 如果是多选题，默认漏选分值为分数一半
 */
watch(() => form.score, (score) => {
    if (!hasMultipleChoice(form)) {
        return
    }

    form.scores.splice(0, 1, parseFloat(new Decimal(score).div(2).toFixed(2)))
})

/**
 * 答案分数修改（填空或客观问答有效）
 * 分数为各子项的分数总和
 */
watch(() => form.scores, (scores) => {
    if (hasFillBlank(form) || (hasQA(form) && hasObjective(form))) {
        form.score = scores.reduce((total: number, cur) => { return new Decimal(total).add(cur as number).toNumber() }, 0)
    }
}, {
    deep: true,
})

/************************事件相关*****************************/
// 添加
async function add() {
    // 数据校验
    try {
        await formRef.value?.validate()
    } catch (e) {
        return
    }

    // 添加
    const params = JSON.parse(JSON.stringify(form)) // 深度拷贝，不要改变子属性
    if (params.type === 3 || (params.type === 5 && params.markType === 1)) {// 如果是填空或主观问答
        params.answers.map((answer: string[], index: number, self: string[]) => {// 答案处理成接口格式
            return self[index] = answer.join('\n')
        })
    }
    params.imgFileIds = imgFileList.value.map((file: UploadUserFile) => file.uid)
    const { data: { code } } = await questionAdd({ ...params })
    if (code !== 200) {
        return
    }

    router.push(`/question-bank/question-nav/list/${route.params.questionBankId}`)
}

// 修改
async function edit() {
    // 数据校验
    try {
        await formRef.value?.validate()
    } catch (e) {
        return
    }

    // 修改
    const params = JSON.parse(JSON.stringify(form)) // 深度拷贝，不要改变子属性
    if (params.type === 3 || (params.type === 5 && params.markType === 1)) {// 如果是填空或主观问答
        params.answers.map((answer: string[], index: number, self: string[]) => {// 答案处理成接口格式
            return self[index] = answer.join('\n')
        })
    }
    params.imgFileIds = imgFileList.value.map((file: UploadUserFile) => file.uid)
    const { data: { code } } = await questionEdit({ ...params })
    if (code !== 200) {
        return
    }

    router.push(`/question-bank/question-nav/list/${form.questionBankId}`)
}

// 删除
async function de1() {
    if (!delConfirm.value) {
        delConfirm.value = true
        return
    }

    const { data: { code } } = await questionDel({ ids: form.id })
    if (code !== 200) {
        return
    }

    router.push(`/question-bank/question-nav/list/${form.questionBankId}`)
}

// 复制
async function copy() {
    const { data: { code } } = await questionCopy({ id: form.id })
    if (code !== 200) {
        return
    }

    router.push(`/question-bank/question-nav/list/${form.questionBankId}`)
}

// 添加选项
function addOption() {
    form.options.push('')
}

// 删除选项
function delOption() {
    form.options.pop()
}

// 添加关键词
function addKeyword() {
    form.answers.push('')
    form.scores.push(1)
}

// 删除关键词
function delKeyword() {
    form.answers.pop()
    form.scores.pop()
}


// 移动
async function move() {
    // 数据校验
    try {
        await moveFormRef.value?.validate()
    } catch (e) {
        return
    }

    // 修改
    const { data: { code } } = await questionMove({ ids: moveForm.id, questionBankId: moveForm.questionBankId })
    if (code !== 200) {
        return
    }

    router.push(`/question-bank/question-nav/list/${form.questionBankId}`)
}

// 上传之前处理
function uploadBefore(rawFile: UploadRawFile) {
    if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
        ElMessage.error('只允许 jpg和png 格式')
        return false
    }
    if (rawFile.size / 1024 > 2048) {
        ElMessage.error('图片最大支持2兆')
        return false
    }

    return true
}

// 上传成功处理
function uploadSuccess(response: any, uploadFile: UploadFile, uploadFiles: UploadFiles) {
    if (response.code === 200) {
        uploadFile.uid = response.data.fileIds
        uploadFile.url = `${downloadUrl}?id=${response.data.fileIds}`
    } else {
        uploadFiles.splice(uploadFiles.indexOf(uploadFile), 1)
    }
}
// 上传预览
function uploadPreview(uploadFile: UploadFile) {
    dialogImageUrl.value = uploadFile.url!
    dialogVisible.value = true
}

</script>

<style lang="scss" scoped>
.question-set {
    display: flex;
    flex-direction: column;
    width: 1200px;
    background-color: #FFFFFF;
    margin: 20px 0px;
    padding: 40px 30px 0px 30px;
    border-radius: 15px 15px 15px 15px;

    .form {
        margin-top: 20px;

        .form__btn {
            height: 38px;
            padding: 0px 30px;
            border-radius: 6px;
            border: 0px;
            color: #FFFFFF;
            font-size: 14px;
            background-image: linear-gradient(to right, #04C7F2, #259FF8);

            &.form__btn--secondary {
                color: #04C7F2;
                border: 1px solid #04C7F2;
                background-image: linear-gradient(to right, #FFFFFF, #FFFFFF);
            }

            &.form__btn--warn {
                color: #FF5D15;
                border: 1px solid #FF5D15;
                background-image: linear-gradient(to right, #FFFFFF, #FFFFFF);
            }
        }

        .form__option-btn {
            border: 0px;
            width: 100px;
            height: 30px;
            border-radius: 6px 6px 6px 6px;

            .form__option-btn-icon {
                font-size: 12px;
            }

            .form__option-btn-txt {
                margin-left: 4px;
                font-size: 12px;
            }
        }

        :deep(.question__img-group) {
            display: flex;

            .el-upload {
                width: 148px;
                height: 148px;
                border: 1px dashed var(--el-border-color);
                border-radius: 6px;
                cursor: pointer;
                position: relative;
                overflow: hidden;
                transition: var(--el-transition-duration-fast);
            }

            .el-upload:hover {
                border-color: #3AA8EF;
                background-color: #FAFAFA;
            }

            .iconfont {
                font-size: 20px;
                color: #8c939d;
            }

            .PhotoSlider__Backdrop {
                opacity: 0.6;
            }

            .el-image__inner {
                background-color: #fff;
                border: 1px solid #dcdfe6;
                border-radius: 6px;
                height: 148px;
                width: 148px;
                margin: 0 8px 8px 0;
                overflow: hidden;
                padding: 0px;
            }

            .question_img-outer {
                display: flex;
                flex-direction: column;
                align-items: center;

                .el-image {
                    cursor: move;
                }

                .question_img-inner {
                    line-height: 0px;

                    .question_img-txt {
                        line-height: 14px;
                        font-size: 14px;
                        color: #000000;
                    }

                    .question_img-btn {
                        cursor: pointer;
                        margin-left: 5px;
                        font-size: 16px;
                    }
                }
            }
        }


        :deep(.mark-option) {
            display: flex;
            flex-direction: column;
            width: 100%;
            border: 1px solid #e4e7ed;
            padding: 20px;

            .mark-option__tip {
                display: flex;
                align-items: center;
                padding: 0px 10px;
                height: 30px;
                background: #F4F4F4;
                border-radius: 4px 4px 4px 4px;

                .mark-option__tip-icon {
                    font-size: 14px;
                    color: #1AC693;
                    margin-right: 5px;
                }

                .mark-option__tip-txt {
                    font-size: 12px;
                    color: #1AC693;
                }
            }

            .mark-option__inner {
                display: flex;
                margin-top: 10px;

                .mark-option__mark-type {
                    .el-radio-button {

                        &.is-active {
                            .el-radio-button__inner {
                                background-color: #D4EEFE;
                                border: 1px solid #D4EEFE;
                            }
                        }

                        .el-radio-button__inner {
                            padding: 8px 10px;
                            font-size: 14px;
                            color: #1EA1EE;
                            border: 1px solid #D4EEFE;
                        }
                    }
                }

                .mark-option__mark-options {
                    margin-left: 30px;
                }
            }
        }

        .answer {
            display: flex;
            flex-direction: column;
            width: 100%;
            border: 1px solid #e4e7ed;
            padding: 20px;

            .answer__tip {
                display: flex;
                align-items: center;
                padding: 0px 10px;
                height: 30px;
                background: #F4F4F4;
                border-radius: 4px 4px 4px 4px;

                .answer__tip-icon {
                    font-size: 14px;
                    color: #1AC693;
                    margin-right: 5px;
                }

                .answer__tip-txt {
                    font-size: 12px;
                    color: #1AC693;
                }
            }

            .answer__inner {
                display: flex;
                margin-bottom: 22px;

                .answer__select {
                    flex: 1;
                }

                .answer__number {
                    width: 180px;
                }
            }

            .answer__btn {
                border: 0px;
                width: 100px;
                height: 30px;
                border-radius: 6px 6px 6px 6px;

                .answer__btn-icon {
                    font-size: 12px;
                }

                .answer__btn-txt {
                    margin-left: 4px;
                    font-size: 12px;
                }
            }
        }
    }

}
</style>
