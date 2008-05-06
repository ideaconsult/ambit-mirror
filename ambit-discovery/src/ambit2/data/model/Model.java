/**
 * Created on 2005-3-21
 *
 */
package ambit2.data.model;

import ambit2.ui.editors.IAmbitEditor;
import ambit2.ui.editors.ModelEditor;
import ambit2.data.AmbitObject;
import ambit2.data.descriptors.Descriptor;
import ambit2.data.descriptors.DescriptorDefinition;
import ambit2.data.descriptors.DescriptorsList;
import ambit2.data.experiment.DefaultTemplate;
import ambit2.data.experiment.Study;
import ambit2.data.experiment.TemplateField;
import ambit2.data.literature.LiteratureEntry;


/**
 * A class to encapsulate information about QSAR model.  This is mainly to be used for database storage and retrieval. 
 * Doesn't have functionality to derive a model! (yet)  
 * TODO make use of {@link org.openscience.cdk.qsar.model.IModel} 
 * @author Nina Jeliazkova <br>
 * @deprecated use QMRFObject
 * <b>Modified</b> 2005-4-7
 */
public class Model extends AmbitObject {
    protected ModelStatistics stats;
	protected ModelType modelType = null;
	protected String note = ""; 
	protected String keywords = "";
	protected String equation = "";
	protected LiteratureEntry reference = null;
	protected int N_Points = 0;
	protected DescriptorsList descriptors = null;
	protected String fileWithData = ""; //empty if database
	protected Study study;
	protected TemplateField experimentField;

	/**
	 * Creates a model
	 * @param name  The model name
	 * @param mtype the model type {@link ModelType}
	 */
	public Model(String name, ModelType mtype) {
		super(name);
		this.modelType = mtype;
		stats = new ModelStatistics();
		study = new Study("Default",new DefaultTemplate("Default"));
		experimentField = null;
	}
	/**
	 * Creates model with default name "Model" and default {@link ModelType} 
	 *
	 */
	public Model() {
		this("Model",new ModelType());
		
	}
	public void clear() {
		super.clear();
		modelType.clear();
		note = ""; 
		keywords = "";
		equation = "";
		if (reference != null) reference.clear();
		N_Points = 0;
		if (descriptors != null) descriptors.clear();
		fileWithData = "";
	}
	public String getFileWithData() {
		return fileWithData;
	}
	public void setFileWithData(String datafile) {
		boolean m = isModified() & !this.fileWithData.equals(datafile);
		this.fileWithData = datafile;
		setModified(m);
	}
	/**
	 * @return descriptors {@link DescriptorsList}
	 */
	public DescriptorsList getDescriptors() {
		return descriptors;
	}
	/**
	 * Sets descriptors list
	 * @param descriptors {@link DescriptorsList}
	 */
	public void setDescriptors(DescriptorsList descriptors) {
		boolean m = true;
		if (this.descriptors != null)
			m = isModified() & !this.descriptors.equals(descriptors);		
		this.descriptors = descriptors;
		setModified(m);
	}
	public String getEquation() {
		return equation;
	}
	public void setEquation(String equation) {
		boolean  m = isModified() & !this.equation.equals(equation);
		this.equation = equation;
		setModified(m);
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		boolean m = isModified() & !this.keywords.equals(keywords);		
		this.keywords = keywords;
		setModified(m);
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		boolean m = isModified() & !this.note.equals(note);	
		this.note = note;
		setModified(m);
	}
	public int getN_Points() {
		return N_Points;
	}
	public void setN_Points(int npoints) {
		boolean m = isModified() & (npoints != N_Points);
		N_Points = npoints;
		setModified(m);
	}
	
	public LiteratureEntry getReference() {
		return reference;
	}
	public void setReference(LiteratureEntry reference) {
		boolean m = true; 
		if (this.reference != null)
			m = !this.reference.equals(reference);		
		this.reference = reference;
		setModified(m);
	}
	/**
	 * adds a descriptor
	 * @param d {@link Descriptor}
	 */
	public void addDescriptor(Descriptor d) {
		if (descriptors == null) descriptors = new DescriptorsList();
		descriptors.addItem(d);
		setModified(true);
	}
	public DescriptorDefinition getDescriptor(int number) {
		if (descriptors == null) return null;
		return descriptors.getDescriptor(number);
	}
	public int getN_Descriptors() {
		if (descriptors == null) return 0;
		else return descriptors.size();
	}
	/**
	 * Sets number of descriptors. Doesn't do anything! To change number of descriptors use {@link #addDescriptor(Descriptor)} or {@link #setDescriptors(DescriptorsList)}
	 * @param n
	 */
	public void setN_Descriptors(int n) {
	}	
	public void clearDescriptors() {
		if (descriptors != null) descriptors.clear();
	}
	public ModelType getModelType() {
		return modelType;
	}
	public void setModelType(ModelType modelType) {
		boolean m = !this.modelType.equals(modelType);
		this.modelType = modelType;
		setModified(m);
	}
	public boolean isEmpty() {
		return (reference == null)  || (descriptors == null);  
	}
	/**
	 * Verifies if literature reference and descriptors are empty.
	 * @return string
	 */
	public String getStatus() {
		StringBuffer buf = new StringBuffer();
		if (reference == null) buf.append("Literature reference not defined\n");
		if (descriptors == null) buf.append("Descriptors not defined \n");
		return buf.toString();
	}
	public boolean hasID() {
		try {
		return super.hasID() && modelType.hasID() &&  descriptors.hasID();
		} catch (Exception x) {
			return false;
		}
	}
	public boolean equals(Object obj) {
		Model m = (Model) obj;
		return super.equals(m) && 
			(N_Points == m.N_Points) &&
			stats.equals(m.getStats()) &&
			(N_Points == m.N_Points) &&
			fileWithData.equals(m.fileWithData) &&
			note.equals(m.note) &&
			keywords.equals(m.keywords) &&
			equation.equals(m.equation) &&
			reference.equals(m.reference) &&
			descriptors.equals(m.descriptors)
			
			;
	}
	
	public Object clone()  throws CloneNotSupportedException {
		if ((reference == null)  || (descriptors == null))
			throw new CloneNotSupportedException("Model has undefined fields!");
		Model m = new Model(name,(ModelType) modelType.clone());
		m.fileWithData = fileWithData; 
		m.note = note;
		m.keywords = keywords;
		m.equation = equation;
		m.N_Points = N_Points;
		m.setStats((ModelStatistics)stats.clone());
		
		m.reference = (LiteratureEntry) reference.clone();
		m.descriptors = (DescriptorsList) descriptors.clone();
		return m;
	}
	public String toString() {
		char d = ',', t = '"';
		StringBuffer b = new StringBuffer();
		b.append("Model");
		b.append(d);		
		b.append(t);b.append(name);b.append(t);		
		b.append(d);
		b.append(t);b.append(modelType.getName());b.append(t);
		b.append(d);
		b.append(t);b.append(equation);b.append(t);
		return b.toString();
	}

	
	

    public synchronized ModelStatistics getStats() {
        return stats;
    }
    public synchronized void setStats(ModelStatistics stats) {
        this.stats = stats;
    }
    /* (non-Javadoc)
     * @see ambit2.data.AmbitObject#editor()
     */
    public IAmbitEditor editor() {
        return new ModelEditor(this);
    }

	public Study getStudy() {
		return study;
	}
	public void setStudy(Study study) {
		this.study = study;
	}
	public TemplateField getExperimentField() {
		return experimentField;
	}
	public void setExperimentField(TemplateField experimentField) {
		this.experimentField = experimentField;
	}
    
}