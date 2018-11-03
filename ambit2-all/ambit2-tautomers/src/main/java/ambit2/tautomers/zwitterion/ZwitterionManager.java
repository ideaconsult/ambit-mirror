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
		
	public boolean FlagUseCarboxylicGroups = true;
	public boolean FlagUseSulfonicGroups = true;
	public boolean FlagUseSulfinicGroups = true;
	public boolean FlagUsePhosphoricGroups = true;
	public int MaxNumberZwitterions = -1;
		
	
	public ZwitterionManager() 
	{
		init();
	}
	
	void init() 
	{	
	}
	
	public void setStructure(IAtomContainer str) throws Exception {
		molecule = str;
		originalMolecule = str;
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
		int min = acidicCenters.size();
		if (basicCenters.size() < min)
			min = basicCenters.size();
		
		if (min == 0)
			return zwittList; //Nothing to be generated
		
		for (int i = 0; i < MaxNumberZwitterions; i++)
		{	
			if (i >= min)
				break;
			List<IAtomContainer> zw = getZwitterionCombinations(i);
			zwittList.addAll(zw);
		}
		return zwittList;
	}
	
	List<IAtomContainer> getZwitterionCombinations(int n)
	{
		//Combinatorial zwitterion generation
		//TODO
		return null;
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
			//combinations c(k-1,n) are used
			//TODO
			break;	
			
		}
		return combList;
	}
}
