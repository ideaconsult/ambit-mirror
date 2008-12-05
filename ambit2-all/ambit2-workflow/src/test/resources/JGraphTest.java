package ambit2.workflow.test;


import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

import ambit2.workflow.ui.WorkflowTools;

import com.microworkflow.process.Activity;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.Workflow;

public class JGraphTest extends WorkflowTools<DefaultGraphCell> {
	protected JGraph graph;
	protected GraphModel model;	
	protected ArrayList<DefaultGraphCell> cells;
	public JGraphTest() {
		init();
	}
	protected DefaultGraphCell getCellByActivity(Activity activity) {
    	for (int i=0; i < cells.size();i++) {
    		DefaultGraphCell n = cells.get(i);
    		if ((n.getUserObject()!= null) && (n.getUserObject().equals(activity.getName()))) {
    			return n;
    		}
    	}
    	return null;
	}
    public DefaultGraphCell process(Activity[] parentActivity, Activity activity)  {

    	DefaultGraphCell n1 = getCellByActivity(activity);
    	if (n1 == null) {
	        n1 = new DefaultGraphCell(new String(activity.getName())); 
			DefaultPort port1 = new DefaultPort();
			n1.add(port1);	        
			GraphConstants.setBounds(n1.getAttributes(), new Rectangle2D.Double(cells.size(),cells.size()*20,40,20));
			GraphConstants.setGradientColor(n1.getAttributes(),(activity instanceof Primitive)?Color.orange:Color.blue);
			GraphConstants.setOpaque(n1.getAttributes(), true);		
			GraphConstants.setAutoSize(n1.getAttributes(), true);
	        cells.add(n1);
    	}
    	
        if (parentActivity != null) {
        	for (Activity a : parentActivity) {
	        	DefaultGraphCell parentNode = null;
	        	for (int i=0; i < cells.size();i++) {
	        		DefaultGraphCell n = cells.get(i);
	        		if ((n.getUserObject()!= null) && (n.getUserObject().equals(a.getName()))) {
		    			parentNode = cells.get(i);
		    			DefaultEdge edge = new DefaultEdge();
		    			edge.setSource(parentNode.getChildAt(0));
		    			edge.setTarget(n1.getChildAt(0));	
		    			int arrow = GraphConstants.ARROW_CLASSIC;
		    			GraphConstants.setLineEnd(edge.getAttributes(), arrow);
		    			GraphConstants.setEndFill(edge.getAttributes(), true);		
		    			GraphConstants.setRouting(edge.getAttributes(), GraphConstants.ROUTING_DEFAULT);
		    			cells.add(edge);
		    			break;
		    		}
		    	}
        	}
        } 
    
        return n1;
    }
    protected void init() {
    	cells = new ArrayList<DefaultGraphCell>();
		model = new DefaultGraphModel();
		GraphLayoutCache view = new GraphLayoutCache(model, new	DefaultCellViewFactory());
		graph = new JGraph(model, view);
    	
		Workflow workflow = WorkflowTools.createWorkflow();
        traverseActivity(null,workflow.getDefinition(), 0, true);
        graph.getGraphLayoutCache().insert(cells.toArray());
        
		JFrame frame = new JFrame();
		frame.getContentPane().add(new JScrollPane(graph));
		frame.pack();
		frame.setVisible(true);    	
    }
    public void show() {
    	
    }
    public static void main(String[] args) {
    	JGraphTest test = new JGraphTest();
    	test.show();
    }
	public static void main_example(String[] args) {
		GraphModel model = new DefaultGraphModel();
		GraphLayoutCache view = new GraphLayoutCache(model, new	DefaultCellViewFactory());
		JGraph graph = new JGraph(model, view);
		DefaultGraphCell[] cells = new DefaultGraphCell[3];
		cells[0] = new DefaultGraphCell(new String("Hello"));
		GraphConstants.setBounds(cells[0].getAttributes(), new Rectangle2D.Double(20,20,40,20));
		GraphConstants.setGradientColor(cells[0].getAttributes(),Color.orange);
		GraphConstants.setOpaque(cells[0].getAttributes(), true);
		DefaultPort port0 = new DefaultPort();
		cells[0].add(port0);
		cells[1] = new DefaultGraphCell(new String("World"));
		GraphConstants.setBounds(cells[1].getAttributes(), new Rectangle2D.Double(140,140,40,20));
		GraphConstants.setGradientColor(cells[1].getAttributes(),Color.red);
		GraphConstants.setOpaque(cells[1].getAttributes(), true);
		DefaultPort port1 = new DefaultPort();
		cells[1].add(port1);
		DefaultEdge edge = new DefaultEdge();
		edge.setSource(cells[0].getChildAt(0));
		edge.setTarget(cells[1].getChildAt(0));
		cells[2] = edge;
		int arrow = GraphConstants.ARROW_CLASSIC;
		GraphConstants.setLineEnd(edge.getAttributes(), arrow);
		GraphConstants.setEndFill(edge.getAttributes(), true);
		graph.getGraphLayoutCache().insert(cells);
		JFrame frame = new JFrame();
		frame.getContentPane().add(new JScrollPane(graph));
		frame.pack();
		frame.setVisible(true);
				
	}
}
