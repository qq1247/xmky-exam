# kevy-empty

#### 介绍
这是一个**全端通用的空状态描述组件**，集成了25种常用场景，支持自定义图标及内容，快点下载试试吧。
- 插件含全部源码，可以给您无限实现可能，随心所欲自定义你的功能；
- 符合uni_modules和easycom规范，直接导入即可通过标签引入使用。

祝您使用愉快，本插件会长期维护更新，开源不易，如果本插件对您有帮助的话请及时点个好评吧或者赞赏一下，总之谢谢您的鼓励啦。


#### 方法和属性

|   名称     |    类型 |     默认值    |    字段说明    |
| -------  | -------    |------    |------
|  show    |      Boolean   |     false   |  是否显示组件 |
|  type    |      String   |     'data'   |  支持常用25种（address、car、comment、coupon、data、equipment、error、favor、goods、history、list、loading、maintain、message、money、network、news、notice、order、permission、points、search、task、404、500），**见下方type说明** |
|  image |      String   |    /    |  自定义图标链接，传此参数则优先级高于type显示图片，图标为正方形1:1大小  |
|  text |String   |    ''   |  描述文字，未填写则根据type自动生成  |
|  textColor | String   |     '#969799'   |  描述文字颜色  |
|  textSize | String、Number   |     28   |  描述文字大小，单位rpx  |
|  imageSize | String、Number   |     320   |  图标大小，单位rpx  |
|  fullScreen | Boolean   |     false   |  是否全屏展示，true时全屏展示  |

**type说明如下**

|   类型     |    描述文字  |
| -------  | -------   
|   address     |    无地址哦~  |
|   car     |    购物车空空如也~  |
|   comment     |    无评论哦~  |
|   coupon     |    无优惠券哦~  |
|   data     |    无数据哦~  |
|   equipment     |    无设备哦~  |
|   error     |    出错了~  |
|   goods     |    无商品哦~  |
|   history     |    无历史记录哦~  |
|   list     |    无列表哦~  |
|   loading     |    努力加载中...  |
|   maintain     |    正在维护中...  |
|   money     |    无余额哦~  |
|   network     |    无网络哦~  |
|   news     |    无新闻哦~  |
|   notice     |    无通知哦~  |
|   order     |    无订单哦~  |
|   permission     |    无权限哦~  |
|   points     |    无积分哦~  |
|   search     |    无搜索结果哦~  |
|   task     |    无任务哦~  |
|   404     |    页面走丢了~  |
|   500     |    服务器出错了~  |


#### 使用方式
插件详情页点击导入hbuilder即可。插件符合uni_modules和easycom规范，导入后可直接在页面通过标签引用。

#### 代码使用示例
```html
<template>
	<view class="content">
		<!-- 这里是直接定义type -->
		<kevy-empty :show="true" type="list"></kevy-empty>
		<!-- 这里是直接定义type和text -->
		<kevy-empty :show="true" type="favor" text="没有列表哦~"></kevy-empty>
		<!-- 这里是自定义图标示例,更多自定义参考api -->
		<kevy-empty :show="true" image="https://img01.yzcdn.cn/vant/empty-image-default.png" text="没有数据哦~"></kevy-empty>
		<!-- 这里是slot插入自定义内容到组件下方 -->
		<kevy-empty :show="true" type="money">
			<view class="self">返回首页</view>
		</kevy-empty>
		<!-- 全屏展示 设置下方full-screen为true即可 -->
		<kevy-empty :show="true" :full-screen="false" type="money">
			<view class="self">返回首页</view>
		</kevy-empty>
	</view>
</template>

<script>
	export default {
		data() {
			return {

			};
		}
	}
</script>

<style lang="scss">
	.content {
		min-height: 100vh;
		min-width: 100vw;
		box-sizing: border-box;
		background-color: #ffffff;
	}

	.self {
		text-align: center;
		margin: 60rpx auto 30rpx;
		font-size: 28rpx;
		color: #a7a7a7;
		background-color: #f7f7f7;
		border-radius: 30rpx;
		padding: 10rpx 20rpx;
		width: 300rpx;
	}
</style>
```