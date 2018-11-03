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
	List<IAcidicCenter> acidicCenters = null;
	List<IBasicCenter> basicCenters = null;
		
	List<String> errors = new ArrayList<String>();
	int numOfRegistrations = 0;
	int status = TautomerConst.STATUS_NONE;
		
	public boolean FlagUseCarboxylicGroups = true;
	public boolean FlagUseSulfonicGroups = true;
	public boolean FlagUseSulfinicGroups = true;
	public boolean FlagUsePhosphoricGroups = true;
		
	
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
		
		//combinatorial zwitterion generation
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
		
		acidicCenters = initialAcidicCenters;
		basicCenters = initialBasicCenters;
	}
}
