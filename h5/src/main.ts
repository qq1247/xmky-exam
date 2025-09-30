// src/main.ts
// 应用入口文件：负责创建 Vue 实例、安装插件、初始化系统并挂载应用

// ========== 核心依赖 ==========
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

// ========== UI 组件与样式 ==========
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import './assets/iconfont/iconfont.css'

// ========== 第三方功能插件 ==========
import vue3PhotoPreview from 'vue3-photo-preview';
import 'vue3-photo-preview/dist/index.css';

// ========== 本地模块 ==========
import App from './App.vue'
import router from './router'
import { initPlugins } from '@/plugins/pluginManager'

// ========== 创建应用实例 ==========
const pinia = createPinia()
const app = createApp(App)
pinia.use(piniaPluginPersistedstate)
app.use(pinia)
app.use(router)
app.use(ElementPlus, {
    locale: zhCn,
})
app.use(vue3PhotoPreview);

(async () => {
    // ========== 初始化插件 ==========
    await initPlugins()

    // ========== 挂载应用到 DOM ==========
    app.mount('#app')
})()
