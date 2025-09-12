// composables/xmky-idle-track.ts
import { onScopeDispose, ref } from 'vue';

/**
 * 用户挂机跟踪器
 *
 * 用于在用户进行任何需要活跃度统计的场景（如练习、考试、观看视频等）中，
 * 自动采集用户活跃状态，周期上报，过滤挂机行为。
 * 支持错峰启动、挂机检测、资源自动清理。
 */
export function useIdleTrack() {
	// --- 状态 ---
	const isTrackingActive = ref(false);
	const isIdle = ref(true); // 默认挂机

	// --- 内部控制器 ---
	let startupDelayTimer: number | null = null;
	let trackingIntervalTimer: number | null = null;
	let lastActiveTime = Date.now();
	let configRef: IdleTrackingConfig | null = null;

	// --- 活跃事件监听器（跨平台） ---
	let eventCleanupFns: (() => void)[] = [];

	/**
	 * 更新最后活跃时间
	 */
	function updateLastActive() {
		lastActiveTime = Date.now();
		isIdle.value = false;
	}

	/**
	 * 启动活跃事件监听（跨平台）
	 */
	function startListeningEvents() {
		eventCleanupFns = [];

		// ===== H5 平台 =====
		if (process.env.UNI_PLATFORM === 'h5') {
			const events = ['mousedown', 'mousemove', 'keydown', 'scroll', 'touchstart'];
			const handler = () => updateLastActive();
			events.forEach((event) => {
				window.addEventListener(event, handler, { passive: true });
				eventCleanupFns.push(() => window.removeEventListener(event, handler));
			});
		}

		// ===== 小程序 & App 平台 =====
		else {
			// 监听页面 show（从后台回到前台）
			const onPageShow = () => updateLastActive();
			uni.onAppShow(onPageShow);
			eventCleanupFns.push(() => uni.offAppShow(onPageShow));

			// 监听页面 hide（切到后台）
			// 不做操作，仅靠超时判断

			// 监听全局点击（需配合页面内元素绑定）
			// 注意：小程序无法监听全局点击，需手动在根 view 上绑定 @touchstart
			// 建议：在 App.vue 或主页面的最外层 <view @touchstart="onUserActive">...</view>
			// 此处不自动绑定，由开发者在页面中手动触发 updateLastActive
			// 你可以在页面中这样写：
			// <view @touchstart="idleTracker?.updateLastActive" @click="idleTracker?.updateLastActive">
			// 提供手动触发接口
		}
	}

	/**
	 * 停止活跃事件监听
	 */
	function stopListeningEvents() {
		eventCleanupFns.forEach((fn) => fn());
		eventCleanupFns = [];
	}

	/**
	 * 启动周期性状态跟踪
	 */
	function startReporting(intervalMs: number, onStatusUpdate: IdleTrackingConfig['onStatusUpdate']) {
		trackingIntervalTimer = setInterval(async () => {
			const now = Date.now();
			const inactiveTime = now - lastActiveTime;
			const threshold = configRef?.inactiveThresholdSec * 1000 || 300 * 1000;
			isIdle.value = inactiveTime > threshold;

			await onStatusUpdate(isIdle.value);
		}, intervalMs) as unknown as number;
	}

	/**
	 * 开始用户活跃行为跟踪
	 */
	function startTracking(config: IdleTrackingConfig) {
		if (isTrackingActive.value) {
			console.warn('[IdleTrack] 跟踪已在进行中，停止上一个实例');
			stopTracking();
		}

		configRef = config;
		const { startupDelaySec, trackingIntervalSec, inactiveThresholdSec, onStatusUpdate } = config;

		const startupDelayMs = Math.max(0, startupDelaySec * 1000);
		const trackingIntervalMs = trackingIntervalSec * 1000;

		isTrackingActive.value = true;

		// 启动活跃事件监听
		startListeningEvents();

		// 启动错峰延迟
		console.log(`[IdleTrack] 跟踪已启动 | 错峰:${startupDelaySec}s | 周期:${trackingIntervalSec}s | 阈值:${inactiveThresholdSec}s`)
		startupDelayTimer = setTimeout(() => {
			startReporting(trackingIntervalMs, onStatusUpdate);
		}, startupDelayMs) as unknown as number;
	}

	/**
	 * 停止所有跟踪并清理资源
	 */
	function stopTracking() {
		if (startupDelayTimer !== null) {
			clearTimeout(startupDelayTimer);
			startupDelayTimer = null;
		}

		if (trackingIntervalTimer !== null) {
			clearInterval(trackingIntervalTimer);
			trackingIntervalTimer = null;
		}

		stopListeningEvents();
		isTrackingActive.value = false;
		configRef = null;
	}

	// 暴露给页面手动触发活跃（用于小程序/APP 全局点击绑定）
	function updateLastActiveManually() {
		updateLastActive();
	}

	onScopeDispose(() => {
		stopTracking();
	});

	// --- 返回 API ---
	return {
		isTrackingActive,
		startTracking,
		updateLastActive: updateLastActiveManually // 手动触发活跃（重要！用于小程序）
	};
}

/**
 * 挂机跟踪配置
 */
export interface IdleTrackingConfig {
	/**
	 * 启动延迟（秒）：首次上报前的随机延迟，用于错峰，减轻服务器瞬时压力。
	 * 建议：1~30 秒随机值，避免大量用户同时触发。
	 * 若设为 <=0，则立即开始跟踪。
	 */
	startupDelaySec: number;

	/**
	 * 跟踪周期（秒）：每隔多久采集并上报一次用户状态。
	 * 示例：60 表示每分钟上报一次，用于累计有效学习时长。
	 */
	trackingIntervalSec: number;

	/**
	 * 非活跃阈值（秒）：用户无任何交互超过该时间，判定为“非活跃”（挂机）。
	 * 示例：300 秒（5 分钟），可用于过滤无效学习时间。
	 */
	inactiveThresholdSec: number;

	/**
	 * 用户活跃状态上报回调：在每次跟踪周期触发时调用。
	 *
	 * @param isIdle - 是否挂机状态
	 *   - `true`: 用户长时间无操作，可能在“挂机”
	 *   - `false`: 用户有操作，视为有效学习
	 */
	onStatusUpdate: (isIdle: boolean) => void | Promise<void>;
}
