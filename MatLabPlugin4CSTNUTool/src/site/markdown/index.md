## Welcome to Conditional Simple Temporal Network with Uncertainties Tool Project

### Introduction

MatLabPlugin 4 CSTNU Tool is an open-source software that offers a class that implements the interface `it.univr.di.cstnu.util.OptimizationEngine` necessary to
manipulate Probabilistic Simple Temporal Networks, represented by `it.univr.di.cstnu.algorithms.PSTN`.

**This plugin works only with MatLab R2024b runtime library and CSTNU-Tool one.**

[CSTNU Tool](https://profs.scienze.univr.it/~posenato/software/cstnu/) contains the `it.univr.di.cstnu.algorithms.PSTN` class that can be used only if there is
an implementation of the `it.univr.di.cstnu.util.OptimizationEngine`.

Since `it.univr.di.cstnu.util.OptimizationEngine` requires to solve a non-linear minimization problem and there is no full-fledged Java open-source library for
this task, I decided to offer an implementation based on MatLab (there is a campus-licence at University of Verona) and make such implementation as a companion
package in order to maintain the CSTNU-Tool not dependent on MatLab library.
