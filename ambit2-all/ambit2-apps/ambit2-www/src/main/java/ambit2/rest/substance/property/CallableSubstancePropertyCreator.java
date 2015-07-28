package ambit2.rest.substance.property;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;

import net.idea.restnet.c.task.CallableProtectedTask;
import net.idea.restnet.i.task.TaskResult;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Params;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolEffectRecord;
import ambit2.base.data.substance.SubstanceProperty;
import ambit2.core.io.json.SubstanceStudyParser;
import ambit2.core.io.study.ProtocolEffectRecord2SubstanceProperty;
import ambit2.rest.property.PropertyURIReporter;
import ambit2.rest.rdf.RDFPropertyIterator;

public class CallableSubstancePropertyCreator<USERID> extends CallableProtectedTask<USERID> {
    protected SubstanceProperty property;
    protected File file = null;
    protected MediaType mediaType = null;
    protected PropertyURIReporter reporter;
    protected ProtocolEffectRecord2SubstanceProperty processor = new ProtocolEffectRecord2SubstanceProperty();
    protected ObjectMapper mapper = new ObjectMapper();

    public CallableSubstancePropertyCreator(PropertyURIReporter reporter, Method method, Form form, Property item,
	    USERID token) throws ResourceException {
	super(token);
	this.reporter = reporter;
	property = parseForm(form);
    }

    /**
     * <pre>
     * @prefix ot:      <http://www.opentox.org/api/1.1#> .
     * @prefix dc:      <http://purl.org/dc/elements/1.1/> .
     * @prefix :        <https://apps.ideaconsult.net/enmtest/> .
     * @prefix ota:     <http://www.opentox.org/algorithmTypes.owl#> .
     * @prefix otee:    <http://www.opentox.org/echaEndpoints.owl#> .
     * @prefix bx:      <http://purl.org/net/nknouf/ns/bibtex#> .
     * @prefix dcterms:  <http://purl.org/dc/terms/> .
     * @prefix rdfs:    <http://www.w3.org/2000/01/rdf-schema#> .
     * @prefix am:      <https://apps.ideaconsult.net/enmtest/model/> .
     * @prefix ac:      <https://apps.ideaconsult.net/enmtest/compound/> .
     * @prefix owl:     <http://www.w3.org/2002/07/owl#> .
     * @prefix xsd:     <http://www.w3.org/2001/XMLSchema#> .
     * @prefix ad:      <https://apps.ideaconsult.net/enmtest/dataset/> .
     * @prefix rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
     * @prefix ag:      <https://apps.ideaconsult.net/enmtest/algorithm/> .
     * @prefix af:      <https://apps.ideaconsult.net/enmtest/feature/> .
     * 
     * ot:hasSource
     *       a       owl:ObjectProperty .
     * 
     * ot:units
     *       a       owl:DatatypeProperty .
     * 
     * []    a       ot:Feature ;
     *       dc:creator "OECD Guideline 423 (Acute Oral toxicity - Acute Toxic Class Method)" ;
     *       dc:title "LDLo" ;
     *       ot:hasSource "OECD Guideline 423 (Acute Oral toxicity - Acute Toxic Class Method)" ;
     *       ot:units "mg/kg bw" ;
     *       =       otee:TO_ACUTE_ORAL .
     * 
     * ot:Feature
     *       a       owl:Class .
     * 
     * ot:ToxicityCategory
     *       rdfs:subClassOf xsd:string .
     * </pre>
     * 
     * <pre>
     * "https://apps.ideaconsult.net/enmtest/property/P-CHEM/ZETA_POTENTIAL_SECTION/ZETA+POTENTIAL/657379CCBB437E98F8A200C38E526FA890887723/d41d8cd9-8f00-3204-a980-0998ecf8427e":{
     * 
     * 	"type":"Feature",
     * 	"title":"ZETA POTENTIAL",
     * 	"units":"58289",
     * 	"isNominal":false,
     * 	"isNumeric":false,
     * 	"isMultiValue":true,
     * 	"sameAs":"http://www.opentox.org/echaEndpoints.owl#ZETA_POTENTIAL",
     * 	"isModelPredictionFeature":false,
     * 	"creator":"",
     * 	"order":1013,
     * 	"source":{
     * 		"URI":"https://apps.ideaconsult.net/enmtest/dataset/",
     * 		"type":"Dataset"
     * 	},
     * 	"annotation":[
     * 	{	"p" : "STD_DEV",	"o" : "6.5 "},
     * 	{	"p" : "pH",	"o" : "4.26"}]
     * 
     * }
     * </pre>
     * 
     * @param reporter
     * @param method
     * @param file
     * @param mediaType
     * @param item
     * @param token
     * @throws ResourceException
     */
    public CallableSubstancePropertyCreator(PropertyURIReporter reporter, Method method, File file,
	    MediaType mediaType, Property item, USERID token) throws ResourceException {
	super(token);
	this.reporter = reporter;
	this.file = file;
	this.mediaType = mediaType;
    }

    private final static String _param_protocol = "protocol";
    private final static String _param_endpointcategory = "endpointcategory";
    private final static String _param_name = "name";
    private final static String _param_units = "unit";
    private final static String _param_conditions = "conditions";

    protected SubstanceProperty parseForm(Form form) throws ResourceException {
	String topCategory = null;
	ProtocolEffectRecord<String, IParams, String> detail = new ProtocolEffectRecord<String, IParams, String>();

	String endpointcategory = form.getFirstValue(_param_endpointcategory);
	try {
	    topCategory = Protocol._categories.valueOf(endpointcategory.trim()).getTopCategory();
	} catch (Exception x) {
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Unsupported endpoint category");
	}
	detail.setEndpoint(form.getFirstValue(_param_name));
	if (detail.getEndpoint() == null)
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Unsupported name");
	detail.setUnit(form.getFirstValue(_param_units));

	try {
	    String p_conditions = form.getFirstValue(_param_conditions);
	    if (p_conditions != null) {
		JsonNode conditions = mapper.readTree(new StringReader(p_conditions));
		if (conditions instanceof ObjectNode) {
		    detail.setConditions(SubstanceStudyParser.parseParams((ObjectNode) conditions));
		}
	    }
	} catch (Exception x) {
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "conditions: unexpected format");
	}

	String guideline = form.getFirstValue(_param_protocol);
	if (guideline == null)
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "protocol: undefined");
	Protocol protocol = new Protocol(guideline);
	// protocol.addGuideline("Method: other: see below");
	protocol.setCategory(endpointcategory);
	protocol.setTopCategory(topCategory);
	detail.setProtocol(protocol);
	try {
	    SubstanceProperty p = processor.process(detail);
	    p.setIdentifier(p.createHashedIdentifier(detail.getConditions()));
	    return p;
	} catch (ResourceException x) {
	    throw x;
	} catch (Exception x) {
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, x.getMessage(), x);
	}
    }

    @Override
    public TaskResult doCall() throws Exception {
	if (file != null) {
	    //TODO RDF iterator ignores property annotations, which will affect the URI generated
	    RDFPropertyIterator iterator = null;
	    FileInputStream in = null;
	    try {
		in = new FileInputStream(file);
		iterator = new RDFPropertyIterator(in, mediaType) {
		    @Override
		    protected Property createRecord() {
			SubstanceProperty p = new SubstanceProperty(null,null,null,null);
			p.setExtendedURI(true);
			return p;
		    }
		};
		iterator.setBaseReference(reporter.getBaseReference());
		iterator.setCloseModel(true);
		Property p = null;
		while (iterator.hasNext()) {
		    p = iterator.next();
		    break;
		}
		try {
			String category = p.getLabel().replace("http://www.opentox.org/echaEndpoints.owl#", "");
			if (!category.contains("_SECTION")) category = category + "_SECTION";
		    Protocol._categories c = Protocol._categories.valueOf(category);
		    ((SubstanceProperty)p).setTopcategory(c.getTopCategory());
		    ((SubstanceProperty)p).setEndpointcategory(c.name());
		} catch (Exception x) {
		    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Unsupported endpoint category");
		}
		IParams conditions = new Params();
		
		((SubstanceProperty)p).setIdentifier(((SubstanceProperty)p).createHashedIdentifier(conditions));
		return new TaskResult(reporter.getURI(p));
	    } catch (ResourceException x) {
		throw x;
	    } catch (Exception x) {
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, x.getMessage(), x);
	    } finally {
		try {
		    if (in != null)
			in.close();
		} catch (Exception x) {
		}
		try {
		    if (iterator != null)
			iterator.close();
		} catch (Exception x) {
		}
	    }
	}
	return new TaskResult(reporter.getURI(property));
    }

}
