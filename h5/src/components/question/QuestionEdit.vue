<template>
    <el-form ref="formRef" :model="form" :rules="formRules" label-width="80" >
        <!-- 试题类型，如果是修改，不能改变试题类型 -->
        <el-form-item label="类型" prop="type">
            <el-radio-group v-model="form.type">
                <el-radio-button 
                    v-for="dict in dictStore.getList('QUESTION_TYPE')"
                    :key="dict.dictKey"
                    :label="Number(dict.dictKey)"
                    :disabled="(form.id && form.type !== Number(dict.dictKey)) ? true : false"
                >
                {{ dict.dictValue }}
                </el-radio-button>
            </el-radio-group>
        </el-form-item>
        <!-- 题干 -->
        <el-form-item label="题干" prop="title">
            <el-input v-model="form.title" placeholder="请输入题干" :autosize="{ minRows: 2 }" type="textarea"/>
        </el-form-item>
        <!-- 选项（单多选有效） -->
        <template v-if="[1, 2].includes(form.type)">
            <el-form-item 
                v-for="(option, index) in form.options"
                :key="index"
                :label="`选项${optionLabs[index]}`"
                :prop="`options[${index}]`"
                :rules="formRules.options"
                >
                <el-input v-model="form.options[index]" placeholder="请输入选项"/>
            </el-form-item>
            <el-form-item>
                <el-button :disabled="form.options.length >= 7" type="primary" @click="addOption" size="small" plain style="border: none; padding: 14px;">
                    <span class="iconfont icon-plus" style="font-size: 12px;">&nbsp;添加选项</span>
                </el-button>
                <el-button :disabled="form.options.length <= 2" type="danger" @click="delOption" size="small" plain style="border: none; padding: 14px;">
                    <span class="iconfont icon-delete" style="font-size: 12px;">&nbsp;删除选项</span>
                </el-button>
            </el-form-item>
        </template>
        <!-- 阅卷类型（填空问答有效，用于是否智能阅卷） -->
        <el-row v-if="[3, 5].includes(form.type)">
            <el-col :span="12">
                <el-form-item label="" prop="markType">
                    <el-switch v-model="form.markType" :active-value="1" active-text="客观题" :inactive-value="2" inactive-text="主观题" />
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <!-- 阅卷选项 -->
                <el-checkbox-group v-model="form.markOptions" style="width:300px">
                    <el-tooltip v-if="form.markType === 1 && form.type === 3" content="默认答案有顺序">
                        <el-checkbox :label="2" class="checkbox">答案无顺序</el-checkbox>
                    </el-tooltip>
                    <el-tooltip v-if="form.markType === 1" content="默认字母大小写敏感">
                        <el-checkbox :label="3" class="checkbox">不分大小写</el-checkbox>
                    </el-tooltip>
                </el-checkbox-group>
                <el-alert v-if="form.markType === 2" :closable="false" title="需人工阅卷" type="success" />
            </el-col>
        </el-row>
        <!-- 分数 -->
        <el-row>
            <el-col :span="12">
                <el-form-item label="分值" prop="score">
                    <el-input-number v-model="form.score" :min="0.5" :step="0.5" controls-position="right"
                    :precision="2" :disabled="form.type === 3 || (form.type === 5 && form.markType === 1)" />
                </el-form-item>
            </el-col>
            <el-col v-if="form.type === 2" :span="12">
                <el-form-item label="漏选得分" prop="scores[0]">
                    <el-input-number v-model="form.scores[0]" :min="0.5" :max="10" :step="0.5" :precision="2" controls-position="right"/>
                </el-form-item>
            </el-col>
        </el-row>
        <!-- 答案 -->
        <el-form-item v-if="form.type === 1" label="答案" prop="answers">
            <el-radio-group v-model="form.answers[0]">
                <el-radio 
                    v-for="(option, index) in form.options" 
                    :key="index"
                    :label="`${optionLabs[index]}`">
                    {{optionLabs[index]}}
                </el-radio>
            </el-radio-group>
        </el-form-item>
        <el-form-item v-else-if="form.type === 2" label="答案" prop="answers">
            <el-checkbox-group v-model="form.answers">
                <el-checkbox v-for="(option, index) in form.options" :key="index" :label="`${optionLabs[index]}`">
                    {{optionLabs[index]}}
                </el-checkbox>
            </el-checkbox-group>
        </el-form-item>
        <el-form-item v-else-if="form.type === 3 || (form.type === 5 && form.markType === 1)" label="答案">
            <el-card shadow="never">
                <el-alert :closable="false" title="单个填空有多个备选答案，用多个标签表示。如：老婆、媳妇" type="success" />
                <el-row v-for="(answer, index) in form.answers" :key="index" :gutter="10">
                    <el-col :span="16">
                        <el-form-item :prop="`answers[${index}]`" :rules="formRules.answers">
                            <span>{{form.type === 3 ? '第' + toChinaNum(index + 1) + '空' : '关键词'}}&nbsp;</span>
                            <el-select v-model="form.answers[index]" remote multiple clearable filterable allow-create default-first-option placeholder="请输入答案" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item :prop="`scores[${index}]`">
                            <span>得分&nbsp;</span>
                            <el-input-number v-model="form.scores[index]" :min="0.5" :max="10" :step="0.5" :precision="1" controls-position="right" style="width: 90px;"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-button v-if="form.type === 5 && form.markType === 1" :disabled="form.answers.length >= 100" type="primary" @click="addKeyword" size="small" plain style="border: none; padding: 14px;">
                    <span class="iconfont icon-plus" style="font-size: 12px;">&nbsp;添加关键词</span>
                </el-button>
                <el-button v-if="form.type === 5 && form.markType === 1" :disabled="form.answers.length <= 1" type="danger" @click="delKeyword" size="small" plain style="border: none; padding: 14px;">
                    <span class="iconfont icon-delete" style="font-size: 12px;">&nbsp;删除关键词</span>
                </el-button>
            </el-card>
        </el-form-item>
        <el-form-item v-else-if="form.type === 4" label="答案" prop="answers">
            <el-radio-group v-model="form.answers[0]">
                <el-radio v-for="judge in judgeLabs" :key="judge" :label="judge">{{ judge }}</el-radio>
            </el-radio-group>
        </el-form-item>
        <el-form-item v-else-if="form.type === 5 && form.markType === 2" label="答案" prop="answers">
            <el-input type="textarea" v-model="form.answers[0]"></el-input>
        </el-form-item>
        <!-- 解析 -->
        <el-form-item label="解析" prop="analysis">
            <el-input v-model="form.analysis" placeholder="请输入题干" :autosize="{ minRows: 2 }" type="textarea"/>
        </el-form-item>
        <el-form-item>
            <el-button v-if="!form.id" type="primary" @click="add()">添加</el-button>
            <el-button v-if="form.id" type="primary" @click="edit()">修改</el-button>
        </el-form-item>
    </el-form>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, watch, nextTick, watchEffect } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import http from '@/request'
import { useDictStore } from '@/stores/dict';
import Decimal from 'decimal.js'

// 定义变量
const props = defineProps({
  id: { type: [Number], },// 编号
})
const emit = defineEmits(['add', 'edit'])
const dictStore = useDictStore()// 字典缓存
const formRef = ref<FormInstance>()// 表单引用
const form = reactive({// 表单
    id: 0, // 编号
    type: 0, // 类型
    title: '',// 标题
    options: [] as String[],// 选项
    markType: 1, // 阅卷类型
    markOptions: [] as Number[],// 阅卷选项
    score: 0,// 分数
    answers: [] as String[],// 答案
    scores: [] as Number[],// 答案分数（单选判断主观问答，只有第0项有效）
    analysis: '',// 解析
})
const formRules = reactive<FormRules>({// 表单校验规则
    title: [{
        trigger: 'blur',
        validator: (rule: any, value: any, callback: any) => {
            if (!value) {
                return callback(new Error('请输入题干'))
              }
              if (form.type === 3 && !/[_]{5,}/.test(value)) {
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
        validator: (rule: any, value: Array<String>, callback: any) => {
            if (!value) {
                return callback(new Error('请输入答案'))
            }
            //   if (!value instanceof Array) {
            //     return callback(new Error('答案格式错误'))
            //   }
            if (value.length === 0) {
                return callback(new Error('请输入答案'))
            }
            if (form.type === 2 && value.length < 2) {// 多选最少两个答案
                return callback(new Error('最少两个答案'))
            }

            value.forEach(cur => {// 主观问答，值为空字符串会通过校验，特殊处理一下
                if (cur.length === 0) {
                    return callback(new Error('请输入答案')) 
                }
            });
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
            if (form.type === 2 && value >= form.score) {// 填空漏选分数校验
                return callback(new Error(`应小于${form.score}分`))
            }
            return callback()
        }
    }],
})

const optionLabs = ref(['A','B','C','D','E','F','G']) // 选项
const judgeLabs = ref(['对','错']) // 判断

// 组件挂载完成后，执行如下方法
onMounted(async () => {
    if (props.id) {
        form.id = props.id
    } else {
        form.type = 1
    }
})

/**
 * 属性ID修改
 */
watch(() => props.id, (n, o) => {
    formRef.value?.resetFields()

    if (props.id) {
        form.id = props.id
    } else {
        form.id = 0
        form.type = 1
    }
})

/**
 * 类型修改
 * 初始化数据，清除校验（显示了多余的校验文字）
 */
watch(() => form.type, (n, o) => {
    formRef.value?.clearValidate(); // 第一次还没有生成clearValidate方法，用?处理一下
    form.title = ''
    form.options.splice(0)
    form.markType = 1
    form.markOptions.splice(0)
    form.score = 1
    form.scores.splice(0)
    form.answers.splice(0)
    form.analysis = ''
    if (n === 1) {
        form.title = '这是一道单选题的题干'
        addOption() 
        addOption()
    } else if (n === 2) {
        form.title = '这是一道多选题的题干'
        form.scores.push(0.5)
        addOption()
        addOption()
    } else if (n === 3) {
        form.title = '这是一道填空题的题干，连续五个或五个以上下划线（_____）表示一个填空'
    } else if (n === 4) {
        form.title = '这是一道判断题的题干'
    } else if (n === 5) {
        form.markType = 2 // 问答默认为主观题
        form.title = '这是一道问答题的题干'
    }
})

/**
 * 题干修改（填空有效）
 * 解析下划线数量，转成填空答案
 */
 watch(() => form.title, (n, o) => {
    if (form.type !== 3) {
          return;
        }
        
        let fillblanksNum = n.match(/[_]{5,}/g)?.length//获取填空数量
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
watch(() => form.markType, (n, o) => {
    formRef.value?.clearValidate(); // 切换阅卷类型时，清空校验。如主观问答没填答案，切换到客观问答时，保留了校验文字

    if (n === 2) { 
        form.markOptions.splice(0)
    }

    if (form.type === 5) {
        form.answers.splice(0)
        form.scores.splice(0)

        form.answers.push('')
        if (n == 1) {
            form.scores.push(1)
        }
    }
})

/**
 * 分数修改（多选有效）
 * 如果是多选题，默认漏选分值为分数一半
 */
watch(() => form.score, (n, o) => {
    if (form.type === 2) {
        form.scores.splice(0, 1, n / 2)
    }
})

/**
 * 答案分数修改（填空或客观问答有效）
 * 分数为各子项的分数总和
 */
watch(() => form.scores, (n, o) => {
    if (form.type === 3 || (form.type === 5 && form.markType === 1)) {
        form.score = form.scores.reduce((total: number, cur) => { return new Decimal(total).add(cur as number).toNumber() }, 0)
    }
}, {
    deep: true,
})

/**
 * id修改
 * 查询并回显数据
 */
watch(() => form.id, async (id) => {
    if (!id) {
        return
    }
    let { data: { code, data } } = await http.post("question/get", { id })
    if (code !== 200) {
        return
    }

    form.type = data.type
    await nextTick()
    form.title = escape2Html(data.title) as string
    await nextTick()
    form.markType = data.markType
    await nextTick()
    form.score = data.score
    await nextTick()
    form.scores = data.scores
    await nextTick()
    form.markOptions = data.markOptions
    form.answers = data.answers.map((an: string|string[]) => escape2Html(an))
    form.options = data.options.map((op: string) => escape2Html(op))
    form.analysis = escape2Html(data.analysis) as string
})

function escape2Html (txt: string|string[]) {
    if (typeof txt === 'string') {
        return txt.replaceAll('&lt;', '<')
            .replaceAll('&gt;', '>')
            .replaceAll('&amp;', '&')
            .replaceAll('&quot;', '"')
            .replaceAll('&apos;', "'")
            .replaceAll('&ldquo;', "“")
            .replaceAll('&rdquo;', "”")
    }

    return (txt as string[]).map((t: string) => {
        return t.replaceAll('&lt;', '<')
            .replaceAll('&gt;', '>')
            .replaceAll('&amp;', '&')
            .replaceAll('&quot;', '"')
            .replaceAll('&apos;', "'")
            .replaceAll('&ldquo;', "“")
            .replaceAll('&rdquo;', "”")
    })

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

// 添加
async function add() {
    if (!formRef.value) return
    await formRef.value.validate((valid, fields) => {
        if (!valid) {
            return
        }

        let params = JSON.parse(JSON.stringify(form)) // 深度拷贝，不要改变子属性
        if (params.type === 3 || (params.type === 5 && params.markType === 1)) {// 如果是填空或主观问答
            params.answers.map((answer: string[], index: number, self: string[]) => {// 答案处理成接口格式
                return self[index] = answer.join('\n')
            })
        }

        emit('add', params)
    })
}

// 修改
async function edit() {
    if (!formRef.value) return
    await formRef.value.validate((valid, fields) => {
        if (!valid) {
            return
        }

        let params = JSON.parse(JSON.stringify(form)) // 深度拷贝，不要改变子属性
        if (params.type === 3 || (params.type === 5 && params.markType === 1)) {// 如果是填空或主观问答
            params.answers.map((answer: string[], index: number, self: string[]) => {// 答案处理成接口格式
                return self[index] = answer.join('\n')
            })
        }

        emit('edit', params)
    })
}

/**
 * 数字转汉字
 * @param num 
 */
function toChinaNum(num: number) {
    // 将接收到的num转换为字符串
    var strNum = String(num)
    // 定义单位
    // var unit = ['拾', '佰', '仟', '万', '拾', '佰', '仟', '亿', '拾', '佰', '仟']
    var unit = ['十', '百', '千', '万', '十', '百', '千', '亿', '十', '百', '千']
    // 结果中放一个符号，用来解决最后的零去不掉的问题
    var result = ['@']
    // 单位下标
    var unitNo = 0
    // 从后往前遍历接收到的数据，省略结束条件
    for (let i = strNum.length - 1; ; i--) {
        // 调用转大写函数，将每一个数字转换成中文大写，一次放入一个到结果数组中
        result.unshift(numToChinese(strNum[i]))
        // 如果不大于0
        if (i <= 0) {
            // 结束循环
            break
        }
        // 放入一个数字，放入一个单位
        result.unshift(unit[unitNo])
        // 单位下标加1
        unitNo++
    }
    // 将结果数组转换成字符串，并使用正则替换一些关键位置，让结果符合语法
    // return result.join('').replace(/(零[仟佰拾]){1,3}/g, '零').replace(/零{2,}/g, '零').replace(/零([万亿])/g, '$1').replace(/亿万/g, '亿').replace(/零*@/g, '')
    return result.join('').replace(/(零[千百十]){1,3}/g, '零').replace(/零{2,}/g, '零').replace(/零([万亿])/g, '$1').replace(/亿万/g, '亿').replace(/零*@/g, '')

    function numToChinese(n: any) {
        // var chineseBigNum = '零壹贰叁肆伍陆柒捌玖'
        var chineseBigNum = '零一二三四五六七八九'
        return chineseBigNum[n]
    }
}
</script>

<style lang="scss" scoped>
.checkbox {
    margin: 0px 10px;
}
.btn-add {
    background-color: #e5f4fd;
    border: none;
    padding: 14px;
}

.el-card {
    width: 100%;
    :deep(.el-card__body) {
        padding: 5px;
        .el-alert {
            padding: 0px;
            margin-bottom: 5px;
        }
        .el-form-item {
            margin-bottom: 20px;
        }

        .el-input-group__append {
            padding: 0 10px;
        }
    }
}
</style>