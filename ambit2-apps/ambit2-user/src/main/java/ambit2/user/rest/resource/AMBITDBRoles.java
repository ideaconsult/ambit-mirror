package ambit2.user.rest.resource;



public enum AMBITDBRoles {
	
	ambit_guest {
		@Override
		public String toString() {
			return "Guest";
		}
		@Override
		public String getHint() {
			return "Guets user";
		}		
	},
	ambit_user {
		@Override
		public String toString() {
			return "Author";
		}
		@Override
		public String getHint() {
			return "Registered user and datasets author";
		}		
	},
	ambit_modeller {
		@Override
		public String getURI() {
			return Resources.algorithm;
		}
		@Override
		public String toString() {
			return "Model builder";
		}
		@Override
		public String getHint() {
			return "Model building enabled";
		}		
	},
	ambit_model_user {
		@Override
		public String getURI() {
			return Resources.model;
		}
		@Override
		public String toString() {
			return "Model user";
		}
		@Override
		public String getHint() {
			return "Model launching enabled";
		}		
	},	
	ambit_datasetmgr {
		@Override
		public String getURI() {
			return Resources.dataset;
		}
		@Override
		public String toString() {
			return "Dataset manager";
		}
		@Override
		public String getHint() {
			return "Dataset upload enabled";
		}
	},
	ambit_admin {
		//can do sysadmin
		@Override
		public String getURI() {
			return Resources.admin;
		}	
		@Override
		public String toString() {
			return "Admin";
		}		
		@Override
		public String getHint() {
			return "System administration";
		}
	};
	public String getURI() {return null;}
	public String getHint() { return toString();}

}

