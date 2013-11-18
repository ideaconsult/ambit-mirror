package ambit2.sln;

public class SLNParserError
{
	//TODO
	public String sourceSLN;
	public String message;
	public int position;
	public String param;
	
	public SLNParserError(String sln, String msg, int pos, String par)
	{
		sourceSLN = sln;
		message = msg;
		position = pos;
		param = par;
	}
	
	public String toString()
	{	
		return(message);
	}
	
	public String getError()
	{	
		if (position < 0)
			return(message + " " + param);
		else
		{	
			if (position > sourceSLN.length())
				position = sourceSLN.length();
			return(message + ": " + sourceSLN.substring(0,position) + "  " + param);
		}	
	}
}
