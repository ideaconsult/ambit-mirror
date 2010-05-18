package ambit2.db.reporters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;

/**
 * Generates report from a query with header at a top (e.g. CSV columns or ARFF header)
 * @author nina
 *
 * @param <T>
 * @param <Q>
 * @param <Output>
 */
public abstract class QueryHeaderReporter<Q extends IQueryRetrieval<IStructureRecord>,Output>  extends QueryStructureReporter<Q, Output> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4653171215072211975L;
	protected Profile groupProperties;
	public Profile getGroupProperties() {
		return groupProperties;
	}
	public void setGroupProperties(Profile gp) {
		this.groupProperties = gp;
	}


	protected Template template;
	protected List<Property> header = null;
	
	
	public Template getTemplate() {
		return template;
	}
	public void setTemplate(Template template) {
		this.template = template;
	}
	
	
	protected List<Property> template2Header(Template template, boolean propertiesOnly) {
		List<Property> h = new ArrayList<Property>();
		Iterator<Property> it;
		if (groupProperties!=null) {
			it = groupProperties.getProperties(true);
			while (it.hasNext()) {
				Property t = it.next();
				h.add(t);
			}
		}			
		it = template.getProperties(true);
		while (it.hasNext()) {
			Property t = it.next();
			if (!propertiesOnly || (propertiesOnly && (t.getId()>0)))
				h.add(t);
		}
		
	
		/*
		Collections.sort(h,new Comparator<Property>() {
			public int compare(Property o1, Property o2) {
				return o1.getOrder()-o2.getOrder();
			}
		});	
		*/
		return h;
	}

}
