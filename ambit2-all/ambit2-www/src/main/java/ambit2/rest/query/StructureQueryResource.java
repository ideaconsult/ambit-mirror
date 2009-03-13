package ambit2.rest.query;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Variant;

import ambit2.core.data.IStructureRecord;
import ambit2.core.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.SDFReporter;
import ambit2.db.reporters.SmilesReporter;
import ambit2.db.search.QueryStructureByID;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.DocumentConvertor;
import ambit2.rest.OutputStreamConvertor;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.structure.StructureURIReporter;

public abstract class StructureQueryResource<Q extends IQueryRetrieval<IStructureRecord>>  
									extends QueryResource<Q,IStructureRecord> {

	
	public StructureQueryResource(Context context, Request request, Response response) {
		super(context,request,response);
		this.getVariants().add(new Variant(ChemicalMediaType.CHEMICAL_MDLSDF));	
		this.getVariants().add(new Variant(ChemicalMediaType.CHEMICAL_SMILES));		
		this.getVariants().add(new Variant(MediaType.TEXT_PLAIN));			
	}
	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException {
		
		if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_MDLSDF)) {
			return new OutputStreamConvertor<IStructureRecord, QueryStructureByID>(
					new SDFReporter<QueryStructureByID>(),ChemicalMediaType.CHEMICAL_MDLSDF);
		} else if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_SMILES)) {
				return new OutputStreamConvertor<IStructureRecord, QueryStructureByID>(
						new SmilesReporter<QueryStructureByID>(),ChemicalMediaType.CHEMICAL_SMILES);
		} else if (variant.getMediaType().equals(MediaType.TEXT_PLAIN)) {
			return new StringConvertor<IStructureRecord, QueryStructureByID>(
					new SmilesReporter<QueryStructureByID>(),MediaType.TEXT_PLAIN);
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			return new StringConvertor<IStructureRecord, QueryStructureByID>(
					new StructureURIReporter<QueryStructureByID>(getRequest().getRootRef()),MediaType.TEXT_URI_LIST);			
			
		} else
			return new DocumentConvertor(new QueryXMLReporter<Q>());
	}
	
}
