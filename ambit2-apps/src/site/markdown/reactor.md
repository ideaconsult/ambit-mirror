# Simulation of chemical reactions and synthetic accessibility 

## Ambit-Reactor

Ambit-Reactor is a newly developed software module for simulation of chemical reactions. For a given set of initial reactants, Ambit-Reactor applies exhaustively all transformations based on generic chemical reaction rules described in a predefined database. For each of the result products all possible transformations are applied to obtain new products and so on. In order to control the combinatorial explosion, the process stops when conditions defined by the user are reached. Ambit-Reactor is configured via JSON files that specify the reaction strategy, reaction data base, allowed and forbidden products, set of parameters and logical conditions for reaction application and definition of sites where reaction occurs. The reactor strategy is defined by logical expressions of molecular descriptors’ values. In this work we demonstrate applications of Ambit-Reactor for generation of virtual compound libraries and for combinatorial generation of metabolites.

* Latest release <a href="https://nexus.ideaconsult.net/service/local/repositories/snapshots/content/ambit/ambit2-reactions/3.1.0-SNAPSHOT/ambit2-reactions-3.1.0-20170630.075524-26.jar">ambitreactor-3.1.0</a>

# Ambit-SA  (Synthetic accessibility)

`SyntheticAccessibiliyCli` (Ambit-SA) is a software tool for theoretical prediction of synthetic accessibility (SA) of organic molecules. The model for SA uses four weighted molecular descriptors, which represent different structural and topological features, combined within an additive scheme. For a given target molecule or set of molecules, the algorithm calculates molecular complexity, stereo chemical complexity and the complexity due to the presence of fused and bridged systems. SA is outputted as a score ranging from 0 to 100 where value 100 is maximal synthetic accessibility i.e. the molecule is most easily synthesizable.

* Latest release <a href="http://web.uni-plovdiv.bg/nick/ambit-tools/SyntheticAccessibilityCli.jar">SyntheticAccessibilityCli.jar</a>

Ambit-SA can be started from command line by following command:

```
java -jar SyntheticAccessibilityCli.jar  option1 option2 …
```

SA calculation for a single molecule can be performed by directly entering the molecule SMILES from the command line with option ‘-s’ e.g.

```
java -jar SyntheticAccessibilityCli.jar -s "FC(F)(F)c1cc(ccc1)N5CCN(CCc2nnc3[C@H]4CCC[C@H]4Cn23)CC5"
       
Calculating SA for: FC(F)(F)c1cc(ccc1)N5CCN(CCc2nnc3[C@H]4CCC[C@H]4Cn23)CC5`
SA = 54.116
```

Option `-v` can be used for a verbose output:

```
java -jar SyntheticAccessibilityCli.jar -s "FC(F)(F)c1cc(ccc1)N5CCN(CCc2nnc3[C@H]4CCC[C@H]4Cn23)CC5" -v      
Calculating SA for: FC(F)(F)c1cc(ccc1)N5CCN(CCc2nnc3[C@H]4CCC[C@H]4Cn23)CC5
SA = 54.116
SA details: 
MOL_COMPLEXITY_01  88.751  score = 40.833
WEIGHTED_NUMBER_OF_STEREO_ELEMENTS  2.000  score = 40.000
CYCLOMATIC_NUMBER  5.000  score = 50.000
RING_COMPLEXITY  1.174  score = 82.609
```

Option `-i` should be used to set an input file with a set structures for batch calculation of SA:

```
java -jar SyntheticAccessibilityCli.jar -i sa-mol-set-01-b.smi       
Calculating SA for molecule set: sa-mol-set-01-b.smi
Reading D:\ChemSoft\JBSMM-Reactor\sa-mol-set-01-b.smi
#	smiles	NumAtoms	SA
1	CCOP(=S)(OCC)Oc1cc(C)nc(n1)N(C)C	19	85.622
2	OOC1CCOP(=O)(N1)N(CCCl)CCCl	16	81.760
3	O=C1Cc2c(N1)ccc3OCC(CNCc4ccccc4)Oc23	23	64.931
4	CC1OC(=NC1CCOc2ccc(CC3C(=O)NOC3=O)cc2)c4ccccc4	29	72.725
5	O=C(NN1CCCCC1)c2nn(-c3ccc(Cl)cc3Cl)c(-c4ccc(Cl)cc4)c2C	30	72.241
6	CC(C)C(=O)Oc1ccc2CC(CCc2c1OC(=O)C(C)C)NC	24	66.674
```





