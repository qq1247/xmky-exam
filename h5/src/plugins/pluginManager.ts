// src/plugins/pluginManager.ts
//
// 🧩 插件管理器核心模块
//
// 本模块负责动态加载、初始化和管理外部插件。
// 宿主应用应调用 `initPlugins()` 启动插件系统。
// 插件开发者需确保其构建产物挂载到 window 并实现标准 API 接口。
//
// 🔧 使用方式：
//   1. 构建插件为 IIFE/UMD 格式，挂载名格式为 camelCase（如 'myPlugin'）
//   2. 实现 init(), mount(), unmount(), getInfo() 四个方法
//   3. 在 plugins-config.json 中配置插件元信息与脚本路径
//   4. 调用 initPlugins() 自动完成加载流程

// ========== 核心依赖 ==========
import * as Vue from 'vue'
import router from '@/router'
import ElementPlus from 'element-plus'
import { camelize } from '@vueuse/core'

// ========== 类型定义 ==========
/**
 * 插件可接收的配置项类型，由宿主传入。
 * 插件内部应自行校验结构与类型。
 */
export interface PluginOption {
    [key: string]: unknown
}

/**
 * 插件配置结构，来源于 plugins-config.json 配置文件。
 */
export interface PluginConfig {
    name: string        // 插件唯一标识（推荐使用 kebab-case）
    url: string         // 插件脚本 URL（支持远程或本地）
    enabled: boolean    // 是否启用
    option?: PluginOption // 初始化配置参数
}

/**
 * 所有插件必须实现的标准接口。
 * 通过 window[camelCaseName] 暴露。
 */
interface PluginAPI {
    init(config: PluginOption): void    // 初始化插件
    mount(): void                       // 挂载资源
    unmount(): void                     // 清理资源
    getInfo(): {                        // 获取元信息
        name: string
        version: string
        displayName: string
        description: string
    }
}

// ========== 状态存储 ==========
// 存储已加载的插件实例
const plugins = new Map<string, PluginAPI>()

// 存储所有插件的元信息（用于展示或调试）
const pluginInfos: Array<ReturnType<PluginAPI['getInfo']>> = []

// ========== 插件生命周期管理 ==========
/**
 * 加载插件
 *
 * 流程：
 * 1. 若未启用则跳过
 * 2. 加载脚本
 * 3. 从 window 获取插件实例
 * 4. 调用 init 和 mount
 * 5. 缓存实例与元信息
 */
export async function loadPlugin(config: PluginConfig): Promise<void> {
    if (!config.enabled) {
        console.log(`⏭️ 插件已禁用，跳过加载: ${config.name}`)
        return
    }

    try {
        await new Promise<void>((resolve, reject) => {
            const script = document.createElement('script')
            script.src = config.url
            script.type = 'text/javascript'
            script.async = true

            script.onload = () => {
                console.log(`✅ 插件加载成功: ${config.url}`)
                resolve()
            }

            script.onerror = () => {
                console.error(`❌ 插件加载失败: ${config.url}`)
                reject(new Error(`插件加载失败: ${config.url}`))
            }

            document.head.appendChild(script)
        })

        const pluginName = camelize(config.name)
        const plugin = window[pluginName] as PluginAPI
        if (!plugin) {
            throw new Error(`插件未注册到 window: ${pluginName}`)
        }

        plugin.init(config.option || {})
        plugin.mount()
        plugins.set(config.name, plugin)

        const info = plugin.getInfo()
        const index = pluginInfos.findIndex(p => p.name === info.name)
        if (index > -1) {
            pluginInfos[index] = info
        } else {
            pluginInfos.push(info)
        }

        console.log(`🔌 插件加载成功: ${info.displayName} (v${info.version})`)
    } catch (err) {
        console.error(`❌ 插件加载失败: ${config.name}`, err)
    }
}

/**
 * 获取所有已加载插件的元信息副本
 * 防止外部直接修改内部状态。
 */
export function getAllPluginInfos(): Array<ReturnType<PluginAPI['getInfo']>> {
    return [...pluginInfos]
}

// ========== 主入口函数 ==========
/**
 * 初始化整个插件系统
 *
 * 步骤：
 * 1. 暴露 Vue、ElementPlus 等核心依赖至 window
 * 2. 加载 plugins-config.json 配置
 * 3. 依次加载并初始化所有启用的插件
 *
 * 建议在主应用启动完成后调用。
 */
export async function initPlugins(): Promise<void> {
    // 暴露核心框架能力给插件使用
    window.vue = Vue
    window.router = router
    window.elementPlus = ElementPlus
    import('@vueuse/core')
        .then(module => {
            window.vueUse = module
        })
        .catch(err => {
            console.warn('⚠️ 无法加载 @vueuse/core，部分功能受限', err)
        })

    // 加载插件配置文件
    let pluginConfigs: PluginConfig[] = []
    try {
        const res = await fetch('/plugins-config.json')
        if (!res.ok) throw new Error(`加载plugins-config.json失败`)
        pluginConfigs = await res.json()
        console.log('✅ 插件配置加载成功:', pluginConfigs)
    } catch (err) {
        console.error('❌ 无法加载插件配置文件:', err)
    }

    // 批量加载所有插件
    try {
        for (const config of pluginConfigs) {
            await loadPlugin(config)
        }
        console.log('🎉 所有插件加载完成')
    } catch (err) {
        console.error('❌ 插件加载过程出错:', err)
    }
}