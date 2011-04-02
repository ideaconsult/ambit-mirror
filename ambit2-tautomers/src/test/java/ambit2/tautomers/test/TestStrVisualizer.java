package ambit2.tautomers.test;

import java.util.Vector;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.templates.MoleculeFactory;


import javax.swing.*;
import ambit2.ui.editors.Panel2D;


public class TestStrVisualizer 
{
	JFrame frame;
	int nStr = 0;
	int nCol = 10;	
	int size = 100;
	
	public static void main(String[] args) 
	{
		Vector<IAtomContainer> structs = new Vector<IAtomContainer> ();
		structs.add(MoleculeFactory.make123Triazole());
		structs.add(MoleculeFactory.makeAdenine());
		TestStrVisualizer tsv = new TestStrVisualizer(structs); 
		
	}
	
	TestStrVisualizer()
	{	
	}
	
	TestStrVisualizer(Vector<IAtomContainer> structs)
	{
		setFrame();
		for (int i = 0; i < structs.size(); i++)
			addStructure(structs.get(i));
	}
	
	
	void setFrame()
	{
		frame = new JFrame();
		frame.setSize(1200, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setVisible(true);
	}
	
	void addStructure(IAtomContainer struct)
	{	
		Panel2D p = new Panel2D();
		p.setAtomContainer(struct);
		frame.getContentPane().add(p);
		p.setBounds((nStr%nCol)*size,(nStr/nCol)*size,size,size);
		frame.setVisible(true);
		nStr++;
	}
	
}
