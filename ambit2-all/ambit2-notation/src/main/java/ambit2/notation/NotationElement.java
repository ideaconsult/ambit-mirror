package ambit2.notation;

import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import ambit2.rules.json.JSONParsingUtils;


public class NotationElement 
{
	public static enum NotationElementType {
		INTEGER, DOUBLE, STRING, UNDEFINED;
		
		public static NotationElementType fromString(String s) {
			try {
				NotationElementType type = NotationElementType.valueOf(s);
				return (type);
			} catch (Exception e) {
				return NotationElementType.UNDEFINED;
			}
		}
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
	
	public static NotationElement extractNotationElement(JsonNode node, int elementIndex, String errContext, List<String> errors)
	{
		NotationElement notElement = new NotationElement();
		JsonNode curNode;
		String elementErrContext = "In " + errContext + ", element [" + (elementIndex+1) + "]";
		
		
		// NAME
		curNode = node.path("NAME");
		if (!curNode.isMissingNode()) {
			try {
				String keyword = JSONParsingUtils.extractStringKeyword(node, "NAME", false);
				if (keyword != null)
					notElement.name = keyword;
			}
			catch (Exception x) {
				errors.add(elementErrContext + ": " + x.getMessage());
			}
		}

		// INFO
		curNode = node.path("INFO");
		if (!curNode.isMissingNode()) {
			try {
				String keyword = JSONParsingUtils.extractStringKeyword(node, "INFO", false);
				if (keyword != null)
					notElement.info = keyword;
			}
			catch (Exception x) {
				errors.add(elementErrContext + ": " + x.getMessage());
			}
		}

		// ORDER
		curNode = node.path("ORDER");
		if (!curNode.isMissingNode()) {
			try {
				Integer value = JSONParsingUtils.extractIntKeyword(node, "ORDER", false);
				if (value != null) {
					notElement.order = value;
					notElement.flagOrder = true;
				}	
			}
			catch (Exception x) {
				errors.add(elementErrContext + ": " + x.getMessage());
			}
		}

		// IS_ACTIVE
		curNode = node.path("IS_ACTIVE");
		if (!curNode.isMissingNode()) {
			try {
				Boolean value = JSONParsingUtils.extractBooleanKeyword(node, "IS_ACTIVE", false);
				if (value != null) {
					notElement.isActive = value;
					notElement.flagIsActive = true;
				}
			}
			catch (Exception x) {
				errors.add(elementErrContext + ": " + x.getMessage());
			}
		}
		
		// TYPE		
		curNode = node.path("TYPE");
		if (!curNode.isMissingNode()) {
			try {
				String keyword = JSONParsingUtils.extractStringKeyword(node, "TYPE", false);
				if (keyword != null) {
					notElement.flagType = true;
					notElement.type =  NotationElementType.fromString(keyword);
					if (notElement.type == NotationElementType.UNDEFINED) {
						errors.add(elementErrContext + " incorrect element type: " + keyword);
					}	
				}	
			}
			catch (Exception x) {
				errors.add(elementErrContext + ": " + x.getMessage());
			}
		}		

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

		if (flagOrder) {
			if (nFields > 0)
				sb.append(",\n");
			sb.append(offset + "\t\"ORDER\" : " + order );
			nFields++;
		}
		
		if (flagIsActive) {
			if (nFields > 0)
				sb.append(",\n");
			sb.append(offset + "\t\"IS_ACTIVE\" : " + isActive);
			nFields++;
		}		

		if (flagType) {
			if (nFields > 0)
				sb.append(",\n");
			sb.append(offset + "\t\"TYPE\" : \"" + type + "\"");
			nFields++;
		}
		
		sb.append("\n");
		
		sb.append(offset + "}\n"); // end of JSON
		return sb.toString();
	}
	
	
}
