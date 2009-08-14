package ambit2.rest.task;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.restlet.data.Reference;
import org.restlet.data.Status;
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
	public CallableFileImport(File file, Connection connection) {
		this.file = file;
		this.connection = connection;
		upload = null;
	}
	
	public CallableFileImport(List<FileItem> items, String fileUploadField, Connection connection) {
		this(null,connection);
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
	public Reference call() throws Exception {
		try {
			if (file == null) upload.call();
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
			DatasetURIReporter<IQueryRetrieval<SourceDataset>> reporter = new DatasetURIReporter<IQueryRetrieval<SourceDataset>>();
			return new Reference(reporter.getURI(dataset));

		} catch (Exception x) {
			 throw new ResourceException(new Status(Status.SERVER_ERROR_INTERNAL,x.getMessage()));
		} finally {
			try { connection.close(); } catch (Exception x) {}
			connection = null;
		}
	}
	
}
