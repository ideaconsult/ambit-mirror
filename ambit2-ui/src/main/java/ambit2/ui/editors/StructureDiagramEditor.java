package ambit2.ui.editors;

import java.awt.Dimension;
import java.util.Calendar;

import javax.swing.JComponent;

import org.openscience.cdk.applications.jchempaint.JCPPropertyHandler;
import org.openscience.cdk.applications.jchempaint.JChemPaintEditorPanel;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.interfaces.IMoleculeSet;

public class StructureDiagramEditor implements IAmbitEditor<IMoleculeSet> {
	protected JChemPaintModel jcpModel; 
	protected JChemPaintEditorPanel jcpep;
	public StructureDiagramEditor() {
		jcpModel = new JChemPaintModel();

		jcpModel.setTitle("JChemPaint structure diagram editor");
		jcpModel.setAuthor(JCPPropertyHandler.getInstance().getJCPProperties().getProperty("General.UserName"));
		Package jcpPackage = Package.getPackage("org.openscience.cdk.applications.jchempaint");
		String version = jcpPackage.getImplementationVersion();
		jcpModel.setSoftware("JChemPaint " + version);
		jcpModel.setGendate((Calendar.getInstance()).getTime().toString());		
		Dimension d = new Dimension(350,350);
		jcpep = new JChemPaintEditorPanel(2,d,true,"stable");
		jcpep.setShowStatusBar(false);
		jcpep.setShowMenuBar(true);
		jcpep.setShowInsertTextField(true);
		jcpep.setPreferredSize(d);
		jcpep.setMaximumSize(new Dimension(800,800));
		jcpep.registerModel(jcpModel);
		jcpep.setJChemPaintModel(jcpModel,d);		
	}
	public boolean confirm() {
		return true;
	}

	public JComponent getJComponent() {
		return jcpep;
	}

	public IMoleculeSet getObject() {
		return jcpModel.getChemModel().getMoleculeSet();
	}

	public boolean isEditable() {
		return true;
	}

	public void setEditable(boolean editable) {
	}

	public void setObject(IMoleculeSet object) {
		jcpModel.getChemModel().setMoleculeSet(object);
		
	}

}
