package ambit2.rest.task;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.InputRepresentation;
import org.restlet.resource.ResourceException;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.processors.ProcessorsChain;
import ambit2.core.io.FileInputState;
import ambit2.db.SourceDataset;
import ambit2.db.processors.BatchDBProcessor;
import ambit2.db.processors.RepositoryWriter;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.QueryExecutor;
import ambit2.db.update.dataset.ReadDataset;
import ambit2.rest.dataset.DatasetURIReporter;

public class CallableFileImport implements	java.util.concurrent.Callable<Reference> {
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
	public DatasetURIReporter<IQueryRetrieval<SourceDataset>> getReporter() {
		return reporter;
	}

	public void setReporter(
			DatasetURIReporter<IQueryRetrieval<SourceDataset>> reporter) {
		this.reporter = reporter;
	}

	public CallableFileImport(File file, Connection connection,DatasetURIReporter<IQueryRetrieval<SourceDataset>> reporter) {
		this.file = file;
		this.connection = connection;
		upload = null;
		this.reporter = reporter;
	}
	
	public CallableFileImport(List<FileItem> items, String fileUploadField, Connection connection,DatasetURIReporter<IQueryRetrieval<SourceDataset>> reporter) {
		this((File)null,connection,reporter);
		upload = new CallableFileUpload(items,fileUploadField) {
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
	public CallableFileImport(InputRepresentation input,  Connection connection,DatasetURIReporter<IQueryRetrieval<SourceDataset>> reporter) {
		this((File)null,connection,reporter);
		try {
			File file = null;
			if (input.getDownloadName()==null) {
		       file = File.createTempFile("ambit2_", ".sdf");
		       file.deleteOnExit();
			} else file = new File(
	        		String.format("%s/%s",
	        				System.getProperty("java.io.tmpdir"),
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
			if (file == null) if (upload != null) upload.call(); else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			if (file != null) return importFile(file);
			else throw new Exception("No file");
		} catch (Exception x) {
			throw x;
		}
	}
	
	public Reference importFile(File file) throws Exception {
		try {

			SourceDataset dataset = new SourceDataset(file.getName(), LiteratureEntry
					.getInstance(file.getName(), "File uploaded by user"));		
			dataset.setId(-1);
			final BatchDBProcessor batch = new BatchDBProcessor();
			batch.setConnection(connection);
			final RepositoryWriter writer = new RepositoryWriter();
			writer.setDataset(dataset);
			final ProcessorsChain<String, IBatchStatistics, IProcessor> chain = new ProcessorsChain<String, IBatchStatistics, IProcessor>();
			chain.add(writer);
			batch.setProcessorChain(chain);
			writer.setConnection(connection);
			IBatchStatistics stats = batch.process(new FileInputState(file));
			
			ReadDataset q = new ReadDataset();
			q.setValue(dataset);
			QueryExecutor<ReadDataset> x = new QueryExecutor<ReadDataset>();
			x.setConnection(connection);
			ResultSet rs = x.process(q);
			dataset = null;
			while (rs.next()) {
				dataset = q.getObject(rs);
				break;
			}
			x.closeResults(rs);
			x.setConnection(null);
			if (dataset== null) throw new ResourceException(Status.SUCCESS_NO_CONTENT);
			if (reporter == null)
				reporter = new DatasetURIReporter<IQueryRetrieval<SourceDataset>>();
			return new Reference(reporter.getURI(dataset));

		} catch (Exception x) {
			 throw new ResourceException(new Status(Status.SERVER_ERROR_INTERNAL,x.getMessage()));
		} finally {
			try { connection.close(); } catch (Exception x) {}
			connection = null;
		}
	}
	
}
