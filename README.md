# Testcontainers-java ScriptScanner performance decrease
This is a small example that demonstrates that the performance of 
```
ScriptUtils.splitSqlScript(
        String resource,
        String script,
        String separator,
        String commentPrefix,
        String blockCommentStartDelimiter,
        String blockCommentEndDelimiter,
        List<String> statements
    )
```

has drastically decreased since testcontainers-java 1.19.2.
I suspect this performance decrease is due to more complex analysis and rules introduced in [fix bug in splitSqlScript #7646](https://github.com/testcontainers/testcontainers-java/pull/7646).

## Running the test cases and test results

Test are run with Maven 3.9.6 and Java 21.0.4-tem on an Apple M3 Max running macOs 15.0.1.

The test generates multirow single statement inserts where the number of rows range between 1000 and 100 000.
Those statements are fed to `ScriptUtils.splitSqlScript` and the duration of that call is logged.

The results for two different test cases are shown below

### Testcontainers-java 1.19.1

`mvn test` will run the test against Testcontainers-java 1.19.1.

| Number of rows | Duration (in milliseconds) |
|----------------|----------------------------|
| 1000           | 26                         |
| 10000          | 44                         |
| 20000          | 89                         |
| 30000          | 97                         |
| 40000          | 129                        |
| 50000          | 136                        |
| 60000          | 157                        |
| 70000          | 191                        |
| 80000          | 205                        |
| 90000          | 228                        |
| 100000         | 254                        |
Total build time: 2.482s

### Testcontainers-java 1.20.2

`mvn test -P slow` will run the test against Testcontainers-java 1.20.2.

| Number of rows | Duration (in milliseconds) |
|----------------|----------------------------|
| 1000           | 110                        |
| 10000          | 2845                       |
| 20000          | 9104                       |
| 30000          | 20093                      |
| 40000          | 34439                      |
| 50000          | 50609                      |
| 60000          | 70932                      |
| 70000          | 95303                      |
| 80000          | 117348                     |
| 90000          | 150061                     |
| 100000         | 188749                     |
Total build time 12:20min

## Impact and takeaways

With 1 000 rows, the new implementation is roughly four times slower than the old one.    
With 10 000 rows, the new implementation is roughly two orders of magnitude slower than the old one.    
With 100 000 rows, the new implementation is roughly three orders of magnitude slower than the old one.    

Now I'm not suggesting that I - or anyone else - uses this functionality to initialize their test database with 100 000 rows of data.    
What the actual impact in my case is that I have 1000+ integration tests that use testcontainers. 
Running all the tests with an older version of testcontainers takes just over 4 minutes. 
Running them with testcontainers 1.20.2 takes over 12 minutes.