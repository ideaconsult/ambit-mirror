package ambit2.core.data.model;

import java.io.Serializable;
import java.util.List;

import ambit2.base.data.Template;

/**
 * Quick implementation of OpenTox Algorithm spec 
 * @author nina
 *
 */
public class Algorithm<T extends Serializable> implements Comparable<Algorithm<T>>, Serializable {
	public enum AlgorithmFormat {JAVA_CLASS,PMML,WEKA};
	protected String id;
	protected String name;
	protected List<Parameter> parameters;
	protected T content;
	protected AlgorithmFormat format=AlgorithmFormat.JAVA_CLASS;
	protected String type;

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	protected String description;
	protected List<String> statistics;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public AlgorithmFormat getFormat() {
		return format;
	}
	public void setFormat(AlgorithmFormat format) {
		this.format = format;
	}
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
	public List<Parameter> getParameters() {
		return parameters;
	}
	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}
	public List<String> getStatistics() {
		return statistics;
	}
	public void setStatistics(List<String> statistics) {
		this.statistics = statistics;
	}
	
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
