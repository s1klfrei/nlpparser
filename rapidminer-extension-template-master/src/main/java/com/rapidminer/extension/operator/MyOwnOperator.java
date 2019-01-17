package com.rapidminer.extension.operator;

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


/**
 * Describe what your operator does.
 *
 * @author Klaus Freiberger
 *
 */
public class MyOwnOperator extends Operator{
	/**
	 * @param PARAMETER_TEXT Zu loggender Text
	 */
	public static final String PARAMETER_TEXT = "log text"; //Name des Parameters
	/**
	 * @param PARAMETER_USE_COSTUM_TEXT Keine Ahnung
	 */
	public static final String PARAMETER_USE_COSTUM_TEXT = "use costum text";
	
	private InputPort exampleSetInput = getInputPorts().createPort("example set", ExampleSet.class); //Name muss wie example set in Kleinbuchstaben und mit Leerzeichen sein wegen Konvention
	private OutputPort exampleSetOutput = getOutputPorts().createPort("example set"); //Name "example set" muss eindeutig fuer alle Operatoren sein
	private final PortPairExtender dummyPorts = 
			new DummyPortPairExtender("through", getInputPorts(), getOutputPorts());
	/**
     * @param description Beschreibung
     */
	public MyOwnOperator(OperatorDescription description) {
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
		*/
		
		dummyPorts.start();
		getTransformer().addRule(dummyPorts.makePassThroughRule());
		
	}
	
	@Override
	public List<ParameterType> getParameterTypes(){
	    List<ParameterType> types = super.getParameterTypes();
	    
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
	    
	    return types;
	}
	
	@Override
	public void doWork() throws OperatorException{
		if(getParameterAsBoolean(PARAMETER_USE_COSTUM_TEXT)) {
			String text = getParameterAsString(PARAMETER_TEXT);
			LogService.getRoot().log(Level.INFO, text);
		}
		
		//fetch example set from input port
		ExampleSet exampleSet = exampleSetInput.getData(ExampleSet.class);
		
		//get attributes from example set
		Attributes attributes = exampleSet.getAttributes();
		
		//create a new attribute
		String newName = "newAttribute";
		
		//define the name and the type of the new attribute
		//valid types are
		// - nominal 
		// - date_time
		// - numerical
		Attribute targetAttribute = AttributeFactory.createAttribute(newName, Ontology.REAL);
		
		//set the index of the attribute
		targetAttribute.setTableIndex(attributes.size());
		
		//add the attribute
		exampleSet.getExampleTable().addAttribute(targetAttribute);
		attributes.addRegular(targetAttribute);
		//go through the example set and set the values of the new attribute
		for(Example example:exampleSet) {
			example.setValue(targetAttribute, Math.round((Math.random())*10 + 0.5));			
		}
		
		//deliver the example set to the output port
		exampleSetOutput.deliver(exampleSet);
		
		
		//PASS THROUGH PORTS
		dummyPorts.passDataThrough();
	}
}
