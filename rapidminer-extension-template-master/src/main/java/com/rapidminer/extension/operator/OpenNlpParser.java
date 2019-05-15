package com.rapidminer.extension.operator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;

import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.nio.file.SimpleFileObject;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.text.Document;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.tools.LogService;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;

public class OpenNlpParser extends Operator{
	
	private InputPort ioobjectInputGrammar = getInputPorts().createPort("grammar", IOObject.class);
	private InputPort ioobjectInputText = getInputPorts().createPort("text", IOObject.class);
	private OutputPort ioobjectOutput = getOutputPorts().createPort("output");
	private OutputPort nameOutput = getOutputPorts().createPort("name");
		
	public OpenNlpParser(OperatorDescription description) {
		super(description);
	}
	
	
	@Override
	public void doWork() throws OperatorException{
		
		Document iooDoc =(Document) ioobjectInputText.getData(IOObject.class);
		
		SimpleFileObject grammarObject = (SimpleFileObject) ioobjectInputGrammar.getData(IOObject.class);
		File grammarFile = grammarObject.getFile();
		String grammarPath = grammarFile.getAbsolutePath();
	
		String text = iooDoc.getTokenText();
		String[] sentences = text.split("\\r?\\n");
		
		String outputText = "";
		
		InputStream modelIn;
		try {
			//Parameter fuer Filename
			//modelIn = new FileInputStream("C:\\Users\\Klaus\\Documents\\Uni\\BachelorArbeit\\Parser\\apache-opennlp-1.9.0-bin.tar\\apache-opennlp-1.9.0\\en-parser-chunking.bin");
			modelIn = new FileInputStream(grammarPath);
			
			try {
			  ParserModel model = new ParserModel(modelIn);
			  Parser parser = ParserFactory.create(model);

			  for(int i = 0; i < sentences.length; i++) {
				  
				  Parse topParses[] = ParserTool.parseLine(sentences[i], parser, 1);
				  for(int j = 0; j < topParses.length; j++) {
					  StringBuffer sb = new StringBuffer(topParses[j].getText().length() * 4);
					  topParses[j].show(sb); 
					  //LogService.getRoot().log(Level.INFO, sb.toString());
					  if(i == 0)
							outputText += sb.toString();
						else 
							outputText += '\n' + sb.toString(); 
				  }
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
		outputText = outputText.replaceAll("\\bTOP\\b", "");
		Document outputDoc = new Document(outputText);
		ioobjectOutput.deliver((IOObject)outputDoc);
		
		Document nameDoc = new Document("OpenNLP Parser");
		nameOutput.deliver((IOObject)nameDoc);
	}
}