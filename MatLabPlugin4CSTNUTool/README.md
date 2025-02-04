# MatLabPlugin 4 CSTNU Tool Project

## About

MatLabPlugin 4 CSTNU Tool is an open-source software that offers a class that implements the interface `it.univr.di.cstnu.util.OptimizationEngine` necessary to
manipulate Probabilistic Simple Temporal Networks, represented by `it.univr.di.cstnu.algorithms.PSTN`.

**This plugin works only with MatLab R2024b runtime library and CSTNU-Tool one.**

[CSTNU Tool](https://profs.scienze.univr.it/~posenato/software/cstnu/) contains the `it.univr.di.cstnu.algorithms.PSTN` class that can be used only if there is
an implementation of the `it.univr.di.cstnu.util.OptimizationEngine`.<br>
Since `it.univr.di.cstnu.util.OptimizationEngine` requires to solve a non-linear minimization problem and there is
no full-fledged Java open-source library for this task, I decided to offer an implementation based on MatLab (there
is a campus-licence at University of Verona) and make such implementation as a companion package in order to
maintain the CSTNU-Tool not dependent on MatLab library.

Such a plugin can be considered as a guide for other implementations of the `it.univr.di.cstnu.util.OptimizationEngine` interface.

## Documentation

The main website is https://profs.scienze.univr.it/~posenato/software/cstnu/matlabplugin

The main source repository is at https://profs.scienze.univr.it/posenato/svn/sw/CSTNU/MatLabPlugin4CSTNUTool

The archived copy of the source repository is
at https://archive.softwareheritage.org/browse/origin/?origin_url=https://profs.scienze.univr.it/posenato/svn/sw/CSTNU/MatLabPlugin4CSTNUTool

## Requirements

- Java &ge; 21
- MatLab R2024b runtime library
- CSTNU Tool &ge; 5.0

## Usage

The MatLabPlugin Plugin is a Java library that must be used with CSTN-Tool one.
It requires the MatLab R2024b runtime library to work.

This library is present in the main directory of the binary distribution archive as JAR package with name
`MatLabPlugin4CSTNUTool-X.Y.jar.jar`,
where `X.Y` are two integers representing the version of the package.
(If the JAR package is built from the sources, it will be present in the `BinaryPkg` subdirectory).

It is sufficient to add the library to the JRE classpath for using any class or method of the library.

Let us assume that:

- the current MatLabPlugin4CSTNUTool is `./MatLabPlugin4CSTNUTool-1.0.jar`,
- the CSTNU-Tool JAR is `./CSTNU-Tool-5.0.jar`)
- the MatLab R2024b runtime library is installed at `/Applications/MATLAB_R2024a.app/bin/maca64` (this installation is
  on a macOS system)
  Then, the java call must be

```bash
$ java -cp ./CSTNU-Tool-5.0.jar:./MatLabPlugin4CSTNUTool-1.0.jar \
       -Djava.library.path=/Applications/MATLAB_R2024a.app/bin/maca64 \
       ...
```

The Jar package is distributed with all debugging code removed.<br>
In case that it is necessary to have debugging messages,
please refer to `BUILDING.md` document in the source repository where it is explained how to build and use a Jar with
debugging messages.

## Licenses

For full licenses text, please check files in `LICENSES` subdirectory.
For source files the licenses (in SPDX term) is

```
// SPDX-FileCopyrightText: 2021 Roberto Posenato <roberto.posenato@univr.it>
//
// SPDX-License-Identifier: LGPL-3.0-or-later
```

For other file is

```
// SPDX-FileCopyrightText: 2021 Roberto Posenato <roberto.posenato@univr.it>
//
// SPDX-License-Identifier: CC0-1.0
```

## Support

Author and contact: Roberto Posenato <roberto.posenato@univr.it>
