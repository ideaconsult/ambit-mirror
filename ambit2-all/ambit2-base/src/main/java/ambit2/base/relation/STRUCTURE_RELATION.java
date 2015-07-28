package ambit2.base.relation;


public enum STRUCTURE_RELATION {
	SIMILARITY,
	HAS_TAUTOMER {
		@Override
		public STRUCTURE_RELATION inverseOf() {
			return TAUTOMER_OF;
		}
	},
	HAS_METABOLITE {
		@Override
		public STRUCTURE_RELATION inverseOf() {
			return METABOLITE_OF;
		}
	},
	HAS_CONSTITUENT {
		@Override
		public STRUCTURE_RELATION inverseOf() {
			return CONSTITUENT_OF;
		}
	},
	HAS_ADDITIVE {
		@Override
		public STRUCTURE_RELATION inverseOf() {
			return ADDITIVE_OF;
		}
	},	
	HAS_IMPURITY {
		@Override
		public STRUCTURE_RELATION inverseOf() {
			return IMPURITY_OF;
		}
	},	
	HAS_CORE {
		@Override
		public STRUCTURE_RELATION inverseOf() {
			return CORE_OF;
		}
	},	
	HAS_COATING {
		@Override
		public STRUCTURE_RELATION inverseOf() {
			return COATING_OF;
		}
	},		
	TAUTOMER_OF {
		@Override
		public STRUCTURE_RELATION inverseOf() {
			return HAS_TAUTOMER;
		}
	},
	METABOLITE_OF {
		@Override
		public STRUCTURE_RELATION inverseOf() {
			return HAS_METABOLITE;
		}
	},
	CONSTITUENT_OF {
		@Override
		public STRUCTURE_RELATION inverseOf() {
			return HAS_CONSTITUENT;
		}
	},	
	ADDITIVE_OF {
		@Override
		public STRUCTURE_RELATION inverseOf() {
			return HAS_ADDITIVE;
		}
	},
	IMPURITY_OF {
		@Override
		public STRUCTURE_RELATION inverseOf() {
			return HAS_IMPURITY;
		}
	},	
	COATING_OF {
		@Override
		public STRUCTURE_RELATION inverseOf() {
			return HAS_COATING;
		}
	},
	CORE_OF {
		@Override
		public STRUCTURE_RELATION inverseOf() {
			return HAS_CORE;
		}
	},
	HAS_STRUCTURE {
		@Override
		public STRUCTURE_RELATION inverseOf() {
			return CONSTITUENT_OF;
		}
	};	
	public STRUCTURE_RELATION inverseOf() {
		return null;
	}
	public String toHumanReadable() {
		return name().toLowerCase().replace("has_","").replace("_"," ");
	}
}

