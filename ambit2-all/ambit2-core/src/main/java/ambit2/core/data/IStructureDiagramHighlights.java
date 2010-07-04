package ambit2.core.data;

import java.awt.image.BufferedImage;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.exceptions.AmbitException;

public interface IStructureDiagramHighlights {
	BufferedImage getStructureDiagramWithHighlights(IAtomContainer mol,String ruleID,int width,int height,boolean atomnumbers)  throws AmbitException;
}
