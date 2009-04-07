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
package ambit2.ui.editors;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EventObject;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openscience.cdk.event.ICDKChangeListener;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;

import ambit2.core.io.CompoundImageTools;


/**
 * 2D structure diagram
 * @author Nina Jeliazkova
 *
 */
public class Panel2D extends JPanel implements ICDKChangeListener, ComponentListener, IAmbitEditor<IAtomContainer>, PropertyChangeListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6293533800645578594L;
	protected CompoundImageTools tools;
	protected IAtomContainer atomContainer;
	protected IAtomContainer selected;
	protected Image image=null;
	protected boolean generate2d = true;
	protected boolean editable = false;
	protected MoleculeEditAction editAction;
	
	public Panel2D() {
		super();
		setEditable(true);
		tools = new CompoundImageTools();
		tools.setImageSize(getPreferredSize());
		image=tools.getDefaultImage();
		addComponentListener(this);
        addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
    			super.mouseClicked(e);        		
        		if (isEditable() && (e.getClickCount()==1)) {
        			if (editAction == null)
        				editAction = new MoleculeEditAction(null);
        			editAction.setMolecule((IMolecule)getObject());
        			editAction.actionPerformed(null);
        			IMolecule molecule = editAction.getMolecule();
        			if (molecule != null) {
        				//to force 2D generation, otherwise the image is broken
	        			//Iterator<IAtom> atoms = molecule.atoms();
	        			//while (atoms.hasNext()) {atoms.next().setPoint2d(null);}
        			}
        			setAtomContainer(molecule, true);
        		}	
        	}
        });
        setPreferredSize(new Dimension(150,150));
	}
	@Override
	public void paint(Graphics g) {
		super.paintComponent(g);    
		if (image == null) {
			image = tools.getImage(atomContainer,selected,generate2d);
		}
		g.drawImage(image,0,0,this);
	}
	
	public void setAtomContainer(IAtomContainer mol, boolean generate2d) {
		this.generate2d = generate2d;
		image = null;
		atomContainer = mol;
		selected = null;
		repaint();
	}	
	public void setAtomContainer(IAtomContainer mol) {
		setAtomContainer(mol,false);
	}
	public void setSelected(IAtomContainer selected) {
		this.selected = selected;
		image = null;
		repaint();
	}	
	public void stateChanged(EventObject e) {
		Rectangle r = ((Component)e.getSource()).getBounds();
		tools.setImageSize(new Dimension(r.width,r.height));
		image = null;
		repaint();
	}

	public void componentResized(ComponentEvent e) {
		Rectangle r = ((Component)e.getSource()).getBounds();
		tools.setImageSize(new Dimension(r.width,r.height));
		image = null;
		repaint();
		
	}

	public JComponent getJComponent() {
		return this;
	}
	public IAtomContainer getObject() {
		return atomContainer;
	}
	public boolean isEditable() {
		return editable;
	}
	public void setEditable(boolean editable) {
		this.editable = editable;
		
	}
	public void setObject(IAtomContainer object) {
		setAtomContainer(object);
		
	}
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getNewValue() instanceof IAtomContainer)
			setAtomContainer((IAtomContainer)evt.getNewValue(),true);
		
	}
	public boolean confirm() {
		return true;
	}	
}


