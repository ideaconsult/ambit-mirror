package ambit2.db.processors;

import java.io.File;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import net.idea.i5.cli.Container;
import net.idea.i5.cli.ContainerClient;
import net.idea.i5.cli.I5LightClient;
import net.idea.i5.cli.QueryToolClient;
import net.idea.i5.cli.QueryToolClient.PredefinedQuery;
import net.idea.i5.io.I5ZReader;
import net.idea.i5.io.IQASettings;
import net.idea.i5.io.QASettings;
import net.idea.modbcum.i.batch.IBatchStatistics;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.processors.ProcessorsChain;
import net.idea.opentox.cli.IIdentifiableResource;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.openscience.cdk.io.IChemObjectReaderErrorHandler;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.SourceDataset;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.FileInputState;
import ambit2.core.io.IRawReader;
import ambit2.core.processors.structure.key.PropertyKey;
import ambit2.core.processors.structure.key.ReferenceSubstanceUUID;
import ambit2.db.substance.processor.DBSubstanceWriter;
import ambit2.rest.DBConnection;
import ambit2.rest.dataset.DatasetURIReporter;
import ambit2.rest.exception.ImportSubstanceException;
import ambit2.rest.substance.SubstanceURIReporter;
import ambit2.rest.task.CallableQueryProcessor;
import ambit2.rest.task.TaskResult;

public class CallableSubstanceI5Query<USERID> extends
		CallableQueryProcessor<FileInputState, IStructureRecord, USERID>
		implements IQASettings {
	protected QASettings qaSettings;
	protected SubstanceRecord importedRecord;
	protected SourceDataset dataset;
	boolean useUUID;
	protected String substanceUUID;
	protected String extIDType;
	protected String extIDValue;
	protected boolean clearMeasurements = true;
	protected boolean clearComposition = true;
	protected Logger localLogger = Logger
			.getLogger(CallableSubstanceI5Query.class.getName());

	public boolean isClearComposition() {
		return clearComposition;
	}

	public void setClearComposition(boolean clearComposition) {
		this.clearComposition = clearComposition;
	}

	protected String server;
	protected String user;
	protected String pass;

	public boolean isClearMeasurements() {
		return clearMeasurements;
	}

	public void setClearMeasurements(boolean clearMeasurements) {
		this.clearMeasurements = clearMeasurements;
	}

	@Override
	public QASettings getQASettings() {
		if (qaSettings == null)
			qaSettings = new QASettings(false);
		return qaSettings;
	}

	@Override
	public void setQASettings(QASettings qa) {
		this.qaSettings = qa;
	}

	public CallableSubstanceI5Query(Reference applicationRootReference,
			Form form, Context context, SubstanceURIReporter substanceReporter,
			DatasetURIReporter datasetURIReporter, USERID token) {
		super(form, context, token);
		sourceReference = applicationRootReference;
	}

	@Override
	protected ProcessorsChain<IStructureRecord, IBatchStatistics, IProcessor> createProcessors()
			throws Exception {
		return null;
	}

	@Override
	protected void processForm(Reference applicationRootReference, Form form) {
		// [(option,UUID), (uuid,ZZZZZZZZZZ), (extidtype,CompTox),
		// (extidvalue,Ambit Transfer), (i5server,null), (i5user,null),
		// (i5pass,null)]
		substanceUUID = form.getFirstValue("uuid");
		useUUID = "UUID".equals(form.getFirstValue("option"));
		extIDType = form.getFirstValue("extidtype");
		extIDValue = form.getFirstValue("extidvalue");
		String qaEnabled = form.getFirstValue("qaenabled");
		getQASettings().clear();
		try {
			if ("on".equals(qaEnabled))
				getQASettings().setEnabled(true);
			if ("yes".equals(qaEnabled))
				getQASettings().setEnabled(true);
			if ("checked".equals(qaEnabled))
				getQASettings().setEnabled(true);
		} catch (Exception x) {
			getQASettings().setEnabled(true);
		}
		for (IQASettings.qa_field f : IQASettings.qa_field.values())
			try {
				String[] values = form.getValuesArray(f.name());
				for (String value : values)
					f.addOption(getQASettings(), "null".equals(value) ? null
							: value == null ? null : value);
			} catch (Exception x) {
			}
		try {
			clearMeasurements = false;
			String cm = form.getFirstValue("clearMeasurements");
			if ("on".equals(cm))
				clearMeasurements = true;
			else if ("yes".equals(cm))
				clearMeasurements = true;
			else if ("checked".equals(cm))
				clearMeasurements = true;
		} catch (Exception x) {
			clearMeasurements = false;
		}
		try {
			clearComposition = false;
			String cm = form.getFirstValue("clearComposition");
			if ("on".equals(cm))
				clearComposition = true;
			else if ("yes".equals(cm))
				clearComposition = true;
			else if ("checked".equals(cm))
				clearComposition = true;
		} catch (Exception x) {
			clearComposition = false;
		}
		try {
			server = form.getFirstValue("i5server").trim();
			if ("".equals(server))
				server = null;
		} catch (Exception x) {
			server = null;
		}
		try {
			user = form.getFirstValue("i5user").trim();
			if ("".equals(user))
				user = null;
		} catch (Exception x) {
			user = null;
		}
		try {
			pass = form.getFirstValue("i5pass").trim();
			if ("".equals(pass))
				pass = null;
		} catch (Exception x) {
			pass = null;
		}

	}

	@Override
	protected FileInputState createTarget(Reference reference) throws Exception {
		return null;
	}

	@Override
	protected TaskResult createReference(Connection connection)
			throws Exception {
		if (useUUID) {
			return new TaskResult(String.format("%s/substance/%s",
					sourceReference, substanceUUID));
		} else {
			return new TaskResult(String.format(
					"%s/substance?type=%s&search=%s", sourceReference,
					Reference.encode(extIDType), Reference.encode(extIDValue)));
		}
	}

	@Override
	public TaskResult doCall() throws Exception {

		Connection connection = null;
		DBConnection dbc = null;
		I5LightClient i5 = null;
		DBSubstanceWriter writer = null;
		FileHandler logHandler = null;
		StreamHandler streamHandler = null;
		OutputStream out = new ByteArrayOutputStream();
		try {
			logger.log(Level.INFO, String.format("Logging to %s", logFile));
			logHandler = new FileHandler(logFile);
			localLogger.addHandler(logHandler);

			SimpleFormatter formatter = new SimpleFormatter();
			streamHandler = new StreamHandler(out, formatter);
			localLogger.addHandler(streamHandler);
			logHandler.setFormatter(formatter);
			localLogger.setUseParentHandlers(false);
			localLogger.fine("Start()");
			if (useUUID) {
				if (substanceUUID == null || "".equals(substanceUUID))
					throw new ResourceException(
							Status.CLIENT_ERROR_BAD_REQUEST, "Empty UUID");
				dbc = new DBConnection(context);
				if (server == null)
					server = dbc.getProperty("i5.server");
				if (user == null)
					user = dbc.getProperty("i5.user");
				if (pass == null)
					pass = dbc.getProperty("i5.pass");
				try {
					i5 = new I5LightClient(server);
					if (i5.login(user, pass)) {
						ContainerClient ccli = i5.getContainerClient();
						if (substanceUUID.indexOf("/") < 0)
							substanceUUID = substanceUUID + "/0";
						localLogger.log(Level.FINE, String.format(
								"Retieving %s from %s", substanceUUID, server));
						List<IIdentifiableResource<String>> container = null;
						try {
							container = ccli.get(substanceUUID);
						} catch (Exception x) {
							localLogger.log(Level.SEVERE, String.format(
									"Failed to retrieve %s from %s. Error %s", substanceUUID, server,x.getMessage()));							
							streamHandler.flush();
							String msg = out.toString();
							throw new ImportSubstanceException(new Status(
									Status.SERVER_ERROR_BAD_GATEWAY,
									String.format("Failed to retrieve %s",substanceUUID)), msg);
						}
						connection = dbc.getConnection();
						localLogger.log(Level.INFO, String.format(
								"Importing %s", substanceUUID, server));
						writer = new DBSubstanceWriter(
								DBSubstanceWriter.datasetMeta(),
								new SubstanceRecord(), clearMeasurements,
								clearComposition);
						writer.setCloseConnection(false);
						writer.setConnection(connection);
						writer.open();
						IIdentifiableResource<String> result = processContainer(
								container, writer,server);

					} else
						throw new ImportSubstanceException(
								new Status(Status.SERVER_ERROR_BAD_GATEWAY,"IUCLID5 server login failed"),
								"IUCLID5 server login failed [" + server + "].");
				} catch (ResourceException x) {
					throw x;
				} catch (Exception x) {
					streamHandler.flush();
					String msg = out.toString();
					throw new ImportSubstanceException(new Status(
							Status.SERVER_ERROR_BAD_GATEWAY, x.getMessage()),
							msg);
				}
			} else { // query
				if (extIDType == null || extIDValue == null)
					throw new ImportSubstanceException(
							new Status(Status.CLIENT_ERROR_BAD_REQUEST, "Invalid query"),"Invalid query");
				dbc = new DBConnection(context);
				if (server == null)
					server = dbc.getProperty("i5.server");
				if (user == null)
					user = dbc.getProperty("i5.user");
				if (pass == null)
					pass = dbc.getProperty("i5.pass");

				try {
					i5 = new I5LightClient(server);
					if (i5.login(user, pass)) {
						QueryToolClient cli = i5.getQueryToolClient();

						List<IIdentifiableResource<String>> queryResults = cli
								.executeQuery(
										PredefinedQuery.query_by_it_identifier,
										extIDType, extIDValue);

						if (queryResults != null && queryResults.size() > 0) {
							connection = dbc.getConnection();
							writer = new DBSubstanceWriter(
									DBSubstanceWriter.datasetMeta(),
									new SubstanceRecord(), clearMeasurements,
									clearComposition);
							writer.setCloseConnection(false);
							writer.setConnection(connection);
							writer.open();

							ContainerClient ccli = i5.getContainerClient();
							int imported = 0;
							int importError = 0;
							for (IIdentifiableResource<String> item : queryResults)
								try {
									localLogger.log(Level.FINE,
											item.getResourceIdentifier());
									List<IIdentifiableResource<String>> container = ccli
											.get(item.getResourceIdentifier());
									processContainer(container, writer,server);

									imported++;
								} catch (Exception x) {
									localLogger.log(Level.SEVERE,
											x.getMessage());
									importError++;
								}
							String foundMsg = String
									.format("Found %d substance(s), imported %d substance(s) %d error(s).",
											queryResults.size(), imported,
											importError);
							localLogger.log(Level.INFO, foundMsg);

							streamHandler.flush();
							String msg = out.toString();

							if (importError != 0) {
								if (imported > 0) {
									TaskResult result = createReference(connection);
									msg = msg
											+ String.format(
													"The imported substances are available at %s",
													result.getUri());
								}
								throw new ImportSubstanceException(new Status(
										Status.SERVER_ERROR_BAD_GATEWAY,
										foundMsg), msg);
							} else if (imported == 0) {
								throw new ImportSubstanceException(
										new Status(Status.CLIENT_ERROR_NOT_FOUND, foundMsg),msg);
							}

						} else {
							streamHandler.flush();
							String msg = out.toString();

							throw new ImportSubstanceException(
									new Status(Status.CLIENT_ERROR_NOT_FOUND,
									"No substances found."),msg);
						}
					} else
						throw new ImportSubstanceException(
								new Status(Status.SERVER_ERROR_BAD_GATEWAY,"IUCLID5 server login failed"),
								"IUCLID5 server login failed [" + server + "].");
				} catch (ResourceException x) {
					throw x;
				} catch (Exception x) {
					localLogger.log(Level.SEVERE, x.getMessage());
					streamHandler.flush();
					String msg = out.toString();					
					throw new ImportSubstanceException(
							new Status(Status.SERVER_ERROR_BAD_GATEWAY, "Error when retrieving substances"),msg);
				}
			}
			
			return createReference(connection);
		} catch (ResourceException x) {
			localLogger.log(Level.SEVERE, x.getMessage(), x);
			throw x;
		} catch (Exception x) {
			localLogger.log(Level.SEVERE, x.getMessage(), x);
			throw x;
		} finally {
			localLogger.fine("Done");
			try {
				if (i5 != null)
					i5.logout();
			} catch (Exception x) {
				localLogger.warning(x.getMessage());
			}
			try {
				if (i5 != null)
					i5.close();
			} catch (Exception x) {
				localLogger.warning(x.getMessage());
			}
			try {
				writer.setConnection(null);
			} catch (Exception x) {
			}
			try {
				writer.close();
			} catch (Exception x) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (Exception x) {
				localLogger.warning(x.getMessage());
			}
			try {

				if (logHandler != null) {
					localLogger.setUseParentHandlers(true);
					localLogger.removeHandler(logHandler);
					localLogger.removeHandler(streamHandler);
					logHandler.close();
				}
			} catch (Exception x) {
			}
		}

	}

	private IIdentifiableResource<String> processContainer(
			List<IIdentifiableResource<String>> container,
			DBSubstanceWriter writer,String server) {
		IIdentifiableResource<String> result = null;
		for (IIdentifiableResource<String> item : container) {
			I5ZReader reader = null;
			File file = null;
			if (item instanceof Container)
				try {
					file = ((Container) item).getIpzarchive();
					if (file.exists()) {
						localLogger.fine(file.getAbsolutePath());
						reader = getReader(((Container) item).getIpzarchive());
						reader.setQASettings(getQASettings());
						write(reader, writer, new ReferenceSubstanceUUID(),server);
						result = item;
						localLogger.log(Level.INFO, String.format(
								"Substance UUID=%s imported successfully",
								item.getResourceIdentifier()));
					} else {
						localLogger.log(
								Level.SEVERE,
								String.format("File %s does not exist.",
										file.getAbsoluteFile()));
					}
				} catch (Exception x) {
					localLogger.log(Level.SEVERE, item.toString(), x);
				} finally {
					try {
						if (reader != null)
							reader.close();
					} catch (Exception x) {
					}
					try {
						file.delete();
					} catch (Exception x) {
					}
				}
		}
		return result;
	}

	private I5ZReader getReader(File i5z) throws Exception {
		I5ZReader reader = new I5ZReader(i5z);
		reader.setErrorHandler(new IChemObjectReaderErrorHandler() {

			@Override
			public void handleError(String message, int row, int colStart,
					int colEnd, Exception exception) {
				localLogger.log(Level.WARNING, String.format(
						"%s [row %d colStart %d colEnd %d] %s", message, row,
						colStart, colEnd, exception.getMessage()));
			}

			@Override
			public void handleError(String message, int row, int colStart,
					int colEnd) {
				localLogger.log(Level.WARNING, String.format(
						"%s [row %d colStart %d colEnd %d]", message, row,
						colStart, colEnd));
			}

			@Override
			public void handleError(String message, Exception exception) {
				localLogger.log(Level.WARNING, exception.getMessage());
			}

			@Override
			public void handleError(String message) {
				localLogger.log(Level.WARNING, message);
			}
		});
		return reader;
	}

	public int write(IRawReader<IStructureRecord> reader,
			DBSubstanceWriter writer, PropertyKey key, String server) throws Exception {
		try {
			int records = 0;
			while (reader.hasNext()) {
				Object record = reader.next();
				if (record == null)
					continue;
				if (record instanceof SubstanceRecord) {
					((SubstanceRecord) record).setContent(String.format("Retrieved from %s at %s", server,System.currentTimeMillis()));
				}
				if (record instanceof IStructureRecord)
					writer.process((IStructureRecord) record);
				records++;
			}
			return records;
		} catch (Exception x) {
			throw x;
		} finally {

		}

	}

}
