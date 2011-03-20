package ambit2.namestructure;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import nu.xom.Serializer;
import uk.ac.cam.ch.wwmm.opsin.OpsinResult;
import uk.ac.cam.ch.wwmm.opsin.OpsinResult.OPSIN_RESULT_STATUS;
import ambit2.base.data.StructureRecord;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.search.AbstractFinder;

public class Name2StructureFinder extends AbstractFinder<Name2StructureProcessor, IStructureRecord>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3159036024126147810L;
	protected IStructureRecord record = new StructureRecord();
	
	public Name2StructureFinder(Template profile,MODE mode) {
		super(profile, new Name2StructureProcessor(), mode);
	}

	@Override
	protected IStructureRecord query(String value) throws AmbitException {
		OpsinResult result = request.name2structure(value);
		record.clear();
		if (!result.getStatus().equals(OPSIN_RESULT_STATUS.SUCCESS)) return record;
		
		   OutputStream out = new OutputStream() {
			   private StringBuilder string = new StringBuilder();
		        @Override
		        public void write(int b) throws IOException {
		            this.string.append((char) b );
		        }
		        public String toString(){
		            return this.string.toString();
		        }

		   };
		try {

		       //Serializer serializer = 
			   new Serializer(System.out, "ISO-8859-1");
		       Serializer serializer = 
			   new Serializer(out,"ISO-8859-1");
		       serializer.setIndent(5);
		       serializer.setMaxLength(100);
		       serializer.write(result.getCml().getDocument());  
		       } catch (IOException e) {
		       } finally {
		    	   try {out.close();} catch (Exception x) {}
		       }	   
		
		record.setContent(out.toString());
		record.setFormat("CML");
		return record;
	}

}
