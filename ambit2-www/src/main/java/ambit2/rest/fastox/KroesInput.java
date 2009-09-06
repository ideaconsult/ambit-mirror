package ambit2.rest.fastox;

import java.io.Writer;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.PropertyValue;
import ambit2.db.readers.RetrieveFieldPropertyValue;
import ambit2.rest.DocumentConvertor;
import ambit2.rest.OutputStreamConvertor;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.StatusException;
import ambit2.rest.StringConvertor;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.propertyvalue.FeatureResource;
import ambit2.rest.propertyvalue.PropertyValueHTMLReporter;
import ambit2.rest.propertyvalue.PropertyValueReporter;
import ambit2.rest.propertyvalue.PropertyValueXMLReporter;
import ambit2.rest.structure.CompoundResource;

public class KroesInput extends FeatureResource {
	protected IStructureRecord record = new StructureRecord();
	public IStructureRecord getRecord() {
		return record;
	}

	public void setRecord(IStructureRecord record) {
		this.record = record;
	}

	public KroesInput(Context context, Request request, Response response) {
		super(context, request, response);
		
	}

	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		if (variant.getMediaType().equals(MediaType.TEXT_PLAIN)) {
	
		return new StringConvertor(new PropertyValueReporter());
		} else if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
			return new DocumentConvertor(new PropertyValueXMLReporter(getRequest().getRootRef()));
			
		} else if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			return new OutputStreamConvertor(
					new PropertyValueHTMLReporter<PropertyValue>(getRequest().getRootRef(),true) {
						protected int count = 0;
						@Override
						public void processItem(PropertyValue item,
								Writer output) {
							super.processItem(item, output);
							try {
								count++;
								StringBuilder b = new StringBuilder();
								b.append("<div class=\"actions\"><span class=\"right\">");
								b.append(String.format("<form action=\"%s/ttc/step2\" method=\"POST\">",
										getUriReporter().getBaseReference()
										));
								
								b.append(String.format("<input name=\"idmodel\" value=\"%d\" type=\"hidden\">",-1));
								b.append(String.format("<input name=\"idstructure\" value=\"%d\" type=\"hidden\">",record.getIdstructure()));
								b.append(String.format("<input name=\"idchemical\" value=\"%d\" type=\"hidden\">",record.getIdchemical()));
								b.append("<h3>ToxTree: ILSI/Kroes decision tree for Threshold for Toxicological Concern (TTC) estimation</h3>");
								b.append("<p>Classifies the compound into one of three classes:");
								b.append("<h4><ol><li>Substance would not be expected to be a safety concern<li>Negligible risk (low probability of a life-time cancer risk greater than 1 in 10^6<li>Risk assessment requires compound-specific toxicity data</ol></h4>");
								b.append("<ul><li>Daily Intake value is necessary for an accurate TTC assessment.<li>Please enter Daily Intake value and press Update.  <li>Press Apply to apply TTC decision tree.</ul>");
								b.append("<input type=\"submit\" name='kroes' value=\"Apply ToxTree: ILSI/Kroes decision tree for TTC\">");
								b.append("</form></span></div>");
								output.write(b.toString());
							} catch (Exception x) {
								
							}
						}
						@Override
						public void footer(Writer w,
								IQueryRetrieval<PropertyValue> query) {
							super.footer(w, query);
						}
						@Override
						protected String getUpdateAction(PropertyValue v) {
							return "";
						}
					},MediaType.TEXT_HTML);		
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			return new StringConvertor(	getURUReporter(getRequest().getRootRef()),MediaType.TEXT_URI_LIST);
			
		} else return new StringConvertor(new PropertyValueReporter());
					
	}	
	
	@Override
	protected IQueryRetrieval<PropertyValue> createQuery(Context context,
			Request request, Response response) throws StatusException {
		RetrieveFieldPropertyValue  field = new RetrieveFieldPropertyValue() {
			@Override
			public String toString() {

				return "TTC: Enter Daily Intake";
			}
		};
		field.setAddNew(false);
		field.setSearchByAlias(false);
		
		Form form = request.getResourceRef().getQueryAsForm();
		record = new StructureRecord();
		try {
			record.setIdchemical(Integer.parseInt(form.getFirstValue("idchemical")));
			record.setIdstructure(Integer.parseInt(form.getFirstValue("idstructure")));
		} catch (NumberFormatException x) {
			throw new StatusException(
					new Status(Status.CLIENT_ERROR_BAD_REQUEST,x,String.format("Invalid resource id %d",request.getAttributes().get(CompoundResource.idcompound)))
					);
		}
		
		try {
			//record.setIdstructure(Integer.parseInt(form.getFirstValue("idstructure")));
			field.setChemicalsOnly(true);
		
		} catch (Exception x) {
			field.setChemicalsOnly(true);
		} finally {

			field.setValue(record);
		}
		field.setFieldname(new Property("DailyIntake","User input",""));
		return (IQueryRetrieval) field;
	}
	@Override
	public void acceptRepresentation(Representation entity)
			throws ResourceException {
		super.acceptRepresentation(entity);
	}
	protected IStructureRecord getRecordByParameters() {
		Form form = getRequest().getResourceRef().getQueryAsForm();
		record.setIdchemical(Integer.parseInt(form.getFirstValue("idchemical")));
		record.setIdstructure(Integer.parseInt(form.getFirstValue("idstructure")));
		return record;
	}	
	@Override
	protected PropertyValue createObjectFromHeaders(Form requestHeaders, Representation entity)
			throws ResourceException {
		Form form = new Form(entity);
		
		String value = form.getFirstValue(headers.value.toString());
		if (value == null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Value not defined");

		try {
			Property p = new Property("");
			p.setId(Integer.parseInt(form.getFirstValue(PropertyResource.idfeaturedef)));
			return new PropertyValue<Double>(p,Double.parseDouble(value.toString()));
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x);
		}
	}	
}
