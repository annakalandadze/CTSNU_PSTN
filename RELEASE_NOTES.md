# RELEASE NOTES

### v5.0

date: 2024-10-07

The dependency on the MatLab library present in version 4.13 was too strong.
So, I decided to split the project into two projects. Such division affects only the use of the class `PSTN`.

- [CSTNU Tool](https://profs.scienze.univr.it/~posenato/software/cstnu/) (this project).
  It is the core project and, limited to the class `PSTN`, it contains its definition and relative algorithms that cannot be
  used directly because the object instantiation requires an external actor class that implements the `it.univr.di.cstnu.util.OptimizationEngine` functional interface to be executed.
  CSTNU Tool project does not contain any implementation of the `OptimizationEngine` because I haven't found a full-fledged open-source implementation of a non-linear optimization problem solver.

- [MatLabPlugin4CSTNUTool](https://profs.scienze.univr.it/~posenato/software/cstnu/matlabplugin).
  It offers an implementation of the `it.univr.di.cstnu.util.OptimizationEngine` functional interface using the MatLab software.
  This plugin joined with the CSTNU Tool, allows one to create and use PSTN objects.
  Refer to the [MatLabPlugin4CSTNUTool website](https://profs.scienze.univr.it/~posenato/software/cstnu/matlabplugin) to discover how to compile and use this plugin within this project.

Added the following classes:

* `OptimizationEngine`: a functional interface that represents a method for solving a non-linear optimization problem. Class `PSTN` depends on this interface.
   A possible implementation of this interface is offered in the project [MatLabPlugin4CSTNUTool](https://profs.scienze.univr.it/~posenato/software/cstnu/matlabplugin).

* `LogNormalDistribution`: represents log-normal distribution parameters. It was a subclass of `PSTN`.

Added 3 test files: `notDC002.stnu`, `notDC020.stnu`, and `notDC033.stnu`.

Removed the following classes:

* `MatLabEngine`: moved to [MatLabPlugin4CSTNUTool](https://profs.scienze.univr.it/~posenato/software/cstnu/matlabplugin)
* `PSTNTest`: moved to [MatLabPlugin4CSTNUTool](https://profs.scienze.univr.it/~posenato/software/cstnu/matlabplugin)
* `PSTNBenchmarkRunner`: moved to [MatLabPlugin4CSTNUTool](https://profs.scienze.univr.it/~posenato/software/cstnu/matlabplugin)

Updated the following classes:

* `STNUEdge`, `STNUEdgeInt`: Method `updateValue(int)` cannot stay in interface `STNUEdge`, otherwise in class `STNUEdgeInt` the method is overridden by the
  method `updateValue(int)` of the superclass `STNEdgeInt`. Therefore, `updateValue(int)` was moved into `STNUEdgeInt`.

* `STNURTE`: parameters `--env` and `--rted` now accept values of `StrategyEnum`.

* `LabeledNode,` `TNGraphMLReader,` `PSTN`: the dependency on the class `PSTN.LogNormalDistribution` is now updated to class `LogNormalDistribution.`

* `PSTN`: the class's constructors require an object of type `OptimizationEngine`.


### v4.13

date: 2024-07-09

Added the following classes:

* `PSTN`: Probabilistic Simple Temporal Network. A specialized version of STNU where each contingent link duration can
  be described by a [log-normal probability distribution](https://en.wikipedia.org/wiki/Log-normal_distribution)
  Since the networks are represented as distance graphs, the edges associated with a contingent link of a PSTN are
  still represented as upper-case and lower-case edges.
  This representation is also useful because the DC checking/execution of a PSTN is done considering its correlated STNU.
  Hence, the log-normal distribution parameters are stored in the contingent node as field `PSTN.LogNormalDistributionParameter
  logNormalDistributionParameter`.
  Determining an approximating (correlated) STNU of a PSTN requires solving a non-linear optimization problem.
  In this release, such a problem is solved using MatLab's `fmincon` function.
  Therefore, if it is required to use the `PSTN` class, executing the library offering access
  to a MatLab-licensed engine is necessary. The core MatLab engine must be extended with the modules "Optimization Toolbox" and
  "Statistics and Machine Learning Toolbox" (thanks to Kim van den Houten and Léon Planken for clarification).
  For macOS systems, where usually the MatLab is installed in `/Applications/MATLAB_R2024a.app/`, it is sufficient to
  add `-Djava.library.path=/Applications/MATLAB_R2024a.app/bin/maca64` to the `java` command.

Updated the following classes:

* `LabeledNode`: added a field to represent log-normal parameters for contingent duration in contingent links of a
  PSTN. Such a field is significant and considered only on contingent nodes of a PSTN. See the description of PSTN.
* `ALabel`: added method `getALetter()`
* `ExtendedPriorityQueue`: added method `getFirstPriority()`
* `STN`: made serializable.
* `STNU`: added the method `applySRNCycleFinder()` that can be called `CheckAlgorithm.SRNCycleFinder` during
  the DC checking phase. This method returns detailed info of the possible semi-negative cycle found during a check.
  Such info is inside the object `STNUCheckStatus` returned by `dynamicControllabilityCheck(CheckAlgorithm.
  SRNCycleFinder)`.
* `STNURTE`: added the possibility of specifying an execution strategy as a parameter. Some standard execution strategies
  are given in the enum `STNURTE.StrategyEnum`. Thanks to Kim van den Houten and Léon Planken for the first
  implementation of such an idea.
* `TNGraph,` `TNGraphReader,` and `TNGraphWriter` expanded to manage `PSTN.`

### v4.12

date: 2024-03-08

Added the following classes:

* `OSTNU`: class representing Simple Temporal Networks with Uncertainty and Oracles.

Updated the following classes:

* `STNURTE`: fixed minor bugs when executing networks having useless wait constraints involving contingent.
  timepoints.
* `STNU`: simplified some checks in `applyMinDispatchableESTNU()` methods.
  Added the original FD_STNU algorithm implementation. So, now there are two implementations of FD_STNU:
  `FD_STNU` (the original) and `FD_STNU_IMPROVED` (as the original, but it does not add waits that are more negative than the duration of the relative contingent link).

### v4.11

date: 2024-02-14

Added the following classes:

* `STNURTE`: class for implementing a Real-Time Execution algorithm for STNUs.
* `TimeInterval`: utility class represents a time interval where the lower bound is guaranteed to be ≤ upper bound.
* `ActiveWaits`: utility class to represent an active wait.

Updated the following classes:

* Class `STNU`: implemented the `MinDispatchableESTNU` algorithm.
  Changed the name to some methods. Fixed minor bugs.
* Class `STN`: fixed an annoying bug in `GET_STRONG_CONNECTED_COMPONENTS` and
  `GET_STRONG_CONNECTED_COMPONENTS_HELPER` methods. Changed the name to some methods.
* Class `TNGraphMLReader.java`: reads the name of the graph from the attributed Name in the document.
* Class `TNGraph`: `addEdge` throws an exception if the edge already exists. Returning a false was not sufficient to
  avoid some errors. Added methods `makeNewEdge` and `getUniqueEdgeName`.
* Class `ExtendedPriorityQueue.java`: renamed from `MinPriorityQueue`. Renamed some of its methods to represent a
  generic priority queue (min or max). Now, it depends on a better heap library.

### v4.10

date: 2023-11-02

Switched to Java 21.
To Apple macOS users: it seems that OpenJDK 17 has some problems running the `TNEditor` because of missing system
libraries. I recommend using Temurin 21 (https://adoptium.net/): I tested it, and it works without problems.

* Class `STN`: method `makeFastDispatchMinimization` adds constraints from the source node to guarantee that the source
  node can reach any node.
* Class `TNEditor`: for STN instances, the dispatchable version is determined using the `makeFastDispatchMinimization` method.

### v4.9

date: 2023-07-20

* Class `AbstractCSTN`: the `reset()` method does not remove the internal graph.
* Class `PriorityQueue`: added methods `delete()` and `getElements()`.
* Class `TNEditor`: added `Capture` function for saving networks shown in the application as PNG images.
* Class `FTNU`: added a constructor that accepts an XML string describing the network.
* Class `CSTNPSU`: method `getEMaxDistanceInContingencyGraph(contingencyGraph)` replaced
  by `getMaxPathContingencySpanInContingencyGraph(nodeName, contingencyGraph)`.
  I added the method `configureSubNetworks(subNetworks, n).`
* Fixed different minor bugs.
* Codd cleaned in most of the classes.

### v4.8

date: 2022-12-28

* Class `CSTNPSU`: added method `getPrototypalLink()` for determining Prototypal Link with contingency (PLC).
* Class `FTNU` (Flexible Temporal Network with Uncertainty): it is an alternative name for the class `CSTNPSU`.
* Cleaned the code of many classes.

### v4.7

date: 2022-11-01

I added the new class `PCSTNU` (Parameterized CSTNU) to represent CSTNU with parameter timepoints.

* Interface `Node`: added methods for checking if a node is a parameter one.
* Class `AbstractNode`: removed default methods because moved into `Node` interface.
* Class `LabeledNode`: A new field was added to identify parameter timepoints.
* Class `TNGraph`: added code for managing PCSTNU instances and improved some logging code.

Fixed some minor issues:

* Class `AbstractCSTN`: improved the determination of the max edge value.
* Class `CSTNPSU`: improved the determination of all constraints when the parameter `propagationOnlyToZ` is false.
* Class `STN`: A useless annotation was removed.

### v4.6

date: 2022-06-03

The project has been updated to run with Java 17.
Moreover, from this release, the source code is tested by `spotbugs` configured with `maximum effort`
and `minimal threshold` parameters.

* Class `STNUDensifier`: Improved, allowing the requirement of an exact number of edges (that can require removing edges).
* Class `STNU`: Changed the name of the fast dispatch algorithm.
* Class `Checker`: Made the long parameter specifications prefixed by `--`.
* Class `STNURandomGenerator`: Improved the documentation of the README.txt generated to describe a benchmark.

### v4.5

date: 2022-03-30

* Class `Checker`: Two statistic indexes are added when STNU is checked.
* Class `PriorityQueue`: An open hash now realizes the map of entries for better performance. Removed throws of
  exceptions when it is not necessary.
* Class `STN`: Generalized all methods to accept edges that extend STNEdge. In this way, some static methods can also be used by the STNU class.
	I fixed a bug in the `getStrongComponents` method.
* Class `STNU`: Added `fastSTNUdispatchability` method. Changed the name of method `RUL2020` to `RUL2021`. Fixed some
  bugs about other methods. Improved the efficiency of some methods.
* Classes `Component` and `AbstractComponent`: removed the `color` attribute because it is necessary to have lightweight
  components. All complementary attributes will be removed.
* Interface `STNUEdge`: Improved the representation of `contingent`/`wait` edges representing such data as an object of the
  internal class `ContingentCasePair`.
* Class `STNEdgeInt`: Improved default constructor.
* Class `STNUEdgeInt`: Improved default constructor.
* Improved the representation of `contingent`/`wait` edges representing such data as an object of the internal
  class `ContingentCasePair`.
* Class `TNGraph`: Changed internal maps representation. Added two caches for speed-up `getInEdgesAndNodes`
  and `getOutEdgesAndNodes` methods.
  The `removing observer` instruction in the `removeEdge` method is removed for performance reasons. Now, when an edge is
  removed from a graph, all its observing graphs are removed. It is not the correct behavior, but removing only the
  correct graph requires too much time for the library method. In a next release, such an issue will be fixed.
  When a graph is created by a copy, its internal `labelAlphabet` is cloned.
* Class `CSTNPSU`: fixed a bug in the initAndCheck method that didn't allow the specification of a guarded link with a label in the external bounds.
* Class `TNEditor`: added visualization of cursor position coordinates.
* Class `CSTNULabelEditingGraphMousePlugin`: fixed the bug that made a contingent edge ordinary after an edit of its
  value.
* Class `CSTNU`: A check was added to guarantee that an activation node is associated with only one contingent node.
* New class `TNPredecessorGraph`: Class for representing predecessor TNGraph very compactly.

### v4.4

date: 2022-01-03

* Improved the parser of labeled value map/set.
* Class `AbstractCSTN`. Improved `checkAndInit`: all negative edges going to an obs time-point are checked and cleaned.
* Class `STN`: improved all SSSP algorithms, making each node reachable by Z in case of forward search. Fixed a bug
  in the `makeDispatchable()` method: it didn't remove all dominated edges if their opposites were absent. Added
  methods `getAccumumulateUndominateEdges` and `getFastDispatchMinimization`, a faster method for determining the
  dispatchable version of a network.
* Class `PriorityQueue`: removed the ambiguous method `value`.
* Class `TNGraph`: introduced the `unmodifiable(TNGraph)` static method.
* Class `ALabelAlphabet`: A new constructor was added.
* Class `LabeledNode`: started its re-factoring. Next releases will specialize such a class to optimize its memory
  footprint.

### v4.3

date: 2021-10-20

* Class `LabeledNode`: extended for Tarjan algorithm.
* Class `STN`: added BFCT (a.k.a. Tarjan algorithm) algorithm in STN class.
  Such an algorithm can return the negative cycle if the network is inconsistent.
* Class `NodePriorityHeap`: renamed as `PriorityQueue` and made generic.
* Class `LabeledNode`: removed rendering code.
* Class `NodeRendering`: new class to customize the rendering of LabeledNode in `TNEditor`.
* Class `TNEditor`: GUI cleaned a little bit.
* Removed the dependency on the FreeHep library because FreeHep does not work with JRE > 8.
* Removed 'normal' and 'constraint' types for `Edge` in favor of `requirement` because in Temporal Networks, `requirement`
  is more appropriate.
* Improved all documentation (now, there is only one README.md). Added BUILDING.md document that explains how to build a
  package and
* the preliminary steps to take before a commit (only for developers).

### v4.2

date: 2021-10-07

* Fixed the main method of the STN class: now it checks a given network.
* Fixed an initialization error of the graphic driver in `TNEditor` and improved the Save dialog.

### v4.1

date: 2021-08-17

* Program `CSTNEditor` is renamed as `TNEditor`.
* Removed all unnecessary exceptions.

### v4.0

date: 2021-08-01

* Relevant change: the library is now a JVM 11 library but is still compatible with JVM 8.
* Improved Javadoc for almost all the classes/methods.
* Fixed all `spotbugs` bugs/errors at medium-level and max effort.

### v3.6

date: 2021-06-20

* Fixed a weird bug in the `CSTNPotential` class: the `dynamicCheck()` method worked right, but, in the end, the edges in the
  checked graph were wrongly saved in a reversed way.
* Refactored classes GraphMLReader and GraphMLWriter to make them more general. Now, GraphMLReader can build a
  TNGraph from a string representing it in GraphML format, and GraphMLWriter can serialize a TNGraph in GraphML format.
* Class `CSTNU`: added the constructor CSTNU(String) for building an instance from a GraphML string.
* Class `AbstractCSTN`: added the method String getGCheckedAsGraphML().

### v3.5

date: 2021-06-14

* Fixed a small bug on the Dijkstra method in the STN class.
* Class `NodePriorityHeap` refactored.

### v3.4

date: 2021-06-03

* CSTNU Check also propagates the bounds of a contingent link as normal constraints to avoid a contingent link having an upper case value different from the negated upper bound.
* Fixed a graphical interface initialization error.

### v3.3

date: 2021-05-23

* CSTN(U)CheckStatus also stores the node that has a negative loop when the network is NOT DC.
* All DC checking methods save the resulting graph into a file before returning the check status.
* Improved some Javadoc comments.

### v3.2

date: 2021-01-14

* Fixed a coding error when saving a network into a file. From this release, the file encoding is UTF8, independent of the
  execution platform.
* Fixed an initialization error in the CSTNU class.
* Fixed almost all Javadoc errors.

### v3.1

date: 2020-12-28

* Some cleaning actions.
* Added copyright and licenses for publication on `archive.softwareheritage.org`.

### v3.0

date: 2020-11-15

* STNU class added with different DC checking algorithms: Morris2016, RUL2018, and RUL2020 faster one.
* CSTNPSU class offers a DC check sound-and-complete that can also adjust guarded links to the right ranges for an
  execution.
* Fixed some minor bugs.

### v2.11

date: 2020-02-02

* Graphical interface simplified.
* Now, it is more intuitive to add/remove nodes/edges.
* CSTNPSU class offers a DC check sound-and-complete.

### v2.10

date: 2020-02-02

* CSTN class restored to previous algorithms.
* Removed the possibility of checking without using unknown literals.
* This version considers the new rule and algorithm names published in a work presented at ICAPS 2020.
* CSTN class implements two DC checking algorithms. With option `--limitedToZ`, the algorithm is the one presented at
  IJCAI18 (algorithm `HR_18`).
  Without `--limitedToZ`, the algorithm is the one presented at ICAPS19 (algorithm `HR_19`). This last version is not
  efficient as IJCAI18 one.
* For a very efficient version, consider the `CSTNPotential` class (reintroduced but in a new form) that makes DC checking
  assuming IR semantics and node without labels (algorithm `HR_20`).
* `CSTNPotential` class implements the new DC checking algorithm based on single-sink the shortest paths and potential
  R0 and potential R3 rules.
* For all objects containing a labeled values field, the default implementation class is LabeledIntTreeMap. To change to
  this default class, I modified the field `DEFAULT_LABELEDINTMAP_CLASS` of LabeledIntMapSupplier and recompiled sources.
* `CSTNPSU` class allows the representation and the verification of temporal constraints containing guarded links. The
  DC checking algorithm is sound but not complete. Moreover, the guarded link bounds are not guaranteed to be shrunk correctly.

### v2.00

date: 2019-11-07

* CSTN class implements 9Rule DC algorithm. Such algorithm consists in 3 rules (LP, R0, and R3*) for generating also -∞
  value and six rules for managing -∞ values stored as potential values in nodes.
* Added CSTNSPFA class that implements the DC checking algorithm as a single-sink BellmanFord one.
  Therefore, only node potential values are generated. This class uses only three rules.

### v1.26.vassar

date: 2019-10-21

* Rewritten many classes to represent edges and graphs for STN networks efficiently.
* Removed class CSTNPotential (last svn-version 296).
* Added class STN & companions for some STN-specific algorithms.

### v1.26.0

date: 2019-03-23

* CSTNU class has a new field, contingentAlsoAsOrdinary, default false.
  When true, the DC checking method also propagates contingent links as ordinary constraints.
  This allows one to see some ordinary values more accurately.

### v1.25.0

date: 2018-11-22

* Minor code optimization and minor bug fixes.
* From this release, the software must be run using JRE 1.8.

### v1.24.0

date: 2018-07-18

* CSTN class allows the DC checking without using unknown literals (necessary for completeness): it is just a
  tool for studying the necessity of unknown literals.
* Literal and Label classes are now immutable.
* Minor code optimization.

### v1.23.2

date: 2018-02-21

* Class CSTNU cleaned and optimized. From this release, its DC checking algorithm is sound-and-complete.
* Contingent link can have a label even if it is not required for sound and completeness.
* Class CSTNPSU.java added.
* Sub-package 'attic' removed. (last svn revision 243).
* SVN version: 245.

### v1.22.4

date: 2018-01-10

* Code cleaning

### v1.22.3

date: 2017-12-14

* Class CSTNU optimized.
* Class CSTNURunningTime removed.
* Class CSTNRunningTime was renamed Checker.

### v1.22.2

date: 2017-11-24

* Code optimization

### v1.22.1

date: 2017-11-22

* The following classes have been renamed re-ordering inside terms:

1. `CSTNEpsilon.java`
2. `CSTNEpsilon3R.java`
3. `CSTNEpsilon3RwoNodeLabels.java`
4. `CSTNEpsilonwoNodeLabels.java`
5. `CSTNIR.java`
6. `CSTNIR3R.java`
7. `CSTNIR3RwoNodeLabels.java`
8. `CSTNIRwoNodeLabels.java`

### v1.22.0

date: 2017-11-21

* Introduced new classes and renamed old ones.
  There are 13 classes for checking CSTN/CSTNU:

```bash
1. it.univr.di.algorithms.CSTN.java
2. it.univr.di.algorithms.CSTN2CSTN0.java
3. it.univr.di.algorithms.CSTN3RIR.java: as IR CSTN, but DC checking uses only three rules.
4. it.univr.di.algorithms.CSTN3RwoNodeLabelEpsilon.java
5. it.univr.di.algorithms.CSTN3RwoNodeLabelIR.java
6. it.univr.di.algorithms.CSTNEpsilon.java
7. it.univr.di.algorithms.CSTNIR.java
8. it.univr.di.algorithms.CSTNU.java
9. it.univr.di.algorithms.CSTNU2CSTN.java
10. it.univr.di.algorithms.CSTNU2UppaalTiga.java
11. it.univr.di.algorithms.CSTNwoNodeLabel.java
12. it.univr.di.algorithms.CSTNwoNodeLabelEpsilon.java
13. it.univr.di.algorithms.CSTNwoNodeLabelIR.java
```

* Replaced Ω node with equivalent constraints in all CSTN classes.
* Removed Ω node from LabeledIntGraph and relative reader/writer.
* Improved CSTN Layout for laying out nodes without explicit temporal relation with Z.
* Started classes re-factoring and exploiting Java 8's new features.
* `CSTNRunningTime` and `CSTNURunningTime` made multi-thread.

### v1.21.0

date: 2017-11-09

* Class `CSTNUGraphMLReader` can read CSTN files that do not contain meta-information about UC and LC values.
* `CSTNirRestricted` renamed `CSTNir3R`.
* `CSTNwoNodeLabel` cleaned.
* Added classes `CSTNirwoNodeLabel` and `CSTNir3RwoNodeLabel`.
* Simplified `CSTNRunningTime`.

### v1.20.0

date: 2017-11-03

* Jung-library has been upgraded to 2.1.1 version.
* Such an update required an adaptation of all GUI classes of the package.
* CSTNU DC checking algorithm is now sound-and-complete.
* CSTNEditor now allows you to view a graph in a bigger window and save it as a PDF or other graphical format.
  The export menu is accessible by clicking the mouse inside the window containing the graph to export.
* There are now six classes for checking CSTN/CSTNU:

1. `it.univr.di.algorithms.CSTN`: it checks a CSTN instance assuming the standard semantics.
2. `it.univr.di.algorithms.CSTNepsilon`: it checks a CSTN instance assuming a not-zero reaction time (epsilon).
3. `it.univr.di.algorithms.CSTNir`: it checks a CSTN instance assuming instantaneous reactions.
4. `it.univr.di.algorithms.CSTNirRestricted`: as `it.univr.di.algorithms.CSTNir`, but the used rules are three instead of 6.
   Such checking can be faster than the `it.univr.di.algorithms.CSTNir` one.
5. `it.univr.di.algorithms.CSTNU`: it checks a CSTNU instance assuming instantaneous reactions.
6. `it.univr.di.algorithms.CSTNwoNodeLabel`: it checks a CSTN instance assuming the standard semantics. The instance
   is translated into an equivalent CSTN instance without node labels and then checked.

* `GraphMLReader` has been rewritten because based on Jung `GraphMLReader2`, that cannot manage big attribute
  data.
* `GraphMLReader` has been renamed `CSTNUGraphMLReader`.
* `GraphMLWriter` has been renamed `CSTNUGraphMLWriter`.

### v1.10.0

date: 2017-06-22

* Package reorganization.
* CSTNEditor cleaned and optimized.

### v1.9.0

date: 2016-03-31

* Labels are represented in a more compact way.
* Labeled value sets require less memory. Choosing which representation to use for
  labeled value sets is still possible.

### v1.8.0

date: 2016-02-24

* A new optimized class represents graphs.

### v1.7.9

date: 2015-11-26

* Added the new feature to CSTN DC-checking. Now, the DC-checking algorithm considers a user-specified 'reaction time' ε
  (ε > 0) of the system during the checking of a network. A reaction time ε means that an engine that executes a CSTN
  reacts in at least ε time units to a setting of a truth value to a proposition.

### v1.7.8

date: 2015-10-28

* A new sanity check about the correctness of an input CSTNU added: each contingent time point has to have one incoming
  edge of type 'contingent' and one outgoing edge of type 'contingent' from/to the same node, representing the
  activation time point.

### v1.7.7

date: 2015-10-22

* Fixed another issue with instantaneous reaction in CSTNU. Thanks to Dian Liu for his help in discovering such an error.
* I repeat that, even in this release, it is better to set any timepoint---that depends on an observation
  timepoint or follows a contingent timepoint---to
* a not 0 distance from the considered observation timepoint or contingent one.

### v1.7.6

date: 2015-09-30

* Fixed a subtle bug in the generation of new edges. Thanks to Dian Liu for his help in discovering such an error.

### v1.7.5

date: 2015-09-23

* Cleaned some log messages.
* We have discovered that the instantaneous reaction feature requires a sharp adjustment in the network semantics.
* We are currently working on introducing an ε-reaction (with ε ≥ 0) feature.
* In the meantime, for CSTN, it is possible to DC check assuming instantaneous reaction (ε = 0), while for CSTNU, it is only possible to check DC assuming non-instantaneous reaction (ε > 0).
* For now, it is better to set any timepoint---that depends on an observation timepoint or follows a contingent
  timepoint---to a not 0 distance from the
* considered observation timepoint or contingent one.

### v1.7.4

date: 2015-09-13

* In this release, a more strict check of contingent links has been introduced. Bounds of a contingent link A==[x,y]==>B
  must observe the property `0<x<y<∞`. Moreover, since for CSTNU instances, it is not yet defined the concept of 'instantaneous reactions,'
  it is not possible to define a CSTNU instance in which there exists a constraint like
  C--[0,0]-->B where B is contingent. This constraint requires that C be executed simultaneously as the
  contingent time point B, but this is impossible because the environment decides B, and the runtime engine must observe it before executing a standard node like C.

### v1.7.3

date: 2015-09-10

* This release contains some minor bug fixes and a revision of public methods of
  class `it.univr.di.cstnu.algorithms.CSTNU`.
* Now, it is possible to check the controllability of a CSTN instance by instantiating a CSTNU object and calling
  its `dynamicControllabilityCheck(LabeledIntGraph)` method.
* It is sufficient to create a `LabeledIntGraph` object representing a given CSTNU instance (in `LabeledIntGraph` class,
  there is also a method for loading an instance from a file written in GraphML format), instantiating a CSTNU object and
  calling its method `dynamicControllabilityCheck(LabeledIntGraph)` passing the created graph.
  Moreover, I add the class `it.univr.di.cstnu.algorithms.CSTNURunningTime` and the script `CSTNURunningTime` for
  checking a bundle of CSTNU instances, obtaining also some execution time statistics.

### v1.7.2

date: 2015-06-24

* This release contains minor bug fixes and a README file explaining the proposed examples of CSTNU/CSTN instances.
* Thanks to Huan Wang for his comments.

### v1.7.1

date: 2015-05-28

* This release contains a faster Dynamic Consistency check for CSTNs.
* The Java class `CSTN.java` has been completely rewritten and almost optimized for a faster check.

### v1.7.0

date: 2015-03-23

* This release is the first public release.
