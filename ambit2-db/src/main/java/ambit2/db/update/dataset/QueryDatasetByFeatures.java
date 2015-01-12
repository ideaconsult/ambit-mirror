package ambit2.db.update.dataset;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.ISourceDataset;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.StringCondition;

public class QueryDatasetByFeatures extends AbstractReadDataset<Property,ISourceDataset> {
	protected IStructureRecord structure;
	
	public IStructureRecord getStructure() {
		return structure;
	}
	public void setStructure(IStructureRecord structure) {
		this.structure = structure;
	}

	protected String sql = 
	"SELECT id_srcdataset,name,user_name,idreference,title,url,licenseURI,rightsHolder,stars,maintainer from	src_dataset	join catalog_references using(idreference)\n"+
	"join  template_def using(idtemplate)\n"+
	"where idproperty in (\n"+
	"select idproperty from properties %s\n"+
	")\n"+
	"%s\n"+
	"group by id_srcdataset\n"+
	"order by id_srcdataset\n";
	
	protected String sql_chemical = 
		"SELECT id_srcdataset,src_dataset.name,src_dataset.user_name,src_dataset.idreference,title,url,licenseURI,rightsHolder,stars,maintainer from structure\n" +
		"join struc_dataset using(idstructure) " +
		"join src_dataset using(id_srcdataset) " +
		"join catalog_references using(idreference)\n"+
		"join template_def using(idtemplate)\n"+
		"where idproperty in (\n"+
		"select idproperty from properties %s\n"+
		")\n"+
		"%s\n"+
		"group by id_srcdataset\n"+
		"order by id_srcdataset\n";
	
	protected String sql_structure = 
		"SELECT id_srcdataset,name,user_name,idreference,title,url,licenseURI,rightsHolder,stars,maintainer from struc_dataset\n" +
		"join src_dataset using(id_srcdataset) " +
		"join catalog_references using(idreference)\n"+
		"join template_def using(idtemplate)\n"+
		"where idproperty in (\n"+
		"select idproperty from properties %s\n"+
		")\n"+
		"%s\n"+
		"group by id_srcdataset\n"+
		"order by id_srcdataset\n";	
	
	protected enum c_feature {
		featureid {
			@Override
			public String getSQL(String condition) {
				return "idproperty=?";
			}
			@Override
			public boolean isDefined(QueryDatasetByFeatures q) {
				Property p = q.getFieldname();
				return p.getId()>0;
			}
			@Override
			public QueryParam getParameter(QueryDatasetByFeatures q) {
				Property p = q.getFieldname();
				return new QueryParam<Integer>(Integer.class,p.getId());
			}
			@Override
			public String explain(QueryDatasetByFeatures q) {
				Property p = q.getFieldname();
				if (isDefined(q)) return String.format("feature id=%d",p.getId());
				else return "";
			}
		},
		featurename {
			@Override
			public String getSQL(String condition) {
				return String.format("name %s ?",condition);
			}
			@Override
			public boolean isDefined(QueryDatasetByFeatures q) {
				Property p = q.getFieldname();
				return p.getName()!=null;
			}
			@Override
			public QueryParam getParameter(QueryDatasetByFeatures q) {
				Property p = q.getFieldname();
				return new QueryParam<String>(String.class,p.getName());
			}
			@Override
			public String explain(QueryDatasetByFeatures q) {
				Property p = q.getFieldname();
				if (isDefined(q)) return String.format("feature name=%s",p.getName());
				else return "";
			}
		},
		featuresameas {
			@Override
			public String getSQL(String condition) {
				return String.format("comments %s ?",condition);
			}
			@Override
			public boolean isDefined(QueryDatasetByFeatures q) {
				Property p = q.getFieldname();
				return p.getLabel()!=null;
			}			
			@Override
			public QueryParam getParameter(QueryDatasetByFeatures q) {
				Property p = q.getFieldname();
				if (p.getLabel().startsWith("http"))
					return new QueryParam<String>(String.class,p.getLabel());
				else
					return new QueryParam<String>(String.class,String.format("http://www.opentox.org/echaEndpoints.owl#%s",p.getLabel()));
						
			}
			@Override
			public String explain(QueryDatasetByFeatures q) {
				Property p = q.getFieldname();
				if (isDefined(q)) return String.format("feature sameas=%s",p.getLabel());
				else return "";
			}			
		},
		featurehassource {
			@Override
			public String getSQL(String condition) {
				return "title=?";
			}
			@Override
			public boolean needJoin() {
				return true;
			}
			@Override
			public boolean isDefined(QueryDatasetByFeatures q) {
				Property p = q.getFieldname();
				return (p.getReference()!= null) && (p.getTitle() != null);
			}
			@Override
			public QueryParam getParameter(QueryDatasetByFeatures q) {
				Property p = q.getFieldname();
				return new QueryParam<String>(String.class,p.getTitle());
			}
			@Override
			public String explain(QueryDatasetByFeatures q) {
				Property p = q.getFieldname();
				if (isDefined(q)) return String.format("feature hasSource=%s",p.getTitle());
				else return "";
			}
		},
		featuretype {
			@Override
			public String getSQL(String condition) {
				return "FIND_IN_SET(?, ptype)";
			}
			@Override
			public boolean isDefined(QueryDatasetByFeatures q) {
				Property p = q.getFieldname();
				return p.getClazz() != null;
			}
			@Override
			public QueryParam getParameter(QueryDatasetByFeatures q) {
				Property p = q.getFieldname();
				return new QueryParam<String>(String.class,(p.getClazz().equals(String.class))?"STRING":"NUMERIC");
			}			
			@Override
			public String explain(QueryDatasetByFeatures q) {
				Property p = q.getFieldname();
				if (isDefined(q)) return String.format("feature of type=%s",p.getClazz().equals(String.class)?"STRING":"NUMERIC");
				else return "";
			}
		},
		search {
			@Override
			public String getSQL(String condition) {
				return String.format("src_dataset.name %s ?",condition);
			}
			@Override
			public boolean needJoin() {
				return false;
			}
			@Override
			public boolean isDefined(QueryDatasetByFeatures q) {
				return (q.getValue()!=null) && (q.getValue().getName()!=null);
			}
			@Override
			public QueryParam getParameter(QueryDatasetByFeatures q) {
				return new QueryParam<String>(String.class,q.getValue().getName());
			}
			@Override
			public String explain(QueryDatasetByFeatures q) {
				if (isDefined(q)) return String.format("dataset name %s",q.getValue().getName());
				else return "";
			}
		};
		
		public abstract String getSQL(String condition);
		public abstract String explain(QueryDatasetByFeatures q);
		public boolean needJoin() {
			return false;
		}
		public boolean isDefined(QueryDatasetByFeatures q) {
			return false;
		}
		public abstract QueryParam getParameter(QueryDatasetByFeatures q);
		
		
	}
	protected String sql_datasetid = "and id_srcdataset=?";
	/**
	 * 
	 */
	private static final long serialVersionUID = -3278861415939066571L;

	public QueryDatasetByFeatures() {
		this(null,StringCondition.getInstance(StringCondition.C_EQ));
	}
	public QueryDatasetByFeatures(Property property,StringCondition condition) {
		super();
		setFieldname(property);
		setCondition(condition);
	}
	
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		if (getFieldname()==null) return null;	
		List<QueryParam> params = new ArrayList<QueryParam>();
		for (c_feature c : c_feature.values()) 
			if (c.isDefined(this)) {
				params.add(c.getParameter(this));
			}
		if (getStructure()!=null)
			if (getStructure().getIdstructure()>0) {
				params.add(new QueryParam<Integer>(Integer.class,getStructure().getIdstructure()));
			} else if (getStructure().getIdchemical()>0) 
				params.add(new QueryParam<Integer>(Integer.class,getStructure().getIdchemical()));
		
		return params;
			
	}

	@Override
	public String getSQL() throws AmbitException {
		StringBuilder b = new StringBuilder();
		String d = "where ";
		String join = "";
		for (c_feature c : c_feature.values()) 
			if (c.isDefined(this)) {
				b.append(d);
				b.append(c.getSQL(getCondition().getSQL()));
				d = " and ";
				if (c.needJoin()) join = "join catalog_references using(idreference)\n" ;
			}
		String properties_sql = String.format("%s %s", join,b.toString());
		
		if ((getStructure()==null) || ((getStructure().getIdchemical()==-1) && getStructure().getIdstructure()==-1))
			return String.format(sql, properties_sql,"");
		else if (getStructure().getIdstructure()>0) {
			return String.format(sql_structure, properties_sql,"and idstructure = ?\n");
		} else 
			return String.format(sql_chemical, properties_sql,"and idchemical = ?\n");
			
	}

	@Override
	public String toString() {
		
		if (getFieldname()==null) return super.toString();
		StringBuilder b = new StringBuilder();
		b.append("Datasets with ");
		for (c_feature c : c_feature.values()) {
			b.append(c.explain(this));
			b.append(",");
		}
		return b.toString();
	}
}
