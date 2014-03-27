package ambit2.db.processors;

import java.io.File;
import java.sql.Connection;
import java.util.List;
import java.util.logging.Level;

import net.idea.i5.cli.Container;
import net.idea.i5.cli.ContainerClient;
import net.idea.i5.cli.I5LightClient;
import net.idea.i5.cli.QueryToolClient;
import net.idea.i5.cli.QueryToolClient.PredefinedQuery;
import net.idea.i5.io.I5ZReader;
import net.idea.i5.io.IQASettings;
import net.idea.i5.io.QASettings;
import net.idea.opentox.cli.IIdentifiableResource;

import org.openscience.cdk.io.IChemObjectReaderErrorHandler;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.SourceDataset;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.ProcessorsChain;
import ambit2.core.io.FileInputState;
import ambit2.core.io.IRawReader;
import ambit2.core.processors.structure.key.PropertyKey;
import ambit2.core.processors.structure.key.ReferenceSubstanceUUID;
import ambit2.rest.DBConnection;
import ambit2.rest.dataset.DatasetURIReporter;
import ambit2.rest.substance.SubstanceURIReporter;
import ambit2.rest.task.CallableQueryProcessor;
import ambit2.rest.task.TaskResult;

public class CallableSubstanceI5Query<USERID> extends CallableQueryProcessor<FileInputState, IStructureRecord,USERID> implements IQASettings {
	protected QASettings qaSettings;
	protected SubstanceRecord importedRecord;
	protected SourceDataset dataset;
	
	boolean useUUID;
	protected String substanceUUID;
	protected String extIDType;
	protected String extIDValue;
	
	@Override
	public QASettings getQASettings() {
		if (qaSettings==null) qaSettings = new QASettings(false);
		return qaSettings;
	}
	@Override
	public void setQASettings(QASettings qa) {
		this.qaSettings = qa;
	}

	
	public CallableSubstanceI5Query(
			Reference applicationRootReference,
			Form form,
			Context context,
			SubstanceURIReporter substanceReporter,
			DatasetURIReporter datasetURIReporter,
			USERID token) {
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
		//[(option,UUID), (uuid,ZZZZZZZZZZ), (extidtype,CompTox), (extidvalue,Ambit Transfer), (i5server,null), (i5user,null), (i5pass,null)]
		substanceUUID = form.getFirstValue("uuid");
		useUUID = "UUID".equals(form.getFirstValue("option"));
		extIDType = form.getFirstValue("extidtype");
		extIDValue = form.getFirstValue("extidvalue");
		String qaEnabled = form.getFirstValue("qaenabled");
		try {
			if ("on".equals(qaEnabled)) getQASettings().setEnabled(true);
			if ("yes".equals(qaEnabled)) getQASettings().setEnabled(true);
			if ("checked".equals(qaEnabled)) getQASettings().setEnabled(true);
		} catch (Exception x) {
			getQASettings().setEnabled(true);
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
			return new TaskResult(String.format("%s/substance/%s",sourceReference,substanceUUID));
		} else {
			return new TaskResult(String.format("%s/substance/?type=%s&search=%s",sourceReference,
					Reference.encode(extIDType),Reference.encode(extIDValue)));
		}
	}

	
	@Override
	public TaskResult doCall() throws Exception {
		
		Context.getCurrentLogger().fine("Start()");
		Connection connection = null;
		DBConnection dbc = null;
		I5LightClient i5=null;
		DBSubstanceWriter writer = null;
		try {
			
			if (useUUID) {
				if (substanceUUID==null || "".equals(substanceUUID)) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Empty UUID");
				dbc = new DBConnection(context);
				i5 = new I5LightClient(dbc.getProperty("i5.server"));
				i5.login(dbc.getProperty("i5.user"),dbc.getProperty("i5.pass"));
				ContainerClient ccli = i5.getContainerClient();
				if (substanceUUID.indexOf("/")<0) substanceUUID = substanceUUID + "/0";
				List<IIdentifiableResource<String>> container = ccli.get(substanceUUID);
				connection = dbc.getConnection();
				writer = new DBSubstanceWriter(DBSubstanceWriter.datasetMeta(),new SubstanceRecord());		
				writer.setCloseConnection(false);
				writer.setConnection(connection);
		        writer.open();
				IIdentifiableResource<String> result = processContainer(container, writer);
		        
			} else { //query
				if (extIDType==null || extIDValue==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Invalid query");
				dbc = new DBConnection(context);
				i5 = new I5LightClient(dbc.getProperty("i5.server"));
				i5.login(dbc.getProperty("i5.user"),dbc.getProperty("i5.pass"));
				QueryToolClient cli = i5.getQueryToolClient();
				ContainerClient ccli = i5.getContainerClient();				
				List<IIdentifiableResource<String>> queryResults = cli.executeQuery(PredefinedQuery.query_by_it_identifier,extIDType,extIDValue);
				
				connection = dbc.getConnection();
				writer = new DBSubstanceWriter(DBSubstanceWriter.datasetMeta(),new SubstanceRecord());		
				writer.setCloseConnection(false);
				writer.setConnection(connection);
		        writer.open();

				for (IIdentifiableResource<String> item : queryResults) {
					List<IIdentifiableResource<String>> container = ccli.get(item.getResourceIdentifier());
					processContainer(container, writer);					
				}
			}
			return createReference(connection);
		} catch (Exception x) {
			Context.getCurrentLogger().log(Level.SEVERE,x.getMessage(),x);

			throw x;
		} finally {
			Context.getCurrentLogger().fine("Done");
			try { if (i5!=null) i5.logout(); } catch (Exception x) {Context.getCurrentLogger().warning(x.getMessage());}
			try { if (i5!=null) i5.close(); } catch (Exception x) {Context.getCurrentLogger().warning(x.getMessage());}
			try {writer.setConnection(null);} catch (Exception x) {}
			try {writer.close();} catch (Exception x) {}
			try { if (connection!=null) connection.close(); } catch (Exception x) {Context.getCurrentLogger().warning(x.getMessage());}
			
		}

		
	}
	
	private IIdentifiableResource<String> processContainer(List<IIdentifiableResource<String>> container,DBSubstanceWriter writer) {
		IIdentifiableResource<String> result = null;
		for (IIdentifiableResource<String> item: container)  {
			I5ZReader reader = null;
			File file = null;
			if (item instanceof Container) try {
				file = ((Container)item).getIpzarchive();
				if (file.exists()) {
					Context.getCurrentLogger().fine(file.getAbsolutePath());
					reader = getReader(((Container)item).getIpzarchive());
					reader.setQASettings(getQASettings());
					write(reader, writer, new ReferenceSubstanceUUID());
					result = item;
				}
			} catch (Exception x) {
				Context.getCurrentLogger().log(Level.SEVERE,x.getMessage(),x);
			} finally {
				try {if (reader!=null) reader.close();} catch (Exception x) {}
				try {file.delete(); } catch (Exception x) {}
			}
		}	
		return result;
	}
	
	private I5ZReader getReader(File i5z) throws Exception {
		 I5ZReader reader = new I5ZReader(i5z);
		    reader.setErrorHandler(new IChemObjectReaderErrorHandler() {
				
				@Override
				public void handleError(String message, int row, int colStart, int colEnd,
						Exception exception) {
				}
				
				@Override
				public void handleError(String message, int row, int colStart, int colEnd) {
				}
				
				@Override
				public void handleError(String message, Exception exception) {
					exception.printStackTrace();
				}
				
				@Override
				public void handleError(String message) {
				}
			});
		    return reader;
	}
	
	public int write(IRawReader<IStructureRecord> reader,DBSubstanceWriter writer,PropertyKey key) throws Exception  {
		try {
			int records = 0;
			while (reader.hasNext()) {
	            Object record = reader.next();
	            if (record==null) continue;
	            if (record instanceof IStructureRecord)
	            	writer.process((IStructureRecord)record);
	            records++;
			}
			return records;
		} catch (Exception x) {
			throw x;
		} finally {
			
		}

	}	

}
