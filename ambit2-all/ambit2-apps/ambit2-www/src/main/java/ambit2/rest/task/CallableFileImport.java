package ambit2.rest.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.batch.IBatchStatistics;
import net.idea.modbcum.i.batch.IBatchStatistics.RECORDS_STATS;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.processors.ProcessorsChain;

import org.apache.commons.fileupload.FileItem;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.restlet.data.ClientInfo;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import ambit2.base.data.AbstractDataset;
import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.ILiteratureEntry._type;
import ambit2.base.data.ISourceDataset;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.FileInputState;
import ambit2.core.io.IInputState;
import ambit2.core.processors.structure.key.CASKey;
import ambit2.core.processors.structure.key.IStructureKey;
import ambit2.db.processors.BatchDBProcessor;
import ambit2.db.processors.RepositoryWriter;
import ambit2.db.search.QueryExecutor;
import ambit2.db.update.dataset.ReadDataset;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.dataset.DatasetURIReporter;
import ambit2.rest.dataset.RDFIteratingReader;
import ambit2.rest.structure.ConformerURIReporter;

public class CallableFileImport<USERID> extends CallableProtectedTask<USERID> {
    protected UUID uuid;

    public UUID getUuid() {
	return uuid;
    }

    public void setUuid(UUID uuid) {
	this.uuid = uuid;
    }

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

    protected String fileDescription;
    protected File file;
    protected Hashtable<String, String> properties;

    protected File getFile() {
	return file;
    }

    protected void setFile(File file, String description) {
	this.file = file;
	this.fileDescription = description;
    }

    protected void setProperties(Hashtable<String, String> properties) {
	this.properties = properties;
    }

    protected Connection connection;
    protected CallableFileUpload upload;

    protected DatasetURIReporter<IQueryRetrieval<ISourceDataset>, ISourceDataset> reporter;
    protected ConformerURIReporter compoundReporter;

    public DatasetURIReporter<IQueryRetrieval<ISourceDataset>, ISourceDataset> getReporter() {
	return reporter;
    }

    public void setReporter(DatasetURIReporter<IQueryRetrieval<ISourceDataset>, ISourceDataset> reporter) {
	this.reporter = reporter;
    }

    public CallableFileImport(ClientInfo client, SourceDataset dataset, File file, Connection connection,
	    DatasetURIReporter<IQueryRetrieval<ISourceDataset>, ISourceDataset> reporter,
	    ConformerURIReporter compoundReporter, boolean firstCompoundOnly, USERID token) {
	super(token);
	setFile(file, file == null ? null : file.getName());
	this.connection = connection;
	upload = null;
	this.reporter = reporter;
	this.compoundReporter = compoundReporter;
	this.targetDataset = dataset;
	this.client = client;
	this.firstCompoundOnly = firstCompoundOnly;
    }

    public CallableFileImport(ClientInfo client, SourceDataset dataset, List<FileItem> items, String fileUploadField,
	    Connection connection, DatasetURIReporter<IQueryRetrieval<ISourceDataset>, ISourceDataset> reporter,
	    ConformerURIReporter compoundReporter, boolean firstCompoundOnly, USERID token) {
	this(client, dataset, (File) null, connection, reporter, compoundReporter, firstCompoundOnly, token);

	for (final Iterator<FileItem> it = items.iterator(); it.hasNext();) {
	    FileItem fi = it.next();
	    if (!fi.isFormField())
		continue;
	    if (fi.getFieldName().equals("match")) {
		try {
		    setMatcher(IStructureKey.Matcher.valueOf(fi.getString()).getMatcher());
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
	    protected void processFile(File file, String description) throws Exception {
		setFile(file, description);
	    }

	    @Override
	    protected void processProperties(Hashtable<String, String> properties) throws Exception {
		setProperties(properties);
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
	if (ChemicalMediaType.NANO_CML.equals(mediaType))
	    return ".nmd";
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
	else if (MediaType.APPLICATION_ZIP.equals(mediaType))
	    return ".zip";

	else
	    return null;
    }

    public CallableFileImport(ClientInfo client, SourceDataset dataset, Representation input, Connection connection,
	    DatasetURIReporter<IQueryRetrieval<ISourceDataset>, ISourceDataset> reporter,
	    ConformerURIReporter compoundReporter, boolean firstCompoundOnly, USERID token) {
	this(client, dataset, (File) null, connection, reporter, compoundReporter, firstCompoundOnly, token);
	try {
	    String extension = getExtension(input.getMediaType());
	    File file = null;
	    String description = null;
	    if (input.getDownloadName() == null) {
		description = UUID.randomUUID().toString();
		file = File.createTempFile(String.format("_ambit2_%s", description), extension);
		file.deleteOnExit();
	    } else {
		description = input.getDownloadName();
		file = new File(String.format("%s/%s", System.getProperty("java.io.tmpdir"), UUID.randomUUID()));
		file.deleteOnExit();
	    }
	    FileOutputStream out = new FileOutputStream(file);
	    input.write(out);
	    out.flush();
	    out.close();
	    setFile(file, description);
	} catch (Exception x) {
	    setFile(null, null);
	}

    }

    @Override
    public TaskResult doCall() throws Exception {
	try {
	    if (file == null)
		if (upload != null)
		    upload.call();
		else
		    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	    if (file != null) {
		return importFile(file);
	    } else
		throw new Exception("No file");
	} catch (Exception x) {
	    throw x;
	} finally {
	    try {
		if (connection != null)
		    connection.close();
	    } catch (Exception x) {
	    }
	    connection = null;
	    try {
		if (file != null && file.exists())
		    file.delete();
	    } catch (Exception x) {
		logger.log(Level.WARNING, x.getMessage(), x);
	    }
	}
    }

    protected RDFIteratingReader getRDFIterator(File file, String baseReference) throws Exception {
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
	    return new RDFIteratingReader(new FileInputStream(file), SilentChemObjectBuilder.getInstance(),
		    baseReference, format);
	} catch (CDKException x) {
	    throw x;
	} catch (Exception x) {
	    throw new AmbitException(String.format("Error reading %s %s", file.toString(), x.getMessage()), x);
	}
    }

    protected IIteratingChemObjectReader getNanoCMLIterator(File file, String baseReference) throws Exception {
	String format = ChemicalMediaType.NANO_CML.getName();
	if (file.getName().endsWith(".nmx") || file.getName().endsWith(".nmd"))
	    try {
		Class clazz = FileInputState.class.getClassLoader().loadClass(
			"net.idea.ambit2.rest.nano.NanoCMLRawReader");
		Constructor<? extends Runnable> constructor = clazz.getConstructor(InputStream.class);
		Object o = constructor.newInstance(new FileInputStream(file));
		return (IIteratingChemObjectReader) o;
	    } catch (Exception x) {
		throw new AmbitException(String.format("Error reading %s %s", file.toString(), x.getMessage()), x);
	    }
	return null;
    }

    protected SourceDataset datasetMeta(File file) {

	String title = properties == null ? null : properties.get("title");
	title = title == null ? fileDescription : ("".equals(title.trim()) ? fileDescription : title.trim());

	String source = properties == null ? null : properties.get(AbstractDataset._props.source.name());
	source = source == null ? fileDescription : source;

	String license = properties == null ? null : properties.get(AbstractDataset._props.license.name());
	license = license == null ? ISourceDataset.license.Unknown.getURI() : license;

	String rightsHolder = properties == null ? null : properties.get(AbstractDataset._props.rightsHolder.name());
	rightsHolder = rightsHolder == null ? "Unknown" : rightsHolder;

	String seeAlso = properties == null ? null : properties.get(AbstractDataset._props.seeAlso.name());

	String publisher = seeAlso == null ? client == null ? "File uploaded by user"
		: (client.getUser() == null ? client.getAddress() : client.getUser().getIdentifier() == null ? client
			.getAddress() : client.getUser().getIdentifier()) : seeAlso;

	ILiteratureEntry reference = LiteratureEntry.getInstance(source, publisher);
	reference.setType(_type.Dataset);
	SourceDataset dataset = new SourceDataset(title, reference);
	dataset.setLicenseURI(license);
	dataset.setrightsHolder(rightsHolder);
	return dataset;
    }

    public TaskResult importFile(File file) throws Exception {
	try {
	    // if target dataset is not defined, create new dataset
	    final SourceDataset dataset = targetDataset != null ? targetDataset : datasetMeta(file);

	    if (targetDataset == null)
		dataset.setId(-1);

	    final BatchDBProcessor<String> batch = new BatchDBProcessor<String>() {
		/**
			     * 
			     */
		private static final long serialVersionUID = -7971761364143510120L;

		@Override
		public Iterator<String> getIterator(IInputState target) throws AmbitException {
		    try {
			File file = ((FileInputState) target).getFile();
			RDFIteratingReader i = getRDFIterator(file, getReporter().getBaseReference().toString());
			if (i == null) {
			    IIteratingChemObjectReader ni = getNanoCMLIterator(file, getReporter().getBaseReference()
				    .toString());
			    if (ni == null)
				return super.getIterator(target);
			    else
				return ni;

			} else {
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
		public void onItemProcessed(String input, Object output, IBatchStatistics stats) {
		    super.onItemProcessed(input, output, stats);
		    if (firstCompoundOnly && (stats.getRecords(RECORDS_STATS.RECORDS_PROCESSED) >= 1)) {
			cancelled = true;
			if (output != null)
			    if ((output instanceof ArrayList) && ((ArrayList) output).size() > 0) {
				if (((ArrayList) output).get(0) instanceof IStructureRecord)
				    recordImported = (IStructureRecord) ((ArrayList) output).get(0);
			    } else if (output instanceof IStructureRecord)
				recordImported = (IStructureRecord) output;
		    }
		}
	    };
	    batch.setReference(dataset.getReference());
	    batch.setConnection(connection);
	    final RepositoryWriter writer = new RepositoryWriter();
	    writer.setUseExistingStructure(isPropertyOnly());
	    writer.setPropertyKey(getMatcher());
	    writer.setDataset(dataset);
	    final ProcessorsChain<String, IBatchStatistics, IProcessor> chain = new ProcessorsChain<String, IBatchStatistics, IProcessor>();
	    chain.add(writer);
	    batch.setProcessorChain(chain);
	    writer.setConnection(connection);
	    IBatchStatistics stats = batch.process(new FileInputState(file));

	    if (firstCompoundOnly) {
		if (recordImported == null)
		    throw new Exception("No compound imported");
		if (compoundReporter == null)
		    compoundReporter = new ConformerURIReporter(null);
		try {
		    batch.close();
		} catch (Exception xx) {
		}
		return new TaskResult(compoundReporter.getURI(recordImported));
	    } else {
		ReadDataset q = new ReadDataset();
		q.setValue(dataset);
		QueryExecutor<ReadDataset> x = new QueryExecutor<ReadDataset>();
		x.setConnection(connection);
		ResultSet rs = x.process(q);

		ISourceDataset newDataset = null;
		while (rs.next()) {
		    newDataset = q.getObject(rs);
		    break;
		}
		x.closeResults(rs);
		x.setConnection(null);
		if (newDataset == null)
		    throw new ResourceException(Status.SUCCESS_NO_CONTENT);
		if (reporter == null)
		    reporter = new DatasetURIReporter<IQueryRetrieval<ISourceDataset>, ISourceDataset>();
		try {
		    batch.close();
		} catch (Exception xx) {
		}
		return new TaskResult(reporter.getURI(newDataset));
	    }

	} catch (ResourceException x) {
	    throw x;
	} catch (Exception x) {
	    throw new ResourceException(new Status(Status.SERVER_ERROR_INTERNAL, x.getMessage()));
	} finally {
	    try {
		connection.close();
	    } catch (Exception x) {
	    }
	    connection = null;
	}
    }

}
