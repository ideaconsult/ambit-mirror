package ambit2.core.test;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ambit2.core.filter.MoleculeFilter;

public class MoleculeFilterTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    public String getMoleculeFilter(String filterString) throws Exception {
	MoleculeFilter filter = MoleculeFilter.parseFromCommandLineString(filterString);
	return filter.toString();
    }

    @Test
    public void testMolFilter01() throws Exception {
	String fs = "NA=[3,45]";
	Assert.assertEquals("Mol. filter " + fs, fs, getMoleculeFilter(fs));
    }

    @Test
    public void testMolFilter02() throws Exception {
	String fs = "#Mol=[100,200];NB=[3,]";
	Assert.assertEquals("Mol. filter " + fs, fs, getMoleculeFilter(fs));
    }

    @Test
    public void testMolFilter03() throws Exception {
	String fs = "NA=[,45];CYCLOMATIC=2;NAromAt=0";
	Assert.assertEquals("Mol. filter " + fs, fs, getMoleculeFilter(fs));
    }

    @Test
    public void testIsAromaticityInfoNeeded01() throws Exception {
	String fs = "NA=[,45];CYCLOMATIC=2;NAromAt=0";
	MoleculeFilter filter = MoleculeFilter.parseFromCommandLineString(fs);

	Assert.assertEquals("Mol. filter " + fs + " isAromaticityInfoNeeded()", true, filter.isAromaticityInfoNeeded());
    }

    @Test
    public void testIsAromaticityInfoNeeded02() throws Exception {
	String fs = "NA=[23,450];CYCLOMATIC=3;NB=23";
	MoleculeFilter filter = MoleculeFilter.parseFromCommandLineString(fs);

	Assert.assertEquals("Mol. filter " + fs + " isAromaticityInfoNeeded()", false, filter.isAromaticityInfoNeeded());
    }

}
