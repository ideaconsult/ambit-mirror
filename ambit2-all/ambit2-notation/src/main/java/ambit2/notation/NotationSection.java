package ambit2.notation;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class NotationSection 
{
	public String name = null;
	public String info = null;
	public boolean flagActive = true; 
	public int order = -1;
	
	public List<NotationElement> sectionElements = new ArrayList<NotationElement>();
	
	public static NotationSection extractNotationSection(JsonNode node, int notSecIndex, List<String> errors)
	{
		NotationSection notSection = new NotationSection();
		
		//TODO
		
		return notSection;
	}
	
	public String toJSONString(String offset) 
	{
		StringBuffer sb = new StringBuffer();
		sb.append(offset + "{\n");
		int nFields = 0;
		
		if (name != null) {
			if (nFields > 0)
				sb.append(",\n");
			sb.append(offset + "\t\"NAME\" : \"" + name + "\"");
			nFields++;
		}
		
		if (info != null) {
			if (nFields > 0)
				sb.append(",\n");
			sb.append(offset + "\t\"INFO\" : \"" + info + "\"");
			nFields++;
		}
		
		
		sb.append(offset + "\n");
		
		sb.append("offset + }\n"); // end of JSON
		return sb.toString();
	}
}
