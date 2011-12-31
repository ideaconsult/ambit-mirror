package ambit2.core.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.Template;

/**
 * Quick implementation of OpenTox Algorithm spec 
 * @author nina
 *
 */
public class Algorithm<T extends Serializable> implements Comparable<Algorithm<T>>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6624262246346428281L;
	public enum requires {
		structure,
		property
	}

	
	public enum AlgorithmFormat {
		JAVA_CLASS {
			@Override
			public String getMediaType() {
				return "application/java";
			}
		},
		PMML {
			@Override
			public String getMediaType() {
				return "application/pmml+xml";
			}
		},
		WEKA {
			@Override
			public String getMediaType() {
				return "application/x-java-serialized-object";
			}
		},
		MOPAC {
			@Override
			public String getMediaType() {
				return "mopac/java";
			}
		},		
		WWW_FORM {
			@Override
			public String getMediaType() {
				return "application/x-www-form-urlencoded";
			}
		},			
		SEARCH {
			@Override
			public String getMediaType() {
				return "text/plain";
			}
		},			
		COVERAGE_SERIALIZED {
			@Override
			public String getMediaType() {
				return "application/x-coverage-serialized-object";
			}
		},	
		PREFERRED_STRUC {
			@Override
			public String getMediaType() {
				return "text/plain";
			}
		};			
		public abstract String getMediaType();
		};
	protected String id;
	protected String name;
	protected List<Parameter> parameters;
	protected T content;
	protected AlgorithmFormat format=AlgorithmFormat.JAVA_CLASS;
	protected String[] type;
	protected String endpoint = null;
	protected requires requirement;
	protected String implementationOf = null;
	


	public String getImplementationOf() {
		return implementationOf;
	}

	public void setImplementationOf(String implementationOf) {
		this.implementationOf = implementationOf;
	}

	public requires getRequirement() {
		return requirement;
	}

	public void setRequirement(requires requirement) {
		this.requirement = requirement;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public boolean isSupervised() {
		return hasType(AlgorithmType.Classification.toString()) || hasType(AlgorithmType.Regression.toString());
	}

	public boolean isRequiresDataset() {
		return hasType(AlgorithmType.Clustering.toString()) || hasType(AlgorithmType.Classification.toString()) 
				|| hasType(AlgorithmType.Regression.toString()) || 
		hasType(AlgorithmType.Learning.toString()) || hasType(AlgorithmType.LazyLearning.toString()) 
		|| hasType(AlgorithmType.EagerLearning.toString()) ||
		hasType(AlgorithmType.DescriptorCalculation.toString()) || hasType(AlgorithmType.AppDomain.toString()) || 
		hasType(AlgorithmType.Mockup.toString()) || hasType(AlgorithmType.SMSD.toString()) ;
	}
	public boolean isDataProcessing() {
		return hasType(AlgorithmType.DescriptorCalculation.toString()) || hasType(AlgorithmType.Fingerprints.toString())
		|| hasType(AlgorithmType.Mockup.toString());
	}

	public String[] getType() {
		return type;
	}
	public void setType(String[] type) {
		this.type = type;
	}
	public boolean hasType(AlgorithmType type) {
		for (String t: this.type)
			if (t.equals(type.toString())) return true;
		return false;
	}		
	public boolean hasType(String type) {
		for (String t: this.type)
			if (t.equals(type)) return true;
		return false;
	}	
	protected String description;
	
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
		setType(new String[] {AlgorithmType.AlgorithmType.toString()});
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
	public void addParameter(Parameter param) {
		if (parameters==null) parameters=new ArrayList<Parameter>();
		parameters.add(param);
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
	public String[] getParametersAsArray() {
		if ((getParameters()==null) || (getParameters().size()==0)) return null;
		String[] prm = new String[getParameters().size()];
		List<Parameter> param = getParameters();
		for (int i=0; i < param.size();i++) { 
			prm[i] = param.get(i).getValue().toString();
		}		
		return prm;
	}
}
