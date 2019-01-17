package com.rapidminer.extension.operator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.tools.LogService;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;

public class OpenNlpParser extends Operator{
	
	/**
	 * @param PARAMETER_TEXT Eingabe des Users
	 */
	public static final String PARAMETER_TEXT = "Input sentence"; //Name des Parameters
	
	
	/**
	 * @param PARAMETER_TEXT Eingabe des Users
	 */
	public static final String PARAMETER_PATH = "Path to the model"; //Name des Parameters
	
	public OpenNlpParser(OperatorDescription description) {
		super(description);
	}
	
	@Override
	public List<ParameterType> getParameterTypes(){
	    List<ParameterType> types = super.getParameterTypes();
	    types.add(new ParameterTypeString(
	    		PARAMETER_TEXT,
	    		"This is the input sentence.",
	    		"This is an easy senctence.",
	    		false
	    		));
	    
	    types.add(new ParameterTypeString(
	    		PARAMETER_PATH,
	    		"This is the path to the model",
	    		"C:\\Users\\Klaus\\Documents\\Uni\\BachelorArbeit\\Parser\\apache-opennlp-1.9.0-bin.tar\\apache-opennlp-1.9.0\\en-parser-chunking.bin",
	    		false
	    		));
	    return types;
	}
	
	
	@Override
	public void doWork() throws OperatorException{
		
		String text = getParameterAsString(PARAMETER_TEXT);
		String pfad = getParameterAsString(PARAMETER_PATH);
	
		
		InputStream modelIn;
		try {
			//Parameter fuer Filename
			//modelIn = new FileInputStream("C:\\Users\\Klaus\\Documents\\Uni\\BachelorArbeit\\Parser\\apache-opennlp-1.9.0-bin.tar\\apache-opennlp-1.9.0\\en-parser-chunking.bin");
			modelIn = new FileInputStream(pfad);
			
			try {
			  ParserModel model = new ParserModel(modelIn);
			  Parser parser = ParserFactory.create(model);
			  String sentence = text;
			  Parse topParses[] = ParserTool.parseLine(sentence, parser, 1);
			  for(int i = 0; i < topParses.length; i++) {
				  StringBuffer sb = new StringBuffer(topParses[i].getText().length() * 4);
				  topParses[i].show(sb); 
				  LogService.getRoot().log(Level.INFO, sb.toString());
			  }
			  
			}
			catch (IOException e) {
				LogService.getRoot().log(Level.INFO, e.getMessage());
			}
			finally {
			  if (modelIn != null) {
			    try {
			      modelIn.close();
			    }
			    catch (IOException e) {
			    }
			  }
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			LogService.getRoot().log(Level.INFO, e1.getMessage());
		}
	}
}