<template>
	<uni-card class="my-exer-page" :is-full="true" spacing="0">
		<swiper :current="swiperIndex" :circular="true" @change="(e: any) => synIndex(e.detail.current, e.detail.source)">
			<swiper-item v-for="(question, index) in questions" :key="index">
				<xm-question
					:no="`${ curQuestionIndex + 1 } / ${ questionNum }`"
					v-model="question.userAnswers"
					:type="question.type"
					:markType="question.markType" 
					:title="question.title" 
					:score="question.score"
					:answers="question.answers"
					:userScore="question.userScore"
					:options="question.options"
					:editable="exer.answerShow && question.userScore == null"
					:errMark="exer.answerShow && question.userScore != null"
					:answersShow="!exer.answerShow"
					@change="autoNext(question)"
				></xm-question>
			</swiper-item>
		</swiper>
		<template #title>
			<view class="my-exer-page-head">
				<uni-data-checkbox 
					v-model="exer.answerShow" 
					:localdata="[{'value': true, 'text': '答题'}, {'value': false, 'text': '背题'}]" 
					mode="tag"
					></uni-data-checkbox>
				<!-- <uni-data-checkbox multiple v-model="exer.orderShow" :localdata="[{'value': true, 'text': `${exer.orderShow[0] ? '顺序' : '随机'}`}]" @change="(e) => {if (!e.detail.value.length) { exer.orderShow.push(false) }}"></uni-data-checkbox>不选为空，处理成false
				<uni-data-checkbox v-if="exer.rmkState === 1" multiple v-model="exer.rmkShow" :localdata="[{'value': true, 'text': '评论'}]"></uni-data-checkbox> -->
				<XmCountDown 
					:expireTime="exer.endTime" 
					preTxt="剩余" 
					:remind="300"
					@end="exerEnd"                                                                             
					@remind="exer.color='red'" 
					:color="exer.color"
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
					:color="markQuestions.includes(getQuestionIds()[curQuestionIndex]) ? '#e43d33' : ''"
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
			<view @click="exerEnd()">
				<uni-icons custom-prefix="iconfont" type="icon-duigoux"></uni-icons>
				<text>完成</text>
			</view>
		</template>
		<uni-popup ref="answerSheet" type="share" safeArea backgroundColor="#fff">
			<uni-card title="答题卡" :isFull="true" sub-title="" extra="" spacing="0" class="my-exer-page-card">
				<uni-grid :column="10" :highlight="true" @change="" class="my-exer-page-card-tips">
					<uni-grid-item class="my-exer-page-card-do"></uni-grid-item>
					<text>已答</text>
					<uni-grid-item class="my-exer-page-card-true"></uni-grid-item>
					<text>答对</text>
					<uni-grid-item class="my-exer-page-card-half"></uni-grid-item>
					<text>半对</text>
					<uni-grid-item class="my-exer-page-card-false"></uni-grid-item>
					<text>答错</text>
					<uni-grid-item class="my-exer-page-card-mark"></uni-grid-item>
					<text>标记</text>
				</uni-grid>
				<scroll-view scroll-y="true" style="height: 40vh;">
					<uni-grid :column="10" :highlight="true" @change="" class="my-exer-page-card-tips">
						<template v-for="(questionId, index) in getQuestionIds()" :key="questionId" >
							<uni-grid-item 
								@click="curQuestionIndex = index" 
								:class=" {
									'my-exer-page-card-do': hasDo(questionId),
									'my-exer-page-card-mark': hasMark(questionId),
									'my-exer-page-card-half': hasHalf(questionId),
									'my-exer-page-card-true': hasTrue(questionId),
									'my-exer-page-card-false': hasFalse(questionId),
								}">{{ index + 1 }}
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
	import { Question } from '@/ts';
	import http from '@/utils/request'
	import { useUserStore } from "@/stores/user"

	// 定义变量
	const userStore = useUserStore()
	const answerSheet = ref()
	const exer = reactive({// 练习信息
		id: 0, // 练习ID
		startTime: null as unknown,// 开始时间
		endTime: null as unknown,// 结束时间
		rmkState: 2,// 评论状态（1：是；2：否）
		questionIds: [] as number[], // 试题列表
		answerShow: true,// 答题模式
		orderShow: [true], // 顺序显示
		rmkShow: [false], // 评论显示
		firstTime: new Date(),// 第一次进入时间
		lastTime: new Date(),// 最后一次答题时间
		color: '', // 倒计时颜色
	})
	const exerRmk = reactive({// 评论
		content: '',
		anon: false
	})
	const rmkListpage = reactive({// 评论分页列表
		curPage: 1,
		pageSize: 20,
		total: 0,
		cache: [] as number[],// 存放ID，用来过滤重复数据
		list: [] as any[],
	})
	const user = reactive({// 用户信息
		name: '',// 姓名
		orgName: '',// 机构名称
	})
	const swiperIndex = ref(0)// 滑块索引
	const curQuestionIndex = ref(-1)// 当前试题索引
	const questions = ref([] as Question[]) //试题数组
	const questionCache = reactive({} as { [questionId: number]: Question}) // 试题缓存
	const markQuestions = ref([] as number[])

	// 页面加载完毕，执行如下方法
	onLoad(async (option) => {
		// 获取用户信息
		let { data: data2 } = await http.post("user/get", {  })
		user.name = data2.name
		user.orgName = data2.orgName
		
		// 获取我的练习
		exer.id = parseInt(option.exerId as string)
		let { data: data3 } = await http.post("myExer/get", { exerId: exer.id })
		exer.startTime = new Date(data3.startTime.replaceAll('-', '/'))
		exer.endTime = new Date(data3.endTime.replaceAll('-', '/'))
		exer.rmkState = data3.rmkState

		let { data } = await http.post("myExer/questionList", { exerId: exer.id })
		exer.questionIds.push(...data)
		
		if (exer.questionIds.length) {// 触发curQuestionIndex监听
			curQuestionIndex.value++
		}
	})

	// 监听属性
	watch(() => exer.orderShow[0], async (n, o) => {// 随机和顺序，同一个索引不是同一个题
		questionUpdate(curQuestionIndex.value)
		//rmkUpdate(questionId)
	})
	watch(curQuestionIndex, async() => {
		questionUpdate(curQuestionIndex.value)
		//rmkUpdate(questionId)
	})

	// 计算属性
	const questionIdsOfShuffle = computed(() => {// 乱序后的试题列表，点随机的时候用
		return [].concat(exer.questionIds).sort(() => Math.random() > 0.5 ? -1 : 1) as number[]
	})
	const questionNum = computed(() => {// 试题数量
		return exer.questionIds.length
	})
	const diffMinute = computed(() => {// 答题时长（分钟数）
		return Math.ceil((exer.lastTime.getTime() - exer.firstTime.getTime()) / 1000 / 60)
	})
	const questionLen = computed(() => {// 试题长度
		return exer.questionIds.length
	})

	// 获取试题
	async function getQuestion(questionId: number) {
		if (!questionCache[questionId]) {
			let { data } = await http.post("myExer/question", { exerId: exer.id, questionId })
			data.userAnswers = [] // 接口没有，填充
			data.userScore = null // 接口没有，填充
			questionCache[questionId] = data
		}
		
		return questionCache[questionId] as Question
	}
	
	// 获取试题ID列表
	function getQuestionIds() {
		return exer.orderShow[0] ? exer.questionIds : questionIdsOfShuffle.value
	}
	
	/**
	 * 试题更新
	 * 一次更新三道题，当前题，上一题，下一题，使滑动换题更流畅
	 * 
	 * @@param curQuestionIndex 当前试题索引
	 */
	async function questionUpdate(curQuestionIndex: number) {
		let questionIds = getQuestionIds()// 获取试题ID列表
		
		let curIndex = curQuestionIndex
		let curQuestion = await getQuestion(questionIds[curIndex])
		
		let nextIndex = curQuestionIndex >= questionLen.value - 1 ? 0 : curQuestionIndex + 1
		let nextQuestion = await getQuestion(questionIds[nextIndex])
		
		let preIndex = curQuestionIndex <= 0 ? questionLen.value - 1 : curQuestionIndex - 1
		let preQuestion = await getQuestion(questionIds[preIndex])
		if (swiperIndex.value === 0) {
			questions.value[0] = curQuestion
			questions.value[1] = nextQuestion
			questions.value[2] = preQuestion
		} else if (swiperIndex.value === 1) {
			questions.value[0] = preQuestion
			questions.value[1] = curQuestion
			questions.value[2] = nextQuestion
		} else if (swiperIndex.value === 2) {
			questions.value[0] = nextQuestion
			questions.value[1] = preQuestion
			questions.value[2] = curQuestion
		}
	}

	// 试题批阅
	function questionMark(question: Question) {
		if (!exer.answerShow) {
			return
		}
		if (question.type === 1 || question.type === 4) {// 如果是单选或判断
			if (question.userScore == null) {// 并且没打分
				if (question.userAnswers && question.userAnswers[0]) {// 并且有答案
					question.userScore = 0
					if (question.userAnswers[0] === question.answers[0]) {// 打分
						question.userScore = question.score
					}
				}
			}
		}
		if (question.type === 2) {// 如果是多选
			if (question.userScore == null) {// 并且没打分
				if (question.userAnswers && question.userAnswers[0]) {// 并且有答案
					question.userScore = 0
					let include = question.userAnswers.every((userAnswer: string) => question.answers.includes(userAnswer))
					if (include && question.answers.length === question.userAnswers.length) {// 全对满分
						question.userScore = question.score
					} else if (include) {// 半对指定分
						question.userScore = question.scores[0]
					}
				}
			}
		}
	}

	// 评论更新
	async function rmkUpdate(questionId: number) {
		if (!(exer.rmkState === 1 && exer.rmkShow)) {// 不允许评论，或不显示，不查询
			return
		}
		rmkListpage.curPage = 1
		rmkListpage.cache = []
		rmkListpage.list = []
		rmkQuery(questionId)
	}

	// 答案格式化
	function answerFormat(question: Question) {
		if (question.type === 1 // 单选
			|| question.type === 4 // 判断
			|| (question.type === 5 && question.markType === 2)) {// 主观问答
			return question.answers && question.answers[0] && question.answers[0].replaceAll('\n', '<br/>')
		}
		if (question.type === 2) {// 多选
			return question.answers?.toString().replaceAll(",", "")
		}

		if (question.type === 3 // 填空
			|| (question.type === 5 && question.markType === 1)) {// 客观问答
			let answerFormat = ''
			question.answers?.forEach((answer: string, index: number) => {
				answerFormat += `${ question.type === 3 ? '填空' : '关键词' }${ index + 1 }：${ answer.replaceAll('\n', '、') }<br/>`
			})

			return answerFormat
		}
	}

	// 错题标记
	function errMark(questionId: number) {
		let question = questionCache[questionId]
		if (!question) {// 如果没有查看过这道题，默认颜色
			return ''
		}
		if (!question.userAnswers?.length) {// 如果没有作答，默认颜色
			return  ''
		}
		if (question.type === 3 || question.type === 5) {// 如果是填空问答，填了答案就代表做过
			if (question.userAnswers.some(userAnswer => userAnswer.length > 0)) {
				return 'my-exer-page-card-do'
			}
		}
		if (question.type === 1 || question.type === 2 || question.type === 4) {// 如果是单选多选判断
			if (question.userScore == null) {// 没打分，默认颜色（作用与多选）
				
			}
			if (question.userScore === question.score) {// 满分绿色
				return ''
			}
			if (question.userScore === 0) { // 零分红色
				return 'my-exer-page-card-false'
			}
			return 'my-exer-page-card-half' // 不满分警告色
		}

		return ''
	}

	// 模拟结束
	function exerEnd() {
		let questionIds = getQuestionIds()
		
		let total = questionIds.length
		let trueNum = 0
		let falseNum = 0
		questionIds.forEach((questionId) => {
			if (questionCache[questionId] && questionCache[questionId].userScore != null) {
				if (questionCache[questionId].userScore === questionCache[questionId].score) {
					trueNum++
				} else {
					falseNum++
				}
			}
		})
		
		uni.showModal({
			title: '提示消息',
			content: '模拟已完成',
			showCancel: false,
			success: function (res) {
				uni.navigateTo({url: `/pages/myExer/myExerResult?total=${total}&trueNum=${trueNum}&falseNum=${falseNum}&diffMinute=${diffMinute.value}`});
			}
		});
	}

	// 评论
	async function rmk(questionId: number) {
		if (!exerRmk.content.trim()) {
			return
		}
		const { code, data } = await http.post('myExer/rmk', {
			exerId: exer.id,
			questionId,
			content: exerRmk.content,
			anon: exerRmk.anon,
		})

		if (code !== 200) {
			return
		}

		let curTime = new Date()
		rmkListpage.list.unshift({// 新添加的放在第一个，有可能按点赞排序，在后面看不到
			id: data.id,
			content: exerRmk.content,
			updateTime: curTime.getFullYear() + '-' + (curTime.getMonth() + 1) + '-' + curTime.getDate() + " " + curTime.getHours() + ":" + curTime.getMinutes() + ":" + curTime.getSeconds(),
			updateUserName: exerRmk.anon ? null : userStore.name,
			likeNum: 0,
			hasLike: false,
		})
		
		uni.pageScrollTo({ scrollTop: 0, duration: 300 });

		exerRmk.content = ''// 评论后清空内容
	}

	// 评论查询
	async function rmkQuery(questionId: number) {
		const { code, data } = await http.post('myExer/rmkListpage', {
			questionId,
			curPage: rmkListpage.curPage,
			pageSize: rmkListpage.pageSize,
		})

		if (code !== 200) {
			return
		}

		rmkListpage.list.push(...data.list.filter((cur: any) => {// 过滤重复数据
			if(rmkListpage.cache.includes(cur.id)) {
				return false
			}

			rmkListpage.cache.push(cur.id)
			return true
		}))
		rmkListpage.total = data.total
		rmkListpage.curPage++
	}

	// 评论点赞
	async function rmkLike(exerRmkId: number) {
		const { code, data } = await http.post('myExer/rmkLike', { exerRmkId })
		if (code !== 200) {
			return
		}

		let rmk = rmkListpage.list.filter(cur => cur.id === exerRmkId)
		rmk[0].hasLike = !rmk[0].hasLike
		if (rmk[0].hasLike) {
			rmk[0].likeNum++
		} else {
			rmk[0].likeNum--
		}
	}

	// 上一题
	async function pre() {
		let questionIds = getQuestionIds()
		if (exer.answerShow) {// 如果是答题模式
			let curQuestion = await getQuestion(questionIds[curQuestionIndex.value])
			if (curQuestion.type === 1 || curQuestion.type === 2 || curQuestion.type === 4) {// 如果是单选多选判断
				if (curQuestion.userAnswers.length) {// 并且已答题
					if (curQuestion.userScore == null) {// 并且没打分
						questionMark(curQuestion) // 打分
						if (curQuestion.score != curQuestion.userScore) {// 如果不是满分，不进入下一题（第二次就打分了，不在进入）
							return
						}
					}
				}
			}
		}
		
		curQuestionIndex.value <= 0 ? curQuestionIndex.value = questionLen.value - 1 : curQuestionIndex.value--// 划到上一题
	}

	// 下一题
	async function next() {
		let questionIds = getQuestionIds()
		let curQuestion = await getQuestion(questionIds[curQuestionIndex.value])
		
		if (curQuestion.type === 1 || curQuestion.type === 2 || curQuestion.type === 4) {// 如果是单选多选判断
			if (curQuestion.userAnswers.length) {// 并且已答题
				if (curQuestion.userScore == null) {// 并且没打分
					questionMark(curQuestion) // 打分
					if (curQuestion.score != curQuestion.userScore) {// 如果不是满分，不进入下一题（第二次就打分了，不在进入）
						return
					}
				}
			}
		}
		
		curQuestionIndex.value >= questionLen.value - 1 ? curQuestionIndex.value = 0 : curQuestionIndex.value++
	}

	// 自动下一题
	function autoNext(question: Question) {
		exer.lastTime = new Date() // 答题就更新时间
		if (question.type === 1 || question.type === 4) {// 如果是单选或判断
			if (exer.answerShow) {// 并且是答题模式
				questionMark(question)
				if (question.score === question.userScore) {// 并且答对
					setTimeout(() => {
						next() // 自动下一题
					}, 500)
				}
			}
		}
	}
	
	// 标记试题
	function markQuesiton() {
		let curQuestionId = getQuestionIds()[curQuestionIndex.value]
		if (markQuestions.value.includes(curQuestionId)) {
			markQuestions.value = markQuestions.value.filter(markQuestion => markQuestion != curQuestionId)
		} else {
			markQuestions.value.push(curQuestionId)
		}
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
		
		let oldSwiperIndex = swiperIndex.value
		if (oldSwiperIndex - newSwiperIndex === -2 || oldSwiperIndex - newSwiperIndex === 1) {// 右滑
			swiperIndex.value <= 0 ? swiperIndex.value = 2 : swiperIndex.value--
			curQuestionIndex.value <= 0 ? curQuestionIndex.value = questionLen.value - 1 : curQuestionIndex.value--
		} else {// 左滑
			swiperIndex.value >= 2 ? swiperIndex.value = 0 : swiperIndex.value++
			curQuestionIndex.value >= questionLen.value - 1 ? curQuestionIndex.value = 0 : curQuestionIndex.value++
		}
	}
	
	// 是否答题
	function hasDo(questionId: number) {
		 let question = questionCache[questionId] as Question
		 if (question) {
			 return question.userAnswers.some(userAnswer => userAnswer.length)
		 }
		 return false
	}
	
	// 是否标记
	function hasMark(questionId: number) {
		return markQuestions.value.includes(questionId)
	}
	
	// 是否答对
	function hasTrue(questionId: number) {
		let question = questionCache[questionId] as Question
		if (question && question.userScore != null) {
			return question.userScore === question.score
		}
		return false
	}
	
	// 是否答错
	function hasFalse(questionId: number) {
		let question = questionCache[questionId] as Question
		if (question && question.userScore != null) {
			return question.userScore === 0
		}
		return false
	}
	
	// 是否半对
	function hasHalf(questionId: number) {
		let question = questionCache[questionId] as Question
		if (question && question.userScore != null) {
			return question.userScore > 0 && question.userScore !== question.score
		}
		return false
	}
</script>

<style lang="scss" scoped>
	:deep(.my-exer-page) {
		height: 100%;
		display: flex;
		flex-direction: column;
		.my-exer-page-chapter-name {
			font-size: 28rpx;
			font-weight: bold;
			margin-bottom: 20rpx;
		}
		.my-exer-page-chapter-txt {
			font-size: 34rpx;
			color: $uni-base-color;
		}
		.my-exer-page-head {
			display: flex;
			justify-content: space-between;
			padding: 20rpx;
			border-bottom: 2rpx solid $uni-border-1;
			.checklist-box {
				margin-right: 20rpx;
			}
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
		
		.my-exer-page-card {
			.uni-card__actions {
				display: none;
			}
			.uni-grid {
				border-left-width: 0px;
				.uni-grid-item--border {
					border: 1px #D2D2D2 solid;
				}
			}
			
			.my-exer-page-card-tips {
				margin-bottom: 10rpx;
				uni-text {
					display: flex;
					align-items: center;
					margin-right: 12rpx;
				}
				.my-exer-page-card-do {
					.uni-grid-item--border {
						background-color: $uni-primary-disable;
						color: white;
					}
				}
				
				.my-exer-page-card-true {
					position: relative;
					&:before {
						font-family: 'iconfont';
						content: "\ec9e";
						position: absolute;
						right: -0rpx;
						bottom: -4rpx;
						font-weight: bold;
						font-size: 30rpx;
						color: $uni-success;
						z-index: 1;
					}
				}
				.my-exer-page-card-false {
					position: relative;
					&:before {
						font-family: 'iconfont';
						content: "\e6e5";
						position: absolute;
						right: -0rpx;
						bottom: -4rpx;
						font-weight: bold;
						font-size: 30rpx;
						color: $uni-error;
						z-index: 1;
					}
				}
				.my-exer-page-card-half {
					position: relative;
					&:before {
						font-family: 'iconfont';
						content: "\e642";
						position: absolute;
						right: 0rpx;
						bottom: -4rpx;
						font-weight: bold;
						font-size: 30rpx;
						color: $uni-error;
						z-index: 1;
					}
				}
				.my-exer-page-card-mark {
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
			}
		}
	}
</style>
