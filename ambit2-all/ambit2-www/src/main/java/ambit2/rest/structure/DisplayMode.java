package ambit2.rest.structure;

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
	};
	//for compatibility
	public boolean isCollapsed() {return true;}
}
