package com.wcpdoc.notify.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.wcpdoc.notify.exception.NotifyException;
import com.wcpdoc.notify.service.EmailService;
import com.wcpdoc.notify.service.NotifyService;

/**
 * 通知服务实现
 * 
 * v1.0 zhanghc 2019年10月15日下午15:51:27
 */
@Component
public class NotifyServiceImpl implements NotifyService {
	private static final Logger log = LoggerFactory.getLogger(NotifyServiceImpl.class);
	@Resource
	private EmailService emailService;

	@Override
	public void pushEmail(String from, String to, String title, String content) throws NotifyException {
		try {
			log.debug("推送邮件：{}向{}发送{}", from, to, title);
			SimpleMailMessage mailMessage = new SimpleMailMessage();
			mailMessage.setFrom(from);
			mailMessage.setTo(to);
			mailMessage.setSubject(title);
			mailMessage.setText(content);
			emailService.getJavaMailSender().send(mailMessage);
		} catch (Exception e) {
			log.error("推送邮件：", e);
			throw new NotifyException("推送邮件异常");
		}
	}

	@Override
	public void pushSMS(String phone, String code) throws NotifyException {
		try {
			//极光短信
			//String appkey = "";
			//String masterSecret = "";
			//SMSClient client = new SMSClient(masterSecret, appkey);
	        //SMSPayload payload = SMSPayload.newBuilder()
	        //		.setMobileNumber(phone)
			//		.setSignId(10756)    // 签名
			//		.setTempId(171442)   // 短信模板
			//		.addTempPara("code", code)  // 验证码
			//		.addTempPara("time", "3")   // 时间
			//		.build();
	        //@SuppressWarnings("unused")
			//SendSMSResult  res = client.sendTemplateSMS(payload);
	        
	        //阿里云短信
	        String appkey = "";
		    String accessKeySecret = "";
	        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", appkey, accessKeySecret);
	        IAcsClient client = new DefaultAcsClient(profile);
	        CommonRequest request = new CommonRequest();
	        request.setMethod(MethodType.POST);
	        request.setDomain("dysmsapi.aliyuncs.com");
	        request.setVersion("2017-05-25");
	        request.setAction("SendSms");
	        request.putQueryParameter("RegionId", "cn-hangzhou");
	        request.putQueryParameter("PhoneNumbers", phone);
	        request.putQueryParameter("SignName", "Hi您好");
	        request.putQueryParameter("TemplateCode", "SMS_177160250");
	        request.putQueryParameter("TemplateParam", "{\"code\":\""+code+"\"}");
	        @SuppressWarnings("unused")
			CommonResponse response = client.getCommonResponse(request);
		} catch (Exception e) {
			log.error("阿里云短信：【{}】推送【{}】时，{}", phone, code, e.getMessage());
			throw new NotifyException(String.format("阿里云短信：【{}】推送【{}】时，{}", phone, code, "未知异常"));
		}
	}

}
