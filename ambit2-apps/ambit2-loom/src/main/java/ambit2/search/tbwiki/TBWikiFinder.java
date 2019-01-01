package ambit2.search.tbwiki;

import net.idea.loom.common.ICallBack;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.tbwiki.CompoundInformation;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.search.AbstractFinder;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class TBWikiFinder extends AbstractFinder<String, IStructureRecord> implements ICallBack<String, QuerySolution> {
    /**
     * 
     */
    private static final long serialVersionUID = 8611534238844322150L;
    protected CompoundInformation cli = new CompoundInformation("", "");
    protected IStructureRecord record = new StructureRecord();
    protected LiteratureEntry le;

    public TBWikiFinder(Template profile, LiteratureEntry le) {
	super(profile, null, ambit2.base.processors.search.AbstractFinder.MODE.propertyonly);
	this.le = le;
    }

    @Override
    protected IStructureRecord query(String term) throws AmbitException {
	try {
	    record.clear();
	    cli.process(term, this);
	    return record;
	} catch (Exception x) {
	    throw new AmbitException(x);
	}
    }

    @Override
    public void process(String term, QuerySolution row) {
	RDFNode node = row.get("p");
	RDFNode object = row.get("o");

	String key = node.asResource().getURI().toString();
	Object value = object.isLiteral() ? object.asLiteral().getString().trim() : object.asResource().getURI();

	String pname = key.replace("http://wiki.toxbank.net/wiki/Special:URIResolver/Property-3A", "").trim();
	String label = node.asResource().getURI().toString();

	if ("Has_CAS".equals(key))
	    label = Property.opentox_CAS;
	else if ("Has_InChI".equals(key))
	    label = Property.opentox_InChI;
	else if ("Has_InChIKey".equals(key))
	    label = Property.opentox_InChIKey;
	else if ("Has_Smiles".equals(key))
	    label = Property.opentox_SMILES;
	else if ("Has_PubChem_CID".equals(key))
	    label = Property.opentox_Pubchem;
	else if ("Has_ChEBI_Id".equals(key))
	    label = Property.opentox_ChEBI;
	else if ("Has_ChEMBL_Id".equals(key))
	    label = Property.opentox_ChEMBL;
	else if ("Has_ChemSpider_Id".equals(key))
	    label = Property.opentox_ChemSpider;
	else if ("Has_DrugBank_Id".equals(key))
	    label = Property.opentox_DrugBank;
	else if ("Has_KEGG_Id".equals(key))
	    label = Property.opentox_KEGG;
	else if ("Has_SigmaAldrich_Id".equals(key))
	    label = Property.opentox_SigmaAldrich;
	else if ("Has_Leadscope_Id".equals(key))
	    label = Property.opentox_LS;

	if ("http://www.w3.org/2000/01/rdf-schema#label".equals(key)) {
	    label = Property.opentox_Name;
	    pname = "Label";
	} else if ("http://www.w3.org/2000/01/rdf-schema#seeAlso".equals(key)) {
	    // System.out.println("seeAlso\t" + value);
	    return;
	} else if ("http://www.w3.org/2002/07/owl#sameAs".equals(key)) {
	    // System.out.println("sameAs\t" + value);
	    return;
	} else if ("Approved_on".equals(pname)) {
	    return;
	} else if ("Has_image".equals(pname)) {
	    return;
	} else if ("http://semantic-mediawiki.org/swivt/1.0#page".equals(key)) {
	    return;
	} else if ("http://www.w3.org/2000/01/rdf-schema#isDefinedBy".equals(key)) {
	    return;
	} else if ("http://semantic-mediawiki.org/swivt/1.0#wikiNamespace".equals(key)) {
	    return;
	} else if ("http://semantic-mediawiki.org/swivt/1.0#wikiPageModificationDate".equals(key)) {
	    return;
	} else if ("http://www.w3.org/1999/02/22-rdf-syntax-ns#type".equals(key)) {
	    pname = object.asResource().getLocalName();
	    label = node.asResource().getURI();
	    value = "Yes";
	} else if ("Has_toxic_effect".equals(pname)) {
	    pname = object.asResource().getLocalName();
	    if ("Subject".equals(pname))
		return;
	    label = node.asResource().getURI();
	    value = "Yes";
	} else if ("Has_category".equals(pname)) {
	    pname = object.asResource().getLocalName();
	    label = node.asResource().getURI();
	    value = "Yes";
	} else if ("Has_Toxicity_List".equals(pname)) {
	    return;
	}
	Property property = new Property(pname, le);
	property.setLabel(label);
	record.setRecordProperty(property, value);
    }

    @Override
    public void done(String identifier) {
    }

}
