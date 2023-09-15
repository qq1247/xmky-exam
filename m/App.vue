<script lang="ts" setup>
	import { onLaunch, onShow, onHide } from '@dcloudio/uni-app'

	onLaunch(() => {
		uni.request({
			url: '/static/config.js?t=' + new Date().getTime(),
		}).then((res: any) => {
			let SERVER = eval(res.data)
			uni.setStorageSync('BASE_URL', SERVER.BASE_URL)
		}).catch(err => {
			console.log(err)
		})
	})

	onShow(() => {

	})

	onHide(() => {

	})
</script>
<style>
	@import "@/static/iconfont.css";
</style>
<style lang="scss">
	@import '@/uni_modules/uni-scss/index.scss';

	/* #ifndef APP-NVUE */
	page {
		background-color: #f5f5f5;
	}

	uni-page-body {
		height: 100%;
	}

	/* #endif */
</style>