package ambit2.db.chemrelation;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.QueryParam;
import ambit2.db.update.AbstractUpdate;

public abstract class AbstractUpdateStructureRelation  extends AbstractUpdate<IStructureRecord,IStructureRecord> {
	
	
	protected String relation;
	protected Double metric;
	public Double getMetric() {
		return metric;
	}
	public void setMetric(Double metric) {
		this.metric = metric;
	}
	public String getRelation() {
		return relation;
	}
	public void setRelation(String relation) {
		this.relation = relation;
	}
	public AbstractUpdateStructureRelation() {
		this(null,null,null,null);
	}
	public AbstractUpdateStructureRelation(IStructureRecord structure1,IStructureRecord structure2,
										   String relation, Double metric) {
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
		params1.add(new QueryParam<String>(String.class, getRelation()));
		return params1;
	}
}
