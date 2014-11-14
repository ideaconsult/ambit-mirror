package ambit2.db.readers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;

import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.EQCondition;

public class RetrieveDescriptor extends AbstractQuery<IMolecularDescriptor,IStructureRecord,EQCondition,DescriptorValue> implements IQueryRetrieval<DescriptorValue> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2275940362173809147L;
	public static final String sql = "SELECT idproperty,title,url,name,value_num,user_name FROM property_values join properties using(idproperty) join catalog_references using(idreference) where idstructure=? and value_num is not null";
	public String getSQL() throws AmbitException {
		return sql;
	}

	public List<QueryParam> getParameters() throws AmbitException {
		if (getValue() == null) throw new AmbitException("Structure not defined");
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getValue().getIdstructure()));
		return params;		
	}

	public DescriptorValue getObject(ResultSet rs) throws AmbitException {
		try {
			DescriptorValue v = new DescriptorValue(
					new DescriptorSpecification(null,null,rs.getString("title"),null),
					new String[] {},
					new Object[] {},
					new DoubleResult(rs.getDouble("value_num")),
					new String[] {rs.getString("name")}
					);

			return v;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}
	public double calculateMetric(DescriptorValue object) {
		return 1;
	}
	public boolean isPrescreen() {
		return false;
	}	


}
