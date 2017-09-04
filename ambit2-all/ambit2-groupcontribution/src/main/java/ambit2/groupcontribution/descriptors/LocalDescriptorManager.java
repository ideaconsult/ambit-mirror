package ambit2.groupcontribution.descriptors;

import java.util.ArrayList;
import java.util.List;

public class LocalDescriptorManager 
{
	private static List<ILocalDescriptor> predefinedLocalDescriptors = null;
	
	public static List<ILocalDescriptor> getPredefinedLocalDescriptors()
	{
		if (predefinedLocalDescriptors == null)
		{
			predefinedLocalDescriptors = new ArrayList<ILocalDescriptor>();
			predefinedLocalDescriptors.add(new LDAtomFormalCharge());
			predefinedLocalDescriptors.add(new LDAtomHeavyNeighbours());
			predefinedLocalDescriptors.add(new LDAtomHybridization());
			predefinedLocalDescriptors.add(new LDAtomSymbol());
			predefinedLocalDescriptors.add(new LDAtomValency());
			predefinedLocalDescriptors.add(new LDHNum());
		}
		
		return predefinedLocalDescriptors;
	}
}
