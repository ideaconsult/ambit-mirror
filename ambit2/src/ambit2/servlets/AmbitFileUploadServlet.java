package ambit2.servlets;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.iterator.IteratingMDLReader;
import org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor;
import org.openscience.cdk.smiles.SmilesGenerator;

import toxTree.tree.cramer.CramerRules;
import verhaar.VerhaarScheme;
import ambit2.io.DelimitedFileFormat;
import ambit2.io.DelimitedFileWriter;
import ambit2.io.FileInputState;
import ambit2.io.FileOutputState;
import ambit2.io.HTMLTableWriter;
import ambit2.ui.editors.IdentifiersProcessor;
import ambit2.data.descriptors.DescriptorFactory;
import ambit2.data.descriptors.DescriptorsHashtable;
import ambit2.database.processors.CASSmilesLookup;
import ambit2.database.processors.FindUniqueProcessor;
import ambit2.database.processors.ReadAliasProcessor;
import ambit2.database.processors.ReadCASProcessor;
import ambit2.database.processors.ReadNameProcessor;
import ambit2.database.processors.ReadSMILESProcessor;
import ambit2.exceptions.AmbitIOException;
import ambit2.io.batch.DefaultBatchProcessing;
import ambit2.io.batch.EmptyBatchConfig;
import ambit2.processors.IAmbitProcessor;
import ambit2.processors.ProcessorsChain;
import ambit2.processors.descriptors.CalculateDescriptors;
import ambit2.processors.results.FingerprintProfile;
import ambit2.processors.structure.FingerprintGenerator;
import ambit2.processors.structure.FingerprintProfileGenerator;
import ambit2.processors.structure.SmilesGeneratorProcessor;
import ambit2.processors.toxtree.ToxTreeProcessor;

/**
 * An {@link ambit2.servlets.AmbitServlet} descendant to perform online batch processing.
 *  The users uploads a file, which is processed by various {@link ambit2.processors.IAmbitProcessor}s. <br>
 *  The result is sent back to the user as a file of specified type.
 *  Valid parameters are:
 *  caslookup - CAS -> structure lookup  {@link ambit2.database.processors.CASSmilesLookup} <br>
 *  smiles - SMILES generation {@link ambit2.processors.structure.SmilesGeneratorProcessor }<br>
 *  fingerprints - Fingerprint generation {@link ambit2.processors.structure.FingerprintGenerator}<br> 
 *  fingerprints - Fingerprint profile generation {@link ambit2.processors.structure.FingerprintProfileGenerator}<br>
 *  verhaar - Classification by Verhaar scheme  {@link ambit2.processors.toxtree.ToxTreeProcessor} with {@link verhaar.VerhaarScheme}<br>
 *  cramer - Classification by Cramer rules {@link ambit2.processors.toxtree.ToxTreeProcessor} with {@link toxTree.tree.cramer.CramerRules}<br>
 *  XlogP - XlogP calculation {@link ambit2.processors.descriptors.CalculateDescriptors} with {@link org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor}<br>
 *  readCAS - Reads CAS numbers from database {@link ambit2.database.processors.FindUniqueProcessor} and {@link ambit2.database.processors.ReadCASProcessor}<br>
 *  readSMILES - Reads SMILES from database {@link ambit2.database.processors.FindUniqueProcessor} and {@link ambit2.database.processors.ReadSMILESProcessor}<br>
 *  readNAME - Reads Chemical names from database {@link ambit2.database.processors.FindUniqueProcessor} and {@link ambit2.database.processors.ReadNameProcessor}<br>
 *  readALIAS - Reads other identifiers from database {@link ambit2.database.processors.FindUniqueProcessor} and {@link ambit2.database.processors.ReadAliasProcessor}<br>
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 29, 2006
 */
public class AmbitFileUploadServlet extends AmbitServlet {
	
	private ServletContext context = null;
	private int maxSize = 1024 * 1024 * 1; // 1MB
	private String errorPage = "Uploaderror.jsp";
	protected DescriptorsHashtable descriptors = new DescriptorsHashtable();

	public AmbitFileUploadServlet() {
		super();
		descriptors.addDescriptorPair(new XLogPDescriptor(), DescriptorFactory.createEmptyDescriptor());
		/*
		descriptors.addDescriptorPair(new ZagrebIndexDescriptor(), DescriptorFactory.createEmptyDescriptor());
		descriptors.addDescriptorPair(new WienerNumbersDescriptor(), DescriptorFactory.createEmptyDescriptor());
		descriptors.addDescriptorPair(new RuleOfFiveDescriptor(), DescriptorFactory.createEmptyDescriptor());		
		descriptors.addDescriptorPair(new RotatableBondsCountDescriptor(), DescriptorFactory.createEmptyDescriptor());
		descriptors.addDescriptorPair(new AromaticAtomsCountDescriptor(), DescriptorFactory.createEmptyDescriptor());
		descriptors.addDescriptorPair(new AromaticBondsCountDescriptor(), DescriptorFactory.createEmptyDescriptor());
		*/
	}

	protected void process(Connection connection, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.print(this.getClass().getName());
		System.out.println("\tSTART");		
		boolean asAttachment = true;
		boolean image = false;
		String queryFile = "";
		FingerprintProfile profile = null;
		
		boolean cas = false;
		boolean smiles = false;
		boolean fingerprints = false;
		boolean fingerprintsProfile = false;
		boolean calcDescriptors = false;
		boolean verhaar = false;
		boolean cramer = false;
		boolean readCAS = false;
		boolean readNAME = false;
		boolean readSMILES = false;
		boolean readALIAS = false;
		boolean find = false;
		
		try {
			//PrintWriter out = response.getWriter();
			OutputStream out = response.getOutputStream();
			
			ServletFileUpload f = new ServletFileUpload(new DiskFileItemFactory());
			List list = f.parseRequest(request);
			
			IChemObjectWriter outWriter = null;
			
			String outfile = "results.csv";
			ProcessorsChain pc = new ProcessorsChain();
			pc.add(0,new IdentifiersProcessor());
			for (int j=0; j < list.size();j++) {
				FileItem file = (FileItem) list.get(j);

				if (file != null)
				if (file.isFormField()) {
					String fieldName = file.getFieldName(); 
					
					if (fieldName.equals("format")) { 
						String outputformat = file.getString();
						try {
							outWriter = FileOutputState.getWriter(out,outputformat);
							
						} catch (AmbitIOException x) {
							outputformat = ".csv";
							outWriter = FileOutputState.getWriter(out,outputformat);
						}


						response.setContentType(FileOutputState.getResponseType(outputformat));
						if (outputformat.equals(FileOutputState.extensions[FileOutputState.SDF_INDEX]))  {
								outfile = "results.sdf";
						} else if (outputformat.equals(FileOutputState.extensions[FileOutputState.CSV_INDEX]))  {
								outfile = "results.csv";
						}  else if (outputformat.equals(FileOutputState.extensions[FileOutputState.SMI_INDEX]))  {
								outfile = "results.smi";
						}  else if (outputformat.equals(FileOutputState.extensions[FileOutputState.TXT_INDEX]))  {
								outfile = "results.txt";
						}  else if (outputformat.equals(".html"))  {
							outfile = "results.html";				
							asAttachment = false;
						}  else if (outputformat.equals(".jpg"))  {
							outfile = "results.jpg";
							asAttachment = false;
							image = true;
						}  else if (outputformat.equals(".png"))  {
							outfile = "results.png";
							asAttachment = false;
							image = true;
						} else {
							response.setContentType("application/vnd.ms-excel");
							outWriter = new DelimitedFileWriter(out,new DelimitedFileFormat(',','"'));
							outfile = "results.csv";					
						}
					} else if (fieldName.equals("caslookup")) {
						cas = true;
					} else if (fieldName.equals("smiles")) {
						smiles =true;
					} else if (fieldName.equals("fingerprints")) {
		            	fingerprints = true;     	
					} else if (fieldName.equals("fingerprintProfile")) {
						fingerprintsProfile = true;
     	
					} else if (fieldName.equals("verhaar")) {
						verhaar = true;

					} else if (fieldName.equals("cramer")) {
		            	cramer = true;
					} else if (fieldName.equals("XlogP")) {
						calcDescriptors = true;
						smiles =true;
						find = true;
					} else if (fieldName.equals("readCAS")) {
						readCAS = true;
						smiles =true;
						find = true;
					} else if (fieldName.equals("readSMILES")) {
						readSMILES = true;
						smiles =true;
						find = true;
					} else if (fieldName.equals("readNAME")) {
						readNAME = true;
						smiles =true;
						find = true;
					} else if (fieldName.equals("readALIAS")) {
						readALIAS = true;
						smiles =true;
						find = true;
						
		            	/*
					} else if (fieldName.equals("descriptors")) {
						IAmbitProcessor p = new ReadDescriptorsProcessor(
								getDescriptors(),
								connection
								);
						pc.addProcessor(5,p);
						*/
					}							
				} else {
					if (file.getFieldName().equals("queryfile")) {
						queryFile = file.getName(); 
					}	
				}
			}
			if (cas) pc.addProcessor(new CASSmilesLookup(connection,true));
        	if (smiles) pc.addProcessor(new SmilesGeneratorProcessor());
        	if (find) pc.addProcessor(new FindUniqueProcessor(connection));
        	if (readSMILES) pc.addProcessor(new ReadSMILESProcessor(connection));
        	if (readCAS) pc.addProcessor(new ReadCASProcessor(connection));
        	if (readNAME) pc.addProcessor(new ReadNameProcessor(connection));
        	if (readALIAS) pc.addProcessor(new ReadAliasProcessor(connection));
        	
        	if (verhaar ) pc.addProcessor(new ToxTreeProcessor(new VerhaarScheme()));
        	if (cramer ) pc.addProcessor(new ToxTreeProcessor(new CramerRules()));
        	if (calcDescriptors) pc.addProcessor(new CalculateDescriptors(descriptors));
        	
        	if (fingerprints) pc.addProcessor(new FingerprintGenerator());
        	if (fingerprintsProfile) {
				IAmbitProcessor processor = new FingerprintProfileGenerator();
				profile = new FingerprintProfile("Fingerprint profile ");
				processor.setResult(profile);						
            	pc.addProcessor(processor);
            	profile.setTitle(queryFile);
        	}        	
			
			boolean headersSent = false;
			for (int j=0; j < list.size();j++) {
				FileItem file = (FileItem) list.get(j);
				if ((file != null) && !file.isFormField()) {
					if (!headersSent) {
						if (asAttachment)
							response.setHeader("Content-Disposition", "attachment; filename=" + outfile);
						else
						if (image) {
							response.setContentLength(1024*50);
						}	

						
					}
					returnFile(file, outWriter,pc);
					out.close();
				}   
			}
			
		} catch (Exception x) {
			x.printStackTrace(response.getWriter());
		}
		System.out.print(this.getClass().getName());
		System.out.println("\tSTART");		
			
		
	}

	 protected void returnFile(FileItem fileItem, IChemObjectWriter writer, IAmbitProcessor processor)
     throws Exception {
		 	long time = System.currentTimeMillis();
		 	System.out.print(this.getClass().getName());
		 	System.out.println("\tSTART");
		 	System.out.println(processor.toString());
			InputStream in = fileItem.getInputStream();
			//IteratingMDLReader reader = new IteratingMDLReader(in);
			IIteratingChemObjectReader reader = FileInputState.getReader(in,fileItem.getName());
			if (reader != null) {
				
				DefaultBatchProcessing batch = new DefaultBatchProcessing(
						reader,writer,processor,new EmptyBatchConfig()) {
					public void start() throws ambit2.io.batch.BatchProcessingException {
						if (writer instanceof HTMLTableWriter)
							((HTMLTableWriter) writer).printHeader();
						setRecordsLimit(1000);
						super.start();
					};
					public void finish() throws ambit2.io.batch.BatchProcessingException {
						super.finish();
						if (writer instanceof HTMLTableWriter) {
							 HTMLTableWriter w = (HTMLTableWriter) writer;
							 //w.writeR(processor.getResult()); 
							((HTMLTableWriter) writer).printFooter();
						}	
					}
				};
				batch.setRecordsLimit(500);
				batch.start();
				
			}	
		 	System.out.print(this.getClass().getName());
		 	System.out.print("\t");
		 	System.out.print(System.currentTimeMillis()-time);
		 	System.out.println("ms\tSTOP");
	 }
	
	
   
	 protected void returnFileSimple(FileItem fileItem, OutputStream out)
     throws FileNotFoundException, IOException {
		 
	        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
			InputStream in = fileItem.getInputStream();
			IteratingMDLReader reader = new IteratingMDLReader(in,builder);
			if (reader != null) {

				DataOutputStream bout = new DataOutputStream(out);
				
				SmilesGenerator g = new SmilesGenerator();
				bout.writeChars("SMILES\n");
				while (reader.hasNext()) {
	                Object object = reader.next();
	                if (object instanceof Molecule) {
	                	try {
	                		bout.writeChars(g.createSMILES((Molecule) object));
	                		bout.writeChar('\n');
	                	} catch (Exception x) {
	                		
	                	}
	                }
				}
			}	
	 }


}
