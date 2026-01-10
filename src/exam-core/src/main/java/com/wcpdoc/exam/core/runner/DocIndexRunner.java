package com.wcpdoc.exam.core.runner;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.wcpdoc.exam.core.entity.QuestionBank;
import com.wcpdoc.exam.core.service.QuestionBankService;
import com.wcpdoc.search.service.DocIndexService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 文档索引启动
 * 
 * v1.0 zhanghc 2026年1月10日上午9:41:53
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DocIndexRunner implements ApplicationRunner {
	private final DocIndexService docIndexService;
	private final QuestionBankService questionBankService;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		docIndexService.delAll();
		
		List<QuestionBank> questionBankList = questionBankService.getList();
		for (QuestionBank questionBank : questionBankList) {
			for (Integer fileId : questionBank.getFileIds()) {
				try {
					docIndexService.add(questionBank.getId(), fileId);
				} catch (Exception e) {
					log.info("题库【{}】文档索引失败：{}", questionBank.getName(), e.getMessage());
				}
			}
		}
		log.info("系统文档索引启动");
	}
}
