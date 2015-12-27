package ambit2.tautomers.processor;

import java.util.List;
import java.util.logging.Level;

import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;

import ambit2.tautomers.TautomerConst;
import ambit2.tautomers.TautomerManager;

public class TautomerProcessor extends
		DefaultAmbitProcessor<IAtomContainer, IAtomContainer> {
	protected TautomerManager tautomerManager = new TautomerManager();
	protected IProcessor<IAtomContainer, IAtomContainer> callback = null;

	public IProcessor<IAtomContainer, IAtomContainer> getCallback() {
		return callback;
	}

	public void setCallback(IProcessor<IAtomContainer, IAtomContainer> callback) {
		this.callback = callback;
	}

	public TautomerProcessor() {
		super();
		tautomerManager.tautomerFilter
				.setFlagApplyDuplicationCheckIsomorphism(false);
		tautomerManager.tautomerFilter.setFlagApplyDuplicationCheckInChI(true);
		tautomerManager.FlagCalculateCACTVSEnergyRank = true;
		tautomerManager.FlagRegisterOnlyBestRankTautomers = true;
		tautomerManager.FlagSetStereoElementsOnTautomerProcess = true;
	}

	public TautomerManager getTautomerManager() {
		return tautomerManager;
	}

	public void setTautomerManager(TautomerManager tautomerManager) {
		this.tautomerManager = tautomerManager;

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6493329018044361388L;

	@Override
	public IAtomContainer process(IAtomContainer mol) throws Exception {
		if (mol == null || mol.getAtomCount() == 0)
			return mol;
		boolean applicable = false;
		//if all single bonds don't waste time on trying the tautomerisation
		for (IBond bond : mol.bonds())
			if (!Order.SINGLE.equals(bond.getOrder())) {
				applicable = true;
				break;
			}
		if (!applicable) return mol;
		
		tautomerManager.setStructure(mol);
		IAtomContainer best = null;
		List<IAtomContainer> resultTautomers = tautomerManager
				.generateTautomersIncrementaly();
		if (tautomerManager.FlagRegisterOnlyBestRankTautomers) {
			best = tautomerManager.getCanonicTautomer(resultTautomers);
			return best == null ? mol : best;
		} else {
			// old version
			double bestRank = 0;

			for (IAtomContainer tautomer : resultTautomers)
				try {
					if (callback != null)
						callback.process(tautomer);

					Object rank_property = tautomer
							.getProperty(TautomerConst.CACTVS_ENERGY_RANK);
					double rank;
					if (rank_property == null) {
						// original struc or switched to CACTVS ranking
						rank_property = tautomer
								.getProperty(TautomerConst.TAUTOMER_RANK);

						if (rank_property == null) {
							tautomer
							.setProperty(TautomerConst.TAUTOMER_RANK,null);
							logger.log(Level.FINE,
									"NO RANK @ " + tautomer.getProperties());
							continue;
						}
					}
					rank = (rank_property instanceof Double) ? ((Double) rank_property)
							.doubleValue() : Double.parseDouble(rank_property
							.toString());
					/**
					 * The rank is energy based, lower rank is better
					 */
					if ((best == null) || (bestRank > rank)) {
						bestRank = rank;
						best = tautomer;
					}

				} catch (Exception x) {
					logger.log(Level.WARNING, x.getMessage());
				}
			return best == null ? mol : best;
		}
	}
}
