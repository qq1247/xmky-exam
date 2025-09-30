// stores/exerTimer.ts
import { defineStore } from 'pinia'
import { useIdle, useTimeoutFn, useIntervalFn, } from '@vueuse/core'
import { ref } from 'vue'

/**
 * 练习行为跟踪器
 * 
 * 用于在用户进行练习时，自动采集活跃状态，周期上报，过滤挂机行为。
 * 支持错峰启动、挂机检测、资源自动清理。
 * 
 * ──────────────────────────────────────────────────
 * 使用示例：
 * const timer = useExerTimerStore()
 * onUnmounted(() => {
 *   timer.startTracking({
 *     startupDelaySec: Math.floor(Math.random() * 30) + 1,
 *     trackingIntervalSec: 60,
 *     inactiveThresholdSec: 300,
 *     onStatusUpdate: async (isIdle) => {
 *        if (isIdle) return
 *        await api.report({ exerId, type })
 *     }
 *   })
 * })
 * 
 * onUnmounted(() => timer.stopTracking())
 * ──────────────────────────────────────────────────
 */
export function xmkyTimer() {
    // --- 状态 ---
    const isTrackingActive = ref(false)

    // --- 内部控制器（用于清理）---
    let startupDelayController: ReturnType<typeof useTimeoutFn> | null = null
    let trackingIntervalController: ReturnType<typeof useIntervalFn> | null = null
    let idleDetector: ReturnType<typeof useIdle> | null = null // 挂机检测器引用

    /**
     * 启动周期性状态跟踪
     */
    function startReporting(intervalMs: number, onStatusUpdate: ExerTrackingConfig['onStatusUpdate']) {
        trackingIntervalController = useIntervalFn(async () => {
            const isIdle = idleDetector?.idle.value ?? false // 是否挂机
            await onStatusUpdate(isIdle)
        },
            intervalMs,
            {
                immediate: false,
            }
        )
        trackingIntervalController.resume()
    }

    /**
     * 开始练习行为跟踪
     *
     * 流程：
     * 1. 等待 startupDelaySec 的错峰延迟
     * 2. 启动挂机检测（监听用户交互）
     * 3. 按 trackingIntervalSec 周期调用 onStatusUpdate
     */
    function startTracking(config: ExerTrackingConfig) {
        if (isTrackingActive.value) {
            console.warn('[ExerTimer] 跟踪已在进行中，停止上一个实例')
            stopTracking()
        }

        const {
            startupDelaySec,
            trackingIntervalSec,
            inactiveThresholdSec,
            onStatusUpdate,
        } = config

        const startupDelayMs = Math.max(0, startupDelaySec * 1000)
        const trackingIntervalMs = trackingIntervalSec * 1000
        const inactiveThresholdMs = inactiveThresholdSec * 1000

        isTrackingActive.value = true

        // 启用挂机检测
        idleDetector = useIdle(inactiveThresholdMs)

        // 启动错峰延迟
        // console.log(`[ExerTimer] 跟踪已启动 | 错峰:${startupDelaySec}s | 周期:${trackingIntervalSec}s | 阈值:${inactiveThresholdSec}s`)
        startupDelayController = useTimeoutFn(() => {
            startReporting(trackingIntervalMs, onStatusUpdate)
        },
            startupDelayMs,
            { immediate: true }
        )
    }

    /**
     * 停止所有跟踪并清理资源
     *
     * 必须在用户离开练习页面时调用，防止后台误上报。
     */
    function stopTracking() {
        if (startupDelayController) {
            startupDelayController.stop()
            startupDelayController = null
        }

        if (trackingIntervalController) {
            trackingIntervalController.pause()
            trackingIntervalController = null
        }

        idleDetector = null
        isTrackingActive.value = false
    }

    // --- 返回 API ---
    return {
        isTrackingActive,
        startTracking,
        stopTracking,
    }
}

/**
 * 练习行为跟踪配置
*/
export interface ExerTrackingConfig {
    /**
     * 启动延迟（秒）：首次上报前的随机延迟，用于错峰，减轻服务器瞬时压力。
     * 建议：1~30 秒随机值，避免大量用户同时触发。
     * 若设为 <=0，则立即开始跟踪。
     */
    startupDelaySec: number

    /**
     * 跟踪周期（秒）：每隔多久采集并上报一次用户状态。
     * 示例：60 表示每分钟上报一次，用于累计有效学习时长。
     */
    trackingIntervalSec: number

    /**
     * 非活跃阈值（秒）：用户无任何交互超过该时间，判定为“非活跃”（挂机）。
     * 示例：300 秒（5 分钟），可用于过滤无效学习时间。
     */
    inactiveThresholdSec: number

    /**
     * 状态更新回调：在每次跟踪周期触发时调用。
     *
     * @param isIdle - 是否挂机状态
     *   - `true`: 用户长时间无操作，可能在“挂机”
     *   - `false`: 用户有操作，视为有效学习
     */
    onStatusUpdate: (isIdle: boolean) => void | Promise<void>
}