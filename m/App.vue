<script>
// #ifdef MP-WEIXIN
import { BASE_URL } from '@/static/config-wx';
// #endif

export default {
	onLaunch: function () {
		// #ifdef H5
		uni.request({
			url: '/static/config.js?t=' + new Date().getTime(),
			success: (res) => {
				let SERVER = eval(res.data);
				uni.setStorageSync('BASE_URL', SERVER.BASE_URL);
			},
			fail: (res) => {
				console.log('获取后台地址错误：' + res.errMsg);
			}
		});
		// #endif

		// #ifdef MP-WEIXIN
		uni.setStorageSync('BASE_URL', BASE_URL);
		// #endif
	},
	onShow: function () {},
	onHide: function () {}
};
</script>

<style>
@import '@/static/font/iconfont.css';

uni-page-body,
page {
	height: 100%;
	background: #f3f6f9;
}
:not(not) {
	box-sizing: border-box;
}
</style>
