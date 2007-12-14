package ambit.ui.data.molecule;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JPanel;

import ambit.data.molecule.DataContainer;


public class NavigationPanel extends JPanel implements Observer {
	protected DataContainer dataContainer;
	protected JLabel navCurrent = null;
	protected JLabel navAll = null;
	protected Color bgColor = Color.BLACK;
	protected Color fColor = Color.WHITE;
	/**
	 * 
	 */
	private static final long serialVersionUID = -7523783333429225550L;

	public NavigationPanel(DataContainer datacontainer, Color bgColor, Color fColor) {
		super();
		setDataContainer(datacontainer);
		addWidgets(bgColor,fColor);
	}

	public NavigationPanel(DataContainer datacontainer,boolean arg0,Color bgColor, Color fColor) {
		super(arg0);
		setDataContainer(datacontainer);
		addWidgets(bgColor,fColor);
	}

	public NavigationPanel(DataContainer datacontainer,LayoutManager arg0,Color bgColor, Color fColor) {
		super(arg0);
		setDataContainer(datacontainer);
		addWidgets(bgColor,fColor);
	}

	public NavigationPanel(DataContainer datacontainer,LayoutManager arg0, boolean arg1,Color bgColor, Color fColor) {
		super(arg0, arg1);
		setDataContainer(datacontainer);
		addWidgets(bgColor,fColor);
	}

	protected void addWidgets(Color bgColor, Color fColor) {
		//navPanel.setBorder(BorderFactory.createTitledBorder("Title"));
		this.bgColor = bgColor;
		this.fColor = fColor;
		setLayout(new FlowLayout());
		setBackground(bgColor);
		setForeground(fColor);
		setPreferredSize(new Dimension(120,32));
		setMinimumSize(new Dimension(64,32));
		
		JLabel nav[] = new JLabel[4];
        nav[0] = createLabel("<html><u><b> First</b></u></html>",
                "Click here to go to the first molecule of the set"
                );
        nav[0].addMouseListener(new MouseAdapter() {
	   		public void mouseClicked(MouseEvent e) {
	   			dataContainer.first();
	   		}
	    });	 
        nav[1] = createLabel("<html><u><b> Prev</b></u></html>",
                "Click here to go to the previous molecule of the set"
                );
        nav[1].addMouseListener(new MouseAdapter() {
	   		public void mouseClicked(MouseEvent e) {
	   			dataContainer.prev();
	   		}
	    });			
        nav[2] = createLabel("<html><u><b> Next</b></u></html>",
                "Click here to go to the next molecule of the set"
                );
        nav[2].addMouseListener(new MouseAdapter() {
	   		public void mouseClicked(MouseEvent e) {
	   			dataContainer.next();
	   		}
	    });
        nav[3] = createLabel("<html><u><b> Last</b></u></html>",
                "Click here to go to the last molecule of the set"
                );
        nav[3].addMouseListener(new MouseAdapter() {
	   		public void mouseClicked(MouseEvent e) {
	   			dataContainer.last();
	   		}
	    });
        navCurrent = new JLabel("1");
        navCurrent.setToolTipText("Current molecule.");
        //navCurrent.setEditable(false);
	    //navCurrent.setEnabled(false);
	    navCurrent.setMinimumSize(new Dimension(32,24));
	    navAll = new JLabel("1");
	    navAll.setToolTipText("Number of molecules in the set loaded.");
	    //navAll.setEditable(false);
	    //navAll.setEnabled(false);
	    	//createLabel("<html><font color=blue><b>/1</b></font></html>",);
	    
        for (int i = 0; i < 2; i++) add(nav[i]);
        add(navCurrent);
        add(createLabel("<html><font color=blue><b>/</b></font></html>",""));
        add(navAll);
        for (int i = 2; i < 4; i++) add(nav[i]);
        
        
	}	
	public void update(Observable arg0, Object arg1) {
		navCurrent.setText(Integer.toString(dataContainer.getCurrentNo()+1));
		//navAll.setToolTipText(dataContainer.toString());
        navAll.setText(	Integer.toString(dataContainer.getMoleculesCount() 
				));
	}

	public DataContainer getDataContainer() {
		return dataContainer;
	}

	public void setDataContainer(DataContainer dataContainer) {
		this.dataContainer = dataContainer;
		dataContainer.addObserver(this);

	}

	
	public  JLabel createLabel(String title, String tooltip) {
        JLabel label = new JLabel(title);
        label.setAlignmentX(CENTER_ALIGNMENT);
        label.setOpaque(true);
        label.setBackground(bgColor);
        label.setForeground(fColor);     
        label.setToolTipText(tooltip);
        return label;
	}	
}
