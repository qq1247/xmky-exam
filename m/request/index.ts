import ajax from '@/uni_modules/u-ajax/js_sdk/index';
import { useUserStore } from '@/stores/user';

const userStore = useUserStore();
const instance = ajax.create({
	baseURL: uni.getStorageSync('BASE_URL'),
	timeout: 6000
});

const refreshInstance = ajax.create({
	baseURL: uni.getStorageSync('BASE_URL'),
	timeout: 6000
});

// 添加请求拦截器
instance.interceptors.request.use(
	(config) => {
		if (userStore.accessToken) {
			config.header['Authorization'] = userStore.accessToken;
		}
		config.header['content-type'] = 'application/x-www-form-urlencoded';
		return config;
	},
	(error) => {
		return Promise.reject(error);
	}
);
refreshInstance.interceptors.request.use(
	(config) => {
		config.header['content-type'] = 'application/x-www-form-urlencoded';
		return config;
	},
	(error) => {
		return Promise.reject(error);
	}
);

// 添加响应拦截器
let isRefreshing = false;
let refreshSubscribers: ((token: string) => void)[] = [];
function onAccessTokenFetched(accessToken: string) {
	refreshSubscribers.forEach((callback) => callback(accessToken));
	refreshSubscribers = [];
}
function addRefreshSubscriber(callback: (token: string) => void) {
	refreshSubscribers.push(callback);
}
instance.interceptors.response.use(
	(response) => {
		if (response.data.code !== 200) {
			uni.showToast({ title: response.data.msg, icon: 'error' }); // 成功静默，失败反馈
		}
		return response.data;
	},
	async (error) => {
		const originalRequest = error.config;
		if (error.statusCode === 401 && !originalRequest._retry) {
			if (!userStore.refreshToken) {
				userStore.reset();
				uni.redirectTo({ url: '/pages/login/login' });
				// uni.showToast({ title: '登录已过期，请重新登录..', icon: 'error' });
				return Promise.reject(error);
			}

			if (isRefreshing) {
				return new Promise((resolve) => {
					addRefreshSubscriber((accessToken: string) => {
						originalRequest.header['Authorization'] = accessToken;
						resolve(instance(originalRequest));
					});
				});
			}

			originalRequest._retry = true;
			isRefreshing = true;

			try {
				const response = await refreshInstance.post(`/login/refresh`, { refreshToken: userStore.refreshToken });
				if (response.data.code !== 200) throw response.data.msg;

				userStore.accessToken = response.data.data.accessToken;
				onAccessTokenFetched(userStore.accessToken);
				originalRequest.header['Authorization'] = userStore.accessToken;
				return instance(originalRequest);
			} catch (err) {
				console.log('未知异常', err);
				uni.showToast({ title: `${err}`, icon: 'error' });
				userStore.reset();
				uni.redirectTo({ url: '/pages/login/login' });
				return Promise.reject(err);
			} finally {
				isRefreshing = false;
			}
		}

		if (error.statusCode === 401 || error.statusCode === 403) {
			uni.showToast({ title: error.data.msg, icon: 'error' });
		} else if (error.code === 'ECONNABORTED') {
			uni.showToast({ title: `请求服务器超时：${error.config.timeout / 1000}秒`, icon: 'error' });
		} else if (error.code === 'ERR_NETWORK') {
			uni.showToast({ title: `连接服务器失败`, icon: 'error' });
		} else if (error.errMsg && error.errMsg.includes('request:fail')) {
			uni.showToast({ title: `连接服务器失败.`, icon: 'error' });
		} else {
			uni.showToast({ title: `未知错误：${error}`, icon: 'error' });
		}
		return Promise.reject(error);
	}
);

// 导出 create 创建后的实例
export default instance;
