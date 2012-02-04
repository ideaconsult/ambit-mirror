package ambit2.model.numeric.distance;

import ambit2.model.numeric.ADomainMethodType;
import ambit2.model.numeric.DataCoverageDistance;

public class DataCoverageDistanceEuclidean extends DataCoverageDistance {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6182394064995003521L;

	public DataCoverageDistanceEuclidean() {
		super(ADomainMethodType._modeEUCLIDEAN);
		setPca(false);
	}
}
