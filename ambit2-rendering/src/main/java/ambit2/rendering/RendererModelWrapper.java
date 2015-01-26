package ambit2.rendering;

import java.awt.Color;

import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.color.IAtomColorer;
import org.openscience.cdk.renderer.generators.AtomNumberGenerator;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.Shape;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.SelectAtomGenerator;

public class RendererModelWrapper {

    private RendererModelWrapper() {
    }

    /**
     * r2dm.setAtomRadius()
     * 
     * @param r2dm
     * @param value
     */
    public static void setAtomRadius(RendererModel r2dm, Double value) {
	r2dm.set(BasicAtomGenerator.AtomRadius.class, value);
    }

    /**
     * r2dm.getAtomRadius()
     * 
     * @param r2dm
     * @return
     */
    public static Double getAtomRadius(RendererModel r2dm) {
	return r2dm.get(BasicAtomGenerator.AtomRadius.class);
    }

    /**
     * r2dm.setCompactShape()
     * 
     * @param r2dm
     * @param value
     */
    public static void setCompactShape(RendererModel r2dm, Shape value) {
	r2dm.set(BasicAtomGenerator.CompactShape.class, value);
    }

    public static BasicAtomGenerator.Shape getCompactShape(RendererModel r2dm) {
	return r2dm.get(BasicAtomGenerator.CompactShape.class);
    }

    /**
     * r2dm.setDrawNumbers(atomNumbers);
     * 
     * @param r2dm
     * @param value
     */
    public static void setDrawNumbers(RendererModel r2dm, boolean value) {
	r2dm.set(AtomNumberGenerator.WillDrawAtomNumbers.class, value);
    }

    /**
     * r2dm.setUseAntiAliasing();
     * 
     * @param r2dm
     * @param value
     */
    public static void setUseAntiAliasing(RendererModel r2dm, boolean value) {
	r2dm.set(BasicSceneGenerator.UseAntiAliasing.class, value);
    }

    public static Double getScale(RendererModel r2dm) {
	return r2dm.get(BasicSceneGenerator.Scale.class);
    }

    /**
     * r2dm.setBackColor()
     * 
     * @param r2dm
     * @param value
     */
    public static void setBackColor(RendererModel r2dm, Color value) {
	r2dm.set(BasicSceneGenerator.BackgroundColor.class, value);
    }

    /**
     * r2dm.setShowImplicitHydrogens() (?)
     * 
     * @param r2dm
     * @param value
     */
    public static void setShowExplicitHydrogens(RendererModel r2dm, boolean value) {
	r2dm.set(BasicAtomGenerator.ShowExplicitHydrogens.class, value);
    }

    /**
     * setShowAromaticity()
     * 
     * @param r2dm
     * @param value
     */
    public static void setShowAromaticity(RendererModel r2dm, boolean value) {
	r2dm.set(BasicAtomGenerator.KekuleStructure.class, !value);
    }

    /**
     * r2dm.setSelectionShape
     * 
     * @param r2dm
     * @param value
     */
    public static void setSelectionShape(RendererModel r2dm, Shape value) {
	r2dm.set(SelectAtomGenerator.SelectionShape.class, value);
    }

    /**
     * r2dm.getSelectionShape()
     * 
     * @param r2dm
     * @return
     */
    public static Shape getSelectionShape(RendererModel r2dm) {
	return r2dm.get(SelectAtomGenerator.SelectionShape.class);
    }

    /**
     * r2dm.setSelectedPartColor()
     * 
     * @param r2dm
     * @param value
     */
    public static void setSelectedPartColor(RendererModel r2dm, Color value) {
	r2dm.set(SelectAtomGenerator.SelectionAtomColor.class, value);
    }

    /**
     * r2dm.getSelectedPartColor()
     * 
     * @param r2dm
     * @return
     */
    public static Color getSelectedPartColor(RendererModel r2dm) {
	return r2dm.get(SelectAtomGenerator.SelectionAtomColor.class);
    }

    public static Double getSelectionRadius(RendererModel r2dm) {
	return r2dm.get(SelectAtomGenerator.SelectionRadius.class);
    }

    public static void setSelectionRadius(RendererModel r2dm, Double value) {
	r2dm.set(SelectAtomGenerator.SelectionRadius.class, value);
    }

    /**
     * r2dm.setColorAtomsByType()
     * 
     * @param r2dm
     * @param value
     */
    public static void setColorAtomsByType(RendererModel r2dm, boolean value) {
	r2dm.set(BasicAtomGenerator.ColorByType.class, value);
    }

    /**
     * 
     * @param r2dm
     * @param value
     */
    public static void setShowAtomTypeNames(RendererModel r2dm, boolean value) {
	// r2dm.set(.class, value); ?????
    }

    public static void setAtomColorer(RendererModel r2dm, IAtomColorer value) {
	r2dm.set(BasicAtomGenerator.AtomColorer.class, value);
    }

}
