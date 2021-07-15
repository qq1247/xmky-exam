//闭包限定命名空间
(function($) {
	$.fn.extend({
		my : {
			/**
			 * 获取随机数
			 */
			randomNum : function(min, max) {
				var range = max - min;
				var rand = Math.random();
				return (min + Math.round(rand * range));
			},
			/**
			 * 获取序列化对象
			 */
			serializeObj : function(form) {
				var param = {};
				$.each(form.serializeArray(), function(index, domEle){
					param[domEle.name] = domEle.value;
				});
				return param;
			},
			/**
			 * 获取序列化属性
			 * serializeField([{ID:1,NAME:1},{ID:2,NAME:2},{ID:5,NAME:5}])
			 * ids=1&ids=2&ids=5
			 */
			serializeField : function(arr, options) {
				var defaluts = {attrName : "ids", fieldName : "ID"};
				var opts = $.extend({}, defaluts, options);
				
				var param = "";
				$.each(arr, function(index, domEle){
					if(index != 0){
						param += "&";
					}
					param += [opts.attrName] + "=" + domEle[opts.fieldName];
				});
				return param;
			}
		}
	});
})(window.jQuery);

/*
 * 例子
 * (function($) {
	$.fn.extend({
		"my" : function(options) {
			// 检测用户传进来的参数是否合法
			if (!isValid(options))
				return this;
			var opts = $.extend({}, defaluts, options); // 使用jQuery.extend
														// 覆盖插件默认参数
			return this.each(function() { // 这里的this 就是 jQuery对象。这里return
											// 为了支持链式调用
				// 遍历所有的要高亮的dom,当调用 highLight()插件的是一个集合的时候。
				var $this = $(this); // 获取当前dom 的 jQuery对象，这里的this是当前循环的dom
				// 根据参数来设置 dom的样式
				$this.css({
					backgroundColor : opts.background,
					color : opts.foreground
				});
				// 格式化高亮文本
				var markup = $this.html();
				markup = $.fn.highLight.format(markup);
				$this.html(markup);
			});
		}
	});
	// 默认参数
	var defaluts = {
		foreground : 'red',
		background : 'yellow'
	};
	// 公共的格式化 方法. 默认是加粗，用户可以通过覆盖该方法达到不同的格式化效果。
	$.fn.highLight.format = function(str) {
		return "<strong>" + str + "</strong>";
	}
	// 私有方法，检测参数是否合法
	function isValid(options) {
		return !options || (options && typeof options === "object") ? true
				: false;
	}
})(window.jQuery);*/

/**
 * 设置全局ajax选项 
 */
$.ajaxSetup({
	type : "post",
	async : false,
	cache : false,
	dataType : "json",
	traditional : true,
	error : function (XMLHttpRequest, textStatus, errorThrown) {
	    if(XMLHttpRequest.readyState != 4){
	    	alert("未收到服务器响应，请刷新页面后重试！");
	    	return;
	    }
	    //if(XMLHttpRequest.status == 404){
	    //	alert("当前请求在服务器不存在，请刷新页面后重试！");
	    //	return;
	    //}
	}
});

/**
 * 格式化日期
 * @param fmt 
 * 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
 * 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
 * 
 * 例子：
 * (new Date()).format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423
 * (new Date()).format("yyyy-M-d h:m:s.S") ==> 2006-7-2 8:9:4.18
 * 
 * @returns
 */
Date.prototype.format = function(fmt) {
	var o = {
		"M+" : this.getMonth() + 1, // 月份
		"d+" : this.getDate(), // 日
		"h+" : this.getHours(), // 小时
		"m+" : this.getMinutes(), // 分
		"s+" : this.getSeconds(), // 秒
		"q+" : Math.floor((this.getMonth() + 3) / 3), // 季度
		"S" : this.getMilliseconds()
	// 毫秒
	};
	if (/(y+)/.test(fmt))
		fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "")
				.substr(4 - RegExp.$1.length));
	for ( var k in o)
		if (new RegExp("(" + k + ")").test(fmt))
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k])
					: (("00" + o[k]).substr(("" + o[k]).length)));
	return fmt;
}
