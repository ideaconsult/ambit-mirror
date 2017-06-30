#ambitReactor

Ambit-Reactor is a newly developed software module for simulation of chemical reactions. For a given set of initial reactants, Ambit-Reactor applies exhaustively all transformations based on generic chemical reaction rules described in a predefined database. For each of the result products all possible transformations are applied to obtain new products and so on. In order to control the combinatorial explosion, the process stops when conditions defined by the user are reached. Ambit-Reactor is configured via JSON files that specify the reaction strategy, reaction data base, allowed and forbidden products, set of parameters and logical conditions for reaction application and definition of sites where reaction occurs. The reactor strategy is defined by logical expressions of molecular descriptors’ values. In this work we demonstrate applications of Ambit-Reactor for generation of virtual compound libraries and for combinatorial generation of metabolites.

## Download

* Latest release <a href="https://nexus.ideaconsult.net/service/local/repositories/snapshots/content/ambit/ambit2-reactions/3.1.0-SNAPSHOT/ambit2-reactions-3.1.0-20170630.075524-26.jar">ambitreactor-3.1.0</a>
