package ambit2.db.reporters;

import net.idea.modbcum.i.IQueryRetrieval;
import ambit2.base.data.Property;
import ambit2.base.data.Template;

/**
 * Adds Properties into a Template
 * @author nina
 *
 * @param <Q>
 */
public class QueryTemplateReporter<Q extends IQueryRetrieval<Property>> extends QueryProperyReporter<Q, Template> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6474255197721950832L;
	public QueryTemplateReporter(Template template)  throws Exception {
		super();
		setOutput(template);
	}
	@Override
	public Object processItem(Property item) throws Exception {
		if (item!=null) {
			item.setEnabled(true);
			return getOutput().add(item);
		} else return null;
	}


}
