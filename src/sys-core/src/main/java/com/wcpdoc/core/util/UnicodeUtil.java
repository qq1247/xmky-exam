package com.wcpdoc.core.util;


import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * unicode 转码
 * 
 * v1.0 chenyun 2021年12月20日上午10:30:38
 */
public class UnicodeUtil {

	public static String getUnicode(String s){
		if (s == null || s.equals("")) {
			return "";
		}
		
        try {
            StringBuffer out = new StringBuffer("");
            byte[] bytes = s.getBytes("unicode");
            for (int i = 0; i < bytes.length - 1; i += 2) {
                out.append("\\u");
                String str = Integer.toHexString(bytes[i + 1] & 0xff);
                for (int j = str.length(); j < 2; j++) {
                    out.append("0");
                }
                String str1 = Integer.toHexString(bytes[i] & 0xff);
                out.append(str1);
                out.append(str);
                 
            }
            return out.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
	}
	
	public static String[] getUnicode(String[] str){
		String[] result;
		if (str == null || str.length <= 0) {
			result = new String[0];
			return result;
		}
		
		result = new String[str.length];
		for(int i = 0 ; i < str.length ; i++ ){
			result[i] = getUnicode(str[i]);
		}
		
		return result;
	}

	public static String getString(String str){
	    Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");    
	    Matcher matcher = pattern.matcher(str);
	    char ch;
	    while (matcher.find()) {
	        ch = (char) Integer.parseInt(matcher.group(2), 16);
	        str = str.replace(matcher.group(1), ch + "");    
	    }
	    return str;
	}
	
}
