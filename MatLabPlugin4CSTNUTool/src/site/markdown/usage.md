## Usage:

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
