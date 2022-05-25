package ambit2.notation;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class NotationElement 
{
	public static enum NotationElementType {
		INTEGER, DOUBLE, STRING
	}
	
	public String name = null;
	public String info = null;
	
	public boolean flagIsActive = false; //indicates weather it is present in JSON
	public boolean isActive = true; 
	
	public boolean flagOrder = false; //indicates weather it is present in JSON
	public int order = -1;
	
	public boolean flagType = false; //indicates weather it is present in JSON
	public NotationElementType type = NotationElementType.STRING; 
	
	public NotationElement() {		
	}
	
	public NotationElement(String name, String info, int order) {
		this.name = name;
		this.info = info;
		this.order = order;
	}
	
	public NotationElement(String name, String info, int order, NotationElementType type) {
		this.name = name;
		this.info = info;
		this.order = order;
		this.type = type; 
	}
	
	public static NotationElement extractNotationElement(JsonNode node, int notSecIndex, List<String> errors)
	{
		NotationElement notElement = new NotationElement();
		
		//TODO
		
		return notElement;
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
