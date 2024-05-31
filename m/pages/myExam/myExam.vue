<template>
	<scroll-view scroll-y="true" class="my-exam">
		<uni-search-bar 
			v-model="queryForm.examName" 
			:focus="true"
			@confirm="query(false)"
			@cancel="queryForm.examName = ''; query(false)"
			 ></uni-search-bar>
		<uni-list>
			<uni-list-item 
				v-for="myExam in listpage.list" 
				:key="myExam.examId" 
				:title="`${ myExam.examName } / ${ myExam.examStartTime }`" 
				:note="`用时：${getMinute(myExam.examStartTime, myExam.examEndTime)}分钟 | 分数：${ myExam.totalScore || '-' } / ${ myExam.examTotalScore } | 排名：${ myExam.no || '-' } / ${ myExam.userNum || '-' } | ${ getDictName('EXAM_STATE', myExam.state) } | ${ getDictName('MARK_STATE', myExam.markState) }`"
				link 
				showArrow
				@click="toExam(myExam)" 
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
	import { MyExam } from '@/ts'
	import http from '@/utils/request'
	import { useDictStore } from "@/stores/dict"

	// 定义变量
	const dictStore = useDictStore()
	const queryForm = reactive({// 查询表单
		examName: '', // 考试名称
	})
	const listpage = reactive({// 分页列表
		curPage: 1,
		pageSize: 10,
		total: 0,
		list: [] as MyExam[],
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
		
		const { code, data } = await http.post('myExam/listpage', {
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

	// 去考试
	async function toExam(myExam : MyExam) {
		let { data } = await http.post("login/sysTime", {})
		let curTime = new Date(data.replaceAll("-", '/'))
		let examStartTime = new Date(myExam.examStartTime.replaceAll("-", '/'))
		if (examStartTime.getTime() > curTime.getTime()) {
			uni.showToast({ title: '考试未开始，请等待...', icon: 'none' })
			return
		}

		uni.navigateTo({ url: `/pages/myExam/myExamPage?examId=${myExam.examId}` });
	}
	
	function getMinute(startTime: string, endTime: string) {
		if (!startTime || !endTime) {
			return '-'
		}
		return Math.ceil((new Date(endTime.replaceAll('-', '/')).getTime() - new Date(startTime.replaceAll('-', '/')).getTime()) / 1000 / 60)
	}
	
	function getDictName(dictIndex: string, dictKey: number) {
		 return dictStore.getValue(dictIndex, dictKey)
	}
</script>

<style lang="scss" scoped>
	.my-exam {
		height: 100%;
	}
</style>