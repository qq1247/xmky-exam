package com.wcpdoc.exam.web.conf;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.wcpdoc.exam.core.util.Param;

@Component
public class Params implements HandlerMethodArgumentResolver {
	// 判断是否是包含注解，启动解析
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(Param.class);
	}
	
	@Override
	public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {		
		//获取参数上的注解
		Param annotation = methodParameter.getParameterAnnotation(Param.class);
		System.err.println(annotation.toString());
		//String name = annotation.name();
		//从Request中获取参数值
		String aa = nativeWebRequest.getParameter("aa");
		System.err.println(aa);
		String bb = nativeWebRequest.getParameter("bb");
		System.err.println(bb);
		String cc = nativeWebRequest.getParameter("cc");
		System.err.println(cc);
		
		/*T t = new T();
		BeanWrapper wrapper = new BeanWrapperImpl(t.getClass());
		wrapper.setPropertyValue("id","20001");
		Object instance = wrapper.getWrappedInstance();
		System.out.println(instance);*/
		
		return aa;
	}
	
}