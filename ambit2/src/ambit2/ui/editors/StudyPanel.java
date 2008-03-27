package ambit2.ui.editors;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;

import ambit2.data.AmbitObjectChanged;
import ambit2.data.IAmbitObjectListener;
import ambit2.data.experiment.Study;
import ambit2.data.experiment.StudyTemplate;
import ambit2.exceptions.AmbitException;
import ambit2.io.MyIOUtilities;
import ambit2.ui.UITools;
import ambit2.ui.data.HashtableModel;

/**
 * UI for {@link ambit2.data.experiment.Study}.
 * @author Nina Jeliazkova nina@acad.bg
 * @deprecated
 * <b>Modified</b> Sep 1, 2006
 */
public class StudyPanel extends JPanel implements IAmbitEditor, IAmbitObjectListener, PropertyChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2087428127611259406L;
	protected Study study;
	protected HashtableModel studyConditionsModel;
	protected HashtableModel studyResultsModel;
	protected JFormattedTextField nameField;
	String defaultDir = "data/templates";
	protected TitledBorder templateBorder;
	
	public StudyPanel(Study study) {
		super();
		this.study = study;
		study.addAmbitObjectListener(this);
		studyConditionsModel = new HashtableModel(study.getStudyConditions()) {
			public int getColumnCount() {
				return 2;
			}
			public String getColumnName(int arg0) {
				switch (arg0) {
				case 0: return "Field";
				case 1: return "Value";
				default: return "";
				}
			}
		};
		
		studyResultsModel = new HashtableModel(study.getStudyResults()) {
			public int getColumnCount() {
				return 1;
			}
			public String getColumnName(int arg0) {
				return "Field";
			}
		};		
		JTable table = new JTable(studyConditionsModel);
        table.getTableHeader().setReorderingAllowed(false);
		table.setSurrendersFocusOnKeystroke(true);
		JScrollPane p = new JScrollPane(table);
		p.setPreferredSize(new Dimension(100,200));

		JTable table1 = new JTable(studyResultsModel);
        table1.getTableHeader().setReorderingAllowed(false);
		table1.setSurrendersFocusOnKeystroke(true);
		JScrollPane p1 = new JScrollPane(table1);
		p1.setPreferredSize(new Dimension(100,200));
		
		nameField = new JFormattedTextField(study.getName());
		//nameField.setPreferredSize(new Dimension(100,));
		nameField.setBorder(BorderFactory.createTitledBorder("Study name"));
		nameField.addPropertyChangeListener("value",this);
		setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		add(nameField);
		
		
		p.setBorder(BorderFactory.createTitledBorder("Study conditions"));
		add(p);
		p1.setBorder(BorderFactory.createTitledBorder("Study results"));
		add(p1);
		
		JPanel buttons = new JPanel();
		templateBorder = BorderFactory.createTitledBorder("Template "+study.getTemplate());
		buttons.setBorder(templateBorder);
        JButton b = new JButton(new AbstractAction("View Template "
        		,UITools.createImageIcon("ambit2/ui/images/template.png")){
	        /* (non-Javadoc)
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed(ActionEvent e) {
                StudyTemplate t = showTemplate();

            }
	    });
        b.setToolTipText("Experimental data recognition depends on a template. Click here to view the current template or load another one.");
        buttons.add(b);
        b = new JButton(new AbstractAction("Load new Template "
        		,UITools.createImageIcon("ambit2/ui/images/template.png")){
	        /* (non-Javadoc)
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed(ActionEvent e) {
                StudyTemplate t = loadTemplate();
                //putValue(AbstractAction.NAME, "Template \""+t+'"');
            }
	    });
        b.setToolTipText("Experimental data recognition depends on a template. Click here to view the current template or load another one.");
        
        buttons.add(b);
        add(buttons);
		
	}
	
	protected StudyTemplate showTemplate() {
		StudyTemplatePanel p = new StudyTemplatePanel(study.getTemplate(),"");
		JOptionPane.showMessageDialog(this,p,"Template",JOptionPane.PLAIN_MESSAGE);
		study.setTemplate(study.getTemplate()); //updates conditions
		templateBorder.setTitle(study.getTemplate().toString());
		return study.getTemplate();
	}
	public StudyTemplate loadTemplate() {
		StudyTemplate studyTemplate = study.getTemplate();
		File file = MyIOUtilities.selectFile(this,"Template files ",
				defaultDir,
		        new String[] {".xml"},new String[] {"Template files for experimental data(*.xml)"},true);
		if (file != null) {
			String s = file.getAbsolutePath();
			int p = s.lastIndexOf(File.separator);
			if (p > 0)
			    defaultDir = s.substring(0,p);
			else defaultDir = s;			
			try {
				studyTemplate.clear();
				studyTemplate.load(new FileInputStream(file));
				studyTemplate.setSource(file.getAbsolutePath());
				study.setTemplate(studyTemplate);
				templateBorder.setTitle(study.getTemplate().toString());
				return studyTemplate;
			} catch (Exception x) {
			}
		};
		return studyTemplate;

	}

	public boolean view(Component parent, boolean editable, String title) throws AmbitException {
		return
		JOptionPane.showConfirmDialog(parent,this,"",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION;

	}

	public JComponent getJComponent() {
		return this;
	}
	/* (non-Javadoc)
     * @see ambit2.data.IAmbitObjectListener#ambitObjectChanged(ambit2.data.AmbitObjectChanged)
     */
    public void ambitObjectChanged(AmbitObjectChanged event) {
        if (event.getObject() instanceof Study) {
            study.removeAmbitObjectListener(this);
            this.study = (Study) event.getObject();
            study.addAmbitObjectListener(this);
	        studyConditionsModel.setTable(study.getStudyConditions());
	        studyResultsModel.setTable(study.getStudyResults());
        }

    }
    public void propertyChange(PropertyChangeEvent evt) {
    	
    	study.setName(evt.getNewValue().toString());
//    	System.out.println(study.getName());
    	
    }
    public void setEditable(boolean editable) {
    	// TODO Auto-generated method stub
    	
    }

	public boolean isEditable() {
		// TODO Auto-generated method stub
		return true;
	}
}
