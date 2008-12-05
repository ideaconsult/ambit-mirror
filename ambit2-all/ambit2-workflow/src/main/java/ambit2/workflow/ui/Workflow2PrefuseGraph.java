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

import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;

import com.microworkflow.process.Activity;
import com.microworkflow.process.Workflow;

public class Workflow2PrefuseGraph extends WorkflowTools<Node> {
	   protected Graph graph;
	    public static final String FIELD_ID = "id";
	    public static final String FIELD_SHORT_NAME = "short";
	    public static final String FIELD_NAME = "name";
	    public static final String FIELD_EXPLANATION = "explanation";
	    public static final String FIELD_NODETYPE = "nodetype";
	    public static final String FIELD_ANSWER = "answer";

	    
	    public Workflow2PrefuseGraph(Graph graph) {
	        this.graph = graph;
	    }


	    public void init() {
	        graph.addColumn(Workflow2PrefuseGraph.FIELD_ID, String.class);
	        graph.addColumn(Workflow2PrefuseGraph.FIELD_SHORT_NAME, String.class);
	        graph.addColumn(Workflow2PrefuseGraph.FIELD_NAME, String.class);
	        graph.addColumn(Workflow2PrefuseGraph.FIELD_NODETYPE, String.class);
	        graph.addColumn(Workflow2PrefuseGraph.FIELD_EXPLANATION, String.class);
	        graph.addColumn(Workflow2PrefuseGraph.FIELD_ANSWER, Boolean.class);
	   }

	    public Node process(Activity[] parentActivity, Activity activity)  {

	    	Node n1 = null;
	    	for (int i=0; i < graph.getNodeCount();i++) {
	    		Node n = graph.getNode(i);
	    		if ((n.getString(FIELD_ID).equals(activity.getName()))) {
	    			n1 = graph.getNode(i);
	    			break;
	    		}
	    	}
	    	if (n1 == null) {
		        n1 = graph.addNode(); 
		        n1.setString(FIELD_ID, activity.getName());
		        n1.setString(FIELD_NODETYPE,activity.getClass().getName());
		        n1.setString(FIELD_EXPLANATION,activity.getClass().getName());
		        //n1.set(FIELD_RULE, rule);
	    	}
	    	
	        if (parentActivity != null) {
	        	for (Activity a : parentActivity) {
		        	Node parentNode = null;
			    	for (int i=0; i < graph.getNodeCount();i++) {
			    		Node n = graph.getNode(i);
			    		if ((n.getString(FIELD_ID).equals(a.getName()))) {
			    			parentNode = graph.getNode(i);
			    			Edge edge = getGraph().addEdge(parentNode, n1);
			    			/*
	                        if (answers[i])
	                        	edge.setString(FIELD_ANSWER, AbstractRule.MSG_YES);
	                        else
	                        	edge.setString(FIELD_ANSWER, AbstractRule.MSG_NO);
	                        edge.setString(FIELD_EXPLANATION,edge.getString(FIELD_ANSWER));
	                        */   			    			
			    			break;
			    		}
			    	}
	        	}
	        } 
        
	        return n1;
	    }
		public Graph getGraph() {
			return graph;
		}
		public void setGraph(Graph graph) {
			this.graph = graph;
		}

	   public Activity[]  walkRules(Workflow workflow) {
		        init();
		        return traverseActivity(null,workflow.getDefinition(), 0, true);
	   }
	
}


