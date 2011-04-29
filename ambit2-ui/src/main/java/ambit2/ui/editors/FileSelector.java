package ambit2.ui.editors;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ambit2.base.config.Preferences;
import ambit2.base.interfaces.IAmbitEditor;
import ambit2.base.io.MyIOUtilities;
import ambit2.core.io.FileState;
import ambit2.core.io.IInputState;
import ambit2.ui.DelimitersPanel;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class FileSelector extends JPanel implements IAmbitEditor<FileState>, ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5574703583284875026L;
	/**
	 * 
	 */
	protected PresentationModel<FileState> detailsModel;
	protected boolean editable = true;
	protected FileState filestate;
	protected JTextField filename;
	protected JButton browse;
	public FileSelector() {
		super();
		initComponents();
		add(buildPanel());
	}
	protected void initComponents() {
	
        detailsModel = new PresentationModel<FileState>(new ValueHolder(null, true));
	    filename = BasicComponentFactory.createTextField(
			                detailsModel.getModel("filename"),true);
		filename.setEditable(false);
		filename.setPreferredSize(new Dimension(300,24));
		browse = new JButton("...");
		browse.setToolTipText("Browse");
		browse.addActionListener(this);
	}	
	  public JComponent buildPanel() {
	        initComponents();

	        FormLayout layout = new FormLayout(
	                "pref,  pref",
	                "pref");

	        PanelBuilder builder = new PanelBuilder(layout);
	        CellConstraints cc = new CellConstraints();
	        cc.insets = new Insets(3,3,3,3);
	        //builder.addSeparator("File");	        
	        builder.add(filename, cc.xy (1,1));
	        builder.add(browse, cc.xy (2,1));

	        return builder.getPanel();
	    }	
	public JComponent getJComponent() {
		return this;
	}

	public FileState getObject() {
		return filestate;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
		
	}

	public void setObject(FileState object) {
		filestate = object;
		if (detailsModel != null)
			detailsModel.setBean(object);
	}
	public void actionPerformed(ActionEvent e) {
		DelimitersPanel accessory = new DelimitersPanel();
		File file = MyIOUtilities.selectFile(JOptionPane.getFrameForComponent(this), "Select file",
				Preferences.getProperty(Preferences.DEFAULT_DIR),
				getObject().getSupportedExtensions(),
				getObject().getSupportedExtDescriptions(), 
				getObject() instanceof IInputState,
				accessory);
		
		if (file != null) {
			getObject().setFile(file,accessory.getFormat());
			try {
				if (file.getParent()!=null) {
				Preferences.setProperty(Preferences.DEFAULT_DIR,file.getParent());					
				Preferences.saveProperties(getClass().getName());
				}
			} catch (Exception x) {}
	
			filename.setText(getObject().getFilename());
			filename.setToolTipText(getObject().getFilename());
		}
		
	}
	public boolean confirm() {
		return true;
	}	
}
