package ambit2.rest.substance;

import java.io.StringReader;
import java.util.Iterator;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.Template;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.json.SubstanceStudyParser;
import ambit2.db.processors.MasterDetailsProcessor;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.IQueryCondition;
import ambit2.db.substance.ReadSubstanceByOwner;
import ambit2.db.substance.study.ReadEffectRecordBySubstance;
import ambit2.rest.OutputWriterConvertor;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.StringConvertor;
import ambit2.rest.structure.CompoundJSONReporter;
import ambit2.rest.substance.owner.SubstanceByOwnerResource;

public class SubstanceDatasetResource extends SubstanceByOwnerResource {
	protected Template template;
	protected Profile groupProperties;
	protected String[] folders;
	
	public SubstanceDatasetResource() {
		super();
		setHtmlbyTemplate(true);
		template = new Template();
		groupProperties = new Profile<Property>();
		groupProperties.add(Property.getInstance(Property.opentox_Name,"Substance"));
		groupProperties.add(Property.getTradeNameInstance("Name"));
		groupProperties.add(Property.getI5UUIDInstance());
	}
	
	@Override
	public String getTemplateName() {
		return "_datasetsubstance.ftl";
	}

	public Template getTemplate() {
		return template;
	}
	public void setTemplate(Template template) {
		this.template = (template==null)?new Template(null):template;

	}
	public Profile getGroupProperties() {
		return groupProperties;
	}
	public void setGroupProperties(Profile groupProperties) {
		this.groupProperties = groupProperties;
	}
	
	@Override
	public IProcessor<ReadSubstanceByOwner, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {
		/* workaround for clients not being able to set accept headers */
		Form acceptform = getResourceRef(getRequest()).getQueryAsForm();
		String media = acceptform.getFirstValue("accept-header");
		if (media != null) variant.setMediaType(new MediaType(media));

		String filenamePrefix = getRequest().getResourceRef().getPath();
		if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			QueryURIReporter r = (QueryURIReporter)getURIReporter();
			r.setDelimiter("\n");
			return new StringConvertor(
					r,MediaType.TEXT_URI_LIST,filenamePrefix);
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_JAVASCRIPT)) {
			return createJSONReporter(filenamePrefix);	
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
			return createJSONReporter(filenamePrefix);	
		} else { //json by default
			return createJSONReporter(filenamePrefix);
		}
	}	
	

	
	protected IProcessor<ReadSubstanceByOwner, Representation> createJSONReporter(String filenamePrefix) {
		String jsonpcallback = getParams().getFirstValue("jsonp");
		if (jsonpcallback==null) jsonpcallback = getParams().getFirstValue("callback");
		return new OutputWriterConvertor(
				new CompoundJSONReporter(getTemplate(),getGroupProperties(),folders,getRequest(),
						getDocumentation(),
						getRequest().getRootRef().toString(),false,jsonpcallback) {
					@Override
					protected String getURI(IStructureRecord item) {
						return SubstanceRecord.getURI(urlPrefix, ((SubstanceRecord)item));
					}
					@Override
					protected void configurePropertyProcessors() {
						IQueryRetrieval<EffectRecord<String, String, String>> queryP = new ReadEffectRecordBySubstance(); 
						MasterDetailsProcessor<SubstanceRecord,EffectRecord<String, String, String>,IQueryCondition> effectReader = 
											new MasterDetailsProcessor<SubstanceRecord,EffectRecord<String, String, String>,IQueryCondition>(
													new ReadEffectRecordBySubstance()) {
							@Override
							protected SubstanceRecord processDetail(SubstanceRecord master,
									EffectRecord<String, String, String> detail) throws Exception {
								if (detail != null) {
									if (detail.getTextValue() != null && detail.getTextValue().toString().startsWith("{")) {
										ObjectMapper dx = new ObjectMapper();
										JsonNode node = dx.readTree(new StringReader(detail.getTextValue().toString()));
										
										Iterator<Entry<String,JsonNode>> i = node.getFields();
										while (i.hasNext()) {
											Entry<String,JsonNode> val = i.next();
											Property key = Property.getInstance(val.getKey(),detail.getSampleID());
											key.setUnits(detail.getUnit());
											groupProperties.add(key);
											master.setProperty(key, val.getValue().get("loValue"));
											
										}
									} else {
										Property key = Property.getInstance(detail.getEndpoint(),detail.getSampleID());
										key.setUnits(detail.getUnit());
										
										groupProperties.add(key);
										if (detail.getLoValue() == null)
											master.setProperty(key, detail.getTextValue());
										else
											master.setProperty(key, detail.getLoValue());
									}
								}
								return master;
								
							}
						};
						getProcessors().add(effectReader);
					}
				},
				MediaType.APPLICATION_JSON,filenamePrefix);
	}
}
