package ambit2.jchempaint.editor;

import java.awt.Dimension;

import javax.swing.JComponent;

import org.openscience.cdk.ChemModel;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.jchempaint.JChemPaintPanel;

import ambit2.base.interfaces.IAmbitEditor;
import ambit2.core.data.MoleculeTools;

public class StructureDiagramEditor implements IAmbitEditor<IAtomContainerSet> {
	protected IChemModel jcpModel;
	protected JChemPaintPanel jcpep;

	public StructureDiagramEditor() {
		jcpModel = new ChemModel();

		IAtomContainerSet mols = MoleculeTools.newMoleculeSet(jcpModel
				.getBuilder());
		mols.addAtomContainer(MoleculeTools.newMolecule(jcpModel.getBuilder()));
		jcpModel.setMoleculeSet(mols);
		// jcpModel.setTitle("JChemPaint structure diagram editor");
		// jcpModel.setAuthor(JCPPropertyHandler.getInstance().getJCPProperties().getProperty("General.UserName"));
		Package jcpPackage = Package
				.getPackage("org.openscience.cdk.jchempaint");
		String version = jcpPackage == null ? "" : jcpPackage
				.getImplementationVersion();
		// jcpModel.setSoftware("JChemPaint " + version);
		// jcpModel.setGendate((Calendar.getInstance()).getTime().toString());
		Dimension d = new Dimension(350, 350);
		// jcpep = new JChemPaintPanel(jcpModel, JChemPaint.GUI_APPLICATION,
		// false,null);
		jcpep = new JChemPaintPanel(jcpModel);
		jcpep.setShowStatusBar(false);
		jcpep.setShowMenuBar(true);
		jcpep.setShowInsertTextField(true);
		jcpep.setPreferredSize(d);
		jcpep.setMaximumSize(new Dimension(800, 800));
		// jcpep.registerModel(jcpModel);
		// jcpep.setJChemPaintModel(jcpModel,d);
	}

	public boolean confirm() {
		return true;
	}

	public JComponent getJComponent() {
		return jcpep;
	}

	public IAtomContainerSet getObject() {
		return jcpModel.getMoleculeSet();
	}

	public boolean isEditable() {
		return true;
	}

	public void setEditable(boolean editable) {
	}

	public void setObject(IAtomContainerSet molecules) {
		if (molecules.getAtomContainerCount() == 0)
			molecules.addAtomContainer(MoleculeTools.newMolecule(jcpModel
					.getBuilder()));
		jcpModel.setMoleculeSet(molecules);

	}

}
