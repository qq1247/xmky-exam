<template>
    <div class="setting">
        <div class="setting-left">
            <el-tabs v-model="curTab" tab-position="right">
                <el-tab-pane v-for="tab in tabs" :name="tab.url">
                    <template #label>
                        <div class="setting-left-tab">
                            <span :class="`iconfont ${tab.icon}`"></span>
                            <div>
                                <div class="setting-left-tab-title">
                                    {{ tab.title }}
                                </div>
                                <div class="setting-left-tab-desc">
                                    {{ tab.desc }}
                                </div>
                            </div>
                        </div>
                    </template>
                </el-tab-pane>
            </el-tabs>
        </div>
        <div class="setting-right">
            <RouterView></RouterView>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { useRouter, useRoute } from 'vue-router'
import { ref, reactive, computed, onMounted } from 'vue'

// 定义变量
const route = useRoute()
const router = useRouter()
const tabs = reactive([] as Tab[]) // 标签组
const curTab = computed({// 当前标签页
    get() {
        return route.path
    },
    set(n) {
        router.push(n)
    }
})

// 组件挂载完成后，执行如下方法
onMounted(() => {
    if (route.path.indexOf('org/add') !== -1) {
        tabs.push({
            title: '机构信息',
            desc: '添加',
            icon: 'icon-edit',
            url: `/org/add/${route.params.parentId}`,
        })
    } else {
        tabs.push({
            title: '机构信息',
            desc: '修改',
            icon: 'icon-edit',
            url: `/org/edit/${route.params.id}`,
        })
        tabs.push({
            title: '删除',
            desc: '删除机构',
            icon: 'icon-delete',
            url: `/org/del/${route.params.id}`,
        })
    }
})

/**
 * 标签接口
 */
interface Tab {
    title: String // 标题
    desc: String// 描述
    icon: String// 图标
    url: String// 跳转链接
}

</script>

<style lang="scss" scoped>
.setting {
    flex: 1;
    display: flex;

    .setting-left {
        width: 300px;
        background-color: white;
        border-radius: 5px;

        :deep(.el-tabs) {
            .el-tabs__header {
                width: 100%;

                .el-tabs__active-bar {
                    width: 3px;
                }

                .el-tabs__item {
                    line-height: normal;
                    height: auto;
                    border-bottom: 1px solid var(--el-border-color);

                    &:last-child {
                        border-bottom: none;
                    }

                    &:hover {
                        background-color: var(--el-color-primary-light-9);

                        .iconfont {
                            color: var(--el-color-primary);
                        }
                    }

                    .iconfont {
                        padding-right: 10px;
                        font-weight: bold;
                    }

                    .setting-left-tab {
                        display: flex;
                        padding: 10px 0;

                        .setting-left-tab-title {
                            font-weight: bold;
                            margin: 2px 0px 5px 0px;
                        }

                        .setting-left-tab-desc {
                            font-size: 12px;
                            color: var(--el-text-color-secondary);
                        }
                    }

                }
            }
        }
    }

    .setting-right {
        flex: 1;
        margin-left: 20px;
        border-radius: 5px;

        :deep(.el-card__header) {
            border-bottom: 0;

            .setting-right-title {}
        }
    }
}
</style>