# Linear Checker

Linear Checker is a pluggable type system built on the Checker Framework. It is primarily designed to detect unsafe usages of Java Crypto APIs, such as `IvParameterSpec, PBEKeySpec, PBEParameterSpec, SecretKeySpec, SecureRandom, and KeyStore`.
## Build

### Prerequisites

Before building Linear Checker, ensure the following prerequisites are met:

- Gradle
- Java 8+

Under the project source directory, execute `./gradlew build`. The output jar file `linear-checker.jar` will be located in `<Project Dir>/build/libs`.

## Run Linear Checker

- To run type checking on the source file, use the following command:

	`javac -processor org.checkerframework.checker.linear.LinearChecker -Astubs=<Project Dir>/src/main/java/org/checkerframework/checker/linear/ -classpath <Project dir>/build/libs/linear-checker.jar:$CLASSPATH` <your source files>
