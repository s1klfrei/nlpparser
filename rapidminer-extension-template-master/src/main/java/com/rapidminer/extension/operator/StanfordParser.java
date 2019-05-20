package com.rapidminer.extension.operator;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.nio.file.SimpleFileObject;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.tools.LogService;

import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

import com.rapidminer.operator.text.Document;


public class StanfordParser extends Operator{

	private InputPort ioobjectInputGrammar = getInputPorts().createPort("grammar", IOObject.class);
	private InputPort ioobjectInputText = getInputPorts().createPort("text", IOObject.class);
	
	private OutputPort nameOutput = getOutputPorts().createPort("name");
	private OutputPort ioobjectOutput = getOutputPorts().createPort("output");

	public StanfordParser(OperatorDescription description) {
		super(description);
	}
	
	
	@Override
	public void doWork() throws OperatorException{
		
		//Document doc = documentInput.getData(Document.class);
		//IOObject ioo = ioobjectInput.getData(IOObject.class);
		Document iooDoc =(Document) ioobjectInputText.getData(IOObject.class);
		/*
		if(ioo instanceof Document) {
			LogService.getRoot().log(Level.INFO, "ioo ist Document");
		} else {
			LogService.getRoot().log(Level.INFO, "ioo ist kein Doc");
		}
		
		
		//LogService.getRoot().log(Level.INFO, );
		
		LogService.getRoot().log(Level.INFO, "Print Document: " + iooDoc.toString());
		LogService.getRoot().log(Level.INFO, "Print IOObject: " + ioo.toString());
		
		
		LogService.getRoot().log(Level.INFO, "Print Token Sequence: " + iooDoc.getTokenSequence());
		LogService.getRoot().log(Level.INFO, "Print Token Text: " + iooDoc.getTokenText());*/
		
		SimpleFileObject grammarObject = (SimpleFileObject) ioobjectInputGrammar.getData(IOObject.class);
		File grammarFile = grammarObject.getFile();
		String grammarPath = grammarFile.getAbsolutePath();

		// String input = getParameterAsString(PARAMETER_INPUT);
		
		LexicalizedParser lp = LexicalizedParser.loadModel(
				grammarPath,
				"-maxLength", "80", "-retainTmpSubcategories");
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		
		// Uncomment the following line to obtain original Stanford Dependencies
		tlp.setGenerateOriginalDependencies(true);
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		
		//String[] sent = {"This", "is", "an", "easy", "sentence", "."};
		//LogService.getRoot().log(Level.INFO, "Text: " + iooDoc.getTokenText());
		
		String text = iooDoc.getTokenText();
		String[] sentences = text.split("\\r?\\n");
		
		String outputText = "";
		
		
		
		for(int i = 0; i < sentences.length; i++) {
		
			//LogService.getRoot().log(Level.INFO, "Line: " + sentences[i]);
			Tree parse = lp.apply(SentenceUtils.toWordList(sentences[i].split(" ")));
			GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
			Collection<TypedDependency> tdl = gs.typedDependencies();
			
			//LogService.getRoot().log(Level.INFO, parse.toString());
			if(i == 0)
				outputText += parse.toString();
			else 
				outputText += '\n' + parse.toString();
		}
		outputText = outputText.replaceAll("\\bROOT\\b", "");
		Document outputDoc = new Document(outputText);
		ioobjectOutput.deliver((IOObject)outputDoc);
		
		Document nameDoc = new Document("Stanford Parser");
		nameOutput.deliver((IOObject)nameDoc);
		/*
		Tree parse = lp.apply(SentenceUtils.toWordList(sent));
		GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
		Collection<TypedDependency> tdl = gs.typedDependencies();
		//System.out.println(parse);
		
		LogService.getRoot().log(Level.INFO, parse.toString());
		*/
		
	}
}
