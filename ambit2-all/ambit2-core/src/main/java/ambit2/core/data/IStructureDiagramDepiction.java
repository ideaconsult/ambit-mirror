package ambit2.core.data;

import java.awt.Dimension;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.openscience.cdk.interfaces.IAtomContainer;

public interface IStructureDiagramDepiction<OUTPUT> {
	OUTPUT getImage(IAtomContainer mol,String ruleID,int width,int height,boolean atomnumbers)  throws AmbitException;
	OUTPUT getImage(IAtomContainer mol)  throws AmbitException;
	OUTPUT getLegend(int width,int height)  throws AmbitException;
	void setImageSize(Dimension imageSize);
	Dimension getImageSize();
}
