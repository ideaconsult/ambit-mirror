package ambit2.base.data;

public interface ISelectableRecords {
	public enum SELECTION_MODE {
		NO_CHANGE {
			@Override
			public String toString() {
				return "";
			}
		},
		SELECT_ALL {
			@Override
			public String toString() {
				return "Select all";
			}
		},
		UNSELECT_ALL {
			@Override
			public String toString() {
				return "Unselect all";
			}
			
		},
		INVERT_SELECTIONS {
			@Override
			public String toString() {
				return "Invert selections";
			}
			
		},
		SHOW_ALL {
			@Override
			public String toString() {
				return "Show ALL";
			}
			
		},			
		SHOW_SELECTIONS {
			@Override
			public String toString() {
				return "Show selected only";
			}
			
		},
		HIDE_SELECTIONS {
			@Override
			public String toString() {
				return "Show unselected only";
			}
			
		}

		};
	void setSelection(SELECTION_MODE mode);
	SELECTION_MODE getSelection();
}
