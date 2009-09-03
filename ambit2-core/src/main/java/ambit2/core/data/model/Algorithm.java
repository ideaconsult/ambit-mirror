package ambit2.core.data.model;

import java.util.List;

/**
 * Quick implementation of OpenTox Algorithm spec 
 * @author nina
 *
 */
public class Algorithm {
	protected int id;
	protected String name;
	protected List<String> parameters;
	public Algorithm() {
		this("Unknown");
	}
	public Algorithm(String name) {
		setName(name);
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getParameters() {
		return parameters;
	}
	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}
	public List<String> getStatistics() {
		return statistics;
	}
	public void setStatistics(List<String> statistics) {
		this.statistics = statistics;
	}
	protected List<String> statistics;
	@Override
	public String toString() {
		return getName();
	}
}
