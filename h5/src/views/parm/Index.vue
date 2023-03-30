<template>
    <div>
        <RouterView></RouterView>
    </div>
</template>
  
<script lang="ts" setup>
import { ref, reactive, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router'

//  定义变量
const route = useRoute()
const queryForm = reactive({// 查询表单
})
const listpage = reactive({// 分页列表
    curPage: 1,
    pageSize: 10,
    total: 0,
    list: [] as any[],
})

// 组件挂载完成后，执行如下方法
onMounted(() => {
    query()
})

// 如果是跳转到列表页，重新查询
watch(() => route.path, (n, o) => {
    if (n === '/parm') {
        query()
    }
})

// 查询
async function query() {
}

</script>

<style lang="scss" scoped>

</style>
  