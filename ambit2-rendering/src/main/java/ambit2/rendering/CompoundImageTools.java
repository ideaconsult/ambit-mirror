/*
 * Created on 2006-3-5
 *
 */
package ambit2.rendering;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIToStructure;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.AtomContainerRenderer;
import org.openscience.cdk.renderer.IRenderer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.color.CDK2DAtomColors;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.OvalElement;
import org.openscience.cdk.renderer.elements.RectangleElement;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.BasicBondGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.generators.IGeneratorParameter;
import org.openscience.cdk.renderer.generators.RingGenerator;
import org.openscience.cdk.renderer.generators.SelectAtomGenerator;
import org.openscience.cdk.renderer.generators.SelectBondGenerator;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;
import org.openscience.cdk.renderer.selection.IncrementalSelection;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.FixBondOrdersTool;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.SaturationChecker;
import org.openscience.cdk.tools.SmilesValencyChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.core.config.AmbitCONSTANTS;
import ambit2.core.data.IStructureDiagramHighlights;
import ambit2.core.data.MoleculeTools;
import ambit2.core.io.ICompoundImageTools;
import ambit2.core.processors.structure.StructureTypeProcessor;
import ambit2.namestructure.Name2StructureProcessor;



/**
 * Generates BufferedImage from smiles or compound.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-3-5
 */
public class CompoundImageTools implements IStructureDiagramHighlights , ICompoundImageTools {
	protected static Logger logger = Logger.getLogger(CompoundImageTools.class.getName());
	protected StringBuilder imageMap = null;
	protected String comma = "";
	int offset = 6;
	private static final String formatJson = "\n\t\t{ \"i\": \"%s\",\"l\": \"%s\", \"x\": %d, \"y\": %d, \"w\": %d, \"h\": %d }";
	public StringBuilder getImageMap() {
		return imageMap;
	}
	
	public void setImageMap(StringBuilder imageMap) {
		this.imageMap = imageMap;
	}
	

	public enum Mode2D {
	
		kekule {
			@Override
			public String toString() {
				return FixBondOrdersTool.class.getName();
			}			
			@Override
			public String getDescription() {
				return String.format("SMILES parser with %s\n%s",SilentChemObjectBuilder.class.getName(),toString());
			}
		},

		saturationChecker {
			@Override
			public String toString() {
				return SaturationChecker.class.getName();
			}
			@Override
			public String getDescription() {
				return String.format("SMILES parser with %s\n%s",SilentChemObjectBuilder.class.getName(),toString());
			}			
		},
		smilesvalencychecker {
			@Override
			public String toString() {
				return SmilesValencyChecker.class.getName();
			}
			@Override
			public String getDescription() {
				return String.format("SMILES parser with %s\n%s",SilentChemObjectBuilder.class.getName(),toString());
			}
		},
		aromatic {
			@Override
			public String toString() {
				return "Aromatic";
			}
			@Override
			public String getDescription() {
				return String.format("SMILES parser with %s %s",SilentChemObjectBuilder.class.getName(),"only.\n Always shows aromatic ring circles.");
			}			
		},
		any {
			@Override
			public String getURIParameter() {
				return "";
			}
			@Override
			public String toString() {
				return "Best guess";
			}
			@Override
			public String getDescription() {
				
				return "SMILES parser only. No additional preprocessing.\nShows circles if there are only single bonds with aromatic flags. \nShows Kekule representation if there are single and double bonds with aromatic flags.";
			}
		};		
		public String getURIParameter() {
			return name();
		}
		public String getDescription() {
			return String.format("SMILES Parser and %s",toString());
		}		
	};	
	public final static String SELECTED_ATOM_COLOR = "ambit2.color";
	public final static String SELECTED_ATOM_SIZE = "ambit2.size";
	public final static String ATOM_ANNOTATION = "ambit2.tooltip";
	public final static Color whiteTransparent = new Color(0x00ffffff,true);
    RendererModel r2dm;
    IRenderer renderer;
    protected Dimension imageSize = new Dimension(200,200);
    protected Color background = whiteTransparent;
    protected BufferedImage defaultImage = null;
    BufferedImage buffer = null;
    protected Color borderColor = whiteTransparent;
    protected int borderWidth = 5;

	public int getBorderWidth() {
		return borderWidth;
	}
	public void setBorderWidth(int borderWidth) {
		this.borderWidth = borderWidth;
	}
	public Color getBorderColor() {
		return borderColor;
	}
    /**
     * Creates gradient border of this color. Set the same as background color if you don't want shadowing effect.
     * @param borderColor
     */
	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}
	protected SmilesParser parser = null;
    public SmilesParser getParser() {
		return parser;
	}
	public void setParser(SmilesParser parser) {
		this.parser = parser;
	}
	/**
     * 
     */
    public CompoundImageTools() {
        this(new Dimension(200,200));
    }
    
    public CompoundImageTools(Dimension cellSize) {
        super();
        renderer = createRenderer(cellSize,background,false,false,false);
        r2dm = renderer.getRenderer2DModel();
		this.imageSize = cellSize;
    }
    
    private IRenderer createRenderer(Dimension cellSize,Color background,boolean rings, boolean atomNumbers, boolean explicitH) {
        List<IGenerator<IAtomContainer>> generators = new ArrayList<IGenerator<IAtomContainer>>();
        generators.add(new BasicSceneGenerator());

        generators.add(new BasicBondGenerator());
        
        generators.add(new SelectBondGenerator());
        if (rings)  generators.add(new RingGenerator());
        //generators.add(new MyBasicAtomGenerator()); //this was to clean the background behind the labels
        generators.add(new BasicAtomGenerator());
        if (atomNumbers)
            generators.add(new AtomLabelGenerator());
        generators.add(new ImageMapGenerator());
        generators.add(new MySelectAtomGenerator());


       generators.add(new AtomAnnotationGenerator());
        
        
 	   IRenderer renderer = new AtomContainerRenderer(generators, new AWTFontManager()) ;
 	   RendererModel r2dm = renderer.getRenderer2DModel();	
 	   
 	   RendererModelWrapper.setCompactShape(r2dm,BasicAtomGenerator.Shape.OVAL);
 	
 	   if (atomNumbers)
 		   RendererModelWrapper.setDrawNumbers(r2dm,atomNumbers);
 	   
 	   RendererModelWrapper.setUseAntiAliasing(r2dm,true);	

 	   //r2dm.set(paramType, value)
 	   RendererModelWrapper.setBackColor(r2dm,new Color(background.getRed(),background.getGreen(),background.getBlue(),128));

 	   RendererModelWrapper.setShowExplicitHydrogens(r2dm,explicitH);
 	   
 	   RendererModelWrapper.setShowAromaticity(r2dm,true);  
 	   
 	   RendererModelWrapper.setAtomColorer(r2dm,new CDK2DAtomColorsHalogens());
 	   
 	  
 		return renderer;
     }
    /*
    private IRenderer createRenderer(Dimension cellSize,Color background,boolean rings, boolean atomNumbers) {
       List<IGenerator<IAtomContainer>> generators = new ArrayList<IGenerator<IAtomContainer>>();
       generators.add(new BasicSceneGenerator());

       generators.add(new BasicBondGenerator());
       if (rings)  generators.add(new RingGenerator());
       generators.add(new MyBasicAtomGenerator());
       generators.add(new MySelectAtomGenerator());
       generators.add(new BasicAtomGenerator());
       if (atomNumbers)
           generators.add(new AtomNumberGenerator());
       
       generators.add(new SelectBondGenerator());

       generators.add(new AtomAnnotationGenerator());
       
       
	   IRenderer renderer = new AtomContainerRenderer(generators, new AWTFontManager()) ;
	   RendererModel r2dm = renderer.getRenderer2DModel();	
	   
	   RendererModelWrapper.setCompactShape(r2dm,BasicAtomGenerator.Shape.OVAL);
	
	   if (atomNumbers)
		   RendererModelWrapper.setDrawNumbers(r2dm,atomNumbers);
	   
	   RendererModelWrapper.setUseAntiAliasing(r2dm,true);	

	   RendererModelWrapper.setBackColor(r2dm,new Color(background.getRed(),background.getGreen(),background.getBlue(),0));

	   RendererModelWrapper.setShowExplicitHydrogens(r2dm,false);
	   
	   RendererModelWrapper.setShowAromaticity(r2dm,true);  
		return renderer;
    }
    */
    public synchronized BufferedImage getImage(Object o) {
        if (o instanceof IAtomContainer)
            return getImage((IAtomContainer)o);
        else return defaultImage;
    }

    public synchronized BufferedImage generateImage(String value) throws CDKException {
    	return generateImage(value, null,false,false);
    }
    public synchronized BufferedImage generateImage(
    		String value,
    		IProcessor<IAtomContainer,IChemObjectSelection> selector, 
    		boolean build2d,
    		boolean atomNumbers
    		) throws CDKException {
    	return generateImage(value, selector, build2d, atomNumbers,null);
    }
    public synchronized BufferedImage generateImage(
    		String value,
    		IProcessor<IAtomContainer,IChemObjectSelection> selector, 
    		boolean build2d,
    		boolean atomNumbers,
    		Mode2D mode2D
    		) throws CDKException {
   		if (value.startsWith(AmbitCONSTANTS.INCHI)) {
    			InChIGeneratorFactory f = InChIGeneratorFactory.getInstance();
    			InChIToStructure c =f.getInChIToStructure(value, SilentChemObjectBuilder.getInstance());
    			
    			if ((c==null) || (c.getAtomContainer()==null) || (c.getAtomContainer().getAtomCount()==0)) 
    				throw new CDKException(String.format("%s %s %s", c.getReturnStatus(),c.getMessage(),c.getLog()));
    			return getImage(c.getAtomContainer(),selector,build2d,atomNumbers,false,mode2D);
    	}  else { 	
	        if (parser == null) parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
	        IAtomContainer molecule = null;
	        try {
	        	molecule = parser.parseSmiles(value);
	        } catch (InvalidSmilesException x) {
	        	Name2StructureProcessor processor = new Name2StructureProcessor();
	        	try {
					molecule = processor.process(value);
	        	} catch (Exception xx) {molecule = null; throw x;}
	        }
	        return molecule==null?null:getImage(molecule,selector,build2d,atomNumbers,false,mode2D);
    	}
    }    
    public synchronized BufferedImage getImage(String smiles) {
        ArrayList<String> m = null;
        try {
            return generateImage(smiles);
        } catch (Exception x) {
            m = new ArrayList<String>();
            m.add(x.getMessage());
        }
        return getImage(m);
    }
    
	public synchronized BufferedImage getImage(IAtomContainer molecule) {
		return getImage(molecule,null,false,false);
	}
	
    public synchronized BufferedImage getImage(IAtomContainer molecule, 
    		IProcessor<IAtomContainer,IChemObjectSelection> selector, 
    		boolean build2d,
    		boolean atomNumbers) {
    	return getImage(molecule, selector,build2d,atomNumbers,false);
    }
    public synchronized BufferedImage getImage(IAtomContainer molecule, 
    		IProcessor<IAtomContainer,IChemObjectSelection> selector, 
    		boolean build2d,
    		boolean atomNumbers,
    		boolean explicitH) {  
    	return getImage(molecule,selector,build2d,atomNumbers,explicitH,null);
    }
    public synchronized BufferedImage getImage(IAtomContainer molecule, 
    		IProcessor<IAtomContainer,IChemObjectSelection> selector, 
    		boolean build2d,
    		boolean atomNumbers,
    		boolean explicitH,
    		Mode2D mode2d) { 
    	
    	imageMap = new StringBuilder();
    	boolean rings = true;
    	if (mode2d == null) {
        	if (molecule!=null)
            	for (int i=0; i < molecule.getBondCount();i++)
            		if (molecule.getBond(i).getFlag(CDKConstants.ISAROMATIC)) 
            			if (IBond.Order.DOUBLE.equals(molecule.getBond(i).getOrder())) {
            				rings = false;
            				break;
            			}
    	} else
	    	switch (mode2d) {
	    	case aromatic: {
	    		rings = true;
	    		break;
	    	}
	 
	    	case kekule : {
	    		rings = false;
	    		try {
	    			//AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
	    			FixBondOrdersTool dbt = new FixBondOrdersTool();
	    			molecule = dbt.kekuliseAromaticRings((IMolecule) molecule);
	    		} catch (Exception x) {
	    			logger.log(Level.WARNING,mode2d.name(),x);
	    		}
	    		break;
	    	}
	    	case saturationChecker: {
	    		rings = false;
	    		try {
	    			//AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
	    			SaturationChecker dbt = new SaturationChecker();
	    			dbt.saturate(molecule);
	    		} catch (Exception x) {
	    			logger.log(Level.WARNING,mode2d.name(),x);
	    		}
	    		break;
	    	}
 	
	    	case smilesvalencychecker: {
	    		rings = false;
	    		try {
	    			//AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
	    			SmilesValencyChecker dbt = new SmilesValencyChecker();
	    			dbt.saturate(molecule);
	    		} catch (Exception x) {
	    			logger.log(Level.WARNING,mode2d.name(),x);
	    		}
	    		break;
	    	}	    	
	    	case any: {
	        	if (molecule!=null)
	            	for (int i=0; i < molecule.getBondCount();i++)
	            		if (molecule.getBond(i).getFlag(CDKConstants.ISAROMATIC)) 
	            			if (IBond.Order.DOUBLE.equals(molecule.getBond(i).getOrder())) {
	            				rings = false;
	            				break;
	            			}
	    	}
	    	}

    	
    	renderer = createRenderer(imageSize,background,rings,atomNumbers,explicitH);
    	r2dm = renderer.getRenderer2DModel();
    	
        if (buffer == null) buffer = createBuffer();
        
		Graphics2D g = buffer.createGraphics();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC , 1.0f ));
		g.setColor(background);
		g.fillRect(0, 0, imageSize.width, imageSize.height);
		
		
		IMoleculeSet molecules = new MoleculeSet();
        generate2D(molecule, build2d, molecules);
        Dimension shrinked = new Dimension(imageSize.width-2*offset,imageSize.height-2*offset);
        paint(renderer,molecules, false, g, selector,shrinked,atomNumbers);
        
        
        if (borderColor != background)
        	paintBorderShadow(g,getBorderWidth(),new Rectangle(imageSize));
        g.dispose();
		return buffer;
    }
    protected BufferedImage createBuffer() {
    	 return new BufferedImage(imageSize.width, imageSize.height,BufferedImage.TYPE_INT_ARGB);
    }
    private void paintBorderShadow(Graphics2D g2, int shadowWidth, Shape shape) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        int sw = shadowWidth*2;
        for (int i=sw; i >= 2; i-=2) {
            float pct = (float)(sw - i) / (sw - 1);
            g2.setColor(getMixedColor(borderColor, pct,
                                      background, 1.0f-pct));
            g2.setStroke(new BasicStroke(i));
            g2.draw(shape);
        }
    }
    private  Color getMixedColor(Color c1, float pct1, Color c2, float pct2) {
        float[] clr1 = c1.getComponents(null);
        float[] clr2 = c2.getComponents(null);
        for (int i = 0; i < clr1.length; i++) {
            clr1[i] = (clr1[i] * pct1) + (clr2[i] * pct2);
        }
        return new Color(clr1[0], clr1[1], clr1[2], clr1[3]);
    }

    public synchronized void generate2D(IAtomContainer molecule,boolean generateCoordinates,IMoleculeSet molecules)  {
        if (molecule != null) {
        	
        	if (!generateCoordinates) {
	            if ((molecule ==null) || (molecule.getAtomCount() == 0)) 
	                generateCoordinates=false;
	            else if (StructureTypeProcessor.has2DCoordinates(molecule)>1)   
	               generateCoordinates=false;
	            else generateCoordinates = true;
        	}
            
            molecules.removeAllAtomContainers();
            if (!generateCoordinates) {
                //IAtomContainer c = AtomContainerManipulator.removeHydrogensPreserveMultiplyBonded(molecule);             	
            	//molecules.addAtomContainer(c);
            	//there still could be multiple parts !
            	IMoleculeSet mset =  ConnectivityChecker.partitionIntoMolecules(molecule);
            	molecules.add(mset);
            	mset.removeAllAtomContainers();
            	mset = null;
            	return;
            }            
            try
            {
            	IMoleculeSet mset =  ConnectivityChecker.partitionIntoMolecules(molecule);
                StructureDiagramGenerator sdg = new StructureDiagramGenerator();
                IMolecule m = null;
                for (int i=0; i < mset.getAtomContainerCount();i++) {
                    IAtomContainer c = AtomContainerManipulator.removeHydrogensPreserveMultiplyBonded(mset.getAtomContainer(i));                	
                    if (generateCoordinates) {
                        sdg.setMolecule((IMolecule)c,false);
                        m = null;
                        sdg.generateCoordinates(new Vector2d(0,1));
                        m = sdg.getMolecule();
                        
                    } else {
                    	m = (IMolecule)c;//mset.getMolecule(i);
                    }
                    molecules.addMolecule(m);
                }
                mset.removeAllAtomContainers();
            }
            
            catch(Exception exc)
            {
                molecules.removeAllAtomContainers();
            }

            	
        } else 
        	molecules.removeAllAtomContainers();
    }


	
	public synchronized void paint(IRenderer renderer, 
    		IMoleculeSet molecules,
			boolean explicitH,  
			Graphics2D g,
			IProcessor<IAtomContainer,IChemObjectSelection> selector,
			boolean atomNumbers) {
		paint(renderer, molecules, explicitH, g, selector,getImageSize(),atomNumbers);
	}

	
	/**
	 * TODO sort molecules, in order to display the largest part first
	 * @param renderer
	 * @param molecules
	 * @param explicitH
	 * @param g
	 * @param highlighted
	 * @param imageSize
	 */
	
	public synchronized void paint(IRenderer renderer, 
    		IMoleculeSet molecules,
			boolean explicitH,  
			Graphics2D g,
			IProcessor<IAtomContainer,IChemObjectSelection> selector,
			Dimension imageSize,
			boolean atomNumbers)
	{
    	renderer = renderer==null?createRenderer(imageSize,whiteTransparent,false,atomNumbers,explicitH):renderer;
    	RendererModel r2dm = renderer.getRenderer2DModel();

		if ((molecules != null) && (molecules.getAtomContainerCount()>0)) {
			
			IMolecule all;
			int c = 0;
			if (molecules.getAtomContainerCount()>1) {
			
				Rectangle2D box = new Rectangle2D.Double(0,0,0,0);
				
				all = MoleculeTools.newMolecule(SilentChemObjectBuilder.getInstance());
				
				//calculate box dimension, so that we can center vertically
				for (IAtomContainer  m : molecules.molecules()) {
					c++;
					Rectangle2D r = GeometryTools.getRectangle2D(m);
					
					box.setRect(
							box.getX(),
							box.getY(),
							1 + box.getWidth() + (r.getWidth()==0?1.5:r.getWidth()),
							r.getHeight()> box.getHeight()?r.getHeight():box.getHeight()
									);

				}				
				double hoffset = box.getHeight()/2.0;
				Rectangle2D b = new Rectangle2D.Double(0,0,0,0);
				for (IAtomContainer  m : molecules.molecules()) {
					c++;
					Rectangle2D r = GeometryTools.getRectangle2D(m);
					
					GeometryTools.translate2D(
							m, 
							- r.getX() + b.getX() + b.getWidth(),
							-r.getY() + b.getY() + hoffset - (r.getHeight()/2.0) 
							);
					b.setRect(
							b.getX(),
							b.getY(),
							1 + b.getWidth() + (r.getWidth()==0?1.5:r.getWidth()),
							r.getHeight()> b.getHeight()?r.getHeight():b.getHeight()
									);
					

					all.add(m);
				}
				b.setRect(b.getX(),b.getY(),b.getWidth(),b.getHeight());
				
			} else all = molecules.getMolecule(0);
			
			
			Rectangle drawArea = new Rectangle(offset,offset,imageSize.width,imageSize.height);
			renderer.setup(all,drawArea);
			//renderer.getRenderer2DModel().setZoomFactor(0.8);
			
			IChemObjectSelection highlighted = null;
            if (selector!= null)
            	try {
            		IMolecule mol2process = (IMolecule)all.clone();
            		IChemObjectSelection selected = selector.process(mol2process);
    				if(selected!=null) {
    					highlighted = selected;
    					
    				}
            	} catch (Exception e) {
            		e.printStackTrace();
            	}
            	
	    	if (highlighted != null) {
	    		RendererModelWrapper.setSelectedPartColor(r2dm,new Color(0,183,239,128));
	    		RendererModelWrapper.setSelectionShape(r2dm,BasicAtomGenerator.Shape.OVAL);
	    		r2dm.setSelection(highlighted);
	    		RendererModelWrapper.setColorAtomsByType(r2dm,true);
	    		RendererModelWrapper.setShowAtomTypeNames(r2dm,true);

	    	} 	  
	    	try {
	    		if (imageMap!= null) imageMap.append("{\n\t\"a\": [");
	    		comma = "";
	    		renderer.paint(all,new AWTDrawVisitorWithImageMap(g) {
	    			@Override
	    			protected void imageMap(ImageMapAreaElement atomSymbol,
	    					Rectangle2D textBounds) {
	    				if ((imageMap!=null)) {
	    					int x = (int) textBounds.getCenterX();
	    					int y = (int) textBounds.getCenterY();
	    					int w = (int) textBounds.getWidth();
	    					int h = (int) textBounds.getHeight();
	    					imageMap.append(comma);
	    					imageMap.append(String.format(formatJson,
	    							atomSymbol.text,
	    							atomSymbol.text,
	    							x,y,
	    							w,h
	    							));
	    					comma =",";
	    				}
	    			}
	    			
	    		},drawArea,false);

	    	} catch (Exception e) {
	    		r2dm.setSelection(null);
	    		renderer.paint(all,new AWTDrawVisitorWithImageMap(g) {
	    			protected void imageMap(ImageMapAreaElement atomSymbol,
	    					Rectangle2D textBounds) {
	    				if ((imageMap!=null)) {
	    					int x = (int) textBounds.getCenterX();
	    					int y = (int) textBounds.getCenterY();
	    					int w = (int) textBounds.getWidth();
	    					int h = (int) textBounds.getHeight();
	    					imageMap.append(comma);
	    					imageMap.append(String.format(formatJson,
	    							atomSymbol.text,
	    							atomSymbol.text,
	    							x,y,
	    							w,h
	    							));
	    					comma =",";
	    				}
	    			}
	    		},drawArea,false);
	    	}
		} else {
			g.setBackground(whiteTransparent);
			g.clearRect(0,0,imageSize.width,imageSize.height);
		}
		if (imageMap!= null) imageMap.append("\n\t]\n}");
	}
           
    public synchronized BufferedImage getImage(ArrayList<?> list) {
        if (buffer == null)  buffer = createBuffer();
        
		Graphics2D g = buffer.createGraphics();
		g.setColor(background);
		g.fillRect(0, 0, imageSize.width, imageSize.height);
		g.setColor(Color.black);
		int y = 32;
		for (int i=0; i < list.size();i++) {
			g.drawString(list.get(i).toString(), 16,y);
			y += 16;
		}	
	
		return buffer;
    }
    

    public synchronized Color getBackground() {
        return background;
    }
    public synchronized void setBackground(Color background) {
        this.background = background;
    }
    public synchronized Dimension getImageSize() {
        return imageSize;
    }
    public synchronized void setImageSize(Dimension imageSize) {
        this.imageSize = imageSize;
        buffer = null;
    }
	public Image getDefaultImage() {
		if (defaultImage==null) defaultImage = createDefaultImage();
		return defaultImage;
	}
	public void setDefaultImage(BufferedImage defaultImage) {
		this.defaultImage = defaultImage;
	}
	public BufferedImage createDefaultImage() {
		return createDefaultImage("N/A");
	}
	public BufferedImage createDefaultImage(String msg) {
			BufferedImage buffer = createBuffer();
			Graphics2D g = buffer.createGraphics();
			g.setColor(background);
			g.fillRect(0, 0, imageSize.width, imageSize.height);
			g.setColor(Color.black);
			g.drawString(msg,imageSize.width/2 - 10,imageSize.height / 2);
			return buffer;
	}
	//this should be done via selectors ...
	public BufferedImage getImage(IAtomContainer mol,
			String smarts, int width, int height, boolean atomnumbers)
			throws AmbitException {
		setImageSize(new Dimension(width,height));
		IProcessor<IAtomContainer,IChemObjectSelection> selector = null;
		if (smarts != null) {
			//get smarts pattern

			selector = null;
		}
		return getImage(mol,selector,false,atomnumbers);
		
	}
	@Override
	public BufferedImage getLegend(int width, int height) throws AmbitException {
		return null;
	}
}

/**
 * Couldn't find how to highlight atoms with filled -in ovals, so this is my workaround
 * @author nina
 *
 */
class MyBasicAtomGenerator extends BasicAtomGenerator {
	public IRenderingElement generate(IAtomContainer ac, RendererModel model) {
		ElementGroup elementGroup = new ElementGroup();
		for (IAtom atom : ac.atoms()) {

			if ("C".equals(atom.getSymbol())) continue;
    	    Point2d p = atom.getPoint2d();
    	    
			if (p==null) continue;
    	    double r = RendererModelWrapper.getAtomRadius(model) / RendererModelWrapper.getScale(model);
    	    double d = 2 * r;
    	    if (RendererModelWrapper.getCompactShape(model) == Shape.SQUARE) {
    	    	elementGroup.add(new RectangleElement(
        	            p.x - r, p.y - r, d, d, true, CompoundImageTools.whiteTransparent));
    	    } else {
    	    	elementGroup.add(new OvalElement(
    	                p.x, p.y, r, true, CompoundImageTools.whiteTransparent));
    	    }
		}
		return elementGroup;
	}
	
	
	@Override
	public List<IGeneratorParameter<?>> getParameters() {
		return super.getParameters();
	}
}

/**
 * Atom selection size and colors are taken from properties, which might be set by different calculation procedures.
 * Currently used to visualize SOM (SmartCYP and SOME) 
 * Web services use
 * http://host/compound/{id}?model_uri=http://host/model/{id}
 * http://apps.ideaconsult.net:8080/ambit2/compound/100?model_uri=http://apps.ideaconsult.net:8080/ambit2/model/48&w=400&h=400
 * where colors are defined by the model itself, e.g. http://apps.ideaconsult.net:8080/ambit2/model/48?media=image/png 
 * 
 * @author nina
 *
 */
class MySelectAtomGenerator extends SelectAtomGenerator  {
	 private boolean autoUpdateSelection = true;

	 public MySelectAtomGenerator() { super(); }

	 @Override
	public IRenderingElement generate(IAtomContainer arg0, RendererModel model) {


	    	ElementGroup selectionElements = new ElementGroup();
	    	

	        Color selectionColor = RendererModelWrapper.getSelectedPartColor(model);
	        BasicAtomGenerator.Shape shape = RendererModelWrapper.getSelectionShape(model);
	        IChemObjectSelection selection = model.getSelection();
	        

	        if(selection==null)
	        	return selectionElements;
	        if (this.autoUpdateSelection || selection.isFilled()) {
	            double r = RendererModelWrapper.getSelectionRadius(model) / RendererModelWrapper.getScale(model);

	            double d = 4 * r;
	            IAtomContainer selectedAC = selection.getConnectedAtomContainer();
	            if (selectedAC != null) {
	                for (IAtom atom : selectedAC.atoms()) {
	                	Color atomColor = selectionColor;
	                	double m = 1;
	                	Object size = atom.getProperty(CompoundImageTools.SELECTED_ATOM_SIZE);
	                	if (size != null) try {
	                		m = Double.parseDouble(size.toString());
	                	} catch (Exception x) {}
	                	Object clr = atom.getProperty(CompoundImageTools.SELECTED_ATOM_COLOR);
	                	if ((clr != null) && (clr instanceof Color)) {
	                		Color c = (Color) clr;
	                		atomColor = new Color(c.getRed(),c.getGreen(),c.getBlue(),128); //semitransparent
	                	}
	                	
	                    Point2d p = atom.getPoint2d();
	                    if (p==null) continue;
	                    IRenderingElement element;
	                    switch (shape) {
	                        case SQUARE:
	                            element =
	                                new RectangleElement(
	                                    p.x - r, p.y - r, d, d, true,
	                                    atomColor);
	                            break;
	                        case OVAL:
	                        default:
	                            element = new OvalElement(
	                                            p.x, p.y, d * m, true, atomColor);
	                    }
	                    selectionElements.add(element);
	                }
	            }
	        }

	        if (selection instanceof IncrementalSelection) {
				IncrementalSelection sel = (IncrementalSelection) selection;
				if (!sel.isFinished())
					selectionElements.add(sel.generate(selectionColor));
			}
	        return selectionElements;
	    }

}

class CDK2DAtomColorsHalogens extends CDK2DAtomColors {
	public CDK2DAtomColorsHalogens() {
		super();
	}
	@Override
	public Color getAtomColor(IAtom atom, Color defaultColor) {
        Color color = defaultColor;
        int atomnumber = 0;
        if(atom.getAtomicNumber()!=null)
            atomnumber = atom.getAtomicNumber();
        if (atomnumber != 0) {
            switch (atomnumber) {
                case 9: color = Color.GREEN; break; //F
                case 17: color = Color.GREEN; break; //Cl
                case 35: color = Color.GREEN; break; //Br
                case 53: color = Color.GREEN; break; //I
                default: return super.getAtomColor(atom,defaultColor);
            }
        } else {
            String symbol = atom.getSymbol();
            if (symbol.equals("F")) {
                color = Color.GREEN;
            } else
            if (symbol.equals("Cl")) {
            	 color = Color.GREEN;
            } else
            if (symbol.equals("Br")) {
            	 color = Color.GREEN;
            } else
            if (symbol.equals("I")) {
            	 color = Color.GREEN;
            } else return super.getAtomColor(atom, defaultColor);
        }
        return color;
	}
}
