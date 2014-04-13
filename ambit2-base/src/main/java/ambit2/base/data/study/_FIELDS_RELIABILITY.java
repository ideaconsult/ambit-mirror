package ambit2.base.data.study;

public enum _FIELDS_RELIABILITY {
	r_id {
		@Override
		public String getTag() {
			return  "id";
		}
	},
	r_value {
		@Override
		public String getTag() {
			return  "value";
		}
	},
	r_isRobustStudy {
		@Override
		public String getTag() {
			return "isRobustStudy";
		}
	},
	r_isUsedforClassification {
		@Override
		public String getTag() {
			return "isUsedforClassification";
		}
	},
	r_isUsedforMSDS {
		@Override
		public String getTag() {
			return "isUsedforMSDS";
		}
	},
	r_purposeFlag {
		@Override
		public String getTag() {
			return "purposeFlag";
		}
	},
	r_studyResultType {
		@Override
		public String getTag() {
			return "studyResultType";
		}
	}
	;
	public String getTag() {
		return name();
	}	
	
}
