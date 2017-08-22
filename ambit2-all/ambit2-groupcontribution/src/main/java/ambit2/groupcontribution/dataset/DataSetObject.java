package ambit2.groupcontribution.dataset;

import java.util.HashMap;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtomContainer;

public class DataSetObject 
{
	public IAtomContainer molecule = null;
	public Map<String, Object> properties = new HashMap<String, Object>();
	public String endpointProperty = "endpoint";
	
	
	public Double getEndpintValue()
	{
		Object o = properties.get(endpointProperty);
		if (o != null)
		{	
			if (o instanceof Double)
				return (Double) o;
			
			if (o instanceof Integer)
				return ((Integer)o).doubleValue();
			
			if (o instanceof Boolean)
				return ((Boolean)o) ? 1.0 : 0.0;
		}	
		
		return null;
	}
	
}
