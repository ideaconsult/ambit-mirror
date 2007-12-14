package ambit.test.ui;


import hypergraph.graphApi.Graph;
import hypergraph.graphApi.GraphEvent;
import hypergraph.graphApi.GraphListener;
import hypergraph.graphApi.GraphSystem;
import hypergraph.graphApi.GraphSystemFactory;
import hypergraph.graphApi.Node;

import java.awt.Dimension;

import javax.swing.JOptionPane;

import ambit.ui.GraphPanel;

import junit.framework.TestCase;

public class HypergraphTest extends TestCase {


protected void createNodes(Graph graph, Node node, int degree, int level) {
	if (level>3) return;
	for (int i=0; i < degree;i++) 
		try {
			Node newNode = graph.createNode(node.getName() + "." + Integer.toString(i+1));
            //Node newNode = graph.createNode(node.getName() + ".L" + Integer.toString(level+1));
			graph.addElement(newNode);
			graph.createEdge(node,newNode);
			createNodes(graph,newNode,degree,level+1);
		} catch (Exception x) {
			
		}
		
}
public void test() {
	try {
		GraphSystem graphSystem = GraphSystemFactory.createGraphSystem("hypergraph.graph.GraphSystemImpl", null);
		Graph graph = graphSystem.createGraph();
		
		Node node1 = graph.createNode("M1");
		graph.addElement(node1);
		createNodes(graph,node1,4,0);
		
		Node node2 = graph.createNode("M2");
		graph.addElement(node2);
		createNodes(graph,node2,2,0);

        Node node3 = graph.createNode("M3");
        graph.addElement(node3);
        createNodes(graph,node3,3,0);
        
        Node node4 = graph.createNode("M4");
        graph.addElement(node4);
        createNodes(graph,node4,2,0);
        
        
		graph.createEdge(node1, node2);
        graph.createEdge(node3, node2);
        graph.createEdge(node4, node3);
        graph.createEdge(node2, node4);
		
		graph.createEdge((Node)graph.getElement("M2.1"), (Node)graph.getElement("M1.4"));		
		//graph.createEdge((Node)graph.getElement("B.2.1"), (Node)graph.getElement("A.2.1"));
		//graph.createEdge((Node)graph.getElement("AE2.1.1"), (Node)graph.getElement("AE1.1.1"));
		
		System.out.println(graph.getNodes().size());
		
		
		/*
		Node node3 = graph.createNode("name3");
		Node node4 = graph.createNode("name4");
		graph.addElement(node1);
		graph.addElement(node2);
		graph.addElement(node3);
		graph.createEdge(node1, node2);
		graph.createEdge(node1, node3);
		graph.createEdge(node1, node4);
		graph.createEdge(node2, node4);
		*/
		GraphPanel graphPanel = new GraphPanel(graph);
		graphPanel.setPreferredSize(new Dimension(400,400));
		JOptionPane.showMessageDialog(null,graphPanel,"Visualization",JOptionPane.PLAIN_MESSAGE);
		
	} catch (Exception e) {
		e.printStackTrace();
	}
	
}
}
