<!-- src/components/xmky-plugin.vue -->
<template>
    <div v-if="pluginInfos.length > 0" class="xmky-plugin">
        <el-button size="large" type="primary" class="xmky-plugin__btn" @click="drawerVisible = true">
            <span class="iconfont icon-a-16ri-03"></span>
        </el-button>
        <el-drawer v-model="drawerVisible" :modal-penetrable="true" title="插件列表" direction="rtl" size="300px">
            <el-scrollbar max-height="100%">
                <div v-for="(pluginInfo, index) in pluginInfos" :key="index" @click="open(pluginInfo.id)" class="list">
                    <div class="list__title">
                        {{ pluginInfo.name }}
                    </div>
                    <div class="list__desc">
                        {{ pluginInfo.description }}
                    </div>
                    <div class="list__tags">
                        <el-tag class="list__tag list__tag--plugin-id">
                            {{ pluginInfo.id }}
                        </el-tag>
                        <el-tag class="list__tag list__tag--plugin-ver">
                            {{ pluginInfo.version }}
                        </el-tag>
                    </div>
                </div>
            </el-scrollbar>
        </el-drawer>
    </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { getAllPluginInfos, type PluginAPI } from '@/plugins/pluginManager'
import { camelize } from '@vueuse/core'
import { useUserStore } from '@/stores/user'

/************************变量定义相关***********************/
const userStore = useUserStore()
const pluginInfos = ref(getAllPluginInfos())
const drawerVisible = ref(false)

watch(() => userStore.role, () => {
    pluginInfos.value = getAllPluginInfos()
}, { immediate: true }
)

/************************事件相关*****************************/
// 打开
function open(pluginId: string) {
    const plugin = window[camelize(pluginId)] as PluginAPI
    plugin.mount()
}
</script>

<style lang="scss" scoped>
.xmky-plugin {
    position: fixed;
    bottom: 40px;
    right: 40px;
    z-index: 1;

    .xmky-plugin__btn {
        width: 48px;
        height: 48px;
        border-radius: 50%;
        border: 0px;
        color: #FFFFFF;
        background-image: linear-gradient(to right, #04C7F2, #259FF8);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);

        .iconfont {
            font-size: 20px;
        }
    }

    :deep(.el-drawer__header) {
        margin-bottom: 10px;
    }

    .list {
        display: flex;
        flex-direction: column;
        justify-content: center;
        position: relative;
        // background-color: #fafafb;
        border-radius: 8px;
        border-bottom: 1px dashed #E5E5E5;
        padding: 10px;
        cursor: pointer;

        &:first-child {
            border-top: 1px dashed #E5E5E5;
        }


        &:hover {
            background-color: #f3f3f5;
        }

        .list__title {
            font-size: 14px;
            color: #333333;
            line-height: 30px;
        }

        .list__desc {
            font-size: 12px;
            color: #999999;
            line-height: 18px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }

        .list__tags {
            margin-top: 10px;

            .list__tag {
                height: 22px;
                padding: 0px 10px;
                font-size: 12px;
                margin-right: 10px;


                &.list__tag--plugin-ver {
                    color: #FC8113;
                    background-color: #FDEDD9;
                    border: 1px solid #FED9B3;
                }

                &.list__tag--plugin-id {
                    color: #1AC693;
                    background-color: #E8F9F4;
                    border: 1px solid #AFE7D6;
                }
            }
        }
    }
}
</style>
<style>
.el-overlay {
    background-color: initial;
}
</style>