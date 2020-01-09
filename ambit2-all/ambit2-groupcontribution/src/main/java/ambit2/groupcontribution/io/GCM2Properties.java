package ambit2.groupcontribution.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import ambit2.groupcontribution.GroupContributionModel;

public class GCM2Properties 
{
	public ArrayList<String> errors = new ArrayList<String>();
	public ArrayList<String> warnings = new ArrayList<String>();
	
	
	public GroupContributionModel loadFromProperties(String fileName) throws Exception 
	{
		errors.clear();
		warnings.clear();
		
		Properties prop =  readPropertiesFile(fileName);
		if (prop == null)
			return null;
		
		GroupContributionModel gcm = new GroupContributionModel();
		GroupContributionModel.GCMConfigInfo addConfigInfo = gcm.getAdditionalConfig();
		String p;
		
		//MODEL_NAME, MODEL_DESCRIPTION
		
		p = prop.getProperty("TRAINING_SET_FILE");
		if (p == null)
			errors.add("Training set file not assigned! Use -t command line option.");
		else
			addConfigInfo.trainingSetFile = p;
		
		p = prop.getProperty("EXTERNAL_SET_FILE");
		if (p != null)
			addConfigInfo.externalSetFile = p;
		
		p = prop.getProperty("MODEL_TYPE");
		if (p != null)
			addConfigInfo.gcmTypeString = p;
		
		p = prop.getProperty("TARGET_PROPERTY");
		if (p == null)
			errors.add("Target property not assigned! Use -p command line option.");
		else
			gcm.setTargetProperty(p);
		
		p = prop.getProperty("LOCAL_DESCRIPTORS");
		if (p == null)
		{
			warnings.add("Local descriptors not assigned!!");
			warnings.add("Using by default: A");
			addConfigInfo.localDescriptorsString = "A";
		}
		else
			addConfigInfo.localDescriptorsString = p;
		
		p = prop.getProperty("GLOBAL_DESCRIPTORS");
		if (p != null)
			addConfigInfo.globalDescriptorsString = p;
				
		p = prop.getProperty("CORRECTION_FACTORS");
		if (p != null)
			addConfigInfo.corFactorsString = p;
				
		p = prop.getProperty("COLUMN_FILTRATION_THRESHOLD");
		if (p == null)
		{
			warnings.add("Using default Column filtration threshold: 0.001");			
			addConfigInfo.columnFiltrationthreshold = 0.001;
		}
		else
		{	
			try {
				double d = Double.parseDouble(p);
				addConfigInfo.columnFiltrationthreshold = d;
			}
			catch (Exception x) {
				errors.add("Incorrect COLUMN_FILTRATION_THRESHOLD: " + x.getMessage());
			}
		}
		
		p = prop.getProperty("FRACTION_DIGITS");
		if (p != null)
		{	
			try {
				int i = Integer.parseInt(p);
				addConfigInfo.fractionDigits = i;
			}
			catch (Exception x) {
				errors.add("Incorrect FRACTION_DIGITS: " + x.getMessage());
			}
		}
		
		p = prop.getProperty("VALIDATION");
		if (p != null)
			addConfigInfo.validationString = p;
		
		//p = prop.getProperty("RESULT_BUFFER_OUT_FILE");
		//if (p != null)
		//if (resultBufferOutFile != null)
		//	gcm.getReportConfig().FlagBufferOutput = true;
		
		//if (FlagFragmenationOnly)
		//	System.out.println("Fragmentation only");
		
		
		return gcm;
	}
	
	public Properties readPropertiesFile(String fileName) throws Exception
	{
		FileInputStream fis = null;
		Properties prop = null;
		try {
			fis = new FileInputStream(fileName);
			prop = new Properties();
			prop.load(fis);
		} catch(FileNotFoundException fnfe) {
			errors.add(fnfe.getMessage());
		} catch(IOException ioe) {
			errors.add(ioe.getMessage());
		} finally {
			fis.close();
		}
		return prop;
	}
	
	public String getAllErrorsAsString()
	{
		StringBuffer sb = new StringBuffer();
		for (String err :errors)
			sb.append(err + "\n");
		return sb.toString();
	}
	
	public String getAllWarningAsString()
	{
		StringBuffer sb = new StringBuffer();
		for (String w : warnings)
			sb.append(w + "\n");
		return sb.toString();
	}
}
