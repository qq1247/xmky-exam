<template>
    <div v-if="!txtForm.show" class="question">
        <el-form :inline="true" :model="queryForm" class="question-query">
            <el-form-item label="" style="width: 150px;">
                <el-input v-model="queryForm.id" placeholder="请输入编号" />
            </el-form-item>
            <el-form-item label="" style="width: 150px;">
                <el-input v-model="queryForm.title" placeholder="请输入题干" />
            </el-form-item>
            <el-form-item label="" style="width: 150px;">
                <el-select v-model="queryForm.type" clearable placeholder="请选择类型">
                    <el-option
                        v-for="dict in dictStore.getList('QUESTION_TYPE')"
                        :key="dict.dictKey"
                        :label="dict.dictValue"
                        :value="dict.dictKey"
                    />
                </el-select>
            </el-form-item>
            <el-form-item label="" style="width: 150px;">
                <el-select v-model="queryForm.markType" clearable placeholder="请选择阅卷类型">
                    <el-option
                        v-for="dict in dictStore.getList('PAPER_MARK_TYPE')"
                        :key="dict.dictKey"
                        :label="dict.dictValue"
                        :value="dict.dictKey"
                    />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="query">
                    <span class="iconfont icon-search" style="font-size:14px;">&nbsp;查询</span>
                </el-button>
            </el-form-item>
            <div style="float: right">
                <el-form-item>
                    <el-button type="success" @click="editForm.show = true">
                        <span class="iconfont icon-plus" style="font-size:14px;">&nbsp;添加</span>
                    </el-button>
                    <el-button type="success" plain @click="txtForm.show = true">
                        <span class="iconfont icon-arrow-right" style="font-size:14px;">&nbsp;文本导入</span>
                    </el-button>
                </el-form-item>
                <el-form-item style="margin-right: 0">
                    <el-radio-group v-model="display">
                        <el-radio-button label="paper">
                            <span class="iconfont icon-menu-s"></span>
                        </el-radio-button>
                        <el-radio-button label="list">
                            <span class="iconfont icon-list-row"></span>
                        </el-radio-button>
                    </el-radio-group>
                </el-form-item>
            </div>
        </el-form>
        <el-empty v-if="listpage.list.length === 0">
            <el-button type="success" @click="editForm.show = true">
                <span class="iconfont icon-plus" style="font-size:14px;">&nbsp;添加</span>
            </el-button>
        </el-empty>
        <Question 
            v-else
            v-for="(question, index) in listpage.list" 
            :no="question.id" 
            :type="question.type"
            :markType="question.markType" 
            :title="question.title" 
            :score="question.score" 
            :answers="question.answers"
            :options="question.options" 
            :display="display"
            :updateUserName="question.updateUserName"
            :userAnswerShow="false"
            >
            <template #bottom-right>
                <el-button type="primary" @click="editForm.id = question.id; editForm.show = true" size="small">
                    <span class="iconfont icon-edit" style="font-size:14px;">&nbsp;修改</span>
                </el-button>
                <el-button type="primary" @click="copy(question.id)" size="small">
                    <span class="iconfont icon-copy" style="font-size:14px;">&nbsp;复制</span>
                </el-button>
                <el-button type="danger" @click="del(question.id)" size="small">
                    <span class="iconfont icon-delete" style="font-size:14px;">&nbsp;删除</span>
                </el-button>
            </template>
        </Question>
        <el-pagination 
            v-model:current-page="listpage.curPage" 
            v-model:page-size="listpage.pageSize" 
            :total="listpage.total" 
            background
            layout="prev, pager, next" 
            :hide-on-single-page="true" 
            @size-change="query"
            @current-change="query"
            @prev-click="query"
            @next-click="query"
        />

        <!-- 试题编辑 关闭时重置ID，问题为第二次打开id未变化，不会从后台重新加载数据 -->
        <el-drawer
            title="试题编辑"
            v-model="editForm.show"
            :size="550"
            @close="editForm.id = 0"
        >
            <QuestionEdit :id="editForm.id" @add="add" @edit="edit"/>
        </el-drawer>
    </div>
    <QuestionEditor v-else @back="txtForm.show = false" @txt-import="txtImport">
        <template #top-right>
            <div v-if="percentage" class="question-editor-top-right">
                <el-progress :percentage="percentage" />
            </div>
        </template>
    </QuestionEditor>
</template>
  
<script lang="ts" setup>
import { ref, reactive, onMounted } from 'vue';
import Question from '@/components/question/Question.vue';
import QuestionEdit from '@/components/question/QuestionEdit.vue';
import http from '@/request';
import { useRoute, useRouter } from 'vue-router';
import { useDictStore } from '@/stores/dict';
import QuestionEditor from '@/components/question/QuestionEditor.vue';

// 定义变量
const route = useRoute()
const display = ref('list') // 显示样式
const dictStore = useDictStore()// 字典缓存
const txtForm = reactive({// 文本表单
    show: false,
})
const editForm = reactive({// 编辑表单
    show: false,
    id: 0
})
const queryForm = reactive({// 查询表单
    questionTypeId: 0,
    id: '',
    title: '',
    type: null,
    markType: null,
})
const listpage = reactive({// 分页列表
    curPage: 1,
    pageSize: 5,
    total: 0,
    list: [] as any[],
})

const percentage = ref(0)// 进度条进度

// 组件挂载完成后，执行如下方法
onMounted(() => {
    queryForm.questionTypeId = parseInt(route.params.questionTypeId as string)
    query()
})

// 查询
async function query() {
    const { data: { code, data } } = await http.post('question/listpage', {
        ...queryForm,
        curPage: listpage.curPage,
        pageSize: listpage.pageSize,
    })
    if (code !== 200) {
        return
    }

    listpage.list = data.list
    listpage.total = data.total
}

// 添加
async function add(question: any) {
    let { data: { code, data } } = await http.post("question/add", {...question, questionTypeId: queryForm.questionTypeId})
    if (code !== 200) {
        return
    }

    editForm.show = false
    query()
}

// 修改
async function edit(question: any) {
    let { data: { code } } = await http.post("question/edit", question)
    if (code !== 200) {
        return
    }

    editForm.show = false // 关闭试题修改页面
    query()
}

// 复制
async function copy(id: number) {
    let { data: { code } } = await http.post("question/copy", {id})
    if (code !== 200) {
        return
    }

    query()
}

// 删除
async function del(id: number) {
    let { data: { code } } = await http.post("question/del", {ids: id})
    if (code !== 200) {
        return
    }

    query()
}

// 文本导入
/**
 * type: 0,
    title: '',
    options: [] as string[],
    answers: [] as string[],
    score: 0,
    scores: [] as number[],
    analysis: '',
    markType: 0,
    markTypeOptions: [] as number[],
    state: 1,
    no: 1,
    errs: '',
 */
async function txtImport(questions: any) {
    let total = questions._value.length
    let curIndex = 1;
    for (let question of questions._value) {
        await http.post("question/add", {...question as Object, questionTypeId: queryForm.questionTypeId})

        percentage.value = Math.ceil(curIndex++ * 100 / total)
    }

    txtForm.show = false
    percentage.value = 0
    query()
}
</script>

<style lang="scss" scoped>
.question {
    background-color: white;
    flex: 1;

    .question-query {
        padding: 15px;
        border-bottom: 1px solid var(--el-border-color);

        .el-form-item {
            margin-bottom: 0px;
        }
    }
}
.question-editor-top-right {
    display: inline-block;
    width: 200px;
}
</style>
  