/*
 * Created on 2006-3-4
 *
 */
package ambit2.test.ui;

import java.awt.Dimension;

import javax.swing.JOptionPane;

import junit.framework.TestCase;

import org.jfree.report.JFreeReportBoot;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.config.AmbitCONSTANTS;
import ambit2.ui.data.CompoundsListPanel;
import ambit2.ui.data.CompoundsListReport;
import ambit2.data.molecule.Compound;
import ambit2.data.molecule.CompoundsList;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-3-4
 */
public class MoleculeListPanelTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(MoleculeListPanelTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Constructor for MoleculeListPanelTest.
     * @param arg0
     */
    public MoleculeListPanelTest(String arg0) {
        super(arg0);
    }

    public void testCompoundsListInTable() {
        CompoundsList list = createTestList();  
        CompoundsListPanel scrollPane = new CompoundsListPanel(list,3,new Dimension(150,150));
        //scrollPane.setPreferredSize(new Dimension(600,450));
        JOptionPane.showMessageDialog(null,scrollPane,"Structures",JOptionPane.PLAIN_MESSAGE,null);

    }
    protected CompoundsList createTestList() {
        CompoundsList list = new CompoundsList();
        IMolecule mol;
        for (int i=0; i < 3; i++) {
	        mol = MoleculeFactory.makeBenzene();
	        mol.setProperty("Name","benzene");
	        mol.setProperty(AmbitCONSTANTS.SMILES,"c1ccccc1");
	        list.addItem(new Compound(mol));
	        list.addItem(new Compound(MoleculeFactory.makeAlkane(5*(i+1))));
	        mol = MoleculeFactory.makeBiphenyl();
	        mol.setProperty(CDKConstants.NAMES,"biphenyl");
	        list.addItem(new Compound(mol));
	        
	        list.addItem(new Compound(MoleculeFactory.makeBranchedAliphatic()));
	        Compound c = new Compound(MoleculeFactory.makeDiamantane());
	        c.setName("Diamantane");
	        list.addItem(c);
        }
        return list;
    }
    
    public void testReportList() {
        JFreeReportBoot.getInstance().start();
        
        CompoundsListReport tr = new CompoundsListReport(createTestList(),"Structures");
        tr.executeReportList();
        tr.executeReportGrid();

    }
    
}



//SwingIconsDemo