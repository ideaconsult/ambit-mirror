package ambit2.groupcontribution.transformations;

import java.util.ArrayList;
import java.util.List;

public class TransformationUtils 
{
	public String paramSplitter = ",";
	public List<String> errors = new ArrayList<String>();
	
	public IValueTransformation parseTransformation(String s)
	{
		errors.clear();
		String tokens[] = s.split(TransformationComposition.composeSeparator);
		if (tokens.length == 0)
			return null;
		
		if (tokens.length == 1)
			return parseTransformation0(s);
		
		IValueTransformation transArray[] = new IValueTransformation[tokens.length];
		boolean FlagError = false;
		for (int i = 0; i < tokens.length; i++)
		{
			IValueTransformation trans = parseTransformation0(tokens[i].trim());
			transArray[i] = trans;
			if (trans == null)
				FlagError = true;
		}
		
		if (FlagError)		
			return null;
		else
		{
			TransformationComposition transComp = new TransformationComposition(transArray);
			return transComp;
		}
	}
	
	IValueTransformation parseTransformation0(String s)
	{	
		String transformationName = null;
		String paramsString = null;
		List<Object> paramObjList = null;
		
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
				paramsString = s.substring(ind+1, s.length()-1).trim();
			}
		}
		
		//System.out.println("transformationName: " + transformationName);
		//System.out.println("paramsString: " + paramsString);
		
		if (paramsString != null)
			if (!paramsString.equals(""))
				paramObjList = extractParams(paramsString);
		
		if (transformationName.equalsIgnoreCase("LN"))
		{
			TransformationLn trans = new TransformationLn();
			if (paramObjList != null)
			{
				if (paramObjList.size() > 1)
				{
					errors.add("Too many parameters in tranformation LN: " + paramsString );
					return null;
				}
				
				if (paramObjList.size() == 1)
				{
					Object o = paramObjList.get(0);
					if (o instanceof String)
					{
						errors.add("Incorrect parameter in tranformation LN: " + paramsString );
						return null;
					}
					trans.setShift((Double)o);
				}
				
			}		
			return trans;
		}
		
		if (transformationName.equalsIgnoreCase("EXP"))
		{
			TransformationExp trans = new TransformationExp();
			return trans;
		}
		
		if (transformationName.equalsIgnoreCase("POW"))
		{
			if ((paramObjList == null) || paramObjList.isEmpty() )
			{
				errors.add("Missing parameter for tranformation POW");
				return null;
			}
			if (paramObjList.size() > 1)
			{
				errors.add("Too many parameters in tranformation POW: " + paramsString );
				return null;
			}
			
			Object o = paramObjList.get(0);
			if (o instanceof String)
			{
				errors.add("Incorrect parameter in tranformation POW: " + paramsString );
				return null;
			}	
			
			TransformationPow trans = new TransformationPow((Double)o);
			return trans;
		}
		
		if (transformationName.equalsIgnoreCase("LIN"))
		{
			if ((paramObjList == null) || paramObjList.isEmpty() )
			{
				errors.add("Missing parameters for tranformation LIN");
				return null;
			}
			if (paramObjList.size() == 1)
			{
				errors.add("Mising parameter in tranformation LIN: " + paramsString );
				return null;
			}
			
			if (paramObjList.size() > 2)
			{
				errors.add("Too many parameters in tranformation LIN: " + paramsString );
				return null;
			}
			
			Object o = paramObjList.get(0);
			if (o instanceof String)
			{
				errors.add("Incorrect parameter in tranformation LIN: " + paramsString );
				return null;
			}
			
			Object o1 = paramObjList.get(1);
			if (o1 instanceof String)
			{
				errors.add("Incorrect parameter in tranformation LIN: " + paramsString );
				return null;
			}
			
			TransformationLin trans = new TransformationLin((Double)o, (Double)o1);
			return trans;
		}
		
		
		errors.add("Unknown tranformation: " + transformationName);
		return null;
	}
	
	
	
	
	List<Object> extractParams(String paramsString)
	{
		List<Object> list = new ArrayList<Object>();
		String tokens[] = paramsString.split(paramSplitter);
		for (int i = 0; i < tokens.length; i++)
		{
			String tok = tokens[i].trim();
			if (tok.equals(""))
			{	
				errors.add("Incorrect empty parameter");
				return null;
			}
			
			try {
				Double d = Double.parseDouble(tok);
				list.add(d);
			}
			catch (Exception x) {
				list.add(tok);
			}
		}
		return list;
	}
}
