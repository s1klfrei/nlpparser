package com.rapidminer.extension.operator;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.nio.file.SimpleFileObject;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.ports.OutputPortExtender;
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
	// Port für Parser Modell
	private InputPort ioobjectInputGrammar = getInputPorts().createPort("grammar", IOObject.class);
	// Port für zu parsenden Text
	private InputPort ioobjectInputText = getInputPorts().createPort("text", IOObject.class);
		
	// Outputports für eigenen Namen
	private OutputPortExtender nameOutputExt = new OutputPortExtender("name", getOutputPorts());
	// Outputports für annotierten Text
	private OutputPortExtender resultOutputExt = new OutputPortExtender("output", getOutputPorts());
	
	
	public StanfordParser(OperatorDescription description) {
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
		
		
		// Standard Code aus Stanford Webseite um Parser zu starten
		// MaxLength gibt an wieviele Wörter ein Satz maximal haben darf
		LexicalizedParser lp = LexicalizedParser.loadModel(
				grammarPath,
				"-maxLength", "80");
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
	
		// Eingabe Document in Zeilen aufteilen
		String text = iooDoc.getTokenText();
		String[] sentences = text.split("\\r?\\n");
		
		// AusgabeText, an welchen jede annotierte Zeile angehangen wird
		String outputText = "";
		
		
		// Jede Zeile in den Parser schicken
		for(int i = 0; i < sentences.length; i++) {
			
			// Parser nimmt String Array als Eingabe. Split teilt Token der Eingabe
			Tree parse = lp.apply(SentenceUtils.toWordList(sentences[i].split(" ")));
			GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
			Collection<TypedDependency> tdl = gs.typedDependencies();
			
			// Rückgabe String befüllen
			if(i == 0)
				outputText += parse.toString();
			else 
				outputText += '\n' + parse.toString();
		}
		// Startelement des Parser 'ROOT' entfernen, Klammer ist notwendig, falls ein Satz das Terminal 'ROOT' enthaelt
		outputText = outputText.replaceAll("\\(ROOT ", "( ");
		
		// Ausgabe Document erstellen
		Document outputDoc = new Document(outputText);
		resultOutputExt.deliverToAll((IOObject)outputDoc, true);
		
		// Name als Document ausgeben
		Document nameDoc = new Document("Stanford Parser");
		nameOutputExt.deliverToAll((IOObject)nameDoc, true);	
	}
}
