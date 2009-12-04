package ambit2.rest;

import java.io.IOException;
import java.io.OutputStream;

import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ambit2.base.io.DownloadTool;

/**
 * favicon.ico support
 * @author nina
 *
 */
public class FavIconResource extends ServerResource {
	@Override
	public Representation get(Variant variant) throws ResourceException {
		return new OutputRepresentation(MediaType.IMAGE_PNG) {
			@Override
			public void write(OutputStream outputStream)
					throws IOException {
				try {
					DownloadTool.download(getClass().getClassLoader().getResourceAsStream("/images/feature.png"), outputStream);
				outputStream.close();				
				} catch (Exception x) {
					
				}
				
			}
		};
	}
}
