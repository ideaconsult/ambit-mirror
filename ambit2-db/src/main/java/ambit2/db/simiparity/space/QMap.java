package ambit2.db.simiparity.space;

import java.io.Serializable;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.Property;

/**
 * Quantitative SAS map header
 * 
 * @author nina
 * 
 */
public class QMap implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 7229458825361028794L;

    int id = -1;

    ISourceDataset dataset;
    Property property;
    double activityThreshold;
    double similarityThreshold;

    public QMap() {
    }

    public QMap(int id) {
	setId(id);
    }

    public QMap(ISourceDataset dataset, Property property, double activityThreshold, double similarityThreshold) {
	setDataset(dataset);
	setProperty(property);
	setActivityThreshold(activityThreshold);
	setSimilarityThreshold(similarityThreshold);
    }

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public ISourceDataset getDataset() {
	return dataset;
    }

    public void setDataset(ISourceDataset dataset) {
	this.dataset = dataset;
    }

    public Property getProperty() {
	return property;
    }

    public void setProperty(Property property) {
	this.property = property;
    }

    public double getActivityThreshold() {
	return activityThreshold;
    }

    public void setActivityThreshold(double activityThreshold) {
	this.activityThreshold = activityThreshold;
    }

    public double getSimilarityThreshold() {
	return similarityThreshold;
    }

    public void setSimilarityThreshold(double similarityThreshold) {
	this.similarityThreshold = similarityThreshold;
    }

    public void clear() {
	setId(-1);
	if (dataset != null)
	    dataset.setID(-1);
	if (property != null)
	    property.setId(-1);
	setSimilarityThreshold(Double.NaN);
	setActivityThreshold(Double.NaN);
    }
}
