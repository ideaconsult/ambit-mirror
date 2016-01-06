package ambit2.export.isa.base;

/**
 * 
 * @author nick
 * ISA Location is specified as string notation using following syntax
 * layer.[layer position].[layer position2].[process index/id].[element name/index].[sub-element name/index]
 * 
 * [layer position2] is used for the definition of ISA location within assay
 * 
 * 
 * Examples:
 * 		investigation.description
 * 		study.1.process[2]   
 * 		assay.0.0.process[2]
 */


public class ISALocation  /* implements IDataLocation */
{
	public static final String splitter = "\\.";
	
	public static enum Layer 
	{
		INVESTIGATION, STUDY, ASSAY, MATERIAL, DATA_FILE, 
		UNDEFINED;
		
		public static Layer fromString(String s)
		{	 
			try
			{
				Layer layer = Layer.valueOf(s) ;
				return (layer);
			}
			catch (Exception e)
			{
				return UNDEFINED;
			}
		}
	}
	
	//The indices/numbers/ are 1-based
	//value -1 is used for undefined/unused
	//value 0 is used to specified for default index/number/
	
	public Layer layer = null;
	
	public int layerPosIndex = -1;    
	public String layerPosID = null;
	
	public int layerPos2Index = -1;    
	public String layerPos2ID = null;
	
	public int processIndex = -1;
	public String processID = null;
	
	public String elementName = null;
	public int elementIndex = -1;
	
	public String subElementName = null;
	public int subElementIndex = -1;
	
	
	public static ISALocation parseString(String isaLocString) throws Exception
	{
		if (isaLocString == null)
			return null;
		
		
		ISALocation isaLoc = new ISALocation();		
		String tokens[] = isaLocString.split(splitter);
		
		if (tokens.length == 0)
			throw new Exception("ISA layer is not specified!");
		
		isaLoc.layer = Layer.fromString(tokens[0].toUpperCase());
		if (isaLoc.layer == Layer.UNDEFINED)
			throw new Exception("ISA layer is not correct!");
		
		
		switch (isaLoc.layer)
		{
		case STUDY:
			if (tokens.length > 1 )
				parseLayerPos(isaLoc, tokens[1]);
			else
				throw new Exception ("ISA study postion is missing");
			
			if (tokens.length > 2)
				parseProcess(isaLoc, tokens[2]);
			else
				throw new Exception ("ISA process is missing");
			
			break;
		case ASSAY:
			if (tokens.length > 1 )
				parseLayerPos(isaLoc, tokens[1]);
			else
				throw new Exception ("ISA study postion is missing");
			
			if (tokens.length > 2 )
				parseLayerPos2(isaLoc, tokens[2]);
			else
				throw new Exception ("ISA assay postion is missing");
			
			
			if (tokens.length > 3)
				parseProcess(isaLoc, tokens[3]);
			else
				throw new Exception ("ISA process is missing");
			
			break;
		}
		
		return isaLoc;
	}
	
	protected static void parseLayerPos(ISALocation isaLoc, String token) throws Exception
	{
		boolean FlagOK = true;
		try
		{
			isaLoc.layerPosIndex = Integer.parseInt(token);
			if (isaLoc.layerPosIndex < 0)
				FlagOK = false;
		}
		catch(Exception e)
		{
			//Incorrect integer then the token is interpreted as position id string
			isaLoc.layerPosID = token;
		}
		
		if (!FlagOK)
			throw new Exception ("ISA study postion is incorrect: " + isaLoc.layerPosIndex);
		
	}
	
	protected static void parseLayerPos2(ISALocation isaLoc, String token) throws Exception
	{
		boolean FlagOK = true;
		try
		{	
			isaLoc.layerPos2Index = Integer.parseInt(token);
			if (isaLoc.layerPos2Index < 0)
				FlagOK = false;
		}
		catch(Exception e)
		{
			//Incorrect integer then the token is interpreted as position id string
			isaLoc.layerPos2ID = token;
		}
		
		if (!FlagOK)
			throw new Exception ("ISA assay postion is incorrect: " + isaLoc.layerPos2Index);
	}
	 
	protected static void parseProcess(ISALocation isaLoc, String token) throws Exception
	{
		if (!token.startsWith("process["))
			throw new Exception ("ISA process is not specified correctly: " + token);
		
		if (!token.endsWith("]"))
			throw new Exception ("ISA process is not specified correctly: " + token);
		
		String proc = token.substring(8, token.length()-1);
		
		boolean FlagOK = true;
		
		try
		{
			isaLoc.processIndex = Integer.parseInt(proc);
			if (isaLoc.processIndex < 0)
				FlagOK = false;
		}
		catch(Exception e)
		{
			//Incorrect integer then the token is interpreted as position id string
			isaLoc.processID = proc;
		}
		
		if (!FlagOK)
			throw new Exception ("ISA process index is incorrect: " + isaLoc.processIndex);
		
	}
	
	protected static void parseElement(ISALocation isaLoc, String token) throws Exception
	{
		//TODO
	}
	
	
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		if (layer == null)
			return ("null");
		
		sb.append(layer.toString().toLowerCase());
		
		if (layer == Layer.STUDY || layer == Layer.ASSAY)
		{
			if (layerPosIndex != -1)
			{
				sb.append("." + layerPosIndex);
			}
			else
			{
				if (layerPosID != null)
					sb.append("." + layerPosID);
			}
			
			if (layer == Layer.ASSAY)
			{
				if (layerPos2Index != -1)
				{
					sb.append("." + layerPos2Index);
				}
				else
				{
					if (layerPos2ID != null)
						sb.append("." + layerPos2ID);
				}
			}
			
			if (processIndex != -1)
			{	
				sb.append(".process[" + processIndex + "]");
			}
			else
			{
				if (processID != null)
					sb.append(".process[" + processID + "]");
			}
		}
		
		if (elementName != null)
		{
			sb.append("." + elementName);
			
			if (subElementName != null)
				sb.append("." + subElementName);
		}
		
		return sb.toString();
	}
	
}
