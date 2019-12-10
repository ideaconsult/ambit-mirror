package ambit2.groupcontribution.transformations;

import java.util.ArrayList;
import java.util.List;

public class TransformationUtils 
{
	public List<String> errors = new ArrayList<String>();
		
	public IValueTransformation parseTransformation(String s)
	{
		errors.clear();
		String transformationName = null;
		String paramsString = null;
		
		int ind = s.indexOf("(");
		
		if (ind == -1)
		{
			transformationName = s;
		}
		else
		{	
			if (ind == 0)
			{
				errors.add("Incorrect transformation: string starts with '('");
				return null;
			}
			else
			{
				if (!s.endsWith(")"))
				{
					errors.add("Incorrect transformation: missing closing bracket ')'");
					return null;
				}
				
				transformationName = s.substring(0, ind);
				paramsString = s.substring(ind+1, s.length()-1);
			}
		}
		
		return null;
	}
}
