package com.datacurationthesis.datacurationthesis.service;

import com.datacurationthesis.datacurationthesis.entity.Venue;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import java.util.Properties;


public class NlpService {

	private StanfordCoreNLP pipeline;
	public NlpService() {
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,depparse,coref,kbp,quote");
		pipeline = new StanfordCoreNLP(props);
	}
}
