package ambit2.rest.task;

import java.sql.Connection;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;

import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.processors.ProcessorsChain;
import ambit2.core.data.model.Algorithm;
import ambit2.db.UpdateExecutor;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.search.QueryExecutor;
import ambit2.db.update.model.CreateModel;
import ambit2.db.update.model.ReadModel;
import ambit2.rest.model.builder.ModelBuilder;

public abstract class CallableModelCreator<DATA,Item,Builder extends ModelBuilder<DATA,Algorithm,ModelQueryResults>>  extends	CallableQueryProcessor<Object, Item> {
	protected Algorithm algorithm;
	protected Builder builder; 
	protected ModelQueryResults model;
	
	public ModelQueryResults getModel() {
		return model;
	}
	public CallableModelCreator(
			Form form,
			Context context,
			Algorithm algorithm,
			Builder builder) {
		super(form, context);
		this.algorithm = algorithm;
		this.builder = builder;
		
	}
	/**
	 * Writes the model into database and returns a reference
	 */
	@Override
	protected Reference createReference(Connection connection) throws Exception {
		UpdateExecutor<CreateModel> x = new UpdateExecutor<CreateModel>();
		try {
			model = createModel();
			CreateModel update = new CreateModel(model);
			
			x.setConnection(connection);
			x.process(update);
			
			if ((model.getId()==null) || (model.getId()<0)) {
				ReadModel q = new ReadModel();
				q.setFieldname(model.getName());
				QueryExecutor<ReadModel> exec = new QueryExecutor<ReadModel>();
				exec.setConnection(connection);
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
					try {rs.close(); } catch (Exception xx) { }
				}
				
			}
			return new Reference(builder.getModelReporter().getURI(model));
		} catch (Exception e) {
			Context.getCurrentLogger().severe(e.getMessage());
			throw e;
		} finally {
			try {x.close();} catch (Exception xx){}
		}
	}
	
	
	protected ModelQueryResults createModel() throws Exception {
		return builder.process(algorithm);
	}
	
	@Override
	protected ProcessorsChain<Item, IBatchStatistics, IProcessor> createProcessors()
			throws Exception {
		return null;
	}	
}
