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
    if (route.path.indexOf('exer/add') !== -1) {
        tabs.push({
            title: '练习信息',
            desc: '添加',
            icon: 'icon-edit',
            url: `/exer/add`,
        })
    } else {
        tabs.push({
            title: '练习信息',
            desc: '修改',
            icon: 'icon-edit',
            url: `/exer/edit/${route.params.id}`,
        })
        tabs.push({
            title: '删除',
            desc: '删除练习',
            icon: 'icon-delete',
            url: `/exer/del/${route.params.id}`,
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