package ambit2.export.isa.base;


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
	
	public Layer layer = null;
	public int sublayerNum = 0;
	public String sublayerID = null;
	
	
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
	  
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		//TODO
		return sb.toString();
	}
	
}
