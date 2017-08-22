package ambit2.groupcontribution.fragmentation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtom;

import ambit2.groupcontribution.GroupContributionModel;
import ambit2.groupcontribution.dataset.DataSet;
import ambit2.groupcontribution.dataset.DataSetObject;
import ambit2.groupcontribution.descriptors.ILocalDescriptor;
import ambit2.groupcontribution.groups.AtomGroup;
import ambit2.groupcontribution.groups.IGroup;

public class Fragmentation 
{
	public Map<String, Integer> groupFrequencies = new HashMap<String, Integer>();
	
	public void addGroup(String groupDesignation)
	{
		Integer freq = groupFrequencies.get(groupDesignation);
		if (freq == null)
			freq = new Integer(1);
		else
			freq = freq + 1;
		groupFrequencies.put(groupDesignation, freq);
	}
	
	public static void makeFragmentation(DataSet dataset, GroupContributionModel gcm)
	{
		gcm.clearGroups();
		
		for (int i = 0; i < dataset.dataObjects.size(); i++)
		{
			DataSetObject dso = dataset.dataObjects.get(i);
			switch (gcm.getModelType())
			{
			case ATOMIC:
				makeAtomicFragmenation(dso, gcm);
				break;
			}
			
			//TODO handle correction factors
		}
		
	}
	
	public static void makeAtomicFragmenation (DataSetObject dso, GroupContributionModel gcm)
	{
		Fragmentation fragmentation = new Fragmentation(); 
		dso.fragmentation = fragmentation;
		List<ILocalDescriptor> locDescr = gcm.getLocalDescriptors();
		
		for (IAtom atom : dso.molecule.atoms())
		{
			int descriptors[] = new int[locDescr.size()];
			for (int i = 0; i < locDescr.size(); i++)
			{	
				int value = locDescr.get(i).calcForAtom(atom, dso.molecule);
				descriptors[i] = value;
			}
			
			IGroup group = new AtomGroup();
			String designation = gcm.getAtomDesignation(descriptors);
			((AtomGroup)group).setAtomDesignation(designation);
			
			fragmentation.addGroup(designation);
			gcm.addGroup(group);
		}
	}
	
	
}
