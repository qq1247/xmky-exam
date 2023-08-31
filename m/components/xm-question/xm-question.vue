<template>
    <view class="question">
		<!-- 题干（如果是填空题，直接在题干答题） -->
		<view class="question-title">
			<text>{{no}}、</text>
			<template v-for="(title, index) in titles as Title[]" :key="index">
				<text v-if="title.type === 'txt'" :decode="true">{{ title.value.replaceAll('<br/>', '\n') }}</text>
				<uni-easyinput 
					v-else
					:modelValue="!answersShow ? userAnswers[title.index] : answers[title.index]"
					@update:modelValue="(value: string) => userAnswers[title.index] = value"
					@input="() => { $emit('update:modelValue', userAnswers); $emit('change', userAnswers) }" 
					:clearable="false"
					:disabled="!editable"
					:style="{ width: title.value.length * 36 + 'rpx' }"
					></uni-easyinput>
			</template>
			<text>（{{ score }}分）</text>
			<uni-icons 
				v-if="errMark" 
				custom-prefix="iconfont" :type="score === userScore ? 'icon-duigoux' : userScore === 0 ? 'icon-cuo' : 'icon-bandui'"
				:style="`color: ${score === userScore ? '#18bc37' : '#e43d33'}`"
				></uni-icons>
			<text v-if="errMark" :style="`color: ${score === userScore ? '#18bc37' : '#e43d33'}`">{{ userScore }}分</text>
		</view>
		<!-- 单选题选项 -->
		<radio-group v-if="type === 1" @change="(e: any) => { userAnswers[0] = e.detail.value; $emit('update:modelValue', userAnswers); $emit('change', userAnswers) }">
			<label v-for="(option, index) in options" :key="index" :class="['question-option', { 'is-checked': isChecked(optionLabs(index)) }, { 'is-succ': isSucc(optionLabs(index)) }, { 'is-err': isErr(optionLabs(index)) }]">
				<radio :value="optionLabs(index)" :checked="isChecked(optionLabs(index))" :disabled="!editable"/>
				<view class="question-option-radio">
					<view class="question-option-radio-inner"></view>
				</view>
				<text class="question-option-content">{{ optionLabs(index) }}、{{ option }}</text>
			</label>
		</radio-group>
		<!-- 多选题选项 -->
		<checkbox-group v-if="type === 2" @change="(e: any) => { userAnswers = e.detail.value; $emit('update:modelValue', userAnswers); $emit('change', userAnswers) }">
			<label v-for="(option, index) in options" :key="index" :class="['question-option', { 'is-checked': isChecked(optionLabs(index)) }, { 'is-succ': isSucc(optionLabs(index)) }, { 'is-err': isErr(optionLabs(index)) }]">
				<checkbox :value="optionLabs(index)" :checked="isChecked(optionLabs(index))" :disabled="!editable"/>
				<view class="question-option-checkbox">
					<view class="question-option-checkbox-inner"></view>
				</view>
				<text class="question-option-content">{{ optionLabs(index) }}、{{ option }}</text>
			</label>
		</checkbox-group>
		<!-- 填空题（题干处填空） -->
		<!-- 判断题选项 -->
		<radio-group v-if="type === 4" @change="(e: any) => { userAnswers[0] = e.detail.value; $emit('update:modelValue', userAnswers); $emit('change', userAnswers) }">
			<label v-for="(option, index) in judges" :key="index" :class="['question-option', { 'is-checked': isChecked(optionLabs(index)) }, { 'is-succ': isSucc(optionLabs(index)) }, { 'is-err': isErr(optionLabs(index)) }]">
				<radio :value="option" :checked="isChecked(optionLabs(index))" :disabled="!editable"/>
				<view class="question-option-radio">
					<view class="question-option-radio-inner"></view>
				</view>
				<text class="question-option-content">{{ option }}</text>
			</label>
		</radio-group>
		<!-- 问答题答案 -->
		<uni-easyinput
			v-if="type === 5"
			:modelValue="!answersShow ? userAnswers[0] : answers[0]"
			@update:modelValue="(value: string) => {userAnswers[0] = value; $emit('update:modelValue', userAnswers); $emit('change', userAnswers) }">
			@input="() => $emit('update:modelValue', userAnswers)"
			type="textarea" 
			autoHeight
			placeholder="请输入答案"
			:disabled="!editable"
			></uni-easyinput>
    </view>
</template>
  
<script lang="ts" setup>
import { computed, ref, watch } from "vue";
import { onLoad } from "@dcloudio/uni-app"
import { Title } from "@/ts";
	// 定义变量
	const emit = defineEmits<{
		'update:modelValue': string[]
		'change': string[]
	}>()
	
	const props = withDefaults(defineProps<{
	    modelValue?: string[] //用户答案
	    no?: string | number // 题号
	    editable?: boolean // 可编辑（true：是；false：否）
	    type: number // 试题类型（1：单选；2：多选；3：填空；4：判断；5：问答）
	    markType: number // 阅卷类型
	    title: string // 题干
	    score: number // 分数
	    answers?: string[] // 标准答案
	    userScore?: number //用户分数
	    options?: string[] // 试题选项
	    answersShow?: boolean // 标准答案显示（true：用户答案显示；false：标准答案显示）
	    errMark?: boolean // 错题标记（true：标记；false：不标记）
	}>(), {
	    editable: false,
		modelValue: () => [],
	    answers: () => [],
	    options: () => [],
	    answersShow: false,
		errMark: false,
	})
	
	const judges = ['对', '错']// 判断题使用
	const userAnswers = ref(props.modelValue) // 用户答案
	
	// 页面加载完毕，执行如下方法
	onLoad(async() => {
		if (props.type === 3 && !userAnswers.value.length) {// 初始化填空题答案
			userAnswers.value = props.title.match(/[_]{5,}/g).map(() => '')
		}
		if (props.type === 5 && !userAnswers.value.length) {// 初始化问答题答案
			userAnswers.value = ['']
		}
	})
	
	// 计算属性
	const titles = computed(() => {
	    // 如果不是填空题，正常返回
	    if (props.type !== 3) {
	        return [{
	            type: 'txt',
	            value: props.title,
	            index: -1,
	        },]
	    }
	
	    // 如果是填空题，下划线转输入框，用于在题干填空
	    let title = props.title// '______AAAAA_____BBBBB____________CCCCC__DDDDD______'
	    let titles = [] as Title[]
	    let pos = 0
	    title.match(/[_]{5,}/g)?.forEach((value) => {// 大于连续的5个下划线算一个填空
	        let index = title.indexOf(value)
	        if (index > 0) {
	            titles.push({// 添加文本
	                type: 'txt',
	                value: title.substring(0, index),
	                index: -1,
	            })
	        }
	        titles.push({// 添加输入框
	            type: 'input',
	            value,
	            index: pos++, // 用于找对应答案的位置
	        })
	        title = title.substring(index + value.length) // 解析过的部分题干删除,下次循环从剩余题干中找
	    })
	    if (title) {// 最后一节拼接
	        titles.push({
	            type: 'txt',
	            value: title,
	            index: -1,
	        })
	    }
	    return titles
	})
	
	// 监听属性
	watch(() => props.modelValue, () => {
		userAnswers.value = props.modelValue
	})
	
	// 是否选中
	function isChecked(curAnswer: string) {
		if (!(props.type === 1 || props.type === 2 || props.type === 4)) {
			return false
		}
		return !props.answersShow ? userAnswers.value.includes(curAnswer) : props.answers.includes(curAnswer)
	}
	
	// 是否答对
	function isSucc(curAnswer: string) {
		if (!props.errMark) {
		        return false
		    }
		
		if (!(props.type === 1 || props.type === 2 || props.type === 4)) {// 不是单多选判断
			return false
		}
		
		if (!props.answers.includes(curAnswer) ) {
			return false
		}
		
		return true
	}
	
	// 是否答错
	function isErr(curAnswer: string) {
		if (!props.errMark) {
		        return false
		    }
		
		if (!(props.type === 1 || props.type === 2 || props.type === 4)) {// 不是单多选判断
			return false
		}
		if (userAnswers.value.includes(curAnswer) && !props.answers.includes(curAnswer)) {
			return true
		}
		return false
	}
	
	// 获取选项标签
	function optionLabs(index: number) {
		if (props.type === 1 || props.type === 2) {
			return String.fromCharCode(65 + index)
		}
		if (props.type === 4) {
			return judges[index]
		}
	}
</script>

<style lang="scss" scoped>
.question {
	.uni-easyinput {
		display: inline-block;
		border-radius: 0rpx;
		
		:deep(.uni-easyinput__content) {
			border-color: $uni-primary !important;
			border-top: 0rpx;
			border-right: 0rpx;
			border-left: 0rpx;
			border-radius: 0px;
			.uni-easyinput__content-input {
				
			}
		}
	}
	.question-title {
		font-size: 30rpx;
		font-weight: bold;
		margin-bottom: 20rpx;
	}
	uni-radio-group, uni-checkbox-group {
		/* #ifndef APP-NVUE */
		display: flex;
		/* #endif */
		flex-direction: column;
		position: relative;
		margin: 10rpx 50rpx 20rpx 0rpx;
		.question-option {
			/* #ifndef APP-NVUE */
			display: flex;
			/* #endif */
			flex-direction: row;
			align-items: flex-start;
			position: relative;
			margin: 5px 0;
			margin-right: 25px;
			uni-radio {// 隐藏默认单选按钮
				position: absolute;
				opacity: 0;
			}
			uni-checkbox {
				position: absolute;
				opacity: 0;
			}
			.question-option-radio {// 重新实现单选按钮
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
				border: 2rpx solid $uni-border-3;
				border-radius: 30rpx;
				background-color: #fff;
				z-index: 1;
				.question-option-radio-inner {
					width: 20rpx;
					height: 20rpx;
					border-radius: 30rpx;
					opacity: 0;
				}
			}
			.question-option-content {
				font-size: 34rpx;
				color: $uni-base-color;
				margin-left: 20rpx;
				line-height: 46rpx;
			}
			.question-option-checkbox {
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
				.question-option-checkbox-inner {
					position: absolute;
					/* #ifndef APP-NVUE */
					top: 2rpx;
					/* #endif */
					left: 12rpx;
					height: 20rpx;
					width: 10rpx;
					border-right-width: 4rpx;
					border-right-color: #fff;
					border-right-style: solid;
					border-bottom-width: 4rpx ;
					border-bottom-color: #fff;
					border-bottom-style: solid;
					opacity: 0;
					transform-origin: center;
					transform: rotate(40deg);
				}
			}
			&.is-checked {// 选中样式
				.question-option-radio {
					border-color: $uni-primary;
					.question-option-radio-inner {
						opacity: 1;
						background-color: $uni-primary;
					}
				}
				.question-option-checkbox {
					border-color: $uni-primary;
					background-color: $uni-primary;
					
					.question-option-checkbox-inner {
						opacity: 1;
						transform: rotate(45deg);
					}
				}
				.question-option-content {
					color: $uni-primary;
				}
			}
			&.is-succ {
				.question-option-radio {
					border-color: $uni-success;
					.question-option-radio-inner {
						opacity: 1;
						background-color: $uni-success;
					}
				}
				.question-option-checkbox {
					border-color: $uni-success;
					background-color: $uni-success;
					
					.question-option-checkbox-inner {
						opacity: 1;
						transform: rotate(45deg);
					}
				}
				.question-option-content {
					color: $uni-success;
				}
			}
			&.is-err {
				.question-option-radio {
					border-color: $uni-error;
					.question-option-radio-inner {
						opacity: 1;
						background-color: $uni-error;
					}
				}
				.question-option-checkbox {
					border-color: $uni-error;
					background-color: $uni-error;
					
					.question-option-checkbox-inner {
						opacity: 1;
						transform: rotate(45deg);
					}
				}
				.question-option-content {
					color: $uni-error;
				}
			}
		}
	}
}
</style>
