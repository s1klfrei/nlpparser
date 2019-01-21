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
		
		String falseLines = ""; 
		String correctLines = "";
		int numberAllLines = parserLines.length;
		int numberCorrectLines = 0;
		
		if(parserLines.length > goldStandardLines.length) {
			numberAllLines = goldStandardLines.length;
			for(int i = 0; i < goldStandardLines.length; i++) {
				
				parserLines[i] = parserLines[i].replaceAll(" ", "");
				goldStandardLines[i] = goldStandardLines[i].replaceAll(" ", "");
				
				if(Objects.equals(goldStandardLines[i], parserLines[i])) {
					numberCorrectLines++;
					correctLines += "\n" + "Zeile " + i + ": " + parserLines[i];
				}
				else {
					falseLines += "\n" + "Parser(Zeile " + i + "): " + "\t" + parserLines[i];
					falseLines += "\n" + "Standard(Zeile " + i + "): " + "\t" + goldStandardLines[i];
					falseLines += "\n" + "Differenz bei(Zeile " + i + "): " + "\t" + indexOfDifference(parserLines[i],  goldStandardLines[i]);
				}
			}
		}
		else {
			for(int i = 0; i < parserLines.length; i++) {
				
				parserLines[i] = parserLines[i].replaceAll(" ", "");
				goldStandardLines[i] = goldStandardLines[i].replaceAll(" ", "");
				
				if(Objects.equals(goldStandardLines[i], parserLines[i])) {
					numberCorrectLines++;
					correctLines += "\n" + "Zeile " + i + ": " + parserLines[i];
				}
				else {
					falseLines += "\n" + "Parser(Zeile " + i + "): " + "\t" + parserLines[i];
					falseLines += "\n" + "Standard(Zeile " + i + "): " + "\t" + goldStandardLines[i];
					falseLines += "\n" + "Differenz bei(Zeile " + i + "): " + "\t" + indexOfDifference(parserLines[i],  goldStandardLines[i]);
				}
			}
		}
		
		int result =(int) ( 100 * ((float)numberCorrectLines/(float)numberAllLines)) ;
		
		String correctString = "Correct lines: " + numberCorrectLines 
				+ "\n" + "All lines: " + numberAllLines
				+ "\n" + "Ratio: " + result + "%"
				+ "\n" + correctLines;
		
		
		Document resultDoc = new Document(correctString);
		Document mistakeDoc = new Document(falseLines);
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
