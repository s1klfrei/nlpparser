package com.rapidminer.extension.operator;

import java.util.Objects;
import java.util.logging.Level;

import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.text.Document;
import com.rapidminer.tools.LogService;

public class CompareResults extends Operator{
	
	private InputPort parserInput = getInputPorts().createPort("parser", IOObject.class);
	private InputPort goldStandardInput = getInputPorts().createPort("gold standard", IOObject.class);
	
	private OutputPort resultOutput = getOutputPorts().createPort("res");
	private OutputPort documentOutput = getOutputPorts().createPort("doc");
	
	
	public CompareResults(OperatorDescription description) {
		super(description);
	}
	
	@Override
	public void doWork() throws UserError {
		Document parserDoc =(Document) parserInput.getData(IOObject.class);
		Document goldStandardDoc =(Document) goldStandardInput.getData(IOObject.class);
		
		String parserText = parserDoc.getTokenText();
		String goldStandardText = goldStandardDoc.getTokenText();
		
		String[] parserLines = parserText.split("\\r?\\n");
		String[] goldStandardLines = goldStandardText.split("\\r?\\n");
		
		String outputText = ""; 
		int allLines = parserLines.length;
		int correctLines = 0;
		
		if(parserLines.length > goldStandardLines.length) {
			allLines = goldStandardLines.length;
			for(int i = 0; i < goldStandardLines.length; i++) {
				
				parserLines[i] = parserLines[i].replaceAll(" ", "");
				goldStandardLines[i] = goldStandardLines[i].replaceAll(" ", "");
				
				if(Objects.equals(goldStandardLines[i], parserLines[i])) {
					correctLines++;
				}
				else {
					outputText += parserLines[i] + "\n\n" + goldStandardLines[i] + "\n";
					outputText += indexOfDifference(parserLines[i],  goldStandardLines[i]) + "\n";
				}
			}
		}
		else {
			for(int i = 0; i < parserLines.length; i++) {
				
				//parserLines[i] = parserLines[i].replaceAll(" ", "");
				//goldStandardLines[i] = goldStandardLines[i].replaceAll(" ", "");
				
				if(Objects.equals(goldStandardLines[i], parserLines[i])) {
					correctLines++;
				}
				else {
					outputText += "\n" + "Parser(Zeile " + i + "): " + "\t" + parserLines[i];
					outputText += "\n" + "Standard(Zeile " + i + "): " + "\t" + goldStandardLines[i];
					outputText += "\n" + "Differenz bei(Zeile " + i + "): " + "\t" + indexOfDifference(parserLines[i],  goldStandardLines[i]);
				}
			}
		}
		
		float result = correctLines/allLines;
		
		String resultNumbers = "Correct lines: " + correctLines 
				+ "\n" + "All lines: " + allLines
				+ "\n" + "Ratio: " + Float.toString(result);
		
		
		Document resultDoc = new Document(resultNumbers);
		Document mistakeDoc = new Document(outputText);
		resultOutput.deliver(resultDoc);
		documentOutput.deliver(mistakeDoc);
		
	}
	
	public static int indexOfDifference(String str1, String str2) {
	    if (str1 == str2) {
	        return -1;
	    }
	    if (str1 == null || str2 == null) {
	        return 0;
	    }
	    int i;
	    for (i = 0; i < str1.length() && i < str2.length(); ++i) {
	        if (str1.charAt(i) != str2.charAt(i)) {
	            break;
	        }
	    }
	    if (i < str2.length() || i < str1.length()) {
	        return i;
	    }
	    return -1;
	}
	
	
}
