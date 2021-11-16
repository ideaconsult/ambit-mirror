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
}
