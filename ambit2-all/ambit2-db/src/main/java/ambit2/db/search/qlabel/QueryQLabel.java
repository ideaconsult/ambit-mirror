package ambit2.db.search.qlabel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.AmbitUser;
import ambit2.base.data.QLabel;
import ambit2.base.data.QLabel.QUALITY;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.NumberCondition;

public class QueryQLabel extends AbstractQuery<AmbitUser, IStructureRecord, NumberCondition, QLabel>  implements IQueryRetrieval<QLabel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6574002693997936309L;
	protected static String sql = "select idstructure,quality_structure.user_name,quality_structure.label,text from structure left join quality_structure using(idstructure) where idstructure %s ? %s";
	protected static String sql_where = "and quality_structure.user_name = ?";
	

	public QueryQLabel(AmbitUser user, IStructureRecord record) {
		setFieldname(user);
		setCondition(NumberCondition.getInstance("="));
		setValue(record);
	}
	
	public QueryQLabel() {
		this(null,null);
	}
	public List<QueryParam> getParameters() throws AmbitException {
		if ((getValue()==null) || (getValue().getIdstructure()<=0)) throw new AmbitException("Structure not defined");
		
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getValue().getIdstructure()));
		if (getFieldname() != null) 
			params.add(new QueryParam<String>(String.class, getFieldname().getName()));		
		return params;		
	}

	public String getSQL() throws AmbitException {
		if ((getValue()==null) || (getValue().getIdstructure()<=0)) throw new AmbitException("Structure not defined");
		
		return String.format(sql,getCondition(),
				(getFieldname()==null)?"":sql_where
				);
	}

	public double calculateMetric(QLabel object) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isPrescreen() {
		return false;
	}

	public QLabel getObject(ResultSet rs) throws AmbitException {
		try {
			QLabel label = new QLabel();
			label.setUser(new AmbitUser(rs.getString(2)));			
			label.setLabel(QUALITY.valueOf(rs.getString(3)));
			label.setText(rs.getString(4));
			return label;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}
	@Override
	public String toString() {
		return "Quality labels";
	}
	
}
