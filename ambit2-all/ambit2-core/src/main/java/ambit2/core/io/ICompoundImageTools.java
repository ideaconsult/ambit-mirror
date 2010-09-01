package ambit2.core.io;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;

import org.openscience.cdk.interfaces.IAtomContainer;

public interface ICompoundImageTools {
	BufferedImage getImage(Object o);
	void setBackground(Color background);
	Dimension getImageSize();
	void setBorderColor(Color borderColor);
	void setImageSize(Dimension imageSize);
	Color getBorderColor();
	Color getBackground();
	Image getDefaultImage();
	
	BufferedImage getImage(IAtomContainer molecule);

}
