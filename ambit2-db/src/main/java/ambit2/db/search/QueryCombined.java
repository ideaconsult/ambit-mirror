package ambit2.db.search;

import java.util.ArrayList;
import java.util.List;

import ambit2.core.exceptions.AmbitException;

public class QueryCombined<Q extends IQueryObject> extends ArrayList<Q> implements IQueryObject {
	protected boolean combine_as_and;
	protected Integer id = null;
	protected static final String union="\nunion\n";
	protected static final String join="\njoin\n";
	protected IQueryObject scope;
	/**
	 * 
	 */
	private static final long serialVersionUID = -439656773187695602L;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
		for (IQueryObject q: this) 
			q.setId(id);
		if (getScope() != null)
			getScope().setId(id);
	}

	public String getSQL() throws AmbitException {
		if (size() == 0)
			throw new AmbitException("Undefined query");
		if (combine_as_and)
			return getSQL_and();
		else
			return getSQL_or();
	}
	
	/**
	 * SQLs of simple queries, union-ed. Example:
<pre>
select ?,idstructure,1 from structure where idstructure <= ?
union
select ?,idstructure,1 from structure where idstructure between ? and ?
</pre> 
	 * @return
	 * @throws AmbitException
	 */
	protected String getSQL_or() throws AmbitException {
		StringBuffer b = new StringBuffer();
		String c = "";
		for (IQueryObject q : this) {
			b.append(c);
			b.append(q.getSQL());
			c = union;
		}
		if (scope == null)
			return b.toString();
		else {
			StringBuffer bb = new StringBuffer();
			c = "select QSCOPE.idquery,s.idchemical,idstructure,QSCOPE.selected as selected,QSCOPE.metric as metric from structure as s";
			bb.append(c);
			join(scope,"SCOPE",bb);
			bb.append(join);
			bb.append("(\n");
			bb.append(b.toString());
			bb.append(")\n");
			bb.append("as U\n");
			bb.append("using (idstructure)");
			return bb.toString();
		}
	}
	
	/**
	 * SQLs of simple queries, join-ed
	 * @return
	 * @throws AmbitException
	 */
	protected String getSQL_and() throws AmbitException {
		StringBuffer b = new StringBuffer();
/**
select Q1.id,idstructure,1 from structure as S
join
(select 1 as id,idstructure,1 from structure as S where idstructure <= 180) as Q1
using(idstructure)
join
(select 1 as id,idstructure,1 from structure where idstructure between 150 and 200)
as Q2
using(idstructure)
 */		String c = "select Q1.idquery,s.idchemical,idstructure,Q1.selected as selected,Q1.metric as metric from structure as s";
		b.append(c);
		if (scope != null) {
			join(scope,"SCOPE",b);
		}		
		for (int i=0; i < size();i++) {
			IQueryObject q = get(i);
			join(q,Integer.toString(i+1),b);
		}

		return b.toString();		
	}
	protected void join(IQueryObject q,String qname,StringBuffer b) throws AmbitException {
		b.append(join);
		b.append("(");
		b.append(q.getSQL());
		b.append(")\n");
		b.append("as Q");
		b.append(qname);
		b.append("\nusing (idstructure)");
	}
	public List<QueryParam> getParameters() throws AmbitException {
		setId(getId());
		ArrayList<QueryParam> param = new ArrayList<QueryParam>();
		if (scope != null) {
			List<QueryParam> p = scope.getParameters();
			for (int j=0; j < p.size(); j++) {
				QueryParam qp = p.get(j);
				//System.out.println(qp);
				param.add(qp);
			}			
		}
		for (int i=0; i < size(); i++) {
			IQueryObject q = get(i);
			List<QueryParam> p = q.getParameters();
			for (int j=0; j < p.size(); j++) {
				QueryParam qp = p.get(j);
				//System.out.println(qp);
				param.add(qp);
			}
		}
			
		return param;
	}

	public boolean isCombine_as_and() {
		return combine_as_and;
	}

	public void setCombine_as_and(boolean combine_as_and) {
		this.combine_as_and = combine_as_and;
	}

	public IQueryObject getScope() {
		return scope;
	}

	public void setScope(Q scope) {
		if (scope instanceof QueryCombined) return;
		this.scope = scope;
	}

}
