<template>
	<view class="container">
		<u-navbar :title="examName" autoBack :placeholder="true" :border-bottom="false">
		</u-navbar>

		<view class="fixed-header" v-if="!preview">
			<u-count-down :time="systemTime" format="DD:HH:mm:ss" autoStart @finish="finishExam">
			</u-count-down>
		</view>

		<swiper :current="swiperCurrent" @change="swiperChange" :duration="duration"
			:style="{width:'100%',height:height+'px'}">
			<swiper-item v-for="(question,index) in questionList" :key="index">
				<scroll-view scroll-top="0" scroll-y="true" :style="{width:'100%',height:height+'px'}">
					<view class="question-content">
						<!-- 标题 -->
						<view class="question-title" v-if="question.type !== 3">
							<view>{{ index + 1 }}、</view>
							<div v-html="question.title"></div>
						</view>
						<view class="question-gap-title" v-else>
							<span>{{ index + 1 }}、</span>
							<view v-for="(item, index) in question.titleList" :key="item + index" style="display: inline;">
							  <span>{{ item }}</span>
							  <div v-if="question.titleList[question.titleList.length - 1] != item" class="input-box">
								<label :ref="`label-${index}`" class="label"></label>
								<u--input border="bottom" :disabled='preview'
									:placeholder="`填空${index+1}`" @blur='(e)=>updateAnswer(e,question.id,3)'
									v-model='myExamDetailCache[question.id].answers[index]'></u--input>
							  </div>
							</view>
						</view>

						<!-- 单选 -->
						<view class="question-option" v-if="question.type === 1">
							<radio-group @change="({detail})=> updateAnswer(detail.value,question.id,question.type)">
								<label class="option-item" v-for="(option, indexOption) in question.options"
									:key="indexOption">
									<radio :value="option.no" color="#0094e5"
										style="transform:scale(0.7)" :disabled="preview"
										:checked="myExamDetailCache[question.id].answers.includes(String.fromCharCode(65 + indexOption))" />
									<div class="option-text"
										v-html="`${String.fromCharCode(65 + indexOption)}、${option.option}`" />
								</label>
							</radio-group>
						</view>

						<!-- 多选 -->
						<view class="question-option" v-if="question.type === 2">
							<checkbox-group @change="({detail})=> updateAnswer(detail.value,question.id,question.type)">
								<label class="option-item" v-for="(option, indexOption) in question.options"
									:key="indexOption">
									<checkbox :value="option.no" color="#0094e5"
										style="transform:scale(0.7)" :disabled="preview"
										:checked="myExamDetailCache[question.id].answers.includes(option.no)" />
									<div class="option-text"
										v-html="`${String.fromCharCode(65 + indexOption)}、${option.option}`" />
								</label>
							</checkbox-group>
						</view>

						<!-- 填空 -->
						<!-- <view class="question-option" v-if="question.type === 3">
							<view v-for="(option, indexOption) in question.answers" :key="indexOption">
								<u--input class="cloze-input" border="bottom" :disabled='preview' :placeholder="`填空${indexOption+1}`"
									@blur='(e)=>updateAnswer(e,question.id,3)'
									v-model='myExamDetailCache[question.id].answers[indexOption]'></u--input>
							</view>
						</view> -->

						<!-- 判断 -->
						<view class="question-option" v-if="question.type === 4">
							<radio-group @change="({detail})=> updateAnswer(detail.value,question.id,question.type)">
								<label class="option-item" v-for="(option, indexOption) in ['对','错']"
									:key="indexOption">
									<radio :value="String.fromCharCode(65 + indexOption)" color="#0094e5"
										style="transform:scale(0.7)" :disabled="preview"
										:checked="myExamDetailCache[question.id].answers.includes(String.fromCharCode(65 + indexOption))" />
									<div class="option-text"
										v-html="`${String.fromCharCode(65 + indexOption)}、${option}`" />
								</label>
							</radio-group>
						</view>

						<!-- 阅读 -->
						<view class="question-textarea" v-if="question.type === 5">
							<u--textarea v-model="myExamDetailCache[question.id].answers[0]" placeholder="请输入答案" :disabled="preview"
								@blur="(e)=> updateAnswer(e,question.id,question.type)"></u--textarea>
						</view>

					</view>
				</scroll-view>
			</swiper-item>
		</swiper>

		<view class="footer">
			<view class="left">
				<view class="btn" @click="prevQuestion">
					<u-icon name="arrow-left" size="12px"></u-icon>
					<text>上一题</text>
				</view>
				<view class="exam-progress" @click="routerShow = true">
					{{ swiperCurrent+1 }} / {{ questionList.length }}
				</view>
				<view class="btn" @click="nextQuestion">
					<text>下一题</text>
					<u-icon name="arrow-right" size="12px"></u-icon>
				</view>
			</view>
			<view class="right">
				<view class="btn finish-exam" @click="showFinish = !showFinish">
					交卷
				</view>
			</view>
		</view>

		<u-modal :show="showFinish" :showCancelButton="true" confirmText="确认提交" cancelText="取消提交" title="提交试卷"
			content="提交试卷将结束考试!" @confirm="examFinish" @cancel="showFinish = false"></u-modal>

		<u-modal :show="showCoerceFinish" content='考试时间到，已强制交卷！' @confirm="coerceFinish"></u-modal>
		
		<u-popup :show="routerShow" :round="10" @close="routerShow = false">
			<view class="popup-box">
				<view class="popup-header">
					<u-icon name="close" @click="routerShow = false"></u-icon>
				</view>
				<view class="popup-body">
					<view :class="['router-item', swiperCurrent === index ? 'router-active' : '']" v-for="(router,index) in questionList" :key="router.id" @click="routerClick(index)">
						{{ index + 1 }}
					</view>
				</view>
			</view>
		</u-popup>
	</view>
</template>

<script>
	import {
		examGet
	} from '@/api/exam.js'
	import {
		loginSysTime
	} from '@/api/common.js'
	import {
		myExamAnswer,
		myExamFinish,
		myExamAnswerList,
	} from '@/api/mine.js'
	import {
		paperGet,
		paperQuestions,
		paperRandomQuestions
	} from '@/api/paper.js'
	import Cloze from '@/components/cloze/cloze.vue'
	import Vue from 'vue'
	export default {
		components: {
			Cloze,
		},
		data() {
			return {
				height: 0,
				genType: 1,
				markType: 1,
				duration: 500,
				systemTime: 1000,
				swiperCurrent: 0,
				examName: '',
				examEndTime: '',
				examId: null,
				preview: false,
				scoreState: true,
				questionList: [],
				myExamDetailCache: {},
				showFinish: false,
				showCoerceFinish: false,
				routerShow: false
			};
		},
		onLoad(options) {
			let {
				screenHeight,
				statusBarHeight,
				platform
			} = uni.getSystemInfoSync();
			let MenuButtonHeight = platform == 'ios' ? 44 : 48;
			this.height = screenHeight - statusBarHeight - MenuButtonHeight - 90;

			const {
				examId,
				preview,
				examEndTime
			} = JSON.parse(options.params)
			this.examId = examId
			this.preview = preview
			this.examEndTime = examEndTime
			this.init()
		},
		methods: {
			// 初始化
			async init() {
				await this.setTime()
				const res = await examGet({
					id: this.examId
				})
				this.examName = res.data.name
				this.scoreState = res.data.scoreState === 1
				this.paperId = res.data.paperId
				await this.queryPaperInfo()
				await this.queryAnswerInfo()
				await this.queryPaper()
			},

			// 校准时间差
			async setTime() {
				const systemTime = await loginSysTime({})
				const times = new Date(systemTime.data) - new Date()
				this.systemTime =
					new Date(this.examEndTime).getTime() - (new Date().getTime() + times)
			},

			// 查询试卷信息
			async queryPaperInfo() {
				let res
				if (this.genType === 1) {
					res = await paperQuestions({
						id: this.paperId,
					})
				} else {
					res = await paperRandomQuestions({
						examId: this.examId,
						userId: uni.getStorageSync('userInfo').userId,
					})
				}
				// res.data.questionList
				res.data.forEach(item => {
					item.questionList.forEach(questItem => {
						questItem.titleList = this.splitTxtForSpaces(this.getTxtForHtml(questItem.title), /_{5,}/)
					})
				})
				this.questionList = res.data.reduce((acc, cur) => {
					acc.push(...cur.questionList)
					return acc
				}, [])
			},
			
			// html中提取文本
			getTxtForHtml(html) {
				return html.replace(/<.*?>/g, "");
			},
			// 将文本按照分割符分割成列表
			splitTxtForSpaces(txtStr, decollator) {
				return txtStr.split(decollator);
			},

			// 查询试卷
			async queryPaper() {
				const res = await paperGet({
					id: this.paperId,
				})
				this.markType = res.data.markType
				this.genType = res.data.genType
			},

			// 查询我的答案信息
			async queryAnswerInfo() {
				let res = await myExamAnswerList({
					examId: this.examId
				})
				// 组合试卷答案信息
				this.myExamDetailCache = res.data.reduce((acc, cur, index) => {
					if (
						cur.questionType === 3 &&
						this.questionList[index].id === cur.questionId
					) {
						cur.answers.length = this.questionList[index].answers.length
					}

					acc[cur.questionId] = cur
					return acc
				}, {})
			},

			// 更新答案
			async updateAnswer(optionValue, questionId, questionType) {

				if (this.preview) {
					return
				}

				if (!this.myExamDetailCache[questionId]) {
					this.$message.error('提交答案失败，请联系管理员！')
					return
				}

				if ([1, 4].includes(questionType)) {
					this.myExamDetailCache[questionId].answers = [optionValue]
				}

				if (questionType === 2) {
					this.myExamDetailCache[questionId].answers = optionValue
				}

				const res = await myExamAnswer({
					questionId,
					examId: this.examId,
					answers: this.myExamDetailCache[questionId].answers
				})

				if (res?.code === 200) {
					const questionItem = this.questionList.find((question) => question.id === questionId)
					if (questionType === 3 && this.myExamDetailCache[questionId].answers.length !== questionItem
						.answers.length) {
						return false
					}
				}
				this.nextQuestion()
			},

			// 页面左右滑动
			swiperChange(event) {
				this.swiperCurrent = event.detail.current;
			},

			// 上一题
			prevQuestion() {
				if (this.swiperCurrent + 1 <= 1) {
					return false
				}
				this.swiperCurrent -= 1
			},

			// 下一题
			nextQuestion() {
				if (this.swiperCurrent + 1 >= this.questionList.length) {
					return false
				}
				this.swiperCurrent += 1
			},
			
			// 点击导航
			routerClick(index) {
				this.swiperCurrent = index
			},
			
			// 倒计时结束事件
			finishExam(){
				myExamFinish({
					examId: this.examId
				})
				this.showCoerceFinish = true
			},
			
			// 强制结束考试
			coerceFinish(){
				const params = {
					examId: this.examId,
					scoreState: this.scoreState,
				}
				this.showCoerceFinish = false
				this.markType === 1 ?
					uni.redirectTo({
						url: `/pages/examResult/examResult?params=${JSON.stringify(params)}`
					}) :
					uni.switchTab({
						url: '/pages/index/index'
					})
			},

			// 结束考试
			async examFinish() {
				const res = await myExamFinish({
					examId: this.examId
				})
				const params = {
					examId: this.examId,
					scoreState: this.scoreState,
				}

				if (res?.code === 200) {
					this.showCoerceFinish = false
					this.showFinish = false
					this.markType === 1 ?
						uni.redirectTo({
							url: `/pages/examResult/examResult?params=${JSON.stringify(params)}`
						}) :
						uni.switchTab({
							url: '/pages/index/index'
						})
				} else {
					uni.$u.toast('请重新提交试卷！')
				}
			},
		}
	}
</script>

<style lang="scss" scoped>
	@import '@/common/css/exam.scss'
</style>