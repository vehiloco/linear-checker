This folder saves corpus files.

Currently, 11 android projects are included as the benchmarks and will run with the Crypto Checker automatically in each building on Travis. These projects are used to make sure that the Crypto Checker can run properly without any exceptions. See them in [androidKeyStoreProject1.yml](./androidKeyStoreProject1.yml) and [androidKeyStoreProject2.yml](androidKeyStoreProject2.yml).

We have other projects used for evaluating the Crypto Checker: They broke the security rules (unpermitted algorithms or providers). They will not run in the Travis building because:

1. They will slow down the Travis (We already have 11 projects).
2. Thy cannot pass the typechecking if we do not modify the source code.

All the benchmark projects can be found in [xwt-benchmarks](https://github.com/xwt-benchmarks).
