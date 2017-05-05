package ambit2.sln.io;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.sln.SLNContainer;
import ambit2.sln.SLNContainerSet;

public class SLN2Substance 
{
	//Conversion flags 
	public boolean FlagProportion = true;
	public boolean FlagCompositionUUID = true;
	public boolean FlagSmiles = true;
	public boolean FlagInchi = true;
	public boolean FlagInchiKey = true;
	public boolean FlagRelation = true;
	public boolean FlagContent = true;
	public boolean FlagProperties = true;
	public boolean FlagStrType = true;
	public boolean FlagFacets = true;	
	/*
	public boolean FlagReference = false;
	public boolean FlagSelected = false;
	public boolean FlagDataEntryID = false;
	public boolean FlagId_srcdataset = false;
	public boolean FlagIdchemical = false;
	public boolean FlagIdstructure = false;
	*/
	
	//Conversion attribute names for CompositionRelation fields
	public String proportion_SLNAttr = "proportion";
	public String compositionUUID_SLNAttr = "compositionUUID";
	public String name_SLNAttr = "name";
	public String relation_SLNAttr = "relationMetric";
	public String relationType_SLNAttr = "relationType";
	
	//Conversion attribute names for Structure Record fields
	public String inchiKey_SLNAttr = "inchiKey";
	public String formula_SLNAttr = "formula";
	public String idchemical_SLNAttr = "idchemical";
	public String idstructure_SNLAttr = "idstricture";
	public String content_SNLAttr = "content";
	public String format_SLNAttr = "format";
	public String reference_SLNAttr = "reference";
	public String properties_SLNAttr = "properties";
	public String type_SLNAttr = "type";
	/*
	public String selected_SLNAttr = "selected";
	public String facets_SLNAttr = "facets";
	public String dataEntryID_SLNAttr = "dataEntryID";
	public String id_srcdataset_SLNAttr = "id_srcdataset";
	*/
	
	public List<CompositionRelation> slnToSubstanceComposition(SLNContainerSet slnContSet)
	{
		if (slnContSet == null)
			return null;
		
		List<CompositionRelation> composition = new ArrayList<CompositionRelation>();
		for (int i = 0; i < slnContSet.containers.size(); i++)
		{
			SLNContainer slnContainer = slnContSet.containers.get(i);
			CompositionRelation compRel = slnToCompositionRelation(slnContainer);
			composition.add(compRel);
		}
		
		return composition;
	}
	
	public SLNContainerSet substanceCompositionToSln(List<CompositionRelation> composition)
	{
		//TODO
		return null;
	}
	
	
	public CompositionRelation slnToCompositionRelation(SLNContainer slnContainer)
	{
		IStructureRecord structure = slnToStructureRecord(slnContainer);
		CompositionRelation comRel = new CompositionRelation(null, structure, null, null);
		
		if (FlagCompositionUUID)
		{
			String attr = slnContainer.getAttributes().userDefiendAttr.get(compositionUUID_SLNAttr);
			if (attr != null)
				comRel.setCompositionUUID(attr);
		}
		
		//TODO
		return comRel;
	}
	
	public SLNContainer compositionRelationToSLN(CompositionRelation compRel)
	{
		//TODO
		return null;
	}
	
	public IStructureRecord slnToStructureRecord(SLNContainer slnContainer)
	{
		IStructureRecord structure = new StructureRecord();
		//TODO
		return structure;
	}
	
	public SLNContainer structureRecordToSLN(IStructureRecord structure)
	{
		//TODO
		return null;
	}
	
	
	
}
