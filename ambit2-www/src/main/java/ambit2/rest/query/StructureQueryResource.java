package ambit2.rest.query;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.ARFFReporter;
import ambit2.db.reporters.CMLReporter;
import ambit2.db.reporters.CSVReporter;
import ambit2.db.reporters.ImageReporter;
import ambit2.db.reporters.PDFReporter;
import ambit2.db.reporters.SDFReporter;
import ambit2.db.reporters.SmilesReporter;
import ambit2.db.search.structure.QueryStructureByID;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.DocumentConvertor;
import ambit2.rest.ImageConvertor;
import ambit2.rest.OutputStreamConvertor;
import ambit2.rest.PDFConvertor;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.structure.CompoundHTMLReporter;
import ambit2.rest.structure.CompoundURIReporter;

/**
 * Abstract parent class for all resources that retrieve compounds/conformers from database
 * @author nina
 *
 * @param <Q>
 */
public abstract class StructureQueryResource<Q extends IQueryRetrieval<IStructureRecord>>  
									extends QueryResource<Q,IStructureRecord> {

	protected String media;
	protected Template template;
	public Template getTemplate() {
		return template;
	}
	public void setTemplate(Template template) {
		this.template = (template==null)?new Template(null):template;

	}
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] {
				MediaType.TEXT_HTML,
				ChemicalMediaType.CHEMICAL_MDLSDF,
				ChemicalMediaType.CHEMICAL_SMILES,
				ChemicalMediaType.CHEMICAL_CML,
				MediaType.IMAGE_PNG,
				MediaType.APPLICATION_PDF,
				MediaType.TEXT_XML,
				MediaType.TEXT_URI_LIST,
				MediaType.TEXT_PLAIN,
				ChemicalMediaType.TEXT_YAML,
				ChemicalMediaType.WEKA_ARFF,
				MediaType.TEXT_CSV
				});
				
	}
	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		/* workaround for clients not being able to set accept headers */
		setTemplate(template);
		Form acceptform = getRequest().getResourceRef().getQueryAsForm();
		String media = acceptform.getFirstValue("accept-header");
		if (media != null) {
			variant.setMediaType(new MediaType(Reference.decode(media)));
		}
		if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_MDLSDF)) {
			return new OutputStreamConvertor<IStructureRecord, QueryStructureByID>(
					new SDFReporter<QueryStructureByID>(template),ChemicalMediaType.CHEMICAL_MDLSDF);
		} else if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_CML)) {
				return new OutputStreamConvertor<IStructureRecord, QueryStructureByID>(
						new CMLReporter<QueryStructureByID>(),ChemicalMediaType.CHEMICAL_CML);				
		} else if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_SMILES)) {
				return new OutputStreamConvertor<IStructureRecord, QueryStructureByID>(
						new SmilesReporter<QueryStructureByID>(),ChemicalMediaType.CHEMICAL_SMILES);
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_PDF)) {
			return new PDFConvertor<IStructureRecord, QueryStructureByID,PDFReporter<QueryStructureByID>>(
					new PDFReporter<QueryStructureByID>(getTemplate()));				
		} else if (variant.getMediaType().equals(MediaType.TEXT_PLAIN)) {
			return new StringConvertor(
					new SmilesReporter<QueryStructureByID>(),MediaType.TEXT_PLAIN);
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			CompoundURIReporter<QueryStructureByID> reporter = new CompoundURIReporter<QueryStructureByID>(getRequest());
			reporter.setDelimiter("\n");
			return new StringConvertor(reporter,MediaType.TEXT_URI_LIST);			
		} else if (variant.getMediaType().equals(MediaType.IMAGE_PNG)) {
			return new ImageConvertor<IStructureRecord, QueryStructureByID>(
					new ImageReporter<QueryStructureByID>(),MediaType.IMAGE_PNG);	
		} else if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			return new OutputStreamConvertor<IStructureRecord, QueryStructureByID>(
					new CompoundHTMLReporter(getRequest(),true),MediaType.TEXT_HTML);
		} else if (variant.getMediaType().equals(ChemicalMediaType.WEKA_ARFF)) {
			return new OutputStreamConvertor<IStructureRecord, QueryStructureByID>(
					new ARFFReporter(getTemplate()),ChemicalMediaType.WEKA_ARFF);			
		} else if (variant.getMediaType().equals(MediaType.TEXT_CSV)) {
			return new OutputStreamConvertor<IStructureRecord, QueryStructureByID>(
					new CSVReporter(getTemplate()),MediaType.TEXT_CSV);				
		} else
			return new DocumentConvertor(new QueryXMLReporter<Q>(getRequest()));
	}
	
	protected String getMediaParameter(Request request) {
		try {
			Object m = request.getAttributes().get("media");
			return m==null?null:Reference.decode(m.toString());
		} catch (Exception x) {
			return null;
		}	
	}
}
