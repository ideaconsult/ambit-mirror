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
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry.Conformation;
import org.openscience.cdk.stereo.DoubleBondStereochemistry;

import ambit2.base.exceptions.EmptyMoleculeException;
import ambit2.descriptors.utils.GraphMatrices;
import ambit2.groupcontribution.nmr.nmr_1h.HAtomEnvironment;
import ambit2.groupcontribution.nmr.nmr_1h.HAtomEnvironmentInstance;
import ambit2.groupcontribution.nmr.nmr_1h.HNMRKnowledgeBase;
import ambit2.groupcontribution.nmr.nmr_1h.HNMRPredefinedKnowledgeBase;
import ambit2.groupcontribution.nmr.nmr_1h.HShift;
import ambit2.groupcontribution.nmr.nmr_1h.SpinSplitManager;
import ambit2.smarts.StereoChemUtils;
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
	private SpinSplitManager spinSplitMan = new SpinSplitManager();
	
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
	
	public void calculateHShifts() throws EmptyMoleculeException
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
		
		spinSplitMan.setup(molecule, atomHShifts);
		spinSplitMan.caclulateSplits();
	}
	
	 
	void findAllHAtomEnvironmentInstances() throws EmptyMoleculeException
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
		//for (int i = 0; i < haeInst.atoms.length; i++)
		int i = 0;
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
		if (haeInst.hEnvironment.name.equalsIgnoreCase("ALKENES"))
			n = 2; //correction for skipping unnecessary substituent search
		
		haeInst.substituentInstances = new ArrayList<List<SubstituentInstance>>();
		
		switch (haeInst.hEnvironment.shiftsAssociation)
		{
		case H_ATOM_POSITION:
			//TODO
			break;
			
		case SUBSTITUENT_POSITION:
			for (int pos = 0; pos < n; pos++)
			{	
				int substPos = haeInst.hEnvironment.getSubstituentPosAtomIndicex(pos);
				int distance = haeInst.hEnvironment.getPositionDistance(pos);
				
				//Find possible starting atoms for substituent match
				//using distance matrix
				List<IAtom> startAtoms = new ArrayList<IAtom>();
				IAtom at0 = haeInst.atoms[substPos];
				int atIndex0 = molecule.indexOf(at0);
				
				//The start atoms are at appropriate distance from atom at0 
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
									if ((map.get(0) == startAt) &&
										checkSubstituentMapping(map, haeInst, atIndex0, distance))
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
	
	public boolean checkSubstituentMapping(List<IAtom> map, 
				HAtomEnvironmentInstance haeInst, int substPosAtIndex, int distance)
	{
		//Check whether:
		//(1) All map atoms are not part of the instance
		//(2) All map atoms are not closer than distance to the substPos atoms
		
		//First atom does not need checking
		if (map.size() == 1)
			return true;
		
		for (int i = 1; i < map.size(); i++)
		{
			IAtom at = map.get(i);
			
			for (int k = 0; k < haeInst.atoms.length; k++)
			{	
				if (at == haeInst.atoms[k])
					return false; //at is part of the instance
			}
			
			int atIndex = molecule.indexOf(at);
			if (distMatrix[atIndex][substPosAtIndex] < distance)
				return false;
		}
		return true;
	}
	
	public void findAllGroupMappings() throws EmptyMoleculeException
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
		Set<IAtom> atoms = atomHAtEnvInstance.keySet();
		for (IAtom at : atoms)
		{
			HAtomEnvironmentInstance inst = atomHAtEnvInstance.get(at);
			
			//Special recognition for ALKENES environment
			if (inst.hEnvironment.name.equalsIgnoreCase("ALKENES"))
			{
				calcAlkeneHShifts(inst, 0);
				calcAlkeneHShifts(inst, 1);
				continue;
			}
			
			HShift hs = calcHShift(inst);
			hShifts.add(hs);
		}	
	}
	
	public HShift calcHShift(HAtomEnvironmentInstance haeInst)
	{			
		HShift hs = new HShift();
		StringBuffer sb = new StringBuffer();
		
		hs.value = haeInst.hEnvironment.chemShift0;
		if (haeInst.hEnvironment.implicitHAtomsNumber != null)
			hs.imlicitHAtomsNumbers = haeInst.hEnvironment.implicitHAtomsNumber;
		hs.atomIndex = molecule.indexOf(haeInst.atoms[0]);
		sb.append(haeInst.hEnvironment.name + " ");
		sb.append(haeInst.hEnvironment.chemShift0);
		
		switch (haeInst.hEnvironment.shiftsAssociation)
		{
		case H_ATOM_POSITION:
			//TODO
			break;
		case SUBSTITUENT_POSITION:
			//Iterate all shift positions
			for (int i = 0; i < haeInst.substituentInstances.size(); i++)
			{	
				List<SubstituentInstance> siList = haeInst.substituentInstances.get(i);
				if (siList == null)
					continue;
				
				for (SubstituentInstance si : siList)
				{
					hs.value += si.substituent.chemShifts[i];
					sb.append(" + " + si.substituent.chemShifts[i]);
					sb.append(" (" + haeInst.hEnvironment.shiftDesignations[i]);
					sb.append(", " + si.substituent.name + ")");
				}
			}	
		}
		
		hs.explanationInfo = sb.toString();
		return hs;
	}
	
	
	public void calcAlkeneHShifts(HAtomEnvironmentInstance haeInst, int atIndex)
	{
		//The other atom from the double bond
		int atIndex2 = (atIndex==0)?1:0;
		
		List<SubstituentInstance> siList = haeInst.substituentInstances.get(atIndex);
		List<SubstituentInstance> siList2 = haeInst.substituentInstances.get(atIndex2);
		
		if (siList == null)
		{
			//TODO check for unidentified substituents to issue a warning message
			
			//No substituents is handled as 2 implicit H atoms
			//Atom at position atIndex contains two implicit H atoms
			//Handling stereo double bond is not needed
			if (siList2 == null)
			{
				//Either unknown substituents or implicit H atoms
				//The unknown substituents are treated as implicit H atoms
				generateAlkeneHShift(haeInst, atIndex, null, null, null, 2, false);
			}
			else
			{
				if (siList2.size() == 1)
				{
					generateAlkeneHShift(haeInst, atIndex, null, siList2.get(0), null, 2, false);
					generateAlkeneHShift(haeInst, atIndex, null, null, siList2.get(0), 2, false);
				}
				else if (siList2.size() == 2)
				{
					generateAlkeneHShift(haeInst, atIndex, null, siList2.get(0), siList2.get(1), 2, false);
					generateAlkeneHShift(haeInst, atIndex, null, siList2.get(1), siList2.get(0), 2, false);					
				}
			}
		}
		else
		{	
			if (siList.size() == 2)
			{	
				//Two substituents at atIndex means no H atoms 
				return;
			}	
			
			//One implicit H atom and one substituent
			IBond bo = molecule.getBond(haeInst.atoms[0], haeInst.atoms[1]); 
			DoubleBondStereochemistry dbsc = StereoChemUtils.findDBStereoElementByStereoBond(bo, molecule);
			
			if (siList2 == null)
			{
				//Either unknown substituents or implicit H atoms
				//The unknown substituents are treated as implicit H atoms
				generateAlkeneHShift(haeInst, atIndex, siList.get(0), null, null, 1, false);
			}
			else
			{
				if (siList2.size() == 1)
				{
					if (dbsc == null) {
						//No stereo specified. Considering both configuration cis and trans
						generateAlkeneHShift(haeInst, atIndex, siList.get(0), siList2.get(0), null, 1, true);
						generateAlkeneHShift(haeInst, atIndex, siList.get(0), null, siList2.get(0), 1, true);
					}
					else {
						//Handle stereo bond
						if (dbsc.getStereo() == Conformation.TOGETHER) {	
							//Cis
							generateAlkeneHShift(haeInst, atIndex, siList.get(0), siList2.get(0), null, 1, false);
						}	
						else {
							//Trans
							generateAlkeneHShift(haeInst, atIndex, siList.get(0), null, siList2.get(0), 1, false);
						}	
					}
				
				}
				else if (siList2.size() == 2)
				{
					if (dbsc == null) {
						//No stereo specified. Considering both configurations cis and trans
						generateAlkeneHShift(haeInst, atIndex, siList.get(0), siList2.get(0), siList2.get(1), 1, true);
						generateAlkeneHShift(haeInst, atIndex, siList.get(0), siList2.get(1), siList2.get(0), 1, true);
					}
					else {
						//Handle stereo bond
						int k = 0; //the index of SubstituentInstance participating in the stereo bond
						if (dbsc.contains(siList2.get(1).atoms[0]))
							k = 1;
						
						int k2 = (k==0)?1:0; ////the index of other SubstituentInstance
						
						if (dbsc.getStereo() == Conformation.OPPOSITE) {	
							//SubstituentInstance(k) is OPPOSITE (i.e. TRANS) to 
							//the substituent on the other side of the double bond 
							//Therofore it is CIS to the H atom on the other side of the double bond
							
							//Cis  
							generateAlkeneHShift(haeInst, atIndex, siList.get(0), siList2.get(k), siList2.get(k2), 1, false);
						}	
						else {
							//Trans (see logic from the upper comment)
							generateAlkeneHShift(haeInst, atIndex, siList.get(0), siList2.get(k2), siList2.get(k), 1, false);
						}
					}
				}
			}		
		}
		
	}
	
	
	void generateAlkeneHShift(HAtomEnvironmentInstance haeInst, int atIndex,
			SubstituentInstance gemSI, SubstituentInstance cisSI, SubstituentInstance transSI, 
			int numImlicitHAtoms, boolean missingStereo)
	{
		HShift hs = new HShift();
		StringBuffer sb = new StringBuffer();
		hs.value = haeInst.hEnvironment.chemShift0;
		hs.imlicitHAtomsNumbers = numImlicitHAtoms;
		hs.atomIndex = molecule.indexOf(haeInst.atoms[atIndex]);
		sb.append(haeInst.hEnvironment.name + " ");
		if (missingStereo)
			sb.append("no stereo ");
		sb.append(haeInst.hEnvironment.chemShift0);
		
		SubstituentInstance si = null;
		int pos = 0;
		
		//Geminal
		if (gemSI != null)
		{	
			si = gemSI;
			pos = 0;
			hs.value += si.substituent.chemShifts[pos];
			sb.append(" + " + si.substituent.chemShifts[pos]);
			sb.append(" (" + haeInst.hEnvironment.shiftDesignations[pos]);
			sb.append(", " + si.substituent.name + ")");
		}
		
		/*
		if (missingStereo)
		{			
			si = cisSI;
			hs.value += (si.substituent.chemShifts[1] + si.substituent.chemShifts[2]) / 2;
			sb.append(" + (" + si.substituent.chemShifts[1] + "+" + si.substituent.chemShifts[2] + ")/2");			
			sb.append(" (" + si.substituent.name + ")");
			
			si = transSI;
			hs.value += (si.substituent.chemShifts[1] + si.substituent.chemShifts[2]) / 2;
			sb.append(" + (" + si.substituent.chemShifts[1] + "+" + si.substituent.chemShifts[2] + ")/2");			
			sb.append(" (" + si.substituent.name + ")");
		}
		*/
				
		//Cis
		if (cisSI != null)
		{	
			si = cisSI;
			pos = 1;
			hs.value += si.substituent.chemShifts[pos];
			sb.append(" + " + si.substituent.chemShifts[pos]);
			sb.append(" (" + haeInst.hEnvironment.shiftDesignations[pos]);
			sb.append(", " + si.substituent.name + ")");
		}

		//Trans
		if (transSI != null)
		{	
			si = transSI;
			pos = 2;
			hs.value += si.substituent.chemShifts[pos];
			sb.append(" + " + si.substituent.chemShifts[pos]);
			sb.append(" (" + haeInst.hEnvironment.shiftDesignations[pos]);
			sb.append(", " + si.substituent.name + ")");
		}

		hs.explanationInfo = sb.toString();
		hShifts.add(hs);
	}
	
	
	public String getCalcLog() 
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append("Initial HAtomEnvironment Instances:\n");
		Set<IAtom> atoms = atomHAtEnvInstanceSet.keySet();
		for (IAtom at : atoms)
		{
			sb.append("At " + at.getSymbol() + "" + (molecule.indexOf(at) + 1) + "\n");
			List<HAtomEnvironmentInstance> haeInstList = atomHAtEnvInstanceSet.get(at);
			for (HAtomEnvironmentInstance inst : haeInstList)
			{
				sb.append("  " + inst.hEnvironment.name);
				for (int k = 0; k < inst.atoms.length; k++)
					sb.append("  " + inst.atoms[k].getSymbol() + (molecule.indexOf(inst.atoms[k])+1));
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
				sb.append("  " + inst.atoms[k].getSymbol() + (molecule.indexOf(inst.atoms[k])+1));
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
								sb.append(" " + /*si.atoms[k].getSymbol()*/ (molecule.indexOf(si.atoms[k])+1));
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
