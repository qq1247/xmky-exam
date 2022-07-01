<template>
	<view class="container">
		<u-navbar title="模拟练习" autoBack :placeholder="true" :border-bottom="false">
		</u-navbar>

		<view class="fixed-header">
			<u-subsection :list="list" :current="current" @change="sectionChange"></u-subsection>
		</view>

		<swiper :current="swiperCurrent" @change="swiperChange" :duration="duration"
			:style="{width:'100%',height:height+'px'}">
			<swiper-item v-for="(question,index) in questionList" :key="index">
				<scroll-view scroll-top="0" scroll-y="true" :style="{width:'100%',height:height+'px'}">
					<!-- 答题模式 -->
					<view class="question-content" v-if="!isRecite">
						<!-- 标题 -->
						<view v-if="question.type !== 3" class="question-title">
							<view>{{ index + 1 }}、</view>
							<div v-html="question.title"></div>
						</view>
						<view v-else class="question-gap-title">
							<span>{{ index + 1 }}、</span>
							<view v-for="(item, index) in question.titleList" :key="item + index" style="display: inline;">
							  <span>{{ item }}</span>
							  <div v-if="question.titleList[question.titleList.length - 1] != item" class="input-box">
								<label :ref="`label-${index}`" class="label"></label>
								<u--input border="bottom" :disabled='question.finish'
									:placeholder="`填空${index+1}`" @blur='(e)=>checkAnswer(e,question.type)'
									v-model='question.selected[index]'></u--input>
							  </div>
							</view>
						</view>

						<!-- 单选 -->
						<view class="question-option" v-if="question.type === 1">
							<radio-group @change="({detail})=> checkAnswer(detail.value,question.type)">
								<label class="option-item" v-for="(option, indexOption) in question.options"
									:key="indexOption">
									<radio :value="String.fromCharCode(65 + indexOption)" color="#0094e5"
										style="transform:scale(0.7)" :disabled="question.finish"
										:checked="question.selected.includes(String.fromCharCode(65 + indexOption))" />
									<div class="option-text"
										:style="{color: question.finish ? optionColor(indexOption) : ''}"
										v-html="`${String.fromCharCode(65 + indexOption)}、${option}`" />
								</label>
							</radio-group>
						</view>

						<!-- 多选 -->
						<view class="question-option" v-if="question.type === 2">
							<checkbox-group @change="({detail})=> checkAnswer(detail.value,question.type)">
								<label class="option-item" v-for="(option, indexOption) in question.options"
									:key="indexOption">
									<checkbox :value="String.fromCharCode(65 + indexOption)" color="#0094e5"
										style="transform:scale(0.7)" :disabled="question.finish"
										:checked="question.selected.includes(String.fromCharCode(65 + indexOption))" />
									<div class="option-text"
										:style="{color: question.finish ? optionColor(indexOption) : ''}"
										v-html="`${String.fromCharCode(65 + indexOption)}、${option}`" />
								</label>
							</checkbox-group>
						</view>

						<!-- 填空 -->
						<!-- <view class="question-option" v-if="question.type === 3">
							<view v-for="(option, indexOption) in question.answers" :key="indexOption">
								<u--input border="bottom" :disabled='question.finish'
									:placeholder="`填空${indexOption+1}`" @blur='(e)=>checkAnswer(e,question.type)'
									v-model='question.selected[indexOption]'></u--input>
							</view>
						</view> -->

						<!-- 判断 -->
						<view class="question-option" v-if="question.type === 4">
							<radio-group @change="({detail})=> checkAnswer(detail.value,question.type)">
								<label class="option-item" v-for="(option, indexOption) in ['对','错']"
									:key="indexOption">
									<radio :value="String.fromCharCode(65 + indexOption)" color="#0094e5"
										style="transform:scale(0.7)" :disabled="question.finish"
										:checked="question.selected.includes(option)" />
									<div class="option-text"
										:style="{color: question.finish ? optionColor(indexOption) : ''}"
										v-html="`${String.fromCharCode(65 + indexOption)}、${option}`" />
								</label>
							</radio-group>
						</view>

						<!-- 阅读 -->
						<view class="question-textarea" v-if="question.type === 5">
							<u--textarea v-model="question.selected" placeholder="请输入答案"
								@blur="(e)=> checkAnswer(e,question.type)"></u--textarea>
						</view>

					</view>

					<!-- 背題模式 -->
					<view class="question-content" v-else>
						<!-- 标题 -->
						<view class="question-title" v-if="question.type !== 3">
							<view>{{ index + 1 }}、</view>
							<div v-html="question.title"></div>
						</view>
						
						<view v-else class="question-gap-title">
							<span>{{ index + 1 }}、</span>
							<view v-for="(item, index) in question.titleList" :key="item + index" style="display: inline;">
							  <span>{{ item }}</span>
							  <div v-if="question.titleList[question.titleList.length - 1] != item" class="answer">
									<span >{{question.answers[index].answer.join()}}</span>
							  </div>
							</view>
						</view>

						<!-- 单选 -->
						<view class="question-option" v-if="question.type === 1">
							<radio-group>
								<label class="option-item" v-for="(option, indexOption) in question.options"
									:key="indexOption">
									<radio :value="String.fromCharCode(65 + indexOption)" color="#0094e5"
										style="transform:scale(0.7)" disabled
										:checked="question.answers[0].answer.includes(String.fromCharCode(65 + indexOption))" />
									<div class="option-text"
										v-html="`${String.fromCharCode(65 + indexOption)}、${option}`" />
								</label>
							</radio-group>
						</view>

						<!-- 多选 -->
						<view class="question-option" v-if="question.type === 2">
							<checkbox-group>
								<label class="option-item" v-for="(option, indexOption) in question.options"
									:key="indexOption">
									<checkbox :value="String.fromCharCode(65 + indexOption)" color="#0094e5"
										style="transform:scale(0.7);" disabled
										:checked="question.answers[0].answer.includes(String.fromCharCode(65 + indexOption))" />
									<div class="option-text"
										v-html="`${String.fromCharCode(65 + indexOption)}、${option}`" />
								</label>
							</checkbox-group>
						</view>
						
						<!-- 填空 -->
						<!-- <view class="question-option" v-if="question.type === 3">
							<view v-for="(option, indexOption) in question.answers" :key="indexOption">
								<u--input border="bottom" disabled style="backgroundColor: #fff"
									v-model='option.answer.join(",")'></u--input>
							</view>
						</view> -->

						<!-- 判断 -->
						<view class="question-option" v-if="question.type === 4">
							<radio-group>
								<label class="option-item" v-for="(option, indexOption) in ['对','错']"
									:key="indexOption">
									<radio :value="String.fromCharCode(65 + indexOption)" color="#0094e5"
										style="transform:scale(0.7)" disabled
										:checked="question.answers[0].answer.includes(option)" />
									<div class="option-text"
										v-html="`${String.fromCharCode(65 + indexOption)}、${option}`" />
								</label>
							</radio-group>
						</view>

						<!-- 阅读 -->
						<view class="question-textarea" v-if="question.type === 5">
							<view v-if="question.ai === 1" class="answer-textarea">
								{{question.answers[0].answer.join('、')}}
							</view>
							<view v-else class="answer-textarea" v-html="question.answers[0].answer"></view>
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
			<view class="right" v-if="!isRecite">
				<view class="status">
					<u-icon name="checkmark-circle-fill" color="#09c8bd"></u-icon>
					<text>{{successNum}}</text>
				</view>
				<view class="status">
					<u-icon name="close-circle-fill" color="#eb5b5b"></u-icon>
					<text>{{errorNum}}</text>
				</view>
			</view>
		</view>
		
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
		questionTypeOpenQuestionIds,
		questionTypeOpenQuestionGet
	} from '@/api/question.js'
	import {
		setTitle
	} from '@/common/setTitle'
	export default {
		data() {
			return {
				height: 0,
				current: 0,
				duration: 500,
				commentState: 0,
				currentIndex: 0,
				swiperCurrent: 0,
				questionTypeId: null,
				questionIds: [],
				commentList: [],
				questionList: [],
				initQuestionIds: [],
				questionDetail: {},
				list: ['答题', '背题'],
				isRecite: false,
				routerShow: false,
			};
		},	
		computed: {
			errorNum() {
				return this.questionList.filter(question => question.correct !== undefined && !question.correct).length
			},
			successNum() {
				return this.questionList.filter(question => question.correct !== undefined && question.correct).length
			},
			optionColor(index) {
				return (index) => {
					const questionDetail = this.questionList[this.swiperCurrent]
					// 单选
					if (questionDetail.type === 1) {
						// 选择完毕且与正确答案不匹配
						if (
							questionDetail.selected === String.fromCharCode(65 + index) &&
							String.fromCharCode(65 + index) !==
							questionDetail.answers[0].answer
						) {
							return '#FF5722'
						}

						// 正确答案
						if (
							questionDetail.answers[0].answer ===
							String.fromCharCode(65 + index)
						) {
							return '#5FB878'
						}
					}

					// 判断
					if (questionDetail.type === 4) {
						// 选择完毕且与正确答案不匹配
						if (
							questionDetail.selected === index &&
							index !== questionDetail.answers[0].answer
						) {
							return '#FF5722'
						}

						// 正确答案
						if (questionDetail.answers[0].answer === index) {
							return '#5FB878'
						}
					}

					// 多选
					if (questionDetail.type === 2) {
						// 选择完毕且与正确答案不匹配
						if (
							questionDetail.selected.includes(
								String.fromCharCode(65 + index)
							) &&
							!questionDetail.answers[0].answer.includes(
								String.fromCharCode(65 + index)
							)
						) {
							return '#FF5722'
						}

						// 正确答案
						if (
							questionDetail.answers[0].answer.includes(
								String.fromCharCode(65 + index)
							)
						) {
							return '#5FB878'
						}
					}
				}
			}
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
				questionTypeId,
				commentState
			} = JSON.parse(options.params)
			this.questionTypeId = questionTypeId
			this.commentState = commentState
			this.query()
		},
		mounted() {
			setTitle(getApp().globalData.entName)
		},
		mounted() {
			setTitle(getApp().globalData.entName)
		},
		methods: {
			// 切换模拟类型
			sectionChange(index) {
				this.current = index
				this.isRecite = index === 0 ? false : true
			},

			// 获取试题列表
			async query() {
				const {
					data
				} = await questionTypeOpenQuestionIds({
					questionTypeId: this.questionTypeId
				})
				this.questionIds = data
				this.initQuestionIds = data
				this.questionList.length = this.questionIds.length
				this.questionList.fill({}, 0, this.questionIds.length)
				this.setQuestionList(this.questionIds[0])
			},

			// 更新试题列表
			async setQuestionList(questionId) {
				const index = this.questionIds.findIndex((item) => item === questionId)
				if (Object.keys(this.questionList[index]).length) {
					return
				}
				const questionDetail = await this.getQuestionDetail(questionId)

				let selected
				if ([1, 4, 5].includes(questionDetail.type)) {
					selected = ''
				}

				if (questionDetail.type === 2 || questionDetail.type === 3) {
					selected = []
				}

				this.$set(this.questionList, index, {
					...questionDetail,
					selected,
					finish: false
				})
			},

			// 获取试题信息
			async getQuestionDetail(questionId) {
				const res = await questionTypeOpenQuestionGet({
					questionId
				})
				if (res?.code !== 200) {
					uni.$u.toast('获取详情失败！请重试')
					return
				}
				// 填空题处理
				if (res.data.type === 3 ){
					res.data.titleList = this.splitTxtForSpaces(this.getTxtForHtml(res.data.title), /_{5,}/)
				}
				return res.data
			},
			
			// html中提取文本
			getTxtForHtml(html) {
				return html.replace(/<.*?>/g, "");
			},
			// 将文本按照分割符分割成列表
			splitTxtForSpaces(txtStr, decollator) {
				return txtStr.split(decollator);
			},

			// 页面左右滑动
			swiperChange(e) {
				this.swiperCurrent = e.detail.current
				this.setQuestionList(this.questionIds[e.detail.current])
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
				this.setQuestionList(this.questionIds[index + 1])
			},

			// 校验答案
			checkAnswer(value, questionType) {
				let flag
				const questionDetail = this.questionList[this.swiperCurrent]
				// 单选、判断
				if ([1, 4].includes(questionType)) {
					questionDetail.selected = value
					flag = value === questionDetail.answers[0].answer
					questionDetail.finish = true
				}

				// 多选
				if (questionDetail.type === 2) {
					questionDetail.selected = value
					// 正确答案数组
					const answers = questionDetail.answers[0].answer.reduce(
						(acc, cur) => {
							acc.push(cur)
							return acc
						},
						[]
					)
					// 所选项与正确答案是否一致
					if (questionDetail.selected.length !== answers.length) {
						const _flag = questionDetail.selected.some((item) =>
							answers.includes(item)
						)
						if (!_flag) {
							flag = false
							questionDetail.finish = true
						}
					} else {
						const _flag = questionDetail.selected.every((item) =>
							answers.includes(item)
						)
						flag = !!_flag
						questionDetail.finish = true
					}
				}

				// 填空
				if (questionDetail.type === 3) {
					// 智能
					if (questionDetail.ai === 1) {
						flag = questionDetail.answers.some((question, index) => {
							question.answer.join(',').includes(questionDetail.selected[index])
						})
					}
				}

				// 问答
				if (questionDetail.type === 5) {
					// 智能
					if (questionDetail.ai === 1) {
						// 正确答案数组
						const answers = questionDetail.answers[0].answer.reduce(
							(acc, cur) => {
								acc.push(cur)
								return acc
							},
							[]
						)
						flag = answers.some((item) =>
							questionDetail.selected.includes(item)
						)
					}
				}

				questionDetail.correct = flag

				if (flag) {
					setTimeout(() => {
						this.nextQuestion()
					}, 300)
				}
			}
		}
	}
</script>

<style lang="scss" scoped>
	@import '@/common/css/exam.scss';

	/deep/ uni-checkbox .uni-checkbox-input.uni-checkbox-input-disabled {
		background-color: #fff;
	}

	/deep/ uni-checkbox .uni-checkbox-input.uni-checkbox-input-disabled:before {
		color: #007aff;
	}	
	.answer {
		display: inline-block;
		border-bottom: 0.5px solid #000;
		padding: 0 20px;
		color: rgb(48, 49, 51);
	}
</style>
