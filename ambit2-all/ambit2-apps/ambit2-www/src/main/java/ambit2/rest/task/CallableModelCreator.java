package ambit2.rest.task;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Iterator;

import net.idea.modbcum.i.batch.IBatchStatistics;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.processors.ProcessorsChain;

import org.opentox.aa.opensso.OpenSSOToken;
import org.restlet.Context;
import org.restlet.data.Form;

import ambit2.base.data.Property;
import ambit2.base.data.PropertyAnnotation;
import ambit2.base.data.Template;
import ambit2.core.data.model.Algorithm;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.db.UpdateExecutor;
import ambit2.db.search.QueryExecutor;
import ambit2.db.update.model.CreateModel;
import ambit2.db.update.model.ReadModel;
import ambit2.db.update.propertyannotations.CreatePropertyAnnotation;
import ambit2.rest.aa.opensso.OpenSSOServicesConfig;
import ambit2.rest.model.builder.ModelBuilder;

public abstract class CallableModelCreator<DATA,Item,Builder extends ModelBuilder<DATA,Algorithm,ModelQueryResults>,USERID>  extends	CallableQueryProcessor<Object, Item,USERID> {
	protected Algorithm algorithm;
	protected Builder builder; 
	public Builder getBuilder() {
		return builder;
	}
	public void setBuilder(Builder builder) {
		this.builder = builder;
	}

	protected ModelQueryResults model;
	protected boolean newModel = true;
	protected CreatePropertyAnnotation annotationsWriter;
	
	public ModelQueryResults getModel() {
		return model;
	}
	public CallableModelCreator(
			Form form,
			Context context,
			Algorithm algorithm,
			Builder builder,
			USERID token) {
		super(form, context,token);
		this.algorithm = algorithm;
		setBuilder(builder);
		
	}
	
	protected void writeAnnotations(Template properties, UpdateExecutor exec) throws Exception {
		
		Iterator<Property> i = properties.getProperties(true);
		while (i.hasNext()) {
			Property property = i.next();
	    	if (property.getAnnotations()!=null) try {
	    		if (annotationsWriter==null) annotationsWriter=new CreatePropertyAnnotation();
	    		annotationsWriter.setGroup(property);

	    		for (PropertyAnnotation a:property.getAnnotations()) {
	    			if (a.getObject() instanceof Property) {
	    				//COULD be done via sql generating the URI, given existing property name and reference
	    				Property linkedProperty = (Property) a.getObject();
	    				if (linkedProperty.getId()<=0) {
	    			    	//propertyWriter.setObject(linkedProperty);
	    			    	//exec.process(propertyWriter);
	    					//TODO
	    				}
	    				if (linkedProperty.getId()>0) {
	    					PropertyAnnotation<String> pa = new PropertyAnnotation<String>();
	    					pa.setPredicate(a.getPredicate());
	    					pa.setType(a.getType());
	    					pa.setIdproperty(property.getId());
	    					pa.setObject(String.format("/feature/%d",linkedProperty.getId()));
			    			annotationsWriter.setObject(pa);
			    			exec.process(annotationsWriter);
	    				}
		    		} else {
		    			annotationsWriter.setObject(a);
		    			exec.process(annotationsWriter);
	    			}
		    	}

	    	} catch (Exception x) {
	    		//do smth
		    		throw x;
		    }
		}
	}
	/**
	 * Writes the model into database and returns a reference
	 */
	@Override
	protected TaskResult createReference(Connection connection) throws Exception {
		UpdateExecutor x = new UpdateExecutor();
		try {
			model = createModel();
			
			CreateModel update = new CreateModel(model);
			
			x.setConnection(connection);
			Integer i = (Integer) x.process(update);
			
			writeAnnotations(model.getPredicted(), x);
			
			newModel = i>0;
			if ((model.getId()==null) || (model.getId()<0)) {
				ReadModel q = new ReadModel();
				q.setFieldname(model.getName());
				QueryExecutor<ReadModel> exec = new QueryExecutor<ReadModel>();
				exec.setConnection(connection);
				exec.setCloseConnection(false);
				java.sql.ResultSet rs = null;
				try {
					rs = exec.process(q);
					while (rs.next()) {
						ModelQueryResults result = q.getObject(rs);
						model.setId(result.getId());
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {rs.close(); rs = null;} catch (Exception xx) { }
					try {exec.closeResults(null); exec = null;} catch (Exception xx) { }
				}
				
			}
			return new TaskResult(builder.getModelReporter().getURI(model),newModel);
		} catch (Exception e) {
			Context.getCurrentLogger().severe(e.getMessage());
			throw e;
		} finally {
			try {x.close();} catch (Exception xx){}
		}
	}
	
	protected String getUser() throws Exception {
		if (getToken()==null) return "guest";
		OpenSSOToken ssoToken = new OpenSSOToken(OpenSSOServicesConfig.getInstance().getOpenSSOService());
		ssoToken.setToken(getToken());
		Hashtable<String,String> results = new Hashtable<String, String>();
		ssoToken.getAttributes(new String[] {"uid"}, results);
		return results.get("uid");
	}
	protected ModelQueryResults createModel() throws Exception {
		ModelQueryResults model = builder.process(algorithm);
		if (model != null) try {
			model.setCreator(getUser());
		} catch (Exception x) {
			model.setCreator(x.getMessage());
		}
		return model;
	}
	
	@Override
	protected ProcessorsChain<Item, IBatchStatistics, IProcessor> createProcessors()
			throws Exception {
		return null;
	}	
}
