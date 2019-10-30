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

import ambit2.descriptors.utils.GraphMatrices;
import ambit2.groupcontribution.nmr.nmr_1h.HAtomEnvironment;
import ambit2.groupcontribution.nmr.nmr_1h.HAtomEnvironmentInstance;
import ambit2.groupcontribution.nmr.nmr_1h.HNMRKnowledgeBase;
import ambit2.groupcontribution.nmr.nmr_1h.HNMRPredefinedKnowledgeBase;
import ambit2.groupcontribution.nmr.nmr_1h.HShift;
import ambit2.smarts.groups.GroupMatch;

public class HNMRShifts 
{
	private List<String> errors = new ArrayList<String>();
	private List<String> warnings = new ArrayList<String>();
	private HNMRKnowledgeBase knowledgeBase = null;
	//private boolean implicitHAtomsWorkingMode = true;
	private double resolutionStep = 0.01;
	
	private IAtomContainer molecule = null;
	private int distMatrix[][] = null;
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
		
		distMatrix = GraphMatrices.getTopDistanceMatrix(molecule);
		
		findAllGroupMappings();
		
		findAllHAtomEnvironmentInstances();
		
		handleOverlappingHAtomEnvironmentInstances();
		
		findAllSubstituents();
		
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
	
	public void findSubstituents(HAtomEnvironmentInstance haeInst)
	{
		int n = haeInst.hEnvironment.shiftDesignations.length;
		haeInst.substituentInstances = new ArrayList<List<SubstituentInstance>>();
		
		switch (haeInst.hEnvironment.shiftsAssociation)
		{
		case H_ATOM_POSITION:
			//TODO
			break;
			
		case SUBSTITUENT_POSITION:
			for (int pos = 0; pos < n; pos++)
			{
				//int substPos = 0; //default value
				//if (haeInst.hEnvironment.substituentPosAtomIndices != null)
				//	substPos = haeInst.hEnvironment.substituentPosAtomIndices[pos]-1; //1-base --> 0-base 
				
				//int distance = 1; //default value
				//if (haeInst.hEnvironment.positionDistances != null)
				//	distance = haeInst.hEnvironment.positionDistances[pos];
				
				int substPos = haeInst.hEnvironment.getSubstituentPosAtomIndicex(pos);
				int distance = haeInst.hEnvironment.getPositionDistance(pos);
				
				//Find possible starting atoms for substituent match
				//using distance matrix
				List<IAtom> startAtoms = new ArrayList<IAtom>();
				IAtom at0 = haeInst.atoms[substPos];
				int atIndex0 = molecule.indexOf(at0);
				//The start atoms are at apropriate distance from atom at0 
				for (int k = 0; k < distMatrix[atIndex0].length; k++)
					if (distMatrix[atIndex0][k] == distance)
					{	
						IAtom at = molecule.getAtom(k);
						
						//Check whether at is part of the instance
						boolean flagAdd = true;
						for (int i = 0; i < haeInst.atoms.length; i++)
							if (at == haeInst.atoms[i])
							{
								flagAdd = false;
								break;
							}
						
						if (flagAdd)
							startAtoms.add(at);
					}
				
				if (startAtoms.size() > 0)
				{
					List<SubstituentInstance> listSubst = new ArrayList<SubstituentInstance>();
					
					for (int k = 0; k < startAtoms.size(); k++)
					{	
						IAtom startAt = startAtoms.get(k);
						
						for (Substituent sub : haeInst.hEnvironment.substituents)
						{	
							List<List<IAtom>> maps = groupMappings.get(sub.name);
							if (maps != null)
							{	
								for (int i = 0; i < maps.size(); i++)
								{
									List<IAtom> map = maps.get(i);
									if (map.get(0) == startAt)
									{
										//Register substituent instance
										SubstituentInstance subInst = new SubstituentInstance(); 
										subInst.substituent = sub;
										subInst.atoms = new IAtom[map.size()];
										for (int j = 0; j < map.size(); j++)
											subInst.atoms[j] = map.get(j);
										listSubst.add(subInst);
									}
								}
							}
						}
					}
					
					if (listSubst.isEmpty())
						haeInst.substituentInstances.add(null);
					else
						haeInst.substituentInstances.add(listSubst);
				}
				else
					haeInst.substituentInstances.add(null);
				
			}
			break;
		}
	}
	
	public void findAllGroupMappings()
	{
		Set<String> keys = knowledgeBase.groupMatchRepository.keySet();
		for (String key : keys)
		{
			GroupMatch gm = knowledgeBase.groupMatchRepository.get(key);
			List<List<IAtom>> maps =  gm.getMappings(molecule);
			if (!maps.isEmpty())
				groupMappings.put(key, maps);
			//System.out.println("Searching: " + key + "  " + maps.size());
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
		sb.append("Filtered HAtomEnvironment Instances:\n");
		atoms = atomHAtEnvInstance.keySet();
		for (IAtom at : atoms)
		{
			//sb.append("At " + at.getSymbol() + "" + molecule.indexOf(at) + "\n");
			HAtomEnvironmentInstance inst = atomHAtEnvInstance.get(at);
			sb.append("  " + inst.hEnvironment.name);
			for (int k = 0; k < inst.atoms.length; k++)
				sb.append("  " + inst.atoms[k].getSymbol() + molecule.indexOf(inst.atoms[k]));
			sb.append("\n");
			
			if (inst.substituentInstances != null)
			{
				switch (inst.hEnvironment.shiftsAssociation)
				{
				case H_ATOM_POSITION:
					//TODO
					break;
				case SUBSTITUENT_POSITION:
					//Iterate all shift positions
					for (int i = 0; i < inst.substituentInstances.size(); i++)
					{	
						List<SubstituentInstance> siList = inst.substituentInstances.get(i);
						if (siList == null)
							continue;
						
						//int pos = 0; //default value
						//if (inst.hEnvironment.substituentPosAtomIndices != null)
						//	pos = inst.hEnvironment.substituentPosAtomIndices[i]-1; //1-base --> 0-base	
						
						//int distance = 1; //default value
						//if (inst.hEnvironment.positionDistances != null)
						//	distance = inst.hEnvironment.positionDistances[i];
						
						int pos = inst.hEnvironment.getSubstituentPosAtomIndicex(i);
						int distance = inst.hEnvironment.getPositionDistance(i);
						
						sb.append("    pos=" + (pos+1) + " distance=" + distance);
						for (SubstituentInstance si : siList)
						{
							sb.append(" " + si.substituent.name);
							for (int k = 0; k < si.atoms.length; k++)
								sb.append(" " + /*si.atoms[k].getSymbol()*/ molecule.indexOf(si.atoms[k]));
						}
						sb.append("\n");
					}
					break;
				}
			}
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
