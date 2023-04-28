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
    tabs.push({
        title: '默认密码',
        desc: '设置密码生成策略',
        icon: 'icon-pwd-reset',
        url: `/parm/pwd`,
    })
    tabs.push({
        title: '企业信息',
        desc: '企业信息',
        icon: 'icon-qiye',
        url: `/parm/Ent`,
    })
    tabs.push({
        title: '服务支持',
        desc: '服务支持',
        icon: 'icon-fuwuzhichi',
        url: `/parm/Custom`,
    })
    // tabs.push({
    //     title: '邮箱设置',
    //     desc: '邮箱设置',
    //     icon: 'icon-email',
    //     url: `/parm/Email`,
    // })
    // tabs.push({
    //     title: '上传附件',
    //     desc: '上传附件保存位置',
    //     icon: 'icon-line-upload',
    //     url: `/parm/file`,
    // })
    // tabs.push({
    //     title: '备份目录',
    //     desc: '备份目录',
    //     icon: 'icon-composition',
    //     url: `/parm/db`,
    // })
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