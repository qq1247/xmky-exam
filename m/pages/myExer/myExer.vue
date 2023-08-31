<template>
	<scroll-view scroll-y="true" class="my-exer">
		<uni-search-bar 
			v-model="queryForm.name" 
			:focus="true" 
			@confirm="query(false)"
			@cancel="queryForm.name = ''; query(false)"
			></uni-search-bar>
		<uni-list>
			<uni-list-item 
				v-for="myExer in listpage.list" 
				:key="myExer.id" 
				:title="myExer.name" 
				:note="`${ myExer.startTime } - ${ myExer.endTime }`"
				link
				showArrow 
				@click="toExer(myExer)" 
				></uni-list-item>
		</uni-list>
		<uni-load-more 
			:status="listpage.status"
			:contentText="{contentdown: '点击查看更多', contentrefresh: '加载中', contentnomore: '到底了' }"
			@clickLoadMore="query(true)"
			></uni-load-more>
	</scroll-view>
</template>

<script lang="ts" setup>
	import { reactive, ref } from "vue";
	import { onLoad } from "@dcloudio/uni-app"
	import { MyExer } from '@/ts'
	import http from '@/utils/request'

	// 定义变量
	const queryForm = reactive({// 查询表单
		name: '', // 名称
	})
	const listpage = reactive({// 分页列表
		curPage: 1,
		pageSize: 10,
		total: 0,
		list: [] as MyExer[],
		status: 'more'
	})

	// 页面加载完毕，执行如下方法
	onLoad(async() => {
		query(false)
	})

/**
	 * 查询
	 * 
	 * @param append 追加数据
	 */
	async function query(append: boolean) {
		listpage.status = 'loading'
		if (append) {
			listpage.curPage++
		} else {
			listpage.curPage = 1
		}
		
		const { code, data } = await http.post('myExer/listpage', {
			...queryForm,
			curPage: listpage.curPage,
			pageSize: listpage.pageSize,
		})
		
		if (code !== 200) {
			return
		}
		
		listpage.total = data.total
		
		if (append) {
			if (data.list.length) {
				listpage.list.push(...data.list)
			}
		} else {
			listpage.list = data.list
		}
		
		if (listpage.list.length < listpage.total) {
			listpage.status = 'more'
		} else {
			listpage.status = 'no-more'
		}
	}

	// 去练习
	async function toExer(exer: MyExer) {
		let { data } = await http.post("login/sysTime", {  })
		let curTime = new Date(data.replaceAll('-', '/'))
		let exerStartTime = new Date(exer.startTime.replaceAll('-', '/'))
		if (exerStartTime.getTime() > curTime.getTime()) {
			uni.showToast({ title: '练习未开始，请等待...', icon: 'none'})
			return
		}

		let exerEndTime = new Date(exer.endTime.replaceAll('-', '/'))
		if (curTime.getTime() > exerEndTime.getTime()) {
			uni.showToast({ title: '练习已结束...', icon: 'none'})
			return
		}

		uni.navigateTo({url: `/pages/myExer/myExerPage?exerId=${exer.id}`});
	}
</script>

<style lang="scss" scoped>
	.my-exer {
		height: 100%;
	}
</style>
