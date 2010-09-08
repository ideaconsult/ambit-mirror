package ambit2.rest.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.restlet.data.ClientInfo;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.SourceDataset;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IBatchStatistics.RECORDS_STATS;
import ambit2.base.processors.ProcessorsChain;
import ambit2.core.io.FileInputState;
import ambit2.core.io.IInputState;
import ambit2.core.processors.structure.key.CASKey;
import ambit2.core.processors.structure.key.IStructureKey;
import ambit2.db.processors.BatchDBProcessor;
import ambit2.db.processors.RepositoryWriter;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.QueryExecutor;
import ambit2.db.update.dataset.ReadDataset;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.dataset.DatasetURIReporter;
import ambit2.rest.dataset.RDFIteratingReader;
import ambit2.rest.structure.ConformerURIReporter;

public class CallableFileImport implements
		java.util.concurrent.Callable<Reference> {
	protected SourceDataset targetDataset;
	protected ClientInfo client;
	protected boolean propertyOnly = false;
	protected boolean firstCompoundOnly = false;
	protected IStructureRecord recordImported = null;

	public boolean isPropertyOnly() {
		return propertyOnly;
	}

	public void setPropertyOnly(boolean propertyOnly) {
		this.propertyOnly = propertyOnly;
	}

	protected IStructureKey propertyKey = new CASKey();

	public IStructureKey getMatcher() {
		return propertyKey;
	}

	public void setMatcher(IStructureKey matchByProperty) {
		this.propertyKey = matchByProperty;
	}

	protected File file;

	protected File getFile() {
		return file;
	}

	protected void setFile(File file) {
		this.file = file;
	}

	protected Connection connection;
	protected CallableFileUpload upload;

	protected DatasetURIReporter<IQueryRetrieval<SourceDataset>> reporter;
	protected ConformerURIReporter compoundReporter;

	public DatasetURIReporter<IQueryRetrieval<SourceDataset>> getReporter() {
		return reporter;
	}

	public void setReporter(
			DatasetURIReporter<IQueryRetrieval<SourceDataset>> reporter) {
		this.reporter = reporter;
	}

	public CallableFileImport(ClientInfo client, SourceDataset dataset,
			File file, Connection connection,
			DatasetURIReporter<IQueryRetrieval<SourceDataset>> reporter,
			ConformerURIReporter compoundReporter,
			boolean firstCompoundOnly) {
		this.file = file;
		this.connection = connection;
		upload = null;
		this.reporter = reporter;
		this.compoundReporter = compoundReporter;
		this.targetDataset = dataset;
		this.client = client;
		this.firstCompoundOnly = firstCompoundOnly;
	}

	public CallableFileImport(ClientInfo client, SourceDataset dataset,
			List<FileItem> items, String fileUploadField,
			Connection connection,
			DatasetURIReporter<IQueryRetrieval<SourceDataset>> reporter,
			ConformerURIReporter compoundReporter,
			boolean firstCompoundOnly) {
		this(client, dataset, (File) null, connection, reporter,compoundReporter,
				firstCompoundOnly);

		for (final Iterator<FileItem> it = items.iterator(); it.hasNext();) {
			FileItem fi = it.next();
			if (!fi.isFormField())
				continue;
			if (fi.getFieldName().equals("match")) {
				try {
					setMatcher(IStructureKey.Matcher.valueOf(fi.getString())
							.getMatcher());
					break;
				} catch (Exception x) {
					setMatcher(null);
				}

			}
		}
		upload = new CallableFileUpload(items, fileUploadField) {
			@Override
			public Reference createReference() {
				return null;
			}

			@Override
			protected void processFile(File file) throws Exception {
				setFile(file);
			}
		};

	}

	protected String getExtension(MediaType mediaType) {
		if (ChemicalMediaType.CHEMICAL_MDLSDF.equals(mediaType))
			return ".sdf";
		if (ChemicalMediaType.CHEMICAL_MDLMOL.equals(mediaType))
			return ".mol";		
		if (ChemicalMediaType.CHEMICAL_CML.equals(mediaType))
			return ".cml";				
		if (ChemicalMediaType.CHEMICAL_SMILES.equals(mediaType))
			return ".smi";	
		if (ChemicalMediaType.CHEMICAL_INCHI.equals(mediaType))
			return ".inchi";		
		else if (MediaType.APPLICATION_RDF_XML.equals(mediaType))
			return ".rdf";
		else if (MediaType.APPLICATION_RDF_TURTLE.equals(mediaType))
			return ".turtle";
		else if (MediaType.TEXT_RDF_N3.equals(mediaType))
			return ".n3";
		else if (MediaType.APPLICATION_EXCEL.equals(mediaType))
			return ".xls";
		else if (MediaType.TEXT_CSV.equals(mediaType))
			return ".csv";
		else if (MediaType.TEXT_PLAIN.equals(mediaType))
			return ".txt";		
		else if (MediaType.APPLICATION_EXCEL.equals(mediaType))
			return ".xls";				
		else
			return null;
	}

	public CallableFileImport(ClientInfo client, SourceDataset dataset,
			Representation input, Connection connection,
			DatasetURIReporter<IQueryRetrieval<SourceDataset>> reporter,
			ConformerURIReporter compoundReporter,
			boolean firstCompoundOnly) {
		this(client, dataset, (File) null, connection, reporter,compoundReporter,
				firstCompoundOnly);
		try {
			String extension = getExtension(input.getMediaType());
			System.out.println(input.getIdentifier());
			File file = null;
			if (input.getDownloadName() == null) {
				file = File.createTempFile("ambit2_", extension);
				file.deleteOnExit();
			} else
				file = new File(String
						.format("%s/%s", System.getProperty("java.io.tmpdir"),
								input.getDownloadName()));
			FileOutputStream out = new FileOutputStream(file);
			input.write(out);
			out.flush();
			out.close();
			setFile(file);
		} catch (Exception x) {
			setFile(null);
		}

	}

	public Reference call() throws Exception {
		try {
			if (file == null)
				if (upload != null)
					upload.call();
				else
					throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			if (file != null)
				return importFile(file);
			else
				throw new Exception("No file");
		} catch (Exception x) {
			throw x;
		}
	}

	protected RDFIteratingReader getRDFIterator(File file, String baseReference) {
		String format = "RDF/XML";
		if (file.getName().endsWith(".rdf"))
			format = "RDF/XML";
		else if (file.getName().endsWith(".n3"))
			format = "N3";
		else if (file.getName().endsWith(".turtle"))
			format = "TURTLE";
		else
			return null;
		try {
			return new RDFIteratingReader(new FileInputStream(file),
					NoNotificationChemObjectBuilder.getInstance(),
					baseReference, format);
		} catch (Exception x) {
			x.printStackTrace();
			return null;
		}
	}

	public Reference importFile(File file) throws Exception {
		try {
			// if target dataset is not defined, create new dataset
			final SourceDataset dataset = targetDataset != null ? targetDataset
					: new SourceDataset(file.getName(), LiteratureEntry
							.getInstance(file.getName(),
									client == null ? "File uploaded by user"
											: client.getAddress()));

			if (targetDataset == null)
				dataset.setId(-1);
			final BatchDBProcessor batch = new BatchDBProcessor() {
				@Override
				public Iterator<String> getIterator(IInputState target)
						throws AmbitException {
					try {
						File file = ((FileInputState) target).getFile();
						RDFIteratingReader i = getRDFIterator(file,
								getReporter().getBaseReference().toString());
						if (i == null)
							return super.getIterator(target);
						else {
							/*
							 * RDFMetaDatasetIterator datasets = null; try {
							 * datasets = new
							 * RDFMetaDatasetIterator(i.getJenaModel());
							 * datasets
							 * .setBaseReference(getReporter().getBaseReference
							 * ()); while (datasets.hasNext()) { SourceDataset d
							 * = datasets.next(); dataset.setId(d.getId());
							 * dataset.setName(d.getName());
							 * dataset.setTitle(d.getTitle());
							 * dataset.setURL(d.getURL()); } } catch (Exception
							 * x) { x.printStackTrace(); } finally { try {
							 * datasets.close();} catch (Exception x) {} }
							 */
							return i;
						}
					} catch (AmbitException x) {
						throw x;
					} catch (Exception x) {
						throw new AmbitException(x);
					}
				}

				@Override
				public void onItemProcessed(String input, Object output,
						IBatchStatistics stats) {
					super.onItemProcessed(input, output, stats);
					if (firstCompoundOnly
							&& (stats
									.getRecords(RECORDS_STATS.RECORDS_PROCESSED) >= 1)) {
						cancelled = true;
						if (output != null)
							if ((output instanceof ArrayList) && ((ArrayList)output).size()>0) {
								if (((ArrayList)output).get(0) instanceof IStructureRecord) 
									recordImported = (IStructureRecord) ((ArrayList)output).get(0);
							} else if (output instanceof IStructureRecord)
								recordImported = (IStructureRecord) output;
					}
				}
			};

			batch.setConnection(connection);
			final RepositoryWriter writer = new RepositoryWriter();
			writer.setPropertiesOnly(isPropertyOnly());
			writer.setPropertyKey(getMatcher());
			writer.setDataset(dataset);
			final ProcessorsChain<String, IBatchStatistics, IProcessor> chain = new ProcessorsChain<String, IBatchStatistics, IProcessor>();
			chain.add(writer);
			batch.setProcessorChain(chain);
			writer.setConnection(connection);
			IBatchStatistics stats = batch.process(new FileInputState(file));

			if (firstCompoundOnly) {
				if (recordImported == null) throw new Exception("No compound imported");
				if (compoundReporter == null)
					compoundReporter = new ConformerURIReporter();
				return new Reference(compoundReporter.getURI(recordImported));				
			} else {
				ReadDataset q = new ReadDataset();
				q.setValue(dataset);
				QueryExecutor<ReadDataset> x = new QueryExecutor<ReadDataset>();
				x.setConnection(connection);
				ResultSet rs = x.process(q);
	
				SourceDataset newDataset = null;
				while (rs.next()) {
					newDataset = q.getObject(rs);
					break;
				}
				x.closeResults(rs);
				x.setConnection(null);
				if (newDataset == null)
					throw new ResourceException(Status.SUCCESS_NO_CONTENT);
				if (reporter == null)
					reporter = new DatasetURIReporter<IQueryRetrieval<SourceDataset>>();
				return new Reference(reporter.getURI(newDataset));
			}

		} catch (Exception x) {
			try {
				connection.close();
			} catch (Exception xx) {
			}
			throw new ResourceException(new Status(
					Status.SERVER_ERROR_INTERNAL, x.getMessage()));
		} finally {
			try {
				connection.close();
			} catch (Exception x) {
			}
			connection = null;
		}
	}

}
