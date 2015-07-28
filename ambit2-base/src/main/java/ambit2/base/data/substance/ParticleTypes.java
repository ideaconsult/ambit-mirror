package ambit2.base.data.substance;

import ambit2.base.data.study.StructureRecordValidator;

//TODO proper ontology lookup
public enum ParticleTypes {
	NPO_1892 {
		// Ag
		@Override
		public String getSMILES() {
			return "[Ag]";
		}

		@Override
		public String getCAS() {
			return "7440-22-4";
		}
		@Override
		public String getFormula() {
			return "Ag";
		}
	},
	NPO_401 {
		// Ag
		@Override
		public String getSMILES() {
			return "[Au]";
		}

		@Override
		public String getCAS() {
			return null;
		}
		@Override
		public String getFormula() {
			return "Au";
		}
	},	
	NPO_602 {
	// MWCNT
		@Override
		public String getSMILES() {
			//useful to have structural reference, not really correct
			return "C";
		}
		@Override
		public String getCAS() {
			return "308068-56-6";
		}
		@Override
			public String getEINECS() {
				return "936-414-1";
			}
	},
	ENM_9000006 {
		// CeO2
		@Override
		public String getSMILES() {
			return "O=[Ce]=O";
		}
		@Override
		public String getCAS() {
			return "1306-38-3";
		}
		@Override
		public String getFormula() {
			return "CeO2";
		}
	},
	NPO_1384 {
		// Metal nanoparticle
		@Override
		public String getReferenceUUID() {
			return null;
		}
	},
	NPO_1550 {
		//fe2o3
		@Override
		public String getSMILES() {
			return "O1[Fe]2O[Fe]1O2";
		}
		@Override
		public String getCAS() {
			return "1309-37-1";
		}		
		@Override
		public String getFormula() {
			return "Fe2O3";
		}
	},
	NPO_1548 {
		// Fe3o4
		@Override
		public String getSMILES() {
			return "O=[Fe].O=[Fe]O[Fe]=O";
		}
		@Override
		public String getCAS() {
			return "1317-61-9";
		}
		@Override
		public String getFormula() {
			return "Fe3O4";
		}		
	},
	NPO_1559 {
	// PLGA-PEO

	},
	NPO_1373 {
		// SiO2
		@Override
		public String getSMILES() {
			return "O=[Si]=O";
		}
		@Override
		public String getCAS() {
			return "7631-86-9";
		}
		@Override
		public String getFormula() {
			return "SiO2";
		}
	},
	NPO_1486 {
		// TiO2
		@Override
		public String getSMILES() {
			return "O=[Ti]=O";
		}
		@Override
		public String getFormula() {
			return "TiO2";
		}
	},

	NPO_1544 {
		// CuO
		@Override
		public String getSMILES() {
			return "[Cu]=O";
		}
		@Override
		public String getCAS() {
			return "1317-38-0";
		}
		public String getFormula() {
			return "CuO";
		};		
	},
	NPO_1542 {
		// ZnO
		@Override
		public String getSMILES() {
			return "O=[Zn]";
		}
		@Override
		public String getCAS() {
			return "1314-13-2";
		}
		public String getEINECS() {
			return "215-222-5";
		};
		@Override
		public String getFormula() {
			return "ZnO";
		}
	},
	NPO_Fe {
		@Override
		public String getSMILES() {
			return "[Fe]";
		}
		@Override
		public String getFormula() {
			return "Fe";
		}
		@Override
		public String getAnnotation() {
			return "NPO_1384";
		}
		@Override
		public String getCAS() {
			return "7439-89-6";
		}
	},	
	NPO_NiO2 {
		@Override
		public String getSMILES() {
			return "[Ni](=O)=O";
		}
		@Override
		public String getFormula() {
			return "NiO2";
		}
		@Override
		public String getAnnotation() {
			return "NPO_1541";
		}
	},
	NPO_CdSe {
		@Override
		public String getSMILES() {
			return "[Se]=[Cd]";
		}
		@Override
		public String getFormula() {
			return "CdSe";
		}
		@Override
		public String getAnnotation() {
			return "NPO_1384";
		}
		@Override
		public String getCAS() {
			return "1306-24-7";
		}
	},
	NPO_ZrO2 {
		@Override
		public String getSMILES() {
			return "O=[Zr]=O";
		}
		@Override
		public String getFormula() {
			return "ZrO2";
		}
		@Override
		public String getAnnotation() {
			return "NPO_1541";
		}
		@Override
		public String getCAS() {
			return "1314-23-4";
		}
	},
	NPO_Yb2O3 {
		@Override
		public String getSMILES() {
			return "[Yb+3].[Yb+3].[O-2].[O-2].[O-2]";
		}
		@Override
		public String getFormula() {
			return "Yb2O3";
		}
		@Override
		public String getAnnotation() {
			return "NPO_1541";
		}
		@Override
		public String getCAS() {
			return "1314-37-0";
		}
	},

	NPO_Y2O3 {
		@Override
		public String getSMILES() {
			return "[O-2].[O-2].[O-2].[Y+3].[Y+3]";
		}
		@Override
		public String getFormula() {
			return "Y2O3";
		}
		@Override
		public String getAnnotation() {
			return "NPO_1541";
		}
		@Override
		public String getCAS() {
			return "1314-36-9";
		}
	},
	NPO_V2O3 {
		@Override
		public String getSMILES() {
			return "O=[V]=O";
		}
		@Override
		public String getFormula() {
			return "V2O3";
		}
		@Override
		public String getAnnotation() {
			return "NPO_1541";
		}
		@Override
		public String getCAS() {
			return "1314-34-7";
		}
	},
	NPO_SnO2 {
		@Override
		public String getSMILES() {
			return "O=[Sn]=O";
		}
		@Override
		public String getFormula() {
			return "SnO2";
		}
		@Override
		public String getAnnotation() {
			return "NPO_707";
		}
		@Override
		public String getCAS() {
			return "18282-10-5";
		}
	},
	NPO_Sb2O3 {
		@Override
		public String getSMILES() {
			return "O=[Sb]O[Sb]=O";
		}
		@Override
		public String getFormula() {
			return "Sb2O3";
		}
		@Override
		public String getAnnotation() {
			return "NPO_1541";
		}
		@Override
		public String getCAS() {
			return "1309-64-4";
		}
	},		
	NPO_Ni2O3 {
		@Override
		public String getSMILES() {
			return "[O-2].[O-2].[O-2].[Ni+3].[Ni+3]";
		}
		@Override
		public String getFormula() {
			return "Ni2O3";
		}
		@Override
		public String getAnnotation() {
			return "NPO_1541";
		}
		@Override
		public String getCAS() {
			return "1314-06-3";
		}
	},	
	NPO_MgO {
		@Override
		public String getSMILES() {
			return "O=[Mg]";
		}
		@Override
		public String getFormula() {
			return "MgO";			
		}
		@Override
		public String getAnnotation() {
			return "NPO_1541";
		}
		@Override
		public String getCAS() {
			return "1309-48-4";
		}
	},		
	NPO_La2O3 {
		@Override
		public String getSMILES() {
			return "[O-2].[O-2].[O-2].[La+3].[La+3]";
		}
		@Override
		public String getFormula() {
			return "La2O3";
		}
		@Override
		public String getAnnotation() {
			return "NPO_1541";
		}
		@Override
		public String getCAS() {
			return "1312-81-8";
		}
	},	
	NPO_In2O3 {
		@Override
		public String getSMILES() {
			return "[O-2].[O-2].[O-2].[In+3].[In+3]";
		}
		@Override
		public String getFormula() {
			return "In2O3";
		}
		@Override
		public String getAnnotation() {
			return "NPO_1541";
		}
		@Override
		public String getCAS() {
			return "1312-43-3";
		}
	},	
	NPO_HfO2 {
		@Override
		public String getSMILES() {
			return "O=[Hf]=O";
		}
		@Override
		public String getFormula() {
			return "HfO2";
		}
		@Override
		public String getAnnotation() {
			return "NPO_1541";
		}
		@Override
		public String getCAS() {
			return "12055-23-1";
		}
	},		
	NPO_Gd2O3 {
		@Override
		public String getSMILES() {
			return "[Gd+3].[Gd+3].[O-2].[O-2].[O-2]";
		}
		@Override
		public String getFormula() {
			return "Gd2O3";
		}
		@Override
		public String getAnnotation() {
			return "NPO_1569";
		}
		@Override
		public String getCAS() {
			return "12064-62-9";
		}
	},		
	NPO_CrO3 {
		@Override
		public String getSMILES() {
			return "O=[Cr](=O)=O";
		}
		@Override
		public String getFormula() {
			return "CrO3";
		}
		@Override
		public String getAnnotation() {
			return "NPO_1541";
		}
		@Override
		public String getCAS() {
			return "1333-82-0";
		}
	},		
	NPO_Cr2O3 {
		@Override
		public String getSMILES() {
			return "O=[Cr]O[Cr]=O";
		}
		@Override
		public String getFormula() {
			return "Cr2O3";
		}
		@Override
		public String getAnnotation() {
			return "NPO_1541";
		}
		@Override
		public String getCAS() {
			return "1308-38-9";
		}
	},		
	NPO_CoO {
		@Override
		public String getSMILES() {
			return "[Co]=O";
		}
		@Override
		public String getFormula() {
			return "CoO";
		}
		@Override
		public String getAnnotation() {
			return "NPO_1541";
		}
		@Override
		public String getCAS() {
			return "1307-96-6";
		}
	},
	NPO_CoO2o3 {
		@Override
		public String getSMILES() {
			return "O=[Co]O[Co]=O";
		}
		@Override
		public String getFormula() {
			return "CoO2O3";
		}
		@Override
		public String getAnnotation() {
			return "NPO_1541";
		}
		@Override
		public String getCAS() {
			return "1308-04-9";
		}
	},	
	NPO_Co3O4 {
		@Override
		public String getSMILES() {
			return "O=[Co].O=[Co]O[Co]=O";
		}
		@Override
		public String getFormula() {
			return "Co3O4";
		}
		@Override
		public String getAnnotation() {
			return "NPO_1541";
		}
		@Override
		public String getCAS() {
			return "1308-06-1";
		}
	},		
	NPO_CoCr {
		@Override
		public String getSMILES() {
			return "[Co].[Cr]";
		}
		@Override
		public String getFormula() {
			return "CoCr";
		}
		@Override
		public String getAnnotation() {
			return "NPO_1384";
		}
	
	},		
	NPO_Bi2O3 {
		@Override
		public String getSMILES() {
			return "O=[Bi]O[Bi]=O";
		}
		@Override
		public String getFormula() {
			return "Bi2O3";
		}
		@Override
		public String getAnnotation() {
			return "NPO_1541";
		}
		@Override
		public String getCAS() {
			return "1304-76-3";
		}
	},			
	NPO_Al2O3 {
		@Override
		public String getSMILES() {
			return "O=[Al]O[Al]=O";
		}
		@Override
		public String getFormula() {
			return "Al2O3";
		}
		@Override
		public String getAnnotation() {
			return "NPO_1541";
		}
	
	},
	NPO_Mn2O3 {
		@Override
		public String getSMILES() {
			return "O=[Mn]O[Mn]=O";
		}
		@Override
		public String getFormula() {
			return "Mn2O3";
		}
		@Override
		public String getAnnotation() {
			return "NPO_1541";
		}
		@Override
		public String getCAS() {
			return "1317-34-6";
		}
	},
	NPO_NiO {
		@Override
		public String getSMILES() {
			return "O=[Ni]";
		}
		@Override
		public String getFormula() {
			return "NiO";
		}
		@Override
		public String getAnnotation() {
			return "NPO_1541";
		}
		@Override
		public String getCAS() {
			return "1313-99-1";
		}
	},
	NPO_WO3 {
		@Override
		public String getSMILES() {
			return "O=[W](=O)=O";
		}
		@Override
		public String getFormula() {
			return "WO3";
		}
		@Override
		public String getAnnotation() {
			return "NPO_1541";
		}
		@Override
		public String getCAS() {
			return "1314-35-8";
		}
		@Override
		public String getEINECS() {
			return "215-231-4";
		}
	}		
	;
	public String getSMILES() {
		return null;
	}

	public String getCAS() {
		return null;
	}
	
	public String getEINECS() {
		return null;
	}

	public String getReferenceUUID() {
		return StructureRecordValidator.generateUUIDfromString("XLSX", getCAS()==null?name():getCAS());
	}
	public String getFormula() {
		return null;
	}
	public String getAnnotation() {
		return name();
	}
}