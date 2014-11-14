package ambit2.db.search.structure;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryObject;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.interfaces.IStructureRecord;

/**
 * Complement of the scope query
 * @author nina
 *
 */
public class QueryComplement extends QueryCombinedStructure {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2818316465477137764L;
	
	@Override
	public String getSQL() throws AmbitException {
		StringBuilder b = new StringBuilder();
		b.append(getScope().getSQL());
		b.append(String.format("\n and %s not in ",isChemicalsOnly()?"idchemical":"idstructure"));
		String union = "";
		b.append("(\n");
		for (IQueryRetrieval<IStructureRecord> q : queries) { 
			b.append(union); union="\nunion\n";
			b.append(q.getSQL());
		}
		b.append("\n)");
		return b.toString();
	}
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		setId(getId());
		ArrayList<QueryParam> param = new ArrayList<QueryParam>();
		if (scope == null) throw new AmbitException("Missing query!");
		List<QueryParam> p = scope.getParameters();
		if (p!=null) for (int j=0; j < p.size(); j++) param.add(p.get(j));
	
		for (int i=0; i < queries.size(); i++) {
			IQueryObject<IStructureRecord> q = queries.get(i);
			List<QueryParam> prm = q.getParameters();
			for (int j=0; j < prm.size(); j++) param.add(prm.get(j));
		}
			
		return param;
	}	
}
