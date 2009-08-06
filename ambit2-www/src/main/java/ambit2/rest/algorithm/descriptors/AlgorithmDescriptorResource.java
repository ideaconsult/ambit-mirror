package ambit2.rest.algorithm.descriptors;

import java.util.ArrayList;
import java.util.Iterator;

import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.AromaticAtomsCountDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.AtomCountDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.BondCountDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.ChiChainDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.LongestAliphaticChainDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.RotatableBondsCountDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.RuleOfFiveDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.TPSADescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.WienerNumbersDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor;
import org.restlet.Context;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

import ambit2.descriptors.PKASmartsDescriptor;
import ambit2.rest.StatusException;
import ambit2.rest.algorithm.AlgorithmResource;

public class AlgorithmDescriptorResource extends AlgorithmDescriptorTypesResource {
	//TODO make use of owl
	protected IMolecularDescriptor descriptor = null;
	public enum descriptors  {
		lipinskifailures {
			@Override
			public IMolecularDescriptor createDescriptor() {
				return new RuleOfFiveDescriptor();
			}
						
		},
		longestAliphaticChain {
			@Override
			public IMolecularDescriptor createDescriptor() {
				return new LongestAliphaticChainDescriptor();
			}
						
		},
		atomCount {
			@Override
			public IMolecularDescriptor createDescriptor() {
				return new AtomCountDescriptor();
			}
						
		},
		aromaticAtomsCount{
			@Override
			public IMolecularDescriptor createDescriptor() {
				return new AromaticAtomsCountDescriptor();
			}
			
		},
		aromaticBondsCount{
			@Override
			public IMolecularDescriptor createDescriptor() {
				return new AromaticAtomsCountDescriptor();
			}
		},
		BCUT{
			@Override
			public IMolecularDescriptor createDescriptor() {
				return new BCUTDescriptor();
			}
		},
		bondCount{
			@Override
			public IMolecularDescriptor createDescriptor() {
				return new BondCountDescriptor();
			}
		},
		chi1{
			@Override
			public IMolecularDescriptor createDescriptor() {
				return new ChiChainDescriptor();
			}			
		},
		rotatableBondsCount {
			@Override
			public IMolecularDescriptor createDescriptor() {
				return new RotatableBondsCountDescriptor();
			}			
		},
		tpsa {
			@Override
			public IMolecularDescriptor createDescriptor() {
				return new TPSADescriptor();
			}			
		},
		wienerNumbers{
			@Override
			public IMolecularDescriptor createDescriptor() {
				return new WienerNumbersDescriptor();
			}
						
		},
		xlogP {
			@Override
			public IMolecularDescriptor createDescriptor() {
				return new XLogPDescriptor();
			}
			
		},
		pka {
			@Override
			public IMolecularDescriptor createDescriptor() {
				return new PKASmartsDescriptor();
			}
			
		};		
		public IMolecularDescriptor createDescriptor() {return null;}
	};
	public AlgorithmDescriptorResource(Context context, Request request,
			Response response) {
		super(context, request, response);
		setCategory(AlgorithmResource.algorithmtypes.descriptorcalculation.toString());
		

	}
	@Override
	protected Iterator<String> createQuery(Context context, Request request,
			Response response) throws StatusException {
		ArrayList<String> q = new ArrayList<String>();
		for (descriptors d : descriptors.values())
			q.add(String.format("%s/%s",request.getOriginalRef(),d.toString()));
		return q.iterator();

	}
	@Override
	public Representation getRepresentation(Variant variant) {
		try {
			Object descriptorID = Reference.decode(getRequest().getAttributes().get(iddescriptor).toString());
			return super.getRepresentation(variant);
		} catch (Exception x) {
			return super.getRepresentation(variant);
		}
	}	
}
