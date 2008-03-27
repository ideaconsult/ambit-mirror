/*
 * Created on 2006-3-5
 *
 */
package ambit2.ui.data;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.jfree.report.JFreeReportBoot;

import ambit2.data.molecule.CompoundsList;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-3-5
 */
public class CompoundsListPanel extends JPanel {
    protected CompoundsGridPane scrollPane;
    protected CompoundsListReport report = null;
    /**
     * 
     */
    public CompoundsListPanel(CompoundsList list,int columns,Dimension cellSize) {
        super();
        addWidgets(list,columns,cellSize);
    }

    /**
     * @param isDoubleBuffered
     */
    public CompoundsListPanel(CompoundsList list,int columns,Dimension cellSize,boolean isDoubleBuffered) {
        super(isDoubleBuffered);
        addWidgets(list,columns,cellSize);
    }

    /**
     * @param layout
     */
    public CompoundsListPanel(CompoundsList list,int columns,Dimension cellSize,LayoutManager layout) {
        super(layout);
        addWidgets(list,columns,cellSize);
    }

    /**
     * @param layout
     * @param isDoubleBuffered
     */
    public CompoundsListPanel(CompoundsList list,int columns,Dimension cellSize,LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
        addWidgets(list,columns,cellSize);
    }
    protected void addWidgets(CompoundsList list, int columns,Dimension cellSize) {
        setLayout(new BorderLayout());
        JTabbedPane tPane = new JTabbedPane();
        
        scrollPane = new CompoundsGridPane(list,columns,cellSize);
        tPane.addTab("Grid",scrollPane);
        Dimension d = new Dimension(150,80);
        scrollPane = new CompoundsGridPane(
                new CompoundsListTableModelImages(list,d),d
                );
        tPane.addTab("Table",scrollPane);
        
        add(tPane,BorderLayout.CENTER);
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createMenu());
        add(menuBar,BorderLayout.NORTH);
    }
    protected JMenu createMenu() {
        JMenu menu = new JMenu("File");
        JMenu menu1 = new JMenu("Print");
        menu.add(menu1);
        JMenuItem menuItem  = new JMenuItem(new AbstractAction("Table") {
            /* (non-Javadoc)
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed(ActionEvent e) {
                if (report == null) {
                    JFreeReportBoot.getInstance().start();
                    report = new CompoundsListReport(scrollPane.getList(),"Structures");
                }
               report.executeReportList();
        }
        });
        menu1.add(menuItem);
        menuItem  = new JMenuItem(new AbstractAction("Grid") {
            /* (non-Javadoc)
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed(ActionEvent e) {
                if (report == null) {
                    JFreeReportBoot.getInstance().start();
                    report = new CompoundsListReport(scrollPane.getList(),"Structures");
                }
               // tr.executeReportList();
                report.executeReportGrid();
        }
        });
                           
        menu1.add(menuItem);
        
        return menu;
    }

}
