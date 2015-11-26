package ambit2.rest.dataset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import net.idea.restnet.c.task.ClientResourceWrapper;
import net.idea.restnet.rdf.ns.OT;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.MDLV2000Format;
import org.openscience.cdk.io.iterator.DefaultIteratingChemObjectReader;
import org.restlet.data.Reference;
import org.restlet.routing.Template;

import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.core.io.IRawReader;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.rdf.RDFObjectIterator;
import ambit2.rest.rdf.RDFPropertyIterator;
import ambit2.rest.structure.CompoundResource;
import ambit2.rest.structure.ConformerResource;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Reading RDF representation of a dataset. This is a copy paste from
 * {@link RDFStructuresReader} TODO refactor to use RDFStructuresIterator
 * 
 * @author nina
 * 
 */
public class RDFIteratingReader extends DefaultIteratingChemObjectReader implements IRawReader<IStructureRecord> {
    protected Template compoundTemplate;
    protected Template conformerTemplate;
    protected Template featureTemplate;
    protected StmtIterator recordIterator;
    protected OntModel jenaModel;
    protected RDFPropertyIterator propertyIterator;

    public OntModel getJenaModel() {
	return jenaModel;
    }

    protected IStructureRecord record;
    protected String rdfFormat;
    protected Hashtable<String, Property> lookup;
    protected String basereference;

    public RDFIteratingReader() {

    }

    public RDFIteratingReader(InputStream in, IChemObjectBuilder builder, String baseReference, String rdfFormat)
	    throws Exception {
	super();
	this.basereference = baseReference;
	this.rdfFormat = rdfFormat;
	setReader(in);

    }

    public void setReader(InputStream in) throws CDKException {
	setReader(new InputStreamReader(in));

    }

    public void setReader(Reader reader) throws CDKException {
	try {
	    jenaModel = OT.createModel();
	    jenaModel.read(reader, null, rdfFormat == null ? "RDF/XML" : rdfFormat);
	    compoundTemplate = new Template(String.format("%s%s", basereference == null ? "" : basereference,
		    CompoundResource.compoundID));
	    conformerTemplate = new Template(String.format("%s%s", basereference == null ? "" : basereference,
		    ConformerResource.conformerID));
	    propertyIterator = createPropertyIterator(jenaModel, basereference);
	} catch (CDKException x) {
	    throw x;
	} catch (Exception x) {
	    throw new CDKException(x.getMessage(), x);
	}

    }

    protected RDFPropertyIterator createPropertyIterator(OntModel jenaModel, String basereference) {
	RDFPropertyIterator pi = new RDFPropertyIterator(jenaModel);
	pi.setBaseReference(new Reference(basereference));
	return pi;
    }

    private StmtIterator initIterator() {
	if (recordIterator == null) {
	    Resource s = OT.OTClass.DataEntry.getOntClass(jenaModel);
	    if (s == null)
		return null;
	    recordIterator = jenaModel.listStatements(new SimpleSelector(null, RDF.type, s));
	}
	return recordIterator;
    }

    public IStructureRecord nextRecord() {
	return record;
    }

    public void close() throws IOException {

    }

    public IResourceFormat getFormat() {
	return MDLV2000Format.getInstance();
    }

    public boolean hasNext() {
	recordIterator = initIterator();
	if ((recordIterator != null) && recordIterator.hasNext()) {
	    Statement st = recordIterator.next();
	    Resource newEntry = (Resource) st.getSubject();
	    try {
		record = parseRecord(newEntry, createRecord());
		return true;
	    } catch (Exception x) {
		return false;
	    }
	} else
	    return false;
    }

    protected IStructureRecord createRecord() {
	if (record == null)
	    return new StructureRecord();
	else
	    record.clear();
	return record;
    }

    public Object next() {
	return record;
    }

    protected IStructureRecord parseRecord(Resource newEntry, IStructureRecord record) throws Exception {
	// get the compound

	StmtIterator compound = jenaModel.listStatements(new SimpleSelector(newEntry, OT.OTProperty.compound
		.createProperty(jenaModel), (RDFNode) null));
	while (compound.hasNext()) {
	    Statement st = compound.next();
	    parseCompoundURI(st.getObject().toString(), record);
	    if (readStructure(st.getObject(), record))
		break;

	}
	// get feature values
	parseFeatureValues(newEntry, record);
	return record;
    }

    protected void parseFeatureValues(Resource dataEntry, IStructureRecord record) throws Exception {
	StmtIterator values = jenaModel.listStatements(new SimpleSelector(dataEntry, OT.OTProperty.values
		.createProperty(jenaModel), (RDFNode) null));

	while (values.hasNext()) {
	    Statement st = values.next();
	    if (st.getObject().isResource()) {
		Resource fv = (Resource) st.getObject();
		Statement property = fv.getProperty(OT.DataProperty.value.createProperty(jenaModel));

		RDFNode value = null;
		if (property != null)
		    value = property.getObject();

		RDFNode feature = fv.getProperty(OT.OTProperty.feature.createProperty(jenaModel)).getObject();

		Property key = null;
		String uri = null;
		if (feature.isURIResource() && (lookup != null)) {
		    uri = ((Resource) feature).getURI();
		    key = lookup.get(uri);
		}

		if (key == null) {
		    key = propertyIterator.parseRecord(feature, key);
		    if (uri != null) {
			if (lookup == null)
			    lookup = new Hashtable<String, Property>();
			lookup.put(uri, key);
		    }
		}

		if (value != null)
		    setFeatureValue(record, key, value);

	    }
	}
    }

    protected void parseCompoundURI(String uri, IStructureRecord record) {
	Map<String, Object> vars = new HashMap<String, Object>();

	String cmpURI = RDFObjectIterator.removeDatasetFragment(uri);
	conformerTemplate.parse(cmpURI, vars);
	try {
	    record.setIdchemical(Integer.parseInt(vars.get(CompoundResource.idcompound).toString()));
	} catch (Exception x) {
	}
	;

	try {
	    record.setIdstructure(Integer.parseInt(vars.get(ConformerResource.idconformer).toString()));
	} catch (Exception x) {
	}
	;

	if (record.getIdchemical() <= 0) {
	    try {
		compoundTemplate.parse(cmpURI, vars);
		record.setIdchemical(Integer.parseInt(vars.get(CompoundResource.idcompound).toString()));
	    } catch (Exception x) {
	    }
	    ;
	}

    }

    public boolean readStructure(RDFNode target, IStructureRecord record) {
	InputStream in = null;
	HttpURLConnection uc = null;
	try {
	    if (target.isURIResource()) {
		uc = ClientResourceWrapper.getHttpURLConnection(((Resource) target).getURI(), "GET",
			ChemicalMediaType.CHEMICAL_MDLSDF.toString());
		HttpURLConnection.setFollowRedirects(true);
		if (HttpURLConnection.HTTP_OK == uc.getResponseCode()) {
		    in = uc.getInputStream();
		    StringBuilder b = new StringBuilder();
		    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		    String line = null;
		    final String newline = System.getProperty("line.separator");
		    while ((line = reader.readLine()) != null) {
			b.append(line);
			b.append(newline);
		    }
		    record.setContent(b.toString());
		    record.setFormat(MOL_TYPE.SDF.toString());
		}

		return true;
	    } else {
		record.setFormat(IStructureRecord.MOL_TYPE.URI.toString());
		record.setContent(target.toString());
		return false;
	    }
	} catch (Exception x) {
	    record.setFormat(IStructureRecord.MOL_TYPE.URI.toString());
	    record.setContent(target.toString());
	    return false;
	} finally {
	    try {
		if (in != null)
		    in.close();
	    } catch (Exception x) {
	    }
	    try {
		if (uc != null)
		    uc.disconnect();
	    } catch (Exception x) {
	    }
	}
    }

    /*
     * public boolean readStructure(RDFNode target,IStructureRecord record) {
     * Representation r = null; ClientResourceWrapper client = null; try { if
     * (target.isURIResource()) {
     * 
     * client = new ClientResourceWrapper(((Resource)target).getURI()); r =
     * client.get(ChemicalMediaType.CHEMICAL_MDLMOL); if
     * (client.getStatus().equals(Status.SUCCESS_OK)) {
     * record.setContent(r.getText());
     * record.setFormat(MOL_TYPE.SDF.toString()); } return true; } else {
     * record.setContent(null); record.setFormat(MOL_TYPE.URI.toString());
     * return false; } } catch (Exception x) {
     * record.setFormat(IStructureRecord.MOL_TYPE.URI.toString());
     * record.setContent(target.toString()); return false; } finally { try { if
     * (client != null) client.release();} catch (Exception x) {} try {
     * r.release();} catch (Exception x) {} }
     * 
     * }
     */

    /*
     * public Property readFeature(final RDFNode target,Property property)
     * throws Exception { RDFPropertyIterator iterator =null; try {
     * if(target.isAnon()) { Property p = new Property("");
     * RDFPropertyIterator.parseRecord(jenaModel, (Resource)target, p, new
     * Reference(basereference)); if (lookup == null) lookup = new
     * Hashtable<String, Property>(); lookup.put(p.getName(), p); return p; }
     * else if (target.isURIResource()) { String uri =
     * ((Resource)target).getURI(); iterator = new RDFPropertyIterator(new
     * Reference(uri)); iterator.setBaseReference(new Reference(basereference));
     * while (iterator.hasNext()) { Property p = iterator.next(); if (lookup ==
     * null) lookup = new Hashtable<String, Property>(); lookup.put(uri, p);
     * return p; }; } } catch (Exception x) { if (target.isAnon()) { throw x; }
     * else { String uri = target.toString(); if (lookup == null) lookup = new
     * Hashtable<String, Property>(); lookup.put(uri, new Property(uri,new
     * LiteratureEntry(uri,uri))); } } finally { try { iterator.close();} catch
     * (Exception x) {} } return lookup.get(target.toString()); }
     */
    protected void setFeatureValue(IStructureRecord record, Property key, RDFNode value) {
	if (value.isLiteral()) {
	    RDFDatatype datatype = ((Literal) value).getDatatype();
	    if (XSDDatatype.XSDdouble.equals(datatype))
		record.setRecordProperty(key, ((Literal) value).getDouble());
	    else if (XSDDatatype.XSDfloat.equals(datatype))
		record.setRecordProperty(key, ((Literal) value).getFloat());
	    else if (XSDDatatype.XSDinteger.equals(datatype))
		record.setRecordProperty(key, ((Literal) value).getInt());
	    else if (XSDDatatype.XSDstring.equals(datatype))
		record.setRecordProperty(key, ((Literal) value).getString());
	    else if (XSDDatatype.XSDboolean.equals(datatype))
		record.setRecordProperty(key, ((Literal) value).getBoolean());
	    else
		record.setRecordProperty(key, ((Literal) value).getString());
	}
    }

    protected Property createFeature(RDFNode feature) {
	Property key = Property.getInstance(feature.toString(), feature.toString());
	key.setClazz(URI.class);
	return key;
    }
}
