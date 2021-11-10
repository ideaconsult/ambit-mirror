package ambit2.notation;

import java.util.ArrayList;
import java.util.List;

public class NotationSection 
{
	public String name = null;
	public String info = null;
	public boolean flagActive = true; 
	public int order = -1;
	
	public List<NotationElement> sectionElements = new ArrayList<NotationElement>();
}
