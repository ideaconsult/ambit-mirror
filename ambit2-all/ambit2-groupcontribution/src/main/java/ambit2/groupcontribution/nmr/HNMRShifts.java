package ambit2.groupcontribution.nmr;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.groupcontribution.nmr.nmr_1h.HAtomEnvironment;
import ambit2.groupcontribution.nmr.nmr_1h.HAtomEnvironmentInstance;
import ambit2.groupcontribution.nmr.nmr_1h.HNMRKnowledgeBase;
import ambit2.groupcontribution.nmr.nmr_1h.HNMRPredefinedKnowledgeBase;
import ambit2.groupcontribution.nmr.nmr_1h.HShift;

public class HNMRShifts 
{
	private List<String> errors = new ArrayList<String>();
	private List<String> warnings = new ArrayList<String>();
	private HNMRKnowledgeBase knowledgeBase = null;
	//private boolean implicitHAtomsWorkingMode = true;
	private double resolutionStep = 0.01;
	
	private IAtomContainer molecule = null;
	private List<HShift> hShifts = new ArrayList<HShift>();
	private Map<Integer, Set<HShift>> binHShifts = new TreeMap<Integer, Set<HShift>>();
	private Map<IAtom, HShift> atomHShifts = new TreeMap<IAtom, HShift>();
	private List<HAtomEnvironmentInstance> hAtEnvInstances = new ArrayList<HAtomEnvironmentInstance>();
	private Map<IAtom, List<HAtomEnvironmentInstance>> atomHAtEnvInstanceSet = new TreeMap<IAtom, List<HAtomEnvironmentInstance>>();
	private Map<IAtom, HAtomEnvironmentInstance> atomHAtEnvInstance = new TreeMap<IAtom, HAtomEnvironmentInstance>();
	
	
	public HNMRShifts() throws Exception
	{
		knowledgeBase = HNMRPredefinedKnowledgeBase.getHNMRKnowledgeBase();
		knowledgeBase.configure();
		if (!knowledgeBase.errors.isEmpty())
			throw new Exception("There are errors in knowledge base:\n" + 
					knowledgeBase.getAllErrorsAsString());
	}
	
	public HNMRShifts(File knowledgeBaseFile) throws Exception
	{
		knowledgeBase = HNMRPredefinedKnowledgeBase.getHNMRKnowledgeBase(knowledgeBaseFile);
		knowledgeBase.configure();
		if (!knowledgeBase.errors.isEmpty())
			throw new Exception("There are errors in knowledge base:\n" + 
					knowledgeBase.getAllErrorsAsString());
	}
	
	public void setStructure(IAtomContainer str) throws Exception {
		molecule = str;
		errors.clear();
		warnings.clear();
	}	
	
	public void calculateHShifts()
	{
		hShifts.clear();
		binHShifts.clear();
		atomHShifts.clear();
		hAtEnvInstances.clear();
		atomHAtEnvInstanceSet.clear();
		atomHAtEnvInstance.clear();
		
		findAllHAtomEnvironmentInstances();
		
		handleOverlappingHAtomEnvironmentInstances();
		
		generateHShifts();
	}
	
	
	void findAllHAtomEnvironmentInstances()
	{
		for (int i = 0; i < knowledgeBase.hAtomEnvironments.size(); i++)
		{
			HAtomEnvironment hae = knowledgeBase.hAtomEnvironments.get(i);
			List<List<IAtom>> atMaps = hae.groupMatch.getMappings(molecule);
			
			if (atMaps.isEmpty())
				continue;
			
			for (int k = 0; k < atMaps.size(); k++ )
			{
				HAtomEnvironmentInstance haeInst = new HAtomEnvironmentInstance();
				haeInst.atoms = (IAtom[])atMaps.get(k).toArray();
				hAtEnvInstances.add(haeInst);
				registerHAtomEnvironmentInstance(haeInst);
			}
		}
	}
	
	public void registerHAtomEnvironmentInstance(HAtomEnvironmentInstance haeInst)
	{
		//Filling the data in atomHAtEnvInstanceSet
		for (int i = 0; i < haeInst.atoms.length; i++)
		{
			IAtom at = haeInst.atoms[i];
			List<HAtomEnvironmentInstance> haeInstList = atomHAtEnvInstanceSet.get(at);
			
			if (haeInstList == null)
			{	
				haeInstList = new ArrayList<HAtomEnvironmentInstance>();
				atomHAtEnvInstanceSet.put(at, haeInstList);
			}	
			
			haeInstList.add(haeInst);
		}
	}
	
	public void handleOverlappingHAtomEnvironmentInstances()
	{
		Set<IAtom> atoms = atomHAtEnvInstanceSet.keySet();
		for (IAtom at : atoms)
		{
			List<HAtomEnvironmentInstance> haeInstList = atomHAtEnvInstanceSet.get(at);
			HAtomEnvironmentInstance selectedInst = haeInstList.get(0);
			
			for (int i = 1; i < haeInstList.size(); i++)
			{
				HAtomEnvironmentInstance inst = haeInstList.get(i);
				
				if (selectedInst.hEnvironment.isHigherPriority(inst.hEnvironment))
					selectedInst = inst;
				else
				{
					if (inst.hEnvironment.isHigherPriority(selectedInst.hEnvironment))
					{
						//do nothing
					}
					else
					{	
						//cannot compare priorityInst with inst
						//TODO issue warning
					}	
				}
			}
		}
	}
	
	public void generateHShifts()
	{
		//TODO
	}
	
	

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public List<String> getWarnings() {
		return warnings;
	}

	public void setWarnings(List<String> warnings) {
		this.warnings = warnings;
	}

	public HNMRKnowledgeBase getKnowledgeBase() {
		return knowledgeBase;
	}

	public void setKnowledgeBase(HNMRKnowledgeBase knowledgeBase) {
		this.knowledgeBase = knowledgeBase;
	}
	
	public List<HShift> getHShifts(){
		return hShifts;
	}
	
	public int getBinNumber(double shiftValue)
	{
		return (int) Math.round(shiftValue / resolutionStep);		
	}

	public double getResolutionStep() {
		return resolutionStep;
	}

	public void setResolutionStep(double resolutionStep) {
		this.resolutionStep = resolutionStep;
	}

	public IAtomContainer getMolecule() {
		return molecule;
	}	
	
}
