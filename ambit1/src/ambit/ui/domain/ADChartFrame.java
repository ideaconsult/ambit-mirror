/**
 * Created on 2005-1-24
 *
 */
package ambit.ui.domain;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Toolkit;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JTabbedPane;

import org.jfree.chart.ChartPanel;

import ambit.data.IAmbitObjectListener;
import ambit.domain.QSARDataset;

/**
 * GUI<br>
 * A Frame with controls for Jfreechart  
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class ADChartFrame extends JFrame {
	//GUI
    protected ADChartAttributePanel[] attributePanel = null;
    protected JDesktopPane desktop;	
    
	/**
	 * @throws java.awt.HeadlessException
	 */
	public ADChartFrame(QSARDataset ds, IADPlotsType[] plotType) throws HeadlessException {
		super();
		customizeChart(ds,plotType);
		setTitle("AMBIT Disclosure chart");		
	}

	/**
	 * @param gc
	 */
	public ADChartFrame(GraphicsConfiguration gc,QSARDataset ds, IADPlotsType[] plotType) {
		super(gc);
		customizeChart(ds,plotType);
		setTitle("AMBIT Disclosure chart");
	}	

	/**
	 * @param title
	 * @throws java.awt.HeadlessException
	 */
	public ADChartFrame(String title,QSARDataset ds, IADPlotsType[] plotType) throws HeadlessException {
		super(title);
		customizeChart(ds,plotType);
	}

	/**
	 * @param title
	 * @param gc
	 */
	public ADChartFrame(String title, GraphicsConfiguration gc,QSARDataset ds, IADPlotsType[] plotType) {
		super(title, gc);
		customizeChart(ds,plotType);		
	}

	protected void customizeChart(QSARDataset ds,IADPlotsType[] plotType) {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                  screenSize.width/2  - inset*2,
                  screenSize.height/2 - inset*2);
        
        setSize(600,550);
//	  Set up the GUI.
        desktop = new JDesktopPane(); //a specialized layered pane
        createFrame(ds,plotType); //create first "window"
        setContentPane(desktop);
        //setJMenuBar(createMenuBar());

        //Make dragging a little faster but perhaps uglier.
        desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
        /*
	    
	    JPanel panel = new JPanel();
	    panel.setForeground(AmbitColors.DarkClr);
	    panel.setBackground(Color.white);
	    panel.setBorder(BorderFactory.createEtchedBorder(AmbitColors.DarkClr,AmbitColors.BrightClr));
	    getContentPane().add(panel,BorderLayout.CENTER);
	    
	    attributePanel = new ADChartAttributePanel("Attribute");
	    //attributePanel.updateData(ds,plotType);
	    JScrollPane xScroller = attributePanel.createTable(ds,plotType);
	    xScroller.setPreferredSize(new Dimension(120,200));
	    panel.add(xScroller);
//	    panel.add(attributePanel);
 * */
 	}

	 	
    public ADChartAttributePanel getAttributePanel(int index) {
        return attributePanel[index];
    }
    public IAmbitObjectListener getModelListener(int index) {
        return attributePanel[index].modelListener;
    }
    public IAmbitObjectListener getTestListener(int index) {
        return attributePanel[index].testListener;
    }
    
    //Create a new internal frame.
    protected void createFrame(QSARDataset ds,IADPlotsType[] plotType) {
        JInternalFrame frame = new JInternalFrame("Chart options",true,false,true,true);
        frame.setPreferredSize(new Dimension(200,200));
        frame.setBounds(320,10,150,300);
        
        JTabbedPane plotsPane = new JTabbedPane();
        attributePanel = new ADChartAttributePanel[plotType.length];
        for (int i=0;i< plotType.length;i++) {
        	attributePanel[i] = new ADChartAttributePanel("Attribute",ds,plotType[i]);
        	plotsPane.addTab(plotType[i].getTitle(), attributePanel[i]);
        }
	    frame.getContentPane().add(plotsPane);
        frame.setVisible(true); //necessary as of 1.3
        desktop.add(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {}
    }
    
    //Create a new internal frame.
    protected void createFrame(String title,ChartPanel chartPanel,
            int x, int y,
            Dimension d) {
        
        JInternalFrame frame = new JInternalFrame(title,true,false,true,true);
        frame.setPreferredSize(d);
        frame.setBounds(x,y,d.width, d.height);
	    frame.getContentPane().add(chartPanel);
	    
        frame.setVisible(true); //necessary as of 1.3
        desktop.add(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {}
    }    
    public int getPanelsCount() {
    	return attributePanel.length;
    }
}

