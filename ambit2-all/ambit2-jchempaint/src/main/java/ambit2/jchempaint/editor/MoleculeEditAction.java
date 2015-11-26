/*
Copyright Ideaconsult Ltd. (C) 2005-2007 

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

 */
package ambit2.jchempaint.editor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.vecmath.Vector2d;

import org.openscience.cdk.ChemModel;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.AtomContainerSet;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.jchempaint.JChemPaintPanel;

import ambit2.core.data.MoleculeTools;
import ambit2.core.helper.CDKHueckelAromaticityDetector;

/**
 * 
 * Launches JChemPaint structure diagram editor for a preset molecule
 * 
 * @author Nina Jeliazkova <b>Modified</b> 2005-10-23
 */
public class MoleculeEditAction extends AbstractMoleculeAction {
	protected IAtomContainerSet molecules;
	protected IChemModel jcpModel;
	protected StructureDiagramGenerator sdg = null;
	protected Component parentComponent = null;
	protected JChemPaintDialog jcpDialog = null;
	protected boolean modal = false;
	/**
	 * 
	 */
	private static final long serialVersionUID = 5166718649430988452L;

	public MoleculeEditAction() {
		this(null);
	}

	public MoleculeEditAction(IAtomContainer molecule) {
		super(molecule);
		setJCPModel();
	}

	public MoleculeEditAction(IAtomContainer molecule, String arg0) {
		super(molecule, arg0);
		setJCPModel();
	}

	public MoleculeEditAction(IAtomContainer molecule, String arg0, Icon arg1) {
		super(molecule, arg0, arg1);
		setJCPModel();

	}

	protected void setJCPModel() {
		jcpModel = new ChemModel();
		// jcpModel.setTitle("JChemPaint structure diagram editor");
		// jcpModel.setAuthor(JCPPropertyHandler.getInstance().getJCPProperties().getProperty("General.UserName"));
		// Package jcpPackage =
		// Package.getPackage("org.openscience.cdk.applications.jchempaint");
		// String version = jcpPackage.getImplementationVersion();
		// jcpModel.setSoftware("JChemPaint " + version);
		// jcpModel.setGendate((Calendar.getInstance()).getTime().toString());
	}

	public void actionPerformed(ActionEvent arg0) {
		if (modal) {
			if (molecules != null) {
				jcpModel.setMoleculeSet(molecules);

				// JChemPaintPanel jcpep = new JChemPaintPanel(jcpModel,
				// JChemPaint.GUI_APPLICATION, false,null);
				JChemPaintPanel jcpep = new JChemPaintPanel(jcpModel);
				Dimension d = new Dimension(500, 500);
				jcpep.setPreferredSize(d);
				// jcpep.registerModel(jcpModel);
				// jcpep.setJChemPaintModel(jcpModel,d);
				JOptionPane pane = new JOptionPane(jcpep,
						JOptionPane.QUESTION_MESSAGE,
						JOptionPane.OK_CANCEL_OPTION);
				JDialog dialog = pane
						.createDialog(parentComponent, "Edit rule");
				dialog.setBounds(200, 200, 600, 600);
				dialog.setVisible(true);
				if (pane.getValue() == null)
					return;
				int value = ((Integer) pane.getValue()).intValue();
				if (value == 0) { // ok
					molecules = jcpep.getChemModel().getMoleculeSet();
					if (molecule == null)
						molecule = new AtomContainer();
					else
						molecule.removeAllElements();
					for (int i = 0; i < molecules.getAtomContainerCount(); i++)
						molecule.add(molecules.getAtomContainer(i));

					updateMolecule(molecule);
					return;

				}
			}
		} else
			editMolecule(true, parentComponent);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 * 
	 * public void actionPerformed(ActionEvent arg0) { if (molecules != null) {
	 * jcpModel.getChemModel().setSetOfMolecules(molecules);
	 * 
	 * JChemPaintEditorPanel jcpep = new JChemPaintEditorPanel(2,new
	 * Dimension(400,400),true,"stable"); jcpep.setPreferredSize(new
	 * Dimension(500,500)); jcpep.registerModel(jcpModel);
	 * jcpep.setJChemPaintModel(jcpModel); JOptionPane pane = new
	 * JOptionPane(jcpep,
	 * JOptionPane.QUESTION_MESSAGE,JOptionPane.OK_CANCEL_OPTION); JDialog
	 * dialog = pane.createDialog(parentComponent, "Edit rule");
	 * dialog.setBounds(200,200,600,600); dialog.setVisible(true); if
	 * (pane.getValue() == null) return; int value = ((Integer)
	 * pane.getValue()).intValue(); if (value == 0) { //ok molecules =
	 * jcpep.getJChemPaintModel().getChemModel().getSetOfMolecules(); if
	 * (molecule == null) molecule = new org.openscience.cdk.Molecule(); else
	 * molecule.removeAllElements(); for (int i=0; i <
	 * molecules.getAtomContainerCount(); i++)
	 * molecule.add(molecules.getMolecule(i)); return;
	 * 
	 * } } }
	 */
	@Override
	public void setMolecule(IAtomContainer molecule) {
		super.setMolecule(molecule);
		try {
			molecules = getMoleculeForEdit(molecule);
		} catch (Exception x) {
			x.printStackTrace();
			molecules = null;
		}
	}

	protected IAtomContainerSet getMoleculeForEdit(IAtomContainer atomContainer)
			throws Exception {
		if (atomContainer == null)
			return null;
		if (atomContainer instanceof QueryAtomContainer) {
			return null;
		}

		IAtomContainerSet molecules = ConnectivityChecker
				.partitionIntoMolecules(atomContainer);

		IAtomContainerSet m = new AtomContainerSet();
		for (int i = 0; i < molecules.getAtomContainerCount(); i++) {
			IAtomContainer a = molecules.getAtomContainer(i);
			if (!GeometryTools.has2DCoordinates(a)) {
				if (sdg == null)
					sdg = new StructureDiagramGenerator();
				sdg.setMolecule((IAtomContainer) a);
				sdg.generateCoordinates(new Vector2d(0, 1));
				molecules.replaceAtomContainer(i, sdg.getMolecule());
			}
			m.addAtomContainer(molecules.getAtomContainer(i));
		}
		if (m.getAtomContainerCount() == 0) // otherwise JChemPaint crashes
			m.addAtomContainer(MoleculeTools.newMolecule(DefaultChemObjectBuilder
					.getInstance()));
		return m;
	}

	/**
	 * @return Returns the parentComponent.
	 */
	public synchronized Component getParentComponent() {
		return parentComponent;
	}

	/**
	 * @param parentComponent
	 *            The parentComponent to set.
	 */
	public synchronized void setParentComponent(Component parentComponent) {
		this.parentComponent = parentComponent;
	}

	protected JFrame getParent(Component c) {
		while ((c != null) && !(c instanceof JFrame))
			c = c.getParent();
		return (JFrame) c;
	}

	public void editMolecule(boolean editable, Component frame) {

		if (molecules != null) {
			if (jcpDialog == null) {
				jcpModel.setMoleculeSet(molecules);

				jcpDialog = new JChemPaintDialog(getParent(frame), false,
						jcpModel) {
					private static final long serialVersionUID = -492805673357520991L;

					@Override
					public IAtomContainer okAction() {
						updateMolecule(super.okAction());
						molecules = jcpep.getChemModel().getMoleculeSet();

						/*
						 * updatedMolecule.setProperties(dataContainer.getMolecule
						 * ().getProperties());
						 * getDataContainer().setEnabled(true);
						 * getDataContainer().setMolecule(updatedMolecule);
						 * getActions().allActionsEnable(true);
						 */
						dispose();
						jcpDialog = null;
						return (IAtomContainer) molecule;
					};

					@Override
					public void cancelAction() {
						super.cancelAction();

						// data.getDataContainer().setEnabled(true);
						// data.getActions().allActionsEnable(true);
						dispose();
						jcpDialog = null;

					}

				};
				jcpDialog.setTitle("JChemPaint structure diagram editor");
				jcpDialog.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent arg0) {
						super.windowClosing(arg0);
						// getDataContainer().setEnabled(true);
						// getActions().allActionsEnable(true);
						jcpDialog = null;
					}
				});
				// TODO center it
				// TODO nonmodal
			} else
				jcpModel.setMoleculeSet(molecules);

			jcpDialog.cleanup();
			jcpDialog.toFront();
			// dataContainer.setEnabled(false);
			// getActions().allActionsEnable(false);
			jcpDialog.setVisible(true);

			/*
			 * while (jcpDialog != null) { try { wait(); } catch
			 * (InterruptedException e) { e.printStackTrace(); } }
			 */
		}
	}

	protected void updateMolecule(IAtomContainer mol) {
		molecule = mol;
		try {
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
			CDKHueckelAromaticityDetector.detectAromaticity(mol);
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	public boolean isModal() {
		return modal;
	}

	public void setModal(boolean modal) {
		this.modal = modal;
	}

}
