package ambit2.rest.task;

import java.io.ObjectInputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.sql.Connection;
import java.util.logging.Level;

import net.idea.modbcum.i.batch.IBatchStatistics;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.processors.ProcessorsChain;
import net.idea.modbcum.p.batch.AbstractBatchProcessor;
import net.idea.restnet.c.task.ClientResourceWrapper;

import org.opentox.dsl.OTDataset;
import org.opentox.dsl.OTFeature;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.db.DbReaderStructure;
import ambit2.db.search.structure.AbstractStructureQuery;
import ambit2.rest.DBConnection;
import ambit2.rest.OpenTox;
import ambit2.rest.dataset.RDFStructuresReader;


public abstract class CallableQueryProcessor<Target,Result,USERID> extends CallableProtectedTask<USERID> {
	protected AbstractBatchProcessor batch; 
	protected Target target;
	protected Reference sourceReference;
	//protected AmbitApplication application;
	protected Context context;

	public CallableQueryProcessor(Form form,Context context,USERID token) {
		this(null,form,context,token);
	}
	public CallableQueryProcessor(Reference applicationRootReference,Form form,Context context,USERID token) {
		super(token);
		processForm(applicationRootReference,form);
		this.context = context;
	}
	protected void processForm(Reference applicationRootReference,Form form) {
		Object dataset = OpenTox.params.dataset_uri.getFirstValue(form);
		String[] xvars = OpenTox.params.feature_uris.getValuesArray(form);
		if (xvars != null) try {
			
			OTDataset ds = OTDataset.dataset(dataset.toString());
			for (String xvar :xvars) {
				String[] xx = xvar.split("\n");
				for (String x : xx )
					if (!x.trim().equals("")) 
						ds = ds.addColumns(OTFeature.feature(x));
			}
			dataset =  ds.getUri().toString();

		} catch (Exception x) {
			
		}
		this.sourceReference = dataset==null?null:new Reference(dataset.toString().trim());
	}
	@Override
	public TaskResult doCall() throws Exception {
		
		Context.getCurrentLogger().fine("Start()");
		Connection connection = null;
		try {
			DBConnection dbc = new DBConnection(context);
			connection = dbc.getConnection();
			try {
				target = createTarget(sourceReference);
			} catch (Exception x) {
				target = (Target)sourceReference;
			}
			
			batch = createBatch(target);
			
			if (batch != null) {
				batch.setCloseConnection(false);
				batch.setProcessorChain(createProcessors());
				try {
		    		if ((connection==null) || connection.isClosed()) throw new Exception("SQL Connection unavailable ");			
					batch.setConnection(connection);
					batch.open();
				} catch (Exception x) { connection = null;}
				/*
				batch.addPropertyChangeListener(AbstractBatchProcessor.PROPERTY_BATCHSTATS,new PropertyChangeListener(){
					public void propertyChange(PropertyChangeEvent evt) {
						context.getLogger().info(evt.getNewValue().toString());
						
					}
				});
				*/
				IBatchStatistics stats = runBatch(target);
			}
			return createReference(connection);
		} catch (Exception x) {
			Context.getCurrentLogger().log(Level.SEVERE,x.getMessage(),x);

			throw x;
		} finally {
			Context.getCurrentLogger().fine("Done");
			try { connection.close(); } catch (Exception x) {Context.getCurrentLogger().warning(x.getMessage());}
		}
		/*
		try {
    		//connection = application.getConnection((Request)null);
    		//if (connection.isClosed()) connection = application.getConnection((Request)null);			
			return createReference(connection);
		} catch (Exception x) {
			x.printStackTrace();
			throw x;
		} finally {
			try { connection.close(); } catch (Exception x) {}
		}		
		*/	
		
	}
	protected IBatchStatistics runBatch(Target target) throws Exception {
		return batch.process(target);
	}
	
	protected AbstractBatchProcessor createBatch(Target target) throws Exception{
		if (target == null) throw new Exception("");
		if (target instanceof AbstractStructureQuery) {
			DbReaderStructure reader = new DbReaderStructure();
			reader.setHandlePrescreen(true);
			return reader;
		} else
			return new RDFStructuresReader(target.toString());
	}
	protected abstract Target createTarget(Reference reference) throws Exception;
	protected abstract TaskResult createReference(Connection connection) throws Exception ;
	protected abstract ProcessorsChain<Result, IBatchStatistics, IProcessor> createProcessors() throws Exception;
	//protected abstract QueryURIReporter createURIReporter(Request request); 
	
	public static Object getQueryObject(Reference reference, Reference applicationRootReference, Context context) throws Exception {
		return getQueryObject(reference, applicationRootReference, context,null,null);
	}
	public static Object getQueryObject(Reference reference, Reference applicationRootReference, Context context,
			String cookies,String agent) throws Exception {
		CookieHandler.setDefault( new CookieManager( null, CookiePolicy.ACCEPT_ORIGINAL_SERVER) );
		if (!applicationRootReference.isParent(reference)) throw 
			new Exception(String.format("Remote reference %s %s",applicationRootReference,reference));
		ObjectInputStream in = null;
		HttpURLConnection connection = null;
		try {
			connection = 
				ClientResourceWrapper.getHttpURLConnection(reference.toString(),"GET",MediaType.APPLICATION_JAVA_OBJECT.toString());
			connection.setFollowRedirects(true);
			if (agent!=null) connection.setRequestProperty("User-Agent", agent);
			if (cookies!=null)
				connection.setRequestProperty("Cookie", cookies);
			if (HttpURLConnection.HTTP_OK== connection.getResponseCode()) {
				in = new ObjectInputStream(connection.getInputStream());
				Object object = in.readObject();
				return object;
			}
			return reference;
		} catch (Exception x) {
			throw x;
		} finally {
			try { if (in!=null) in.close();} catch (Exception x) {}
			try { if (connection!=null) connection.disconnect();} catch (Exception x) {}
		}
	}		

}
