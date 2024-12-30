REST API :
1. Considered all the use-cases of assessment as separate endpoints
2. Utilized Java Streams for data manipulation
3. Global Exception handler utilized for custom exception handling
4. Slf4j Loggers are used appropriately.
5. Unit and Integration tests are covered.



***Key learnings during this development ***
1. Mockito by default cannot mock static or final methods in a class. Need extended library - Mockito-inline.
2. Private method unit testing - Suggested way to cover via public classes. If an extensive private method test is needed - use Power Mockito.
