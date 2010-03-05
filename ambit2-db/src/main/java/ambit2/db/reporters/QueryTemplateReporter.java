package ambit2.db.reporters;

import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;

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
	public QueryTemplateReporter(Template template)  throws AmbitException {
		super();
		setOutput(template);
	}
	@Override
	public Object processItem(Property item) throws AmbitException {
		item.setEnabled(true);
		return getOutput().add(item);
	}


}
