package ambit2.db.update.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ambit2.base.data.Dictionary;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.search.QueryParam;
import ambit2.db.update.AbstractObjectUpdate;
import ambit2.db.update.dictionary.CreateDictionary;
import ambit2.db.update.dictionary.TemplateAddProperty;

public class CreateModel extends AbstractObjectUpdate<ModelQueryResults>{
	protected List<TemplateAddProperty> templatePredictors;
	protected List<TemplateAddProperty> templateDependent;
	protected Dictionary dictPredictors;
	protected Dictionary dictDependent;
	protected CreateDictionary createDictionary;
	
	public static final String create_sql = 
		"INSERT IGNORE INTO models (idmodel,name,idquery,predictors,dependent,content) SELECT null,?,?,t1.idtemplate,t2.idtemplate,? from template t1 join template t2 where t1.name=? and t2.name =?"
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
		
		params1.add(new QueryParam<String>(String.class, getObject().getName()));
		if (getObject().getTrainingInstances()==null)
			params1.add(new QueryParam<Integer>(Integer.class, null));
		else
			params1.add(new QueryParam<Integer>(Integer.class, getObject().getTrainingInstances().getFieldname().getId()));
		params1.add(new QueryParam<String>(String.class, getObject().getContent()));
		params1.add(new QueryParam<String>(String.class, getObject().getPredictors().getName()));
		params1.add(new QueryParam<String>(String.class, getObject().getDependent().getName()));
		return params1;
		
	}

	public String[] getSQL() throws AmbitException {
		List<String> sqls = new ArrayList<String>();
		
		if (templatePredictors.size()==0) {
			createDictionary.setObject(dictPredictors);
			String[] sql = createDictionary.getSQL();
			for (String s:sql) sqls.add(s);
		} else
			for (TemplateAddProperty t : templatePredictors) {
				String[] sql = t.getSQL();
				for (String s:sql) sqls.add(s);
	 		}
		
		if (templateDependent.size()==0) {
			createDictionary.setObject(dictDependent);
			String[] sql = createDictionary.getSQL();
			for (String s:sql) sqls.add(s);
		} else		
			for (TemplateAddProperty t : templateDependent) {
				String[] sql = t.getSQL();
				for (String s:sql) sqls.add(s);
	 		}	
		sqls.add(create_sql);
		//for (String s:sqls) System.out.println(s);
		return sqls.toArray(new String[sqls.size()]);
	}
	public void setID(int index, int id) {
		getObject().setId(id);
	}
	@Override
	public boolean returnKeys(int index) {
		return true;
	}
}
