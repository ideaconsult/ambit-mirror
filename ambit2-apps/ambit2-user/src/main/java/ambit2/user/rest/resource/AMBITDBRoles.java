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
	ambit_curator {
		@Override
		public String getURI() {
			return Resources.editor;
		}
		@Override
		public String toString() {
			return "Curator";
		}
		@Override
		public String getHint() {
			return "Curating existing entries";
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

