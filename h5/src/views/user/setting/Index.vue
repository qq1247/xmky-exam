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
const tabs = reactive([] as Tab[])// 标签组
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
    if (route.path.indexOf('user/add') !== -1) {
        tabs.push({
            title: '用户信息',
            desc: '添加',
            icon: 'icon-edit',
            url: `/user/add/${route.params.parentId}`,
        })
    } else {
        tabs.push({
            title: '用户信息',
            desc: '修改',
            icon: 'icon-edit',
            url: `/user/edit/${route.params.id}`,
        })
        tabs.push({
            title: '密码重置',
            desc: '密码重置',
            icon: 'icon-pwd-reset',
            url: `/user/pwdInit/${route.params.id}`,
        })
        tabs.push({
            title: '账号冻结',
            desc: '账号冻结',
            icon: 'icon-dongjie',
            url: `/user/frozen/${route.params.id}`,
        })
        tabs.push({
            title: '删除',
            desc: '删除用户',
            icon: 'icon-delete',
            url: `/user/del/${route.params.id}`,
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
@import '@/assets/css/setting.scss';
</style>