package ambit2.rest.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.idea.i5.io.QASettings;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.json.JSONUtils;
import ambit2.rest.aa.opensso.OpenSSOUser;
import ambit2.rest.dataset.DatasetURIReporter;
import ambit2.rest.freemarker.FreeMarkerResource;
import ambit2.rest.substance.CallableSubstanceImporter;
import ambit2.rest.substance.SubstanceURIReporter;
import ambit2.rest.task.TaskResult;

public class UIResource extends FreeMarkerResource {
	private static final String key = "key";
	private enum pages { 
			index, query, 
			uploadstruc, uploadprops, 
			uploadsubstance {
				@Override
				public boolean enablePOST() {
					return true;
				}
			},
			uploadsubstance1,
			predict, 
			login, register, myprofile, 
			uploadstruc_register,uploadprops_batch,uploadprops_biodata,
			createstruc,
			dataprep,
			_dataset,
			_search,
			editor;
			public boolean enablePOST() {
				return false;
			}
		}
	public UIResource() {
		super();
		setHtmlbyTemplate(true);
	}
	
	@Override
	public String getTemplateName() {
		Object ui = getRequest().getAttributes().get(key);
		try {
			return ui==null?"index.ftl":String.format("%s.ftl", pages.valueOf(ui.toString()).name());
		} catch (Exception x) { return "index.ftl";}
	}
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
        getVariants().add(new Variant(MediaType.TEXT_HTML));
        getVariants().add(new Variant(MediaType.APPLICATION_JSON));
	}
	protected Representation getHTMLByTemplate(Variant variant) throws ResourceException {
        Map<String, Object> map = new HashMap<String, Object>();
        if (getClientInfo().getUser()!=null) 
        	map.put("username", getClientInfo().getUser().getIdentifier());
		if (getClientInfo().getUser() == null) {
			OpenSSOUser ou = new OpenSSOUser();
			ou.setUseSecureCookie(useSecureCookie(getRequest()));
			getClientInfo().setUser(ou);
		}
        setTokenCookies(variant, useSecureCookie(getRequest()));
        configureTemplateMap(map);
        return toRepresentation(map, getTemplateName(), MediaType.TEXT_PLAIN);
	}
	
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		Object ui = getRequest().getAttributes().get(key);
		try {
			if (pages.valueOf(ui.toString()).enablePOST()) {
				return uploadsubstance(entity,variant);
			}
		} catch (Exception x) { 
			x.printStackTrace();
		}
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);

	}
	
	protected Representation uploadsubstance(Representation entity, Variant variant)
			throws ResourceException {
		if ((entity == null) || !entity.isAvailable()) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Empty content");

		if (entity.getMediaType()!= null && MediaType.MULTIPART_FORM_DATA.getName().equals(entity.getMediaType().getName())) {
			DiskFileItemFactory factory = new DiskFileItemFactory();
			RestletFileUpload upload = new RestletFileUpload(factory);
			try {
				List<FileItem> items = upload.parseRequest(getRequest());
				StringBuilder json = new StringBuilder();
				json.append("{\"files\": [");
				String delimiter = "";
				QASettings qa = new QASettings();
				qa.setEnabled(false);
				for (FileItem file : items) if (file.isFormField()) {
					if ("qaenabled".equals(file.getFieldName())) try {
						if ("on".equals(file.getString())) qa.setEnabled(true);
						if ("yes".equals(file.getString())) qa.setEnabled(true);
						if ("checked".equals(file.getString())) qa.setEnabled(true);
					} catch (Exception x) {
						qa.setEnabled(true);
					}
				}	
				for (FileItem file : items) {
					if (file.isFormField()) continue;
					
					json.append(delimiter);
					json.append("\n{\n");
					json.append("\"name\":");
					json.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(file.getName())));
					json.append(",\n\"size\":");
					json.append(file.getSize());
					if (file.getName().endsWith(".i5z")) {
						
						try {
							List<FileItem> item = new ArrayList<FileItem>();
							item.add(file);
							CallableSubstanceImporter<String> callable = new CallableSubstanceImporter<String>(
									item, 
									"files[]",
									getRootRef(),
									getContext(),
									new SubstanceURIReporter(getRequest().getRootRef(), null),
									new DatasetURIReporter(getRequest().getRootRef(), null),
									null);
							callable.setQASettings(qa);
							TaskResult result = callable.call();
							json.append(",\n\"url\":");
							json.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(result.getReference().toString())));
							json.append(",\n\"thumbnailUrl\":");
							json.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(String.format("%s/images/i5z.png",getRequest().getRootRef()))));
							json.append(",\n\"deleteUrl\":");
							json.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(result.getReference().toString())));
							json.append(",\n\"deleteType\":");
							json.append("\"Delete\"");							
						} catch (Exception x) {
							json.append(",\n\"error\":");
							json.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(x.getMessage())));	
						} finally {
						}

					} else {
						json.append(",\n\"error\":");
						json.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape("File type not allowed!")));
					}
					json.append("\n}");
				}
				json.append("\n]}");
				getResponse().setStatus(Status.SUCCESS_OK);
				return new StringRepresentation(json.toString(),MediaType.APPLICATION_JSON);
			} catch (FileUploadException x) {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x);
			}
			
		} else 
			throw new ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
	}	
}
