# kevy-result-page

#### 介绍
这是一个**全端通用的结果页组件**，简单的设置完成一个反馈页面展示。
祝您使用愉快，本插件会长期维护更新，开源不易，如果本插件对您有帮助的话请及时点个好评吧或者赞赏一下，总之谢谢您的鼓励啦。


#### 方法和属性

|   名称     |    类型 |     默认值    |    字段说明    |
| -------  | -------    |------    |------
|  type    |      String   |     'success'   |  类型：可选参数为success、info、error、waiting、warning（用于改变顶部图标） |
|  title    |      String   |     ''   |  标题 |
|  description|      String   |     ''   |  描述  |
|  details  |      Array  |     []   |  详细数量列表（可选），内部数据为Object类型，超过三条自动折叠，通过收缩按钮点击查看，**数据格式见下方details说明** |
|  primarColor    |      String   |     #4fc08d   |  主题色（背景） |
|  primaryBtnText    |      String   |     ''   |  底部主要按钮文字，不填不显示主要按钮 |
|  secondaryBtnText    |      String   |     ''   |  底部次要按钮文字，不填不显示次要按钮 |
|  primaryBtnClick    |      Event   |        |  主要按钮点击事件 |
|  secondaryBtnClick    |      Event   |        |  次要按钮点击事件 |

#### details字段说明（Object对象属性）
| 名称  | 类型  | 说明  |
| :------------ | :------------ | :------------ |
| label  | String  | 字段名  |
| value | String  | 字段值  |
| bold | Boolean  |  是否加粗，默认false |

#### 使用方式
插件详情页点击导入hbuilder即可。插件符合uni_modules和easycom规范，导入后可直接在页面通过标签引用。

#### 代码使用示例
```
<template>
	<view class="content">
		<kevy-result-page v-if="type" :type="type" :title="title" :description="description" :details="details" primaryBtnText="主要操作" secondaryBtnText="次要操作" @primaryBtnClick="primaryBtnClick" @secondaryBtnClick="secondaryBtnClick"></kevy-result-page>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				//类型：可选参数为success、info、error、waiting、warning
				type:'',
				//标题
				title:'',
				//描述
				description:'',
				//详情数据列表
				details:[],
			}
		},
		onLoad() {
			//这里模拟请求到了数据后设置组件
			this.type = 'success';
			this.title = '预约成功';
			this.description = '这里是操作描述，内容会自动换行，建议最多设置两行更佳好';
			this.details = [{
						label: '预约人',
						value: '张某某',
						bold: true,
					},
					{
						label: '预约地点',
						value: '秦岭野生动物园',
					},
					{
						label: '预约时间',
						value: '2023/4/8 13:23',
					},
					{
						label: '流水号',
						value: '1000802909309389',
					},
					{
						label: '备注',
						value: '携带相关证件享受优惠',
					}
				];
			
		},
		methods: {
			primaryBtnClick(){
				console.log("主要按钮被点击了");
			},
			secondaryBtnClick(){
				console.log("次要按钮被点击了");
			}
		}
	}
</script>

<style scoped>
	.content {
		box-sizing: border-box;
	}
</style>
```