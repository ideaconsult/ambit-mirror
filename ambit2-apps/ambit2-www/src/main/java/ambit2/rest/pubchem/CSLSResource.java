package ambit2.rest.pubchem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.NotFoundException;
import net.idea.restnet.c.ChemicalMediaType;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.io.DownloadTool;
import ambit2.pubchem.NCISearchProcessor;
import ambit2.pubchem.NCISearchProcessor.METHODS;
import ambit2.rest.ProtectedResource;
import ambit2.rest.query.QueryResource;
import ambit2.search.csls.CSLSRequest;
import ambit2.search.csls.CSLSStringRequest;

public class CSLSResource extends ProtectedResource {

	public static final String resource = "/cir";
	protected static final String resourceKey = "term";
	protected static final String representationKey = "representation";
	public static final String resourceID = String.format("/{%s}", resourceKey);
	public static final String representationID = String.format("/{%s}",
			representationKey);
	protected String term = "";
	protected NCISearchProcessor.METHODS representation = METHODS.sdf;

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		MediaType[] mimeTypes = new MediaType[] { MediaType.TEXT_PLAIN,
				MediaType.TEXT_HTML, ChemicalMediaType.CHEMICAL_MDLSDF };

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
		try {
			this.representation = METHODS.valueOf(Reference.decode(getRequest()
					.getAttributes().get(representationKey).toString()));
		} catch (Exception x) {
			Form form = getResourceRef(getRequest()).getQueryAsForm();
			Object key = form.getFirstValue(representationKey);
			if (key != null)
				try {
					representation = METHODS.valueOf(Reference.decode(key
							.toString()));
				} catch (Exception xx) {
					this.representation = METHODS.sdf;
				}
			else
				this.representation = METHODS.sdf;
		}

	}

	@Override
	protected Representation get(Variant variant) throws ResourceException {
		setFrameOptions("SAMEORIGIN");
		try {
			if (term != null) {

				if (variant.getMediaType().equals(
						ChemicalMediaType.CHEMICAL_MDLSDF))
					return new OutputRepresentation(variant.getMediaType()) {
						@Override
						public void write(OutputStream stream)
								throws IOException {
							try {
								CSLSRequest<InputStream> q = new CSLSRequest<InputStream>() {
									/**
						     * 
						     */
									private static final long serialVersionUID = 2638378178348461366L;

									@Override
									protected InputStream read(InputStream in)
											throws Exception {
										return in;
									}
								};
								q.setRepresentation(representation);
								DownloadTool.download(q.process(term), stream);
								stream.flush();
							} catch (ResourceException x) {
								throw x;
							} catch (AmbitException x) {
								throw new ResourceException(
										Status.SERVER_ERROR_INTERNAL,
										x.getMessage(), x);
								// throw new IOException(x.getMessage());
							} finally {
								try {
									if (stream != null)
										stream.flush();
								} catch (Exception x) {
									getLogger().log(Level.WARNING,
											x.getMessage(), x);
								}
							}
						}
					};
				else {
					CSLSStringRequest q = new CSLSStringRequest();
					q.setRepresentation(representation);
					return new StringRepresentation(q.process(term),
							MediaType.TEXT_PLAIN);
				}
			} else
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		
		} catch (NotFoundException x) {

			getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND,
					"No results for query " + term);
			return null;
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, x);
		}
	}
}
