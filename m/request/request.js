import ajax from '@/uni_modules/u-ajax/js_sdk/index';
import refreshToken from '@/request/refresh';
import { useUserStore } from '@/stores/user';

const userStore = useUserStore();
const instance = ajax.create({
	baseURL: uni.getStorageSync('BASE_URL'),
	timeout: 6000
});

// 添加请求拦截器
instance.interceptors.request.use(
	(config) => {
		// 请求前携带鉴权令牌
		if (userStore.user.accessToken) {
			config.header['Authorization'] = userStore.user.accessToken;
		}
		config.header['content-type'] = 'application/x-www-form-urlencoded';
		return config;
	},
	(error) => {
		uni.showToast({ title: `请求错误`, icon: 'error' });
		console.error(`请求错误：${response.data.msg} `);
		return Promise.reject(error);
	}
);

// 定义 pending 状态的 Promise，用于避免进入 catch 回调
const pending = new Promise(() => {});

// 添加响应拦截器
instance.interceptors.response.use(
	(response) => {
		// 如果服务器鉴权失败，重定向到首页
		if (response.data.code === 401) {
			// return refreshToken().then(() => instance(response.config));
			uni.showToast({ title: response.data.msg, icon: 'error' });
			
			let pages = getCurrentPages();
			uni.setStorageSync('redirectPath', pages[0].$page.fullPath);
			
			setTimeout(() => {
				uni.redirectTo({ url: '/pages/login/login' });
			}, 2000);
			return;
		}

		// 如果服务器异常，提示
		if (response.data.code === 500) {
			uni.showToast({ title: response.data.msg, icon: 'error' });
			console.error(`未知错误：${response.data.msg} `);
			return response.data;
		}

		// 如果服务器有刷新令牌，则替换
		if (response.header.authorization) {
			console.log(`刷新令牌：${response.header.authorization}`);
			const userStore = useUserStore();
			userStore.accessToken = response.header.authorization;
		}

		//返回响应数据
		return response.data;
	},
	(error) => {
		/**
		 * 在有返回错误的拦截器里返回 pending 状态的 Promise
		 *
		 * 那如果想要在请求方法接收响应错误是怎么办呢？
		 * 我们可以通过拦截器传值再做相应逻辑，
		 * 这里我用传值 catch 判断是否需要返回错误，如果是 true 返回错误信息，否则不返回。
		 */
		if (error.errMsg === 'request:fail') {
			uni.showToast({ title: '连接服务器失败', icon: 'error' });
			console.error(`连接服务器失败：${url}`);
		} else if (error.errMsg === 'request:fail timeout') {
			uni.showToast({ title: `响应超时`, icon: 'error' });
			console.error(`响应超时`);
		} else {
			uni.showToast({ title: `未知错误`, icon: 'error' });
			console.error(`未知错误：${error} `);
		}

		return error.config.catch ? Promise.reject(error) : pending;
	}
);

// 在 Ajax 实例上挂载 upload 方法
instance.upload = function (filePath, formData, callback) {
	return new Promise(async (resolve, reject) => {
		const url = await instance.getURL({
			url: 'upload'
		});

		const uploadTask = uni.uploadFile({
			url,
			filePath,
			name: 'file', // 如果第二个参数是 object 类型则作为 formData 使用
			formData: typeof formData === 'object' ? formData : {},
			complete: (res) => (res.statusCode === 200 ? resolve(res) : reject(res))
		});

		// 如果第二个参数是 function 类型则作为 uploadTask 的回调函数使用，并不管第三个参数了
		if (typeof formData === 'function') {
			formData(uploadTask);
		} else if (typeof callback === 'function') {
			callback(uploadTask);
		}
	});
};

// 导出 create 创建后的实例
export default instance;
