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

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.iterator.IteratingMDLReader;
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
import org.openscience.cdk.renderer.selection.IChemObjectSelection;
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.core.data.MoleculeTools;
import ambit2.jchempaint.renderer.generators.SelectAtomGenerator;
import ambit2.jchempaint.renderer.selection.SingleSelection;

/**
 * 
 * @author nina
 <pre>
 parameter: BasicSceneGenerator$BackgroundColor -> java.awt.Color[r=255,g=255,b=255]
parameter: BasicSceneGenerator$ForegroundColor -> java.awt.Color[r=0,g=0,b=0]
parameter: BasicSceneGenerator$Margin -> 10.0
parameter: BasicSceneGenerator$UseAntiAliasing -> true
parameter: BasicSceneGenerator$UsedFontStyle -> NORMAL
parameter: BasicSceneGenerator$FontName -> Arial
parameter: BasicSceneGenerator$ZoomFactor -> 1.0
parameter: BasicSceneGenerator$Scale -> 1.0
parameter: BasicSceneGenerator$FitToScreen -> false
parameter: BasicSceneGenerator$ShowMoleculeTitle -> false
parameter: BasicSceneGenerator$ShowTooltip -> false
parameter: BasicBondGenerator$BondWidth -> 1.0
parameter: BasicBondGenerator$DefaultBondColor -> java.awt.Color[r=0,g=0,b=0]
parameter: BasicBondGenerator$BondLength -> 40.0
parameter: BasicBondGenerator$WedgeWidth -> 2.0
parameter: BasicBondGenerator$BondDistance -> 2.0
parameter: BasicBondGenerator$TowardsRingCenterProportion -> 0.15
parameter: BasicAtomGenerator$AtomColor -> java.awt.Color[r=0,g=0,b=0]
parameter: BasicAtomGenerator$AtomColorer -> org.openscience.cdk.renderer.color.CDK2DAtomColors@192d342
parameter: BasicAtomGenerator$AtomRadius -> 8.0
parameter: BasicAtomGenerator$ColorByType -> true
parameter: BasicAtomGenerator$CompactShape -> SQUARE
parameter: BasicAtomGenerator$CompactAtom -> false
parameter: BasicAtomGenerator$KekuleStructure -> false
parameter: BasicAtomGenerator$ShowEndCarbons -> false
parameter: BasicAtomGenerator$ShowExplicitHydrogens -> true

</pre>
 */
public class RendererTest {

	@Test
	public void testImage() throws Exception {
		
		CompoundImageTools t = new CompoundImageTools();
		//t.setBackground(Color.GRAY);
		final IMolecule mol = MoleculeFactory.makePhenylAmine();
		IAtom anAtom = null;
		for (IAtom atom : mol.atoms()) {
			if (atom.getSymbol().equals("N")) {
				atom.setProperty(CompoundImageTools.SELECTED_ATOM_COLOR,Color.CYAN);
				anAtom = atom;
			}
		}
		StructureDiagramGenerator g = new StructureDiagramGenerator(mol);
		g.generateCoordinates();
		final IMolecule selectedMol = MoleculeTools.newMolecule(SilentChemObjectBuilder.getInstance());
		selectedMol.addAtom(anAtom);
		selectedMol.addBond(mol.getBond(0));
		selectedMol.addAtom(mol.getBond(0).getAtom(0));
		selectedMol.addAtom(mol.getBond(0).getAtom(1));
		BufferedImage img = t.getImage(mol,new IProcessor<IAtomContainer, IChemObjectSelection>() {
			
			@Override
			public void setEnabled(boolean value) {
				
			}
			
			@Override
			public IChemObjectSelection process(IAtomContainer target)
					throws AmbitException {
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
		},true,false);
		
		
		File file = new File("test.png");
		if (file.exists()) file.delete();
		ImageIO.write(img, "png", file);
		Assert.assertTrue(file.exists());
		System.out.println(file.getAbsolutePath());
		
	}

	@Test
	public void testNoWeirdGrayLines() throws Exception {
		testsdf2image("ambit2/rendering/test-ok.sdf","test-ok.png");
	}
	@Test
	public void testWeirdGrayLines() throws Exception {
		testsdf2image("ambit2/rendering/test-gray.sdf","test-gray.png");
	}
	
	public void testsdf2image(String filename,String imgfile) throws Exception {

  	    InputStream in = RendererTest.class.getClassLoader().getResourceAsStream(filename);
  	    
		Assert.assertNotNull(in);
		IteratingMDLReader reader = new IteratingMDLReader(in, SilentChemObjectBuilder.getInstance());

		while (reader.hasNext()) {
			IChemObject mol = reader.next();
			Assert.assertTrue(mol instanceof IMolecule);
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms((IMolecule)mol);
			/**
			 * if generating 2D coordinates via StructureDiagramGenerator, the problem goes away, 
			 * but it is still valid use case to generate a structure diagram from coordinates retrieved from a file 
			 */
			//StructureDiagramGenerator sdg = new StructureDiagramGenerator((IMolecule)mol);
			//sdg.generateCoordinates(new Vector2d(0,1));

			BufferedImage img  = paint((IMolecule)mol);
			File file = new File(imgfile);
			//file.deleteOnExit();
			if (file.exists()) file.delete();
			ImageIO.write(img, "png", file);
			Assert.assertTrue(file.exists());
			System.out.println(file.getAbsolutePath());
		}
		in.close();
	}
	
	protected BufferedImage paint(IAtomContainer mol) throws Exception {
		Rectangle drawArea = new Rectangle(0,0,200,200);
		BufferedImage img = new BufferedImage(drawArea.width,drawArea.height,BufferedImage.TYPE_INT_RGB);
		int alpha = 128;
		final Graphics2D g = img.createGraphics();
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC,1.0f);
		g.setComposite(ac);
		g.setColor(new Color(255,255,255,255));
		g.fillRect(0, 0, drawArea.width,drawArea.height);
		
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
        		return new Color(0,0,0,255);
        	}
        });
       // generators.add(new BasicAtomGenerator());
        
        IRenderer renderer = new AtomContainerRenderer(generators, new AWTFontManager()) ;	
        final RendererModel rendererModel = renderer.getRenderer2DModel();
        rendererModel.set(BasicBondGenerator.DefaultBondColor.class, new Color(255,0,0,alpha));
        rendererModel.set(BasicSceneGenerator.BackgroundColor.class, new Color(0,0,255,alpha));

        renderer.setup(mol,drawArea);
        renderer.paint(mol,new AWTDrawVisitor(g));
     
		
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
		IRenderer renderer = new AtomContainerRenderer(
		  generators, new AWTFontManager()
		);

		// dump all parameters
		for (Object generator : renderer.getGenerators()) {
			IGenerator g = (IGenerator) generator;
		  for (Object parameter : g.getParameters()) {
		      System.out.println("parameter: " +
		      parameter.getClass().getName().substring(40) +
		      " -> " +
		      ((IGeneratorParameter)parameter).getValue());
		  }
		}
	}
}
