<template>
    <div class="exam-user">
        <div class="exam-user__head">
            <div class="opt">
                <el-button v-if="userStore.type === 0" type="success" class="opt__btn"
                    @click="$router.push('/exam-user-nav/add')">
                    <span class="iconfont icon-tubiaoziti2-02 opt__btn-icon"></span>
                    <span class="opt__btn-txt">添加</span>
                </el-button>
                <el-button v-if="userStore.type === 0" type="success" class="opt__btn opt__btn--secondary"
                    @click="download">
                    <span class="iconfont icon-xiazaimoban opt__btn-icon"></span>
                    <span class="opt__btn-txt">模板下载</span>
                </el-button>
                <el-upload v-if="userStore.type === 0" :action="`${http.defaults.baseURL}file/upload`"
                    :headers="{ Authorization: userStore.accessToken }" name="files" :show-file-list="false"
                    :before-upload="uploadBefore" :on-success="uploadSuccess">
                    <el-button type="success" class="opt__btn opt__btn--secondary">
                        <span class="iconfont icon-daoru opt__btn-icon"></span>
                        <span class="opt__btn-txt">批量导入</span>
                    </el-button>
                </el-upload>
            </div>
            <el-form :model="queryForm" :inline="true" size="large" class="query">
                <el-form-item label="">
                    <el-input v-model="queryForm.name" placeholder="请输入姓名或机构名称" />
                </el-form-item>
                <el-form-item label="">
                    <el-select v-model="queryForm.state" placeholder="请选择状态" clearable size="large"
                        style="width: 200px;">
                        <el-option v-for="dict in dictStore.getList('STATE_NF')" :key="dict.dictKey"
                            :label="dict.dictValue" :value="dict.dictKey" />
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" class="query__btn" @click="query">查询</el-button>
                </el-form-item>
            </el-form>
        </div>
        <div class="exam-user__main">
            <el-table :data="listpage.list" size="large" row-key="id" height="calc(100vh - 460px)" class="table">
                <el-table-column prop="name" label="姓名" align="center" />
                <el-table-column prop="loginName" label="登录账号" align="center" />
                <el-table-column prop="orgName" label="所属机构" align="center" />
                <el-table-column prop="state" label="状态" align="center">
                    <template #default="scope">
                        <span v-if="scope.row.state === 2" style="color: #f56c6c">
                            {{ dictStore.getValue('STATE_NF', scope.row.state) }}
                        </span>
                        <span v-else>{{ dictStore.getValue('STATE_NF', scope.row.state) }}</span>
                    </template>
                </el-table-column>
                <el-table-column v-if="userStore.type === 0" prop="" label="操作" align="center" width="300">
                    <template #default="scope">
                        <el-tooltip ffect="dark" content="设置">
                            <span class="iconfont icon-liebiao-01 table__btn"
                                @click="$router.push(`/exam-user-nav/set/${scope.row.id}`)"></span>
                        </el-tooltip>
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination v-model:current-page="listpage.curPage" v-model:page-size="listpage.pageSize"
                :total="listpage.total" background layout="prev, pager, next" :hide-on-single-page="true" size="large"
                class="pagination" @size-change="query" @current-change="query" @prev-click="query"
                @next-click="query" />
        </div>
    </div>
</template>
<script setup lang="ts">
import { reactive, onMounted, } from 'vue'
import type { Listpage } from '@/ts/common/listpage'
import { userListpage } from '@/api/base/user'
import http from "@/request"
import { useDictStore } from '@/stores/dict'
import { useUserStore } from '@/stores/user'
import { ElMessage, type UploadFile, type UploadFiles, type UploadRawFile } from 'element-plus'

/************************变量定义相关***********************/
const userStore = useUserStore()// 用户缓存
const dictStore = useDictStore()// 字典缓存
const queryForm = reactive({// 查询表单
    name: '',
    state: '',
    type: 1,// 查询考试用户
})
const listpage = reactive<Listpage>({// 分页列表
    curPage: 1,
    pageSize: 8,
    total: 0,
    list: [],
})

/************************组件生命周期相关*********************/
onMounted(() => {
    query()
})

/************************事件相关*****************************/
// 查询
async function query() {
    const { data: { code, data } } = await userListpage({
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

// 下载模板
async function download() {
    const data = await http.post('user/template', {}, { responseType: 'blob' })
    const blob = new Blob([data.data]);
    const aEle = document.createElement('a');
    aEle.download = '用户模板.xlsx'
    aEle.style.display = 'none'
    aEle.href = URL.createObjectURL(blob);
    aEle.click();
    URL.revokeObjectURL(aEle.href);
}

// 上传之前处理
function uploadBefore(rawFile: UploadRawFile) {
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
    await http.post('user/import', { fileId: response.data.fileIds })

    query()
}
</script>
<style lang="scss" scoped>
.exam-user {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 30px 30px 30px 30px;

    .exam-user__head {
        display: flex;
        justify-content: space-between;

        .opt {
            flex: 1;
            display: flex;

            .opt__btn {
                height: 40px;
                padding: 0px 30px;
                border-radius: 6px;
                border: 0px;
                background-image: linear-gradient(to right, #04C7F2, #259FF8);

                .opt__btn-icon {
                    color: #FFFFFF;
                    font-size: 16px;
                    margin-right: 4px;
                }

                .opt__btn-txt {
                    color: #FFFFFF;
                    font-size: 14px;
                }
            }

            .opt__btn--secondary {
                padding: 0px 25px;
                border: 1px solid #04C7F2;
                background-image: linear-gradient(to right, #FFFFFF, #FFFFFF);
                margin-left: 10px;

                .opt__btn-icon {
                    color: #04C7F2;
                }

                .opt__btn-txt {
                    color: #04C7F2;
                }
            }
        }

        .query {
            .el-form-item {
                &:last-child {
                    margin-right: 0px;
                }

                .query__btn {
                    height: 40px;
                    padding: 0px 30px;
                    border-radius: 6px;
                    border: 0px;
                    color: #FFFFFF;
                    font-size: 16px;
                    background-image: linear-gradient(to right, #04C7F2, #259FF8);
                }
            }
        }
    }

    .exam-user__main {
        flex: 1;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: space-between;

        .table {

            .table__btn {
                color: #409EFF;
                cursor: pointer;
            }

            .table__btn--warn {
                color: #FF5D15;
            }

            :deep(.el-table__inner-wrapper) {
                &::before {
                    height: 0px;
                }
            }
        }

        :deep(.pagination) {

            .btn-prev,
            .btn-next {
                width: 38px;
                height: 38px;

                .el-icon {
                    font-size: 20px;
                }
            }

            .el-pager {
                li {
                    width: 38px;
                    height: 38px;
                    border-radius: 4px 4px 4px 4px;
                    font-size: 16px;

                    &.is-active {
                        background-image: linear-gradient(to right, #04C7F2, #259FF8);
                    }
                }
            }

        }
    }
}
</style>
