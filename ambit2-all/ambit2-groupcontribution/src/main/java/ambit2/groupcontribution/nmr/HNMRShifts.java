package ambit2.groupcontribution.nmr;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
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
	private Map<IAtom, HShift> atomHShifts = new HashMap<IAtom, HShift>();
	private List<HAtomEnvironmentInstance> hAtEnvInstances = new ArrayList<HAtomEnvironmentInstance>();
	private Map<IAtom, List<HAtomEnvironmentInstance>> atomHAtEnvInstanceSet = new HashMap<IAtom, List<HAtomEnvironmentInstance>>();
	private Map<IAtom, HAtomEnvironmentInstance> atomHAtEnvInstance = new HashMap<IAtom, HAtomEnvironmentInstance>();
	private Map<String, List<List<IAtom>>> groupMappings = new HashMap<String, List<List<IAtom>>>();
	
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
		groupMappings.clear();
		
		findAllHAtomEnvironmentInstances();
		
		handleOverlappingHAtomEnvironmentInstances();
		
		generateHShifts();
	}
	
	
	void findAllHAtomEnvironmentInstances()
	{
		for (int i = 0; i < knowledgeBase.hAtomEnvironments.size(); i++)
		{
			HAtomEnvironment hae = knowledgeBase.hAtomEnvironments.get(i);
			if (!hae.flagUse)
				continue;
			
			List<List<IAtom>> atMaps = hae.groupMatch.getMappings(molecule);
			
			if (atMaps.isEmpty())
				continue;
			
			for (int k = 0; k < atMaps.size(); k++ )
			{
				HAtomEnvironmentInstance haeInst = new HAtomEnvironmentInstance();
				haeInst.hEnvironment = hae;
				
				int n = atMaps.get(k).size();
				haeInst.atoms = new IAtom[n];
				for (int r = 0; r < n; r++)
					haeInst.atoms[r] = atMaps.get(k).get(r);
				
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
			
			atomHAtEnvInstance.put(at, selectedInst);
		}
	}
	
	public void findAllSubstituents()
	{
		Set<IAtom> atoms = atomHAtEnvInstance.keySet();
		for (IAtom at : atoms)
		{
			HAtomEnvironmentInstance inst = atomHAtEnvInstance.get(at);
			findSubstituents(inst);
		}
	}
	
	public void findSubstituents(HAtomEnvironmentInstance inst)
	{
		switch (inst.hEnvironment.shiftsAssociation)
		{
		case H_ATOM_POSITION:
			//TODO
			break;
			
		case SUBSTITUENT_POSITION:
			for (int i = 0; i < inst.hEnvironment.shiftDesignations.length; i++)
			{
				int substPos = 0; //default value
				if (inst.hEnvironment.substituentPosAtomIndices != null)
					substPos = inst.hEnvironment.substituentPosAtomIndices[i]-1; //1-base --> 0-base 
				
				int distance = 1; //default value
				if (inst.hEnvironment.positionDistances != null)
					distance = inst.hEnvironment.positionDistances[i];
				
			}
			break;
		}
	}
	
	
	public void generateHShifts()
	{
		//TODO
	}
	
	public String getCalcLog() 
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append("Initial HAtomEnvironment Instances:\n");
		Set<IAtom> atoms = atomHAtEnvInstanceSet.keySet();
		for (IAtom at : atoms)
		{
			sb.append("At " + at.getSymbol() + "" + molecule.indexOf(at) + "\n");
			List<HAtomEnvironmentInstance> haeInstList = atomHAtEnvInstanceSet.get(at);
			for (HAtomEnvironmentInstance inst : haeInstList)
			{
				sb.append("  " + inst.hEnvironment.name);
				for (int k = 0; k < inst.atoms.length; k++)
					sb.append("  " + inst.atoms[k].getSymbol() + molecule.indexOf(inst.atoms[k]));
				sb.append("\n");
			}
		}
		
		sb.append("\n");
		sb.append("Refined HAtomEnvironment Instances:\n");
		atoms = atomHAtEnvInstance.keySet();
		for (IAtom at : atoms)
		{
			sb.append("At " + at.getSymbol() + "" + molecule.indexOf(at) + "\n");
			HAtomEnvironmentInstance inst = atomHAtEnvInstance.get(at);
			sb.append("  " + inst.hEnvironment.name);
			for (int k = 0; k < inst.atoms.length; k++)
				sb.append("  " + inst.atoms[k].getSymbol() + molecule.indexOf(inst.atoms[k]));
			sb.append("\n");
		}

		return sb.toString();
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
