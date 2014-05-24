package ambit2.model.numeric;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import Jama.Matrix;
import ambit2.core.io.MyIteratingMDLReader;
import ambit2.model.numeric.distance.DataCoverageDistanceEuclidean;

public class DataCoverageDistanceTest {

	@Test
	public void testNoPCA() throws Exception {
		DataCoverageDistanceEuclidean distance = new DataCoverageDistanceEuclidean();
		//distance.build()
		Matrix data = read("ambit2/model/numeric/test/AD_problem_apps_dataset_603204.sdf");
		data.print(10,3);
		distance.setPca(false);
		distance.build(data);
	}
	
	@Test
	public void testWithPCA() throws Exception {
		DataCoverageDistanceEuclidean distance = new DataCoverageDistanceEuclidean();
		//distance.build()
		Matrix data = read("ambit2/model/numeric/test/AD_problem_apps_dataset_603204.sdf");
		data.print(10,3);
		distance.setPca(true);
		
		try {
			distance.build(data);
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
	
	public Matrix read(String file) throws Exception {
		
		URL url = getClass().getClassLoader().getResource(file);
		InputStream in = new FileInputStream(new File(url.getFile()));
		MyIteratingMDLReader reader = new MyIteratingMDLReader(in, SilentChemObjectBuilder.getInstance());
		List<String> header = null;
		int n = 0;
		
		while (reader.hasNext()) {
			Object object = reader.next();
			if (object instanceof IAtomContainer) {
				IAtomContainer ac = ((IAtomContainer) object);
				if (header==null) {
					header = new ArrayList<String>();
					Iterator keys = ac.getProperties().keySet().iterator();
					while (keys.hasNext()) {
						Object key = keys.next();
						if (CDKConstants.TITLE.equals(key)) continue;
						header.add(key.toString());
					}
				}
				n++;
			}
		}
		reader.close();
		in.close();
		Assert.assertEquals(70,n);
		Assert.assertEquals(16,header.size());
		//once again
		in = new FileInputStream(new File(url.getFile()));
		reader = new MyIteratingMDLReader(in, SilentChemObjectBuilder.getInstance());

		Matrix matrix = new Matrix(n,header.size());
		n = 0;
		while (reader.hasNext()) {
			Object object = reader.next();
			if (object instanceof IAtomContainer) {
				IAtomContainer ac = ((IAtomContainer) object);
				for (int i=0; i < header.size(); i++) {
					Object v = ac.getProperty(header.get(i));
					if (v!=null)
						matrix.set(n,i,Double.parseDouble(v.toString()));
					else 
						matrix.set(n,i,Double.NaN);
				}
				n++;
			}
		}
		reader.close();
		return matrix;
	}
}
