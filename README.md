# tlsChecker
Kotlin util program to check IP/Host_NAME to support TLS without sni name.

#limitation

to detect network connected or disconnected (online/offline), the quickest way on Mac is to run
need gtimeout

```
brew install coreutils
gtimeout 0.5 nc -z 1.1.1.1 8443  &>/dev/null && echo "Online" || echo "Offline, Failure Status Code: $?"
```

## build
```sh
mkdir lib
kotlinc src/main/kotlin/main.kt -include-runtime -d lib/tlsChecker.jar
```

## run example
```sh
java -jar lib/tlsChecker.jar www.google.com 443
```

