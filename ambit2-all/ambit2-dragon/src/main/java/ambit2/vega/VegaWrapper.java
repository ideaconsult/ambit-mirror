package ambit2.vega;

import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.data.Property;
import net.idea.modbcum.i.exceptions.AmbitException;

public interface VegaWrapper {
    public List<Property> createProperties() throws AmbitException;
    public IAtomContainer process(IAtomContainer record) throws Exception;
}
