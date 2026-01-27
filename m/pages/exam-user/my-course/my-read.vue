<template>
	<view class="myread">
		<view class="myread-head">
			<image class="myread-head__bg" src="@/static/img/home-bg.png"></image>
			<view class="user">
				<view class="user__head">
					<view class="user__icon">
						<uni-icons customPrefix="iconfont" type="icon-icon-people" color="white" size="26rpx"></uni-icons>
					</view>
					<text class="user__title">用户信息</text>
				</view>
				<view class="user__main">
					<image class="user__avatar" src="@/static/img/user-avatar.png"></image>
					<view class="user__wrapper">
						<view class="user__outer">
							<view class="user__inner">
								<text class="user__label">账号：</text>
								<text class="user__value">{{ user.loginName || '-' }}</text>
							</view>
						</view>
						<view class="user__outer">
							<view class="user__inner">
								<text class="user__label">姓名：</text>
								<text class="user__value">{{ user.name || '-' }}</text>
							</view>
							<view class="user__inner">
								<text class="user__label">机构：</text>
								<text class="user__value">{{ user.orgName || '-' }}</text>
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>
		<view class="myread-main">
			<scroll-view scroll-y="true" class="myread-main__scroll" :style="{ height: myreadMainHeight + 'px' }">
				<view class="warn">
					<view class="warn__head">
						<view class="warn__icon">
							<uni-icons customPrefix="iconfont" type="icon-wodekaoshi" color="white" size="26rpx"></uni-icons>
						</view>
						<text class="warn__title">注意事项</text>
					</view>
					<view class="warn__main" style="font-size: 26rpx">
						<view>1、本课程视频已禁用快进等操作，请勿尝试违规行为，否则可能导致本次学习无效</view>
						<view>2、学习过程中如出现答题界面，请认真作答；仅当答题成功后方可继续观看，未完成答题将无法标记课程为“已完成”</view>
					</view>
				</view>
				<view class="course">
					<view class="course__head">
						<view class="course__icon">
							<uni-icons customPrefix="iconfont" type="icon-icon-pen" color="white" size="26rpx"></uni-icons>
						</view>
						<text class="course__title">课程信息</text>
					</view>
					<view class="course__main">
						<view class="course__row">
							<text class="course__label">课程名称：</text>
							<text class="course__value">{{ course.name }}</text>
						</view>
						<view class="course__row" style="height: initial">
							<text class="course__label">课程简介：</text>
							<text class="course__value">{{ course.content }}</text>
						</view>
						<view class="course__row">
							<text class="course__label">总题数：</text>
							<text class="course__value">{{ questionNum }}</text>
						</view>
						<view class="course__row">
							<text class="course__label">已答题：</text>
							<text class="course__value">{{ questionNum - unAnsweredQuestionNum }}</text>
						</view>
						<view class="course__row">
							<text class="course__label">未答题：</text>
							<text class="course__value">{{ unAnsweredQuestionNum }}</text>
						</view>
						<view class="course__row">
							<text class="course__label">课程进度：</text>
							<text class="course__value">{{ watchedVideoNum }}/{{ videoNum }}</text>
						</view>
					</view>
				</view>
				<view>
					<button v-if="showBtn" class="myread-foot__btn myread-foot__btn--active" type="primary" @click="toStudy">
						<text>去学习</text>
					</button>
					<view style="height: 20rpx"></view>
				</view>
			</scroll-view>
		</view>
		<view class="myread-foot"></view>
	</view>
</template>

<script lang="ts" setup>
import { ref, reactive, computed } from 'vue';
import { onLoad, onReady, onShow } from '@dcloudio/uni-app';
import { User } from '@/ts/user.d';
import { PaperStatis } from '@/ts/paper.d';
import { userGet } from '@/api/user';
import { loginSysTime } from '@/api/login';
import { myCourseCourseListpage, myCourseGenerate, myCourseList } from '@/api/my-course';
import { Page } from '@/ts/page.d';
import dayjs from 'dayjs';
import Decimal from 'decimal.js';

/************************变量定义相关***********************/
// 变量定义
const courseId = ref(0);
const myreadMainHeight = ref(0);
const user = reactive<User>({
	id: null,
	name: '',
	loginName: '',
	orgName: ''
}); // 用户
const course = reactive({
	name: '',
	content: ''
}); // 课程信息
const myCourseMaterials = ref<MyCourseMaterial[]>([]);
const showBtn = ref(false); // 按钮显示

/************************组件生命周期相关*********************/
onLoad(async (options) => {
	courseId.value = options.courseId;
	userQuery();
	courseQuery();
	await courseGenerate();
	myCourseListQuery();
	showBtn.value = true;
});
onShow(async () => {
	// 进入课程后返回来需要
	userQuery();
	courseQuery();
	await courseGenerate();
	myCourseListQuery();
	showBtn.value = true;
});
onReady(() => {
	uni.createSelectorQuery()
		.select('.myread-main__scroll')
		.boundingClientRect((data: any) => {
			myreadMainHeight.value = uni.getWindowInfo().windowHeight - data.top - 10;
		})
		.exec();
});

/************************计算属性相关*************************/
const questionNum = computed(() => {
	// 总题数
	return myCourseMaterials.value.reduce((total, myCourseMaterial) => {
		return (total + myCourseMaterial.questions?.length) as number;
	}, 0);
});
const unAnsweredQuestionNum = computed(() => {
	// 未答题数
	return myCourseMaterials.value.reduce((total, myCourseMaterial) => {
		return total + myCourseMaterial.questions.filter((q) => !q.answerTime).length;
	}, 0);
});
const videoNum = computed(() => {
	// 总资料数
	return myCourseMaterials.value.length;
});
const watchedVideoNum = computed(() => {
	// 已完成学习数量
	return myCourseMaterials.value.filter((myCourseMaterial) => myCourseMaterial.state === 1).length;
});

/************************事件相关*****************************/
// 用户查询
async function userQuery() {
	let { data } = await userGet({});
	user.id = data.id;
	user.name = data.name;
	user.loginName = data.loginName;
	user.orgName = data.orgName;
}

// 课程查询
async function courseQuery() {
	const { data } = await myCourseCourseListpage({ courseId: courseId.value });
	course.name = data.list[0].name;
	course.content = data.list[0].content;
}

// 课程生成
async function courseGenerate() {
	await myCourseGenerate({ courseId: courseId.value });
}

// 我的课程列表查询
async function myCourseListQuery() {
	const { data } = await myCourseList({ courseId: courseId.value });
	myCourseMaterials.value = data;
}

// 去学习
async function toStudy() {
	uni.navigateTo({
		url: `/pages/exam-user/my-course/my-paper?courseId=${courseId.value}`
	});
}
</script>

<style lang="scss" scoped>
.myread {
	height: inherit;
	display: flex;
	flex-direction: column;

	.myread-head {
		display: flex;
		flex-direction: column;
		align-items: center;
		height: 230rpx;
		position: relative;
		margin-bottom: 40rpx;

		.myread-head__bg {
			position: absolute;
			width: 750rpx;
			height: 230rpx;
		}

		.user {
			display: flex;
			flex-direction: column;
			position: absolute;
			width: 710rpx;
			height: 230rpx;
			bottom: -20rpx;
			z-index: 0;
			box-shadow: 0rpx 10rpx 20rpx 0rpx rgba(0, 0, 0, 0.2);
			border-radius: 30rpx 30rpx 30rpx 30rpx;
			background: linear-gradient(to bottom, #bff3ff 0%, #e1f2ff 100%);
			overflow: hidden;

			.user__head {
				display: flex;
				align-items: center;
				padding: 0rpx 30rpx;
				height: 80rpx;
				background: linear-gradient(to bottom, #bff3ff 0%, #b3eeff 100%);

				//border: 1rpx solid red;
				.user__icon {
					display: flex;
					justify-content: center;
					align-items: center;
					height: 42rpx;
					width: 42rpx;
					background: linear-gradient(to bottom right, #04c7f2 0%, #259ff8 100%);
					border-radius: 8rpx 8rpx 8rpx 8rpx;
				}

				.user__title {
					margin-left: 20rpx;
					font-weight: bold;
					font-size: 32rpx;
					color: #333333;
				}
			}

			.user__main {
				flex: 1;
				display: flex;
				align-items: center;
				margin: 0rpx 50rpx;

				.user__avatar {
					width: 100rpx;
					height: 100rpx;
					margin-right: 50rpx;
				}

				.user__wrapper {
					flex: 1;
					display: flex;
					flex-direction: column;

					.user__outer {
						display: flex;
						justify-content: space-between;

						.user__inner {
							display: flex;

							.user__label {
								font-size: 26rpx;
								color: #8f939c;
								line-height: 48rpx;
							}

							.user__value {
								font-size: 26rpx;
								color: #333333;
								line-height: 48rpx;
								max-width: 160rpx; /* 最大宽度 100rpx */
								white-space: nowrap; /* 禁止换行 */
								overflow: hidden; /* 隐藏溢出 */
								text-overflow: ellipsis; /* 溢出显示省略号 */
							}
						}
					}
				}
			}
		}
	}

	.myread-main {
		flex: 1;
		display: flex;
		flex-direction: column;
		align-items: center;

		margin: 0rpx 20rpx 20rpx 20rpx;
		overflow: hidden;
		box-shadow: 0rpx 10rpx 20rpx 0rpx rgba(0, 0, 0, 0.2);
		border-radius: 30rpx 30rpx 30rpx 30rpx;

		.myread-main__scroll {
			.warn {
				display: flex;
				flex-direction: column;
				padding: 30rpx 0rpx;
				margin-bottom: 20rpx;
				background-color: white;
				box-shadow: 0rpx 10rpx 20rpx 0rpx rgba(0, 0, 0, 0.2);
				border-radius: 16rpx;
				overflow: hidden;

				.warn__head {
					display: flex;
					align-items: center;
					padding: 0rpx 30rpx;
					height: 80rpx;

					.warn__icon {
						display: flex;
						justify-content: center;
						align-items: center;
						height: 42rpx;
						width: 42rpx;
						background: linear-gradient(to bottom right, #04c7f2 0%, #259ff8 100%);
						border-radius: 8rpx 8rpx 8rpx 8rpx;
					}

					.warn__title {
						margin-left: 20rpx;
						font-weight: bold;
						font-size: 32rpx;
						color: #333333;
					}
				}

				.warn__main {
					padding: 0rpx 30rpx;
					font-size: 22rpx;
					line-height: 42rpx;
					color: #e43d33;
				}
			}

			.course {
				display: flex;
				flex-direction: column;
				padding: 30rpx 0rpx;
				margin-bottom: 20rpx;
				background-color: white;
				box-shadow: 0rpx 10rpx 20rpx 0rpx rgba(0, 0, 0, 0.2);
				border-radius: 16rpx;
				overflow: hidden;

				.course__head {
					display: flex;
					align-items: center;
					padding: 0rpx 30rpx;
					height: 80rpx;

					//border: 1rpx solid red;
					.course__icon {
						display: flex;
						justify-content: center;
						align-items: center;
						height: 42rpx;
						width: 42rpx;
						background: linear-gradient(to bottom right, #04c7f2 0%, #259ff8 100%);
						border-radius: 8rpx 8rpx 8rpx 8rpx;
					}

					.course__title {
						margin-left: 20rpx;
						font-weight: bold;
						font-size: 32rpx;
						color: #333333;
					}
				}

				.course__main {
					padding: 0rpx 30rpx;

					.course__row {
						height: 60rpx;

						.course__label {
							font-size: 26rpx;
							color: #8f939c;
							line-height: 60rpx;
						}

						.course__value {
							font-size: 26rpx;
							color: #333333;
							line-height: 60rpx;
						}
					}
				}
			}
			.my-course-list {
				display: flex;
				flex-direction: column;
				padding: 30rpx 0rpx;
				margin-bottom: 20rpx;
				background-color: white;
				box-shadow: 0rpx 10rpx 20rpx 0rpx rgba(0, 0, 0, 0.2);
				border-radius: 16rpx;
				overflow: hidden;

				.my-course-list__head {
					display: flex;
					align-items: center;
					padding: 0rpx 30rpx;
					height: 80rpx;

					//border: 1rpx solid red;
					.my-course-list__icon {
						display: flex;
						justify-content: center;
						align-items: center;
						height: 42rpx;
						width: 42rpx;
						background: linear-gradient(to bottom right, #04c7f2 0%, #259ff8 100%);
						border-radius: 8rpx 8rpx 8rpx 8rpx;
					}

					.my-course-list__title {
						margin-left: 20rpx;
						font-weight: bold;
						font-size: 32rpx;
						color: #333333;
					}
				}

				.my-course-list__main {
					.my-course-list__name {
						font-size: 26rpx;
						color: #3b4144;
					}
					.my-course-list__sub {
						margin-top: 4rpx;
					}
					.my-course-list__lable {
						font-size: 24rpx;
						color: #999;
					}
					.my-course-list__value {
						font-size: 24rpx;
						color: #3b4144;
						margin-right: 30rpx;
					}
				}
			}

			.my-course {
				display: flex;
				flex-direction: column;
				padding: 30rpx 0rpx;
				margin-bottom: 20rpx;
				background-color: white;
				box-shadow: 0rpx 10rpx 20rpx 0rpx rgba(0, 0, 0, 0.2);
				border-radius: 16rpx;
				overflow: hidden;

				.my-course__head {
					display: flex;
					align-items: center;
					padding: 0rpx 30rpx;
					height: 80rpx;

					.my-course__icon {
						display: flex;
						justify-content: center;
						align-items: center;
						height: 42rpx;
						width: 42rpx;
						background: linear-gradient(to bottom right, #04c7f2 0%, #259ff8 100%);
						border-radius: 8rpx 8rpx 8rpx 8rpx;
					}

					.my-course__title {
						margin-left: 20rpx;
						font-weight: bold;
						font-size: 32rpx;
						color: #333333;
					}
				}

				.my-course__main {
					padding: 0rpx 30rpx;
					.form {
					}
					.my-course__row {
						display: flex;
						align-items: center;
						.my-course__value {
							width: 200rpx;
						}
					}

					.my-course__btn {
						display: flex;
						flex-direction: row;
						justify-content: center;
						align-items: center;
						width: 100%;
						height: 100rpx;
						border-radius: 50px;
						line-height: 40rpx;
					}

					.my-course__btn--active {
						background: linear-gradient(to right, #04c7f2 0%, #259ff8 100%);
					}

					.my-course__btn--disable {
						border: 1rpx solid #04c7f2;
						color: #04c7f2;
						background: linear-gradient(to right, #fff 0%, #fff 100%);
					}

					// display: flex;
					// flex-direction: column;

					// .my-course__row {
					// 	display: flex;
					// 	height: 60rpx;
					// 	.my-course__label {
					// 		font-size: 26rpx;
					// 		color: #8f939c;
					// 		line-height: 60rpx;
					// 	}
					// 	.my-course__value {
					// 		font-size: 26rpx;
					// 		color: #333333;
					// 		line-height: 60rpx;
					// 	}
					// }
				}
			}

			.myread-foot__btn {
				display: flex;
				flex-direction: column;
				justify-content: center;
				align-items: center;
				// width: 100%;
				height: 100rpx;
				margin: 30rpx 10rpx;
				border-radius: 50px;
				line-height: 40rpx;
				.myread-foot__time-count-down {
					line-height: 30rpx;
					font-size: 24rpx;
				}
			}
			.myread-foot__btn--active {
				background: linear-gradient(to right, #04c7f2 0%, #259ff8 100%);
			}
		}
	}

	.myread-foot {
	}
}
</style>
