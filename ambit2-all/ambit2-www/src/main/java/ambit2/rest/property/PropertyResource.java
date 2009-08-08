package ambit2.rest.property;

import java.io.Writer;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Variant;

import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.StringCondition;
import ambit2.db.search.property.RetrieveFieldNamesByAlias;
import ambit2.db.update.property.ReadProperty;
import ambit2.rest.DocumentConvertor;
import ambit2.rest.OutputStreamConvertor;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.StatusException;
import ambit2.rest.StringConvertor;
import ambit2.rest.query.QueryResource;
import ambit2.rest.structure.CompoundResource;
import ambit2.rest.structure.ConformerResource;

/**
 * Feature definition resource
 * @author nina
 *
 */
public class PropertyResource extends QueryResource<IQueryRetrieval<Property>, Property> {
	public final static String featuredef = "/feature_definition";
	public final static String idfeaturedef = "id_feature_definition";
	public final static String featuredefID = String.format("%s/{%s}",featuredef,idfeaturedef);
	public final static String CompoundFeaturedefID = String.format("%s%s/{%s}",CompoundResource.compoundID,featuredef,idfeaturedef);
	public final static String ConformerFeaturedefID = String.format("%s%s/{%s}",ConformerResource.conformerID,featuredef,idfeaturedef);
	public final static String ConformerFeaturedef = String.format("%s%s",ConformerResource.conformerID,featuredef);
	public final static String CompoundFeaturedef = String.format("%s%s",CompoundResource.compoundID,featuredef);
	
	protected boolean collapsed ;
	
	public PropertyResource(Context context, Request request, Response response) {
		super(context,request,response);
		this.getVariants().add(new Variant(MediaType.TEXT_HTML));
		this.getVariants().add(new Variant(MediaType.TEXT_XML));
		this.getVariants().add(new Variant(MediaType.TEXT_URI_LIST));	
	}
	
	
	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException {
		if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
			return new DocumentConvertor(new PropertyDOMReporter(getRequest().getRootRef()));
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
				return new StringConvertor(	new PropertyURIReporter(getRequest().getRootRef()) {
					@Override
					public void processItem(Property dataset, Writer output) {
						super.processItem(dataset, output);
						try {
						output.write('\n');
						} catch (Exception x) {}
					}
				},MediaType.TEXT_URI_LIST);
				
		} else 
			return new OutputStreamConvertor(
					new PropertyHTMLReporter(getRequest().getRootRef(),collapsed)
					,MediaType.TEXT_HTML);
	}

	@Override
	protected IQueryRetrieval<Property> createQuery(Context context,
			Request request, Response response) throws StatusException {
		
		IStructureRecord record = null;
		boolean chemicalsOnly = true;
		Object key = request.getAttributes().get(CompoundResource.idcompound);		
		if (key != null) try {
			record = new StructureRecord();
			record.setIdchemical(Integer.parseInt(Reference.decode(key.toString())));
		} catch (Exception x) { record = null;}
		
		key = request.getAttributes().get(ConformerResource.idconformer);		
		if (key != null) try {
			if (record ==null) record = new StructureRecord();
			record.setIdstructure(Integer.parseInt(Reference.decode(key.toString())));
			chemicalsOnly = false;
		} catch (Exception x) { }
		
		Object o = request.getAttributes().get(idfeaturedef);
		try {
			collapsed = o==null;
			if (o==null) {
				Form form = request.getResourceRef().getQueryAsForm();
				key = form.getFirstValue("search");
				if (key != null) {
					RetrieveFieldNamesByAlias q = new RetrieveFieldNamesByAlias(Reference.decode(key.toString()));
					q.setFieldname(record);
					q.setChemicalsOnly(chemicalsOnly);
					q.setCondition(StringCondition.getInstance(StringCondition.C_REGEXP));
					return q;
				} else {
					ReadProperty property = new ReadProperty();
					property.setFieldname(record);
					property.setChemicalsOnly(chemicalsOnly);					
					return property;
				}
			}
			else return new ReadProperty(new Integer(o.toString()));
		} catch (Exception x) {
			collapsed = true;
			return new ReadProperty();
		} finally {
		}
	}

}
