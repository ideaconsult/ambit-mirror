package ambit2.db.reporters;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.RetrieveGroupedValuesByAlias;
import ambit2.db.readers.RetrieveProfileValues;
import ambit2.db.readers.RetrieveProfileValues.SearchMode;
import ambit2.db.readers.RetrieveStructure;
import ambit2.rendering.CompoundImageTools;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

/**
 * Generates PDF 
 * @author nina
 * TODO make use of ImageReporter 
 *
 * @param <Q>
 */
public class PDFReporter<Q extends IQueryRetrieval<IStructureRecord>> extends QueryHeaderReporter< Q, Document> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2931123688036795689L;
	protected MoleculeReader reader = new MoleculeReader();
	protected CompoundImageTools depict = new CompoundImageTools();
	protected PdfPTable table;
	protected Font font;

	public PDFReporter() {	
		this(new Template());
	}
	public PDFReporter(Template template) {
		this(template,null);
	}
	public PDFReporter(Template template, Profile groupedPrperties) {
		super();
		setGroupProperties(groupedPrperties);
		setTemplate(template==null?new Template(null):template);
		depict.setBackground(Color.white);
		depict.setBorderColor(Color.white);
		depict.setImageSize(new Dimension(400,400));
		getProcessors().clear();
		RetrieveStructure r = new RetrieveStructure();
		r.setPage(0);
		r.setPageSize(1);
		getProcessors().add(new ProcessorStructureRetrieval(r));
		if ((getGroupProperties()!=null) && (getGroupProperties().size()>0))
			getProcessors().add(new ProcessorStructureRetrieval(new RetrieveGroupedValuesByAlias(getGroupProperties())) {
				@Override
				public IStructureRecord process(IStructureRecord target)
						throws AmbitException {
					((RetrieveGroupedValuesByAlias)getQuery()).setRecord(target);
					return super.process(target);
				}
			});		
		if (getTemplate().size()>0) 
			getProcessors().add(new ProcessorStructureRetrieval(new RetrieveProfileValues(SearchMode.idproperty,getTemplate(),true)) {
				@Override
				public IStructureRecord process(IStructureRecord target)
						throws AmbitException {
					((RetrieveProfileValues)getQuery()).setRecord(target);
					return super.process(target);
				}
			});

		getProcessors().add(new DefaultAmbitProcessor<IStructureRecord,IStructureRecord>() {
			public IStructureRecord process(IStructureRecord target) throws AmbitException {
				processItem(target);
				return target;
			};
		});	
	}
	@Override
	public void setOutput(Document pdfDoc) throws Exception {
		super.setOutput(pdfDoc);
	}

	protected void writeHeader(Document writer) throws IOException {
        String[] tops = new String[] {"Structure","Properties"};
        for (String top:tops) { 
        	Chunk chunk = new Chunk(top);
        	chunk.setFont(font);
        	PdfPCell cell = new PdfPCell(new Paragraph(chunk));
	        cell.setBackgroundColor(Color.white);
	        table.addCell(cell);
        }
        
       
    }
    @Override
    public void close() throws Exception {
    	try {
    		getOutput().close();
    	} catch (Exception x) {logger.log(Level.FINEST,x.getMessage(),x); }
    	super.close();
    	
    }
    public void header(Document output, Q query) {
    	
		output.addCreationDate();
        output.addCreator(getClass().getName());
        output.addSubject("");
        output.addAuthor("http://ambit.sourceforge.net");
        output.addHeader("License", getLicenseURI());
        output.addTitle(query.toString());
        output.addKeywords(query.toString());        
        
        if (getLicenseURI()!=null) {
	        HeaderFooter footer = new HeaderFooter(new Phrase(String.format("License %s",getLicenseURI())),false);
	        footer.setAlignment(Element.ALIGN_CENTER);
	        output.setFooter(footer) ;
        }
        
        output.open();		
        
        font = new Font(Font.TIMES_ROMAN,10,Font.NORMAL);
        
        table = new PdfPTable(new float[]{3f,5f});
        table.setWidthPercentage(100);      
        
        
        try {
        	writeHeader(output);
        } catch (Exception x) {
        	
        }
    	
    };
    public void footer(Document output, Q query) {
        try {
        	output.add(table);
        } catch (Exception x) {
        	logger.log(Level.WARNING,x.getMessage(),x);
        }	
    };
	
	@Override
	public Object processItem(IStructureRecord item) throws AmbitException {

		try {
			if(header == null) header = template2Header(getTemplate(),true);
			
			IAtomContainer mol = reader.process(item);
			for (Property p : item.getRecordProperties())
				mol.setProperty(p,item.getRecordProperty(p));
			writeMolecule(mol);
			
			
		} catch (Exception x) {
            logger.log(java.util.logging.Level.FINE,x.getMessage(),x);
		}
		return null;
		
	}
    protected void writeMolecule(IAtomContainer molecule) {
        Object value;       
        
        try {
 
            Paragraph p = new Paragraph("");

            String s;
            
	            for (Property property : header) {
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
	                    
	                    Chunk chunk = new Chunk(b.toString());
	                    chunk.setFont(font);
	                    p.add(new Paragraph(chunk));
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
            logger.log(java.util.logging.Level.FINE,x.getMessage(),x);
        }
        
	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getFileExtension() {
		return "pdf";
	}
}