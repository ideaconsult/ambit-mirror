package ambit2.rest.tuple;

import java.io.Writer;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.PropertiesTuple;
import ambit2.db.SourceDataset;
import ambit2.db.update.tuple.QueryTuple;
import ambit2.rest.StatusException;
import ambit2.rest.StringConvertor;
import ambit2.rest.dataset.DatasetsResource;
import ambit2.rest.query.QueryResource;
import ambit2.rest.structure.CompoundResource;
import ambit2.rest.structure.ConformerResource;

/**
 * Returns available tuples id for a given compound and dataset 
 * GET /compound/{id}/tuple  returns uri-list
 * @author nina
 *
 */
public class TupleResource extends QueryResource<QueryTuple, PropertiesTuple> {
	public static String resourceKey = "idtuple";
	public static String resourceTag = "/tuple";
	//public static String resource = String.format("%s/%s",CompoundResource.compoundID,resourceTag);

//	public static String resourceDataset = String.format("%s%s/{%s}",DatasetsResource.datasetID,CompoundResource.compoundID,resourceTag);


	@Override
	public IProcessor<QueryTuple, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {
		/*
		if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
			return new DocumentConvertor(new DatasetsXMLReporter(getRequest().getRootRef()));	
			*/
		if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			return new StringConvertor(
					new TupleHTMLReporter(getRequest(),queryObject.getFieldname()),MediaType.TEXT_HTML);
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
		
			return new StringConvertor(	new TupleURIReporter(getRequest(),queryObject.getFieldname()) {
				@Override
				public void processItem(PropertiesTuple src, Writer output) {
					super.processItem(src, output);
					try {
					output.write("\n");
					} catch (Exception x) {}
				}
			},MediaType.TEXT_URI_LIST);
			
		} else //html 	
			return new StringConvertor(
					new TupleHTMLReporter(getRequest(),queryObject.getFieldname()),MediaType.TEXT_HTML);
		
	}
	
	@Override
	protected QueryTuple createQuery(Context context, Request request,
			Response response) throws StatusException {
		
		QueryTuple tuple = new QueryTuple();
		tuple.setChemicalsOnly(true);
		IStructureRecord record = new StructureRecord();
		tuple.setFieldname(record);
		Object cid = getRequest().getAttributes().get(CompoundResource.idcompound);
		if (cid == null) throw new StatusException(status.CLIENT_ERROR_BAD_REQUEST);
		try {
			record.setIdchemical(Integer.parseInt(cid.toString()));
		} catch (Exception x) {
			throw new StatusException(status.CLIENT_ERROR_BAD_REQUEST);
		}
		cid = getRequest().getAttributes().get(ConformerResource.idconformer);
		try {
			record.setIdstructure(Integer.parseInt(cid.toString()));
			tuple.setChemicalsOnly(false);
		} catch (Exception x) {
			tuple.setChemicalsOnly(true);
		}
		cid = getRequest().getAttributes().get(DatasetsResource.datasetKey);
		
		try {
			SourceDataset dataset = new SourceDataset();
			dataset.setId(Integer.parseInt(cid.toString()));
			tuple.setValue(dataset);
		} catch (Exception x) {
			tuple.setValue(null);
		}
		return tuple;
	}

}

/*
 * Select tuple ,filter by template
 
select idtuple,idproperty,idstructure,properties.name,value_num,ifnull(text,value),title,url from
property_values
left join property_tuples using(id)
left join property_string using(idvalue_string)
join properties using(idproperty)
join structure using (idstructure)
join catalog_references using(idreference)
where idtuple=257318
and idproperty in (select idproperty from template_def join template using(idtemplate) where name="LLNA_3D.sdf")
*/