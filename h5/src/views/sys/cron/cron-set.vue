<template>
    <el-scrollbar max-height="calc(100vh - 190px)" class="cron-set">
        <xmks-edit-card title="定时任务" desc="定时任务">
            <template #card-main>
                <el-form ref="formRef" :model="form" :rules="formRules" label-width="100" size="large" class="form">
                    <el-form-item label="名称" prop="name">
                        <el-input v-model="form.name" placeholder="请输入名称" />
                    </el-form-item>
                    <el-form-item label="实现类" prop="jobClass">
                        <el-input v-model="form.jobClass" placeholder="请输入实现类" />
                    </el-form-item>
                    <el-form-item label="cron表达式" prop="cron">
                        <el-input v-model="form.cron" placeholder="请输入cron表达式" />
                    </el-form-item>
                    <el-form-item>
                        <el-button v-if="$route.path.indexOf('add') !== -1" type="primary" class="form__btn"
                            @click="add">添加</el-button>
                        <el-button v-else type="primary" class="form__btn" @click="edit">修改</el-button>
                    </el-form-item>
                </el-form>
            </template>
        </xmks-edit-card>
        <xmks-edit-card v-if="form.id" title="执行任务" desc="执行任务">
            <template #card-main>
                <el-form size="large" class="form">
                    <el-radio-group v-model="form.state">
                        <el-radio v-for="(dict, index) in dictStore.getList('STATE_SS')" :key="index"
                            :value="parseInt(dict.dictKey)">
                            {{ dict.dictValue }}
                        </el-radio>
                    </el-radio-group>
                </el-form>
            </template>
            <template #card-side>
                <el-button type="primary" class="form__btn" @click="execute"
                    style="margin-bottom: 30px;">执行任务</el-button>
            </template>
        </xmks-edit-card>
        <xmks-edit-card v-if="form.id" title="执行一次" desc="执行一次">
            <template #card-side>
                <el-button type="primary" class="form__btn" @click="runOnce"
                    style="margin-bottom: 14px;">执行一次</el-button>
            </template>
        </xmks-edit-card>
        <xmks-edit-card v-if="form.id" title="删除定时任务" desc="删除定时任务">
            <template #card-side>
                <el-button type="primary" class="form__btn" :class="{ 'form__btn--warn': delConfirm }" @click="del"
                    style="margin-bottom: 14px;">删除定时任务</el-button>
            </template>
        </xmks-edit-card>
    </el-scrollbar>
</template>

<script lang="ts" setup>
import { cronAdd, cronDel, cronEdit, cronGet, cronRunOnceTask, cronStartTask, cronStopTask } from '@/api/sys/cron'
import XmksEditCard from '@/components/card/xmks-card-edit.vue'
import { useDictStore } from '@/stores/dict'
import type { Cron } from '@/ts/sys/cron'
import { type FormInstance, type FormRules } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

/************************变量定义相关***********************/
const route = useRoute()// 路由
const router = useRouter()// 路由
const dictStore = useDictStore()// 字典缓存
const formRef = ref<FormInstance>()// 表单引用
const form = reactive<Cron>({// 表单
    id: null,
    name: '',
    jobClass: '',
    cron: '',
    state: 0,
})
const delConfirm = ref(false) // 删除确认
const formRules = reactive<FormRules>({// 表单规则
    name: [
        { required: true, message: '请输入名称', trigger: 'blur' },
        { min: 1, max: 16, message: '长度介于1-16', trigger: 'blur' },
    ],
    jobClass: [
        { required: true, message: '请输入实现类', trigger: 'blur' },
        { min: 1, max: 32, message: '长度介于1-32', trigger: 'blur' },
    ],
    cron: [
        { required: true, message: '请输入cron表达式', trigger: 'blur' },
        { min: 1, max: 32, message: '长度介于1-32', trigger: 'blur' },
    ],
})

/************************组件生命周期相关*********************/
onMounted(async () => {
    if (route.path.indexOf('add') !== -1) {// 添加

    } else {// 修改
        const { data: { data } } = await cronGet({ id: route.params.id })
        form.id = data.id
        form.name = data.name
        form.jobClass = data.jobClass
        form.cron = data.cron
        form.state = data.state
    }
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
    const { data: { code } } = await cronAdd({ ...form })
    if (code !== 200) {
        return
    }

    router.push("/sys-nav/cron-list")
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
    const { data: { code } } = await cronEdit({ ...form })
    if (code !== 200) {
        return
    }

    router.push("/sys-nav/cron-list")
}

// 删除
async function del() {
    if (!delConfirm.value) {
        delConfirm.value = true
        return
    }

    const { data: { code } } = await cronDel({
        id: form.id,
    })
    if (code !== 200) {
        return
    }

    router.push("/sys-nav/cron-list")
}

// 执行任务
async function execute() {
    if (form.state === 1) {
        const { data: { code } } = await cronStartTask({ ...form })
        if (code !== 200) {
            return
        }
    }

    if (form.state === 2) {
        const { data: { code } } = await cronStopTask({ ...form })
        if (code !== 200) {
            return
        }
    }

    router.push("/sys-nav/cron-list")
}

// 执行一次
async function runOnce() {
    const { data: { code } } = await cronRunOnceTask({ ...form })
    if (code !== 200) {
        return
    }

    router.push("/sys-nav/cron-list")
}
</script>

<style lang="scss" scoped>
.cron-set {
    display: flex;
    flex-direction: column;
    width: 1200px;
    background-color: #FFFFFF;
    margin: 20px 0px;
    padding: 40px 30px 0px 30px;
    border-radius: 15px 15px 15px 15px;

    .form {
        margin-top: 20px;
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
}
</style>
