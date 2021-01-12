package ambit2.sln.dictionary;

import java.util.ArrayList;
import java.util.List;

public class MarkushHelper 
{
	String slnString = null; 
	List<Integer> delimeterPositions = new ArrayList<Integer>();
	List<String> errors = new ArrayList<String>();
	List<String> componentList = new ArrayList<String>();
	String markushAtomName = "";
	
	
	public void setSLNString(String slnString) {
		this.slnString = slnString;
		errors.clear();
		componentList.clear();
		delimeterPositions.clear();
		markushAtomName = "";
		findSimpleMarkushAtomDelimiterPositions();
	}
	
	public boolean isMarkushAtomSLNString() {
		return (delimeterPositions.size() > 0);
	}
	
	public String getSlnString() {
		return slnString;
	}

	public List<Integer> getDelimeterPositions() {
		return delimeterPositions;
	}

	public List<String> getErrors() {
		return errors;
	}
	
	public String getErrorMessages() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < errors.size(); i++) {
			sb.append(errors.get(i) + "\n");
		}
		return (sb.toString());
	}
	
	public List<String> getComponentList() {
		return componentList;
	}
	
	public String getMarkushAtomName() {
		return markushAtomName;
	}

	void findSimpleMarkushAtomDelimiterPositions()
	{	
		//Simple recognition algorithm: searching  symbol '|' outside atom expression:
		//e.g. [...] ... | ... [...]
		
		boolean withinAtomExpression = false;
		int numOpenBrackets = 0;
		int n = slnString.length();
		
		for (int pos = 0; pos < n; pos++)
		{
			if (slnString.charAt(pos) == '|' && !withinAtomExpression)
			{	
				delimeterPositions.add(pos);
				continue;
			}	
			
			if (slnString.charAt(pos) == '[')
			{	
				numOpenBrackets++;
				withinAtomExpression = true; 
				continue;
			}
			
			if (slnString.charAt(pos) == ']')
			{	
				numOpenBrackets--;
				if (numOpenBrackets == 0)
					withinAtomExpression = false;
				else
					errors.add("Incorrect closing bracket ] at position " + (pos+1));
				continue;
			}
		}
	}
	
	
	public void analyzeMarkushString()
	{	
		if (delimeterPositions.isEmpty())
			return;
		
		int n = delimeterPositions.size();
		
		//First component. It must contain the markush atom name as well
		String comp0 = slnString.substring(0, delimeterPositions.get(0));
		int pos = comp0.indexOf(":");
		if (pos == -1) {
			errors.add("Incorrect name specification. Missing ':' delimiter");
			return;
		}	
		markushAtomName = comp0.substring(0,pos);
		componentList.add(comp0.substring(pos+1));
		
		
		//Middle components
		if (n > 1)
			for (int i = 0; i < n-1; i++)
			{	
				String comp = slnString.substring(delimeterPositions.get(i)+1, delimeterPositions.get(i+1));
				componentList.add(comp);
			}
		
		//Last component
		String comp1 = slnString.substring(delimeterPositions.get(n-1) + 1);
		componentList.add(comp1);
	}
}
