package ambit2.base.data.study;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.CaseFormat;

import ambit2.base.json.JSONUtils;

/**
 * <pre>
 *  	"protocol": {
 *         "topcategory": "TOX",
 *         "category": {
 *             "code": "TO_ACUTE_INHAL_SECTION",
 *             "title": "7.2.2Acutetoxicity-inhalation"
 *         },
 *         "endpoint": "Acute toxicity: inhalation,IUC4#10/Ch.5.1.2",
 *         "guideline": [
 *             "Method: other: sensoryirritationaccordingtoAlarie,Y.;(nofurtherdata)"
 *         ]
 *     }
 * </pre>
 * 
 * @author nina
 * 
 */
public class Protocol {
	private String category;
	/**
	 * <pre>
	 *         "endpoint": "Acute toxicity: inhalation,IUC4#10/Ch.5.1.2",
	 * </pre>
	 */
	String endpoint;
	/**
	 * <pre>
	 * 	"guideline" : []
	 * </pre>
	 */
	List<String> guideline;

	/**
	 * TODO refactor this to make ontology use easier
	 * 
	 * @author nina
	 *
	 */
	public enum _categories {
		/**
		 * Phys chem properties
		 */
		GI_GENERAL_INFORM_SECTION {
			@Override
			public String toString() {
				return "Appearance";
			}

			@Override
			public String getNumber() {
				return "4.1";
			}

			@Override
			public int getSortingOrder() {
				return 4010;
			}

			/**
			 * net.idea.i5.io.I5_ROOT_OBJECTS https://github.com/ideaconsult/i5/blob
			 * /master/iuclid_5_common/src/ main/java/net/idea/i5/io/I5_ROOT_OBJECTS.java
			 */
			public String getTopCategory() {
				return "P-CHEM";
			}
		},
		PC_MELTING_SECTION {
			@Override
			public String toString() {
				return "Melting point / freezing point";
			}

			@Override
			public String getNumber() {
				return "4.2";
			}

			@Override
			public int getSortingOrder() {
				return 4020;
			}

			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				return "http://semanticscience.org/resource/CHEMINF_000256";
			}
		},
		PC_BOILING_SECTION {
			@Override
			public String toString() {
				return "Boiling point";
			}

			@Override
			public String getNumber() {
				return "4.3";
			}

			@Override
			public int getSortingOrder() {
				return 4030;
			}

			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				return "http://semanticscience.org/resource/CHEMINF_000257";
			}
		},
		PC_DENSITY_SECTION {
			@Override
			public String toString() {
				return "Density";
			}

			@Override
			public String getNumber() {
				return "4.4";
			}

			@Override
			public int getSortingOrder() {
				return 4040;
			}

			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_0000084";
			}

		},
		PC_GRANULOMETRY_SECTION {
			@Override
			public String toString() {
				return "Particle size distribution (Granulometry)";
			}

			@Override
			public String getNumber() {
				return "4.5";
			}

			@Override
			public int getSortingOrder() {
				return 4050;
			}

			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.obolibrary.org/obo/CHMO_0002119";
			}
		},
		PC_VAPOUR_SECTION {
			@Override
			public String toString() {
				return "Vapour pressure";
			}

			@Override
			public String getNumber() {
				return "4.6";
			}

			@Override
			public int getSortingOrder() {
				return 4060;
			}

			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				// http://purl.obolibrary.org/obo/CHMO_0002119
				return "http://semanticscience.org/resource/CHEMINF_000419";
			}
		},
		SURFACE_TENSION_SECTION {
			@Override
			public String toString() {
				return "Surface tension";
			}

			@Override
			public String getNumber() {
				return "4.10";
			}

			@Override
			public int getSortingOrder() {
				return 4100;
			}

			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				// http://purl.obolibrary.org/obo/CHMO_0002119
				return "http://semanticscience.org/resource/CHEMINF_000420";
			}
		},
		PC_PARTITION_SECTION {
			@Override
			public String toString() {
				return "Partition coefficient";
			}

			@Override
			public String getNumber() {
				return "4.7";
			}

			@Override
			public int getSortingOrder() {
				return 4070;
			}

			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				// http://purl.obolibrary.org/obo/CHMO_0002119
				// return "CHMO_0002119";
				return "http://www.bioassayontology.org/bao#BAO_0002130";
			}
		},
		PC_WATER_SOL_SECTION {
			@Override
			public String toString() {
				return "Water solubility";
			}

			@Override
			public String getNumber() {
				return "4.8";
			}

			@Override
			public int getSortingOrder() {
				return 4080;
			}

			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				return "http://www.bioassayontology.org/bao#BAO_0002775";
			}
		},
		PC_SOL_ORGANIC_SECTION {
			@Override
			public String toString() {
				return "Solubility in organic solvents";
			}

			@Override
			public String getNumber() {
				return "4.9";
			}

			@Override
			public int getSortingOrder() {
				return 4090;
			}

			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				return "http://www.bioassayontology.org/bao#BAO_0002135";
			}
		},
		PC_NON_SATURATED_PH_SECTION {
			@Override
			public String toString() {
				return "pH";
			}

			@Override
			public String getNumber() {
				return "4.20";
			}

			@Override
			public int getSortingOrder() {
				return 4200;
			}

			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				return "UO_0000196";
			}
		},
		PC_DISSOCIATION_SECTION {
			@Override
			public String toString() {
				return "Dissociation constant";
			}

			@Override
			public String getNumber() {
				return "4.21";
			}

			@Override
			public int getSortingOrder() {
				return 4210;
			}

			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				return "http://semanticscience.org/resource/CHEMINF_000194";
			}

		},
		PC_VISCOSITY_SECTION {
			@Override
			public String toString() {
				return "Viscosity";
			}

			@Override
			public String getNumber() {
				return "4.22";
			}

			@Override
			public int getSortingOrder() {
				return 4220;
			}

			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.bioontology.org/ontology/npo#NPO_1852";
			}

		},
		BIO_NANO_INTERACTION_SECTION {
			@Override
			public String toString() {
				return "Bio-nano interactions";
			}

			@Override
			public String getNumber() {
				return "";
			}

			@Override
			public int getSortingOrder() {
				return 4890;
			}

			public String getTopCategory() {
				return "P-CHEM";
			}
		},		
		PC_UNKNOWN_SECTION {
			@Override
			public String toString() {
				return "Physico chemical properties (other)";
			}

			@Override
			public String getNumber() {
				return "4.99";
			}

			@Override
			public int getSortingOrder() {
				return 4990;
			}

			public String getTopCategory() {
				return "P-CHEM";
			}
		},
		/**
		 * Environmental fate
		 */
		TO_PHOTOTRANS_AIR_SECTION {
			@Override
			public String toString() {
				return "Phototransformation in Air";
			}

			@Override
			public String getNumber() {
				return "5.1.1";
			}

			@Override
			public int getSortingOrder() {
				return 5011;
			}

			public String getTopCategory() {
				return "ENV FATE";
			}
		},
		TO_PHOTOTRANS_SOIL_SECTION {
			@Override
			public String toString() {
				return "Phototransformation in soil";
			}

			@Override
			public String getNumber() {
				return "5.1.2";
			}

			@Override
			public int getSortingOrder() {
				return 5012;
			}

			public String getTopCategory() {
				return "ENV FATE";
			}
		},
		TO_HYDROLYSIS_SECTION {
			@Override
			public String toString() {
				return "Hydrolysis";
			}

			@Override
			public String getNumber() {
				return "5.1.2";
			}

			@Override
			public int getSortingOrder() {
				return 5012;
			}

			public String getTopCategory() {
				return "ENV FATE";
			}
		},
		TO_BIODEG_WATER_SCREEN_SECTION {
			@Override
			public String toString() {
				return "Biodegradation in water - screening tests";
			}

			@Override
			public String getNumber() {
				return "5.2.1";
			}

			@Override
			public int getSortingOrder() {
				return 5021;
			}

			public String getTopCategory() {
				return "ENV FATE";
			}
		},
		TO_BIODEG_WATER_SIM_SECTION {
			@Override
			public String toString() {
				return "Biodegradation in water and sediment: simulation tests";
			}

			@Override
			public String getNumber() {
				return "5.2.2";
			}

			@Override
			public int getSortingOrder() {
				return 5022;
			}

			public String getTopCategory() {
				return "ENV FATE";
			}
		},
		EN_STABILITY_IN_SOIL_SECTION {
			@Override
			public String toString() {
				return "Biodegradation in Soil";
			}

			@Override
			public String getNumber() {
				return "5.2.3";
			}

			@Override
			public int getSortingOrder() {
				return 5023;
			}

			public String getTopCategory() {
				return "ENV FATE";
			}
		},
		EN_BIOACCUMULATION_SECTION {
			@Override
			public String toString() {
				return "Bioaccumulation: aquatic / sediment";
			}

			@Override
			public String getNumber() {
				return "5.3.1";
			}

			@Override
			public int getSortingOrder() {
				return 5031;
			}

			public String getTopCategory() {
				return "ENV FATE";
			}
		},
		EN_BIOACCU_TERR_SECTION {
			@Override
			public String toString() {
				return "Bioaccumulation: terrestrial";
			}

			@Override
			public String getNumber() {
				return "5.3.2";
			}

			@Override
			public int getSortingOrder() {
				return 5032;
			}

			public String getTopCategory() {
				return "ENV FATE";
			}
		},
		EN_ADSORPTION_SECTION {
			@Override
			public String toString() {
				return "Adsorption / Desorption";
			}

			@Override
			public String getNumber() {
				return "5.4.1";
			}

			@Override
			public int getSortingOrder() {
				return 5041;
			}

			public String getTopCategory() {
				return "ENV FATE";
			}
		},
		EN_HENRY_LAW_SECTION {
			@Override
			public String toString() {
				return "Henry's Law constant";
			}

			@Override
			public String getNumber() {
				return "5.4.2";
			}

			@Override
			public int getSortingOrder() {
				return 5042;
			}

			public String getTopCategory() {
				return "ENV FATE";
			}

			@Override
			public String getOntologyURI() {
				return "http://semanticscience.org/resource/CHEMINF_000433";
			}
		},

		/**
		 * Toxicity
		 */
		TO_ACUTE_ORAL_SECTION {
			@Override
			public String toString() {
				return "Acute toxicity - oral";
			}

			@Override
			public String getNumber() {
				return "7.2.1";
			}

			@Override
			public int getSortingOrder() {
				return 7021;
			}

			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_0000020";
			}
		},
		TO_ACUTE_PULMONARY_INSTILLATION_SECTION {
			@Override
			public String toString() {
				return "Acute toxicity - pulmonary instillation";
			}

			@Override
			public String getNumber() {
				return "";
			}

			@Override
			public int getSortingOrder() {
				return 7029;
			}

			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public String getOntologyURI() {
				return "";
			}
		},
		TO_ACUTE_INHAL_SECTION {
			@Override
			public String toString() {
				return "Acute toxicity - inhalation";
			}

			@Override
			public String getNumber() {
				return "7.2.2";
			}

			@Override
			public int getSortingOrder() {
				return 7022;
			}

			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_0000023";
			}
		},
		TO_ACUTE_DERMAL_SECTION {
			@Override
			public String toString() {
				return "Acute toxicity - dermal";
			}

			@Override
			public String getNumber() {
				return "7.2.3";
			}

			@Override
			public int getSortingOrder() {
				return 7023;
			}

			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_0000026";
			}
		},
		TO_ACUTE_OTHER_SECTION {
			@Override
			public String toString() {
				return "Acute toxicity: other routes";
			}

			@Override
			public String getNumber() {
				return "7.2.4";
			}

			@Override
			public int getSortingOrder() {
				return 7021;
			}

			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_0000020";
			}
		},		
		TO_SKIN_IRRITATION_SECTION {
			@Override
			public String toString() {
				return "Skin irritation / Corrosion";
			}

			@Override
			public String getNumber() {
				return "7.3.1";
			}

			@Override
			public int getSortingOrder() {
				return 7031;
			}

			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_0000032";
			}
		},
		TO_EYE_IRRITATION_SECTION {
			@Override
			public String toString() {
				return "Eye irritation";
			}

			@Override
			public String getNumber() {
				return "7.3.2";
			}

			@Override
			public int getSortingOrder() {
				return 7032;
			}

			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_0000033";
			}
		},
		TO_SENSITIZATION_SECTION {
			@Override
			public String toString() {
				return "Skin sensitisation";
			}

			@Override
			public String getNumber() {
				return "7.4.1";
			}

			@Override
			public int getSortingOrder() {
				return 7041;
			}

			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_0000034";
			}
		},
		TO_SENSITIZATION_INSILICO_SECTION {
			@Override
			public String toString() {
				return "Skin sensitisation (in silico)";
			}

			@Override
			public String getNumber() {
				return "7.4.1";
			}

			@Override
			public int getSortingOrder() {
				return 7041;
			}

			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_0000034";
			}
		},
		TO_SENSITIZATION_INVITRO_SECTION {
			@Override
			public String toString() {
				return "Skin sensitisation (in vitro)";
			}

			@Override
			public String getNumber() {
				return "7.4.1";
			}

			@Override
			public int getSortingOrder() {
				return 7041;
			}

			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_0000034";
			}
		},
		TO_SENSITIZATION_INCHEMICO_SECTION {
			@Override
			public String toString() {
				return "Skin sensitisation (in chemico)";
			}

			@Override
			public String getNumber() {
				return "7.4.1";
			}

			@Override
			public int getSortingOrder() {
				return 7041;
			}

			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_0000034";
			}
		},
		TO_SENSITIZATION_HUMANDB_SECTION {
			@Override
			public String toString() {
				return "Skin sensitisation (human)";
			}

			@Override
			public String getNumber() {
				return "7.4.1";
			}

			@Override
			public int getSortingOrder() {
				return 7041;
			}

			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_0000034";
			}
		},
		TO_SENSITIZATION_LLNA_SECTION {
			@Override
			public String toString() {
				return "Skin sensitisation (LLNA)";
			}

			@Override
			public String getNumber() {
				return "7.4.1";
			}

			@Override
			public int getSortingOrder() {
				return 7041;
			}

			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_0000034";
			}
		},
		TO_REPEATED_ORAL_SECTION {
			@Override
			public String toString() {
				return "Repeated dose toxicity - oral";
			}

			@Override
			public String getNumber() {
				return "7.5.1";
			}

			@Override
			public int getSortingOrder() {
				return 7051;
			}

			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_0000021";
			}
		},
	
		TO_REPEATED_INHAL_SECTION {
			@Override
			public String toString() {
				return "Repeated dose toxicity - inhalation";
			}

			@Override
			public String getNumber() {
				return "7.5.2";
			}

			@Override
			public int getSortingOrder() {
				return 7052;
			}

			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_0000024";
			}
		},
		TO_REPEATED_DERMAL_SECTION {
			@Override
			public String toString() {
				return "Repeated dose toxicity - dermal";
			}

			@Override
			public String getNumber() {
				return "7.5.3";
			}

			@Override
			public int getSortingOrder() {
				return 7053;
			}

			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_0000027";
			}
		},
		TO_REPEATED_OTHER_SECTION {
			@Override
			public String toString() {
				return "Repeated dose toxicity - other";
			}

			@Override
			public String getNumber() {
				return "7.5.4";
			}

			@Override
			public int getSortingOrder() {
				return 7051;
			}

			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_0000021";
			}
		},				
		TO_GENETIC_IN_VITRO_SECTION {
			@Override
			public String toString() {
				return "Genetic toxicity in vitro";
			}

			@Override
			public String getNumber() {
				return "7.6.1";
			}

			@Override
			public int getSortingOrder() {
				return 7061;
			}

			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://www.bioassayontology.org/bao#BAO_0002167";
			}
		},
		TO_GENETIC_IN_VIVO_SECTION {
			@Override
			public String toString() {
				return "Genetic toxicity in vivo";
			}

			@Override
			public String getNumber() {
				return "7.6.2";
			}

			@Override
			public int getSortingOrder() {
				return 7062;
			}

			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_0000028";
			}
		},
		TO_CARCINOGENICITY_SECTION {
			@Override
			public String toString() {
				return "Carcinogenicity";
			}

			@Override
			public String getNumber() {
				return "7.7";
			}

			@Override
			public int getSortingOrder() {
				return 7070;
			}

			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_0000029";
			}
		},
		TO_REPRODUCTION_SECTION {
			@Override
			public String toString() {
				return "Toxicity to reproduction";
			}

			@Override
			public String getNumber() {
				return "7.8.1";
			}

			@Override
			public int getSortingOrder() {
				return 7081;
			}

			public String getTopCategory() {
				return "TOX";
			}
		},
		TO_DEVELOPMENTAL_SECTION {
			@Override
			public String toString() {
				return "Developmental toxicity / teratogenicity";
			}

			@Override
			public String getNumber() {
				return "7.8.2";
			}

			@Override
			public int getSortingOrder() {
				return 7082;
			}

			public String getTopCategory() {
				return "TOX";
			}
		},

		/**
		 * Ecotoxicity
		 */
		EC_FISHTOX_SECTION {
			@Override
			public String toString() {
				return "Short-term toxicity to fish";
			}

			@Override
			public String getNumber() {
				return "6.1.1";
			}

			@Override
			public int getSortingOrder() {
				return 6011;
			}

			public String getTopCategory() {
				return "ECOTOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_0000010";
			}
		},
		EC_CHRONFISHTOX_SECTION {
			@Override
			public String toString() {
				return "Long-term toxicity to fish";
			}

			@Override
			public String getNumber() {
				return "6.1.2";
			}

			@Override
			public int getSortingOrder() {
				return 6012;
			}

			public String getTopCategory() {
				return "ECOTOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_0000011";
			}
		},
		EC_DAPHNIATOX_SECTION {
			@Override
			public String toString() {
				return "Short-term toxicity to aquatic invertebrates";
			}

			@Override
			public String getNumber() {
				return "6.1.3";
			}

			@Override
			public int getSortingOrder() {
				return 6013;
			}

			public String getTopCategory() {
				return "ECOTOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_0000006";
			}
		},
		EC_CHRONDAPHNIATOX_SECTION {
			@Override
			public String toString() {
				return "Long-term toxicity to aquatic invertebrates";
			}

			@Override
			public String getNumber() {
				return "6.1.4";
			}

			@Override
			public int getSortingOrder() {
				return 6014;
			}

			public String getTopCategory() {
				return "ECOTOX";
			}
		},
		EC_ALGAETOX_SECTION {
			@Override
			public String toString() {
				return "Toxicity to aquatic algae and cyanobacteria";
			}

			@Override
			public String getNumber() {
				return "6.1.5";
			}

			@Override
			public int getSortingOrder() {
				return 6015;
			}

			public String getTopCategory() {
				return "ECOTOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_0000005";
			}
		},
		EC_BACTOX_SECTION {
			@Override
			public String toString() {
				return "Toxicity to microorganisms";
			}

			@Override
			public String getNumber() {
				return "6.1.7";
			}

			@Override
			public int getSortingOrder() {
				return 6017;
			}

			public String getTopCategory() {
				return "ECOTOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_0000015";
			}
		},
		EC_SEDIMENTDWELLINGTOX_SECTION {
			@Override
			public String toString() {
				return "Sediment toxicity";
			}

			@Override
			public String getNumber() {
				return "6.2";
			}

			@Override
			public int getSortingOrder() {
				return 6020;
			}

			public String getTopCategory() {
				return "ECOTOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_0000009";
			}
		},
		EC_SOILDWELLINGTOX_SECTION {
			@Override
			public String toString() {
				return "Toxicity to soil macroorganisms";
			}

			@Override
			public String getNumber() {
				return "6.3.1";
			}

			@Override
			public int getSortingOrder() {
				return 6031;
			}

			public String getTopCategory() {
				return "ECOTOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_0000013";
			}
		},
		EC_HONEYBEESTOX_SECTION {
			@Override
			public String toString() {
				return "Toxicity to terrestrial arthropods";
			}

			@Override
			public String getNumber() {
				return "6.3.2";
			}

			@Override
			public int getSortingOrder() {
				return 6032;
			}

			public String getTopCategory() {
				return "ECOTOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_0000016";
			}
		},
		EC_PLANTTOX_SECTION {
			@Override
			public String toString() {
				return "Toxicity to terrestrial plants";
			}

			@Override
			public String getNumber() {
				return "6.3.3";
			}

			@Override
			public int getSortingOrder() {
				return 6033;
			}

			public String getTopCategory() {
				return "ECOTOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_0000017";
			}
		},
		EC_SOIL_MICRO_TOX_SECTION {
			@Override
			public String toString() {
				return "Toxicity to soil microorganisms";
			}

			@Override
			public String getNumber() {
				return "6.3.4";
			}

			@Override
			public int getSortingOrder() {
				return 6034;
			}

			public String getTopCategory() {
				return "ECOTOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_0000008";
			}
		},
		PC_THERMAL_STABILITY_SECTION {
			@Override
			public String toString() {
				return "Stability (thermal)";
			}

			@Override
			public String getNumber() {
				return "4.19";
			}

			@Override
			public int getSortingOrder() {
				return 4190;
			}

			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				return "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#C54072";
			}
		},

		RADICAL_FORMATION_POTENTIAL_SECTION {
			@Override
			public String toString() {
				return "Radical formation potential";
			}

			@Override
			public String getNumber() {
				// TBD
				return "4.28.12";
			}

			@Override
			public int getSortingOrder() {
				return 42812;
			}

			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				return null;
			}
		},
		AGGLOMERATION_AGGREGATION_SECTION {
			@Override
			public String toString() {
				return "Nanomaterial agglomeration/aggregation";
			}

			@Override
			public String getNumber() {
				return "4.28.01";
			}

			@Override
			public int getSortingOrder() {
				return 42801;
			}

			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				// return "NPO_1967 NPO_1968";
				return "http://purl.bioontology.org/ontology/npo#NPO_1967";
			}
		},
		CRYSTALLINE_PHASE_SECTION {
			@Override
			public String toString() {
				return "Nanomaterial crystalline phase";
			}

			@Override
			public String getNumber() {
				return "4.28.02";
			}

			@Override
			public int getSortingOrder() {
				return 42802;
			}

			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.bioontology.org/ontology/npo#NPO_1512";
			}
		},
		CRYSTALLITE_AND_GRAIN_SIZE_SECTION {
			@Override
			public String toString() {
				return "Nanomaterial crystallite and grain size";
			}

			@Override
			public String getNumber() {
				return "4.28.03";
			}

			@Override
			public int getSortingOrder() {
				return 42803;
			}

			public String getTopCategory() {
				return "P-CHEM";
			}
		},
		ASPECT_RATIO_SHAPE_SECTION {
			@Override
			public String toString() {
				return "Nanomaterial aspect ratio/shape";
			}

			@Override
			public String getNumber() {
				return "4.28.04";
			}

			@Override
			public int getSortingOrder() {
				return 42804;
			}

			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				// return "NPO_274 NPO_1365";
				return "http://purl.bioontology.org/ontology/npo#NPO_274";
			}
		},
		SPECIFIC_SURFACE_AREA_SECTION {
			@Override
			public String toString() {
				return "Nanomaterial specific surface area";
			}

			@Override
			public String getNumber() {
				return "4.28.05";
			}

			@Override
			public int getSortingOrder() {
				return 42805;
			}

			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.bioontology.org/ontology/npo#NPO_1235";
			}
		},
		ZETA_POTENTIAL_SECTION {
			@Override
			public String toString() {
				return "Nanomaterial zeta potential";
			}

			@Override
			public String getNumber() {
				return "4.28.06";
			}

			@Override
			public int getSortingOrder() {
				return 42806;
			}

			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.bioontology.org/ontology/npo#NPO_1302";
			}
		},
		SURFACE_CHEMISTRY_SECTION {
			@Override
			public String toString() {
				return "Nanomaterial surface chemistry";
			}

			@Override
			public String getNumber() {
				return "4.28.07";
			}

			@Override
			public int getSortingOrder() {
				return 42807;
			}

			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_0000090";
			}
		},
		DUSTINESS_SECTION {
			@Override
			public String toString() {
				return "Nanomaterial dustiness";
			}

			@Override
			public String getNumber() {
				return "4.28.08";
			}

			@Override
			public int getSortingOrder() {
				return 42808;
			}

			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_9000003";
			}
		},
		POROSITY_SECTION {
			@Override
			public String toString() {
				return "Nanomaterial porosity";
			}

			@Override
			public String getNumber() {
				return "4.28.9";
			}

			@Override
			public int getSortingOrder() {
				return 42809;
			}

			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				return "PATO_0000973";
			}
		},
		POUR_DENSITY_SECTION {
			@Override
			public String toString() {
				return "Nanomaterial pour density";
			}

			@Override
			public String getNumber() {
				return "4.28.10";
			}

			@Override
			public int getSortingOrder() {
				return 42810;
			}

			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_9000004";
			}
		},
		PHOTOCATALYTIC_ACTIVITY_SECTION {
			@Override
			public String toString() {
				return "Nanomaterial photocatalytic activity";
			}

			@Override
			public String getNumber() {
				return "4.28.11";
			}

			@Override
			public int getSortingOrder() {
				return 42811;
			}

			public String getTopCategory() {
				return "P-CHEM";
			}
		},
		CATALYTIC_ACTIVITY_SECTION {
			@Override
			public String toString() {
				return "Nanomaterial catalytic activity";
			}

			@Override
			public String getNumber() {
				return "4.28.13";
			}

			@Override
			public int getSortingOrder() {
				return 42813;
			}

			public String getTopCategory() {
				return "P-CHEM";
			}
		},
		ENM_0000081_SECTION {
			@Override
			public String toString() {
				return "Batch Dispersion quality";
			}

			@Override
			public String getNumber() {
				return "ENM_0000081";
			}

			@Override
			public int getSortingOrder() {
				return 4360;
			}

			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_0000081";
			}

			@Override
			public boolean deprecated() {
				// should not be category
				return true;
			}
		},
		BAO_0002189_SECTION {
			// to be merged with I5 category
			@Override
			public String toString() {
				return "Toxicity Assay";
			}

			@Override
			public String getNumber() {
				return "BAO_0002189";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public int getSortingOrder() {
				return 82189;
			}

			@Override
			public String getOntologyURI() {
				return "http://www.bioassayontology.org/bao#BAO_0002189";
			}

			@Override
			public boolean deprecated() {
				// assays should not be here
				return true;
			}
		},
		UNKNOWN_TOXICITY_SECTION {
			@Override
			public String toString() {
				return "Toxicity (other)";
			}

			@Override
			public String getNumber() {
				return "BAO_0002189";
			}

			@Override
			public int getSortingOrder() {
				return 7990;
			}

			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://www.bioassayontology.org/bao#BAO_0002189";
			}
		},
		RISKASSESSMENT_SECTION {
			@Override
			public String toString() {
				return "Risk assessment";
			}

			@Override
			public String getNumber() {
				return "MESH_D018570";
			}

			@Override
			public int getSortingOrder() {
				return 9999;
			}

			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.bioontology.org/ontology/MESH/D018570";
			}
		},
		SUPPORTING_INFO_SECTION {
			@Override
			public String toString() {
				return "Supporting information";
			}

			@Override
			public String getNumber() {
				return "7.999.9";
			}

			@Override
			public int getSortingOrder() {
				return 7999;
			}

			public String getTopCategory() {
				return "TOX";
			}
		},
		PUBCHEM_CONFIRMATORY_SECTION {
			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public boolean deprecated() {
				// too specific
				return true;
			}
		},
		PUBCHEM_SUMMARY_SECTION {
			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public boolean deprecated() {
				// too specific
				return true;
			}
		},
		PUBCHEM_SCREENING_SECTION {
			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public boolean deprecated() {
				// too specific
				return true;
			}
		},
		PUBCHEM_CELLBASED_SECTION {
			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public boolean deprecated() {
				// too specific
				return true;
			}
		},
		PUBCHEM_DOSERESPONSE_SECTION {
			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public boolean deprecated() {
				// too specific
				return true;
			}
		},
		PUBCHEM_PANEL_SECTION {
			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public boolean deprecated() {
				// too specific
				return true;
			}
		},
		PUBCHEM_BIOCHEMICAL_SECTION {
			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public boolean deprecated() {
				// too specific
				return true;
			}
		},
		PUBCHEM_INVIVO_SECTION {
			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public boolean deprecated() {
				// too specific
				return true;
			}
		},
		PUBCHEM_ACTIVECONCENTRATIONSPECIFIED_SECTION {
			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public boolean deprecated() {
				// too specific
				return true;
			}
		},
		PUBCHEM_INVITRO_SECTION {
			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public boolean deprecated() {
				// too specific
				return true;
			}
		},
		TRANSCRIPTOMICS_SECTION {
			@Override
			public String toString() {
				return "Transcriptomics";
			}

			@Override
			public int getSortingOrder() {
				return 801;
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public String getNumber() {
				return "OBI_0000424";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/OBI_0000424";
			}

		},
		PROTEOMICS_SECTION {
			@Override
			public String toString() {
				return "Proteomics";
			}

			@Override
			public int getSortingOrder() {
				return 800;
			}

			@Override
			public String getNumber() {
				return "8.100";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}
		},
		METABOLOMICS_SECTION {
			@Override
			public String toString() {
				return "Metabolomics";
			}

			@Override
			public int getSortingOrder() {
				return 800;
			}

			@Override
			public String getNumber() {
				return "";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}			
		},
		NPO_296_SECTION {
			@Override
			public String toString() {
				return "Cellular uptake";
			}

			@Override
			public int getSortingOrder() {
				return 83011;
			}

			@Override
			public String getNumber() {
				return "ENM_0000068";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.bioontology.org/ontology/npo#NPO_296";
			}
		},
		ENM_0000068_SECTION {
			@Override
			public String toString() {
				return "Cell Viability";
			}

			@Override
			public int getSortingOrder() {
				return 83009;
			}

			@Override
			public String getNumber() {
				return "ENM_0000068";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://www.bioassayontology.org/bao#ENM_0000068";
			}
		},
		BAO_0003009_SECTION {
			@Override
			public String toString() {
				return "Cell Viability Assay";
			}

			@Override
			public int getSortingOrder() {
				return 83009;
			}

			@Override
			public String getNumber() {
				return "BAO_0003009";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://www.bioassayontology.org/bao#BAO_0003009";
			}

			@Override
			public boolean deprecated() {
				// this should not be on endpoint category level!
				return true;
			}
		},
		BAO_0002993_SECTION {
			@Override
			public String toString() {
				return "Cytotoxicity Assay";
			}

			@Override
			public int getSortingOrder() {
				return 82993;
			}

			@Override
			public String getNumber() {
				return "BAO_0002993";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://www.bioassayontology.org/bao#BAO_0002993";
			}

			@Override
			public boolean deprecated() {
				// this should not be on endpoint category level!
				return true;
			}
		},
		BAO_0002100_SECTION {
			@Override
			public String toString() {
				return "Cell Growth Assay";
			}

			@Override
			public int getSortingOrder() {
				return 82100;
			}

			@Override
			public String getNumber() {
				return "BAO_0002100";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://www.bioassayontology.org/bao#BAO_0002100";
			}

			@Override
			public boolean deprecated() {
				// this should not be on endpoint category level!
				return true;
			}
		},
		BAO_0002167_SECTION {
			// to be merged with genotox
			@Override
			public String toString() {
				return "Genotoxicity Assay";
			}

			@Override
			public int getSortingOrder() {
				return 82167;
			}

			@Override
			public String getNumber() {
				return "BAO_0002167";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://www.bioassayontology.org/bao#BAO_0002167";
			}

			@Override
			public boolean deprecated() {
				// there is already genotox section
				return true;
			}
		},

		ENM_0000037_SECTION {
			// to be merged with I5 category
			@Override
			public String toString() {
				return "Oxidative Stress";
			}

			@Override
			public String getNumber() {
				return "ENM_0000037";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public int getSortingOrder() {
				return 82168;
			}

			@Override
			public String getOntologyURI() {
				return "http://www.bioassayontology.org/bao#ENM_0000037";
			}
		},
		BAO_0002168_SECTION {
			// to be merged with I5 category
			@Override
			public String toString() {
				return "Oxidative Stress Assay";
			}

			@Override
			public String getNumber() {
				return "BAO_0002168";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public int getSortingOrder() {
				return 82168;
			}

			@Override
			public String getOntologyURI() {
				return "http://www.bioassayontology.org/bao#BAO_0002168";
			}

			@Override
			public boolean deprecated() {
				// this should not be on endpoint category level!
				return true;
			}
		},
		BAO_0002084_SECTION {
			@Override
			public String toString() {
				return "Real-time PCR";
			}

			@Override
			public String getNumber() {
				return "BAO_0002084";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public int getSortingOrder() {
				return 82189;
			}

			@Override
			public String getOntologyURI() {
				return "http://www.bioassayontology.org/bao#BAO_0002084";
			}

			@Override
			public boolean deprecated() {
				// this should not be on endpoint category level!
				return true;
			}
		},
		BAO_0000451_SECTION {
			@Override
			public String toString() {
				return "Optical microscopy";
			}

			@Override
			public String getNumber() {
				return "BAO_0000451";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public int getSortingOrder() {
				return 82189;
			}

			@Override
			public String getOntologyURI() {
				return "http://www.bioassayontology.org/bao#BAO_0000451";
			}

			@Override
			public boolean deprecated() {
				// this should not be on endpoint category level!
				return true;
			}
		},
		BAO_0002733_SECTION {
			@Override
			public String toString() {
				return "HPLC (High-performance liquid chromatography)";
			}

			@Override
			public String getNumber() {
				return "BAO_0002733";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public int getSortingOrder() {
				return 82189;
			}

			@Override
			public String getOntologyURI() {
				return "http://www.bioassayontology.org/bao#BAO_0002733";
			}

			@Override
			public boolean deprecated() {
				// this should not be on endpoint category level!
				return true;
			}
		},
		CHMO_0000287_SECTION {
			@Override
			public String toString() {
				return "Fluorimetry";
			}

			@Override
			public String getNumber() {
				return "CHMO_0000287";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public int getSortingOrder() {
				return 82189;
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.obolibrary.org/obo/CHMO_0000287";
			}

			@Override
			public boolean deprecated() {
				// this should not be on endpoint category level!
				return true;
			}
		},
		MMO_0000368_SECTION {
			@Override
			public String toString() {
				return "Hemocytometer";
			}

			@Override
			public String getNumber() {
				return "MMO_0000368";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public int getSortingOrder() {
				return 82189;
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.obolibrary.org/obo/MMO_0000368";
			}

			@Override
			public boolean deprecated() {
				// this should not be on endpoint category level!
				return true;
			}
		},
		OBI_0302736_SECTION {
			@Override
			public String toString() {
				return "Comet Assay";
			}

			@Override
			public String getNumber() {
				return "OBI_0302736";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public int getSortingOrder() {
				return 82189;
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.obolibrary.org/obo/OBI_0302736";
			}

			@Override
			public boolean deprecated() {
				// this should not be on endpoint category level!
				return true;
			}
		},
		CSEO_00001191_SECTION {
			@Override
			public String toString() {
				return "Neutral Red Uptake Assay";
			}

			@Override
			public String getNumber() {
				return "CSEO_00001191";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public int getSortingOrder() {
				return 82189;
			}

			@Override
			public String getOntologyURI() {
				return "http://scai.fraunhofer.de/CSEO#CSEO_00001191";
			}

			@Override
			public boolean deprecated() {
				// this should not be on endpoint category level!
				return true;
			}
		},
		CHMO_0000239_SECTION {
			@Override
			public String toString() {
				return "GFAAS (Graphite Furnace Atomic Absorption Spectroscopy)";
			}

			@Override
			public String getNumber() {
				return "CHMO_0000239";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public int getSortingOrder() {
				return 82189;
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.obolibrary.org/obo/CHMO_0000239";
			}

			@Override
			public boolean deprecated() {
				// this should not be on endpoint category level!
				return true;
			}
		},
		CHMO_0000538_SECTION {
			@Override
			public String toString() {
				return "ICP-MS (Inductively Coupled Plasma-Mass Spectrometry)";
			}

			@Override
			public String getNumber() {
				return "CHMO_0000538";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public int getSortingOrder() {
				return 82189;
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.obolibrary.org/obo/CHMO_0000538";
			}

			@Override
			public boolean deprecated() {
				// this should not be on endpoint category level!
				return true;
			}
		},
		CHMO_0000234_SECTION {
			@Override
			public String toString() {
				return "AAS (Atomic Absorption Spectroscopy)";
			}

			@Override
			public String getNumber() {
				return "CHMO_0000234";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public int getSortingOrder() {
				return 82189;
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.obolibrary.org/obo/CHMO_0000234";
			}

			@Override
			public boolean deprecated() {
				// this should not be on endpoint category level!
				return true;
			}
		},
		NPO_1773_SECTION {

			@Override
			public String toString() {
				return "Spectrophotometer";
			}

			@Override
			public String getNumber() {
				return "NPO_1773";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public int getSortingOrder() {
				return 82190;
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.bioontology.org/ontology/npo#NPO_1773";
			}

			@Override
			public boolean deprecated() {
				// this should not be on endpoint category level!
				return true;
			}
		},
		NPO_1709_SECTION {

			@Override
			public String toString() {
				return "LDH Release Assay";
			}

			@Override
			public String getNumber() {
				return "NPO_1709";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public int getSortingOrder() {
				return 82190;
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.bioontology.org/ontology/npo#NPO_1709";
			}

			@Override
			public boolean deprecated() {
				// this should not be on endpoint category level!
				return true;
			}
		},

		ENM_8000223_SECTION {
			@Override
			public String toString() {
				return "Aerosol characterisation";
			}

			@Override
			public String getNumber() {
				return "ENM_8000223";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public int getSortingOrder() {
				return 82190;
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_8000223";
			}

			@Override
			public boolean deprecated() {
				// this should not be on endpoint category level!
				return true;
			}
		},
		ENM_0000069_SECTION {
			@Override
			public String toString() {
				return "Air Liquid Interface";
			}

			@Override
			public String getNumber() {
				return "ENM_9000011";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public int getSortingOrder() {
				return 82190;
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_0000069";
			}

			@Override
			public boolean deprecated() {
				// this should not be on endpoint category level!
				return true;
			}
		},

		ENM_9000011_SECTION {
			@Override
			public String toString() {
				return "Tumor Necrosis Factor assay";
			}

			@Override
			public String getNumber() {
				return "ENM_9000011";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public int getSortingOrder() {
				return 82190;
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_9000011";
			}

			@Override
			public boolean deprecated() {
				// this should not be on endpoint category level!
				return true;
			}
		},
		ENM_9000013_SECTION {
			@Override
			public String toString() {
				return "WST-1 assay";
			}

			@Override
			public String getNumber() {
				return "ENM_9000013";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public int getSortingOrder() {
				return 82191;
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_9000013";
			}

			@Override
			public boolean deprecated() {
				// this should not be on endpoint category level!
				return true;
			}
		},
		ENM_9000100_SECTION {
			@Override
			public String toString() {
				return "Boiling Point";
			}

			@Override
			public String getNumber() {
				return "ENM_9000100";
			}

			@Override
			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_9000100";
			}

			@Override
			public boolean deprecated() {
				// this should not be on endpoint category level! there is already boiling point
				// section
				return true;
			}
		},
		ENM_9000111_SECTION {
			@Override
			public String toString() {
				return "Particle Size Distribution D11";
			}

			@Override
			public String getNumber() {
				return "ENM_9000111";
			}

			@Override
			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_9000111";
			}

			@Override
			public boolean deprecated() {
				// this should not be on endpoint category level!
				return true;
			}
		},
		ENM_9000112_SECTION {
			@Override
			public String toString() {
				return "Particle Size Distribution D12";
			}

			@Override
			public String getNumber() {
				return "ENM_9000112";
			}

			@Override
			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_9000112";
			}

			@Override
			public boolean deprecated() {
				// this should not be on endpoint category level!
				return true;
			}
		},
		ENM_9000127_SECTION {
			@Override
			public String toString() {
				return "Particle Size Distribution D27";
			}

			@Override
			public String getNumber() {
				return "ENM_9000127";
			}

			@Override
			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_9000127";
			}

			@Override
			public boolean deprecated() {
				// this should not be on endpoint category level!
				return true;
			}
		},
		ENM_9000177_SECTION {
			@Override
			public String toString() {
				return "Particle Size Distribution D77";
			}

			@Override
			public String getNumber() {
				return "ENM_9000177";
			}

			@Override
			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_9000177";
			}

			@Override
			public boolean deprecated() {
				// this should not be on endpoint category level!
				return true;
			}
		},
		ENM_9000195_SECTION {
			@Override
			public String toString() {
				return "Particle Size Distribution D95";
			}

			@Override
			public String getNumber() {
				return "ENM_9000195";
			}

			@Override
			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_9000195";
			}

			@Override
			public boolean deprecated() {
				// this should not be on endpoint category level!
				return true;
			}
		},
		BAO_0010001_SECTION {

			@Override
			public String toString() {
				return "ATP Assay";
			}

			@Override
			public String getNumber() {
				return "BAO_0010001";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public int getSortingOrder() {
				return 82190;
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.bioontology.org/ontology/bao#BAO_0010001";
			}

			@Override
			public boolean deprecated() {
				// this should not be on endpoint category level!
				return true;
			}
		},

		NPO_1911_SECTION {

			@Override
			public String toString() {
				return "MTT Assay";
			}

			@Override
			public String getNumber() {
				return "NPO_1911";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public int getSortingOrder() {
				return 82190;
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.bioontology.org/ontology/npo#NPO_1911";
			}

			@Override
			public boolean deprecated() {
				// this should not be on endpoint category level!
				return true;
			}
		},

		NPO_1699_SECTION {

			@Override
			public String toString() {
				return "Particle Size Distribution";
			}

			@Override
			public String getNumber() {
				return "NPO_1699";
			}

			@Override
			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.bioontology.org/ontology/npo#NPO_1699";
			}

			@Override
			public boolean deprecated() {
				// this should not be on endpoint category level!
				return true;
			}
		},
		NPO_1914_SECTION {

			@Override
			public String toString() {
				return "Hydrodynamic Particle Size";
			}

			@Override
			public String getNumber() {
				return "NPO_1914";
			}

			@Override
			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.bioontology.org/ontology/npo#NPO_1914";
			}

			@Override
			public boolean deprecated() {
				// this should not be on endpoint category level!
				return true;
			}
		},
		NPO_1204_SECTION {

			@Override
			public String toString() {
				return "Isoelectric Point";
			}

			@Override
			public String getNumber() {
				return "NPO_1204";
			}

			@Override
			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.bioontology.org/ontology/npo#NPO_1204";
			}

			@Override
			public boolean deprecated() {
				// this should not be on endpoint category level!
				return true;
			}
		},
		NPO_1694_SECTION {

			@Override
			public String toString() {
				return "Particle Size";
			}

			@Override
			public String getNumber() {
				return "NPO_1694";
			}

			@Override
			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.bioontology.org/ontology/npo#NPO_1694";
			}

			@Override
			public boolean deprecated() {
				// this should not be on endpoint category level!
				return true;
			}
		},
		NPO_1539_SECTION {

			@Override
			public String toString() {
				return "Particle Diameter";
			}

			@Override
			public String getNumber() {
				return "NPO_1539";
			}

			@Override
			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.bioontology.org/ontology/npo#NPO_1539";
			}

			@Override
			public boolean deprecated() {
				// this should not be on endpoint category level!
				return true;
			}
		},
		PATO_0000921_SECTION {

			@Override
			public String toString() {
				return "Particle Width";
			}

			@Override
			public String getNumber() {
				return "PATO_0000921";
			}

			@Override
			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.obolibrary.org/obo/PATO_0000921";
			}

			@Override
			public boolean deprecated() {
				// this should not be on endpoint category level!
				return true;
			}
		},
		ENM_0000044_SECTION {
			@Override
			public String toString() {
				return "Barrier integrity";
			}

			@Override
			public String getNumber() {
				return "ENM_0000044";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public String getOntologyURI() {
				return "http://purl.enanomapper.org/onto/ENM_0000044";
			}
		},
		// Toxcast - BAO
		CELL_CYCLE {
			// obo:GO:0007049
			@Override
			public String getNumber() {
				return "10.1";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public int getSortingOrder() {
				return 1001;
			}

			@Override
			public boolean deprecated() {
				return true;
			}
		},
		CELL_DEATH {
			// obo:GO:0008219
			@Override
			public String getNumber() {
				return "10.2";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public int getSortingOrder() {
				return 1002;
			}

			@Override
			public boolean deprecated() {
				return true;
			}
		},
		CELL_MORPHOLOGY {
			// BAO_0170002
			@Override
			public String getNumber() {
				return "10.3";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public int getSortingOrder() {
				return 1003;
			}

			@Override
			public boolean deprecated() {
				return true;
			}
		},
		CELL_PROLIFERATION {
			// obo:GO:0008283
			@Override
			public String getNumber() {
				return "10.4";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public int getSortingOrder() {
				return 1004;
			}

			@Override
			public boolean deprecated() {
				return true;
			}
		},
		MITOCHONDRIAL_DEPOLARIZATION {

			@Override
			public String getNumber() {
				return "10.5";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public int getSortingOrder() {
				return 1005;
			}

			@Override
			public boolean deprecated() {
				return true;
			}
		},
		OXIDATIVE_PHOSPHORYLATION {
			@Override
			public String getNumber() {
				return "10.6";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public int getSortingOrder() {
				return 1006;
			}

			@Override
			public boolean deprecated() {
				return true;
			}
		},
		PROTEIN_STABILIZATION {
			// obo:GO:0050821
			@Override
			public String getNumber() {
				return "10.7";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public int getSortingOrder() {
				return 1007;
			}

			@Override
			public boolean deprecated() {
				return true;
			}
		},
		RECEPTOR_BINDING {
			// obo:GO:0005102
			@Override
			public String getNumber() {
				return "10.8";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public int getSortingOrder() {
				return 1008;
			}

			@Override
			public boolean deprecated() {
				return true;
			}
		},
		REGULATION_OF_CATALYTIC_ACTIVITY {
			// BAO_0002737
			@Override
			public String getNumber() {
				return "10.9";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public int getSortingOrder() {
				return 1009;
			}

			@Override
			public boolean deprecated() {
				return true;
			}
		},
		REGULATION_OF_GENE_EXPRESSION {
			// obo:GO:0010468
			@Override
			public String getNumber() {
				return "10.10";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public int getSortingOrder() {
				return 1010;
			}

			@Override
			public boolean deprecated() {
				return true;
			}
		},
		REGULATION_OF_TRANSCRIPTION_FACTOR_ACTIVITY {
			// obo:GO:0006355
			@Override
			public String getNumber() {
				return "10.11";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public int getSortingOrder() {
				return 1011;
			}

			@Override
			public boolean deprecated() {
				return true;
			}
		},
		AUTOFLUORESCENCE {

			@Override
			public String getNumber() {
				return "10.12";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public int getSortingOrder() {
				return 1012;
			}

			@Override
			public boolean deprecated() {
				return true;
			}
		},
		// Immunotoxicity
		NPO_1339_SECTION {
			@Override
			public String toString() {
				return "Immunotoxicity";
			}

			@Override
			public String getNumber() {
				return "NPO_1339";
			}

			@Override
			public String getTopCategory() {
				return "TOX";
			}

			@Override
			public int getSortingOrder() {
				return 1013;
			}

			public String getOntologyURI() {
				return "http://purl.obolibrary.org/obo/NPO_1339";
			}

		},
		IMPURITY_SECTION {
			@Override
			public String toString() {
				return "Elemental composition and chemical purity";
			}

			@Override
			public String getNumber() {
				// return "ncit:C79333";
				return "_";
			}

			@Override
			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public int getSortingOrder() {
				return 1013;
			}

			public String getOntologyURI() {
				return null;
				// ncit:C79333;
			}

		},
		ANALYTICAL_METHODS_SECTION {
			@Override
			public String toString() {
				return "Analytical Methods";
			}

			@Override
			public String getNumber() {
				return "CHMO_0001075";
			}

			@Override
			public String getTopCategory() {
				return "P-CHEM";
			}

			@Override
			public int getSortingOrder() {
				return 1013;
			}

			public String getOntologyURI() {
				return "http://purl.obolibrary.org/obo/CHMO_0001075";
			}

		},
		OMICS_SECTION {
			@Override
			public String getNumber() {
				return "_";
			}

			@Override
			public String getTopCategory() {
				return "OMICS";
			}

			@Override
			public String toString() {
				return "Omics";
			}

			@Override
			public int getSortingOrder() {
				return 3500;
			}

		},
		EXPOSURE_SECTION {
			@Override
			public String getNumber() {
				return "3.5.0";
			}

			@Override
			public String getTopCategory() {
				return "EXPOSURE";
			}

			@Override
			public String toString() {
				return "Use and exposure information";
			}

			@Override
			public int getSortingOrder() {
				return 3500;
			}

		},
		EXPOSURE_MANUFACTURE_SECTION {
			@Override
			public String getNumber() {
				return "3.5.1";
			}

			@Override
			public String getTopCategory() {
				return "EXPOSURE";
			}

			@Override
			public String toString() {
				return "Use and exposure information. Manufacture";
			}

			@Override
			public int getSortingOrder() {
				return 3510;
			}

		},
		EXPOSURE_FORMULATION_REPACKAGING_SECTION {
			@Override
			public String getNumber() {
				return "3.5.2";
			}

			@Override
			public String getTopCategory() {
				return "EXPOSURE";
			}

			@Override
			public String toString() {
				return "Use and exposure information. Formulation or re-packing";
			}

			@Override
			public int getSortingOrder() {
				return 3520;
			}
		},
		EXPOSURE_INDUSTRIAL_SITES_SECTION {
			@Override
			public String getNumber() {
				return "3.5.3";
			}

			@Override
			public String getTopCategory() {
				return "EXPOSURE";
			}

			@Override
			public String toString() {
				return "Use and exposure information. Uses at industrial sites";
			}

			@Override
			public int getSortingOrder() {
				return 3530;
			}
		},
		EXPOSURE_PROFESSIONAL_WORKERS_SECTION {
			@Override
			public String getNumber() {
				return "3.5.4";
			}

			@Override
			public String getTopCategory() {
				return "EXPOSURE";
			}

			@Override
			public String toString() {
				return "Use and exposure information. Widespread use by industrial workers";
			}

			@Override
			public int getSortingOrder() {
				return 3540;
			}
		},
		EXPOSURE_CONSUMER_USE_SECTION {
			@Override
			public String getNumber() {
				return "3.5.5";
			}

			@Override
			public String getTopCategory() {
				return "EXPOSURE";
			}

			@Override
			public String toString() {
				return "Use and exposure information. Consumer use";
			}

			@Override
			public int getSortingOrder() {
				return 3550;
			}
		},
		EXPOSURE_SERVICE_LIFE_SECTION {
			@Override
			public String getNumber() {
				return "3.5.6";
			}

			@Override
			public String getTopCategory() {
				return "EXPOSURE";
			}

			@Override
			public String toString() {
				return "Use and exposure information. Service Life";
			}

			@Override
			public int getSortingOrder() {
				return 3560;
			}
		};

		public boolean deprecated() {
			return false;
		}

		public String getTopCategory() {
			return "P-CHEM";
		}

		public String getNumber() {
			return String.format("10.%d", ordinal());
		}

		public String toString() {
			return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL,
					name().replace("_", " ").replace("SECTION", ""));
		}

		public int getSortingOrder() {
			return 10000;
		}

		public Protocol getProtocol(String guide) {
			Protocol protocol = new Protocol(guide);
			protocol.setCategory(name());
			protocol.setTopCategory(getTopCategory());
			return protocol;
		}

		public String getOntologyURI() {
			// generic assay by default
			return "http://www.bioassayontology.org/bao#BAO_0000015";
		}

	}

	public static enum _fields {
		topcategory, category, endpoint, guideline
	}

	protected String topCategory;

	public String getTopCategory() {
		return topCategory;
	}

	public void setTopCategory(String topCategory) {
		this.topCategory = topCategory;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<String> getGuideline() {
		return guideline;
	}

	public void setGuideline(List<String> guide) {
		this.guideline = guide;
	}

	public void addGuideline(String guide) {
		if (this.guideline == null)
			this.guideline = new ArrayList<String>();
		this.guideline.add(guide == null ? null : guide.trim());
	}

	public Protocol(String endpoint) {
		this(endpoint, null);
	}

	public Protocol(String endpoint, String guideline) {
		setEndpoint(endpoint);
		if (guideline != null)
			addGuideline(guideline);
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	@Override
	public String toString() {
		Protocol._categories pcategory = null;
		try {
			pcategory = Protocol._categories.valueOf(category);
		} catch (Exception x) {

		}

		StringBuilder b = new StringBuilder();
		b.append("{");

		b.append("\n\t");
		b.append(JSONUtils.jsonQuote(_fields.topcategory.name()));
		b.append(": ");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(topCategory)));
		b.append(",\n\t");
		b.append(JSONUtils.jsonQuote(_fields.category.name()));
		b.append(": {");
		b.append(JSONUtils.jsonQuote("code"));
		b.append(": ");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(category)));
		b.append(",");
		// onto
		if (pcategory != null) {
			b.append(JSONUtils.jsonQuote("term"));
			b.append(": ");
			b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(pcategory.getOntologyURI())));
			b.append(",");
		}
		b.append(JSONUtils.jsonQuote("title"));
		b.append(": ");
		try {
			b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(_categories.valueOf(category).getNumber()) + " "
					+ _categories.valueOf(category).toString()));
		} catch (Exception x) {
			b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(category)));
		}
		b.append("},\n\t");
		b.append(JSONUtils.jsonQuote(_fields.endpoint.name()));
		b.append(":");
		b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(endpoint)));
		b.append(",\n\t");
		b.append(JSONUtils.jsonQuote(_fields.guideline.name()));
		b.append(": [");
		if (guideline != null)
			for (int i = 0; i < guideline.size(); i++) {
				if (i > 0)
					b.append(",");
				b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(guideline.get(i))));
			}
		b.append("]}");
		return b.toString();
	}

}
