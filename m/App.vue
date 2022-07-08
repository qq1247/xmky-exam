<script>
	export default {
		globalData: {
			//#ifdef H5
			baseUrl: process.env.NODE_ENV === 'development' ? '/api' : window.server.BASE_URL,
			entName: ''
			//#endif
		},
		onLaunch: function() {
			console.log('App Launch')
		},
		onShow: function() {
			console.log('App Show')
		},
		onHide: function() {
			console.log('App Hide')
		}
	}
</script>

<style lang="scss">
	.container {
		position: relative;
		background: #f7f8f9;
		width: 750rpx;
		min-height: 100vh;
		display: flex;
		flex-direction: column;
		align-items: center;
		box-sizing: border-box;
	}
	/*每个页面公共css */
	@import "uview-ui/index.scss";
</style>
