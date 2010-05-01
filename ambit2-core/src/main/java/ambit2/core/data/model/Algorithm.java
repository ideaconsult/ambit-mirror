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
	/**
	 * 
	 */
	private static final long serialVersionUID = -6624262246346428281L;
	public enum requires {
		structure,
		property
	}
	public static String typeAny = "http://www.opentox.org/algorithmTypes.owl#AlgorithmType";
	public static String typeLearning = "http://www.opentox.org/algorithmTypes.owl#Learning";
	public static String typeDescriptor = "http://www.opentox.org/algorithmTypes.owl#DescriptorCalculation";
	public static String typeRules = "http://www.opentox.org/algorithmTypes.owl#Rules";
	public static String typeClustering = "http://www.opentox.org/algorithmTypes.owl#Clustering";
	public static String typeRegression = "http://www.opentox.org/algorithmTypes.owl#Regression";
	public static String typeClassification = "http://www.opentox.org/algorithmTypes.owl#Classification";
	public static String typeSingleTarget = "http://www.opentox.org/algorithmTypes.owl#SingleTarget";
	public static String typeEagerLearning = "http://www.opentox.org/algorithmTypes.owl#EagerLearning";
	public static String typeLazyLearning = "http://www.opentox.org/algorithmTypes.owl#LazyLearning";
	public static String typeSupervised = "http://www.opentox.org/algorithmTypes.owl#Supervised";
	public static String typeUnSupervised = "http://www.opentox.org/algorithmTypes.owl#Unsupervised";
	public static String typeAppDomain = "http://www.opentox.org/algorithmTypes.owl#ApplicabilityDomain";
	public static String typeFingerprints = "http://www.opentox.org/algorithmTypes.owl#Fingerprints";
	public static String typeMockup = "http://www.opentox.org/algorithmTypes.owl#Mockup";
	public static String typeSuperService = "http://www.opentox.org/algorithmTypes.owl#SuperService";
	
	
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
		COVERAGE_SERIALIZED {
			@Override
			public String getMediaType() {
				return "application/x-coverage-serialized-object";
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
		return hasType(typeClassification) || hasType(typeRegression);
	}

	public boolean isRequiresDataset() {
		return hasType(typeClustering) || hasType(typeClassification) || hasType(typeRegression) || 
		hasType(typeLearning) || hasType(typeLazyLearning) || hasType(typeEagerLearning) ||
		hasType(typeDescriptor) || hasType(typeAppDomain) || hasType(typeMockup) ;
	}
	public boolean isDataProcessing() {
		return hasType(typeDescriptor) || hasType(typeFingerprints) || hasType(typeMockup);
	}

	public String[] getType() {
		return type;
	}
	public void setType(String[] type) {
		this.type = type;
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
		setType(new String[] {typeAny});
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
