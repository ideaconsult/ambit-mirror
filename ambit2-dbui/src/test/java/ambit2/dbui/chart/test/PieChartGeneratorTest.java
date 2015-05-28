package ambit2.dbui.chart.test;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Connection;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.idea.modbcum.c.DatasourceFactory;

import org.junit.Test;

import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.db.chart.FuncGroupsChartGenerator;
import ambit2.db.chart.Nominal2SimilarityChartGenerator;
import ambit2.db.chart.Numeric2SimilarityChartGenerator;
import ambit2.db.chart.PieChartGenerator;
import ambit2.db.chart.PropertiesChartGenerator;
import ambit2.db.chart.XTabChartGenerator;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.StoredQuery;
import ambit2.dbui.test.RepositoryTest;

public class PieChartGeneratorTest extends RepositoryTest  {
	
	protected void initDatasource()  throws Exception {
		datasource = DatasourceFactory.getDataSource(null,
				DatasourceFactory.getConnectionURI(
						"jdbc:mysql", 
						"localhost", "3306", "ambit2", "guest","guest" ),"com.mysql.jdbc.Driver");		
	}	
	
	public Image test() {
		Connection c = null;
		try {
			initDatasource();
			c = datasource.getConnection();
			//IStoredQuery q = new StoredQuery(6);
			SourceDataset dataset = new SourceDataset();
			dataset.setId(6);
			PieChartGenerator gen = new PieChartGenerator();
			gen.setConnection(c);
			Property p = Property.getInstance("toxTree.tree.cramer.CramerRules", "");
			p.setId(12361);
			gen.setProperty(p);
			
			return gen.process(dataset);

		} catch (Exception x) {
			x.printStackTrace();
			return null;
		} finally {
			try {c.close(); } catch (Exception x) {}
		}
	}
	
	public Image testScatterPlot() {
		Connection c = null;
		try {
			initDatasource();
			c = datasource.getConnection();
			SourceDataset q = new SourceDataset(); q.setId(78);
			PropertiesChartGenerator gen = new PropertiesChartGenerator();
			gen.setConnection(c);
			Property p = Property.getInstance("XLogP", ""); p.setId(13993);
			gen.setPropertyX(p);
			p = Property.getInstance("LLNA EC3 %", ""); p.setId(13995);
			gen.setPropertyY(p);
			
			return gen.process(q);

		} catch (Exception x) {
			x.printStackTrace();
			return null;
		} finally {
			try {c.close(); } catch (Exception x) {}
		}
	}	
	
	public Image testMetricNominalPlot() {
		Connection c = null;
		try {
			initDatasource();
			c = datasource.getConnection();
			IStoredQuery q = new StoredQuery(2);
			Nominal2SimilarityChartGenerator gen = new Nominal2SimilarityChartGenerator();
			gen.setConnection(c);
			gen.setProperty(Property.getInstance("Inventories", ""));
			
			return gen.process(q);

		} catch (Exception x) {
			x.printStackTrace();
			return null;
		} finally {
			try {c.close(); } catch (Exception x) {}
		}
	}
	
	@Test
	public Image testPropertyHistogramPlot() {
		Connection c = null;
		try {
			initDatasource();
			c = datasource.getConnection();
			IStoredQuery q = new StoredQuery(13);//this is dataset now
			q.setName("IRISTR: EPA Integrated Risk Information System (IRIS) Toxicity Review Data");
			XTabChartGenerator gen = new XTabChartGenerator();
			gen.setBinWidth(0.001);
			gen.setConnection(c);
			gen.setPropertyX(Property.getInstance("Inhalation_UnitRisk_micromol_per_m3", ""));
			
			//gen.setPropertyY(Property.getInstance("Cramer Class", ""));
			gen.setPropertyY(Property.getInstance("Oral_RfD_Confidence", ""));
			
			
			return gen.process(q);

		} catch (Exception x) {
			x.printStackTrace();
			return null;
		} finally {
			try {c.close(); } catch (Exception x) {}
		}
	}		
	/*
	public Image testPropertyHistogramPlot() {
		Connection c = null;
		try {
			initDatasource();
			c = datasource.getConnection();
			IStoredQuery q = new StoredQuery(6);//this is dataset now
			q.setName("Skin sensitisation (LLNA)");
			HistogramChartGenerator gen = new HistogramChartGenerator();
			gen.setBinWidth(0.1);
			gen.setConnection(c);
			gen.setPropertyX(Property.getInstance("LLNA EC3 %", ""));
			
			//gen.setPropertyY(Property.getInstance("Cramer Class", ""));
			gen.setPropertyY(Property.getInstance("Reaction domain (verbose)", ""));
			
			
			return gen.process(q);

		} catch (Exception x) {
			x.printStackTrace();
			return null;
		} finally {
			try {c.close(); } catch (Exception x) {}
		}
	}	
	*/
	/*
	public Image testPropertyHistogramPlot() {
		Connection c = null;
		try {
			initDatasource();
			c = datasource.getConnection();
			IStoredQuery q = new StoredQuery(9);//this is dataset now
			q.setName("CPDBAS: Carcinogenic Potency Database Summary Tables - All Species");
			HistogramChartGenerator gen = new HistogramChartGenerator();
			gen.setBinWidth(10);
			gen.setConnection(c);
			gen.setPropertyX(Property.getInstance("TD50_Mouse_mg", ""));
			
			//gen.setPropertyY(Property.getInstance("Cramer Class", ""));
			gen.setPropertyY(Property.getInstance("toxTree.tree.cramer.CramerRules", ""));
			
			
			return gen.process(q);

		} catch (Exception x) {
			x.printStackTrace();
			return null;
		} finally {
			try {c.close(); } catch (Exception x) {}
		}
	}	
	*/
/*
	public Image testPropertyHistogramPlot() {
		Connection c = null;
		try {
			initDatasource();
			c = datasource.getConnection();
			IStoredQuery q = new StoredQuery(1);//this is dataset now
			q.setName("Munro dataset");
			HistogramChartGenerator gen = new HistogramChartGenerator();
			gen.setBinWidth(0.15);
			gen.setConnection(c);
			gen.setPropertyX(Property.getInstance("NOEL", ""));
			
			//gen.setPropertyY(Property.getInstance("Cramer Class", ""));
			gen.setPropertyY(Property.getInstance("toxTree.tree.cramer.CramerRules", ""));
			
			
			return gen.process(q);

		} catch (Exception x) {
			x.printStackTrace();
			return null;
		} finally {
			try {c.close(); } catch (Exception x) {}
		}
	}	
	*/		
	public Image testMetricPlot() {
		Connection c = null;
		try {
			initDatasource();
			c = datasource.getConnection();
			IStoredQuery q = new StoredQuery(2);
			Numeric2SimilarityChartGenerator gen = new Numeric2SimilarityChartGenerator();
			gen.setConnection(c);
			gen.setProperty(Property.getInstance("XLogP", ""));
			
			return gen.process(q);

		} catch (Exception x) {
			x.printStackTrace();
			return null;
		} finally {
			try {c.close(); } catch (Exception x) {}
		}
	}	
	
	public Image testFungGroups() {
		Connection c = null;
		try {
			initDatasource();
			c = datasource.getConnection();
			IStoredQuery q = new StoredQuery(2);
			FuncGroupsChartGenerator gen = new FuncGroupsChartGenerator();
			gen.setConnection(c);

			
			return gen.process(q);

		} catch (Exception x) {
			x.printStackTrace();
			return null;
		} finally {
			try {c.close(); } catch (Exception x) {}
		}
	}		
	public static void main(String args[]) {
		JPanel p = new JPanel();
		//p.setLayout(new GridLayout(1,2));
		p.setLayout(new BoxLayout(p,BoxLayout.PAGE_AXIS));
		
		Image image;
		JLabel lblChart;
		//Image image = new PieChartGeneratorTest().testPropertyHistogramPlot();
		/*
		Image image = new PieChartGeneratorTest().test();
		JLabel lblChart = new JLabel();
		lblChart.setIcon(new ImageIcon(image));
		lblChart.setBorder(BorderFactory.createEtchedBorder());
		p.add(lblChart);
		*/
		
		image = new PieChartGeneratorTest().testScatterPlot();
		lblChart = new JLabel();
		lblChart.setIcon(new ImageIcon(image));
		lblChart.setBorder(BorderFactory.createEtchedBorder());
		p.add(lblChart);
		/*
		image = new PieChartGeneratorTest().testMetricPlot();
		lblChart = new JLabel();
		lblChart.setIcon(new ImageIcon(image));
		lblChart.setBorder(BorderFactory.createEtchedBorder());
		p.add(lblChart);
		
		image = new PieChartGeneratorTest().testMetricNominalPlot();
		lblChart = new JLabel();
		lblChart.setIcon(new ImageIcon(image));
		lblChart.setBorder(BorderFactory.createEtchedBorder());
		p.add(lblChart);		

		image = new PieChartGeneratorTest().test();
		lblChart = new JLabel();
		lblChart.setIcon(new ImageIcon(image));
		lblChart.setBorder(BorderFactory.createEtchedBorder());
		p.add(lblChart);
		
		image = new PieChartGeneratorTest().testFungGroups();
		lblChart = new JLabel();
		lblChart.setIcon(new ImageIcon(image));
		lblChart.setBorder(BorderFactory.createEtchedBorder());
		p.add(lblChart);
		*/
		
		JOptionPane.showMessageDialog(null,p);
		try {
		ImageIO.write((BufferedImage)image,"png",new File("text.png"));
		} catch (Exception x) {}
	}	
}
