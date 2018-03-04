package ambit2.rest.ui;

import java.util.ArrayList;
import java.util.List;

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
import ambit2.rest.dataset.DatasetURIReporter;
import ambit2.rest.substance.CallableSubstanceImporter;
import ambit2.rest.substance.SubstanceURIReporter;
import ambit2.rest.task.CallableFileUpload;
import net.idea.i5.io.IQASettings;
import net.idea.i5.io.QASettings;
import net.idea.restnet.i.task.ITaskResult;

public class UIResource extends UIResourceBase {
	@Override
	protected Representation uploadsubstance(Representation entity,
			Variant variant) throws ResourceException {
		if ((entity == null) || !entity.isAvailable())
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
					"Empty content");

		if (entity.getMediaType() != null
				&& MediaType.MULTIPART_FORM_DATA.getName().equals(
						entity.getMediaType().getName())) {
			DiskFileItemFactory factory = new DiskFileItemFactory();
			RestletFileUpload upload = new RestletFileUpload(factory);
			try {
				List<FileItem> items = upload.parseRequest(getRequest());
				StringBuilder json = new StringBuilder();
				json.append("{\"files\": [");
				String delimiter = "";
				QASettings qa = new QASettings();
				qa.clear(); // sets enabled to false and clears all flags
				boolean clearMeasurements = false;
				boolean clearComposition = false;
				for (FileItem file : items)
					if (file.isFormField()) {
						if ("qaenabled".equals(file.getFieldName()))
							try {
								if ("on".equals(file.getString()))
									qa.setEnabled(true);
								if ("yes".equals(file.getString()))
									qa.setEnabled(true);
								if ("checked".equals(file.getString()))
									qa.setEnabled(true);
							} catch (Exception x) {
								qa.setEnabled(true);
							}
						else if ("clearMeasurements"
								.equals(file.getFieldName())) {
							try {
								clearMeasurements = false;
								String cm = file.getString();
								if ("on".equals(cm))
									clearMeasurements = true;
								else if ("yes".equals(cm))
									clearMeasurements = true;
								else if ("checked".equals(cm))
									clearMeasurements = true;
							} catch (Exception x) {
								clearMeasurements = false;
							}

						} else if ("clearComposition".equals(file
								.getFieldName())) {
							try {
								clearComposition = false;
								String cm = file.getString();
								if ("on".equals(cm))
									clearComposition = true;
								else if ("yes".equals(cm))
									clearComposition = true;
								else if ("checked".equals(cm))
									clearComposition = true;
							} catch (Exception x) {
								clearComposition = false;
							}
						} else
							for (IQASettings.qa_field f : IQASettings.qa_field
									.values())
								if (f.name().equals(file.getFieldName()))
									try {
										String value = file.getString("UTF-8");
										f.addOption(
												qa,
												"null".equals(value) ? null
														: value == null ? null
																: value.toString());
									} catch (Exception x) {
									}
					}

				for (FileItem file : items) {
					if (file.isFormField())
						continue;

					json.append(delimiter);
					json.append("\n{\n");
					json.append("\"name\":");
					json.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(file
							.getName())));
					json.append(",\n\"size\":");
					json.append(file.getSize());
					String ext = file.getName().toLowerCase();
					if (ext.endsWith(".i5z") || ext.endsWith(".i6z") || ext.endsWith(".csv") 
							|| ext.endsWith(".rdf") || ext.endsWith(".ttl") || ext.endsWith(".json")) {
						String img = "i5z.png";
						if (ext.endsWith(".csv"))
							img = "csv64.png";
						else if (ext.endsWith(".xlsx"))
							img = "xlsx.png";
						else if (ext.endsWith(".rdf"))
							img = "rdf64.png";
						else if (ext.endsWith(".ttl"))
							img = "rdf64.png";						
						else if (ext.endsWith(".json"))
							img = "json64.png";
						else if (ext.endsWith(".i6z"))
							img = "i6z.png";
						try {
							List<FileItem> item = new ArrayList<FileItem>();
							item.add(file);
							CallableSubstanceImporter<String> callable = new CallableSubstanceImporter<String>(
									item, CallableFileUpload.field_files,
									CallableFileUpload.field_config,
									getRootRef(), getContext(),
									new SubstanceURIReporter(getRequest()
											.getRootRef()),
									new DatasetURIReporter(getRequest()
											.getRootRef()), null, getRequest()
											.getResourceRef().toString(),getClientInfo());
							callable.setClearComposition(clearComposition);
							callable.setClearMeasurements(clearMeasurements);
							callable.setQASettings(qa);
							ITaskResult result = callable.call();
							json.append(",\n\"url\":");
							json.append(JSONUtils.jsonQuote(JSONUtils
									.jsonEscape(result.getReference()
											.toString())));
							json.append(",\n\"thumbnailUrl\":");
							json.append(JSONUtils.jsonQuote(JSONUtils
									.jsonEscape(String.format("%s/images/%s",
											getRequest().getRootRef(), img))));
							json.append(",\n\"deleteUrl\":");
							json.append(JSONUtils.jsonQuote(JSONUtils
									.jsonEscape(result.getReference()
											.toString())));
							json.append(",\n\"deleteType\":");
							json.append("\"Delete\"");
						} catch (Exception x) {
							json.append(",\n\"error\":");
							json.append(JSONUtils.jsonQuote(JSONUtils
									.jsonEscape(x.getMessage())));
						} finally {
						}

					} else {
						json.append(",\n\"error\":");
						json.append(JSONUtils.jsonQuote(JSONUtils
								.jsonEscape("File type not allowed!")));
					}
					json.append("\n}");
				}
				json.append("\n]}");
				getResponse().setStatus(Status.SUCCESS_OK);
				return new StringRepresentation(json.toString(),
						MediaType.APPLICATION_JSON);
			} catch (FileUploadException x) {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, x);
			}

		} else
			throw new ResourceException(
					Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
	}
}
