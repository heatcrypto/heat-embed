# heat-embed

This work in progress project acts both as a test-bed for further developing HEAT 
embedding functionality. As well as a working starter application that shows you
how to add a custom binary message protocol and how through a combination of a 
`handler` and a `model` we can turn those blockchain messages into entries in a SQL
database. Plus we show how to extend the HEAT server HTTP API with your own HTTP
end-points.

## The Sample Application

The whole of the sample application lives in `com.heatledger.embed.sample` which can 
be found in [src/main/java/com/heatledger/embed/sample](/src/main/java/com/heatledger/embed/sample).

Lets briefly go over the files in the sample to explain what they do.

### SampleMain.java

This is the heart of the application that ties everything together. It is also where we find the `main` method.

[SampleMain.java](/src/main/java/com/heatledger/embed/sample/SampleMain.java)