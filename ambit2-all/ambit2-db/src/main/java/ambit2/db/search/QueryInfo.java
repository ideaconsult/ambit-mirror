package ambit2.db.search;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.data.SourceDataset;

/**
 * TODO add a working test
 * 
 * @author Nina Jeliazkova nina@acad.bg
 * 
 */
public class QueryInfo {

    public static String COMBINE_ALL = "AND";
    public static String COMBINE_ANY = "OR";

    public static String SCOPE_QUERY = "Last query";
    public static String SCOPE_DATASET = "Dataset";
    public static String SCOPE_DATABASE = "Entire database";

    public static String SEARCH_FORMULA = "Formula";
    public static String SEARCH_SMILES = "SMILES";
    public static String SEARCH_INCHI = "INChI";

    public static String METHOD_EXACT = "Exact structure";
    public static String METHOD_SUBSTRUCTURE = "Substructure";
    public static String METHOD_SIMILARITY = "Similarity";
    public static String METHOD_SMARTS = "SMARTS";

    public static String PROPERTY_THRESHOLD = "threshold";
    public static String PROPERTY_SCOPE = "scope";
    public static String PROPERTY_METHOD = "method";
    public static String PROPERTY_DATASET = "dataset";
    public static final String PROPERTYNAME_DATASET_ENABLED = "datasetEnabled";

    public static String PROPERTY_COMBINE = "combine";
    public static String PROPERTY_FIELDNAME1 = "fieldname1";
    public static String PROPERTY_FIELDNAME2 = "fieldname2";
    public static String PROPERTY_FIELDNAME3 = "fieldname3";

    public static String PROPERTY_COND1 = "condition1";
    public static String PROPERTY_COND2 = "condition2";
    public static String PROPERTY_COND3 = "condition3";

    // public static String PROPERTY_DATASETS = "datasets";

    public static String[] combine_options = { COMBINE_ALL, COMBINE_ANY };
    protected String combine = COMBINE_ANY;

    protected String identifier1;
    protected String identifier2;
    protected String identifier3;

    protected String condition1 = StringCondition.C_EQ;
    protected String condition2 = StringCondition.C_EQ;
    protected String condition3 = StringCondition.C_EQ;

    protected String formula;
    protected String smiles;
    protected String inchi;

    protected IAtomContainer molecule;
    protected Double threshold = 0.5;
    public static Double[] threshold_options = { 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0 };

    public static String[] method_options = { METHOD_EXACT, METHOD_SUBSTRUCTURE, METHOD_SIMILARITY, METHOD_SMARTS };
    protected String method = METHOD_EXACT;

    public static String[] scope_options = { SCOPE_QUERY, SCOPE_DATASET, SCOPE_DATABASE };
    protected String scope = SCOPE_QUERY;
    protected IStoredQuery storedQuery;

    public static String[] structure_search_options = { SEARCH_FORMULA, SEARCH_SMILES, SEARCH_INCHI };

    protected static SourceDataset[] datasets = new SourceDataset[0];
    protected SourceDataset dataset;

    protected static String[] fieldnames = new String[0];
    protected String fieldname1;
    protected String fieldname2;
    protected String fieldname3;

    public String getFormula() {
	return formula;
    }

    public void setFormula(String formula) {
	this.formula = formula;
    }

    public String getIdentifier1() {
	return identifier1;
    }

    public void setIdentifier1(String identifier1) {
	this.identifier1 = identifier1;
    }

    public String getIdentifier2() {
	return identifier2;
    }

    public void setIdentifier2(String identifier2) {
	this.identifier2 = identifier2;
    }

    public String getIdentifier3() {
	return identifier3;
    }

    public void setIdentifier3(String identifier3) {
	this.identifier3 = identifier3;
    }

    public String getInchi() {
	return inchi;
    }

    public void setInchi(String inchi) {
	this.inchi = inchi;
    }

    public IAtomContainer getMolecule() {
	return molecule;
    }

    public void setMolecule(IAtomContainer molecule) {
	this.molecule = molecule;
    }

    public String getSmiles() {
	return smiles;
    }

    public void setSmiles(String smiles) {
	this.smiles = smiles;
    }

    public String getScope() {
	return scope;
    }

    public void setScope(String scope) {
	this.scope = scope;
    }

    public static SourceDataset[] getDatasets() {
	return datasets;
    }

    public void setDataset(SourceDataset dataset) {
	this.dataset = dataset;
	if (dataset == null)
	    setScope(SCOPE_DATABASE);
	else
	    setScope(SCOPE_DATASET);
    }

    public SourceDataset getDataset() {
	return dataset;
    }

    public String getFieldname1() {
	return fieldname1;
    }

    public void setFieldname1(String fieldname1) {
	this.fieldname1 = fieldname1;
    }

    public String getFieldname2() {
	return fieldname2;
    }

    public void setFieldname2(String fieldname2) {
	this.fieldname2 = fieldname2;
    }

    public String getFieldname3() {
	return fieldname3;
    }

    public void setFieldname3(String fieldname3) {
	this.fieldname3 = fieldname3;
    }

    public static void setFieldnames(String[] fieldnames) {
	QueryInfo.fieldnames = fieldnames;
    }

    public static String[] getFieldnames() {
	return fieldnames;
    }

    public String getMethod() {
	return method;
    }

    public void setMethod(String method) {
	this.method = method;
    }

    public String getCondition1() {
	return condition1;
    }

    public void setCondition1(String condition1) {
	this.condition1 = condition1;
    }

    public String getCondition2() {
	return condition2;
    }

    public void setCondition2(String condition2) {
	this.condition2 = condition2;
    }

    public String getCondition3() {
	return condition3;
    }

    public void setCondition3(String condition3) {
	this.condition3 = condition3;
    }

    public String getCombine() {
	return combine;
    }

    public void setCombine(String combine) {
	this.combine = combine;
    }

    public double getThreshold() {
	return threshold;
    }

    public void setThreshold(Double threshold) {
	this.threshold = threshold;
    }

    public IStoredQuery getStoredQuery() {
	return storedQuery;
    }

    public void setStoredQuery(IStoredQuery storedQuery) {
	this.storedQuery = storedQuery;
	if (storedQuery == null)
	    setScope(SCOPE_DATABASE);
	else
	    setScope(SCOPE_QUERY);
    }

    public static void setDatasets(SourceDataset[] datasets) {
	QueryInfo.datasets = datasets;
    }

}
