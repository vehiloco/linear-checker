# crypto-checker

The Crypto Checker is a pluggable type system built on the Checker Framework. It can help you find whether there are any weak or unsupported crypto algorithms and the unsupported algorithm providers being used in your program. If the Crypto Checker issues no warnings for a given program, then you have a guarantee that your program at runtime will never have these issues.

The Crypto Checker aims to be sound, which means that a false positive may be reported if your code is too complicated for it to understand. In this case, the Crypto Checker also helps you improve your code style.

See [vehiloco.github.io/crypto-checker/](https://vehiloco.github.io/crypto-checker/) for more information.
