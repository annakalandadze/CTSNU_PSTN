# Building MatLabPlugin4CSTNUTool

version 1.0

The MatLabPlugin4CSTNUTool source code is organized as a Maven project.<br>
File `pom.xml` in the main directory contains all directives for compiling, testing, building the jar, and building the site of the project.

### IMPORTANT

Since this plugin depends on MatLab Tool 2024b and on CSTNU Tool, it is necessary to customize `pom.xml` setting the
path where MatLab runtime library and CSTNU Tool one are present.<br>
Please, consider the comments labelled as `***CONFIGURE***` inside `pom.xml` and adjust the settings before continuing.

### Main Commands

Summary of the main maven commands:

* `mvn clean`
  Cleans the environment removing all dangling or temporary files

* `mvn compile`
  Compiles all the sources

* `mvn test`
  Executes all JUNIT4 tests

* `mvn package`
  Builds the jar package `MatLabPlugin4CSTNUTool-X.Y.jar` and the binary-distribution archive `MatLabPlugin4CSTNUTool-*.*.tgz`.
  The archive  `MatLabPlugin4CSTNUTool-*.*.tgz` contains the JAR, `README.md`, and `RELEASE_NOTES.md`.

* `mvn package -Ddebug`
  As `mvn package` but with all debug messages enabled.
  To customize the level of debugging, it is necessary to give a `java.util.logging configuration` file as parameter to the JRE.
  For using the `logging.properties` file, it is sufficient to specify it as parameter:
     ```bash
     java -Djava.util.logging.config.file=logging.properties\
            -cp CSTNU-Tool-x.y.jar:MatLabPlugin4CSTNUTool-X.Y.jar \
            ...
     ```

* `mvn spotbugs:check`
  It makes a static analysis of Java sources.
  Behind some warning about the tool (ignore them!), the output must be similar to
    ```bash
  [INFO] >>> spotbugs:4.8.6.2:check (default-cli) > :spotbugs @ MatLabPlugin4CSTNUTool >>>
  [INFO]
  [INFO] --- spotbugs:4.8.6.2:spotbugs (spotbugs) @ MatLabPlugin4CSTNUTool ---
  [INFO] Fork Value is true
  [INFO] Done SpotBugs Analysis....
  [INFO] <<< spotbugs:4.8.6.2:check (default-cli) < :spotbugs @ MatLabPlugin4CSTNUTool <<<
  [INFO] --- spotbugs:4.8.6.2:check (default-cli) @ MatLabPlugin4CSTNUTool ---
  [INFO] BugInstance size is 0
  [INFO] Error size is 0
  [INFO] No errors/warnings found
  ```
