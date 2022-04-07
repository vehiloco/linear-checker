# An android demo project using the Crypto Checker

To run the Crypto Checker with this project:

- Run `./gradlew assemble publishToMavenLocal` in project [Crypto Checker](https://github.com/vehiloco/crypto-checker) to generate the dependencies.
- Run `./gradlew build` in this android project, the expected errors will be shown in terminal like:

```
demo/app/src/main/java/com/example/demo/Encryptor.java:72: error: [algorithm.not.allowed] Algorithm: DES is not allowed by the current rules
        final KeyGenerator keyGenerator = KeyGenerator.getInstance("DES", ANDROID_KEY_STORE);
                                                                   ^
demo/app/src/main/java/com/example/demo/Encryptor.java:91: error: [provider.not.allowed] Provider: WRONGPROVIDER is not allowed by the current rules
                        "WrongProvider"); // Error (NoSuchProviderException)
                        ^
2 errors
```
