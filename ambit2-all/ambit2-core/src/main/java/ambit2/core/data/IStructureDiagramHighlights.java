package ambit2.core.data;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.exceptions.AmbitException;

public interface IStructureDiagramHighlights {
	BufferedImage getImage(IAtomContainer mol,String ruleID,int width,int height,boolean atomnumbers)  throws AmbitException;
	BufferedImage getImage(IAtomContainer mol)  throws AmbitException;
	void setImageSize(Dimension imageSize);
	Dimension getImageSize();
}
