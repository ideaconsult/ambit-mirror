package ambit2.groupcontribution.fragmentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import ambit2.groupcontribution.GroupContributionModel;
import ambit2.groupcontribution.correctionfactors.ICorrectionFactor;
import ambit2.groupcontribution.dataset.DataSet;
import ambit2.groupcontribution.dataset.DataSetObject;
import ambit2.groupcontribution.descriptors.ILocalDescriptor;
import ambit2.groupcontribution.descriptors.LDAtomSymbol;
import ambit2.groupcontribution.groups.AtomGroup;
import ambit2.groupcontribution.groups.BondGroup;
import ambit2.groupcontribution.groups.DGroup;
import ambit2.groupcontribution.groups.GGroup;
import ambit2.groupcontribution.groups.IGroup;
import ambit2.groupcontribution.utils.math.MatrixDouble;

public class Fragmentation 
{
	public Map<String, Integer> groupFrequencies = new HashMap<String, Integer>();
	public List<Double> correctionFactors = new ArrayList<Double>();
	
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
		if (gcm.isAllowGroupRegistration())
			gcm.clearGroups();
		
		for (int i = 0; i < dataset.dataObjects.size(); i++)
		{
			DataSetObject dso = dataset.dataObjects.get(i);
			try	{
				makeFragmentation(dso, gcm);
			}
			catch (Exception e) {
				dso.error = "Error on making fragmentation: " + e.getMessage();
				e.printStackTrace();
				dataset.nErrors++;
			}
		}
	}
	
	public static void makeFragmentation(DataSetObject dso, GroupContributionModel gcm)
	{
		switch (gcm.getModelType())
		{
		case ATOMIC:
			makeAtomicFragmenation(dso, gcm);
			break;
		case BOND_BASED:
			makeBondBasedFragmenation(dso, gcm);
			break;
		case SECOND_ORDER:
			makeSecondOrderFragmenation(dso, gcm);
			break;				
		case CORRECTION_FACTORS_ONLY:
			Fragmentation fragmentation = new Fragmentation(); 
			dso.fragmentation = fragmentation;
			break;
		}

		//Handle correction factors
		if (!gcm.getCorrectionFactors().isEmpty())
			calcCorrectionFactors(dso,gcm);
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
			if (gcm.isAllowGroupRegistration())
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
			if (gcm.isAllowGroupRegistration())
				gcm.addGroup(group);
		}
		
	}
	
	public static void makeSecondOrderFragmenation(DataSetObject dso, GroupContributionModel gcm)
	{
		Fragmentation fragmentation = new Fragmentation(); 
		dso.fragmentation = fragmentation;
		
		List<ILocalDescriptor> locDescr = gcm.getLocalDescriptors();
		Map<IAtom, int[]> atomLocDescr = calcAtomLocalDescriptors(dso.molecule, locDescr);		
		calcBondFragments(fragmentation, dso, atomLocDescr, gcm);
		
		List<ILocalDescriptor> locDescr2 = getDefaultSecondOrderLocalDescriptors();
		Map<IAtom, int[]> atomLocDescr2 = calcAtomLocalDescriptors(dso.molecule, locDescr2);		
		calcGGroupFragments(fragmentation, dso, atomLocDescr2, gcm);		
		calcDGroupFragments(fragmentation, dso, atomLocDescr2, gcm);
	}
	
	public static void makeCustomGroupFragmenation(DataSetObject dso, GroupContributionModel gcm)
	{
		Fragmentation fragmentation = new Fragmentation(); 
		dso.fragmentation = fragmentation;
					
		List<ILocalDescriptor> gcmlocDescr = gcm.getLocalDescriptors();
		Map<IAtom, int[]> gcmAtomLocDescr = null;
				
		for (int i = 0; i < gcm.getCustomGroups().size(); i++)
		{
			IGroup.Type grType = gcm.getCustomGroups().get(i);
			List<ILocalDescriptor> locDescr = gcm.getCustomGroupLocalDescriptors().get(i); 
			Map<IAtom, int[]> atomLocDescr = null;
			
			if (locDescr == null)
			{
				if (gcmAtomLocDescr == null)
					gcmAtomLocDescr = calcAtomLocalDescriptors(dso.molecule, gcmlocDescr);
				
				//By default local descriptors from the GCM model are used
				atomLocDescr = gcmAtomLocDescr;
			}
			else
				atomLocDescr = calcAtomLocalDescriptors(dso.molecule, locDescr);
			
			switch (grType)
			{
			case ATOM:
				calcAtomFragments(fragmentation, dso, atomLocDescr, gcm);
				break;
			case BOND:
				calcBondFragments(fragmentation, dso, atomLocDescr, gcm);
				break;				
			case D_GROUP:
				calcDGroupFragments(fragmentation, dso, atomLocDescr, gcm);
				break;
			case G_GROUP:
				calcGGroupFragments(fragmentation, dso, atomLocDescr, gcm);
				break;	
			case L_GROUP:
				calcLGroupFragments(fragmentation, dso, atomLocDescr, gcm);
				break;
			}
		}
	}
	
	public static void calcCorrectionFactors (DataSetObject dso, GroupContributionModel gcm)
	{
		for (int i = 0; i < gcm.getCorrectionFactors().size(); i++)
		{
			ICorrectionFactor cf = gcm.getCorrectionFactors().get(i);
			double cfVal = cf.calculateFor(dso.molecule);
			dso.fragmentation.correctionFactors.add(cfVal);
		}
	}
	
	public static void calcAtomFragments(Fragmentation fragmentation, 
			DataSetObject dso, 
			Map<IAtom, int[]> atomLocDescr, 
			GroupContributionModel gcm)
	{
		List<ILocalDescriptor> locDescr = gcm.getLocalDescriptors();

		for (IAtom atom : dso.molecule.atoms())
		{
			int descriptors[] = atomLocDescr.get(atom);
			
			IGroup group = new AtomGroup();
			String designation = gcm.getAtomDesignation(descriptors);
			((AtomGroup)group).setAtomDesignation(designation);

			fragmentation.addGroup(designation);
			if (gcm.isAllowGroupRegistration())
				gcm.addGroup(group);
		}
	}
	
	public static void calcBondFragments(Fragmentation fragmentation, 
			DataSetObject dso, 
			Map<IAtom, int[]> atomLocDescr, 
			GroupContributionModel gcm)
	{
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
			if (gcm.isAllowGroupRegistration())
				gcm.addGroup(group);
		}
	}
		
	public static void calcDGroupFragments(Fragmentation fragmentation, 
			DataSetObject dso, 
			Map<IAtom, int[]> atomLocDescr, 
			GroupContributionModel gcm)
	{
		//Calculate atom designations
		Map<IAtom, String> atomDes = new HashMap<IAtom, String>();
		for (IAtom at : dso.molecule.atoms())
		{
			int d[] = atomLocDescr.get(at);
			String des = GroupContributionModel.
					makeAtomDesignation(d, getDefaultSecondOrderLocalDescriptors());			
			atomDes.put(at, des);
		}
		
		for (IAtom at : dso.molecule.atoms())
		{
			List<IAtom> neighAt = dso.molecule.getConnectedAtomsList(at);
			int n = neighAt.size(); 
			if (n < 3)
				continue;
									
			for (int i = 0; i < n-2; i++)			
				for (int k = i+1; k < n-1; k++)
					for (int r = k+1; r < n; r++)
					{
						String des[] = new String[3];
						des[0] = atomDes.get(neighAt.get(i));
						des[1] = atomDes.get(neighAt.get(k));
						des[2] = atomDes.get(neighAt.get(r));
						
						String boType[] = new String[3];

						boType[0] = "-";
						IBond bo0 = dso.molecule.getBond(at,neighAt.get(i));
						if (bo0.getOrder() == IBond.Order.DOUBLE)
							boType[0] = "=";
						else
							if (bo0.getOrder() == IBond.Order.TRIPLE)
								boType[0] = "#";

						boType[1] = "-";
						IBond bo1 = dso.molecule.getBond(at,neighAt.get(k));
						if (bo1.getOrder() == IBond.Order.DOUBLE)
							boType[1] = "=";
						else
							if (bo1.getOrder() == IBond.Order.TRIPLE)
								boType[1] = "#";
						
						boType[2] = "-";
						IBond bo2 = dso.molecule.getBond(at,neighAt.get(r));
						if (bo2.getOrder() == IBond.Order.DOUBLE)
							boType[2] = "=";
						else
							if (bo1.getOrder() == IBond.Order.TRIPLE)
								boType[2] = "#";

						String designation;
						
						//Sort neighbor atoms designations
						int si[] = {0,1,2};
						//Put max at the end
						if (des[si[0]].compareTo(des[si[1]]) > 0)
						{	
							int tmp = si[0];
							si[0] = si[1];
							si[1] = tmp;						
						}	
						if (des[si[1]].compareTo(des[si[2]]) > 0)
						{	
							int tmp = si[1];
							si[1] = si[2];
							si[2] = tmp;						
						}
						//Check first two elements again
						if (des[si[0]].compareTo(des[si[1]]) > 0)
						{	
							int tmp = si[0];
							si[0] = si[1];
							si[1] = tmp;						
						}
							
						
						designation = atomDes.get(at) + "(" + boType[si[0]] + des[si[0]] + ")"
								+ "(" + boType[si[1]] + des[si[1]] + ")" + boType[si[2]] + des[si[2]];
						
						//add D group
						IGroup group = new DGroup();
						((DGroup)group).setGroupDesignation(designation);

						fragmentation.addGroup(designation);
						if (gcm.isAllowGroupRegistration())
							gcm.addGroup(group);
					}
		}
		
				
	}
	
	public static void calcGGroupFragments(Fragmentation fragmentation, 
			DataSetObject dso, 
			Map<IAtom, int[]> atomLocDescr, 
			GroupContributionModel gcm)
	{
		//Calculate atom designations
		Map<IAtom, String> atomDes = new HashMap<IAtom, String>();
		for (IAtom at : dso.molecule.atoms())
		{
			int d[] = atomLocDescr.get(at);
			String des = GroupContributionModel.
					makeAtomDesignation(d, getDefaultSecondOrderLocalDescriptors());			
			atomDes.put(at, des);
		}
		
		for (IAtom at : dso.molecule.atoms())
		{
			List<IAtom> neighAt = dso.molecule.getConnectedAtomsList(at);
			int n = neighAt.size(); 
			if (n < 2)
				continue;
									
			for (int i = 0; i < n-1; i++)
			{
				for (int k = i+1; k < n; k++)
				{
					String des0 = atomDes.get(neighAt.get(i));
					String des1 = atomDes.get(neighAt.get(k));
					
					String boType0 = "-";
					IBond bo0 = dso.molecule.getBond(at,neighAt.get(i));
					if (bo0.getOrder() == IBond.Order.DOUBLE)
						boType0 = "=";
					else
						if (bo0.getOrder() == IBond.Order.TRIPLE)
							boType0 = "#";
					
					String boType1 = "-";
					IBond bo1 = dso.molecule.getBond(at,neighAt.get(k));
					if (bo1.getOrder() == IBond.Order.DOUBLE)
						boType1 = "=";
					else
						if (bo1.getOrder() == IBond.Order.TRIPLE)
							boType1 = "#";
										
					String designation;
					if (des0.compareTo(des1) < 0)
						designation = atomDes.get(at) + "(" + boType0 + des0 + ")" + boType1 + des1;
					else
						designation = atomDes.get(at) + "(" + boType1 + des1 + ")" + boType0 + des0;
					//add G group
					IGroup group = new GGroup();
					((GGroup)group).setGroupDesignation(designation);

					fragmentation.addGroup(designation);
					if (gcm.isAllowGroupRegistration())
						gcm.addGroup(group);
				}
			}
		}
		
	}
	
	public static void calcLGroupFragments(Fragmentation fragmentation, 
			DataSetObject dso, 
			Map<IAtom, int[]> atomLocDescr, 
			GroupContributionModel gcm)
	{
		//TODO
	}
	
	public static void calculateLocalDescriptors(DataSetObject dso, GroupContributionModel gcm)
	{	
		dso.localDescriptors = new HashMap<Object, double[]>();
		List<ILocalDescriptor> locDescr = gcm.getLocalDescriptors();
		
		switch (gcm.getModelType())
		{
		case ATOM_LOCAL_PROPERTY:
			for (IAtom atom : dso.molecule.atoms())
			{
				IAtom ats[] = new IAtom[] {atom};
				double descriptors[] = new double[locDescr.size()];
				for (int i = 0; i < locDescr.size(); i++)
				{	
					Double value = locDescr.get(i).calcForAtoms(ats, dso.molecule);
					if (value != null)
						descriptors[i] = value;
				}
				dso.localDescriptors.put(atom, descriptors);
			}			
			break;

		case BOND_LOCAL_PROPERTY:
			for (IBond bond : dso.molecule.bonds())
			{
				IAtom ats[] = new IAtom[] {bond.getAtom(0), bond.getAtom(1)};
				double descriptors[] = new double[locDescr.size()];
				for (int i = 0; i < locDescr.size(); i++)
				{	
					Double value = locDescr.get(i).calcForAtoms(ats, dso.molecule);
					if (value != null)
						descriptors[i] = value;
				}
				dso.localDescriptors.put(bond, descriptors);
			}			
			break;
		
		case ATOM_PAIR_LOCAL_PROPERTY:
			int n = dso.molecule.getAtomCount();
			for (int k = 0; k < n; k++)
				for (int j = k+1; j < n; j++)
				{
					IAtom ats[] = new IAtom[] 
							{dso.molecule.getAtom(k), dso.molecule.getAtom(j)};
					
					double descriptors[] = new double[locDescr.size()];
					for (int i = 0; i < locDescr.size(); i++)
					{	
						Double value = locDescr.get(i).calcForAtoms(ats, dso.molecule);
						if (value != null)
							descriptors[i] = value;
					}
					dso.localDescriptors.put(ats, descriptors);
				}			
			break;
	
	
		default:
			break;
		}
		
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
	
	public static List<ILocalDescriptor> getDefaultSecondOrderLocalDescriptors()
	{
		List<ILocalDescriptor> locDescr = new ArrayList<ILocalDescriptor>();
		ILocalDescriptor ld = new LDAtomSymbol();
		locDescr.add(ld);
		return locDescr;
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
	
	public static MatrixDouble generateCorrectionFactorMatrix(DataSet dataset, GroupContributionModel gcm)
	{
		int m = dataset.dataObjects.size();
		int n = gcm.getCorrectionFactors().size();
				
		MatrixDouble Cf = new MatrixDouble(m,n);
		for (int i = 0; i < m; i++)
		{
			DataSetObject dso = dataset.dataObjects.get(i);
			for (int j = 0; j < n; j++)
				Cf.el[i][j] = dso.fragmentation.correctionFactors.get(j);			
		}
		
		return Cf;
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
