package ambit2.rest;

public enum DisplayMode {
	
	table,
	singleitem {
		@Override
		public boolean isCollapsed() {
			return false;
		}
	},
	hierarchy,
	edit {
		@Override
		public boolean isCollapsed() {
			return false;
		}
	},
	properties {
		@Override
		public boolean isCollapsed() {
			return false;
		}
	};
	//for compatibility
	public boolean isCollapsed() {return true;}
}
