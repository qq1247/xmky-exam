module.exports = (vm) => {
	uni.$u.http.setConfig((config) => {
		config.baseURL = getApp().globalData.baseUrl; /* 根域名 */
		config.header = {
			'Content-Type': 'application/x-www-form-urlencoded'
		};
		config.showLoading = true; // 是否显示请求中的loading
		config.loadingText = '努力加载中~'; // 请求loading中的文字提示
		config.loadingTime = 800; // 在此时间内，请求还没回来的话，就显示加载中动画，单位ms
		config.originalData = false; // 是否在拦截器中返回服务端的原始数据
		config.loadingMask = true; // 展示loading的时候，是否给一个透明的蒙层，防止触摸穿透
		return config
	})

	// 请求拦截
	uni.$u.http.interceptors.request.use((config) => { // 可使用async await 做异步操作
		// 初始化请求拦截器时，会执行此方法，此时data为undefined，赋予默认{}
		config.data = config.data || {}
		// 根据custom参数中配置的是否需要token，添加对应的请求头
		if (config?.custom?.auth) {
			// 可以在此通过vm引用vuex中的变量，具体值在vm.$store.state中
			config.header.Authorization = uni.getStorageSync('accessToken')
		}
		return config
	}, config => { // 可使用async await 做异步操作
		return Promise.reject(config)
	})
	
	// 响应拦截
	uni.$u.http.interceptors.response.use((response) => {
		/* 对响应成功做点什么 可使用async await 做异步操作*/
		const data = response.data
		// 自定义参数
		const custom = response.config?.custom
		if (data.code !== 200) {
			
			if(data.code === 401) {
				uni.$u.toast('请您重新登录！')
				uni.clearStorageSync();
				setTimeout(() => {
					uni.navigateTo({
						url: '/pages/login/login'
					})
				}, 1000);
				return false;
			}
			
			// 如果没有显式定义custom的toast参数为false的话，默认对报错进行toast弹出提示
			if (custom.toast !== false) {
				uni.$u.toast(data.msg)
			}

			// 如果需要catch返回，则进行reject
			if (custom?.catch) {
				return Promise.reject(data)
			} else {
				// 否则返回一个pending中的promise，请求不会进入catch中
				return new Promise(() => {})
			}
		}
		return data
	}, (response) => {
		// 对响应错误做点什么 （statusCode !== 200）
		return Promise.reject(response)
	})
}
