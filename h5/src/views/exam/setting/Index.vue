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
    tabs.push({
        title: '暂停',
        desc: '暂停考试',
        icon: 'icon-delete',
        url: `/exam/pause/${route.params.id}`,
    })
    tabs.push({
        title: '删除',
        desc: '删除考试',
        icon: 'icon-delete',
        url: `/exam/del/${route.params.id}`,
    })
    tabs.push({
        title: '时间变更',
        desc: '时间变更',
        icon: 'icon-time',
        url: `/exam/time/${route.params.id}`,
    })
    tabs.push({
        title: '协助阅卷',
        desc: '协助阅卷',
        icon: 'icon-persons',
        url: `/exam/markUser/${route.params.id}`,
    })
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