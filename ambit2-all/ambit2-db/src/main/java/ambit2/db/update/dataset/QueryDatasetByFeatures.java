package ambit2.db.update.dataset;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.QueryParam;

public class QueryDatasetByFeatures extends AbstractReadDataset<Property> {
	protected String sql = 
	"SELECT id_srcdataset,name,user_name,idreference,title,url from	src_dataset	join catalog_references using(idreference)\n"+
	"join  template_def using(idtemplate)\n"+
	"where idproperty in (\n"+
	"select idproperty from properties %s\n"+
	")\n"+
	"%s\n"+
	"group by id_srcdataset\n";
	
	protected enum c_feature {
		featureid {
			@Override
			public String getSQL() {
				return "idproperty=?";
			}
			@Override
			public boolean isDefined(Property p) {
				return p.getId()>0;
			}
			@Override
			public QueryParam getParameter(Property p) {
				return new QueryParam<Integer>(Integer.class,p.getId());
			}
			@Override
			public String explain(Property p) {
				if (isDefined(p)) return String.format("Datasets with feature id=%d",p.getId());
				else return "";
			}
		},
		featurename {
			@Override
			public String getSQL() {
				return "name regexp ?";
			}
			@Override
			public boolean isDefined(Property p) {
				return p.getName()!=null;
			}
			@Override
			public QueryParam getParameter(Property p) {
				return new QueryParam<String>(String.class,p.getName());
			}
			@Override
			public String explain(Property p) {
				if (isDefined(p)) return String.format("Datasets with feature name=%s",p.getName());
				else return "";
			}
		},
		featuresameas {
			@Override
			public String getSQL() {
				return "comments=?";
			}
			@Override
			public boolean isDefined(Property p) {
				return p.getLabel()!=null;
			}			
			@Override
			public QueryParam getParameter(Property p) {
				return new QueryParam<String>(String.class,p.getLabel());
			}
			@Override
			public String explain(Property p) {
				if (isDefined(p)) return String.format("Datasets with feature sameas=%s",p.getLabel());
				else return "";
			}			
		},
		featurehassource {
			@Override
			public String getSQL() {
				return "title=?";
			}
			@Override
			public boolean needJoin() {
				return true;
			}
			@Override
			public boolean isDefined(Property p) {
				return (p.getReference()!= null) && (p.getTitle() != null);
			}
			@Override
			public QueryParam getParameter(Property p) {
				return new QueryParam<String>(String.class,p.getTitle());
			}
			@Override
			public String explain(Property p) {
				if (isDefined(p)) return String.format("Datasets with feature hasSource=%s",p.getTitle());
				else return "";
			}
		},
		featuretype {
			@Override
			public String getSQL() {
				return "FIND_IN_SET(?, ptype)";
			}
			@Override
			public boolean isDefined(Property p) {
				return p.getClazz() != null;
			}
			@Override
			public QueryParam getParameter(Property p) {
				return new QueryParam<String>(String.class,(p.getClazz().equals(String.class))?"STRING":"NUMERIC");
			}			
			@Override
			public String explain(Property p) {
				if (isDefined(p)) return String.format("Datasets with feature of type=%s",p.getClazz().equals(String.class)?"STRING":"NUMERIC");
				else return "";
			}
		};
		
		public abstract String getSQL();
		public abstract String explain(Property p);
		public boolean needJoin() {
			return false;
		}
		public boolean isDefined(Property p) {
			return false;
		}
		public abstract QueryParam getParameter(Property p);
		
		
	}
	protected String sql_datasetid = "and id_srcdataset=?";
	/**
	 * 
	 */
	private static final long serialVersionUID = -3278861415939066571L;

	public QueryDatasetByFeatures() {
		this(null);
	}
	public QueryDatasetByFeatures(Property property) {
		super();
		setFieldname(property);
	}
	
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		if (getFieldname()==null) return null;	
		List<QueryParam> params = new ArrayList<QueryParam>();
		for (c_feature c : c_feature.values()) 
			if (c.isDefined(getFieldname())) {
				params.add(c.getParameter(getFieldname()));
			}
		return params;
			
	}

	@Override
	public String getSQL() throws AmbitException {
		StringBuilder b = new StringBuilder();
		String d = "where ";
		String join = "";
		for (c_feature c : c_feature.values()) 
			if (c.isDefined(getFieldname())) {
				b.append(d);
				b.append(c.getSQL());
				d = " and ";
				if (c.needJoin()) join = "join catalog_references using(idreference)\n" ;
			}
		String properties_sql = String.format("%s %s", join,b.toString());
		return String.format(sql, properties_sql,"");
	}

	@Override
	public String toString() {
		
		if (getFieldname()==null) return super.toString();
		StringBuilder b = new StringBuilder();
		for (c_feature c : c_feature.values())
			b.append(c.explain(getFieldname()));
		return b.toString();
	}
}
