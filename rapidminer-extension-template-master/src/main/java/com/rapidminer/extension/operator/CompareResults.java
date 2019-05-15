package com.rapidminer.extension.operator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.logging.Level;

import com.rapidminer.datatable.SimpleDataTable;
import com.rapidminer.datatable.SimpleDataTableRow;
import com.rapidminer.example.Attribute;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.example.table.DataRow;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.example.utils.ExampleSetBuilder;
import com.rapidminer.example.utils.ExampleSets;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.text.Document;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.Ontology;


public class CompareResults extends Operator{
	
	private InputPort nameInput = getInputPorts().createPort("name", IOObject.class);
	private InputPort parserInput = getInputPorts().createPort("parser", IOObject.class);
	private InputPort goldStandardInput = getInputPorts().createPort("gold standard", IOObject.class);
	
	private OutputPort resultOutput = getOutputPorts().createPort("res");	
	
	public CompareResults(OperatorDescription description) {
		super(description);
	}
	
	@Override
	public void doWork() throws UserError {
		Document nameDoc =(Document) nameInput.getData(IOObject.class);
		Document parserDoc =(Document) parserInput.getData(IOObject.class);
		Document goldStandardDoc =(Document) goldStandardInput.getData(IOObject.class);
		
		String parserName = nameDoc.getTokenText();
		String parserText = parserDoc.getTokenText();
		String goldStandardText = goldStandardDoc.getTokenText();
		
		String[] parserLines = parserText.split("\\r?\\n");
		String[] goldStandardLines = goldStandardText.split("\\r?\\n");
		
		double globalPrecision = 0.0;
		double globalRecall = 0.0;
		double globalF1 = 0.0;
		double globalCrossBrackets = 0.0;
		int count = 0;
		
		
		for(int i = 0; i < Math.min(goldStandardLines.length, parserLines.length); i++) {
			
			parserLines[i] = formatSentence(parserLines[i]);
			goldStandardLines[i] = formatSentence(goldStandardLines[i]);
			
			List<ParseTreeNode> goldStandardNodes = parseSentence(goldStandardLines[i]);
			List<ParseTreeNode> parserNodes = parseSentence(parserLines[i]);
			
			int numberCorrectNodes = 0;
			for(int j = 0; j < goldStandardNodes.size(); j++) {
				boolean korrekt = false;
				for(int k = 0; k < parserNodes.size(); k++) {
					if(!korrekt) {
						if(goldStandardNodes.get(j).equals(parserNodes.get(k))){
							numberCorrectNodes ++;
							korrekt = true;
						}
					}
				}	
			}
			
			double precision = (double)numberCorrectNodes/(double)parserNodes.size();
			double recall = (double)numberCorrectNodes/(double)goldStandardNodes.size();
			double f1 = 2*(precision*recall)/(precision+recall);
			
			if(precision != 0.0 && recall != 0.0) {
				globalPrecision += precision;
				globalRecall += recall;
				globalF1 += f1;
			}
			count ++;
			
		}
		
		globalPrecision /= (double)count;
		globalRecall /= (double)count;
		globalF1 /= (double)count;
		
		List<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(AttributeFactory.createAttribute("Name",Ontology.STRING));
		attributes.add(AttributeFactory.createAttribute("Precision",Ontology.REAL));
		attributes.add(AttributeFactory.createAttribute("Recall",Ontology.REAL));
		attributes.add(AttributeFactory.createAttribute("F1",Ontology.REAL));
		
		ExampleSetBuilder examplesetBuilder = ExampleSets.from(attributes);

		double[] row = new double[4];
	    
		row[0] = attributes.get(0).getMapping().mapString(parserName);
	    row[1] = globalPrecision;
	    row[2] = globalRecall;
	    row[3] = globalF1;
	    
	    examplesetBuilder.addRow(row);
		ExampleSet resSet = examplesetBuilder.build();
		
		/*String correctString = "Precision: " + globalPrecision 
				+ "\n" + "Recall: " + globalRecall
				+ "\n" + "F1: " + globalF1
				+ "\n" + "Number of lines: " + count;
		*/
		//Document resultDoc = new Document(correctString);
		//resultOutput.deliver(resultDoc);		
		resultOutput.deliver(resSet);
	}
	
	
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
					/*if(ptn.typ.ordinal() <= 9) {
						res.add(ptn);
					}*/
					res.add(ptn);
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
