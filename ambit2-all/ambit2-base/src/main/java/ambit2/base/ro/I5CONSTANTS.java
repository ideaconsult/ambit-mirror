package ambit2.base.ro;

public class I5CONSTANTS {
	public static final String I5_STUDY_SECTION = "eu.europa.echa.schemas.iuclid5._%s.studyrecord.%s_SECTION";
    public static final String Physstate = "Physical state";
    public static final String Color = "Color";
    public static final String Odor = "Odor";
    public static final String Form = "Form";

    public static final String eRadicalFormation_Observations="RADICAL_FORMATION_OBSERVATION";
    public static final String eRadicalFormation_Quantitative="RADICAL_FORMATION_QUANTITATIVE";
    
    public static final String cSpecies = "Species";
    public static final String cSex = "Sex";

    public static final String cToxicity = "Toxicity";
    public static final String cGeneration = "Generation";
    public static final String cEffectType = "Effect type";
    public static final String cDoses = "Doses/concentrations";
    public static final String cRouteAdm = "Route of administration";
    public static final String cRoute = "Route";
    public static final String cReference = "Reference";
    public static final String cYear = "Study year";
    public static final String cTypeMethod = "Type of method";
    public static final String cTypeStudy = "Type of study";
    public static final String cTypeCoverage = "TYPE_COVERAGE";

    public static final String rDECOMPOSITION = "Decomposition";
    public static final String methodType = "Method type";
    public static final String pH = "pH";
    public static final String cTemperature = "Temperature";
    public static final String cConc = "Concentration";
    public static final String Remark = "Remark";
    public static final String cSolvent = "Solvent";
    public static final String Pressure = "Pressure";
    public static final String eVapourPressure = "Vapour Pressure";
    public static final String AtmPressure = "Atm. Pressure";
    public static final String eWaterSolubility = "Water solubility";
    public static final String rSUBLIMATION = "Sublimation";
    public static final String eMELTINGPOINT = "Melting Point";
    public static final String BOILINGPOINT = "Boiling point";
    public static final String HLC = "Henry's Law const.";
    public static final String pKa = "pKa";

    public static final String eBCF = "BCF";

    public static final String rOrgCarbonPercent = "% Org.Carbon";
    public static final String rDegradation = "Degradation";
    public static final String rDegradationRateConstant = "DegradationRateConstant";
    public static final String cDegradationParameter = "Degradation Parameter";
    public static final String cBioaccBasis = "Bioacc. basis";
    public static final String cTestType = "Test type";
    public static final String cTimePoint = "Sampling time";
    public static final String ePercentDegradation = "% Degradation";
    public static final String TestCondition = "Test condition";

    public static final String cSalinity = "Salinity";
    public static final String cExposure = "Exposure";
    public static final String cTestMedium = "Test Medium";
    public static final String cMEDIUM = "MEDIUM";
    public static final String cTestOrganism = "Test organism";
    public static final String cMeasuredConcentration = "Measured concentration";
    public static final String cEffect = "Effect";
    public static final String cBasisForEffect = "Basis for effect";
    public static final String cConcType = "Based on";

    public static final String cSoilNo = "Soil No."; // SOILNUMBER
    public static final String cSoilType = "Soil type"; // SOILTYPE
    public static final String cOCContent = "OC content"; // PRECISION_CARBON_LOQUALIFIER

    public static final String eGenotoxicity = "GENOTOXICITY";
    public static final String cTypeGenotoxicity = "Type of genotoxicity";
    public static final String cTargetGene = "Target gene";
    public static final String cMetabolicActivationSystem = "Metabolic activation system";
    public static final String cMetabolicActivation = "Metabolic activation";

    public static final String rNo = "No.";

    public static final String eSOLUBILITY_ORG_SOLVENT = "Solubility org. solvents";

    /*
     * Moved to ambit2.base.data.study._FIELDS_RANGE public static final String
     * unit = "unit"; public static final String loValue = "loValue"; public
     * static final String upValue = "upValue"; public static final String
     * loQualifier = "loQualifier"; public static final String upQualifier =
     * "upQualifier";
     */
    /*
     * Moved ot package ambit2.base.data.study._FIELDS_RELIABILITY; public
     * static final String r_id = "id"; public static final String r_value =
     * "value"; public static final String r_isRobustStudy = "isRobustStudy";
     * public static final String r_isUsedforClassification =
     * "isUsedforClassification"; public static final String r_isUsedforMSDS =
     * "isUsedforMSDS"; public static final String r_purposeFlag =
     * "purposeFlag"; public static final String r_studyResultType =
     * "studyResultType";
     */
    public static final String pReactant = "Reactant";

    // NM
    public static final String pSAMPLING = "SAMPLING";
    public static final String pMETHODDETAILS = "Method details";
    public static final String pDATA_GATHERING_INSTRUMENTS = "DATA_GATHERING_INSTRUMENTS";
    public static final String pMATERIAL_ISOTROPIC = "MATERIAL_ISOTROPIC";
    public static final String pTESTMAT_FORM = "TESTMAT_FORM";
    // particlesize

    public static final String eAGGLO_AGGR_DIAM = "AGGLOMERATION AGGREGATION DIAMETER";
    public static final String eAGGLO_AGGR_SIZE = "AGGLOMERATION AGGREGATION SIZE";
    public static final String eAGGLO_AGGR_SIZE_DIST = "AGGLOMERATION AGGREGATION SIZE DIST";
    public static final String eAGGLO_AGGR_DISTRIBUTION = "AGGLOMERATION AGGREGATION SIZE DISTRIBUTION";

    public static final String eAGGLO_AGGREGATION_ID = "AGGLOMERATION AGGREGATION INDEX";

    public static final String pDISTRIBUTION_TYPE = "DISTRIBUTION_TYPE";
    public static final String pDISTRIBUTION = "DISTRIBUTION";
    public static final String pMMD = "MASS MEDIAN DIAMETER";
    public static final String pMMAD = "MASS_MEDIAN_AERODYNAMIC_DIAMETER";
    public static final String eAERODYNAMIC_DIAMETER = "AERODYNAMIC_DIAMETER";
    
    public static final String pGSD = "GEOMETRIC STANDARD DEVIATION";
    public static final String pPARTICLESIZE = "PARTICLE SIZE";
    
    public static final String pCOATING = "COATING";
    public static final String pHasCOATING = "HAS_COATING";
    public static final String pHasFUNCTIONALIZATION = "HAS_FUNCTIONALIZATION";
    public static final String pFUNCTIONALIZATION = "FUNCTIONALIZATION";
    public static final String pTYPE = "TYPE";
    public static final String pDESCRIPTION = "DESCRIPTION";
    public static final String pCOATING_DESCRIPTION = "COATING_DESCRIPTION";

    public static final String eATOMIC_COMPOSITION = "ATOMIC COMPOSITION";
    public static final String eFUNCTIONAL_GROUP = "FUNCTIONAL GROUP";
    public static final String pELEMENT_OR_GROUP = "ELEMENT_OR_GROUP";
    public static final String eZETA_POTENTIAL = "ZETA POTENTIAL";
    public static final String eISOELECTRIC_POINT = "ISOELECTRIC POINT";
    //element composition 
    public static final String eFRACTION = "CONTRIBUTION";

    public static final String eSHAPE = "SHAPE_PERCENTAGE";
    public static final String eSHAPE_DESCRIPTIVE = "SHAPE";
    public static final String eASPECT_RATIO = "ASPECT RATIO";
    public static final String eASPECT_RATIO_X = "X";
    public static final String eASPECT_RATIO_Y = "Y";
    public static final String eASPECT_RATIO_Z = "Z";
    public static final String eDUSTINEX = "DUSTINESS INDEX";
    public static final String ePOROSITY = "POROSITY";
    public static final String eSPECIFIC_PORE_VOLUME = "SPECIFIC_PORE_VOLUME";
    public static final String eMODAL_PORE_VOLUME = "MODAL_PORE_VOLUME";
    public static final String ePOUR_DENSITY = "POUR DENSITY";
    public static final String eDENSITY = "DENSITY";
    public static final String eVISCOSITY = "VISCOSITY";
    public static final String eSURFACE_TENSION = "SURFACE_TENSION";
    public static final String eCATALYTIC_ACTIVITY = "CATALYTIC_ACTIVITY";
    public static final String ePHOTOCATALYTIC_ACTIVITY = "PHOTOCATALYTIC_ACTIVITY";
    public static final String eTURNOVERFREQUENCY = "TURN_OVER_FREQUENCY";
    public static final String eCRYSTALLINE_PHASE = "CRYSTALLINE_PHASE";

    public static final String rCRYSTALLINE_PHASE_COMMON_NAME = "COMMON NAME";
    public static final String rCRYSTALLINE_PHASE_CRYSTAL_SYSTEM = "CRYSTAL_SYSTEM";
    public static final String rCRYSTALLINE_PHASE_BRAVAIS_LATTICE = "BRAVAIS_LATTICE";
    public static final String rCRYSTALLINE_PHASE_POINT_GROUP = "POINT_GROUP";
    public static final String rCRYSTALLINE_PHASE_SPACE_GROUP = "SPACE_GROUP";
    public static final String rCRYSTALLINE_PHASE_CRYSTGRPH_PLANES = "CRYSTALLOGRAPHIC_PLANES";
    
    public static final String eCRYSTALLITE_SIZE = "CRYSTALLITE_SIZE";

    public static final String rSTD_DEV = "STD_DEV";

    public static final String cPERCENTILE = "PERCENTILE";
    public static final String rPERCENTILE_DT50 = "DT50";
    public static final String cSEQ_NUM = "SEQ_NUM";

    public static final String AGGLO_AGGR_DIAM = "AGGLO_AGGR_DIAM";
    public static final String AGGLO_AGGR_SIZE = "AGGLO_AGGR_SIZE";
    public static final String AGGLO_AGGR_SIZE_DIST = "AGGLO_AGGR_SIZE_DIST";
    public static final String AGGLO_AGGREGATION_IDX = "AGGLO_AGGREGATION_IDX";

    public static final String MEAN_DIAMETER = "MEAN DIAMETER";
    public static final String X = "X";
    public static final String Y = "Y";
    public static final String Z = "Z";

    public static final String SPECIFIC_SURFACE_AREA = "SPECIFIC_SURFACE_AREA";
    
    //irritation
    public static final String cReversibility = "Reversibility";
    public static final String cMaxScore = "MaxScore";

    public static final String CriticalEffectsObserved = "CriticalEffectsObserved";
    public static final String ReproductiveEffectsObserved = "ReproductiveEffectsObserved";
    public static final String RelationToOtherToxicEffects = "RelationToOtherToxicEffects";
    public static final String DevelopmentalEffectsObserved = "DevelopmentalEffectsObserved";
    public static final String TreatmentRelated = "TreatmentRelated";
    public static final String DoseResponseRelationship = "DoseResponseRelationship";
    public static final String KeyResult = "KEY_RESULT";
    public static final String Organ = "Organ";
    public static final String System = "System";

    public static final String endpoint_type_MEAN="MEAN";
    public static final String endpoint_type_PERCENT="PERCENT";
    public static final String endpoint_type_MEDIAN="MEDIAN";
    public static final String endpoint_type_MEDIAN_MASS="MEDIAN MASS";
    public static final String effect_stdev="SD";
    
    public static final String e_variability = "VARIABILITY";
    public static final String e_testgroup = "TEST_GROUP";
    public static final String e_observation = "OBSERVATION";
    public static final String e_nowithreactions = "NO_WITH_REACTIONS";
    public static final String e_hoursafterchallenge = "HOURS_AFTER_CHALLENGE";
    public static final String e_clinicalobservations = "CLINICAL_OBSERVATION";
    public static final String e_totalnoingroup = "TOTAL_NO_IN_GROUP";
    
    public static final String p_invivo = "in vivo";
    public static final String p_invitro = "in vitro";
    public static final String p_chemico = "in chemico";
    
    
}

