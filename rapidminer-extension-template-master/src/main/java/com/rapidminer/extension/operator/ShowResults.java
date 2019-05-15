package com.rapidminer.extension.operator;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.example.utils.ExampleSetBuilder;
import com.rapidminer.example.utils.ExampleSets;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.InputPortExtender;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.ports.Ports;
import com.rapidminer.operator.text.Document;
import com.rapidminer.tools.Ontology;

public class ShowResults extends Operator{

	private InputPortExtender inputExt = new InputPortExtender("input", getInputPorts());
	
	private OutputPort resOutput = getOutputPorts().createPort("res");
	
	
	public ShowResults(OperatorDescription description) {
		super(description);
		
		inputExt.start();
	}
	
	@Override
	public void doWork() throws UserError {
		
		List<InputPort> ports = getInputPorts().getAllPorts();
		
		List<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(AttributeFactory.createAttribute("Name",Ontology.STRING));
		attributes.add(AttributeFactory.createAttribute("Precision",Ontology.REAL));
		attributes.add(AttributeFactory.createAttribute("Recall",Ontology.REAL));
		attributes.add(AttributeFactory.createAttribute("F1",Ontology.REAL));
		
		ExampleSetBuilder examplesetBuilder = ExampleSets.from(attributes);
		
		for(int i = 0; i < ports.size() -1; i++) {
			ExampleSet parserSet = ports.get(i).getData(ExampleSet.class);
			Example ex = parserSet.getExample(0);
			
			double[] row = new double[4];
		    
			row[0] = attributes.get(0).getMapping().mapString(ex.getNominalValue(ex.getAttributes().get("Name")));
		    row[1] = ex.getValue(attributes.get(1));
		    row[2] = ex.getValue(attributes.get(2));
		    row[3] = ex.getValue(attributes.get(3));
			
			examplesetBuilder.addRow(row);
		}
		
		resOutput.deliver(examplesetBuilder.build());
		
	}
}
