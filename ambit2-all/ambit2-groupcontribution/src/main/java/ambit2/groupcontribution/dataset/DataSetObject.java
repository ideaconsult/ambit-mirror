package ambit2.groupcontribution.dataset;

import java.util.HashMap;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.groupcontribution.fragmentation.Fragmentation;

public class DataSetObject 
{
	public IAtomContainer molecule = null;
	//public Map<String, Object> externalProperties = new HashMap<String, Object>();
	public Fragmentation fragmentation = null;
	public String molNotation = null;
	public String error = null;
	
	public DataSetObject() {		
	}
	
	public DataSetObject(IAtomContainer molecule) {
		this.molecule = molecule;
	}
	
	public Double getPropertyDoubleValue(String property)
	{
		//Internal molecule properties take precedence over external ones
		Object o = molecule.getProperty(property);
		if (o != null)
		{	
			if (o instanceof String)
			{
				try {
					double d = Double.parseDouble((String)o);
					return new Double(d);
				}
				catch(Exception e) {
					return null;
				}	
			}
			
			if (o instanceof Double)
				return (Double) o;
			
			if (o instanceof Integer)
				return ((Integer)o).doubleValue();
			
			if (o instanceof Boolean)
				return ((Boolean)o) ? 1.0 : 0.0;
		}
		
		/*
		o = externalProperties.get(endpointProperty);
		if (o != null)
		{	
			if (o instanceof Double)
				return (Double) o;
			
			if (o instanceof Integer)
				return ((Integer)o).doubleValue();
			
			if (o instanceof Boolean)
				return ((Boolean)o) ? 1.0 : 0.0;
		}
		*/	
		return null;
	}
	
	public String getPropertiesAsString()
	{
		StringBuffer sb = new StringBuffer();
		Map<Object, Object> props = molecule.getProperties();
		for (Object key: props.keySet())
		{
			Object val = props.get(key);
			if (val != null)
				sb.append(key.toString() + ":" + val + " ");
		}
				
		return sb.toString();
	}
	
}
