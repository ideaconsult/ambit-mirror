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
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.core.data.MoleculeTools;
import ambit2.core.groups.ComponentGroup;
import ambit2.core.groups.ContainerGroup;
import ambit2.core.groups.DataGroup;
import ambit2.core.groups.IExpandable;
import ambit2.core.groups.ISGroup;
import ambit2.core.groups.MonomerGroup;
import ambit2.core.groups.MultipleGroup;
import ambit2.core.groups.StructureRepeatingUnit;
import ambit2.core.groups.SuppleAtomContainer;
import ambit2.core.io.FileInputState;
import ambit2.core.io.MDLV2000ReaderExtended;
import ambit2.core.io.MyIteratingMDLReader;
import ambit2.core.io.SGroupMDL2000Helper.SGROUP_CONNECTIVITY;

public class MDLV2000ReaderExtendedTest  {
	protected static Logger logger = Logger.getLogger(MDLV2000ReaderExtendedTest.class.getName());
	protected IChemObject readSGroup(String file) throws Exception {
		return readSGroup("ambit2/core/data/mdl/", file);
	}	
	protected IChemObject readSGroup(String dir,String file) throws Exception {
		
		MDLV2000ReaderExtended reader = new MDLV2000ReaderExtended(
				MDLV2000ReaderExtended.class.getClassLoader().getResourceAsStream(dir+file),
				IChemObjectReader.Mode.RELAXED);
		IMolecule mol = MoleculeTools.newMolecule(SilentChemObjectBuilder.getInstance());
		IChemObject newMol = reader.read(mol);
		reader.close();
		return newMol;
	}
	@Test
	public void testAnyAtom() throws Exception {
        IChemObject mol = readSGroup("A.mol");
		Assert.assertNotNull(mol);
		Assert.assertTrue(mol instanceof IMolecule);
      
        
	}
	@Test
	public void testAbbreviation() throws Exception {
        IChemObject mol = readSGroup("abbreviation.mol");
		Assert.assertNotNull(mol);
		Assert.assertTrue(mol instanceof SuppleAtomContainer);
        SuppleAtomContainer sca = (SuppleAtomContainer) mol;
        sca.setFiltered(false);
        Assert.assertEquals(10,sca.getBondCount());
        Assert.assertEquals(10,sca.getAtomCount());
        List<ISGroup> superatom = getGroup(sca);
        Assert.assertNotNull(superatom);
        Assert.assertEquals(1,superatom.size());
        Assert.assertNotNull(superatom.get(0));
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
        Assert.assertEquals(atoms,mol.getAtomCount());
        Assert.assertEquals(bonds,mol.getBondCount());
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
    @Test
	public void testCopolymer() throws Exception {
        IChemObject mol = readSGroup("copolymer.mol");
        Assert.assertNotNull(mol);
        Assert.assertTrue(mol instanceof SuppleAtomContainer);
        SuppleAtomContainer sca = (SuppleAtomContainer) mol;
        sca.setFiltered(false);
        Assert.assertEquals(20,sca.getBondCount());
        Assert.assertEquals(20+2,sca.getAtomCount());
        List<ISGroup> superatom = getGroup(sca);
        Assert.assertNotNull(superatom);
        Assert.assertEquals(2,superatom.size());
        for (int i=0; i < superatom.size(); i++) {
            if (superatom.get(i) instanceof ComponentGroup) {
                Assert.assertNotNull(superatom.get(i));
                Assert.assertNull(((ComponentGroup)superatom.get(i)).getOrder());
            } else  if (superatom.get(i) instanceof ContainerGroup) {
                Assert.assertEquals(1,((ContainerGroup)superatom.get(i)).getComponents().size());
            }

        }
        sca.setFiltered(true);
        verify((IAtomContainer)mol, superatom, false,20,20,true);
        verify((IAtomContainer)mol, superatom, true,20,20,true);           
	}
    @Test
	public void testGeneric() throws Exception {
        IChemObject mol = readSGroup("generic.mol");
        Assert.assertNotNull(mol);
        Assert.assertTrue(mol instanceof SuppleAtomContainer);
        SuppleAtomContainer sca = (SuppleAtomContainer) mol;
        sca.setFiltered(false);
        Assert.assertEquals(11,sca.getBondCount());
        Assert.assertEquals(19,sca.getAtomCount());
        List<ISGroup> superatom = getGroup(sca);
        Assert.assertNotNull(superatom);
        Assert.assertEquals(2,superatom.size());
        Assert.assertNotNull(superatom.get(0));
        Assert.assertNotNull(superatom.get(1));
        sca.setFiltered(true);

        verify((IAtomContainer)mol, superatom, false,17,11,false);
        verify((IAtomContainer)mol, superatom, true,17,11,false);        
	}
    @Test
	public void testMixture() throws Exception {
        IChemObject mol = readSGroup("mixture_unordered.mol");
        Assert.assertNotNull(mol);
        Assert.assertTrue(mol instanceof SuppleAtomContainer);
        SuppleAtomContainer sca = (SuppleAtomContainer) mol;
        sca.setFiltered(false);
        Assert.assertEquals(11,sca.getBondCount());
        Assert.assertEquals(15+4,sca.getAtomCount());
        List<ISGroup> superatom = getGroup(sca);
        Assert.assertNotNull(superatom);
        Assert.assertEquals(4,superatom.size());
        for (int i=0; i < superatom.size(); i++) {
            if (superatom.get(i) instanceof ComponentGroup) {
                Assert.assertNotNull(superatom.get(i));
                Assert.assertNull(((ComponentGroup)superatom.get(i)).getOrder());
            } else  if (superatom.get(i) instanceof ContainerGroup) {
                Assert.assertEquals(3,((ContainerGroup)superatom.get(i)).getComponents().size());
            }

        }
        sca.setFiltered(true);
        verify((IAtomContainer)mol, superatom, false,15,11,true);
        verify((IAtomContainer)mol, superatom, true,15,11,true);          
	}
    @Test
	public void testFormulation() throws Exception {
        IChemObject mol = readSGroup("mixture_ordered.mol");
        Assert.assertNotNull(mol);
        Assert.assertTrue(mol instanceof SuppleAtomContainer);
        SuppleAtomContainer sca = (SuppleAtomContainer) mol;
        sca.setFiltered(false);
        Assert.assertEquals(29,sca.getBondCount());
        Assert.assertEquals(31+3,sca.getAtomCount());
        List<ISGroup> superatom = getGroup(sca);
        Assert.assertNotNull(superatom);
        Assert.assertEquals(3,superatom.size());
        for (int i=0; i < superatom.size(); i++) {
            if (superatom.get(i) instanceof ComponentGroup) {
                Assert.assertNotNull(superatom.get(i));
                Assert.assertNotNull(((ComponentGroup)superatom.get(i)).getOrder());
                System.out.println(((ComponentGroup)superatom.get(i)).getSubscript());
            } else  if (superatom.get(i) instanceof ContainerGroup) {
                Assert.assertEquals(2,((ContainerGroup)superatom.get(i)).getComponents().size());
                Assert.assertEquals(ContainerGroup.SGROUP_MIXTURE.FORMULATION,
                        ((ContainerGroup)superatom.get(i)).getType());
                Assert.assertEquals("f",
                        ((ContainerGroup)superatom.get(i)).getSubscript());                
            }

        }
        sca.setFiltered(true);
        verify((IAtomContainer)mol, superatom, false,31,29,false);
        verify((IAtomContainer)mol, superatom, true,31,29,false);         
        
	}
    @Test
	public void testMonomer() throws Exception {
        IChemObject mol = readSGroup("monomer.mol");
        Assert.assertNotNull(mol);
        Assert.assertTrue(mol instanceof SuppleAtomContainer);
        SuppleAtomContainer sca = (SuppleAtomContainer) mol;
        sca.setFiltered(false);
        Assert.assertEquals(2,sca.getBondCount());
        Assert.assertEquals(3+1,sca.getAtomCount());
        List<ISGroup> superatom = getGroup(sca);
        Assert.assertNotNull(superatom);
        Assert.assertEquals(1,superatom.size());
        Assert.assertNotNull(superatom.get(0));
        Assert.assertEquals("mon",superatom.get(0).getSubscript());
        sca.setFiltered(true);
        verify((IAtomContainer)mol, superatom, false,3,2,false);
        verify((IAtomContainer)mol, superatom, true,3,2,false);          
	}
    @Test
    public void testMUL_MON2() throws Exception {
        IChemObject mol = readSGroup("12401-47-7.mol");
        Assert.assertNotNull(mol);
        Assert.assertTrue(mol instanceof SuppleAtomContainer);
        SuppleAtomContainer sca = (SuppleAtomContainer) mol;
        sca.setFiltered(false);
        Assert.assertEquals(3,sca.getBondCount());
        Assert.assertEquals(7+3,sca.getAtomCount());
        List<ISGroup> superatom = getGroup(sca);
        Assert.assertNotNull(superatom);
        Assert.assertEquals(3,superatom.size());
        int mon = 0;
        int mul = 0;
        for (int i=0; i < superatom.size();i++) {
            ISGroup g = superatom.get(i);
            if (g instanceof MonomerGroup) {
                Assert.assertEquals("mon",g.getSubscript());
                mon++;
            }
            if (g instanceof MultipleGroup) {
                mul++;
                Assert.assertEquals(SGROUP_CONNECTIVITY.HT,g.getProperty(ISGroup.SGROUP_CONNECTIVITY));
                Assert.assertEquals("2",g.getSubscript());
            }
        }
        Assert.assertEquals(1,mul);
        Assert.assertEquals(2,mon);
        
        sca.setFiltered(true);
        verify((IAtomContainer)mol, superatom, false,6,3,false);
        verify((IAtomContainer)mol, superatom, true,7,3,false);          
    }   
    @Test
	public void testPolymer() throws Exception {
        IChemObject mol = readSGroup("polymer.mol");
        Assert.assertNotNull(mol);
        Assert.assertTrue(mol instanceof SuppleAtomContainer);
        SuppleAtomContainer sca = (SuppleAtomContainer) mol;
        sca.setFiltered(false);
        Assert.assertEquals(5,sca.getBondCount());
        Assert.assertEquals(6+1,sca.getAtomCount());
        List<ISGroup> superatom = getGroup(sca);
        Assert.assertNotNull(superatom);
        Assert.assertEquals(1,superatom.size());
        Assert.assertNotNull(superatom.get(0));
        sca.setFiltered(true);
        Assert.assertEquals(SGROUP_CONNECTIVITY.HT,((IChemObject)superatom.get(0)).getProperty(ISGroup.SGROUP_CONNECTIVITY));
        Assert.assertEquals("n",superatom.get(0).getSubscript());        
        verify((IAtomContainer)mol, superatom, false,6,5,false);
        verify((IAtomContainer)mol, superatom, true,6,5,false);           
	}
    /**
     * CAS 1398-61-4 Chitin
     * @throws Exception
     */
    @Test
    public void testChitin() throws Exception {
        IChemObject mol = readSGroup("sru.mol");
        Assert.assertNotNull(mol);
        Assert.assertTrue(mol instanceof SuppleAtomContainer);
        SuppleAtomContainer sca = (SuppleAtomContainer) mol;
        sca.setFiltered(false);
        Assert.assertEquals(49,sca.getBondCount());
        Assert.assertEquals(47+1,sca.getAtomCount());

        List<ISGroup> superatom = getGroup(sca);
        Assert.assertNotNull(superatom);
        Assert.assertEquals(1,superatom.size());
        Assert.assertNotNull(superatom.get(0));
        sca.setFiltered(true);
        Assert.assertEquals(SGROUP_CONNECTIVITY.HT,((IChemObject)superatom.get(0)).getProperty(ISGroup.SGROUP_CONNECTIVITY));
        logger.fine(superatom.get(0).getSubscript());        
        verify((IAtomContainer)mol, superatom, false,47,49,false);
        verify((IAtomContainer)mol, superatom, true,47,49,false);
        int sru = 0;
        for (IAtom atom : superatom.get(0).getAtoms(true)) {
            sru++;
        }
        Assert.assertEquals(16,sru);
    }    
    @Test
	public void testMultipleGroups() throws Exception {
        IChemObject mol = readSGroup("multiplegroups.mol");
        Assert.assertNotNull(mol);
        Assert.assertTrue(mol instanceof SuppleAtomContainer);
        SuppleAtomContainer sca = (SuppleAtomContainer) mol;
        sca.setFiltered(false);
        Assert.assertEquals(6,sca.getBondCount());
        Assert.assertEquals(10,sca.getAtomCount());
        List<ISGroup> superatom = getGroup(sca);
        Assert.assertNotNull(superatom);
        sca.setFiltered(true);

        verify((IAtomContainer)mol, superatom, false,3,2,false);
        verify((IAtomContainer)mol, superatom, true,9,6,false);        
	}
    @Test
    public void testMultipleGroup_SCN() throws Exception {
        IChemObject mol = readSGroup("multiplegroups_SCN.mol");
        Assert.assertNotNull(mol);
        Assert.assertTrue(mol instanceof SuppleAtomContainer);
        SuppleAtomContainer sca = (SuppleAtomContainer) mol;
        sca.setFiltered(false);
        Assert.assertEquals(0,sca.getBondCount());
        Assert.assertEquals(4+2,sca.getAtomCount());
        List<ISGroup> superatom = getGroup(sca);
        Assert.assertNotNull(superatom);
        Assert.assertEquals(2,superatom.size());
        sca.setFiltered(true);

        verify((IAtomContainer)mol, superatom, true,4,0,false);
        verify((IAtomContainer)mol, superatom, false,2,0,false);        
    }
    @Test
    public void testMultipleGroup_SAL3() throws Exception {
        IChemObject mol = readSGroup("multiplegroups_SAL3.mol");
        Assert.assertNotNull(mol);
        Assert.assertTrue(mol instanceof SuppleAtomContainer);
        SuppleAtomContainer sca = (SuppleAtomContainer) mol;
        sca.setFiltered(false);
        Assert.assertEquals(36,sca.getBondCount());
        Assert.assertEquals(37+1,sca.getAtomCount());
        List<ISGroup> superatom = getGroup(sca);
        Assert.assertNotNull(superatom);
        Assert.assertEquals(1,superatom.size());
        sca.setFiltered(true);

        verify((IAtomContainer)mol, superatom, true,37,36,false);
        verify((IAtomContainer)mol, superatom, false,13,12,false);        
    }
    @Test
    public void testMultipleGroup_SAL5SPA2() throws Exception {
        IChemObject mol = readSGroup("multiplegroups_SAL5SPA2.mol");
        Assert.assertNotNull(mol);
        Assert.assertTrue(mol instanceof SuppleAtomContainer);
        SuppleAtomContainer sca = (SuppleAtomContainer) mol;
        sca.setFiltered(false);
        Assert.assertEquals(63,sca.getBondCount());
        Assert.assertEquals(67+1,sca.getAtomCount());
        List<ISGroup> superatom = getGroup(sca);
        Assert.assertNotNull(superatom);
        Assert.assertEquals(1,superatom.size());
        sca.setFiltered(true);

        verify((IAtomContainer)mol, superatom, true,67,63,false);
        verify((IAtomContainer)mol, superatom, false,23,21,false);        
    }
    @Test
	public void testRGroup() throws Exception {
        IChemObject mol = readSGroup("rgroup.mol");
        Assert.assertNotNull(mol);
        Assert.assertTrue(mol instanceof SuppleAtomContainer);
        SuppleAtomContainer sca = (SuppleAtomContainer) mol;
        sca.setFiltered(false);
        Assert.assertEquals(18,sca.getBondCount());
        Assert.assertEquals(19+2,sca.getAtomCount());
        List<ISGroup> superatom = getGroup(sca);
        Assert.assertNotNull(superatom);
        Assert.assertEquals(2,superatom.size());
        int sru=0;
        int dat = 0;
        for (int i=0; i < superatom.size();i++) {
            if (superatom.get(i) instanceof StructureRepeatingUnit) {
                Assert.assertEquals("3,5-7",superatom.get(i).getSubscript());
                sru++;
            } else if (superatom.get(i) instanceof DataGroup) {
                dat++;
                Assert.assertNotNull(((DataGroup)superatom.get(i)).getMulticenter());
            }
            
        }
        Assert.assertEquals(1,sru);
        Assert.assertEquals(1,dat);
        sca.setFiltered(true);

        verify((IAtomContainer)mol, superatom, true,19,18,true);
        verify((IAtomContainer)mol, superatom, false,19,18,true);           
	}
	public void testSuperGroup() throws Exception {
        IChemObject mol = readSGroup("supergroup.mol");
        Assert.assertNotNull(mol);
        Assert.assertTrue(mol instanceof SuppleAtomContainer);
        SuppleAtomContainer sca = (SuppleAtomContainer) mol;
        sca.setFiltered(false);
        Assert.assertEquals(15,sca.getBondCount());
        Assert.assertEquals(16,sca.getAtomCount());
        List<ISGroup> superatom = getGroup(sca);
        Assert.assertNotNull(superatom);
        Assert.assertEquals(2,superatom.size());
        Assert.assertNotNull(superatom.get(0));
        Assert.assertNotNull(superatom.get(1));
        sca.setFiltered(true);
        verify((IAtomContainer)mol, superatom, false,12,11,false);
        verify((IAtomContainer)mol, superatom, true,14,13,false);
                     
	}	
	@Test
	public void testSTY() throws Exception {
		String dir = getClass().getClassLoader().getResource("ambit2/core/data/M__STY").getFile();
		File[] files = new File(dir).listFiles();
		if (files == null) throw new Exception("Files not found");
		int count = 0;
		for (File file: files) {
			MyIteratingMDLReader reader = new MyIteratingMDLReader(new FileInputStream(file),SilentChemObjectBuilder.getInstance());
            try {
            	int records = 0;
            	while (reader.hasNext()) {
            		
            		Object a = reader.next();
            		Object title = ((IChemObject)a).getProperty(CDKConstants.TITLE);
            		records++;
            		if ("Rgroup bad way".equals(title) || ("Radicals".equals(title))) {
            			//IMolecule
            		} else Assert.assertTrue(a instanceof SuppleAtomContainer);
            	}
            	if ("sgroup.sdf".equals(file.getName()))
            			Assert.assertEquals(23,records);
            	else
            		Assert.assertEquals(1,records);
                count++;
            } catch (Exception x) {
                throw x;
            } finally {
                reader.close();
            }
		}
		Assert.assertEquals(1,count);
	}
	@Test
	public void testRGP() throws Exception {
		File[] files = new File("src/test/resources/ambit2/core/data/M__RGP").listFiles();
		if (files == null) throw new Exception("Files not found");
		for (File file: files) 
			try {
				MDLV2000ReaderExtended reader = new MDLV2000ReaderExtended(new FileInputStream(file));
				IMolecule mol = MoleculeTools.newMolecule(SilentChemObjectBuilder.getInstance());
				reader.read(mol);
				reader.close();
			} catch (Exception x) {
				System.err.println(file.getName());
				x.printStackTrace();
				//throw new Exception(file.getName(),x);
			}
	}	
	@Test
	public void testRGFile() throws Exception {
        IChemObject mol = readSGroup("ambit2/core/data/M__RGP/","marvin_sketch.mol");
	}		
	@Test
	public void testMol_without_Mend() throws Exception {
		IIteratingChemObjectReader reader = 
			 FileInputState.getReader(getClass().getClassLoader().getResourceAsStream("ambit2/core/data/mdl/test_no_mend.mol"),
					 "test_no_mend.mol");
		int count = 0;
		while (reader.hasNext()) {
			Object o = reader.next();
			Assert.assertTrue(o instanceof IAtomContainer);
			Assert.assertEquals(23,((IAtomContainer)o).getAtomCount());
			count++;
		}
		reader.close();
		Assert.assertEquals(1,count);
	}		
	
}
