package ambit2.dragon;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DragonDescriptorDictionary {
	protected static Logger logger = Logger.getLogger(DragonDescriptorDictionary.class.getName());
	protected List<DragonDescriptor> descriptors = new ArrayList<DragonDescriptor>();
	protected List<Block> blocks = new ArrayList<Block>();
	protected List<Block> subBlocks = new ArrayList<Block>();
	
	public List<Block> getSubBlocks() {
		return subBlocks;
	}

	public List<Block> getBlocks() {
		return blocks;
	}

	public DragonDescriptorDictionary() {
		super();
		initialize();
	}
	
    public String[] getDescriptorNames() {
    	List<String> names = new ArrayList<String>();
		for (DragonDescriptor d : descriptors)
			if (d.isSelected()) names.add(d.toString());
		
		return names.toArray(new String[names.size()]);
	}	
	public int setSelected(Object[] names) {
		int n = 0;
		if ((names==null) || names.length==0) {
			for (DragonDescriptor descriptor:descriptors) {descriptor.setSelected(true);n++;};
			return n;
		}
		Arrays.sort(names);
		for (DragonDescriptor descriptor:descriptors) {
			if (Arrays.binarySearch(names,descriptor.getName())>=0) {
				descriptor.setSelected(true);
				n++;
			} else if (Arrays.binarySearch(names,descriptor.getBlock().getName())>=0) {
				descriptor.setSelected(true);
				n++;
			} else descriptor.setSelected(false); 
		}		
		return n;
	}
	protected void initialize() {
	       InputStream in =null;
	  		try {
	  			in = DragonDescriptorDictionary.class.getClassLoader().getResourceAsStream("ambit2/dragon/descriptors.txt");
	  	      	BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	  	        String line;
	  	        int i=0;
	  	        while ((line = reader.readLine())!=null) {
	  	        	i++;
	  	        	if (i==1) continue;
	  	        	String[] columns = line.split("\t");
	  	        	
	  	        	Block block = new Block(columns[4], Integer.parseInt(columns[3]));
	  	        	int id = blocks.indexOf(block);
	  	        	if (id<0) { blocks.add(block); } else block = blocks.get(id);
	  	        	
	  	        	Block subblock = new Block(columns[5], Integer.parseInt(columns[6]));
	  	        	id = subBlocks.indexOf(subblock);
	  	        	if (id<0) { subBlocks.add(subblock); } else subblock = subBlocks.get(id);
	  	        	
	  	        	descriptors.add(new DragonDescriptor(block, subblock, columns[1]));
	  	        }
	  	        reader.close();
	  		} catch (Exception x) {
	  			logger.log(Level.WARNING,"initialize()",x);
	  		} finally {
	  			try {
	  				in.close();
	  			} catch (Exception x) {}
	  		}
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (DragonDescriptor d : descriptors) {
			b.append(d);
			b.append("\n");
		}
		return b.toString();
	}
	
    public String getDescriptorNamesAsString() {
    	return compress(false);
	}	
    
    public String toXML() {
    	return compress(true);
    }
    
	public String compress(boolean asXML) {
		StringBuilder b = new StringBuilder();
		StringBuilder d = new StringBuilder();
		Block block = null;
		int count = 0;
		int selected = 0;
		if (asXML) b.append("\n");
		for (DragonDescriptor descriptor : descriptors) {
			if ((block==null) || (descriptor.getBlock().getId()!=block.getId())) {
				if (block != null) {
					if (selected==0) {} //skip
					else if (selected==count) //all selected
						if (asXML)
							b.append(String.format("<block id=\"%d\" SelectAll=\"true\"/>\n",block.getId()));
						else
							b.append(String.format("%s\t",block.getName()));
					else {
						if (asXML) {
							b.append(String.format("\n<block id=\"%d\">\n",block.getId()));
							b.append(d.toString());
							b.append("</block>\n");
						} else 
							b.append(d.toString());
					}
				}
				count=0; selected = 0;		
				d = new StringBuilder();
			}
			count++;
			if (descriptor.isSelected()) {
				if (asXML)
					d.append(String.format("<descriptor name=\"%s\"/>\n",descriptor.getName()));
				else
					d.append(String.format("%s\t",descriptor.getName()));
				selected++;
			}
			block = descriptor.getBlock();
		}
		//final
		if (selected==0) {} //skip
		else if (selected==count) //all selected
			if (asXML)
				b.append(String.format("<block id=\"%d\" SelectAll=\"true\"/>\n",block.getId()));
			else
				b.append(String.format("%s\t",block.getName()));
		else {
			if (asXML) {
				b.append(String.format("\n<block id=\"%d\">\n",block.getId()));
				b.append(d.toString());
				b.append("</block>\n");
			} else 
				b.append(d.toString());
		}
		return b.toString();
	}
	
	
}


class Block implements Comparable<Block> {
	protected String name;
	protected int id;
	public String getName() {
		return name;
	}
	public int getId() {
		return id;
	}
	public Block(String name,int id) {
		super();
		this.name = name;
		this.id = id;
	}
	@Override
	public String toString() {
		return name;
	}
	@Override
	public int compareTo(Block o) {
		int r = getId() - o.getId();
		if (r==0) return getName().compareTo(o.getName());
		else return r;
	}
	@Override
	public boolean equals(Object obj) {
		Block d = (Block) obj;
		return (d.getId()==getId()) &&(d.getName().equals(getName()));
	}
	
}
class DragonDescriptor implements Comparable<DragonDescriptor> {
	protected Block block;
	protected Block subblock;
	public Block getSubblock() {
		return subblock;
	}

	public Block getBlock() {
		return block;
	}

	protected String name;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	protected boolean selected = true;
	
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public DragonDescriptor(Block block, Block subblock, String name) {
		super();
		this.name = name;
		
		this.subblock = subblock;
		this.block = block;
		
	}
	@Override
	public String toString() {

		return String.format("%s.%s.%s", block,subblock,name);
	}

	@Override
	public int compareTo(DragonDescriptor o) {
		return toString().compareTo(o.toString());
	}
	@Override
	public boolean equals(Object obj) {
	
		return toString().equals(obj.toString());
	}

}

