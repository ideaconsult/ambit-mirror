/*
Copyright (C) 2005-2006  

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

package ambit.taglibs;

import java.io.File;

import junit.framework.TestCase;
import ambit.data.literature.ReferenceFactory;
import ambit.data.molecule.SourceDataset;


public class DbImportBatchTest  extends TestCase {
	public void test() {
		try {
			
			DBImportBatch.import2DB(new File("data/benzene.sdf"),
					new SourceDataset("test",ReferenceFactory.createDatasetReference("test","")),
					"localhost",
					"3306",
					"ambit_qmrf",
					"lri",
					"lriadmin");
			
		} catch (Exception x) {
			x.printStackTrace();
			fail(x.getMessage());
		}
	}
	public void testXML() {
		try {
			StringBuffer b = new StringBuffer();
			b.append("<molecules embedded=\"No\" filetype=\"data_validation\" url=\"/var/lib/qmrf/BCF_Grammatica_SMILES.xls\" description=\"BCF\" dataset=\"/var/lib/qmrf/BCF_Grammatica_SMILES.xls\">");
			b.append("<property fieldname=\"CAS\" fieldtype=\"CasRN\" newname=\"CAS\"/>");
			b.append("<property fieldname=\"SMILES\" fieldtype=\"SMILES\" newname=\"SMILES\"/>");
			b.append("<property fieldname=\"Chemical\" fieldtype=\"ignore\" newname=\"Chemical\"/>");
			b.append("<property fieldname=\"GATS2e\" fieldtype=\"ignore\" newname=\"GATS2e\"/>");
			b.append("<property fieldname=\"H6p\" fieldtype=\"ignore\" newname=\"H6p\"/>");
			b.append("<property fieldname=\"ID\" fieldtype=\"ignore\" newname=\"ID\"/>");
			b.append("<property fieldname=\"MATS2m\" fieldtype=\"ignore\" newname=\"MATS2m\"/>");
			b.append("<property fieldname=\"nHAcc\" fieldtype=\"ignore\" newname=\"nHAcc\"/>");
			b.append("<property fieldname=\"VIM_D_deg\" fieldtype=\"ignore\" newname=\"VIM_D_deg\"/>");
			b.append("<property fieldname=\"Y_Exp.\" fieldtype=\"ignore\" newname=\"Y_Exp.\"/>");
			b.append("<property fieldname=\"Y_Pred._BCFWIN\" fieldtype=\"ignore\" newname=\"Y_Pred._BCFWIN\"/>");
			b.append("<property fieldname=\"Y_Pred._MCI\" fieldtype=\"ignore\" newname=\"Y_Pred._MCI\"/>");
			b.append("<property fieldname=\"Y_Pred._Mod.3\" fieldtype=\"ignore\" newname=\"Y_Pred._Mod.3\"/>");
			b.append("</molecules>");
			DBImportBatch.import2DB_xml(b.toString(),
					"localhost",
					"3306",
					"ambit_qmrf",
					"lri_admin",
					"lri");
			
		} catch (Exception x) {
			x.printStackTrace();
			fail(x.getMessage());
		}
	}	
	
}


