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
	
	// Input Port Extender erstellt variabel viele Input Ports namens 'input'
	private InputPortExtender inputExt = new InputPortExtender("input", getInputPorts());
	
	// Ausgabe Tabelle mit allen Parser Werten
	private OutputPort resOutput = getOutputPorts().createPort("res");
	
	
	public ShowResults(OperatorDescription description) {
		super(description);
		
		// Startet Port Extender, notwendig
		inputExt.start();
	}
	
	@Override
	public void doWork() throws UserError {
		// Hole alle Ports
		List<InputPort> ports = getInputPorts().getAllPorts();
		
		// Erstelle Ausgabetabelle
		List<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(AttributeFactory.createAttribute("Name",Ontology.STRING));
		attributes.add(AttributeFactory.createAttribute("Precision",Ontology.REAL));
		attributes.add(AttributeFactory.createAttribute("Recall",Ontology.REAL));
		attributes.add(AttributeFactory.createAttribute("F1",Ontology.REAL));
		attributes.add(AttributeFactory.createAttribute("Crossing Brackets", Ontology.REAL));

		
		ExampleSetBuilder examplesetBuilder = ExampleSets.from(attributes);
		
		// Durchlaufe alle Inputports, der letzte ist immer unbesetzt, deshalb Grenze um 1 dekrementiert
		for(int i = 0; i < ports.size() -1; i++) {
			// Hole erste Zeile aus aktuellem Inputport
			ExampleSet parserSet = ports.get(i).getData(ExampleSet.class);
			Example ex = parserSet.getExample(0);
			
			// Schreibe Zeile mit identischen Daten in Ausgabe Tabelle
			double[] row = new double[5];
		    
			// Notwendig, da sonst String als '?' in Ausgabetabelle steht
			row[0] = attributes.get(0).getMapping().mapString(ex.getNominalValue(ex.getAttributes().get("Name")));
		    row[1] = ex.getValue(attributes.get(1));
		    row[2] = ex.getValue(attributes.get(2));
		    row[3] = ex.getValue(attributes.get(3));
		    row[4] = ex.getValue(attributes.get(4));
		    
			examplesetBuilder.addRow(row);
		}
		
		// Gebe Tabelle an Outputport
		resOutput.deliver(examplesetBuilder.build());
		
	}
}
