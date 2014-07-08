package ambit2.db.update.dataset;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryCondition;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.ISourceDataset;
import ambit2.base.data.SourceDataset;
import ambit2.db.readers.PropertyValue;
import ambit2.db.search.structure.AbstractStructureQuery;

public abstract class DatasetQueryFieldGeneric<T, C extends IQueryCondition> extends AbstractStructureQuery<ISourceDataset, PropertyValue<T>, IQueryCondition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4878852850705024770L;

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		
		if ((getFieldname()==null) || getFieldname().getID()<=0) throw new AmbitException("No dataset!");
		params.add(new QueryParam<Integer>(Integer.class, getFieldname().getID()));
		
		if (getValue()==null) throw new AmbitException("No feature!");	
		
		if (getValue().getProperty()==null || getValue().getProperty().getId()<=0) throw new AmbitException("No feature!");		
		params.add(new QueryParam<Integer>(Integer.class, getValue().getProperty().getId()));
		
		params.add(createSearchValueParam());
		return params;
	}
	protected abstract QueryParam<T> createSearchValueParam();
	protected abstract String getSqlDataset();
	protected abstract String getSqlRDataset();
	@Override
	public String getSQL() throws AmbitException {
		if ((getFieldname()==null) || getFieldname().getID()<=0) throw new AmbitException("No dataset!");
		if (getFieldname() instanceof SourceDataset)
			return getSqlDataset();
		else
			return getSqlRDataset();
	}

}
