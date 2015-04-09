package ambit2.rest.property;

import java.io.Writer;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.restnet.rdf.ns.OT;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.data.ILiteratureEntry._type;
import ambit2.base.data.Property;
import ambit2.base.data.PropertyAnnotation;
import ambit2.base.data.study.MultiValue;
import ambit2.base.data.substance.SubstanceProperty;
import ambit2.base.json.JSONUtils;

/**
 * JSON
 * 
 * @author nina
 * 
 * @param <Q>
 */
public class PropertyJSONReporter extends PropertyURIReporter {
    /**
	 * 
	 */
    private static final long serialVersionUID = 410930501401847402L;
    protected String comma = null;

    enum jsonFeature {
	URI, title, units, isNominal, isNumeric, isMultiValue, sameAs, isModelPredictionFeature, source, order, creator, annotation;

	public String jsonname() {
	    return name();
	}
    }

    enum jsonAnnotation {
	URI, annotation;

	public String jsonname() {
	    return name();
	}
    }

    public PropertyJSONReporter(Request baseRef) {
	super(baseRef);
    }

    public PropertyJSONReporter(Request baseRef, String jsoncallback) {
	this(baseRef);
    }

    @Override
    public Object processItem(Property feature) throws AmbitException {
	try {
	    String uri = getURI(feature);
	    boolean numeric = (feature.getClazz() == Number.class) || (feature.getClazz() == Double.class)
		    || (feature.getClazz() == Float.class) || (feature.getClazz() == Integer.class)
		    || (feature.getClazz() == Long.class);
	    boolean multivalue = feature.getClazz() == MultiValue.class;
	    // tuple
	    // else if (item.getClazz()==Dictionary.class)
	    // feature.addOntClass(OTClass.TupleFeature.getOntClass(jenaModel));

	    // sameas
	    String uriSameAs = feature.getLabel();
	    if (uriSameAs == null)
		uriSameAs = Property.guessLabel(feature.getName());
	    if ((uriSameAs != null) && (uri.indexOf("http://") < 0) && (uri.indexOf("https://") < 0)) {
		uriSameAs = String.format("%s%s", OT.NS, Reference.encode(uriSameAs));
	    }
	    // source, etc
	    String uriSource = feature.getTitle();
	    String typeSource = "Source";
	    boolean isModelPredictionFeature = feature.getTitle().indexOf("/model/") > 0;
	    if (feature instanceof SubstanceProperty) try {
		SubstanceProperty sp = (SubstanceProperty) feature;
		uriSource = String.format("%s/dataset/%s", getBaseReference(), Reference.encode(uriSource));		
		if (sp.getStudyResultType()!=null) {
        	      isModelPredictionFeature = sp.getStudyResultType().isPredicted();
        	      typeSource = sp.getStudyResultType().toString();
		}
	    } catch (Exception x) {}
	    else 
	    if ((uriSource.indexOf("http://") < 0) && (uriSource.indexOf("https://") < 0)) {
		if (_type.Algorithm.equals(feature.getReference().getType())) {
		    uriSource = String.format("%s/algorithm/%s", getBaseReference(), Reference.encode(uriSource));
		    typeSource = "Algorithm";
		} else if (_type.Model.equals(feature.getReference().getType())) {
		    uriSource = String.format("%s/model/%s", getBaseReference(), Reference.encode(uriSource));
		    typeSource = "Model";
		    isModelPredictionFeature = true;
		} else if (_type.Feature.equals(feature.getReference().getType())) {
		    uriSource = String.format("%s/feature/%s", getBaseReference(), Reference.encode(uriSource));
		    typeSource = "Feature";
		} else if (_type.Dataset.equals(feature.getReference().getType())) {
		    uriSource = String.format("%s/dataset/%s", getBaseReference(), Reference.encode(uriSource));
		    typeSource = "Dataset";
		} else {
		    uriSource = String.format("%s/dataset/%s", getBaseReference(), Reference.encode(uriSource));
		    typeSource = "Dataset";
		}
		// feature.addProperty(DC.creator,
		// item.getReference().getURL());
	    }

	    String annotation = null;
	    if (feature.getAnnotations() != null)
		annotation = annotation2JSON(feature);

	    if (comma != null)
		getOutput().write(comma);
	    getOutput().write(
		    String.format("\n\"%s\":{\n" + // uri
			    "\n\t\"type\":\"Feature\"," + // uri
			    "\n\t\"%s\":%s," + // title
			    "\n\t\"%s\":%s," + // units
			    "\n\t\"%s\":%s," + // nominal
			    "\n\t\"%s\":%s," + // numeric
			    "\n\t\"%s\":%s," + // multivalue
			    "\n\t\"%s\":%s," + // sameAs
			    "\n\t\"%s\":%s," + // isModelPredictionFeature
			    "\n\t\"%s\":\"%s\"," + // creator
			    "\n\t\"%s\":%d," + // order
			    "\n\t\"%s\":{\n\t\t\"URI\":%s,\n\t\t\"type\":%s\n\t}," + // source
			    "\n\t\"%s\":[%s]\n\n}", uri, jsonFeature.title.jsonname(),
			    JSONUtils.jsonQuote(JSONUtils.jsonEscape(getAggregatedFeatureName(feature))),
			    jsonFeature.units.jsonname(),
			    JSONUtils.jsonQuote(JSONUtils.jsonEscape(feature.getUnits())),
			    jsonFeature.isNominal.jsonname(), feature.isNominal(), jsonFeature.isNumeric.jsonname(),
			    numeric, jsonFeature.isMultiValue.jsonname(), multivalue, jsonFeature.sameAs.jsonname(),
			    JSONUtils.jsonQuote(JSONUtils.jsonEscape(uriSameAs)),
			    jsonFeature.isModelPredictionFeature.jsonname(), isModelPredictionFeature,
			    jsonFeature.creator.jsonname(), JSONUtils.jsonEscape(feature.getReference().getURL()),
			    jsonFeature.order.jsonname(), feature.getOrder(), jsonFeature.source.jsonname(),
			    JSONUtils.jsonQuote(JSONUtils.jsonEscape(uriSource)),
			    JSONUtils.jsonQuote(JSONUtils.jsonEscape(typeSource)), jsonFeature.annotation.jsonname(),
			    annotation == null ? "" : annotation

		    ));

	    comma = ",";

	} catch (Exception x) {
	    x.printStackTrace();
	}
	return feature;
    }

    private String getAggregatedFeatureName(Property feature) {
	if (feature.getName() == null)
	    return null;
	if (feature.getName().startsWith("http://www.opentox.org/")) {
	    if (Property.opentox_CAS.equals(feature.getName()))
		return Property.CAS;
	    if (Property.opentox_IupacName.equals(feature.getName()))
		return "IUPAC name";
	    if (Property.opentox_Name.equals(feature.getName()))
		return Property.Names;
	    if (Property.opentox_InChI.equals(feature.getName()))
		return "InChI";
	    if (Property.opentox_InChI_std.equals(feature.getName()))
		return "Std. InChI";
	    if (Property.opentox_InChIKey.equals(feature.getName()))
		return "InChI Key";
	    if (Property.opentox_InChIKey_std.equals(feature.getName()))
		return "Std. InChI key";
	    if (Property.opentox_SMILES.equals(feature.getName()))
		return "SMILES";
	    if (Property.opentox_REACHDATE.equals(feature.getName()))
		return "REACH registration date";
	    if (Property.opentox_EC.equals(feature.getName()))
		return "EC number";
	    if (Property.opentox_TradeName.equals(feature.getName()))
		return "Trade Name";

	    /* Other database IDs */
	    if (Property.opentox_Pubchem.equals(feature.getName()))
		return "PUBCHEM ID";
	    if (Property.opentox_ChEBI.equals(feature.getName()))
		return "CHEBI ID";
	    if (Property.opentox_ChEMBL.equals(feature.getName()))
		return "CHEMBL ID";
	    if (Property.opentox_ChemSpider.equals(feature.getName()))
		return "ChemSpider ID";
	    /* IUCLID5 Reference substance UUID */
	    if (Property.opentox_IUCLID5_UUID.equals(feature.getName()))
		return "IUCLID 5 Reference substance UUID";

	    /* Mostly for usage in ToxBank */
	    if (Property.opentox_ToxbankWiki.equals(feature.getName()))
		return "ToxBank Wiki URL";
	    if (Property.opentox_CMS.equals(feature.getName()))
		return "COSMOS ID";
	}
	return feature.getName();
    }

    public static String annotation2JSON(Property feature) {
	StringBuilder b = new StringBuilder();
	String acomma = "";
	for (PropertyAnnotation annotation : feature.getAnnotations())
	    try {
		b.append(acomma);
		b.append(annotation.toJSON());
		acomma = ",";
	    } catch (Exception x) {

		return null;
	    }
	return b.toString();
    }

    public void header(java.io.Writer output, IQueryRetrieval<Property> query) {
	try {
	    output.write("{");
	    output.write("\n\"feature\":{\n");
	} catch (Exception x) {
	}
    };

    @Override
    public void footer(Writer output, IQueryRetrieval<Property> query) {
	try {
	    output.write("\t\n}");
	    output.write("\n}");
	} catch (Exception x) {
	}
    };

}
