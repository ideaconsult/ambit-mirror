package ambit2.db.simiparity.space;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.conditions.StringCondition;
import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.StructureRecord;
import ambit2.base.data.substance.SubstanceProperty;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.AbstractQuery;

/**
 * Reads {@link QMapSpace}
 * @author nina
 *
 */

public class QueryQMapSpace extends AbstractQuery<IStructureRecord,QMap,StringCondition,QMapSpace>  implements IQueryRetrieval<QMapSpace>{
	private QMap qmap;
	private QMapSpace qspace;
	private int threshold_a=0; //set to -1 to view all
	public int getThreshold_a() {
		return threshold_a;
	}

	public void setThreshold_a(int threshold_a) {
		this.threshold_a = threshold_a;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -5648740520878436166L;
	private static String sql = 
		"SELECT idsasmap,id_srcdataset,q.idproperty,threshold_dact,threshold_sim," +
		"idchemical,a,b,c,d,fisher,g2,value_num,\n" +
		"p.name propertyname,p.units,p.comments,p.idreference\n"+
		"from qsasheader q\n"+
		"join qsasmap4 using(idsasmap)\n"+
		"left join property_values v using(idchemical)\n"+
		"left join properties p on v.idproperty=p.idproperty\n"+
		//"where v.idproperty=q.idproperty and p.idproperty=q.idproperty and g2>0 and a>0\n"+
		"where v.idproperty=q.idproperty and p.idproperty=q.idproperty and g2>0 and a>?\n"+
		"%s\n"+
		"order by q.id_srcdataset,q.idproperty\n";	
	
	private static final String sql_study = 
			"SELECT idsasmap,id_srcdataset,-1,threshold_dact,threshold_sim,\n"+
			"idchemical,a,b,c,d,fisher,g2,null,\n"+
			"reference propertyname,null,guidance,null,null value_num\n"+ //add interpretation-result
			"from qsasheader q\n"+
			"join qsasmap4 using(idsasmap)\n"+
			"where g2>0 and a>?\n"+
			"%s\n";

	protected StringBuilder conditionString=null;
	
	public QueryQMapSpace() {
		super();
		qmap = new QMap();
		qmap.setDataset(new SourceDataset());
		qmap.setProperty(new Property(null));
		qspace = new QMapSpace(qmap);
		qspace.setRecord(new StructureRecord());
		setPageSize(100000);
	}

	@Override
	public String getSQL() throws AmbitException {
		String the_sql = sql_study; 
		if (getValue()!=null) {
			if (getValue().getProperty() instanceof SubstanceProperty) the_sql = sql_study;
			if ((getValue().getId()>0)) {
				initCondition();
				conditionString.append(" and idsasmap=?");			
			} else {
				if ((getValue().getDataset()!=null) && (getValue().getDataset().getID()>0)) {
					initCondition();
					conditionString.append(" and id_srcdataset=?");			
				}
				if ((getValue().getProperty()!=null) && (getValue().getProperty().getId()>0)) {
					initCondition();
					conditionString.append(" and q.idproperty=?");					
				}		
			}
		}

		return String.format(the_sql,conditionString);
	}
	protected boolean initCondition() {
		if (conditionString==null) {conditionString = new StringBuilder(); return true;}
		else return false;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class,threshold_a));
		if (getValue()!=null) {
			if ((getValue().getId()>0)) {
				params.add(new QueryParam<Integer>(Integer.class,getValue().getId()));
				return params;
			}	
			if ((getValue().getDataset()!=null) && (getValue().getDataset().getID()>0)) {
				params.add(new QueryParam<Integer>(Integer.class,getValue().getDataset().getID()));
			}
			if ((getValue().getProperty()!=null) && (getValue().getProperty().getId()>0)) {
				params.add(new QueryParam<Integer>(Integer.class,getValue().getProperty().getId()));
			}			
		} else throw new AmbitException("No query parameters specified");
		return params;
	}

	

	@Override
	public boolean isPrescreen() {
		return false;
	}
	@Override
	public QMapSpace getObject(ResultSet rs) throws AmbitException {
		try {
			qspace.clear();
			qmap.setId(rs.getInt(1));
			qmap.getDataset().setID(rs.getInt(2));
			qmap.setProperty(new Property(rs.getString("propertyname")));
			qmap.getProperty().setId(rs.getInt(3));
			qmap.setActivityThreshold(rs.getDouble(4));
			qmap.setSimilarityThreshold(rs.getDouble(5));
			qspace.getRecord().setIdchemical(rs.getInt(6));
			qspace.getRecord().setIdstructure(-1);
			qspace.getRecord().setRecordProperty(qmap.getProperty(),rs.getDouble("value_num"));
			qspace.setG2(rs.getDouble("g2"));
			qspace.setA(rs.getDouble("a"));
			qspace.setB(rs.getDouble("b"));
			qspace.setC(rs.getDouble("c"));
			qspace.setD(rs.getDouble("d"));
			qspace.setFisher(rs.getDouble("fisher"));
			return qspace;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	@Override
	public double calculateMetric(QMapSpace object) {
		return 0;
	}



}