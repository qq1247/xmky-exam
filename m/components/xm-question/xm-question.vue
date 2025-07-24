<template>
	<view class="question">
		<!-- 题干（如果是填空题，直接在题干答题） -->
		<view class="question-title">
			<slot name="title-pre"></slot>
			<template v-for="(title, index) in titles as Title[]" :key="index">
				// #ifdef H5
				<text v-if="title.type === 'txt'" class="question-title__text" v-html="title.value"></text>
				// #endif // #ifdef MP-WEIXIN
				<text v-if="title.type === 'txt'" class="question-title__text" decode="true">
					{{
						title.value
							.replace(/&amp;times;/g, '×')
							.replace(/&amp;divide;/g, '÷')
							.replace(/&amp;plusmn;/g, '±')
					}}
				</text>
				// #endif
				<view v-else class="question-title__fill-blank">
					<input
						v-model="userAnswers[title.index]"
						@input="
							() => {
								$emit('update:modelValue', userAnswers);
								$emit('change', userAnswers);
							}
						"
						:disabled="!editable"
						:style="{ width: (title.value.length > 20 ? 20 : title.value.length) * 26 + 50 + 'rpx' }"
					/>
					<text :class="['question-title__score', { 'question-title__score--err': userScore !== score }, { 'question-title__score--succ': userScore === score }]"></text>
				</view>
			</template>
			<slot name="title-post"></slot>
		</view>
		<view class="question__img-group">
			<view v-for="(imgId, index) in imgIds" :key="index" class="question__img-inner">
				<image :src="`${host}/file/download?id=${imgId}`" mode="aspectFit" view @click="preview(index)" class="question__img"></image>
				<text>图{{ toChinaNum(index + 1) }}</text>
			</view>
		</view>
		<view>
			<video v-if="videoId" :src="`${host}/file/download?id=${videoId}`" class="question__video"></video>
		</view>
		<!-- 单选题选项 -->
		<radio-group
			v-if="type === 1"
			@change="(e: any) => { userAnswers[0] = e.detail.value; $emit('update:modelValue', userAnswers); $emit('change', userAnswers) }"
			class="question-option__radio-wrap"
		>
			<label
				v-for="(option, index) in options"
				:key="index"
				:class="['question-option', { 'is-checked': isChecked(optionLabs(index)) }, { 'is-succ': isSucc(optionLabs(index)) }, { 'is-err': isErr(optionLabs(index)) }]"
			>
				<radio :value="optionLabs(index)" :checked="isChecked(optionLabs(index))" :disabled="!editable" class="question-option__radio-hover" />
				<view class="question-option__radio">
					<view class="question-option__radio-inner"></view>
				</view>
				// #ifdef H5
				<text class="question-option__content" v-html="`${optionLabs(index)}、${option}`"></text>
				// #endif // #ifdef MP-WEIXIN
				<text class="question-option__content" decode="true">
					{{ optionLabs(index) }}、{{
						option
							.replace(/&amp;times;/g, '×')
							.replace(/&amp;divide;/g, '÷')
							.replace(/&amp;plusmn;/g, '±')
					}}
				</text>
				// #endif
			</label>
		</radio-group>
		<!-- 多选题选项 -->
		<checkbox-group
			v-if="type === 2"
			@change="(e: any) => { userAnswers = e.detail.value; $emit('update:modelValue', userAnswers); $emit('change', userAnswers) }"
			class="question-option__checkbox-wrap"
		>
			<label
				v-for="(option, index) in options"
				:key="index"
				:class="['question-option', { 'is-checked': isChecked(optionLabs(index)) }, { 'is-succ': isSucc(optionLabs(index)) }, { 'is-err': isErr(optionLabs(index)) }]"
			>
				<checkbox :value="optionLabs(index)" :checked="isChecked(optionLabs(index))" :disabled="!editable" class="question-option__checkbox-hover" />
				<view class="question-option__checkbox">
					<view class="question-option__checkbox-inner"></view>
				</view>
				// #ifdef H5
				<text class="question-option__content" v-html="`${optionLabs(index)}、${option}`"></text>
				// #endif // #ifdef MP-WEIXIN
				<text class="question-option__content" decode="true">
					{{ optionLabs(index) }}、{{
						option
							.replace(/&amp;times;/g, '×')
							.replace(/&amp;divide;/g, '÷')
							.replace(/&amp;plusmn;/g, '±')
					}}
				</text>
				// #endif
			</label>
		</checkbox-group>
		<!-- 填空题（题干处填空） -->
		<!-- 判断题选项 -->
		<radio-group
			v-if="type === 4"
			@change="(e: any) => { userAnswers[0] = e.detail.value; $emit('update:modelValue', userAnswers); $emit('change', userAnswers) }"
			class="question-option__radio-wrap"
		>
			<label
				v-for="(option, index) in judges"
				:key="index"
				:class="['question-option', { 'is-checked': isChecked(optionLabs(index)) }, { 'is-succ': isSucc(optionLabs(index)) }, { 'is-err': isErr(optionLabs(index)) }]"
			>
				<radio :value="option" :checked="isChecked(optionLabs(index))" :disabled="!editable" class="question-option__radio-hover" />
				<view class="question-option__radio">
					<view class="question-option__radio-inner"></view>
				</view>
				<text class="question-option__content">{{ option }}</text>
			</label>
		</radio-group>
		<!-- 问答题答案 -->
		<textarea
			v-if="type === 5"
			v-model="userAnswers[0]"
			@input="
				() => {
					$emit('update:modelValue', userAnswers);
					$emit('change', userAnswers);
				}
			"
			type="textarea"
			placeholder="请输入答案"
			:inputBorder="false"
			:disabled="!editable"
			:styles="{
				backgroundColor: '#F6F7FC'
			}"
			maxlength="999"
			class="question-user-answer"
		></textarea>
		<slot name="user-answer-post"></slot>
		<!-- 分数 -->
		<view v-if="analysisShow" class="question-score">
			<view>
				<uni-icons customPrefix="iconfont" type="icon-defen" color="#0d9df6" size="34rpx"></uni-icons>
				<text class="question-score__label">得分:</text>
			</view>
			<view>
				<text :class="['question-score__user', { 'question-score__user--err': userScore === 0 }, { 'question-score__user--succ': userScore === score }]">
					{{ userScore }}分
				</text>
				<text class="question-score__standard">/{{ score }}分</text>
			</view>
		</view>
		<!-- 答案 -->
		<view v-if="analysisShow && (type === 1 || type === 2 || type === 4)" class="question-select-answer">
			<view class="question-select-answer__diff">
				<text class="question-select-answer__label">作答答案</text>
				<text
					:class="[
						'question-select-answer__value',
						{ 'question-select-answer__value--err': userScore === 0 },
						{ 'question-select-answer__value--succ': userScore === score }
					]"
				>
					{{ userAnswers.join('') || '-' }}
				</text>
			</view>
			<view class="question-select-answer__diff">
				<text class="question-select-answer__label">标准答案</text>
				<text class="question-select-answer__value question-select-answer__value--succ">{{ answers.join('') }}</text>
			</view>
		</view>
		<view v-else-if="analysisShow && type === 3" class="question-fill-blank-answer">
			<text class="question-fill-blank-answer__label">标准答案</text>
			<view class="question-title">
				<template v-for="(title, index) in titles as Title[]" :key="index">
					<text v-if="title.type === 'txt'" class="question-title__text">{{ title.value }}</text>
					<view v-else class="question-title__fill-blank">
						<input :value="answers[title.index]" :disabled="!editable" :style="{ width: (title.value.length > 20 ? 20 : title.value.length) * 26 + 50 + 'rpx' }" />
						<text class="question-title__score"></text>
					</view>
				</template>
			</view>
		</view>
		<view v-else-if="analysisShow && type === 5" class="question-qa-answer">
			<text class="question-qa-answer__label">标准答案</text>
			<view>
				<text class="question-qa-answer__content">{{ answers[0] }}</text>
			</view>
		</view>
		<!-- 解析 -->
		<view v-if="analysisShow && analysis" class="question-analysis">
			<view class="question-analysis__title-wrap">
				<view class="question-analysis__title-icon"></view>
				<view class="question-analysis__title-icon1"></view>
				<text class="question-analysis__title">解析</text>
			</view>
			<text class="question-analysis__content">{{ analysis || '无' }}</text>
		</view>
	</view>
</template>

<script lang="ts" setup>
import { computed, ref, watch } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { Title } from '@/ts/question.d';
import { toChinaNum } from '@/util/numberUtil';
// #ifdef H5
import Prism from 'prismjs'; // uniapp对prismjs支持不好，手动导入常见样式
import 'prismjs/plugins/match-braces/prism-match-braces';
import 'prismjs/plugins/copy-to-clipboard/prism-copy-to-clipboard';
import 'prismjs/plugins/line-numbers/prism-line-numbers.css';
import 'prismjs/themes/prism-tomorrow.css';
import 'prismjs/components/prism-javascript';
import 'prismjs/components/prism-markup';
import 'prismjs/components/prism-css';
import 'prismjs/components/prism-clike';
import 'prismjs/components/prism-javascript';
import 'prismjs/components/prism-c';
import 'prismjs/components/prism-csharp';
import 'prismjs/components/prism-cpp';
import 'prismjs/components/prism-csv';
import 'prismjs/components/prism-diff';
import 'prismjs/components/prism-docker';
import 'prismjs/components/prism-http';
import 'prismjs/components/prism-java';
import 'prismjs/components/prism-javadoclike';
import 'prismjs/components/prism-javastacktrace';
import 'prismjs/components/prism-json';
import 'prismjs/components/prism-markdown';
import 'prismjs/components/prism-markup-templating';
import 'prismjs/components/prism-php';
import 'prismjs/components/prism-phpdoc';
import 'prismjs/components/prism-python';
import 'prismjs/components/prism-regex';
import 'prismjs/components/prism-sass';
import 'prismjs/components/prism-scss';
import 'prismjs/components/prism-sql';
import 'prismjs/components/prism-typescript';
// #endif
/************************变量定义相关***********************/
const emit = defineEmits<{
	'update:modelValue': string[];
	change: string[];
}>();

const props = withDefaults(
	defineProps<{
		modelValue?: string[]; //用户答案
		title: string; // 题干
		imgIds?: number[]; // 图片IDS
		videoId?: number | null; // 图片IDS
		options?: string[]; // 试题选项
		type: number; // 试题类型（1：单选；2：多选；3：填空；4：判断；5：问答）
		markType: number; // 阅卷方式（1：客观题；2：主观题；
		answers?: string[]; // 标准答案
		score: number; // 分数
		userScore?: number; //用户分数
		analysis?: string; // 解析
		editable?: boolean; // 可编辑（true：是；false：否）
		answerShow?: boolean; // 标准答案显示（true：用户答案显示；false：标准答案显示）
		analysisShow?: boolean; // 解析显示（true：显示；false：不显示）
	}>(),
	{
		modelValue: () => [],
		title: '',
		imgIds: () => [],
		type: 1,
		markType: 1,
		score: 0,
		answers: () => [],
		options: () => [],
		editable: false,
		answerShow: false,
		analysisShow: false
	}
);

const judges = ['对', '错']; // 判断题使用
const userAnswers = ref(escape2Html(props.modelValue)); // 用户答案
const host = ref(uni.getStorageSync('BASE_URL'));

/************************组件生命周期相关*********************/
onLoad(async () => {
	if (props.type === 3 && !userAnswers.value.length) {
		userAnswers.value = props.title.match(/[_]{5,}/g).map(() => ''); // 初始化填空题答案
	}
	if (props.type === 5 && !userAnswers.value.length) {
		userAnswers.value = ['']; // 初始化问答题答案
	}
});

/************************计算属性相关*****************************/
const titles = computed(() => {
	let titleWithHeight = props.title;
	// #ifdef H5
	titleWithHeight = titleWithHeight.replace(/```([a-z]*)\n([\s\S]*?)\n```/g, (match, lang, code) => {
		const higCode = Prism.highlight(escape2Html(code.trim()) as string, Prism.languages[lang] || Prism.languages.plaintext, lang);

		const lineCount = higCode.split('\n').length;
		let lineNumbersRows = '<span aria-hidden="true" class="line-numbers-rows">';
		for (let i = 0; i < lineCount; i++) {
			lineNumbersRows += '<span></span>';
		}
		lineNumbersRows += '</span>';

		return `<pre class="language-${lang} line-numbers match-braces"><code class="language-${lang} ">${higCode}${lineNumbersRows}</code></pre>`;
	});
	// #endif

	// 如果不是填空题，正常返回
	if (props.type !== 3) {
		return [{ type: 'txt', value: titleWithHeight, index: -1 }];
	}

	// 如果是填空题，下划线转输入框，用于在题干填空
	let title = titleWithHeight; // '______AAAAA_____BBBBB____________CCCCC__DDDDD______'
	let titles: Title[] = [];
	let pos = 0;
	title.match(/[_]{5,}/g)?.forEach((value) => {
		// 大于连续的5个下划线算一个填空
		let index = title.indexOf(value);
		if (index > 0) {
			titles.push({ type: 'txt', value: title.substring(0, index), index: -1 }); // 添加文本
		}
		titles.push({ type: 'input', value, index: pos++ }); //添加输入框；pos用于找对应答案的位置
		title = title.substring(index + value.length); // 解析过的部分题干删除,下次循环从剩余题干中找
	});
	if (title) {
		titles.push({ type: 'txt', value: title, index: -1 }); // 最后一节拼接
	}
	return titles;
});

const isChecked = computed(() => (curAnswer: string) => {
	if (!(props.type === 1 || props.type === 2 || props.type === 4)) {
		return false;
	}
	return !props.answerShow ? userAnswers.value.includes(curAnswer) : props.answers.includes(curAnswer);
}); // 指定选项是否选中

const isSucc = computed(() => (curAnswer: string) => {
	if (!props.analysisShow) {
		return false;
	}

	if (!(props.type === 1 || props.type === 2 || props.type === 4)) {
		return false; // 不是单多选判断
	}

	if (!props.answers.includes(curAnswer)) {
		return false;
	}

	return true;
}); // 指定选项是否答对

const isErr = computed(() => (curAnswer: string) => {
	if (!props.analysisShow) {
		return false;
	}

	if (!(props.type === 1 || props.type === 2 || props.type === 4)) {
		return false; // 不是单多选判断
	}
	if (userAnswers.value.includes(curAnswer) && !props.answers.includes(curAnswer)) {
		return true;
	}
	return false;
}); // 指定选项是否答错

const optionLabs = computed(() => (index: number) => {
	if (props.type === 1 || props.type === 2) {
		return String.fromCharCode(65 + index);
	}
	if (props.type === 4) {
		return judges[index];
	}
}); // 获取选项标签

/************************监听相关*****************************/
watch(
	() => props.modelValue,
	() => {
		userAnswers.value = props.modelValue;
	}
);

/************************事件相关*****************************/
// 特殊字符转义
function escape2Html(txt: string | string[]) {
	if (typeof txt === 'string') {
		return txt
			.replaceAll('&lt;', '<')
			.replaceAll('&gt;', '>')
			.replaceAll('&amp;', '&')
			.replaceAll('&quot;', '"')
			.replaceAll('&apos;', "'")
			.replaceAll('&ldquo;', '“')
			.replaceAll('&rdquo;', '”');
	}

	if (Array.isArray(txt)) {
		return (txt as string[]).map((t: string) => {
			return t
				.replaceAll('&lt;', '<')
				.replaceAll('&gt;', '>')
				.replaceAll('&amp;', '&')
				.replaceAll('&quot;', '"')
				.replaceAll('&apos;', "'")
				.replaceAll('&ldquo;', '“')
				.replaceAll('&rdquo;', '”');
		});
	}

	return '';
}

// 预览图片
function preview(index: number) {
	let urls = props.imgIds.map((imgId) => `${host.value}/file/download?id=${imgId}`);
	uni.previewImage({
		current: index,
		urls: urls
	});
}
</script>

<style lang="scss" scoped>
.question {
	.question-title {
		margin-bottom: 20rpx;
		.question-title__text {
			display: inline;
			font-size: 34rpx;
			color: #303133;
			line-height: 60rpx;
		}
		:deep(.question-title__fill-blank) {
			display: inline-block;
			padding-right: 50rpx;
			border-bottom: 1px solid black;
			position: relative;
			vertical-align: bottom;

			.uni-input-input {
				text-align: center;
			}
			// #ifdef MP-WEIXIN
			input {
				text-align: center;
			}
			// #endif
			.question-title__score {
				position: absolute;
				//left: 50%;
				//dtransform: translateX(-50%);
				right: -10rpx;
				bottom: -5rpx;
				font-size: 24rpx;
				color: #0d9df6;
			}
			.question-title__score--succ {
				color: #18bc38;
			}
			.question-title__score--err {
				color: #e43d33;
			}
		}
	}
	.question__img-group {
		display: flex;

		.question__img-inner {
			display: flex;
			flex-direction: column;
			align-items: center;
			.question__img {
				display: inline-block;
				height: 140rpx;
				width: 140rpx;
				background-color: #fff;
				border: 1px solid #dcdfe6;
				border-radius: 6px;
				margin-left: 10rpx;
			}
		}
	}
	.question__video {
		width: 100%;
		padding-top: 75%;
	}
	:deep(.question-user-answer) {
		background-color: #f6f7fc;
		width: 100%;
		padding: 20rpx;
		min-height: 600rpx;
		line-height: 50rpx;
		.uni-textarea-wrap {
			min-height: 600rpx;
		}
	}
	.question-score {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-top: 20rpx;
		padding: 0rpx 20rpx;
		height: 96rpx;
		background: #f6f7fc;
		border-radius: 10rpx 10rpx 10rpx 10rpx;
		.question-score__label {
			margin-left: 5rpx;
			font-weight: bold;
			font-size: 34rpx;
			color: #303133;
		}
		.question-score__user {
			font-weight: bold;
			font-size: 34rpx;
			color: #0d9df6;
		}
		.question-score__user--succ {
			color: #18bc38;
		}
		.question-score__user--err {
			color: #e43d33;
		}
		.question-score__standard {
			font-weight: bold;
			font-size: 34rpx;
			color: #999999;
		}
	}
	.question-select-answer {
		display: flex;
		justify-content: center;
		align-items: center;
		margin-top: 20rpx;
		padding: 20rpx 0rpx 10rpx 0rpx;
		background: #f6f7fc;
		border-radius: 10rpx;
		.question-select-answer__diff {
			flex: 1;
			display: flex;
			flex-direction: column;
			align-items: center;
			.question-select-answer__label {
				font-weight: bold;
				font-size: 30rpx;
				color: #999999;
			}
			.question-select-answer__value {
				margin-top: 10rpx;
				font-weight: bold;
				font-size: 30rpx;
				color: #0d9df6;
			}
			.question-select-answer__value--succ {
				color: #18bc38;
			}
			.question-select-answer__value--err {
				color: #e43d33;
			}
		}
	}
	.question-fill-blank-answer {
		margin-top: 20rpx;
		padding: 20rpx;
		background: #f6f7fc;
		border-radius: 10rpx;
		.question-fill-blank-answer__label {
			display: inline-block;
			margin-bottom: 20rpx;
			font-weight: bold;
			font-size: 30rpx;
			color: #999999;
		}
	}
	.question-qa-answer {
		margin-top: 20rpx;
		padding: 20rpx;
		background: #f6f7fc;
		border-radius: 10rpx;
		.question-qa-answer__label {
			margin-top: 20rpx;
			font-weight: bold;
			font-size: 30rpx;
			color: #999999;
		}
		.question-qa-answer__content {
			display: inline-block;
			margin-top: 20rpx;
			line-height: 60rpx;
		}
	}
	.question-analysis {
		margin-top: 20rpx;
		.question-analysis__title-wrap {
			position: relative;
			.question-analysis__title-icon {
				position: absolute;
				left: 0rpx;
				top: 10rpx;
				width: 20rpx;
				height: 20rpx;
				border-radius: 50%;
				background: #2979ff;
			}
			.question-analysis__title-icon1 {
				position: absolute;
				left: 12rpx;
				top: 10rpx;
				width: 20rpx;
				height: 20rpx;
				border-radius: 50%;
				background: #2979ff;
				opacity: 0.6;
			}
			.question-analysis__title {
				margin-left: 50rpx;
				font-weight: bold;
				font-size: 36rpx;
				color: #303133;
			}
		}

		.question-analysis__content {
			display: inline-block;
			margin-top: 20rpx;
			line-height: 60rpx;
			font-size: 34rpx;
			color: #303133;
		}
	}
	.question-option__radio-wrap,
	.question-option__checkbox-wrap {
		/* #ifndef APP-NVUE */
		display: flex;
		/* #endif */
		flex-direction: column;
		position: relative;
		margin: 10rpx 0rpx 20rpx 0rpx;
		.question-option {
			/* #ifndef APP-NVUE */
			display: flex;
			/* #endif */
			flex-direction: row;
			align-items: center;

			position: relative;
			margin: 0rpx 0rpx 25rpx 0rpx;
			padding: 20rpx 0rpx 20rpx 20rpx;
			.question-option__radio-hover {
				// 隐藏默认单选按钮
				position: absolute;
				opacity: 0;
			}
			.question-option__checkbox-hover {
				position: absolute;
				opacity: 0;
			}
			.question-option__radio {
				// 重新实现单选按钮
				/* #ifndef APP-NVUE */
				display: flex;
				flex-shrink: 0;
				box-sizing: border-box;
				/* #endif */
				justify-content: center;
				align-items: center;
				position: relative;
				width: 40rpx;
				height: 40rpx;
				border: 2rpx solid #0292f8;
				border-radius: 30rpx;
				background-color: #fff;
				z-index: 1;
				.question-option__radio-inner {
					width: 20rpx;
					height: 20rpx;
					border-radius: 30rpx;
					opacity: 0;
				}
			}
			.question-option__content {
				font-size: 34rpx;
				color: #303133;
				margin: 0rpx 20rpx;
				line-height: 46rpx;
			}
			.question-option__checkbox {
				/* #ifndef APP-NVUE */
				flex-shrink: 0;
				box-sizing: border-box;
				/* #endif */
				position: relative;
				width: 38rpx;
				height: 38rpx;
				border: 2rpx solid $uni-border-3;
				border-radius: 8rpx;
				background-color: #fff;
				z-index: 1;
				.question-option__checkbox-inner {
					position: absolute;
					/* #ifndef APP-NVUE */
					top: 6rpx;
					/* #endif */
					left: 12rpx;
					height: 20rpx;
					width: 14rpx;
					border-right-width: 4rpx;
					border-right-color: #fff;
					border-right-style: solid;
					border-bottom-width: 4rpx;
					border-bottom-color: #fff;
					border-bottom-style: solid;
					opacity: 0;
					transform-origin: center;
					transform: rotate(40deg);
				}
			}
			&.is-checked {
				background-color: #e0f9ff;
				// 选中样式
				.question-option__radio {
					.question-option__radio-inner {
						opacity: 1;
						background: linear-gradient(to right, #04b7f2 0%, #007dfc 100%);
					}
				}
				.question-option__checkbox {
					border-width: 0;
					background: linear-gradient(to right, #04b7f2 0%, #007dfc 100%);

					.question-option__checkbox-inner {
						opacity: 1;
						transform: rotate(45deg);
					}
				}
				.question-option__content {
					font-size: 34rpx;
					color: #303133;
				}
			}
			&.is-succ {
				background-color: #d1f2d7;
				.question-option__radio {
					.question-option__radio-inner {
						opacity: 1;
					}
				}
				.question-option__checkbox {
					.question-option__checkbox-inner {
						opacity: 1;
						transform: rotate(45deg);
					}
				}
				.question-option__content {
				}
			}
			&.is-err {
				background-color: #fad8d6;
				.question-option__radio {
					.question-option__radio-inner {
						opacity: 1;
					}
				}
				.question-option__checkbox {
					.question-option__checkbox-inner {
						opacity: 1;
						transform: rotate(45deg);
					}
				}
				.question-option__content {
				}
			}
		}
	}
}
</style>
