package ambit2.tautomers.zwitterion;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.tautomers.TautomerConst;



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
		
	List<String> errors = new ArrayList<String>();
	int numOfRegistrations = 0;
	int status = TautomerConst.STATUS_NONE;
	List<int[]> prevAcidComb = null;
	List<int[]> prevBaseComb = null;
		
	public boolean FlagUseCarboxylicGroups = true;
	public boolean FlagUseSulfonicGroups = true;
	public boolean FlagUseSulfinicGroups = true;
	public boolean FlagUsePhosphoricGroups = true;
	public int MaxNumberZwitterions = 1000000;
		
	
	public ZwitterionManager() 
	{	
	}
	
	void init() 
	{
		initialAcidicCenters.clear();
		initialBasicCenters.clear();
		acidicCenters.clear();
		basicCenters.clear();
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
		
		if (minZC > MaxNumberZwitterions)
			minZC = MaxNumberZwitterions;
		
		if (minZC == 0)
			return zwittList; //Nothing to be generated
		
		prevAcidComb = null;
		prevBaseComb = null;
		
		for (int i = 1; i <= minZC; i++)
		{				
			setAllToNeutralState();
			List<IAtomContainer> zw = getZwitterionCombinations(i);
			zwittList.addAll(zw);
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
						zwList.add(newZwitt);
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
		{
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
						zwList.add(newZwitt);
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
			if (k >= 3)
			{
				prevAcidComb = acidComb;
				prevBaseComb = baseComb;
			}
		}
		
		return null;
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
						for (int r = j+1; r < n; r++)
							combList.add(new int[] {i,j,s,r});
		default: // k > 4
			List<int[]> prev_comb_list = comb_k_1;
			if (prev_comb_list == null)
				prev_comb_list = getCombinations(k-1, n, null); //recursion
			
			//combinations c(k-1,n) are used
			for (int[] c : prev_comb_list)
			{	
				//Previous combination (c) forms the first k-1 
				//elements from the new of combination (comb) 
				for (int i = c[k-2]+1; i < k; i++)
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
}
