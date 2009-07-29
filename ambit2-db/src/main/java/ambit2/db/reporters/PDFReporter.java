package ambit2.db.reporters;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.sql.SQLException;
import java.util.Iterator;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.core.io.CompoundImageTools;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

public class PDFReporter<Q extends IQueryRetrieval<IStructureRecord>> extends QueryReporter<IStructureRecord, Q, Document> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2931123688036795689L;
	protected MoleculeReader reader = new MoleculeReader();
	protected CompoundImageTools depict = new CompoundImageTools();
	protected PdfPTable table;
	protected Profile header;
	

	public Profile getHeader() {
		return header;
	}
	public void setHeader(Profile header) {
		this.header = header;
	}
	public PDFReporter() {
		depict.setBackground(Color.white);
		depict.setBorderColor(Color.white);
		getProcessors().clear();
		getProcessors().add(new ProcessorStructureRetrieval());
		getProcessors().add(new DefaultAmbitProcessor<IStructureRecord,IStructureRecord>() {
			public IStructureRecord process(IStructureRecord target) throws AmbitException {
				processItem(target,getOutput());
				return target;
			};
		});	
	}
	@Override
	public void setOutput(Document pdfDoc) throws AmbitException {
		super.setOutput(pdfDoc);
	}

    protected void writeHeader()  {
        
        PdfPCell cell = new PdfPCell(new Paragraph("Structure"));
        cell.setBackgroundColor(Color.white);
        table.addCell(cell);    
        
        cell = new PdfPCell(new Paragraph("Properties"));
        cell.setBackgroundColor(Color.white);
        table.addCell(cell);
        
       
    }
    @Override
    public void close() throws SQLException {
    	try {
    	getOutput().close();
    	} catch (Exception x) {x.printStackTrace(); }
    	super.close();
    	
    }
    public void header(Document output, Q query) {
		output.addCreationDate();
        output.addCreator(getClass().getName());
        output.addSubject("");
        output.addAuthor("http://ambit.sourceforge.net");
        output.addTitle(query.toString());
        output.addKeywords(query.toString());        
        output.open();		
        
        table = new PdfPTable(new float[]{3f,5f});
        table.setWidthPercentage(100);      
        
        writeHeader();
    	
    };
    public void footer(Document output, Q query) {
        try {
        	getOutput().add(table);
        } catch (Exception x) {

        }	
    };
	
	@Override
	public void processItem(IStructureRecord item, Document document) {

		try {

			writeMolecule(reader.process(item));
			
			
		} catch (Exception x) {
			logger.error(x);
		}
		
	}
    protected void writeMolecule(IAtomContainer molecule) {
        Object value;       
        
        try {

        		/*
            //give it a chance to create a header just before the first write
            if (!writingStarted) {
                if (header == null) setHeader(molecule.getProperties());
                writeHeader();
                writingStarted = true;
            }
                  */   
            Paragraph p = new Paragraph("");
            String s;
            
            if (header != null)  {
            Iterator<Property> props = header.getProperties(true);
	            while (props.hasNext()) {
	            	Property property = props.next();
	                StringBuffer b = new StringBuffer();
	                b.append(property.getLabel());
	                b.append(" = ");
	                value = molecule.getProperty(property);
	
	                if (value != null) {
	                    if (value instanceof Number) {
	                        s = value.toString();
	                    } else {
	                        s = value.toString();
	                        
	                    }
	                    
	                    b.append(s);
	                    p.add(new Paragraph(b.toString()));
	                } 
	                
	            }
            }
            BufferedImage image = depict.getImage(molecule);
            image.flush();
      
            Image png_struc = Image.getInstance(image,Color.white);
            png_struc.setAlignment(Image.LEFT);
            png_struc.scalePercent(100);
            
            PdfPCell cell = new PdfPCell();
            cell.setMinimumHeight(cell.height());
            cell.addElement(png_struc);            
            table.addCell(cell);
            
            cell = new PdfPCell(p);
            cell.setBackgroundColor(Color.white);
            table.addCell(cell);            

        } catch(Exception x) {
            logger.error("ERROR while writing Molecule: " + x.getMessage());
            logger.debug(x);
            x.printStackTrace();
        }
        
	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}

}