package ambit2.rest.substance;

import java.io.StringReader;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryCondition;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.processors.ProcessorsChain;
import net.idea.modbcum.r.QueryAbstractReporter;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.OutputWriterConvertor;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.Template;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.MultiValue;
import ambit2.base.data.study.ProtocolEffectRecord;
import ambit2.base.data.study.Value;
import ambit2.base.data.study.ReliabilityParams._r_flags;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.data.substance.SubstanceName;
import ambit2.base.data.substance.SubstanceOwner;
import ambit2.base.data.substance.SubstanceProperty;
import ambit2.base.data.substance.SubstancePublicName;
import ambit2.base.data.substance.SubstanceUUID;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.json.SubstanceStudyParser;
import ambit2.db.processors.MasterDetailsProcessor;
import ambit2.db.reporters.CSVReporter;
import ambit2.db.reporters.SDFReporter;
import ambit2.db.search.structure.QueryStructureByID;
import ambit2.db.substance.study.ReadEffectRecordBySubstance;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.RDFJenaConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.dataset.ARFF3ColResourceReporter;
import ambit2.rest.dataset.ARFFResourceReporter;
import ambit2.rest.dataset.DatasetRDFReporter;
import ambit2.rest.structure.CompoundJSONReporter;
import ambit2.rest.substance.owner.SubstanceByOwnerResource;

public class SubstanceDatasetResource<Q extends IQueryRetrieval<SubstanceRecord>> extends SubstanceByOwnerResource<Q> {
    protected Template template;
    protected Profile groupProperties;
    protected String[] folders;
    protected ObjectMapper dx = new ObjectMapper();

    public SubstanceDatasetResource() {
	super();
	setHtmlbyTemplate(true);
	template = new Template();
	groupProperties = new Profile<Property>();
    }

    @Override
    protected void doInit() throws ResourceException {
	super.doInit();
	customizeVariants(new MediaType[] { ChemicalMediaType.WEKA_ARFF, MediaType.TEXT_CSV,
		ChemicalMediaType.CHEMICAL_MDLSDF, ChemicalMediaType.THREECOL_ARFF });

    }

    @Override
    public String getTemplateName() {
	return "_datasetsubstance.ftl";
    }

    public Template getTemplate() {
	return template;
    }

    public void setTemplate(Template template) {
	this.template = (template == null) ? new Template(null) : template;

    }

    public Profile getGroupProperties() {
	return groupProperties;
    }

    public void setGroupProperties(Profile groupProperties) {
	this.groupProperties = groupProperties;
    }

    @Override
    public IProcessor<Q, Representation> createConvertor(Variant variant) throws AmbitException, ResourceException {
	/* workaround for clients not being able to set accept headers */
	Form acceptform = getResourceRef(getRequest()).getQueryAsForm();
	String media = acceptform.getFirstValue("accept-header");
	if (media != null)
	    variant.setMediaType(new MediaType(media));

	String filenamePrefix = getRequest().getResourceRef().getPath();
	if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
	    QueryURIReporter r = (QueryURIReporter) getURIReporter(getRequest());
	    return new StringConvertor(r, MediaType.TEXT_URI_LIST, filenamePrefix);
	} else if (variant.getMediaType().equals(MediaType.APPLICATION_JAVASCRIPT)) {
	    return createJSONReporter(filenamePrefix);
	} else if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
	    return createJSONReporter(filenamePrefix);
	} else if (variant.getMediaType().equals(ChemicalMediaType.WEKA_ARFF)) {
	    return createARFFReporter(filenamePrefix);
	} else if (variant.getMediaType().equals(ChemicalMediaType.THREECOL_ARFF)) {
	    QueryAbstractReporter reporter = new ARFF3ColResourceReporter<IQueryRetrieval<IStructureRecord>>(
		    getTemplate(), getGroupProperties(), getRequest(), String.format("%s%s", getRequest().getRootRef(),
			    ""));
	    return new OutputWriterConvertor(reporter, ChemicalMediaType.THREECOL_ARFF, filenamePrefix);

	} else if (variant.getMediaType().equals(MediaType.TEXT_CSV)) {
	    return createCSVReporter(filenamePrefix);
	} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML)) {
	    /*
	     * switch (rdfwriter) { case stax: { return new RDFStaXConvertor(new
	     * DatasetRDFStaxReporter(null, getRequest(), getTemplate(),
	     * getGroupProperties()), filenamePrefix); } default: { // jena
	     */
	    return createRDFReporter(variant.getMediaType(), filenamePrefix);
	    /*
	     * } }
	     */
	} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE)
		|| variant.getMediaType().equals(MediaType.TEXT_RDF_N3)
		|| variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES)
		|| variant.getMediaType().equals(MediaType.APPLICATION_RDF_TRIG)
		|| variant.getMediaType().equals(MediaType.APPLICATION_RDF_TRIX)) {
	    return createRDFReporter(variant.getMediaType(), filenamePrefix);
	} else if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_MDLSDF)) {
	    return new OutputWriterConvertor(new SDFReporter<QueryStructureByID>(template, getGroupProperties(),
		    changeLineSeparators), ChemicalMediaType.CHEMICAL_MDLSDF, filenamePrefix);
	} else { // json by default
	    return createJSONReporter(filenamePrefix);
	}
    }

    protected IQueryRetrieval<ProtocolEffectRecord<String, String, String>> getEffectQuery() {
	return new ReadEffectRecordBySubstance();
    }

    protected void getCompositionProcessors(ProcessorsChain chain) {

    }

    protected NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);

    protected IProcessor getPropertyProcessors(final boolean removeIdentifiers, final boolean removeStringProperties) {
	IQueryRetrieval<ProtocolEffectRecord<String, String, String>> queryP = getEffectQuery();

	MasterDetailsProcessor<SubstanceRecord, ProtocolEffectRecord<String, String, String>, IQueryCondition> effectReader = new MasterDetailsProcessor<SubstanceRecord, ProtocolEffectRecord<String, String, String>, IQueryCondition>(
		queryP) {
	    /**
		     * 
		     */
	    private static final long serialVersionUID = -7354966336095750101L;
	    ProtocolEffectRecord2SubstanceProperty processor = new ProtocolEffectRecord2SubstanceProperty();

	    @Override
	    protected SubstanceRecord processDetail(SubstanceRecord master,
		    ProtocolEffectRecord<String, String, String> detail) throws Exception {

		if (detail != null) {
		    if (detail.getTextValue() != null && detail.getTextValue().toString().startsWith("{")) {

			JsonNode node = dx.readTree(new StringReader(detail.getTextValue().toString()));

			List<String> guideline = detail.getProtocol().getGuideline();
			ILiteratureEntry ref = LiteratureEntry.getInstance(guideline == null ? null
				: guideline.size() == 0 ? null : guideline.get(0),
				guideline == null ? null : guideline.size() == 0 ? null : guideline.get(0));

			Iterator<Entry<String, JsonNode>> i = node.getFields();
			while (i.hasNext()) {
			    Entry<String, JsonNode> val = i.next();

			    SubstanceProperty key = new SubstanceProperty(detail.getProtocol().getTopCategory(), detail
				    .getProtocol().getCategory(), val.getKey(), detail.getUnit(), ref);
			    try {
				key.setStudyResultType(_r_flags.valueOf(detail.getStudyResultType().replace(":", "")
					.replace("_", "").replace(" ", "").replace("-", "").replace(")", "")
					.replace("(", "")));
			    } catch (Exception x) {
				key.setStudyResultType(null);
			    }
			    key.setExtendedURI(true);
			    key.setIdentifier(detail.getSampleID() + "/" + val.getKey());

			    groupProperties.add(key);
			    if (val.getValue().get(EffectRecord._fields.loValue.name()) != null) {
				master.setProperty(key, val.getValue().get(EffectRecord._fields.loValue.name()).asInt());
				key.setClazz(Number.class);
			    } else {
				master.setProperty(key, val.getValue().getTextValue());
				key.setClazz(String.class);
			    }
			}
		    } else {
			boolean isTextValue = ((detail.getLoValue() == null) && (detail.getUpValue() == null));
			if (isTextValue && removeStringProperties)
			    return master;
			/*
			 * JsonNode conditions = detail.getConditions() == null
			 * ? null : dx.readTree(new StringReader(
			 * detail.getConditions()));
			 * 
			 * PropertyAnnotations ann = new PropertyAnnotations();
			 * 
			 * Iterator<Entry<String, JsonNode>> i = conditions ==
			 * null ? null : conditions.getFields();
			 * 
			 * if (i != null) while (i.hasNext()) { Entry<String,
			 * JsonNode> val = i.next(); if (val.getValue()
			 * instanceof NullNode) continue;
			 * 
			 * if (val.getValue().getTextValue() == null) try {
			 * PropertyAnnotation a = new PropertyAnnotation();
			 * String unit =
			 * val.getValue().get(EffectRecord._fields.unit.name())
			 * == null ? null :
			 * val.getValue().get(EffectRecord._fields
			 * .unit.name()).asText(); a.setPredicate(val.getKey());
			 * if (unit == null)
			 * a.setObject(val.getValue().get(EffectRecord
			 * ._fields.loValue.name()) .asText()); else {
			 * a.setObject(String.format("%s %s",
			 * val.getValue().get(
			 * EffectRecord._fields.loValue.name()).asText(),
			 * unit)); } ann.add(a); } catch (Exception x) { } else
			 * { PropertyAnnotation a = new PropertyAnnotation();
			 * a.setPredicate(val.getKey());
			 * a.setObject(val.getValue().getTextValue());
			 * ann.add(a); }
			 * 
			 * }
			 */
			ProtocolEffectRecord<String, IParams, String> effect = new ProtocolEffectRecord<String, IParams, String>();
			effect.setStudyResultType(detail.getStudyResultType());
			effect.setProtocol(detail.getProtocol());
			if (detail.getEndpoint() != null)
			    effect.setEndpoint(detail.getEndpoint());
			if (detail.getUnit() != null)
			    effect.setUnit(detail.getUnit());
			if (detail.getLoValue() != null)
			    effect.setLoValue(detail.getLoValue());
			if (detail.getUpValue() != null)
			    effect.setUpValue(detail.getUpValue());
			if (detail.getLoQualifier() != null)
			    effect.setLoQualifier(detail.getLoQualifier());
			if (detail.getUpQualifier() != null)
			    effect.setUpQualifier(detail.getUpQualifier());
			if (detail.getTextValue() != null)
			    effect.setTextValue(detail.getTextValue());
			if (detail.getErrorValue() != null)
			    effect.setErrorValue(detail.getErrorValue());
			try {
			    JsonNode conditions = detail.getConditions() == null ? null : dx.readTree(new StringReader(
				    detail.getConditions()));
			    if (conditions instanceof ObjectNode) {
				effect.setConditions(SubstanceStudyParser.parseParams((ObjectNode) conditions));
			    }
			} catch (Exception x) {
			    logger.log(Level.FINE, x.getMessage());
			}
			SubstanceProperty key = processor.process(effect);
			key.setIdentifier(key.createHashedIdentifier(effect.getConditions()));
			Object oldValue = master.getProperty(key);
			groupProperties.add(key);
			/*
			 * if (isTextValue) { //textvalue if (oldValue == null)
			 * master.setProperty(key, detail.getTextValue()); else
			 * { master.setProperty(key, String.format( "%s, %s",
			 * oldValue instanceof Number ? nf.format((Number)
			 * oldValue) : oldValue .toString(),
			 * detail.getTextValue())); }
			 * key.setClazz(String.class); } else { //numeric
			 */
			Value value = processValue(detail, isTextValue);
			if (value != null) {
			    if (oldValue == null) {
				master.setProperty(key, new MultiValue<Value>(value));
			    } else if (oldValue instanceof MultiValue) {
				((MultiValue) oldValue).add(value);
			    } else {
				logger.log(Level.WARNING, oldValue.getClass().getName());
				master.setProperty(key, new MultiValue<Value>(value));
			    }
			}

			key.setClazz(MultiValue.class);
			// }
		    }
		}
		return master;

	    }
	};
	return effectReader;
    }

    protected Value processValue(ProtocolEffectRecord<String, String, String> detail, boolean istextvalue) {
	if (istextvalue) {
	    Value<String> value = new Value<String>();
	    if (detail.getTextValue() == null)
		if (detail.getInterpretationResult() == null)
		    value.setLoValue("");
		else
		    value.setLoValue(detail.getInterpretationResult());
	    else
		value.setLoValue(detail.getTextValue().toString());
	    return value;

	} else {
	    Value value = new Value();
	    value.setLoQualifier(detail.getLoQualifier());
	    value.setUpQualifier(detail.getUpQualifier());
	    value.setUpValue(detail.getUpValue());
	    value.setLoValue(detail.getLoValue());
	    return value;

	}

    }

    protected IProcessor<Q, Representation> createARFFReporter(String filenamePrefix) {

	return new OutputWriterConvertor<SubstanceRecord, Q>(new ARFFResourceReporter(getTemplate(),
		getGroupProperties(), getRequest(), String.format("%s%s", getRequest().getRootRef(), "")) {
	    /**
		     * 
		     */
	    private static final long serialVersionUID = -2563353206914543953L;

	    @Override
	    protected void configurePropertyProcessors() {
		getProcessors().add(getPropertyProcessors(true, true));
	    }

	}, ChemicalMediaType.WEKA_ARFF, filenamePrefix);

    }

    protected IProcessor<Q, Representation> createARFF3ColumnReporter(String filenamePrefix) {
	return new OutputWriterConvertor<SubstanceRecord, Q>(new ARFF3ColResourceReporter(getTemplate(),
		getGroupProperties(), getRequest(), String.format("%s%s", getRequest().getRootRef(), "")) {
	    /**
		     * 
		     */
	    private static final long serialVersionUID = 6838109982780717749L;

	    @Override
	    protected void configurePropertyProcessors() {
		getProcessors().add(getPropertyProcessors(true, false));
	    }

	}, ChemicalMediaType.THREECOL_ARFF, filenamePrefix);

    }

    protected IProcessor<Q, Representation> createCSVReporter(String filenamePrefix) {
	groupProperties.add(new SubstancePublicName());
	groupProperties.add(new SubstanceName());
	groupProperties.add(new SubstanceUUID());
	groupProperties.add(new SubstanceOwner());
	return new OutputWriterConvertor<SubstanceRecord, Q>(new CSVReporter(getRequest().getRootRef().toString(),
		getTemplate(), groupProperties, String.format("%s%s", getRequest().getRootRef(), "")) {
	    private static final long serialVersionUID = -2558990024073170008L;

	    @Override
	    protected void configurePropertyProcessors() {
		getProcessors().add(getPropertyProcessors(false, false));
	    }

	}, MediaType.TEXT_CSV, filenamePrefix);

    }

    protected SubstanceEndpointsBundle[] getBundles() {
	return null;
    }

    protected IProcessor<Q, Representation> createRDFReporter(MediaType media, String filenamePrefix) {
	groupProperties.add(new SubstancePublicName());
	groupProperties.add(new SubstanceName());
	groupProperties.add(new SubstanceUUID());
	groupProperties.add(new SubstanceOwner());
	DatasetRDFReporter reporter = new DatasetRDFReporter(getRequest(), media, getTemplate(), getGroupProperties()) {
	    @Override
	    protected boolean acceptProperty(Property p) {
		return true;
	    }

	    @Override
	    protected void configurePropertyProcessors() {
		getProcessors().add(getPropertyProcessors(false, false));
	    }
	};
	return new RDFJenaConvertor(reporter, media, filenamePrefix);

    }

    protected IProcessor<Q, Representation> createJSONReporter(String filenamePrefix) {
	groupProperties.add(new SubstancePublicName());
	groupProperties.add(new SubstanceName());
	groupProperties.add(new SubstanceUUID());
	groupProperties.add(new SubstanceOwner());
	String jsonpcallback = getParams().getFirstValue("jsonp");
	if (jsonpcallback == null)
	    jsonpcallback = getParams().getFirstValue("callback");
	return new OutputWriterConvertor(new CompoundJSONReporter(getTemplate(), getGroupProperties(), folders,
		getBundles(), getRequest(), getRequest().getRootRef().toString(), false, jsonpcallback) {
	    private static final long serialVersionUID = -5059577943753305935L;

	    @Override
	    protected String getURI(IStructureRecord item) {
		if (item instanceof SubstanceRecord)
		    return SubstanceRecord.getURI(urlPrefix, ((SubstanceRecord) item));
		else
		    return super.getURI(item);
	    }

	    @Override
	    protected void configurePropertyProcessors() {
		getCompositionProcessors(getProcessors());
		getProcessors().add(getPropertyProcessors(false, false));
	    }

	    @Override
	    protected void append2header(Writer writer, IStructureRecord item) {
		if (header == null)
		    return;
		for (Property p : item.getProperties()) {
		    if (header.indexOf(p) < 0)
			header.add(p);
		}
	    }
	}, MediaType.APPLICATION_JSON, filenamePrefix);
    }
}
