/**
 * Created on 2005-3-29
 *
 */
package ambit2.model.numeric;



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
		public String getClazz() {
			return "ambit2.model.numeric.DataCoverageDescriptors.class";
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
		public String getClazz() {
			return "ambit2.model.numeric.DataCoverageLeverage.class";
		}		
	},
	_modeEUCLIDEAN {
		@Override
		public String getName() {
			return "Euclidean distance";
		}
		@Override
		public String getClazz() {
			return "ambit2.model.numeric.DataCoverageDistance.class";
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
		public String getClazz() {
			return "ambit2.model.numeric.DataCoverageDistance.class";
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
		public String getClazz() {
			return "ambit2.model.numeric.DataCoverageDistance.class";
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
		public String getClazz() {
			return "ambit2.model.numeric.DataCoverageDensity.class";
		}			
	},
	_modeFINGERPRINTS {
		@Override
		public String getName() {
			return "Fingerprints";
		}
		@Override
		public String getClazz() {
			return "ambit2.model.structure.DataCoverageFingerprints.class";
		}			
	},
	_modeATOMENVIRONMENT {
		@Override
		public String getName() {
			return "Atom environments";
		}
		@Override
		public String getClazz() {
			return "ambit2.model.structure.DataCoverageAtomEnvironment.class";
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
	};
	public abstract String getName();
	public String getId() {
		return String.format("AD%d", ordinal());
	}
	public abstract String getClazz();
	public boolean isDistance() { return false; }
	public boolean isRange() { return false; }
	@Override
	public String toString() {
		return getName();
	}

}
