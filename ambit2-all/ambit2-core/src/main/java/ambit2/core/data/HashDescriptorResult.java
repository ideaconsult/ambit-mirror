package ambit2.core.data;

import java.util.Map;

import org.openscience.cdk.qsar.result.IDescriptorResult;

public class HashDescriptorResult<V> implements IDescriptorResult  {
	
	protected Map<String,V> results; 
	
	public HashDescriptorResult(Map<String,V> map) throws Exception {
		if (map==null) throw new Exception("Null map");
		setResults(map);
	}
	public Map<String, V> getResults() {
		return results;
	}

	public void setResults(Map<String, V> results) {
		this.results = results;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4765713371505859931L;
	
	
	@Override
	public int length() {
		return  results==null?0:results.size();
	}
	
	@Override
	public String toString() {
		return results==null?null:results.toString();
	}

	
}
