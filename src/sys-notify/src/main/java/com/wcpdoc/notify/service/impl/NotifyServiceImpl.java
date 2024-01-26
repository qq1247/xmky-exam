package com.wcpdoc.notify.service.impl;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.notify.exception.NotifyException;
import com.wcpdoc.notify.service.EmailService;
import com.wcpdoc.notify.service.NotifyService;

import lombok.extern.slf4j.Slf4j;

/**
 * 通知服务实现
 * 
 * v1.0 zhanghc 2019年10月15日下午15:51:27
 */
@Component
@Slf4j
public class NotifyServiceImpl implements NotifyService {
	@Resource
	private EmailService emailService;

	@Override
	public void emailPush(String from, String to, String title, String content) throws NotifyException {
		try {
			log.debug("推送邮件：{}向{}发送{}", from, to, title);
			MimeMessage mailMessage = emailService.getJavaMailSender().createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true);
			messageHelper.setFrom(from);
			messageHelper.setTo(to.split(","));
			messageHelper.setSubject(title);
			messageHelper.setText(content, true);
			emailService.getJavaMailSender().send(mailMessage);
		} catch (Exception e) {
			if (e.getMessage().contains("550 User has no permission")) {
				log.error("推送邮件错误：需要开启POP3/SMTP服务");
				throw new MyException("需要开启POP3/SMTP服务");
			}
			if (e.getMessage().contains("535 ")) {
				log.error("推送邮件错误：需要使用授权码替代密码");
				throw new MyException("需要使用授权码替代密码");
			}
			log.error("推送邮件错误：", e);
			throw new NotifyException(e.getMessage());
		}
	}

	@Override
	public void SMSPush(String phone, String code) throws NotifyException {
		try {
			// 极光短信
			// String appkey = "";
			// String masterSecret = "";
			// SMSClient client = new SMSClient(masterSecret, appkey);
			// SMSPayload payload = SMSPayload.newBuilder()
			// .setMobileNumber(phone)
			// .setSignId(10756) // 签名
			// .setTempId(171442) // 短信模板
			// .addTempPara("code", code) // 验证码
			// .addTempPara("time", "3") // 时间
			// .build();
			// @SuppressWarnings("unused")
			// SendSMSResult res = client.sendTemplateSMS(payload);

			// 阿里云短信
//	        String appkey = "";
//		    String accessKeySecret = "";
//	        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", appkey, accessKeySecret);
//	        IAcsClient client = new DefaultAcsClient(profile);
//	        CommonRequest request = new CommonRequest();
//	        request.setMethod(MethodType.POST);
//	        request.setDomain("dysmsapi.aliyuncs.com");
//	        request.setVersion("2017-05-25");
//	        request.setAction("SendSms");
//	        request.putQueryParameter("RegionId", "cn-hangzhou");
//	        request.putQueryParameter("PhoneNumbers", phone);
//	        request.putQueryParameter("SignName", "Hi您好");
//	        request.putQueryParameter("TemplateCode", "SMS_177160250");
//	        request.putQueryParameter("TemplateParam", "{\"code\":\""+code+"\"}");
//	        @SuppressWarnings("unused")
//			CommonResponse response = client.getCommonResponse(request);
		} catch (Exception e) {
			log.error("阿里云短信：【{}】推送【{}】时，{}", phone, code, e.getMessage());
			throw new NotifyException(String.format("阿里云短信：【{}】推送【{}】时，{}", phone, code, "未知异常"));
		}
	}

}
