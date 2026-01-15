package com.wcpdoc.search.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp4.MP4Parser;
import org.apache.tika.sax.BodyContentHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * 视频工具类
 * 
 * v1.0 zhanghc 2026-01-14 10:11:08
 */
@Slf4j
public class VideoUtil {
	/**
	 * 视频时长获取
	 * 
	 * v1.0 zhanghc 2026-01-14 10:14:08
	 * 
	 * @param videoFile
	 * @return double
	 */
	public static double getSecond(File videoFile) {
		if (!videoFile.exists() || !videoFile.canRead()) {
			return -1;
		}

		try (InputStream is = new FileInputStream(videoFile)) {
			MP4Parser parser = new MP4Parser();
			BodyContentHandler handler = new BodyContentHandler(0);
			Metadata metadata = new Metadata();
			ParseContext context = new ParseContext();
			parser.parse(is, handler, metadata, context);
			String durationStr = metadata.get("xmpDM:duration");
			if (durationStr != null) {
				return Double.parseDouble(durationStr);
			}
		} catch (Exception e) {
			log.error("视频时长获取失败: {}", e.getMessage());
			return -1;
		}
		return -1;
	}
}
