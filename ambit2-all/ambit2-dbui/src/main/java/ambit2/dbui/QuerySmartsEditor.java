package ambit2.dbui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.i.processors.IProcessor;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;
import org.openscience.cdk.renderer.selection.SingleSelection;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.processors.structure.AtomConfigurator;
import ambit2.core.processors.structure.HydrogenAdderProcessor;
import ambit2.db.search.BooleanCondition;
import ambit2.db.search.structure.QuerySMARTS;
import ambit2.descriptors.FuncGroupsDescriptorFactory;
import ambit2.descriptors.FunctionalGroup;
import ambit2.descriptors.VerboseDescriptorResult;
import ambit2.smarts.SmartsToChemObject;
import ambit2.ui.Panel2D;
import ambit2.ui.Utils;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.beans.BeanAdapter;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * An editor for {@link QuerySMARTS}
 * 
 * @author nina
 * 
 */
public class QuerySmartsEditor
		extends
		QueryEditor<String, FunctionalGroup, BooleanCondition, IStructureRecord, QuerySMARTS> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9201007230031542807L;
	protected SmartsToChemObject smartsToChemObject;
	protected static List<FunctionalGroup> gf;

	public QuerySmartsEditor() {
		this(SilentChemObjectBuilder.getInstance());
	}

	public QuerySmartsEditor(IChemObjectBuilder builder) {
		super();
		smartsToChemObject = new SmartsToChemObject(builder);
	}

	public JComponent buildPanel() {

		FormLayout layout = new FormLayout("125dlu,3dlu,75dlu,3dlu,125dlu",
				"pref,pref,pref,pref,pref,pref,pref,pref,pref,pref:grow");
		PanelBuilder panel = new PanelBuilder(layout);
		panel.setDefaultDialogBorder();

		final Panel2D panel2D = new Panel2D();
		panel2D.setToolTipText("Double click here to edit structure");
		panel2D.setPreferredSize(new Dimension(200, 200));
		panel2D.setMinimumSize(new Dimension(150, 150));
		panel2D.setBorder(BorderFactory.createTitledBorder("Test compound"));

		CellConstraints cc = new CellConstraints();

		FuncGroupsDescriptorFactory factory = new FuncGroupsDescriptorFactory();

		try {
			// TODO add new features and save in config file
			gf = factory.process("ambit2/descriptors/strucfeatures.xml");
		} catch (Exception x) {
			gf = new ArrayList<FunctionalGroup>();
		}
		final SelectionInList<FunctionalGroup> selectionInList = new SelectionInList<FunctionalGroup>(
				gf, presentationModel.getModel("value"));
		selectionInList.addPropertyChangeListener("value",
				new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {
						/*
						 * show some picture when smarts is selected ... but 2d
						 * coordinates are poor
						 */
						try {
							// AbstractSmartsPattern<IAtomContainer> matcher =
							// new SmartsPatternAmbit();
							// ((FunctionalGroup)
							// evt.getNewValue()).setQuery(matcher);
							// IAtomContainer ac =
							// smartsToChemObject.process(matcher.getQuery());
							if (((FunctionalGroup) evt.getNewValue())
									.getExample() != null) {
								SmilesParser p = new SmilesParser(
										SilentChemObjectBuilder.getInstance());
								IAtomContainer ac = p
										.parseSmiles(((FunctionalGroup) evt
												.getNewValue()).getExample());
								AtomConfigurator c = new AtomConfigurator();
								HydrogenAdderProcessor ha = new HydrogenAdderProcessor();
								panel2D.setAtomContainer(
										ha.process(c.process(ac)), false);
							} else
								panel2D.setAtomContainer(null);
						} catch (Exception x) {
							panel2D.setAtomContainer(null);
						}

					}
				});
		JToolBar bar = new JToolBar();
		bar.setFloatable(false);
		JButton b1 = new JButton(new AbstractAction("Add") {
			public void actionPerformed(ActionEvent e) {
				gf.add(new FunctionalGroup("New", "", "", ""));
				selectionInList.setSelectionIndex(gf.size() - 1);
			}
		});
		bar.add(b1);
		JButton b2 = new JButton(new AbstractAction("Remove") {
			public void actionPerformed(ActionEvent e) {
				try {
					int index = selectionInList.getSelectionIndex();
					gf.remove(index);
					selectionInList
							.setSelectionIndex(index > 0 ? index - 1 : 0);
				} catch (Exception x) {
				}
			}
		});
		bar.add(b2);
		JButton b3 = new JButton(new AbstractAction("Load") {
			public void actionPerformed(ActionEvent e) {
				try {
					String tmpDir = System.getProperty("java.io.tmpdir");
					FuncGroupsDescriptorFactory f = new FuncGroupsDescriptorFactory();
					List<FunctionalGroup> list = f.process(tmpDir
							+ "structfeatures.xml");
					gf.clear();
					for (FunctionalGroup fg : list)
						gf.add(fg);
					selectionInList.setSelectionIndex(0);
				} catch (Exception x) {
					x.printStackTrace();
				}
			}
		});
		bar.add(b3);
		JButton b4 = new JButton(new AbstractAction("Save") {
			public void actionPerformed(ActionEvent e) {
				try {
					String tmpDir = System.getProperty("java.io.tmpdir");
					FileOutputStream file = new FileOutputStream(tmpDir
							+ "structfeatures.xml");
					FuncGroupsDescriptorFactory.saveFragments(file, gf);
					file.close();
				} catch (Exception x) {
					x.printStackTrace();
				}
			}
		});
		bar.add(b4);
		JButton b5 = new JButton(new AbstractAction("Default") {
			public void actionPerformed(ActionEvent e) {
				try {
					FuncGroupsDescriptorFactory f = new FuncGroupsDescriptorFactory();
					List<FunctionalGroup> list = f
							.process("ambit2/descriptors/strucfeatures.xml");
					gf.clear();
					for (FunctionalGroup fg : list)
						gf.add(fg);
					selectionInList.setSelectionIndex(0);

				} catch (Exception x) {
				}
			}
		});
		bar.add(b5);
		panel.add(bar, cc.xywh(1, 1, 1, 1));
		JComboBox box = BasicComponentFactory.createComboBox(selectionInList);
		panel.add(box, cc.xywh(3, 1, 3, 1));

		String[][] config = { { "family", "Group" }, { "name", "Name" },
				{ "smarts", "SMARTS" }, { "hint", "Description" }, };
		int offset = 2;
		BeanAdapter beanAdapter = new BeanAdapter(selectionInList);
		for (int j = 0; j < config.length; j++) {
			String[] c = config[j];
			ValueModel model = beanAdapter.getValueModel(c[0]);
			panel.add(BasicComponentFactory.createLabel(new ValueHolder(c[1])),
					cc.xywh(1, j + 2, 1, 1));
			JTextField t = BasicComponentFactory.createTextField(model);
			t.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					super.mouseEntered(e);
					((JTextField) e.getSource()).setToolTipText(((JTextField) e
							.getSource()).getText());
				}
			});
			panel.add(t, cc.xywh(3, j + 2, 3, 1));
		}

		final JLabel status = new JLabel("");
		status.setBorder(BorderFactory.createTitledBorder("Test result"));
		JButton b = new JButton(new AbstractAction("Test SMARTS") {
			public void actionPerformed(java.awt.event.ActionEvent arg0) {
				try {
					if (selectionInList.getSelection() == null) {
						status.setText("No SMARTS selected!");
						status.setIcon(null);
					} else if (panel2D.getObject() == null) {
						status.setText("Empty test structure!");
						status.setIcon(null);
					} else {
						selectionInList.getSelection().setVerboseMatch(true);
						VerboseDescriptorResult result = selectionInList
								.getSelection().process(panel2D.getObject());

						if (result.getExplanation() instanceof String) {
							status.setText(result.getExplanation().toString());
							status.setIcon(null);
						} else {
							boolean notfound = result.getResult().toString()
									.equals("0");
							status.setText((notfound) ? "NOT FOUND" : "FOUND");
							status.setIcon((notfound) ? Utils
									.createImageIcon("images/cross.png")
									: Utils.createImageIcon("images/tick.png"));
							panel2D.setSelector(new IProcessor<IAtomContainer, IChemObjectSelection>() {
								public IChemObjectSelection process(
										IAtomContainer target)
										throws AmbitException {
									selectionInList.getSelection()
											.setVerboseMatch(true);
									VerboseDescriptorResult result = selectionInList
											.getSelection().process(target);
									if (result.getExplanation() instanceof IAtomContainer)
										return new SingleSelection<IAtomContainer>(
												(IAtomContainer) result
														.getExplanation());
									else
										return null;
								}
								

								public long getID() {
									return 0;
								}

								public void setEnabled(boolean value) {
								}

								public boolean isEnabled() {
									return true;
								}

								public void open() throws Exception {
								};

								public void close() throws Exception {
								};
							});
						}
					}
				} catch (Exception x) {
					status.setText(x.getMessage());
					status.setIcon(null);
				} finally {
					if (selectionInList.getSelection() != null)
						selectionInList.getSelection().setVerboseMatch(false);
				}

			};
		});
		b.setMinimumSize(new Dimension(64, 18));
		b.setToolTipText("Click here to verify SMARTS validity");

		panel.addSeparator("Test Structure (click to edit)",
				cc.xywh(3, config.length + 2, 3, 1));
		panel.add(panel2D, cc.xywh(3, config.length + 3, 3, 4));
		// panel.add(new
		// JLabel("Test structure"),cc.xywh(1,config.length+3,1,1));

		panel.add(b, cc.xywh(1, config.length + 2, 1, 1));
		panel.add(status, cc.xywh(1, config.length + 5, 1, 2));
		return panel.getPanel();
	}

	@Override
	protected JComponent createConditionComponent() {
		return null;
	}

	protected static final String t_group = "group";
	protected static final String t_name = "name";
	protected static final String t_smarts = "smarts";
	protected static final String t_hint = "hint";
	protected static final String t_family = "family";

	@Override
	protected JComponent createFieldnameComponent() {
		return null;
	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub

	}

}
