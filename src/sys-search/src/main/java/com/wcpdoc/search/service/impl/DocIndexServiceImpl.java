package com.wcpdoc.search.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.file.entity.FileEx;
import com.wcpdoc.file.service.FileService;
import com.wcpdoc.search.entity.DocSummary;
import com.wcpdoc.search.service.DocIndexService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 文档检索服务层实现
 * 
 * v1.0 zhanghc 2026年1月7日下午11:33:10
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DocIndexServiceImpl implements DocIndexService {
	private final IndexWriter indexWriter;
	private final Analyzer analyzer;
	private final SearcherManager searcherManager;
	private final Tika tika;
	private final FileService fileService;

	@Override
	public void add(Integer bizId, Integer fileId) {
		if (!ValidateUtil.isValid(bizId)) {
			throw new MyException("参数错误：bizId");
		}
		if (!ValidateUtil.isValid(fileId)) {
			throw new MyException("参数错误：fileId");
		}

		FileEx fileEx = fileService.getFileEx(fileId);
		if (fileEx == null) {
			throw new MyException("附件不存在");
		}
		String content;
		try {
			content = tika.parseToString(fileEx.getFile());
		} catch (Exception e) {
			throw new MyException("解析文档失败");
		}
		if (!ValidateUtil.isValid(content)) {
			throw new MyException("文档内容为空");
		}

		Document doc = new Document();
		doc.add(new IntPoint("bizId", bizId));
		doc.add(new IntPoint("fileId", fileId));
		doc.add(new TextField("title",
				String.format("%s.%s", fileEx.getEntity().getName(), fileEx.getEntity().getExtName()),
				Field.Store.YES));
		doc.add(new TextField("content", content, Field.Store.YES));

		try {
			indexWriter.addDocument(doc);
			searcherManager.maybeRefresh();
		} catch (Exception e) {
			log.error("写入索引失败：", e);
			throw new MyException("写入索引失败");
		}
	}

	@Override
	public void del(Integer bizId, Integer fileId) {
		if (!ValidateUtil.isValid(bizId)) {
			throw new MyException("参数错误：bizId");
		}
		if (!ValidateUtil.isValid(fileId)) {
			throw new MyException("参数错误：fileId");
		}

		BooleanQuery.Builder builder = new BooleanQuery.Builder();
		builder.add(IntPoint.newExactQuery("bizId", bizId), Occur.MUST);
		builder.add(IntPoint.newExactQuery("fileId", fileId), Occur.MUST);
		Query deleteQuery = builder.build();

		try {
			indexWriter.deleteDocuments(deleteQuery);
			searcherManager.maybeRefresh();
		} catch (Exception e) {
			log.error("删除索引失败：", e);
			throw new MyException("删除索引失败");
		}
	}

	public List<DocSummary> search(Integer bizId, String content, Integer pageSize) {
		if (!ValidateUtil.isValid(bizId)) {
			throw new MyException("参数错误：bizId");
		}
		if (!ValidateUtil.isValid(content)) {
			throw new MyException("参数错误：content");
		}
		if (!ValidateUtil.isValid(pageSize)) {
			throw new MyException("参数错误：pageSize");
		}

		IndexSearcher searcher = null;
		try {
			searcher = searcherManager.acquire();

			BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
			booleanQueryBuilder.add(IntPoint.newExactQuery("bizId", bizId), Occur.MUST);
			booleanQueryBuilder.add(new QueryParser("content", analyzer).parse(QueryParser.escape(content.trim())),
					Occur.MUST);
			Query query = booleanQueryBuilder.build();
			TopDocs topDocs = searcher.search(query, pageSize);

			QueryScorer scorer = new QueryScorer(query);
			Highlighter highlighter = new Highlighter(new SimpleHTMLFormatter("<mark>", "</mark>"), scorer);
			highlighter.setTextFragmenter(new SimpleSpanFragmenter(scorer, 100));
			highlighter.setMaxDocCharsToAnalyze(1_000_000);

			List<DocSummary> results = new ArrayList<>();
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				Document doc = searcher.storedFields().document(scoreDoc.doc);
				List<String> summaryList = List.of();
				try (TokenStream tokenStream = analyzer.tokenStream("content", doc.get("content"))) {
					summaryList = Arrays.asList(highlighter.getBestFragments(tokenStream, doc.get("content"), 2));
				} catch (Exception e) {
					log.error("检索内容错误：{}", e.getMessage());
				}

				results.add(DocSummary.builder()//
						.id(doc.get("bizId"))//
						.id(doc.get("fileId"))//
						.title(doc.get("title"))//
						.summaryList(summaryList)//
						.build());
			}

			return results;
		} catch (Exception e) {
			log.error("查询索引失败：", e);
			throw new MyException("查询索引失败");
		} finally {
			if (searcher != null) {
				try {
					searcherManager.release(searcher);
				} catch (IOException e) {
				}
			}
		}
	}

	@Override
	public void delAll() {
		try {
			indexWriter.deleteAll();
			searcherManager.maybeRefresh();
		} catch (Exception e) {
			log.error("删除全部索引失败：", e);
			throw new MyException("删除全部索引失败");
		}
	}

}