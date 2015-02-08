package ambit2.rest.dataset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;
import net.idea.modbcum.p.batch.AbstractBatchProcessor;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.rdf.ns.OT.OTProperty;

import org.opentox.rdf.OT;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.MediaType;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;
import ambit2.db.DbReader;
import ambit2.db.DbReaderStructure;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.RetrieveGroupedValuesByAlias;
import ambit2.db.readers.RetrieveProfileValues;
import ambit2.db.readers.RetrieveProfileValues.SearchMode;
import ambit2.rest.OpenTox;
import ambit2.rest.QueryRDFReporter;
import ambit2.rest.dataEntry.DataEntryURIReporter;
import ambit2.rest.property.PropertyRDFReporter;
import ambit2.rest.structure.CompoundURIReporter;
import ambit2.rest.structure.ConformerURIReporter;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * RDF/XML
 * 
 * @author nina
 * 
 * @param <Q>
 */
public class DatasetRDFReporter<Q extends IQueryRetrieval<IStructureRecord>> extends
	QueryRDFReporter<IStructureRecord, Q> {

    /**
	 * 
	 */
    private static final long serialVersionUID = -6410553622662161903L;
    protected PropertyRDFReporter propertyReporter;
    protected CompoundURIReporter<IQueryRetrieval<IStructureRecord>> compoundReporter;
    protected DataEntryURIReporter<IQueryRetrieval<IStructureRecord>> dataEntryReporter;
    protected Comparator<Property> comp;
    protected com.hp.hpl.jena.rdf.model.Property acceptValue;

    protected Profile groupProperties;

    public Profile getGroupProperties() {
	return groupProperties;
    }

    public void setGroupProperties(Profile gp) {
	this.groupProperties = gp;
    }

    protected Template template;
    protected List<Property> header = null;

    public Template getTemplate() {
	return template;
    }

    public void setTemplate(Template template) {
	this.template = template;
    }

    protected Resource dataset;

    public DatasetRDFReporter(Request request, MediaType mediaType, Template template,
	    Profile groupedProperties) {
	this("", request,  mediaType, template, groupedProperties);
    }

    public DatasetRDFReporter(String prefix, Request request,  MediaType mediaType, Template template,
	    Profile groupedProperties) {
	super(prefix, request, mediaType);
	setGroupProperties(groupedProperties);
	setTemplate(template == null ? new Template(null) : template);
	initProcessors();
	propertyReporter = new PropertyRDFReporter(request, mediaType);

	comp = new Comparator<Property>() {
	    public int compare(Property o1, Property o2) {
		return o1.getId() - o2.getId();
	    }
	};

    }

    @Override
    protected QueryURIReporter<IStructureRecord, IQueryRetrieval<IStructureRecord>> createURIReporter(Request req,
	    ResourceDoc doc) {
	try {
	    compoundReporter = new CompoundURIReporter<IQueryRetrieval<IStructureRecord>>(compoundInDatasetPrefix, req,
		    false);
	    dataEntryReporter = new DataEntryURIReporter<IQueryRetrieval<IStructureRecord>>(req);
	    return new ConformerURIReporter<IQueryRetrieval<IStructureRecord>>(compoundInDatasetPrefix, req);
	} catch (Exception x) {
	    logger.log(Level.WARNING, x.getMessage(), x);
	    return null;
	}
    }

    protected void initProcessors() {

	getProcessors().clear();
	if ((getGroupProperties() != null) && (getGroupProperties().size() > 0))
	    getProcessors().add(
		    new ProcessorStructureRetrieval(new RetrieveGroupedValuesByAlias(getGroupProperties())) {
			/**
			     * 
			     */
			private static final long serialVersionUID = 2350564410343233839L;

			@Override
			public IStructureRecord process(IStructureRecord target) throws AmbitException {
			    ((RetrieveGroupedValuesByAlias) getQuery()).setRecord(target);
			    return super.process(target);
			}
		    });

	if ((getTemplate() != null) && (getTemplate().size() > 0))
	    getProcessors().add(
		    new ProcessorStructureRetrieval(new RetrieveProfileValues(SearchMode.idproperty, getTemplate(),
			    true)) {
			/**
			     * 
			     */
			private static final long serialVersionUID = 8265299610363766873L;

			@Override
			public IStructureRecord process(IStructureRecord target) throws AmbitException {
			    ((RetrieveProfileValues) getQuery()).setRecord(target);
			    return super.process(target);
			}
		    });

	getProcessors().add(new DefaultAmbitProcessor<IStructureRecord, IStructureRecord>() {
	    /**
		     * 
		     */
	    private static final long serialVersionUID = 134099514248741144L;

	    public IStructureRecord process(IStructureRecord target) throws AmbitException {
		processItem(target);
		return target;
	    };
	});
    }

    @Override
    public void header(OntModel output, Q query) {
	super.header(output, query);
	OT.OTClass.Dataset.createOntClass(output);
	OT.OTClass.DataEntry.createOntClass(output);
	OT.OTClass.Feature.createOntClass(output);
	OT.OTClass.FeatureValue.createOntClass(output);
	OT.OTClass.Compound.createOntClass(output);

	output.createAnnotationProperty(DC.title.getURI());
	output.createAnnotationProperty(DC.description.getURI());
	// output.createAnnotationProperty(DC.identifier.getURI());
	output.createAnnotationProperty(DC.type.getURI());

	dataset = null;
	acceptValue = OTProperty.acceptValue.createProperty(getJenaModel());

	try {
	    propertyReporter.setOutput(getJenaModel());
	} catch (Exception x) {
	    logger.log(Level.WARNING, x.getMessage(), x);
	}

	if (header == null)
	    header = template2Header(template, true);

	for (ambit2.base.data.Property p : header)
	    try {
		propertyReporter.processItem(p);
	    } catch (Exception x) {
	    }
    }

    protected void createDatasetIndividual(int datasetID) {
	if (uriReporter.getResourceRef().getQueryAsForm().getFirstValue(OpenTox.params.feature_uris.toString()) != null) {
	    dataset = output.createIndividual(OT.OTClass.Dataset.getOntClass(output));
	} else {
	    if (datasetID <= 0)
		dataset = output.createIndividual(String.format("%s:%s", uriReporter.getResourceRef().getScheme(),
			uriReporter.getResourceRef().getHierarchicalPart()), OT.OTClass.Dataset.getOntClass(output));
	    else
		dataset = output.createIndividual(String.format("%s/%s/%d", uriReporter.getBaseReference(),
			OpenTox.URI.dataset.name(), datasetID), OT.OTClass.Dataset.getOntClass(output));
	}

	if (getLicenseURI() != null) {

	    com.hp.hpl.jena.rdf.model.Property rights = DCTerms.rights;
	    for (ISourceDataset.license l : ISourceDataset.license.values())
		if (l.getURI().equals(getLicenseURI())) {
		    rights = DCTerms.license;
		    break;
		}
	    Resource licenseNode = output.createResource(getLicenseURI());
	    dataset.addProperty(rights, licenseNode);
	} else {
	    // dataset.addProperty(DCTerms.license,ISourceDataset.license.Unknown.toString());
	}
    }

    @Override
    public Object processItem(IStructureRecord item) throws AmbitException {
	try {
	    if (dataset == null)
		createDatasetIndividual(item.getDatasetID());

	    if (header == null)
		header = template2Header(template, true);

	    boolean sort = false;
	    for (Property p : item.getProperties())
		if (p.getId() <= 0)
		    continue;
		else if (Collections.binarySearch(header, p, comp) < 0) {
		    header.add(p);
		    sort = true;
		}
	    if (sort)
		Collections.sort(header, comp);

	    Individual dataEntry;
	    if (item.getDataEntryID() > 0) {
		dataEntry = getJenaModel().createIndividual(dataEntryReporter.getURI(item),
			OT.OTClass.DataEntry.getOntClass(getJenaModel()));
	    } else
		dataEntry = getJenaModel().createIndividual(OT.OTClass.DataEntry.getOntClass(getJenaModel()));

	    dataset.addProperty(OT.OTProperty.dataEntry.createProperty(getJenaModel()), dataEntry);
	    int i = 0;

	    String uri = item.getType().equals(STRUC_TYPE.NA) || item.getType().equals(STRUC_TYPE.NANO) ? compoundReporter
		    .getURI(item) : uriReporter.getURI(item);
	    Individual compound = getJenaModel().createIndividual(uri, OT.OTClass.Compound.getOntClass(getJenaModel()));

	    if (item.getType().equals(STRUC_TYPE.NANO)) {
		compound.addOntClass(OT.OTClass.OTMaterial.getOntClass(getJenaModel()));
	    }
	    // compound.addProperty(DC.identifier, uriReporter.getURI(item));
	    dataEntry.addProperty(OT.OTProperty.compound.createProperty(getJenaModel()), compound);

	    // if (item.getIdstructure()>0)
	    // writer.write(String.format("/conformer/%d",item.getIdstructure()));

	    for (ambit2.base.data.Property p : header) {
		Object value = item.getProperty(p);
		if (value == null)
		    continue;

		p.setClazz((value instanceof Number) ? Double.class : String.class);
		Individual feature = (Individual) propertyReporter.processItem(p);

		Individual featureValue = getJenaModel().createIndividual(
			OT.OTClass.FeatureValue.getOntClass(getJenaModel()));
		featureValue.addProperty(OT.OTProperty.feature.createProperty(getJenaModel()), feature);

		Literal lValue = null;
		i++;
		dataEntry.addProperty(OT.OTProperty.values.createProperty(getJenaModel()), featureValue);
		// TODO this is way too inefficient
		if (p.isNominal()) {
		    boolean add = true;
		    StmtIterator values = getJenaModel().listStatements(
			    new SimpleSelector(feature, acceptValue, (RDFNode) null));
		    while (values.hasNext()) {
			Statement st = values.next();
			if (st.getObject().isLiteral()
				&& value.toString().equals(((Literal) st.getObject()).getString())) {
			    add = false;
			    lValue = (Literal) st.getObject();
			    break;
			}
		    }
		    values.close();
		    if (add) {
			lValue = getJenaModel().createTypedLiteral(value,
				(value instanceof Number) ? XSDDatatype.XSDdouble : XSDDatatype.XSDstring);
			feature.addProperty(acceptValue, value.toString());
		    }
		} else {
		    lValue = getJenaModel().createTypedLiteral(value,
			    (value instanceof Number) ? XSDDatatype.XSDdouble : XSDDatatype.XSDstring);
		}

		featureValue.addLiteral(OT.DataProperty.value.createProperty(getJenaModel()), lValue);

	    }
	    return dataEntry;
	} catch (Exception x) {
	    Context.getCurrentLogger().severe(x.getMessage());
	}
	return null;

    }

    public void open() throws DbAmbitException {

    }

    protected List<Property> template2Header(Template template, boolean propertiesOnly) {
	List<Property> h = new ArrayList<Property>();
	Iterator<Property> it;
	if (groupProperties != null) {
	    it = groupProperties.getProperties(true);
	    while (it.hasNext()) {
		Property t = it.next();
		h.add(t);
	    }
	}

	it = template.getProperties(true);
	while (it.hasNext()) {
	    Property t = it.next();
	    if (!propertiesOnly || (propertiesOnly && (t.getId() > 0)))
		h.add(t);
	}
	/*
	 * Collections.sort(h,new Comparator<Property>() { public int
	 * compare(Property o1, Property o2) { return
	 * o1.getOrder()-o2.getOrder(); } });
	 */
	Collections.sort(h, comp);
	return h;
    }

    @Override
    protected AbstractBatchProcessor<IQueryRetrieval<IStructureRecord>, IStructureRecord> createBatch(Q query) {
	if (query.isPrescreen()) {
	    DbReader<IStructureRecord> reader = new DbReaderStructure();
	    reader.setHandlePrescreen(true);
	    return reader;
	} else
	    return super.createBatch(query);
    }

    @Override
    public String getFileExtension() {
	if (MediaType.APPLICATION_RDF_XML.equals(mediaType))
	    return "rdf";
	else if (MediaType.TEXT_RDF_N3.equals(mediaType))
	    return "n3";
	return "rdf";
    }
}
