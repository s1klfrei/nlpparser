package com.rapidminer.extension.operator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
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
		
		/*
		//Entfernt Suffixe (RelationTags) aus Parser und Goldstandard
		parserText = removeSuffix(parserText);
		goldStandardText = removeSuffix(goldStandardText);
		*/
		String[] parserLines = parserText.split("\\r?\\n");
		String[] goldStandardLines = goldStandardText.split("\\r?\\n");
		
		
		/*
		String falseLines = ""; 
		String correctLines = "";
		int numberAllLines = parserLines.length;*/
		
		double globalPrecision = 0.0;
		double globalRecall = 0.0;
		double globalF1 = 0.0;
		int count = 0;
		
		
		// numberAllLines = goldStandardLines.length;
		for(int i = 0; i < Math.min(goldStandardLines.length, parserLines.length); i++) {
			
			parserLines[i] = formatSentence(parserLines[i]);
			goldStandardLines[i] = formatSentence(goldStandardLines[i]);
			
			List<ParseTreeNode> goldStandardNodes = parseSentence(goldStandardLines[i]);
			List<ParseTreeNode> parserNodes = parseSentence(parserLines[i]);
			
			int numberCorrectNodes = 0;
			for(int j = 0; j < goldStandardNodes.size(); j++) {
				for(int k = 0; k < parserNodes.size(); k++) {
					if(goldStandardNodes.get(j).equals(parserNodes.get(k))){
						numberCorrectNodes ++;
					}
				}	
			}
			
			double precision = (double)numberCorrectNodes/(double)parserNodes.size();
			double recall = (double)numberCorrectNodes/(double)goldStandardNodes.size();
			double f1 = 2*(precision*recall)/(precision+recall);
			// LogService.getRoot().log(Level.INFO, String.valueOf(f1));
			
			if(precision != 0.0 && recall != 0.0) {
				globalPrecision += precision;
				globalRecall += recall;
				globalF1 += f1;
			}
			count ++;
			/*
			if(Objects.equals(goldStandardLines[i], parserLines[i])) {
				numberCorrectLines++;
				correctLines += "\n" + "Zeile " + i + ": " + parserLines[i];
			}
			else {
				falseLines += "\n" + "Parser(Zeile " + i + "): " + "\t" + parserLines[i];
				falseLines += "\n" + "Standard(Zeile " + i + "): " + "\t" + goldStandardLines[i];
				falseLines += "\n" + "Differenz bei(Zeile " + i + "): " + "\t" + indexOfDifference(parserLines[i],  goldStandardLines[i]);
			}*/
			
		}
		
		globalPrecision /= (double)count;
		globalRecall /= (double)count;
		globalF1 /= (double)count;

		
		// int result =(int) ( 100 * ((float)numberCorrectLines/(float)numberAllLines)) ;
		
		String correctString = "Precision: " + globalPrecision 
				+ "\n" + "Recall: " + globalRecall
				+ "\n" + "F1: " + globalF1
				+ "\n" + "Number of lines: " + count;
		
		Document resultDoc = new Document(correctString);
		resultOutput.deliver(resultDoc);
		documentOutput.deliver(resultDoc);
		
	}
	/*
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
	
	public static String removeSuffix(String s) {
		return s.replaceAll("-[\\S]*", "");
	}*/
	
	private static List<ParseTreeNode> parseSentence(String s) {
		List<ParseTreeNode> res = new ArrayList<ParseTreeNode>();
		Stack<ParseTreeNode> st = new Stack<ParseTreeNode>();
		
		String[] token = s.split(" ");
		
		int wordCount = 0;
		
		for(int i = 0; i < token.length; i ++) {
			if(token[i].equals("(")) {
				if(!token[i+1].equals("(")) {
					ParseTreeNode ptn = new ParseTreeNode();
					ptn.start = wordCount;
					st.push(ptn);
				}
			}
			else if(token[i].equals(")")) {
				if(!st.empty()) {
					ParseTreeNode ptn = st.pop();
					ptn.ende = wordCount;
					if(ptn.typ.ordinal() <= 9) {
						res.add(ptn);
					}				
				}
			}
			else { 
				if(token[i-1].equals("(")) {
					ParseTreeNode ptn = st.pop();
					ptn.typ =  PennTag.stringToPennTag(token[i]);
					st.push(ptn);
				}
				else {
					wordCount ++;
				}
			}
		}
		return res;
	}
	
	private static String formatSentence(String s) {
		s = s.replaceAll("\\(", " ( ");
		s = s.replaceAll("\\)", " ) ");
		s = s.replaceAll("\\s+",  " ");
		if(s.indexOf(" ") == 0) {
			s = s.substring(1);
		}
		return s;
	}
	
}
