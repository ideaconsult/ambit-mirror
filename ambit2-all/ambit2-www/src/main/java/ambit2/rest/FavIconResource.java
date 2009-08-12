package ambit2.rest;

import java.io.IOException;
import java.io.OutputStream;

import org.restlet.data.MediaType;
import org.restlet.resource.OutputRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

import ambit2.base.io.DownloadTool;

public class FavIconResource extends Resource {
	@Override
	public Representation represent(Variant variant) throws ResourceException {
		return new OutputRepresentation(MediaType.IMAGE_PNG) {
			@Override
			public void write(OutputStream outputStream)
					throws IOException {
				try {
				DownloadTool.download(getClass().getClassLoader().getResourceAsStream("16x16.png"), outputStream);
				outputStream.close();				
				} catch (Exception x) {
					x.printStackTrace();
				}
				
			}
		};
	}
}
