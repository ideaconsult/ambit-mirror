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
	};
	public STRUCTURE_RELATION inverseOf() {
		return null;
	}
}

