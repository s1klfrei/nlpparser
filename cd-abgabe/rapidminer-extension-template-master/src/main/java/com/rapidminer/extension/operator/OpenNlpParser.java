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
import com.rapidminer.operator.ports.InputPortExtender;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.ports.OutputPortExtender;
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
	// Port für Parser Modell
	private InputPort ioobjectInputGrammar = getInputPorts().createPort("grammar", IOObject.class);
	// Port für zu parsenden Text
	private InputPort ioobjectInputText = getInputPorts().createPort("text", IOObject.class);
	// Outputports für eigenen Namen
	private OutputPortExtender nameOutputExt = new OutputPortExtender("name", getOutputPorts());
	// Outputports für annotierten Text
	private OutputPortExtender resultOutputExt = new OutputPortExtender("output", getOutputPorts());
	
	public OpenNlpParser(OperatorDescription description) {
		super(description);
		//PortExtender starten, notwendig
		nameOutputExt.start();
		resultOutputExt.start();
	}
	
	
	@Override
	public void doWork() throws OperatorException{
		// Document aus Port holen
		Document iooDoc =(Document) ioobjectInputText.getData(IOObject.class);

		// Für Grammatik Modell wird nur Pfad der Datei benötigt
		SimpleFileObject grammarObject = (SimpleFileObject) ioobjectInputGrammar.getData(IOObject.class);
		File grammarFile = grammarObject.getFile();
		String grammarPath = grammarFile.getAbsolutePath();

		// Eingabe Document in Zeilen aufteilen
		String text = iooDoc.getTokenText();
		String[] sentences = text.split("\\r?\\n");

		// AusgabeText, an welchen jede annotierte Zeile angehangen wird
		String outputText = "";
		
		// Standard Code aus OpenNLP Webseite um Parser zu starten + auto-generierte Exceptions
		InputStream modelIn;
		try {
			
			modelIn = new FileInputStream(grammarPath);
			
			try {
			  ParserModel model = new ParserModel(modelIn);
			  Parser parser = ParserFactory.create(model);
			  // Jede Zeile in den Parser schicken
			  for(int i = 0; i < sentences.length; i++) {
				  
				  Parse topParses[] = ParserTool.parseLine(sentences[i], parser, 1);
				  for(int j = 0; j < topParses.length; j++) {
					  StringBuffer sb = new StringBuffer(topParses[j].getText().length() * 4);
					  topParses[j].show(sb); 
					  
					// Rückgabe String befüllen
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
		// Startelement des Parser 'TOP' entfernen, Klammer ist notwendig, falls ein Satz das Terminal 'TOP' enthaelt
		outputText = outputText.replaceAll("\\(TOP ", "( ");
		
		// Ausgabe Document erstellen
		Document outputDoc = new Document(outputText);
		resultOutputExt.deliverToAll((IOObject)outputDoc, true);
		
		// Name als Document ausgeben
		Document nameDoc = new Document("OpenNLP Parser");
		nameOutputExt.deliverToAll((IOObject)nameDoc, true);
	}
}