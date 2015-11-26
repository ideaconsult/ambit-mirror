package ambit2.descriptors.processors;

import java.util.BitSet;

import net.idea.modbcum.i.processors.IProcessor;

import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.fingerprint.CircularFingerprinter;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.base.data.Property;
import ambit2.core.config.AmbitCONSTANTS;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.descriptors.AtomEnvironentMatrix;
import ambit2.smarts.CMLUtilities;
import ambit2.smarts.processors.StructureKeysBitSetGenerator;

public enum FPTable {
	fp1024 {
		@Override
		public String getProperty() {
			return name();
		}

		@Override
		public String getStatusProperty() {
			return name() + ".status";
		}

		@Override
		public String getTimeProperty() {
			return name() + ".time";
		}

		@Override
		public IProcessor<IAtomContainer, BitSet> getGenerator()
				throws Exception {
			return new FingerprintGenerator(new Fingerprinter(1024));
		}

		@Override
		public String getTable() {
			return "fp1024";
		}

		@Override
		public String toString() {
			return "Fingerprints (hashed 1024 fingerprnts used for similarity search and prescreening)";
		}

	},
	fp1024_struc {
		@Override
		public String getProperty() {
			return AmbitCONSTANTS.Fingerprint;
		}

		@Override
		public String getStatusProperty() {
			return AmbitCONSTANTS.FingerprintSTATUS;
		}

		@Override
		public String getTimeProperty() {
			return AmbitCONSTANTS.FingerprintTIME;
		}

		@Override
		public IProcessor<IAtomContainer, BitSet> getGenerator()
				throws Exception {
			return new FingerprintGenerator(new Fingerprinter(1024));
		}

		@Override
		public String getTable() {
			return "fp1024_struc";
		}

		@Override
		public String toString() {
			return "Fingerprints (hashed 1024 fingerprnts used for similarity search and prescreening)";
		}

	},
	sk1024 {
		@Override
		public String getProperty() {
			return name();
		}

		@Override
		public String getTimeProperty() {
			return name() + ".time";
		}

		@Override
		public String getStatusProperty() {
			return name() + ".status";
		}

		@Override
		public IProcessor<IAtomContainer, BitSet> getGenerator()
				throws Exception {
			return new StructureKeysBitSetGenerator();
		}

		@Override
		public String getTable() {
			return "sk1024";
		}

		@Override
		public String toString() {
			return "Structural keys (1024 structural fragments used to speed up SMARTS search)";
		}
	},
	smarts_accelerator {
		@Override
		public String getProperty() {
			return CMLUtilities.SMARTSProp;
		}

		@Override
		public String getStatusProperty() {
			return null;
		}

		@Override
		public String getTimeProperty() {
			return null;
		}

		@Override
		public IProcessor<IAtomContainer, BitSet> getGenerator()
				throws Exception {
			return null;
		}

		@Override
		public String getTable() {
			return "structure";
		}

		@Override
		public String toString() {
			return "SMARTS data";
		}

	},
atomenvironments {
		@Override
		public String getProperty() {
			return AmbitCONSTANTS.AtomEnvironment;
		}
		@Override
		public String getStatusProperty() {
			return null;
		}
		@Override
		public String getTimeProperty() {
			return null;
		}
		@Override
		public IProcessor<IAtomContainer, BitSet> getGenerator() {
			return null;
		}
		@Override
		public String getTable() {
			return "fpatomenvironments";
		}
		@Override
		public String toString() {
			return "Atom Environments";
		}
		
	},
	aematrix {
		@Override
		public String getProperty() {
			return name();
		}

		@Override
		public String getTimeProperty() {
			return name() + ".time";
		}

		@Override
		public String getStatusProperty() {
			return name() + ".status";
		}

		@Override
		public IProcessor<IAtomContainer, BitSet> getGenerator()
				throws Exception {
			AtomEnvironentMatrix gen = new AtomEnvironentMatrix(CDKAtomTypeMatcher.getInstance(SilentChemObjectBuilder.getInstance()),"org/openscience/cdk/dict/data/cdk-atom-types.owl",7);
			return new FingerprintGenerator(gen);
		}

		@Override
		public String getTable() {
			return "fpatomenvironments";
		}

		@Override
		public String toString() {
			return "Atom Environment Matrix";
		}

	},
	inchi {
		@Override
		public String getProperty() {
			return Property.opentox_InChI;
		}

		@Override
		public String getStatusProperty() {
			return null;
		}

		@Override
		public String getTimeProperty() {
			return null;
		}

		@Override
		public IProcessor<IAtomContainer, BitSet> getGenerator() {
			return null;
		}

		@Override
		public String getTable() {
			return "chemicals";
		}

		@Override
		public String toString() {
			return "InChI in chemicals table";
		}
	},
	cf1024 {
		@Override
		public String getProperty() {
			return name();
		}

		@Override
		public String getTimeProperty() {
			return name() + ".time";
		}

		@Override
		public String getStatusProperty() {
			return name() + ".status";
		}

		@Override
		public IProcessor<IAtomContainer, BitSet> getGenerator()
				throws Exception {
			return new FingerprintGenerator(new CircularFingerprinter());
		}

		@Override
		public String getTable() {
			return "pc1024";
		}

		@Override
		public String toString() {
			return "PubChem fingerprints";
		}
	};

	abstract public String getProperty();

	abstract public String getTimeProperty();

	abstract public String getStatusProperty();

	abstract public IProcessor<IAtomContainer, BitSet> getGenerator()
			throws Exception;

	abstract public String getTable();
};