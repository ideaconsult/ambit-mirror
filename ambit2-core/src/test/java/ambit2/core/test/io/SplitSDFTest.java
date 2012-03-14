package ambit2.core.test.io;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.RawIteratingSDFReader;

public class SplitSDFTest {
	
	protected String getProperty(String content, String tag) {
		int casIndex = content.indexOf(tag);
		
		if (casIndex>0) {
			String cas = content.substring(casIndex).replace(tag, "").trim();
			int nextLine = cas.indexOf("> <"); 
			if (nextLine>0) 
				return cas.substring(0,nextLine).trim();
		}
		return null;
	}
	public int split(File file) throws Exception {
		FileReader reader= new FileReader(file);
		RawIteratingSDFReader iterator = new RawIteratingSDFReader(reader);
		int recordNo = 0;
		String molFile = file.getName();
		final String ECTAG = "> <EC>";
		final String CASTAG = "> <CasRN>";
		final String NAMETAG = "> <Substance Name>";
		final String inchitag = "> <http://www.opentox.org/api/1.1#InChIKey_std>";
		try {
			while (iterator.hasNext()) {
				IStructureRecord record = iterator.nextRecord();
				recordNo++;
				molFile = file.getName().replace(".sdf", String.format("_%d.mol",recordNo));
				
				String name = getProperty(record.getContent(),NAMETAG);
				String inchi = getProperty(record.getContent(),inchitag);
				if (inchi != null) System.out.println(inchi);
				String cas = getProperty(record.getContent(),CASTAG);
				String ec = getProperty(record.getContent(),ECTAG);
				String title = null;
				if (cas != null) { title = cas.trim(); molFile = String.format("CAS_%s.mol",title.trim()); 	}
				else if (ec != null) { title = ec.trim();  molFile = String.format("EC_%s.mol",title.trim()); 	}
				else if (inchi != null) { title = inchi.trim();  molFile = String.format("%s.mol",title.trim()); 	}	
				else if (name != null) { title = name.trim(); }
				else {
					System.out.println(record.getContent());
				}
				if (title != null)  {
					//replace first line
					int nameindex = record.getContent().indexOf("\n");
					if (nameindex>=0)
					record.setContent(String.format("%s\n%s",title.trim(),record.getContent().substring(nameindex+1)));
				}
				int mend = record.getContent().indexOf("M  END\n");
				if (mend>0) {
					FileWriter writer = new FileWriter(String.format("%s/%s",file.getParent()==null?"":file.getParent(),molFile));
					writer.write(record.getContent().substring(0,mend+7));
					writer.flush();
					writer.close();
				}
			//	if (recordNo > 3) break;
			}
		} catch (Exception x) {
			x.printStackTrace();
		} finally {
			reader.close();
		}
		return recordNo;
	}
	public static void main(String[] args) {
		if (args.length<1) System.err.println(String.format("%s sdf_file_name",SplitSDFTest.class.getName()));
		else {
			SplitSDFTest split=new SplitSDFTest();
			try {
				System.out.println(split.split(new File(args[0])));
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
	}
}
