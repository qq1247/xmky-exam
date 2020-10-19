<%@ page language="java" pageEncoding="utf-8"%>
<script type="text/javascript">
	//定义变量
	var countdown = $("#countdown");//倒计时
	var curTimer;
	var endTimeStr ="${exam.endTime }";
	var endTime = new Date(
			endTimeStr.substring(0, 4),
			Number(endTimeStr.substring(5, 7) - 1),
			endTimeStr.substring(8, 10),
			endTimeStr.substring(11, 13),
			endTimeStr.substring(14, 16),
			endTimeStr.substring(17, 19)
			);
	
	//页面加载完毕，执行如下方法：
	$(function() {
		updateCountdown();
		setInterval(updateCountdown(), 5*60*1000);//5分钟和服务器同步一次，本地精度不够。
	});
	
	// 更新倒计时
	function updateCountdown() {
		$.ajax({
			url : "login/curTime",
			async : true,
			success : function(obj) {
				var serverTime = new Date(
					obj.data.substring(0, 4),
					Number(obj.data.substring(5, 7) - 1),
					obj.data.substring(8, 10),
					obj.data.substring(11, 13),
					obj.data.substring(14, 16),
					obj.data.substring(17, 19)
				);
				/* console.info(endTime.format("yyyy-MM-dd hh:mm:ss"));
				console.info(serverTime.format("yyyy-MM-dd hh:mm:ss")); */
				layui.util.countdown(endTime, serverTime, function(date, serverTime, timer) {
					if (curTimer) {
						clearTimeout(curTimer);
					}
					
					curTimer = timer;
					
					var timeLeft = "剩余时间：";
					if (date[0] > 0) {
						timeLeft += date[0] + "天";
					}
					timeLeft += date[1] + "时";
					timeLeft += date[2] + "分";
					timeLeft += date[3] + "秒";
					if (date[0] <= 0 && date[1] <= 0 && date[2] <= 4) {
						countdown.addClass("countdown-warn");
					} else {
						countdown.addClass("countdown-info");
					}
					countdown.html(timeLeft);
					
					if (date[0] <= 0 && date[1] <= 0 && date[2] <= 0 && date[3] <= 0) {
						doPaper(true);
					}
				});
			}
		});
	}


	// 单选多选提交
	function rdoCbxSubmit(curObj, examUserQuestionId) {
		// 选中当前行
		var li = $(curObj);
		li.children("div:first").click(function(e) {
			return false;
		}).trigger("click");
		
		var lis = li.parent().children();
		var vals = [];
		lis.each(function() {
			var _li = $(this);
			_li.removeClass("exam-qst-op-select");
			
			_li.find(":checked").each(function() {
		    	vals.push($(this).val());
		    	_li.addClass("exam-qst-op-select");
		    });
		});
		
		
		//答题卡标记为已做
		var anCardOption = $("#examCard_" + examUserQuestionId);
		if (vals.length == 0) {
			anCardOption.removeClass("select");
		} else {
			anCardOption.addClass("select");
		}
		
		//异步提交答案
		$.ajax({
			data : {
				examUserQuestionId : examUserQuestionId, 
				answer : vals 
			},
			url : "myExam/updateAnswer",
			async : true,
			success : function(obj) {
				if(!obj.succ){
					layer.alert(obj.msg, {"title" : "提示消息"});
				}
			}
		});
	}
	
	//文本提交
	function txtSubmit(curObj, examUserQuestionId){
		//答题卡标记为已做
		var input = $(curObj);
		var nameAttrVal = input.attr("name");
		var row = input.parent();
		var inputs = row.find("[name='"+nameAttrVal+"']");
		var answer = "";
		inputs.each(function (index, domEle) {
			if(index > 0){
				answer += "\n";
			}
			answer += $(domEle).val();
		});
		
		var anCardOption = $("#examCard_" + examUserQuestionId);
		if (answer.replace(/\n/g, "")) {
			anCardOption.addClass("select");
		} else {
			anCardOption.removeClass("select");
		}
		
		//异步提交答案
		$.ajax({
			data : {
				examUserQuestionId : examUserQuestionId, 
				answer : answer 
			},
			url : "myExam/updateAnswer",
			async : true, //异步提交
			success : function(obj) {
				if(!obj.succ){
					layer.alert(obj.msg, {"title" : "提示消息"});
				}
			}
		});
	}
	
	// 完成考试
	function doPaper(auto){
		$.ajax({
			url : "myExam/doExam",
			data : {examUserId : "${examUser.id }"},
			async : true, 
			success : function(obj) {
				if(!obj.succ) {
					layer.alert(obj.msg, {"title" : "提示消息"});
				}
				
				var message = auto ? "考试时间到！" : "交卷成功！";
				layer.alert(message, {"title" : "提示消息"});
				setTimeout("window.location.href = 'login/toHome'", 2000)
			}
		});
	}
</script>