package ambit2.db.reporters;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.db.readers.IQueryRetrieval;

/**
 * Generates report from a query with header at a top (e.g. CSV columns or ARFF header)
 * @author nina
 *
 * @param <T>
 * @param <Q>
 * @param <Output>
 */
public abstract class QueryHeaderReporter<T,Q extends IQueryRetrieval<T>,Output>  extends QueryReporter<T, Q, Output> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4653171215072211975L;
	protected Template template;
	protected List<Property> header = null;
	
	
	public Template getTemplate() {
		return template;
	}
	public void setTemplate(Template template) {
		this.template = template;
	}
	
	
	protected List<Property> template2Header(Template template) {
		List<Property> h = new ArrayList<Property>();
		Iterator<Property> it = template.getProperties(true);
		while (it.hasNext()) h.add(it.next());
		Collections.sort(h,new Comparator<Property>() {
			public int compare(Property o1, Property o2) {
				return o1.getOrder()-o2.getOrder();
			}
		});	
		return h;
	}
}
