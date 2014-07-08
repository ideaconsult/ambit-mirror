package ambit2.db.update.structure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.structure.AbstractStructureQuery;

/**
 * idchemical by dataset
 * @author nina
 *
 */
public class ChemicalByDataset extends AbstractStructureQuery<String,Integer,NumberCondition> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8329798753353233477L;
	public final static String sql = 
		"select idchemical from structure join struc_dataset using(idstructure) where id_srcdataset %s %s";
	public ChemicalByDataset(SourceDataset dataset) {
		this(dataset==null?null:dataset.getId());
	}
	public ChemicalByDataset() {
		this((SourceDataset)null);
	}
	public ChemicalByDataset(Integer id) {
		super();
		setValue(id);
		setCondition(NumberCondition.getInstance("="));
	}	
	public String getSQL() throws AmbitException {
		return 	String.format(sql,getCondition().getSQL(),getValue()==null?"":"?");
	}
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getValue()!=null)
			params.add(new QueryParam<Integer>(Integer.class,getValue()));
		return params;
	}
	@Override
	public String toString() {
		if (getValue()==null) return "Datasets";
		return String.format("Dataset %s %s",getCondition().toString(),getValue()==null?"":getValue());
	}
	public IStructureRecord getObject(ResultSet rs) throws AmbitException {
		try {
			IStructureRecord record = new StructureRecord();
			record.setIdchemical(rs.getInt(1));
			record.setIdstructure(-1);
			return record;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}

}