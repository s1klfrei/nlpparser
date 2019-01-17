package com.rapidminer.extension.operator;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
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
	
	/**
	 * @param PARAMETER_TEXT Eingabe des Users
	 */
	public static final String PARAMETER_GRAMMAR = "Grammar for StanfordParser"; //Name des Parameters
	
	public static final String PARAMETER_INPUT = "Input sentence for the parser";
	
	
	//private InputPort documentInput = getInputPorts().createPort("document", Document.class);
	private InputPort ioobjectInput = getInputPorts().createPort("io object", IOObject.class);
	
	public StanfordParser(OperatorDescription description) {
		super(description);
	}
	
	@Override
	public List<ParameterType> getParameterTypes(){
	    List<ParameterType> types = super.getParameterTypes();
	    types.add(new ParameterTypeString(
	    		PARAMETER_GRAMMAR,
	    		"This is the absolute path of the grammar file",
	    		"C:\\Users\\Klaus\\Documents\\Uni\\BachelorArbeit\\Parser\\stanford-parser-full-2018-02-27\\englishPCFG.ser.gz",
	    		false
	    		));
	    types.add(new ParameterTypeString(
	    		PARAMETER_INPUT,
	    		"This is the input sentence for the parser",
	    		"This is an easy Sentence.",
	    		false
	    		));
	    return types;
	}
	
	
	@Override
	public void doWork() throws OperatorException{
		
		//Document doc = documentInput.getData(Document.class);
		IOObject ioo = ioobjectInput.getData(IOObject.class);
		//Document iooDoc = ioobjectInput.getData(IOObject.class);
		/*
		if(ioo instanceof Document) {
			LogService.getRoot().log(Level.INFO, "ioo ist Document");
		} else {
			LogService.getRoot().log(Level.INFO, "ioo ist kein Doc");
		}
		*/
		
		//LogService.getRoot().log(Level.INFO, );
		
		/*LogService.getRoot().log(Level.INFO, "Print Document: " + iooDoc.toString());
		LogService.getRoot().log(Level.INFO, "Print IOObject: " + ioo.toString());
		
		
		LogService.getRoot().log(Level.INFO, "Print Token Sequence: " + iooDoc.getTokenSequence());
		LogService.getRoot().log(Level.INFO, "Print Token Text: " + iooDoc.getTokenText());*/

		String grammar = getParameterAsString(PARAMETER_GRAMMAR);

		String input = getParameterAsString(PARAMETER_INPUT);
		
		LexicalizedParser lp = LexicalizedParser.loadModel(
				grammar,
				"-maxLength", "80", "-retainTmpSubcategories");
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		// Uncomment the following line to obtain original Stanford Dependencies
		tlp.setGenerateOriginalDependencies(true);
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		//String[] sent = {"This", "is", "an", "easy", "sentence", "."};
		String[] sent = input.split(" ");
		Tree parse = lp.apply(SentenceUtils.toWordList(sent));
		GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
		Collection<TypedDependency> tdl = gs.typedDependencies();
		//System.out.println(parse);
		
		LogService.getRoot().log(Level.INFO, parse.toString());
		
	}
}
