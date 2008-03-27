package ambit2.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;

import ambit2.config.AmbitCONSTANTS;
import ambit2.smiles.SmilesParserWrapper;
import ambit2.ui.editors.IdentifiersProcessor;
import ambit2.data.molecule.SourceDataset;
import ambit2.database.search.DbExactSearchReader;
import ambit2.database.search.DbSearchReader;
import ambit2.database.search.DbSimilarityByAtomenvironmentsReader;
import ambit2.database.search.DbSimilarityByFingerprintsReader;
import ambit2.database.search.DbSubstructureSearchReader;
import ambit2.exceptions.AmbitException;

/**
 * Servlet, implementing server side similarity search given a structure.
 * Valid parameters are:
 * smiles  (SMILES string, optional) <br>
 * mol  (MOL file format representation of a compound, optional) <br>
 * page  (starting page, default =0 (first), optional) <br>
 * pagesize  (number of records per page, default =1000, optional) <br>
 * Datasets (id_srcdataset from table src_dataset) Dataset to search within, entire database if missing <br>
 * similarity = {fingerprint,atomenvironment,substructure}  ,mandatory) <br>
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 29, 2006
 */
public class AmbitSimilarityServlet extends AmbitServlet {
	protected static final String[] columns = new String[] {"similarity","idsubstance","formula","smiles" };
	protected static final String[] columnsSubSearch = new String[] {"idsubstance","formula","smiles" };
	public static final String[] columnsExactSearch = new String[] {"idsubstance","casno","chemname","formula","smiles","dataset" };
	protected static final int columnsNo = 3; 
	
	public AmbitSimilarityServlet() {
		super();
		// TODO Auto-generated constructor stub
	}
	protected void process(Connection connection,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        
        
        PrintWriter out = response.getWriter();
        printHeaders(out,"../../styles/pstyle.css","details");

        int page = 0;
        int pagesize = 100;
        double threshold = 0.5;
        String smiles = "";
        String name=null;
        String alias=null;
        String formula=null;
        String cas=null;
        Object datasets = "";
        IMolecule mol = null;
        StringBuffer condition = new StringBuffer();
        SourceDataset srcDataset = null;
        try 
        { 
        	datasets = request.getParameter("Datasets");
        	if ((datasets != null)  && (!datasets.toString().equals(""))) {
        		try {
        			
        			int id_src = Integer.parseInt(datasets.toString());
        			srcDataset = new SourceDataset();
        			srcDataset.setId(id_src);
            		datasets = "id_srcdataset = " + datasets;
        			
        		} catch (Exception x) {
        			x.printStackTrace(out);
        			srcDataset = null;
        			datasets = "";
        		}
        	} else {
        		srcDataset = null;
        		datasets = "";
        	}
            smiles = request.getParameter("smiles");
            if ((smiles != null)  && (!smiles.equals(""))) 
            	try {
            		SmilesParserWrapper p = SmilesParserWrapper.getInstance();
	            	mol = p.parseSmiles(smiles.trim());
            	} catch (InvalidSmilesException x) {
            		System.out.println(x);
            		mol = new Molecule();
            		smiles = null;
            	}
            String molecule = request.getParameter("mol");
            if ((molecule != null)  && (!molecule.equals(""))) {
            	//out.println(molecule);
            	//out.println("<hr>");
            	molecule = URLDecoder.decode(molecule);
            	//out.println(molecule);
            	MDLReader reader = new MDLReader(new StringReader(molecule));
            	try {
                ChemFile chemFile = (ChemFile) reader.read(new ChemFile());
                IChemSequence seq = chemFile.getChemSequence(0);
                IChemModel model = seq.getChemModel(0);
                IMoleculeSet som = model.getMoleculeSet();
                IMolecule newMol = som.getMolecule(0);
                
                SmilesGenerator g = new SmilesGenerator();
                try {
                	smiles = g.createSMILES(newMol);
                } catch (Exception x ) {
                	smiles = null;
                }
                g = null;
                
            	if ((newMol != null) && (newMol.getAtomCount() > 0))
            			mol = newMol;
            	} catch (Exception x) {
            		mol = new Molecule();
            		x.printStackTrace(out);
            	}

            } else mol = new Molecule();
            
            String param = request.getParameter("pagesize");
            if ((param != null)  && (!param.equals(""))) {
            	try {
            		pagesize = Integer.parseInt(param.trim());
            		if (pagesize > 500) pagesize = 500;
            	} catch (Exception x) {
            		pagesize = 500;
            	}
            }
            param = request.getParameter("page");
            if ((param != null)  && (!param.equals(""))) {
            	try {
            		page = Integer.parseInt(param.trim());
            		if (page < 0) page = 0;
            	} catch (Exception x) {
            		page = 0;
            	}
            }        
            param = request.getParameter("threshold");
            if ((param != null)  && (!param.equals(""))) {
                try {
                    threshold = ((Number)DecimalFormat.getInstance().parse(param.trim())).doubleValue();
                } catch (Exception x) {
                    threshold = 0;
                }
            }    
            param  = request.getParameter("name");
            if ((param != null)  && (!param.equals(""))) {
            	name = param.toString();
            }
            param  = request.getParameter("identifier");
            if ((param != null)  && (!param.equals(""))) {
            	alias = param.toString();
            }
            
            param  = request.getParameter("cas");
            if ((param != null)  && (!param.equals(""))) {
            	cas = param.toString();
            }            

            param  = request.getParameter("formula");
            if ((param != null)  && (!param.equals(""))) {
            	formula = getCanonicFormula(param.toString());
            }
            

        } catch (Exception x) {
        	x.printStackTrace(out);
        }

        if (mol != null) {
        	String similarity = request.getParameter("similarity");
        	DbSearchReader reader = null;
            try {
            	if (similarity.equals("exact")) {
            		if (name != null)
            			mol.setProperty(CDKConstants.NAMES,name);
            		if (cas != null)
            			mol.setProperty(CDKConstants.CASRN,IdentifiersProcessor.hyphenateCAS(cas));
            		if (formula != null)
            			mol.setProperty(AmbitCONSTANTS.FORMULA,formula);
            		if ((smiles != null) && (!("".equals(smiles))))
            			mol.setProperty(AmbitCONSTANTS.SMILES,smiles);            		
            		
                    reader = new DbExactSearchReader(
                            connection,
                            mol,
                            srcDataset,
                            page,pagesize); 
                    dbSimilarity(reader,mol,out,columnsExactSearch);            	
            	} else if (similarity.equals("fingerprint")) {
                    reader = new DbSimilarityByFingerprintsReader(
                            connection,
                            mol,
                            srcDataset,
                            threshold,
                            page,pagesize); 
                    dbSimilarity(reader,mol,out);
            	} else if (similarity.equals("atomenvironment")) {
                       reader = new DbSimilarityByAtomenvironmentsReader(
                                connection,
                                mol,
                                srcDataset,
                                threshold,
                                page,pagesize);            
                       dbSimilarity(reader,mol,out);
            	} else if (similarity.equals("substructure")) {
            	    try {
                        reader = new DbSubstructureSearchReader(connection,
                                mol,
                                srcDataset,
                                threshold,
                                page,pagesize);     
    	                out.print("Looking for substructure ");
    	                out.println(getImageURL(smiles,"",""));
    	                out.print("<p>");
    	        		dbSubstructureSearch(reader,mol,out);   
    	        		
            	    } catch (CloneNotSupportedException x) {
            	        out.print(x.getMessage());
            	        out.print("<p>");    
            	    }
            	} else {
                    reader = new DbSimilarityByFingerprintsReader(
                            connection,
                            mol,
                            srcDataset,
                            threshold,
                            page,pagesize);
                    dbSimilarity(reader,mol,out);
            	}
                
            } catch (Exception x) {
                System.out.println(x.getMessage());
            }
            finally {
                reader.close();
            }
            
        }	
        printFooters(out);

	}
	public long dbSimilarity(DbSearchReader reader,
			IMolecule mol, 
			PrintWriter out
            ) throws AmbitException {
		return dbSimilarity(reader, mol, out, columns);
	}	
	public long dbSimilarity(DbSearchReader reader,
			IMolecule mol, 
			PrintWriter out,
			String[] columns
            ) throws AmbitException {
		long now = System.currentTimeMillis();
        //out.print("Looking for similar structures to ");
        Object smiles = mol.getProperty(AmbitCONSTANTS.SMILES);
        if (smiles == null) {
        	if (sg == null) sg = new SmilesGenerator();
        	smiles = sg.createSMILES(mol);
        }
        if (smiles != null)
        	out.println(getImageURL(smiles.toString(),"",""));
        out.print(" by ");
        out.print(reader.toString());
        out.print("<p>");
        
		ResultSet resultset = reader.getResultset();
		
		if (resultset == null) return 0;
		out.println(printTableHeader(columns).toString());
		try {
			out.println(printTableRows(resultset, columns));
			
			resultset.close();
			resultset = null;
			reader.close();
			reader = null;
		} catch (IOException x) {
			out.println("</table>");
			throw new AmbitException(x);
			
		} catch (SQLException x) {
			out.println("</table>");
			throw new AmbitException(x);
		} finally {
			
			out.println("</table>");	
		}
	 	//System.out.print(this.getClass().getName());
	 	//System.out.print("\tSTOP AE SIMILARITY\t");
	 	//System.out.println;
        String stats = "<br>Retrieved in "+(System.currentTimeMillis()-now)/1000 + " seconds<br>";
		return System.currentTimeMillis()-now;
	}
    
	public void dbSubstructureSearch(DbSearchReader reader,IMolecule mol,PrintWriter out) throws Exception {
		long time = System.currentTimeMillis();
		int nrow = 0;
		out.println(printTableHeader(columnsSubSearch));
			//out.println(printTableRows(rs,columnsSubSearch));

		while( reader.hasNext() ) {
				Object c = reader.next();
                if (c == null) continue;
				try {
						nrow++;
						out.println(printRow(nrow,reader.getResultset(),columnsSubSearch));
				} catch (Exception x) {
					x.printStackTrace();
				}
		}	
				
		out.println("</table>");

	}
	
	protected StringBuffer printTableHeader(String[] columns) {
		StringBuffer out = new StringBuffer();		
		//out.append("<table width=100% border=0 bgcolor='#428EFF' class='table'>\t<th>");
		out.append("<table width=100% border=0 bgcolor='#FFFFFF' class='table'>\t<th>");
		//for (int i=0; i < columnsNo;i++)
			//out.append("<td>Structure</td>");
		out.append("</th>");
		return out;
//		out.println("\t<td><a href=http://www.daylight.com/dayhtml/smiles/ TARGET=\"_blank\">SMILES</a></td></th>");         
	}
	
	protected StringBuffer printTableRows(ResultSet rs,String[] columns) throws SQLException {
		StringBuffer out = new StringBuffer();
		int nline = 0;
		while(rs.next()) {
			   nline++;
			   if ((nline % columnsNo) == 1)
				   out.append("\t<tr bgcolor='#FFFFFF'>"); 
			   
			   out.append("<td>");  out.append(nline);  out.append(".<br>\n");
			   
			   int idsubstance = -1;
			   for (int i=0; i < columns.length;i++) 
				   if (columns[i].equals("idsubstance")) { idsubstance = i; break;}
				   
			   int i = 0;
			   
			   
			   
			   while ( i < columns.length) {

				   if (columns[i].equals("idsubstance")) {
				   } else if (columns[i].toLowerCase().equals("smiles")) {
					      out.append("<b>");
					   	  out.append(columns[i]);
					   	  out.append("</b>");
					   	  out.append('\t');
					   	  out.append(displayField(rs.getObject(columns[i])));
					   	  out.append("<br>");
                        
                          Object id = rs.getObject("idsubstance");
					   	  out.append(getDisplayURL(id,displayField(idsubstance)));
                          
						  out.append(getImageURL(displayField(rs.getObject(columns[i])).toString(),id.toString(),""));
						  out.append("</a>");
						  out.append("<br>\n");
					   } else {
						   out.append("<b>");
						   out.append(columns[i]);
						   out.append("</b>");
						   out.append('\t');
						   out.append(displayField(rs.getObject(columns[i])));
						   out.append("<br>\n");
					   }

				   
				   i++;
			   }	  
			   out.append("</td>");
			   if ((nline % columnsNo) == (columnsNo))
				   out.append("</tr>\n");
		}
		return out;
	}
	protected StringBuffer printRow(int row,ResultSet rs,String[] columns) throws SQLException {
		   StringBuffer out = new StringBuffer();
		   if ((row % columnsNo) == 1)
		   out.append("\t<tr bgcolor='#FFFFFF'>"); 
		   
		   out.append("<td>");  out.append(row); out.append(".<br>\n"); 
		   int i = 0;
		   while ( i < columns.length) {
			   if (columns[i].equals("idsubstance")) {
				   /*
				   out.append(columns[i+1]);
				   out.append('\t');				 				   
				   out.append("<a href='http://ambit2.acad.bg/ambit/php/display2.php?id=");
				   out.append(displayField(rs.getObject("idsubstance")));
				   out.append("'>");
				   i++;
				   out.append(displayField(rs.getObject(columns[i])));
				   out.append("</a>");
				   */         
			   } else if (columns[i].toLowerCase().equals("smiles")) {
				  out.append("<b>");
				  out.append(columns[i]);
				  out.append("</b>");
				  out.append('\t');
				  out.append(displayField(rs.getObject(columns[i])));
				  out.append("<br>");
				  
				  String idsubstance = rs.getString("idsubstance");
				  out.append(getDisplayURL(idsubstance,displayField(idsubstance)));
				  out.append(getImageURL(displayField(rs.getObject(columns[i])).toString(),idsubstance,""));
				  out.append("</a>");
				  out.append("<br>\n");
			   } else {
				   out.append("<b>");
				   out.append(columns[i]);
				   out.append("</b>");
				   out.append('\t');
				   out.append(displayField(rs.getObject(columns[i])));
				   out.append("<br>\n");
			   }
			   
			   i++;
		   }	
		   out.append("</td>\n");
		   if ((row % columnsNo) == (columnsNo))
			   out.append("</tr>\n");
		   return out;
		
	}	
}
