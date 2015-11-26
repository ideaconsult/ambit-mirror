package ambit2.rest.structure.diagram;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.reporter.Reporter;
import net.idea.restnet.c.RepresentationConvertor;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.engine.util.Base64;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.rest.algorithm.CatalogResource;
import ambit2.rest.query.QueryResource;
import ambit2.rest.query.StructureQueryResource.QueryType;
import ambit2.rest.structure.diagram.DepictQuery.depict_type;

public class DepictionResource extends CatalogResource<DepictQuery> {
	public static final String resource = "/depict";
	public static final String resourceKey = "option";

	protected Form params;
	protected DepictQuery query;

	public DepictionResource() {
		super();
		setHtmlbyTemplate(true);
	}

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] { MediaType.IMAGE_PNG,
				MediaType.TEXT_HTML });
	}

	@Override
	public String getTemplateName() {
		return "demo/depict.ftl";
	}

	@Override
	protected Iterator<DepictQuery> createQuery(Context context,
			Request request, Response response) throws ResourceException {
		depict_type depictType;
		try {
			depictType = depict_type.valueOf(getRequest().getAttributes()
					.get(resourceKey).toString());
		} catch (Exception x) {
			depictType = depict_type.all;
		}

		query = new DepictQuery(depictType);
		try {

			Form form = getParams();

			try {
				query.setW(Integer.parseInt(form.getFirstValue("w")));
			} catch (Exception x) {
				query.setW(400);
			}
			try {
				query.setH(Integer.parseInt(form.getFirstValue("h")));
			} catch (Exception x) {
				query.setH(200);
			}
			try {
				query.setRecordType(form.getFirstValue("record_type"));
			} catch (Exception x) {
				query.setRecordType("2d");
			}
			try {
				query.setqType(QueryType.valueOf(form.getFirstValue("type")));
			} catch (Exception x) {
				query.setqType(QueryType.smiles);
			}
			switch (query.getqType()) {
			case mol: { // base64 encoded mol files
				query.setSmiles(form
						.getValuesArray(QueryResource.b64search_param));
				if (query.getSmiles() != null)
					for (int i = 0; i < query.getSmiles().length; i++)
						query.getSmiles()[i] = new String(Base64.decode(query
								.getSmiles()[i]));
				break;
			}
			default: {
				query.setSmiles(form.getValuesArray(QueryResource.search_param));
				if ((query.getSmiles() == null)
						|| (query.getSmiles().length < 1))
					query.setSmiles(new String[] { null });
				else
					query.getSmiles()[0] = query.getSmiles()[0] == null ? ""
							: query.getSmiles()[0].trim();
			}
			}

			query.setSmarts(form.getFirstValue("smarts"));
			query.setSmirks(null);
			String[] smirks_patterns = form.getValuesArray("smirks");
			for (String sm : smirks_patterns)
				if (sm != null) {
					query.setSmirks(sm);
					break;
				}
		} catch (Exception x) {

		}
		List<DepictQuery> t = new ArrayList<DepictQuery>();
		t.add(query);
		return t.iterator();
	}

	@Override
	public IProcessor<Iterator<DepictQuery>, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {
		if (variant.getMediaType().equals(MediaType.IMAGE_PNG)) {
			return new ImageConvertor<DepictQuery, Iterator<DepictQuery>, DepictionReporter>(
					query.getDepictType().getReporter(), MediaType.IMAGE_PNG,
					"png");
		} else
			throw new ResourceException(
					Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
	}

	protected Form getParams() {
		if (params == null)
			if (Method.GET.equals(getRequest().getMethod()))
				params = getResourceRef(getRequest()).getQueryAsForm();
			// if POST, the form should be already initialized
			else
				params = getRequest().getEntityAsForm();

		return params;
	}

	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request,
			IFreeMarkerApplication app) {
		super.configureTemplateMap(map, request, app);
		if (query != null) {
			map.put("depict_option", query.getDepictType());
			if (query.getSmiles()!=null && query.getSmiles()[0]!=null)
				map.put("depict_smiles", query.getSmiles()[0]);
			if (query.getSmarts()!=null)
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

	public Representation processDoc(final BufferedImage image)
			throws AmbitException {
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