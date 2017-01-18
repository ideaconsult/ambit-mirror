package ambit2.export.isa;

import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;

import ambit2.export.isa.base.ISAConst.DataFileFormat;
import ambit2.export.isa.base.ISAConst.ISAFormat;
import ambit2.export.isa.base.ISAConst.ISAVersion;
import ambit2.export.isa.base.ISALocation;


public class ISAExportConfig 
{
	protected final static Logger logger = Logger.getLogger(ISAExportConfig.class.getName());
	
	public ISAFormat isaFormat = ISAFormat.JSON;
	public ISAVersion isaVersion = ISAVersion.Ver1_0;
	public DataFileFormat dataFileFormat = DataFileFormat.TEXT_TAB;
	
	
	//Data locations
	public ISALocation protocolParamLoc = null;
	public String protocolParamLocString = "study.0.process[1].element";
	
	public ISALocation effectRecordLoc = null;
	public String effectRecordLocString = "assay.0.process[2].element"; 
	
	//Bundle info  data locations
	public ISALocation bundleDescriptionLoc = null; 
	public String bundleDescriptionLocString = "investigation.description"; 
	public ISALocation bundleTitleLoc = null; 
	public String bundleTitleLocString = "investigation.title"; 
	
	//Content export flags
	public boolean FlagSaveCompositionAsStudy = true;
	public boolean FlagSaveCompositionAsExtensionMaterial = false;
	//public boolean FlagSaveSourceAndSampleOnlyInProcess = true;
	public boolean FlagAllCompositionInOneProcess = true;
	
	public boolean FlagDescriptionAdditiveContent = true;  //If true the content is summed from various possible data sources
	
	
	
	public void parseJSONConfig(JsonNode rootNode) throws Exception
	{	
		JsonNode curNode;
		
		//Handling section EXPORT_INFO
		curNode = rootNode.path("EXPORT_INFO");
		if (curNode.isMissingNode())
			throw new Exception ("Section \"EXPORT_INFO\" is missing!");
		else
		{
			//ISA_FORMAT
			//ISA_VERSION
			//TODO
		}		

		//Handling section ISA_MAPPING
		curNode = rootNode.path("ISA_MAPPING");
		if (curNode.isMissingNode())
		{	
			//If missing default values are used
		}	
		else
		{
			
			//TODO
		}
		
		parseISALocations();
	}
	
	public void parseISALocations() throws Exception
	{
		protocolParamLoc = parseISALocationString(protocolParamLocString, "ISA Location for protocol Param");
		effectRecordLoc = parseISALocationString(effectRecordLocString, "ISA Location for Effect Record");
		
		bundleDescriptionLoc = parseISALocationString(bundleDescriptionLocString, "ISA Location for bundle Description");
		bundleTitleLoc = parseISALocationString(bundleTitleLocString, "ISA Location for bundle Title");
	}
	
	protected ISALocation parseISALocationString(String isaLocStr, String contexInfo) throws Exception
	{
		try
		{
			ISALocation loc = ISALocation.parseString(isaLocStr);
			return loc;
		}
		catch(Exception e)
		{
			throw new Exception("Error in " + contexInfo + "/n" + e.getMessage());
		}
	}
	
	public static void fillDefaultISAConfig(ISAExportConfig cfg)
	{
		try
		{
			cfg.parseISALocations();
		}
		catch(Exception e){
			System.out.println("!!! " + e.toString());
		};
	}
	
}
