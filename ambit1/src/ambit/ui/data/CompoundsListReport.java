/*
 * Created on 2006-3-5
 *
 */
package ambit.ui.data;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;

import javax.swing.table.TableModel;

import org.jfree.report.ElementAlignment;
import org.jfree.report.JFreeReport;
import org.jfree.report.ReportProcessingException;
import org.jfree.report.elementfactory.ImageFieldElementFactory;
import org.jfree.report.elementfactory.TextFieldElementFactory;
import org.jfree.report.modules.gui.base.PreviewDialog;
import org.jfree.ui.FloatDimension;
import org.jfree.util.Log;

import ambit.data.AmbitList;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-3-5
 */
public class CompoundsListReport {
    protected AmbitList list = null;
    protected TableModel listTableModel = null;
    protected TableModel gridTableModel = null;
    protected String title = "Structures";
    
    
    public CompoundsListReport(AmbitList list, String title) {
        this.list = list;
        this.title = title;
    }
    public void executeReportList ()
    {
      if (listTableModel == null)  
          listTableModel = new CompoundsListTableModelImages(list,new Dimension(100,80));
      final JFreeReport report = createReportDefinitionList(listTableModel);
      report.setData(listTableModel);
      try
      {
        final PreviewDialog preview = new PreviewDialog(report);
        preview.pack();
        preview.setModal(true);
        preview.setVisible(true);
      }
      catch (ReportProcessingException e)
      {
        Log.error("Failed to generate report ", e);
      }
    }
    
    public void executeReportGrid ()
    {
      if (gridTableModel == null)
          gridTableModel = new GridTableModelImages(list,4,new Dimension(120,120));
      final JFreeReport report = createReportDefinitionGrid(gridTableModel);
      report.setData(gridTableModel);
      try
      {
        final PreviewDialog preview = new PreviewDialog(report);
        preview.pack();
        preview.setModal(true);
        preview.setVisible(true);
      }
      catch (ReportProcessingException e)
      {
        Log.error("Failed to generate report ", e);
      }
    }
    
    private JFreeReport createReportDefinitionGrid (TableModel tableModel)
    {

      final JFreeReport report = new JFreeReport();
      
      report.setName(getDescription());

      Dimension cellSize = ((GridTableModelImages) tableModel).getImageSize();
      FloatDimension fd = new FloatDimension(cellSize.width,cellSize.height);
      ImageFieldElementFactory imageFieldElementFactory = new ImageFieldElementFactory();
      TextFieldElementFactory factory = new TextFieldElementFactory();
      for (int i = 0; i < tableModel.getColumnCount();i++) {
   
	      imageFieldElementFactory.setAbsolutePosition(new Point2D.Float(i*cellSize.width,0)); 
	      imageFieldElementFactory.setFieldname(tableModel.getColumnName(i)); 
	      imageFieldElementFactory.setName("Structure"); 
	      imageFieldElementFactory.setKeepAspectRatio(Boolean.TRUE); 
	      imageFieldElementFactory.setScale(Boolean.TRUE); 
	      imageFieldElementFactory.setMinimumSize(fd); 
	      report.getItemBand().addElement(imageFieldElementFactory.createElement());
      }
     
      
    	return report;
    }
    
    private JFreeReport createReportDefinitionList (TableModel tableModel)
    {

      final JFreeReport report = new JFreeReport();
      
      report.setName(getDescription());

      Dimension cellSize = ((CompoundsListTableModelImages) tableModel).getImageSize();
      FloatDimension fd = new FloatDimension(cellSize.width,cellSize.height);
      ImageFieldElementFactory imageFieldElementFactory = new ImageFieldElementFactory();
      TextFieldElementFactory factory = new TextFieldElementFactory();


      
      //image
      imageFieldElementFactory.setAbsolutePosition(new Point2D.Float(0,0)); 
      imageFieldElementFactory.setFieldname("Structure"); 
      imageFieldElementFactory.setName("Structure"); 
      imageFieldElementFactory.setKeepAspectRatio(Boolean.TRUE); 
      imageFieldElementFactory.setScale(Boolean.TRUE); 
      imageFieldElementFactory.setMinimumSize(fd); 
      report.getItemBand().addElement(imageFieldElementFactory.createElement());
      
      //no
      factory.setName(tableModel.getColumnName(0));
      factory.setAbsolutePosition(new Point2D.Float(0,0));
      factory.setMinimumSize(new FloatDimension(cellSize.width, 12));
      factory.setColor(Color.black);
      factory.setVerticalAlignment(ElementAlignment.TOP);
      factory.setNullString("-");
      factory.setFieldname(tableModel.getColumnName(0));
      factory.setBold(new Boolean(true));
      factory.setHorizontalAlignment(ElementAlignment.LEFT);
      
      report.getItemBand().addElement(factory.createElement());      
      for (int i = 1; i < 4;i++) {
          factory.setName(tableModel.getColumnName(i));
          factory.setAbsolutePosition(new Point2D.Float(cellSize.width,(i-1)*24));
          factory.setMinimumSize(new FloatDimension(cellSize.width*2, 24));
          factory.setBold(new Boolean(false));
          factory.setColor(Color.black);
          factory.setVerticalAlignment(ElementAlignment.TOP);
          factory.setNullString("-");
          factory.setFieldname(tableModel.getColumnName(i));
          factory.setHorizontalAlignment(ElementAlignment.LEFT);
          factory.setDynamicHeight(new Boolean(true));
          report.getItemBand().addElement(factory.createElement());
      } 
      for (int i = 5; i < tableModel.getColumnCount();i++) {
          factory.setName(tableModel.getColumnName(i));
          factory.setAbsolutePosition(new Point2D.Float(cellSize.width*3,(i-5)*12));
          factory.setMinimumSize(new FloatDimension(cellSize.width, 12));
          factory.setBold(new Boolean(false));
          factory.setColor(Color.black);
          factory.setVerticalAlignment(ElementAlignment.TOP);
          factory.setNullString("-");
          factory.setFieldname(tableModel.getColumnName(i));
          factory.setHorizontalAlignment(ElementAlignment.LEFT);
          factory.setDynamicHeight(new Boolean(true));
          report.getItemBand().addElement(factory.createElement());
      } 
      

        
     
      
    	return report;
    }
    
    protected String getDescription ()
    {
      return title;
    }


}
