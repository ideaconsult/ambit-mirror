# Ambit-SMIRKS

Ambit-SMIRKS is an extension of the [AMBIT-SMARTS](smarts.html) Java library, both part of the [AMBIT project](http://ambit.sf.net), it is used to enable: 

- metabolite predictions in [Toxtree](http://toxtree.sourceforge.net) [SMARTCyp module](http://toxtree.sourceforge.net/smartcyp.html), once that site of metabolisms are predicted by [SMARTCyp](http://http://smartcyp.sund.ku.dk/)
- structure transformation for the purpose of [chemical structure standardisation](http://ambit.sourceforge.net/ambitcli_standardisation.html)
- Biotransformation predictions in [enviPath](https://envipath.org/)
- [BioTransformer](https://bitbucket.org/djoumbou/biotransformer) a tool developed by [The human metabolome database (HMDB 4.0)](https://doi.org/10.1093/nar/gkx1089) team.
- [Ambit Reactor](reactor.html)

## Main functionality

* Parsing of [SMIRKS linear notations](http://www.daylight.com/dayhtml_tutorials/languages/smirks/) into internal reaction (transformation) representations based on [The Chemistry Development Kit](http://cdk.sf.net) objects 
* Application of the stored reactions against target molecules for actual transformation of the target chemical objects 

##### Transformation mapping modes

The transformations can be applied on various sites of the target molecule in several modes:
 
 * single 
 * non-overlapping 
 * non-identical
 * non-homomorphic or
 * externally specified list of sites

#### Maven artifacts

[Maven arfifacts search](https://nexus.ideaconsult.net/#nexus-search;quick~ambit2-smarts)

* Depends on [The CDK](http://cdk.sf.net) 2.1.1
 
````
    <dependency>
      <groupId>ambit</groupId>
      <artifactId>ambit2-smarts</artifactId>
      <version>4.0.1-SNAPSHOT</version>
    </dependency>
    <repository>
		<id>nexus-idea-snapshots</id>
		<url>https://nexus.ideaconsult.net/content/repositories/snapshots</url>
    </repository>
    
* Depends on [The CDK](http://cdk.sf.net) 1.5.13 
 
````
    <dependency>
      <groupId>ambit</groupId>
      <artifactId>ambit2-smarts</artifactId>
      <version>3.1.0-SNAPSHOT</version>
    </dependency>
    <repository>
		<id>nexus-idea-snapshots</id>
		<url>https://nexus.ideaconsult.net/content/repositories/snapshots</url>
    </repository> 
````

* Depends on [The CDK](http://cdk.sf.net) 1.5.10

````
    <dependency>
      <groupId>ambit</groupId>
      <artifactId>ambit2-smarts</artifactId>
      <version>3.0.0</version>
    </dependency>
    <repository>
		<id>nexus-idea-releases</id>
		<url>https://nexus.ideaconsult.net/content/repositories/releases</url>
    </repository>
````

#### Code examples

```java
    IAtomContainer target = (initialize)
    SMIRKSManager smrkMan = new SMIRKSManager();
    SMIRKSReaction reaction = smrkMan.parse(smirks);
    if (!smrkMan.getErrors().equals(""))  {
           throw(new Exception("Smirks Parser errors:\n" + smrkMan.getErrors()));
    }
    if (smrkMan.applyTransformation(target, reaction)) 
           return target; //all products inside the same atomcontainer, could be disconnected
    else return null;
```

 * Generate separate products for every possible reaction (used in [Toxtree](http://toxtree.sf.net/smartcyp.html))

```java
    SMIRKSManager smrkMan = new SMIRKSManager();
    SMIRKSReaction smr = smrkMan.parse(reaction.getSMIRKS());
    IAtomContainer product = reactant; //(IAtomContainer) reactant.clone();
    IAtomContainerSet rproducts = smrkMan.applyTransformationWithSingleCopyForEachPos(product, null, smr);
    //products returned in a separate atom sontainer set
```

 * More examples https://github.com/ideaconsult/apps-ambit/tree/master/smirks-example

## Applications 

### Toxtree 

[Toxtree](http://toxtree.sf.net) is an open-source application that predicts various kinds of toxic effects, mostly by applying structural alerts, arranged in a decision tree fashion.
You could [download the application](http://toxtree.sourceforge.net/download.html) or test [Toxtree online](http://toxtree.sourceforge.net/predict?search=omeprazole)

### enviPath - The environmental contaminant biotransformation pathway resource

>To represent the biotransformation rules, enviPath use [SMIRKS](http://daylight.com/dayhtml_tutorials/languages/smirks/). enviPath uses AMBIT-SMARTS, which implements SMARTS and SMIRKS for CDK.

 * [https://envipath.org/](https://envipath.org/)
 
 * Wicker, J.; Lorsbach, T.; Gutlein, M.; Schmid, E.; Latino, D.; Kramer, S.; Fenner, K. enviPath - The environmental contaminant biotransformation pathway resource. Nucleic Acids Res. 2016 [DOI: 10.1093/nar/gkv1229](http://dx.doi.org/10.1093/nar/gkv1229).

### Ambit SMIRKS applications

 * [AmbitSmirksGUI.jar](http://web.uni-plovdiv.bg/nick/ambit-tools/AmbitSmirksGUI-1.02b.jar)
 
 * [Online test page](https://apps.ideaconsult.net/data/demo/reaction)
  
 * [ambitcli - chemical structure standardisation](http://ambit.sourceforge.net/ambitcli_standardisation.html)
 
---

### Publications 

 * Kochev N, Avramova S, Jeliazkova N: Ambit-SMIRKS: a software module for reaction representation, reaction search and structure transformation. J Cheminform 2018, 10:42. [DOI:10.1186/s13321-018-0295-6](https://jcheminf.springeropen.com/articles/10.1186/s13321-018-0295-6)
 
 * Best poster award at [OpenTox Euro 2013](https://www.slideshare.net/jeliazkova_nina/bx37-ambit-smirksmainzseptember2013) 
 
 * AMBIT SMARTS Publication [N. Jeliazkova and N. Kochev, AMBIT-SMARTS: Efficient Searching of Chemical Structures and Fragments, Mol. Inform., vol. 30, no. 8, pp. 707-720, 2011.](http://onlinelibrary.wiley.com/doi/10.1002/minf.201100028/abstract) 
 
### Social

 * <a href="https://twitter.com/share" class="twitter-share-button" data-via="10705013" data-related="10705013" data-count="none" data-hashtags="ambit,smirks,metabolism,oteu13,opentox">Tweet</a>



