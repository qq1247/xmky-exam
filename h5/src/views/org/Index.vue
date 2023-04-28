<template>
    <template v-if="$route.path === '/org'">
        <el-form :inline="true" :model="queryForm" size="large" class="query">
            <el-form-item label="">
                <el-input v-model="queryForm.name" placeholder="请输入名称" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="query">
                    <Iconfont icon="icon-search" color="white">&nbsp;查询</Iconfont>
                </el-button>
            </el-form-item>
        </el-form>
        <el-table :data="listpage.list" style="width: 100%" class="table" size="large" row-key="id"
            default-expand-all>
            <el-table-column prop="name" label="名称" align="center" />
            <el-table-column prop="parentName" label="所属机构" align="center" />
            <el-table-column prop="no" label="排序" align="center" />
            <el-table-column prop="" label="操作" align="center" width="300">
                <template #default="scope">
                    <el-tooltip effect="dark" content="添加">
                        <Iconfont icon="icon-add" :size="18" color="#409eff"
                            @click="$router.push(`/org/add/${scope.row.id}`)"></Iconfont>
                    </el-tooltip>
                    <el-tooltip ffect="dark" content="修改">
                        <Iconfont icon="icon-edit" :size="18" color="#409eff"
                            @click="$router.push(`/org/edit/${scope.row.id}`)"></Iconfont>
                    </el-tooltip>
                    <el-tooltip v-if="scope.row.id !== 1" effect="dark" content="删除">
                        <Iconfont icon="icon-delete" :size="18" color="#409eff"
                            @click="$router.push(`/org/del/${scope.row.id}`)"></Iconfont>
                    </el-tooltip>
                </template>
            </el-table-column>
        </el-table>
    </template>
    <RouterView v-else></RouterView>
</template>
  
<script lang="ts" setup>
import { ref, reactive, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router'
import http from "@/request"
import { Download } from '@element-plus/icons-vue';
import { useUserStore } from '@/stores/user';
import { ElMessage, type UploadFile, type UploadFiles, type UploadRawFile } from 'element-plus';

//  定义变量
const route = useRoute()
const userStore = useUserStore()
const queryForm = reactive({// 查询表单
    name: '',
})
const listpage = reactive({// 分页列表
    curPage: 1,
    pageSize: 100,
    total: 0,
    list: [] as any[],
})

// 组件挂载完成后，执行如下方法
onMounted(() => {
    query()
})

// 如果是跳转到列表页，重新查询
watch(() => route.path, (n, o) => {
    if (n === '/org') {
        query()
    }
})

// 查询
async function query() {
    const { data: { code, data } } = await http.post('org/listpage', {
        name: queryForm.name,
        curPage: listpage.curPage,
        pageSize: listpage.pageSize,
    })

    if (code !== 200) {
        return
    }

    let list = data.list
    const treeList = []// 列表转树形列表
    const treeMap: any = {}
    const idFiled = 'id'
    const parentField = 'parentId'
    for (let i = 0; i < list.length; i++) {
        list[i]['id'] = list[i][idFiled]
        treeMap[list[i]['id']] = list[i]
    }

    for (let i = 0; i < list.length; i++) {
        if (treeMap[list[i][parentField]] && list[i]['id'] !== list[i][parentField]) {
            if (!treeMap[list[i][parentField]]['children']) {
                treeMap[list[i][parentField]]['children'] = []
            }
            treeMap[list[i][parentField]]['children'].push(list[i])
        } else {
            treeList.push(list[i])
        }
    }

    listpage.list = treeList
    listpage.total = data.total
}

// 下载模板
async function download() {
    let data = await http.post('org/template', {}, { responseType: 'blob' })
    const blob = new Blob([data.data]);
    const aEle = document.createElement('a');
    aEle.download = '机构模板.xlsx';
    aEle.style.display = 'none';
    aEle.href = URL.createObjectURL(blob);
    aEle.click();
    URL.revokeObjectURL(aEle.href);
}

// 上传之前处理
function uploadBefore (rawFile: UploadRawFile) {
    if (rawFile.name.indexOf('.xlsx') === -1) {
        ElMessage.error('只允许 xlsx 文件')
        return false
    }
    if (rawFile.size / 1024 > 512) {
        ElMessage.error('最大0.5兆')
        return false
    }

    return true
}

// 上传成功处理
async function uploadSuccess(response: any, uploadFile: UploadFile, uploadFiles: UploadFiles) {
    await http.post('org/import', {fileId: response.data.fileIds})
    
    query()
}

</script>

<style lang="scss" scoped>

</style>
  