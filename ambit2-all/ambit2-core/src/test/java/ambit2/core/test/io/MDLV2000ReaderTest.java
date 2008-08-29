/*
Copyright (C) 2005-2008  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.core.test.io;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.IChemObjectReader;

import ambit2.core.groups.ComponentGroup;
import ambit2.core.groups.ContainerGroup;
import ambit2.core.groups.DataGroup;
import ambit2.core.groups.IExpandable;
import ambit2.core.groups.ISGroup;
import ambit2.core.groups.MonomerGroup;
import ambit2.core.groups.MultipleGroup;
import ambit2.core.groups.StructureRepeatingUnit;
import ambit2.core.groups.SuppleAtomContainer;
import ambit2.core.io.MDLV2000Reader;
import ambit2.core.io.SGroupMDL2000Helper.SGROUP_CONNECTIVITY;

public class MDLV2000ReaderTest extends TestCase {

	protected IChemObject readSGroup(String file) throws Exception {
		return readSGroup("data/mdl/", file);
	}	
	protected IChemObject readSGroup(String dir,String file) throws Exception {
		MDLV2000Reader reader = new MDLV2000Reader(new FileInputStream(dir+file),IChemObjectReader.Mode.RELAXED);
		IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
		IChemObject newMol = reader.read(mol);
		reader.close();
		return newMol;
	}
	public void testAbbreviation() throws Exception {
        IChemObject mol = readSGroup("abbreviation.mol");
		assertNotNull(mol);
		assertTrue(mol instanceof SuppleAtomContainer);
        SuppleAtomContainer sca = (SuppleAtomContainer) mol;
        sca.setFiltered(false);
        assertEquals(10,sca.getBondCount());
        assertEquals(10,sca.getAtomCount());
        List<ISGroup> superatom = getGroup(sca);
        assertNotNull(superatom);
        assertEquals(1,superatom.size());
        assertNotNull(superatom.get(0));
		sca.setFiltered(true);

        verify((IAtomContainer)mol, superatom, false,7,7,false);
        verify((IAtomContainer)mol, superatom, true,9,9,false);
	}
    protected List<ISGroup> getGroup(IAtomContainer sca) {
        List<ISGroup> sgroup = new ArrayList<ISGroup>();
        for (int i=0; i < sca.getAtomCount(); i++) {
            if (sca.getAtom(i) instanceof ISGroup) {
                sgroup.add((ISGroup) sca.getAtom(i));
            }    
            sca.getAtom(i).setID(Integer.toString(i+1));
        }      
        return sgroup;
    }
    protected void verify(IAtomContainer mol, List<ISGroup> superatom, boolean expanded, int atoms, int bonds, boolean display) throws Exception {
        for (int i=0; i < superatom.size();i++)
        if (superatom.get(i) instanceof IExpandable)
            ((IExpandable)superatom.get(i)).setExpanded(expanded);
        //print(mol);
        assertEquals(atoms,mol.getAtomCount());
        assertEquals(bonds,mol.getBondCount());
        if (display)
        display(mol,superatom.getClass().getName() + " [ expanded is "+expanded + "]");
                
    }
    protected void display(IAtomContainer mol, String title) {
        /*
        Panel2D p = new Panel2D();
        p.setAtomContainer(mol, false);
        //p.getRenderer2DModel().setDrawNumbers(true);
        p.setPreferredSize(new Dimension(400,400));
        JOptionPane.showMessageDialog(null,p,title, JOptionPane.PLAIN_MESSAGE);
        */        
    }
    protected void print(IAtomContainer sca) {
        for (int i=0; i < sca.getAtomCount(); i++) 
            System.out.println((i+1)+"."+sca.getAtom(i).getSymbol() + "["+sca.getAtom(i).getID()+"]");

        for (int i=0; i < sca.getBondCount(); i++) 
            System.out.println((i+1)+"."+sca.getBond(i).getAtom(0).getID() + " "+sca.getBond(i).getAtom(1).getID());        
    }
	public void testCopolymer() throws Exception {
        IChemObject mol = readSGroup("copolymer.mol");
        assertNotNull(mol);
        assertTrue(mol instanceof SuppleAtomContainer);
        SuppleAtomContainer sca = (SuppleAtomContainer) mol;
        sca.setFiltered(false);
        assertEquals(20,sca.getBondCount());
        assertEquals(20+2,sca.getAtomCount());
        List<ISGroup> superatom = getGroup(sca);
        assertNotNull(superatom);
        assertEquals(2,superatom.size());
        for (int i=0; i < superatom.size(); i++) {
            if (superatom.get(i) instanceof ComponentGroup) {
                assertNotNull(superatom.get(i));
                assertNull(((ComponentGroup)superatom.get(i)).getOrder());
            } else  if (superatom.get(i) instanceof ContainerGroup) {
                assertEquals(1,((ContainerGroup)superatom.get(i)).getComponents().size());
            }

        }
        sca.setFiltered(true);
        verify((IAtomContainer)mol, superatom, false,20,20,true);
        verify((IAtomContainer)mol, superatom, true,20,20,true);           
	}
	public void testGeneric() throws Exception {
        IChemObject mol = readSGroup("generic.mol");
        assertNotNull(mol);
        assertTrue(mol instanceof SuppleAtomContainer);
        SuppleAtomContainer sca = (SuppleAtomContainer) mol;
        sca.setFiltered(false);
        assertEquals(11,sca.getBondCount());
        assertEquals(19,sca.getAtomCount());
        List<ISGroup> superatom = getGroup(sca);
        assertNotNull(superatom);
        assertEquals(2,superatom.size());
        assertNotNull(superatom.get(0));
        assertNotNull(superatom.get(1));
        sca.setFiltered(true);

        verify((IAtomContainer)mol, superatom, false,17,11,false);
        verify((IAtomContainer)mol, superatom, true,17,11,false);        
	}
	public void testMixture() throws Exception {
        IChemObject mol = readSGroup("mixture_unordered.mol");
        assertNotNull(mol);
        assertTrue(mol instanceof SuppleAtomContainer);
        SuppleAtomContainer sca = (SuppleAtomContainer) mol;
        sca.setFiltered(false);
        assertEquals(11,sca.getBondCount());
        assertEquals(15+4,sca.getAtomCount());
        List<ISGroup> superatom = getGroup(sca);
        assertNotNull(superatom);
        assertEquals(4,superatom.size());
        for (int i=0; i < superatom.size(); i++) {
            if (superatom.get(i) instanceof ComponentGroup) {
                assertNotNull(superatom.get(i));
                assertNull(((ComponentGroup)superatom.get(i)).getOrder());
            } else  if (superatom.get(i) instanceof ContainerGroup) {
                assertEquals(3,((ContainerGroup)superatom.get(i)).getComponents().size());
            }

        }
        sca.setFiltered(true);
        verify((IAtomContainer)mol, superatom, false,15,11,true);
        verify((IAtomContainer)mol, superatom, true,15,11,true);          
	}
	public void testFormulation() throws Exception {
        IChemObject mol = readSGroup("mixture_ordered.mol");
        assertNotNull(mol);
        assertTrue(mol instanceof SuppleAtomContainer);
        SuppleAtomContainer sca = (SuppleAtomContainer) mol;
        sca.setFiltered(false);
        assertEquals(29,sca.getBondCount());
        assertEquals(31+3,sca.getAtomCount());
        List<ISGroup> superatom = getGroup(sca);
        assertNotNull(superatom);
        assertEquals(3,superatom.size());
        for (int i=0; i < superatom.size(); i++) {
            if (superatom.get(i) instanceof ComponentGroup) {
                assertNotNull(superatom.get(i));
                assertNotNull(((ComponentGroup)superatom.get(i)).getOrder());
                System.out.println(((ComponentGroup)superatom.get(i)).getSubscript());
            } else  if (superatom.get(i) instanceof ContainerGroup) {
                assertEquals(2,((ContainerGroup)superatom.get(i)).getComponents().size());
                assertEquals(ContainerGroup.SGROUP_MIXTURE.FORMULATION,
                        ((ContainerGroup)superatom.get(i)).getType());
                assertEquals("f",
                        ((ContainerGroup)superatom.get(i)).getSubscript());                
            }

        }
        sca.setFiltered(true);
        verify((IAtomContainer)mol, superatom, false,31,29,false);
        verify((IAtomContainer)mol, superatom, true,31,29,false);         
        
	}
	public void testMonomer() throws Exception {
        IChemObject mol = readSGroup("monomer.mol");
        assertNotNull(mol);
        assertTrue(mol instanceof SuppleAtomContainer);
        SuppleAtomContainer sca = (SuppleAtomContainer) mol;
        sca.setFiltered(false);
        assertEquals(2,sca.getBondCount());
        assertEquals(3+1,sca.getAtomCount());
        List<ISGroup> superatom = getGroup(sca);
        assertNotNull(superatom);
        assertEquals(1,superatom.size());
        assertNotNull(superatom.get(0));
        assertEquals("mon",superatom.get(0).getSubscript());
        sca.setFiltered(true);
        verify((IAtomContainer)mol, superatom, false,3,2,false);
        verify((IAtomContainer)mol, superatom, true,3,2,false);          
	}
    public void testMUL_MON2() throws Exception {
        IChemObject mol = readSGroup("12401-47-7.mol");
        assertNotNull(mol);
        assertTrue(mol instanceof SuppleAtomContainer);
        SuppleAtomContainer sca = (SuppleAtomContainer) mol;
        sca.setFiltered(false);
        assertEquals(3,sca.getBondCount());
        assertEquals(7+3,sca.getAtomCount());
        List<ISGroup> superatom = getGroup(sca);
        assertNotNull(superatom);
        assertEquals(3,superatom.size());
        int mon = 0;
        int mul = 0;
        for (int i=0; i < superatom.size();i++) {
            ISGroup g = superatom.get(i);
            if (g instanceof MonomerGroup) {
                assertEquals("mon",g.getSubscript());
                mon++;
            }
            if (g instanceof MultipleGroup) {
                mul++;
                assertEquals(SGROUP_CONNECTIVITY.HT,g.getProperty(ISGroup.SGROUP_CONNECTIVITY));
                assertEquals("2",g.getSubscript());
            }
        }
        assertEquals(1,mul);
        assertEquals(2,mon);
        
        sca.setFiltered(true);
        verify((IAtomContainer)mol, superatom, false,6,3,false);
        verify((IAtomContainer)mol, superatom, true,7,3,false);          
    }    
	public void testPolymer() throws Exception {
        IChemObject mol = readSGroup("polymer.mol");
        assertNotNull(mol);
        assertTrue(mol instanceof SuppleAtomContainer);
        SuppleAtomContainer sca = (SuppleAtomContainer) mol;
        sca.setFiltered(false);
        assertEquals(5,sca.getBondCount());
        assertEquals(6+1,sca.getAtomCount());
        List<ISGroup> superatom = getGroup(sca);
        assertNotNull(superatom);
        assertEquals(1,superatom.size());
        assertNotNull(superatom.get(0));
        sca.setFiltered(true);
        assertEquals(SGROUP_CONNECTIVITY.HT,((IChemObject)superatom.get(0)).getProperty(ISGroup.SGROUP_CONNECTIVITY));
        assertEquals("n",superatom.get(0).getSubscript());        
        verify((IAtomContainer)mol, superatom, false,6,5,false);
        verify((IAtomContainer)mol, superatom, true,6,5,false);           
	}
    /**
     * CAS 1398-61-4 Chitin
     * @throws Exception
     */
    public void testChitin() throws Exception {
        IChemObject mol = readSGroup("sru.mol");
        assertNotNull(mol);
        assertTrue(mol instanceof SuppleAtomContainer);
        SuppleAtomContainer sca = (SuppleAtomContainer) mol;
        sca.setFiltered(false);
        assertEquals(49,sca.getBondCount());
        assertEquals(47+1,sca.getAtomCount());
        List<ISGroup> superatom = getGroup(sca);
        assertNotNull(superatom);
        assertEquals(1,superatom.size());
        assertNotNull(superatom.get(0));
        sca.setFiltered(true);
        assertEquals(SGROUP_CONNECTIVITY.HT,((IChemObject)superatom.get(0)).getProperty(ISGroup.SGROUP_CONNECTIVITY));
        System.out.println(superatom.get(0).getSubscript());        
        verify((IAtomContainer)mol, superatom, false,47,49,false);
        verify((IAtomContainer)mol, superatom, true,47,49,false);
        int sru = 0;
        for (IAtom atom : superatom.get(0).getAtoms(true)) {
            sru++;
        }
        assertEquals(16,sru);
    }    
	public void testMultipleGroups() throws Exception {
        IChemObject mol = readSGroup("multiplegroups.mol");
        assertNotNull(mol);
        assertTrue(mol instanceof SuppleAtomContainer);
        SuppleAtomContainer sca = (SuppleAtomContainer) mol;
        sca.setFiltered(false);
        assertEquals(6,sca.getBondCount());
        assertEquals(10,sca.getAtomCount());
        List<ISGroup> superatom = getGroup(sca);
        assertNotNull(superatom);
        sca.setFiltered(true);

        verify((IAtomContainer)mol, superatom, false,3,2,false);
        verify((IAtomContainer)mol, superatom, true,9,6,false);        
	}
    public void testMultipleGroup_SCN() throws Exception {
        IChemObject mol = readSGroup("multiplegroups_SCN.mol");
        assertNotNull(mol);
        assertTrue(mol instanceof SuppleAtomContainer);
        SuppleAtomContainer sca = (SuppleAtomContainer) mol;
        sca.setFiltered(false);
        assertEquals(0,sca.getBondCount());
        assertEquals(4+2,sca.getAtomCount());
        List<ISGroup> superatom = getGroup(sca);
        assertNotNull(superatom);
        assertEquals(2,superatom.size());
        sca.setFiltered(true);

        verify((IAtomContainer)mol, superatom, true,4,0,false);
        verify((IAtomContainer)mol, superatom, false,2,0,false);        
    }
    public void testMultipleGroup_SAL3() throws Exception {
        IChemObject mol = readSGroup("multiplegroups_SAL3.mol");
        assertNotNull(mol);
        assertTrue(mol instanceof SuppleAtomContainer);
        SuppleAtomContainer sca = (SuppleAtomContainer) mol;
        sca.setFiltered(false);
        assertEquals(36,sca.getBondCount());
        assertEquals(37+1,sca.getAtomCount());
        List<ISGroup> superatom = getGroup(sca);
        assertNotNull(superatom);
        assertEquals(1,superatom.size());
        sca.setFiltered(true);

        verify((IAtomContainer)mol, superatom, true,37,36,false);
        verify((IAtomContainer)mol, superatom, false,13,12,false);        
    }
    public void testMultipleGroup_SAL5SPA2() throws Exception {
        IChemObject mol = readSGroup("multiplegroups_SAL5SPA2.mol");
        assertNotNull(mol);
        assertTrue(mol instanceof SuppleAtomContainer);
        SuppleAtomContainer sca = (SuppleAtomContainer) mol;
        sca.setFiltered(false);
        assertEquals(63,sca.getBondCount());
        assertEquals(67+1,sca.getAtomCount());
        List<ISGroup> superatom = getGroup(sca);
        assertNotNull(superatom);
        assertEquals(1,superatom.size());
        sca.setFiltered(true);

        verify((IAtomContainer)mol, superatom, true,67,63,false);
        verify((IAtomContainer)mol, superatom, false,23,21,false);        
    }
	public void testRGroup() throws Exception {
        IChemObject mol = readSGroup("rgroup.mol");
        assertNotNull(mol);
        assertTrue(mol instanceof SuppleAtomContainer);
        SuppleAtomContainer sca = (SuppleAtomContainer) mol;
        sca.setFiltered(false);
        assertEquals(18,sca.getBondCount());
        assertEquals(19+2,sca.getAtomCount());
        List<ISGroup> superatom = getGroup(sca);
        assertNotNull(superatom);
        assertEquals(2,superatom.size());
        int sru=0;
        int dat = 0;
        for (int i=0; i < superatom.size();i++) {
            if (superatom.get(i) instanceof StructureRepeatingUnit) {
                assertEquals("3,5-7",superatom.get(i).getSubscript());
                sru++;
            } else if (superatom.get(i) instanceof DataGroup) {
                dat++;
                assertNotNull(((DataGroup)superatom.get(i)).getMulticenter());
            }
            
        }
        assertEquals(1,sru);
        assertEquals(1,dat);
        sca.setFiltered(true);

        verify((IAtomContainer)mol, superatom, true,19,18,true);
        verify((IAtomContainer)mol, superatom, false,19,18,true);           
	}
	public void testSuperGroup() throws Exception {
        IChemObject mol = readSGroup("supergroup.mol");
        assertNotNull(mol);
        assertTrue(mol instanceof SuppleAtomContainer);
        SuppleAtomContainer sca = (SuppleAtomContainer) mol;
        sca.setFiltered(false);
        assertEquals(15,sca.getBondCount());
        assertEquals(16,sca.getAtomCount());
        List<ISGroup> superatom = getGroup(sca);
        assertNotNull(superatom);
        assertEquals(2,superatom.size());
        assertNotNull(superatom.get(0));
        assertNotNull(superatom.get(1));
        sca.setFiltered(true);
        verify((IAtomContainer)mol, superatom, false,12,11,false);
        verify((IAtomContainer)mol, superatom, true,14,13,false);
                     
	}	
	public void testSTY() throws Exception {
		File[] files = new File("data/M__STY").listFiles();
		if (files == null) throw new Exception("Files not found");
		for (File file: files) {
			MDLV2000Reader reader = new MDLV2000Reader(new FileInputStream(file));
			IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
            try {
                reader.read(mol);
            } catch (Exception x) {
                System.out.println(file);
            } finally {
                reader.close();
            }
		}
	}
	public void testRGP() throws Exception {
		File[] files = new File("data/M__RGP").listFiles();
		if (files == null) throw new Exception("Files not found");
		for (File file: files) 
			try {
				MDLV2000Reader reader = new MDLV2000Reader(new FileInputStream(file));
				IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
				reader.read(mol);
				reader.close();
			} catch (Exception x) {
				System.err.println(file.getName());
				x.printStackTrace();
				//throw new Exception(file.getName(),x);
			}
	}	
	public void testRGFile() throws Exception {
        IChemObject mol = readSGroup("data/M__RGP/","marvin_sketch.mol");
	}		
}
