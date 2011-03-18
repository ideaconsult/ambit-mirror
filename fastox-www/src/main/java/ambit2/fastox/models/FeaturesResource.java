package ambit2.fastox.models;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.opentox.dsl.OTFeature;
import org.opentox.dsl.OTFeatures;
import org.opentox.dsl.OTModel;
import org.opentox.dsl.OTModels;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.fastox.ModelTools;
import ambit2.fastox.steps.FastoxStepResource;

/**
 * Features from selected models
 * @author nina
 *
 */
public class FeaturesResource extends FastoxStepResource {
	public static final String resource = "/feature";
	public FeaturesResource() {
		super(0);
		helpResource = null;
	}
	@Override
	protected boolean isMandatory(String param) {
		return false;
	}
	@Override
	public void renderFormContent(Writer writer, String key) throws IOException {
		try {
			renderRDFModels(writer, session, false, getRequest().getRootRef(),true);
		} catch (Exception x) {
			writer.write(x.getMessage());
		}
	}
	@Override
	public void footer(Writer output) throws IOException {
		output.write(ModelTools.jsIFrame());
		super.footer(output);
	}
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		return new OutputRepresentation(variant.getMediaType()) {
			@Override
			public void write(OutputStream out) throws IOException {
				OutputStreamWriter writer = null;
				try {
					writer = new OutputStreamWriter(out,"UTF-8");	  
					session = getSession(getUserKey());
					OTModels models = session.getSelectedModels();
					for (OTModel model: models.getItems()) {
						OTFeatures ff = model.getPredictedVariables();
						for (OTFeature f : ff.getItems()) {
							writer.write(f.getUri().toString());
							writer.write("\n");
						}
					}
				} catch (ResourceException x) {
					throw x;
				} catch (Exception x) {
					throw new ResourceException(x);
				} finally {

					writer.flush();
					out.close();
				}
			}
		};
	}
}
