<template>
    <el-card class="edit" shadow="never">
        <template #header>
            <div class="edit-title">扫码答题</div>
            <div class="edit-desc">移动端扫码答题时使用。<router-link v-if="userStore.type === 0"
                    to="/parm/Mobile">去设置</router-link><span v-else>请联系管理员设置</span></div>
        </template>
        <vue-qrcode v-if="host" :value="`${host}${uri}`" :options="{ width: 200 }"></vue-qrcode>
        <el-input v-if="host" :value="`${host}${uri}`" style="width: 100%" placeholder="" />
    </el-card>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import http from '@/request'
import { useRoute } from 'vue-router'
import VueQrcode from '@chenfengyuan/vue-qrcode';
import { useUserStore } from '@/stores/user';

// 定义变量
const route = useRoute()
const userStore = useUserStore()
const host = ref('')
const uri = ref('')

// 组件挂载完成后，执行如下方法
onMounted(async () => {
    let { data: { data: exam } } = await http.post("exam/get", { id: route.params.id })

    let { data: { data } } = await http.post("parm/get")
    host.value = data.mHost

    if (exam.loginType === 2) {
        uri.value = `/pages/login/noLogin?redirectPath=` + encodeURIComponent(`/pages/myExam/myRead?examId=${route.params.id}&loginType=2`)
    } else {
        uri.value = `/pages/login/login?redirectPath=` + encodeURIComponent(`/pages/myExam/myRead?examId=${route.params.id}&loginType=1`)
    }
})

</script>

<style lang="scss" scoped>
.edit {
    padding: 10px;

    :deep(.el-card__header) {
        padding-bottom: 0px;

        .edit-title {
            font-size: 14px;
            font-weight: bold;
            padding-bottom: 5px;
        }

        .edit-desc {
            font-size: 12px;
            color: var(--el-text-color-secondary);
        }
    }

}
</style>