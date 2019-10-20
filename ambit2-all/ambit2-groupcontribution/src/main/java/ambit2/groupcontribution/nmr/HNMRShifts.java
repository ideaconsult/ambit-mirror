package ambit2.groupcontribution.nmr;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.groupcontribution.nmr.nmr_1h.HAtomEnvironmentInstance;
import ambit2.groupcontribution.nmr.nmr_1h.HNMRKnowledgeBase;
import ambit2.groupcontribution.nmr.nmr_1h.HNMRPredefinedKnowledgeBase;
import ambit2.groupcontribution.nmr.nmr_1h.HShift;

public class HNMRShifts 
{
	private List<String> errors = new ArrayList<String>();
	private List<String> warnings = new ArrayList<String>();
	private HNMRKnowledgeBase knowledgeBase = null;
	private boolean implicitHAtomsWorkingMode = true;
	private double resolutionStep = 0.05;
	
	private IAtomContainer molecule = null;
	private List<HShift> hShifts = new ArrayList<HShift>();
	private Map<Integer, HShift> binHShifts = new TreeMap<Integer, HShift>();
	
	private List<HAtomEnvironmentInstance> hAtEnvInstances = new ArrayList<HAtomEnvironmentInstance>();
	
	
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
		hAtEnvInstances.clear();
		
		findAllHAtomEnvironmentInstances();
		
		//TODO
		//generate H shifts
	}
	
	
	void findAllHAtomEnvironmentInstances()
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
	
	
}
