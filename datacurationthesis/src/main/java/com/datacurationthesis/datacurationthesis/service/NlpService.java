package com.datacurationthesis.datacurationthesis.service;

import com.datacurationthesis.datacurationthesis.entity.Venue;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;


public class NlpService {

	private StanfordCoreNLP pipeline;

	public NlpService() {
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,depparse,coref,kbp,quote");
		pipeline = new StanfordCoreNLP(props);
	}

	public List<String> tokenize(String text) {
		CoreDocument document = new CoreDocument(text);
		pipeline.annotate(document);
		return document.tokens().stream().map(CoreLabel::word).collect(Collectors.toList());
	}
}
