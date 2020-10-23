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
	function rdoCbxSubmit(curObj, myExamDetailId) {
		var li = $(curObj);
		var rdoCbxBtn = li.find("input");
		var elmName = event.target.tagName;//当前点击的元素名称
		if (elmName == "LI") {// 如果点击的是li元素，直接反选；// 如果点击的是li里面美化后的选择框，它的事件已经选择，冒泡到LI元素时，忽略就可以
			if (rdoCbxBtn.prop("checked")) {
				rdoCbxBtn.prop("checked", false);
			} else {
				rdoCbxBtn.prop("checked", true);
			}
		}
		
		if (rdoCbxBtn.attr("type") == "radio") {
			layui.form.render("radio", "paperCfgFrom");
		} else {
			layui.form.render("checkbox", "paperCfgFrom");
		}
		
		var answers = [];
		li.parent().children().each(function() {
			var _li = $(this);
			var rdoCbxBtn = _li.find("input");
			if (rdoCbxBtn.prop("checked")) {
				answers.push(rdoCbxBtn.val());
				_li.addClass("exam-qst-op-select");
			} else {
				_li.removeClass("exam-qst-op-select");
			}
		});
		
		//答题卡标记为已做
		var anCardOption = $("#examCard_" + myExamDetailId);
		if (answers.length == 0) {
			anCardOption.removeClass("select");
		} else {
			anCardOption.addClass("select");
		}
		 
		//异步提交答案
		$.ajax({
			data : {
				myExamDetailId : myExamDetailId, 
				answer : answers 
			},
			url : "myExam/updateAnswer",
			async : true,
			success : function(obj) {
				if (!obj.succ) {
					layer.alert(obj.msg, {"title" : "提示消息"});
					
				}
			}
		});
	}
	
	//文本提交
	function txtSubmit(curObj, myExamDetailId) {
		//答题卡标记为已做
		var input = $(curObj);
		var nameAttrVal = input.attr("name");
		var row = input.parent();
		var inputs = row.find("[name='"+nameAttrVal+"']");
		var answer = "";
		inputs.each(function (index, domEle) {
			if (index > 0) {
				answer += "\n";
			}
			answer += $(domEle).val();
		});
		
		var anCardOption = $("#examCard_" + myExamDetailId);
		if (answer.replace(/\n/g, "")) {
			anCardOption.addClass("select");
		} else {
			anCardOption.removeClass("select");
		}
		
		//异步提交答案
		$.ajax({
			data : {
				myExamDetailId : myExamDetailId, 
				answer : answer 
			},
			url : "myExam/updateAnswer",
			async : true, //异步提交
			success : function(obj) {
				if (!obj.succ) {
					layer.alert(obj.msg, {"title" : "提示消息"});
				}
			}
		});
	}
	
	// 完成考试
	function doPaper(auto) {
		$.ajax({
			url : "myExam/doExam",
			data : {myExamId : "${myExam.id }"},
			async : true, 
			success : function(obj) {
				if (!obj.succ) {
					layer.alert(obj.msg, {"title" : "提示消息"});
					return;
				}
				
				var message = auto ? "考试时间到！" : obj.msg;
				layer.alert(message, {"title" : "提示消息"});
				setTimeout("window.close();", 2000)
			}
		});
	}
</script>