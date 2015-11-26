package ambit2.rendering;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import junit.framework.Assert;
import net.idea.modbcum.i.exceptions.AmbitException;

import org.junit.Test;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.graph.matrix.AdjacencyMatrix;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.AtomContainerRenderer;
import org.openscience.cdk.renderer.IRenderer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.generators.AtomNumberGenerator;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.BasicBondGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.generators.IGeneratorParameter;
import org.openscience.cdk.renderer.generators.SelectAtomGenerator;
import org.openscience.cdk.renderer.generators.standard.StandardGenerator;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;
import org.openscience.cdk.renderer.selection.SingleSelection;
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.core.data.MoleculeTools;
import ambit2.core.smiles.SmilesParserWrapper;
import ambit2.core.smiles.SmilesParserWrapper.SMILES_PARSER;

/**
 * 
 * @author nina
 * 
 *         <pre>
 *  parameter: BasicSceneGenerator$BackgroundColor -> java.awt.Color[r=255,g=255,b=255]
 * parameter: BasicSceneGenerator$ForegroundColor -> java.awt.Color[r=0,g=0,b=0]
 * parameter: BasicSceneGenerator$Margin -> 10.0
 * parameter: BasicSceneGenerator$UseAntiAliasing -> true
 * parameter: BasicSceneGenerator$UsedFontStyle -> NORMAL
 * parameter: BasicSceneGenerator$FontName -> Arial
 * parameter: BasicSceneGenerator$ZoomFactor -> 1.0
 * parameter: BasicSceneGenerator$Scale -> 1.0
 * parameter: BasicSceneGenerator$FitToScreen -> false
 * parameter: BasicSceneGenerator$ShowMoleculeTitle -> false
 * parameter: BasicSceneGenerator$ShowTooltip -> false
 * parameter: BasicBondGenerator$BondWidth -> 1.0
 * parameter: BasicBondGenerator$DefaultBondColor -> java.awt.Color[r=0,g=0,b=0]
 * parameter: BasicBondGenerator$BondLength -> 40.0
 * parameter: BasicBondGenerator$WedgeWidth -> 2.0
 * parameter: BasicBondGenerator$BondDistance -> 2.0
 * parameter: BasicBondGenerator$TowardsRingCenterProportion -> 0.15
 * parameter: BasicAtomGenerator$AtomColor -> java.awt.Color[r=0,g=0,b=0]
 * parameter: BasicAtomGenerator$AtomColorer -> org.openscience.cdk.renderer.color.CDK2DAtomColors@192d342
 * parameter: BasicAtomGenerator$AtomRadius -> 8.0
 * parameter: BasicAtomGenerator$ColorByType -> true
 * parameter: BasicAtomGenerator$CompactShape -> SQUARE
 * parameter: BasicAtomGenerator$CompactAtom -> false
 * parameter: BasicAtomGenerator$KekuleStructure -> false
 * parameter: BasicAtomGenerator$ShowEndCarbons -> false
 * parameter: BasicAtomGenerator$ShowExplicitHydrogens -> true
 * </pre>
 */
public class RendererTest {

	@Test
	public void testImage() throws Exception {

		CompoundImageTools t = new CompoundImageTools();
		// t.setBackground(Color.GRAY);
		final IAtomContainer mol = MoleculeFactory.makePhenylAmine();

		StructureDiagramGenerator g = new StructureDiagramGenerator(mol);
		g.generateCoordinates();


		IAtomContainerHighlights p = new IAtomContainerHighlights() {

					@Override
					public void setEnabled(boolean value) {

					}

					@Override
					public void open() throws Exception {
					}

					@Override
					public void close() throws Exception {
					}

					@Override
					public IChemObjectSelection process(IAtomContainer mol)
							throws AmbitException {
						IAtom anAtom = null;
						for (IAtom atom : mol.atoms()) {
							if (atom.getSymbol().equals("N")) {
								anAtom = atom;
							}
						}						
						final IAtomContainer selectedMol = MoleculeTools
								.newMolecule(SilentChemObjectBuilder.getInstance());
						selectedMol.addAtom(anAtom);
						selectedMol.addBond(mol.getBond(0));
						selectedMol.addAtom(mol.getBond(0).getAtom(0));
						selectedMol.addAtom(mol.getBond(0).getAtom(1));						
						return new SingleSelection<IChemObject>(selectedMol);
					}

					@Override
					public boolean isEnabled() {
						return true;
					}

					@Override
					public long getID() {
						return 0;
					}
				};
		BufferedImage img = t.getImage(mol,p,true, false);

		File file = new File("test.png");
		if (file.exists())
			file.delete();
		ImageIO.write(img, "png", file);
		Assert.assertTrue(file.exists());
		System.out.println(file.getAbsolutePath());

	}

	@Test
	public void testNoWeirdGrayLines() throws Exception {
		testsdf2image("ambit2/rendering/test-ok.sdf", "test-ok.png");
	}

	@Test
	public void testWeirdGrayLines() throws Exception {
		testsdf2image("ambit2/rendering/test-gray.sdf", "test-gray.png");
	}

	public void testsdf2image(String filename, String imgfile) throws Exception {

		InputStream in = RendererTest.class.getClassLoader()
				.getResourceAsStream(filename);

		Assert.assertNotNull(in);
		IteratingSDFReader reader = new IteratingSDFReader(in,
				SilentChemObjectBuilder.getInstance());

		while (reader.hasNext()) {
			IChemObject mol = reader.next();
			Assert.assertTrue(mol instanceof IAtomContainer);
			AtomContainerManipulator
					.percieveAtomTypesAndConfigureAtoms((IAtomContainer) mol);
			/**
			 * if generating 2D coordinates via StructureDiagramGenerator, the
			 * problem goes away, but it is still valid use case to generate a
			 * structure diagram from coordinates retrieved from a file
			 */
			// StructureDiagramGenerator sdg = new
			// StructureDiagramGenerator((IAtomContainer)mol);
			// sdg.generateCoordinates(new Vector2d(0,1));

			BufferedImage img = paint((IAtomContainer) mol);
			File file = new File(imgfile);
			// file.deleteOnExit();
			if (file.exists())
				file.delete();
			ImageIO.write(img, "png", file);
			Assert.assertTrue(file.exists());
			System.out.println(file.getAbsolutePath());
		}
		in.close();
	}

	protected BufferedImage paint(IAtomContainer mol) throws Exception {
		Rectangle drawArea = new Rectangle(0, 0, 200, 200);
		BufferedImage img = new BufferedImage(drawArea.width, drawArea.height,
				BufferedImage.TYPE_INT_ARGB);
		int alpha = 128;
		final Graphics2D g = img.createGraphics();
		AlphaComposite ac = AlphaComposite
				.getInstance(AlphaComposite.SRC, 1.0f);
		g.setComposite(ac);
		g.setColor(new Color(255, 255, 255, 255));
		g.fillRect(0, 0, drawArea.width, drawArea.height);

		List<IGenerator<IAtomContainer>> generators = new ArrayList<IGenerator<IAtomContainer>>();
		generators.add(new BasicSceneGenerator() {
			@Override
			public IRenderingElement generate(IAtomContainer ac,
					RendererModel model) {
				// TODO Auto-generated method stub
				return super.generate(ac, model);
			}
		});

		generators.add(new BasicBondGenerator() {
			@Override
			public Color getColorForBond(IBond bond, RendererModel model) {
				Color clr = super.getColorForBond(bond, model);
				return new Color(0, 0, 0, 255);
			}
		});
		// generators.add(new BasicAtomGenerator());

		IRenderer renderer = new AtomContainerRenderer(generators,
				new AWTFontManager());
		final RendererModel rendererModel = renderer.getRenderer2DModel();
		rendererModel.set(BasicBondGenerator.DefaultBondColor.class, new Color(
				255, 0, 0, alpha));
		rendererModel.set(BasicSceneGenerator.BackgroundColor.class, new Color(
				0, 0, 255, alpha));

		renderer.setup(mol, drawArea);
		renderer.paint(mol, new AWTDrawVisitor(g));

		return img;
	}

	public void testListParams() {
		// generators make the image elements
		List generators = new ArrayList();
		generators.add(new BasicSceneGenerator());
		generators.add(new BasicBondGenerator());
		generators.add(new BasicAtomGenerator());
		generators.add(new AtomNumberGenerator());
		generators.add(new SelectAtomGenerator());

		// the renderer needs to have a toolkit-specific font manager
		IRenderer renderer = new AtomContainerRenderer(generators,
				new AWTFontManager());

		// dump all parameters
		for (Object generator : renderer.getGenerators()) {
			IGenerator g = (IGenerator) generator;
			for (Object parameter : g.getParameters()) {
				System.out.println("parameter: "
						+ parameter.getClass().getName().substring(40) + " -> "
						+ ((IGeneratorParameter) parameter).getValue());
			}
		}
	}

	@Test
	public void testFullerene() throws Exception {
		// String smiles =
		// "O=C(O)C(N)CCCCNC%31%26C%29C5C3=C%25C=%23C%17=C4C=2c%16c1c%15c%14C%12=C1C=%10C=2C=9C(=C34)C5=C8c%30c7c6c%28C%22=C%21C6=C%13C=%11C7=C8C=9C=%10C=%11C%12=C%13C=%20c%14c%19c%18c%15c(c%16%17)C%24=C%18C=%27C(=C%19C=%20%21)C%22=C(C=%27C(C=%23%24)C%25%26)C%31c%28c%29%30";
		// String smiles =
		// "NC(=O)C%32=C(N)C(N1C=CC=CC1)=C(C(N)=O)C%31%35C3=C%30C=2c%29c%10C=9C=2C=%26C3=C%34C%27=C8c7c%33c6c(c%28C=5C%31C%30=C4c%29c%11C%12=C4C=5C=%13c%28c%14c6c%15c7C%16=C8C%17=C(C=9C%18C%20c%10c%11C%19C%24=C%12C=%13C=%23C%14=C%15C%25=C%16C(=C%17%18)C%21(C(C(N)=O)=C(C(N)=C(C(N)=O)C%19%20%21)N%22C=CC=CC%22)C%25C=%23%24)C=%26%27)C%32%35C%33%34";

		String smiles = "c1ccccc1";
		SmilesParserWrapper w = SmilesParserWrapper
				.getInstance(SMILES_PARSER.CDK);
		IAtomContainer a = w.parseSmiles(smiles);
		for (int i = 0; i < a.getAtomCount(); i++) {
			a.getAtom(i).setID(Integer.toString(i + 1));
		}

		int[][] aMatrix = PathTools.computeFloydAPSP(AdjacencyMatrix
				.getMatrix(a));
		/*
		 * System.out.println("%%MatrixMarket matrix coordinate real general");
		 * System
		 * .out.println(String.format("%d %d %d",a.getAtomCount(),a.getAtomCount
		 * (),a.getBondCount()));
		 */
		int count = 0;
		double max = 0;

		for (int i = 0; i < aMatrix.length; i++)
			for (int j = i + 1; j < aMatrix[i].length; j++) {
				double v = getVal(aMatrix[i][j]);
				if (v > max)
					max = v;
				if (v > 0)
					count++;
			}
		System.out.println("%%MatrixMarket matrix coordinate real general");
		System.out.println(String.format("%d %d %d", aMatrix.length,
				aMatrix.length, count));

		for (int i = 0; i < aMatrix.length; i++)
			for (int j = i + 1; j < aMatrix[i].length; j++)
				if (aMatrix[i][j] > 0) {
					double v = getVal(aMatrix[i][j]);
					System.out.println(String.format("%s %s %f", i + 1, j + 1,
							v));
				}
		System.out.println(max);

		/*
		 * System.out.println("%%MatrixMarket matrix coordinate real general");
		 * System
		 * .out.println(String.format("%d %d %d",a.getAtomCount(),a.getAtomCount
		 * (),a.getBondCount())); for (int i=0; i < a.getBondCount(); i++) {
		 * System.out.println(String.format("%s %s %d",
		 * a.getBond(i).getAtom(0).getID(), a.getBond(i).getAtom(1).getID(), 1
		 * )); }
		 */
	}

	private static final double coeff = Math.sqrt(3);

	protected double getVal(int m) {

		if (m % 2 == 0)
			return m * coeff;
		else
			return m;
	}
}
