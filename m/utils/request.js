import { useUserStore } from "@/stores/user"

const userStore = useUserStore()
function request(option) {
	let BASE_URL = uni.getStorageSync('BASE_URL')
	let url = /^https?/i.test(option.url) ? option.url : (BASE_URL + '/' + option.url) 
	let method = option.method || 'POST'
	let data = option.params
	let header = { 
		Authorization: userStore.accessToken || '',
		'content-type': 'application/x-www-form-urlencoded'
	}
	let timeout = option.timeout || 6000
	// uni.showLoading({ title: '加载中' })
	return new Promise((resolve, reject) => {
		uni.request({
			url,
			method,
			header,
			data,
			timeout,
			success(res) {
				resolve(res.data)
				
				if (res.data.code === 401) {// 鉴权失败，跳转到登录页
					uni.showToast({ title: res.data.msg, icon: 'error' })
					setTimeout(() => {
						uni.redirectTo({ url: '/pages/login/login' })
					}, 2000)
					return
				}
				
				if (res.data.code === 500) {// 接口错误，提示错误
					uni.showToast({ title: res.data.msg, icon: 'error' })
					return
				}
			},
			fail(err) {
				reject(err)
				
				if (err.errMsg === 'request:fail') {
					uni.showToast({ title: '连接服务器失败', icon: 'error' })
					console.error(`连接服务器失败：${url}`)
					return
				}
				if (err.errMsg === 'request:fail timeout') {
					uni.showToast({ title: `请求超时：${ timeout / 1000 }秒`, icon: 'error' })
					console.error(`请求超时：${ timeout / 1000 }秒 ${url}`)
					return
				}
				
				uni.showToast({ title: `未知错误`, icon: 'error' })
				console.error(`未知错误：${ error } `)
			},
			complete() {
				//uni.hideLoading()
			}
		})
	})
}

export default {
	post: (url, params, config) => request({
		url,
		params,
		...config,
	})
}
