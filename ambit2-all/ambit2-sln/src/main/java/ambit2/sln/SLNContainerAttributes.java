package ambit2.sln;

import java.util.HashMap;


public class SLNContainerAttributes
{
	public HashMap<String,String> userDeffAttr = new HashMap<String,String>();

	//TODO

	public String toString() 
	{
		StringBuffer sb = new StringBuffer();
		sb.append("<");
		for (int i=0; i < userDeffAttr.size(); i++)
			sb.append(userDeffAttr.get(i));
		sb.append(">");
		return sb.toString();
	}
}
