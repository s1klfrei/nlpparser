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
		
		//Fuer den Parser muessen Leerzeichen zwischen allen Token sein, ansonsten ordnet er sie falsch ein.
	
		String outputText = inputDoc.getTokenText();
		
		//Apostroph s muss wegen Genitiv besonders getrennt werden
		outputText = outputText.replaceAll("\\bhe's\\b", "he 's");
		outputText = outputText.replaceAll("\\bshe's\\b", "she 's");
		outputText = outputText.replaceAll("\\bit's\\b", "it 's");
		outputText = outputText.replaceAll("\\there's\\b", "there 's");
		outputText = outputText.replaceAll("\\that's\\b", "that 's");
		
		//Im Gold Standard wird das n mit abgetrennt
		outputText = outputText.replaceAll("n't", " n't");
		
		outputText = outputText.replaceAll("'ve", " 've");
		outputText = outputText.replaceAll("'d", " 'd");
		outputText = outputText.replaceAll("'ll", " 'll");
		outputText = outputText.replaceAll("'m", " 'm");
		
		//Satzzeichen
		outputText = outputText.replaceAll(",", " ,");
		outputText = outputText.replaceAll("\\.", " .");
		outputText = outputText.replaceAll("\\?", " ?");
		outputText = outputText.replaceAll("!", " !");
		outputText = outputText.replaceAll("\"\\s", " \"");
		outputText = outputText.replaceAll("\"[\\w]", " \" ");
		outputText = outputText.replaceAll(":", " :");
		
		
		//Bei allen Zeilen Trim aufrufen
		String[] sentences = outputText.split("\\r?\\n");
		
		outputText = "";
		
		
		for(int i = 0; i < sentences.length; i++) {
			sentences[i] = sentences[i].trim();
			if(i==0)
				outputText += sentences[i];
			else
				outputText += "\n" + sentences[i];
		}
		
		Document outputDoc = new Document(outputText);
		
		textOutput.deliver(outputDoc);
	}
}
