package ambit2.rest.structure;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Variant;

import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.reporters.ImageReporter;
import ambit2.db.reporters.SDFReporter;
import ambit2.db.reporters.SmilesReporter;
import ambit2.db.search.structure.QueryStructureByID;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.DocumentConvertor;
import ambit2.rest.ImageConvertor;
import ambit2.rest.OutputStreamConvertor;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.query.StructureQueryResource;

public class StructureResource extends StructureQueryResource<QueryStructureByID> {
	
	public StructureResource(Context context, Request request, Response response) {
		super(context,request,response);

	}
	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException {
		if (variant.getMediaType().equals(MediaType.TEXT_XML)) 
			return new DocumentConvertor<IStructureRecord, QueryStructureByID>(new StructureReporter(getRequest().getRootRef()));
		else if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_MDLSDF)) {
			return new OutputStreamConvertor<IStructureRecord, QueryStructureByID>(
					new SDFReporter<QueryStructureByID>(),ChemicalMediaType.CHEMICAL_MDLSDF);
		} else if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_SMILES)) {
				return new OutputStreamConvertor<IStructureRecord, QueryStructureByID>(
						new SmilesReporter<QueryStructureByID>(),ChemicalMediaType.CHEMICAL_SMILES);
		} else if (variant.getMediaType().equals(MediaType.IMAGE_PNG)) {
			return new ImageConvertor<IStructureRecord, QueryStructureByID>(
					new ImageReporter<QueryStructureByID>(),MediaType.IMAGE_PNG);				
		} else
			return new OutputStreamConvertor<IStructureRecord, QueryStructureByID>(
					new SDFReporter<QueryStructureByID>(),ChemicalMediaType.CHEMICAL_MDLSDF);			
			

	}

	@Override
	protected QueryStructureByID createQuery(Context context, Request request,
			Response response) throws AmbitException {
		
		try {
			IStructureRecord record = new StructureRecord();
			record.setIdstructure(Integer.parseInt(Reference.decode(request.getAttributes().get("idstructure").toString())));
			QueryStructureByID query = new QueryStructureByID();
			query.setValue(record);
			return query;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}	
}
