# Crypto Checker

[![Build Status](https://travis-ci.org/vehiloco/crypto-checker.svg?branch=master)](https://travis-ci.org/vehiloco/crypto-checker)

The Crypto Checker is a pluggable type system built on the [Checker Framework](https://checkerframework.org/).
It can help you find whether there are any weak or unsupported crypto algorithms and the unsupported
algorithm providers being used in your program. If the Crypto Checker issues no warnings for a given
program, then you have a guarantee that your program at runtime will never have these issues.

The Crypto Checker aims to be sound, which means that a false positive may be reported if your code
is too complicated for it to understand. In this case, the Crypto Checker also helps you improve your 
code style.

For more details see this pre-print of the FTfJP 2021 paper
[Ensuring Correct Cryptographic Algorithm and Provider Usage at Compile
Time](2021-06-04-Crypto-Checker-FTfJP-2021-preprint.pdf).


## The Crypto Checker annotations

 - `@AllowedAlgorithms(String[])`: Indicates that a list of algorithms is allowed by the provided
   rules. For example, `@AllowedAlgorithms({"RSA", "EC"})` means the algorithm `RSA` and `EC` are allowed.
   Regular expressions are also allowed to use to indicate multiple algorithms easily, e.g.,
   `@AllowedAlgorithms(HmacSHA(1|224|256|384|512))` can match `HmacSHA1`, `HmacSHA224`, `HmacSHA256`,
   `HmacSHA384` and `HmacSHA512`.
   
   **Note**: Algorithm names are case-insensitive, i.e., `HMACSHA` equals to `HmacSHA`.
   
   If we annotate `KeyPairGenerator.getInstance` as follow:
   
   ```java
   class KeyPairGenerator {
       static KeyPairGenerator getInstance(@AllowedAlgorithms({"RSA", "EC"}) String arg0);
   }
   ```
   
   Then when we call `KeyPairGenerator.getInstance()`:
   
   ```java
   KeyPairGenerator.getInstance("EC"); // correct
   ```
   
   ```java
   KeyPairGenerator.getInstance("DSA"); // error, "DSA" is not allowed
   ```

 - `@AllowedProviders(String[])`: Indicates that a list of providers is allowed by the provided
   rules. For example, `@AllowedProviders({"AndroidKeyStore"})` means the provider `AndroidKeyStore`
   is allowed. The same as `@AllowedAlgorithms()`, regular expressions can also be used at here.
   
   **Note**: Provider names are case-insensitive. 
   
   Suppose that we annotate `KeyPairGenerator.getInstance` as:
   
   ```java
   class KeyPairGenerator {
       static KeyPairGenerator getInstance(String arg0, @AllowedProviders({"AndroidKeyStore"}) String arg1);
   }
   ```
   
   Then only `AndroidKeyStore` can be passed to the `arg1` of the method `getInstance`.

 - `@UnknownAlgorithmOrProvider`: The top type in the Crypto Checker. It is used internally by the type
   system but should never be written by a programmer. It indicates that no information about algorithm
   or provider is known.

 - `@Bottom`: The bottom type in the Crypto Checker. It is used internally by the type system but should
   never be written by a programmer.

### The subtyping hierarchy

<div style="text-align: center;"><img src="./subtyping_hierarchy.jpeg" alt="Subtyping Hierarchy"/></div>

For two `@AllowedAlgorithms` or two `@AllowedProviders`, the subtyping rule depends on the values.
For example:
 - `@AllowedAlgorithms({"a"})` is a subtype of `@AllowedAlgorithms({"a", "b"})`.
 - `@AllowedAlgorithms({"a", "b", "c"})` is a subtype of `@AllowedAlgorithms({"a", "b", "c", "d"})`.
 - `@AllowedAlgorithms({"a", "b", "c"})` is **not** a subtype of `@AllowedAlgorithms({"a", "b", "d"})`.

## Build

To build the Crypto Checker (In the root directory of the checker):

```bash
./gradlew build
```

If building with local Checker Framework:

```bash
# Set checkerframework_local = true at first in build.gradle
./scripts/dependency-build.sh
./gradlew build
```

## Quick Start

The Crypto Checker can work with [multiple build tools](https://checkerframework.org/manual/#external-tools),
here we provide a quick start with `javac` command.

```bash
./gradlew assemble copyDependencies

javac -cp ./build/libs/checker.jar:./build/libs/crypto-checker.jar -processor org.checkerframework.checker.crypto.CryptoChecker \
-Astubs="cipher.astub" tests/cipher/CipherTest.java
```

For the users who have installed the [Checker Framework](https://checkerframework.org/) from source:

```bash
./gradlew assemble
javacheck -cp ./build/libs/crypto-checker.jar -processor org.checkerframework.checker.crypto.CryptoChecker \
-Astubs="cipher.astub" tests/cipher/CipherTest.java
```

The expected output will be something like:

```
tests/cipher/CipherTest.java:9: error: [algorithm.not.allowed] Algorithm: AES is not allowed by the current rules
        Cipher.getInstance("AES");
                           ^
tests/cipher/CipherTest.java:20: error: [algorithm.not.allowed] Algorithm: DES/ECB/PKCS5PADDING is not allowed by the current rules
        Cipher.getInstance("DES/ECB/PKCS5Padding");
                           ^
tests/cipher/CipherTest.java:23: error: [algorithm.not.allowed] Algorithm: AES/ECB/NOPADDING is not allowed by the current rules
        Cipher.getInstance("AES/ECB/NoPadding");
                           ^
tests/cipher/CipherTest.java:26: error: [algorithm.not.allowed] Algorithm: DES/CCM/PKCS5PADDING is not allowed by the current rules
        Cipher.getInstance("DES/CCM/PKCS5Padding");
                           ^
tests/cipher/CipherTest.java:29: error: [algorithm.not.allowed] Algorithm: BLOWFISH/CBC/NOPADDING is not allowed by the current rules
        Cipher.getInstance("Blowfish/CBC/NoPadding");
                           ^
tests/cipher/CipherTest.java:32: error: [algorithm.not.allowed] Algorithm: RC4/CBC/NOPADDING is not allowed by the current rules
        Cipher.getInstance("RC4/CBC/NoPadding");
                           ^
tests/cipher/CipherTest.java:35: error: [algorithm.not.allowed] Algorithm: RC2/CBC/NOPADDING is not allowed by the current rules
        Cipher.getInstance("RC2/CBC/NoPadding");
                           ^
tests/cipher/CipherTest.java:38: error: [algorithm.not.allowed] Algorithm: IDEA/CBC/NOPADDING is not allowed by the current rules
        Cipher.getInstance("IDEA/CBC/NoPadding");
                           ^
tests/cipher/CipherTest.java:41: error: [algorithm.not.allowed] Algorithm: PBEWITHMD5ANDDES is not allowed by the current rules
        Cipher.getInstance("PBEWithMD5AndDES");
                           ^
tests/cipher/CipherTest.java:44: error: [algorithm.not.allowed] Algorithm: PBEWITHMD5ANDDES/CBC/PKCS5PADDING is not allowed by the current rules
        Cipher.getInstance("PBEWithMD5AndDES/CBC/PKCS5Padding");
                           ^
tests/cipher/CipherTest.java:47: error: [algorithm.not.allowed] Algorithm: PBEWITHMD5ANDDES/CCM/NOPADDING is not allowed by the current rules
        Cipher.getInstance("PBEWithMD5AndDES/CCM/NoPadding");
                           ^
tests/cipher/CipherTest.java:54: error: [algorithm.not.allowed] Algorithm: PBEWITHHMACSHA577ANDAES_128 is not allowed by the current rules
        Cipher.getInstance("PBEWithHmacSHA577AndAES_128");
                           ^
tests/cipher/CipherTest.java:57: error: [algorithm.not.allowed] Algorithm: PBEWITHSHAAND3KEYTRIPLEDES is not allowed by the current rules
        Cipher.getInstance("PBEWithSHAAnd3KeyTripleDES");
                           ^
tests/cipher/CipherTest.java:60: error: [algorithm.not.allowed] Algorithm: PBEWITHMD5ANDTRIPLEDES is not allowed by the current rules
        Cipher.getInstance("PBEWithMD5AndTripleDES");
                           ^
tests/cipher/CipherTest.java:63: error: [algorithm.not.allowed] Algorithm: PBEWITHMD5ANDTRIPLEDES is not allowed by the current rules
        Cipher.getInstance("PBEWithMD5AndTripleDES");
                           ^
tests/cipher/CipherTest.java:70: error: [algorithm.not.allowed] Algorithm: RSA/NONE/OAEPWITHSHA-1ANDMGF1PADDING is not allowed by the current rules
        Cipher.getInstance("RSA/NONE/OAEPwithSHA-1andMGF1Padding");
                           ^
16 errors
```

## Tests

To run all the basic tests (including test cases from cryptoapi-bench):

```bash
./gradlew test
```

## Run the Crypto Checker with the whole project

[demo](./demo) is a simple Android project which integrates with the Crypto Checker. Every time
you build the project, the Crypto Checker will run automatically to check the whole project.
[build.gradle](./demo/app/build.gradle) is provided as an example gradle file running the Crypto Checker.

More projects which have been integrated with the Crypto Checker (Remember to change the path in `app/build.gradle`):

- [revolution-irc](https://github.com/xwt-benchmarks/revolution-irc): Run `./gradlew build` to perform checking.
- [BiometricPromptDemo](https://github.com/xwt-benchmarks/BiometricPromptDemo): Run `./gradlew build` to perform checking.

To integrate with other tools, see [Chapter 32  Integration with external tools](https://checkerframework.org/manual/#external-tools)
in the Checker Framework manual.

## Stub files

The Crypto Checker supplies some well-formed default stub files which contain the rules of which algorithms
or providers are allowed to use. You can also create your own stub files as your need.

- [hardwarebacked.astub](src/main/java/org/checkerframework/checker/crypto/hardwarebacked.astub):
  Implement the security rules of Android's Hardware-backed Keystore.
- [strongboxbacked.astub](src/main/java/org/checkerframework/checker/crypto/strongboxbacked.astub):
  Implement the security rules of Android's Strongbox-backed Keystore. 
- [cipher.astub](src/main/java/org/checkerframework/checker/crypto/cipher.astub):
  Implement the security rules of Symmetric Cipher.
- [messagedigest.astub](src/main/java/org/checkerframework/checker/crypto/messagedigest.astub):
  Implement the security rules of Message Digest.

Two forms of a transformation, `algorithm` and `algorithm/mode/padding`, are fully supported by
the Crypto Checker. Apparently, `algorithm` is easy to indicate in the stub file. While you want 
to express the second form, remember to use the escape character. For example, `AES/GCM/NoPadding`
should be written as `AES\\/GCM\\/NoPadding` in the stub file. [cipher.astub](src/main/java/org/checkerframework/checker/crypto/cipher.astub) is a good example that combines several
secure algorithms in the first and second forms together.

See [Using stub class](https://checkerframework.org/manual/#stub) for more usage information.

## References:

- [The Checker Framework](https://checkerframework.org/)
- [aws-crypto-policy-compliance-checker](https://github.com/awslabs/aws-crypto-policy-compliance-checker)
- [cryptoapi-bench](https://github.com/CryptoGuardOSS/cryptoapi-bench)
