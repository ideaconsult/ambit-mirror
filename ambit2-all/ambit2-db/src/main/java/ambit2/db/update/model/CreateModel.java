package ambit2.db.update.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.Dictionary;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.db.update.AbstractObjectUpdate;
import ambit2.db.update.dictionary.CreateDictionary;
import ambit2.db.update.dictionary.TemplateAddProperty;

public class CreateModel extends AbstractObjectUpdate<ModelQueryResults>{
	protected List<TemplateAddProperty> templatePredictors;
	protected List<TemplateAddProperty> templateDependent;
	protected List<TemplateAddProperty> templatePredicted;
	protected Dictionary dictPredictors;
	protected Dictionary dictDependent;
	protected Dictionary dictPredicted;
	protected CreateDictionary createDictionary;
	protected int sql_size = 0;
	public static final String create_sql = 
		"INSERT IGNORE INTO models (idmodel,name,dataset,predictors,dependent,predicted,content,mediatype,algorithm,parameters,hidden,creator) " +
		"SELECT null,?,?,t1.idtemplate,t2.idtemplate,t3.idtemplate,?,?,?,?,?,? from template t1 join template t2 join template t3 where t1.name=? and t2.name =? and t3.name=?";
	;

	public CreateModel(ModelQueryResults ref) {
		super(ref);
		createDictionary = new CreateDictionary();
	}
	public CreateModel() {
		this(null);
	}		
	@Override
	public void setObject(ModelQueryResults object) {
		super.setObject(object);
		if (object != null) {
			dictPredictors = new Dictionary(object.getPredictors().getName(),"Predictors");
			templatePredictors = processTemplate(dictPredictors, object.getPredictors());
			
			dictDependent = new Dictionary(object.getDependent().getName(),"Models");
			templateDependent = processTemplate(dictDependent, object.getDependent());
			
			dictPredicted = new Dictionary(object.getPredicted().getName(),"Models");
			templatePredicted = processTemplate(dictPredicted, object.getPredicted());			
		}
	}
	protected List<TemplateAddProperty> processTemplate(Dictionary d, Template t) {
		List<TemplateAddProperty> list = new ArrayList<TemplateAddProperty>();
		Iterator<Property> i = t.iterator();
		while (i.hasNext()) {
			list.add(new TemplateAddProperty(d,i.next()));
		}
		return list;
	}
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		int c = 0;
		//independent vars
		if (templatePredictors.size()==0) {
			createDictionary.setObject(dictPredictors);
			String[] sql = createDictionary.getSQL();
			for (int k=0; k < sql.length;k++) {
				if (c==index) return createDictionary.getParameters(k);
				c++;
			}
		} else		
			for (TemplateAddProperty t : templatePredictors) {
				String[] sql = t.getSQL();
				for (int k=0; k < sql.length;k++) {
					if (c==index) return t.getParameters(k);
					c++;
				}
	 		}
		//dependent vars
		if (templateDependent.size()==0) {
			createDictionary.setObject(dictDependent);
			String[] sql = createDictionary.getSQL();
			for (int k=0; k < sql.length;k++) {
				if (c==index) return createDictionary.getParameters(k);
				c++;
			}
		} else				
			for (TemplateAddProperty t : templateDependent) {
				String[] sql = t.getSQL();
				for (int k=0; k < sql.length;k++) {
					if (c==index) return t.getParameters(k);
					c++;
				}
	 		}		
		//Predicted
		if (templatePredicted.size()==0) {
			createDictionary.setObject(dictPredicted);
			String[] sql = createDictionary.getSQL();
			for (int k=0; k < sql.length;k++) {
				if (c==index) return createDictionary.getParameters(k);
				c++;
			}
		} else				
			for (TemplateAddProperty t : templatePredicted) {
				String[] sql = t.getSQL();
				for (int k=0; k < sql.length;k++) {
					if (c==index) return t.getParameters(k);
					c++;
				}
	 		}	
		
		params1.add(new QueryParam<String>(String.class, getObject().getName()));
		if (getObject().getTrainingInstances()==null)
			params1.add(new QueryParam<String>(String.class, null));
		else
			params1.add(new QueryParam<String>(String.class, getObject().getTrainingInstances()));
		params1.add(new QueryParam<String>(String.class, getObject().getContent()));
		params1.add(new QueryParam<String>(String.class, getObject().getContentMediaType()));
		params1.add(new QueryParam<String>(String.class, getObject().getAlgorithm()));

		StringBuilder b = null; 
		String[] params = getObject().getParameters();
		if (params!=null) {
			b = new StringBuilder();
			for (String param: params) {b.append(param);b.append("\t"); }
		}
		params1.add(new QueryParam<String>(String.class, b==null?null:b.toString()));		
		params1.add(new QueryParam<Boolean>(Boolean.class, getObject().isHidden()));
		//model creator
		
		params1.add(new QueryParam<String>(String.class, getObject().getCreator()==null?"guest":getObject().getCreator()));
		
		params1.add(new QueryParam<String>(String.class, getObject().getPredictors().getName()));
		params1.add(new QueryParam<String>(String.class, getObject().getDependent().getName()));
		params1.add(new QueryParam<String>(String.class, getObject().getPredicted().getName()));

		return params1;
		
	}

	public String[] getSQL() throws AmbitException {
		List<String> sqls = new ArrayList<String>();
		//independent
		if (templatePredictors.size()==0) {
			createDictionary.setObject(dictPredictors);
			String[] sql = createDictionary.getSQL();
			for (String s:sql) sqls.add(s);
		} else
			for (TemplateAddProperty t : templatePredictors) {
				String[] sql = t.getSQL();
				for (String s:sql) sqls.add(s);
	 		}
		//dependent
		if (templateDependent.size()==0) {
			createDictionary.setObject(dictDependent);
			String[] sql = createDictionary.getSQL();
			for (String s:sql) sqls.add(s);
		} else		
			for (TemplateAddProperty t : templateDependent) {
				String[] sql = t.getSQL();
				for (String s:sql) sqls.add(s);
	 		}	
		//predicted
		if (templatePredicted.size()==0) {
			createDictionary.setObject(dictPredicted);
			String[] sql = createDictionary.getSQL();
			for (String s:sql) sqls.add(s);
		} else		
			for (TemplateAddProperty t : templatePredicted) {
				String[] sql = t.getSQL();
				for (String s:sql) sqls.add(s);
	 		}			
		
		sqls.add(create_sql);
		sql_size = sqls.size();
		return sqls.toArray(new String[sqls.size()]);
	}
	public void setID(int index, int id) {
		if (index== (sql_size-1))
			getObject().setId(id);
	}
	@Override
	public boolean returnKeys(int index) {
		return true;
	}
}
