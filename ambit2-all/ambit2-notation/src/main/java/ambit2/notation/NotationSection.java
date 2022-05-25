package ambit2.notation;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import ambit2.rules.json.JSONParsingUtils;

public class NotationSection 
{
	public String name = null;
	public String info = null;
	
	public boolean flagIsActive = false; //indicates weather it is present in JSON
	public boolean isActive = true; 
	
	public boolean flagOrder = false; //indicates weather it is present in JSON
	public int order = -1;

	public List<NotationElement> elements = new ArrayList<NotationElement>();

	public NotationSection() {
	}

	public NotationSection(String name, String info, int order) {
		this.name = name;
		this.info = info;
		this.order = order;
	}

	public static NotationSection extractNotationSection(JsonNode node, int notSecIndex, List<String> errors)
	{
		NotationSection notSection = new NotationSection();
		JsonNode curNode;
		String errContext = "SECTIONS [" + (notSecIndex +1) + "]";

		// NAME
		curNode = node.path("NAME");
		if (!curNode.isMissingNode()) {
			try {
				String keyword = JSONParsingUtils.extractStringKeyword(node, "NAME", false);
				if (keyword != null)
					notSection.name = keyword;
			}
			catch (Exception x) {
				errors.add(errContext + ": " + x.getMessage());
			}
		}

		// INFO
		curNode = node.path("INFO");
		if (!curNode.isMissingNode()) {
			try {
				String keyword = JSONParsingUtils.extractStringKeyword(node, "INFO", false);
				if (keyword != null)
					notSection.info = keyword;
			}
			catch (Exception x) {
				errors.add(errContext + ": " + x.getMessage());
			}
		}

		// ORDER
		curNode = node.path("ORDER");
		if (!curNode.isMissingNode()) {
			try {
				Integer value = JSONParsingUtils.extractIntKeyword(node, "ORDER", false);
				if (value != null) {
					notSection.order = value;
					notSection.flagOrder = true;
				}	
			}
			catch (Exception x) {
				errors.add(errContext + ": " + x.getMessage());
			}
		}

		// IS_ACTIVE
		curNode = node.path("IS_ACTIVE");
		if (!curNode.isMissingNode()) {
			try {
				Boolean value = JSONParsingUtils.extractBooleanKeyword(node, "IS_ACTIVE", false);
				if (value != null) {
					notSection.isActive = value;
					notSection.flagIsActive = true;
				}
			}
			catch (Exception x) {
				errors.add(errContext + ": " + x.getMessage());
			}
		}
		
		
		// ELEMENTS
		curNode = node.path("ELEMENTS");
		if (!curNode.isMissingNode()) {
			if (!curNode.isArray())
				errors.add("ELEMENTS is not an array!");
			else {
				for (int i = 0; i < curNode.size(); i++)
				{
					NotationElement element = 
							NotationElement.extractNotationElement(curNode.get(i), i, errContext, errors);
					notSection.elements.add(element);
				}
			}
		}

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
		
		if (nFields > 0)
			sb.append(",\n");
		sb.append(offset + "\t\"ELEMENTS\":\n");
		sb.append(offset + "\t[\n");
		for (int i = 0; i < elements.size(); i++) {
			sb.append(elements.get(i).toJSONString(offset + "\t\t"));
			if (i < elements.size() - 1)
				sb.append(",\n");
			sb.append("\n");
		}
		sb.append(offset +"\t]\n");
		
		sb.append(offset + "}\n"); // end of JSON
		return sb.toString();
	}
}
