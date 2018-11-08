package ambit2.tautomers.zwitterion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.tautomers.TautomerConst;
import net.sf.jniinchi.INCHI_OPTION;
import net.sf.jniinchi.INCHI_RET;



public class ZwitterionManager 
{
	protected static final Logger logger = Logger
			.getLogger(ZwitterionManager.class.getName());
	
	IAtomContainer originalMolecule = null;
	IAtomContainer molecule = null;
	List<IAcidicCenter> initialAcidicCenters = new ArrayList<IAcidicCenter>();
	List<IBasicCenter> initialBasicCenters = new ArrayList<IBasicCenter>();	
	List<IAcidicCenter> acidicCenters = new ArrayList<IAcidicCenter>();
	List<IBasicCenter> basicCenters = new ArrayList<IBasicCenter>();
	
	List<INCHI_OPTION> options = new ArrayList<INCHI_OPTION>();
	InChIGeneratorFactory igf = null;
	List<String> errors = new ArrayList<String>();
	Set<String> registeredInchies = new HashSet<String>();
	int numOfRegistrations = 0;
	int status = TautomerConst.STATUS_NONE;
	List<int[]> prevAcidComb = null;
	List<int[]> prevBaseComb = null;
	public Map<Integer,Integer> zwitterionCounts = new HashMap<Integer,Integer>(); 
	public int numOfGeneratedZwitterions = 0;
	
	//Configuration flags
	public boolean FlagUseCarboxylicGroups = true;
	public boolean FlagUseSulfonicAndSulfinicGroups = true;	
	public boolean FlagUsePhosphoricGroups = true;	
	public boolean FlagUsePrimaryAmines = true;
	public boolean FlagUseSecondaryAmines = true;	
	public boolean FlagUseTertiaryAmines = true;
	public boolean FlagFilterDuplicates = false;
	public int MaxNumberOfZwitterionicPairs = 100;
	public int MaxNumberOfRegisteredZwitterions = 10000;
		
	public ZwitterionManager() 
	{	
		inchiSetup();
	}
	
	void inchiSetup()
	{
		options.add(INCHI_OPTION.FixedH);
		options.add(INCHI_OPTION.SAbs);
		options.add(INCHI_OPTION.SAsXYZ);
		options.add(INCHI_OPTION.SPXYZ);
		options.add(INCHI_OPTION.FixSp3Bug);
		options.add(INCHI_OPTION.AuxNone);		
		try	{
			igf = InChIGeneratorFactory.getInstance();
		}
		catch (Exception x) {
		}
	}
	
	void init() 
	{
		initialAcidicCenters.clear();
		initialBasicCenters.clear();
		acidicCenters.clear();
		basicCenters.clear();
		zwitterionCounts.clear();
		registeredInchies.clear();
		numOfGeneratedZwitterions = 0;
	}
	
	public void setStructure(IAtomContainer str) throws Exception {		
		originalMolecule = str;
		molecule = (IAtomContainer) originalMolecule.clone();
		init();
	}	
	
	public List<IAtomContainer> generateZwitterions() throws Exception {
		status = TautomerConst.STATUS_STARTED;
		numOfRegistrations = 0;
		
		searchAllAcidicAndBasicCenters();
		
		//TODO handle initial ion states combinations
		
		acidicCenters.addAll(initialAcidicCenters);
		basicCenters.addAll(initialBasicCenters);		
		
		List<IAtomContainer> res = getZwitterionCombinations();
		
		return res;
	}
	
	List<IAtomContainer> getZwitterionCombinations()
	{
		List<IAtomContainer> zwittList = new ArrayList<IAtomContainer>();
		int minZC = acidicCenters.size();
		if (basicCenters.size() < minZC)
			minZC = basicCenters.size();
		
		if (minZC > MaxNumberOfZwitterionicPairs)
			minZC = MaxNumberOfZwitterionicPairs;
		
		if (minZC == 0)
			return zwittList; //Nothing to be generated
		
		prevAcidComb = null;
		prevBaseComb = null;
		
		for (int i = 1; i <= minZC; i++)
		{				
			if (numOfGeneratedZwitterions >= MaxNumberOfRegisteredZwitterions)
				break;
			setAllToNeutralState();
			List<IAtomContainer> zw = getZwitterionCombinations(i);
			zwittList.addAll(zw);
			zwitterionCounts.put(i, zw.size());
		}
		return zwittList;
	}
	
	List<IAtomContainer> getZwitterionCombinations(int k)
	{		
		List<IAtomContainer> zwList = new ArrayList<IAtomContainer>();
		
		if (k == 1)
		{
			for (int i = 0; i < acidicCenters.size(); i++)
			{	
				IAcidicCenter ac = acidicCenters.get(i);
				ac.shiftState();
				
				for (int j = 0; j < basicCenters.size(); j++)
				{
					IBasicCenter bc = basicCenters.get(j);
					bc.shiftState();
					try {
						IAtomContainer newZwitt = molecule.clone();
						
						if (FlagFilterDuplicates)
						{
							try {
								String inchiKey = getInchiKey(newZwitt);								
								if (!registeredInchies.contains(inchiKey))
								{
									zwList.add(newZwitt);
									numOfGeneratedZwitterions++;
									registeredInchies.add(inchiKey);
								}
							}
							catch (Exception x) {
							}
						}
						else
						{	
							zwList.add(newZwitt);
							numOfGeneratedZwitterions++;
						}	
						
						if (numOfGeneratedZwitterions >= MaxNumberOfRegisteredZwitterions)
							return zwList;
					}
					catch (Exception x) {
						errors.add(x.getMessage());
					}
					bc.shiftState(); //return to neutral
				}
				ac.shiftState(); //return to neutral
			}
			
			return zwList;
		}
		
		//Combinatorial zwitterion generation for k > 1
		
		List<int[]> acidComb = getCombinations(k, acidicCenters.size(), prevAcidComb);
		List<int[]> baseComb = getCombinations(k, basicCenters.size(), prevBaseComb);
		for (int[] a_comb : acidComb)
		{
			IAcidicCenter ac[] = new IAcidicCenter[k]; 
			for (int i = 0; i < k; i++)
			{	
				ac[i] = acidicCenters.get(a_comb[i]);
				ac[i].shiftState();
			}			

			for (int[] b_comb : baseComb)
			{
				IBasicCenter bc[] = new IBasicCenter[k]; 
				for (int i = 0; i < k; i++)
				{	
					bc[i] = basicCenters.get(b_comb[i]);
					bc[i].shiftState();
				}

				try {
					IAtomContainer newZwitt = molecule.clone();
					
					if (FlagFilterDuplicates)
					{
						try {
							String inchiKey = getInchiKey(newZwitt);
							if (!registeredInchies.contains(inchiKey))
							{
								zwList.add(newZwitt);
								numOfGeneratedZwitterions++;
								registeredInchies.add(inchiKey);
							}
						}
						catch (Exception x) {}
					}
					else
					{	
						zwList.add(newZwitt);
						numOfGeneratedZwitterions++;
					}
					
					if (numOfGeneratedZwitterions >= MaxNumberOfRegisteredZwitterions)
						return zwList;
				}
				catch (Exception x) {
					errors.add(x.getMessage());
				}

				for (int i = 0; i < k; i++)
					bc[i].shiftState(); //return to neutral
			}

			for (int i = 0; i < k; i++)				
				ac[i].shiftState(); //return to neutral
		}

		//Combination setup for next value of k
		if (k >= 4)
		{
			prevAcidComb = acidComb;
			prevBaseComb = baseComb;
		}

		return zwList;
	}
	
	void setAllToNeutralState()
	{
		for (IAcidicCenter ac : acidicCenters)
			ac.setState(IAcidicCenter.State.NEUTRAL);
		
		for (IBasicCenter bc : basicCenters)
			bc.setState(IBasicCenter.State.NEUTRAL);		
	}
	
	void searchAllAcidicAndBasicCenters()
	{
		for (IAtom atom : molecule.atoms())
		{
			switch (atom.getAtomicNumber())
			{
			case 6:
				if (FlagUseCarboxylicGroups)
				{
					IAcidicCenter c = CarboxylicGroup.getCenter(molecule, atom);
					if (c != null)
						initialAcidicCenters.add(c);
				}
				break;
			
			case 7:
				IBasicCenter c = AminoGroup.getCenter(molecule, atom);
				if (c != null)
					initialBasicCenters.add(c);
				break;
			
			case 15:
				if (FlagUsePhosphoricGroups)
				{
					IAcidicCenter pg = PhosphoricGroup.getCenter(molecule, atom);
					if (pg != null)
						initialAcidicCenters.add(pg);
				}
				break;
				
			case 16:
				if (FlagUseSulfonicAndSulfinicGroups)
				{
					IAcidicCenter sg = SulfGroup.getCenter(molecule, atom);
					if (sg != null)
						initialAcidicCenters.add(sg);
				}
				break;	
			}	
		}
		
	}
	
	List<int[]> getCombinations(int k, int n, List<int[]> comb_k_1)
	{
		//Choosing k elements out of a set with n elements
		List<int[]> combList = new ArrayList<int[]>();
		if (k > n)
			return combList; //empty list
		
		switch (k)
		{
		case 1:
			for (int i = 0; i < n; i++)
				combList.add(new int[] {i});
			break;
		case 2:
			for (int i = 0; i < n-1; i++)
				for (int j = i+1; j < n; j++)
					combList.add(new int[] {i,j});
			break;
		case 3:
			for (int i = 0; i < n-2; i++)
				for (int j = i+1; j < n-1; j++)
					for (int s = j+1; s < n; s++)
						combList.add(new int[] {i,j,s});	
			break;
		case 4:
			for (int i = 0; i < n-3; i++)
				for (int j = i+1; j < n-2; j++)
					for (int s = j+1; s < n-1; s++)
						for (int r = s+1; r < n; r++)
							combList.add(new int[] {i,j,s,r});
			break;
		default: // k > 4
			List<int[]> prev_comb_list = comb_k_1;
			if (prev_comb_list == null)
				prev_comb_list = getCombinations(k-1, n, null); //recursion
			
			//combinations c(k-1,n) are used
			for (int[] c : prev_comb_list)
			{	
				//Previous combination (c) forms the first k-1 
				//elements from the new of combination (comb) 
				for (int i = c[k-2]+1; i < n; i++)
				{
					int comb[] = new int[k];
					for (int s = 0; s < (k-1); s++)
						comb[s] = c[s];
					comb[k-1] = i;
					combList.add(comb);
				}
			}
			break;	
			
		}
		return combList;
	}
	
	String getInchiKey(IAtomContainer mol) throws Exception
	{
		InChIGenerator ig = igf.getInChIGenerator(mol, options);
		INCHI_RET returnCode = ig.getReturnStatus();
		if (INCHI_RET.ERROR == returnCode) {
			System.out.println("inchi error " + ig.getMessage());
			return null;
		}
		
		return ig.getInchiKey();
	}
}
