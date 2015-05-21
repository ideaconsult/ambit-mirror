package ambit2.base.data.study;

import java.util.ArrayList;
import java.util.List;

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
    String category;
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

    // hack for IUCLID categories, as i can't find the exact titles in the
    // schema :(
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
	     * net.idea.i5.io.I5_ROOT_OBJECTS
	     * https://github.com/ideaconsult/i5/blob
	     * /master/iuclid_5_common/src/
	     * main/java/net/idea/i5/io/I5_ROOT_OBJECTS.java
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
		return 5110;
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
		return 5120;
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
		return 5210;
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
		return 5220;
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
		return 5230;
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
		return 5310;
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
		return 5320;
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
		return 5410;
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
		return 5420;
	    }

	    public String getTopCategory() {
		return "ENV FATE";
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
		return 7210;
	    }

	    public String getTopCategory() {
		return "TOX";
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
		return 7220;
	    }

	    public String getTopCategory() {
		return "TOX";
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
		return 7230;
	    }

	    public String getTopCategory() {
		return "TOX";
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
		return 7310;
	    }

	    public String getTopCategory() {
		return "TOX";
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
		return 7320;
	    }

	    public String getTopCategory() {
		return "TOX";
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
		return 7410;
	    }

	    public String getTopCategory() {
		return "TOX";
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
		return 7411;
	    }

	    public String getTopCategory() {
		return "TOX";
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
		return 7412;
	    }

	    public String getTopCategory() {
		return "TOX";
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
		return 7413;
	    }

	    public String getTopCategory() {
		return "TOX";
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
		return 7414;
	    }

	    public String getTopCategory() {
		return "TOX";
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
		return 751;
	    }

	    public String getTopCategory() {
		return "TOX";
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
		return 752;
	    }

	    public String getTopCategory() {
		return "TOX";
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
		return 753;
	    }

	    public String getTopCategory() {
		return "TOX";
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
		return 761;
	    }

	    public String getTopCategory() {
		return "TOX";
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
		return 762;
	    }

	    public String getTopCategory() {
		return "TOX";
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
		return 770;
	    }

	    public String getTopCategory() {
		return "TOX";
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
		return 781;
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
		return 782;
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
		return 611;
	    }

	    public String getTopCategory() {
		return "ECOTOX";
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
		return 612;
	    }

	    public String getTopCategory() {
		return "ECOTOX";
	    }
	},
	EC_DAPHNIATOX_SECTION {
	    @Override
	    public String toString() {
		return "Short-term toxicity to aquatic inverterbrates";
	    }

	    @Override
	    public String getNumber() {
		return "6.1.3";
	    }

	    @Override
	    public int getSortingOrder() {
		return 613;
	    }

	    public String getTopCategory() {
		return "ECOTOX";
	    }
	},
	EC_CHRONDAPHNIATOX_SECTION {
	    @Override
	    public String toString() {
		return "Long-term toxicity to aquatic inverterbrates";
	    }

	    @Override
	    public String getNumber() {
		return "6.1.4";
	    }

	    @Override
	    public int getSortingOrder() {
		return 614;
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
		return 615;
	    }

	    public String getTopCategory() {
		return "ECOTOX";
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
		return 617;
	    }

	    public String getTopCategory() {
		return "ECOTOX";
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
		return 620;
	    }

	    public String getTopCategory() {
		return "ECOTOX";
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
		return 631;
	    }

	    public String getTopCategory() {
		return "ECOTOX";
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
		return 632;
	    }

	    public String getTopCategory() {
		return "ECOTOX";
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
		return 633;
	    }

	    public String getTopCategory() {
		return "ECOTOX";
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
		return 634;
	    }

	    public String getTopCategory() {
		return "ECOTOX";
	    }
	},
	AGGLOMERATION_AGGREGATION_SECTION {
	    @Override
	    public String toString() {
		return "Nanomaterial agglomeration/aggregation";
	    }

	    @Override
	    public String getNumber() {
		return "4.24";
	    }

	    @Override
	    public int getSortingOrder() {
		return 424;
	    }

	    public String getTopCategory() {
		return "P-CHEM";
	    }
	},
	CRYSTALLINE_PHASE_SECTION {
	    @Override
	    public String toString() {
		return "Nanomaterial crystalline phase";
	    }

	    @Override
	    public String getNumber() {
		return "4.25";
	    }

	    @Override
	    public int getSortingOrder() {
		return 425;
	    }

	    public String getTopCategory() {
		return "P-CHEM";
	    }
	},
	CRYSTALLITE_AND_GRAIN_SIZE_SECTION {
	    @Override
	    public String toString() {
		return "Nanomaterial crystallite and grain size";
	    }

	    @Override
	    public String getNumber() {
		return "4.26";
	    }

	    @Override
	    public int getSortingOrder() {
		return 426;
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
		return "4.27";
	    }

	    @Override
	    public int getSortingOrder() {
		return 427;
	    }

	    public String getTopCategory() {
		return "P-CHEM";
	    }
	},
	SPECIFIC_SURFACE_AREA_SECTION {
	    @Override
	    public String toString() {
		return "Nanomaterial specific surface area";
	    }

	    @Override
	    public String getNumber() {
		return "4.28";
	    }

	    @Override
	    public int getSortingOrder() {
		return 428;
	    }

	    public String getTopCategory() {
		return "P-CHEM";
	    }
	},
	ZETA_POTENTIAL_SECTION {
	    @Override
	    public String toString() {
		return "Nanomaterial zeta potential";
	    }

	    @Override
	    public String getNumber() {
		return "4.29";
	    }

	    @Override
	    public int getSortingOrder() {
		return 429;
	    }

	    public String getTopCategory() {
		return "P-CHEM";
	    }
	},
	SURFACE_CHEMISTRY_SECTION {
	    @Override
	    public String toString() {
		return "Nanomaterial surface chemistry";
	    }

	    @Override
	    public String getNumber() {
		return "4.30";
	    }

	    @Override
	    public int getSortingOrder() {
		return 430;
	    }

	    public String getTopCategory() {
		return "P-CHEM";
	    }
	},
	DUSTINESS_SECTION {
	    @Override
	    public String toString() {
		return "Nanomaterial dustiness";
	    }

	    @Override
	    public String getNumber() {
		return "4.31";
	    }

	    @Override
	    public int getSortingOrder() {
		return 431;
	    }

	    public String getTopCategory() {
		return "P-CHEM";
	    }
	},
	POROSITY_SECTION {
	    @Override
	    public String toString() {
		return "Nanomaterial porosity";
	    }

	    @Override
	    public String getNumber() {
		return "4.32";
	    }

	    @Override
	    public int getSortingOrder() {
		return 432;
	    }

	    public String getTopCategory() {
		return "P-CHEM";
	    }
	},
	POUR_DENSITY_SECTION {
	    @Override
	    public String toString() {
		return "Nanomaterial pour density";
	    }

	    @Override
	    public String getNumber() {
		return "4.33";
	    }

	    @Override
	    public int getSortingOrder() {
		return 433;
	    }

	    public String getTopCategory() {
		return "P-CHEM";
	    }
	},
	PHOTOCATALYTIC_ACTIVITY_SECTION {
	    @Override
	    public String toString() {
		return "Nanomaterial photocatalytic activity";
	    }

	    @Override
	    public String getNumber() {
		return "4.34";
	    }

	    @Override
	    public int getSortingOrder() {
		return 434;
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
		return "4.36";
	    }

	    @Override
	    public int getSortingOrder() {
		return 436;
	    }

	    public String getTopCategory() {
		return "P-CHEM";
	    }
	},
	UNKNOWN_TOXICITY_SECTION {
	    @Override
	    public String toString() {
		return "Unclassified toxicity";
	    }

	    @Override
	    public String getNumber() {
		return "7.99";
	    }

	    @Override
	    public int getSortingOrder() {
		return 799;
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
	},
	PUBCHEM_SUMMARY_SECTION {
	    @Override
	    public String getTopCategory() {
		return "TOX";
	    }
	},
	PUBCHEM_SCREENING_SECTION {
	    @Override
	    public String getTopCategory() {
		return "TOX";
	    }
	},
	PUBCHEM_CELLBASED_SECTION {
	    @Override
	    public String getTopCategory() {
		return "TOX";
	    }
	},
	PUBCHEM_DOSERESPONSE_SECTION {
	    @Override
	    public String getTopCategory() {
		return "TOX";
	    }
	},
	PUBCHEM_PANEL_SECTION {
	    @Override
	    public String getTopCategory() {
		return "TOX";
	    }
	},
	PUBCHEM_BIOCHEMICAL_SECTION {
	    @Override
	    public String getTopCategory() {
		return "TOX";
	    }
	},
	PUBCHEM_INVIVO_SECTION {
	    @Override
	    public String getTopCategory() {
		return "TOX";
	    }
	},
	PUBCHEM_ACTIVECONCENTRATIONSPECIFIED_SECTION {
	    @Override
	    public String getTopCategory() {
		return "TOX";
	    }
	},
	PUBCHEM_INVITRO_SECTION {
	    @Override
	    public String getTopCategory() {
		return "TOX";
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

	},
	NA {

	    @Override
	    public String getNumber() {
		return "10.12";
	    }

	    @Override
	    public String getTopCategory() {
		return "TOX";
	    }
	};

	;
	public String getTopCategory() {
	    return "P-CHEM";
	}

	public String getNumber() {
	    return String.format("10.%d", ordinal());
	}

	public String toString() {
	    return name().replace("_", " ").replace("SECTION", "");
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
	this.guideline.add(guide);
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
