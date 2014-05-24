/**
 * Created on 2005-3-29
 *
 */
package ambit2.model.numeric;

import ambit2.model.structure.DataCoverageFingeprintsMissingFragments;
import ambit2.model.structure.DataCoverageFingerprintsTanimoto;



/**
 * Applicability domain method type
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public enum ADomainMethodType {
	_modeRANGE {
		@Override
		public String getName() {
			return "PCARanges";
		}
		@Override
		public DataCoverage createDataCoverageObject() {
			return new DataCoverageDescriptors();
		}
		@Override
		public boolean isRange() {
			return true;
		}
	},
	_modeLEVERAGE {
		@Override
		public String getName() {
			return "Leverage";
		}

		@Override
		public DataCoverage createDataCoverageObject() {
			return new DataCoverageLeverageHatMatrix();
		}
	},
	_modeEUCLIDEAN {
		@Override
		public String getName() {
			return "Euclidean distance";
		}

		@Override
		public boolean isDistance() {
			return true;
		}
		
	},
	_modeCITYBLOCK {
		@Override
		public String getName() {
			return "City-block distance";
		}

		@Override
		public boolean isDistance() {
			return true;
		}
		
	},
	_modeMAHALANOBIS {
		@Override
		public String getName() {
			return "Mahalanobis distance";
		}
		@Override
		public boolean isDistance() {
			return true;
		}
	},
	_modeDENSITY {
		@Override
		public String getName() {
			return "Probability density";
		}

		@Override
		public DataCoverage createDataCoverageObject() {
			return new DataCoverageDensity();
		}
	},
	_modeFINGERPRINTS_CONSENSUS {
		@Override
		public String getName() {
			return "Tanimoto Fingerprints (consensus)";
		}

		@Override
		public DataCoverage createDataCoverageObject() {
			return new DataCoverageFingerprintsTanimoto();
		}
			
	},
	_modeFINGERPRINT_MISSINGFRAGMENTS {
		@Override
		public String getName() {
			return "Tanimoto Fingerprints (consensus)";
		}

		@Override
		public DataCoverage createDataCoverageObject() {
			return new DataCoverageFingeprintsMissingFragments();
		}
	}	
	/*
	,
	_modeATOMENVIRONMENT {
		@Override
		public String getName() {
			return "Atom environments";
		}
		@Override
		public String getClazz() {
			return "ambit2.model.DataCoverageAtomEnvironment.class";
		}			
	},
	_modeATOMENVIRONMENTRANK {
		@Override
		public String getName() {
			return "Atom environments ranking";
		}
		@Override
		public String getClazz() {
			return "ambit2.model.structure.DataCoverageAtomEnvironment.class";
		}			
	}
	*/;
	public abstract String getName();
	public String getId() {
		return String.format("AD%d", ordinal());
	}

	public DataCoverage createDataCoverageObject() {
		return new DataCoverageDistance(this);
	}
	public boolean isDistance() { return false; }
	public boolean isRange() { return false; }
	@Override
	public String toString() {
		return getName();
	}

}
