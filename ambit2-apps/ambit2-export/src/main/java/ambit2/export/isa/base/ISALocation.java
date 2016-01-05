package ambit2.export.isa.base;

/**
 * 
 * @author nick
 * ISA Location is specified as string notation using following syntax
 * layer.[layer position].[process number/id].[element name/number].[sub-element name/number]
 */


public class ISALocation  /* implements IDataLocation */
{
	public static final String splitter = ".";
	
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
	
	public int processNum = -1;
	public String processID = null;
	
	public String elementName = null;
	public int elementNum = -1;
	
	public String subElementName = null;
	public int subElementNum = -1;
	
	
	public static ISALocation parseString(String isaLocString) throws Exception
	{
		if (isaLocString == null)
			return null;
		
		ISALocation isaLoc = new ISALocation();		
		String tokens[] = isaLocString.split(splitter);
		
		if (tokens.length == 0)
			throw new Exception("ISA layer is not specified!");
		
		isaLoc.layer = Layer.fromString(tokens[0]);
		if (isaLoc.layer == Layer.UNDEFINED)
			throw new Exception("ISA layer is not correct!");
		
		
		
		
		return isaLoc;
	}
	
	protected static void parseLayerPos(ISALocation isaLoc, String token) throws Exception
	{
		//TODO
	}
	 
	protected static void parseProcess(ISALocation isaLoc, String token) throws Exception
	{
		//TODO
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
		
		sb.append(layer.toString());
		
		if (layer == Layer.STUDY || layer == Layer.ASSAY)
		{
			if (processNum != -1)
				sb.append("process[" + (processNum+1) + "]");
		}
		
		if (elementName != null)
		{
			
		}
		
		return sb.toString();
	}
	
}
