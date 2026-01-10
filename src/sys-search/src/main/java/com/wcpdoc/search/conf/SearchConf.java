package com.wcpdoc.search.conf;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.Tika;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * 检索配置
 * 
 * v1.0 zhanghc 2026年1月7日下午11:33:10
 */
@Configuration
public class SearchConf {

	@Bean(destroyMethod = "close")
	public Directory luceneDirectory() throws IOException {
		return FSDirectory.open(Paths.get(System.getProperty("user.dir"), "bak", "search-index"));
	}

	@Bean(destroyMethod = "close")
	@DependsOn("luceneDirectory")
	public IndexWriter luceneIndexWriter(Directory directory, Analyzer analyzer) throws IOException {
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
		return new IndexWriter(directory, config);
	}

	@Bean(destroyMethod = "close")
	@DependsOn("luceneIndexWriter")
	public SearcherManager searcherManager(IndexWriter indexWriter) throws IOException {
		return new SearcherManager(indexWriter, null);
	}

	@Bean
	public Tika tika() {
		return new Tika();
	}
}