package ambit2.core.data.model;

public enum AlgorithmType {

		AlgorithmType {
			@Override
			public String getTitle() {
				return "Any";
			}
		},
		Learning,
		Clustering,
		Classification,
		Regression,
		SingleTarget,
		MultipleTarget,
		EagerLearning,
		LazyLearning,
		Supervised,
		UnSupervised,
		FeatureSelection,
		DescriptorCalculation,
		Rules {
			@Override
			public String getTitle() {
				return "Expert rules";
			}
		},
		AppDomain {
			@Override
			public String getTitle() {
				return "Applicability domain";
			}
		},
		Fingerprints,
		SuperService,
		SuperBuilder,
		Structure,
		Structure2D,
		Expert,
		Finder,
		SMSD,
		Mockup,
		PreferredStructure,
		TautomerGenerator;
		public String toString() {
			return String.format("http://www.opentox.org/algorithmTypes.owl#%s",name());
		}
		public String getTitle() {
			return name();
		}
	
}
