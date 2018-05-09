package ambit2.groupcontribution.fragmentation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import ambit2.groupcontribution.GroupContributionModel;
import ambit2.groupcontribution.dataset.DataSet;
import ambit2.groupcontribution.dataset.DataSetObject;
import ambit2.groupcontribution.descriptors.ILocalDescriptor;
import ambit2.groupcontribution.groups.AtomGroup;
import ambit2.groupcontribution.groups.BondGroup;
import ambit2.groupcontribution.groups.IGroup;
import ambit2.groupcontribution.utils.math.MatrixDouble;

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
			case BOND_BASED:
				makeBondBasedFragmenation(dso, gcm);
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
	
	public static void makeBondBasedFragmenation (DataSetObject dso, GroupContributionModel gcm)
	{
		Fragmentation fragmentation = new Fragmentation(); 
		dso.fragmentation = fragmentation;
		List<ILocalDescriptor> locDescr = gcm.getLocalDescriptors();
		Map<IAtom, int[]> atomLocDescr = calcAtomLocalDescriptors(dso.molecule, locDescr);
		
		for (IBond bo : dso.molecule.bonds())
		{
			int d0[] = atomLocDescr.get(bo.getAtom(0));
			int d1[] = atomLocDescr.get(bo.getAtom(1));
			String des0 = gcm.getAtomDesignation(d0);
			String des1 = gcm.getAtomDesignation(d1);
			
			String boType = "-";
			if (bo.getOrder() == IBond.Order.DOUBLE)
				boType = "=";
			else
				if (bo.getOrder() == IBond.Order.TRIPLE)
					boType = "#";
			
			String designation;
			if (des0.compareTo(des1) < 0)
				designation = des0 + boType + des1;
			else
				designation = des1 + boType + des0;
			
			IGroup group = new BondGroup();
			((BondGroup)group).setBondDesignation(designation);
			fragmentation.addGroup(designation);
			gcm.addGroup(group);
		}
		
	}
	
	public static void makeCustomGroupFragmenation(DataSetObject dso, GroupContributionModel gcm)
	{
		Fragmentation fragmentation = new Fragmentation(); 
		dso.fragmentation = fragmentation;
		List<ILocalDescriptor> locDescr = gcm.getLocalDescriptors();
		Map<IAtom, int[]> atomLocDescr = calcAtomLocalDescriptors(dso.molecule, locDescr);
		
		for (IGroup.Type grType : gcm.getCustomGroups())
		{
			switch (grType)
			{
			case ATOM:
				calcAtomFragments(dso, atomLocDescr, gcm);
				break;
			case BOND:
				calcBondFragments(dso, atomLocDescr, gcm);
				break;
			case B_GROUP:
				calcBGroupFragments(dso, atomLocDescr, gcm);
				break;
			case D_GROUP:
				calcDGroupFragments(dso, atomLocDescr, gcm);
				break;
			case G_GROUP:
				calcGGroupFragments(dso, atomLocDescr, gcm);
				break;	
			case L_GROUP:
				calcLGroupFragments(dso, atomLocDescr, gcm);
				break;	
				//TODO
			}
		}
	}
	
	public static void calcAtomFragments(DataSetObject dso, Map<IAtom, int[]> atomLocDescr, GroupContributionModel gcm)
	{
		//TODO
	}
	
	public static void calcBondFragments(DataSetObject dso, Map<IAtom, int[]> atomLocDescr, GroupContributionModel gcm)
	{
		//TODO
	}
	
	public static void calcBGroupFragments(DataSetObject dso, Map<IAtom, int[]> atomLocDescr, GroupContributionModel gcm)
	{
		//TODO
	}
	
	public static void calcDGroupFragments(DataSetObject dso, Map<IAtom, int[]> atomLocDescr, GroupContributionModel gcm)
	{
		//TODO
	}
	
	public static void calcGGroupFragments(DataSetObject dso, Map<IAtom, int[]> atomLocDescr, GroupContributionModel gcm)
	{
		//TODO
	}
	
	public static void calcLGroupFragments(DataSetObject dso, Map<IAtom, int[]> atomLocDescr, GroupContributionModel gcm)
	{
		//TODO
	}
	
	
	public static Map<IAtom, int[]> calcAtomLocalDescriptors(IAtomContainer molecule, 
												List<ILocalDescriptor> locDescr)
	{
		Map<IAtom, int[]> descr = new HashMap<IAtom, int[]>();
		
		for (IAtom atom : molecule.atoms())
		{
			int descriptors[] = new int[locDescr.size()];
			for (int i = 0; i < locDescr.size(); i++)
			{	
				int value = locDescr.get(i).calcForAtom(atom, molecule);
				descriptors[i] = value;
			}
			descr.put(atom, descriptors);
		}
		return descr;
	}
	
	
	
	public static MatrixDouble generateFragmentationMatrix(DataSet dataset, GroupContributionModel gcm)
	{
		int m = dataset.dataObjects.size();
		int n = gcm.getGroups().keySet().size();
		String groups[] = gcm.getGroups().keySet().toArray(new String[n]);
		
		MatrixDouble A = new MatrixDouble(m,n);
		for (int i = 0; i < m; i++)
		{
			DataSetObject dso = dataset.dataObjects.get(i);
			for (int j = 0; j < n; j++)
			{
				Integer iObj = dso.fragmentation.groupFrequencies.get(groups[j]);
				if (iObj == null)
					A.el[i][j] = 0.0;
				else
					A.el[i][j] = iObj.doubleValue();
			}
		}
		
		return A;
	}
	
	public static MatrixDouble generatePropertyMatrix(DataSet dataset, String property) throws Exception
	{
		int m = dataset.dataObjects.size();
		MatrixDouble b = new MatrixDouble(m,1);
		for (int i = 0; i < m; i++)
		{
			DataSetObject dso = dataset.dataObjects.get(i);
			Double d = dso.getPropertyDoubleValue(property);
			if (d == null)
				throw new Exception("Property "+ property + " of object #" + (i+1) +
						" is not double value or not defined!");
			b.el[i][0] = d;
		}
		
		return b;
	}
	
	public static MatrixDouble generateMultiplePropertyMatrix(DataSet dataset, List<String> properties) throws Exception
	{
		int m = dataset.dataObjects.size();
		int n = properties.size();
		MatrixDouble b = new MatrixDouble(m,n);
		for (int i = 0; i < m; i++)
		{
			DataSetObject dso = dataset.dataObjects.get(i);
			for (int k = 0; k < n; k++)
			{	
				Double d = dso.getPropertyDoubleValue(properties.get(k));
				if (d == null)
					throw new Exception("Property "+ properties.get(k) + " of object #" + (i+1) +
							" is not double value or not defined!");
				b.el[i][k] = d;
			}
		}
		
		return b;
	}
	
	
	
	
}
