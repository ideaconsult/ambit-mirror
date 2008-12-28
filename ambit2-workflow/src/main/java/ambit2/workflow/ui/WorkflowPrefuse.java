/*
Copyright (C) 2005-2007  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.workflow.ui;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.layout.Layout;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.action.layout.graph.NodeLinkTreeLayout;
import prefuse.activity.Activity;
import prefuse.controls.ControlAdapter;
import prefuse.controls.DragControl;
import prefuse.controls.FocusControl;
import prefuse.controls.NeighborHighlightControl;
import prefuse.controls.PanControl;
import prefuse.controls.ToolTipControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Graph;
import prefuse.data.search.PrefixSearchTupleSet;
import prefuse.data.search.SearchTupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.GraphicsLib;
import prefuse.util.display.DisplayLib;
import prefuse.util.display.ItemBoundsListener;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;
import ambit2.ui.Utils;

import com.microworkflow.process.Workflow;

public class WorkflowPrefuse extends Display {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4945963443432036734L;
    private static final String LABEL = "id";
    public static final String GRAPH = "graph";
    public static final String NODES = "graph.nodes";
    public static final String EDGES = "graph.edges";
    
	protected Graph graph;
	protected Workflow workflow;
    protected PropertyChangeSupport propertyChangeSupport;
	
    public WorkflowPrefuse(final Workflow workflow)  {
    	this(workflow,false);
    }
	public WorkflowPrefuse(final Workflow tree,  boolean forcefieldLayout)  {
        // setup a visualisation
        super(new Visualization());
        propertyChangeSupport = new PropertyChangeSupport(this);
        setSize(700, 750);
        setDecisionMethod(tree);

        m_vis.setValue(EDGES, null, VisualItem.INTERACTIVE, Boolean.TRUE);
        
        LabelRenderer nodeRenderer = new LabelRenderer(LABEL);
        nodeRenderer.setRoundedCorner(8, 8);
        EdgeRenderer edgeR = new EdgeRenderer(Constants.EDGE_TYPE_CURVE);
        edgeR.setDefaultLineWidth(2);        

        DefaultRendererFactory rendererFactory = new DefaultRendererFactory(nodeRenderer,edgeR);
        m_vis.setRendererFactory(rendererFactory);
        
        ColorAction nodeText = new ColorAction(NODES, VisualItem.TEXTCOLOR);
        nodeText.setDefaultColor(ColorLib.gray(0));
        ColorAction nodeStroke = new ColorAction(NODES, VisualItem.STROKECOLOR);
        nodeStroke.setDefaultColor(ColorLib.gray(100));
        //ColorAction nodeFill = new ColorAction(NODES, VisualItem.FILLCOLOR);
        //nodeFill.setDefaultColor(ColorLib.gray(255));
        ColorAction edgeStrokes = new ColorAction(EDGES, VisualItem.STROKECOLOR);
        edgeStrokes.setDefaultColor(ColorLib.gray(100));
        /*
        ColorAction edgeFill = new ColorAction(EDGES, VisualItem.FILLCOLOR);
        edgeFill.setDefaultColor(ColorLib.gray(100));
        */
        
	    DataColorAction nodeFill = new DataColorAction(NODES, Workflow2PrefuseGraph.FIELD_NODETYPE,
         Constants.NOMINAL, VisualItem.FILLCOLOR,  createPalette(tree));
	    
	    nodeFill.add(VisualItem.HIGHLIGHT, ColorLib.rgb(255,0,0)); 	    

        // bundle the color actions
        ActionList draw = new ActionList();
        //draw.add(shape);        
        draw.add(nodeText);
        draw.add(nodeStroke);
        draw.add(nodeFill);
        draw.add(edgeStrokes);
        
    	ColorAction edgeStrokeColor = new ColorAction(EDGES, 
    			VisualItem.STROKECOLOR, ColorLib.gray(200));
    	ColorAction edgeFillColor = new ColorAction(EDGES, 
    			VisualItem.FILLCOLOR, ColorLib.gray(200));    	

    	/*
   		edgeStrokeColor.add(ExpressionParser.predicate("answer == '" + AbstractRule.MSG_YES + "'"), ColorLib.rgba(0,255,0,100));
   		edgeStrokeColor.add(ExpressionParser.predicate("answer == '" + AbstractRule.MSG_NO + "'"), ColorLib.rgba(255,0,0,100));

   		edgeFillColor.add(ExpressionParser.predicate("answer == '" + AbstractRule.MSG_YES + "'"), ColorLib.rgba(0,255,0,100));
   		edgeFillColor.add(ExpressionParser.predicate("answer == '" + AbstractRule.MSG_NO + "'"), ColorLib.rgba(255,0,0,100));

   		*/
   		draw.add(edgeStrokeColor);
   		draw.add(edgeFillColor);
        
        m_vis.putAction("draw", draw);

/*

                */
        
//      create an action list with an animated layout
//      the INFINITY parameter tells the action list to run indefinitely
        if (forcefieldLayout) {
	        ActionList layout = new ActionList(Activity.INFINITY);
	        layout.add(new ForceDirectedLayout(GRAPH));
	        layout.add(new RepaintAction());
	        m_vis.putAction("treeLayout", layout);
        } else {
            Layout treeLayout = new NodeLinkTreeLayout(GRAPH, Constants.ORIENT_TOP_BOTTOM,10,10,10);
            ((NodeLinkTreeLayout)treeLayout).setRootNodeOffset(10);
            m_vis.putAction("treeLayout", treeLayout);        	
        }
                   


        
        ToolTipControl ttc = new ToolTipControl(Workflow2PrefuseGraph.FIELD_EXPLANATION);
        addControlListener(ttc);
        FocusControl fc = new FocusControl() {
        	@Override
        	public void itemClicked(VisualItem item, MouseEvent e) {
        		// TODO Auto-generated method stub
        		super.itemClicked(item, e);
        		/*
                Object o = null;
        		int no = item.getInt(Workflow2PrefuseGraph.FIELD_ID);
        		if (no > 0) 
                    o = tree.getRule(no-1);                   
                
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (o instanceof IDecisionRule) {
                        propertyChangeSupport.firePropertyChange(Workflow2PrefuseGraph.FIELD_RULE, null,o);     
                    }
                } else {
                    if (e.isPopupTrigger()) {
                        JPopupMenu pm = getPopupMenu();
                        if (pm != null)
                            pm.show(e.getComponent(),e.getX(), e.getY());
                    }
                    
                } 
                */               
        	}
        };
        addControlListener(fc);
        /*
        Interactive controls of the display are specified using the Control objects found in prefuse.controls. 
        These controls might be to handle tool tips (ToolTipControl), hovering the mouse (HoverActionControl), 
        or the focusing of items the user clicks on (FocusControl). 
        Other controls that don't affect the data include the DragControl, PanControl, and the ZoomControl.
        */
        /*
        addControlListener(new HoverActionControl("repaint"));
        ColorAction colorAction = new ColorAction(FIELD_ID, VisualItem.FILLCOLOR, ColorLib.rgba(255,255,255,0));
        colorAction.add("_hover", ColorLib.gray(200,210));
        
//      adds the control
        addControlListener(new NeighborHighlightControl());
//         set the _highlight color
        colorAction = new ColorAction(FIELD_ID, VisualItem.FILLCOLOR, ColorLib.rgba(255,255,255,0));
        colorAction.add("_highlight", ColorLib.rgb(255,200,125));
        */

        addControlListener(new FocusControl(1));
        addControlListener(new DragControl());
        addControlListener(new PanControl());
        addControlListener(new ZoomControl());
        addControlListener(new WheelZoomControl());
        addControlListener(new ZoomToFitControl());
        addControlListener(new NeighborHighlightControl());        
        //addControlListener(new RotationControl());
        
        addControlListener(new ControlAdapter() {
            public void itemEntered(VisualItem item, MouseEvent e) {
            	
            	//System.out.println("itemEntered " + item.get);
            	
 
                    
            }
            public void itemExited(VisualItem item, MouseEvent e) {
            	/*
            	System.out.println(e.paramString());
            	
                if ( item.canGetInt(Tree2PrefuseGraph.FIELD_ID) ) {
                	int no = item.getInt(Tree2PrefuseGraph.FIELD_ID);
                	if (no > 0)
                		System.out.println(Integer.toString(no) + '\t' + tree.getRule(no-1));
                    
                } 
                */  
            }
        });
        
        SearchTupleSet search = new PrefixSearchTupleSet();
        m_vis.addFocusGroup(Visualization.SEARCH_ITEMS, search);

        // run actions
        m_vis.run("treeLayout");
        m_vis.run("draw");
	}

	public void setDecisionMethod(Workflow workflow) {
		this.workflow = workflow;
		try {
			initGraph(workflow);
		} catch (Exception x) {
			x.printStackTrace();
			graph = new Graph();  
		}
		
	}
	public JComponent getJComponent() {
		return this;
	}

	protected int[] createPalette(Workflow tree) {
		/*
		if ((tree != null) && (tree.getCategories().size() > 0)) {
			IDecisionCategories ct = tree.getCategories();
			CategoriesRenderer r = new CategoriesRenderer(ct);
			int[] c = new int[ct.size() + 1];
			c[0] = ColorLib.rgb(250,250,250);
			for (int i=0; i < ct.size();i++) {
				Color clr = r.getShowColor(i);
				c[i+1] = clr.getRGB();
			}	
			return c;
		} else
		*/ 
		    return  new int[] {
			         ColorLib.rgb(250,250,250), ColorLib.rgb(100,100,255),
			         ColorLib.rgb(100,255,100),ColorLib.rgb(255,100,100),
			         ColorLib.rgb(100,255,255),ColorLib.rgb(255,100,255),
			         ColorLib.rgb(255,255,100),ColorLib.rgb(100,100,100),
			         ColorLib.rgb(0,255,0)
			     };
	
	}
    private VisualGraph initGraph(Workflow workflow) throws Exception {
        graph = new Graph(true);
        /*
        public Graph(Table nodes, Table edges, boolean directed,
                String nodeKey, String sourceKey, String targetKey)
                */
        Workflow2PrefuseGraph tg = new Workflow2PrefuseGraph(graph);
        graph = tg.write(workflow);
        VisualGraph vg =  m_vis.addGraph(GRAPH, graph);
        //vg.addColumn(LABEL,"CONCAT([id], [name])");
        return vg;
    }
	
    public Display getOverview() {
        // overview display
        Display overview = new Display(m_vis);
        overview.setSize(290,290);
        overview.addItemBoundsListener(new FitOverviewListener());
        return overview;
    }
    

    public void addSelectRuleListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public JPopupMenu getPopupMenu() {
        // TODO Auto-generated method stub
        return null;
    }
    public Action getHelpAction() {
    	ImageIcon icon = null;
    	try {
    		icon = Utils.createImageIcon("images/help.png");
    	} catch (Exception x) {
    		
    	}
        Action a = new AbstractAction("Help",icon) {
            
            public void actionPerformed(ActionEvent arg0) {
                showHelp();
            }
        };
        a.putValue(Action.SHORT_DESCRIPTION, getHelp());
        return a;
    }
    protected String getHelp() {
        StringBuffer b = new StringBuffer();
        b.append("<html>");
        b.append("<ul>");
        b.append("<li>");
        b.append("<b>Panning: </b>Pans the display, changing the viewable region of the visualization.");
        b.append("<br>Panning is accomplished by clicking on the background of a");
        b.append(" visualization with the left mouse button and then dragging.");
        b.append("<li>");
        b.append("<b>Zooming: </b>");
        b.append("Zooms the display, changing the scale of the viewable region.<br>");
        b.append("Zooming is achieved by pressing the right mouse button on the background");
        b.append("of the visualization and dragging the mouse up or down. <br>Moving the mouse up");
        b.append("zooms out the display around the spot the mouse was originally pressed.");
        b.append("<br>Moving the mouse down similarly zooms in the display, making items larger" );
        b.append("<br>Also the display might be zoomed by using the mouse scroll wheel, changing the scale of the viewable region.");
        b.append("<li>");
        b.append("<b>Dragging: </b>Changes a node's location when dragged on screen. ");
        b.append("<li>");
        b.append("<b>Fit to screen: </b>Zooms a display such that all items within a given group will fit within ");
        b.append("the display bounds.<br> Click the right mouse button once, with no dragging.");        
        b.append("</ul>");
        b.append("</html>");
        return b.toString();
    }
    protected void showHelp() {
        JOptionPane.showMessageDialog(this, getHelp(), "Help",JOptionPane.PLAIN_MESSAGE, null);        
    }
}

class FitOverviewListener implements ItemBoundsListener {
    private Rectangle2D m_bounds = new Rectangle2D.Double();
    private Rectangle2D m_temp = new Rectangle2D.Double();
    private double m_d = 15;
    public void itemBoundsChanged(Display d) {
        d.getItemBounds(m_temp);
        GraphicsLib.expand(m_temp, 25/d.getScale());
        
        double dd = m_d/d.getScale();
        double xd = Math.abs(m_temp.getMinX()-m_bounds.getMinX());
        double yd = Math.abs(m_temp.getMinY()-m_bounds.getMinY());
        double wd = Math.abs(m_temp.getWidth()-m_bounds.getWidth());
        double hd = Math.abs(m_temp.getHeight()-m_bounds.getHeight());
        if ( xd>dd || yd>dd || wd>dd || hd>dd ) {
            m_bounds.setFrame(m_temp);
            DisplayLib.fitViewToBounds(d, m_bounds, 0);
        }
    }
}

