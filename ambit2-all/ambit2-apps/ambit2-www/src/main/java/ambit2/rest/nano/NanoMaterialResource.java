package ambit2.rest.nano;

import net.idea.restnet.c.ResourceDoc;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.structure.QueryStructureByID;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.rdf.RDFObjectIterator;
import ambit2.rest.structure.CompoundResource;

public class NanoMaterialResource extends CompoundResource {
	public final static String nanomaterial = "/nanomaterial";
	
	public NanoMaterialResource() {
		super();
		setDocumentation(new ResourceDoc("nanomaterial","NanoMaterial"));
	}
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		getVariants().clear();
		customizeVariants(new MediaType[] {
				MediaType.TEXT_HTML,
				//ChemicalMediaType.CHEMICAL_MDLSDF,
				//ChemicalMediaType.CHEMICAL_MDLMOL,
				//ChemicalMediaType.CHEMICAL_SMILES,
				//ChemicalMediaType.CHEMICAL_INCHI,
				ChemicalMediaType.CHEMICAL_CML,
				MediaType.APPLICATION_PDF,
				MediaType.TEXT_URI_LIST,
				MediaType.TEXT_PLAIN,
				ChemicalMediaType.WEKA_ARFF,
				ChemicalMediaType.THREECOL_ARFF,
				MediaType.TEXT_CSV,
				MediaType.APPLICATION_RDF_XML,
				MediaType.APPLICATION_RDF_TURTLE,
				MediaType.APPLICATION_RDF_TRIG,
				MediaType.APPLICATION_RDF_TRIX,
				MediaType.TEXT_RDF_N3,
				MediaType.TEXT_RDF_NTRIPLES,
				MediaType.APPLICATION_JAVA_OBJECT
				});
	}
	@Override
	protected RDFObjectIterator<IStructureRecord> createObjectIterator(
			Representation entity) throws ResourceException {
		//replace with RDFNanoMaterialsIterator
		//return new RDFStructuresIterator(entity,entity.getMediaType());
		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
	}
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
	}
	@Override
	protected QueryStructureByID createQueryByID(IStructureRecord record) {
		QueryStructureByID query = super.createQueryByID(record);
		query.setNanomaterial(true);
		return query;
	}
}
