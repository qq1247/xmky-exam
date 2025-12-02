package com.wcpdoc.base.util;

import com.wcpdoc.core.exception.MyException;

/**
 * 邀请码
 * 
 * v1.0 zhanghc 2025年12月1日上午10:13:56
 */
public class InviteCodeUtil {

	// 自定义安全字符集（32个字符，排除 0/O/1/I/l 等易混淆字符）
	private static final String CHARSET = "ABCDEFGHJKMNPQRSTUVWXYZ23456789";
	private static final int BASE = CHARSET.length(); // 32
	private static final int CODE_LENGTH = 4; // 后缀长度
	private static final String PREFIX = "XMKY";

	/**
	 * 将机构ID编码为4位字符串（左补零）
	 */
	public static String encode(int id) {
		if (id <= 0 || id >= Math.pow(BASE, CODE_LENGTH)) {
			throw new MyException("ID必须在 1 ~ " + ((int) Math.pow(BASE, CODE_LENGTH) - 1) + " 之间");
		}

		StringBuilder encoded = new StringBuilder();
		int num = id;
		while (num > 0) {
			encoded.insert(0, CHARSET.charAt(num % BASE));
			num /= BASE;
		}

		// 左侧补零至4位
		while (encoded.length() < CODE_LENGTH) {
			encoded.insert(0, CHARSET.charAt(0)); // 'A' 代表0
		}

		return encoded.toString();
	}

	/**
	 * 将4位后缀解码为机构ID
	 */
	public static int decodeSuffix(String suffix) {
		if (suffix == null || suffix.length() != CODE_LENGTH) {
			throw new IllegalArgumentException("后缀必须为4位字符");
		}

		int id = 0;
		for (int i = 0; i < CODE_LENGTH; i++) {
			char c = suffix.charAt(i);
			int index = CHARSET.indexOf(c);
			if (index == -1) {
				throw new MyException("非法字符: " + c);
			}
			id = id * BASE + index;
		}
		return id;
	}

	/**
	 * 生成完整邀请码：xmky + 编码后缀
	 */
	public static String generateInviteCode(int id) {
		String suffix = encode(id);
		return PREFIX + suffix;
	}

	/**
	 * 从完整邀请码中提取机构ID
	 */
	public static int extractIdFromCode(String inviteCode) {
		if (inviteCode == null || !inviteCode.startsWith(PREFIX)
				|| inviteCode.length() != PREFIX.length() + CODE_LENGTH) {
			throw new MyException("无效的邀请码格式，应为 XMKY + 4位字符");
		}
		String suffix = inviteCode.substring(PREFIX.length());
		return decodeSuffix(suffix);
	}

	public static void main(String[] args) {
		int[] testIds = { 1, 101, 205, 999999 };

		for (int id : testIds) {
			String code = generateInviteCode(id);
			int decodedId = extractIdFromCode(code);
			System.out.println("机构ID: " + id + " → 邀请码: " + code + " → 解码: " + decodedId);
		}

		try {
			extractIdFromCode("XMKYABCD1"); // 长度错误
		} catch (Exception e) {
			System.out.println("捕获异常: " + e.getMessage());
		}

		try {
			extractIdFromCode("XMKYAB0D"); // 包含非法字符 '0'
		} catch (Exception e) {
			System.out.println("捕获异常: " + e.getMessage());
		}
	}
}
