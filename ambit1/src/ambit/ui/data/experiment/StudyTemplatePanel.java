package ambit.ui.data.experiment;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ambit.data.IAmbitEditor;
import ambit.data.experiment.StudyTemplate;
import ambit.exceptions.AmbitException;
import ambit.io.MyIOUtilities;

/**
 *  UI for {@link ambit.data.experiment.StudyTemplate}.
 * @author Nina Jeliazkova nina@acad.bg
 * @deprecated
 * <b>Modified</b> Sep 1, 2006
 */
public class StudyTemplatePanel extends JPanel implements IAmbitEditor, ListSelectionListener {
	JTable table;
	JTextField caption;
	String defaultDir = "data/templates";
	
	public StudyTemplatePanel(StudyTemplate study, String defaultDir) {
		super();
		if (!defaultDir.equals(""))
		    this.defaultDir = defaultDir;
		addWidgets(study);
	}
	public void addWidgets(StudyTemplate study) {
		setLayout(new BorderLayout());
		table = new JTable(new StudyTemplateTableModel(study));
		table.setSurrendersFocusOnKeystroke(true);
		table.setRowSelectionAllowed(true);
		DefaultListSelectionModel lm = new DefaultListSelectionModel();
		lm.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
		lm.addListSelectionListener(this);
		table.setSelectionModel(lm);
		
		table.setToolTipText("The values stored in these fields,if found in the input file, will be written to the database repository of experimental data. You could change the template by loading another template from .xml file");
		
		caption = new JTextField(study.getName());
		caption.setBorder(BorderFactory.createTitledBorder("Template name"));
		add(caption,BorderLayout.NORTH);
		
		JScrollPane sp = new JScrollPane(table);
		sp.setBorder(BorderFactory.createTitledBorder("Template fields"));
		add(sp,BorderLayout.CENTER);
		
		JButton button = new JButton(new AbstractAction("Load template from file") {
			
			public void actionPerformed(java.awt.event.ActionEvent arg0) {
				loadTemplate(((StudyTemplateTableModel)table.getModel()).getStudy());
			};
		});
		JComponent bar = new JToolBar();
		bar.add(button);
		add(bar,BorderLayout.SOUTH);
		
	}
	protected void setTemplate(StudyTemplate study) {
		caption.setText(study.getName());
		caption.setToolTipText(study.getSource());
		((StudyTemplateTableModel)table.getModel()).setStudy(study);
		
	}
	
	public boolean loadTemplate(StudyTemplate study) {
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
				study.clear();
				study.load(new FileInputStream(file));
				study.setSource(file.getAbsolutePath());
				setTemplate(study);
				return true;
			} catch (Exception x) {
			}
		};
		return false;

	}
	public boolean view(Component parent, boolean editable, String title) throws AmbitException {
		return
		JOptionPane.showConfirmDialog(parent,this,"",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION;

	}

	public JComponent getJComponent() {
		return this;
	}
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			ListSelectionModel lsm =   (ListSelectionModel)e.getSource();
	        if (!lsm.isSelectionEmpty()) {
	            int selectedRow = lsm.getMinSelectionIndex();
	            System.out.println(table.getModel().getValueAt(selectedRow,0));
	        }


			
		}
		
	}
	public void setEditable(boolean editable) {
		// TODO Auto-generated method stub
		
	}
	public boolean isEditable() {
		// TODO Auto-generated method stub
		return true;
	}
}


