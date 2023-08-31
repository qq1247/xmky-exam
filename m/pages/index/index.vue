<template>
	<view class="home">
		<cc-noticeBar 
			:noticeList="bulletinList" 
			class="home-bulletin"
			@click="toBulletinDetail"></cc-noticeBar>
		<uni-card class="home-top" margin="0rpx">
			<uni-grid :column="4" :show-border="false">
				<uni-grid-item @click="toExamList">
					<view class="home-top-menu">
						<uni-icons custom-prefix="iconfont" type="icon-diannao" size="30" color="$uni-info"></uni-icons>
						<text class="home-top-menu-text">参与考试</text>
						<view class="home-top-menu-num">
							<uni-badge :text="`${ statis.examNum }场`" type="primary" />
						</view>
					</view>
				</uni-grid-item>
				<uni-grid-item @click="toExexList">
					<view class="home-top-menu">
						<uni-icons custom-prefix="iconfont" type="icon-shiti" size="30" color="$uni-info"></uni-icons>
						<text class="home-top-menu-text">参与练习</text>
						<view class="home-top-menu-num">
							<uni-badge :text="`${ statis.exerNum }场`" type="success" />
						</view>
					</view>
				</uni-grid-item>
				<uni-grid-item @click="toExamList">
					<view class="home-top-menu">
						<uni-icons custom-prefix="iconfont" type="icon-mark-paper" size="30" color="$uni-info"></uni-icons>
						<text class="home-top-menu-text">及格次数</text>
						<view class="home-top-menu-num">
							<uni-badge :text="`${ statis.passExamNum }次`" type="warning" />
						</view>
					</view>
				</uni-grid-item>
				<uni-grid-item @click="toExamList">
					<view class="home-top-menu">
						<uni-icons custom-prefix="iconfont" type="icon-ai-users" size="30" color="$uni-info"></uni-icons>
						<text class="home-top-menu-text">最高排名</text>
						<view class="home-top-menu-num">
							<uni-badge :text="`${ statis.topRank }名`" type="error" />
						</view>
					</view>
				</uni-grid-item>
			</uni-grid>
		</uni-card>
		<uni-card class="home-bottom" margin="0rpx" padding="0rpx">
			<uni-section title="考试任务" type="line">
				<scroll-view scroll-y="true" class="home-bottom-exam">
					<uni-list v-if="todoExamList.length" v-for="(todoExam, index) in todoExamList" :key="index" :border="true" @click="toExam(todoExam)">
						<uni-list-item :title="todoExam.examName" :note="`${ todoExam.examStartTime } - ${ todoExam.examEndTime }`" showArrow rightText="" />
					</uni-list>
					<kevy-empty v-else :show="true" type="list" text="暂无数据" :imageSize="200"></kevy-empty>
				</scroll-view>
			</uni-section>
			<uni-section title="练习任务" type="line">
				<scroll-view scroll-y="true" class="home-bottom-exam">
					<uni-list v-if="todoExerList.length" v-for="(todoExer, index) in todoExerList" :key="index" :border="true" @click="toExer(todoExer)">
						<uni-list-item :title="todoExer.name" :note="`${ todoExer.startTime } - ${ todoExer.endTime }`" showArrow rightText="" />
					</uni-list>
					<kevy-empty v-else :show="true" type="list" text="暂无数据" :imageSize="200"></kevy-empty>
				</scroll-view>
			</uni-section>
		</uni-card>
	</view>
</template>

<script lang="ts" setup>
	import { ref, reactive } from 'vue'
	import { useUserStore } from "@/stores/user"
	import { onLoad, onPullDownRefresh } from '@dcloudio/uni-app'
	import http from '@/utils/request.js'
	import { Bulletin, MyExam, MyExer } from '@/ts';

	// 属性定义
	const userStore = useUserStore()
	const statis = reactive({// 首页统计
	    examNum: 0,// 考试数量
	    exerNum: 0,// 练习数量
	    passExamNum: 0,// 及格次数
	    topRank: 0,// 最高排名
	})
	const todoExamList = ref([] as MyExam[])// 未完成考试列表
	const todoExerList = ref([] as MyExer[])// 未完成练习列表
	const bulletinList = ref([] as Bulletin[])// 公告列表
	
	// 页面加载完毕，执行如下方法
	onLoad(async () => {
		if (!userStore.accessToken) {
			uni.redirectTo({ url: '/pages/login/login' })
			return
		}
		
		statisQuery()
		myExamQuery()
		myExerQuery()
		bulletinQuery()
	})

	onPullDownRefresh(() => {
		statisQuery()
		myExamQuery()
		myExerQuery()
		bulletinQuery()
		uni.stopPullDownRefresh();
	})
	
	// 统计查询
	async function statisQuery() {
		let { data } = await http.post("report/user/home", {  })// 首页统计
		statis.examNum = data.examNum
		statis.exerNum = data.exerNum
		statis.passExamNum = data.passExamNum
		statis.topRank = data.topRank
	}
	
	// 我的考试查询
	async function myExamQuery() {
		let { data } = await http.post("myExam/listpage", { todo: true, pageSize: 100 })// 未完成的考试列表
		todoExamList.value = data.list
	}
	
	// 我的练习查询
	async function myExerQuery() {
		let { data } = await http.post("myExer/listpage", { todo: true, pageSize: 100 })// 未完成的练习列表
		todoExerList.value = data.list
	}
	
	// 公告查询
	async function bulletinQuery() {
		let { data } = await http.post('bulletin/listpage', { pageSize: 100 })// 公告查询
		bulletinList.value.push(...data.list)
		if (!bulletinList.value.length) {
			bulletinList.value.push({
				id : 0,
				title : '暂无公告',
				content : '暂无公告',
				startTime: null,
				endTime: null,
			})
		}
	}
	
	// 去考试
	async function toExam(myExam: any) {
		let examStartTime = new Date(myExam.examStartTime.replaceAll("-", '/'))
		let { data } = await http.post("login/sysTime", {  })
		let curTime = new Date(data.replaceAll("-", '/'))
		if (curTime.getTime() < examStartTime.getTime()) {
			uni.showToast({ title: '考试未开始，请等待...', icon: 'none'})
			return
		}
	
		uni.navigateTo({url: `/pages/myExam/myExamPage?examId=${myExam.examId}`});
	}
	
	// 去考试列表
	async function toExamList() {
		uni.switchTab({
			url: '/pages/myExam/myExam'
		})
	}
	// 去练习列表
	async function toExexList() {
		uni.switchTab({
			url: '/pages/myExer/myExer'
		})
	}
	
	// 去练习
	async function toExer(myExer: any) {
	    let startTime = new Date(myExer.startTime.replaceAll("-", '/'))
	    let { data } = await http.post("login/sysTime", {  })
	    let curTime = new Date(data.replaceAll("-", '/'))
	    if (curTime.getTime() < startTime.getTime()) {
	        uni.showToast({ title: '练习未开始，请等待...', icon: 'none'})
	        return
	    }
	
	    uni.navigateTo({url: `/pages/myExer/myExerPage?exerId=${ myExer.id }`});
	}
	
	// 去查看公告
	function toBulletinDetail(bulletin: Bulletin) {
		if (bulletin.id) {// 打印触发两次，等作者修复
			uni.navigateTo({url: `/pages/bulletin/bulletinDetail?id=${ bulletin.id }`})
		}
	}
</script>

<style lang="scss" scoped>
	.home {
		display: flex;
		flex-direction: column;
		height: 100%;
		.home-bulletin {
			background-color: white;
			margin: 5rpx 0rpx;
		}
		.home-top {
			height: 180rpx;
			flex: inherit;

			.home-top-menu {
				flex: 1;
				/* #ifndef APP-NVUE */
				display: flex;
				/* #endif */
				flex-direction: column;
				align-items: center;
				justify-content: center;
				padding: 30rpx 0;

				.home-top-menu-image {
					width: 50rpx;
					height: 50rpx;
				}

				.home-top-menu-text {
					font-size: 28rpx;
					margin-top: 10rpx;
				}

				.home-top-menu-num {
					position: absolute;
					top: 10rpx;
					right: 30rpx;
				}
			}
		}

		.home-bottom {
			flex: 1;
			display: flex;
			flex-direction: column;
			.home-bottom-exam {
				height: calc((100vh - 700rpx) / 2);
			}
			.home-bottom-exer {
				height: calc((100vh - 700rpx) / 2);
			}
			
			.chat-custom-right {
				flex: 1;
				/* #ifndef APP-NVUE */
				display: flex;
				/* #endif */
				flex-direction: column;
				justify-content: space-between;
				align-items: flex-end;
			}
			
			.chat-custom-text {
				font-size: 24rpx;
				color: #999;
			}
		}
	}
</style>