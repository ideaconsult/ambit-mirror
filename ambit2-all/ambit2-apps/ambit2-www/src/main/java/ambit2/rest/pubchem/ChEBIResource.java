package ambit2.rest.pubchem;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.logging.Level;

import net.idea.restnet.c.ChemicalMediaType;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import uk.ac.ebi.chebi.webapps.chebiWS.model.SearchCategory;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.chebi.ChEBIClient;
import ambit2.rest.ProtectedResource;
import ambit2.rest.query.QueryResource;

public class ChEBIResource extends ProtectedResource {
	public static final String resource = "/chebi";
	protected static final String resourceKey = "term";
	public static final String resourceID = String.format("/{%s}", resourceKey);
	protected String term = "";

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		MediaType[] mimeTypes = new MediaType[] { ChemicalMediaType.CHEMICAL_MDLSDF };

		for (MediaType mileType : mimeTypes)
			getVariants().add(new Variant(mileType));
		// getVariants().put(Method.GET, variants);
		try {
			this.term = Reference.decode(getRequest().getAttributes()
					.get(resourceKey).toString());
		} catch (Exception x) {
			Form form = getResourceRef(getRequest()).getQueryAsForm();
			Object key = form.getFirstValue(QueryResource.search_param);
			if (key != null) {
				term = Reference.decode(key.toString());
			} else
				this.term = null;
		}
	}

	@Override
	protected Representation get(Variant variant) throws ResourceException {
		setFrameOptions("SAMEORIGIN");
		try {
			if (term != null) {

				// if
				// (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_MDLSDF))
				return new OutputRepresentation(
						ChemicalMediaType.CHEMICAL_MDLSDF) {
					@Override
					public void write(OutputStream stream) throws IOException {
						OutputStreamWriter writer = null;
						try {
							writer = new OutputStreamWriter(stream);
							ChEBIClient client = new ChEBIClient(term,
									SearchCategory.ALL);
							while (client.hasNext()) {
								IStructureRecord record = client.next();
								writer.write(record.getContent());
								for (Property p : record.getRecordProperties()) {
									writer.write(String.format("\n> <%s>\n",
											p.getName()));
									writer.write(String.format("%s\n\n",
											record.getRecordProperty(p)));
								}
							}

							writer.flush();
						} catch (ResourceException x) {
							throw x;
						} catch (Exception x) {
							throw new ResourceException(
									Status.SERVER_ERROR_BAD_GATEWAY,
									x.getMessage(), x);
						} finally {
							try {
								if (stream != null)
									stream.flush();
							} catch (Exception x) {
								getLogger().log(Level.WARNING, x.getMessage(),
										x);
							}
						}
					}
				};

			} else
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		} catch (Exception x) {

			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, x);
			return null;
		}
	}
}
