<template>
    <el-scrollbar max-height="calc(100vh - 240px)" class="bulletin-detail">
        <div class="bulletin">
            <div class="bulletin__title">
                {{ form.title }}
            </div>
            <div class="bulletin__sub-title">
                {{ form.startTime }}
            </div>
            <div class="bulletin__content">
                {{ form.content }}
            </div>
        </div>
    </el-scrollbar>
</template>

<script lang="ts" setup>
import { bulletinGet } from '@/api/sys/bulletin'
import { onMounted, reactive } from 'vue'
import { useRoute } from 'vue-router'

/************************变量定义相关***********************/
const route = useRoute()// 路由
const form = reactive({
    id: null,
    title: '',
    content: '',
    startTime: '',
    endTime: '',
})

/************************组件生命周期相关*********************/
onMounted(async () => {
    const { data: { data } } = await bulletinGet({ id: route.params.id })
    form.id = data.id
    form.startTime = data.startTime
    form.endTime = data.endTime
    form.title = data.title
    form.content = data.content
})

</script>

<style lang="scss" scoped>
.bulletin-detail {
    display: flex;
    flex-direction: column;
    width: 1200px;
    margin: 20px 0px;

    .bulletin {
        height: calc(100vh - 240px);
        border-radius: 15px 15px 15px 15px;
        background-color: #FFFFFF;
        padding: 30px;

        .bulletin__title {
            display: flex;
            justify-content: center;
            margin-top: 10px;
            font-size: 16px;
        }

        .bulletin__sub-title {
            display: flex;
            justify-content: center;
            margin-top: 5px;
            font-size: 14px;
            color: #8F939C;
        }

        .bulletin__content {
            margin-top: 15px;
            font-size: 14px;
            color: #8F939C;
            line-height: 20px;
            text-indent: 2em;
        }
    }
}
</style>
