/* PDFWriter.java
 * Author: Nina Jeliazkova
 * Date: Feb 2, 2008 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Nina Jeliazkova
 * 
 * Contact: nina@acad.bg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit2.core.io;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.logging.Level;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.tools.DataFeatures;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Exporting set of structures as PDF file.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Feb 2, 2008
 */
public class PDFWriter extends FilesWithHeaderWriter {
    protected Document pdfDoc = null;
    protected PdfWriter pdfWriter;
    protected PdfPTable table;
    protected ICompoundImageTools imageTools;
    protected Dimension cell = new Dimension(200,200);
    /**
     * 
     */
    public PDFWriter(OutputStream outputStream) throws CDKException {
        this(outputStream,null);
    } 
    
    public PDFWriter(OutputStream outputStream, ICompoundImageTools imgTools) throws CDKException {
        super();
        setWriter(outputStream);
        if (imgTools==null) try {
	        	
	        	Class c = Class.forName("ambit2.rendering.CompoundImageTools");
	        	imageTools = (ICompoundImageTools) c.newInstance();
	        } catch (Exception x) { imageTools = null; }
        else imageTools = imgTools;
        
    }       

    /* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectWriter#setWriter(java.io.Writer)
     */
    public void setWriter(Writer arg0) throws CDKException {
        throw new CDKException("Not supported!");

    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectWriter#setWriter(java.io.OutputStream)
     */
    public void setWriter(OutputStream out) throws CDKException {
        pdfDoc = new Document(PageSize.A4, 80, 50, 30, 65);
        try {
            pdfWriter = PdfWriter.getInstance(pdfDoc,out);
            //writer.setViewerPreferences(PdfWriter.HideMenubar| PdfWriter.HideToolbar);
            pdfWriter.setViewerPreferences(PdfWriter.PageModeUseThumbs | PdfWriter.PageModeUseOutlines);
            pdfDoc.addCreationDate();
            pdfDoc.addCreator(getClass().getName());
            pdfDoc.addKeywords("structures");
            pdfDoc.addTitle("structures");
            pdfDoc.addSubject("");
            pdfDoc.addAuthor("Toxtree");
            pdfDoc.open();
            /*
            pdfDoc.add(new Paragraph(method.getTitle()));
            pdfDoc.add(new Paragraph(method.getExplanation()));
            pdfDoc.add(new Paragraph());
            */
            
            table = new PdfPTable(new float[]{3f,5f});
            table.setWidthPercentage(100);

            
        } catch (Exception x) {
            throw new CDKException(x.getMessage());
        }

    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectWriter#write(org.openscience.cdk.interfaces.IChemObject)
     */
    public void write(IChemObject object) throws CDKException {
        
        if (object instanceof IAtomContainerSet) {
            writeSetOfMolecules((IAtomContainerSet)object);
        } else if (object instanceof IAtomContainer) {
            writeMolecule((IAtomContainer)object);
        } else {
            throw new CDKException("Only supported is writing of ChemFile and Molecule objects.");
        }

    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectIO#accepts(java.lang.Class)
     */
    public boolean accepts(Class classObject) {
        Class[] interfaces = classObject.getInterfaces();
        for (int i=0; i<interfaces.length; i++) {
            if (IChemFile.class.equals(interfaces[i])) return true;
            if (IAtomContainerSet.class.equals(interfaces[i])) return true;
        }
        return false;
    }
    public int getSupportedDataFeatures() {
        return DataFeatures.HAS_GRAPH_REPRESENTATION  ;
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectIO#close()
     */
    public void close() throws IOException {
        try {
        pdfDoc.add(table);
        } catch (Exception x) {
            x.printStackTrace();
        }
        pdfDoc.close();
    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectIO#getFormat()
     */
    public IResourceFormat getFormat() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void writeHeader() throws IOException {
        
        PdfPCell cell = new PdfPCell(new Paragraph("Structure"));
        cell.setBackgroundColor(Color.white);
        table.addCell(cell);    
        
        cell = new PdfPCell(new Paragraph("Properties"));
        cell.setBackgroundColor(Color.white);
        table.addCell(cell);
        
       
    }
    public void  writeSetOfMolecules(IAtomContainerSet som)
    {
        for (int i = 0; i < som.getAtomContainerCount(); i++) {
            try {
                
                writeMolecule(som.getAtomContainer(i));
            } catch (Exception exc) {
            }
        }
    }    
    public void writeMolecule(IAtomContainer molecule) {
        Object value;       
        
        try {

    
            //give it a chance to create a header just before the first write
            if (!writingStarted) {
                if (header == null) setHeader(molecule.getProperties());
                writeHeader();
                writingStarted = true;
            }
                     
            Paragraph p = new Paragraph("");
            String s;
            for (int i =0; i< header.size(); i++) {
                StringBuffer b = new StringBuffer();
                b.append(header.get(i));
                b.append(" = ");
                value = molecule.getProperty(header.get(i));
                if (i == smilesIndex) {
                    
                    if (value == null) //no SMILES available
                    try {
                        value = ""; //sg.createSMILES(molecule);
                    } catch (Exception x) {
                        logger.log(Level.WARNING,"Error while createSMILES\t",x);
                        value = "";
                    }
                } 
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
            BufferedImage image = imageTools.getImage(molecule);
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
        	logger.log(Level.SEVERE,"Error while writing molecule",x);
        }
        
    }    
}
