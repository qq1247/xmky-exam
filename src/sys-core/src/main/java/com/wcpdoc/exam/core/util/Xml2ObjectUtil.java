package com.wcpdoc.exam.core.util;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * xml转对象工具类
 * 
 * v1.0 zhanghc 2019年11月5日下午3:04:44
 */
public class Xml2ObjectUtil {
	@SuppressWarnings("unchecked")
	public static <T> T toObject(String xml, Class<T> t) {
		try {
			JAXBContext context = JAXBContext.newInstance(t);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			return (T) unmarshaller.unmarshal(new StringReader(xml));
		} catch (Exception e) {
			throw new RuntimeException("解析xml异常");
		}
	}

	public static String toXml(Object object) {
		try {
			StringWriter xml = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(object.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(object, xml);
			return xml.toString();
		} catch (Exception e) {
			throw new RuntimeException("生成xml异常");
		}
	}
}