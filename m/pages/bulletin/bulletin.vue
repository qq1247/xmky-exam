<template>
	<scroll-view scroll-y="true" class="bulletin">
		<uni-search-bar 
			v-model="queryForm.title" 
			:focus="true" 
			@confirm="query(false)"
			@cancel="queryForm.title = ''; query(false)"
			></uni-search-bar>
		<uni-list>
			<uni-list-item 
				v-for="bulletin in listpage.list" 
				:key="bulletin.id" 
				:title="bulletin.title" 
				link
				showArrow 
				:to="`/pages/bulletin/bulletinDetail?id=${ bulletin.id }`"
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
	import { reactive } from 'vue'
	import { onLoad } from "@dcloudio/uni-app"
	import { Bulletin } from '@/ts'
	import http from '@/utils/request.js'

	// 定义变量
	const queryForm = reactive({// 查询表单
	    title: '',// 标题
	})
	const listpage = reactive({// 分页列表
		curPage: 1,
		pageSize: 10,
		total: 0,
		list: [] as Bulletin[],
		status: 'more'
	})

	// 页面加载完毕，执行如下方法
	onLoad(async () => {
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
		
		const { code, data } = await http.post('bulletin/listpage', {
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
</script>

<style lang="scss" scoped>
	.bulletin {
		height: 100%;
	}
</style>