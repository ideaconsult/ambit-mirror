package ambit2.rest.substance;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.StructureRecordValidator;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.IRawReader;
import ambit2.rest.algorithm.CatalogResource;
import net.enanomapper.expand.SubstanceRecordMapper;
import net.enanomapper.parser.ExcelParserConfigurator;
import net.enanomapper.parser.GenericExcelParser;

public class NMParserResource extends CatalogResource<String> {
	public static final String resource = "/nmparser";

	public NMParserResource() {
		super();
		setHtmlbyTemplate(true);
	}
	@Override
	public String getTemplateName() {
		return "nmparser.ftl";
	}
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] { MediaType.APPLICATION_JSON });
	}

	@Override
	protected Representation post(Representation entity, Variant variant) throws ResourceException {
		synchronized (this) {
			try {
				//Form form = entity.isAvailable() ? new Form(entity) : new Form();
				DiskFileItemFactory factory = new DiskFileItemFactory();
				RestletFileUpload upload = new RestletFileUpload(factory);
				List<FileItem> items = upload.parseRequest(getRequest());
				FileItem xlsxfile = null;
				FileItem jsonfile = null;
				FileItem expandfile = null;
				String prefix = "XLSX";
				for (FileItem file : items) {
					if (file.isFormField()) {
						if ("prefix".equals(file.getFieldName()))
							prefix= file.getString().toUpperCase();
						
					} else {
						String ext = file.getName().toLowerCase().trim();
						
						if (ext.endsWith(".json")) {
							if ("jsonconfig".equals(file.getFieldName()))
								jsonfile = file;
							else if ("expandconfig".equals(file.getFieldName()))
								expandfile = file;
						} else if (ext.endsWith(".xlsx")) {
							xlsxfile = file;
						}
						
					}
					if (xlsxfile != null && jsonfile !=null) 
						break;
				}
				if (xlsxfile != null && jsonfile !=null)  {
				
					try {
						
						ExcelParserConfigurator config = ExcelParserConfigurator.loadFromJSON(jsonfile.getInputStream());
						GenericExcelParser parser = new GenericExcelParser(xlsxfile.getInputStream(), config, true, prefix.substring(0,3));
						try (StringWriter writer = new StringWriter()) {
							StructureRecordValidator validator = new StructureRecordValidator();
							validator.setPrefix(prefix);
							validator.setFixErrors(true);
							SubstanceRecordMapper expandmapper = null;
							if (expandfile!=null)
								expandmapper = new SubstanceRecordMapper(prefix,expandfile.getInputStream());
							//new SubstanceRecordMapper(getPrefix(), getExpandMap());
							writeAsJSON(parser,validator,expandmapper,writer);
							return new StringRepresentation(writer.toString(),MediaType.APPLICATION_JSON);
						} catch (Exception x) {
							throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage());
						}
					} catch (ResourceException x) {
						throw x;
					} catch (Exception x) {
						throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage());
						
					} finally {
						xlsxfile.getInputStream().close();

					}
					
				} else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"xlsx or json files missing");
			} catch (FileUploadException x) {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage());
			} catch (Exception x) {
				 throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage());
			}
		}
	}
	/*
	protected int convertSingleFile(InputStream fin) throws Exception {
		IRawReader<IStructureRecord> parser = null;

		try (FileInputStream fin = new FileInputStream(settings.getInputFile())) {

			parser = createParser(fin, settings.getInputFile().getName());
			logger_cli.log(Level.INFO, "MSG_IMPORT",
					new Object[] { parser.getClass().getName(), settings.getInputFile().getAbsolutePath() });

			StructureRecordValidator validator = new StructureRecordValidator(settings.getInputFile().getName(), true,
					"XLSX") {
				@Override
				public IStructureRecord validate(SubstanceRecord record) throws Exception {
					if (record.getRelatedStructures() != null && !record.getRelatedStructures().isEmpty()) {

						for (int i = record.getRelatedStructures().size() - 1; i >= 0; i--) {
							CompositionRelation rel = record.getRelatedStructures().get(i);
							int props = 0;
							for (Property p : rel.getSecondStructure().getRecordProperties()) {
								Object val = rel.getSecondStructure().getRecordProperty(p);
								if (val != null && !"".equals(val.toString()))
									props++;
							}
							if ((rel.getContent() == null || "".equals(rel.getContent())) && (props == 0))
								record.getRelatedStructures().remove(i);

						}

					}
					return super.validate(record);
				}
			};

			return write(parser, validator, settings.getOutformat(), settings.getOutputFile());
		} catch (Exception x) {
			throw x;
		} finally {
			if (parser != null)
				parser.close();
		}
	}	
	*/
	public int writeAsJSON(IRawReader<IStructureRecord> reader, StructureRecordValidator validator, SubstanceRecordMapper mapper, StringWriter writer)
			throws Exception {
		int records = 0;
		try  {
			writer.write("{ \"substance\" : [\n");
			String delimiter = "";
			while (reader.hasNext()) {
				Object record = reader.next();
				if (record == null)
					continue;
				try {
					if (validator!=null)
						validator.process((IStructureRecord) record);
					if (mapper != null)
						record = mapper.process((IStructureRecord) record);
					String tmp = ((SubstanceRecord) record).toJSON("http://localhost/ambit2", true);
					writer.write(delimiter);
					writer.write(tmp);
					writer.flush();
					delimiter = ",";
				} catch (Exception x) {
					x.printStackTrace();
					//logger.log(Level.FINE, x.getMessage());
				}
				records++;
			}
			writer.write("\n]}");
		} catch (Exception x) {
			//logger_cli.log(Level.WARNING, x.getMessage(), x);
		} finally {

			//logger_cli.log(Level.INFO, "MSG_IMPORTED", new Object[] { records });
		}
		return records;
	}

	@Override
	protected Iterator<String> createQuery(Context context, Request request, Response response)
			throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}	
}
