package ambit2.db.search;


public abstract class QuerySimilarity<T,C extends IQueryCondition> 
			extends AbstractStructureQuery<String,T,C> {
	protected double threshold = 0.5;
	
	public QuerySimilarity() {
		setFieldname("similarity");
	}
	public void setThreshold(Double threshold) {
		this.threshold = threshold;
	}
	public double getThreshold() {
		return threshold;
	}
	
}
