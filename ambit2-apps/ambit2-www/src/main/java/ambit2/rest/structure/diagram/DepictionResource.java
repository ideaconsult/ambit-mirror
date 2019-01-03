package ambit2.rest.structure.diagram;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.rest.algorithm.CatalogResource;
import ambit2.rest.structure.diagram.DepictQuery.depict_type;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.reporter.Reporter;
import net.idea.restnet.c.RepresentationConvertor;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

public class DepictionResource<DQ extends DepictQuery> extends CatalogResource<DQ> {
	public static final String resource = "/depict";
	public static final String resourceKey = "option";

	protected Form params;
	protected DQ query;

	public DepictionResource() {
		super();
		setHtmlbyTemplate(true);
	}

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] { MediaType.IMAGE_PNG, MediaType.TEXT_HTML });
	}

	@Override
	public String getTemplateName() {
		return "demo/depict.ftl";
	}

	@Override
	protected Iterator<DQ> createQuery(Context context, Request request, Response response) throws ResourceException {
		depict_type depictType;
		try {
			depictType = depict_type.valueOf(getRequest().getAttributes().get(resourceKey).toString());
		} catch (Exception x) {
			depictType = depict_type.all;
		}

		query = (DQ) depictType.createQuery();
		query.parseQuery(getParams());
		List<DQ> t = new ArrayList<DQ>();
		t.add(query);
		return t.iterator();
	}

	@Override
	public IProcessor<Iterator<DQ>, Representation> createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		if (variant.getMediaType().equals(MediaType.IMAGE_PNG)) {
			return new ImageConvertor<DQ, Iterator<DQ>, DepictionReporter<DQ>>(query.getDepictType().getReporter(),
					MediaType.IMAGE_PNG, "png");
		} else
			throw new ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
	}

	protected Form getParams() {
		if (params == null)
			if (Method.GET.equals(getRequest().getMethod()))
				params = getResourceRef(getRequest()).getQueryAsForm();
			// if POST, the form should be already initialized
			else
				params = new Form(getRequest().getEntity());

		return params;
	}

	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request, IFreeMarkerApplication app) {
		super.configureTemplateMap(map, request, app);
		if (query != null) {
			map.put("depict_option", query.getDepictType());
			if (query.getSmiles() != null && query.getSmiles()[0] != null)
				map.put("depict_smiles", query.getSmiles()[0]);
			if (query.getSmarts() != null)
				map.put("depict_smarts", query.getSmarts());
			else
				map.put("depict_smarts", "");
		}
	}
}

class ImageConvertor<Item, Content, R extends Reporter<Content, BufferedImage>>
		extends RepresentationConvertor<Item, Content, BufferedImage, R> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2228783055860556941L;

	public ImageConvertor(R reporter, MediaType media, String fileNamePrefix) {
		super(reporter, media, fileNamePrefix);
	}

	protected BufferedImage createOutput(Content query) throws AmbitException {
		return null;
	}

	@Override
	public Representation process(Content query) throws Exception {
		reporter.setOutput(createOutput(query));
		Representation r = processDoc(reporter.process(query));
		try {
			reporter.close();
		} catch (Exception x) {
		}
		return r;
	};

	public Representation processDoc(final BufferedImage image) throws AmbitException {
		return new OutputRepresentation(mediaType) {
			@Override
			public void write(OutputStream stream) throws IOException {
				try {
					if (MediaType.IMAGE_PNG.equals(mediaType))
						ImageIO.write(image, "png", stream);
					else if (MediaType.IMAGE_JPEG.equals(mediaType))
						ImageIO.write(image, "jpeg", stream);
					else if (MediaType.IMAGE_GIF.equals(mediaType))
						ImageIO.write(image, "gif", stream);
					else if (MediaType.IMAGE_TIFF.equals(mediaType))
						ImageIO.write(image, "tiff", stream);
					else if (MediaType.IMAGE_BMP.equals(mediaType))
						ImageIO.write(image, "bmp", stream);
					else
						ImageIO.write(image, "png", stream);
				} catch (Exception x) {
					Throwable ex = x;
					while (ex != null) {
						if (ex instanceof IOException)
							throw (IOException) ex;
						ex = ex.getCause();
					}

				} finally {
					try {
						if (stream != null)
							stream.flush();
					} catch (IOException x) {
						Context.getCurrentLogger().warning(x.getMessage());
					} catch (Exception x) {
						Context.getCurrentLogger().warning(x.getMessage());

					}
				}
			}
		};
	}

}