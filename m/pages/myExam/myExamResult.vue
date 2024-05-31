<template>
	<kevy-result-page
		v-if="type" 
		:type="type" 
		:title="title" 
		:description="description" 
		:details="details"
		primaryBtnText="返回首页" 
		primarColor="#2979ff"
		@primaryBtnClick="toHome"
		></kevy-result-page>
</template>

<script lang="ts" setup>
	import { ref, reactive, computed } from 'vue'
	import { onLoad } from "@dcloudio/uni-app"	
	import { useDictStore } from "@/stores/dict"
	import http from '@/utils/request'
	
	// 定义变量
	const type = ref('success')
	const title = ref('考试结束')
	const description = ref('正在查询中')
	const details = ref([])
	const dictStore = useDictStore()
	const exam = reactive({// 考试信息
		id: 0, // 考试ID
		name: '',// 试卷名称
		color: '', // 倒计时颜色
		markState: 0, // 阅卷状态
		scoreState: 0, // 分数状态
		rankState: 0,// 排名状态
		markType: 0,// 阅卷类型
		markEndTime: null,// 阅卷结束时间
	}) 
	const examEndTime = ref()// 考试结束时间（用reactive必须new Date()，会造成问题）
	const myExam = reactive({// 我的考试信息
		totalScore: 0, //总分
		examStartTime: new Date(),// 答题开始时间
		answerEndTime: new Date(),// 答题结束时间
		markStartTime: new Date(),// 阅卷开始时间
		markEndTime: new Date(),// 阅卷结束时间
		state: 0, // 考试状态
		markState: 0, // 阅卷状态
		answerState: 0, // 答题状态
		no: 0, // 用户排名
		userNum: 0, // 用户数量
	})
	
	// 页面加载完毕，执行如下方法
	onLoad(async (option) => {
		// 查询我的考试信息
		let counter = 0
		let queryTime = false
		var timer = setInterval(async() => {
			counter++
			
			exam.id = option.examId
			let { data: _exam } = await http.post("myExam/get", { examId: exam.id })
			exam.name = _exam.examName
			exam.markState = _exam.examMarkState
			exam.scoreState = _exam.examScoreState
			exam.rankState = _exam.examRankState
			examEndTime.value = new Date(_exam.examEndTime.replaceAll("-", '/'))
			exam.markEndTime = _exam.examMarkEndTime

			uni.setNavigationBarTitle({
				title: exam.name
			});

			myExam.totalScore = _exam.totalScore
			myExam.examStartTime = _exam.examStartTime ? new Date(_exam.examStartTime.replaceAll("-", '/')) : null
			myExam.answerEndTime = _exam.answerEndTime ? new Date(_exam.answerEndTime.replaceAll("-", '/')) : null
			myExam.markStartTime = _exam.markStartTime ? new Date(_exam.markStartTime.replaceAll("-", '/')) : null
			myExam.markEndTime = _exam.markEndTime ? new Date(_exam.markEndTime.replaceAll("-", '/')) : null
			myExam.state = _exam.state
			myExam.markState = _exam.markState
			myExam.answerState = _exam.answerState
			myExam.no = _exam.no
			myExam.userNum = _exam.userNum
			
			// 如果本次考试是不公布，提示消息并返回
			if (exam.scoreState === 2) {// 1：考试结束后；2：不公布；3：交卷后
				description.value = '本次考试不公布'
				clearInterval(timer);
				return
			}
			
			// 如果本次考试是交卷后公布
			if (exam.scoreState === 3) {
				// 但试卷是主观题试卷，提示消息并返回
				if (exam.markEndTime) {
					description.value = `主观题未阅，请于${exam.markEndTime}后查询`
					clearInterval(timer);
					return
				}
				
				// 如果查询5次还没查询到，提示消息并返回
				if (counter >= 5) {
					description.value = `查询失败，请稍后再次查询`
					clearInterval(timer);
					return
				}
				
				// 如果没查询到，等待下一次查询
				if (myExam.totalScore == null) {
					description.value = '正在查询中。。。'
					return
				}
				
				// 如果查询到，显示消息
				clearInterval(timer);
				description.value = '查询成功'
				details.value = []
				details.value.push({
					label: '考试名称',
					value: exam.name,
					bold: true,
				})
				details.value.push({
					label: '用时：',
					value: myExam.state === 3 ? timeDiff(myExam.examStartTime, myExam.answerEndTime) : '-',
					bold: true,
				})
				details.value.push({
					label: '得分：',
					value: scoreShow.value ? myExam.totalScore : '-',
					bold: true,
				})
				details.value.push({
					label: '成绩：',
					value: scoreShow.value ? dictStore.getValue('ANSWER_STATE', myExam.answerState) : '-',
					bold: true,
				})
				details.value.push({
					label: '排名',
					value: rankShow.value ? `${myExam.no || '-'} / ${myExam.userNum}` : `-`,
					bold: true,
				})
			}
			
			// 如果本次考试是考试结束后公布
			if (exam.scoreState === 1) {
				// 如果距离考试结束大于3秒，提示消息并返回
				// if (!queryTime) {
				// 	queryTime = true
				// 	let { data } = await http.post("login/sysTime", {  })
				// 	let curTime = new Date(data.replaceAll('-', '/'))
				// 	let remainSecond = ((examEndTime.value as Date).getTime() - curTime.getTime()) / 1000
					
				// 	if (remainSecond > 3) {
				// 		description.value = '整场考试未结束，请稍后查询'
				// 		clearInterval(timer);
				// 		return
				// 	}
				// }
				
				// 如果查询8次还没查询到，提示消息并返回
				if (counter >= 8) {
					description.value = `查询失败，请稍后再次查询`
					clearInterval(timer);
					return
				}
				
				// 如果没查询到，等待下一次查询
				if (myExam.totalScore == null) {
					description.value = '正在查询中。。。'
					return
				}
				
				// 如果查询到，显示消息
				clearInterval(timer);
				description.value = '查询成功'
				details.value.push({
					label: '考试名称',
					value: exam.name,
					bold: true,
				})
				details.value.push({
					label: '用时：',
					value: myExam.state === 3 ? timeDiff(myExam.examStartTime, myExam.answerEndTime) : '-',
					bold: true,
				})
				details.value.push({
					label: '得分：',
					value: scoreShow.value ? myExam.totalScore : '-',
					bold: true,
				})
				details.value.push({
					label: '成绩：',
					value: scoreShow.value ? dictStore.getValue('ANSWER_STATE', myExam.answerState) : '-',
					bold: true,
				})
				details.value.push({
					label: '排名',
					value: rankShow.value ? `${myExam.no || '-'} / ${myExam.userNum}` : `-`,
					bold: true,
				})
			}
		}, 1500);
	})
	
	// 返回首页
	function toHome() {
		uni.switchTab({
			url: '/'
		})
	}
	
	// 返回上一页
	function toBack() {
		uni.navigateBack()
	}
	
	// 计算属性
	const scoreShow = computed(() => {// 分数显示（后端已经数据过滤掉，这里控制只是好理解）
		return (exam.scoreState == 1 && exam.markState == 3) // 如果是考试结束后显示成绩，需要等到考试结束
			|| (exam.scoreState == 3 && myExam.markState == 3);// 如果是交卷后显示成绩，需要等到该试卷阅卷完成。比如主观题没阅卷，得不到总分，得不到是否及格
	})
	const rankShow = computed(() => exam.rankState === 1 )
	
	// 时间差值
	function timeDiff(startTime: Date, endTime: Date) {
		let h = Math.floor((endTime.getTime() - startTime.getTime()) / 1000 / 60 / 60)
		let m = Math.floor(((endTime.getTime() - startTime.getTime()) / 1000 / 60) % 60)
		let s = Math.floor(((endTime.getTime() - startTime.getTime()) / 1000) % 60)
		return `${('0' + h).slice(-2)}:${('0' + m).slice(-2)}:${('0' + s).slice(-2)}`
	}
	
	// 返回
	function back() {
		uni.switchTab({ url:'/pages/index/index' })
	}
</script>

<style lang="scss" scoped>
	:deep(.my-exam-result) {
		.uni-card__content {
			display: flex;

			.my-exam-result-left {
				width: 450rpx;
				display: flex;
				flex-direction: column;
			}

			.my-exam-result-right {}
		}
	}
</style>