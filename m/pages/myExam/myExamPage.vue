<template>
	<uni-card class="my-exam-page" :is-full="true" spacing="0">
		<swiper :current="swiperIndex" :circular="true" @change="(e: any) => synIndex(e.detail.current, e.detail.source) ">
			<swiper-item v-for="(examQuestion, index) in swiperQuestions" :key="index">
				<view class="swiper-item">
					<view v-if="examQuestion.type === 1" class="my-exam-page-chapter-name">
						<text>{{ examQuestion.chapterName }}</text>
					</view>
					<view v-if="examQuestion.type === 1" class="my-exam-page-chapter-txt">
						<text>{{ examQuestion.chapterTxt }}</text>
					</view>
					<xm-question
						v-if="examQuestion.type === 2"
						v-model="examQuestion.userAnswers"
						:no="`${examQuestion.no} / ${ questionNum }`"
						:type="examQuestion.questionType"
						:markType="examQuestion.markType" 
						:title="examQuestion.title" 
						:score="examQuestion.score"
						:answers="examQuestion.answers"
						:userScore="examQuestion.userScore"
						:options="examQuestion.options"
						:editable="isAnswer"
						:errMark="scoreShow"
						@change="(answers: string[]) => answerUpdate(examQuestion, answers)"
					></xm-question>
				</view>
			</swiper-item>
		</swiper>
		<template #title>
			<view class="my-exam-page-head">
				<XmCountDown 
					v-if="isAnswer"
					:expireTime="examEndTime" 
					preTxt="距结束：" 
					:remind="300"
					@end="finish1" 
					@remind="exam.color='red'" 
					:color="exam.color"
					></XmCountDown>
			</view>
		</template>
		<template #actions>
			<view @click="answerSheet.open()">
				<uni-icons custom-prefix="iconfont" type="icon-datika"></uni-icons>
				<text>答题卡</text>
			</view>
			<view @click="markQuesiton">
				<uni-icons 
					custom-prefix="iconfont" 
					type="icon-biaoji" 
					:color="markQuestions.includes(curQuestionIndex) ? '#e43d33' : ''"
				></uni-icons>
				<text>标记</text> 
			</view>
			<view @click="pre">
				<uni-icons custom-prefix="iconfont" type="icon-arrow-left"></uni-icons>
				<text>上一题</text>
			</view>
			<view @click="next">
				<uni-icons custom-prefix="iconfont" type="icon-arrow-right"></uni-icons>
				<text>下一题</text>
			</view>
			<view v-if="isAnswer" @click="finish">
				<uni-icons custom-prefix="iconfont" type="icon-duigoux"></uni-icons>
				<text>交卷</text>
			</view>
		</template>
		<uni-popup ref="answerSheet" type="share" safeArea backgroundColor="#fff">
			<uni-card title="答题卡" :isFull="true" sub-title="" extra="" spacing="0" class="my-exam-page-card">
				<uni-grid :column="10" :highlight="true" @change="" class="my-exam-page-card-tips">
					<uni-grid-item class="my-exam-page-card-todo"></uni-grid-item>
					<text>未答</text>
					<uni-grid-item class="my-exam-page-card-do"></uni-grid-item>
					<text>已答</text>
					<uni-grid-item class="my-exam-page-card-mark"></uni-grid-item>
					<text>标记</text>
				</uni-grid>
				<scroll-view scroll-y="true" style="height: 40vh;">
					<uni-grid :column="10" :highlight="true" @change="">
						<template v-for="(examQuestion, index) in examQuestions" :index="index" :key="index" >
							<text v-if="examQuestion.type === 1" @click="curQuestionIndex = index" class="my-exam-page-card-chapter">{{ examQuestion.chapterName }}</text>
							<uni-grid-item
								v-else
								@click="curQuestionIndex = index"
								:class="{ 
									'my-exam-page-card-do': examQuestion.userAnswers.some(userAnswer => userAnswer.length > 0), 
									'my-exam-page-card-mark': markQuestions.includes(index)
								}"
								>
								{{ examQuestion.no }}
							</uni-grid-item>
						</template>
					</uni-grid>
				</scroll-view>
			</uni-card>
		</uni-popup>
	</uni-card>
</template>

<script lang="ts" setup>
	import { ref, reactive, computed, watch } from 'vue'
	import XmQuestion from '@/components/xm-question/xm-question.vue'
	import XmCountDown from '@/components/xm-count-down/xm-count-down.vue'
	import { onLoad } from "@dcloudio/uni-app"
	import { ExamQuestion } from '@/ts';
	import http from '@/utils/request'
		
	// 定义变量
	const answerSheet = ref()
	const markQuestions = ref([] as number[])
	const examQuestions = ref([] as ExamQuestion[])// 试卷信息
	const exam = reactive({// 考试信息
		id: 0, // 考试ID
		name: '',// 考试名称
		color: '', // 倒计时颜色
		markState: 0, // 阅卷状态
		scoreState: 0, // 分数状态
		rankState: 0,// 排名状态
	}) 
	const examEndTime = ref()// 考试结束时间（用reactive必须new Date()，会造成问题）
	const myExam = reactive({// 我的考试信息
		totalScore: 0, //总分
		answerStartTime: new Date(),// 答题开始时间
		answerEndTime: new Date(),// 答题结束时间
		markStartTime: new Date(),// 阅卷开始时间
		markEndTime: new Date(),// 阅卷结束时间
		state: 0, // 考试状态
		markState: 0, // 阅卷状态
		answerState: 0, // 答题状态
		no: 0, // 用户排名
		userNum: 0, // 用户数量
	})
	const user = reactive({// 用户信息
		name: '',// 姓名
		orgName: '',// 机构名称
	})
	const timerId = ref()
	const swiperIndex = ref(0) // 滑块索引
	const swiperQuestions = ref([] as ExamQuestion[]) // 滑块试题数组
	const curQuestionIndex = ref(-1)// 当前试题索引

	// 组件加载完成后，执行如下方法
	onLoad(async(option) => {
		// 获取机构信息
		let { data: _user } = await http.post("user/get", {  })
		user.name = _user.name
		user.orgName = _user.orgName
		
		
		exam.id = option.examId
		// 获取试卷信息（先获取试卷信息，因为后台检测到，当前用户如果是第一次打开试卷，才生成当前用户的考试和结束时间
		let { data: _examQuestions } = await http.post("myExam/paper", { examId: exam.id })
		let no = 1
		examQuestions.value = _examQuestions.map((examQuestion: ExamQuestion) => {
			if (examQuestion.type === 2) {// 处理题号
				examQuestion.no = no++
			}
			return examQuestion
		})
		
		// 获取我的考试信息
		let { data: _exam } = await http.post("myExam/get", { examId: exam.id })
		exam.name = _exam.examName
		exam.markState = _exam.examMarkState
		exam.scoreState = _exam.examScoreState
		exam.rankState = _exam.examRankState
		examEndTime.value = new Date(_exam.examEndTime.replaceAll("-", '/'))
		
		uni.setNavigationBarTitle({
			title: exam.name
		});

		myExam.totalScore = _exam.totalScore
		myExam.answerStartTime = _exam.answerStartTime ? new Date(_exam.answerStartTime.replaceAll("-", '/')) : null
		myExam.answerEndTime = _exam.answerEndTime ? new Date(_exam.answerEndTime.replaceAll("-", '/')) : null
		myExam.markStartTime = _exam.markStartTime ? new Date(_exam.markStartTime.replaceAll("-", '/')) : null
		myExam.markEndTime = _exam.markEndTime ? new Date(_exam.markEndTime.replaceAll("-", '/')) : null
		myExam.state = _exam.state
		myExam.markState = _exam.markState
		myExam.answerState = _exam.answerState
		myExam.no = _exam.no
		myExam.userNum = _exam.userNum
		
		curQuestionIndex.value++
	})

	// 监听属性
	watch(curQuestionIndex, async(n, o) => {
		questionUpdate(curQuestionIndex.value)
	})

	// 计算属性
	const questionNum = computed(() => {// 试题数量
		return examQuestions.value.reduce((total, examQuestion) => {
				return (examQuestion.type === 2) ? total + 1 : total
			}, 0)
	})
	const userAnswerNum  = computed(() => {// 用户答题数量
		return examQuestions.value.reduce((total, examQuestion) => {
			if(examQuestion.type === 2 && examQuestion.userAnswers.some(userAnswer => userAnswer.length > 0)) {
				return total + 1
			}
			return total
		}, 0)
	})
	const isAnswer = computed(() => (myExam.state === 1 && myExam.markState != 3) || myExam.state === 2) // 是否答题。未考试且未阅卷显示（未考试且考试结束时间到，后台会处理成未考试且已阅卷）；考试中显示
	const scoreShow = computed(() => {// 分数显示（后端已经数据过滤掉，这里控制只是好理解）
	    return (exam.scoreState == 1 && exam.markState == 3) // 如果是考试结束后显示成绩，需要等到考试结束
	        || (exam.scoreState == 3 && myExam.markState == 3);// 如果是交卷后显示成绩，需要等到该试卷阅卷完成。比如主观题没阅卷，得不到总分，得不到是否及格
	})

	/**
	 * 试题更新
	 * 一次更新三道题，当前题，上一题，下一题，使滑块换题更流畅
	 * 
	 * @@param curQuestionIndex 当前试题索引
	 */
	function questionUpdate(curIndex: number) {
		let curQuestion = examQuestions.value[curIndex]
		
		let questionLen = examQuestions.value.length
		let nextIndex = curIndex >= questionLen - 1 ? 0 : curIndex + 1
		let nextQuestion = examQuestions.value[nextIndex]
		
		let preIndex = curIndex <= 0 ? questionLen - 1 : curIndex - 1
		let preQuestion = examQuestions.value[preIndex]
		if (swiperIndex.value === 0) {
			swiperQuestions.value[0] = curQuestion
			swiperQuestions.value[1] = nextQuestion
			swiperQuestions.value[2] = preQuestion
		} else if (swiperIndex.value === 1) {
			swiperQuestions.value[0] = preQuestion
			swiperQuestions.value[1] = curQuestion
			swiperQuestions.value[2] = nextQuestion
		} else if (swiperIndex.value === 2) {
			swiperQuestions.value[0] = nextQuestion
			swiperQuestions.value[1] = preQuestion
			swiperQuestions.value[2] = curQuestion
		}
	}
	
	// 上一题
	async function pre() {
		let questionLen = examQuestions.value.length
		curQuestionIndex.value <= 0 ? curQuestionIndex.value = questionLen - 1 : curQuestionIndex.value--// 划到上一题
	}
	
	// 下一题
	async function next() {
		let questionLen = examQuestions.value.length
		curQuestionIndex.value >= questionLen - 1 ? curQuestionIndex.value = 0 : curQuestionIndex.value++
	}
	
	/**
	 * 同步索引
	 * 类似小齿轮带动大齿轮转动
	 * 
	 * @param newSwiperIndex 滑动试图索引
	 * @@param source  
	 */
	function synIndex(newSwiperIndex: number, source: string) {
		if (source !== 'touch') {// 不是滑动触发，不处理
			return
		}
		
		let questionLen = examQuestions.value.length
		let oldSwiperIndex = swiperIndex.value
		if (oldSwiperIndex - newSwiperIndex === -2 || oldSwiperIndex - newSwiperIndex === 1) {// 右滑
			swiperIndex.value <= 0 ? swiperIndex.value = 2 : swiperIndex.value--
			curQuestionIndex.value <= 0 ? curQuestionIndex.value = questionLen - 1 : curQuestionIndex.value--
		} else {// 左滑
			swiperIndex.value >= 2 ? swiperIndex.value = 0 : swiperIndex.value++
			curQuestionIndex.value >= questionLen - 1 ? curQuestionIndex.value = 0 : curQuestionIndex.value++
		}
	}

	// 答案更新
	function answerUpdate(examQuestion: ExamQuestion, answers: string[]) {
		clearTimeout(timerId.value)
		timerId.value = setTimeout(async() => {
			let { code, data } = await http.post('myExam/answer', {
				examId: exam.id,
				questionId: examQuestion.questionId,
				answers: answers
			})
			if (code != 200) {// 答题失败也不要清空答案，比如问答题清空就尴尬了
				return
			}
		}, 500)
	}

	// 标记试题
	function markQuesiton() {
		if (markQuestions.value.includes(curQuestionIndex.value)) {
			markQuestions.value = markQuestions.value.filter(markQuestion => markQuestion != curQuestionIndex.value)
		} else {
			markQuestions.value.push(curQuestionIndex.value)
		}
	}

	// 交卷
	async function finish() {
		let unUserAnswerNum = (questionNum.value - userAnswerNum.value)
		if (unUserAnswerNum > 0) {
			uni.showModal({
				title: '提示消息',
				content: `剩余${ unUserAnswerNum }道未答，是否继续交卷？`,
				showCancel: true,
				success: async res => {
					if (res.cancel) {
						return
					}
					
					let { code } = await http.post("myExam/finish", { examId: exam.id })
					if (code !== 200) {
						return
					}
					
					examEnd()
				}
			})
			return
		}
		
		let { code } = await http.post("myExam/finish", { examId: exam.id })
		if (code !== 200) {
			return
		}

		examEnd()
	}
	
	async function finish1() {
		let { code } = await http.post("myExam/finish", { examId: exam.id })
		if (code !== 200) {
			return
		}
		
		examEnd()
	}

	// 考试结束
	function examEnd() {
		uni.showModal({
			title: '提示消息',
			content: '已交卷，正在阅卷中',
			showCancel: false,
			success: function (res) {
				uni.navigateTo({url: `/pages/myExam/myExamResult?examId=${ exam.id }`})
			}
		});
	}
</script>

<style lang="scss" scoped>
	:deep(.my-exam-page) {
		height: 100%;
		display: flex;
		flex-direction: column;
		.my-exam-page-chapter-name {
			font-size: 30rpx;
			font-weight: bold;
			margin-bottom: 20rpx;
		}
		.my-exam-page-chapter-txt {
			font-size: 28rpx;
			color: $uni-base-color;
		}
		.my-exam-page-head {
			display: flex;
			justify-content: flex-end;
			padding: 20rpx;
			border-bottom: 2rpx solid $uni-border-1;
		}
		.uni-card__content {
			flex: 1;
			uni-swiper {
				height: 900rpx;
			}
		}
		.uni-card__actions {
			height: 100rpx;
			border-top: 2rpx #eee solid;
			display: flex;
			font-size: 26rpx;
			uni-view {
				flex: 1;
				display: flex;
				justify-content: center;
				align-items: center;
				border-right: 1px solid #eee;
				&:active {
					background-color: #eee;
				}
			}
		}
		.uni-grid-item--border {
			border-radius: 50%;
			justify-content: center;
			align-items: center;
			margin: 2px;
		}
		
		.my-exam-page-card {
			.uni-card__actions {
				display: none;
			}
			.uni-grid {
				border-left-width: 0px;
				.uni-grid-item--border {
					border: 1px #D2D2D2 solid;
				}
			}
				
			.my-exam-page-card-tips {
				margin-bottom: 10rpx;
				uni-text {
					display: flex;
					align-items: center;
					margin-right: 12rpx;
				}
			}
			
			.my-exam-page-card-todo {
				
			}
			
			.my-exam-page-card-do {
				.uni-grid-item--border {
					background-color: $uni-primary-disable;
					color: white;
				}
			}
			.my-exam-page-card-mark {
				position: relative;
				&:before {
					font-family: 'iconfont';
					content: "\e660";
					position: absolute;
					right: -0rpx;
					top: -0rpx;
					font-weight: bold;
					font-size: 30rpx;
					color: $uni-error;
					z-index: 1;
				}
			}
			
			.my-exam-page-card-chapter {
				width: 100%;
				margin-top: 20rpx;
				margin-bottom: 6rpx;
			}
		}
	}
</style>
