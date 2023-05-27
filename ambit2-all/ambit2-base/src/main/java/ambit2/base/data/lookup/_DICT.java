package ambit2.base.data.lookup;


public enum _DICT {
	pDictionary1 {
		public String getResource() {
			return "parameters_marina2enm";
		}
	},
	pDictionary2 {
		public String getResource() {
			return "parameters_enm2nanoreg";
		}			
	},
	protocolDictionary {
		public String getResource() {
			return "protocol";
		}				
	}, 
	valuesDictionary {
		public String getResource() {
			return "values";
		}				
		
	},
	ownersDictionary {
		public String getResource() {
			return "ownernames";
		}			
	}, 
	unitsDictionary {
		public String getResource() {
			return "units";
		}			
	}, 
	endpointsDictionary {
		public String getResource() {
			return "endpoints";
		}			
	},
	jrc2enm {
		public String getResource() {
			return "JRC2ENM";
		}			
	},
	nmcode2jrcid {
		public String getResource() {
			return "nmcode2jrcid";
		}			
	},
	jrcid2substancename {
		public String getResource() {
			return "substancename";
		}			
	};

	public abstract String getResource();
}
