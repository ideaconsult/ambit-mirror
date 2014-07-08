/*
Copyright (C) 2005-2008  

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

package ambit2.descriptors;

import java.util.Enumeration;
import java.util.Hashtable;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ambit2.base.data.AmbitBean;
import ambit2.smarts.query.ISmartsPattern;
import ambit2.smarts.query.SMARTSException;
import ambit2.smarts.query.SmartsPatternFactory;

public class FunctionalGroup extends AmbitBean implements
		IProcessor<IAtomContainer, VerboseDescriptorResult> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9069207547772781160L;
	protected static final String t_group = "group";
	protected static final String t_name = "name";
	protected static final String t_smarts = "smarts";
	protected static final String t_hint = "hint";
	protected static final String t_family = "family";
	protected static final String t_example = "example";
	protected Hashtable<String, String> properties;
	protected ISmartsPattern<IAtomContainer> query = null;

	public ISmartsPattern<IAtomContainer> getQuery() {
		return query;
	}

	public long getID() {
		return 0;
	}

	public void setQuery(ISmartsPattern<IAtomContainer> query)
			throws SMARTSException {
		this.query = query;
		query.setSmarts(getSmarts());
	}

	protected boolean enabled = true;
	protected boolean verboseMatch = false;

	public boolean isVerboseMatch() {
		return verboseMatch;
	}

	public void setVerboseMatch(boolean verboseMatch) {
		this.verboseMatch = verboseMatch;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public FunctionalGroup() {
		this("", "", "");

	}

	public FunctionalGroup(String name, String smarts, String hint,
			String family) {
		super();
		properties = new Hashtable<String, String>();
		setFamily(family);
		setSmarts(smarts);
		setName(name);
		setHint(hint);
		setExample(smarts);
	}

	public FunctionalGroup(String name, String smarts, String hint,
			String family, String example) {
		this(name, smarts, hint, family);
		setExample(example);
	}

	public FunctionalGroup(String name, String smarts, String hint) {
		this(name, smarts, hint, "");
	}

	public void clear() {
		properties.clear();
	}

	public String getFamily() {
		return properties.get(t_family);
	}

	public String getHint() {
		return properties.get(t_hint);
	}

	public String getName() {
		return properties.get(t_name);
	}

	public String getSmarts() {
		return properties.get(t_smarts);
	}

	public String getExample() {
		return properties.get(t_example);
	}

	public void setName(String name) {
		properties.put(t_name, name);
	}

	public void setExample(String name) {
		properties.put(t_example, name == null ? getSmarts() : name);
	}

	public void setSmarts(String smarts) {
		try {
			// This is a HACK to deal with kekule structures!
			// Find out if this is a valid smiles ,
			SmilesParser parser = new SmilesParser(
					SilentChemObjectBuilder.getInstance());
			IMolecule mol = parser.parseSmiles(smarts);
			for (IAtom atom : mol.atoms())
				if (atom.getFlag(CDKConstants.ISAROMATIC)) {
					SmilesGenerator g = new SmilesGenerator();
					g.setUseAromaticityFlag(true);
					smarts = g.createSMILES(mol);
					break;
				}
		} catch (Exception x) {
			// not a valid smiles, so we keep the original smarts
		}
		properties.put(t_smarts, smarts);
		query = null;
	}

	public void setHint(String hint) {
		properties.put(t_hint, hint);
	}

	public void setFamily(String family) {
		properties.put(t_family, family);
	}

	@Override
	public String toString() {
		return String.format("%s %s", getFamily(), getName());
	}

	public Element toXML(Document document) throws AmbitException {
		Element element = document.createElement(t_group);
		Enumeration<String> keys = properties.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			element.setAttribute(key, properties.get(key));
		}
		return element;
	}

	public VerboseDescriptorResult process(IAtomContainer target)
			throws AmbitException {
		if (query == null)
			query = SmartsPatternFactory.createSmartsPattern(
					SmartsPatternFactory.SmartsParser.smarts_nk, getSmarts(),
					false);
		int hits = 0;
		IAtomContainer match = null;
		if (isVerboseMatch())
			try {
				match = query.getMatchingStructure(target);
				hits = (match.getAtomCount() > 0) ? 1 : 0;
				return new VerboseDescriptorResult<IAtomContainer, IntegerResult>(
						new IntegerResult(hits), match);
			} catch (UnsupportedOperationException x) {
				hits = query.match(target);
				return new VerboseDescriptorResult<String, IntegerResult>(
						new IntegerResult(hits), null);
			} catch (SMARTSException x) {
				match = null;
				hits = 0;
				return new VerboseDescriptorResult<String, IntegerResult>(
						new IntegerResult(hits), x.getMessage());
			}
		else {
			hits = query.match(target);
			return new VerboseDescriptorResult<String, IntegerResult>(
					new IntegerResult(hits), null);
		}
	}

	@Override
	public void open() throws Exception {
	}

	@Override
	public void close() throws Exception {
	}

}
