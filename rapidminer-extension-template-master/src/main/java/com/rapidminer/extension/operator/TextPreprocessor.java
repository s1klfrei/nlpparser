package com.rapidminer.extension.operator;

import java.util.Collection;

import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.text.Document;

import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;

public class TextPreprocessor extends Operator {
	
	
	private InputPort textInput = getInputPorts().createPort("doc", IOObject.class);
	
	private OutputPort textOutput = getOutputPorts().createPort("doc");
	
	
	public TextPreprocessor(OperatorDescription description) {
		super(description);
	}
	
	public void doWork() throws UserError {
		Document inputDoc =(Document) textInput.getData(IOObject.class);
	
		String outputText = "";
		
		String[] sentences = inputDoc.getTokenText().split("\\r?\\n");
		
		
		for(int i = 0; i < sentences.length; i++) {
			if(!(sentences[i].startsWith("\t") || sentences[i].startsWith(" ")))
				if ( outputText != "") {
					outputText +=  "\n" + sentences[i].trim();
				} 
				else {
					outputText += sentences[i].trim();
				}
			else
				outputText += sentences[i].trim();
		}
		
		Document outputDoc = new Document(outputText);
		
		textOutput.deliver(outputDoc);
	}
}
