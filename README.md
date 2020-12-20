# tlsChecker
Kotlin util program to check IP/Host_NAME to support TLS without sni name.

## build
mkdir lib
kotlinc src/main/kotlin/main.kt -include-runtime -d lib/tlsChecker.jar

## run example
java -jar lib/tlsChecker.jar www.google.com 443
