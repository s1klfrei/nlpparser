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
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.Ontology;


public class CompareResults extends Operator{
	// Name des Parsers
	private InputPort nameInput = getInputPorts().createPort("name", IOObject.class);
	
	// Ergebnistext des Parsers
	private InputPort parserInput = getInputPorts().createPort("parser", IOObject.class);
	
	// Text des Goldstandards
	private InputPort goldStandardInput = getInputPorts().createPort("gold standard", IOObject.class);
	
	// Ausgabe des Vergleichs
	private OutputPort resultOutput = getOutputPorts().createPort("res");	
	
	// Parameter der angibt, ob Suffix ignoriert werden sollen
	public static final String PARAMETER_REMOVE_SUFFIX = "remove suffixes";
	
	// Parameter der angibt, ob nur Syntaktische Tags gewertet werden sollen
	public static final String PARAMETER_COUNT_ONLY_SYNTACTIC_TAGS = "count only syntactic tags";
	
	public CompareResults(OperatorDescription description) {
		super(description);
	}
	
	@Override
	public List<ParameterType> getParameterTypes(){
	    List<ParameterType> types = super.getParameterTypes();

	    types.add(new ParameterTypeBoolean(
	        PARAMETER_REMOVE_SUFFIX,
	        "If checked all suffixes are ignored (NP-TMP = NP)",
	        true,
	        false));
	    
	    types.add(new ParameterTypeBoolean(
		        PARAMETER_COUNT_ONLY_SYNTACTIC_TAGS,
		        "If checked only the syntactic tags are counted",
		        false,
		        false));
	    return types;
	}
	
	@Override
	public void doWork() throws UserError {
		
		// Aus Input Ports Documente holen
		Document nameDoc =(Document) nameInput.getData(IOObject.class);
		Document parserDoc =(Document) parserInput.getData(IOObject.class);
		Document goldStandardDoc =(Document) goldStandardInput.getData(IOObject.class);
		
		// Inhalt der Documents in String umwandeln
		String parserName = nameDoc.getTokenText();
		String parserText = parserDoc.getTokenText();
		String goldStandardText = goldStandardDoc.getTokenText();
		
		// Text in Zeilen aufteilen
		String[] parserLines = parserText.split("\\r?\\n");
		String[] goldStandardLines = goldStandardText.split("\\r?\\n");
		
		// Ergebniswerte initialisieren
		double globalPrecision = 0.0;
		double globalRecall = 0.0;
		double globalF1 = 0.0;
		double globalCrossingBrackets = 0.0;
		
		// Zeilenzähler
		int count = 0;
		
		// Minimum aus Zeilenzahl des Parser und des Goldstandards um bei unterschiedlichen Texten
		// die Grenzen des Arrays nicht zu überschreiten
		for(int i = 0; i < Math.min(goldStandardLines.length, parserLines.length); i++) {
			// die aktuelle Zeile formatieren
			parserLines[i] = formatSentence(parserLines[i]);
			goldStandardLines[i] = formatSentence(goldStandardLines[i]);
			
			// die aktuelle Zeile parsen
			List<ParseTreeNode> goldStandardNodes = parseSentence(
					goldStandardLines[i],
					getParameterAsBoolean(PARAMETER_REMOVE_SUFFIX),
					getParameterAsBoolean(PARAMETER_COUNT_ONLY_SYNTACTIC_TAGS));
			
			List<ParseTreeNode> parserNodes = parseSentence(
					parserLines[i], 
					getParameterAsBoolean(PARAMETER_REMOVE_SUFFIX),
					getParameterAsBoolean(PARAMETER_COUNT_ONLY_SYNTACTIC_TAGS));
			
			// Alle Teilbäume des Parserbaumes auf Korrektheit testen
			int numberCorrectNodes = 0;
			int numberCrossingBrackets = 0;
			
			for(int j = 0; j < parserNodes.size(); j++) {
				
				// Alle unbekannten ParseNodes (also auf Empty zugeordnet) werden nicht als richtig gewertet
				if(parserNodes.get(j).typ != PennTag.Empty) {
					
					// korrekt verhindert, dass ein ParseNode 2 mal im Goldstandard vorkommt und 2 mal als richtig gewertet wird
					boolean korrekt = false;
					
					for(int k = 0; k < goldStandardNodes.size(); k++) {	
						
						if(!korrekt) {
							
							if(parserNodes.get(j).equals(goldStandardNodes.get(k))){
								numberCorrectNodes ++;
								korrekt = true;
							}					
						}					
					}				
				}
				
			}
			
			// Die Crossing Bracket Rate berechnen
			for(int j = 0; j < parserNodes.size(); j++) {
				ParseTreeNode pNode = parserNodes.get(j);
				// Für die Rate wird jeder ParserNode nur einmal gewertet, daher der boolean
				boolean isCrossing = false;
				
				for(int k = 0; k < goldStandardNodes.size(); k++) {	
					
					if(!isCrossing) {
						ParseTreeNode gNode = goldStandardNodes.get(k);		
						
						// Test ob Grenzen sich kreuzen
						if((pNode.start < gNode.start && gNode.start < pNode.ende && pNode.ende < gNode.ende) ||
								(gNode.start < pNode.start && pNode.start < gNode.ende && gNode.ende < pNode.ende)) {
							numberCrossingBrackets ++;
							isCrossing = true;
						}						
					}
				}
			}

			// Precision, Recall, F1 und CrossingBrackets der Zeile nach Vorschrift berechnen
			double precision = 0.0;
			double recall = 0.0;
			double f1 = 0.0;
			double crossing = 0.0;
			
			if(parserNodes.size() != 0) {
				precision = (double)numberCorrectNodes/(double)parserNodes.size();
				crossing = (double)numberCrossingBrackets/(double)parserNodes.size();
			}

			if(goldStandardNodes.size() != 0) {
				recall = (double)numberCorrectNodes/(double)goldStandardNodes.size();
			}
			
			if(precision + recall != 0 ) {
				f1 = 2*(precision*recall)/(precision+recall);
			}
			
			// Werte der Zeile zum globalen Wert addieren und Zeilenzähler erhöhen
			if(precision != 0.0 && recall != 0.0) {
				globalPrecision += precision;
				globalRecall += recall;
				globalF1 += f1;
				globalCrossingBrackets += crossing;
			}
			count ++;
			
		}
		
		// Globale Werte durch Zeilenzähler teilen
		globalPrecision /= (double)count;
		globalRecall /= (double)count;
		globalF1 /= (double)count;
		globalCrossingBrackets /= (double)count;
		
		// Tabelle erstellen
		List<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(AttributeFactory.createAttribute("Name", Ontology.STRING));
		attributes.add(AttributeFactory.createAttribute("Precision", Ontology.REAL));
		attributes.add(AttributeFactory.createAttribute("Recall", Ontology.REAL));
		attributes.add(AttributeFactory.createAttribute("F1", Ontology.REAL));
		attributes.add(AttributeFactory.createAttribute("Crossing Brackets", Ontology.REAL));
		
		ExampleSetBuilder examplesetBuilder = ExampleSets.from(attributes);

		
		// Eine Zeile mit den Ergebniswerten befüllen und zur Tabelle hinzufügen
		double[] row = new double[5];
	    
		row[0] = attributes.get(0).getMapping().mapString(parserName);
	    row[1] = globalPrecision;
	    row[2] = globalRecall;
	    row[3] = globalF1;
	    row[4] = globalCrossingBrackets;
	    
	    examplesetBuilder.addRow(row);
		ExampleSet resSet = examplesetBuilder.build();
		
		// Tabelle an Outputport übergeben
		resultOutput.deliver(resSet);
	}
	
	/**
	 * Es werden alle ParseTreeNodes aus Übergabestring s in eine Liste geschrieben.
	 *  @param	s	String der zu parsen ist
	 *  @param removeSuffix	gibt an ob Suffixe ignoriert werden sollen
	 *  @param countOnlySyntacticTags gibt an ob POS ignoriert werden oder nicht
	 *  @return		Liste mit ParseTreeNodes die in s enthalten sind
	 */
	private static List<ParseTreeNode> parseSentence(String s, boolean removeSuffix, boolean countOnlySyntacticTags) {
		
		// Rückgabe Liste res initialisieren
		List<ParseTreeNode> res = new ArrayList<ParseTreeNode>();
		
		// Stack für offene Nodes initialisieren
		Stack<ParseTreeNode> st = new Stack<ParseTreeNode>();
		
		// Übergabetext nach Leerzeichen splitten, man erhält Klammern, Nichtterminale und Terminale
		String[] token = s.split(" ");
		
		// Wörterzähler um Grenzen der Nodes zu setzen
		int wordCount = 0;
		
		// Alle Token der Zeile durchlaufen
		for(int i = 0; i < token.length; i ++) {
			// Fall 1: '('
			if(token[i].equals("(")) {
				// Sonderfall: POS Opening oder Closing Bracket kommt als nächstes
				// dann sind die aktuellen 4 Zeichen: '( ( x )' oder '( ) x )' 
				// wobei x eine eckige runde oder geschweifte Klammer ist. 
				// Sonst kann hier kein Zeichen an x stehen, deswegen wird es nicht abgefragt
				// boolean, da i eventuell hochgezählt wurde und dann dieser Schleifendurchlauf verlassen werden muss
				boolean bracketsFound = false;
				/*if(token[i+1].equals("(") && token[i+3].equals(")")) {
					if(!countOnlySyntacticTags) {
						ParseTreeNode ptn = new ParseTreeNode();
						ptn.start = wordCount;
						wordCount ++; 
						ptn.ende = wordCount;
						ptn.typ = PennTag.OpeningBracket;
						res.add(ptn);
					}
					i += 4;
					bracketsFound = true;
				}
				else if (token[i+1].equals(")") && token[i+3].equals(")")) {
					if(!countOnlySyntacticTags) {
						ParseTreeNode ptn = new ParseTreeNode();
						ptn.start = wordCount;
						wordCount ++; 
						ptn.ende = wordCount;
						ptn.typ = PennTag.ClosingBracket;
						res.add(ptn);
					}
					i += 4;
					bracketsFound = true;
				}*/
				if(!bracketsFound) {
					// Falls Satzbeginn kommt 2 mal '(' hintereinander und es wird ignoriert
					// Andernfall wird neuer Node mit Startpunkt Wortzähler auf den Stack gelegt
					if(!token[i+1].equals("(")) {
						ParseTreeNode ptn = new ParseTreeNode();
						ptn.start = wordCount;
						st.push(ptn);
					}
				}
			}
			// Fall 2: ')'
			else if(token[i].equals(")")) {
				// Falls Stack leer ist Satz zu Ende, letzte ')' wird ignoriert
				// Andernfalls wird oberster Node vom Stack geholt und mit restlichen Informationen gefüllt
				// und in res eingefügt
				if(!st.empty()) {
					ParseTreeNode ptn = st.pop();
					ptn.ende = wordCount;
					// falls nur syntaktische Tags gewertet werden kommen auch nur diese in die Ergebnisliste
					// X ist das letzte der syntaktischen Enums
					if(countOnlySyntacticTags) {
						if(ptn.typ.ordinal() <= PennTag.X.ordinal()) {
							res.add(ptn);
						}
					} else {
						res.add(ptn);
					}
					
				}
			}
			// Fall 3 + 4: Nichtterminal oder Terminal
			else { 
				// Falls Token zuvor '(' kommt jetzt ein Nichtterminal, dieses wird im obersten Node des Stacks gespeichert
				if(token[i-1].equals("(")) {
					ParseTreeNode ptn = st.pop();
					ptn.typ =  PennTag.stringToPennTag(token[i], removeSuffix);
					st.push(ptn);
				}
				// Andernfalls kommt Terminal, dann nur Wortzähler erhöhen
				else {
					wordCount ++;
				}
			}
		}
		return res;
	}
	
	/**
	 * Einem Annotierten Satz aus Parser oder Goldstandard werden um 
	 * die Klammern Leerzeichen eingefügt und mehrfache Leerzeichen
	 * entfernt. Dadurch kann man durch splitten nach Leerzeichen 
	 * alle Elemente des Satzes (Klammern, Nichtterminale, Terminale) erhalten
	 *  @param	s	String der zu formatieren ist
	 *  @return		Formatierter String
	 */
	private static String formatSentence(String s) {
		
		// Übergabestring bekommt Leerzeichen um alle Klammern
		s = s.replaceAll("\\(", " ( ");
		s = s.replaceAll("\\)", " ) ");
		
		// Mehrfache Leerzeichen werden gelöscht
		s = s.replaceAll("\\s+",  " ");
		
		// Führendes Leerzeichen wird gelöscht
		if(s.indexOf(" ") == 0) {
			s = s.substring(1);
		}
		return s;
	}
	
}
