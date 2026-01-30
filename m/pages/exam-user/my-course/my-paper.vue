<template>
	<view class="mypaper">
		<view class="mypaper__head">
			<video
				v-show="!answerShow"
				id="videoPlayerRef"
				:src="videoOptions.src"
				:enable-progress-gesture="false"
				class="mypaper__video"
				@timeupdate="onTimeupdate"
				@ended="finish"
			></video>
			<view v-if="curCourseQuestion && answerShow" class="mypaper__question">
				<xmky-question
					v-model="curCourseQuestion.userAnswers"
					:type="curCourseQuestion?.questionType"
					:title="curCourseQuestion.title"
					:img-ids="curCourseQuestion.imgFileIds"
					:video-id="curCourseQuestion.videoFileId"
					:options="curCourseQuestion.options"
					:answers="curCourseQuestion.answers"
					:mark-type="curCourseQuestion.markType"
					:score="curCourseQuestion.score"
					:analysis="curCourseQuestion.analysis"
					:user-score="curCourseQuestion?.userScore"
					:answer-show="false"
					:user-answer-show="false"
					:analysis-show="false"
					:editable="true"
					@change="(userAnswers: string[]) => {
						(curCourseQuestion as ExamQuestion).userAnswers = userAnswers
					}"
				></xmky-question>
				<button class="mypaper__btn" @click="answer">确认作答</button>
			</view>
		</view>
		<view v-if="!answerShow" class="mypaper-main">
			<view :style="{ height: scrollHeight + 'px' }" class="mypaper-main__scroll">
				<scroll-view scroll-y="true" style="height: 100%">
					<text class="course-title">{{ course.name }}</text>
					<view class="course-list">
						<view
							v-for="(myCourseMaterial, index) in myCourseMaterials"
							:key="index"
							:class="[
								'course-list__row',
								{ 'course-list__row--finish': myCourseMaterial.state === 1 },
								{ 'course-list__row--watch': curMyCourseMaterial && curMyCourseMaterial?.courseMaterialId === myCourseMaterial.courseMaterialId }
							]"
							@click="
								async () => {
									videoPlayerRef.pause();
									curMyCourseMaterial = myCourseMaterial;
									videoOptions.src = `${host}/file/download?id=${curMyCourseMaterial.videoFileId}`;
									videoOptions.title = `${curMyCourseMaterial.name}`;
									videoOptions.currentTime = 0;
									lastTime = 0;
									triggeredTimes.clear();
									answerShow = false;
									await $nextTick();
									videoPlayerRef.play();
								}
							"
						>
							<uni-icons
								customPrefix="iconfont"
								color=""
								size=""
								:type="myCourseMaterial.state === 1 ? 'icon-lianxi-61' : 'icon-dingwei'"
								class="course-list__icon"
							></uni-icons>
							<text class="course-list__txt">{{ myCourseMaterial.name }}</text>
						</view>
					</view>
				</scroll-view>
			</view>
		</view>
		<view class="mypaper-foot"></view>
	</view>
</template>

<script lang="ts" setup>
import { ref, reactive, computed } from 'vue';
import { onLoad, onReady } from '@dcloudio/uni-app';
import type { MyCourseMaterial } from '@/ts/my-course-material';
import { ExamQuestion } from '@/ts/paper.d';
import { myCourseCourseListpage, myCourseList, myCourseAnswer, myCourseFinish, myCourseQuestion } from '@/api/my-course.ts';

/************************变量定义相关***********************/
defineProps({
	courseId: String
});
const pageParm = reactive({
	courseId: 0
}); // 页面参数
const scrollHeight = ref(0); // 试题滚动高度
const host = ref(uni.getStorageSync('BASE_URL'));
const myCourseMaterials = ref<MyCourseMaterial[]>([]); // 我的课程资料列表
const videoPlayerRef = ref();
const videoOptions = reactive({
	src: ''
});
const course = reactive({
	name: '',
	content: ''
}); // 课程
const curMyCourseMaterial = ref<MyCourseMaterial>(); // 当前我的课程资料
const triggeredTimes = new Set<number>(); // 已触发的时间集合
const answerShow = ref(false); // 答题显示
const curCourseQuestion = ref<ExamQuestion>(); // 当前课程试题
const lastTime = ref(0);

/************************组件生命周期相关*********************/
onLoad(async (options) => {
	pageParm.courseId = options.courseId;

	courseQuery();
	await myCourseListQuery();
	const _myCourseMaterial = myCourseMaterials.value.find((material) => material.state === 2 || material.state === 3);
	if (_myCourseMaterial) {
		// 默认显示第一个未开始或进行中的课程资料
		curMyCourseMaterial.value = _myCourseMaterial;
		uni.setNavigationBarTitle({ title: _myCourseMaterial.name });
		videoOptions.src = `${host.value}/file/download?id=${_myCourseMaterial.videoFileId}`;
	} else {
		// 否则重看第一个课程资料
		curMyCourseMaterial.value = myCourseMaterials.value[0];
		uni.setNavigationBarTitle({ title: myCourseMaterials.value[0].name });
		videoOptions.src = `${host.value}/file/download?id=${myCourseMaterials.value[0].videoFileId}`;
	}
});

onReady(() => {
	videoPlayerRef.value = uni.createVideoContext('videoPlayerRef');
	uni.createSelectorQuery()
		.select('.mypaper-main__scroll')
		.boundingClientRect((data: any) => {
			scrollHeight.value = uni.getWindowInfo().windowHeight - data.top - 50;
		})
		.exec();
});

/************************事件相关*****************************/
// 课程查询
async function courseQuery() {
	const { data } = await myCourseCourseListpage({ courseId: pageParm.courseId });
	course.name = data.list[0].name;
	course.content = data.list[0].content;
}

// 我的课程查询
async function myCourseListQuery() {
	const { data } = await myCourseList({ courseId: pageParm.courseId });
	myCourseMaterials.value.push(...data);
}

// 播放器事件回调
async function onTimeupdate(event: any) {
	const playTime = Math.floor(event.detail.currentTime); // 向下取整，避免浮点误差

	if (Math.abs(playTime - lastTime.value) > 2) {
		videoPlayerRef.value.seek(lastTime.value);
		return; // 不允许拖拽看视频
	}
	lastTime.value = playTime;

	if (curMyCourseMaterial.value?.questions?.length) {
		for (const curMyCourseQuestion of curMyCourseMaterial.value?.questions) {
			const triggerTime = timeToSeconds(curMyCourseQuestion.courseTime);
			if (triggerTime === -1) {
				continue;
			}
			if (triggeredTimes.has(triggerTime)) {
				continue;
			}
			if (playTime === triggerTime) {
				answerShow.value = true;
				triggeredTimes.add(triggerTime);
				videoPlayerRef.value.pause();
				// #ifdef H5
				if (document.fullscreenElement) {
					await document.exitFullscreen();
				}
				// #endif
				// #ifdef MP-WEIXIN
				videoPlayerRef.value.exitFullScreen();
				// #endif

				const { data } = await myCourseQuestion({
					courseMaterialId: curMyCourseMaterial.value.courseMaterialId,
					questionId: curMyCourseQuestion.questionId
				});
				curCourseQuestion.value = data;
				break;
			}
		}
	}
}

// 答题
async function answer() {
	const { code, data } = await myCourseAnswer({
		courseMaterialId: curMyCourseMaterial.value?.courseMaterialId,
		questionId: curCourseQuestion.value?.questionId,
		userAnswers: curCourseQuestion.value?.userAnswers
	});

	if (code !== 200) {
		return;
	}

	if (!data) {
		uni.showToast({ title: '回答错误', icon: 'error' });
		return;
	}

	answerShow.value = false;
	videoPlayerRef.value.play();
}

// 完成
async function finish() {
	const { code, data } = await myCourseFinish({
		courseMaterialId: curMyCourseMaterial.value?.courseMaterialId
	});
	if (code != 200) {
		return;
	}

	if (curMyCourseMaterial.value) {
		curMyCourseMaterial.value.state = 1;
	}
}
// 小时分秒转秒数
function timeToSeconds(timeStr: string): number {
	if (!timeStr) return -1;
	const parts = timeStr.split(':').map(Number);
	return parts[0] * 3600 + parts[1] * 60 + parts[2];
}
</script>

<style lang="scss" scoped>
.mypaper {
	height: inherit;
	background-color: white;
	display: flex;
	flex-direction: column;
	.mypaper__head {
		.mypaper__video {
			width: 100%;
		}
		.mypaper__question {
			padding: 30rpx;
		}
		.mypaper__btn {
			display: flex;
			flex-direction: row;
			justify-content: center;
			align-items: center;
			width: 100%;
			height: 100rpx;
			// border-radius: 50px;
			line-height: 40rpx;
			color: #fff;
			background: linear-gradient(to right, #04c7f2 0%, #259ff8 100%);
		}
	}
	.mypaper-main {
		.mypaper-main__scroll {
			padding-bottom: max(20px, env(safe-area-inset-bottom, 20px));
			padding: 30rpx 30rpx 0rpx 30rpx;
			.course-title {
				font-size: 34rpx;
			}
			.course-list {
				margin-top: 10rpx;
				.course-list__row {
					display: flex;
					align-items: center;
					height: 86rpx;
					border-bottom: 1rpx dashed #999999;
					color: #999999;
					font-size: 34rpx;
					&.course-list__row--watch {
						color: #04c7f2 !important;
						font-weight: bold;
					}
					&.course-list__row--finish {
						color: #1ac693;
					}
					.course-list__icon {
						font-size: 38rpx;
					}
					.course-list__txt {
						margin-left: 10rpx;
					}
				}
			}
		}
	}
	.mypaper-foot {
	}
}
</style>
