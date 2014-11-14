package ambit2.db.reporters;

import net.idea.modbcum.i.IQueryCondition;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.batch.AbstractBatchProcessor;
import net.idea.modbcum.r.QueryReporter;
import ambit2.base.data.Property;
import ambit2.base.data.PropertyAnnotation;
import ambit2.base.data.PropertyAnnotations;
import ambit2.db.DbReader;
import ambit2.db.processors.MasterDetailsProcessor;
import ambit2.db.update.propertyannotations.ReadPropertyAnnotations;

public abstract class QueryProperyReporter<Q extends IQueryRetrieval<Property>,Output> extends QueryReporter<Property, Q, Output> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2662159144067243671L;

	public QueryProperyReporter() {
		super();

		IQueryRetrieval<PropertyAnnotation> queryP = new ReadPropertyAnnotations(); 
		MasterDetailsProcessor<Property,PropertyAnnotation,IQueryCondition> annotationsReader = new MasterDetailsProcessor<Property,PropertyAnnotation,IQueryCondition>(queryP) {
			@Override
			protected Property processDetail(Property master,
					PropertyAnnotation detail) throws Exception {
				
				if (master.getAnnotations()==null) master.setAnnotations(new PropertyAnnotations());
				master.getAnnotations().add(detail);
				if ((detail.getObject()!=null) && "acceptValue".equals(detail.getPredicate())) 
					master.addAllowedValue(detail.getObject().toString());
				return master;
			}
		};
		annotationsReader.setCloseConnection(false);
		getProcessors().add(annotationsReader);
	}
	@Override
	public void footer(Output output, Q query) {
		
	}

	@Override
	public void header(Output output, Q query) {
	}

	public void open() throws DbAmbitException {
	}

	protected AbstractBatchProcessor<IQueryRetrieval<Property>, Property> createBatch(Q query) {
		DbReader<Property> reader = new DbReader<Property>();
		reader.setHandlePrescreen(false);
		return reader;
	}
}
