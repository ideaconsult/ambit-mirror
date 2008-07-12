package ambit.database.writers;

import ambit.data.AmbitObject;

/**
 * Query results stored in dsname/datasets tables.
 * Used by {@link QueryWriter}.
 * @author Nina Jeliazkova nina@acad.bg
 *
 */
public class QueryResults extends AmbitObject {
	protected boolean overwrite = true;
	/**
	 * 
	 */
	private static final long serialVersionUID = -2005175876907230417L;
	public QueryResults() {
		super();
	}
	public QueryResults(String name) {
		super();
		setName(name);
	}
		
	@Override
	public String toString() {
		return getName() + "["+getId()+"]";
	}
	public boolean isOverwrite() {
		return overwrite;
	}
	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

}
