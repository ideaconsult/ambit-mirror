package ambit2.sln.io;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.base.relation.composition.Proportion;
import ambit2.sln.SLNContainer;
import ambit2.sln.SLNSubstance;
import ambit2.smarts.SmartsHelper;

public class SLN2Substance 
{
	public SLN2SubstanceConfig config = new SLN2SubstanceConfig();
	
	private List<String> conversionErrors = new ArrayList<String>();
	private List<String> conversionWarnings = new ArrayList<String>();
	public SLN2ChemObject sln2ChemObject = new SLN2ChemObject();
	
	
	public List<String> getConversionErrors() {
		return conversionErrors;
	}
	
	public List<String> getConversionWarnings() {
		return conversionWarnings;
	}
	
	public void clearAllErrorsAndWarnings(){
		conversionErrors.clear();
		conversionWarnings.clear();
	}
	
	public String getAllErrors()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < conversionErrors.size(); i++)
			sb.append(conversionErrors.get(i) + "\n");
		return sb.toString();
	}
	
	
	public List<CompositionRelation> slnToSubstanceComposition(SLNSubstance slnSubst)
	{
		if (slnSubst == null)
			return null;
		
		List<CompositionRelation> composition = new ArrayList<CompositionRelation>();
		for (int i = 0; i < slnSubst.containers.size(); i++)
		{
			SLNContainer slnContainer = slnSubst.containers.get(i);
			CompositionRelation compRel = slnContainerToCompositionRelation(slnContainer);
			composition.add(compRel);
		}
		
		return composition;
	}
	
	public SLNSubstance substanceCompositionToSln(List<CompositionRelation> composition)
	{
		SLNSubstance slnSubst = new SLNSubstance();
		for (int i = 0; i < composition.size(); i++)
		{
			CompositionRelation compRel = composition.get(i);
			SLNContainer slnContainer = compositionRelationToSLNContainer(compRel);
			slnSubst.containers.add(slnContainer);
		}
		return slnSubst;
	}
	
	
	public CompositionRelation slnContainerToCompositionRelation(SLNContainer slnContainer)
	{
		IStructureRecord structure = slnContainerToStructureRecord(slnContainer);
		CompositionRelation compRel = new CompositionRelation(null, structure, null, null);
		
		if (config.FlagCompositionUUID)
		{
			String attr = slnContainer.getAttributes().userDefiendAttr.get(config.compositionUUID_SLNAttr);
			if (attr != null)
				compRel.setCompositionUUID(attr);
		}
		
		if (config.FlagRelationMetric)
		{
			String attr = slnContainer.getAttributes().userDefiendAttr.get(config.relationMetric_SLNAttr);
			if (attr != null)
			{
				try
				{
					Proportion prop = SLNIOHelpers.proportionFromString(attr);
					compRel.setRelation(prop);
				}
				catch (Exception e)	
				{
					//Handle error
				}
			}
		}
		
		//TODO
		return compRel;
	}
	
	public SLNContainer compositionRelationToSLNContainer(CompositionRelation compRel)
	{
		SLNContainer container;
		if (compRel.getSecondStructure() != null)
			container = structureRecordToSLNContainer(compRel.getSecondStructure());
		else
			container = new SLNContainer(SilentChemObjectBuilder.getInstance());
		
		if (config.FlagCompositionUUID)
		{
			String attr = compRel.getCompositionUUID();
			if (attr != null)
				container.getAttributes().userDefiendAttr.put(config.compositionUUID_SLNAttr, attr);
		}
		
		if (config.FlagRelationMetric)
		{
			Proportion prop = compRel.getRelation();
			if (prop != null)
			{	
				try
				{
					String attr = SLNIOHelpers.proportionToString(prop);
					if (attr != null)
						container.getAttributes().userDefiendAttr.put(config.relationMetric_SLNAttr, attr);
				}
				catch (Exception e)	
				{
					//Handle error
				}
			}
			
		}
		
		return container;
	}
	
	public IStructureRecord slnContainerToStructureRecord(SLNContainer slnContainer)
	{
		if (slnContainer == null)
			return null;
		
		IAtomContainer container = sln2ChemObject.slnContainerToAtomContainer(slnContainer);
		try{
			SmartsHelper.preProcessStructure(container, true, config.FlagAddImplicitHAtomsOnSLNAtomConversion);
		}
		catch(Exception e)
		{
			conversionErrors.add("SSMILES generation error: " + e.getMessage());
		}
		conversionErrors.addAll(sln2ChemObject.getConversionErrors());
		conversionWarnings.addAll(sln2ChemObject.getConversionWarnings());
		
		IStructureRecord structure = new StructureRecord();
		if (config.FlagSmiles)
		{
			try{
				String smiles = SmartsHelper.moleculeToSMILES(container, true);
				structure.setSmiles(smiles);
			}
			catch(Exception e)
			{
				conversionErrors.add("SMILES generation error: " + e.getMessage());
			}
		}
		
		return structure;
	}
	
	public SLNContainer structureRecordToSLNContainer(IStructureRecord structure)
	{
		if (structure == null)
			return null;
		
		SLNContainer slnContainer = null; 
		
		if (structure.getSmiles() != null)
		{
			IAtomContainer container = null;
			try{
				container = SmartsHelper.getMoleculeFromSmiles(structure.getSmiles());
			}
			catch(Exception e)
			{
				conversionErrors.add("Incorrect SMILES: " + e.getMessage());
			}
			
			if (container != null)
			{	
				slnContainer = sln2ChemObject.atomContainerToSLNContainer(container);
				conversionErrors.addAll(sln2ChemObject.getConversionErrors());
				conversionWarnings.addAll(sln2ChemObject.getConversionWarnings());
			}
		}
		
		//TODO
		
		
		if (slnContainer == null)
			slnContainer = new SLNContainer(SilentChemObjectBuilder.getInstance());
		
		return slnContainer;
	}
	
	
	
}
