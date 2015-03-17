package ambit2.db.update.model;

import java.sql.ResultSet;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.PredictedVarsTemplate;
import ambit2.base.data.Template;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.StringCondition;

public abstract class AbstractModelQuery<F,V>  extends AbstractQuery<F, V, StringCondition, ModelQueryResults>  implements IQueryRetrieval<ModelQueryResults> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5670785871072092628L;
	protected static String sql = 
		"select idmodel,m.name,dataset,t1.idtemplate,t1.name,t2.idtemplate,t2.name,t3.idtemplate,t3.name,content,mediatype,algorithm,parameters,hidden,creator,null,stars\n"+
		"from models m join template t2 on t2.idtemplate=m.dependent left join template t1 on t1.idtemplate = m.predictors join template t3 on t3.idtemplate = m.predicted %s order by stars desc\n";

	protected static String sql_predicted = 
		"select idmodel,m.name,dataset,t1.idtemplate,t1.name,t2.idtemplate,t2.name,t3.idtemplate,t3.name,content,mediatype,algorithm,parameters,hidden,creator,group_concat(distinct(properties.comments)),stars\n"+
		"from models m join template t2 on t2.idtemplate=m.dependent left join template t1 on t1.idtemplate = m.predictors join template t3 on t3.idtemplate = m.predicted \n"+
		"join template_def t on t.idtemplate=t3.idtemplate\n"+
		"join properties on t.idproperty=properties.idproperty\n"+
		" %s\n"+
		"group by idmodel";
	
	public enum _models_criteria {
		id {
			public String getSQL() {
				return " idmodel = ? ";
			}
			@Override
			public boolean isEnabled(ModelQueryResults model,String endpoint) {
				return (model.getId()!=null) && (model.getId()>0);
			}
			@Override
			public QueryParam getParam(ModelQueryResults model,String endpoint) {
				return new QueryParam<Integer>(Integer.class,model.getId());
			}				
		},
		name {
			@Override
			public String getSQL() {
				return " m.name %s substr(?,1,255)";
			}
			@Override
			public boolean isEnabled(ModelQueryResults model,String endpoint) {
				return model.getName()!= null;
			}
			@Override
			public QueryParam getParam(ModelQueryResults model,String endpoint) {
				return new QueryParam<String>(String.class,model.getName());
			}				
		},
		dataset {
			@Override
				public String getSQL() {
					return " m.dataset = substr(?,1,255)";
				}	
			@Override
			public boolean isEnabled(ModelQueryResults model,String endpoint) {
				return model.getTrainingInstances()!=null;
			}	
			@Override
			public QueryParam getParam(ModelQueryResults model,String endpoint) {
				return new QueryParam<String>(String.class,model.getTrainingInstances());
			}			
		},
		algorithm {
			@Override
			public String getSQL() {
				return " m.algorithm = substr(?,1,255)";
			}
			@Override
			public int getIndex() {
				return 12;
			}
			@Override
			public boolean isEnabled(ModelQueryResults model,String endpoint) {
				return model.getAlgorithm()!=null;
			}	
			@Override
			public QueryParam getParam(ModelQueryResults model,String endpoint) {
				return new QueryParam<String>(String.class,model.getAlgorithm());
			}			
		},
		creator {
			@Override
			public String getSQL() {
				return " m.creator = substr(?,1,45)";
			}
			@Override
			public int getIndex() {
				return 15;
			}
			@Override
			public boolean isEnabled(ModelQueryResults model,String endpoint) {
				return model.getCreator()!=null;
			}	
			@Override
			public QueryParam getParam(ModelQueryResults model,String endpoint) {
				return new QueryParam<String>(String.class,model.getCreator());
			}
		},
		endpoint {
			@Override
			public boolean isEnabled(ModelQueryResults arg0, String arg1) {
				return arg0.getEndpoint() != null;
			}
			@Override
			public String getSQL() {
				return " properties.comments regexp substr(?,1,128)";
			}
			@Override
			public QueryParam getParam(ModelQueryResults model,String endpoint) {
				return new QueryParam<String>(String.class,model.getEndpoint());
			}			

			@Override
			public int getIndex() {
				return 16;
			}
		},		
		stars {
			@Override
			public boolean isEnabled(ModelQueryResults model, String arg1) {
				return (model.getStars()>=0);
			}
			@Override
			public String getSQL() {
				return " stars >= ? ";
			}
			@Override
			public QueryParam getParam(ModelQueryResults model,String endpoint) {
				return new QueryParam<Integer>(Integer.class,model.getStars());
			}			

			@Override
			public int getIndex() {
				return 17;
			}
		};
		public abstract boolean isEnabled(ModelQueryResults model,String endpoint);
		public abstract String getSQL();
		public int getIndex() {
			return ordinal()+1;
		}
		public QueryParam getParam(ModelQueryResults model,String endpoint) {
			return null;
		}
	}
	
	public ModelQueryResults getObject(ResultSet rs) throws AmbitException {
		ModelQueryResults q = new ModelQueryResults();
		try {
			
			q.setId(rs.getInt(1));
			q.setName(rs.getString(2));
			q.setTrainingInstances(rs.getString(3));
			Object idtemplate = rs.getObject(4);
			if (idtemplate==null) q.setPredictors(null);
			else {
				Template t = new Template(rs.getString(5));
				t.setId(rs.getInt(4));
				q.setPredictors(t);
			}
			idtemplate = rs.getObject(6);
			if (idtemplate==null) q.setDependent(null);
			else {
				Template t = new Template(rs.getString(7));
				t.setId(rs.getInt(6));
				q.setDependent(t);
			}		
//predicted
			idtemplate = rs.getObject(8);
			if (idtemplate==null) q.setDependent(null);
			else {
				PredictedVarsTemplate t = new PredictedVarsTemplate(rs.getString(9));
				t.setId(rs.getInt(8));
				q.setPredicted(t);
			}
			
			
			q.setContent(rs.getString(10));
			q.setContentMediaType(rs.getString(11));
			q.setAlgorithm(rs.getString(12));
			
			String params = rs.getString("parameters");
			q.setParameters(params==null?null:params.split("\t"));
			q.setHidden(rs.getBoolean(14));
			
			q.setCreator(rs.getString(_models_criteria.creator.getIndex()));
			q.setEndpoint(rs.getString(_models_criteria.endpoint.getIndex()));
		
		} catch (Exception x) {
			return null;
		}
		try {
			q.setStars(rs.getInt(_models_criteria.stars.name()));
		} catch (Exception x) {
			q.setStars(-1);
		}
		return q;
	}
	
	@Override
	public String toString() {
		return getValue()==null?"Models":String.format("%s",getValue().toString());
	}
}
