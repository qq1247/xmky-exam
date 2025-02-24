<template>
    <el-scrollbar max-height="calc(100vh - 190px)" class="bulletin-set">
        <xmks-edit-card title="公告" desc="公告">
            <template #card-main>
                <el-form ref="formRef" :model="form" :rules="formRules" label-width="100" size="large" class="form">
                    <el-form-item label="查阅时间" prop="times">
                        <el-date-picker v-model="form.times" type="datetimerange" start-placeholder="开始时间"
                            end-placeholder="结束时间" value-format="YYYY-MM-DD HH:mm:ss" />
                    </el-form-item>
                    <el-form-item label="标题" prop="title">
                        <el-input v-model="form.title" placeholder="请输入标题" />
                    </el-form-item>
                    <el-form-item label="内容" prop="content">
                        <el-input v-model="form.content" placeholder="请输入内容" :autosize="{ minRows: 2, maxRows: 20 }"
                            type="textarea" />
                    </el-form-item>
                    <el-form-item>
                        <el-button v-if="$route.path.indexOf('add') !== -1" type="primary" class="form__btn"
                            @click="add">添加</el-button>
                        <el-button v-else type="primary" class="form__btn" @click="edit">修改</el-button>
                    </el-form-item>
                </el-form>
            </template>
        </xmks-edit-card>
        <xmks-edit-card v-if="form.id" title="删除公告" desc="删除公告">
            <template #card-side>
                <el-button type="primary" class="form__btn" :class="{ 'form__btn--warn': delConfirm }" @click="del"
                    style="margin-bottom: 14px;">删除公告</el-button>
            </template>
        </xmks-edit-card>
    </el-scrollbar>
</template>

<script lang="ts" setup>
import { bulletinAdd, bulletinDel, bulletinEdit, bulletinGet } from '@/api/sys/bulletin'
import XmksEditCard from '@/components/card/xmks-card-edit.vue'
import type { Bulletin } from '@/ts/sys/bulletin'
import { dayjs, type FormInstance, type FormRules } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

/************************变量定义相关***********************/
const route = useRoute()// 路由
const router = useRouter()// 路由
const formRef = ref<FormInstance>()// 表单引用
const form = reactive<Bulletin>({// 表单
    id: null,
    title: '',
    content: '',
    times: [
        dayjs().add(1, 'day').hour(0).minute(0).second(0).toDate(),
        dayjs().add(1, 'day').hour(23).minute(59).second(59).toDate(),
    ]
})
const delConfirm = ref(false) // 删除确认
const formRules = reactive<FormRules>({// 表单规则
    times: [{
        trigger: 'change',
        validator: (rule: any, value: Date[], callback: any) => {
            if (!value || !value[0] || !value[1]) {
                return callback(new Error("请选择时间段"))
            }
            if (dayjs(value[0], 'YYYY-MM-DD HH:mm:ss').toDate() >= dayjs(value[1], 'YYYY-MM-DD HH:mm:ss').toDate()) {
                return callback(new Error("开始时间必须小于结束时间"))
            }
            return callback()
        }
    }],
    title: [
        { required: true, message: '请输入标题', trigger: 'blur' },
        { min: 1, max: 15, message: '长度介于1-15', trigger: 'blur' },
    ],
    content: [
        { required: true, message: '请输入内容', trigger: 'blur' },
        { min: 1, max: 10000, message: '长度介于1-10000', trigger: 'blur' },
    ],
})

/************************组件生命周期相关*********************/
onMounted(async () => {
    if (route.path.indexOf('add') !== -1) {// 添加

    } else {// 修改
        const { data: { data } } = await bulletinGet({ id: route.params.id })
        form.id = data.id
        form.times[0] = data.startTime
        form.times[1] = data.endTime
        form.title = data.title
        form.content = data.content
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
    const { data: { code } } = await bulletinAdd({
        startTime: form.times[0],
        endTime: form.times[1],
        title: form.title,
        content: form.content,
    })
    if (code !== 200) {
        return
    }

    router.push("/sys-nav/bulletin-list")
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
    const { data: { code } } = await bulletinEdit({
        id: form.id,
        startTime: form.times[0],
        endTime: form.times[1],
        title: form.title,
        content: form.content,
    })
    if (code !== 200) {
        return
    }

    router.push("/sys-nav/bulletin-list")
}

// 删除
async function del() {
    if (!delConfirm.value) {
        delConfirm.value = true
        return
    }

    const { data: { code } } = await bulletinDel({
        id: form.id,
    })
    if (code !== 200) {
        return
    }

    router.push("/sys-nav/bulletin-list")
}
</script>

<style lang="scss" scoped>
.bulletin-set {
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
