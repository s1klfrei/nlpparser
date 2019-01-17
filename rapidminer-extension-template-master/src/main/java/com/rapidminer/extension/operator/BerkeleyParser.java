package com.rapidminer.extension.operator;





import java.io.FilePermission;
import java.io.IOException;
import java.security.AccessControlException;
import java.security.Permissions;
import java.util.List;
import java.util.logging.Level;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.security.PluginSandboxPolicy;
import com.rapidminer.tools.LogService;

public class BerkeleyParser extends Operator{
	
	/**
	 * @param PARAMETER_TEXT Eingabe des Users
	 */
	public static final String PARAMETER_TEXT = "Input sentence"; //Name des Parameters
	
	
	public BerkeleyParser(OperatorDescription description) {
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
	    return types;
	}
	
	@Override
	public void doWork() throws OperatorException{
		/*
		String text = getParameterAsString(PARAMETER_TEXT);
		
		Runtime rt = Runtime.getRuntime();
		try {
			//FilePermission permission = new FilePermission("C:\\Users\\Klaus\\Documents\\Uni\\BachelorArbeit\\Parser\\berkeleyparser-master\\BerkeleyParser-1.7.jar", "execute");

			Process pr = rt.exec("java -jar C:\\Users\\Klaus\\Documents\\Uni\\BachelorArbeit\\Parser\\berkeleyparser-master\\BerkeleyParser-1.7.jar "
					+ "-gr C:\\Users\\Klaus\\Documents\\Uni\\BachelorArbeit\\Parser\\berkeleyparser-master\\eng_sm6.gr "
					+ "-inputFile C:\\Users\\Klaus\\Documents\\Uni\\BachelorArbeit\\Parser\\berkeleyparser-master\\inputFile.txt "
					+ "-outputFile C:\\Users\\Klaus\\Documents\\Uni\\BachelorArbeit\\Parser\\berkeleyparser-master\\outputFile.txt");
			
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
		} */
	}
}