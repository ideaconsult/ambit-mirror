package ambit2.ui.editors;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JToolBar.Separator;

import ambit2.data.AmbitList;
import ambit2.data.experiment.StudyTemplate;
import ambit2.io.MyIOUtilities;
import ambit2.ui.UITools;
import ambit2.ui.actions.AbstractActionWithTooltip;

/**
 *  UI for {@link ambit2.data.experiment.StudyTemplate}.
 * @author Nina Jeliazkova nina@acad.bg
 *
 */
public class StudyTemplateEditor extends AmbitListEditor {
	String defaultDir = "data/templates";
	JFormattedTextField templatename;
	public StudyTemplateEditor(StudyTemplate list, boolean editable, String defaultDir) {
		super(list,editable,"Template fields");
		setPreferredSize(new Dimension(toolbar.getPreferredSize().width,splitPane.getPreferredSize().height));
		this.defaultDir = defaultDir;
		
		templatename = new JFormattedTextField(list.getName());
		templatename.setBorder(BorderFactory.createTitledBorder("Template name"));
		add(templatename,BorderLayout.SOUTH);
		
		templatename.setFocusLostBehavior(JFormattedTextField.COMMIT);
		templatename.addActionListener(new ActionListener() {
     		public void actionPerformed(java.awt.event.ActionEvent e) {
     			
     			model.getList().setName(templatename.getText());
     		};
     	});
	}
	@Override
	protected String getToolbarPosition() {
		return BorderLayout.NORTH;
	}
	@Override
	public JToolBar createJToolbar() {
		JToolBar t = super.createJToolbar();
		t.add(new Separator());
		JButton button = new JButton( new AbstractActionWithTooltip("",
				UITools.createImageIcon("ambit2/ui/images/open_document_16.png"),"Load template from file") {
			public void actionPerformed(ActionEvent e) {
				loadTemplate((StudyTemplate)list);
			}
		});
		t.add(button);
		button = new JButton( new AbstractActionWithTooltip("",
				UITools.createImageIcon("ambit2/ui/images/save_16.png"),"Save template to file") {
			public void actionPerformed(ActionEvent e) {
				saveTemplate((StudyTemplate)list);
			}
		});
		t.add(button);
		t.setFloatable(true);
		t.setRollover(true);

		t.setPreferredSize(new Dimension(350,32));
		t.setMinimumSize(new Dimension(200,32));
		return t;
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
				setAmbitList(study);
				selectItem(0, study.isEditable());
				return true;
			} catch (Exception x) {
			}
		};
		return false;

	}
	public boolean saveTemplate(StudyTemplate study) {
		File file = MyIOUtilities.selectFile(this,"Template files ",
				defaultDir,
		        new String[] {".xml"},
		        new String[] {"Template files for experimental data(*.xml)"},false);
		if (file != null) {
			String s = file.getAbsolutePath();
			int p = s.lastIndexOf(File.separator);
			if (p > 0)
			    defaultDir = s.substring(0,p);
			else defaultDir = s;			
			try {
				
				study.save(new FileOutputStream(file));
				
				return true;
			} catch (Exception x) {
			}
		};
		return false;

	}	
	@Override
	public void setAmbitList(AmbitList list) {
		super.setAmbitList(list);
		if (templatename != null)
		templatename.setText(list.getName());
	}
}
