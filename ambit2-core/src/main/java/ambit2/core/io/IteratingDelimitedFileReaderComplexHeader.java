package ambit2.core.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIToStructure;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.io.setting.StringIOSetting;
import org.openscience.cdk.io.setting.IOSetting.Importance;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.processors.CASProcessor;

public abstract class IteratingDelimitedFileReaderComplexHeader<COLUMN> extends IteratingFilesWithHeaderReader<COLUMN> {
	   public static String defaultSMILESHeader = "SMILES";
	    public static String optionalSMILESHeader = "CANONICAL_SMILES";
	    protected String commentChar = "#";
		public String getCommentChar() {
			return commentChar;
		}

		public void setCommentChar(String commentChar) {
			this.commentChar = commentChar;
		}

		private BufferedReader input;

		private boolean nextAvailableIsKnown;

		private boolean hasNext;

		private IAtomContainer nextMolecule;

		protected DelimitedFileFormat format;

		protected Object[] values;
		
		protected CASProcessor casTransformer = new CASProcessor();



		public IteratingDelimitedFileReaderComplexHeader(Reader in) throws CDKException {
			this(in, new DelimitedFileFormat()); // default format
		}

		/**
		 * 
		 */
		public IteratingDelimitedFileReaderComplexHeader(Reader in, DelimitedFileFormat format) throws CDKException {
			super();
			this.format = format;
			setReader(in);
		}
		public void setReader(InputStream in) throws CDKException {
			setReader(new InputStreamReader(in));
			
		}
		public void setReader(Reader reader) throws CDKException {
			input = new BufferedReader(reader);
			nextMolecule = null;
			nextAvailableIsKnown = false;
			hasNext = false;
			
		}
		@Override
		protected LiteratureEntry getReference() {
			return LiteratureEntry.getInstance(getClass().getName(),getClass().getName());
		}	

		public IteratingDelimitedFileReaderComplexHeader(InputStream in) throws UnsupportedEncodingException, CDKException {
			this(new InputStreamReader(in,"UTF-8"));
		}

		public IteratingDelimitedFileReaderComplexHeader(InputStream in,DelimitedFileFormat format) throws UnsupportedEncodingException, CDKException {
			this(new InputStreamReader(in,"UTF-8"), format);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.openscience.cdk.io.ChemObjectIO#getFormat()
		 */
		public IResourceFormat getFormat() {
			return format;

		}

		// TODO fix: returns false if a column without a header has some data
		// (throws exception and the returns false)

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			if (isHeaderEmpty()) {
		    	fireIOSettingQuestion(new StringIOSetting("",Importance.MEDIUM,Property.IO_QUESTION.IO_START.toString(),""));
				processHeader(input);
		    	fireIOSettingQuestion(new StringIOSetting("",Importance.MEDIUM,Property.IO_QUESTION.IO_STOP.toString(),""));
			}
			
			if (!nextAvailableIsKnown) {
				hasNext = false;

				// now try to parse the next Molecule
				try {
					if (input.ready()) {

						extractRowKeyAndData(input.readLine().trim());
						nextMolecule = null;
						if (inchiIndex>=0) try {
		        		   if (inchiFactory==null) inchiFactory = InChIGeneratorFactory.getInstance();
			           		
		        		   InChIToStructure c =inchiFactory.getInChIToStructure(values[inchiIndex].toString(), SilentChemObjectBuilder.getInstance());
		        		   nextMolecule = c.getAtomContainer();
		        		   
						} catch (Exception x) {
							nextMolecule = null;
						}
						
						if ((nextMolecule==null) && (smilesIndex >= 0)) {
							try {
								if (values[smilesIndex]==null) {
									nextMolecule = SilentChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
								} else 
							    nextMolecule = sp.parseSmiles(values[smilesIndex].toString());
							} catch (InvalidSmilesException x) {
									// do not want to break if a record is faulty
									logger.fine("Empty molecule!");
									nextMolecule = SilentChemObjectBuilder.getInstance().newInstance(IAtomContainer.class); // just create
									nextMolecule.setProperty("SMILES", "Invalid SMILES");
							}
						}

						if (nextMolecule == null) nextMolecule = SilentChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
						
						for (int i = 0; i < values.length; i++) 
							if (values[i]!=null)  {
								String cas = casTransformer.process(values[i].toString());
								if (CASProcessor.isValidFormat(cas)) {
									//if (getHeaderColumn(i) instanceof Property)
										//getHeaderColumn(i).setLabel(Property.CAS);
									values[i] = cas;
								}
								//else if (EINECS.isValidFormat(values[i].toString())) 
									//getHeaderColumn(i).setLabel(Property.EC);
								nextMolecule.setProperty(getHeaderColumn(i), 
										values[i].toString().trim());
							} else  
								nextMolecule.removeProperty(getHeaderColumn(i));


						/*
						 * if (nextMolecule.getAtomCount() > 0) { hasNext = true; }
						 * else { hasNext = false; }
						 */
						hasNext = true;
					} else {
						hasNext = false;
					}
				} catch (Exception exception) {
		            logger.log(Level.SEVERE,"Error while reading next molecule: ", exception);
					hasNext = true;
				}
				if (!hasNext)
					nextMolecule = null;
				nextAvailableIsKnown = true;
			}
			return hasNext;

		}

		public Object next() {
			if (!nextAvailableIsKnown) {
				hasNext();
			}
			nextAvailableIsKnown = false;
			if (!hasNext) {
				throw new NoSuchElementException();
			}
			/*
			for (int i=0; i < headerOptions.length;i++) 
	        if (headerOptions[i] instanceof MolPropertiesIOSetting) 
	        	((MolPropertiesIOSetting) headerOptions[i]).getProperties()
	        		.assign(nextMolecule);
	        	*/	
			return nextMolecule;
		}

		public void close() throws IOException {
			input.close();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}


		/*
		protected IOSetting[] setHeaderOptions() {
		    Vector commandOptions = new Vector();
		    IOSetting[] options = new IOSetting[header.size()];
		    for (int i=0; i < IColumnTypeSelection._columnTypesS.length;i++)
		        commandOptions.add(IColumnTypeSelection._columnTypesS[i]);
		    
		    for (int i=0; i < header.size();i++) {
		        options[i] = new OptionIOSetting("Select column type",
		                IOSetting.HIGH,
		                header.get(i).toString(), commandOptions, 
		                IColumnTypeSelection._columnTypesS[IColumnTypeSelection._ctX]);
		    }
	        return options;
		}
		*/
		protected void processComment(String line) {
			
		}
		protected void processHeader(BufferedReader in) {
			try {
				
				String line = in.readLine();
				while (line.startsWith(commentChar)) {
					processComment(line);
					line = in.readLine();
				}
				StringTokenizer st = new StringTokenizer(line,new String(format.getFieldDelimiter()));
				while (st.hasMoreTokens()) {
					addHeaderColumn(st.nextToken().trim());	
				}
				for (int i=0; i < getNumberOfColumns(); i++) {
					String smiHeader = getSmilesHeader(i);
					if (smiHeader.equals(defaultSMILESHeader)) {
						smilesIndex = i; break;
					} 
					if ((smilesIndex<0) && smiHeader.equals(optionalSMILESHeader)) {
						smilesIndex = i; break;
					}
				}
				values = new Object[getNumberOfColumns()];
				
				
			} catch (IOException x) {
				logger.log(Level.SEVERE,x.getMessage(),x); 
			}
		}

		protected abstract String getSmilesHeader(int index );
		
		protected String removeStringDelimiters(String key) {
			char textDelimiter = format.getTextDelimiter();
			String k = key.trim();
			if (k.length() == 0)
				return null;
			if (k.charAt(0) == textDelimiter) {
				k = k.substring(1);
			}
			if (k.charAt(k.length() - 1) == textDelimiter) {
				k = k.substring(0, k.length() - 1);
			}
			return k;
		}

		/**
		 * Extract values from a line
	*/
		public void extractRowKeyAndData(String line) {
			
			if (values!=null) for (int i=0; i < values.length; i++) values[i] = null;
			QuotedTokenizer st = new QuotedTokenizer(line,format.getFieldDelimiter().charAt(0));
				int fieldIndex = 0;
				while (st.hasMoreTokens()) {
					if (fieldIndex>=values.length) break;
					String next = st.nextToken();
					if (next != null) {
						values[fieldIndex] = removeStringDelimiters(next);
						if (next.startsWith("InChI=")) inchiIndex = fieldIndex;
					} else values[fieldIndex] = "";
					
					fieldIndex ++;
				}

		}
		 
		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
	    @Override
		public String toString() {
	        return "Reading compounds from " + format.toString(); 
	    }
	    @Override
	    protected ArrayList<COLUMN> createHeader() {
	    	return new ArrayList<COLUMN>();
	    }
	    @Override
	    protected abstract COLUMN createPropertyByColumnName(String name);
	    
}
