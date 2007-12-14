package ambit.processors.experiments;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.openscience.cdk.interfaces.IMolecule;

import ambit.data.IAmbitEditor;
import ambit.data.experiment.DefaultTemplate;
import ambit.data.experiment.Experiment;
import ambit.data.experiment.Study;
import ambit.data.experiment.StudyTemplate;
import ambit.data.experiment.TemplateField;
import ambit.data.literature.LiteratureEntry;
import ambit.data.literature.ReferenceFactory;
import ambit.exceptions.AmbitException;
import ambit.misc.AmbitCONSTANTS;
import ambit.processors.DefaultAmbitProcessor;
import ambit.processors.IAmbitResult;
import ambit.ui.data.experiment.StudyTemplateEditor;
import ambit.ui.data.literature.LiteratureEntryEditor;

/**
 * Examines property of the molecule according to the given template {@link ambit.data.experiment.StudyTemplate},<br>
 * removes the properties belonging to the template,<br>
 * creates {@link ambit.data.experiment.Experiment} with these properties <br>
 * and assigns the  {@link ambit.data.experiment.Experiment} object as a property {@link AmbitCONSTANTS#EXPERIMENT}.<br>
 * This is used to import experimental data by {@link ambit.database.writers.ExperimentWriter}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class ExperimentParser extends DefaultAmbitProcessor {
	protected StudyTemplate template;
	protected LiteratureEntry reference;
	protected String templateDir = "";
	protected Study defaultStudy = null;
	protected Hashtable lookup = null;
	
	/**
	 * Creates {@link ExperimentParser} processor.
	 * @param studyTemplate {@link StudyTemplate} to be used while parsing molecular properties
	 * @param reference {@link LiteratureEntry} literature reference of the experiment
	 * @param templateDir the directory where to search for templates
	 */
	public ExperimentParser(StudyTemplate studyTemplate,LiteratureEntry reference,String templateDir) {
		super();
		this.template = studyTemplate;
		this.reference = reference;
		if (reference == null) 
			this.reference = ReferenceFactory.createDatasetReference(template.getName(),template.getSource());
		this.templateDir = templateDir;
	}
	/**
	 * Launches a dialog allowing to visualize template fields and to select another template
	 * @return {@link StudyTemplate}
	 * @throws AmbitException
	 */
	public StudyTemplate selectTemplate() throws AmbitException {
		StudyTemplate template = new DefaultTemplate("Default");
		try {
			template.load(new FileInputStream("data/templates/DSSTox_LC50_template.xml"));
		} catch (Exception x) {
			logger.error(x);
		}
		StudyTemplateEditor p = new StudyTemplateEditor(template,template.isEditable(),templateDir);
		if (JOptionPane.showConfirmDialog(null,p,"Experimental data template ",JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION)
			throw new AmbitException("Template not defined!");
		else return template;
	
	}
	public Object process(Object object) throws AmbitException {
		if (object instanceof IMolecule) {
			if (template == null) {
				template = selectTemplate();
				reference = ReferenceFactory.createDatasetReference(template.getName(),template.getSource());
			}
			IMolecule mol = (IMolecule) object;
			Study study = new Study("",template);
			Experiment e = new Experiment(mol,study,reference);
			study.clearStudyConditions();
			//to be able to set default study conditions
			if (defaultStudy != null)
				study.setStudyConditions(defaultStudy.getStudyConditions());

			if (lookup != null) {
				Enumeration ee = lookup.keys();
				while (ee.hasMoreElements()) {
					Object key = ee.nextElement();
					Object p = mol.getProperty(key);
					if (p==null) continue; // no such property
					
					TemplateField field = template.getField(lookup.get(key));
					
					
					if (field != null) {
						mol.removeProperty(key);
						try {
							
							if (field.isNumeric()) {
								try {
									p = new Double(p.toString());
								} catch (Exception x) {
									
								}
							}
							if (field.isResult()) {
								e.setResult(field, p);
							} else study.setStudyCondition(field, p);
						} catch (AmbitException x) {
							System.err.println(x.getMessage());
						}
					}	
				}
			} else
			for (int i=0; i < template.size();i++) {
				TemplateField field = template.getField(i);
				Object p = mol.getProperty(field.getName());
				
				if (p != null) {
					mol.removeProperty(field.getName());
					try {
						
						if (field.isNumeric()) {
							try {
								p = new Double(p.toString());
							} catch (Exception x) {
								
							}
						}
						if (field.isResult()) {
							e.setResult(field, p);
						} else study.setStudyCondition(field, p);
					} catch (AmbitException x) {
						System.err.println(x.getMessage());
					}
				}	
			}

			mol.setProperty(AmbitCONSTANTS.EXPERIMENT, e);
			return mol;
		} else return null;
	}

	public IAmbitResult createResult() {
		// TODO Auto-generated method stub
		return null;
	}

	public IAmbitResult getResult() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setResult(IAmbitResult result) {
		// TODO Auto-generated method stub

	}

	public void close() {
		// TODO Auto-generated method stub

	}
	   public IAmbitEditor getEditor() {
		   return new ExperimentParserEditor(this);
	    }
	public String toString() {
		return "Extracts fields according to template "+template;
	}

	public LiteratureEntry getReference() {
		return reference;
	}

	public void setReference(LiteratureEntry reference) {
		this.reference = reference;
	}

	/**
	 * {@link StudyTemplate}
	 * @return the template to be used when parsing  
	 */
    public synchronized StudyTemplate getTemplate() {
        return template;
    }
    /**
     * Sets the template to be used when parsing. 
     * @param template  {@link StudyTemplate}
     */
    public synchronized void setTemplate(StudyTemplate template) {
        this.template = template;
    }
    /**
     * The directory where to find XML files of templates {@link StudyTemplate}
     * @return the directory
     */
	public String getTemplateDir() {
		return templateDir;
	}
	/**
	 * Sets the directory where to find XML files of templates {@link StudyTemplate}
	 * @param templateDir
	 */
	public void setTemplateDir(String templateDir) {
		this.templateDir = templateDir;
	}
	/**
	 * The default {@link Study} is used to provide default study conditions for an experiment, if they are not found as molecule properties.
	 * @return default study
	 */
	public Study getDefaultStudy() {
		return defaultStudy;
	}
	/**
	 * The default {@link Study} is used to provide default study conditions for an experiment, if they are not found as molecule properties.
	 * @param defaultStudy
	 */
	public void setDefaultStudy(Study defaultStudy) {
		this.defaultStudy = defaultStudy;
	}
	/**
	 * The lookup {@link Hashtable} provides the correspondence between property names and template fields. 
	 * If null, then the processor looks for properties with the same names as the template fields.  
	 * @return lookup
	 */
	public Hashtable getLookup() {
		return lookup;
	}
	/**
	 * The lookup {@link Hashtable} provides the correspondence between property names and template fields. 
	 * If null, then the processor looks for properties with the same names as the template fields. 
	 * @param lookup
	 */
	public void setLookup(Hashtable lookup) {
		this.lookup = lookup;
	}
}

/**
 * 
 * Visualization of {@link ExperimentParser}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
class ExperimentParserEditor extends JPanel implements IAmbitEditor {
    /**
     * Creates {@link JPanel} embedding {@link LiteratureEntryEditor} and {@link StudyTemplatePanel}
     * @param parser
     */
	public ExperimentParserEditor(ExperimentParser parser) {
		super(new BorderLayout());
		LiteratureEntryEditor le = new LiteratureEntryEditor(parser.getReference());
		le.setBorder(BorderFactory.createTitledBorder("Assign this reference to imported experimental data"));
		add(le,BorderLayout.NORTH);
		StudyTemplateEditor p = new StudyTemplateEditor(parser.getTemplate(),parser.getTemplate().isEditable(),"Template");
		p.setPreferredSize(new Dimension(400,200));
		p.setBorder(BorderFactory.createTitledBorder("Experimental data template"));
		add(p,BorderLayout.CENTER);
	}
	public JComponent getJComponent() {
		return this;
	}
	public boolean view(Component parent, boolean editable, String title) throws AmbitException {
		return 
		JOptionPane.showConfirmDialog(parent, getJComponent(),"Experimental data",
				JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION;
		
	}
	public void setEditable(boolean editable) {
		// TODO Auto-generated method stub
		
	}
	public boolean isEditable() {
		// TODO Auto-generated method stub
		return true;
	}
}
