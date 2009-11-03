package ambit2.core.data.model;

import java.util.List;

import ambit2.base.data.Template;

/**
 * Quick implementation of OpenTox Algorithm spec 
 * @author nina
 *
 */
public class Algorithm<T> implements Comparable<Algorithm<T>> {
	protected String id;
	protected String name;
	protected List<String> parameters;
	protected T content;
	protected Template input;
	public Template getInput() {
		return input;
	}
	public void setInput(Template input) {
		this.input = input;
	}
	public T getContent() {
		return content;
	}
	public void setContent(T content) {
		this.content = content;
	}
	public Algorithm() {
		this("Unknown");
	}
	public Algorithm(String name) {
		setName(name);
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
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
	public int compareTo(Algorithm<T> o) {
		return getId().compareTo(o.getId());
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Algorithm) {
			return getId().equals(((Algorithm)obj).getId());
		} else return false;
	}
}
