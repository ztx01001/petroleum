package com.university.springboot_petroleum.service.impl;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class SmsService {

	public boolean sendSms(String PhoneNumbers, String TemplateCode, Map<String,String> code) {
		
		DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI4G16F251gw8zRaePUPQo", "你自己的密码");
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");

        //自定义信息
        request.putQueryParameter("PhoneNumbers", PhoneNumbers); //发送至手机号
        request.putQueryParameter("SignName", "alarm");  //自己配置的短信签名
        request.putQueryParameter("TemplateCode", TemplateCode); //自己配置的模板 模版CODE

        
        try {
        	//构建一个短信验证码
            
            ObjectMapper mapper = new ObjectMapper(); //转换器 
    		String template = mapper.writeValueAsString(code);

            request.putQueryParameter("TemplateParam", template);   //转换成json字符串
            
            CommonResponse response = client.getCommonResponse(request); //发送至客户端
            System.out.println(response.getData());
            return response.getHttpResponse().isSuccess();//返回是否发送成功
        } catch (ClientException e) {
            log.error("SmsService ClientException="+e.getLocalizedMessage());
        }catch (Exception e) {
			log.error("SmsService exception="+e.getLocalizedMessage());
		}

        return false;

	}
}
