package com.wcpdoc.search.analyzer;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.springframework.stereotype.Component;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

/**
 * 分析器
 * 
 * v1.0 zhanghc 2026年1月7日下午11:33:10
 */
@Component
public class HanLPLuceneAnalyzer extends Analyzer {

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		return new TokenStreamComponents(new HanLPTokenizer());
	}

	public static class HanLPTokenizer extends Tokenizer {
		private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
		private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);

		private List<Term> terms = null;
		private int current = 0;

		public HanLPTokenizer() {
			super();
		}

		@Override
		public boolean incrementToken() throws IOException {
			if (terms == null) {
				String text = inputStreamToString(input);
				Segment segment = HanLP.newSegment().enableIndexMode(true).enablePartOfSpeechTagging(true);
				List<Term> rawTerms = segment.seg(text);
				this.terms = rawTerms.stream().filter(term -> {
					Nature pos = term.nature;
					if (pos == null) {
						return true;
					}
					if (pos == Nature.w) {
						return false;
					}
					String tag = pos.toString();
					if (tag.startsWith("u") || tag.startsWith("c") || tag.startsWith("p") || tag.startsWith("e")
							|| tag.startsWith("o") || tag.startsWith("y") || "vshi".equals(tag) || "vyou".equals(tag)) {
						return false;
					}
					return true;
				}).collect(Collectors.toList());
				current = 0;
			}

			if (current >= terms.size()) {
				return false;
			}

			Term term = terms.get(current++);
			clearAttributes();
			termAtt.setEmpty().append(term.word);
			int start = term.offset;
			int end = start + term.word.length();
			offsetAtt.setOffset(correctOffset(start), correctOffset(end));
			return true;
		}

		private String inputStreamToString(Reader reader) throws IOException {
			StringBuilder sb = new StringBuilder();
			char[] buffer = new char[2048];
			int len;
			while ((len = reader.read(buffer)) != -1) {
				sb.append(buffer, 0, len);
			}
			return sb.toString();
		}

		@Override
		public void reset() throws IOException {
			super.reset();
			this.terms = null;
			this.current = 0;
		}
	}
}