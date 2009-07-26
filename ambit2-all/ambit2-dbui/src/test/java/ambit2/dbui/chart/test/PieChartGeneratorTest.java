package ambit2.dbui.chart.test;

import java.awt.Image;
import java.sql.Connection;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ambit2.base.data.Property;
import ambit2.db.chart.FuncGroupsChartGenerator;
import ambit2.db.chart.Nominal2SimilarityChartGenerator;
import ambit2.db.chart.Numeric2SimilarityChartGenerator;
import ambit2.db.chart.PieChartGenerator;
import ambit2.db.chart.PropertiesChartGenerator;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.StoredQuery;
import ambit2.dbui.test.RepositoryTest;

public class PieChartGeneratorTest extends RepositoryTest  {
	
	
	public Image test() {
		Connection c = null;
		try {
			initDatasource();
			c = datasource.getConnection();
			IStoredQuery q = new StoredQuery(4);
			PieChartGenerator gen = new PieChartGenerator();
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
	
	public Image testScatterPlot() {
		Connection c = null;
		try {
			initDatasource();
			c = datasource.getConnection();
			IStoredQuery q = new StoredQuery(23);
			PropertiesChartGenerator gen = new PropertiesChartGenerator();
			gen.setConnection(c);
			gen.setPropertyX(Property.getInstance("XLogP", ""));
			gen.setPropertyY(Property.getInstance("LLNA EC3 %", ""));
			
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
			IStoredQuery q = new StoredQuery(23);
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

	public Image testMetricPlot() {
		Connection c = null;
		try {
			initDatasource();
			c = datasource.getConnection();
			IStoredQuery q = new StoredQuery(23);
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
			IStoredQuery q = new StoredQuery(23);
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
		p.setLayout(new BoxLayout(p,BoxLayout.PAGE_AXIS));
		
		
		Image image = new PieChartGeneratorTest().testFungGroups();
		JLabel lblChart = new JLabel();
		lblChart.setIcon(new ImageIcon(image));
		lblChart.setBorder(BorderFactory.createEtchedBorder());
		p.add(lblChart);
		
		image = new PieChartGeneratorTest().testMetricPlot();
		lblChart = new JLabel();
		lblChart.setIcon(new ImageIcon(image));
		lblChart.setBorder(BorderFactory.createEtchedBorder());
		p.add(lblChart);

		image = new PieChartGeneratorTest().test();
		lblChart = new JLabel();
		lblChart.setIcon(new ImageIcon(image));
		lblChart.setBorder(BorderFactory.createEtchedBorder());
		p.add(lblChart);
		
		JOptionPane.showMessageDialog(null,p);
	}	
}
