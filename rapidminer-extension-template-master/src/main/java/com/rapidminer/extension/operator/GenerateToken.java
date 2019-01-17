package com.rapidminer.extension.operator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessControlException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.tools.LogService;

import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.ports.PortPairExtender;
import com.rapidminer.operator.ports.metadata.ExampleSetPrecondition;
import com.rapidminer.operator.ports.metadata.MetaData;
import com.rapidminer.operator.ports.metadata.SetRelation;
import com.rapidminer.operator.ports.metadata.SimplePrecondition;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.parameter.conditions.BooleanParameterCondition;
import com.rapidminer.operator.ports.DummyPortPairExtender;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.example.Attribute;
import com.rapidminer.example.Attributes;
import com.rapidminer.example.Example;
import com.rapidminer.tools.Ontology;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;




/**
 * Describe what your operator does.
 *
 * @author Klaus Freiberger
 *
 */
public class GenerateToken extends Operator{
	
	/**
	 * @param PARAMETER_TEXT Eingabe des Users
	 */
	public static final String PARAMETER_TEXT = "Input sentence"; //Name des Parameters
	
	//private InputPort exampleSetInput = getInputPorts().createPort("example set", ExampleSet.class); //Name muss wie example set in Kleinbuchstaben und mit Leerzeichen sein wegen Konvention

	private OutputPort tokenOutput = getOutputPorts().createPort("token"); //Name "example set" muss eindeutig fuer alle Operatoren sein
	//private final PortPairExtender dummyPorts = 
	//		new DummyPortPairExtender("through", getInputPorts(), getOutputPorts());
	/**
	 * Macht irgendwas
	 * 
     * @param description Beschreibung
     */
	public GenerateToken(OperatorDescription description) {
		super(description);
		/*
		exampleSetInput.addPrecondition(
				new ExampleSetPrecondition( exampleSetInput, new String[]{"test"}, 
			            Ontology.ATTRIBUTE_VALUE ));
		
		getTransformer().addPassThroughRule(exampleSetInput, exampleSetOutput);
		getTransformer().addRule(
			    new ExampleSetPassThroughRule( exampleSetInput, exampleSetOutput, SetRelation.EQUAL){
			        @Override
			        public ExampleSetMetaData modifyExampleSet( 
			            ExampleSetMetaData metaData ) throws UndefinedParameterError {
			                return metaData;
			        }
			});
			
		getTransformer().addRule(
		    new ExampleSetPassThroughRule( exampleSetInput, exampleSetOutput, SetRelation.EQUAL){
		        @Override
		        public ExampleSetMetaData modifyExampleSet( 
		            ExampleSetMetaData metaData ) throws UndefinedParameterError {
		                metaData.addAttribute(
		                    new AttributeMetaData("newAttribute", Ontology.REAL));
		                return metaData;
		        }
		});	
		
		AttributeMetaData testAMD = metaData.getAttributeByName("test");
		if(testAMD!=null){
		    testAMD.setType(Ontology.DATE_TIME);
		    testAMD.setName( "date(" + testAMD.getName() + ")" );
		    testAMD.setValueSetRelation(SetRelation.UNKNOWN);
		}
		return metaData;

		
		getTransformer().addGenerationRule(exampleSetOutput, ExampleSet.class);
		
		
		dummyPorts.start();
		getTransformer().addRule(dummyPorts.makePassThroughRule());*/
		
	}
	
	@Override
	public List<ParameterType> getParameterTypes(){
	    List<ParameterType> types = super.getParameterTypes();
	    /*
	    types.add(new ParameterTypeBoolean(
	    		PARAMETER_USE_COSTUM_TEXT,
	    		"If checked, a costum text is printed to the log view",
	    		false,
	    		false));
	    
	    ParameterType type = new ParameterTypeString(
	    		PARAMETER_TEXT,
	    		"This parameter defines which text is logged to the console "
	    		+ "when this operater is executed.",
	    		"This is a default text", 
	    		false);
	    
	    type.registerDependencyCondition(
	    		new BooleanParameterCondition(
	    				this, PARAMETER_USE_COSTUM_TEXT, true, true));
	    
	    types.add(type);
	    */
	    types.add(new ParameterTypeString(
	    		PARAMETER_TEXT,
	    		"This is the input sentence.",
	    		"This is an easy senctence.",
	    		false
	    		));
	    return types;
	}
	
	@Override
	public void doWork() throws OperatorException{
		
		String text = getParameterAsString(PARAMETER_TEXT);
		LogService.getRoot().log(Level.INFO, text);
		
		
		Runtime rt = Runtime.getRuntime();
		try {
			FilePermission permission = new FilePermission("C:\\Users\\Klaus\\Documents\\Uni\\BachelorArbeit\\Parser\\berkeleyparser-master\\BerkeleyParser-1.7.jar", "execute");

			Process pr = rt.exec("java -jar C:\\Users\\Klaus\\Documents\\Uni\\BachelorArbeit\\Parser\\berkeleyparser-master\\BerkeleyParser-1.7.jar -gr C:\\Users\\Klaus\\Documents\\Uni\\BachelorArbeit\\Parser\\berkeleyparser-master\\eng_sm6.gr");
			//Process pr = rt.exec("java -jar C:\\Users\\Klaus\\Documents\\Uni\\BachelorArbeit\\Parser\\berkeleyparser-master\\BerkeleyParser-1.7.jar -gr C:\\Users\\Klaus\\Documents\\Uni\\BachelorArbeit\\Parser\\berkeleyparser-master\\eng_sm6.gr -inputFile C:\\Users\\Klaus\\Documents\\Uni\\BachelorArbeit\\Parser\\berkeleyparser-master\\inputFile.txt -outputFile C:\\Users\\Klaus\\Documents\\Uni\\BachelorArbeit\\Parser\\berkeleyparser-master\\outputFile.txt");
			try {
				pr.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
		} catch (AccessControlException e) {
			LogService.getRoot().log(Level.INFO, e.getMessage());
		}
		
		
		/*InputStream modelIn;
		try {
			
			modelIn = new FileInputStream("C:\\Users\\Klaus\\Documents\\Uni\\BachelorArbeit\\Parser\\apache-opennlp-1.9.0-bin.tar\\apache-opennlp-1.9.0\\en-parser-chunking.bin");
		
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
		}*/
		/*
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		tlp.setGenerateOriginalDependencies(true);
		
		LexicalizedParser lp = LexicalizedParser.loadModel(
				text,
				"-maxLength", "80", "-retainTmpSubcategories");
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		// Uncomment the following line to obtain original Stanford Dependencies
		tlp.setGenerateOriginalDependencies(true);
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		String[] sent = {"This", "is", "an", "easy", "sentence", "."};
		Tree parse = lp.apply(SentenceUtils.toWordList(sent));
		GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
		Collection<TypedDependency> tdl = gs.typedDependencies();
		//System.out.println(parse);
		
		LogService.getRoot().log(Level.INFO, parse.toString());
		*/
		//deliver the example set to the output port
		//exampleSetOutput.deliver(exampleSet);

	}
}
