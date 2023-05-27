package ambit2.rest.substance;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFWriter;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.lookup.DictionaryConfig;
import ambit2.base.data.lookup.DictionaryConfigPropertyRB;
import ambit2.base.data.lookup.SubstanceStudyValidator;
import ambit2.base.data.study.StructureRecordValidator;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.json.JSONUtils;
import ambit2.base.ro.SubstanceRecordAnnotationProcessor;
import ambit2.core.io.IRawReader;
import ambit2.rest.AmbitFreeMarkerApplication;
import ambit2.rest.algorithm.CatalogResource;
import net.enanomapper.expand.SubstanceRecordMapper;
import net.enanomapper.parser.ExcelParserConfigurator;
import net.enanomapper.parser.GenericExcelParser;
import net.idea.restnet.c.ChemicalMediaType;

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
				// Form form = entity.isAvailable() ? new Form(entity) : new Form();
				DiskFileItemFactory factory = new DiskFileItemFactory();
				RestletFileUpload upload = new RestletFileUpload(factory);
				List<FileItem> items = upload.parseRequest(getRequest());
				FileItem xlsxfile = null;
				InputStream json_inputstream = null;
				FileItem expandfile = null;
				String prefix = "XLSX";
				for (FileItem file : items) {
					if (file.isFormField()) {
						if ("prefix".equals(file.getFieldName()))
							prefix = file.getString().toUpperCase();
						else if ("jsonconfig".equals(file.getFieldName())) {
							json_inputstream = new URL(file.getString()).openStream();
						}
					} else {
						String ext = file.getName().toLowerCase().trim();

						if (ext.endsWith(".json")) {
							if ("jsonconfig".equals(file.getFieldName()))
								json_inputstream = file.getInputStream();
							else if ("expandconfig".equals(file.getFieldName()))
								expandfile = file;
						} else if (ext.endsWith(".xlsx")) {
							xlsxfile = file;
						}

					}
				}
				if (xlsxfile != null && json_inputstream != null) {

					try {

						ExcelParserConfigurator config = ExcelParserConfigurator.loadFromJSON(json_inputstream);
						GenericExcelParser parser = new GenericExcelParser(xlsxfile.getInputStream(), config, true,
								prefix.substring(0, 3));
						try (StringWriter writer = new StringWriter()) {
							// StructureRecordValidator validator = new StructureRecordValidator();
							SubstanceRecordAnnotationProcessor annotator = null;
							try {
								annotator = new SubstanceRecordAnnotationProcessor(new File(
										((AmbitFreeMarkerApplication) getApplication()).getProperties().getMapFolder()),
										false);
							} catch (Exception x) {
								Logger.getGlobal().log(Level.WARNING, x.getMessage());
								annotator = null;
							}
							DictionaryConfig _dict = new DictionaryConfigPropertyRB(
									((AmbitFreeMarkerApplication) getApplication()).getProperties()
											.getNMParserDictFolder());

							final String file_name = xlsxfile.getName();
							SubstanceStudyValidator validator = new SubstanceStudyValidator(_dict, file_name, true,
									prefix) {
								public boolean isAddDefaultComposition() {
									return true;
								}

								@Override
								public IStructureRecord validate(SubstanceRecord record) throws Exception {
									record = StructureRecordValidator.basic_validation(record, file_name,
											file_name.toLowerCase().endsWith(".xlsx"), new Date());
									return super.validate(record);
								}
							};
							validator.setPrefix(prefix);
							validator.setFixErrors(true);
							SubstanceRecordMapper expandmapper = null;
							if (expandfile != null)
								expandmapper = new SubstanceRecordMapper(prefix, expandfile.getInputStream());
							// new SubstanceRecordMapper(getPrefix(), getExpandMap());

							MediaType outmedia = variant.getMediaType();
							if (outmedia.equals(MediaType.APPLICATION_RDF_TURTLE)
									|| outmedia.equals(MediaType.TEXT_RDF_N3)
									|| outmedia.equals(MediaType.TEXT_RDF_NTRIPLES)
									|| outmedia.equals(ChemicalMediaType.APPLICATION_JSONLD))
								return writeAsRDF(parser, validator, expandmapper, outmedia);
							else {
								writeAsJSON(parser, validator, expandmapper, annotator, writer);
								return new StringRepresentation(writer.toString(), MediaType.APPLICATION_JSON);
							}
						} catch (Exception x) {
							throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, x.getMessage());
						}
					} catch (ResourceException x) {
						throw x;
					} catch (Exception x) {
						throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, x.getMessage());

					} finally {
						xlsxfile.getInputStream().close();

					}

				} else {
					if (json_inputstream != null)
						json_inputstream.close();
					throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "xlsx or json files missing");
				}
			} catch (FileUploadException x) {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, x.getMessage());
			} catch (Exception x) {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, x.getMessage());
			}
		}
	}

	/*
	 * protected int convertSingleFile(InputStream fin) throws Exception {
	 * IRawReader<IStructureRecord> parser = null;
	 * 
	 * try (FileInputStream fin = new FileInputStream(settings.getInputFile())) {
	 * 
	 * parser = createParser(fin, settings.getInputFile().getName());
	 * logger_cli.log(Level.INFO, "MSG_IMPORT", new Object[] {
	 * parser.getClass().getName(), settings.getInputFile().getAbsolutePath() });
	 * 
	 * StructureRecordValidator validator = new
	 * StructureRecordValidator(settings.getInputFile().getName(), true, "XLSX") {
	 * 
	 * @Override public IStructureRecord validate(SubstanceRecord record) throws
	 * Exception { if (record.getRelatedStructures() != null &&
	 * !record.getRelatedStructures().isEmpty()) {
	 * 
	 * for (int i = record.getRelatedStructures().size() - 1; i >= 0; i--) {
	 * CompositionRelation rel = record.getRelatedStructures().get(i); int props =
	 * 0; for (Property p : rel.getSecondStructure().getRecordProperties()) { Object
	 * val = rel.getSecondStructure().getRecordProperty(p); if (val != null &&
	 * !"".equals(val.toString())) props++; } if ((rel.getContent() == null ||
	 * "".equals(rel.getContent())) && (props == 0))
	 * record.getRelatedStructures().remove(i);
	 * 
	 * }
	 * 
	 * } return super.validate(record); } };
	 * 
	 * return write(parser, validator, settings.getOutformat(),
	 * settings.getOutputFile()); } catch (Exception x) { throw x; } finally { if
	 * (parser != null) parser.close(); } }
	 */
	public int writeAsJSON(IRawReader<IStructureRecord> reader, StructureRecordValidator validator,
			SubstanceRecordMapper mapper, SubstanceRecordAnnotationProcessor annotator, StringWriter writer)
			throws Exception {
		int records = 0;
		Map<String, String[]> annotations = new HashMap<String, String[]>();
		try {
			writer.write("{ \"substance\" : [\n");
			String delimiter = "";
			while (reader.hasNext()) {
				Object record = reader.next();
				if (record == null)
					continue;
				try {
					if (validator != null)
						validator.process((IStructureRecord) record);
					if (mapper != null)
						record = mapper.process((IStructureRecord) record);
					if ((annotator != null) && (record instanceof SubstanceRecord)) {
						record = annotator.process((SubstanceRecord) record);
						annotator.annotate_all((SubstanceRecord) record, annotations);
					}
					String tmp = ((SubstanceRecord) record).toJSON("http://localhost/ambit2", true);
					writer.write(delimiter);
					writer.write(tmp);
					// writer.flush();
					delimiter = ",";
				} catch (Exception x) {
					x.printStackTrace();
					// logger.log(Level.FINE, x.getMessage());
				}
				records++;
			}

		} catch (Exception x) {
			// logger_cli.log(Level.WARNING, x.getMessage(), x);
		} finally {
			writer.write("\n],");
			writer.write("\"annotations\" : {\n");
			String comma = "";
			try {
				for (Map.Entry<String,String[]> e : annotations.entrySet()) {
					writer.write(comma);
					writer.write(String.format("%s : [\n",JSONUtils.jsonQuote(JSONUtils.jsonEscape(e.getKey()))));
					String d = "";
					for (String t : e.getValue()) {
						writer.write(String.format("%s%s",d,JSONUtils.jsonQuote(JSONUtils.jsonEscape(t))));
						d=",";
					}
					writer.write("\n]");
					comma=",";
				}
			} catch (Exception x) {}
			writer.write("\n}");
			writer.write("\n}");
			// logger_cli.log(Level.INFO, "MSG_IMPORTED", new Object[] { records });
		}
		return records;
	}

	public OutputRepresentation writeAsRDF(IRawReader<IStructureRecord> reader, StructureRecordValidator validator,
			SubstanceRecordMapper mapper, MediaType outmedia) throws Exception {

		SubstanceRecordAnnotationProcessor annotator = null;
		try {
			annotator = new SubstanceRecordAnnotationProcessor(
					new File(((AmbitFreeMarkerApplication) getApplication()).getProperties().getMapFolder()), false);
		} catch (Exception x) {
			Logger.getGlobal().log(Level.WARNING, x.getMessage());
			annotator = null;
		}

		Request hack = new Request();
		hack.setRootRef(new Reference("http://localhost/ambit2"));

		SubstanceRDFReporter exporter = new SubstanceRDFReporter(hack, outmedia, annotator);
		Model model = ModelFactory.createDefaultModel();
		exporter.header(model, null);
		exporter.setOutput(model);

		int records = 0;
		try {
			while (reader.hasNext()) {
				Object record = reader.next();
				if (record == null)
					continue;
				try {
					validator.process((IStructureRecord) record);
					exporter.processItem((SubstanceRecord) record);
				} catch (Exception x) {
					// logger.log(Level.FINE, x.getMessage());
				}
				records++;
			}

			return new OutputRepresentation(outmedia) {
				@Override
				public void write(OutputStream output) throws IOException {
					try {
						RDFWriter fasterWriter = null;
						if (outmedia.equals(MediaType.APPLICATION_RDF_TURTLE))
							fasterWriter = model.getWriter("TURTLE");
						else if (outmedia.equals(MediaType.TEXT_RDF_N3))
							fasterWriter = model.getWriter("N3");
						else if (outmedia.equals(MediaType.TEXT_RDF_NTRIPLES))
							fasterWriter = model.getWriter("N-TRIPLE");
						else
							fasterWriter = model.getWriter("JSON-LD");

						fasterWriter.write(model, output, "http://opentox.org/api/1.1");
					} catch (Exception x) {
						Throwable ex = x;
						while (ex != null) {
							if (ex instanceof IOException)
								throw (IOException) ex;
							ex = ex.getCause();
						}
						Context.getCurrentLogger().warning(x.getMessage() == null ? x.toString() : x.getMessage());
					} finally {

						try {
							if (output != null)
								output.flush();
						} catch (Exception x) {
							x.printStackTrace();
						}
						try {
							if (model != null)
								model.close();
						} catch (Exception x) {
							x.printStackTrace();
						}
					}
				}
			};

		} catch (Exception x) {
			// logger.log(Level.WARNING, x.getMessage(), x);
			throw (x);
		} finally {
			if (exporter != null)
				exporter.close();
			// logger_cli.log(Level.INFO, "MSG_IMPORTED", new Object[] { records });
		}
	}

	@Override
	protected Iterator<String> createQuery(Context context, Request request, Response response)
			throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}
}
