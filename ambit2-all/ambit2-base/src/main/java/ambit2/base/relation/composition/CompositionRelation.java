package ambit2.base.relation.composition;

import java.util.Map;

import net.idea.modbcum.i.facet.IFacet;
import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.json.JSONUtils;
import ambit2.base.relation.AbstractRelation;
import ambit2.base.relation.STRUCTURE_RELATION;

/**
 * Substance composition
 * @author nina
<pre>
{
    "composition": [
        {
            "substance": {
                "URI": "http://apps.ideaconsult.net:8080/data/substance/1"
            },
            "component": {
                "compound": {
                    "URI": "http://apps.ideaconsult.net:8080/data/compound/21219/conformer/39738",
                    "structype": "NA",
                    "metric": null,
                    "name": "",
                    "cas": "",
                    "einecs": ""
                },
                "values": {
                    "http://apps.ideaconsult.net:8080/data/feature/http%3A%2F%2Fwww.opentox.org%2Fapi%2F1.1%23ChemicalNameDefault": "formaldehyde|Formaldehyde",
                    "http://apps.ideaconsult.net:8080/data/feature/http%3A%2F%2Fwww.opentox.org%2Fapi%2F1.1%23CASRNDefault": "50-00-0",
                    "http://apps.ideaconsult.net:8080/data/feature/http%3A%2F%2Fwww.opentox.org%2Fapi%2F1.1%23EINECSDefault": "200-001-8",
                    "http://apps.ideaconsult.net:8080/data/feature/http%3A%2F%2Fwww.opentox.org%2Fapi%2F1.1%23TradeNameDefault": "Formaldehyde, gas|Oxomethane|Methanal|Formaldehyde (8CI, 9CI)|Paraform|Formaldehyde solution|Methylene oxide|Formol|Formaldehyd|Formalin|Morbicid|Oxymethylene|Methyl aldehyde|Formic aldehyde|Formalith|Methaldehyde|TRA0001"
                },
                "facets": []
            },
            "compositionUUID": "L-340e87fb-e95f-3743-823d-1b9b25a94d56",
            "compositionName": "other: pure",
            "relation": "HAS_CONSTITUENT",
            "proportion": {
                "typical": {
                    "precision": "",
                    "value": 100,
                    "unit": "% (w/w)"
                },
                "real": {
                    "lowerPrecision": "",
                    "lowerValue": 0,
                    "upperPrecision": "",
                    "upperValue": 0,
                    "unit": ""
                },
                "function_as_additive": null
            }
        },
        {
            "substance": {
                "URI": "http://apps.ideaconsult.net:8080/data/substance/1"
            },
            "component": {
                "compound": {
                    "URI": "http://apps.ideaconsult.net:8080/data/compound/21219/conformer/39738",
                    "structype": "NA",
                    "metric": null,
                    "name": "",
                    "cas": "",
                    "einecs": ""
                },
                "values": {
                    "http://apps.ideaconsult.net:8080/data/feature/http%3A%2F%2Fwww.opentox.org%2Fapi%2F1.1%23ChemicalNameDefault": "formaldehyde|Formaldehyde",
                    "http://apps.ideaconsult.net:8080/data/feature/http%3A%2F%2Fwww.opentox.org%2Fapi%2F1.1%23CASRNDefault": "50-00-0",
                    "http://apps.ideaconsult.net:8080/data/feature/http%3A%2F%2Fwww.opentox.org%2Fapi%2F1.1%23EINECSDefault": "200-001-8",
                    "http://apps.ideaconsult.net:8080/data/feature/http%3A%2F%2Fwww.opentox.org%2Fapi%2F1.1%23TradeNameDefault": "Formaldehyde, gas|Oxomethane|Methanal|Formaldehyde (8CI, 9CI)|Paraform|Formaldehyde solution|Methylene oxide|Formol|Formaldehyd|Formalin|Morbicid|Oxymethylene|Methyl aldehyde|Formic aldehyde|Formalith|Methaldehyde|TRA0001"
                },
                "facets": []
            },
            "compositionUUID": "L-cf10d721-b0e7-37cd-a233-e9a2483c4d3c",
            "compositionName": "other: sales products in aqueous solution",
            "relation": "HAS_CONSTITUENT",
            "proportion": {
                "typical": {
                    "precision": "",
                    "value": 0,
                    "unit": ""
                },
                "real": {
                    "lowerPrecision": "",
                    "lowerValue": 0,
                    "upperPrecision": "",
                    "upperValue": 0,
                    "unit": ""
                },
                "function_as_additive": null
            }
        },
        {
            "substance": {
                "URI": "http://apps.ideaconsult.net:8080/data/substance/1"
            },
            "component": {
                "compound": {
                    "URI": "http://apps.ideaconsult.net:8080/data/compound/21220/conformer/39739",
                    "structype": "NA",
                    "metric": null,
                    "name": "",
                    "cas": "",
                    "einecs": ""
                },
                "values": {
                    "http://apps.ideaconsult.net:8080/data/feature/http%3A%2F%2Fwww.opentox.org%2Fapi%2F1.1%23ChemicalNameDefault": "water|Water",
                    "http://apps.ideaconsult.net:8080/data/feature/http%3A%2F%2Fwww.opentox.org%2Fapi%2F1.1%23CASRNDefault": "7732-18-5",
                    "http://apps.ideaconsult.net:8080/data/feature/http%3A%2F%2Fwww.opentox.org%2Fapi%2F1.1%23EINECSDefault": "231-791-2"
                },
                "facets": []
            },
            "compositionUUID": "L-f2d0bc7e-a154-3f6d-babd-22ea28467466",
            "compositionName": "",
            "relation": "HAS_ADDITIVE",
            "proportion": {
                "typical": {
                    "precision": "ca.",
                    "value": 49,
                    "unit": "% (w/w)"
                },
                "real": {
                    "lowerPrecision": "",
                    "lowerValue": 0,
                    "upperPrecision": "",
                    "upperValue": 0,
                    "unit": ""
                },
                "function_as_additive": "solvent"
            }
        },
        {
            "substance": {
                "URI": "http://apps.ideaconsult.net:8080/data/substance/1"
            },
            "component": {
                "compound": {
                    "URI": "http://apps.ideaconsult.net:8080/data/compound/21221/conformer/39740",
                    "structype": "NA",
                    "metric": null,
                    "name": "",
                    "cas": "",
                    "einecs": ""
                },
                "values": {
                    "http://apps.ideaconsult.net:8080/data/feature/http%3A%2F%2Fwww.opentox.org%2Fapi%2F1.1%23ChemicalNameDefault": "6,6'-(m-phenylene)bis(1,3,5-triazine-2,4-diamine)",
                    "http://apps.ideaconsult.net:8080/data/feature/http%3A%2F%2Fwww.opentox.org%2Fapi%2F1.1%23CASRNDefault": "5118-80-9",
                    "http://apps.ideaconsult.net:8080/data/feature/http%3A%2F%2Fwww.opentox.org%2Fapi%2F1.1%23EINECSDefault": "225-859-0"
                },
                "facets": []
            },
            "compositionUUID": "L-f2d0bc7e-a154-3f6d-babd-22ea28467466",
            "compositionName": "",
            "relation": "HAS_ADDITIVE",
            "proportion": {
                "typical": {
                    "precision": "",
                    "value": 0,
                    "unit": ""
                },
                "real": {
                    "lowerPrecision": "",
                    "lowerValue": 0,
                    "upperPrecision": "",
                    "upperValue": 0,
                    "unit": ""
                },
                "function_as_additive": ""
            }
        },
        {
            "substance": {
                "URI": "http://apps.ideaconsult.net:8080/data/substance/1"
            },
            "component": {
                "compound": {
                    "URI": "http://apps.ideaconsult.net:8080/data/compound/21222/conformer/39741",
                    "structype": "NA",
                    "metric": null,
                    "name": "",
                    "cas": "",
                    "einecs": ""
                },
                "values": {
                    "http://apps.ideaconsult.net:8080/data/feature/http%3A%2F%2Fwww.opentox.org%2Fapi%2F1.1%23ChemicalNameDefault": "methanol|Methanol",
                    "http://apps.ideaconsult.net:8080/data/feature/http%3A%2F%2Fwww.opentox.org%2Fapi%2F1.1%23CASRNDefault": "67-56-1",
                    "http://apps.ideaconsult.net:8080/data/feature/http%3A%2F%2Fwww.opentox.org%2Fapi%2F1.1%23EINECSDefault": "200-659-6"
                },
                "facets": []
            },
            "compositionUUID": "L-f2d0bc7e-a154-3f6d-babd-22ea28467466",
            "compositionName": "",
            "relation": "HAS_IMPURITY",
            "proportion": {
                "typical": {
                    "precision": "",
                    "value": 0,
                    "unit": ""
                },
                "real": {
                    "lowerPrecision": "",
                    "lowerValue": 0.5,
                    "upperPrecision": "",
                    "upperValue": 2,
                    "unit": "% (w/w)"
                },
                "function_as_additive": null
            }
        }
        
    ],
    "feature": {
        "http://apps.ideaconsult.net:8080/data/feature/http%3A%2F%2Fwww.opentox.org%2Fapi%2F1.1%23IUPACNameDefault": {
            "type": "Feature",
            "title": "IUPAC name",
            "units": "",
            "isNominal": "false",
            "isNumeric": "false",
            "sameAs": "http://www.opentox.org/api/1.1#IUPACName",
            "isModelPredictionFeature": false,
            "creator": "http://ambit.sourceforge.net",
            "order": 1,
            "source": {
                "URI": "http://apps.ideaconsult.net:8080/data/dataset/Default",
                "type": "Dataset"
            },
            "annotation": []
        },
        "http://apps.ideaconsult.net:8080/data/feature/http%3A%2F%2Fwww.opentox.org%2Fapi%2F1.1%23ChemicalNameDefault": {
            "type": "Feature",
            "title": "Names",
            "units": "",
            "isNominal": "false",
            "isNumeric": "false",
            "sameAs": "http://www.opentox.org/api/1.1#ChemicalName",
            "isModelPredictionFeature": false,
            "creator": "http://ambit.sourceforge.net",
            "order": 2,
            "source": {
                "URI": "http://apps.ideaconsult.net:8080/data/dataset/Default",
                "type": "Dataset"
            },
            "annotation": []
        }
    }
}
</pre>
 */
public class CompositionRelation extends AbstractRelation<STRUCTURE_RELATION, Proportion,SubstanceRecord,IStructureRecord> implements IStructureRecord {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1214326347782066597L;
	
	
	
	public enum jsonFeature {
		substance,
		component,
		compositionUUID,
		compositionName,
		relation,
		proportion
		;
		
		public String jsonname() {
			return name();
		}
	}
	
	protected String compositionUUID;
	/**
	 * Composition UUID
	 * @return
	 */
	public String getCompositionUUID() {
		return compositionUUID;
	}
	public void setCompositionUUID(String compositionUUID) {
		this.compositionUUID = compositionUUID;
	}
	public CompositionRelation(SubstanceRecord structure1,IStructureRecord structure2, Proportion proportion) {
		this(structure1,structure2,STRUCTURE_RELATION.HAS_CONSTITUENT,proportion);
	}
	public CompositionRelation(SubstanceRecord structure1,IStructureRecord structure2, STRUCTURE_RELATION type, Proportion proportion) {
		super(structure1,structure2,proportion);
		setRelationType(type);
	}
	@Override
	public String getFormula() {
		return getSecondStructure().getFormula();
	}
	@Override
	public void setFormula(String formula) {
		getSecondStructure().setFormula(formula);
	}
	@Override
	public String getSmiles() {
		return getSecondStructure().getSmiles();
	}
	@Override
	public void setSmiles(String smiles) {
		getSecondStructure().setSmiles(smiles);
	}
	@Override
	public String getInchi() {
		return getSecondStructure().getInchi();
	}
	@Override
	public void setInchi(String inchi) {
		getSecondStructure().setInchi(inchi);		
	}
	@Override
	public String getInchiKey() {
		return getSecondStructure().getInchiKey();
	}
	@Override
	public void setInchiKey(String key) {
		getSecondStructure().setInchiKey(key);		
	}
	@Override
	public int getIdchemical() {
		return getSecondStructure().getIdchemical();
	}
	@Override
	public void setIdchemical(int idchemical) {
		getSecondStructure().setIdchemical(idchemical);
	}
	@Override
	public String getFormat() {
		return getSecondStructure().getFormat();
	}
	@Override
	public void setFormat(String format) {
		getSecondStructure().setFormat(format);
	}
	@Override
	public int getIdstructure() {
		return getSecondStructure().getIdstructure();
	}
	@Override
	public void setIdstructure(int idstructure) {
		getSecondStructure().setIdstructure(idstructure);
	}
	@Override
	public String getContent() {
		return getSecondStructure().getContent();
	}
	@Override
	public void setContent(String content) {
		getSecondStructure().setContent(content);
	}
	@Override
	public boolean isSelected() {
		return getSecondStructure().isSelected();
	}
	@Override
	public void setSelected(boolean value) {
		getSecondStructure().setSelected(value);
	}
	@Override
	public int getNumberOfProperties() {
		return getSecondStructure().getNumberOfProperties();
	}
	@Override
	public Iterable<Property> getProperties() {
		return getSecondStructure().getProperties();
	}
	@Override
	public void setProperty(Property key, Object value) {
		getSecondStructure().setProperty(key, value);
	}
	@Override
	public Object getProperty(Property key) {
		return getSecondStructure().getProperty(key);
	}
	@Override
	public Object removeProperty(Property key) {
		return getSecondStructure().removeProperty(key);
	}
	@Override
	public void clearProperties() {
		getSecondStructure().clearProperties();
	}
	@Override
	public void addProperties(Map newProperties) {
		getSecondStructure().addProperties(newProperties);
	}
	@Override
	public String getWritableContent() {
		return getSecondStructure().getWritableContent();
	}
	@Override
	public ILiteratureEntry getReference() {
		return getSecondStructure().getReference();
	}
	@Override
	public void setReference(ILiteratureEntry reference) {
		getSecondStructure().setReference(reference);
	}
	@Override
	public STRUC_TYPE getType() {
		return getSecondStructure().getType();
	}
	@Override
	public void setType(STRUC_TYPE type) {
		getSecondStructure().setType(type);
	}
	@Override
	public int getDataEntryID() {
		return getSecondStructure().getDataEntryID();
	}
	@Override
	public void setDataEntryID(int id) {
		getSecondStructure().setDataEntryID(id);
	}
	@Override
	public int getDatasetID() {
		return getSecondStructure().getDatasetID();
	}
	@Override
	public void setDatasetID(int id) {
		getSecondStructure().setDatasetID(id);
	}
	@Override
	public boolean usePreferedStructure() {
		return getSecondStructure().usePreferedStructure();
	}
	@Override
	public void setUsePreferedStructure(boolean value) {
		getSecondStructure().setUsePreferedStructure(value);
	}
	@Override
	public Iterable<IFacet> getFacets() {
		return getSecondStructure().getFacets();
	}
	@Override
	public void addFacet(IFacet facet) {
		getSecondStructure().addFacet(facet);
	}
	@Override
	public void removeFacet(IFacet facet) {
		getSecondStructure().removeFacet(facet);
		
	}
	@Override
	public void clearFacets() {
		getSecondStructure().clearFacets();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public String toJSON(String substanceURI,String component) {
		return String.format(
				"\n{"+
				"\n\t\"%s\": {\"URI\" : %s }," + 
				"\n\t\"%s\": \t%s ," +
				"\n\t\"%s\":%s," +
				"\n\t\"%s\":%s," +
				"\n\t\"%s\":%s," + 
				"\n\t\"%s\":%s" + //metric
				"\n}",
				CompositionRelation.jsonFeature.substance.jsonname(),JSONUtils.jsonQuote(substanceURI),
				CompositionRelation.jsonFeature.component.jsonname(),
				component,
				CompositionRelation.jsonFeature.compositionUUID.jsonname(),JSONUtils.jsonQuote(getCompositionUUID()),
				CompositionRelation.jsonFeature.compositionName.jsonname(),JSONUtils.jsonQuote(getName()),
				CompositionRelation.jsonFeature.relation.jsonname(),JSONUtils.jsonQuote(getRelationType().name()),
				CompositionRelation.jsonFeature.proportion.jsonname(),getRelation().toJSON()
				);
	}
}
