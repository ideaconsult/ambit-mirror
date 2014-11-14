package ambit2.db.chemrelation;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.interfaces.IStructureRecord;

public abstract class AbstractUpdateStructureRelation<S extends IStructureRecord,O extends IStructureRecord,RELATION,METRIC>  extends AbstractUpdate<S,O> {
	
	
	protected RELATION relation;
	protected METRIC metric;
	public METRIC getMetric() {
		return metric;
	}
	public void setMetric(METRIC metric) {
		this.metric = metric;
	}
	public RELATION getRelation() {
		return relation;
	}
	public void setRelation(RELATION relation) {
		this.relation = relation;
	}
	public AbstractUpdateStructureRelation() {
		this(null,null,null,null);
	}
	public AbstractUpdateStructureRelation(S structure1,O structure2,
			RELATION relation, METRIC metric) {
		super();
		setGroup(structure1);
		setObject(structure2);
		setRelation(relation);
		setMetric(metric);
	}

	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		params1.add(new QueryParam<Integer>(Integer.class, getGroup().getIdchemical()));
		params1.add(new QueryParam<Integer>(Integer.class, getObject().getIdchemical()));
		params1.add(new QueryParam<String>(String.class, getRelation().toString()));
		return params1;
	}
}
