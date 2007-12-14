/**
 * <b>Filename</b> MoleculeFragmentsTest.java 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Created</b> 2005-7-27
 * <b>Project</b> ambit
 */
package ambit.test.data.molecule;

import junit.framework.TestCase;
import ambit.ranking.MFNode;
import ambit.ranking.MoleculeFragments;

/**
 * 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-7-27
 */
public class MoleculeFragmentsTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(MoleculeFragmentsTest.class);
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

    public void testClear() {
    }

    protected MoleculeFragments generateMF() {
        MoleculeFragments mf = new MoleculeFragments();
        mf.add("m1","f1",1);
        mf.add("m1","f1",1);        
        mf.add("m1","f2",1);
        mf.add("m1","f3",1);        
        
        mf.add("m2","f2",1);        
        mf.add("m2","f3",1);
        mf.add("m2","f4",1);        
        
        mf.add("m3","f2",1);        
        mf.add("m3","f3",2);
        mf.add("m3","f4",3);        
        
        mf.add("m4","f5",3);        
        return mf;
    }
    public void testAddFragmentForMolecule() {
        MoleculeFragments mf = generateMF();
        assertEquals(4,mf.getMoleculesCount());
        assertEquals(5,mf.getFragmentsCount());
        assertEquals(10,mf.getLinksCount(true));
        assertEquals(16,mf.getLinksCount(false));        
        
        MFNode molecule = mf.getMolecule("m1");
        assertEquals(4,molecule.getLinkCount());
        molecule = mf.getMolecule("m2");
        assertEquals(3,molecule.getLinkCount());
        molecule = mf.getMolecule("m3");
        assertEquals(6,molecule.getLinkCount());        
        molecule = mf.getMolecule("m4");
        assertEquals(3,molecule.getLinkCount());
        
        MFNode fragment = mf.getFragment("f1");
        assertEquals(2,fragment.getLinkCount());
        fragment = mf.getFragment("f2");
        assertEquals(3,fragment.getLinkCount());
        fragment = mf.getFragment("f3");
        assertEquals(4,fragment.getLinkCount());
        fragment = mf.getFragment("f4");
        assertEquals(4,fragment.getLinkCount());
        fragment = mf.getFragment("f5");
        assertEquals(3,fragment.getLinkCount());
        

        mf.clear();
        mf = null;
    }
    public void xtestupdateProbability() {
        MoleculeFragments mf = new MoleculeFragments();
        mf.add("m1","f1",1);
        mf.add("m1","f2",5);
        mf.add("m1","f3",1);        
        
        mf.add("m2","f2",3);        
        mf.add("m2","f3",1);
        mf.add("m2","f4",1);        
        
        mf.add("m3","f2",1);        
        mf.add("m3","f3",2);
        mf.add("m3","f4",1);                
        
        mf.add("m4","f5",1);   
        
        mf.add("m5","f6",1);        
        mf.add("m5","f7",1); 
        
        mf.setMaxIterations(100);
        mf.setDampening(1);
        mf.updateProbability();
        System.out.println(mf);
        
        Comparable fr[] = new Comparable[5];
        fr[0] = "f2";
        fr[1] = "f2";
        fr[2] = "f2";
        fr[3] = "f2";
        fr[4] = "f2";
     
        double pr = mf.getMoleculeProbability(fr);
        System.out.println(pr);
    }
    
    public void testupdateProbability1() {
        MoleculeFragments mf = new MoleculeFragments();
        mf.add("m1","f1",1);
        
        mf.add("m2","f2",1);        
        mf.add("m2","f4",1);        
        
        mf.add("m3","f3",1);        
        
        mf.add("m4","f4",1);   
        
        mf.add("m5","f5",1);
        mf.add("m5","f2",1);        
        
        mf.add("m6","f6",1); 
        
        mf.setMaxIterations(100);
        mf.setDampening(1);
        mf.updateProbability();
        System.out.println(mf);
        
        Comparable fr[] = new Comparable[5];
        fr[0] = "f2";
        fr[1] = "f2";
        fr[2] = "f2";
        fr[3] = "f2";
        fr[4] = "f9";
     
        double pr = mf.getMoleculeProbability(fr);
        System.out.println(pr);
    }
    
}

