package com.datacurationthesis.datacurationthesis.service;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;



@Service
public class NlpService {

	private final StanfordCoreNLP pipeline;

	public NlpService() {
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
		pipeline = new StanfordCoreNLP(props);
	}


	public List<String> tokenize(String text) {
		CoreDocument document = new CoreDocument(text);
		pipeline.annotate(document);
		return document.tokens().stream().map(CoreLabel::word).collect(Collectors.toList());
	}

	public Map<String, List<String>> extractEntities(String text) {
		CoreDocument document = new CoreDocument(text);
		pipeline.annotate(document);

		Map<String, List<String>> entities = new HashMap<>();
		for (CoreLabel token : document.tokens()) {
			String word = token.word();
			String ner = token.ner();
			if (!"O".equals(ner)) {
				entities.computeIfAbsent(ner, k -> new ArrayList<>()).add(word);
			}
		}
		return entities;
	}
}
