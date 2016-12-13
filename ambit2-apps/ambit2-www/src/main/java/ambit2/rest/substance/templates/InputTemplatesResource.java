package ambit2.rest.substance.templates;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Workbook;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.rest.StreamConvertor;
import ambit2.rest.algorithm.CatalogResource;
import net.enanomapper.maker.TemplateMaker;
import net.enanomapper.maker.TemplateMakerSettings;
import net.enanomapper.maker.TemplateMakerSettings._TEMPLATES_CMD;
import net.enanomapper.maker.TemplateMakerSettings._TEMPLATES_TYPE;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.r.AbstractReporter;

public class InputTemplatesResource extends CatalogResource<TemplateMakerSettings> {

	public InputTemplatesResource() {
		super();
		setHtmlbyTemplate(true);
	}

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] { MediaType.APPLICATION_MSOFFICE_XLSX });

	}

	@Override
	protected Iterator<TemplateMakerSettings> createQuery(Context context, Request request, Response response)
			throws ResourceException {
		ArrayList<TemplateMakerSettings> q = new ArrayList<TemplateMakerSettings>();
		TemplateMakerSettings ts = new TemplateMakerSettings();
		Form form = getRequest().getResourceRef().getQueryAsForm();

		String endpoint = form.getFirstValue("endpoint");
		if (endpoint == null)
			endpoint = "PCHEM_IEP.xlsx";
		String assayname = form.getFirstValue("assay");
		if (assayname == null)
			assayname = "isoElectric point";
		ts.setEndpointname(endpoint);
		ts.setAssayname(assayname);
		q.add(ts);
		return q.iterator();
	}

	@Override
	public IProcessor<Iterator<TemplateMakerSettings>, Representation> createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		if (variant.getMediaType().equals(MediaType.APPLICATION_MSOFFICE_XLSX)) {
			TemplateReporter reporter = new TemplateReporter();
			return new StreamConvertor(reporter, MediaType.APPLICATION_MSOFFICE_XLSX, "xlsx");

		}
		return null;
	}

}

class TemplateReporter extends AbstractReporter<Iterator<TemplateMakerSettings>, OutputStream> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4282879409634932625L;

	@Override
	public OutputStream process(Iterator<TemplateMakerSettings> ts) throws Exception {

		while (ts.hasNext()) {
			TemplateMaker maker = new TemplateMaker();
			TemplateMakerSettings settings = ts.next();
			settings.setTemplatesCommand(_TEMPLATES_CMD.generate);
			settings.setTemplatesType(_TEMPLATES_TYPE.jrc);

			// FIXME no input for generation needed, this is a placeholder
			File tmpdir = new File(System.getProperty("java.io.tmpdir"));
			settings.setInputfolder(tmpdir);
			settings.setOutputfolder(tmpdir);

			Workbook workbook = maker.generateJRCTemplates(settings);
			workbook.write(output);
			workbook.close();
			break;
		}
		return output;
	}

}
